package com.xettuyen.ui.panel;

import com.xettuyen.entity.DiemThiXettuyen;
import com.xettuyen.ui.MainFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DiemThiDialog extends JDialog {
    private JTextField txtCCCD, txtSBD, txtToan, txtLy, txtHoa, txtSinh, txtSu, txtDia, txtVan, txtAnh, txtNL1;
    private JComboBox<String> cbPT; // Chọn loại điểm
    private JButton btnSave, btnCancel;
    private boolean confirmed = false;
    private DiemThiXettuyen diemThi;

    public DiemThiDialog(Frame parent, DiemThiXettuyen dt) {
        super(parent, "Chi Tiết Điểm Thi", true);
        this.diemThi = (dt == null) ? new DiemThiXettuyen() : dt;
        
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // 1. HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(MainFrame.C_PRIMARY);
        JLabel lblHeader = new JLabel("NHẬP ĐIỂM THÍ SINH");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        headerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(headerPanel, BorderLayout.NORTH);

        // 2. FORM NHẬP LIỆU
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Khởi tạo ComboBox loại điểm (Yêu cầu 6)
        cbPT = new JComboBox<>(new String[]{"4 - Điểm THPT", "0 - Điểm ĐGNL", "3 - Điểm VSAT"});
        cbPT.setPreferredSize(new Dimension(150, 30));
        // Set giá trị mặc định nếu đang sửa
        if (diemThi.getDPhuongThuc() != null) {
            if (diemThi.getDPhuongThuc().equals("4")) cbPT.setSelectedIndex(0);
            else if (diemThi.getDPhuongThuc().equals("0")) cbPT.setSelectedIndex(1);
            else if (diemThi.getDPhuongThuc().equals("3")) cbPT.setSelectedIndex(2);
        }

        txtCCCD = createStyledField(diemThi.getCccd());
        if (diemThi.getCccd() != null && !diemThi.getCccd().isEmpty()) {
            txtCCCD.setEditable(false);
            txtCCCD.setBackground(new Color(245, 245, 245));
        }
        
        txtSBD = createStyledField(diemThi.getSoBaoDanh());
        txtToan = createStyledField(formatDiem(diemThi.getTo()));
        txtLy   = createStyledField(formatDiem(diemThi.getLi()));
        txtHoa  = createStyledField(formatDiem(diemThi.getHo()));
        txtSinh = createStyledField(formatDiem(diemThi.getSi()));
        txtSu   = createStyledField(formatDiem(diemThi.getSu()));
        txtDia  = createStyledField(formatDiem(diemThi.getDi()));
        txtVan  = createStyledField(formatDiem(diemThi.getVa()));
        txtAnh  = createStyledField(formatDiem(diemThi.getN1Thi()));
        txtNL1  = createStyledField(formatDiem(diemThi.getNl1())); // Điểm ĐGNL

        // Row 0: Loại điểm & CCCD
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Loại điểm:"), gbc);
        gbc.gridx = 1; formPanel.add(cbPT, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Số báo danh:"), gbc);
        gbc.gridx = 3; formPanel.add(txtSBD, gbc);

        // Row 1: CCCD
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("CCCD thí sinh:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; formPanel.add(txtCCCD, gbc);
        gbc.gridwidth = 1;

        // Row 2: Toán - Lý
        addFormField(formPanel, "Toán:", txtToan, 0, 2, gbc);
        addFormField(formPanel, "Vật Lý:", txtLy, 2, 2, gbc);

        // Row 3: Hóa - Sinh
        addFormField(formPanel, "Hóa Học:", txtHoa, 0, 3, gbc);
        addFormField(formPanel, "Sinh Học:", txtSinh, 2, 3, gbc);

        // Row 4: Sử - Địa
        addFormField(formPanel, "Lịch Sử:", txtSu, 0, 4, gbc);
        addFormField(formPanel, "Địa Lý:", txtDia, 2, 4, gbc);

        // Row 5: Văn - Anh
        addFormField(formPanel, "Ngữ Văn:", txtVan, 0, 5, gbc);
        addFormField(formPanel, "Ngoại Ngữ:", txtAnh, 2, 5, gbc);

        // Row 6: Đánh giá năng lực (NL1)
        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(new JLabel("Điểm ĐGNL:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; formPanel.add(txtNL1, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 3. NÚT CHỨC NĂNG
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(new Color(250, 250, 250));

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.addActionListener(e -> dispose());

        btnSave = new JButton("Lưu dữ liệu");
        btnSave.setBackground(MainFrame.C_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setPreferredSize(new Dimension(120, 35));
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> handleSave());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    private JTextField createStyledField(String value) {
        JTextField field = new JTextField(value != null ? value : "");
        field.setPreferredSize(new Dimension(120, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    private void addFormField(JPanel p, String label, JTextField f, int col, int row, GridBagConstraints gbc) {
        gbc.gridx = col; gbc.gridy = row;
        p.add(new JLabel(label), gbc);
        gbc.gridx = col + 1;
        p.add(f, gbc);
    }

    private String formatDiem(Double d) {
        return (d == null || d == 0.0) ? "" : String.valueOf(d);
    }

    private void handleSave() {
        try {
            String cccd = txtCCCD.getText().trim();
            if (cccd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập CCCD!");
                return;
            }

            // Gán giá trị vào Entity
            diemThi.setCccd(cccd);
            diemThi.setSoBaoDanh(txtSBD.getText().trim());
            
            // Cắt chuỗi lấy số đầu của ComboBox (4, 0, 3)
            String pt = ((String) cbPT.getSelectedItem()).substring(0, 1);
            diemThi.setDPhuongThuc(pt);

            // Parse điểm (nếu trống thì mặc định 0.0)
            diemThi.setTo(parse(txtToan.getText()));
            diemThi.setLi(parse(txtLy.getText()));
            diemThi.setHo(parse(txtHoa.getText()));
            diemThi.setSi(parse(txtSinh.getText()));
            diemThi.setSu(parse(txtSu.getText()));
            diemThi.setDi(parse(txtDia.getText()));
            diemThi.setVa(parse(txtVan.getText()));
            diemThi.setN1Thi(parse(txtAnh.getText()));
            diemThi.setNl1(parse(txtNL1.getText()));

            // Kiểm tra ràng buộc điểm THPT
            if (pt.equals("4")) {
                if (isInvalidPoint(diemThi.getTo()) || isInvalidPoint(diemThi.getVa())) {
                    JOptionPane.showMessageDialog(this, "Điểm THPT phải nằm trong khoảng 0 - 10!");
                    return;
                }
            }

            confirmed = true;
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Điểm nhập vào phải là số!");
        }
    }

    private Double parse(String s) {
        if (s == null || s.trim().isEmpty()) return 0.0;
        return Double.parseDouble(s.trim());
    }

    private boolean isInvalidPoint(Double d) {
        return d != null && (d < 0 || d > 10);
    }

    public boolean isConfirmed() { return confirmed; }
    public DiemThiXettuyen getDiemThi() { return diemThi; }
}