package com.xettuyen.ui.panel;

import com.xettuyen.dao.BangQuyDoiDAO;
import com.xettuyen.entity.BangQuydoi;
import com.xettuyen.util.HibernateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BangQuyDoiPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtPhuongThuc, txtTohop, txtMon;
    private JTextField txtA, txtB, txtC, txtD;
    private JTextField txtMaQD, txtPhanVi;

    private BangQuyDoiDAO dao;
    private JLabel lblTotal, lblShowing;
    // ===== PAGINATION =====
    private JButton btnPrev, btnNext;
    private JLabel lblPage;
    private int currentPage = 1;
    private int totalRecords = 0;

    private int pageSize = 7;
    private List<BangQuydoi> fullList = new ArrayList<>();
    public BangQuyDoiPanel() {
        dao = new BangQuyDoiDAO(HibernateUtil.getSessionFactory());
        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());

        JLabel title = new JLabel("BẢNG QUY ĐỔI ĐIỂM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel subtitle = new JLabel("Quản lý bảng quy đổi điểm các tổ hợp xét tuyển");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new GridLayout(2,1));
        titlePanel.add(title);
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);
        

        add(header, BorderLayout.NORTH);
        //========filter//
        JPanel filterPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc"));

        JComboBox<String> cbPhuongThuc = new JComboBox<>(new String[]{"Tất cả", "DGNL"});
        JComboBox<String> cbToHop = new JComboBox<>(new String[]{"Tất cả", "A01"});

        JTextField txtSearchMon = new JTextField();
        JTextField txtSearchMa = new JTextField();

        filterPanel.add(new JLabel("Phương thức"));
        filterPanel.add(cbPhuongThuc);
        filterPanel.add(new JLabel("Tổ hợp"));
        filterPanel.add(cbToHop);

        filterPanel.add(new JLabel("Môn"));
        filterPanel.add(txtSearchMon);
        filterPanel.add(new JLabel("Mã QĐ"));
        filterPanel.add(txtSearchMa);

        //============stats//
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        lblTotal = new JLabel("0");
        lblShowing = new JLabel("0");
        statsPanel.add(createStatCardPanel("Tổng", lblTotal, new Color(33,150,243)));
        statsPanel.add(createStatCardPanel("Đang hiển thị", lblShowing, new Color(76,175,80)));

        statsPanel.add(createStatCardPanel("Tổ hợp", new JLabel("A01"), new Color(255,152,0)));

        //add(statsPanel, BorderLayout.AFTER_LAST_LINE);
        // ===== FORM =====
        JPanel form = new JPanel(new GridLayout(3, 6, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin quy đổi"));

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
        form.add(new JLabel("Mã QĐ")); form.add(txtMaQD);
        form.add(new JLabel("Phân vị")); form.add(txtPhanVi);

        // ===== ACTION BUTTON =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton btnAdd = createButton("Thêm", new Color(33,150,243));
        JButton btnEdit = createButton("Sửa", new Color(255,193,7));
        JButton btnDelete = createButton("Xóa", new Color(244,67,54));

        btnAdd.setBackground(new Color(25,118,210)); // xanh đậm
        btnEdit.setBackground(new Color(255,193,7)); // vàng
        btnDelete.setBackground(new Color(244,67,54)); // đỏ

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);

        header.add(actionPanel, BorderLayout.EAST);

        JPanel topContainer = new JPanel();

        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        topContainer.add(header);
        topContainer.add(filterPanel);
        topContainer.add(form);
        topContainer.add(actionPanel);
        topContainer.add(statsPanel);

        add(topContainer, BorderLayout.NORTH);
        // ===== TABLE =====
        model = new DefaultTableModel(new String[]{
                "ID", "Phương thức", "Tổ hợp", "Môn",
                "A", "B", "C", "D", "Mã QĐ", "Phân vị"
        }, 0);

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.setSelectionBackground(new Color(200, 230, 255));

        table.getTableHeader().setBackground(new Color(33,150,243));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ===== PAGINATION =====
        lblPage = new JLabel();
        JPanel pagingPanel = new JPanel();

        btnPrev = new JButton("<");
        btnNext = new JButton(">");

        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        lblPage.setText("Trang " + currentPage + "/" + totalPages);

        pagingPanel.add(btnPrev);
        pagingPanel.add(lblPage);
        pagingPanel.add(btnNext);

        add(pagingPanel, BorderLayout.SOUTH);

        // ===== EVENTS =====
        btnAdd.addActionListener(e -> addData());
        btnEdit.addActionListener(e -> updateData());
        btnDelete.addActionListener(e -> deleteData());

        btnPrev.addActionListener(e -> prevPage());
        btnNext.addActionListener(e -> nextPage());

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
    }
    //=========== STAT CARD ========
    private JPanel createStatCardPanel(String title, JLabel lblValue, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(color);
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValue.setForeground(Color.WHITE);

        p.add(lblTitle, BorderLayout.NORTH);
        p.add(lblValue, BorderLayout.CENTER);

        return p;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return btn;
    }

    // ===== LOAD =====
    private void loadData() {
    fullList = dao.findAll(BangQuydoi.class);

    if (fullList == null) fullList = new ArrayList<>();

    totalRecords = fullList.size();
    currentPage = 1; // reset về trang 1

    showPage();
}

    private void showPage() {
        model.setRowCount(0);

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, fullList.size());

        for (int i = start; i < end; i++) {
            BangQuydoi b = fullList.get(i);
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

        int totalPages = (int) Math.ceil((double) fullList.size() / pageSize);

    lblPage.setText("Trang " + currentPage + " / " + totalPages);

    btnPrev.setEnabled(currentPage > 1);
    btnNext.setEnabled(currentPage < totalPages);

    lblTotal.setText(String.valueOf(fullList.size()));
    lblShowing.setText(String.valueOf(end - start));
    }

    private void nextPage() {
        if (currentPage * pageSize < fullList.size()) {
            currentPage++;
            showPage();
        }
    }

    private void prevPage() {
        if (currentPage > 1) {
            currentPage--;
            showPage();
        }
    }

    // ===== CRUD =====
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