package com.xettuyen.ui;

import com.xettuyen.dao.UserDAO;
import com.xettuyen.dao.DAOFactory;
import com.xettuyen.entity.User;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * LoginForm – Modern Redesign v2
 *
 * Thay đổi so với v1:
 * ─────────────────────────────────────────────────────────
 * [RESPONSIVE]
 *   • Tất cả kích thước (font, padding, inset, field height,
 *     button size) tính theo % màn hình tại runtime.
 *   • Card chiếm ~36vw, min 360px, max 560px.
 *   • Không còn bất kỳ hằng số pixel cứng nào cho UI size.
 *   • JFrame dùng pack() để tự co theo nội dung đã scale.
 *
 * [FIX ICON ẩn/hiện mật khẩu]
 *   • Không dùng JButton.setText(emoji) — Swing clip thành "..."
 *     khi layout width < text preferred width.
 *   • Thay bằng EmojiIconButton: custom JComponent, tự vẽ
 *     emoji qua Graphics2D.drawString() với font size = 55%
 *     chiều cao ô → luôn vừa khít, không bao giờ bị clip.
 * ─────────────────────────────────────────────────────────
 */
public class LoginForm extends JFrame {

    // ── Bảng màu ──────────────────────────────────────────
    private static final Color C_PRIMARY    = new Color(0x00, 0x62, 0xFF);
    private static final Color C_PRIMARY_HV = new Color(0x00, 0x4E, 0xCC);
    private static final Color C_DANGER     = new Color(0xDC, 0x35, 0x45);
    private static final Color C_DANGER_HV  = new Color(0xB0, 0x2A, 0x37);
    private static final Color C_SUCCESS    = new Color(0x19, 0x87, 0x54);
    private static final Color C_CARD       = new Color(0xFA, 0xFB, 0xFF);
    private static final Color C_BORDER     = new Color(0xE0, 0xE0, 0xE0);
    private static final Color C_LABEL      = new Color(0x37, 0x37, 0x3A);
    private static final Color C_HINT       = new Color(0x9E, 0x9E, 0x9E);
    private static final Color C_TITLE      = new Color(0x12, 0x12, 0x14);

    // ── Scale helpers ─────────────────────────────────────
    private static final int   SW;
    private static final int   SH;
    private static final float SU;   // 1 SU = 1vw

    static {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        SW = screen.width;
        SH = screen.height;
        SU = SW / 100f;
    }

