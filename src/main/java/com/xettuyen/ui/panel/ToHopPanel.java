package com.xettuyen.ui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.IOException;
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
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.xettuyen.entity.TohopMonthi;
import com.xettuyen.service.ToHopMonThiService;

import static com.xettuyen.ui.MainFrame.*;

public class ToHopPanel extends JPanel {

    private static final int PAGE_SIZE = 10;

    private final ToHopMonThiService service = new ToHopMonThiService();

    private JTextField searchField;
    private JTextField pageField;
    private JTextField totalPageField;
    private JTextField txtId;
    private JTextField txtMa;
    private JTextField txtMon1;
    private JTextField txtMon2;
    private JTextField txtMon3;
    private JTextField txtTen;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnImport;
    private JButton btnClear;
    private JButton btnPrev;
    private JButton btnNext;
    private int currentPage = 1;
    private int totalPages = 1;

    public ToHopPanel() {
        setLayout(new BorderLayout());
        setBackground(C_CONTENT_BG);
        initUI();
        initEvents();
        loadData(1);
    }

    private void initUI() {
        // ==========================================
        // 1. TẠO TOOLBAR TỔNG HỢP (TOP PANEL)
        // ==========================================
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // --- TẦNG 1: Tiêu đề & Ô Tìm Kiếm ---
        JPanel headerSearchPanel = new JPanel(new BorderLayout());
        headerSearchPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("QUẢN LÝ TỔ HỢP MÔN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(41, 128, 185));

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(250, 32));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        searchField.setBackground(Color.WHITE);

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBox.setOpaque(false);
        JLabel lblSearch = new JLabel("Tìm kiếm: ");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchBox.add(lblSearch);
        searchBox.add(searchField);

        headerSearchPanel.add(lblTitle, BorderLayout.WEST);
        headerSearchPanel.add(searchBox, BorderLayout.EAST);

        // --- TẦNG 2: Form Nhập Liệu (Sử dụng GridBagLayout) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.weightx = 1.0;

        // Initialize text fields
        txtId = new JTextField();
        txtId.setEditable(false);
        txtMa = new JTextField();
        txtMon1 = new JTextField();
        txtMon2 = new JTextField();
        txtMon3 = new JTextField();
        txtTen = new JTextField();

        configureField(txtId, true);
        configureField(txtMa, false);
        configureField(txtMon1, false);
        configureField(txtMon2, false);
        configureField(txtMon3, false);
        configureField(txtTen, false);

        // DÒNG 1 CỦA FORM: Mã Tổ Hợp, Môn 1, Môn 2, Môn 3
        gbc.gridy = 0;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Mã tổ hợp:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtMa, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Môn 1:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtMon1, gbc);

        gbc.gridx = 4;
        formPanel.add(new JLabel("Môn 2:"), gbc);
        gbc.gridx = 5;
        formPanel.add(txtMon2, gbc);

        gbc.gridx = 6;
        formPanel.add(new JLabel("Môn 3:"), gbc);
        gbc.gridx = 7;
        formPanel.add(txtMon3, gbc);

        // DÒNG 2 CỦA FORM: ID và Tên Tổ Hợp
        gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Tên tổ hợp:"), gbc);
        gbc.gridx = 3;
        gbc.gridwidth = 5;
        formPanel.add(txtTen, gbc);
        gbc.gridwidth = 1;

