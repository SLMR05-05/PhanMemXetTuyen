package com.xettuyen.ui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.xettuyen.entity.DiemCongXettuyen;
import com.xettuyen.ui.MainFrame;

public class DiemCongDialog extends JDialog {
    private JTextField txtCCCD, txtMaNganh, txtMaToHop, txtPhuongThuc, txtDiemCC, txtDiemUT;
    private JButton btnSave, btnCancel;
    private boolean confirmed = false;
    private DiemCongXettuyen diemCong;

    public DiemCongDialog(Frame parent, DiemCongXettuyen dc) {
        super(parent, "Thông Tin Điểm Ưu Tiên", true);
        this.diemCong = (dc == null) ? new DiemCongXettuyen() : dc;
        
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // 1. HEADER (Giống trang Quản lý điểm thi)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(MainFrame.C_PRIMARY);
        JLabel lblHeader = new JLabel("CẬP NHẬT ĐIỂM ƯU TIÊN");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        headerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(headerPanel, BorderLayout.NORTH);

        // 2. FORM NHẬP LIỆU (Sử dụng GridBagLayout để căn chỉnh đẹp)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Khởi tạo các ô nhập
        txtCCCD = createStyledField(diemCong.getTsCccd());
        if (diemCong.getTsCccd() != null && !diemCong.getTsCccd().isEmpty()) {
            txtCCCD.setEditable(false); // Khóa CCCD khi sửa
            txtCCCD.setBackground(new Color(245, 245, 245));
        }
        
        txtMaNganh = createStyledField(diemCong.getMaNganh());
        txtMaToHop = createStyledField(diemCong.getMaTohop());
        txtPhuongThuc = createStyledField(diemCong.getPhuongThuc());
        txtDiemCC = createStyledField(formatDiem(diemCong.getDiemCc()));
        txtDiemUT = createStyledField(formatDiem(diemCong.getDiemUtxt()));

        // Add components vào Grid
        // Dòng 0: CCCD
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("CCCD Thí sinh:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; formPanel.add(txtCCCD, gbc);
        gbc.gridwidth = 1; // Reset width

        // Dòng 1: Mã Ngành - Mã Tổ Hợp
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Mã Ngành:"), gbc);
        gbc.gridx = 1; formPanel.add(txtMaNganh, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Mã Tổ Hợp:"), gbc);
        gbc.gridx = 3; formPanel.add(txtMaToHop, gbc);

        // Dòng 2: Phương thức - (Trống)
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Phương thức:"), gbc);
        gbc.gridx = 1; formPanel.add(txtPhuongThuc, gbc);

        // Dòng 3: Điểm Chứng Chỉ (CC) - Điểm Ưu Tiên (UT)
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Điểm Chứng chỉ:"), gbc);
        gbc.gridx = 1; formPanel.add(txtDiemCC, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Điểm Ưu tiên:"), gbc);
        gbc.gridx = 3; formPanel.add(txtDiemUT, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 3. NÚT CHỨC NĂNG (Dưới cùng)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(new Color(250, 250, 250));

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dispose());

        btnSave = new JButton("Lưu dữ liệu");
        btnSave.setBackground(MainFrame.C_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setPreferredSize(new Dimension(120, 35));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.addActionListener(e -> handleSave());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private JTextField createStyledField(String value) {
        JTextField field = new JTextField(value != null ? value : "");
        field.setPreferredSize(new Dimension(150, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    private String formatDiem(Double d) {
        if (d == null) return "0.0";
        return String.valueOf(d);
    }

    private void handleSave() {
        try {
            String cccd = txtCCCD.getText().trim();
            String manganh = txtMaNganh.getText().trim();
            
            if (cccd.isEmpty() || manganh.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ CCCD và Mã Ngành!");
                return;
            }

            // Gán giá trị vào Entity
            diemCong.setTsCccd(txtCCCD.getText().trim());
            diemCong.setMaNganh(txtMaNganh.getText().trim());
            diemCong.setMaTohop(txtMaToHop.getText().trim());
            diemCong.setPhuongThuc(txtPhuongThuc.getText().trim());
        
            double dCC = Double.parseDouble(txtDiemCC.getText().trim());
            double dUT = Double.parseDouble(txtDiemUT.getText().trim());
            diemCong.setDiemCc(dCC);
            diemCong.setDiemUtxt(dUT);
            diemCong.setDiemTong(dCC + dUT);
            
            // Tạo key duy nhất để tránh trùng lặp
            diemCong.setDcKeys(diemCong.getTsCccd() + "_" + diemCong.getMaNganh() + "_" + diemCong.getMaTohop());

            confirmed = true;
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Điểm phải là số (VD: 1.0)!");
        }
    }

    public boolean isConfirmed() { return confirmed; }
    public DiemCongXettuyen getDiemCong() { return diemCong; }
}