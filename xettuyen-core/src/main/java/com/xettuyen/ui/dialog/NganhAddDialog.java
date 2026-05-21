package com.xettuyen.ui.dialog;

import com.xettuyen.entity.Nganh;
import com.xettuyen.service.NganhService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NganhAddDialog extends JDialog {
    private NganhService nganhService = new NganhService();
    private boolean isSaved = false;

    // Map chứa các JTextField để dễ dàng quản lý dữ liệu nhập vào
    private Map<String, JTextField> fields = new HashMap<>();

    public NganhAddDialog(Frame owner) {
        super(owner, "Thêm Ngành Học Mới", true);
        initComponents();
        this.setSize(650, 750); // Kích thước vừa đủ cho form nhập liệu đơn
        this.setLocationRelativeTo(owner);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());

        // Nội dung nhập liệu bọc trong JScrollPane đề phòng màn hình nhỏ
        JPanel formPanel = createAddForm();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scrollPane, BorderLayout.CENTER);

        // Thanh nút bấm ở dưới
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnAdd = new JButton("➕ Xác Nhận Thêm");
        JButton btnCancel = new JButton("❌ Hủy");

        // Chỉnh màu chữ đen theo ý bạn
        btnAdd.setBackground(new Color(215, 255, 225)); // Màu lục nhạt
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnCancel.setBackground(new Color(255, 220, 225)); // Màu đỏ nhạt
        btnCancel.setForeground(Color.BLACK);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnAdd.addActionListener(e -> handleAdd());
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnAdd);
        btnPanel.add(btnCancel);
        this.add(btnPanel, BorderLayout.SOUTH);
    }

    private JPanel createAddForm() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Nhóm 1: Thông tin cơ bản
        JPanel p1 = new JPanel(new GridLayout(0, 2, 10, 10));
        p1.setBorder(new TitledBorder("Thông tin định danh"));
        addInputField(p1, "Mã Ngành", "", true);
        addInputField(p1, "Tên Ngành", "", true);
        addInputField(p1, "Tổ Hợp Gốc", "", true);
        container.add(p1);

        // Nhóm 2: Chỉ tiêu & Điểm số
        JPanel p2 = new JPanel(new GridLayout(0, 2, 10, 10));
        p2.setBorder(new TitledBorder("Chỉ tiêu & Điểm số"));
        addInputField(p2, "Chỉ Tiêu", "0", true);
        addInputField(p2, "Điểm Sàn", "0.0", true);
        addInputField(p2, "Điểm Chuẩn", "0.0", true);
        addInputField(p2, "Tuyển Thẳng", "", true);
        addInputField(p2, "ĐGNL (N_DGNL)", "", true);
        addInputField(p2, "THPT (N_THPT)", "", true);
        addInputField(p2, "VSAT (N_VSAT)", "", true);
        container.add(p2);

        // Nhóm 3: Số lượng thực tế
        JPanel p3 = new JPanel(new GridLayout(0, 2, 10, 10));
        p3.setBorder(new TitledBorder("Số lượng thực tế (SL)"));
        addInputField(p3, "SL XTT", "0", true);
        addInputField(p3, "SL ĐGNL", "0", true);
        addInputField(p3, "SL VSAT", "0", true);
        addInputField(p3, "SL THPT", "", true);
        container.add(p3);

        return container;
    }

    private void addInputField(JPanel p, String label, String value, boolean editable) {
        p.add(new JLabel(label));
        JTextField txt = new JTextField(value);
        txt.setEditable(editable);
        fields.put(label, txt);
        p.add(txt);
    }

    private void handleAdd() {
        try {
            // Kiểm tra các trường bắt buộc
            String ma = fields.get("Mã Ngành").getText().trim();
            String ten = fields.get("Tên Ngành").getText().trim();
            
            if (ma.isEmpty() || ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã ngành và Tên ngành!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tạo đối tượng Entity mới
            Nganh n = new Nganh();
            n.setMaNganh(ma);
            n.setTenNganh(ten);
            n.setNTohopGoc(fields.get("Tổ Hợp Gốc").getText());
            n.setNTuyenThang(fields.get("Tuyển Thẳng").getText());
            n.setNDgNl(fields.get("ĐGNL (N_DGNL)").getText());
            n.setNThpt(fields.get("THPT (N_THPT)").getText());
            n.setNVsat(fields.get("VSAT (N_VSAT)").getText());
            n.setSlThpt(fields.get("SL THPT").getText());

            // Ép kiểu số an toàn
            n.setNChiTieu(safeParseInt(fields.get("Chỉ Tiêu").getText()));
            n.setNDiemSan(safeParseDouble(fields.get("Điểm Sàn").getText()));
            n.setNDiemTrungTuyen(safeParseDouble(fields.get("Điểm Chuẩn").getText()));
            n.setSlXtt(safeParseInt(fields.get("SL XTT").getText()));
            n.setSlDgNl(safeParseInt(fields.get("SL ĐGNL").getText()));
            n.setSlVsat(safeParseInt(fields.get("SL VSAT").getText()));

            // Gọi Service để lưu vào DB
            if (nganhService.saveNganh(n)) {
                JOptionPane.showMessageDialog(this, "Thêm ngành mới thành công!");
                isSaved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm mới thất bại, có thể mã ngành đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu nhập vào: " + ex.getMessage());
        }
    }

    private int safeParseInt(String value) {
        try { return (value == null || value.trim().isEmpty()) ? 0 : Integer.parseInt(value.trim()); } catch (Exception e) { return 0; }
    }

    private double safeParseDouble(String value) {
        try { return (value == null || value.trim().isEmpty()) ? 0.0 : Double.parseDouble(value.trim()); } catch (Exception e) { return 0.0; }
    }

    public boolean isSaved() { return isSaved; }
}