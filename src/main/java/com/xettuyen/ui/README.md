# 🎨 UI LAYER - JAVA SWING

## 📋 Cấu Trúc Giao Diện

```
LOGIN FORM (LoginForm.java)
└─► Nhập username & password
    ├─ Check UserDAO
    ├─ Nếu đúng: Mở MainFrame
    └─ Nếu sai: Show error

MAIN FRAME (MainFrame.java) - BorderLayout
├─ NORTH: Thanh tiêu đề (tên user + role)
├─ WEST: Sidebar Menu (6 nút + Logout)
│  ├─ 🏠 Trang Chủ (Home)
│  ├─ 👥 Quản Lý Thí Sinh (ThiSinhPanel)
│  ├─ 🎓 Quản Lý Ngành (NganhPanel)
│  ├─ 📊 Quản Lý Điểm (DiemPanel)
│  ├─ 📋 Quản Lý Nguyện Vọng (NguyenVongPanel)
│  ├─ 👤 Quản Lý Hệ Thống (UserPanel) - ADMIN ONLY
│  └─ 🚪 Đăng Xuất
│
├─ CENTER: CardLayout (6 Cards)
│  ├─ HomePanel (Trang Chủ)
│  ├─ ThiSinhPanel (Quản Lý Thí Sinh)
│  ├─ NganhPanel (Quản Lý Ngành)
│  ├─ DiemPanel (Quản Lý Điểm)
│  ├─ NguyenVongPanel (Quản Lý Nguyện Vọng)
│  └─ UserPanel (Quản Lý Hệ Thống)
│
└─ SOUTH: Footer (© 2026)
```

---

## 🎨 Đặc Điểm Giao Diện

