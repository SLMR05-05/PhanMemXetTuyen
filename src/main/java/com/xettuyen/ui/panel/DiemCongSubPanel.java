package com.xettuyen.ui.panel;

import com.xettuyen.dao.DAOFactory;
import com.xettuyen.entity.DiemCongXettuyen;
import com.xettuyen.service.ExcelImportService;
import com.xettuyen.ui.MainFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DiemCongSubPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private List<DiemCongXettuyen> listDiemCong;

    public DiemCongSubPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(java.awt.Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. TOOLBAR
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolBar.setBackground(java.awt.Color.WHITE);

        txtSearch = new JTextField(12);
        txtSearch.setPreferredSize(new Dimension(150, 35));
        
        JButton btnSearch = createStyledButton("Tìm kiếm", null);
        JButton btnRefresh = createStyledButton("Làm mới", null);
        
        JButton btnAdd = createStyledButton("Thêm", MainFrame.C_PRIMARY);
        JButton btnEdit = createStyledButton("Sửa", new java.awt.Color(255, 193, 7));
        btnEdit.setForeground(java.awt.Color.BLACK);
        JButton btnDelete = createStyledButton("Xóa", MainFrame.C_DANGER);
        
        JButton btnImport = createStyledButton("Nhập Excel", new Color(0, 150, 136));
        JButton btnExport = createStyledButton("Xuất Excel", new Color(76, 175, 80));

        toolBar.add(new JLabel("CCCD:")); toolBar.add(txtSearch); toolBar.add(btnSearch); toolBar.add(btnRefresh);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(btnAdd); toolBar.add(btnEdit); toolBar.add(btnDelete);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(btnImport); toolBar.add(btnExport);
        
        add(toolBar, BorderLayout.NORTH);

        // 2. TABLE
        String[] header = {"CCCD", "Mã Ngành", "Tổ Hợp", "Phương Thức", "Điểm CC", "Điểm UT", "Tổng"};
        tableModel = new DefaultTableModel(header, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? java.awt.Color.WHITE : new java.awt.Color(245, 248, 255));
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
        table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        table.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        table.setSelectionBackground(MainFrame.C_PRIMARY);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
    }

    private JButton createStyledButton(String text, java.awt.Color bg) {
        JButton b = new JButton(text);
        b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        b.setPreferredSize(new Dimension(b.getPreferredSize().width + 10, 35));
        if (bg != null) { b.setBackground(bg); b.setForeground(java.awt.Color.WHITE); b.setBorderPainted(false); }
        return b;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        // Lưu danh sách vào biến toàn cục để dùng khi Sửa/Xóa
        listDiemCong = DAOFactory.getDiemCongDAO().findAll(DiemCongXettuyen.class);
        for (DiemCongXettuyen dc : listDiemCong) {
            tableModel.addRow(new Object[]{
                dc.getTsCccd(), dc.getMaNganh(), dc.getMaTohop(), 
                dc.getPhuongThuc(), dc.getDiemCc(), dc.getDiemUtxt(), dc.getDiemTong()
            });
        }
    }

    private void handleSearch() {
        String key = txtSearch.getText().trim();
        if (key.isEmpty()) { loadData(); return; }
    
        tableModel.setRowCount(0);
        // Cập nhật lại listDiemCong theo kết quả tìm kiếm
        listDiemCong = DAOFactory.getDiemCongDAO().findByCCCD(key);
        for (DiemCongXettuyen dc : listDiemCong) {
            tableModel.addRow(new Object[]{
                dc.getTsCccd(), dc.getMaNganh(), dc.getMaTohop(), 
                dc.getPhuongThuc(), dc.getDiemCc(), dc.getDiemUtxt(), dc.getDiemTong()
            });
        }
    }

    private void handleAdd() {
        DiemCongDialog d = new DiemCongDialog(null, null);
        d.setVisible(true);
        if (d.isConfirmed()) {
            DAOFactory.getDiemCongDAO().save(d.getDiemCong());
            DiemPanel.addLog("Thêm điểm ưu tiên: " + d.getDiemCong().getTsCccd());
            loadData();
        }
    }

    private void handleEdit() {
        int r = table.getSelectedRow();
        if (r != -1) {
            // Lấy đúng đối tượng từ listDiemCong dựa trên chỉ số dòng r
            DiemCongXettuyen dcSelected = listDiemCong.get(r);
        
            // Truyền đối tượng đã có ID này vào Dialog
            DiemCongDialog d = new DiemCongDialog(null, dcSelected);
            d.setVisible(true);
        
            if (d.isConfirmed()) {
                // Hibernate merge sẽ dựa vào ID để update, không tạo dòng mới
                DAOFactory.getDiemCongDAO().update(d.getDiemCong());
                DiemPanel.addLog("Cập nhật điểm CCCD: " + dcSelected.getTsCccd());
                loadData(); // Load lại để cập nhật bảng
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng trên bảng để sửa!");
        }
    }

    private void handleDelete() {
        int r = table.getSelectedRow();
        if (r != -1) {
            String cccd = table.getValueAt(r, 0).toString();
            if (JOptionPane.showConfirmDialog(this, "Xóa " + cccd + "?") == JOptionPane.YES_OPTION) {
                DAOFactory.getDiemCongDAO().delete(DAOFactory.getDiemCongDAO().findByCCCD(cccd).get(0));
                loadData();
            }
        }
    }

    private void handleExport() {
        JFileChooser fs = new JFileChooser();
        if (fs.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Workbook wb = new XSSFWorkbook()) {
                Sheet s = wb.createSheet("DiemCong");
                Row h = s.createRow(0);
                for(int i=0; i<tableModel.getColumnCount(); i++) h.createCell(i).setCellValue(tableModel.getColumnName(i));
                for(int r=0; r<tableModel.getRowCount(); r++) {
                    Row row = s.createRow(r+1);
                    for(int c=0; c<tableModel.getColumnCount(); c++) row.createCell(c).setCellValue(String.valueOf(tableModel.getValueAt(r, c)));
                }
                FileOutputStream out = new FileOutputStream(fs.getSelectedFile().getPath() + ".xlsx");
                wb.write(out); out.close();
                JOptionPane.showMessageDialog(this, "Xuất Excel thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }

    private void handleImport() {
        JFileChooser fs = new JFileChooser();
        fs.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        if (fs.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                List<DiemCongXettuyen> list = new ExcelImportService().importDiemCong(fs.getSelectedFile().getPath());
                int count = 0;
                for (DiemCongXettuyen dc : list) {
                    // Kiểm tra trùng lặp dựa trên dcKeys
                    DAOFactory.getDiemCongDAO().save(dc); 
                    count++;
                }
                DiemPanel.addLog("Import thành công " + count + " dòng điểm ưu tiên.");
                loadData();
                JOptionPane.showMessageDialog(this, "Đã nhập thành công " + count + " bản ghi!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi import: " + ex.getMessage());
            }
        }
    }
}