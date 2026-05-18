package com.xettuyen.ui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.xettuyen.entity.TohopMonthi;
import com.xettuyen.service.ToHopMonThiService;

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
        initUI();
        initEvents();
        loadData(1);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm tổ hợp"));
        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin tổ hợp"));
        txtId = new JTextField();
        txtId.setEditable(false);
        txtMa = new JTextField();
        txtMon1 = new JTextField();
        txtMon2 = new JTextField();
        txtMon3 = new JTextField();
        txtTen = new JTextField();

        form.add(new JLabel("ID:"));
        form.add(txtId);
        form.add(new JLabel("Mã tổ hợp:"));
        form.add(txtMa);
        form.add(new JLabel("Môn 1:"));
        form.add(txtMon1);
        form.add(new JLabel("Môn 2:"));
        form.add(txtMon2);
        form.add(new JLabel("Môn 3:"));
        form.add(txtMon3);
        form.add(new JLabel("Tên tổ hợp:"));
        form.add(txtTen);
        form.add(new JLabel(""));

        top.add(searchPanel);
        top.add(form);
        root.add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[] { "ID", "Mã tổ hợp", "Môn 1", "Môn 2", "Môn 3", "Tên tổ hợp" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Chỉnh sửa");
        btnDelete = new JButton("Xóa");
        btnImport = new JButton("Import Excel");
        btnClear = new JButton("Làm mới");
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnImport);
        btnPanel.add(btnClear);
        bottom.add(btnPanel, BorderLayout.NORTH);

        JPanel pagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPrev = new JButton("<<");
        btnNext = new JButton(">>");
        pageField = new JTextField("1");
        totalPageField = new JTextField("1");
        pageField.setPreferredSize(new Dimension(50, 26));
        totalPageField.setPreferredSize(new Dimension(50, 26));
        pageField.setHorizontalAlignment(JTextField.CENTER);
        totalPageField.setHorizontalAlignment(JTextField.CENTER);
        pageField.setEditable(false);
        totalPageField.setEditable(false);
        pagePanel.add(btnPrev);
        pagePanel.add(pageField);
        pagePanel.add(new JLabel("/"));
        pagePanel.add(totalPageField);
        pagePanel.add(btnNext);
        bottom.add(pagePanel, BorderLayout.SOUTH);

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
}