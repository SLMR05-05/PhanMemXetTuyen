package com.xettuyen.ui.panel;

import com.xettuyen.dao.BaseDAO;
import com.xettuyen.entity.Nganh;
import com.xettuyen.service.NganhService;
import com.xettuyen.ui.component.ActionColumnEditor; 
import com.xettuyen.ui.component.ActionColumnRenderer;
import com.xettuyen.ui.dialog.NganhAddDialog;
import com.xettuyen.ui.dialog.NganhEditDialog; 
import com.xettuyen.util.HibernateUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.util.List;

import static com.xettuyen.ui.MainFrame.*;

public class NganhPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private final BaseDAO<Nganh> dao = new BaseDAO<Nganh>(HibernateUtil.getSessionFactory()) {};
    private final NganhService nganhService = new NganhService(); 

    public NganhPanel() {
        initComponents();
        loadDataToTable();
    }

private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setBackground(C_CONTENT_BG);
        int pad = clamp(vw(2), 20, 36);
        this.setBorder(new EmptyBorder(pad, pad, pad, pad));

        // --- 1. Header & Toolbar ---
        JPanel headerActions = new JPanel(new BorderLayout());
        headerActions.setOpaque(false);
        headerActions.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLbl = new JLabel("Quản Lý Chi Tiết Danh Mục Ngành");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(2.8f), 22, 30)));
        titleLbl.setForeground(C_TITLE);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setOpaque(false);

        // --- 🔍 Ô TÌM KIẾM ĐÃ ĐƯỢC KÉO DÀI VÀ CĂN CHỈNH LẠI ---
        JTextField txtSearch = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    // Khử răng cưa cho chữ mịn
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(160, 160, 160)); // Màu xám nhẹ
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    
                    // Lấy thông số font để căn giữa theo chiều dọc
                    FontMetrics fm = g2.getFontMetrics();
                    int x = 10; // Cách lề trái 10px
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    
                    g2.drawString("Tìm kiếm bằng mã ngành hoặc tên ngành...", x, y);
                    g2.dispose();
                }
            }
        };
        
        // Tăng chiều dài lên 450 để khớp với dòng chữ dài
        txtSearch.setPreferredSize(new Dimension(450, 35));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(0, 10, 0, 10) // Padding trong để chữ không dính viền
        ));

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchBox.setOpaque(false);
        searchBox.add(txtSearch);

        JButton btnAdd = createStyledButton("➕ Thêm Mới", C_PRIMARY);
        JButton btnRefresh = createStyledButton("🔄 Làm Mới", C_SUCCESS);
        JButton btnDelete = createStyledButton("🗑 Xóa Dữ Liệu", new Color(220, 53, 69)); 
        
        btnAdd.setForeground(Color.BLACK);
        btnRefresh.setForeground(Color.BLACK);
        btnDelete.setForeground(Color.BLACK);

        toolbar.add(searchBox); 
        toolbar.add(btnAdd);
        toolbar.add(btnRefresh);
        toolbar.add(btnDelete);

        headerActions.add(titleLbl, BorderLayout.WEST);
        headerActions.add(toolbar, BorderLayout.EAST);

        // --- 2. Table Area (Cấu hình 17 cột) ---
        JPanel tableCard = makeCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {
            "ID", "Mã Ngành", "Tên Ngành", "Tổ Hợp Gốc", "Chỉ Tiêu", 
            "Điểm Sàn", "Điểm Chuẩn", "Tuyển Thẳng", "ĐGNL", "THPT", 
            "VSAT", "SL XTT", "SL ĐGNL", "SL VSAT", "SL THPT", 
            "Tổ hợp", "Thao tác" 
        };
        
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c >= getColumnCount() - 2;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); 
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        styleTable(table); 

        // --- 🟢 BỘ LỌC (SORT 3 TRẠNG THÁI) ---
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(tableModel) {
            @Override
            public void toggleSortOrder(int column) {
                java.util.List<? extends SortKey> sortKeys = getSortKeys();
                if (!sortKeys.isEmpty()) {
                    SortKey sortKey = sortKeys.get(0);
                    if (sortKey.getColumn() == column && sortKey.getSortOrder() == SortOrder.DESCENDING) {
                        setSortKeys(null);
                        return;
                    }
                }
                super.toggleSortOrder(column);
            }
        };
        table.setRowSorter(sorter);

        sorter.setComparator(4, (o1, o2) -> {
            Integer v1 = Integer.parseInt(o1.toString().isEmpty() ? "0" : o1.toString());
            Integer v2 = Integer.parseInt(o2.toString().isEmpty() ? "0" : o2.toString());
            return v1.compareTo(v2);
        });
        sorter.setComparator(5, (o1, o2) -> {
            Double v1 = Double.parseDouble(o1.toString().isEmpty() ? "0" : o1.toString());
            Double v2 = Double.parseDouble(o2.toString().isEmpty() ? "0" : o2.toString());
            return v1.compareTo(v2);
        });

        // --- ⚡ LOGIC TÌM KIẾM TỨC THỜI ---
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }

            private void filter() {
                String text = txtSearch.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2));
                }
            }
        });

        table.getTableHeader().setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- GẮN RENDERER/EDITOR ---
        int colTohop = table.getColumnCount() - 2;
        int colThaoTac = table.getColumnCount() - 1;

        table.getColumnModel().getColumn(colTohop).setCellRenderer(new ActionColumnRenderer("menu"));
        table.getColumnModel().getColumn(colTohop).setCellEditor(new ActionColumnEditor(new JCheckBox(), "menu", this));

        table.getColumnModel().getColumn(colThaoTac).setCellRenderer(new ActionColumnRenderer("edit_delete"));
        table.getColumnModel().getColumn(colThaoTac).setCellEditor(new ActionColumnEditor(new JCheckBox(), "edit_delete", this));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        tableCard.add(scrollPane, BorderLayout.CENTER);

        this.add(headerActions, BorderLayout.NORTH);
        this.add(tableCard, BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> {
            txtSearch.setText(""); 
            loadDataToTable();
        });
        btnDelete.addActionListener(e -> executeDeleteLogic());
        btnAdd.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            NganhAddDialog dialog = new NganhAddDialog((Frame) owner);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadDataToTable(); 
            }
        });
    }


    // Hàm mở Dialog (Dùng chung cho icon 3 gạch và icon bút chì)
    public void openEditDialog() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        Integer id = Integer.valueOf(table.getValueAt(row, 0).toString());
        
        new Thread(() -> {
            try {
                Nganh selected = dao.findById(Nganh.class, id);
                SwingUtilities.invokeLater(() -> {
                    Window owner = SwingUtilities.getWindowAncestor(NganhPanel.this);
                    NganhEditDialog dialog = new NganhEditDialog((Frame) owner, selected);
                    dialog.setVisible(true);
                    loadDataToTable();
                });
            } catch (Exception ex) { ex.printStackTrace(); }
        }).start();
    }

