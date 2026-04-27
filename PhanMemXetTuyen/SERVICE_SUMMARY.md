# 📊 SERVICE LAYER - TÓMLẠI & HƯỚNG DẪN

## 🎯 Tổng Quan Toàn Bộ PROJECT

```
KIẾN TRÚC 3 TẦNG:

┌─────────────────────────────────────────────────────────┐
│  TẦNG 3: UI LAYER                                       │
│  (JavaSwing - GUI)                                      │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│  TẦNG 2: SERVICE LAYER (👈 VỪA VIẾT)                   │
│  - ExcelImportService (import dữ liệu)                  │
│  - XetTuyenService (tính điểm)                          │
│  - ThiSinhService (quản lý thí sinh)                    │
│  - NganhService (quản lý ngành)                         │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│  TẦNG 1: DAO LAYER (Đã viết)                           │
│  - BaseDAO<T> (Generic CRUD)                            │
│  - 8 DAO Classes (ThiSinhDAO, etc)                      │
│  - DAOFactory                                           │
│  - HibernateUtil                                        │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│  MODEL LAYER                                            │
│  - 8 Entity Classes (JPA @Entity)                       │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│  DATABASE: MySQL                                        │
│  - xettuyen2026 (8 tables)                              │
└─────────────────────────────────────────────────────────┘
```

---

## 🔧 TẦNG SERVICE - CHI TIẾT

### 1️⃣ ExcelImportService

**Vị trí**: `src/main/java/com/xettuyen/service/ExcelImportService.java`

**Mục đích**: Đọc file Excel (.xlsx) và chuyển đổi thành Java objects

**Dependencies**:
- Apache POI 5.2.3 (xử lý Excel)

**Các Hàm Chính**:

| Hàm | Input | Output | Mô Tả |
|-----|-------|--------|-------|
| `importThiSinh()` | File path | `List<ThiSinhXettuyen>` | Import thí sinh |
| `importDiemThi()` | File path | `List<DiemThiXettuyen>` | Import điểm thi |
| `importNguyenVong()` | File path | `List<NguyenVongXettuyen>` | Import nguyện vọng |

**Xử Lý An Toàn**:
- ✅ Bỏ qua header (dòng 1)
- ✅ Bỏ qua row trống
- ✅ Kiểm tra cell null
- ✅ Ép kiểu String/Double/Integer an toàn
- ✅ Log lỗi từng dòng nhưng **tiếp tục** đọc
- ✅ Trả về List những bản ghi đọc được

**Ví Dụ Sử Dụng**:
```java
ExcelImportService excel = new ExcelImportService();

// Import thí sinh từ Excel
List<ThiSinhXettuyen> thiSinh = excel.importThiSinh("D:\\data\\thisinh.xlsx");
// ✅ Import thành công 100 thí sinh từ D:\data\thisinh.xlsx

// Lưu vào database
ThiSinhDAO dao = DAOFactory.getThiSinhDAO();
for (ThiSinhXettuyen ts : thiSinh) {
    dao.save(ts);
}
```

---

### 2️⃣ XetTuyenService

**Vị trí**: `src/main/java/com/xettuyen/service/XetTuyenService.java`

**Mục đích**: Tính điểm xét tuyển cho thí sinh

**Dependencies**:
- DAO layer (DAOFactory)
- Hibernate sessions

**Constructor**:
```java
public XetTuyenService(
    DiemThiDAO,           // Lấy điểm thi
    DiemCongDAO,          // Lấy điểm cộng
    BangQuyDoiDAO,        // Lấy bảng quy đổi
    NguyenVongDAO,        // Update kết quả
    NganhToHopDAO         // Lấy tổ hợp môn
)
```

**Hàm Chính**:

#### `tinhDiemXetTuyenCuaThiSinh(String cccd)`
```java
// Tính điểm cho một thí sinh
xetTuyenService.tinhDiemXetTuyenCuaThiSinh("0123456789");

// OUTPUT:
// 📊 Bắt đầu tính điểm xét tuyển cho: 0123456789
//   ✓ Nguyện vọng 1 | Ngành: CNTT | Điểm xét tuyển: 7.85
//   ✓ Nguyện vọng 2 | Ngành: KTDN | Điểm xét tuyển: 7.62  
// ✅ Tính điểm xét tuyển thành công cho thí sinh: 0123456789
```

