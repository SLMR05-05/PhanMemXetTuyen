# 🏗️ ARCHITECTURE - TỔNG QUAN CÓC BỘ HỆ THỐNG

## 📊 4-TIER ARCHITECTURE DIAGRAM

```
┌─────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER (UI)                    │
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐│
│  │ LoginForm.java (450x400)                                   ││
│  │ ├─ TextField: username                                     ││
│  │ ├─ PasswordField: password                                 ││
│  │ ├─ Button: Login → UserDAO.login()                         ││
│  │ └─ Button: Exit                                            ││
│  └────────────────────────────────────────────────────────────┘│
│                             ↓                                    │
│  ┌────────────────────────────────────────────────────────────┐│
│  │ MainFrame.java (1024x768) - BorderLayout                   ││
│  │ ├─ NORTH: Header + User info                              ││
│  │ ├─ WEST: Sidebar Menu (6 buttons + logout)                ││
│  │ │        Role-based access (Admin/User)                   ││
│  │ ├─ CENTER: CardLayout (6 panels)                          ││
│  │ │          - HomePanel (placeholder)                      ││
│  │ │          - ThiSinhPanel (placeholder)                   ││
│  │ │          - NganhPanel (placeholder)                     ││
│  │ │          - DiemPanel (placeholder)                      ││
│  │ │          - NguyenVongPanel (placeholder)                ││
│  │ │          - UserPanel (placeholder, admin only)          ││
│  │ └─ SOUTH: Footer + copyright                              ││
│  └────────────────────────────────────────────────────────────┘│
│                       (Component: JFrame, JPanel, JButton,        │
│                        JTable (placeholder), CardLayout)         │
└─────────────────────────────────────────────────────────────────┘
                              ↓ ↑
┌─────────────────────────────────────────────────────────────────┐
│                      BUSINESS LOGIC LAYER                         │
│                  (Service Layer)                                 │
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐│
│  │ ExcelImportService.java                                    ││
│  │ ├─ importThiSinh(filePath): List<ThiSinhXettuyen>         ││
│  │ ├─ importDiemThi(filePath): List<DiemThiXettuyen>         ││
│  │ ├─ importNguyenVong(filePath): List<NguyenVongXettuyen>   ││
│  │ └─ Helper methods                                          ││
│  │    ├─ getCellValueAsString()                              ││
│  │    ├─ getCellValueAsDouble()                              ││
│  │    ├─ getCellValueAsInteger()                             ││
│  │    └─ isRowEmpty()                                        ││
│  └────────────────────────────────────────────────────────────┘│
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐│
│  │ XetTuyenService.java                                       ││
│  │ ├─ tinhDiemXetTuyenCuaThiSinh(cccd)                        ││
│  │ │  ├─ Fetch DiemThiXettuyen                               ││
│  │ │  ├─ Fetch NguyenVongXettuyen list                       ││
│  │ │  ├─ For each wish:                                      ││
│  │ │  │    ├─ Calculate diem_thxt (weighted score)           ││
│  │ │  │    ├─ Calculate diem_utqd (preferential)             ││
│  │ │  │    ├─ Calculate diem_xettuyen = thxt + utqd          ││
│  │ │  │    └─ Update database                                ││
│  │ │  └─ Log results                                         ││
│  │ │                                                          ││
│  │ │  Formula: diem_thxt = (M1×H1 + M2×H2 + M3×H3)/(H1+H2+H3) ││
│  │ │                                                          ││
│  │ ├─ tinhDiemXetTuyenChoAllThiSinh(List<ThiSinhXettuyen>)    ││
│  │ ├─ resetDiemXetTuyenCuaThiSinh(cccd)                       ││
│  │ └─ Helper methods                                          ││
│  │    ├─ tinhDiemToHop()                                     ││
│  │    ├─ tinhDiemCong()                                      ││
│  │    ├─ layDiemUuTien()                                     ││
│  │    └─ layDiemMon()                                        ││
│  └────────────────────────────────────────────────────────────┘│
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐│
│  │ ThiSinhService.java / NganhService.java                    ││
│  │ ├─ getThiSinhFullInfo()                                    ││
│  │ ├─ searchByKeyword(keyword, pageSize, pageNumber)          ││
│  │ ├─ createThiSinh(thiSinh)                                 ││
│  │ ├─ Inner class: SearchResult<T>                            ││
│  │ └─ Inner class: ThiSinhInfo                                ││
│  └────────────────────────────────────────────────────────────┘│
│                                                                  │
│            (Apache POI for Excel, Stream API,                   │
│             Exception Handling, Transaction Management)         │
└─────────────────────────────────────────────────────────────────┘
                              ↓ ↑
┌─────────────────────────────────────────────────────────────────┐
│                    DATA ACCESS LAYER                             │
│                   (DAO Layer)                                   │
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐│
│  │ BaseDAO<T> - Abstract Generic Class                        ││
│  │ ├─ CRUD Methods:                                           ││
│  │ │  ├─ save(T entity)                                       ││
│  │ │  ├─ update(T entity)                                     ││
│  │ │  ├─ delete(T entity)                                     ││
│  │ │  ├─ findById(ID id)                                      ││
│  │ │  ├─ findAll(Class<T> entityClass)                        ││
│  │ │  └─ getAll()                                             ││
│  │ │                                                          ││
│  │ ├─ Transaction Management:                                 ││
│  │ │  ├─ session.beginTransaction()                           ││
│  │ │  ├─ transaction.commit()                                 ││
│  │ │  └─ transaction.rollback()                               ││
│  │ │                                                          ││
│  │ └─ Exception Handling:                                      ││
│  │    ├─ Try-Catch-Finally                                    ││
│  │    ├─ Null checks                                          ││
│  │    └─ Error logging                                        ││
│  └────────────────────────────────────────────────────────────┘│
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐│
│  │ Concrete DAO Classes (extends BaseDAO<T>)                  ││
│  │ ├─ UserDAO                                                 ││
│  │ │  ├─ login(username, password): User                     ││
│  │ │  ├─ findByUsername(username): User                      ││
│  │ │  ├─ getAllActiveUsers(): List<User>                     ││
│  │ │  ├─ createUser(user): boolean                           ││
│  │ │  ├─ updateUser(user): boolean                           ││
│  │ │  └─ deleteUser(userId): boolean                         ││
│  │ │                                                          ││
│  │ ├─ ThiSinhDAO                                              ││
│  │ │  ├─ findByCCCD(cccd)                                     ││
│  │ │  ├─ searchByKeyword(keyword)                             ││
│  │ │  └─ custom HQL queries                                   ││
│  │ │                                                          ││
│  │ ├─ DiemThiDAO, DiemCongDAO                                 ││
│  │ ├─ NganhDAO, NganhToHopDAO                                 ││
│  │ ├─ NguyenVongDAO                                           ││
│  │ ├─ BangQuyDoiDAO                                           ││
│  │ └─ ToHopMonDAO                                             ││
│  └────────────────────────────────────────────────────────────┘│
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐│
│  │ DAOFactory - Factory Pattern (Singleton)                   ││
│  │ ├─ Cached DAO instances                                    ││
│  │ ├─ getBangQuyDoiDAO(): BangQuyDoiDAO                       ││
│  │ ├─ getDiemCongDAO(): DiemCongDAO                           ││
│  │ ├─ getDiemThiDAO(): DiemThiDAO                             ││
│  │ ├─ getNganhDAO(): NganhDAO                                 ││
│  │ ├─ getNganhToHopDAO(): NganhToHopDAO                       ││
│  │ ├─ getNguyenVongDAO(): NguyenVongDAO                       ││
│  │ ├─ getThiSinhDAO(): ThiSinhDAO                             ││
│  │ ├─ getToHopMonDAO(): ToHopMonDAO                           ││
│  │ ├─ getUserDAO(): UserDAO                                   ││
│  │ └─ reset(): void                                           ││
│  └────────────────────────────────────────────────────────────┘│
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐│
│  │ HibernateUtil - Singleton Pattern                          ││
│  │ ├─ SessionFactory sessionFactory                           ││
│  │ ├─ getSessionFactory(): SessionFactory                     ││
│  │ ├─ shutdown(): void                                        ││
│  │ └─ Config from hibernate.cfg.xml                           ││
│  └────────────────────────────────────────────────────────────┘│
│                                                                  │
│      (Hibernate ORM, HQL, Transaction, Session Management)     │
└─────────────────────────────────────────────────────────────────┘
                              ↓ ↑
┌─────────────────────────────────────────────────────────────────┐
│                      ENTITY LAYER (Model)                        │
│                    (JPA Annotations)                             │
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐│
│  │ Entity Classes (@Entity, @Table, JPA mappings):            ││
│  │                                                            ││
│  │ 1. User.java                                              ││
│  │    @Table(name = "users")                                 ││
│  │    ├─ id (PK)                                             ││
│  │    ├─ username (UNIQUE)                                   ││
│  │    ├─ password                                            ││
│  │    ├─ email                                               ││
│  │    ├─ fullName                                            ││
│  │    ├─ role ("admin" / "user")                              ││
│  │    └─ isActive (boolean)                                  ││
│  │                                                            ││
│  │ 2. ThiSinhXettuyen.java                                   ││
│  │    @Table(name = "xt_thisinh")                            ││
│  │    ├─ id, cccd (UNIQUE)                                   ││
│  │    ├─ hoTen, gioiTinh                                     ││
│  │    ├─ ngaySinh, diaChi                                    ││
│  │    └─ email, sdt                                          ││
│  │                                                            ││
│  │ 3. DiemThiXettuyen.java                                   ││
│  │    @Table(name = "xt_diemthi")                            ││
│  │    ├─ id, cccd (FK)                                       ││
│  │    └─ diemToan, diemLy, diemHoa, ... (9 subjects)        ││
│  │                                                            ││
│  │ 4. Nganh.java                                             ││
│  │    @Table(name = "xt_nganh")                              ││
│  │    ├─ id, maNganh                                         ││
│  │    ├─ tenNganh                                            ││
│  │    └─ chiTieu (quota)                                     ││
│  │                                                            ││
│  │ 5. NganhTohop.java                                        ││
│  │    @Table(name = "xt_nganh_tohop")                        ││
│  │    ├─ id, maNganh (FK), maToHop (FK)                      ││
│  │    └─ Linking between Nganh & ToHop                       ││
│  │                                                            ││
│  │ 6. TohopMonthi.java                                       ││
│  │    @Table(name = "xt_tohop_monthi")                       ││
│  │    ├─ Subject combinations (Toán-Lý-Hóa, etc.)           ││
│  │    └─ heSo1, heSo2, heSo3 (weights)                       ││
│  │                                                            ││
│  │ 7. NguyenVongXettuyen.java                                ││
│  │    @Table(name = "xt_nguyenvong")                         ││
│  │    ├─ id, cccd (FK)                                       ││
│  │    ├─ diem_thxt (combined score)                          ││
│  │    ├─ diem_utqd (preferential)                            ││
│  │    ├─ diem_xettuyen (final)                               ││
│  │    └─ Order (1st wish, 2nd wish, ...)                     ││
│  │                                                            ││
│  │ 8. DiemCongXettuyen.java                                  ││
│  │    @Table(name = "xt_diemcong")                           ││
│  │    ├─ Bonus points based on policies                      ││
│  │    └─ Applied to diem_utqd                                ││
│  │                                                            ││
│  │ 9. BangQuydoi.java                                        ││
│  │    @Table(name = "xt_bangquydoi")                         ││
│  │    ├─ Conversion table                                    ││
│  │    └─ Points mapping                                      ││
│  │                                                            ││
│  └────────────────────────────────────────────────────────────┘│
│                                                                  │
│       (JPA: @Entity, @Table, @Id, @GeneratedValue,             │
│        @Column, @ManyToOne, @OneToMany, @JoinColumn)           │
└─────────────────────────────────────────────────────────────────┘
                              ↓ ↑
┌─────────────────────────────────────────────────────────────────┐
│                      DATABASE LAYER                              │
│                                                                  │
│  MySQL 8.0 (Database: xettuyen2026)                             │
│  ├─ xt_thisinh (Students)                                       │
│  ├─ xt_diemthi (Test scores)                                    │
│  ├─ xt_diemcong (Bonus points)                                  │
│  ├─ xt_nganh (Programs)                                         │
│  ├─ xt_nganh_tohop (Program-Subject mappings)                   │
│  ├─ xt_nguyenvong (Wishes)                                      │
│  ├─ xt_tohop_monthi (Subject combinations)                      │
│  ├─ xt_bangquydoi (Conversion table)                            │
│  └─ users (Authentication - NEW)                               │
│                                                                  │
│  Engine: InnoDB (transaction support)                           │
│  Charset: utf8mb4 (Vietnamese support)                          │
│                                                                  │
│  Connections: C3P0 Connection Pool (5-20)                       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 DATA FLOW

### Login Flow
```
1. User fills LoginForm
   ↓
