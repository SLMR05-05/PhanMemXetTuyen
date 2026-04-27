# 🚀 QUICK START - UI LAYER

## ⚡ 5 Bước Cơ Bản

### Bước 1: Setup Database (1 phút)

```bash
# Mở terminal/command prompt

# Chạy MySQL
mysql -u root -p

# Chọn database
USE xettuyen2026;

# Load SQL file
SOURCE setup_users.sql;

# Verify
SELECT * FROM users;
```

**Output Expected**:
```
+----+----------+----------+-----------------------------+-----------------+-------+-----------+
| id | username | password | email                       | full_name       | role  | is_active |
+----+----------+----------+-----------------------------+-----------------+-------+-----------+
|  1 | admin    | admin123 | admin@xettuyen.edu.vn       | Administrator   | admin |         1 |
|  2 | user1    | user123  | user1@xettuyen.edu.vn       | Nguyễn Văn A    | user  |         1 |
+----+----------+----------+-----------------------------+-----------------+-------+-----------+
```

---

### Bước 2: Cấu Hình Hibernate (30 giây)

**File**: `src/main/resources/hibernate.cfg.xml`

**Tìm dòng**:
```xml
<property name="hibernate.connection.password">YOUR_PASSWORD_HERE</property>
```

**Sửa thành**:
```xml
<property name="hibernate.connection.password">root</property>
<!-- hoặc password của bạn -->
```

---

### Bước 3: Compile (1 phút)

```bash
cd d:\Khanh\XayDungPhanMemTheoMoHinhPhanLop\xettuyen2026

mvn clean install
```

**Expected Output**:
```
[INFO] BUILD SUCCESS
[INFO] Total time: 30 seconds
```

---

### Bước 4: Run Application (30 giây)

**Option A - IntelliJ IDEA**:
```
1. Right-click: src/main/java/com/xettuyen/App.java
2. Select: Run 'App.main()'
```

**Option B - Eclipse**:
```
1. Right-click: src/main/java/com/xettuyen/App.java
2. Select: Run As → Java Application
```

**Option C - Command Line**:
```bash
mvn exec:java -Dexec.mainClass="com.xettuyen.App"
```

---

### Bước 5: Test Login (1 phút)

**LoginForm sẽ hiển thị**:
```
┌─────────────────────────────┐
│ HỆ THỐNG XÉT TUYỂN 2026     │
│                             │
│ Tên đăng nhập: [admin    ] │
│ Mật khẩu:      [*****   ] │
│                             │
│    [Đăng Nhập]  [Thoát]    │
└─────────────────────────────┘
```

**Test Credentials** (thử cách này):
```
Username: admin
Password: admin123

→ Nếu OK: MainFrame hiển thị
→ Nếu lỗi: Check troubleshooting bên dưới
```

---

## 🎯 Kết Quả Mong Đợi

### Login Thành Công

```
✅ Đăng nhập thành công!

→ MainFrame mở ra (1024x768)
→ Sidebar menu có 7 nút (nếu admin):
   - 🏠 Trang Chủ
   - 👥 Quản Lý Thí Sinh
   - 🎓 Quản Lý Ngành
   - 📊 Quản Lý Điểm
   - 📋 Quản Lý Nguyện Vọng
   - 👤 Quản Lý Hệ Thống
   - 🚪 Đăng Xuất
```

### Thử Menu

```
1. Click "👥 Quản Lý Thí Sinh"
   → CENTER panel chuyển sang ThiSinhPanel
   → Hiển thị: "[Chính trong thiết kế]..."

2. Click "🚪 Đăng Xuất"
   → Dialog: "Bạn có chắc chắn muốn đăng xuất?"
   → If YES:
      - MainFrame đóng
      - LoginForm hiển thị lại
```

---

## ❌ Troubleshooting

### Problem 1: "java.sql.SQLException: No suitable driver found"

**Solution**:
```bash
# Check dependencies
mvn dependency:tree | grep mysql

# Compile again
mvn clean compile

# Run again
mvn exec:java -Dexec.mainClass="com.xettuyen.App"
```

---

### Problem 2: "Communications link failure"

**Solution**:
```bash
# Start MySQL (Windows)
net start MySQL80

# Or MySQL (Linux)
sudo systemctl start mysql

# Test connection
mysql -u root -p --port=3306
```

---

### Problem 3: "Login page doesn't work"

**Solution**:
```bash
# Check users table
mysql -u root -p xettuyen2026
SELECT * FROM users;

# If empty, insert test data
SOURCE setup_users.sql;
```

---

### Problem 4: "Cannot find symbol: class LoginForm"

**Solution**:
```bash
# Refresh IDE
# IntelliJ: Ctrl+Alt+F5
# Eclipse: F5

# Compile again
mvn clean compile
```

---

## 📁 File Mới Tạo

```
xettuyen2026/
├── src/main/java/com/xettuyen/
│   ├── ui/
│   │   ├── LoginForm.java     ← Giao diện đăng nhập
│   │   └── MainFrame.java     ← Giao diện chính
│   ├── entity/
│   │   └── User.java          ← New entity
│   └── dao/
│       └── UserDAO.java       ← New DAO
├── setup_users.sql            ← SQL setup
├── SETUP_UI_LAYER.md          ← Chi tiết guide
├── UI_SUMMARY.md              ← Tóm tắt
└── QUICK_START.md             ← File này
```

---

## 🎨 Giao Diện

### LoginForm (450x400px)

```
Màu: Xanh dương (#1967D2)
Input: Username & Password
Buttons: Đăng Nhập (xanh), Thoát (đỏ)
Message: Error message (nếu có)
```

### MainFrame (1024x768px)

```
Layout: BorderLayout
- NORTH: Thanh tiêu đề
- WEST: Sidebar menu (200px)
- CENTER: CardLayout (chứa 6 panel)
- SOUTH: Footer
```

---

## 🔐 Test Accounts

| Username | Password | Role | Email |
|----------|----------|------|-------|
| admin | admin123 | admin | admin@xettuyen.edu.vn |
| user1 | user123 | user | user1@xettuyen.edu.vn |
| user2 | pass456 | user | user2@xettuyen.edu.vn |
| user3 | secure789 | user | user3@xettuyen.edu.vn |

---

## ✅ Verification

```
□ MySQL chạy
□ Database xettuyen2026 tồn tại
□ Table users được tạo
□ 4 test accounts insert được
□ hibernate.cfg.xml updated (password)
□ pom.xml: mvn clean install SUCCESS
□ App.java: LoginForm import thêm vào
□ Run: mvn exec:java -Dexec.mainClass="com.xettuyen.App"
□ LoginForm hiển thị
□ Test login: admin/admin123 → MainFrame OK
```

---

## 📞 Support

**See**: 
- [SETUP_UI_LAYER.md](SETUP_UI_LAYER.md) - Hướng dẫn chi tiết
- [UI_SUMMARY.md](UI_SUMMARY.md) - Tóm tắt toàn bộ
- [src/main/java/com/xettuyen/ui/README.md](src/main/java/com/xettuyen/ui/README.md) - UI docs

---

## 🎯 Next Steps

✅ **Đã Làm**:
- LoginForm (login logic)
- MainFrame (sidebar + CardLayout)
- User Entity & UserDAO
- Database setup

⏳ **Cần Làm**:
- ThiSinhPanel (JTable + CRUD)
- NganhPanel (JTable + CRUD)
- DiemPanel (JTable + CRUD)
- NguyenVongPanel (score calculator)
- UserPanel (user management)

---

**Status**: 🟢 Ready to Test  
**Date**: 31/03/2026  
**Version**: 1.0
