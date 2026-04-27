# 📌 DAO Layer - Tóm Tắt & Hướng Dẫn

## 🎯 Tóm Tắt Những Gì Vừa Được Tạo

### ✅ Yêu Cầu 1: Lớp GenericDAO
- **BaseDAO.java** - Lớp abstract generic cung cấp CRUD cơ bản cho tất cả Entity
  - `save(T entity)` - Lưu entity
  - `update(T entity)` - Cập nhật entity
  - `delete(T entity)` - Xóa entity
  - `findById(Class<T>, int id)` - Tìm theo ID
  - `findAll(Class<T>)` - Lấy toàn bộ
  - `findAllWithPagination(Class<T>, offset, limit)` - Lấy có phân trang
  - `countAll(Class<T>)` - Đếm tổng số
- ✅ Xử lý Transaction an toàn (try-catch-rollback)
- ✅ Mỗi hàm tự động mở/đóng Session

---

### ✅ Yêu Cầu 2: 8 Lớp DAO Cụ Thể
| DAO Class | Kế Thừa | Mục Đích |
|-----------|---------|---------|
| **BangQuyDoiDAO** | BaseDAO<BangQuydoi> | Bảng quy đổi điểm |
| **DiemCongDAO** | BaseDAO<DiemCongXettuyen> | Điểm cộng xét tuyển |
| **DiemThiDAO** | BaseDAO<DiemThiXettuyen> | Điểm thi xét tuyển |
| **NganhDAO** | BaseDAO<Nganh> | Quản lý ngành học |
| **NganhToHopDAO** | BaseDAO<NganhTohop> | Mối quan hệ ngành-tổ hợp |
| **NguyenVongDAO** | BaseDAO<NguyenVongXettuyen> | Nguyện vọng xét tuyển |
| **ThiSinhDAO** | BaseDAO<ThiSinhXettuyen> | Quản lý thí sinh |
| **ToHopMonDAO** | BaseDAO<TohopMonthi> | Tổ hợp môn thi |

---

### ✅ Yêu Cầu 3: Custom HQL Queries

#### **ThiSinhDAO**
```java
// Tìm thí sinh chính xác theo CCCD
ThiSinhXettuyen findByCCCD(String cccd)

// Tìm kiếm theo keyword (LIKE) - hỗ trợ phân trang
List<ThiSinhXettuyen> searchByKeyword(String keyword, int offset, int limit)

// Đếm kết quả tìm kiếm
long countSearchResult(String keyword)
```

#### **NganhDAO**
```java
// Tìm ngành theo mã
Nganh findByMaNganh(String maNganh)
```

#### **DiemThiDAO, DiemCongDAO, NguyenVongDAO**
```java
// Lấy danh sách dữ liệu theo CCCD của thí sinh
List<DiemThi> findByCCCD(String cccd)
List<DiemCong> findByCCCD(String cccd)
List<NguyenVong> findByCCCD(String cccd)  // Sắp xếp theo thứ tự
```

#### **NganhToHopDAO**
```java
// Tìm mối quan hệ ngành và tổ hợp
NganhTohop findByMaNganhAndMaToHop(String maNganh, String maToHop)
```

---

## 📂 Cấu Trúc Thư Mục

```
xettuyen2026/
│
src/main/java/com/xettuyen/
│
├── entity/
│   ├── BangQuydoi.java
│   ├── DiemCongXettuyen.java
│   ├── DiemThiXettuyen.java
│   ├── Nganh.java
│   ├── NganhTohop.java
│   ├── NguyenVongXettuyen.java
│   ├── ThiSinhXettuyen.java
│   └── TohopMonthi.java
│
├── dao/
│   ├── BaseDAO.java                 # Generic base class
│   ├── BangQuyDoiDAO.java
│   ├── DiemCongDAO.java
│   ├── DiemThiDAO.java
│   ├── NganhDAO.java
│   ├── NganhToHopDAO.java
│   ├── NguyenVongDAO.java
│   ├── ThiSinhDAO.java
│   ├── ToHopMonDAO.java
│   ├── DAOFactory.java              # Factory pattern
│   ├── DAOExample.java              # Ví dụ sử dụng
│   └── README.md                    # Tài liệu chi tiết
│
├── service/
│   ├── ThiSinhService.java          # Service layer example
│   └── NganhService.java
│
├── util/
│   └── HibernateUtil.java           # SessionFactory management
│
└── App.java

src/main/resources/
└── hibernate.cfg.xml                # Cấu hình Hibernate
```

---

## 🚀 Cách Sử Dụng Nhanh