**Quy Trình 6 Bước**:
1. Lấy điểm thi của thí sinh
2. Lấy danh sách nguyện vọng
3. Cho MỖI nguyện vọng:
   - Lấy tổ hợp môn của ngành
   - Tính điểm tổ hợp (diem_thxt)
   - Lấy điểm ưu tiên (diem_utqd)
   - Tính điểm cộng (diem_cong)
   - Tính điểm xét tuyển = diem_thxt + diem_utqd
4. Update kết quả vào database
5. Log thông tin

**Công Thức Tính Toán**:

**Điểm Tổ Hợp** (diem_thxt):
```
diem_thxt = (Mon1×Hs1 + Mon2×Hs2 + Mon3×Hs3) / (Hs1+Hs2+Hs3)

VD: Toán 8.0×2 + Lý 7.5×1 + Hoá 7.0×1 / (2+1+1)
    = (16 + 7.5 + 7) / 4 = 7.625
```

**Điểm Ưu Tiên** (diem_utqd):
```
Lấy từ căn cứ:
- Đối tượng tuyển sinh (kinh tế khó khăn, dân tộc, v.v.)
- Khu vực (A, B, C)
- Chính sách ưu tiên khác
```

**Điểm Xét Tuyển Cuối Cùng**:
```
DIEM_XET_TUYEN = diem_thxt + diem_utqd
VD: 7.625 + 0.75 = 8.375
```

**Hàm Bổ Trợ**:

| Hàm | Mô Tả |
|-----|-------|
| `layDiemMon()` | Lấy điểm một môn cụ thể |
| `tinhDiemToHop()` | Tính điểm according công thức |
| `layDiemUuTien()` | Lấy điểm ưu tiên |
| `tinhDiemCong()` | Tính tổng điểm 3 môn |
| `tinhDiemXetTuyenChoAllThiSinh()` | Tính hàng loạt |
| `resetDiemXetTuyenCuaThiSinh()` | Reset lại điểm |

---

### 3️⃣ ThiSinhService (Đã Có)

**Hàm chính**:
- `getThiSinhFullInfo()` - Lấy toàn bộ thông tin
- `searchByKeyword()` - Tìm kiếm với phân trang
- `createThiSinh()` - Tạo mới
- `updateThiSinhEmail()` - Cập nhật

---

### 4️⃣ NganhService (Đã Có)

**Hàm chính**:
- `getNganhDetail()` - Lấy chi tiết ngành + tổ hợp
- `createNganh()` - Tạo mới
- `getNganhWithPagination()` - Lấy với phân trang

---

## 📂 Cấu Trúc Thư Mục

```
xettuyen2026/
│
├── src/main/java/com/xettuyen/
│   ├── entity/                  ✅ Entity (8 classes)
│   ├── dao/                     ✅ DAO Layer (9 classes)
│   ├── service/                 ✨ SERVICE LAYER (VỪA VIẾT)
│   │   ├── ExcelImportService.java
│   │   ├── XetTuyenService.java
│   │   ├── ThiSinhService.java
│   │   ├── NganhService.java
│   │   ├── ServiceExample.java
│   │   └── README.md            📖 Tài liệu chi tiết
│   ├── util/
│   │   └── HibernateUtil.java
│   └── App.java
│
├── src/main/resources/
│   └── hibernate.cfg.xml        ✅ Cấu hình Hibernate
│
├── pom.xml                       ✅ UPDATED - Dependencies
│
├── DAO_SUMMARY.md               📖 Tài liệu DAO Layer
├── SERVICE_SUMMARY.md           📖 File này
│
└── target/                       Build output
```

---

## 🚀 CÔNG VIỆC CẦN LÀM

### Bước 1: Cập Nhật Maven Dependencies
```bash
# File: pom.xml
# ✅ Đã cập nhật với:
# - Hibernate Core 6.2.0
# - MySQL Connector 8.0.33
# - Apache POI 5.2.3 (Excel)
# - Logback 1.4.8 (Logging)
```

