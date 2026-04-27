# 🎨 UI LAYER - TÓM TẮT TOÀN BỘ

## 📊 TỔNG QUAN

```
┌──────────────────────────────────────┐
│      HỆ THỐNG XÉT TUYỂN 2026         │
│     4-TIER ARCHITECTURE              │
└──────────────────────────────────────┘
         ↓ ↑
┌────────────────────────────────────────┐
│  TẦNG 4: UI LAYER (👈 VỪA VIẾT)      │
│  - LoginForm (450x400)                 │
│  - MainFrame (1024x768)                │
│  - CardLayout để chuyển panel          │
│  - Sidebar Menu (6 buttons)            │
└────────────────────────────────────────┘
         ↓ ↑
┌────────────────────────────────────────┐
│  TẦNG 3: SERVICE LAYER (Đã viết)     │
│  - ExcelImportService                  │
│  - XetTuyenService                     │
│  - ThiSinhService                      │
│  - NganhService                        │
└────────────────────────────────────────┘
         ↓ ↑
┌────────────────────────────────────────┐
│  TẦNG 2: DAO LAYER (Đã viết)          │
│  - BaseDAO<T> + 8 DAOs                 │
│  - DAOFactory                          │
│  - UserDAO (NEW)                       │
└────────────────────────────────────────┘
         ↓ ↑
┌────────────────────────────────────────┐
│  TẦNG 1: ENTITY + DATABASE            │
│  - 8 Entities + User (NEW)             │
│  - MySQL xettuyen2026                  │
│  - 8 tables + users (NEW)              │
└────────────────────────────────────────┘
```

---

## 📁 FILE ĐƯỢC TẠO/CẬP NHẬT

### NEW - Tệp Mới Tạo

| File | Vị Trí | Mục Đích |
|------|--------|---------|
| **User.java** | entity/ | Entity cho user login |
| **UserDAO.java** | dao/ | DAO cho login check |
| **LoginForm.java** | ui/ | Giao diện đăng nhập |
| **MainFrame.java** | ui/ | Giao diện chính |
| **setup_users.sql** | root | SQL tạo table users + test data |

### UPDATED - Tệp Được Cập Nhật

| File | Thay Đổi |
|------|----------|
| **DAOFactory.java** | + getUserDAO() method |
| **App.java** | Khởi động LoginForm |

### DOCUMENTATION - Tài Liệu

| File | Nội Dung |
|------|----------|
| **SETUP_UI_LAYER.md** | Hướng dẫn cấu hình + run |
| **src/main/java/com/xettuyen/ui/README.md** | Chi tiết giao diện |
| **UI_SUMMARY.md** | File này - tóm tắt |

---

## 🎨 GIAO DIỆN CHI TIẾT

### 1️⃣ LoginForm (450x400px)

```
┌──────────────────────────────┐
│  HỆ THỐNG XÉT TUYỂN 2026     │  <- xanh dương, BOLD 24pt
│                              │
│  Tên đăng nhập:             │
│  ┌──────────────────────┐    │
│  │[username_field]      │    │
│  └──────────────────────┘    │
│                              │
│  Mật khẩu:                   │
│  ┌──────────────────────┐    │
│  │[password_field]      │    │
│  └──────────────────────┘    │
│                              │
│  [❌ Error message]         │  <- nếu có lỗi
│                              │
│       [Đăng Nhập] [Thoát]    │  <- xanh / đỏ
│                              │
└──────────────────────────────┘
```

**Sự kiện**:
- Click "Đăng Nhập" → UserDAO.login(username, password)
  - ✅ Thành công → Close LoginForm, open MainFrame
  - ❌ Thất bại → Show error, clear password
- Click "Thoát" → System.exit(0)

---

### 2️⃣ MainFrame (1024x768px)

#### Layout: BorderLayout

```
┌─────────────────────────────────────────────────────────┐
│  HỆ THỐNG XÉT TUYỂN 2026  |  Đăng nhập: admin (admin) │  NORTH (60px)
├──────────────┬────────────────────────────────────────┤
│              │                                        │
│  SIDEBAR     │         CENTER - CardLayout           │
│  Menu        │                                        │
│  (200px)     │         HomePanel / ThiSinhPanel /    │
│              │         NganhPanel / etc.             │
│              │                                        │
│  • Trang Chủ │                                        │
│  • Thí Sinh  │                                        │
│  • Ngành     │     (Card switching via menu click)   │
│  • Điểm      │                                        │
│  • Nguyện V. │                                        │
│  • Hệ Thống* │                                        │
│              │                                        │
│              │                                        │
│  • Đăng Xuất │                                        │
│              │                                        │
├──────────────┴────────────────────────────────────────┤
│ © 2026 Hệ Thống Xét Tuyển - Version 1.0             │  SOUTH (30px)
└─────────────────────────────────────────────────────────┘
* Chỉ admin thấy
```

