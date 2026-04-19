package com.xettuyen.ui.panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.xettuyen.ui.MainFrame;

public class DiemPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private DiemThiSubPanel diemThiSubPanel;
    private DiemCongSubPanel diemCongSubPanel;
    public static JTextArea logArea; 

    public DiemPanel() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.C_CONTENT_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        diemThiSubPanel = new DiemThiSubPanel();
        diemCongSubPanel = new DiemCongSubPanel();
        
        // Tab Lịch sử phong cách Console
        JPanel historyPanel = new JPanel(new BorderLayout());
        logArea = new JTextArea(" --- HỆ THỐNG GHI NHẬT KÝ --- \n");
        logArea.setEditable(false);
        logArea.setBackground(new Color(30, 30, 30));
        logArea.setForeground(new Color(0, 255, 0));
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setMargin(new Insets(10, 10, 10, 10));
        historyPanel.add(new JScrollPane(logArea));

        tabbedPane.addTab(" Nhập Điểm Thi ", diemThiSubPanel);
        tabbedPane.addTab(" Điểm Cộng / Ưu Tiên ", diemCongSubPanel);
        tabbedPane.addTab(" Lịch Sử Chỉnh Sửa ", historyPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
    
    public static void addLog(String msg) {
        String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        if (logArea != null) logArea.append("[" + time + "] " + msg + "\n");
    }
}