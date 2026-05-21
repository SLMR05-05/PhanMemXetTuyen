package com.xettuyen.ui.panel;

import com.xettuyen.entity.DiemThiXettuyen;
import com.xettuyen.entity.ThiSinhXettuyen;
import com.xettuyen.service.ThiSinhService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;

import static com.xettuyen.ui.MainFrame.*;

public class HomePanel extends JPanel {
    private String currentUser;
    private final ThiSinhService thiSinhService = new ThiSinhService();

    private JLabel totalThiSinhValueLabel;
    private JLabel soNhomDoiTuongValueLabel;
    private JLabel soNhomKhuVucValueLabel;

    private DefaultTableModel doiTuongModel;
    private DefaultTableModel khuVucModel;

    private JTextField searchField;
    private JComboBox<CandidateOption> candidateCombo;
    private JLabel hoTenValue;
    private JLabel cccdValue;
    private JLabel sbdValue;
    private JLabel ngaySinhValue;
    private JLabel doiTuongValue;
    private JLabel khuVucValue;
    private JLabel trangThaiValue;

    private DefaultTableModel thptModel;
    private DefaultTableModel dgnlModel;
    private DefaultTableModel vsatModel;

    public HomePanel(String current_User) {
        currentUser = current_User;
        initComponents();
        loadDashboardData();
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
        JPanel statsRow = new JPanel(new GridLayout(1, 3, clamp(vw(1), 10, 18), 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, clamp(vh(14), 90, 130)));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalThiSinhValueLabel = makeStatCard("👥", "Tổng thí sinh", "0", C_PRIMARY);
        soNhomDoiTuongValueLabel = makeStatCard("📌", "Nhóm đối tượng", "0", C_SUCCESS);
        soNhomKhuVucValueLabel = makeStatCard("🗺️", "Nhóm khu vực", "0", C_WARNING);

        statsRow.add(wrapCard(totalThiSinhValueLabel));
        statsRow.add(wrapCard(soNhomDoiTuongValueLabel));
        statsRow.add(wrapCard(soNhomKhuVucValueLabel));

        // 3. Main Content Card
        JPanel contentCard = makeCard();
        contentCard.setLayout(new BorderLayout(clamp(vw(1), 10, 16), clamp(vh(1), 8, 14)));
        contentCard.setBorder(new EmptyBorder(cardPad, cardPad, cardPad, cardPad));
        contentCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel leftStatsPanel = buildStatisticTablesPanel();
        JPanel rightDetailPanel = buildCandidateDetailPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftStatsPanel, rightDetailPanel);
        splitPane.setResizeWeight(0.38);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        splitPane.setContinuousLayout(true);

        contentCard.add(splitPane, BorderLayout.CENTER);

        // Lắp ráp
        centerWrapper.add(welcomeCard);
        centerWrapper.add(Box.createVerticalStrut(clamp(vh(2), 15, 25)));
        centerWrapper.add(statsRow);
        centerWrapper.add(Box.createVerticalStrut(clamp(vh(2), 15, 25)));
        centerWrapper.add(contentCard);
        centerWrapper.add(Box.createVerticalGlue());

