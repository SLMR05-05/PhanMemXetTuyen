package com.xettuyen.ui.panel;

import com.xettuyen.dao.BangQuyDoiDAO;
import com.xettuyen.entity.BangQuydoi;
import com.xettuyen.util.HibernateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BangQuyDoiPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtPhuongThuc, txtTohop, txtMon;
    private JTextField txtA, txtB, txtC, txtD;
    private JTextField txtMaQD, txtPhanVi;

    private BangQuyDoiDAO dao;

    public BangQuyDoiPanel() {
        dao = new BangQuyDoiDAO(HibernateUtil.getSessionFactory());

        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {

        // ===== TABLE =====
        model = new DefaultTableModel(new String[]{
                "ID", "Phương thức", "Tổ hợp", "Môn",
                "A", "B", "C", "D",
                "Mã QĐ", "Phân vị"
        }, 0);

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== FORM =====
        JPanel form = new JPanel(new GridLayout(5, 4, 5, 5));

        txtPhuongThuc = new JTextField();
        txtTohop = new JTextField();
        txtMon = new JTextField();

        txtA = new JTextField();
        txtB = new JTextField();
        txtC = new JTextField();
        txtD = new JTextField();

        txtMaQD = new JTextField();
        txtPhanVi = new JTextField();

        form.add(new JLabel("Phương thức")); form.add(txtPhuongThuc);
        form.add(new JLabel("Tổ hợp")); form.add(txtTohop);

        form.add(new JLabel("Môn")); form.add(txtMon);
        form.add(new JLabel("Điểm A")); form.add(txtA);

        form.add(new JLabel("Điểm B")); form.add(txtB);
        form.add(new JLabel("Điểm C")); form.add(txtC);

        form.add(new JLabel("Điểm D")); form.add(txtD);
        form.add(new JLabel("Mã quy đổi")); form.add(txtMaQD);

        form.add(new JLabel("Phân vị")); form.add(txtPhanVi);

        add(form, BorderLayout.NORTH);

        // ===== BUTTON =====
        JPanel btnPanel = new JPanel();

        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        add(btnPanel, BorderLayout.SOUTH);

        // ===== EVENTS =====
        btnAdd.addActionListener(e -> addData());
        btnUpdate.addActionListener(e -> updateData());
        btnDelete.addActionListener(e -> deleteData());

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    // ===== LOAD =====
    private void loadData() {
        model.setRowCount(0);
        List<BangQuydoi> list = dao.findAll(BangQuydoi.class);

        for (BangQuydoi b : list) {
            model.addRow(new Object[]{
                    b.getIdQd(),
                    b.getDPhuongThuc(),
                    b.getDTohop(),
                    b.getDMon(),
                    b.getDDiemA(),
                    b.getDDiemB(),
                    b.getDDiemC(),
                    b.getDDiemD(),
                    b.getDMaQuydoi(),
                    b.getDPhanvi()
            });
        }
    }

    // ===== ADD =====
    private void addData() {
        try {
            BangQuydoi b = new BangQuydoi();

            b.setDPhuongThuc(txtPhuongThuc.getText());
            b.setDTohop(txtTohop.getText());
            b.setDMon(txtMon.getText());

            b.setDDiemA(Double.parseDouble(txtA.getText()));
            b.setDDiemB(Double.parseDouble(txtB.getText()));
            b.setDDiemC(Double.parseDouble(txtC.getText()));
            b.setDDiemD(Double.parseDouble(txtD.getText()));

            b.setDMaQuydoi(txtMaQD.getText());
            b.setDPhanvi(txtPhanVi.getText());

            dao.save(b);
            loadData();
            clearForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm: " + e.getMessage());
        }
    }

    // ===== UPDATE =====
    private void updateData() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        try {
            Integer id = (Integer) model.getValueAt(row, 0);
            BangQuydoi b = dao.findById(BangQuydoi.class, id);

            b.setDPhuongThuc(txtPhuongThuc.getText());
            b.setDTohop(txtTohop.getText());
            b.setDMon(txtMon.getText());

            b.setDDiemA(Double.parseDouble(txtA.getText()));
            b.setDDiemB(Double.parseDouble(txtB.getText()));
            b.setDDiemC(Double.parseDouble(txtC.getText()));
            b.setDDiemD(Double.parseDouble(txtD.getText()));

            b.setDMaQuydoi(txtMaQD.getText());
            b.setDPhanvi(txtPhanVi.getText());

            dao.update(b);
            loadData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa: " + e.getMessage());
        }
    }

    // ===== DELETE =====
    private void deleteData() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        try {
            Integer id = (Integer) model.getValueAt(row, 0);
            BangQuydoi b = dao.findById(BangQuydoi.class, id);

            dao.delete(b);
            loadData();
            clearForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage());
        }
    }

    // ===== FILL FORM =====
    private void fillForm() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        txtPhuongThuc.setText(model.getValueAt(row, 1).toString());
        txtTohop.setText(model.getValueAt(row, 2).toString());
        txtMon.setText(model.getValueAt(row, 3).toString());

        txtA.setText(model.getValueAt(row, 4).toString());
        txtB.setText(model.getValueAt(row, 5).toString());
        txtC.setText(model.getValueAt(row, 6).toString());
        txtD.setText(model.getValueAt(row, 7).toString());

        txtMaQD.setText(model.getValueAt(row, 8).toString());
        txtPhanVi.setText(model.getValueAt(row, 9).toString());
    }

    private void clearForm() {
        txtPhuongThuc.setText("");
        txtTohop.setText("");
        txtMon.setText("");
        txtA.setText("");
        txtB.setText("");
        txtC.setText("");
        txtD.setText("");
        txtMaQD.setText("");
        txtPhanVi.setText("");
    }
}