    /** % chiều rộng màn hình → px */
    private static int vw(float pct) { return Math.round(SU * pct); }
    /** % chiều cao màn hình → px */
    private static int vh(float pct) { return Math.round(SH * pct / 100f); }
    /** Giới hạn giá trị trong [min, max] */
    private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }

    // ── Components ────────────────────────────────────────
    private RoundedTextField     usernameField;
    private RoundedPasswordField passwordField;
    private RoundedButton        loginButton;
    private RoundedButton        exitButton;
    private JLabel               messageLabel;
    private boolean              passwordVisible = false;
    private EmojiIconButton      toggleBtn;

    public LoginForm() {
        initComponents();
        setupUI();
    }

    // ══════════════════════════════════════════════════════
    //  BUILD UI
    // ══════════════════════════════════════════════════════
    private void initComponents() {

        // ── Kích thước responsive ─────────────────────────
        int cardW     = clamp(vw(36),    360, 560);
        int fieldH    = clamp(vh(5),      38,  56);
        int toggleW   = fieldH;                       // nút toggle = hình vuông = fieldH
        int btnH      = clamp(vh(5),      38,  52);
        int btnLoginW = clamp(vw(12),    120, 180);
        int btnExitW  = clamp(vw(9),      90, 140);
        int padH      = clamp(vh(4),      28,  52);
        int padW      = clamp(vw(3),      28,  52);
        int gapS      = clamp(vh(1),       6,  14);
        int gapM      = clamp(vh(2),      12,  22);
        int gapL      = clamp(vh(2.5f),   16,  28);
        int iconFontSz= clamp(vh(5),      32,  60);

        Font fTitle  = new Font("Segoe UI", Font.BOLD,  clamp(vh(2.8f), 16, 28));
        Font fSub    = new Font("Segoe UI", Font.PLAIN, clamp(vh(1.6f), 11, 16));
        Font fLabel  = new Font("Segoe UI", Font.PLAIN, clamp(vh(1.6f), 11, 16));
        Font fInput  = new Font("Segoe UI", Font.PLAIN, clamp(vh(1.8f), 12, 18));
        Font fButton = new Font("Segoe UI", Font.BOLD,  clamp(vh(1.8f), 12, 17));
        Font fMsg    = new Font("Segoe UI", Font.PLAIN, clamp(vh(1.5f), 10, 14));

        // ── Background ────────────────────────────────────
        JPanel bg = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(0xEEF4FF),
                        getWidth(), getHeight(), new Color(0xF4F8FF)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setOpaque(true);

        // ── Card ──────────────────────────────────────────
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                int arc = clamp(vw(1), 12, 22);
                for (int i = 4; i >= 1; i--) {
                    g2.setColor(new Color(0, 0, 0, 6 * i));
                    g2.fill(new RoundRectangle2D.Float(i, i + 1, w - i, h - i, arc, arc));
                }
                g2.setColor(C_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 4, h - 4, arc, arc));
                int barH = clamp(vh(0.6f), 4, 7);
                g2.setColor(C_PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 4, barH, arc / 2, arc / 2));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(padH, padW, padH - 4, padW));

        // ── Tiêu đề ───────────────────────────────────────
        JLabel iconLabel = new JLabel("🎓");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, iconFontSz));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titleLabel = new JLabel("HỆ THỐNG XÉT TUYỂN 2026");
        titleLabel.setFont(fTitle);
        titleLabel.setForeground(C_TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Đăng nhập để tiếp tục");
        subtitleLabel.setFont(fSub);
        subtitleLabel.setForeground(C_HINT);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JSeparator sep = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(C_BORDER);
                g.fillRect(0, getHeight() / 2, getWidth(), 1);
            }
        };
        sep.setOpaque(false);

        // ── Input labels ──────────────────────────────────
        JLabel usernameLabel = makeLabel("Tên đăng nhập", fLabel);
        JLabel passwordLabel  = makeLabel("Mật khẩu",      fLabel);

        // ── Input fields ──────────────────────────────────
        usernameField = new RoundedTextField();
        usernameField.setFont(fInput);
        usernameField.setPlaceholder("Nhập tên đăng nhập...");
        usernameField.setPreferredSize(new Dimension(cardW - padW * 2, fieldH));

        passwordField = new RoundedPasswordField();
        passwordField.setFont(fInput);
        passwordField.setPlaceholder("Nhập mật khẩu...");
        // Width = full card - padding - toggleBtn - 2px gap
        passwordField.setPreferredSize(new Dimension(cardW - padW * 2 - toggleW - 2, fieldH));

        // ── EmojiIconButton (KHÔNG dùng JButton text) ─────
        toggleBtn = new EmojiIconButton("👁", toggleW, fieldH);
        toggleBtn.addActionListener(e -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                passwordField.setEchoChar((char) 0);
                toggleBtn.setEmoji("🙈");
            } else {
                passwordField.setEchoChar('●');
                toggleBtn.setEmoji("👁");
            }
        });

        JPanel pwdWrapper = new JPanel(new BorderLayout(2, 0));
        pwdWrapper.setOpaque(false);
        pwdWrapper.add(passwordField, BorderLayout.CENTER);
        pwdWrapper.add(toggleBtn,     BorderLayout.EAST);

        // ── Buttons ───────────────────────────────────────
        loginButton = new RoundedButton("Đăng Nhập", C_PRIMARY, C_PRIMARY_HV);
        loginButton.setFont(fButton);
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(btnLoginW, btnH));
        loginButton.addActionListener(new LoginActionListener());

        exitButton = new RoundedButton("Thoát", C_DANGER, C_DANGER_HV);
        exitButton.setFont(fButton);
        exitButton.setForeground(Color.WHITE);
        exitButton.setPreferredSize(new Dimension(btnExitW, btnH));
        exitButton.addActionListener(e -> System.exit(0));

        // ── Message label ─────────────────────────────────
        messageLabel = new JLabel(" ");
        messageLabel.setFont(fMsg);
        messageLabel.setForeground(C_DANGER);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // ── Enter key ─────────────────────────────────────
        passwordField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) loginButton.doClick();
            }
        });

        // ════ Card layout ═════════════════════════════════
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        gc.gridwidth = 2;
        int r = 0;

        gc.gridy = r++; gc.insets = new Insets(0, 0, gapS,     0); card.add(iconLabel,     gc);
        gc.gridy = r++; gc.insets = new Insets(0, 0, gapS / 2, 0); card.add(titleLabel,    gc);
        gc.gridy = r++; gc.insets = new Insets(0, 0, gapM,     0); card.add(subtitleLabel, gc);
        gc.gridy = r++; gc.insets = new Insets(0, 0, gapL,     0); card.add(sep,           gc);
        gc.gridy = r++; gc.insets = new Insets(0, 2, gapS / 2, 0); card.add(usernameLabel, gc);
        gc.gridy = r++; gc.insets = new Insets(0, 0, gapM,     0); card.add(usernameField, gc);
        gc.gridy = r++; gc.insets = new Insets(0, 2, gapS / 2, 0); card.add(passwordLabel, gc);
        gc.gridy = r++; gc.insets = new Insets(0, 0, gapS,     0); card.add(pwdWrapper,    gc);
        gc.gridy = r++; gc.insets = new Insets(0, 0, gapM,     0); card.add(messageLabel,  gc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, clamp(vw(1), 10, 18), 0));
        btnRow.setOpaque(false);
        btnRow.add(loginButton);
        btnRow.add(exitButton);
        gc.gridy = r; gc.insets = new Insets(0, 0, 0, 0);
        card.add(btnRow, gc);

        bg.add(card);
        this.setContentPane(bg);
    }

    private void setupUI() {
        this.setTitle("Đăng Nhập – Hệ Thống Xét Tuyển 2026");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setMinimumSize(new Dimension(380, 460));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private JLabel makeLabel(String text, Font f) {
        JLabel l = new JLabel(text);
        l.setFont(f);
        l.setForeground(C_LABEL);
        return l;
    }

    // ══════════════════════════════════════════════════════
    //  LOGIN LOGIC  (giữ nguyên 100%)
    // ══════════════════════════════════════════════════════
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                showMessage("❌ Vui lòng nhập tên đăng nhập và mật khẩu!", C_DANGER);
                return;
            }

            try {
                UserDAO userDAO = DAOFactory.getUserDAO();
                User    user    = userDAO.login(username, password);

                if (user != null) {
                    showMessage("✅ Đăng nhập thành công!", C_SUCCESS);
                    Timer t = new Timer(500, ev -> {
                        LoginForm.this.dispose();
                        new MainFrame(user.getUsername(), user.getRole()).setVisible(true);
                    });
                    t.setRepeats(false);
                    t.start();
                } else {
                    showMessage("❌ Tên đăng nhập hoặc mật khẩu không đúng!", C_DANGER);
                    passwordField.setText("");
                }
            } catch (Exception ex) {
                showMessage("❌ Lỗi hệ thống: " + ex.getMessage(), C_DANGER);
                ex.printStackTrace();
            }
        }
    }

    private void showMessage(String text, Color color) {
        messageLabel.setText(text);
        messageLabel.setForeground(color);
    }

    // ══════════════════════════════════════════════════════
    //  EMOJI ICON BUTTON
    //  ► Custom JComponent — vẽ emoji bằng Graphics2D.drawString()
    //    → không bao giờ bị Swing clip thành "..."
    //  ► Font size = 55% chiều cao ô → luôn vừa khít
    // ══════════════════════════════════════════════════════
    static class EmojiIconButton extends JComponent {
        private String emoji;
        private boolean hovered = false;
        private final java.util.List<ActionListener> listeners = new java.util.ArrayList<>();

        EmojiIconButton(String emoji, int w, int h) {
            this.emoji = emoji;
            setPreferredSize(new Dimension(w, h));
            setMinimumSize(new Dimension(w, h));
            setMaximumSize(new Dimension(w, h));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Hiện / ẩn mật khẩu");
            setFocusable(true);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) fire();
                }
            });
            addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE
                            || e.getKeyCode() == KeyEvent.VK_ENTER) fire();
                }
            });
        }

        void setEmoji(String s) { this.emoji = s; repaint(); }
        void addActionListener(ActionListener l) { listeners.add(l); }
        private void fire() {
            ActionEvent ev = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "toggle");
            listeners.forEach(l -> l.actionPerformed(ev));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Hover circle
            if (hovered) {
                g2.setColor(new Color(0, 0, 0, 18));
                int d = Math.min(getWidth(), getHeight()) - 2;
                g2.fillOval((getWidth() - d) / 2, (getHeight() - d) / 2, d, d);
            }

            // Emoji: font size = 55% of cell height, always fits
            int fontSize = Math.max(10, (int)(getHeight() * 0.55));
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth()  - fm.stringWidth(emoji)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(new Color(0x55, 0x55, 0x66));
            g2.drawString(emoji, x, y);
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════
    //  ROUNDED BORDER
    // ══════════════════════════════════════════════════════
    static class RoundedBorder extends AbstractBorder {
        private final int radius, pad;
        private final Color color;

        RoundedBorder(int radius, Color color, int pad) {
            this.radius = radius; this.color = color; this.pad = pad;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(x + 1, y + 1, w - 2, h - 2, radius, radius));
            g2.dispose();
        }

        @Override public Insets getBorderInsets(Component c)            { return new Insets(pad, pad + 4, pad, pad + 4); }
        @Override public Insets getBorderInsets(Component c, Insets in) { in.set(pad, pad + 4, pad, pad + 4); return in; }
    }

    // ══════════════════════════════════════════════════════
    //  ROUNDED TEXT FIELD
    // ══════════════════════════════════════════════════════
    static class RoundedTextField extends JTextField {
        private String placeholder = "";
        private static final int RADIUS = 10;

        RoundedTextField() {
            setForeground(new Color(0x12, 0x12, 0x14));
            setOpaque(false);
            refreshBorder(C_BORDER);
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { refreshBorder(C_PRIMARY); }
                @Override public void focusLost (FocusEvent e)  { refreshBorder(C_BORDER);  }
            });
        }

        private void refreshBorder(Color c) {
            int pad = Math.max(6, getPreferredSize().height / 7);
            setBorder(new RoundedBorder(RADIUS, c, pad));
            repaint();
        }

        void setPlaceholder(String ph) { this.placeholder = ph; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RADIUS, RADIUS));
            g2.dispose();
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) drawPlaceholder(g);
        }

        private void drawPlaceholder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            g2.setColor(C_HINT);
            Insets ins = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(placeholder, ins.left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════
    //  ROUNDED PASSWORD FIELD
    // ══════════════════════════════════════════════════════
    static class RoundedPasswordField extends JPasswordField {
        private String placeholder = "";
        private static final int RADIUS = 10;

        RoundedPasswordField() {
            setForeground(new Color(0x12, 0x12, 0x14));
            setEchoChar('●');
            setOpaque(false);
            refreshBorder(C_BORDER);
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { refreshBorder(C_PRIMARY); }
                @Override public void focusLost (FocusEvent e)  { refreshBorder(C_BORDER);  }
            });
        }

        private void refreshBorder(Color c) {
            int pad = Math.max(6, getPreferredSize().height / 7);
            setBorder(new RoundedBorder(RADIUS, c, pad));
            repaint();
        }

        void setPlaceholder(String ph) { this.placeholder = ph; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RADIUS, RADIUS));
            g2.dispose();
            super.paintComponent(g);
            if (getPassword().length == 0 && !isFocusOwner()) drawPlaceholder(g);
        }

        private void drawPlaceholder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            g2.setColor(C_HINT);
            Insets ins = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(placeholder, ins.left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════
    //  ROUNDED BUTTON WITH HOVER
    // ══════════════════════════════════════════════════════
    static class RoundedButton extends JButton {
        private static final int RADIUS = 10;
        private final Color normalColor, hoverColor;
        private Color currentColor;
        private float hoverProgress = 0f;
        private Timer hoverTimer;

        RoundedButton(String text, Color normal, Color hover) {
            super(text);
            this.normalColor  = normal;
            this.hoverColor   = hover;
            this.currentColor = normal;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { animHover(true);  }
                @Override public void mouseExited (MouseEvent e) { animHover(false); }
            });
        }

        private void animHover(boolean in) {
            if (hoverTimer != null && hoverTimer.isRunning()) hoverTimer.stop();
            hoverTimer = new Timer(12, e -> {
                hoverProgress += in ? 0.1f : -0.1f;
                hoverProgress  = Math.max(0f, Math.min(1f, hoverProgress));
                currentColor   = blend(normalColor, hoverColor, hoverProgress);
                repaint();
                if ((in && hoverProgress >= 1f) || (!in && hoverProgress <= 0f))
                    ((Timer) e.getSource()).stop();
            });
            hoverTimer.start();
        }

        private Color blend(Color a, Color b, float t) {
            return new Color(
                (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t),
                (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 28));
            g2.fill(new RoundRectangle2D.Float(2, 3, getWidth() - 2, getHeight() - 2, RADIUS, RADIUS));
            g2.setColor(currentColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 3, RADIUS, RADIUS));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(LoginForm::new);
    }
}