### Bước 2: Chạy Maven Install
```bash
mvn clean install
```

### Bước 3: Cấu Hình hibernate.cfg.xml
```xml
<!-- Sửa trong: src/main/resources/hibernate.cfg.xml -->
<property name="hibernate.connection.url">
    jdbc:mysql://localhost:3306/xettuyen2026
</property>
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">YOUR_PASSWORD</property>
```

### Bước 4: Chạy ServiceExample
```bash
# Từ IDE: Right-click ServiceExample.java → Run As → Java Application
# Hoặc terminal:
mvn exec:java -Dexec.mainClass="com.xettuyen.service.ServiceExample"
```

---

## 💡 Ví Dụ Sử Dụng Thực Tế

### Quy Trình Hoàn Chỉnh

```java
public class XetTuyenMain {
    public static void main(String[] args) {
        try {
            // ============ KHỞI TẠO ============
            ExcelImportService excel = new ExcelImportService();
            XetTuyenService xetTuyen = new XetTuyenService(
                DAOFactory.getDiemThiDAO(),
                DAOFactory.getDiemCongDAO(),
                DAOFactory.getBangQuyDoiDAO(),
                DAOFactory.getNguyenVongDAO(),
                DAOFactory.getNganhToHopDAO()
            );
            
            // ============ IMPORT ============
            // Đọc file Excel
            List<ThiSinhXettuyen> thiSinhList = 
                excel.importThiSinh("D:\\data\\thisinh.xlsx");
            List<DiemThiXettuyen> diemThiList = 
                excel.importDiemThi("D:\\data\\diemthi.xlsx");
            List<NguyenVongXettuyen> nvList = 
                excel.importNguyenVong("D:\\data\\nguyenvong.xlsx");
            
            // ============ LƯU DATABASE ============
            ThiSinhDAO tDAO = DAOFactory.getThiSinhDAO();
            DiemThiDAO dtDAO = DAOFactory.getDiemThiDAO();
            NguyenVongDAO nvDAO = DAOFactory.getNguyenVongDAO();
            
            thiSinhList.forEach(tDAO::save);
            diemThiList.forEach(dtDAO::save);
            nvList.forEach(nvDAO::save);
            
            // ============ TÍNH ĐIỂM ============
            // Tính cho từng thí sinh
            for (ThiSinhXettuyen ts : thiSinhList) {
                xetTuyen.tinhDiemXetTuyenCuaThiSinh(ts.getCccd());
            }
            
            // ============ XUẤT KẾT QUẢ ============
            List<NguyenVongXettuyen> results = 
                nvDAO.findAll(NguyenVongXettuyen.class);
            
            for (NguyenVongXettuyen nv : results) {
                System.out.println(nv.getDiemXettuyen());
            }
            
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
```

---

## 🧪 Kiểm Thử (Testing)

### Unit Test ExcelImportService
```java
@Test
public void testImportThiSinh() {
    ExcelImportService service = new ExcelImportService();
    List<ThiSinhXettuyen> result = 
        service.importThiSinh("test_data/thisinh.xlsx");
    
    assertNotNull(result);
    assertEquals(100, result.size());
    assertEquals("0123456789", result.get(0).getCccd());
}
```

### Unit Test XetTuyenService
```java
@Test
public void testTinhDiem() {
    // Mock DAOs
    XetTuyenService service = new XetTuyenService(...);
    
    // Tính điểm
    service.tinhDiemXetTuyenCuaThiSinh("0123456789");
    
    // Verify kết quả
    assertTrue(diemXetTuyen > 0);
}
```

---

## 🔍 Debugging Tips

### Logger Output
```
ExcelImportService:
✅ Import thành công 100 thí sinh từ D:\data\thisinh.xlsx
⚠️ Lỗi khi xử lý dòng 42: NumberFormatException

XetTuyenService:
📊 Bắt đầu tính điểm xét tuyển cho: 0123456789
  ✓ Nguyện vọng 1 | Ngành: CNTT | Điểm xét tuyển: 7.85
✅ Tính điểm xét tuyển thành công
```