### 1️⃣ Cấu Hình Hibernate
Sửa file `src/main/resources/hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/xettuyen2026</property>
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">your_password</property>
```

### 2️⃣ Lấy DAO Instance
```java
// Cách 1: Từ DAOFactory (Recommended)
ThiSinhDAO thiSinhDAO = DAOFactory.getThiSinhDAO();

// Cách 2: Trực tiếp
SessionFactory sf = HibernateUtil.getSessionFactory();
ThiSinhDAO thiSinhDAO = new ThiSinhDAO(sf);
```

### 3️⃣ Thực Hiện CRUD
```java
ThiSinhDAO dao = DAOFactory.getThiSinhDAO();

// Create
ThiSinhXettuyen ts = new ThiSinhXettuyen();
ts.setCccd("123456789");
ts.setHo("Trần");
ts.setTen("Minh");
dao.save(ts);

// Read
ThiSinhXettuyen found = dao.findByCCCD("123456789");
System.out.println(found.getTen());

// Update
ts.setEmail("minh@example.com");
dao.update(ts);

// Delete
dao.delete(ts);
```

### 4️⃣ Truy Vấn Đặc Thù (Custom Queries)
```java
// Tìm kiếm thí sinh
List<ThiSinhXettuyen> results = dao.searchByKeyword("Nguyễn", 0, 10);

// Lấy nguyện vọng của thí sinh
NguyenVongDAO nv = DAOFactory.getNguyenVongDAO();
List<NguyenVongXettuyen> wishes = nv.findByCCCD("123456789");

// Tìm ngành
NganhDAO nganhDAO = DAOFactory.getNganhDAO();
Nganh nganh = nganhDAO.findByMaNganh("CNTT");
```

### 5️⃣ Phân Trang
```java
ThiSinhDAO dao = DAOFactory.getThiSinhDAO();

// Lấy 15 bản ghi từ vị trí 30 (trang 3 với 15 item/trang)
List<ThiSinhXettuyen> page = dao.findAllWithPagination(
    ThiSinhXettuyen.class, 30, 15
);

// Tổng số bản ghi
long total = dao.countAll(ThiSinhXettuyen.class);
int totalPages = (int) Math.ceil((double) total / 15);
```

---

## 🏗️ Kiến Trúc 3 Tầng

```
┌─────────────────────────────┐
│   UI/View Layer (Swing)     │  ← JavaSwing GUI
├─────────────────────────────┤
│   Service Layer             │  ← Business Logic
│   (ThiSinhService, etc)     │
├─────────────────────────────┤
│   DAO Layer                 │  ← Data Access
│   (DAOFactory, DAOs)        │
├─────────────────────────────┤
│   Entity Classes            │  ← Model/POJO
│   (ThiSinhXettuyen, etc)    │
├─────────────────────────────┤
│   Hibernate + JPA           │  ← ORM Framework
├─────────────────────────────┤
│   MySQL Database            │  ← Persistence
└─────────────────────────────┘
```

---

## 💡 Ví Dụ Toàn Bộ Quy Trình

### Tìm thí sinh và lấy toàn bộ thông tin:
```java
public class MainApp {
    public static void main(String[] args) {
        // Lấy service
        ThiSinhService service = new ThiSinhService();
        
        // Lấy thông tin toàn bộ thí sinh
        String cccd = "123456789";
        ThiSinhService.ThiSinhInfo info = service.getThiSinhFullInfo(cccd);
        
        if (info != null) {
            // Thông tin cơ bản
            System.out.println("Họ: " + info.thiSinh.getHo());
            System.out.println("Tên: " + info.thiSinh.getTen());
            System.out.println("Email: " + info.thiSinh.getEmail());
            
            // Điểm thi
            System.out.println("\nĐiểm thi:");
            if (!info.diemThi.isEmpty()) {
                DiemThiXettuyen dt = info.diemThi.get(0);
                System.out.println("Toán: " + dt.getTo());
                System.out.println("Lý: " + dt.getLi());
                System.out.println("Hoá: " + dt.getHo());
            }
            
            // Nguyện vọng
            System.out.println("\nNguyện vọng:");
            for (int i = 0; i < info.nguyenVongs.size(); i++) {
                NguyenVongXettuyen nv = info.nguyenVongs.get(i);
                System.out.println((i+1) + ". " + nv.getNvMaNganh());
            }
        }
        
        // Kết thúc
        HibernateUtil.shutdown();
    }
}
```

---

## 🎓 Design Patterns Được Áp Dụng