        this.add(centerWrapper, BorderLayout.CENTER);
    }

    private JPanel wrapCard(JLabel valueLabel) {
        return (JPanel) valueLabel.getClientProperty("statCard");
    }

    private JLabel makeStatCard(String icon, String title, String value, Color accentColor) {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(12, 0));
        int p = clamp(vw(1.2f), 12, 20);
        card.setBorder(new EmptyBorder(p, p, p, p));

        JComponent accent = new JComponent() {
            {
                setPreferredSize(new Dimension(4, 0));
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
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
        card.add(text, BorderLayout.CENTER);

        valueLbl.putClientProperty("statCard", card);
        return valueLbl;
    }

    private JPanel buildStatisticTablesPanel() {
        JPanel container = new JPanel(new BorderLayout(0, clamp(vh(1.2f), 8, 14)));
        container.setOpaque(false);

        JLabel title = new JLabel("Bảng thống kê");
        title.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(2f), 15, 20)));
        title.setForeground(C_TITLE);
        container.add(title, BorderLayout.NORTH);

        JPanel tables = new JPanel(new GridLayout(2, 1, 0, clamp(vh(1.2f), 8, 14)));
        tables.setOpaque(false);

        doiTuongModel = new DefaultTableModel(new Object[] { "Đối tượng", "Số lượng" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable doiTuongTable = createSimpleTable(doiTuongModel);
        tables.add(buildTableCard("Theo đối tượng", doiTuongTable));

        khuVucModel = new DefaultTableModel(new Object[] { "Khu vực", "Số lượng" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable khuVucTable = createSimpleTable(khuVucModel);
        tables.add(buildTableCard("Theo khu vực", khuVucTable));

        container.add(tables, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildCandidateDetailPanel() {
        JPanel container = new JPanel(new BorderLayout(0, clamp(vh(1.2f), 8, 14)));
        container.setOpaque(false);

        JLabel title = new JLabel("Tra cứu chi tiết 01 thí sinh");
        title.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(2f), 15, 20)));
        title.setForeground(C_TITLE);
        container.add(title, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, clamp(vh(1.2f), 8, 14)));
        body.setOpaque(false);

        JPanel searchPanel = new JPanel(new BorderLayout(clamp(vw(0.8f), 8, 12), 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Nhập CCCD, SBD hoặc Họ tên");
        JButton searchBtn = new JButton("Tìm");
        searchBtn.setBackground(C_PRIMARY);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);

        candidateCombo = new JComboBox<>();
        candidateCombo.addActionListener(e -> loadSelectedCandidateDetail());

        JPanel searchTop = new JPanel(new BorderLayout(clamp(vw(0.8f), 8, 12), 0));
        searchTop.setOpaque(false);
        searchTop.add(searchField, BorderLayout.CENTER);
        searchTop.add(searchBtn, BorderLayout.EAST);

        searchPanel.add(searchTop, BorderLayout.NORTH);
        searchPanel.add(candidateCombo, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> searchCandidates());
        searchField.addActionListener(e -> searchCandidates());

        body.add(searchPanel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(4, 2, clamp(vw(0.6f), 6, 10), clamp(vh(0.8f), 6, 10)));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(2, 0, 4, 0));

        hoTenValue = addInfoCell(infoPanel, "Họ tên:");
        cccdValue = addInfoCell(infoPanel, "CCCD:");
        sbdValue = addInfoCell(infoPanel, "SBD:");
        ngaySinhValue = addInfoCell(infoPanel, "Ngày sinh:");
        doiTuongValue = addInfoCell(infoPanel, "Đối tượng:");
        khuVucValue = addInfoCell(infoPanel, "Khu vực:");
        trangThaiValue = addInfoCell(infoPanel, "Trúng tuyển:");
        addInfoCell(infoPanel, "");

        body.add(infoPanel, BorderLayout.CENTER);

        JTabbedPane scoreTabs = new JTabbedPane();
        thptModel = createScoreModel();
        dgnlModel = createScoreModel();
        vsatModel = createScoreModel();

        scoreTabs.addTab("Điểm THPT", new JScrollPane(createSimpleTable(thptModel)));
        scoreTabs.addTab("Điểm ĐGNL", new JScrollPane(createSimpleTable(dgnlModel)));
        scoreTabs.addTab("Điểm VSAT", new JScrollPane(createSimpleTable(vsatModel)));
        body.add(scoreTabs, BorderLayout.SOUTH);

        container.add(body, BorderLayout.CENTER);
        return container;
    }

    private JLabel addInfoCell(JPanel panel, String title) {
        JPanel cell = new JPanel(new BorderLayout(4, 0));
        cell.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(1.5f), 11, 14)));
        titleLabel.setForeground(C_HINT);

        JLabel valueLabel = new JLabel("-");
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, clamp(vh(1.5f), 11, 14)));
        valueLabel.setForeground(C_TEXT);

        cell.add(titleLabel, BorderLayout.WEST);
        cell.add(valueLabel, BorderLayout.CENTER);
        panel.add(cell);
        return valueLabel;
    }

    private JPanel buildTableCard(String title, JTable table) {
        JPanel panel = makeCard();
        panel.setLayout(new BorderLayout(0, 8));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(1.7f), 12, 16)));
        titleLabel.setForeground(C_TITLE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(C_BORDER));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JTable createSimpleTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(clamp(vh(3.5f), 24, 34));
        table.setFont(new Font("Segoe UI", Font.PLAIN, clamp(vh(1.5f), 11, 14)));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, clamp(vh(1.4f), 10, 13)));
        table.getTableHeader().setBackground(C_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(C_BORDER);
        table.setSelectionBackground(new Color(220, 232, 255));
        table.setSelectionForeground(C_TEXT);
        return table;
    }

    private DefaultTableModel createScoreModel() {
        return new DefaultTableModel(new Object[] {
                "SBD", "Toán", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Văn", "N1_THI", "N1_CC", "NL1", "NK1", "NK2"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void loadDashboardData() {
        try {
            ThiSinhService.HomeDashboardData data = thiSinhService.getHomeDashboardData();
            totalThiSinhValueLabel.setText(String.valueOf(data.totalThiSinh));
            soNhomDoiTuongValueLabel.setText(String.valueOf(data.byDoiTuong.size()));
            soNhomKhuVucValueLabel.setText(String.valueOf(data.byKhuVuc.size()));

            fillGroupTable(doiTuongModel, data.byDoiTuong);
            fillGroupTable(khuVucModel, data.byKhuVuc);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tải dữ liệu thống kê HomePanel: " + ex.getMessage(),
                    "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillGroupTable(DefaultTableModel model, Map<String, Long> data) {
        model.setRowCount(0);
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            model.addRow(new Object[] { entry.getKey(), entry.getValue() });
        }
    }

    private void searchCandidates() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập CCCD, SBD hoặc Họ tên để tìm kiếm.");
            return;
        }

        try {
            List<ThiSinhXettuyen> candidates = thiSinhService.searchThiSinhForHome(keyword, 50);
            candidateCombo.removeAllItems();

            for (ThiSinhXettuyen candidate : candidates) {
                candidateCombo.addItem(new CandidateOption(candidate));
            }

            if (candidates.isEmpty()) {
                clearCandidateDetail();
                JOptionPane.showMessageDialog(this, "Không tìm thấy thí sinh phù hợp.");
            } else {
                candidateCombo.setSelectedIndex(0);
                loadSelectedCandidateDetail();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tìm kiếm thí sinh: " + ex.getMessage(),
                    "Lỗi tra cứu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedCandidateDetail() {
        CandidateOption selected = (CandidateOption) candidateCombo.getSelectedItem();
        if (selected == null) {
            return;
        }

        try {
            ThiSinhService.CandidateDetail detail = thiSinhService.getCandidateDetail(selected.getKeyword());
            if (detail == null || detail.thiSinh == null) {
                clearCandidateDetail();
                return;
            }

            ThiSinhXettuyen thiSinh = detail.thiSinh;
            hoTenValue.setText(safeText(thiSinh.getHo()) + " " + safeText(thiSinh.getTen()));
            cccdValue.setText(safeText(thiSinh.getCccd()));
            sbdValue.setText(safeText(thiSinh.getSoBaoDanh()));
            ngaySinhValue.setText(safeText(thiSinh.getNgaySinh()));
            doiTuongValue.setText(safeText(thiSinh.getDoiTuong()));
            khuVucValue.setText(safeText(thiSinh.getKhuVuc()));
            trangThaiValue.setText(safeText(thiSinh.getTrangThaiTrungTuyen()));

            fillScoreTable(thptModel, detail.thptScores);
            fillScoreTable(dgnlModel, detail.dgnlScores);
            fillScoreTable(vsatModel, detail.vsatScores);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải chi tiết thí sinh: " + ex.getMessage(),
                    "Lỗi chi tiết",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillScoreTable(DefaultTableModel model, List<DiemThiXettuyen> scores) {
        model.setRowCount(0);
        for (DiemThiXettuyen score : scores) {
            model.addRow(new Object[] {
                    safeText(score.getSoBaoDanh()),
                    fmt(score.getTo()),
                    fmt(score.getLi()),
                    fmt(score.getHo()),
                    fmt(score.getSi()),
                    fmt(score.getSu()),
                    fmt(score.getDi()),
                    fmt(score.getVa()),
                    fmt(score.getN1Thi()),
                    fmt(score.getN1Cc()),
                    fmt(score.getNl1()),
                    fmt(score.getNk1()),
                    fmt(score.getNk2())
            });
        }
    }

    private String safeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value;
    }

    private String fmt(Double value) {
        if (value == null) {
            return "";
        }
        return String.format("%.2f", value);
    }

    private void clearCandidateDetail() {
        hoTenValue.setText("-");
        cccdValue.setText("-");
        sbdValue.setText("-");
        ngaySinhValue.setText("-");
        doiTuongValue.setText("-");
        khuVucValue.setText("-");
        trangThaiValue.setText("-");
        thptModel.setRowCount(0);
        dgnlModel.setRowCount(0);
        vsatModel.setRowCount(0);
    }

    private static class CandidateOption {
        private final ThiSinhXettuyen candidate;

        CandidateOption(ThiSinhXettuyen candidate) {
            this.candidate = candidate;
        }

        String getKeyword() {
            if (candidate.getCccd() != null && !candidate.getCccd().trim().isEmpty()) {
                return candidate.getCccd();
            }
            return candidate.getSoBaoDanh();
        }

        @Override
        public String toString() {
            return safe(candidate.getHo()) + " " + safe(candidate.getTen()) +
                    " | CCCD: " + safe(candidate.getCccd()) +
                    " | SBD: " + safe(candidate.getSoBaoDanh());
        }

        private String safe(String value) {
            return value == null || value.trim().isEmpty() ? "-" : value;
        }
    }
}
