package com.xettuyen.ui.panel;

import com.xettuyen.entity.DiemThiXettuyen;
import com.xettuyen.ui.MainFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DiemThiDialog extends JDialog {
    private JTextField txtCCCD, txtSBD, txtToan, txtVan, txtAnh, txtLy, txtHoa, txtSinh, txtSu, txtDia, txtNL1;
    private JTextField txtCNCN, txtCNNN, txtTI, txtKTPL, txtNK1, txtNK2;
    private JComboBox<String> cbPT;
    private JButton btnSave, btnCancel;
    private boolean confirmed = false;
    private DiemThiXettuyen diemThi;

    public DiemThiDialog(Frame parent, DiemThiXettuyen dt) {
        super(parent, "Chi Tiết Điểm Thí Sinh", true);
        this.diemThi = (dt == null) ? new DiemThiXettuyen() : dt;
        setLayout(new BorderLayout());

        // 1. HEADER
        JPanel header = new JPanel();
        header.setBackground(MainFrame.C_PRIMARY);
        JLabel lbl = new JLabel("BẢNG NHẬP ĐIỂM CHI TIẾT");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setBorder(new EmptyBorder(12, 0, 12, 0));
        header.add(lbl);
        add(header, BorderLayout.NORTH);

        // 2. FORM NHẬP LIỆU
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 10, 7, 10); // Tăng khoảng cách dòng

        // Khởi tạo các ô nhập
        txtCCCD = createField(diemThi.getCccd(), true);
        txtSBD = createField(diemThi.getSoBaoDanh(), true);
        cbPT = new JComboBox<>(new String[]{"4", "0", "3"});
        if (diemThi.getDPhuongThuc() != null) cbPT.setSelectedItem(diemThi.getDPhuongThuc());

        txtToan = createField(f(diemThi.getTo()), true);   txtVan = createField(f(diemThi.getVa()), true);
        txtAnh  = createField(f(diemThi.getN1Thi()), true);txtLy  = createField(f(diemThi.getLi()), true);
        txtHoa  = createField(f(diemThi.getHo()), true);   txtSinh = createField(f(diemThi.getSi()), true);
        txtSu   = createField(f(diemThi.getSu()), true);   txtDia = createField(f(diemThi.getDi()), true);
        txtNL1  = createField(f(diemThi.getNl1()), true);  txtCNCN = createField(f(diemThi.getCncn()), true);
        txtCNNN = createField(f(diemThi.getCnnn()), true); txtTI  = createField(f(diemThi.getTi()), true);
        txtKTPL = createField(f(diemThi.getKtpl()), true); txtNK1 = createField(f(diemThi.getNk1()), true);
        txtNK2  = createField(f(diemThi.getNk2()), true);

        // Cột 1
        addF(form, "CCCD:", txtCCCD, 0, 0, g);
        addF(form, "SBD:", txtSBD, 0, 1, g);
        addF(form, "Toán:", txtToan, 0, 2, g);
        addF(form, "Ngoại ngữ:", txtAnh, 0, 3, g);
        addF(form, "Hóa học:", txtHoa, 0, 4, g);
        addF(form, "Lịch sử:", txtSu, 0, 5, g);
        addF(form, "ĐGNL (NL1):", txtNL1, 0, 6, g);
        addF(form, "Công nghệ NN:", txtCNNN, 0, 7, g);
        addF(form, "KT Pháp luật:", txtKTPL, 0, 8, g);

        // Cột 2
        addF(form, "Loại PT:", cbPT, 2, 0, g);
        addF(form, "Ngữ văn:", txtVan, 2, 2, g);
        addF(form, "Vật lý:", txtLy, 2, 3, g);
        addF(form, "Sinh học:", txtSinh, 2, 4, g);
        addF(form, "Địa lý:", txtDia, 2, 5, g);
        addF(form, "Công nghệ CN:", txtCNCN, 2, 6, g);
        addF(form, "Tin học:", txtTI, 2, 7, g);
        addF(form, "Năng khiếu 1:", txtNK1, 2, 8, g);
        addF(form, "Năng khiếu 2:", txtNK2, 2, 9, g);

        add(form, BorderLayout.CENTER);

        // 3. FOOTER
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        footer.setBackground(new Color(245, 245, 245));
        btnSave = new JButton("Lưu dữ liệu");
        btnSave.setPreferredSize(new Dimension(120, 35));
        btnSave.setBackground(MainFrame.C_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setPreferredSize(new Dimension(100, 35));

        btnSave.addActionListener(e -> {
            diemThi.setCccd(txtCCCD.getText()); diemThi.setSoBaoDanh(txtSBD.getText());
            diemThi.setDPhuongThuc((String)cbPT.getSelectedItem());
            diemThi.setTo(p(txtToan.getText())); diemThi.setVa(p(txtVan.getText()));
            diemThi.setN1Thi(p(txtAnh.getText())); diemThi.setLi(p(txtLy.getText()));
            diemThi.setHo(p(txtHoa.getText())); diemThi.setSi(p(txtSinh.getText()));
            diemThi.setSu(p(txtSu.getText())); diemThi.setDi(p(txtDia.getText()));
            diemThi.setNl1(p(txtNL1.getText())); diemThi.setCncn(p(txtCNCN.getText()));
            diemThi.setCnnn(p(txtCNNN.getText())); diemThi.setTi(p(txtTI.getText()));
            diemThi.setKtpl(p(txtKTPL.getText())); diemThi.setNk1(p(txtNK1.getText()));
            diemThi.setNk2(p(txtNK2.getText()));
            confirmed = true; dispose();
        });

        btnCancel.addActionListener(e -> dispose());
        footer.add(btnCancel); footer.add(btnSave);
        add(footer, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    private JTextField createField(String val, boolean editable) {
        JTextField f = new JTextField(val, 12); // Tăng kích thước cột mặc định
        f.setPreferredSize(new Dimension(180, 30)); // Đảm bảo độ rộng ô nhập
        f.setEditable(editable);
        if(!editable) f.setBackground(new Color(240, 240, 240));
        return f;
    }

    private void addF(JPanel p, String text, Component c, int x, int y, GridBagConstraints g) {
        g.gridx = x; g.gridy = y;
        g.anchor = GridBagConstraints.EAST; // Căn nhãn sang phải sát ô nhập
        p.add(new JLabel(text), g);
        g.gridx = x + 1;
        g.anchor = GridBagConstraints.WEST;
        p.add(c, g);
    }

    private String f(Double d) { return (d == null) ? "" : String.valueOf(d); }
    private Double p(String s) { try { return Double.parseDouble(s); } catch(Exception e) { return null; } }
    public boolean isConfirmed() { return confirmed; }
    public DiemThiXettuyen getDiemThi() { return diemThi; }
}