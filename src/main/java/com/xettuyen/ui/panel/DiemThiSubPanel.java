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
import javax.swing.JOptionPane;
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
    
    // Các Label phục vụ thống kê
    private JLabel lblToanStats, lblVanStats, lblAnhStats, lblLyStats, lblHoaStats, lblCount;

    public DiemThiSubPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. TOOLBAR CHÍNH
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolBar.setBackground(Color.WHITE);

        txtSearch = new JTextField(8);
        txtSearch.setPreferredSize(new Dimension(100, 35));
        
        // ComboBox lọc loại điểm
        cbLoaiDiem = new JComboBox<>(new String[]{"Tất cả điểm", "THPT (PT4)", "ĐGNL (PT0)", "VSAT (PT3)"});
        cbLoaiDiem.setPreferredSize(new Dimension(120, 35));
        cbLoaiDiem.addActionListener(e -> loadData());

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

        toolBar.add(new JLabel("CCCD:")); toolBar.add(txtSearch); toolBar.add(btnSearch); 
        toolBar.add(Box.createHorizontalStrut(5));
        toolBar.add(new JLabel(" Loại:")); toolBar.add(cbLoaiDiem);
        toolBar.add(btnRefresh);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(btnAdd); toolBar.add(btnEdit); toolBar.add(btnDelete);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(btnImport); toolBar.add(btnExport);
        
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

        // Bảng hiển thị dữ liệu (Zebra Style)
        String[] header = {"CCCD", "PT", "Toán", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Văn", "Anh", "ĐGNL"};
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
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 3. XỬ LÝ SỰ KIỆN
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); cbLoaiDiem.setSelectedIndex(0); loadData(); });
        btnSearch.addActionListener(e -> handleSearch());
        btnAdd.addActionListener(e -> handleAdd());
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnImport.addActionListener(e -> handleImport());
        btnExport.addActionListener(e -> handleExport());
        
        loadData();
    }

    private JLabel createStatLabel(String name) {
        JLabel label = new JLabel(name + ": --", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(MainFrame.C_TEXT);
        label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        return label;
    }

    private void setupTableStyle() {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(MainFrame.C_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(b.getPreferredSize().width + 10, 35));
        if (bg != null) { 
            b.setBackground(bg); 
            b.setForeground(Color.WHITE); 
            b.setBorderPainted(false); 
            b.setFocusPainted(false);
        }
        return b;
    }

    // Load dữ liệu tích hợp bộ lọc và thống kê
    private void loadData() {
        tableModel.setRowCount(0);
        String filter = (String) cbLoaiDiem.getSelectedItem();
        List<DiemThiXettuyen> list = DAOFactory.getDiemThiDAO().findAll(DiemThiXettuyen.class);
        
        double tToan = 0, tVan = 0, tAnh = 0, tLy = 0, tHoa = 0;
        int count = 0;
        
        for (DiemThiXettuyen d : list) {
            // Logic lọc: 4=THPT, 0=DGNL, 3=VSAT
            boolean match = filter.equals("Tất cả điểm") 
                || (filter.contains("THPT") && "4".equals(d.getDPhuongThuc()))
                || (filter.contains("ĐGNL") && "0".equals(d.getDPhuongThuc()))
                || (filter.contains("VSAT") && "3".equals(d.getDPhuongThuc()));

            if (match) {
                tableModel.addRow(new Object[]{
                    d.getCccd(), d.getDPhuongThuc(), d.getTo(), d.getLi(), 
                    d.getHo(), d.getSi(), d.getSu(), d.getDi(), d.getVa(), 
                    d.getN1Thi(), d.getNl1()
                });
                
                tToan += (d.getTo() != null) ? d.getTo() : 0;
                tVan += (d.getVa() != null) ? d.getVa() : 0;
                tAnh += (d.getN1Thi() != null) ? d.getN1Thi() : 0;
                tLy += (d.getLi() != null) ? d.getLi() : 0;
                tHoa += (d.getHo() != null) ? d.getHo() : 0;
                count++;
            }
        }
        
        // Cập nhật nhãn thống kê
        lblCount.setText("Số lượng: " + count);
        if (count > 0) {
            lblToanStats.setText(String.format("Toán: %.2f", tToan/count));
            lblVanStats.setText(String.format("Văn: %.2f", tVan/count));
            lblAnhStats.setText(String.format("Anh: %.2f", tAnh/count));
            lblLyStats.setText(String.format("Lý: %.2f", tLy/count));
            lblHoaStats.setText(String.format("Hóa: %.2f", tHoa/count));
        } else {
            lblToanStats.setText("Toán: 0.0"); lblVanStats.setText("Văn: 0.0");
            lblAnhStats.setText("Anh: 0.0"); lblLyStats.setText("Lý: 0.0"); lblHoaStats.setText("Hóa: 0.0");
        }
    }

    private void handleSearch() {
        String key = txtSearch.getText().trim();
        if (key.isEmpty()) { loadData(); return; }
        tableModel.setRowCount(0);
        List<DiemThiXettuyen> list = DAOFactory.getDiemThiDAO().findByCCCD(key);
        for (DiemThiXettuyen d : list) {
            tableModel.addRow(new Object[]{d.getCccd(), d.getDPhuongThuc(), d.getTo(), d.getLi(), d.getHo(), d.getSi(), d.getSu(), d.getDi(), d.getVa(), d.getN1Thi(), d.getNl1()});
        }
    }

    private void handleAdd() {
        DiemThiDialog dialog = new DiemThiDialog(null, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            DAOFactory.getDiemThiDAO().save(dialog.getDiemThi());
            DiemPanel.addLog("Thêm điểm mới cho CCCD: " + dialog.getDiemThi().getCccd());
            loadData();
        }
    }

    private void handleEdit() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Chọn dòng cần sửa!"); return; }
        String cccd = (String) table.getValueAt(r, 0);
        DiemThiXettuyen dt = DAOFactory.getDiemThiDAO().findByCCCD(cccd).get(0);
        DiemThiDialog dialog = new DiemThiDialog(null, dt);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            DAOFactory.getDiemThiDAO().update(dialog.getDiemThi());
            DiemPanel.addLog("Cập nhật điểm cho CCCD: " + cccd);
            loadData();
        }
    }

    private void handleDelete() {
        int r = table.getSelectedRow();
        if (r == -1) return;
        String cccd = (String) table.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Xóa điểm của thí sinh " + cccd + "?") == JOptionPane.YES_OPTION) {
            DAOFactory.getDiemThiDAO().delete(DAOFactory.getDiemThiDAO().findByCCCD(cccd).get(0));
            DiemPanel.addLog("Xóa điểm CCCD: " + cccd);
            loadData();
        }
    }

    private void handleImport() {
        JFileChooser fs = new JFileChooser();
        fs.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        if (fs.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            List<DiemThiXettuyen> list = new ExcelImportService().importDiemThi(fs.getSelectedFile().getPath());
            int count = 0;
            for (DiemThiXettuyen dt : list) {
                List<DiemThiXettuyen> ex = DAOFactory.getDiemThiDAO().findByCCCD(dt.getCccd());
                if(ex.isEmpty()) DAOFactory.getDiemThiDAO().save(dt); 
                else DAOFactory.getDiemThiDAO().update(dt);
                count++;
            }
            DiemPanel.addLog("Import Excel thành công " + count + " bản ghi điểm thi.");
            loadData();
            JOptionPane.showMessageDialog(this, "Đã nhập thành công " + count + " dòng!");
        }
    }

    private void handleExport() {
        JFileChooser fs = new JFileChooser();
        if (fs.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Workbook wb = new XSSFWorkbook()) {
                Sheet s = wb.createSheet("DiemThi_Export");
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
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + ex.getMessage()); }
        }
    }
}