| Pattern | Nơi Sử Dụng | Lợi Ích |
|---------|-------------|--------|
| **Generic DAO** | BaseDAO<T> | Tái sử dụng code, giảm duplication |
| **Factory** | DAOFactory | Tập trung điểm tạo DAO instances |
| **Singleton** | HibernateUtil | Chỉ một SessionFactory duy nhất |
| **DTO** | SearchResult<T> | Chuyển dữ liệu giữa tầng |
| **Template Method** | BaseDAO | Định nghĩa skeleton của CRUD |

---

## ⚙️ Cấu Hình Maven (pom.xml)

Đảm bảo project có các dependencies sau:
```xml
<!-- Hibernate -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.2.0.Final</version>
</dependency>

<!-- MySQL Connector -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- C3P0 Connection Pool -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-c3p0</artifactId>
    <version>6.2.0.Final</version>
</dependency>
```

---

## 📊 So Sánh: Trước vs Sau

### Trước (Không có Generic DAO)
```java
// Người dùng phải viết mã tương tự cho mỗi DAO
public class ThiSinhDAOOld {
    public void save(ThiSinhXettuyen ts) { /* code */ }
    public void update(ThiSinhXettuyen ts) { /* code */ }
    public void delete(ThiSinhXettuyen ts) { /* code */ }
    // ... 5 hàm khác lặp lại
}

public class NganhDAOOld {
    public void save(Nganh n) { /* code */ }
    public void update(Nganh n) { /* code */ }
    public void delete(Nganh n) { /* code */ }
    // ... 5 hàm khác lặp lại (SAME CODE!)
}
```
❌ **Code Duplication**: ~80% code trùng nhau

### Sau (Với Generic DAO)
```java
// BaseDAO<T> định nghĩa 1 lần
public abstract class BaseDAO<T> {
    public void save(T entity) { /* code */ }
    public void update(T entity) { /* code */ }
    // ... các hàm generic khác
}

// Các DAO con chỉ cần kế thừa
public class ThiSinhDAO extends BaseDAO<ThiSinhXettuyen> { }
public class NganhDAO extends BaseDAO<Nganh> { }
```
✅ **Reusability**: Giảm 80% code duplication

---

## 🔍 Troubleshooting

### ❌ Lỗi: `Session was closed` hoặc `LazyInitializationException`
**Nguyên nhân**: Session bị đóng trước khi truy cập lazy fields  
**Giải pháp**: 
```java
// ✅ Đúng - Truy cập dữ liệu trong Session
Session session = sessionFactory.openSession();
Data data = session.get(Data.class, 1);
String value = data.getField();  // Truy cập ngay
session.close();
```

### ❌ Lỗi: `Could not locate cfg.xml`
**Nguyên nhân**: File `hibernate.cfg.xml` không ở đúng vị trí  
**Giải pháp**: Đặt file dalam `src/main/resources/`

### ❌ Lỗi: `Dialect class not found`
**Nguyên nhân**: Hibernate dialect sai với DB  
**Giải pháp** (MySQL 8):
```xml
<property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
```

---

## ✅ Checklist Trước Khi Chạy

- [ ] Cấu hình `hibernate.cfg.xml` (URL, username, password)
- [ ] Database `xettuyen2026` đã tạo
- [ ] MySQL running trên port 3306
- [ ] Maven dependencies đã cài đặt
- [ ] `hibernate.cfg.xml` ở trong `src/main/resources/`
- [ ] Entity classes được mapping trong `hibernate.cfg.xml`
- [ ] JDBC Driver (mysql-connector-java) đã thêm vào pom.xml

---

## 📚 Tài Liệu Thêm

| File | Mô Tả |
|------|-------|
| `dao/README.md` | Hướng dẫn chi tiết DAO layer |
| `dao/DAOExample.java` | Ví dụ toàn bộ CRUD operations |
| `service/ThiSinhService.java` | Ví dụ Service layer |
| `service/NganhService.java` | Ví dụ Service layer #2 |
| `util/HibernateUtil.java` | SessionFactory management |

---

## 🎓 Bài Học Chính

✅ Generic DAO Pattern → Giảm code duplication  
✅ Factory Pattern → Quản lý DAO instances tập trung  
✅ Singleton Pattern → Một SessionFactory duy nhất  
✅ Service Layer → Tách business logic ra khỏi DAO  
✅ HQL Queries → Truy vấn database một cách type-safe  
✅ Transaction Management → Xử lý lỗi an toàn  

---

**Tác Giả**: Senior Java Developer  
**Ngôn Ngữ**: Java 11+  
**Framework**: Hibernate 6.x + JPA  
**CSDL**: MySQL 8.0  
**Pattern**: Generic DAO + Factory + Singleton  

**Chúc lộc! 🚀**
