package com.xettuyen.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import com.xettuyen.ui.panel.*;
/**
 * MainFrame – Modern Redesign (đồng bộ với LoginForm v2)
 *
 * Cải tiến:
 * ─────────────────────────────────────────────────────────
 * [RESPONSIVE]
 *   • Mọi kích thước tính theo vw/vh runtime (giống LoginForm).
 *   • setExtendedState(MAXIMIZED_BOTH) → full screen mặc định.
 *   • Sidebar width = clamp(vw(16), 180, 260).
 *   • Font, icon size, padding đều scale theo màn hình.
 *
 * [SIDEBAR]
 *   • Nền gradient dọc từ #0a1628 → #0e2040 (dark navy).
 *   • Logo / brand block ở trên với accent line #0062ff.
 *   • Mỗi menu item là SidebarButton: vẽ tay bằng Graphics2D,
 *     bo góc phải 0 / trái 10px (pill bên trái), có hiệu ứng
 *     hover mượt và indicator bar bên trái khi active.
 *   • Emoji icon vẽ qua Graphics2D (không bị "...").
 *   • Nút Đăng Xuất tách biệt ở dưới cùng.
 *
 * [HEADER]
 *   • Nền #0062ff với gradient nhẹ.
 *   • Bên trái: tên hệ thống + breadcrumb trang hiện tại.
 *   • Bên phải: chip avatar + tên user + role badge.
 *
 * [CONTENT AREA]
 *   • Nền #f0f4ff (xanh rất nhạt).
 *   • Mỗi panel con có card trắng bo góc, shadow nhẹ.
 *   • Home panel: stats cards + welcome message.
 *
 * [FOOTER]
 *   • Mỏng, tinh tế, font nhỏ căn phải.
 * ─────────────────────────────────────────────────────────
 */
public class MainFrame extends JFrame {

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

    // ── State ─────────────────────────────────────────────
    private final String currentUser;
    private final String userRole;

    private CardLayout cardLayout;
    private JPanel     contentPanel;
    private String     activeCard = "HOME";

    // ── Panels ────────────────────────────────────────────
    private JPanel homePanel;
    private JPanel thiSinhPanel;
    private JPanel nganhPanel;
    private JPanel diemPanel;
    private JPanel nguyenVongPanel;
    private JPanel userPanel;

    public MainFrame(String username, String role) {
        this.currentUser = username;
        this.userRole    = role;
        initComponents();
        setupUI();
    }