### Kiểm Tra Database
```sql
-- Kiểm tra dữ liệu sau import
SELECT * FROM xt_thisinh LIMIT 5;
SELECT * FROM xt_diemthi LIMIT 5;
SELECT * FROM xt_nguyenvong LIMIT 5;

-- Kiểm tra điểm xét tuyển
SELECT cccd, 
       diem_thxt, 
       diem_utqd, 
       diem_xettuyen 
FROM xt_nguyenvong 
WHERE diem_xettuyen IS NOT NULL;
```

---

## ⚙️ Cấu Hình Maven Build

```bash
# Compile
mvn clean compile

# Test
mvn test

# Build JAR
mvn package

# Run từ command line
mvn exec:java -Dexec.mainClass="com.xettuyen.service.ServiceExample"

# Build với dependencies
mvn assembly:single
```

---

## 📦 Dependencies Được Thêm

| GroupId | ArtifactId | Version |
|---------|-----------|---------|
| org.hibernate.orm | hibernate-core | 6.2.0.Final |
| org.hibernate.orm | hibernate-c3p0 | 6.2.0.Final |
| com.mysql | mysql-connector-java | 8.0.33 |
| org.apache.poi | poi | 5.2.3 |
| org.apache.poi | poi-ooxml | 5.2.3 |
| org.slf4j | slf4j-api | 2.0.7 |
| ch.qos.logback | logback-classic | 1.4.8 |
| junit | junit | 4.13.2 |

---

## 🞋 Tình Trạng Dự Án

| Tầng | Status | Classes | Tệp |
|------|--------|---------|-----|
| **Entity** | ✅ Complete | 8 | entity/ |
| **DAO** | ✅ Complete | 10 | dao/ |
| **Service** | ✅ Complete | 4 | service/ |
| **UI (Swing)** | ⏳ TODO | - | - |
| **Test** | ⏳ TODO | - | - |

---

## 🎓 Bài Học Thiết Kế

✅ **Layer Architecture** - Tách biệt concerns  
✅ **Generic Programming** - BaseDAO<T>  
✅ **Factory Pattern** - DAOFactory  
✅ **Excel Processing** - Apache POI  
✅ **Business Logic** - Service Layer  
✅ **Error Handling** - Try-catch-rollback  
✅ **Transaction Management** - Hibernate  
✅ **Logging** - Structured logging  

---

## 📞 Hỗ Trợ

### Lỗi Thường Gặp

| Lỗi | Nguyên Nhân | Giải Pháp |
|-----|-----------|----------|
| `java.io.FileNotFoundException` | File Excel không tìm thấy | Kiểm tra đường dẫn |
| `NumberFormatException` | Cell không phải number | Do xử lý an toàn, return 0.0 |
| `SQLException` | Database không kết nối | Cấu hình `hibernate.cfg.xml` |
| `LazyInitializationException` | Session đóng sớm | Truy cập data trong session |

### Cách Chạy Local Development

```bash
# Terminal 1: Start MySQL
mysqld

# Terminal 2: Create Database
mysql -u root -p xettuyen2026 < xettuyen2026_empty.sql

# Terminal 3: Run ứng dụng
cd xettuyen2026
mvn clean compile
mvn exec:java -Dexec.mainClass="com.xettuyen.service.ServiceExample"
```

---

## 🎉 Hoàn Thành

✅ **ExcelImportService** - Đọc Excel an toàn  
✅ **XetTuyenService** - Tính điểm đầy đủ  
✅ **Service Layer** - Logic nghiệp vụ hoàn chỉnh  
✅ **pom.xml** - Dependencies cập nhật  
✅ **Tài liệu** - README chi tiết  
✅ **Ví dụ** - ServiceExample chạy được  

**Tiếp Theo**: Xây dựng UI Layer (JavaSwing)

---

**Tác Giả**: Senior Java Developer  
**Ngôn Ngữ**: Java 11+  
**Framework**: Hibernate + Apache POI  
**Cơ Sở Dữ Liệu**: MySQL 8.0  
**Build Tool**: Maven 3.x  

**Date**: 31/03/2026
