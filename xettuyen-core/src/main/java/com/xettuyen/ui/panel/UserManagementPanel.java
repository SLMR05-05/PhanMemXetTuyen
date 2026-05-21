/**
 * Quản lý người dùng - User Management Panel
 * - Hiển thị danh sách người dùng với phân trang và tìm kiếm
 */

package com.xettuyen.ui.panel;

import com.xettuyen.entity.User;
import com.xettuyen.service.UserService;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class UserManagementPanel extends JPanel {

	private static final int PAGE_SIZE = 20;

	private final UserService userService = new UserService();

	/** Các thành phần trang
	 * - searchField: ô tìm kiếm người dùng theo username/email/fullname/role
	 * - pageField: ô nhập số trang hiện tại
	 * - totalPageField: hiển thị tổng số trang
	 * - prevBtn: nút chuyển trang trước
	 * - nextBtn: nút chuyển trang sau
	 */
	private JTextField searchField;
	private JTextField pageField;
	private JTextField totalPageField;
	private JButton prevBtn;
	private JButton nextBtn;
	private int totalPages = 1;

	/**
	 * model: mô hình dữ liệu cho bảng người dùng
	 * table: bảng hiển thị danh sách người dùng
	 */
	private DefaultTableModel model;
	private JTable table;

	/** Các hằng số màu */
	public static final Color C_PRIMARY = new Color(0x00, 0x62, 0xFF);
	public static final Color C_PRIMARY_HV = new Color(0x00, 0x4E, 0xCC);
	public static final Color C_CONTENT_BG = new Color(0xF0, 0xF4, 0xFF);
	public static final Color C_CARD = Color.WHITE;
	public static final Color C_BORDER = new Color(0xE0, 0xE8, 0xFF);
	public static final Color C_TITLE = new Color(0x12, 0x12, 0x14);
	public static final Color C_TEXT = new Color(0x37, 0x37, 0x3A);
	public static final Color C_DANGER = new Color(0xDC, 0x35, 0x45);
	public static final Color C_DANGER_HV = new Color(0xB0, 0x2A, 0x37);
	public static final Color C_WARNING = new Color(0xFF, 0x8C, 0x00);

	/** Các chức năng hỗ trợ căn chỉnh kích thước các thành phần */
	private static final int SW;
	private static final int SH;

	static {
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		SW = scr.width;
		SH = scr.height;
	}

	public static int vw(float p) {
		return Math.round(SW * p / 100f);
	}

	public static int vh(float p) {
		return Math.round(SH * p / 100f);
	}

	public UserManagementPanel() {
		setLayout(new BorderLayout());

		/* North (Search) ================================================================================ */
		JPanel northPanel = new JPanel(new GridBagLayout());
		northPanel.setBackground(C_CONTENT_BG);
		northPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		searchField = new JTextField();
		searchField.setPreferredSize(new Dimension(vw(30), vh(5)));
		searchField.putClientProperty("JTextField.placeholderText", "Nhập username/email/fullname/role");
		searchField.putClientProperty("JTextField.padding", new Insets(5, 5, 5, 5));
		searchField.putClientProperty("JTextField.showClearButton", true);
		searchField.putClientProperty("FlatLaf.style",
				"arc: 10; " +
				"font: 14 $font; " +
				"foreground: " + toHex(C_TEXT) + "; ");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		northPanel.add(searchField, gbc);

		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				loadUsers(1);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				loadUsers(1);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				loadUsers(1);
			}
		});

		/* Center (Table) ============================================================================== */
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(C_CONTENT_BG);
		centerPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

		String[] headers = {"ID", "Username", "Email", "Fullname", "Role", "Active", "Chức năng"};
		model = new DefaultTableModel(headers, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 6;
			}
		};

		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		int equalWidth = 120;
		for (int i = 0; i < headers.length; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(equalWidth);
		}

		table.getColumnModel().getColumn(6).setCellRenderer(new DetailButtonRenderer());
		table.getColumnModel().getColumn(6).setCellEditor(new DetailButtonEditor(table));

		table.getTableHeader().setPreferredSize(new Dimension(0, vh(5)));
		table.getTableHeader().putClientProperty("FlatLaf.style",
				"font: 14 $medium.font; " +
						"background: " + toHex(C_PRIMARY) + "; " +
						"foreground: " + toHex(C_CARD) + "; ");

		table.putClientProperty("FlatLaf.style",
				"font: 14 $font; " +
						"rowHeight: " + vh(4) + "; " +
						"showHorizontalLines: true; " +
						"selectionBackground: " + toHex(C_BORDER) + "; " +
						"selectionForeground: #000000;");

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.putClientProperty("FlatLaf.style", "arc: 15;borderWidth: 0;focusWidth: 0;");
		centerPanel.add(scrollPane, BorderLayout.CENTER);

		/* South (Pagination) ============================================================================== */
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
		totalPageField.setHighlighter(null);

		JTextField per = new JTextField("/");
		per.setEditable(false);
		per.setBorder(null);
		per.setBackground(C_CONTENT_BG);
		per.setPreferredSize(new Dimension(vw(2), vh(4)));
		per.setHorizontalAlignment(JTextField.CENTER);
		per.setFont(paginationFont);
		per.setFocusable(false);
		per.setHighlighter(null);

		prevBtn = new JButton("<");
		prevBtn.setPreferredSize(new Dimension(vw(2), vh(4)));
		prevBtn.setFont(paginationFont);
		prevBtn.putClientProperty("FlatLaf.style",
				"font: 14 $medium.font; " +
						"background: " + toHex(C_PRIMARY) + "; " +
						"foreground: #FFFFFF; " +
						"disabledBackground: #D0D7E2; " +
						"disabledForeground: #7B8794;");

		nextBtn = new JButton(">");
		nextBtn.setPreferredSize(new Dimension(vw(2), vh(4)));
		nextBtn.setFont(paginationFont);
		nextBtn.putClientProperty("FlatLaf.style",
				"font: 14 $medium.font; " +
						"background: " + toHex(C_PRIMARY) + "; " +
						"foreground: #FFFFFF; " +
						"disabledBackground: #D0D7E2; " +
						"disabledForeground: #7B8794;");

		pageField = new JTextField("1");
		pageField.setPreferredSize(new Dimension(vw(4), vh(4)));
		pageField.setHorizontalAlignment(JTextField.CENTER);
		pageField.setFont(paginationFont);

		prevBtn.addActionListener(e -> loadUsers(getCurrentPage() - 1));
		nextBtn.addActionListener(e -> loadUsers(getCurrentPage() + 1));
		pageField.addActionListener(e -> loadUsers(getCurrentPage()));

		southPanel.add(prevBtn);
		southPanel.add(pageField);
		southPanel.add(per);
		southPanel.add(totalPageField);
		southPanel.add(nextBtn);

		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);

		loadUsers(1);
	}

	private void loadUsers(int requestedPage) {
		int safePage = Math.max(1, requestedPage);
		String keyword = searchField.getText() == null ? "" : searchField.getText().trim();

		UserService.SearchResult<User> result = userService.searchUsers(keyword, safePage, PAGE_SIZE);
		totalPages = Math.max(1, result.totalPages);

		int currentPage = Math.max(1, Math.min(result.currentPage, totalPages));
		pageField.setText(String.valueOf(currentPage));
		totalPageField.setText(String.valueOf(totalPages));
		prevBtn.setEnabled(currentPage > 1);
		nextBtn.setEnabled(currentPage < totalPages);

		model.setRowCount(0);
		for (User user : result.data) {
			model.addRow(new Object[]{
					user.getId(),
					nullToEmpty(user.getUsername()),
					nullToEmpty(user.getEmail()),
					nullToEmpty(user.getFullName()),
					nullToEmpty(user.getRole()),
					user.getIsActive() != null && user.getIsActive() ? "Đang hoạt động" : "Tạm khóa",
					"Chi tiết"
			});
		}
	}

	private int getCurrentPage() {
		try {
			int parsed = Integer.parseInt(pageField.getText().trim());
			return Math.max(1, Math.min(parsed, totalPages));
		} catch (NumberFormatException ex) {
			return 1;
		}
	}

	private static String toHex(Color color) {
		if (color == null) {
			return "#FFFFFF";
		}
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	private class DetailButtonRenderer extends JPanel implements TableCellRenderer {
		private final JButton button = new JButton("Chi tiết");

		public DetailButtonRenderer() {
			setLayout(new GridBagLayout());
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));

			button.putClientProperty("FlatLaf.style",
					"arc: 10; " +
							"background: " + toHex(C_WARNING) + "; " +
							"foreground: #FFFFFF; " +
							"margin: 2,12,2,12; " +
							"font: 12 $font;");
			add(button);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
													   boolean isSelected, boolean hasFocus, int row, int column) {
			setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
			return this;
		}
	}

	private class DetailButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
		private final JPanel panel = new JPanel(new GridBagLayout());
		private final JButton button = new JButton("Chi tiết");
		private final JTable table;
		private int row;

		public DetailButtonEditor(JTable table) {
			this.table = table;
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
		public Object getCellEditorValue() {
			return "Chi tiết";
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int modelRow = table.convertRowIndexToModel(row);
			Integer userId = (Integer) table.getModel().getValueAt(modelRow, 0);
			User user = userService.getUserById(userId);

			if (user == null) {
				JOptionPane.showMessageDialog(UserManagementPanel.this,
						"Không tìm thấy user để hiển thị chi tiết.",
						"Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
				fireEditingStopped();
				return;
			}

			UserDetailDialog dialog = new UserDetailDialog(
					SwingUtilities.getWindowAncestor(UserManagementPanel.this),
					user);
			dialog.setVisible(true);

			if (dialog.isSaved()) {
				loadUsers(getCurrentPage());
			}
			fireEditingStopped();
		}
	}

	private class UserDetailDialog extends JDialog {
		private final User user;
		private final Integer userId;
		private final JTextField idField = new JTextField();
		private final JTextField usernameField = new JTextField();
		private final JTextField passwordField = new JTextField();
		private final JTextField emailField = new JTextField();
		private final JTextField fullNameField = new JTextField();
		private final JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"admin", "user"});
		private final JCheckBox activeCheckBox = new JCheckBox("Đang hoạt động");
		private final JTextField createdAtField = new JTextField();
		private final JTextField updatedAtField = new JTextField();
		private boolean saved;

		private UserDetailDialog(Window owner, User user) {
			super(owner, "Chi tiết người dùng", ModalityType.APPLICATION_MODAL);
			this.user = user;
			this.userId = user.getId();
			buildUi();
			fillData();
			setSize(Math.min(vw(60), 900), Math.min(vh(70), 780));
			setLocationRelativeTo(UserManagementPanel.this);
		}

		private void buildUi() {
			JPanel root = new JPanel(new BorderLayout(12, 12));
			root.setBorder(new EmptyBorder(16, 16, 16, 16));
			root.setBackground(C_CONTENT_BG);

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

			addFormRow(formPanel, gbc, 0, "ID", idField);
			addFormRow(formPanel, gbc, 1, "Username", usernameField);
			addFormRow(formPanel, gbc, 2, "Password", passwordField);
			addFormRow(formPanel, gbc, 3, "Email", emailField);
			addFormRow(formPanel, gbc, 4, "Fullname", fullNameField);
			addComboRow(formPanel, gbc, 5, "Role", roleComboBox);
			addFormRow(formPanel, gbc, 6, "Created at", createdAtField);
			addFormRow(formPanel, gbc, 7, "Updated at", updatedAtField);

			JLabel activeLabel = new JLabel("Active");
			activeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			activeLabel.setForeground(C_TEXT);

			gbc.gridx = 0;
			gbc.gridy = 8;
			gbc.weightx = 0;
			formPanel.add(activeLabel, gbc);

			activeCheckBox.setBackground(C_CARD);
			activeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			activeCheckBox.setForeground(C_TEXT);
			gbc.gridx = 1;
			gbc.gridy = 8;
			gbc.weightx = 1;
			formPanel.add(activeCheckBox, gbc);

			JScrollPane formScroll = new JScrollPane(formPanel);
			formScroll.setBorder(null);
			formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			root.add(formScroll, BorderLayout.CENTER);

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
							"foreground: #FFFFFF;");
			saveButton.putClientProperty("FlatLaf.style",
					"arc: 10; " +
							"font: 14 $medium.font; " +
							"background: " + toHex(C_PRIMARY) + "; " +
							"hoverBackground: " + toHex(C_PRIMARY_HV) + "; " +
							"foreground: #FFFFFF;");

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
							"foreground: " + toHex(C_TEXT) + ";");

			gbc.gridx = 0;
			gbc.gridy = row;
			gbc.weightx = 0;
			panel.add(label, gbc);

			gbc.gridx = 1;
			gbc.gridy = row;
			gbc.weightx = 1;
			panel.add(field, gbc);
		}

		private void addComboRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComboBox<String> comboBox) {
			JLabel label = new JLabel(labelText);
			label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			label.setForeground(C_TEXT);

			comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			comboBox.putClientProperty("FlatLaf.style",
					"arc: 8; " +
							"font: 14 $font; " +
							"foreground: " + toHex(C_TEXT) + ";");

			gbc.gridx = 0;
			gbc.gridy = row;
			gbc.weightx = 0;
			panel.add(label, gbc);

			gbc.gridx = 1;
			gbc.gridy = row;
			gbc.weightx = 1;
			panel.add(comboBox, gbc);
		}

		private void fillData() {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			idField.setText(userId == null ? "" : String.valueOf(userId));
			idField.setEditable(false);
			idField.setFocusable(false);
			idField.setBackground(new Color(0xF5, 0xF7, 0xFA));

			usernameField.setText(nullToEmpty(user.getUsername()));
			passwordField.setText(nullToEmpty(user.getPassword()));
			emailField.setText(nullToEmpty(user.getEmail()));
			fullNameField.setText(nullToEmpty(user.getFullName()));

			String roleValue = nullToEmpty(user.getRole()).toLowerCase();
			if (!"admin".equals(roleValue) && !"user".equals(roleValue)) {
				roleValue = "user";
			}
			roleComboBox.setSelectedItem(roleValue);

			activeCheckBox.setSelected(user.getIsActive() != null && user.getIsActive());

			createdAtField.setText(user.getCreatedAt() == null ? "Chưa có" : formatter.format(user.getCreatedAt()));
			createdAtField.setEditable(false);
			createdAtField.setBackground(new Color(0xF5, 0xF7, 0xFA));

			updatedAtField.setText(user.getUpdatedAt() == null ? "Chưa có" : formatter.format(user.getUpdatedAt()));
			updatedAtField.setEditable(false);
			updatedAtField.setBackground(new Color(0xF5, 0xF7, 0xFA));
		}

		private void onSave() {
			User updated = new User();
			updated.setUsername(usernameField.getText().trim());
			updated.setPassword(passwordField.getText().trim());
			updated.setEmail(emailField.getText().trim());
			updated.setFullName(fullNameField.getText().trim());
			updated.setRole(String.valueOf(roleComboBox.getSelectedItem()));
			updated.setIsActive(activeCheckBox.isSelected());

			try {
				userService.updateUserInfo(userId, updated);
				saved = true;
				JOptionPane.showMessageDialog(this,
						"Đã cập nhật thông tin user.",
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

		private boolean isSaved() {
			return saved;
		}
	}

	private static String nullToEmpty(String value) {
		return value == null ? "" : value;
	}
}
