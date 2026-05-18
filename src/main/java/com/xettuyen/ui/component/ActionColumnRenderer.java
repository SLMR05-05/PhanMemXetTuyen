package com.xettuyen.ui.component;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ActionColumnRenderer extends JPanel implements TableCellRenderer {
    private String type; // Để phân biệt cột "Tổ hợp" hay "Thao tác"

    public ActionColumnRenderer(String type) {
        this.type = type;
        this.setOpaque(false); // Làm nền trong suốt để thấy màu dòng của JTable
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        this.removeAll(); // Xóa sạch để vẽ lại cho dòng mới

        if ("menu".equals(type)) {
            // Icon 3 gạch cho cột Tổ hợp
            JLabel lblMenu = createLabel("\u2630", new Color(0, 123, 255), 22);
            this.add(lblMenu);
        } else if ("edit_delete".equals(type)) {
            // Icon Bút chì và Thùng rác cho cột Thao tác
            JLabel lblEdit = createLabel("\u270F", new Color(40, 167, 69), 20); // Bút chì
            JLabel lblDelete = createLabel("\uD83D\uDDD1", new Color(220, 53, 69), 22); // Thùng rác
            
            this.add(lblEdit);
            this.add(lblDelete);
        }

        // Đổi màu nền khi dòng được chọn
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
            this.setOpaque(true);
        } else {
            this.setOpaque(false);
        }

        return this;
    }

    private JLabel createLabel(String icon, Color color, int size) {
        JLabel l = new JLabel(icon);
        l.setFont(new Font("Segoe UI Symbol", Font.BOLD, size));
        l.setForeground(color);
        return l;
    }
}