#### Mô Tả Chi Tiết

**NORTH Panel (60px)**:
- Label: "HỆ THỐNG XÉT TUYỂN 2026" (xanh dương, BOLD 18pt)
- Label: "Đăng nhập: admin (admin)" (trắng, 12pt)
- Background: Xanh dương (#1967D2)

**WEST Panel - Sidebar (200px)**:
- BoxLayout Y_AXIS
- 6 Menu Buttons (admin thấy 7 + logout)
- Button style: 200x40px, xanh dương, trắng text
- Logout Button: đỏ (#DC3545)

**CENTER Panel - CardLayout**:
- 6 Cards: HOME, THI_SINH, NGANH, DIEM, NGUYEN_VONG, USER
- Hiển thị panel tương ứng theo menu click
- Default card: HOME (Trang Chủ)

**SOUTH Panel (30px)**:
- Footer: "© 2026 Hệ Thống Xét Tuyển - Version 1.0"
- Xám nhạt background

---

## 🔐 AUTHENTICATION FLOW

```
START
  ↓
[LoginForm displayed]
  ↓
User input: username + password
  ├─ Empty validation
  │   └─ Show: "❌ Vui lòng nhập..."
  │
  └─ Non-empty
      ↓
  [UserDAO.login(username, password)]
      ├─ SQL: SELECT FROM users 
      │        WHERE username=? 
      │        AND password=? 
      │        AND isActive=true
      │
      ├─ Found User object
      │   ├─ Show: "✅ Đăng nhập thành công!"
      │   ├─ Wait 500ms (Timer)
      │   ├─ LoginForm.dispose()
      │   ├─ new MainFrame(username, role)
      │   ├─ MainFrame.setVisible(true)
      │   └─ SHOW APPLICATION
      │
      └─ User NOT found (null)
          ├─ Show: "❌ Tên đăng nhập hoặc mật khẩu sai!"
          ├─ Clear PasswordField
          └─ STAY AT LoginForm
```

---

## 🔄 CARDLAYOUT SWITCH

```
User clicks "👥 Quản Lý Thí Sinh"
  ↓
actionPerformed(ActionEvent)
  ↓
if "LOGOUT":
  ├─ Show confirm dialog
  └─ If YES:
      ├─ MainFrame.dispose()
      └─ new LoginForm()
else:
  ├─ cardLayout.show(contentPanel, "THI_SINH")
  └─ CENTER panel chuyển sang ThiSinhPanel
```

**Các Card Names**:
| Button | Card Name | Panel |
|--------|-----------|-------|
| 🏠 Trang Chủ | "HOME" | HomePanel |
| 👥 Thí Sinh | "THI_SINH" | ThiSinhPanel |
| 🎓 Ngành | "NGANH" | NganhPanel |
| 📊 Điểm | "DIEM" | DiemPanel |
| 📋 Nguyện Vọng | "NGUYEN_VONG" | NguyenVongPanel |
| 👤 Hệ Thống | "USER" | UserPanel |

---

## 👥 ROLE-BASED ACCESS CONTROL

### Admin Role ("admin")
✅ Xem Menu: 6 items + Logout  
✅ Truy cập: UserPanel (Quản Lý Hệ Thống)  
✅ Quyền: Toàn bộ chức năng

### User Role ("user")
✅ Xem Menu: 5 items + Logout  
❌ Không có: UserPanel  
✅ Quyền: Nhập/xem data, không quản lý user

**Implementation**:
```java
if ("admin".equalsIgnoreCase(userRole)) {
    // Thêm UserPanel Button
    westPanel.add(userButton);
}
```

---

## 📂 FILE STRUCTURE

```
d:\Khanh\XayDungPhanMemTheoMoHinhPhanLop\xettuyen2026\
│
├── src/
│   └── main/
│       ├── java/com/xettuyen/
│       │   ├── ui/                       🆕 NEW
│       │   │   ├── LoginForm.java        (450x400, login logic)
│       │   │   ├── MainFrame.java        (1024x768, main app)
│       │   │   └── README.md             (UI documentation)
│       │   │
│       │   ├── entity/
│       │   │   ├── User.java             🆕 NEW (username/password/role)
│       │   │   ├── ThiSinhXettuyen.java
│       │   │   ├── ... 7 khác
│       │   │
│       │   ├── dao/
│       │   │   ├── UserDAO.java          🆕 NEW (login method)
│       │   │   ├── DAOFactory.java       ✏️ UPDATED (+ getUserDAO)
│       │   │   ├── ... 9 khác
│       │   │
│       │   ├── service/
│       │   │   ├── ExcelImportService.java
│       │   │   ├── XetTuyenService.java
│       │   │   ├── ...
│       │   │
│       │   └── App.java                  ✏️ UPDATED (launch LoginForm)
│       │
│       └── resources/
│           └── hibernate.cfg.xml         ✏️ UPDATED (add User mapping)
│
├── SETUP_UI_LAYER.md                     🆕 NEW (cấu hình + run guide)
├── UI_SUMMARY.md                         🆕 NEW (file này)
├── setup_users.sql                       🆕 NEW (schema + test data)
├── pom.xml                               ✏️ UPDATED (dependencies)
│
└── ... (other project files)
```

---

## 🎨 COLOR PALETTE

| Component | Color | Hex | Usage |
|-----------|-------|-----|-------|
| Primary - Header/Button | Xanh Dương | #1967D2 | Buttons, headers, primary action |
| Secondary - Logout | Đỏ | #DC3545 | Logout button, error messages |
| Success | Xanh Lá | #28A745 | Success confirmation |
| Background - Light | Xám Nhạt | #F5F5F5 | Panel backgrounds |
| Text - Primary | Đen | #000000 | Main text |
| Text - Secondary | Xám | #646464 | Secondary text |
| Border - Light | Xám | #C8C8C8 | Input field borders |
| White | Trắng | #FFFFFF | Card backgrounds, text color |

---

## 📏 LAYOUT DIMENSIONS

| Component | Width | Height | Notes |
|-----------|-------|--------|-------|
| LoginForm | 450px | 400px | Fixed (not resizable) |
| MainFrame | 1024px | 768px | Resizable |
| Sidebar (West) | 200px | 100% | Fixed width |
| Menu Button | 200px | 40px | Standard menu button |
| Input Field | 180px | 30px | TextField/PasswordField |
| Input Label | 100px | 25px | Label text |
| Title Label | Auto | 40px | Page title |

---

## 🔗 DEPENDENCIES

### Swing (Built-in)
- `javax.swing.*` (Standard Java Swing)

### Hibernate
- `org.hibernate.*` (ORM)
- `com.mysql:mysql-connector-java` (MySQL driver)

### Logging (Optional)
- `org.slf4j:slf4j-api`
- `ch.qos.logback:logback-classic`

**Verify**: 
```bash
mvn dependency:tree | grep -E "swing|javax|jakarta"
```

---

## ✅ TEST DATA

### Users Table

```sql
-- Admin
Username: admin
Password: admin123
Email: admin@xettuyen.edu.vn
Role: admin

-- User 1
Username: user1
Password: user123
Email: user1@xettuyen.edu.vn
Role: user

-- User 2
Username: user2
Password: pass456
Email: user2@xettuyen.edu.vn
Role: user

-- User 3
Username: user3
Password: secure789
Email: user3@xettuyen.edu.vn
Role: user
```

**Insert Command**:
```sql
SOURCE setup_users.sql;
```

---

## 🚀 QUICK START

### 5-Minute Setup

```bash
# 1. Setup Database
mysql -u root -p xettuyen2026 < setup_users.sql

# 2. Configure Database (sửa password trong hibernate.cfg.xml)
# Change: YOUR_PASSWORD_HERE → your_mysql_password

# 3. Compile
mvn clean compile

# 4. Run
mvn exec:java -Dexec.mainClass="com.xettuyen.App"

# 5. Test
# Login as: admin / admin123
```

---

## 🎯 CHECKLIST

- [x] User Entity (username, password, email, role)
- [x] UserDAO (login, findByUsername, createUser, etc.)
- [x] DAOFactory (+ getUserDAO)
- [x] LoginForm (450x400, validation, error handling)
- [x] MainFrame (1024x768, BorderLayout, CardLayout)
- [x] Sidebar Menu (6 buttons, role-based)
- [x] CardLayout (6 panel cards)
- [x] Database setup (users table + test data)
- [x] Hibernate config (update with User mapping)
- [x] App.java (launch LoginForm)
- [x] Documentation (SETUP_UI_LAYER.md, README.md)
- [ ] ThiSinhPanel (JTable + CRUD)
- [ ] NganhPanel (JTable + CRUD)
- [ ] DiemPanel (JTable + CRUD)
- [ ] NguyenVongPanel (JTable + calculator button)
- [ ] UserPanel (JTable + user management)

---

## ⏭️ NEXT STEPS

### Phase 2 - Panel Implementation

1. **ThiSinhPanel**
   - JTable showing ThiSinhXettuyen data
   - Buttons: +Thêm, +Sửa, +Xóa, +Tìm kiếm
   - Import Excel button

2. **NganhPanel**
   - JTable showing Nganh data
   - CRUD buttons
   - Show related NganhTohop

3. **DiemPanel**
   - JTable for DiemThi & DiemCong
   - Import Excel button
   - View/Edit scores

4. **NguyenVongPanel**
   - JTable for NguyenVong
   - "Tính Điểm" button → XetTuyenService
   - Show diem_xettuyen results

5. **UserPanel** (Admin only)
   - JTable for users
   - +Thêm, +Sửa, +Xóa user
   - Reset password function

---

## 📊 STATISTICS

| Metric | Value |
|--------|-------|
| **Classes Created** | 4 (User, UserDAO, LoginForm, MainFrame) |
| **Files Updated** | 3 (DAOFactory, App, pom.xml) |
| **Database Tables** | + 1 (users) |
| **Total LOC (Java)** | ~1100 lines |
| **Documentation Pages** | 3 |
| **Test Accounts** | 4 |

---

## 🎨 VISUAL HIERARCHY

```
LOGIN SCREEN
└─ Title (24pt BOLD)
   └─ Labels (14pt)
      └─ Input Fields (14pt)
         └─ Buttons (14pt BOLD)
            └─ Error Message (12pt RED)

MAIN SCREEN
└─ Header Title (18pt BOLD BLUE)
   └─ User Info (12pt WHITE)
      └─ Sidebar Buttons (13pt WHITE)
         └─ Content Area
            ├─ Panel Title (18pt BOLD)
            └─ Panel Content (12pt)
               └─ Footer (10pt GRAY)
```

---

## 🔧 DEBUGGING TIPS

### Enable Console Logging
```java
// In LoginForm.java
System.out.println("✅ Đăng nhập thành công: " + username);
System.err.println("❌ Đăng nhập thất bại: Username hoặc password sai");
```

### Database Verification
```sql
-- Check users table
SELECT * FROM users;

-- Check login
SELECT * FROM users WHERE username='admin' AND password='admin123';

-- Monitor active logins
SELECT COUNT(*) as active_users FROM users WHERE is_active=true;
```

### UI Testing
```java
// Manually test LoginForm
// 1. Start app
// 2. Enter admin/admin123
// 3. Verify MainFrame opens
// 4. Click menu items
// 5. Verify CardLayout switches
// 6. Click Logout
// 7. Verify LoginForm reopens
```

---

## 📞 SUPPORT

**Common Issues**:
1. Database connection failed → Check hibernate.cfg.xml
2. Login page blank → Check LoginForm.java not compiled
3. Menu not working → Check CardLayout names match
4. Admin button invisible → Check userRole comparison

**See**: `SETUP_UI_LAYER.md` - Troubleshooting section

---

## 📅 PROJECT STATUS

**Phase 1: Entity + DAO** ✅ COMPLETE
- 8 Entities + User
- 10 DAOs with Generic Pattern

**Phase 2: Service Layer** ✅ COMPLETE
- ExcelImportService
- XetTuyenService
- Business logic

**Phase 3: UI Layer (Basic)** ✅ COMPLETE
- LoginForm
- MainFrame
- CardLayout

**Phase 4: UI Panels** ⏳ TODO
- ThiSinhPanel (with JTable)
- NganhPanel (with JTable)
- DiemPanel (with JTable)
- NguyenVongPanel (with calculator)
- UserPanel (Admin only)

**Phase 5: Integration & Testing** ⏳ TODO
- Service ↔ UI integration
- Unit tests
- Integration tests

---

**Version**: 1.0-UI  
**Date**: 31/03/2026  
**Status**: 🟢 Ready for Testing

---

## 🎉 NEXT ACTION

**Try now**:
```bash
# 1. Setup database
mysql -u root -p xettuyen2026 < setup_users.sql

# 2. Update hibernate.cfg.xml (password)

# 3. Run
mvn exec:java -Dexec.mainClass="com.xettuyen.App"

# 4. Test login
# admin / admin123
```
