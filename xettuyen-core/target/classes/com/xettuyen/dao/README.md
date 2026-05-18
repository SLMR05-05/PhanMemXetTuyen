# DAO Layer - Generic DAO Pattern

## Kiến Trúc Tổng Quan

Tầng DAO được xây dựng theo **Generic DAO Pattern**, cung cấp một giải pháp tối ưu và dễ bảo trì cho truy cập dữ liệu.

### Cấu Trúc Thư Mục

```
com.xettuyen.dao/
├── BaseDAO.java                 # Abstract generic DAO class
├── BangQuyDoiDAO.java           # DAO cho BangQuydoi
├── DiemCongDAO.java             # DAO cho DiemCongXettuyen (+ custom findByCCCD)
├── DiemThiDAO.java              # DAO cho DiemThiXettuyen (+ custom findByCCCD)
├── NganhDAO.java                # DAO cho Nganh (+ custom findByMaNganh)
├── NganhToHopDAO.java           # DAO cho NganhTohop (+ custom findByMaNganhAndMaToHop)
├── NguyenVongDAO.java           # DAO cho NguyenVongXettuyen (+ custom findByCCCD)
├── ThiSinhDAO.java              # DAO cho ThiSinhXettuyen (+ custom queries)
├── ToHopMonDAO.java             # DAO cho TohopMonthi
├── DAOFactory.java              # Factory Pattern - tạo và quản lý DAO instances
└── DAOExample.java              # Ví dụ sử dụng

com.xettuyen.util/
└── HibernateUtil.java           # Utility để quản lý SessionFactory
```

## 1. BaseDAO<T> - Generic Base Class

`BaseDAO` là một lớp abstract kế thừa generic, cung cấp các hàm CRUD cơ bản cho mọi Entity:

### Các hàm có sẵn:

| Hàm | Mô Tả |
|-----|-------|
| `save(T entity)` | Lưu entity mới |
| `update(T entity)` | Cập nhật entity |
| `delete(T entity)` | Xóa entity |
| `findById(Class<T>, int id)` | Tìm entity theo ID |
| `findAll(Class<T>)` | Lấy tất cả entity |
| `findAllWithPagination(Class<T>, offset, limit)` | Lấy dữ liệu có phân trang |
| `countAll(Class<T>)` | Đếm tổng số entity |

**Đặc điểm:**
- ✅ Xử lý Transaction tự động (try-catch-rollback)
- ✅ Đóng Session sau khi sử dụng
- ✅ Throw RuntimeException với thông báo lỗi chi tiết

## 2. Concrete DAO Classes

Mỗi Entity có một DAO class tương ứng kế thừa từ BaseDAO:

### ThiSinhDAO
```java
// Tìm thí sinh theo CCCD (exact match)
ThiSinhXettuyen findByCCCD(String cccd)

// Tìm kiếm thí sinh theo keyword (ho, ten, cccd) - LIKE query
List<ThiSinhXettuyen> searchByKeyword(String keyword, int offset, int limit)

// Đếm kết quả tìm kiếm
long countSearchResult(String keyword)
```

### NganhDAO
```java
// Tìm ngành theo mã
Nganh findByMaNganh(String maNganh)
```

### NganhToHopDAO
```java
// Tìm mối quan hệ ngành-tổ hợp
NganhTohop findByMaNganhAndMaToHop(String maNganh, String maToHop)
```

### DiemThiDAO / DiemCongDAO / NguyenVongDAO
```java
// Lấy danh sách dữ liệu theo CCCD của thí sinh
List<DiemThi> findByCCCD(String cccd)
List<DiemCong> findByCCCD(String cccd)
List<NguyenVong> findByCCCD(String cccd)  // sắp xếp theo thứ tự nguyện vọng
```

## 3. DAOFactory - Factory Pattern

`DAOFactory` cung cấp một cách tập trung để lấy DAO instances:

```java
// Lấy DAO instances
ThiSinhDAO thiSinhDAO = DAOFactory.getThiSinhDAO();
NganhDAO nganhDAO = DAOFactory.getNganhDAO();
DiemThiDAO diemThiDAO = DAOFactory.getDiemThiDAO();
// ...

// Reset tất cả instances (nếu cần)
DAOFactory.reset();
```

**Ưu điểm:**
- Single Point of Entry cho tất cả DAOs
- Lazy initialization - DAOs chỉ tạo khi cần
- Dễ thay đổi việc tạo DAO instances (nếu cần)

## 4. HibernateUtil - SessionFactory Management

`HibernateUtil` quản lý SessionFactory duy nhất cho toàn bộ ứng dụng:

```java
// Lấy SessionFactory
SessionFactory sf = HibernateUtil.getSessionFactory();

// Kết thúc ứng dụng
HibernateUtil.shutdown();
```

**Đặc điểm:**
- Singleton Pattern - chỉ có một instance
- Static initializer - khởi tạo lần đầu khi class được load
- Đọc cấu hình từ `hibernate.cfg.xml`

