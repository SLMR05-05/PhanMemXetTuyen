package com.xettuyen.ui.panel;

import com.xettuyen.entity.NguyenVongXettuyen;
import com.xettuyen.entity.ThiSinhXettuyen;
import com.xettuyen.service.ExcelImportService;
import com.xettuyen.service.ThiSinhService;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CandidateManagementPanel extends JPanel {

    private static final int PAGE_SIZE = 10;

    private final ExcelImportService excelImportService = new ExcelImportService();
    private final ThiSinhService thiSinhService = new ThiSinhService();

    private JTextField searchField;
    private JTextField pageField;
    private JTextField totalPageField;
    private JButton prevBtn;
    private JButton nextBtn;
    private JButton importBtn;
    private DefaultTableModel model;
    private JTable table;

    private int totalPages = 1;

    // ── Bảng màu (đồng bộ LoginForm) ──────────────────────
    public static final Color C_PRIMARY      = new Color(0x00, 0x62, 0xFF);
    public static final Color C_PRIMARY_HV   = new Color(0x00, 0x4E, 0xCC);
    public static final Color C_SIDEBAR_TOP  = new Color(0x0A, 0x16, 0x28);
    public static final Color C_SIDEBAR_BOT  = new Color(0x0E, 0x20, 0x40);
    public static final Color C_SIDEBAR_ITEM = new Color(0xFF, 0xFF, 0xFF, 30);
    public static final Color C_SIDEBAR_HV   = new Color(0xFF, 0xFF, 0xFF, 55);
    public static final Color C_SIDEBAR_ACT  = new Color(0x00, 0x62, 0xFF);
    public static final Color C_SIDEBAR_TEXT = new Color(0xC8, 0xD8, 0xF0);
    public static final Color C_SIDEBAR_ATXT = Color.WHITE;
    public static final Color C_CONTENT_BG   = new Color(0xF0, 0xF4, 0xFF);
    public static final Color C_CARD         = Color.WHITE;
    public static final Color C_BORDER       = new Color(0xE0, 0xE8, 0xFF);
    public static final Color C_TITLE        = new Color(0x12, 0x12, 0x14);
    public static final Color C_TEXT         = new Color(0x37, 0x37, 0x3A);
    public static final Color C_HINT         = new Color(0x7A, 0x8A, 0xAA);
    public static final Color C_DANGER       = new Color(0xDC, 0x35, 0x45);
    public static final Color C_DANGER_HV    = new Color(0xB0, 0x2A, 0x37);
    public static final Color C_SUCCESS      = new Color(0x19, 0x87, 0x54);
    public static final Color C_WARNING      = new Color(0xFF, 0x8C, 0x00);

    // ── Scale helpers ──────────────────────────────────────
    private static final int   SW;
    private static final int   SH;
    static {
        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        SW = scr.width;
        SH = scr.height;
    }
    public static int vw(float p)  { return Math.round(SW * p / 100f); }
    public static int vh(float p)  { return Math.round(SH * p / 100f); }
    public static int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }

    // ── Constructor ───────────────────────────────────────
    public CandidateManagementPanel() {
        setLayout(new BorderLayout());
        // setBackground(Color.red);

        // == North =========================================
        JPanel northPanel = new JPanel(new GridBagLayout());
        northPanel.setBackground(C_CONTENT_BG);
        northPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(vw(30), vh(5)));
        searchField.putClientProperty("JTextField.placeholderText", "Nhập CCCD của thí sinh");
        searchField.putClientProperty("JTextField.padding", new Insets(5, 5, 5, 5));
        searchField.putClientProperty("JTextField.showClearButton", true);
        searchField.putClientProperty("JTextField.clearButtonTooltip", "always");
        searchField.putClientProperty("FlatLaf.style", 
            "arc: 10; " + 
            "font: 14 $font; " +
            "foreground: " + toHex(C_TEXT) + "; "
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.weightx = 1; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        northPanel.add(searchField, gbc);

        JPanel space = new JPanel();
        gbc.gridx = 1;
        gbc.gridy = 0; 
        gbc.weightx = 0; 
        northPanel.add(space, gbc);

        importBtn = new JButton("Import");
        importBtn.setPreferredSize(new Dimension(vw(10), vh(5)));
        importBtn.putClientProperty("FlatLaf.style", 
            "arc: 10; " + 
            "font: 14 $medium.font; " +
            "foreground: " + toHex(Color.WHITE) + "; " +
            "background: " + toHex(C_PRIMARY) + "; " +
            "hoverBackground: " + toHex(C_PRIMARY_HV) + "; "
        );

        gbc.gridx = 2; 
        gbc.gridy = 0; 
        gbc.weightx = 0; 
        gbc.fill = GridBagConstraints.NONE;
        northPanel.add(importBtn, gbc);
        importBtn.addActionListener(e -> openImportFileDialog());

        // == Center =========================================
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(C_CONTENT_BG);
        centerPanel.setBorder(new EmptyBorder(0, 10, 10, 10)); // Cách lề để không dính sát viền

        // Khởi tạo table như hướng dẫn trên
        String[] headers = {"ID", "CCCD", "SBD", "Họ", "Tên", "Trạng thái trúng tuyển", "Ngành trúng tuyển", "Chức năng"};
        model = new DefaultTableModel(headers, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Chỉ cho phép bấm nút "Chi tiết" ở cột cuối
            }
        };
        table = new JTable(model);

        // -- Customize table -----------------------------------------
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        int equalWidth = 130;
        for (int i = 0; i < 8; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(equalWidth);
        }
        // Gắn renderer + editor để cột "Chức năng" hiển thị nút thật thay vì text.
        table.getColumnModel().getColumn(7).setCellRenderer(new DetailButtonRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new DetailButtonEditor(table));

        table.getTableHeader().setPreferredSize(new Dimension(0, vh(5)));
        table.getTableHeader().putClientProperty("FlatLaf.style", 
            "font: 14 $medium.font; " +
            "background: " + toHex(C_PRIMARY) + "; " + // Màu nền header
            "foreground: " + toHex(C_CARD) + "; " // Màu chữ header
        );  
        table.putClientProperty("FlatLaf.style", 
            "font: 14 $font; " +
            "rowHeight: " + vh(4) + "; " +
            "showHorizontalLines: true; " +
            "selectionBackground: " + toHex(C_BORDER) + "; " +
            "selectionForeground: #000000; "
        );

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.putClientProperty("FlatLaf.style", 
            "arc: 15;" +
            "borderWidth: 0;" +
            "forcusWidth: 0;"
        );
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // == South =========================================
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setBackground(C_CONTENT_BG);
        southPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        Font paginationFont = new Font("Segoe UI", Font.PLAIN, 14);

        totalPageField = new JTextField(String.valueOf(totalPages));
        totalPageField.setEditable(false);
        totalPageField.setBorder(null);
        totalPageField.setBackground(C_CONTENT_BG);
        totalPageField.setPreferredSize(new Dimension(vw(4), vh(4)));
        totalPageField.setHorizontalAlignment(JTextField.CENTER);
        totalPageField.setFont(paginationFont);
        totalPageField.setFocusable(false);
        totalPageField.setRequestFocusEnabled(false);
        totalPageField.setHighlighter(null);

        JTextField per = new JTextField("/");
        per.setEditable(false);
        per.setBorder(null);
        per.setBackground(C_CONTENT_BG);
        per.setPreferredSize(new Dimension(vw(2), vh(4)));
        per.setHorizontalAlignment(JTextField.CENTER);
        per.setFont(paginationFont);
        per.setFocusable(false);
        per.setRequestFocusEnabled(false);
        per.setHighlighter(null);

        prevBtn = new JButton("<");
        prevBtn.setPreferredSize(new Dimension(vw(2), vh(4)));
        prevBtn.setFont(paginationFont);
        prevBtn.putClientProperty("FlatLaf.style",
            "font: 14 $medium.font; " +
            "background: " + toHex(C_PRIMARY) + "; " +
            "foreground: #FFFFFF; " +
            "disabledBackground: #D0D7E2; " +
            "disabledForeground: #7B8794;"
        );

        nextBtn = new JButton(">");
        nextBtn.setPreferredSize(new Dimension(vw(2), vh(4)));
        nextBtn.setFont(paginationFont);
        nextBtn.putClientProperty("FlatLaf.style",
            "font: 14 $medium.font; " +
            "background: " + toHex(C_PRIMARY) + "; " +
            "foreground: #FFFFFF; " +
            "disabledBackground: #D0D7E2; " +
            "disabledForeground: #7B8794;"
        );

        pageField = new JTextField(String.valueOf(1));
        pageField.setPreferredSize(new Dimension(vw(4), vh(4)));
        pageField.setHorizontalAlignment(JTextField.CENTER);
        pageField.setFont(paginationFont);

        prevBtn.addActionListener(e -> loadCandidates(getCurrentPage() - 1));
        nextBtn.addActionListener(e -> loadCandidates(getCurrentPage() + 1));
        pageField.addActionListener(e -> loadCandidates(getCurrentPage()));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadCandidates(1);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadCandidates(1);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadCandidates(1);
            }
        });

        southPanel.add(prevBtn);
        southPanel.add(pageField);
        southPanel.add(per);
        southPanel.add(totalPageField);
        southPanel.add(nextBtn);

        // == Add panels to main panel =========================
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        loadCandidates(1);
    }

    /**
     * Tải dữ liệu thí theo trang được yêu cầu
     * Cập nhật trạng thái phân trang (nút bấm, số trang hiện tại, tổng số trang)
     * @param requestedPage
     */
    private void loadCandidates(int requestedPage) {
        int safePage = Math.max(1, requestedPage);
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();

        ThiSinhService.SearchResult<ThiSinhXettuyen> result = thiSinhService.searchThiSinh(keyword, safePage, PAGE_SIZE);
        totalPages = Math.max(1, result.totalPages);

        int currentPage = Math.max(1, Math.min(result.currentPage, totalPages));
        pageField.setText(String.valueOf(currentPage));
        totalPageField.setText(String.valueOf(totalPages));
        prevBtn.setEnabled(currentPage > 1);
        nextBtn.setEnabled(currentPage < totalPages);

        model.setRowCount(0);
        for (ThiSinhXettuyen thiSinh : result.data) {
            model.addRow(new Object[] {
                thiSinh.getIdThiSinh(),
                thiSinh.getCccd(),
                thiSinh.getSoBaoDanh(),
                thiSinh.getHo(),
                thiSinh.getTen(),
                thiSinh.getTrangThaiTrungTuyen(),
                thiSinh.getNganhTrungTuyen(),
                "Chi tiết"
            });
        }
    }

    /**
     * Lấy trang hiện tại từ ô nhập trang, đảm bảo giá trị hợp lệ (số nguyên dương, không vượt quá tổng trang)
     * @return
     */
    private int getCurrentPage() {
        try {
            int parsed = Integer.parseInt(pageField.getText().trim());
            return Math.max(1, Math.min(parsed, totalPages));
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private void openImportFileDialog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file Excel thí sinh");
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));

        int selected = chooser.showOpenDialog(this);
        if (selected != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (file == null) {
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog loadingDialog = createImportLoadingDialog(owner);

        importBtn.setEnabled(false);

        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() {
                return excelImportService.importThiSinhVaDiemThi(file.getAbsolutePath());
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                importBtn.setEnabled(true);

                try {
                    int importedRows = get();
                    JOptionPane.showMessageDialog(
                        CandidateManagementPanel.this,
                        "Đã import thành công " + importedRows + " dòng từ file đã chọn.",
                        "Import thành công",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    loadCandidates(1);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    JOptionPane.showMessageDialog(
                        CandidateManagementPanel.this,
                        cause.getMessage(),
                        "Import thất bại",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
        loadingDialog.setVisible(true);
    }

    private JDialog createImportLoadingDialog(Window owner) {
        JDialog dialog = new JDialog(owner, "Đang import", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(C_CARD);

        JLabel title = new JLabel("Đang import dữ liệu thí sinh...");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(C_TITLE);

        JLabel message = new JLabel("Vui lòng chờ trong giây lát ...");
        message.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        message.setForeground(C_TEXT);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.putClientProperty("FlatLaf.style",
            "arc: 8; " +
            "foreground: " + toHex(C_PRIMARY) + ";"
        );

        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(message, BorderLayout.NORTH);
        center.add(progressBar, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        return dialog;
    }



    /**
     * Chuyển các thuộc tính màu sang chuỗi hex để sử dụng trong FlatLaf style
     * @param color
     * @return
     */
    private static String toHex(Color color) {
        if (color == null) return "#FFFFFF";
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Class 1: Renderer dùng để VẼ nút trong ô bảng.
     * Lưu ý: renderer chỉ để hiển thị, không xử lý click thực tế.
     */
    private class DetailButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton button = new JButton("Chi tiết");

        public DetailButtonRenderer() {
            setLayout(new GridBagLayout());
            // Tạo đường kẻ dưới để phân tách dòng rõ ràng
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
            
            button.putClientProperty("FlatLaf.style", 
                "arc: 10; " + 
                "background: " + toHex(C_WARNING) + "; " + 
                "foreground: #FFFFFF; " +
                "margin: 2,12,2,12; " + // Tăng độ dài ngang cho nút cân đối hơn
                "font: 12 $font;"
            );
            add(button);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // Màu nền thay đổi theo trạng thái chọn dòng
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    /**
     * Class 2: Editor dùng để xử lý TƯƠNG TÁC click nút trong ô bảng.
     * Đây là class quyết định hành vi khi người dùng bấm nút "Chi tiết".
     */
    private class DetailButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private final JPanel panel = new JPanel(new GridBagLayout());
        private final JButton button = new JButton("Chi tiết");
        private final JTable table;
        private int row;

        public DetailButtonEditor(JTable table) {
            this.table = table;
            // Đường kẻ dưới đồng bộ với Renderer
            panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
            
            button.putClientProperty("FlatLaf.style", 
                "arc: 8; background: " + toHex(C_WARNING) + "; foreground: #FFFFFF; margin: 2,12,2,12; font: 12 $font;");
            button.addActionListener(this);
            panel.add(button);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return "Chi tiết"; }

        @Override
        public void actionPerformed(ActionEvent e) {
            int modelRow = table.convertRowIndexToModel(row);
            String cccd = String.valueOf(table.getModel().getValueAt(modelRow, 1));
            ThiSinhService.ThiSinhInfo info = thiSinhService.getThiSinhFullInfo(cccd);
            if (info == null) {
                JOptionPane.showMessageDialog(CandidateManagementPanel.this,
                    "Không tìm thấy thí sinh để hiển thị chi tiết.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
                fireEditingStopped();
                return;
            }

            CandidateDetailDialog dialog = new CandidateDetailDialog(
                SwingUtilities.getWindowAncestor(CandidateManagementPanel.this),
                info
            );
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadCandidates(getCurrentPage());
            }
            fireEditingStopped();
        }
    }

    private class CandidateDetailDialog extends JDialog {
        private final ThiSinhService.ThiSinhInfo info;
        private final Integer candidateId;
        private final JTextField idThiSinhField = new JTextField();
        private final JTextField cccdField = new JTextField();
        private final JTextField soBaoDanhField = new JTextField();
        private final JTextField hoField = new JTextField();
        private final JTextField tenField = new JTextField();
        private final JTextField ngaySinhField = new JTextField();
        private final JTextField dienThoaiField = new JTextField();
        private final JTextField gioiTinhField = new JTextField();
        private final JTextField emailField = new JTextField();
        private final JTextField noiSinhField = new JTextField();
        private final JTextField doiTuongField = new JTextField();
        private final JTextField khuVucField = new JTextField();
        private final JTextField updatedAtField = new JTextField();
        private final JTextArea nguyenVongArea = new JTextArea();
        private boolean saved;

        private CandidateDetailDialog(Window owner, ThiSinhService.ThiSinhInfo info) {
            super(owner, "Chi tiết thí sinh", ModalityType.APPLICATION_MODAL);
            this.info = info;
            this.candidateId = info.thiSinh.getIdThiSinh();
            buildUi();
            fillData();
            setSize(Math.min(vw(75), 1100), Math.min(vh(80), 850));
            setLocationRelativeTo(CandidateManagementPanel.this);
        }

        private void buildUi() {
            JPanel root = new JPanel(new BorderLayout(12, 12));
            root.setBorder(new EmptyBorder(16, 16, 16, 16));
            root.setBackground(C_CONTENT_BG);

            JPanel content = new JPanel(new GridLayout(1, 2, 12, 12));
            content.setOpaque(false);

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(C_CARD);
            formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                new EmptyBorder(14, 14, 14, 14)
            ));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 0;

            addFormRow(formPanel, gbc, 0, "ID thí sinh", idThiSinhField);
            addFormRow(formPanel, gbc, 1, "CCCD", cccdField);
            addFormRow(formPanel, gbc, 2, "SBD", soBaoDanhField);
            addFormRow(formPanel, gbc, 3, "Họ", hoField);
            addFormRow(formPanel, gbc, 4, "Tên", tenField);
            addFormRow(formPanel, gbc, 5, "Ngày sinh", ngaySinhField);
            addFormRow(formPanel, gbc, 6, "Điện thoại", dienThoaiField);
            addFormRow(formPanel, gbc, 7, "Giới tính", gioiTinhField);
            addFormRow(formPanel, gbc, 8, "Email", emailField);
            addFormRow(formPanel, gbc, 9, "Nơi sinh", noiSinhField);
            addFormRow(formPanel, gbc, 10, "Đối tượng", doiTuongField);
            addFormRow(formPanel, gbc, 11, "Khu vực", khuVucField);
            addFormRow(formPanel, gbc, 12, "Cập nhật lúc", updatedAtField);

            JScrollPane formScroll = new JScrollPane(formPanel);
            formScroll.setBorder(null);
            formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
            rightPanel.setBackground(C_CARD);
            rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                new EmptyBorder(14, 14, 14, 14)
            ));

            JLabel nguyenVongLabel = new JLabel("Nguyện vọng");
            nguyenVongLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            nguyenVongLabel.setForeground(C_TITLE);
            rightPanel.add(nguyenVongLabel, BorderLayout.NORTH);

            nguyenVongArea.setEditable(false);
            nguyenVongArea.setLineWrap(true);
            nguyenVongArea.setWrapStyleWord(true);
            nguyenVongArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            nguyenVongArea.setBackground(C_CARD);
            nguyenVongArea.setBorder(new EmptyBorder(8, 8, 8, 8));

            JScrollPane nguyenVongScroll = new JScrollPane(nguyenVongArea);
            nguyenVongScroll.setBorder(BorderFactory.createLineBorder(C_BORDER));
            rightPanel.add(nguyenVongScroll, BorderLayout.CENTER);

            content.add(formScroll);
            content.add(rightPanel);
            root.add(content, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setOpaque(false);

            JButton cancelButton = new JButton("Đóng");
            JButton saveButton = new JButton("Lưu thay đổi");

            Dimension buttonSize = new Dimension(vw(8), vh(4.5f));
            cancelButton.setPreferredSize(buttonSize);
            saveButton.setPreferredSize(buttonSize);
            cancelButton.putClientProperty("FlatLaf.style",
                "arc: 10; " +
                "font: 14 $medium.font; " +
                "background: " + toHex(C_DANGER) + "; " +
                "hoverBackground: " + toHex(C_DANGER_HV) + "; " +
                "foreground: #FFFFFF;"
            );
            saveButton.putClientProperty("FlatLaf.style",
                "arc: 10; " +
                "font: 14 $medium.font; " +
                "background: " + toHex(C_PRIMARY) + "; " +
                "hoverBackground: " + toHex(C_PRIMARY_HV) + "; " +
                "foreground: #FFFFFF;"
            );

            cancelButton.addActionListener(e -> dispose());
            saveButton.addActionListener(e -> onSave());

            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
            root.add(buttonPanel, BorderLayout.SOUTH);

            setContentPane(root);
        }

        private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            label.setForeground(C_TEXT);

            field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            field.putClientProperty("FlatLaf.style", 
                "arc: 8; " +
                "font: 14 $font; " +
                "foreground: " + toHex(C_TEXT) + ";"
            );

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0;
            panel.add(label, gbc);

            gbc.gridx = 1;
            gbc.gridy = row;
            gbc.weightx = 1;
            panel.add(field, gbc);
        }

        private void fillData() {
            ThiSinhXettuyen thiSinh = info.thiSinh;
            idThiSinhField.setText(candidateId == null ? "" : String.valueOf(candidateId));
            idThiSinhField.setEditable(false);
            idThiSinhField.setFocusable(false);
            idThiSinhField.setBackground(new Color(0xF5, 0xF7, 0xFA));
            cccdField.setText(nullToEmpty(thiSinh.getCccd()));
            soBaoDanhField.setText(nullToEmpty(thiSinh.getSoBaoDanh()));
            hoField.setText(nullToEmpty(thiSinh.getHo()));
            tenField.setText(nullToEmpty(thiSinh.getTen()));
            ngaySinhField.setText(nullToEmpty(thiSinh.getNgaySinh()));
            dienThoaiField.setText(nullToEmpty(thiSinh.getDienThoai()));
            gioiTinhField.setText(nullToEmpty(thiSinh.getGioiTinh()));
            emailField.setText(nullToEmpty(thiSinh.getEmail()));
            noiSinhField.setText(nullToEmpty(thiSinh.getNoiSinh()));
            doiTuongField.setText(nullToEmpty(thiSinh.getDoiTuong()));
            khuVucField.setText(nullToEmpty(thiSinh.getKhuVuc()));

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            updatedAtField.setText(thiSinh.getUpdatedAt() == null ? "Chưa cập nhật" : formatter.format(thiSinh.getUpdatedAt()));
            updatedAtField.setEditable(false);
            updatedAtField.setBackground(new Color(0xF5, 0xF7, 0xFA));

            nguyenVongArea.setText(buildNguyenVongText(info.nguyenVongs));
            nguyenVongArea.setCaretPosition(0);
        }

        private void onSave() {
            ThiSinhXettuyen updated = new ThiSinhXettuyen();
            updated.setCccd(cccdField.getText().trim());
            updated.setSoBaoDanh(soBaoDanhField.getText().trim());
            updated.setHo(hoField.getText().trim());
            updated.setTen(tenField.getText().trim());
            updated.setNgaySinh(ngaySinhField.getText().trim());
            updated.setDienThoai(dienThoaiField.getText().trim());
            updated.setGioiTinh(gioiTinhField.getText().trim());
            updated.setEmail(emailField.getText().trim());
            updated.setNoiSinh(noiSinhField.getText().trim());
            updated.setDoiTuong(doiTuongField.getText().trim());
            updated.setKhuVuc(khuVucField.getText().trim());

            try {
                thiSinhService.updateThiSinhInfo(candidateId, updated);
                saved = true;
                JOptionPane.showMessageDialog(this,
                    "Đã cập nhật thông tin thí sinh.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        private String buildNguyenVongText(List<NguyenVongXettuyen> nguyenVongs) {
            if (nguyenVongs == null || nguyenVongs.isEmpty()) {
                return "Chưa có nguyện vọng";
            }

            StringBuilder builder = new StringBuilder();
            for (NguyenVongXettuyen nguyenVong : nguyenVongs) {
                builder.append("Nguyện vọng ")
                    .append(valueOrDefault(nguyenVong.getNvTt()))
                    .append("\nMã ngành: ")
                    .append(nullToEmpty(nguyenVong.getNvMaNganh()))
                    .append("\nKết quả: ")
                    .append(defaultIfBlank(nguyenVong.getNvKetqua(), "Chưa có"))
                    .append("\nĐiểm xét tuyển: ")
                    .append(valueOrDefault(nguyenVong.getDiemXettuyen()))
                    .append("\nĐiểm cộng: ")
                    .append(valueOrDefault(nguyenVong.getDiemCong()))
                    .append("\nĐiểm UTQD: ")
                    .append(valueOrDefault(nguyenVong.getDiemUtqd()))
                    .append("\nPhương thức: ")
                    .append(defaultIfBlank(nguyenVong.getTtPhuongThuc(), "Chưa có"))
                    .append("\nTHM: ")
                    .append(defaultIfBlank(nguyenVong.getTtThm(), "Chưa có"))
                    .append("\n\n");
            }
            return builder.toString().trim();
        }

        private boolean isSaved() {
            return saved;
        }
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String defaultIfBlank(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private static String valueOrDefault(Number value) {
        return value == null ? "Chưa có" : String.valueOf(value);
    }

}
