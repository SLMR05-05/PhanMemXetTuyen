package com.xettuyen.ui.panel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.xettuyen.entity.ToHop;
import com.xettuyen.service.ToHopService;

public class ToHopPanel extends JPanel {

    private JTextField txtMa, txtTen;
    private JTable table;
    private DefaultTableModel model;

    private JButton btnAdd, btnUpdate, btnDelete, btnImport;
    private JButton btnPrev, btnNext;

    private ToHopService service = new ToHopService();

    private int currentPage = 1;
    private int pageSize = 10;

    public ToHopPanel() {
        setLayout(new BorderLayout());

        initUI();
        loadData();
    }

    private void initUI() {

        // ===== FORM =====
        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin tổ hợp"));

        txtMa = new JTextField();
        txtTen = new JTextField();

        form.add(new JLabel("Mã tổ hợp:"));
        form.add(txtMa);
        form.add(new JLabel("Tên tổ hợp:"));
        form.add(txtTen);

        add(form, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(new String[]{"Mã", "Tên"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BUTTON =====
        JPanel bottom = new JPanel(new BorderLayout());

        JPanel btnPanel = new JPanel();
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnImport = new JButton("Import Excel");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnImport);

        bottom.add(btnPanel, BorderLayout.NORTH);

        // ===== PAGINATION =====
        JPanel pagePanel = new JPanel();
        btnPrev = new JButton("<<");
        btnNext = new JButton(">>");

        pagePanel.add(btnPrev);
        pagePanel.add(new JLabel("Trang"));
        pagePanel.add(btnNext);

        bottom.add(pagePanel, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);

        // ===== EVENTS =====
        initEvents();
    }

    private void initEvents() {

        // CLICK TABLE
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMa.setText(model.getValueAt(row, 0).toString());
                txtTen.setText(model.getValueAt(row, 1).toString());
            }
        });

        // ADD
        btnAdd.addActionListener(e -> {
            ToHop th = new ToHop();
            th.setMaToHop(txtMa.getText());
            th.setTenToHop(txtTen.getText());

            service.add(th);
            loadData();
        });

        // UPDATE
        btnUpdate.addActionListener(e -> {
            ToHop th = new ToHop();
            th.setMaToHop(txtMa.getText());
            th.setTenToHop(txtTen.getText());

            service.update(th);
            loadData();
        });

        // DELETE
        btnDelete.addActionListener(e -> {
            String id = txtMa.getText();
            service.delete(id);
            loadData();
        });

        // IMPORT EXCEL
        btnImport.addActionListener(e -> importExcel());

        // PAGINATION
        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadData();
            }
        });

        btnNext.addActionListener(e -> {
            currentPage++;
            loadData();
        });
    }

    // ===== LOAD DATA =====
    private void loadData() {
        model.setRowCount(0);

        List<ToHop> list = service.getAll(currentPage, pageSize);

        for (ToHop th : list) {
            model.addRow(new Object[]{
                    th.getMaToHop(),
                    th.getTenToHop()
            });
        }
    }

    // ===== IMPORT EXCEL =====
    private void importExcel() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            try {
                // TODO: đọc file bằng Apache POI
                JOptionPane.showMessageDialog(this, "Import thành công: " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}