## 5. Cách Sử Dụng (Usage Examples)

### Lấy Thí Sinh Theo CCCD
```java
ThiSinhDAO dao = DAOFactory.getThiSinhDAO();
ThiSinhXettuyen ts = dao.findByCCCD("123456789");
if (ts != null) {
    System.out.println("Tên: " + ts.getTen());
}
```

### Tìm Kiếm Thí Sinh (Có Phân Trang)
```java
ThiSinhDAO dao = DAOFactory.getThiSinhDAO();

// Tìm kiếm với keyword, trang 1, 10 bản ghi/trang
List<ThiSinhXettuyen> results = dao.searchByKeyword("Nguyễn", 0, 10);

// Đếm tổng kết quả
long total = dao.countSearchResult("Nguyễn");
long totalPages = (total + 9) / 10; // Làm tròn lên
```

### Lấy Nguyện Vọng Của Thí Sinh (Sắp Xếp)
```java
NguyenVongDAO dao = DAOFactory.getNguyenVongDAO();

// Lấy nguyện vọng theo thứ tự
List<NguyenVongXettuyen> wishes = dao.findByCCCD("123456789");
for (NguyenVongXettuyen w : wishes) {
    System.out.println(w.getNvTt() + ": " + w.getNvMaNganh());
}
```

### CRUD Operations
```java
ThiSinhDAO dao = DAOFactory.getThiSinhDAO();

// Create
ThiSinhXettuyen ts = new ThiSinhXettuyen("123456", "BA01", "Trần", "Minh", ...);
dao.save(ts);

// Read
ThiSinhXettuyen ts = dao.findById(ThiSinhXettuyen.class, 1);

// Update
ts.setEmail("new@example.com");
dao.update(ts);

// Delete
dao.delete(ts);
```

### Phân Trang (Pagination)
```java
ThiSinhDAO dao = DAOFactory.getThiSinhDAO();

// Lấy 15 thí sinh từ vị trí 30 (trang 3)
List<ThiSinhXettuyen> page = dao.findAllWithPagination(ThiSinhXettuyen.class, 30, 15);

// Tổng số bản ghi
long total = dao.countAll(ThiSinhXettuyen.class);
```

## 6. HQL Queries Được Sử Dụng

### Câu lệnh HQL cơ bản:
```sql
-- Lấy tất cả
FROM EntityName

-- Tìm theo điều kiện
FROM EntityName WHERE property = :param

-- Tìm kiếm (LIKE)
FROM EntityName WHERE field LIKE :keyword

-- Sắp xếp
FROM EntityName WHERE ... ORDER BY property ASC/DESC

-- COUNT
SELECT COUNT(*) FROM EntityName

-- CONCAT (nối chuỗi)
FROM EntityName WHERE CONCAT(field1, ' ', field2) LIKE :keyword
```

## 7. Xử Lý Lỗi & Exception

Tất cả hàm DAO đều:
- ✅ Bắt Exception cụ thể
- ✅ Rollback Transaction nếu có lỗi
- ✅ Wrap error message vào RuntimeException
- ✅ Log thông báo lỗi chi tiết

```java
try {
    // Thực thi query
} catch (Exception e) {
    if (transaction != null) {
        transaction.rollback();
    }
    throw new RuntimeException("Mô tả lỗi: " + e.getMessage(), e);
}
```

## 8. Yêu Cầu Cấu Hình

Đảm bảo file `hibernate.cfg.xml` được đặt trong thư mục `src/main/resources/` với các entity mapping:

```xml
<mapping class="com.xettuyen.entity.ThiSinhXettuyen"/>
<mapping class="com.xettuyen.entity.Nganh"/>
<!-- ... -->
```

## 9. Advantages of Generic DAO Pattern

| Lợi Ích | Mô Tả |
|---------|-------|
| **DRY Principle** | Tránh viết lại code CRUD cho mỗi Entity |
| **Maintainability** | Thay đổi logic DAO ở một chỗ, áp dụng cho tất cả |
| **Scalability** | Dễ thêm Entity mới - chỉ cần tạo DAO kế thừa BaseDAO |
| **Type Safety** | Generic<T> đảm bảo type checking tại compile-time |
| **Consistency** | Tất cả DAOs tuân theo quy tắc chung |

## 10. Best Practices

✅ Luôn lấy DAO từ `DAOFactory`  
✅ Gọi `HibernateUtil.shutdown()` khi ứng dụng kết thúc  
✅ Kiểm tra null trước khi sử dụng kết quả query  
✅ Sử dụng phân trang cho danh sách lớn  
✅ Viết custom queries (DAO con) cho logic phức tạp  

---

**Tác giả:** Senior Java Developer  
**Pattern:** Generic DAO Pattern + Factory Pattern + Singleton Pattern  
**Framework:** Hibernate + JPA