/**
     * Hàm xử lý Xóa: Hỗ trợ xóa hàng loạt và lách luật biến final của Java
     */
    public void executeDeleteLogic() {
        // 1. Lấy danh sách các dòng đang được chọn
        int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một dòng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Hỏi xác nhận một lần duy nhất cho tất cả các dòng đã chọn
        String message = selectedRows.length == 1 ? "Bạn có chắc muốn xóa ngành này?" : "Bạn có chắc muốn xóa " + selectedRows.length + " ngành đã chọn?";
        int confirm = JOptionPane.showConfirmDialog(this, message, "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                // Dùng mảng 1 phần tử để lách lỗi "variable must be final"
                final int[] successCount = {0}; 
                
                for (int rowIndex : selectedRows) {
                    try {
                        // Lấy ID từ cột 0 của từng dòng
                        Integer id = Integer.valueOf(table.getValueAt(rowIndex, 0).toString());
                        
                        // Gọi Service xóa và tăng biến đếm nếu thành công
                        if (nganhService.deleteNganh(id)) {
                            successCount[0]++;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                // 3. Sau khi xóa xong hết thì thông báo và load lại bảng
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công " + successCount[0] + " mục.");
                    loadDataToTable(); // Chỉ load lại 1 lần duy nhất cho hiệu suất cao
                });
            }).start();
        }
    }
    public void loadDataToTable() {
        new Thread(() -> {
            try {
                List<Nganh> list = dao.findAll(Nganh.class);
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    if (list == null) return;
                    for (Nganh n : list) {
                        tableModel.addRow(new Object[]{
                            n.getIdNganh(), n.getMaNganh(), n.getTenNganh(), n.getNTohopGoc(),
                            n.getNChiTieu(), n.getNDiemSan(), n.getNDiemTrungTuyen(),
                            n.getNTuyenThang(), n.getNDgNl(), n.getNThpt(), n.getNVsat(),
                            n.getSlXtt(), n.getSlDgNl(), n.getSlVsat(), n.getSlThpt(),
                            "", "" // Giữ chỗ cho 2 cột Icon
                        });
                    }
                });
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void styleTable(JTable table) {
        table.setRowHeight(55); // Tăng chiều cao để icon không bị sát nhau
        TableColumnModel columnModel = table.getColumnModel();
        
        columnModel.getColumn(0).setPreferredWidth(60);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(280);
        
        // Căn lề và độ rộng cho 2 cột cuối
        columnModel.getColumn(table.getColumnCount() - 2).setPreferredWidth(80);
        columnModel.getColumn(table.getColumnCount() - 1).setPreferredWidth(120);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Căn giữa cho các cột số liệu
        for (int i = 3; i < table.getColumnCount() - 2; i++) {
            columnModel.getColumn(i).setPreferredWidth(110);
            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}