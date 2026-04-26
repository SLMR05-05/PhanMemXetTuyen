package com.xettuyen.ui.panel;

import com.xettuyen.entity.DiemCongXettuyen;
import com.xettuyen.ui.MainFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DiemCongDialog extends JDialog {
    private JTextField txtCCCD, txtMaNganh, txtMaToHop, txtPhuongThuc, txtDiemCC, txtDiemUT, txtGhiChu;
    private JButton btnSave, btnCancel;
    private boolean confirmed = false;
    private DiemCongXettuyen diemCong;

    public DiemCongDialog(Frame parent, DiemCongXettuyen dc) {
        super(parent, "Thông Tin Điểm Ưu Tiên", true);
        this.diemCong = (dc == null) ? new DiemCongXettuyen() : dc;
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel();
        header.setBackground(MainFrame.C_PRIMARY);
        JLabel lbl = new JLabel("CHI TIẾT ĐIỂM CỘNG & ƯU TIÊN");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setBorder(new EmptyBorder(12, 0, 12, 0));
        header.add(lbl);
        add(header, BorderLayout.NORTH);

        // FORM
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(25, 40, 25, 40));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 10, 10, 10);

        txtCCCD = createField(diemCong.getTsCccd(), true);
        txtMaNganh = createField(diemCong.getMaNganh(), true);
        txtMaToHop = createField(diemCong.getMaTohop(), true);
        txtPhuongThuc = createField(diemCong.getPhuongThuc(), true);
        txtDiemCC = createField(f(diemCong.getDiemCc()), true);
        txtDiemUT = createField(f(diemCong.getDiemUtxt()), true);
        txtGhiChu = createField(diemCong.getGhiChu(), true);

        // Layout các trường
        addF(form, "CCCD Thí sinh:", txtCCCD, 0, g);
        addF(form, "Mã Ngành:", txtMaNganh, 1, g);
        addF(form, "Mã Tổ Hợp:", txtMaToHop, 2, g);
        addF(form, "Phương Thức:", txtPhuongThuc, 3, g);
        addF(form, "Điểm Chứng chỉ:", txtDiemCC, 4, g);
        addF(form, "Điểm Ưu tiên:", txtDiemUT, 5, g);
        addF(form, "Ghi chú:", txtGhiChu, 6, g);

        add(form, BorderLayout.CENTER);

        // FOOTER
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        btnSave = new JButton("Lưu dữ liệu");
        btnSave.setPreferredSize(new Dimension(120, 35));
        btnSave.setBackground(MainFrame.C_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(80, 35));

        btnSave.addActionListener(e -> {
            diemCong.setTsCccd(txtCCCD.getText().trim());
            diemCong.setMaNganh(txtMaNganh.getText().trim());
            diemCong.setMaTohop(txtMaToHop.getText().trim());
            diemCong.setPhuongThuc(txtPhuongThuc.getText().trim());
            diemCong.setDiemCc(p(txtDiemCC.getText()));
            diemCong.setDiemUtxt(p(txtDiemUT.getText()));
            diemCong.setDiemTong((diemCong.getDiemCc() != null ? diemCong.getDiemCc() : 0) + (diemCong.getDiemUtxt() != null ? diemCong.getDiemUtxt() : 0));
            diemCong.setGhiChu(txtGhiChu.getText().trim());
            diemCong.setDcKeys(diemCong.getTsCccd() + "_" + diemCong.getMaNganh() + "_" + diemCong.getMaTohop());
            confirmed = true; dispose();
        });

        btnCancel.addActionListener(e -> dispose());
        footer.add(btnCancel); footer.add(btnSave);
        add(footer, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private JTextField createField(String val, boolean editable) {
        JTextField f = new JTextField(val, 20);
        f.setPreferredSize(new Dimension(250, 30));
        f.setEditable(editable);
        if(!editable) f.setBackground(new Color(240, 240, 240));
        return f;
    }

    private void addF(JPanel p, String label, JTextField c, int y, GridBagConstraints g) {
        g.gridx = 0; g.gridy = y;
        g.anchor = GridBagConstraints.EAST; // Nhãn căn lề phải
        p.add(new JLabel(label), g);
        g.gridx = 1;
        g.anchor = GridBagConstraints.WEST;
        p.add(c, g);
    }

    private String f(Double d) { return (d == null) ? "" : String.valueOf(d); }
    private Double p(String s) { try { return Double.parseDouble(s); } catch(Exception e) { return 0.0; } }
    public boolean isConfirmed() { return confirmed; }
    public DiemCongXettuyen getDiemCong() { return diemCong; }
}