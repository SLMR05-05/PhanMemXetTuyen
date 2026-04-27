package com.xettuyen.ui.dialog;

import com.xettuyen.entity.NganhTohop;
import com.xettuyen.dao.BaseDAO;
import com.xettuyen.util.HibernateUtil;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class tohop extends JDialog {
    private NganhTohop th;
    private List<JTextField> editFields = new ArrayList<>();
    private JButton btnEdit;
    private boolean isEditing = false;
    private boolean isSaved = false;
    private final BaseDAO<NganhTohop> dao = new BaseDAO<NganhTohop>(HibernateUtil.getSessionFactory()) {};

    public tohop(Frame owner, NganhTohop th) {
        super(owner, "Chi tiết Tổ hợp: " + th.getMaTohop(), true);
        this.th = th;
        initComponents();
        this.setSize(500, 550);
        this.setLocationRelativeTo(owner);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Nhóm 1: Định danh
        JPanel pIdent = new JPanel(new GridLayout(0, 2, 10, 15));
        pIdent.setBorder(new TitledBorder("Thông tin định danh"));
        addInfoField(pIdent, "Mã Ngành:", th.getMaNganh(), false);
        addInfoField(pIdent, "Mã Tổ Hợp:", th.getMaTohop(), true);
        addInfoField(pIdent, "Khóa tra cứu (tbKeys):", th.getTbKeys(), true);
        addInfoField(pIdent, "Độ lệch điểm:", String.valueOf(th.getDoLech()), true);
        mainPanel.add(pIdent);

        mainPanel.add(Box.createVerticalStrut(20));

        // Nhóm 2: Hệ số
        JPanel pDetail = new JPanel(new GridLayout(0, 2, 10, 15));
        pDetail.setBorder(new TitledBorder("Cấu trúc môn học & Hệ số"));
        addInfoField(pDetail, "Môn chính 1:", th.getThMon1(), true);
        addInfoField(pDetail, "Hệ số môn 1:", String.valueOf(th.getHsMon1()), true);
        addInfoField(pDetail, "Môn chính 2:", th.getThMon2(), true);
        addInfoField(pDetail, "Hệ số môn 2:", String.valueOf(th.getHsMon2()), true);
        addInfoField(pDetail, "Môn chính 3:", th.getThMon3(), true);
        addInfoField(pDetail, "Hệ số môn 3:", String.valueOf(th.getHsMon3()), true);
        mainPanel.add(pDetail);

        this.add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Nút bấm
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnEdit = new JButton("✏️ Chỉnh sửa");
        JButton btnClose = new JButton("❌ Đóng");

        btnEdit.addActionListener(e -> handleEditAction());
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnEdit);
        btnPanel.add(btnClose);
        this.add(btnPanel, BorderLayout.SOUTH);
    }

    private void addInfoField(JPanel p, String label, String value, boolean canEdit) {
        p.add(new JLabel(label));
        JTextField txt = new JTextField(value != null ? value : "");
        txt.setEditable(false);
        txt.setBackground(new Color(245, 245, 245));
        txt.setFont(new Font("Segoe UI", Font.BOLD, 13));
        if (canEdit) editFields.add(txt);
        p.add(txt);
    }

private void handleEditAction() {
        if (!isEditing) {
            // Chuyển sang chế độ Sửa
            isEditing = true;
            btnEdit.setText("💾 Lưu dữ liệu");
            btnEdit.setBackground(new Color(40, 167, 69));
            btnEdit.setForeground(Color.WHITE);

            for (JTextField txt : editFields) {
                txt.setEditable(true);
                txt.setBackground(Color.WHITE); // Đổi màu trắng để biết là được gõ
            }
        } else {
            // Thực hiện Lưu dữ liệu
            try {
                // 1. Gán lại dữ liệu từ các ô nhập liệu vào object 'th'
                // Đảm bảo thứ tự chỉ số (0, 1, 2...) khớp chính xác với lúc bạn add vào editFields
                th.setMaTohop(editFields.get(0).getText().trim());
                th.setTbKeys(editFields.get(1).getText().trim());
                th.setDoLech(Double.parseDouble(editFields.get(2).getText().trim()));
                
                th.setThMon1(editFields.get(3).getText().trim());
                th.setHsMon1(Integer.parseInt(editFields.get(4).getText().trim()));
                
                th.setThMon2(editFields.get(5).getText().trim());
                th.setHsMon2(Integer.parseInt(editFields.get(6).getText().trim()));
                
                th.setThMon3(editFields.get(7).getText().trim());
                th.setHsMon3(Integer.parseInt(editFields.get(8).getText().trim()));

                // 2. KIỂM TRA LƯU MỚI HAY CẬP NHẬT
                if (th.getId() == null) {
                    // Nếu chưa có ID -> Đây là tổ hợp mới được tạo từ nút ➕
                    dao.save(th); 
                    JOptionPane.showMessageDialog(this, "Thêm mới tổ hợp thành công!");
                } else {
                    // Nếu đã có ID -> Đây là tổ hợp cũ đang được sửa
                    dao.update(th);
                    JOptionPane.showMessageDialog(this, "Cập nhật thay đổi thành công!");
                }

                isSaved = true;
                dispose(); // Đóng cửa sổ để bảng cha (NganhEditDialog) tự load lại dữ liệu
                
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Lỗi: Vui lòng nhập đúng định dạng số cho Hệ số và Độ lệch!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage());
            }
        }
    }
    public boolean isSaved() { return isSaved; }
}