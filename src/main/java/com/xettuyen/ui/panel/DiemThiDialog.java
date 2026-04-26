package com.xettuyen.ui.panel;

import com.xettuyen.entity.DiemThiXettuyen;
import com.xettuyen.ui.MainFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DiemThiDialog extends JDialog {
    private JTextField txtCCCD, txtSBD;
    private JTextField txtToan, txtVan, txtAnhThi, txtAnhCC, txtLy, txtHoa, txtSinh, txtSu, txtDia, txtTin, txtCNCN, txtCNNN, txtKTPL, txtNL1, txtNK1, txtNK2, txtLoaiCC;
    private JButton btnSave, btnCancel;
    private boolean confirmed = false;
    private DiemThiXettuyen diemThi;
    private JComboBox<String> cbPT;

    public DiemThiDialog(Frame parent, DiemThiXettuyen dt) {
        super(parent, "Chi Tiết Điểm Thí Sinh", true);
        this.diemThi = (dt == null) ? new DiemThiXettuyen() : dt;
        
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- HEADER ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(MainFrame.C_PRIMARY);
        JLabel lblHeader = new JLabel("BẢNG NHẬP ĐIỂM CHI TIẾT");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(12, 0, 12, 0));
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        // --- FORM NHẬP LIỆU (GridBagLayout) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);

        // Khởi tạo các ô nhập (Thông tin chung)
        txtCCCD = createField(diemThi.getCccd(), 15, true);
        txtSBD = createField(diemThi.getSoBaoDanh(), 10, true);
        txtLoaiCC = createField(diemThi.getLoaiChungChi(), 10, true);

        String[] options = {"4 - THPT", "0 - ĐGNL", "3 - VSAT"};
        cbPT = new JComboBox<>(options);
        if (diemThi.getDPhuongThuc() != null) {
            String pt = diemThi.getDPhuongThuc();
            if (pt.equals("4")) cbPT.setSelectedIndex(0);
            else if (pt.equals("0")) cbPT.setSelectedIndex(1);
            else if (pt.equals("3")) cbPT.setSelectedIndex(2);
        }

        // Khởi tạo các ô nhập (Môn học - lấy từ Entity, null hiển thị trống)
        txtToan = createField(f(diemThi.getTo()), 6, true);
        txtVan = createField(f(diemThi.getVa()), 6, true);
        txtAnhThi = createField(f(diemThi.getN1Thi()), 6, true);
        txtAnhCC = createField(f(diemThi.getN1Cc()), 6, true);
        txtLy = createField(f(diemThi.getLi()), 6, true);
        txtHoa = createField(f(diemThi.getHo()), 6, true);
        txtSinh = createField(f(diemThi.getSi()), 6, true);
        txtSu = createField(f(diemThi.getSu()), 6, true);
        txtDia = createField(f(diemThi.getDi()), 6, true);
        txtTin = createField(f(diemThi.getTi()), 6, true);
        txtCNCN = createField(f(diemThi.getCncn()), 6, true);
        txtCNNN = createField(f(diemThi.getCnnn()), 6, true);
        txtKTPL = createField(f(diemThi.getKtpl()), 6, true);
        txtNL1 = createField(f(diemThi.getNl1()), 6, true);
        txtNK1 = createField(f(diemThi.getNk1()), 6, true);
        txtNK2 = createField(f(diemThi.getNk2()), 6, true);

        // --- Layout các trường lên Form ---
        // Hàng 0: CCCD & PT
        addF(formPanel, "CCCD:", txtCCCD, 0, 0, gbc);
        addF(formPanel, "Phương thức:", cbPT, 2, 0, gbc);

        // Hàng 1: SBD
        addF(formPanel, "SBD:", txtSBD, 0, 1, gbc);

        // Hàng 2 -> 9: Chia 2 cột môn học
        addF(formPanel, "Toán:", txtToan, 0, 2, gbc);        addF(formPanel, "Ngữ văn:", txtVan, 2, 2, gbc);
        addF(formPanel, "Anh (Thi):", txtAnhThi, 0, 3, gbc);  addF(formPanel, "Anh (CC):", txtAnhCC, 2, 3, gbc);
        addF(formPanel, "Vật lý:", txtLy, 0, 4, gbc);       addF(formPanel, "Hóa học:", txtHoa, 2, 4, gbc);
        addF(formPanel, "Sinh học:", txtSinh, 0, 5, gbc);     addF(formPanel, "Lịch sử:", txtSu, 2, 5, gbc);
        addF(formPanel, "Địa lý:", txtDia, 0, 6, gbc);        addF(formPanel, "NL1:", txtNL1, 2, 6, gbc);
        addF(formPanel, "Tin học:", txtTin, 0, 7, gbc);       addF(formPanel, "Công nghệ CN:", txtCNCN, 2, 7, gbc);
        addF(formPanel, "Công nghệ NN:", txtCNNN, 0, 8, gbc);  addF(formPanel, "KT Pháp luật:", txtKTPL, 2, 8, gbc);
        addF(formPanel, "Năng khiếu 1:", txtNK1, 0, 9, gbc);  addF(formPanel, "Năng khiếu 2:", txtNK2, 2, 9, gbc);
        addF(formPanel, "Loại chứng chỉ:", txtLoaiCC, 0, 10, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- FOOTER (Nút bấm) ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnCancel = new JButton("Hủy bỏ");
        btnCancel.addActionListener(e -> dispose());

        btnSave = new JButton("Lưu dữ liệu");
        btnSave.setBackground(MainFrame.C_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.addActionListener(e -> handleSave());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    // Hàm chuyển Double -> String (Null hoặc 0.0 -> rỗng)
    private String f(Double d) {
        return (d == null || d == 0.0) ? "" : String.valueOf(d);
    }

    // Hàm chuyển String -> Double (Rỗng -> null)
    private Double p(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private JTextField createField(String val, int cols, boolean editable) {
        JTextField f = new JTextField(val, cols);
        f.setPreferredSize(new Dimension(160, 30));
        f.setEditable(editable);
        if (!editable) f.setBackground(new Color(245, 245, 245));
        return f;
    }

    private void addF(JPanel p, String label, Component c, int x, int y, GridBagConstraints g) {
        g.gridx = x; g.gridy = y;
        g.anchor = GridBagConstraints.EAST;
        p.add(new JLabel(label), g);
        g.gridx = x + 1;
        g.anchor = GridBagConstraints.WEST;
        p.add(c, g);
    }

    private void handleSave() {
        try {
            if (txtCCCD.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng không để trống CCCD!");
                return;
            }

            diemThi.setCccd(txtCCCD.getText().trim());
            diemThi.setSoBaoDanh(txtSBD.getText().trim());
            String selectedPT = ((String) cbPT.getSelectedItem()).substring(0, 1);
            diemThi.setDPhuongThuc(selectedPT);

            // Gán giá trị điểm từ các ô nhập
            diemThi.setTo(p(txtToan.getText()));
            diemThi.setVa(p(txtVan.getText()));
            diemThi.setN1Thi(p(txtAnhThi.getText()));
            diemThi.setN1Cc(p(txtAnhCC.getText()));
            diemThi.setLi(p(txtLy.getText()));
            diemThi.setHo(p(txtHoa.getText()));
            diemThi.setSi(p(txtSinh.getText()));
            diemThi.setSu(p(txtSu.getText()));
            diemThi.setDi(p(txtDia.getText()));
            diemThi.setTi(p(txtTin.getText()));
            diemThi.setCncn(p(txtCNCN.getText()));
            diemThi.setCnnn(p(txtCNNN.getText()));
            diemThi.setKtpl(p(txtKTPL.getText()));
            diemThi.setNl1(p(txtNL1.getText()));
            diemThi.setNk1(p(txtNK1.getText()));
            diemThi.setNk2(p(txtNK2.getText()));
            diemThi.setLoaiChungChi(txtLoaiCC.getText().trim());

            confirmed = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu!");
        }
    }

    public boolean isConfirmed() { return confirmed; }
    public DiemThiXettuyen getDiemThi() { return diemThi; }
}