        // --- TẦNG 3: Nhóm Nút Bấm ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        btnAdd = createStyledButton("Thêm mới", C_PRIMARY, Color.WHITE);
        btnUpdate = createStyledButton("Chỉnh sửa", new Color(0x14, 0xA7, 0x8A), Color.WHITE);
        btnDelete = createStyledButton("Xóa", C_DANGER, Color.WHITE);
        btnImport = createStyledButton("Import Excel", new Color(0x13, 0x6F, 0xFD), Color.WHITE);
        btnClear = createStyledButton("Làm mới", C_SUCCESS, Color.WHITE);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnImport);
        buttonPanel.add(btnClear);

        // Gộp Form và Button vào 1 khối
        JPanel controlArea = new JPanel(new BorderLayout(0, 10));
        controlArea.setOpaque(false);
        controlArea.add(formPanel, BorderLayout.CENTER);
        controlArea.add(buttonPanel, BorderLayout.SOUTH);

        // Lắp ráp toàn bộ Toolbar
        topPanel.add(headerSearchPanel, BorderLayout.NORTH);
        topPanel.add(controlArea, BorderLayout.CENTER);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(C_CONTENT_BG);
        root.add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. TẠO BẢNG DỮ LIỆU
        // ==========================================
        model = new DefaultTableModel(new String[] { "ID", "Mã tổ hợp", "Môn 1", "Môn 2", "Môn 3", "Tên tổ hợp" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);

        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(0xDDE9FF));
        table.setSelectionForeground(C_TEXT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 38));
        header.setBackground(C_PRIMARY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        leftRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        centerRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(240);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(i == 5 ? leftRenderer : centerRenderer);
        }

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        tableScrollPane.setBackground(Color.WHITE);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        root.add(tableScrollPane, BorderLayout.CENTER);

        // ==========================================
        // 3. TẠO THANH PHÂN TRANG (BOTTOM PANEL)
        // ==========================================
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottom.setOpaque(false);

        btnPrev = createNavButton("<");
        btnNext = createNavButton(">");
        pageField = new JTextField("1");
        totalPageField = new JTextField("1");
        pageField.setPreferredSize(new Dimension(54, 30));
        totalPageField.setPreferredSize(new Dimension(54, 30));
        pageField.setHorizontalAlignment(JTextField.CENTER);
        totalPageField.setHorizontalAlignment(JTextField.CENTER);
        pageField.setEditable(false);
        totalPageField.setEditable(false);
        configureField(pageField, true);
        configureField(totalPageField, true);

        bottom.add(btnPrev);
        bottom.add(pageField);
        JLabel slash = new JLabel("/");
        slash.setFont(new Font("Segoe UI", Font.BOLD, 15));
        slash.setForeground(C_HINT);
        bottom.add(slash);
        bottom.add(totalPageField);
        bottom.add(btnNext);

        root.add(bottom, BorderLayout.SOUTH);
        add(root, BorderLayout.CENTER);
    }

    private void initEvents() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadData(1);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadData(1);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadData(1);
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                return;
            }

            int row = table.convertRowIndexToModel(viewRow);
            txtId.setText(valueAt(row, 0));
            txtMa.setText(valueAt(row, 1));
            txtMon1.setText(valueAt(row, 2));
            txtMon2.setText(valueAt(row, 3));
            txtMon3.setText(valueAt(row, 4));
            txtTen.setText(valueAt(row, 5));
        });

        btnPrev.addActionListener(e -> loadData(currentPage - 1));
        btnNext.addActionListener(e -> loadData(currentPage + 1));
        btnClear.addActionListener(e -> clearForm());
        btnAdd.addActionListener(e -> addRecord());
        btnUpdate.addActionListener(e -> updateRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnImport.addActionListener(e -> importExcel());
    }

    private void loadData(int requestedPage) {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();
        ToHopMonThiService.SearchResult result = service.search(keyword, requestedPage, PAGE_SIZE);

        currentPage = result.currentPage;
        totalPages = result.totalPages;
        pageField.setText(String.valueOf(currentPage));
        totalPageField.setText(String.valueOf(totalPages));
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);

        model.setRowCount(0);
        table.clearSelection();
        txtId.setText("");

        List<TohopMonthi> list = result.data;
        for (TohopMonthi th : list) {
            model.addRow(new Object[] {
                    th.getIdTohop(),
                    emptyIfNull(th.getMaTohop()),
                    emptyIfNull(th.getMon1()),
                    emptyIfNull(th.getMon2()),
                    emptyIfNull(th.getMon3()),
                    emptyIfNull(th.getTenTohop())
            });
        }
    }

    private void addRecord() {
        try {
            TohopMonthi entity = readForm();
            if (entity == null) {
                return;
            }

            if (service.findByMaTohop(entity.getMaTohop()) != null) {
                JOptionPane.showMessageDialog(this, "Mã tổ hợp đã tồn tại.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            service.add(entity);
            JOptionPane.showMessageDialog(this, "Thêm mới tổ hợp thành công.", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData(currentPage);
        } catch (Exception ex) {
            showError("Không thể thêm mới tổ hợp", ex);
        }
    }

    private void updateRecord() {
        try {
            Integer id = parseSelectedId();
            if (id == null) {
                JOptionPane.showMessageDialog(this, "Hãy chọn một dòng để chỉnh sửa.", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            TohopMonthi entity = readForm();
            if (entity == null) {
                return;
            }

            entity.setIdTohop(id);
            service.update(entity);
            JOptionPane.showMessageDialog(this, "Cập nhật tổ hợp thành công.", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData(currentPage);
        } catch (Exception ex) {
            showError("Không thể cập nhật tổ hợp", ex);
        }
    }

    private void deleteRecord() {
        try {
            Integer id = parseSelectedId();
            if (id == null) {
                JOptionPane.showMessageDialog(this, "Hãy chọn một dòng để xóa.", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa tổ hợp này?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            ToHopMonThiService.DeleteResult deleteResult = service.deleteWithUsageCheck(id);
            if (!deleteResult.success) {
                JOptionPane.showMessageDialog(this, deleteResult.message, "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, deleteResult.message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData(currentPage);
        } catch (Exception ex) {
            showError("Không thể xóa tổ hợp", ex);
        }
    }

    private void importExcel() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        int imported = 0;
        DataFormatter formatter = new DataFormatter();

        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                JOptionPane.showMessageDialog(this, "File Excel không có sheet dữ liệu.", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row, formatter)) {
                    continue;
                }

                String maTohop = readCell(row, 0, formatter);
                String mon1 = readCell(row, 1, formatter);
                String mon2 = readCell(row, 2, formatter);
                String mon3 = readCell(row, 3, formatter);
                String tenTohop = readCell(row, 4, formatter);

                if (maTohop.isEmpty() || mon1.isEmpty() || mon2.isEmpty() || mon3.isEmpty() || tenTohop.isEmpty()) {
                    continue;
                }

                TohopMonthi existing = service.findByMaTohop(maTohop);
                if (existing == null) {
                    service.add(new TohopMonthi(maTohop, mon1, mon2, mon3, tenTohop));
                } else {
                    existing.setMon1(mon1);
                    existing.setMon2(mon2);
                    existing.setMon3(mon3);
                    existing.setTenTohop(tenTohop);
                    service.update(existing);
                }
                imported++;
            }

            JOptionPane.showMessageDialog(this,
                    "Import thành công: " + imported + " dòng từ " + file.getName(),
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData(1);
        } catch (IOException ex) {
            showError("Không thể đọc file Excel", ex);
        } catch (Exception ex) {
            showError("Import Excel thất bại", ex);
        }
    }

    private TohopMonthi readForm() {
        String maTohop = textOf(txtMa);
        String mon1 = textOf(txtMon1);
        String mon2 = textOf(txtMon2);
        String mon3 = textOf(txtMon3);
        String tenTohop = textOf(txtTen);

        if (maTohop.isEmpty() || mon1.isEmpty() || mon2.isEmpty() || mon3.isEmpty() || tenTohop.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin tổ hợp.", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new TohopMonthi(maTohop, mon1, mon2, mon3, tenTohop);
    }

    private Integer parseSelectedId() {
        String idText = textOf(txtId);
        if (idText.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(idText);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private boolean isRowEmpty(Row row, DataFormatter formatter) {
        for (int i = 0; i < 5; i++) {
            if (!readCell(row, i, formatter).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String readCell(Row row, int index, DataFormatter formatter) {
        if (row == null) {
            return "";
        }

        return formatter.formatCellValue(row.getCell(index)).trim();
    }

    private void clearForm() {
        txtId.setText("");
        txtMa.setText("");
        txtMon1.setText("");
        txtMon2.setText("");
        txtMon3.setText("");
        txtTen.setText("");
        table.clearSelection();
    }

    private String valueAt(int row, int column) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private String textOf(JTextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this,
                message + "\n" + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
    }

    private void configureField(JTextField field, boolean muted) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        field.setBackground(muted ? new Color(0xF8, 0xFA, 0xFF) : Color.WHITE);
        field.setForeground(C_TEXT);
    }

    private JButton createStyledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isArmed() ? background.darker() : background;
                if (getModel().isRollover()) {
                    fill = fill.brighter();
                }
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(foreground);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        button.setOpaque(false);
        button.setRolloverEnabled(true);
        return button;
    }

    private JButton createNavButton(String text) {
        JButton button = createStyledButton(text, C_PRIMARY, Color.WHITE);
        button.setPreferredSize(new Dimension(42, 34));
        return button;
    }
}