2. Click "Đăng Nhập"
   ↓
3. LoginForm → UserDAO.login(username, password)
   ↓
4. UserDAO → HibernateUtil → SessionFactory
   ↓
5. SQL Query: SELECT FROM users WHERE username=? AND password=?
   ↓
6. MySQL Database (users table)
   ├─ Found: User object
   │   ├─ Close LoginForm
   │   ├─ new MainFrame(username, role)
   │   └─ ENTER APPLICATION
   │
   └─ Not Found: null
       ├─ Show error
       └─ STAY AT LoginForm
```

### Excel Import Flow
```
1. User selects Excel file
   ↓
2. ExcelImportService.importThiSinh(filePath)
   ↓
3. Apache POI → Read .xlsx file
   ├─ For each row:
   │  ├─ getCellValueAsString/Double/Integer()
   │  ├─ Create Entity object (ThiSinhXettuyen)
   │  └─ Add to List (error logged if issue)
   ↓
4. Return List<ThiSinhXettuyen>
   ↓
5. UI Layer → For each entity:
   ├─ ThiSinhDAO.save(entity)
   │  ├─ HibernateUtil → SessionFactory
   │  ├─ Session.persist(entity)
   │  └─ Transaction.commit()
   ↓
6. MySQL Database (insert rows)
   ↓
