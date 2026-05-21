package com.xettuyen.ui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.FileOutputStream;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.SwingWorker;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.xettuyen.dao.DAOFactory;
import com.xettuyen.entity.DiemCongXettuyen;
import com.xettuyen.entity.DiemThiXettuyen;
import com.xettuyen.service.ExcelImportService;
import com.xettuyen.ui.MainFrame;

public class DiemCongSubPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private List<DiemCongXettuyen> listDiemCong;

    public DiemCongSubPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. TOOLBAR
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolBar.setBackground(Color.WHITE);

        txtSearch = new JTextField(12);
        txtSearch.setPreferredSize(new Dimension(150, 35));
        
        JButton btnSearch = createStyledButton("Tìm kiếm", null);
        JButton btnRefresh = createStyledButton("Làm mới", null);
        
        JButton btnAdd = createStyledButton("Thêm", MainFrame.C_PRIMARY);
        JButton btnEdit = createStyledButton("Sửa", new Color(255, 193, 7));
        btnEdit.setForeground(Color.BLACK);
        JButton btnDelete = createStyledButton("Xóa", MainFrame.C_DANGER);
        
        JButton btnImport = createStyledButton("Nhập Excel", new Color(0, 150, 136));
        btnImport.setForeground(Color.WHITE);
        JButton btnExport = createStyledButton("Xuất Excel", new Color(76, 175, 80));
        btnExport.setForeground(Color.WHITE);

        toolBar.add(new JLabel("CCCD:")); toolBar.add(txtSearch); toolBar.add(btnSearch); toolBar.add(btnRefresh);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(btnAdd); toolBar.add(btnEdit); toolBar.add(btnDelete);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(btnImport); toolBar.add(btnExport);
        
        add(toolBar, BorderLayout.NORTH);

        // 2. TABLE
        String[] header = {"TT", "CCCD", "Mã Ngành", "Mã Tổ Hợp", "Phương Thức", "Điểm CC", "Điểm UT", "Điểm Tổng", "Ghi Chú", "DC Keys"};
        tableModel = new DefaultTableModel(header, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 255));
                }
                return c;
            }
        };
        setupTableStyle();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. EVENTS
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadData(); });
        btnSearch.addActionListener(e -> handleSearch());
        btnAdd.addActionListener(e -> handleAdd());
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnExport.addActionListener(e -> handleExport());
        btnImport.addActionListener(e -> handleImport());

        loadData(); 
    }

    private void setupTableStyle() {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(MainFrame.C_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(b.getPreferredSize().width + 10, 35));
        if (bg != null) { b.setBackground(bg); b.setForeground(Color.WHITE); b.setBorderPainted(false); }
        return b;
    }

    private Object f(Object v) { return (v == null) ? "" : v; }

    private void loadData() {
        tableModel.setRowCount(0);
        // Lấy toàn bộ từ DB và gán vào listDiemCong để quản lý ID
        listDiemCong = DAOFactory.getDiemCongDAO().findAll(DiemCongXettuyen.class);
        for (DiemCongXettuyen dc : listDiemCong) {
            tableModel.addRow(new Object[]{
                dc.getIdDiemCong(), dc.getTsCccd(), dc.getMaNganh(), dc.getMaTohop(), 
                dc.getPhuongThuc(), f(dc.getDiemCc()), f(dc.getDiemUtxt()), 
                f(dc.getDiemTong()), f(dc.getGhiChu()), f(dc.getDcKeys())
            });
        }
    }

    private void handleSearch() {
        String key = txtSearch.getText().trim();
        if (key.isEmpty()) { loadData(); return; }
        tableModel.setRowCount(0);
        listDiemCong = DAOFactory.getDiemCongDAO().findByCCCD(key);
        for (DiemCongXettuyen dc : listDiemCong) {
            tableModel.addRow(new Object[]{
                dc.getTsCccd(), dc.getMaNganh(), dc.getMaTohop(), 
                dc.getPhuongThuc(), f(dc.getDiemCc()), f(dc.getDiemUtxt()), 
                f(dc.getDiemTong()), f(dc.getGhiChu())
            });
        }
    }

    private void handleAdd() {
        DiemCongDialog d = new DiemCongDialog(null, null);
        d.setVisible(true);
        if (d.isConfirmed()) {
            DAOFactory.getDiemCongDAO().save(d.getDiemCong());
            DiemPanel.addLog("Thêm điểm cộng mới: " + d.getDiemCong().getTsCccd());
            loadData();
        }
    }

    private void handleEdit() {
        int r = table.getSelectedRow();
        if (r != -1) {
            // Lấy đúng đối tượng có ID từ danh sách tương ứng với dòng r trên bảng
            DiemCongXettuyen dcSelected = listDiemCong.get(r);
            DiemCongDialog d = new DiemCongDialog(null, dcSelected);
            d.setVisible(true);
            if (d.isConfirmed()) {
                DAOFactory.getDiemCongDAO().update(d.getDiemCong());
                DiemPanel.addLog("Cập nhật điểm ưu tiên CCCD: " + dcSelected.getTsCccd());
                loadData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa!");
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
                // để tránh bị lệch Index của listDiemCong
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int modelRow = table.convertRowIndexToModel(selectedRows[i]);
                    DiemCongXettuyen dc = listDiemCong.get(modelRow);
                
                    // Gọi DAO xóa
                    DAOFactory.getDiemCongDAO().delete(dc);
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

    private void handleExport() {
        JFileChooser fs = new JFileChooser();
        fs.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        if (fs.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Workbook wb = new XSSFWorkbook()) {
                Sheet s = wb.createSheet("DiemCong_Export");
                Row h = s.createRow(0);
                for(int i=0; i<tableModel.getColumnCount(); i++) h.createCell(i).setCellValue(tableModel.getColumnName(i));
                for(int r=0; r<tableModel.getRowCount(); r++) {
                    Row row = s.createRow(r+1);
                    for(int c=0; c<tableModel.getColumnCount(); c++) {
                        Object val = tableModel.getValueAt(r, c);
                        row.createCell(c).setCellValue(val != null ? val.toString() : "");
                    }
                }
                String path = fs.getSelectedFile().getPath();
                if(!path.endsWith(".xlsx")) path += ".xlsx";
                try (FileOutputStream out = new FileOutputStream(path)) { wb.write(out); }
                JOptionPane.showMessageDialog(this, "Xuất file thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }

    private void handleImport() {
        JFileChooser fs = new JFileChooser();
        fs.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
    
        if (fs.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fs.getSelectedFile().getPath();
        
            // Chạy Worker để không bị đơ giao diện
            new SwingWorker<Integer, Void>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    List<DiemCongXettuyen> list = new ExcelImportService().importDiemCong(path);
                    int count = 0;
                    for (DiemCongXettuyen dc : list) {
                        try {
                            DAOFactory.getDiemCongDAO().save(dc);
                            count++;
                        } catch (Exception e) {
                            // Nếu trùng Key thì cập nhật
                            DAOFactory.getDiemCongDAO().update(dc);
                            count++;
                        }
                    }
                    return count;
                }

                @Override
                protected void done() {
                    try {
                        int total = get();
                        loadData(); // Load lại bảng
                        DiemPanel.addLog("Import Excel thành công: " + total + " dòng điểm cộng.");
                        JOptionPane.showMessageDialog(DiemCongSubPanel.this, "Đã nhập thành công " + total + " bản ghi!");
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(DiemCongSubPanel.this, "Lỗi: " + e.getMessage());
                    }
                }
            }.execute();
        }
    }
}