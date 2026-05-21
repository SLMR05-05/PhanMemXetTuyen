package com.xettuyen.ui.panel;

import com.xettuyen.dao.*;
import com.xettuyen.entity.*;
import com.xettuyen.service.XetTuyenService;
import com.xettuyen.service.ExcelImportService;
import com.xettuyen.ui.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NguyenVongPanel extends JPanel {
    private JTextField txtCCCD;
    private JButton btnSearch, btnImport, btnAdd, btnEdit, btnDelete;
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private static final int PAGE_SIZE = 50;
    private int currentOffset = 0;
    private boolean isSearchMode = false;
    private boolean allLoaded = false;

    private final NguyenVongDAO nguyenVongDAO = DAOFactory.getNguyenVongDAO();
    private final XetTuyenService xetTuyenService = new XetTuyenService(DAOFactory.getDiemThiDAO(),
            DAOFactory.getDiemCongDAO(), DAOFactory.getBangQuyDoiDAO(), DAOFactory.getNguyenVongDAO(),
            DAOFactory.getNganhToHopDAO());

    private final DiemCongDAO diemCongDAO = DAOFactory.getDiemCongDAO();
    private final DiemThiDAO diemThiDAO = DAOFactory.getDiemThiDAO();
    private final BangQuyDoiDAO bangQuyDoiDAO = DAOFactory.getBangQuyDoiDAO();
    private final NganhToHopDAO nganhToHopDAO = DAOFactory.getNganhToHopDAO();
    private final ThiSinhDAO thiSinhDAO = DAOFactory.getThiSinhDAO();
    private JButton btnRecalculate;

    // Log storage
    private StringBuilder logBuilder;

    public NguyenVongPanel() {
        setBackground(MainFrame.C_CONTENT_BG);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // --- TOP ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Nhập CCCD:"));
        txtCCCD = new JTextField(15);
        btnSearch = new JButton("Tìm kiếm");
        btnImport = new JButton("Import Excel 📄");
        for (JButton b : new JButton[] { btnSearch, btnImport })
            styleBasicButton(b);

        searchPanel.add(txtCCCD);
        searchPanel.add(btnSearch);
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(btnImport, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER: Table ---
        String[] columns = { "ID", "CCCD", "Mã Ngành", "Thứ Tự", "Điểm THXT", "Ưu Tiên", "Điểm Cộng", "Tổng Điểm",
                "Kết Quả", "Phương Thức", "Tên Tổ Hợp" };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ẩn cột ID (index 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(MainFrame.C_BORDER));
        add(scrollPane, BorderLayout.CENTER);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (e.getValueIsAdjusting())
                return;
            if (isSearchMode || allLoaded)
                return;
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            int extent = bar.getModel().getExtent();
            int maximum = bar.getMaximum();
            int value = bar.getValue();
            if (value + extent >= maximum - 20) {
                loadMoreAllMode();
            }
        });

        // --- BOTTOM: CRUD buttons ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bottomPanel.setOpaque(false);
        btnAdd = new JButton("Thêm ➕");
        btnEdit = new JButton("Sửa ✏️");
        btnDelete = new JButton("Xóa 🗑️");
        btnRecalculate = new JButton("Tính lại điểm 🔄");
        for (JButton b : new JButton[] { btnAdd, btnEdit, btnDelete, btnRecalculate })
            styleBasicButton(b);
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        bottomPanel.add(new JLabel(" | "));
        bottomPanel.add(btnRecalculate);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Sự kiện ---
        btnSearch.addActionListener(e -> {
            String cccd = txtCCCD.getText().trim();
            if (cccd.isEmpty())
                resetAndLoadAll();
            else
                loadByCccd(cccd);
        });

        txtCCCD.addActionListener(e -> btnSearch.doClick());

        btnImport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn file Excel danh sách nguyện vọng");
            fileChooser
                    .setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
            if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
                return;

            File selectedFile = fileChooser.getSelectedFile();
            btnImport.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            SwingWorker<Integer, Void> worker = new SwingWorker<>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    // 1. Đọc dữ liệu từ Excel vào List (Chạy ngầm)
                    List<NguyenVongXettuyen> list = importNguyenVongLogic(selectedFile.getAbsolutePath());
                    if (list == null || list.isEmpty())
                        return 0;

                    // Tự động tính điểm cho từng nguyện vọng trước khi lưu
                    // for (NguyenVongXettuyen nv : list) {
                    // tinhDiemXetTuyen(nv);
                    // }

                    nguyenVongDAO.saveOrUpdateAll(list);

                    return list.size();
                }

                @Override
                protected void done() {
                    try {
                        int total = get();
                        if (total > 0) {
                            JOptionPane.showMessageDialog(NguyenVongPanel.this,
                                    "Đã import và tự động tính điểm cho " + total + " nguyện vọng.");
                        }
                        resetAndLoadAll(); // Làm mới bảng hiển thị
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(NguyenVongPanel.this, "Lỗi khi import: " + ex.getMessage(),
                                "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        btnImport.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            };
            worker.execute();
        });

        btnAdd.addActionListener(e -> {
            NguyenVongXettuyen nv = showEditDialog(null, "");
            if (nv == null)
                return;
            try {
                tinhDiemXetTuyen(nv); // Tự động tính điểm trước khi save
                nguyenVongDAO.save(nv);
                resetAndLoadAll();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage());
            }
        });

        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa!");
                return;
            }
            Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
            NguyenVongXettuyen existing = nguyenVongDAO.findById(NguyenVongXettuyen.class, id);
            NguyenVongXettuyen updated = showEditDialog(existing, existing.getNnCccd());
            if (updated == null)
                return;
            try {
                tinhDiemXetTuyen(updated); // Tự động tính lại điểm nếu có thay đổi (mã ngành, tổ hợp...)
                nguyenVongDAO.update(updated);
                if (isSearchMode)
                    loadByCccd(txtCCCD.getText().trim());
                else
                    resetAndLoadAll();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi sửa: " + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION)
                return;

            Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
            try {
                nguyenVongDAO.delete(nguyenVongDAO.findById(NguyenVongXettuyen.class, id));
                if (isSearchMode)
                    loadByCccd(txtCCCD.getText().trim());
                else
                    resetAndLoadAll();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage());
            }
        });

        btnRecalculate.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Hệ thống sẽ tính toán lại điểm cho TẤT CẢ nguyện vọng dựa trên điểm thi mới nhất. Tiếp tục?",
                    "Xác nhận tính lại điểm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION)
                return;
            // Hiển thị trạng thái đang xử lý để người dùng không bấm lung tung
            btnRecalculate.setEnabled(false);
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    try {
                        // Khởi tạo log builder
                        logBuilder = new StringBuilder();
                        logBuilder.append("=== TÍNH LẠI ĐIỂM - ")
                                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                .append(" ===\n\n");

                        addLog("=== BẮT ĐẦU TÍNH LẠI ĐIỂM ===");

                        // Bước 1: Lấy tất cả nguyện vọng
                        List<NguyenVongXettuyen> all = nguyenVongDAO.findAll();
                        addLog("📊 Tổng nguyện vọng: " + (all != null ? all.size() : 0));

                        if (all == null || all.isEmpty()) {
                            addLog("⚠️ CẢNH BÁO: Không có nguyện vọng nào để tính toán!");
                            saveLogToFile();
                            return "Không tìm thấy nguyện vọng nào trong hệ thống.";
                        }

                        // Bước 2: Tính điểm cho từng nguyện vọng
                        int successCount = 0;
                        int skipCount = 0;
                        StringBuilder errors = new StringBuilder();

                        for (NguyenVongXettuyen nv : all) {
                            try {
                                String cccd = nv.getNnCccd();
                                String maNganh = nv.getNvMaNganh();
                                addLog("\n📌 Xử lý: CCCD=" + cccd + ", Ngành=" + maNganh);

                                // Kiểm tra dữ liệu đầu vào
                                if (cccd == null || cccd.trim().isEmpty()) {
                                    addLog("  ❌ CCCD trống → bỏ qua");
                                    skipCount++;
                                    continue;
                                }
                                if (maNganh == null || maNganh.trim().isEmpty()) {
                                    addLog("  ❌ Mã ngành trống → bỏ qua");
                                    skipCount++;
                                    continue;
                                }

                                // Tính điểm
                                tinhDiemXetTuyen(nv);

                                // Cập nhật DB
                                nguyenVongDAO.update(nv);
                                addLog("  ✅ Cập nhật thành công → Điểm=" + nv.getDiemThxt());
                                successCount++;

                            } catch (Exception ex) {
                                String errorMsg = "Lỗi tính điểm cho CCCD " + nv.getNnCccd() + ": " + ex.getMessage();
                                addLog("  ❌ " + errorMsg);
                                errors.append("\n  - ").append(errorMsg);
                            }
                        }

                        addLog("\n=== KẾT THÚC ===");
                        addLog("✅ Thành công: " + successCount);
                        addLog("⏭️ Bỏ qua: " + skipCount);
                        addLog("❌ Lỗi: " + (all.size() - successCount - skipCount));

                        // Lưu log vào file
                        saveLogToFile();

                        if (errors.length() > 0) {
                            return "Cập nhật " + successCount + "/" + all.size() + " nguyện vọng.\nCác lỗi: "
                                    + errors.toString();
                        }
                        return "Đã cập nhật điểm cho " + successCount + " nguyện vọng thành công!";

                    } catch (Exception ex) {
                        addLog("❌ LỖI CHUNG: " + ex.getMessage());
                        ex.printStackTrace();
                        saveLogToFile();
                        return "Lỗi khi tính toán: " + ex.getMessage();
                    }
                }

                @Override
                protected void done() {
                    try {
                        String message = get(); // Lấy message từ doInBackground
                        btnRecalculate.setEnabled(true);
                        resetAndLoadAll(); // Tải lại bảng để hiện điểm mới
                        JOptionPane.showMessageDialog(NguyenVongPanel.this, message, "Kết quả tính toán",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        btnRecalculate.setEnabled(true);
                        JOptionPane.showMessageDialog(NguyenVongPanel.this,
                                "Lỗi: " + ex.getMessage(), "Lỗi Hệ Thống",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
        resetAndLoadAll();
    }

    private void resetAndLoadAll() {
        isSearchMode = false;
        allLoaded = false;
        currentOffset = 0;
        txtCCCD.setText("");
        tableModel.setRowCount(0);
        loadMoreAllMode();
    }

    private void loadMoreAllMode() {
        if (allLoaded)
            return;
        List<NguyenVongXettuyen> list = nguyenVongDAO.findAllPaged(currentOffset, PAGE_SIZE);
        if (list == null || list.isEmpty()) {
            allLoaded = true;
            return;
        }
        for (NguyenVongXettuyen nv : list)
            appendRow(nv);
        currentOffset += list.size();
        if (list.size() < PAGE_SIZE)
            allLoaded = true;
    }

    private void loadByCccd(String cccd) {
        List<NguyenVongXettuyen> list = nguyenVongDAO.findByCCCD(cccd);

        // 1. Nếu không tìm thấy (hoặc vừa xóa/sửa CCCD), âm thầm quay về Load All
        if (list == null || list.isEmpty()) {
            resetAndLoadAll(); // Hàm này sẽ tự xóa txtCCCD và load lại toàn bộ
            return;
        }

        // 2. Nếu tìm thấy dữ liệu, mới bắt đầu chuyển sang Search Mode
        isSearchMode = true;
        tableModel.setRowCount(0); // Xóa bảng cũ để hiện kết quả tìm kiếm

        // Đổ dữ liệu tìm được vào bảng
        for (NguyenVongXettuyen nv : list) {
            appendRow(nv);
        }

        // KIỂM TRA ĐỂ CẮM BIỂN "HẾT HÀNG" (allLoaded)
        // Nếu danh sách tìm được ít hơn 50 dòng (PAGE_SIZE), chắc chắn là đã hết dữ
        // liệu
        if (list.size() < PAGE_SIZE) {
            allLoaded = true;
        } else {
            // Nếu đúng 50 dòng, có thể vẫn còn nữa (cần cuộn để tải thêm)
            allLoaded = false;
        }
    }

    private void appendRow(NguyenVongXettuyen nv) {
        tableModel.addRow(new Object[] { nv.getIdNv(), // ID (ẩn)
                nv.getNnCccd(), // CCCD
                nv.getNvMaNganh(), // Mã Ngành
                nv.getNvTt(), // Thứ Tự
                formatScore(nv.getDiemThxt()), // Điểm THXT
                formatScore(nv.getDiemUtqd()), // Điểm Ưu Tiên
                formatScore(nv.getDiemCong()), // Điểm Cộng
                formatScore(nv.getDiemXettuyen()), // Điểm Xét Tuyển (Tổng)
                nv.getNvKetqua() != null ? nv.getNvKetqua() : "Chờ xét", // Kết Quả
                nv.getTtPhuongThuc(), // Phương Thức
                nv.getTtThm() // Tên Tổ Hợp
        });
    }

    private String formatScore(Double score) {
        return (score != null) ? String.format("%.2f", score) : "---";
    }

    private NguyenVongXettuyen showEditDialog(NguyenVongXettuyen existing, String defaultCccd) {
        boolean isEdit = existing != null;

        JTextField fCccd = new JTextField(isEdit ? existing.getNnCccd() : defaultCccd, 15);
        JTextField fMaNganh = new JTextField(isEdit ? existing.getNvMaNganh() : "", 15);
        JTextField fThuTu = new JTextField(isEdit ? String.valueOf(existing.getNvTt()) : "", 5);

        // --- Ô lựa chọn Phương thức (Combo Box) ---
        String[] ptDisplay = { "2 - DGNL", "3 - VSAT", "4 - THPT" };
        String[] ptValues = { "2", "3", "4" };
        JComboBox<String> cbPhuongThuc = new JComboBox<>(ptDisplay);

        if (isEdit && existing.getTtPhuongThuc() != null) {
            // Loại bỏ tiền tố "PT" để tìm index tương ứng (ví dụ "PT4" -> "4")
            String currentVal = existing.getTtPhuongThuc().replace("PT", "");
            for (int i = 0; i < ptValues.length; i++) {
                if (ptValues[i].equals(currentVal)) {
                    cbPhuongThuc.setSelectedIndex(i);
                    break;
                }
            }
        }

        // --- Ô lựa chọn Kết quả ---
        String[] statusOptions = { "Chờ xét", "Dưới sàn", "Đậu" };
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        if (isEdit && existing.getNvKetqua() != null) {
            cbStatus.setSelectedItem(existing.getNvKetqua());
        }

        // --- Giao diện Panel ---
        JPanel panel = new JPanel(new java.awt.GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("CCCD:"));
        panel.add(fCccd);
        panel.add(new JLabel("Mã ngành:"));
        panel.add(fMaNganh);
        panel.add(new JLabel("Thứ tự NV:"));
        panel.add(fThuTu);
        panel.add(new JLabel("Phương thức:"));
        panel.add(cbPhuongThuc);
        panel.add(new JLabel("Kết quả:"));
        panel.add(cbStatus);

        int result = JOptionPane.showConfirmDialog(this, panel, isEdit ? "Sửa Nguyện Vọng" : "Thêm Nguyện Vọng",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION)
            return null;

        try {
            NguyenVongXettuyen nv = isEdit ? existing : new NguyenVongXettuyen();

            nv.setNnCccd(fCccd.getText().trim());
            nv.setNvMaNganh(fMaNganh.getText().trim());
            nv.setNvTt(Integer.parseInt(fThuTu.getText().trim()));

            // --- Xử lý lưu kèm tiền tố PT ---
            int selectedIndex = cbPhuongThuc.getSelectedIndex();
            String selectedVal = ptValues[selectedIndex];
            nv.setTtPhuongThuc("PT" + selectedVal); // Lưu vào DB là PT0, PT3 hoặc PT4

            nv.setNvKetqua(cbStatus.getSelectedItem().toString());
            nv.setNvKeys(nv.getNnCccd() + "_" + nv.getNvMaNganh() + "_" + nv.getTtPhuongThuc());

            return nv;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Thứ tự nguyện vọng phải là một con số!");
            return null;
        }
    }

    private List<NguyenVongXettuyen> importNguyenVongLogic(String filePath) {
        try {
            return new ExcelImportService().importNguyenVong(filePath);
        } catch (Exception e) {
            return null;
        }
    }

    private void styleBasicButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MainFrame.C_BORDER),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
    }

    /**
     * Giai đoạn 1: Load dữ liệu chuẩn bị
     * - Lấy danh sách tổ hợp hợp lệ theo mã ngành
     * - Lấy TẤT CẢ DiemThiXettuyen của thí sinh (THPT, VSAT, DGNL)
     * - Cache BangQuydoi để sử dụng trong quy đổi
     */
    public void tinhDiemXetTuyen(NguyenVongXettuyen nv) {
        String cccd = nv.getNnCccd();
        String maNganh = nv.getNvMaNganh();

        // Giai đoạn 1: Load dữ liệu
        List<NganhTohop> validToHops = nganhToHopDAO.findByMaNganh(maNganh);
        List<DiemThiXettuyen> allDiemThi = diemThiDAO.findByCCCD(cccd);

        System.out.println(
                "  [STAGE 1] Ngành=" + maNganh + " → Tổ hợp=" + (validToHops != null ? validToHops.size() : 0));
        System.out.println("  [STAGE 1] CCCD=" + cccd + " → Điểm thi=" + (allDiemThi != null ? allDiemThi.size() : 0));

        // ⚠️ Xử lý trường hợp allDiemThi bị null
        if (allDiemThi == null || allDiemThi.isEmpty()) {
            // Nếu không có dữ liệu điểm thi, set mặc định
            System.out.println("  ⚠️ [STAGE 1] CCCD=" + cccd + " không có điểm thi → set điểm = 0");
            nv.setDiemThxt(0.0);
            nv.setDiemUtqd(0.0);
            nv.setDiemCong(0.0);
            nv.setDiemXettuyen(0.0);
            addLog("  ⚠️ CCCD " + cccd + ": Không có dữ liệu điểm thi");
            return;
        }

        // Kiểm tra tổ hợp hợp lệ
        if (validToHops == null || validToHops.isEmpty()) {
            System.out.println("  ⚠️ [STAGE 2] Ngành " + maNganh + " không có tổ hợp hợp lệ → set điểm = 0");
            nv.setDiemThxt(0.0);
            nv.setDiemUtqd(0.0);
            nv.setDiemCong(0.0);
            nv.setDiemXettuyen(0.0);
            addLog("  ⚠️ CCCD " + cccd + ": Ngành " + maNganh + " không có tổ hợp hợp lệ");
            return;
        }

        // Giai đoạn 2: Tính điểm cho từng phương thức
        ScoreResult thptScore = calculateTHPTScore(allDiemThi, validToHops);
        ScoreResult vsatScore = calculateVSATScore(allDiemThi, validToHops);
        ScoreResult dgnlScore = calculateDGNLScore(allDiemThi);

        System.out.println("  [STAGE 2] THPT=" + thptScore.getDiemThxt() + " | VSAT=" + vsatScore.getDiemThxt()
                + " | DGNL=" + dgnlScore.getDiemThxt());

        // Giai đoạn 3: So sánh và tối ưu - tìm phương thức tốt nhất
        ScoreResult bestScore = findBestMethodScore(thptScore, vsatScore, dgnlScore);

        System.out.println("  [STAGE 3] Phương thức tốt nhất: " + bestScore.getPhuongThuc() + " → Điểm="
                + bestScore.getDiemThxt());

        // Giai đoạn 4: Cập nhật kết quả vào NguyenVongXettuyen
        if (bestScore != null && bestScore.getDiemThxt() > 0) {
            nv.setDiemThxt(bestScore.getDiemThxt());
            nv.setTtThm(bestScore.getMaToHop());
            nv.setTtPhuongThuc("PT" + bestScore.getPhuongThuc()); // Thêm tiền tố PT để nhất quán

            // Tính điểm cộng dựa trên tổ hợp được chọn
            DiemCongXettuyen dc = diemCongDAO.findByDcKeys(cccd, maNganh, bestScore.getMaToHop());
            double diemCong = 0.0;
            if (dc != null) {
                double diemAnh = (dc.getDiemCc() != null) ? dc.getDiemCc() : 0.0;
                double diemHSG = (dc.getDiemUtxt() != null) ? dc.getDiemUtxt() : 0.0;
                diemCong = Math.min(diemAnh + diemHSG, 3.0);
            }
            nv.setDiemCong(diemCong);

            // Tính điểm ưu tiên từ khu vực + đối tượng
            ThiSinhXettuyen ts = thiSinhDAO.findByCCCD(cccd);
            double diemKhuVuc = getDiemKhuVuc(ts != null ? ts.getKhuVuc() : "3");
            double diemDoiTuong = getDiemDoiTuong(ts != null ? ts.getDoiTuong() : "");
            double tongMucUuTienNiemYet = diemKhuVuc + diemDoiTuong;

            double tongDiemTho = bestScore.getDiemThxt() + diemCong;
            double diemUtqd = calculateDiemUtqd(tongDiemTho, tongMucUuTienNiemYet);
            nv.setDiemUtqd(diemUtqd);

            double diemXetTuyen = tongDiemTho + diemUtqd;
            nv.setDiemXettuyen(diemXetTuyen);

            System.out.println("  [STAGE 4] ✅ Cộng=" + diemCong + " | Ưu=" + diemUtqd + " | Tổng=" + diemXetTuyen);
        } else {
            System.out.println("  [STAGE 4] ❌ Tất cả phương thức đều = 0 → set điểm = 0");
            nv.setDiemThxt(0.0);
            nv.setDiemUtqd(0.0);
            nv.setDiemCong(0.0);
            nv.setDiemXettuyen(0.0);
        }
    }

    /**
     * Luồng 2.1: Tính điểm phương thức THPT (không quy đổi)
     * Bước 1: Duyệt tổ hợp hợp lệ
     * Bước 2: Lấy 3 môn từ d_phuongthuc = '4', cộng lại
     * Bước 3: Áp dụng độ lệch: Điểm THPT = Tổng 3 môn - Độ lệch
     * Bước 4: Ghi nhận điểm THPT cao nhất
     */
    private ScoreResult calculateTHPTScore(List<DiemThiXettuyen> allDiemThi, List<NganhTohop> validToHops) {
        DiemThiXettuyen thptData = null;
        for (DiemThiXettuyen dt : allDiemThi) {
            if ("4".equals(dt.getDPhuongThuc())) {
                thptData = dt;
                break;
            }
        }

        if (thptData == null) {
            System.out.println("    → THPT: Không có d_phuongthuc='4' → return 0");
            return new ScoreResult(0.0, "4", "");
        }

        double maxScore = 0.0;
        String bestToHop = "";

        for (NganhTohop toHop : validToHops) {
            // Lấy 3 môn
            double d1 = getDiemByMaMon(thptData, toHop.getThMon1());
            double d2 = getDiemByMaMon(thptData, toHop.getThMon2());
            double d3 = getDiemByMaMon(thptData, toHop.getThMon3());

            // Cộng lại
            double tongMon = d1 + d2 + d3;

            // Áp dụng độ lệch
            double doLech = (toHop.getDoLech() != null) ? toHop.getDoLech() : 0.0;
            double diemThxt = tongMon - doLech;
            diemThxt = Math.round(diemThxt * 100.0) / 100.0;

            if (diemThxt > maxScore) {
                maxScore = diemThxt;
                bestToHop = toHop.getMaTohop();
            }
        }

        System.out.println("    → THPT: maxScore=" + maxScore + ", tổ hợp=" + bestToHop);
        return new ScoreResult(maxScore, "4", bestToHop); // PT4 = THPT
    }

    /**
     * Luồng 2.2: Tính điểm phương thức V-SAT (quy đổi theo từng môn)
     * Bước 1: Duyệt tổ hợp
     * Bước 2: Lấy điểm VSAT gốc (thang 150)
     * Bước 3: Tìm khoảng a, b, c, d từ BangQuydoi
     * Bước 4: Áp dụng công thức nội suy tuyến tính
     */
    private ScoreResult calculateVSATScore(List<DiemThiXettuyen> allDiemThi, List<NganhTohop> validToHops) {
        DiemThiXettuyen vsatData = null;
        for (DiemThiXettuyen dt : allDiemThi) {
            if ("3".equals(dt.getDPhuongThuc())) {
                vsatData = dt;
                break;
            }
        }

        if (vsatData == null) {
            System.out.println("    → VSAT: Không có d_phuongthuc='3' → return 0");
            return new ScoreResult(0.0, "3", "");
        }

        double maxScore = 0.0;
        String bestToHop = "";

        for (NganhTohop toHop : validToHops) {
            // Lấy 3 môn gốc
            double d1 = getDiemByMaMon(vsatData, toHop.getThMon1());
            double d2 = getDiemByMaMon(vsatData, toHop.getThMon2());
            double d3 = getDiemByMaMon(vsatData, toHop.getThMon3());

            // Quy đổi từng môn
            double qd1 = convertVSATSubjectScore(d1, toHop.getThMon1());
            double qd2 = convertVSATSubjectScore(d2, toHop.getThMon2());
            double qd3 = convertVSATSubjectScore(d3, toHop.getThMon3());

            // Cộng điểm quy đổi
            double tongMonQuydoi = qd1 + qd2 + qd3;

            // Áp dụng độ lệch
            double doLech = (toHop.getDoLech() != null) ? toHop.getDoLech() : 0.0;
            double diemThxt = tongMonQuydoi - doLech;
            diemThxt = Math.round(diemThxt * 100.0) / 100.0;

            if (diemThxt > maxScore) {
                maxScore = diemThxt;
                bestToHop = toHop.getMaTohop();
            }
        }

        System.out.println("    → VSAT: maxScore=" + maxScore + ", tổ hợp=" + bestToHop);
        return new ScoreResult(maxScore, "3", bestToHop); // PT3 = VSAT
    }

    /**
     * Luồng 2.3: Tính điểm phương thức ĐGNL (quy đổi điểm tổng)
     * Bước 1: Lấy điểm NL1 gốc (thang 1200)
     * Bước 2: Tìm khoảng a, b, c, d từ BangQuydoi (d_phuongthuc = 'DGNL')
     * Bước 3: Quy đổi từ thang 1200 về thang 30
     */
    private ScoreResult calculateDGNLScore(List<DiemThiXettuyen> allDiemThi) {
        DiemThiXettuyen dgnlData = null;
        for (DiemThiXettuyen dt : allDiemThi) {
            if ("2".equals(dt.getDPhuongThuc())) {
                dgnlData = dt;
                break;
            }
        }

        if (dgnlData == null || dgnlData.getNl1() == null) {
            System.out.println("    → DGNL: Không có d_phuongthuc='2' hoặc NL1=null → return 0");
            return new ScoreResult(0.0, "2", "");
        }

        double nl1 = dgnlData.getNl1();
        System.out.println("    → DGNL: NL1=" + nl1);

        // Tìm khoảng quy đổi cho ĐGNL
        BangQuydoi quyDoiRange = bangQuyDoiDAO.findRangeForDGNL("", nl1); // "" vì DGNL không xét tổ hợp

        if (quyDoiRange == null) {
            System.out.println("    → DGNL: Không tìm thấy khoảng quy đổi cho NL1=" + nl1 + " → return 0");
            return new ScoreResult(0.0, "2", "");
        }

        // Áp dụng công thức nội suy tuyến tính
        double a = quyDoiRange.getDDiemA();
        double b = quyDoiRange.getDDiemB();
        double c = quyDoiRange.getDDiemC();
        double d = quyDoiRange.getDDiemD();

        System.out.println("    → DGNL: Khoảng quy đổi: [a=" + a + ", b=" + b + ", c=" + c + ", d=" + d + "]");

        double diemThxt = c + ((nl1 - a) / (b - a)) * (d - c);
        diemThxt = Math.round(diemThxt * 100.0) / 100.0;

        System.out.println("    → DGNL: maxScore=" + diemThxt);
        return new ScoreResult(diemThxt, "2", ""); // 2 = DGNL, không có tổ hợp
    }

    /**
     * Giai đoạn 3: So sánh 3 phương thức và tìm phương thức tốt nhất
     */
    private ScoreResult findBestMethodScore(ScoreResult thpt, ScoreResult vsat, ScoreResult dgnl) {
        double maxScore = Math.max(Math.max(thpt.getDiemThxt(), vsat.getDiemThxt()), dgnl.getDiemThxt());

        if (maxScore == thpt.getDiemThxt()) {
            return thpt;
        } else if (maxScore == vsat.getDiemThxt()) {
            return vsat;
        } else {
            return dgnl;
        }
    }

    /**
     * Hỗ trợ: Quy đổi điểm một môn VSAT theo công thức nội suy tuyến tính
     */
    private double convertVSATSubjectScore(double vsatScore, String maMon) {
        if (vsatScore <= 0)
            return 0.0;

        BangQuydoi quyDoiRange = findVSATConversionRange(maMon, vsatScore);

        if (quyDoiRange == null) {
            return 0.0;
        }

        double a = quyDoiRange.getDDiemA();
        double b = quyDoiRange.getDDiemB();
        double c = quyDoiRange.getDDiemC();
        double d = quyDoiRange.getDDiemD();

        double diemQuydoi = c + ((vsatScore - a) / (b - a)) * (d - c);
        return Math.round(diemQuydoi * 100.0) / 100.0;
    }

    /**
     * Hỗ trợ: Tìm khoảng quy đổi cho một môn VSAT
     */
    private BangQuydoi findVSATConversionRange(String maMon, double vsatScore) {
        return bangQuyDoiDAO.findRangeForVSAT(maMon, vsatScore);
    }

    // Hàm này đã được tích hợp vào calculateTHPTScore và calculateVSATScore, không
    // còn cần thiết

    public double calculateDiemUtqd(double tongDiemTho, double tongMucUuTienNiemYet) {
        if (tongDiemTho >= 30.0)
            return 0.0;
        if (tongDiemTho >= 22.5) {
            double result = ((30.0 - tongDiemTho) / 7.5) * tongMucUuTienNiemYet;
            return Math.max(0, Math.round(result * 100.0) / 100.0);
        }
        return Math.round(tongMucUuTienNiemYet * 100.0) / 100.0;
    }

    public double getDiemKhuVuc(String khuVuc) {
        if (khuVuc == null)
            return 0.0;
        switch (khuVuc.trim().toUpperCase()) {
            case "1":
                return 0.75;
            case "2NT":
                return 0.50;
            case "2":
                return 0.25;
            case "3":
                return 0.0;
            default:
                return 0.0;
        }
    }

    public double getDiemDoiTuong(String doiTuong) {
        if (doiTuong == null || doiTuong.isEmpty())
            return 0.0;
        try {
            String numericPart = doiTuong.replaceAll("[^0-9]", "");
            int dtVal = Integer.parseInt(numericPart);
            if (dtVal >= 1 && dtVal <= 5)
                return 2.0;
            if (dtVal >= 6 && dtVal <= 7)
                return 1.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
        return 0.0;
    }

    // Hàm này đã được tích hợp vào calculateDGNLScore, không còn cần thiết

    // Hàm này đã được tích hợp vào calculateTHPTScore, không còn cần thiết

    // Hàm bổ trợ để map từ mã viết tắt (TO, LI, VA...) sang thuộc tính trong object
    // DiemThi - XỬ LÝ NULL GRACEFULLY
    private double getDiemByMaMon(DiemThiXettuyen dt, String maMon) {
        if (dt == null || maMon == null)
            return 0.0;

        Double value = null;
        switch (maMon.toUpperCase()) {
            case "TO":
                value = dt.getTo();
                break;
            case "VA":
                value = dt.getVa();
                break;
            case "LI":
                value = dt.getLi();
                break;
            case "HO":
                value = dt.getHo();
                break;
            case "SI":
                value = dt.getSi();
                break;
            case "SU":
                value = dt.getSu();
                break;
            case "DI":
                value = dt.getDi();
                break;
            case "N1":
                value = dt.getN1Cc();
                break;
            case "TI":
                value = dt.getTi();
                break;
            case "KTPL":
                value = dt.getKtpl();
                break;
            default:
                return 0.0;
        }

        // Nếu getter trả về null, trả về 0 (không crash)
        return (value != null) ? value : 0.0;
    }

    // Hàm này đã được tích hợp vào calculateTHPTScore, calculateVSATScore,
    // calculateDGNLScore, không còn cần thiết

    // Hàm này đã được tích hợp vào calculateDGNLScore, không còn cần thiết

    /**
     * Inner class để lưu kết quả tính điểm của mỗi phương thức
     */
    private static class ScoreResult {
        private final double diemThxt;
        private final String phuongThuc;
        private final String maToHop;

        public ScoreResult(double diemThxt, String phuongThuc, String maToHop) {
            this.diemThxt = diemThxt;
            this.phuongThuc = phuongThuc;
            this.maToHop = maToHop;
        }

        public double getDiemThxt() {
            return diemThxt;
        }

        public String getPhuongThuc() {
            return phuongThuc;
        }

        public String getMaToHop() {
            return maToHop;
        }

        @Override
        public String toString() {
            return "ScoreResult{" +
                    "diemThxt=" + diemThxt +
                    ", phuongThuc='" + phuongThuc + '\'' +
                    ", maToHop='" + maToHop + '\'' +
                    '}';
        }
    }

    /**
     * Helper: Thêm log vào StringBuilder
     */
    private void addLog(String message) {
        if (logBuilder != null) {
            logBuilder.append(message).append("\n");
        }
        System.out.println(message); // Cũng in ra console
    }

    /**
     * Helper: Lưu log vào file
     */
    private void saveLogToFile() {
        if (logBuilder == null || logBuilder.length() == 0) {
            return;
        }

        try {
            // Tạo thư mục logs nếu chưa tồn tại
            File logsDir = new File("logs");
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }

            // Tạo tên file log theo timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String logFileName = "logs/tinh_diem_" + timestamp + ".log";

            // Lưu log vào file
            try (FileWriter writer = new FileWriter(logFileName)) {
                writer.write(logBuilder.toString());
                writer.flush();
                System.out.println("✅ Log đã được lưu vào: " + logFileName);
            }
        } catch (IOException ex) {
            System.err.println("❌ Lỗi khi lưu log: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