7. Show: "✅ Import thành công 100 thí sinh"
```

### Score Calculation Flow
```
1. User selects student + clicks "Tính Điểm"
   ↓
2. XetTuyenService.tinhDiemXetTuyenCuaThiSinh(cccd)
   ↓
3. Fetch data (6 step process):
   Step 1: DiemThiDAO.findByCCCD(cccd) → DiemThiXettuyen
   Step 2: NguyenVongDAO.findByCCCD(cccd) → List<NguyenVongXettuyen>
   Step 3: For each NguyenVong:
           - NganhToHopDAO.find()
           - Calculate diem_thxt = (Mon1×Hs1 + Mon2×Hs2 + Mon3×Hs3)/(Hs1+Hs2+Hs3)
           - Calculate diem_utqd = (exam merit + policy bonus)
           - Calculate diem_xettuyen = diem_thxt + diem_utqd
   Step 4: NguyenVongDAO.update(nguyen_vong)
           - Set diem_xettuyen field
   Step 5: Transaction.commit()
   Step 6: Print log + return result
   ↓
4. MySQL Database (update rows)
   ↓
5. UI: Show "✅ Tính điểm xét tuyển thành công"
```

---

## 📦 FRAMEWORKS & LIBRARIES

| Framework | Version | Purpose |
|-----------|---------|---------|
| **Hibernate** | 6.2.0.Final | ORM (Object-Relational Mapping) |
| **MySQL Connector** | 8.0.33 | JDBC Driver for MySQL |
| **Apache POI** | 5.2.3 | Excel (.xlsx) processing |
| **C3P0** | Latest | Connection pooling |
| **SLF4J** | 2.0.7 | Logging facade |
| **Logback** | 1.4.8 | Logging implementation |
| **JUnit** | 4.13.2 | Unit testing |
| **Java Swing** | Built-in | GUI framework |
| **Jakarta Persistence** | Latest | JPA annotations |

---

## 🎯 DESIGN PATTERNS USED

| Pattern | Usage |
|---------|-------|
| **Generic Programming** | BaseDAO<T> - one class for all entities |
| **Factory Pattern** | DAOFactory - centralized DAO creation |
| **Singleton Pattern** | HibernateUtil - one SessionFactory |
| **MVC** | Model (Entity), View (MainFrame), Controller (Service) |
| **Service Layer** | Separates business logic from data access |
| **CardLayout Pattern** | MainFrame panels switching |
| **Role-Based Access Control** | Admin vs User menu based on role |
| **Try-Catch-Rollback** | Transaction safety |
| **Null Object Pattern** | Handle Optional data |

---

## 📊 CLASS DIAGRAM

```
┌──────────────────────────────────────┐
│           Entity (9 classes)         │
├──────────────────────────────────────┤
│  User, ThiSinh, DiemThi, Nganh,     │
│  NganhTohop, TohopMonthi, NguyenVong,│
│  DiemCong, BangQuydoi                │
└────────────────────┬─────────────────┘
                     │ @Entity
                     ↓
