package com.xettuyen.ui.panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import static com.xettuyen.ui.MainFrame.*;
public class HomePanel extends JPanel{
    private String currentUser;

    public HomePanel(String current_User){
        currentUser = current_User;
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setBackground(C_CONTENT_BG);
        int pad = clamp(vw(2), 20, 36);
        this.setBorder(new EmptyBorder(pad, pad, pad, pad));

        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setBackground(C_CONTENT_BG);

        int cardPad = clamp(vw(1.5f), 16, 26);

        // 1. Welcome Card
        JPanel welcomeCard = makeCard();
        welcomeCard.setLayout(new BorderLayout(20, 0)); 
        welcomeCard.setBorder(new EmptyBorder(cardPad, cardPad, cardPad, cardPad));
        welcomeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, clamp(vh(15), 100, 140)));
        welcomeCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel wIcon = new JLabel("👋");
        wIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, clamp(vh(4), 36, 52)));

        JPanel wText = new JPanel();
        wText.setLayout(new BoxLayout(wText, BoxLayout.Y_AXIS));
        wText.setOpaque(false);

        JLabel wTitle = new JLabel("Chào mừng trở lại, " + currentUser + "!");
        wTitle.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(2.5f), 18, 24)));
        wTitle.setForeground(C_TITLE);
        
        JLabel wSub = new JLabel("Hôm nay bạn muốn làm gì? Chọn một chức năng từ menu bên trái.");
        wSub.setFont(new Font("Segoe UI", Font.PLAIN, clamp(vh(1.7f), 12, 16)));
        wSub.setForeground(C_HINT);

        wText.add(wTitle);
        wText.add(Box.createVerticalStrut(8));
        wText.add(wSub);

        welcomeCard.add(wIcon, BorderLayout.WEST);
        welcomeCard.add(wText, BorderLayout.CENTER);

        // 2. Stats Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, clamp(vw(1), 10, 18), 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, clamp(vh(14), 90, 130)));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        statsRow.add(makeStatCard("📁", "Tổng Thí Sinh",   "—", C_PRIMARY));
        statsRow.add(makeStatCard("🎓", "Ngành Học",        "—", C_SUCCESS));
        statsRow.add(makeStatCard("📋", "Nguyện Vọng",      "—", C_WARNING));
        statsRow.add(makeStatCard("✅", "Trúng Tuyển",      "—", new Color(0x6C, 0x3F, 0xEB)));

        // 3. Info Card
        JPanel infoCard = makeCard();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(new EmptyBorder(cardPad, cardPad, cardPad, cardPad));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoTitle = new JLabel("Về Hệ Thống");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(2f), 16, 20)));
        infoTitle.setForeground(C_TITLE);
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.add(infoTitle);
        infoCard.add(Box.createVerticalStrut(clamp(vh(1.2f), 10, 16)));

        String[] lines = {
            "• Quản lý toàn bộ thông tin thí sinh đăng ký xét tuyển.",
            "• Cấu hình ngành học, tổ hợp môn và chỉ tiêu tuyển sinh.",
            "• Nhập, kiểm tra và xử lý điểm thi THPT Quốc Gia.",
            "• Quản lý và xét duyệt nguyện vọng của từng thí sinh.",
            "• Xuất báo cáo kết quả xét tuyển theo nhiều định dạng."
        };
        for (String line : lines) {
            JLabel l = new JLabel(line);
            l.setFont(new Font("Segoe UI", Font.PLAIN, clamp(vh(1.6f), 12, 15)));
            l.setForeground(C_TEXT);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            l.setBorder(new EmptyBorder(3, 0, 3, 0));
            infoCard.add(l);
        }

        // Lắp ráp
        centerWrapper.add(welcomeCard);
        centerWrapper.add(Box.createVerticalStrut(clamp(vh(2), 15, 25)));
        centerWrapper.add(statsRow);
        centerWrapper.add(Box.createVerticalStrut(clamp(vh(2), 15, 25)));
        centerWrapper.add(infoCard);
        centerWrapper.add(Box.createVerticalGlue());

        this.add(centerWrapper, BorderLayout.CENTER); 
    }

    private JPanel makeStatCard(String icon, String title, String value, Color accentColor) {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(12, 0));
        int p = clamp(vw(1.2f), 12, 20);
        card.setBorder(new EmptyBorder(p, p, p, p));

        JComponent accent = new JComponent() {
            { setPreferredSize(new Dimension(4, 0)); setOpaque(false); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fill(new RoundRectangle2D.Float(0, 4, 4, getHeight() - 8, 4, 4));
                g2.dispose();
            }
        };

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);
        text.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, clamp(vh(2.8f), 22, 32)));
        iconLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(2.8f), 22, 32)));
        valueLbl.setForeground(C_TITLE);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, clamp(vh(1.5f), 11, 14)));
        titleLbl.setForeground(C_HINT);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        text.add(iconLbl);
        text.add(Box.createVerticalStrut(5));
        text.add(valueLbl);
        text.add(Box.createVerticalStrut(2));
        text.add(titleLbl);

        card.add(accent, BorderLayout.WEST);
        card.add(text,   BorderLayout.CENTER);
        return card;
    }
}
