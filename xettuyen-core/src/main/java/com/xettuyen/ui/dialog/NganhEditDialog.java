package com.xettuyen.ui.dialog;

import com.xettuyen.entity.Nganh;
import com.xettuyen.entity.NganhTohop;
import com.xettuyen.service.NganhService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NganhEditDialog extends JDialog {
    private Nganh nganh;
    private NganhService nganhService = new NganhService();
    private boolean isSaved = false;
    private Map<String, JTextField> fields = new HashMap<>();

    public NganhEditDialog(Frame owner, Nganh nganh) {
        super(owner, "Chỉnh sửa ngành: " + nganh.getTenNganh(), true);
        this.nganh = nganh;
        initComponents();
        this.setSize(1200, 750);
        this.setLocationRelativeTo(owner);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        splitPane.setLeftComponent(new JScrollPane(createEditForm())); 
        splitPane.setRightComponent(createTohopTable());
        
        splitPane.setDividerLocation(550); 
        splitPane.setDividerSize(8);
        this.add(splitPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnSave = new JButton("💾 Lưu Thay Đổi");
        JButton btnClose = new JButton("❌ Đóng");
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> handleSave());
        btnClose.addActionListener(e -> dispose());
        btnPanel.add(btnSave); btnPanel.add(btnClose);
        this.add(btnPanel, BorderLayout.SOUTH);
    }

private JPanel createTohopTable() {
    JPanel p = new JPanel(new BorderLayout());
    p.setBorder(new TitledBorder("Danh sách Tổ hợp (Hệ số môn)"));

    // --- TẠO THANH TOOLBAR CHO BẢNG ---
    JPanel tableToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btnAddTohop = new JButton("➕ Thêm Tổ Hợp");
    btnAddTohop.setFont(new Font("Segoe UI", Font.BOLD, 12));
    btnAddTohop.setBackground(new Color(230, 240, 255));
    btnAddTohop.setForeground(Color.BLACK);
    
    tableToolbar.add(btnAddTohop);
    p.add(tableToolbar, BorderLayout.NORTH); // Đưa nút lên phía trên bảng

    // --- CẤU HÌNH BẢNG (Giữ nguyên phần Renderer và MouseListener cũ) ---
    String[] cols = {"Mã TH", "Môn 1 (HS)", "Môn 2 (HS)", "Môn 3 (HS)", "Sửa", "Xóa"};
    DefaultTableModel model = new DefaultTableModel(cols, 0) {
        @Override 
        public boolean isCellEditable(int r, int c) { return c >= 4; }
    };

    JTable table = new JTable(model);
    table.setRowHeight(40); 
    loadTohopData(table);

    // Renderer cho icon (đã nhỏ lại 1/3 như bạn yêu cầu)
    DefaultTableCellRenderer iconRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20)); 
            if (!isSelected) {
                if (column == 4) label.setForeground(new Color(0, 123, 255));
                if (column == 5) label.setForeground(new Color(220, 53, 69));
            }
            return label;
        }
    };
    table.getColumnModel().getColumn(4).setCellRenderer(iconRenderer);
    table.getColumnModel().getColumn(5).setCellRenderer(iconRenderer);

    // --- SỰ KIỆN NÚT THÊM TỔ HỢP ---
    btnAddTohop.addActionListener(e -> {
        // Tạo một đối tượng tổ hợp mới và gán sẵn mã ngành hiện tại
        NganhTohop newTH = new NganhTohop();
        newTH.setMaNganh(nganh.getMaNganh());
        
        // Mở form tohop ở chế độ thêm mới
        tohop dialog = new tohop((Frame) SwingUtilities.getWindowAncestor(this), newTH);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadTohopData(table); // Load lại bảng sau khi thêm
        }
    });

    // Sự kiện Click bảng (Sửa/Xóa) - Giữ nguyên như cũ
    table.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            int col = table.columnAtPoint(e.getPoint());
            int row = table.rowAtPoint(e.getPoint());
            if (row == -1) return;

            NganhService.NganhDetail detail = nganhService.getNganhDetail(nganh.getMaNganh());
            NganhTohop selectedTH = detail.nganhToHopList.get(row);

            if (col == 4) { // Sửa
                tohop dialog = new tohop((Frame) SwingUtilities.getWindowAncestor(NganhEditDialog.this), selectedTH);
                dialog.setVisible(true);
                if (dialog.isSaved()) loadTohopData(table);
            }
            if (col == 5) { // Xóa
                int confirm = JOptionPane.showConfirmDialog(NganhEditDialog.this, "Xóa tổ hợp này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (nganhService.deleteNganhTohop(selectedTH.getId())) loadTohopData(table);
                }
            }
        }
    });

    p.add(new JScrollPane(table), BorderLayout.CENTER);
    return p;
}
// Cập nhật hàm load dữ liệu để thêm icon vào 2 cột cuối
private void loadTohopData(JTable table) {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    model.setRowCount(0);
    NganhService.NganhDetail detail = nganhService.getNganhDetail(nganh.getMaNganh());
    if (detail != null && detail.nganhToHopList != null) {
        for (NganhTohop th : detail.nganhToHopList) {
            model.addRow(new Object[]{ 
                th.getMaTohop(), 
                th.getThMon1() + " (" + th.getHsMon1() + ")", 
                th.getThMon2() + " (" + th.getHsMon2() + ")", 
                th.getThMon3() + " (" + th.getHsMon3() + ")", 
                "📝", // Icon sửa có màu
                "🗑️"  // Icon xóa dấu X đỏ hoặc 🗑️ thùng rác
            });
        }
    }
}

    private JPanel createEditForm() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel p1 = new JPanel(new GridLayout(0, 2, 10, 10));
        p1.setBorder(new TitledBorder("Thông tin định danh"));
        addInputField(p1, "Mã Ngành", nganh.getMaNganh(), false);
        addInputField(p1, "Tên Ngành", nganh.getTenNganh(), true);
        addInputField(p1, "Tổ Hợp Gốc", nganh.getNTohopGoc(), true);
        container.add(p1);

        JPanel p2 = new JPanel(new GridLayout(0, 2, 10, 10));
        p2.setBorder(new TitledBorder("Chỉ tiêu & Điểm số"));
        addInputField(p2, "Chỉ Tiêu", String.valueOf(nganh.getNChiTieu()), true);
        addInputField(p2, "Điểm Sàn", String.valueOf(nganh.getNDiemSan()), true);
        addInputField(p2, "Điểm Chuẩn", String.valueOf(nganh.getNDiemTrungTuyen()), true);
        container.add(p2);
        
        return container;
    }

    private void addInputField(JPanel p, String label, String value, boolean editable) {
        p.add(new JLabel(label));
        JTextField txt = new JTextField(value != null && !value.equals("null") ? value : "");
        txt.setEditable(editable);
        fields.put(label, txt);
        p.add(txt);
    }

    private void handleSave() {
        try {
            nganh.setTenNganh(fields.get("Tên Ngành").getText());
            nganh.setNChiTieu(Integer.parseInt(fields.get("Chỉ Tiêu").getText()));
            nganh.setNDiemSan(safeParseDouble(fields.get("Điểm Sàn").getText()));
            nganh.setNDiemTrungTuyen(safeParseDouble(fields.get("Điểm Chuẩn").getText()));
            if (nganhService.updateNganh(nganh)) {
                JOptionPane.showMessageDialog(this, "Đã lưu!");
                isSaved = true; dispose();
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi!"); }
    }

    private int safeParseInt(String v) { try { return Integer.parseInt(v.trim()); } catch (Exception e) { return 0; } }
    private double safeParseDouble(String v) { try { return Double.parseDouble(v.trim()); } catch (Exception e) { return 0.0; } }
    public boolean isSaved() { return isSaved; }
}