┌──────────────────────────────────────┐
│     DAO Layer (10 classes)           │
├──────────────────────────────────────┤
│         BaseDAO<T> (abstract)        │
│              ↑ extends               │
│     ┌────────┼────────┐              │
│     │        │        │              │
│  UserDAO ThiSinhDAO DiemThiDAO ...   │
└────────────────────┬─────────────────┘
                     │ uses
                     ↓
┌──────────────────────────────────────┐
│    Service Layer (4 classes)         │
├──────────────────────────────────────┤
│ ExcelImportService, XetTuyenService, │
│ ThiSinhService, NganhService         │
└────────────────────┬─────────────────┘
                     │ uses
                     ↓
┌──────────────────────────────────────┐
│       UI Layer (2 main + Panels)     │
├──────────────────────────────────────┤
│ LoginForm, MainFrame,                │
│ HomePanel, ThiSinhPanel, etc.        │
└──────────────────────────────────────┘
```

---

## 📈 PROJECT PROGRESSION

```
Phase 1: Entity + DAO ✅ COMPLETE
├─ 8 Entities (SQL tables)
├─ BaseDAO<T> + 8 DAOs
├─ DAOFactory + HibernateUtil
└─ Test data + SQL scripts

Phase 2: Service Layer ✅ COMPLETE
├─ ExcelImportService
├─ XetTuyenService
├─ ThiSinhService + NganhService
└─ Business logic + formulas