✅ **LoginForm**:
- Kích thước: 450x400px
- Màu: Xanh dương (#1967D2)
- TextField username + PasswordField password
- Nút Login (xanh) + Nút Thoát (đỏ)
- Thông báo lỗi khác biệt rõ ràng
- Căn giữa màn hình

✅ **MainFrame**:
- Kích thước: 1024x768px
- BorderLayout với 4 phần
- Sidebar chiều rộng ~200px
- CardLayout để chuyển màn hình
- Hỗ trợ Admin/User role
- Căn giữa màn hình

---

## 📂 Cấu Trúc File

```
src/main/java/com/xettuyen/
├── ui/                           🆕 NEW LAYER
│   ├── LoginForm.java            Giao diện đăng nhập
│   └── MainFrame.java            Giao diện chính
│
├── entity/
│   ├── User.java                 🆕 NEW Entity
│   ├── ThiSinhXettuyen.java
│   ├── DiemThiXettuyen.java
│   ├── Nganh.java
│   ├── NguyenVongXettuyen.java
│   └── ... (7 entities)
│
├── dao/
│   ├── UserDAO.java              🆕 NEW DAO
│   ├── ThiSinhDAO.java
│   ├── DiemThiDAO.java
│   ├── DAOFactory.java           ✏️ UPDATED
│   └── ... (10 DAOs)
│
├── service/
│   ├── ExcelImportService.java
│   ├── XetTuyenService.java
│   └── ...
│
├── util/
│   └── HibernateUtil.java
│
└── App.java                       ✏️ UPDATED (khởi động LoginForm)
```

---

## 🔐 Quy Trình Đăng Nhập

```
START
  ↓
[LoginForm Displayed]
  ↓
User nhập username & password
  ↓
Click "Đăng Nhập"
  ↓
[Validation Check]
  ├─ Nếu trống → Show "Vui lòng nhập tên đăng nhập và mật khẩu!"
  └─ Nếu không trống → Continue
      ↓
  [UserDAO.login(username, password)]
      ├─ SQL: SELECT FROM users WHERE username=? AND password=? AND isActive=true
      │
      ├─ Nếu có kết quả → User object found
      │   ├─ Show "✅ Đăng nhập thành công!"
      │   ├─ Đợi 500ms
      │   ├─ Close LoginForm
      │   ├─ Open MainFrame(username, role)
      │   └─ LOGOUT: Dispose + Open LoginForm
      │
      └─ Nếu không có kết quả → User NOT found
          ├─ Show "❌ Tên đăng nhập hoặc mật khẩu sai!"
          └─ Clear PasswordField
```

---

## 🔄 CardLayout Switch Logic

```
Click "👥 Quản Lý Thí Sinh"
  ↓
Button Click Event Listener
  ↓
cardLayout.show(contentPanel, "THI_SINH")
  ↓
[MainFrame CENTER chuyển sang ThiSinhPanel]
```

**Các Card Names**:
- "HOME" → HomePanel
- "THI_SINH" → ThiSinhPanel
- "NGANH" → NganhPanel
- "DIEM" → DiemPanel
- "NGUYEN_VONG" → NguyenVongPanel
- "USER" → UserPanel (Admin only)

---

## 🛡️ Role-Based Access Control

### Admin Role
- ✅ Xem tất cả Menu (6 items)
- ✅ Truy cập "Quản Lý Hệ Thống" (UserPanel)
- ✅ Quản lý tài khoản user
- ✅ Tất cả chức năng khác

### User Role
- ✅ Xem Menu (5 items)
- ❌ KHÔNG có "Quản Lý Hệ Thống"
- ✅ Nhập/xem dữ liệu
- ✅ Xem báo cáo

**Kiểm tra Role**:
```java
if ("admin".equalsIgnoreCase(userRole)) {
    westPanel.add(userButton);  // Hiển thị UserPanel Button
}
```

---

## 🚀 CHẠY ỨNG DỤNG

### Cách 1: Chạy từ IDE (IntelliJ / Eclipse)
```
1. Right-click App.java
2. Chọn "Run 'App.main()'"
3. LoginForm sẽ hiển thị
```

### Cách 2: Maven Command
```bash
# Compile
mvn clean compile

# Run
mvn exec:java -Dexec.mainClass="com.xettuyen.App"
```

### Cách 3: Chạy JAR
```bash
# Build JAR with dependencies
mvn assembly:assembly

# Run
java -cp "target/xettuyen2026-1.0-jar-with-dependencies.jar" com.xettuyen.App
```

---

## 🧪 Test Accounts

Để test, bạn cần tạo dữ liệu trong table `users`:

```sql
-- Tạo table users
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    role VARCHAR(20),
    is_active BOOLEAN DEFAULT true
);

-- Insert test accounts
INSERT INTO users (username, password, email, full_name, role) VALUES
('admin', 'admin123', 'admin@xettuyen.edu.vn', 'Admin User', 'admin'),
('user1', 'user123', 'user1@xettuyen.edu.vn', 'Nguyễn Văn A', 'user'),
('user2', 'pass456', 'user2@xettuyen.edu.vn', 'Trần Thị B', 'user');
```

**Test Credentials**:
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | admin |
| user1 | user123 | user |
| user2 | pass456 | user |

---

## 🎨 Color Scheme

| Thành Phần | Màu | Hex |
|-----------|-----|-----|
| Primary (Button, Header) | Xanh dương | #1967D2 |
| Logout (Red) | Đỏ | #DC3545 |
| Error (Red) | Đỏ | #DC3545 |
| Success (Green) | Xanh lá | #28A745 |
| Background | Xám nhạt | #F5F5F5 |
| White | Trắng | #FFFFFF |
| Dark Text | Đen | #000000 |
| Gray Text | Xám | #646464 |

---

## 📏 Layout Dimensions

| Component | Chiều Rộng | Chiều Cao |
|-----------|-----------|----------|
| LoginForm | 450px | 400px |
| MainFrame | 1024px | 768px |
| Sidebar | 200px | 100% |
| Menu Button | 200px | 40px |
| Button (Input) | 180px | 30px |

---

## ⚙️ Cấu Hình Hibernate cho User Entity

**File**: `src/main/resources/hibernate.cfg.xml`

```xml
<!-- Thêm User Entity -->
<mapping class="com.xettuyen.entity.User" />
```

**Alternative**: Nếu dùng Auto-Discovery (nếu Hibernate config hỗ trợ):
```xml
<property name="hibernate.archive.autodetection">class, hbm</property>
```

---

## 🔗 Dependency Cần Thêm

**File**: `pom.xml`

```xml
<!-- Java Swing không cần dependency (build-in với Java) -->
<!-- Nhưng cần đảm bảo Java version >= 11 -->
<maven.compiler.source>11</maven.compiler.source>
<maven.compiler.target>11</maven.compiler.target>
```

---

## 🐛 Troubleshooting

### ❌ "Cannot find symbol: class LoginForm"
**Nguyên nhân**: Import không được tìm thấy  
**Giải pháp**: Đảm bảo LoginForm.java nằm trong folder: `src/main/java/com/xettuyen/ui/`

### ❌ "Database connection failed"
**Nguyên nhân**: Không kết nối được MySQL  
**Giải pháp**: Kiểm tra `hibernate.cfg.xml`
```xml
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/xettuyen2026</property>
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">password</property>
```

### ❌ "Login failed: User not found"
**Nguyên nhân**: Không có dữ liệu trong table `users`  
**Giải pháp**: Insert test accounts (xem Test Accounts section)

### ❌ "NullPointerException in CardLayout.show()"
**Nguyên nhân**: Một panel chưa được add vào contentPanel  
**Giải pháp**: Kiểm tra tất cả panels được add trong `initComponents()`

---

## 📝 Tiếp Theo

✅ **Đã Hoàn Thành**:
- LoginForm UI
- MainFrame UI
- CardLayout chuyển màn hình
- Sidebar menu
- Role-based access control

⏳ **Cần Làm**:
- ThiSinhPanel (JTable + CRUD buttons)
- NganhPanel (JTable + CRUD buttons)
- DiemPanel (JTable + CRUD buttons)
- NguyenVongPanel (JTable + CRUD buttons)
- UserPanel (JTable + CRUD buttons)
- Integration với Service Layer (Excel import, score calculation)
- Validation & Error handling
- Unit tests for UI

---

## 📝 Công Thức Tính Điểm - V1

Khi click button "Tính Điểm Xét Tuyển" ở NguyenVongPanel:
```
Gọi XetTuyenService.tinhDiemXetTuyenCuaThiSinh(cccd)
  ├─ 6 Bước (xem SERVICE_SUMMARY.md)
  └─ Update database + Show notification
```

---

**Date**: 31/03/2026  
**Version**: 1.0  
**Status**: 🟡 Giao diện cơ bản hoàn thành - Chờ Panel chi tiết

---

## 🎯 CHECKLIST CẬP NHẬT

- [x] User Entity - add username/password/email/role
- [x] UserDAO - add login() method
- [x] DAOFactory - add getUserDAO()
- [x] LoginForm - 450x400, blue theme, login logic
- [x] MainFrame - 1024x768, BorderLayout, CardLayout
- [x] Sidebar Menu - 6 buttons + Logout
- [x] Role-based access (Admin/User)
- [x] Update App.java - Launch LoginForm
- [x] Test accounts created in SQL
- [ ] ThiSinhPanel - Table + CRUD
- [ ] NganhPanel - Table + CRUD
- [ ] DiemPanel - Table + CRUD
- [ ] NguyenVongPanel - Table + Score calculation
- [ ] UserPanel - User management (Admin only)
- [ ] Service Layer integration
- [ ] Unit tests