    // ══════════════════════════════════════════════════════
    //  INIT
    // ══════════════════════════════════════════════════════
    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.add(createHeaderPanel(),  BorderLayout.NORTH);
        this.add(createSidebarPanel(), BorderLayout.WEST);
        this.add(createContentPanel(), BorderLayout.CENTER);
        this.add(createFooterPanel(),  BorderLayout.SOUTH);
    }

    private void setupUI() {
        this.setTitle("Hệ Thống Xét Tuyển 2026");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setMinimumSize(new Dimension(900, 600));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        cardLayout.show(contentPanel, "HOME");
    }

    // ══════════════════════════════════════════════════════
    //  HEADER
    // ══════════════════════════════════════════════════════
    private JPanel createHeaderPanel() {
        int hdrH     = clamp(vh(6), 52, 76);
        int fontTitle= clamp(vh(2.2f), 14, 22);
        int fontSub  = clamp(vh(1.4f), 10, 14);

        JPanel hdr = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, C_PRIMARY, getWidth(), 0, C_PRIMARY_HV));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Bottom shadow line
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2.dispose();
            }
        };
        hdr.setOpaque(false);
        hdr.setPreferredSize(new Dimension(0, hdrH));
        int padH = clamp(vh(1.2f), 8, 14); // Padding trên/dưới mỏng lại một chút
        int padW = clamp(vw(1.5f), 16, 28);
        hdr.setBorder(new EmptyBorder(padH, padW, padH, padW));

        // ── Left: title + breadcrumb ──
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel titleLbl = new JLabel("HỆ THỐNG XÉT TUYỂN 2026");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, fontTitle));
        titleLbl.setForeground(Color.WHITE);


        left.add(titleLbl);
        left.add(Box.createVerticalStrut(2));

        // ── Right: user chip ──
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, clamp(vw(0.8f), 8, 14), 0));
        right.setOpaque(false);

        // Avatar circle (first letter)
        JComponent avatar = new JComponent() {
            { setPreferredSize(new Dimension(clamp(vh(3.5f), 28, 42), clamp(vh(3.5f), 28, 42))); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFF, 0xFF, 0xFF, 50));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                String init = currentUser.isEmpty() ? "?" : String.valueOf(currentUser.charAt(0)).toUpperCase();
                Font f = new Font("Segoe UI", Font.BOLD, clamp(vh(1.8f), 12, 18));
                g2.setFont(f);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(init, (getWidth() - fm.stringWidth(init)) / 2,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };

        JLabel userLbl = new JLabel(currentUser);
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, fontSub + 1));
        userLbl.setForeground(Color.WHITE);

        // Role badge
        JLabel roleBadge = new JLabel(" " + userRole.toUpperCase() + " ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = "admin".equalsIgnoreCase(userRole)
                        ? new Color(0xFF, 0xA0, 0x00, 180)
                        : new Color(0x00, 0xCC, 0x88, 180);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        roleBadge.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(1.3f), 9, 12)));
        roleBadge.setForeground(Color.WHITE);
        roleBadge.setOpaque(false);

        right.add(avatar);
        right.add(Box.createHorizontalStrut(6));
        right.add(userLbl);
        right.add(Box.createHorizontalStrut(6));
        right.add(roleBadge);

        hdr.add(left,  BorderLayout.WEST);
        hdr.add(right, BorderLayout.EAST);
        return hdr;
    }

    // ══════════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════════
    private JPanel createSidebarPanel() {
        int sideW   = clamp(vw(16), 185, 260);
        int padTop  = clamp(vh(2),  14, 28);
        int padSide = clamp(vw(0.8f), 8, 14);

        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, C_SIDEBAR_TOP, 0, getHeight(), C_SIDEBAR_BOT));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Right border subtle glow
                g2.setColor(new Color(0x00, 0x62, 0xFF, 60));
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(sideW, 0));
        sidebar.setBorder(new EmptyBorder(padTop, padSide, padTop, padSide));

        // ── Brand block ──
        JPanel brand = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFF, 0xFF, 0xFF, 15));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                // Bottom accent
                g2.setColor(C_PRIMARY);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        brand.setOpaque(false);
        int brandPad = clamp(vh(1.2f), 8, 16);
        brand.setBorder(new EmptyBorder(brandPad, brandPad, brandPad + 2, brandPad));
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, clamp(vh(8), 60, 90)));

        JLabel brandIcon = new JLabel("🎓");
        brandIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, clamp(vh(3.5f), 24, 36)));

        JPanel brandText = new JPanel();
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.setOpaque(false);
        JLabel brandTitle = new JLabel("Xét Tuyển");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(1.8f), 12, 17)));
        brandTitle.setForeground(Color.WHITE);
        JLabel brandYear = new JLabel("Hệ thống 2026");
        brandYear.setFont(new Font("Segoe UI", Font.PLAIN, clamp(vh(1.3f), 9, 12)));
        brandYear.setForeground(new Color(0x80, 0xA0, 0xCC));
        brandText.add(brandTitle);
        brandText.add(brandYear);

        brand.add(brandIcon, BorderLayout.WEST);
        brand.add(Box.createHorizontalStrut(8), BorderLayout.CENTER);
        brand.add(brandText, BorderLayout.EAST);
        // Workaround: use a wrapper to get proper horizontal layout
        JPanel brandWrapper = new JPanel(new BorderLayout(8, 0));
        brandWrapper.setOpaque(false);
        brandWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, clamp(vh(8), 60, 90)));
        brandWrapper.setBorder(brand.getBorder());
        brandWrapper.add(brandIcon, BorderLayout.WEST);
        brandWrapper.add(brandText, BorderLayout.CENTER);

        // Paint brand background on wrapper
        JPanel brandFinal = new JPanel(new BorderLayout(8, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFF, 0xFF, 0xFF, 15));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(C_PRIMARY);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        brandFinal.setOpaque(false);
        brandFinal.setMaximumSize(new Dimension(Integer.MAX_VALUE, clamp(vh(9), 64, 96)));
        brandFinal.setBorder(new EmptyBorder(brandPad, brandPad, brandPad + 2, brandPad));
        brandFinal.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandFinal.add(brandIcon, BorderLayout.WEST);
        brandFinal.add(brandText, BorderLayout.CENTER);

        sidebar.add(brandFinal);
        sidebar.add(Box.createVerticalStrut(clamp(vh(2), 14, 24)));

        // ── Divider label ──
        JLabel navLabel = new JLabel("ĐIỀU HƯỚNG");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(1.2f), 9, 11)));
        navLabel.setForeground(new Color(0x50, 0x70, 0xA0));
        navLabel.setBorder(new EmptyBorder(0, 4, clamp(vh(0.8f), 5, 10), 0));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navLabel);

        // ── Menu items ──
        int itemH = clamp(vh(5.5f), 40, 58);
        SidebarButton homeBtn = addSidebarItem(sidebar, "🏠", "Trang Chủ", "HOME", itemH, false);
        homeBtn.setActive(true);
        addSidebarItem(sidebar, "👥", "Quản Lý Thí Sinh",  "THI_SINH",   itemH, false);
        addSidebarItem(sidebar, "🎓", "Quản Lý Ngành",     "NGANH",      itemH, false);
        addSidebarItem(sidebar, "📊", "Quản Lý Điểm",      "DIEM",       itemH, false);
        addSidebarItem(sidebar, "📋", "Nguyện Vọng",       "NGUYEN_VONG",itemH, false);

        if ("admin".equalsIgnoreCase(userRole)) {
            sidebar.add(Box.createVerticalStrut(clamp(vh(1), 6, 12)));
            JLabel sysLabel = new JLabel("HỆ THỐNG");
            sysLabel.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(1.2f), 9, 11)));
            sysLabel.setForeground(new Color(0x50, 0x70, 0xA0));
            sysLabel.setBorder(new EmptyBorder(0, 4, clamp(vh(0.8f), 5, 10), 0));
            sysLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(sysLabel);
            addSidebarItem(sidebar, "👤", "Quản Lý Hệ Thống","USER", itemH, false);
        }

        sidebar.add(Box.createVerticalGlue());

        // ── Logout ──
        addSidebarItem(sidebar, "🚪", "Đăng Xuất", "LOGOUT", itemH, true);

        return sidebar;
    }

    private SidebarButton addSidebarItem(JPanel sidebar, String emoji, String label,
                                 String card, int itemH, boolean isDanger) {
        SidebarButton btn = new SidebarButton(emoji, label, card, itemH, isDanger);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, itemH));
        btn.addActionListener(e -> switchCard(card, label, btn));
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(clamp(vh(0.5f), 3, 7)));
        return btn;
    }

    private void switchCard(String card, String label, SidebarButton clicked) {
        if ("LOGOUT".equals(card)) {
            int r = JOptionPane.showConfirmDialog(
                    this, "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác Nhận Đăng Xuất", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(LoginForm::new);
            }
            return;
        }
        activeCard = card;
        cardLayout.show(contentPanel, card);

        // Update active state on all SidebarButtons
        Container sidebar = clicked.getParent();
        for (Component c : sidebar.getComponents())
            if (c instanceof SidebarButton) ((SidebarButton) c).setActive(c == clicked);
    }

    // ══════════════════════════════════════════════════════
    //  CONTENT
    // ══════════════════════════════════════════════════════
    private JPanel createContentPanel() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(C_CONTENT_BG);

        homePanel       = new HomePanel(currentUser);
        thiSinhPanel    = new CandidateManagementPanel();
        nganhPanel      = createPlaceholderPanel("🎓", "Quản Lý Ngành",
                new String[]{"Danh sách ngành học","Thêm / Sửa / Xóa ngành","Quản lý tổ hợp môn","Bảng quy đổi điểm"});
        diemPanel = new DiemPanel();
        /* diemPanel       = createPlaceholderPanel("📊", "Quản Lý Điểm",
                new String[]{"Nhập điểm thi","Điểm cộng / ưu tiên","Lịch sử chỉnh sửa","Export kết quả"});*/
        nguyenVongPanel = createPlaceholderPanel("📋", "Quản Lý Nguyện Vọng",
                new String[]{"Danh sách nguyện vọng","Nhập nguyện vọng","Tính điểm xét tuyển","Kết quả trúng tuyển"});
        userPanel       = new UserManagementPanel();

        contentPanel.add(homePanel,       "HOME");
        contentPanel.add(thiSinhPanel,    "THI_SINH");
        contentPanel.add(nganhPanel,      "NGANH");
        contentPanel.add(diemPanel,       "DIEM");
        contentPanel.add(nguyenVongPanel, "NGUYEN_VONG");
        contentPanel.add(userPanel,       "USER");

        return contentPanel;
    }

    // ── Placeholder panels ────────────────────────────────
    private JPanel createPlaceholderPanel(String emoji, String title, String[] features) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(C_CONTENT_BG);

        JPanel card = makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        int pad = clamp(vw(2), 24, 40);
        card.setBorder(new EmptyBorder(pad, pad, pad, pad));
        card.setPreferredSize(new Dimension(clamp(vw(50), 400, 680), 0));

        // Icon
        JLabel iconLbl = new JLabel(emoji);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, clamp(vh(6), 48, 72)));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLbl.setBorder(new EmptyBorder(0, 0, clamp(vh(1.5f), 10, 18), 0));

        // Title
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(2.5f), 16, 24)));
        titleLbl.setForeground(C_TITLE);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLbl.setBorder(new EmptyBorder(0, 0, clamp(vh(0.8f), 5, 10), 0));

        // Under construction badge
        JLabel badge = new JLabel("  Đang phát triển  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFF, 0xA5, 0x00, 40));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(C_WARNING);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(1.4f), 10, 13)));
        badge.setForeground(C_WARNING);
        badge.setOpaque(false);
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));

        // Separator
        JSeparator sep = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(C_BORDER);
                g.fillRect(0, getHeight() / 2, getWidth(), 1);
            }
        };
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Features
        JLabel featuresTitle = new JLabel("Tính năng dự kiến:");
        featuresTitle.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(1.6f), 11, 14)));
        featuresTitle.setForeground(C_HINT);
        featuresTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        featuresTitle.setBorder(new EmptyBorder(clamp(vh(1.5f), 10, 18), 0, clamp(vh(0.8f), 5, 10), 0));

        card.add(iconLbl);
        card.add(titleLbl);
        card.add(badge);
        card.add(Box.createVerticalStrut(clamp(vh(1.5f), 12, 20)));
        card.add(sep);
        card.add(featuresTitle);

        for (String f : features) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
            row.setOpaque(false);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, clamp(vh(3.5f), 26, 38)));

            JComponent dot = new JComponent() {
                { setPreferredSize(new Dimension(8, 8)); }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(C_PRIMARY);
                    g2.fillOval(0, 0, 8, 8);
                    g2.dispose();
                }
            };
            JLabel fl = new JLabel(f);
            fl.setFont(new Font("Segoe UI", Font.PLAIN, clamp(vh(1.7f), 11, 15)));
            fl.setForeground(C_TEXT);
            row.add(dot);
            row.add(fl);
            card.add(row);
        }

        outer.add(card);
        return outer;
    }

    // ── Card factory ──────────────────────────────────────
    public static JPanel makeCard() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = clamp(vw(0.8f), 10, 18);
                // Shadow
                for (int i = 3; i >= 1; i--) {
                    g2.setColor(new Color(0, 0, 40, 7 * i));
                    g2.fill(new RoundRectangle2D.Float(i, i + 1, getWidth() - i, getHeight() - i, arc, arc));
                }
                g2.setColor(C_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, arc, arc));
                g2.dispose();
            }
        };
    }

    // ══════════════════════════════════════════════════════
    //  FOOTER
    // ══════════════════════════════════════════════════════
    private JPanel createFooterPanel() {
        int ftrH = clamp(vh(3), 22, 34);
        JPanel footer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(0xE8, 0xEE, 0xF8));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(C_BORDER);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(0, ftrH));
        footer.setBorder(new EmptyBorder(0, clamp(vw(1.5f), 14, 22), 0, clamp(vw(1.5f), 14, 22)));

        JLabel left = new JLabel("© 2026 Hệ Thống Xét Tuyển – Phiên bản 1.0");
        left.setFont(new Font("Segoe UI", Font.PLAIN, clamp(vh(1.3f), 9, 12)));
        left.setForeground(C_HINT);

        JLabel right = new JLabel("Đăng nhập: " + currentUser);
        right.setFont(new Font("Segoe UI", Font.PLAIN, clamp(vh(1.3f), 9, 12)));
        right.setForeground(C_HINT);

        footer.add(left,  BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    // ══════════════════════════════════════════════════════
    //  SIDEBAR BUTTON
    //  Custom JComponent: vẽ emoji + text bằng Graphics2D,
    //  indicator bar trái khi active, hover animation mượt.
    // ══════════════════════════════════════════════════════
    static class SidebarButton extends JComponent {
        private final String emoji;
        private final String label;
        private final String card;
        private final int    itemH;
        private final boolean isDanger;
        private boolean active   = false;
        private boolean hovered  = false;
        private float   hoverProg = 0f;
        private Timer   hoverTimer;

        private final java.util.List<ActionListener> listeners = new java.util.ArrayList<>();

        SidebarButton(String emoji, String label, String card, int itemH, boolean isDanger) {
            this.emoji    = emoji;
            this.label    = label;
            this.card     = card;
            this.itemH    = itemH;
            this.isDanger = isDanger;
            // Avoid zero-width preferred size; BoxLayout may visually center such components.
            setPreferredSize(new Dimension(160, itemH));
            setMinimumSize(new Dimension(120, itemH));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, itemH));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFocusable(true);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { animHover(true);  }
                @Override public void mouseExited (MouseEvent e) { animHover(false); }
                @Override public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) fire();
                }
            });
            addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) fire();
                }
            });
        }

        void setActive(boolean a) { this.active = a; repaint(); }
        void addActionListener(ActionListener l) { listeners.add(l); }
        private void fire() {
            listeners.forEach(l -> l.actionPerformed(
                    new ActionEvent(this, ActionEvent.ACTION_PERFORMED, card)));
        }

        private void animHover(boolean in) {
            hovered = in;
            if (hoverTimer != null && hoverTimer.isRunning()) hoverTimer.stop();
            hoverTimer = new Timer(12, e -> {
                hoverProg += in ? 0.12f : -0.12f;
                hoverProg  = Math.max(0f, Math.min(1f, hoverProg));
                repaint();
                if ((in && hoverProg >= 1f) || (!in && hoverProg <= 0f))
                    ((Timer) e.getSource()).stop();
            });
            hoverTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int arc = clamp(h / 3, 8, 14);
            int barW = 4;

            // Background
            Color bgColor;
            if (isDanger) {
                bgColor = new Color(220, 53, 69,
                        (int)(30 + hoverProg * 60));
            } else if (active) {
                bgColor = new Color(0x00, 0x62, 0xFF, 220);
            } else {
                bgColor = new Color(255, 255, 255, (int)(hoverProg * 55));
            }
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Float(barW + 2, 2, w - barW - 4, h - 4, arc, arc));

            // Active indicator bar
            if (active && !isDanger) {
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, h * 0.2f, barW, h * 0.6f, barW, barW));
            }

            // Emoji (55% of itemH, centered vertically)
            int emojiSize = Math.max(10, (int)(h * 0.45));
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, emojiSize));
            FontMetrics efm = g2.getFontMetrics();
            int ex = barW + clamp(w / 14, 8, 14);
            int ey = (h - efm.getHeight()) / 2 + efm.getAscent();
            Color emojiColor = (active || hoverProg > 0.5f) ? Color.WHITE : new Color(0xC8, 0xD8, 0xF0);
            if (isDanger) emojiColor = new Color(0xFF, 0xB0, 0xB8);
            g2.setColor(emojiColor);
            g2.drawString(emoji, ex, ey);

            // Label text
            int labelFontSize = Math.max(9, (int)(h * 0.30));
            g2.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, labelFontSize));
            FontMetrics lfm = g2.getFontMetrics();
            int lx = ex + emojiSize + clamp(w / 16, 6, 12);
            int ly = (h - lfm.getHeight()) / 2 + lfm.getAscent();
            Color labelColor;
            if (isDanger)        labelColor = new Color(0xFF, 0xB0, 0xB8);
            else if (active)     labelColor = Color.WHITE;
            else                 labelColor = new Color(
                    (int)(0xC8 + hoverProg * (0xFF - 0xC8)),
                    (int)(0xD8 + hoverProg * (0xFF - 0xD8)),
                    (int)(0xF0 + hoverProg * (0xFF - 0xF0)));
            g2.setColor(labelColor);

            // Clip text if too wide
            String displayLabel = label;
            while (lfm.stringWidth(displayLabel) > w - lx - 8 && displayLabel.length() > 4)
                displayLabel = displayLabel.substring(0, displayLabel.length() - 4) + "…";
            g2.drawString(displayLabel, lx, ly);

            g2.dispose();
        }
    }
}