Phase 3: UI Layer (Basic) ✅ COMPLETE
├─ LoginForm (authentication)
├─ MainFrame (main application)
├─ CardLayout (panel switching)
├─ User Entity + UserDAO
└─ Role-based access control

Phase 4: UI Panels ⏳ IN PROGRESS
├─ □ ThiSinhPanel (JTable + CRUD)
├─ □ NganhPanel (JTable + CRUD)
├─ □ DiemPanel (JTable + CRUD)
├─ □ NguyenVongPanel (calculator)
└─ □ UserPanel (admin management)

Phase 5: Integration & Testing ⏳ TODO
├─ □ Service ↔ UI integration
├─ □ Unit tests
├─ □ Integration tests
└─ □ Performance optimization
```

---

## 🔐 Security Considerations

| Component | Security Measure |
|-----------|------------------|
| **Login** | Username + Password match check |
| **Database** | MySQL user/password + firewall |
| **Session** | Hibernate transaction management |
| **Data** | Role-based access control (RBAC) |
| **Excel** | Cell validation + error handling |
| **SQL** | HQL (parameterized queries) |
| **UI** | Logout functionality |

---

## 📊 STATISTICS

| Metric | Value |
|--------|-------|
| **Total Classes** | 25+ |
| **Total Lines of Code** | ~3500+ |
| **Design Patterns** | 8 |
| **Frameworks** | 8 |
| **Database Tables** | 9 |
| **Entities** | 9 |
| **DAOs** | 10 |
| **Services** | 4 |
| **UI Components** | 2 main + 6 panels |
| **Test Accounts** | 4 |

---

## 🎯 Key Features

✅ **Authentication**: Username/password login with role-based access  
✅ **Excel Import**: Safe cell reading with error handling  
✅ **Score Calculation**: Weighted formula with multiple subject combinations  
✅ **Database**: 9 tables with proper relationships  
✅ **Transaction Management**: Rollback on error  
✅ **GUI**: Professional 4-tier UI with CardLayout  
✅ **Logging**: Detailed error messages and audit trail  
✅ **Code Quality**: Clean code, comments, proper exception handling  

---

## 📚 DOCUMENTATION

| File | Purpose |
|------|---------|
| QUICK_START.md | 5-minute setup guide |
| SETUP_UI_LAYER.md | Detailed configuration |
| UI_SUMMARY.md | UI layer overview |
| SERVICE_SUMMARY.md | Service layer overview |
| DAO_SUMMARY.md | DAO layer overview |
| ARCHITECTURE.md | This file |
| README files | Component-specific docs |

---

## 🚀 DEPLOYMENT

```
1. Build
   mvn clean install

2. Package
   mvn assembly:assembly

3. Run
   java -cp target/xettuyen2026-jar-with-dependencies.jar com.xettuyen.App

4. Database
   MySQL 8.0 (xettuyen2026 database already created)
```

---

**Architecture Version**: 1.0  
**Date**: 31/03/2026  
**Status**: 🟡 3/5 Phases Complete
