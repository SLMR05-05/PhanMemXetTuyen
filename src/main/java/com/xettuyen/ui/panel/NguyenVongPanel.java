package com.xettuyen.ui.panel;

import com.xettuyen.dao.DAOFactory;
import com.xettuyen.dao.NguyenVongDAO;
import com.xettuyen.entity.NguyenVongXettuyen;
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
    private JButton btnSearch, btnTinhDiem, btnImport, btnAdd, btnEdit, btnDelete;
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private static final int PAGE_SIZE = 50;
    private int currentOffset = 0;
    private boolean isSearchMode = false;
    private boolean allLoaded = false;

    private final NguyenVongDAO nguyenVongDAO = DAOFactory.getNguyenVongDAO();
    private final XetTuyenService xetTuyenService = new XetTuyenService(DAOFactory.getDiemThiDAO(), DAOFactory.getDiemCongDAO(), DAOFactory.getBangQuyDoiDAO(), DAOFactory.getNguyenVongDAO(), DAOFactory.getNganhToHopDAO());

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
        btnTinhDiem = new JButton("Tính điểm xét tuyển");
        btnImport = new JButton("Import Excel 📄");
        for (JButton b : new JButton[]{btnSearch, btnTinhDiem, btnImport})
            styleBasicButton(b);

        searchPanel.add(txtCCCD);
        searchPanel.add(btnSearch);
        searchPanel.add(btnTinhDiem);
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(btnImport, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER: Table ---
        // Cập nhật danh sách cột theo yêu cầu mới
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
        for (JButton b : new JButton[]{btnAdd, btnEdit, btnDelete})
            styleBasicButton(b);
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Sự kiện ---
        btnSearch.addActionListener(e -> {
            String cccd = txtCCCD.getText().trim();
            if (cccd.isEmpty()) resetAndLoadAll();
            else loadByCccd(cccd);
        });

        txtCCCD.addActionListener(e -> btnSearch.doClick());

        btnTinhDiem.addActionListener(e -> {
            String cccd = txtCCCD.getText().trim();
            if (cccd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập CCCD trước khi tính điểm!");
                return;
            }
            try {
                xetTuyenService.tinhDiemXetTuyenCuaThiSinh(cccd);
                JOptionPane.showMessageDialog(this, "Đã tính toán và cập nhật điểm thành công!");
                loadByCccd(cccd);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tính điểm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

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

                    // 2. Đẩy cả List xuống DB bằng hàm Batch vừa tạo
                    // Tốc độ sẽ cực nhanh vì chỉ mở 1 Connection/Transaction duy nhất
                    nguyenVongDAO.saveOrUpdateAll(list);

                    return list.size();
                }

                @Override
                protected void done() {
                    try {
                        int total = get();
                        if (total > 0) {
                            JOptionPane.showMessageDialog(NguyenVongPanel.this, "Đã xử lý xong " + total + " nguyện vọng (Thêm mới/Cập nhật).");
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
            NguyenVongXettuyen nv = showEditDialog(null, txtCCCD.getText().trim());
            if (nv == null) return;
            try {
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

        resetAndLoadAll();
    }

    private void resetAndLoadAll() {
        isSearchMode = false;
        allLoaded = false;
        currentOffset = 0;
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
        isSearchMode = true;
        allLoaded = true;
        tableModel.setRowCount(0);
        List<NguyenVongXettuyen> list = nguyenVongDAO.findByCCCD(cccd);
        if (list == null || list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu.");
            return;
        }
        for (NguyenVongXettuyen nv : list) appendRow(nv);
    }

    /**
     * Cập nhật hàm appendRow để đổ đúng dữ liệu vào các cột mới
     */
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

        // --- Các ô nhập liệu thông tin cơ bản ---
        JTextField fCccd = new JTextField(isEdit ? existing.getNnCccd() : defaultCccd, 15);
        JTextField fMaNganh = new JTextField(isEdit ? existing.getNvMaNganh() : "", 15);
        JTextField fThuTu = new JTextField(isEdit ? String.valueOf(existing.getNvTt()) : "", 5);
        JTextField fPhuongThuc = new JTextField(isEdit ? existing.getTtPhuongThuc() : "", 15);
        JTextField fToHop = new JTextField(isEdit ? existing.getTtThm() : "", 15);

        // --- Ô lựa chọn Kết quả (Combo Box) ---
        String[] statusOptions = {"Chờ xét", "duoisan", "yes"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        if (isEdit && existing.getNvKetqua() != null) {
            cbStatus.setSelectedItem(existing.getNvKetqua());
        }

        // --- Các ô hiển thị điểm (Read-only để tránh sửa nhầm điểm hệ thống đã tính) ---
        JTextField fTongDiem = new JTextField(isEdit && existing.getDiemXettuyen() != null ? String.valueOf(existing.getDiemXettuyen()) : "Chưa tính", 10);
        fTongDiem.setEditable(false);
        fTongDiem.setBackground(new Color(240, 240, 240)); // Màu xám nhẹ để biết là không sửa được

        // --- Giao diện Panel ---
        JPanel panel = new JPanel(new java.awt.GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("CCCD:"));
        panel.add(fCccd);
        panel.add(new JLabel("Mã ngành:"));
        panel.add(fMaNganh);
        panel.add(new JLabel("Thứ tự NV:"));
        panel.add(fThuTu);
        panel.add(new JLabel("Phương thức:"));
        panel.add(fPhuongThuc);
        panel.add(new JLabel("Tên tổ hợp:"));
        panel.add(fToHop);
        panel.add(new JLabel("Tổng điểm:"));
        panel.add(fTongDiem); // Cho xem điểm tổng
        panel.add(new JLabel("Kết quả:"));
        panel.add(cbStatus);  // Combo box lựa chọn

        int result = JOptionPane.showConfirmDialog(this, panel, isEdit ? "Sửa Nguyện Vọng" : "Thêm Nguyện Vọng", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return null;

        try {
            NguyenVongXettuyen nv = isEdit ? existing : new NguyenVongXettuyen();

            // Cập nhật thông tin từ các ô nhập
            nv.setNnCccd(fCccd.getText().trim());
            nv.setNvMaNganh(fMaNganh.getText().trim());
            nv.setNvTt(Integer.parseInt(fThuTu.getText().trim()));
            nv.setTtPhuongThuc(fPhuongThuc.getText().trim());
            nv.setTtThm(fToHop.getText().trim());

            // Lấy giá trị từ Combo Box
            nv.setNvKetqua(cbStatus.getSelectedItem().toString());

            // Cập nhật lại Keys (rất quan trọng cho logic Batch Update/Save)
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
}
