package com.xettuyen.ui.component;

import com.xettuyen.ui.panel.NganhPanel;
import javax.swing.*;
import java.awt.*;

public class ActionColumnEditor extends DefaultCellEditor {
    private JPanel panel;
    private String type;
    private NganhPanel parent;

    public ActionColumnEditor(JCheckBox checkBox, String type, NganhPanel parent) {
        super(checkBox);
        this.type = type;
        this.parent = parent;
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        panel.removeAll();
        panel.setBackground(table.getSelectionBackground());

        if ("menu".equals(type)) {
            JButton btnMenu = createButton("\u2630", new Color(0, 123, 255));
            btnMenu.addActionListener(e -> {
                fireEditingStopped();
                parent.openEditDialog(); // Gọi hàm mở dialog xem tổ hợp
            });
            panel.add(btnMenu);
        } else {
            JButton btnEdit = createButton("\u270F", new Color(40, 167, 69));
            JButton btnDelete = createButton("\uD83D\uDDD1", new Color(220, 53, 69));

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                parent.openEditDialog(); // Bấm bút chì mở dialog sửa
            });
            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                parent.executeDeleteLogic(); // Bấm thùng rác thực hiện xóa
            });

            panel.add(btnEdit);
            panel.add(btnDelete);
        }
        return panel;
    }

    private JButton createButton(String icon, Color color) {
        JButton b = new JButton(icon);
        b.setFont(new Font("Segoe UI Symbol", Font.BOLD, 22));
        b.setForeground(color);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    @Override
    public Object getCellEditorValue() { return ""; }
}