package com.xettuyen.ui.panel;

import com.xettuyen.dao.BangQuyDoiDAO;
import com.xettuyen.entity.BangQuydoi;
import com.xettuyen.util.HibernateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
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

    private JComboBox<String> cbPhuongThuc;
    private JComboBox<String> cbToHop;
    private JTextField txtSearchMon;
    private JTextField txtSearchMa;
    public BangQuyDoiPanel() {
        dao = new BangQuyDoiDAO(HibernateUtil.getSessionFactory());
        setLayout(new BorderLayout());
        initUI();
        loadComboBoxData();
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

        cbPhuongThuc = new JComboBox<>();
        cbToHop = new JComboBox<>();

        txtSearchMon = new JTextField();
        txtSearchMa = new JTextField();

        filterPanel.add(new JLabel("Phương thức"));
        filterPanel.add(cbPhuongThuc);
        filterPanel.add(new JLabel("Tổ hợp"));
        filterPanel.add(cbToHop);

        filterPanel.add(new JLabel("Môn"));
        filterPanel.add(txtSearchMon);
        filterPanel.add(new JLabel("Mã QĐ"));
        filterPanel.add(txtSearchMa);

        //============stats//
        JPanel statsPanel = new JPanel(new FlowLayout(
        FlowLayout.LEFT,
        20,
        10
        ));
        lblTotal = new JLabel("0");
        lblShowing = new JLabel("0");
        statsPanel.add(createStatCardPanel("Tổng", lblTotal, new Color(33,150,243)));
        statsPanel.add(createStatCardPanel("Đang hiển thị", lblShowing, new Color(76,175,80)));

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

        topContainer.setBorder(
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        );
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        topContainer.add(header);
        topContainer.add(Box.createVerticalStrut(1));
        topContainer.add(filterPanel);
        topContainer.add(Box.createVerticalStrut(1));

        topContainer.add(form);
        topContainer.add(Box.createVerticalStrut(1));
        topContainer.add(actionPanel);
        topContainer.add(Box.createVerticalStrut(1));

        topContainer.add(statsPanel);
        add(topContainer, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(new String[]{
                "ID", "Phương thức", "Tổ hợp", "Môn",
                "A", "B", "C", "D", "Mã QĐ", "Phân vị"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // khóa edit trực tiếp
            }
        };

        table = new JTable(model);

        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.getTableHeader().setBackground(new Color(25,118,210));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        table.setSelectionBackground(new Color(225,240,255));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(230,230,230));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JScrollPane scrollPane = new JScrollPane(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120);  // Phương thức
        table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Tổ hợp
        table.getColumnModel().getColumn(3).setPreferredWidth(120);  // Môn

        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);

        table.getColumnModel().getColumn(8).setPreferredWidth(150); // mã QĐ
        table.getColumnModel().getColumn(9).setPreferredWidth(80);

        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setOpaque(false);

        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // ===== PAGINATION =====
        lblPage = new JLabel();
        JPanel pagingPanel = new JPanel();

        btnPrev = createButton("<", new Color(120,120,120));
        btnNext = createButton(">", new Color(33,150,243));

        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        lblPage.setText("Trang " + currentPage + "/" + totalPages);

        pagingPanel.add(btnPrev);
        pagingPanel.add(lblPage);
        pagingPanel.add(btnNext);
        pagingPanel.setBackground(Color.WHITE);
        add(pagingPanel, BorderLayout.SOUTH);

        // ===== EVENTS =====
        btnAdd.addActionListener(e -> addData());
        btnEdit.addActionListener(e -> updateData());
        btnDelete.addActionListener(e -> deleteData());

        btnPrev.addActionListener(e -> prevPage());
        btnNext.addActionListener(e -> nextPage());

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
            cbPhuongThuc.addActionListener(e -> filterData());
            cbToHop.addActionListener(e -> filterData());

            txtSearchMon.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent e) {
                    filterData();
                }
            });

            txtSearchMa.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent e) {
                    filterData();
                }
            });

        DefaultTableCellRenderer headerRenderer =
        new DefaultTableCellRenderer();

        headerRenderer.setBackground(new Color(25,118,210));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i)
                    .setHeaderRenderer(headerRenderer);
        }
        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {

            table.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(centerRenderer);
        }
        topContainer.setBackground(new Color(245,247,250));
        filterPanel.setBackground(Color.WHITE);
        form.setBackground(Color.WHITE);
        actionPanel.setBackground(new Color(245,247,250));
        statsPanel.setBackground(new Color(245,247,250));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(1,0,1,0));

        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230,230,230)),
            BorderFactory.createEmptyBorder(10,10,10,10)
        ));
        filterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        form.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
    }
    //=========== STAT CARD ========
    private JPanel createStatCardPanel(String title, JLabel lblValue, Color color) {
    JPanel outer = new JPanel(new BorderLayout());
    outer.setBackground(new Color(245,247,250));
    outer.setBorder(BorderFactory.createEmptyBorder(2,6,2,6));
    outer.setPreferredSize(new Dimension(260, 80));
    JPanel p = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            // shadow
            g2.setColor(new Color(0,0,0,40));
            g2.fillRoundRect(2,2,getWidth()-4,getHeight()-4,16,16);
            // card
            g2.setColor(color);
            g2.fillRoundRect(0,0,getWidth()-8,getHeight()-8,20,20);
            super.paintComponent(g);
        }
    };
    p.setOpaque(false);
    p.setBorder(BorderFactory.createEmptyBorder(2,3,2,3));
    JLabel lblTitle = new JLabel(title);
    lblTitle.setForeground(Color.WHITE);
    lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
    lblValue.setForeground(Color.WHITE);
    p.add(lblTitle, BorderLayout.NORTH);
    p.add(lblValue, BorderLayout.CENTER);
    outer.add(p, BorderLayout.CENTER);
    // hover effect
    p.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            p.setBorder(BorderFactory.createEmptyBorder(12,18,18,22));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            p.setBorder(BorderFactory.createEmptyBorder(15,20,15,20));
        }
    });
    return outer;
}

    private JButton createButton(String text, Color color) {
    final JButton btn = new JButton(text) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
        }
    };
    btn.setForeground(Color.WHITE);
    btn.setBackground(color);

    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setContentAreaFilled(false);

    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btn.setPreferredSize(new Dimension(100, 40));
    btn.setBorder(BorderFactory.createEmptyBorder());
    btn.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            btn.setBackground(color.darker());
        }
        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            btn.setBackground(color);
        }
    });
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

            b.setDDiemA(getDouble(txtA));
            b.setDDiemB(getDouble(txtB));
            b.setDDiemC(getDouble(txtC));
            b.setDDiemD(getDouble(txtD));

            b.setDMaQuydoi(txtMaQD.getText());
            b.setDPhanvi(txtPhanVi.getText());

            dao.save(b);
            loadData();
            clearForm();
            loadComboBoxData();
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

            b.setDDiemA(getDouble(txtA));
            b.setDDiemB(getDouble(txtB));
            b.setDDiemC(getDouble(txtC));
            b.setDDiemD(getDouble(txtD));

            b.setDMaQuydoi(txtMaQD.getText());
            b.setDPhanvi(txtPhanVi.getText());

            dao.update(b);
            loadData();
            loadComboBoxData();

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
            loadComboBoxData();
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
    private double getDouble(JTextField txt) {
        String value = txt.getText().trim();
        if (value.isEmpty()) {
            return 0;
        }
        return Double.parseDouble(value);
    }
    private void loadComboBoxData() {
        cbPhuongThuc.removeAllItems();
        cbToHop.removeAllItems();
        cbPhuongThuc.addItem("Tất cả");
        cbToHop.addItem("Tất cả");
        List<BangQuydoi> list = dao.findAll(BangQuydoi.class);
        List<String> dsPhuongThuc = new ArrayList<>();
        List<String> dsToHop = new ArrayList<>();
        for (BangQuydoi b : list) {
            String pt = b.getDPhuongThuc();
            String th = b.getDTohop();
            if (pt != null && !dsPhuongThuc.contains(pt)) {
                dsPhuongThuc.add(pt);
                cbPhuongThuc.addItem(pt);
            }
            if (th != null && !dsToHop.contains(th)) {
                dsToHop.add(th);
                cbToHop.addItem(th);
            }
        }
    }
        private void filterData() {
            String phuongThuc = "Tất cả";
            String toHop = "Tất cả";

            if (cbPhuongThuc.getSelectedItem() != null) {
                phuongThuc =
                        cbPhuongThuc.getSelectedItem().toString();
            }

            if (cbToHop.getSelectedItem() != null) {
                toHop =
                        cbToHop.getSelectedItem().toString();
            }
            String mon =
                    txtSearchMon.getText().trim().toLowerCase();
            String maQD =
                    txtSearchMa.getText().trim().toLowerCase();
            List<BangQuydoi> filtered = new ArrayList<>();
            for (BangQuydoi b : dao.findAll(BangQuydoi.class)) {
                boolean match = true;
                // phương thức
                if (!phuongThuc.equals("Tất cả") &&
                        !b.getDPhuongThuc().equalsIgnoreCase(phuongThuc)) {
                    match = false;
                }
                // tổ hợp
                if (!toHop.equals("Tất cả") &&
                        !b.getDTohop().equalsIgnoreCase(toHop)) {
                    match = false;
                }
                // môn
                if (!mon.isEmpty()) {
                    String dMon =
                            b.getDMon() == null ? "" : b.getDMon();
                    if (!dMon.toLowerCase().contains(mon)) {
                        match = false;
                    }
                }
                // mã quy đổi
                if (!maQD.isEmpty()) {
                    String dMa =
                            b.getDMaQuydoi() == null ? "" : b.getDMaQuydoi();
                    if (!dMa.toLowerCase().contains(maQD)) {
                        match = false;
                    }
                }
                if (match) {
                    filtered.add(b);
                }
            }
            fullList = filtered;
            currentPage = 1;
            showPage();
        }
}