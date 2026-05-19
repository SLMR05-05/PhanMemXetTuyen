package com.xettuyen.ui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileOutputStream;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.SwingWorker;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.xettuyen.dao.DAOFactory;
import com.xettuyen.entity.DiemThiXettuyen;
import com.xettuyen.service.ExcelImportService;
import com.xettuyen.ui.MainFrame;

public class DiemThiSubPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbLoaiDiem;
    private List<DiemThiXettuyen> listDiemThi; // Danh sách dữ liệu hiện hành trên bảng

    // Các Label phục vụ thống kê
    private JLabel lblToanStats, lblVanStats, lblAnhStats, lblLyStats, lblHoaStats, lblCount;

    public DiemThiSubPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. TOOLBAR
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolBar.setBackground(Color.WHITE);

        txtSearch = new JTextField(8);
        txtSearch.setPreferredSize(new Dimension(100, 35));

        cbLoaiDiem = new JComboBox<>(new String[] { "Tất cả điểm", "THPT (PT4)", "ĐGNL (PT0)", "VSAT (PT3)" });
        cbLoaiDiem.setPreferredSize(new Dimension(120, 35));
        cbLoaiDiem.addActionListener(e -> loadData());

        JButton btnSearch = createStyledButton("Tìm kiếm", null);
        JButton btnRefresh = createStyledButton("Làm mới", null);

        JButton btnAdd = createStyledButton("Thêm", MainFrame.C_PRIMARY);
        JButton btnEdit = createStyledButton("Sửa", new Color(255, 193, 7));
        btnEdit.setForeground(Color.BLACK);
        JButton btnDelete = createStyledButton("Xóa", MainFrame.C_DANGER);

        JButton btnImport = createStyledButton("Nhập Excel ▼", new Color(0, 150, 136));
        btnImport.setForeground(Color.WHITE);
        JButton btnExport = createStyledButton("Xuất Excel", new Color(76, 175, 80));
        btnExport.setForeground(Color.WHITE);

        toolBar.add(new JLabel("CCCD:"));
        toolBar.add(txtSearch);
        toolBar.add(btnSearch);
        toolBar.add(Box.createHorizontalStrut(5));
        toolBar.add(new JLabel(" Loại:"));
        toolBar.add(cbLoaiDiem);
        toolBar.add(btnRefresh);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(btnAdd);
        toolBar.add(btnEdit);
        toolBar.add(btnDelete);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(btnImport);
        toolBar.add(btnExport);

        add(toolBar, BorderLayout.NORTH);

        // 2. PANEL TRUNG TÂM (THỐNG KÊ & BẢNG)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(Color.WHITE);

        // Panel Thống kê
        JPanel statsPanel = new JPanel(new GridLayout(1, 6, 10, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                " Thống kê trung bình môn (Theo bộ lọc) ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), MainFrame.C_PRIMARY));

        lblCount = createStatLabel("Số lượng");
        lblToanStats = createStatLabel("Toán");
        lblVanStats = createStatLabel("Văn");
        lblAnhStats = createStatLabel("Anh");
        lblLyStats = createStatLabel("Lý");
        lblHoaStats = createStatLabel("Hóa");

        statsPanel.add(lblCount);
        statsPanel.add(lblToanStats);
        statsPanel.add(lblVanStats);
        statsPanel.add(lblAnhStats);
        statsPanel.add(lblLyStats);
        statsPanel.add(lblHoaStats);
        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Bảng dữ liệu
        String[] header = { "TT", "CCCD", "SBD", "PT", "Toán", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Văn", "N1_THI",
                "N1_CC", "CNCN", "CNNN", "Tin", "KTPL", "NL1", "NK1", "NK2", "Loại CC" };
        tableModel = new DefaultTableModel(header, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 255));
                return c;
            }
        };
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setupTableStyle();
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 3. SỰ KIỆN
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbLoaiDiem.setSelectedIndex(0);
            loadData();
        });
        btnSearch.addActionListener(e -> handleSearch());
        btnAdd.addActionListener(e -> handleAdd());
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnImport.addActionListener(e -> showImportMenu(btnImport));
        btnExport.addActionListener(e -> handleExport());

        loadData();
    }

    private void loadData() {
        listDiemThi = DAOFactory.getDiemThiDAO().findAll(DiemThiXettuyen.class);
        updateTableAndStats();
    }

    private void handleSearch() {
        String key = txtSearch.getText().trim();
        if (key.isEmpty()) {
            loadData();
            return;
        }
        listDiemThi = DAOFactory.getDiemThiDAO().findByCCCD(key);
        updateTableAndStats();
    }

    private void updateTableAndStats() {
        tableModel.setRowCount(0);
        String filter = (String) cbLoaiDiem.getSelectedItem();
        double tT = 0, tV = 0, tA = 0, tL = 0, tH = 0;
        int count = 0;

        for (DiemThiXettuyen d : listDiemThi) {
            boolean match = filter.equals("Tất cả điểm")
                    || (filter.contains("THPT") && "4".equals(d.getDPhuongThuc()))
                    || (filter.contains("ĐGNL") && "0".equals(d.getDPhuongThuc()))
                    || (filter.contains("VSAT") && "3".equals(d.getDPhuongThuc()));

            if (match) {
                tableModel.addRow(new Object[] {
                        d.getIdDiemThi(), d.getCccd(), d.getSoBaoDanh(), d.getDPhuongThuc(),
                        f(d.getTo()), f(d.getLi()), f(d.getHo()), f(d.getSi()), f(d.getSu()),
                        f(d.getDi()), f(d.getVa()), f(d.getN1Thi()), f(d.getN1Cc()), f(d.getCncn()),
                        f(d.getCnnn()), f(d.getTi()), f(d.getKtpl()), f(d.getNl1()), f(d.getNk1()),
                        f(d.getNk2()), (d.getLoaiChungChi() == null ? "" : d.getLoaiChungChi())
                });
                tT += (d.getTo() != null ? d.getTo() : 0);
                tV += (d.getVa() != null ? d.getVa() : 0);
                tA += (d.getN1Thi() != null ? d.getN1Thi() : 0);
                tL += (d.getLi() != null ? d.getLi() : 0);
                tH += (d.getHo() != null ? d.getHo() : 0);
                count++;
            }
        }
        lblCount.setText("Số lượng: " + count);
        if (count > 0) {
            lblToanStats.setText(String.format("Toán: %.2f", tT / count));
            lblVanStats.setText(String.format("Văn: %.2f", tV / count));
            lblAnhStats.setText(String.format("Anh: %.2f", tA / count));
            lblLyStats.setText(String.format("Lý: %.2f", tL / count));
            lblHoaStats.setText(String.format("Hóa: %.2f", tH / count));
        }
    }

    private void handleAdd() {
        DiemThiDialog dialog = new DiemThiDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            DAOFactory.getDiemThiDAO().save(dialog.getDiemThi());
            DiemPanel.addLog("Thêm điểm mới: " + dialog.getDiemThi().getCccd());
            loadData();
        }
    }

    private void handleEdit() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Chọn dòng để sửa!");
            return;
        }
        // Lấy đúng đối tượng từ danh sách đã lọc hiển thị trên bảng
        DiemThiXettuyen dt = listDiemThi.get(r);
        DiemThiDialog dialog = new DiemThiDialog(null, dt);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            DAOFactory.getDiemThiDAO().update(dialog.getDiemThi());
            DiemPanel.addLog("Cập nhật điểm: " + dt.getCccd());
            loadData();
        }
    }

    private void handleDelete() {
        // Lấy danh sách chỉ số các dòng đang được chọn
        int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một dòng để xóa!");
            return;
        }

        // Hỏi xác nhận 1 lần duy nhất cho tất cả các dòng
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa " + selectedRows.length + " bản ghi đã chọn?",
                "Xác nhận xóa hàng loạt", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int count = 0;
                // Duyệt danh sách từ dưới lên hoặc lấy đối tượng trước khi xóa
                // để tránh bị lệch Index của listDiemThi
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int modelRow = table.convertRowIndexToModel(selectedRows[i]);
                    DiemThiXettuyen dt = listDiemThi.get(modelRow);

                    // Gọi DAO xóa
                    DAOFactory.getDiemThiDAO().delete(dt);
                    count++;
                }

                DiemPanel.addLog("Xóa hàng loạt: thành công " + count + " bản ghi.");
                loadData(); // Load lại bảng sau khi xóa xong
                JOptionPane.showMessageDialog(this, "Đã xóa thành công " + count + " bản ghi!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa dữ liệu: " + ex.getMessage());
                loadData(); // Load lại để đảm bảo đồng bộ
            }
        }
    }

    private void showImportMenu(JButton anchor) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem importThiTotNghiep = new JMenuItem("Import điểm thi tốt nghiệp");
        importThiTotNghiep.addActionListener(e -> handleImportDiemTotNghiep());

        JMenuItem importDgnlVsat = new JMenuItem("Import DGNL và VSAT");
        importDgnlVsat.addActionListener(e -> handleImportDgnlVsat());

        JMenuItem importIelts = new JMenuItem("Import IELTS");
        importIelts.addActionListener(e -> handleImportIelts());

        menu.add(importThiTotNghiep);
        menu.add(importDgnlVsat);
        menu.add(importIelts);
        menu.show(anchor, 0, anchor.getHeight());
    }

    private void handleImportDiemTotNghiep() {
        JFileChooser fs = new JFileChooser();
        fs.setDialogTitle("Chọn file Excel điểm thi tốt nghiệp");
        fs.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        if (fs.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        String filePath = fs.getSelectedFile().getPath();

        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                List<DiemThiXettuyen> list = new ExcelImportService().importDiemThi(filePath);
                int count = 0;
                for (DiemThiXettuyen dt : list) {
                    List<DiemThiXettuyen> existing = DAOFactory.getDiemThiDAO().findByCCCD(dt.getCccd());
                    if (!existing.isEmpty()) {
                        DiemThiXettuyen dbItem = existing.get(0);
                        dt.setIdDiemThi(dbItem.getIdDiemThi());
                        DAOFactory.getDiemThiDAO().update(dt);
                    } else {
                        DAOFactory.getDiemThiDAO().save(dt);
                    }
                    count++;
                }
                return count;
            }

            @Override
            protected void done() {
                try {
                    int total = get();
                    DiemPanel.addLog("Import điểm thi tốt nghiệp thành công: " + total + " bản ghi.");
                    loadData();
                    JOptionPane.showMessageDialog(DiemThiSubPanel.this,
                            "Đã xử lý thành công " + total + " bản ghi!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DiemThiSubPanel.this,
                            "Lỗi khi import: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void handleImportDgnlVsat() {
        JFileChooser fs = new JFileChooser();
        fs.setDialogTitle("Chọn file Excel DGNL và VSAT");
        fs.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        if (fs.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        String filePath = fs.getSelectedFile().getPath();

        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                List<DiemThiXettuyen> list = new ExcelImportService().importDGNLvaVSAT(filePath);
                int count = 0;
                for (DiemThiXettuyen dt : list) {
                    List<DiemThiXettuyen> existing = DAOFactory.getDiemThiDAO().findByCCCD(dt.getCccd());
                    if (!existing.isEmpty()) {
                        DiemThiXettuyen dbItem = existing.get(0);
                        dt.setIdDiemThi(dbItem.getIdDiemThi());
                        DAOFactory.getDiemThiDAO().update(dt);
                    } else {
                        DAOFactory.getDiemThiDAO().save(dt);
                    }
                    count++;
                }
                return count;
            }

            @Override
            protected void done() {
                try {
                    int total = get();
                    DiemPanel.addLog("Import DGNL/VSAT thành công: " + total + " bản ghi.");
                    loadData();
                    JOptionPane.showMessageDialog(DiemThiSubPanel.this,
                            "Đã xử lý thành công " + total + " bản ghi!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DiemThiSubPanel.this,
                            "Lỗi khi import: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void handleImportIelts() {
        JFileChooser fs = new JFileChooser();
        fs.setDialogTitle("Chọn file Excel IELTS");
        fs.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        if (fs.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        String filePath = fs.getSelectedFile().getPath();

        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                return new ExcelImportService().importIelts(filePath);
            }

            @Override
            protected void done() {
                try {
                    int total = get();
                    DiemPanel.addLog("Import IELTS thành công: " + total + " bản ghi được cập nhật.");
                    loadData();
                    JOptionPane.showMessageDialog(DiemThiSubPanel.this,
                            "Đã cập nhật thành công " + total + " bản ghi IELTS!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DiemThiSubPanel.this,
                            "Lỗi khi import IELTS: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void handleExport() {
        JFileChooser fs = new JFileChooser();
        if (fs.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Workbook wb = new XSSFWorkbook()) {
                Sheet s = wb.createSheet("DiemThi");
                Row h = s.createRow(0);
                for (int i = 0; i < tableModel.getColumnCount(); i++)
                    h.createCell(i).setCellValue(tableModel.getColumnName(i));
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    Row row = s.createRow(r + 1);
                    for (int c = 0; c < tableModel.getColumnCount(); c++) {
                        Object val = tableModel.getValueAt(r, c);
                        row.createCell(c).setCellValue(val != null ? val.toString() : "");
                    }
                }
                String path = fs.getSelectedFile().getPath();
                if (!path.endsWith(".xlsx"))
                    path += ".xlsx";
                try (FileOutputStream out = new FileOutputStream(path)) {
                    wb.write(out);
                }
                JOptionPane.showMessageDialog(this, "Xuất file thành công!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private Object f(Double v) {
        return (v == null) ? "" : v;
    }

    private JLabel createStatLabel(String n) {
        JLabel l = new JLabel(n + ": --", SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        return l;
    }

    private void setupTableStyle() {
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionBackground(MainFrame.C_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
    }

    private JButton createStyledButton(String t, Color bg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(b.getPreferredSize().width + 10, 35));
        if (bg != null) {
            b.setBackground(bg);
            b.setForeground(Color.WHITE);
            b.setBorderPainted(false);
        }
        return b;
    }
}