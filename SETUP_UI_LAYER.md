# 🎨 HỮỡNG DẪN SETUP UI LAYER

## 📋 MỤC LỤC
1. [Cấu hình Database](#cấu-hình-database)
2. [Cấu hình Hibernate](#cấu-hình-hibernate)
3. [Compile & Run](#compile--run)
4. [Test Login](#test-login)
5. [Troubleshooting](#troubleshooting)

---

## 🗄️ Cấu Hình Database

### Bước 1: Tạo Table Users

**Option A: Dùng MySQL Workbench**
```
1. Mở MySQL Workbench
2. Connect đến Database Server
3. Double-click database 'xettuyen2026'
4. File → Open SQL Script → Chọn setup_users.sql
5. Ctrl+Shift+Enter để execute
```

**Option B: Dùng Command Line**
```bash
# Connect to MySQL
mysql -u root -p

# Select database
USE xettuyen2026;

# Execute SQL file
SOURCE setup_users.sql;

# Verify
SELECT * FROM users;
```

**Output Expected**:
```
+----+----------+----------+-----------------------------+-----------------+-------+-----------+---------------------+---------------------+
| id | username | password | email                       | full_name       | role  | is_active | created_at          | updated_at          |
+----+----------+----------+-----------------------------+-----------------+-------+-----------+---------------------+---------------------+
|  1 | admin    | admin123 | admin@xettuyen.edu.vn       | Administrator   | admin |         1 | 2026-03-31 10:00:00 | 2026-03-31 10:00:00 |
|  2 | user1    | user123  | user1@xettuyen.edu.vn       | Nguyễn Văn A    | user  |         1 | 2026-03-31 10:00:00 | 2026-03-31 10:00:00 |
|  3 | user2    | pass456  | user2@xettuyen.edu.vn       | Trần Thị B      | user  |         1 | 2026-03-31 10:00:00 | 2026-03-31 10:00:00 |
|  4 | user3    | secure789 | user3@xettuyen.edu.vn      | Lê Văn C        | user  |         1 | 2026-03-31 10:00:00 | 2026-03-31 10:00:00 |
+----+----------+----------+-----------------------------+-----------------+-------+-----------+---------------------+---------------------+
```

---

## ⚙️ Cấu Hình Hibernate

### Bước 2: Cập Nhật hibernate.cfg.xml

**File**: `src/main/resources/hibernate.cfg.xml`

Thêm User Entity vào danh sách mapping:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <!-- Database Connection -->
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/xettuyen2026</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password">YOUR_PASSWORD_HERE</property>

        <!-- Hibernate Properties -->
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
        <property name="hibernate.hbm2ddl.auto">update</property>
        <property name="hibernate.show_sql">false</property>
        <property name="hibernate.format_sql">true</property>
        <property name="hibernate.use_sql_comments">true</property>

        <!-- Connection Pool -->
        <property name="hibernate.connection.provider_class">
            org.hibernate.hikaricp.internal.HikariCPConnectionProvider
        </property>
        <property name="hibernate.hikari.maximumPoolSize">20</property>
        <property name="hibernate.hikari.minimumIdle">5</property>

        <!-- Entity Mappings -->
        <mapping class="com.xettuyen.entity.User"/>
        <mapping class="com.xettuyen.entity.ThiSinhXettuyen"/>
        <mapping class="com.xettuyen.entity.DiemThiXettuyen"/>
        <mapping class="com.xettuyen.entity.DiemCongXettuyen"/>
        <mapping class="com.xettuyen.entity.Nganh"/>
        <mapping class="com.xettuyen.entity.NganhTohop"/>
        <mapping class="com.xettuyen.entity.TohopMonthi"/>
        <mapping class="com.xettuyen.entity.NguyenVongXettuyen"/>
        <mapping class="com.xettuyen.entity.BangQuydoi"/>
    </session-factory>
</hibernate-configuration>
```

### Bước 3: Sửa Password

```xml
<!-- CHANGE THIS LINE -->
<property name="hibernate.connection.password">YOUR_PASSWORD_HERE</property>

<!-- TO YOUR ACTUAL PASSWORD -->
<property name="hibernate.connection.password">root</property>
<!-- or -->
<property name="hibernate.connection.password">mysql123</property>
<!-- or leave blank nếu MySQL không có password -->
<property name="hibernate.connection.password"></property>
```

---

## 🔧 Compile & Run

### Bước 4: Maven Clean Install

```bash
# Mở terminal/command prompt và navigate đến project root
cd d:\Khanh\XayDungPhanMemTheoMoHinhPhanLop\xettuyen2026

# Clean previous build
mvn clean

# Install dependencies & compile
mvn install

# OUTPUT EXPECTED:
# BUILD SUCCESS
# Total time: XX seconds
```

### Bước 5: Run Application

**Option A: Chạy từ IDE (IntelliJ IDEA)**
```
1. File → Open → Chọn xettuyen2026 folder
2. Wait for IDE to index
3. Right-click src/main/java/com/xettuyen/App.java
4. Chọn "Run 'App.main()'"
5. LoginForm sẽ hiển thị
```

**Option B: Chạy từ IDE (Eclipse)**
```
1. File → Import → Existing Maven Projects
2. Select xettuyen2026 folder
3. Right-click App.java
4. Run As → Java Application
5. LoginForm sẽ hiển thị
```

**Option C: Chạy từ Command Line**
```bash
cd d:\Khanh\XayDungPhanMemTheoMoHinhPhanLop\xettuyen2026

# Compile
mvn clean compile

# Run
mvn exec:java -Dexec.mainClass="com.xettuyen.App"
```

---

## 🔐 Test Login

### Bước 6: Try Login

**Giao diện sẽ hiển thị**:
```
=========================================
   HỆ THỐNG XÉT TUYỂN 2026
=========================================

Tên đăng nhập: [           ]
Mật khẩu:     [           ]

             [Đăng Nhập] [Thoát]
```

#### Test Case 1: Admin Login
```
Username: admin
Password: admin123
Expected: ✅ Đăng nhập thành công!
          → MainFrame mở ra
          → Sidebar có nút "Quản Lý Hệ Thống"
```

#### Test Case 2: User Login
```
Username: user1
Password: user123
Expected: ✅ Đăng nhập thành công!
          → MainFrame mở ra
          → Sidebar KHÔNG có nút "Quản Lý Hệ Thống"
```

#### Test Case 3: Wrong Password
```
Username: admin
Password: wrongpass
Expected: ❌ Tên đăng nhập hoặc mật khẩu sai!
          → PasswordField được clear
          → Vẫn ở LoginForm
```

#### Test Case 4: Non-existent User
```
Username: fakeuser
Password: fakepass
Expected: ❌ Tên đăng nhập hoặc mật khẩu sai!
          → PasswordField được clear
          → Vẫn ở LoginForm
```

---

## 🎨 MainFrame - Giao Diện

### Khi đăng nhập thành công

**MainFrame Layout**:
```
┌─────────────────────────────────────────────────────────────┐
│ HỆ THỐNG XÉT TUYỂN 2026        Đăng nhập: admin (admin)   │  NORTH
├────────────┬──────────────────────────────────────────────┤
│            │                                              │
│  🏠 Trang  │                                              │
│    Chủ     │                                              │
│            │                                              │
│  👥 Quản   │           [CardLayout Area]                 │
│    Lý      │           HomePanel / ThiSinhPanel /        │
│    Thí     │           NganhPanel / etc.                 │
│    Sinh    │                                              │
│            │                                              │
│  🎓 Quản   │                                              │
│    Lý      │                                              │
│    Ngành   │                                              │  CENTER
│            │                                              │
│  📊 Quản   │                                              │
│    Lý      │                                              │
│    Điểm    │                                              │
│            │                                              │
│  📋 Quản   │                                              │
│    Lý      │                                              │
│    Nguyện  │                                              │
│    Vọng    │                                              │
│            │                                              │
│  👤 Quản   │                                              │
│    Lý      │                                              │
│    Hệ      │                                              │
│    Thống   │  (ADMIN ONLY)                               │
│            │                                              │
│  🚪 Đăng   │                                              │
│    Xuất    │                                              │
│            │                                              │
├────────────┴──────────────────────────────────────────────┤
│ © 2026 Hệ Thống Xét Tuyển - Version 1.0                  │  SOUTH
└─────────────────────────────────────────────────────────────┘

WEST (200px)        CENTER (~800px)
```

### Thao Tác

1. **Click "👥 Quản Lý Thí Sinh"**
   - CardLayout chuyển sang ThiSinhPanel
   - Hiển thị: "[Chính trong thiết kế]..."

2. **Click "🚪 Đăng Xuất"**
   - Show confirmation dialog "Bạn có chắc chắn muốn đăng xuất?"
   - Nếu YES: Close MainFrame + Open LoginForm
   - Nếu NO: Vẫn ở MainFrame

---

## 🐛 Troubleshooting

### ❌ **Error: "java.sql.SQLException: No suitable driver found"**

**Nguyên nhân**: MySQL JDBC driver không được load  
**Giải pháp**:
```bash
# 1. Kiểm tra pom.xml có mysql-connector-java không
mvn dependency:tree | grep mysql

# 2. Nếu không có, thêm vào pom.xml:
<!-- File: pom.xml -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

# 3. Chạy lại
mvn clean install
```

### ❌ **Error: "HibernateException: Unable to instantiate default SessionFactory"**

**Nguyên nhân**: Không tìm thấy hibernate.cfg.xml hoặc lỗi XML  
**Giải pháp**:
```
1. Kiểm tra file: src/main/resources/hibernate.cfg.xml có tồn tại không
2. Validate XML format (không có lỗi syntax)
3. Đảm bảo file nằm đúng thư mục
```

### ❌ **Error: "com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure"**

**Nguyên nhân**: MySQL Server không khởi động hoặc username/password sai  
**Giải pháp**:
```bash
# 1. Kiểm tra MySQL chạy không
mysql -u root -p
# Nếu lỗi "Access denied" → password sai
# Nếu lỗi "Can't connect" → MySQL không chạy

# 2. Start MySQL (Windows)
net start MySQL80
# hoặc
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe"

# 3. Start MySQL (Linux/Mac)
sudo systemctl start mysql
# hoặc
sudo /usr/local/mysql/support-files/mysql.server start

# 4. Test connection
mysql -h localhost -u root -p --port=3306
```

### ❌ **Error: "No user found" when trying to login**

**Nguyên nhân**: Table `users` chưa được tạo hoặc data chưa insert  
**Giải pháp**:
```bash
# 1. Check if table exists
mysql -u root -p xettuyen2026
SELECT * FROM users;

# 2. Nếu bảng chưa tồn tại, chạy setup_users.sql
SOURCE setup_users.sql;

# 3. Verify dữ liệu

SELECT COUNT(*) FROM users;
# Output: 4
```

### ❌ **Error: "Cannot find symbol: class LoginForm"**

**Nguyên nhân**: File LoginForm.java chưa được tạo hoặc nằm sai folder  
**Giải pháp**:
```
1. Kiểm tra folder: src/main/java/com/xettuyen/ui/
2. Đảm bảo file LoginForm.java có tồn tại
3. Run Maven → Right-click project → Maven → Update Project
```

### ❌ **Error: "java.lang.NoClassDefFoundError: com/xettuyen/ui/LoginForm"**

**Nguyên nhân**: LoginForm class chưa được compile  
**Giải pháp**:
```bash
# 1. Clean & compile
mvn clean compile

# 2. Refresh IDE
# - IntelliJ: Press Ctrl+Alt+F5
# - Eclipse: Press F5

# 3. Run lại
mvn exec:java -Dexec.mainClass="com.xettuyen.App"
```

### ❌ **LoginForm hiển thị nhưng không responsive**

**Nguyên nhân**: Run trên thread chính (EDT issue)  
**Giải pháp**: Đã fix sẵn trong LoginForm, sử dụng `SwingUtilities.invokeLater()`

### ❌ **Font không hiển thị tiếng Việt đúng**

**Nguyên nhân**: Font System không support Unicode  
**Giải pháp**:
```java
// Update font trong LoginForm & MainFrame:
JLabel titleLabel = new JLabel("HỆ THỐNG XÉT TUYỂN 2026");
titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
// Hoặc
titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
```

---

## ✅ Verification Checklist

- [ ] MySQL Server chạy
- [ ] Database `xettuyen2026` tồn tại
- [ ] Table `users` được tạo
- [ ] 4 test accounts được insert
- [ ] hibernate.cfg.xml được cập nhật
- [ ] Password trong hibernate.cfg.xml đúng
- [ ] pom.xml dependencies compile successful
- [ ] App.java chứa LoginForm
- [ ] LoginForm.java compiled
- [ ] MainFrame.java compiled
- [ ] User.java & UserDAO.java compiled
- [ ] DAOFactory.java updated (+ getUserDAO)
- [ ] Run App.main() → LoginForm hiển thị

---

## 🎯 Flow Diagram

```
START
  ↓
App.main()
  ↓
UIManager.setLookAndFeel()
  ↓
new LoginForm()
  ↓
User nhập username & password
  ↓
Click "Đăng Nhập"
  ↓
UserDAO.login(username, password)
  ├─ Query: SELECT FROM users WHERE username & password
  ├─ Found: (User) object
  │   ├─ Close LoginForm
  │   ├─ new MainFrame(username, role)
  │   ├─ Show MainFrame
  │   └─ Go to: MAIN APPLICATION
  │
  └─ Not Found: null
      ├─ Show error message
      ├─ Clear password field
      └─ Return to Login
```

---

## 📚 Tài Liệu Thêm

- [UI Layer README](src/main/java/com/xettuyen/ui/README.md)
- [DAO Layer README](src/main/java/com/xettuyen/dao/README.md)
- [Service Layer README](SERVICE_SUMMARY.md)
- [Setup Users SQL](setup_users.sql)

---

## 🚀 Quick Start (5 phút)

```bash
# 1. Setup database (2 phút)
mysql -u root -p xettuyen2026 < setup_users.sql

# 2. Update hibernate.cfg.xml password (30 giây)
# Sửa: <property name="hibernate.connection.password">YOUR_PASSWORD</property>

# 3. Compile (1 phút)
mvn clean install

# 4. Run (30 giây)
mvn exec:java -Dexec.mainClass="com.xettuyen.App"

# 5. Test login (1 phút)
# Username: admin
# Password: admin123
```

---

**Tác Giả**: Senior Java Developer  
**Date**: 31/03/2026  
**Status**: ✅ UI Layer - Ready to Test
