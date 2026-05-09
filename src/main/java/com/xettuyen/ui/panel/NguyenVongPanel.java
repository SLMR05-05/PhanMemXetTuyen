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
    private final XetTuyenService xetTuyenService = new XetTuyenService(DAOFactory.getDiemThiDAO(), DAOFactory.getDiemCongDAO(), DAOFactory.getBangQuyDoiDAO(), DAOFactory.getNguyenVongDAO(), DAOFactory.getNganhToHopDAO());

    private final DiemCongDAO diemCongDAO = DAOFactory.getDiemCongDAO();
    private final DiemThiDAO diemThiDAO = DAOFactory.getDiemThiDAO();
    private final BangQuyDoiDAO bangQuyDoiDAO = DAOFactory.getBangQuyDoiDAO();
    private final NganhToHopDAO nganhToHopDAO = DAOFactory.getNganhToHopDAO();
    private final ThiSinhDAO thiSinhDAO = DAOFactory.getThiSinhDAO();
    private JButton btnRecalculate;

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
        for (JButton b : new JButton[]{btnSearch, btnImport})
            styleBasicButton(b);

        searchPanel.add(txtCCCD);
        searchPanel.add(btnSearch);
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(btnImport, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER: Table ---
        String[] columns = {"ID", "CCCD", "Mã Ngành", "Thứ Tự", "Điểm THXT", "Ưu Tiên", "Điểm Cộng", "Tổng Điểm", "Kết Quả", "Phương Thức", "Tên Tổ Hợp"};

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
            if (e.getValueIsAdjusting()) return;
            if (isSearchMode || allLoaded) return;
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
        for (JButton b : new JButton[]{btnAdd, btnEdit, btnDelete, btnRecalculate})
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
            if (cccd.isEmpty()) resetAndLoadAll();
            else loadByCccd(cccd);
        });

        txtCCCD.addActionListener(e -> btnSearch.doClick());

        btnImport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn file Excel danh sách nguyện vọng");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
            if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

            File selectedFile = fileChooser.getSelectedFile();
            btnImport.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            SwingWorker<Integer, Void> worker = new SwingWorker<>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    // 1. Đọc dữ liệu từ Excel vào List (Chạy ngầm)
                    List<NguyenVongXettuyen> list = importNguyenVongLogic(selectedFile.getAbsolutePath());
                    if (list == null || list.isEmpty()) return 0;

                    // Tự động tính điểm cho từng nguyện vọng trước khi lưu
                    for (NguyenVongXettuyen nv : list) {
                        tinhDiemXetTuyen(nv);
                    }

                    nguyenVongDAO.saveOrUpdateAll(list);

                    return list.size();
                }

                @Override
                protected void done() {
                    try {
                        int total = get();
                        if (total > 0) {
                            JOptionPane.showMessageDialog(NguyenVongPanel.this, "Đã import và tự động tính điểm cho " + total + " nguyện vọng.");
                        }
                        resetAndLoadAll(); // Làm mới bảng hiển thị
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(NguyenVongPanel.this, "Lỗi khi import: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
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
            if (nv == null) return;
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
            if (updated == null) return;
            try {
                tinhDiemXetTuyen(updated); // Tự động tính lại điểm nếu có thay đổi (mã ngành, tổ hợp...)
                nguyenVongDAO.update(updated);
                if (isSearchMode) loadByCccd(txtCCCD.getText().trim());
                else resetAndLoadAll();
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
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
            try {
                nguyenVongDAO.delete(nguyenVongDAO.findById(NguyenVongXettuyen.class, id));
                if (isSearchMode) loadByCccd(txtCCCD.getText().trim());
                else resetAndLoadAll();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage());
            }
        });

        btnRecalculate.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Hệ thống sẽ tính toán lại điểm cho TẤT CẢ nguyện vọng dựa trên điểm thi mới nhất. Tiếp tục?", "Xác nhận tính lại điểm", JOptionPane.YES_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            // Hiển thị trạng thái đang xử lý để người dùng không bấm lung tung
            btnRecalculate.setEnabled(false);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    List<NguyenVongXettuyen> all = nguyenVongDAO.findAll();
                    for (NguyenVongXettuyen nv : all) {
                        tinhDiemXetTuyen(nv);
                        nguyenVongDAO.update(nv);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    btnRecalculate.setEnabled(true);
                    resetAndLoadAll(); // Tải lại bảng để hiện điểm mới
                    JOptionPane.showMessageDialog(null, "Đã cập nhật điểm mới nhất cho toàn bộ danh sách!");
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
        if (allLoaded) return;
        List<NguyenVongXettuyen> list = nguyenVongDAO.findAllPaged(currentOffset, PAGE_SIZE);
        if (list == null || list.isEmpty()) {
            allLoaded = true;
            return;
        }
        for (NguyenVongXettuyen nv : list) appendRow(nv);
        currentOffset += list.size();
        if (list.size() < PAGE_SIZE) allLoaded = true;
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
        // Nếu danh sách tìm được ít hơn 50 dòng (PAGE_SIZE), chắc chắn là đã hết dữ liệu
        if (list.size() < PAGE_SIZE) {
            allLoaded = true;
        } else {
            // Nếu đúng 50 dòng, có thể vẫn còn nữa (cần cuộn để tải thêm)
            allLoaded = false;
        }
    }

    private void appendRow(NguyenVongXettuyen nv) {
        tableModel.addRow(new Object[]{nv.getIdNv(),                                      // ID (ẩn)
                nv.getNnCccd(),                                    // CCCD
                nv.getNvMaNganh(),                                 // Mã Ngành
                nv.getNvTt(),                                      // Thứ Tự
                formatScore(nv.getDiemThxt()),                     // Điểm THXT
                formatScore(nv.getDiemUtqd()),                     // Điểm Ưu Tiên
                formatScore(nv.getDiemCong()),                     // Điểm Cộng
                formatScore(nv.getDiemXettuyen()),                 // Điểm Xét Tuyển (Tổng)
                nv.getNvKetqua() != null ? nv.getNvKetqua() : "Chờ xét", // Kết Quả
                nv.getTtPhuongThuc(),                              // Phương Thức
                nv.getTtThm()                                      // Tên Tổ Hợp
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
        String[] ptDisplay = {"0 - DGNL", "3 - VSAT", "4 - THPT"};
        String[] ptValues = {"0", "3", "4"};
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
        String[] statusOptions = {"Chờ xét", "Dưới sàn", "Đậu"};
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

        int result = JOptionPane.showConfirmDialog(this, panel, isEdit ? "Sửa Nguyện Vọng" : "Thêm Nguyện Vọng", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return null;

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
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MainFrame.C_BORDER), BorderFactory.createEmptyBorder(5, 15, 5, 15)));
    }

    public void tinhDiemXetTuyen(NguyenVongXettuyen nv) {
        ThiSinhXettuyen ts = thiSinhDAO.findByCCCD(nv.getNnCccd());
        double diemKhuVuc = getDiemKhuVuc(ts != null ? ts.getKhuVuc() : "3");
        double diemDoiTuong = getDiemDoiTuong(ts != null ? ts.getDoiTuong() : "");
        double tongMucUuTienNiemYet = diemKhuVuc + diemDoiTuong;

        double dthxt = getBestThxt(nv);
        DiemCongXettuyen dc = diemCongDAO.findByDcKeys(nv.getNnCccd(), nv.getNvMaNganh(), nv.getTtThm());
        double diemCong = 0.0;
        if (dc != null) {
            double diemAnh = (dc.getDiemCc() != null) ? dc.getDiemCc() : 0.0;
            double diemHSG = (dc.getDiemUtxt() != null) ? dc.getDiemUtxt() : 0.0;
            diemCong = Math.min(diemAnh + diemHSG, 3.0);
        }
        double dthgxt = getDiemToHopGocXetTuyen(dthxt, nv.getNvMaNganh(), nv.getTtThm());

        double tongDiemTho = dthgxt + diemCong;
        double diemUtqd = calculateDiemUtqd(tongDiemTho, tongMucUuTienNiemYet);
        double diemXetTuyen = tongDiemTho + diemUtqd;

        nv.setDiemThxt(dthxt);
        nv.setDiemUtqd(diemUtqd);
        nv.setDiemCong(diemCong);
        nv.setDiemXettuyen(diemXetTuyen);
    }

    public double getDiemToHopGocXetTuyen(double dthxt, String maNganh, String maToHopXetTuyen) {
        double doLech = 0.0;
        NganhTohop nganhTohop = nganhToHopDAO.findByMaNganhAndMaToHop(maNganh, maToHopXetTuyen);
        if (nganhTohop != null && nganhTohop.getDoLech() != null) {
            doLech = nganhTohop.getDoLech();
        }
        double diemQuyDoi = dthxt - doLech;
        return Math.round(diemQuyDoi * 100.0) / 100.0;
    }

    public double calculateDiemUtqd(double tongDiemTho, double tongMucUuTienNiemYet) {
        if (tongDiemTho >= 30.0) return 0.0;
        if (tongDiemTho >= 22.5) {
            double result = ((30.0 - tongDiemTho) / 7.5) * tongMucUuTienNiemYet;
            return Math.max(0, Math.round(result * 100.0) / 100.0);
        }
        return Math.round(tongMucUuTienNiemYet * 100.0) / 100.0;
    }

    public double getDiemKhuVuc(String khuVuc) {
        if (khuVuc == null) return 0.0;
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
        if (doiTuong == null || doiTuong.isEmpty()) return 0.0;
        try {
            String numericPart = doiTuong.replaceAll("[^0-9]", "");
            int dtVal = Integer.parseInt(numericPart);
            if (dtVal >= 1 && dtVal <= 5) return 2.0;
            if (dtVal >= 6 && dtVal <= 7) return 1.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
        return 0.0;
    }

    private double quyDoiDiemDGNL(String maToHop, double x) {
        BangQuydoi qd = bangQuyDoiDAO.findRangeForDGNL(maToHop, x);
        if (qd == null) return 0.0;
        double a = qd.getDDiemA();
        double b = qd.getDDiemB();
        double c = qd.getDDiemC();
        double d = qd.getDDiemD();
        // Công thức quy đổi sang thang 30
        double y = c + ((x - a) / (b - a)) * (d - c);
        return Math.round(y * 100.0) / 100.0;
    }

    private double tinhDiemMotTohop(DiemThiXettuyen dt, NganhTohop th) {
        // Lấy điểm 3 môn dựa trên mã viết tắt lưu trong th_mon1, th_mon2, th_mon3
        double d1 = getDiemByMaMon(dt, th.getThMon1());
        double d2 = getDiemByMaMon(dt, th.getThMon2());
        double d3 = getDiemByMaMon(dt, th.getThMon3());
        // Lấy hệ số (trọng số)
        int w1 = th.getHsMon1();
        int w2 = th.getHsMon2();
        int w3 = th.getHsMon3();
        int W = w1 + w2 + w3;
        if (W == 0) return 0.0;
        // Tính theo công thức trọng số quy về thang 30
        double tongDiem = ((d1 * w1 + d2 * w2 + d3 * w3) / W) * 3;
        return Math.round(tongDiem * 100.0) / 100.0;
    }

    // Hàm bổ trợ để map từ mã viết tắt (TO, LI, VA...) sang thuộc tính trong object DiemThi
    private double getDiemByMaMon(DiemThiXettuyen dt, String maMon) {
        if (maMon == null) return 0.0;
        switch (maMon.toUpperCase()) {
            case "TO":
                return dt.getTo();
            case "VA":
                return dt.getVa();
            case "LI":
                return dt.getLi();
            case "HO":
                return dt.getHo();
            case "SI":
                return dt.getSi();
            case "SU":
                return dt.getSu();
            case "DI":
                return dt.getDi();
            case "N1":
                return dt.getN1Cc();
            case "TI":
                return dt.getTi();
            case "KTPL":
                return dt.getKtpl();
            default:
                return 0.0;
        }
    }

    public double getBestThxt(NguyenVongXettuyen nv) {
        String cccd = nv.getNnCccd();
        String maNganh = nv.getNvMaNganh();
        List<DiemThiXettuyen> list = diemThiDAO.findByCCCD(cccd);
        if (list == null || list.isEmpty()) return 0.0;
        DiemThiXettuyen dt = list.get(0);
        if (dt == null) return 0.0;
        double maxScore = 0.0;
        // 1. Xét các tổ hợp môn truyền thống của ngành này
        List<NganhTohop> dsTohop = nganhToHopDAO.findByMaNganh(maNganh);
        for (NganhTohop th : dsTohop) {
            double diemTh = tinhDiemMotTohop(dt, th);
            if (diemTh > maxScore) {
                maxScore = diemTh;
                nv.setTtThm(th.getMaTohop());
            }
            if (dt.getNl1() != null) {
                maxScore = calculateDiemQuyDoiDGNL(dt, maxScore, th.getMaTohop(), nv);
            }
        }
        return maxScore;
    }

    private double calculateDiemQuyDoiDGNL(DiemThiXettuyen dt, double maxScore, String maToHop, NguyenVongXettuyen nv) {
        double diemQuyDoiDGNL = quyDoiDiemDGNL(maToHop, dt.getNl1());
        if (diemQuyDoiDGNL > maxScore) {
            maxScore = diemQuyDoiDGNL;
            nv.setTtThm(maToHop);
        }
        return maxScore;
    }
}
