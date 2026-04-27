# 📚 SERVICE LAYER - Hướng Dẫn Chi Tiết

## 📋 Tổng Quan

Tầng Service được xây dựng với **hai lớp chính**:

1. **ExcelImportService** - Đọc dữ liệu từ file Excel (.xlsx)
2. **XetTuyenService** - Tính toán điểm xét tuyển
3. **ThiSinhService** - Quản lý thí sinh (đã có sẵn)
4. **NganhService** - Quản lý ngành (đã có sẵn)

---

## 🔧 ExcelImportService

### Mục Đích
Import dữ liệu từ file Excel vào trong-bộ nhớ (List) trước khi lưu vào database.

### Các Hàm Chính

#### 1. `importThiSinh(String filePath)`
```java
List<ThiSinhXettuyen> thiSinhList = excelService.importThiSinh("D:\\data\\thisinh.xlsx");
```

**Định dạng Excel (Header):**
```
CCCD | SoBaoDanh | Ho | Ten | NgaySinh | DienThoai | GioiTinh | Email | NoiSinh | DoiTuong | KhuVuc
```

**Ví dụ dữ liệu:**
```
0123456789 | BA001 | Trần | Minh | 01/01/2005 | 0123456789 | Nam | minh@example.com | Hà Nội | Kinh tế | Khu A
```

**Xử lý:**
- ✅ Bỏ qua dòng header (dòng 1)
- ✅ Bỏ qua row trống
- ✅ An toàn khi cell null
- ✅ Log lỗi từng dòng nhưng tiếp tục
- ✅ Return List tất cả bản ghi đọc được

---

#### 2. `importDiemThi(String filePath)`
```java
List<DiemThiXettuyen> diemThiList = excelService.importDiemThi("D:\\data\\diemthi.xlsx");
```

**Định dạng Excel (Header):**
```
CCCD | SoBaoDanh | TO | LI | HO | SI | SU | DI | VA | N1_THI | CNCN | CNNN | TI | KTPL
```

**Ví dụ dữ liệu:**
```
0123456789 | BA001 | 7.5 | 8.0 | 7.2 | 6.5 | 8.5 | 7.8 | 8.2 | 7.8 | 0 | 0 | 0 | 0
```

**Xử lý:**
- ✅ Ép kiểu numeric (Double) an toàn
- ✅ Mặc định 0.0 nếu cell trống hoặc lỗi
- ✅ Hỗ trợ các kiểu cell: NUMERIC, STRING, BOOLEAN

---

#### 3. `importNguyenVong(String filePath)`
```java
List<NguyenVongXettuyen> nguyenVongList = excelService.importNguyenVong("D:\\data\\nguyenvong.xlsx");
```

**Định dạng Excel (Header):**
```
CCCD | MaNganh | ThuTu | PhuongThuc | GhiChu
```

**Ví dụ dữ liệu:**
```
0123456789 | CNTT | 1 | XTT | Khoa học máy tính
0123456789 | KTDN | 2 | XTT | CT khoa
```

**Xử lý:**
- ✅ Tạo `nv_keys` = CCCD_ThuTu nếu chưa có
- ✅ Tự động sắp xếp theo thứ tự nguyện vọng

---

### 🛡️ Xử Lý An Toàn

#### getCellValueAsString()
```java
// Xử lý các kiểu cell khác nhau
Cell types: STRING, NUMERIC, BOOLEAN, FORMULA, BLANK
```

#### getCellValueAsDouble()
```java
// Chuyển đổi an toàn sang Double
- NULL/BLANK → 0.0
- NUMERIC → value
- STRING: "7.5" → 7.5, "" → 0.0
- BOOLEAN: true → 1.0, false → 0.0
- Lỗi: log warning + trả 0.0
```

#### getCellValueAsInteger()
```java
// Tương tự Double nhưng kiểu Integer
```

#### isRowEmpty()
```java
// Kiểm tra row có toàn bộ cell trống
```

---

### 📝 Ví Dụ Sử Dụng

```java
ExcelImportService excelService = new ExcelImportService();

// Import thí sinh
List<ThiSinhXettuyen> thiSinhList = excelService.importThiSinh("D:\\data\\thisinh.xlsx");
// Output:
// ✅ Import thành công 100 thí sinh từ D:\data\thisinh.xlsx

// Lưu vào database
ThiSinhDAO dao = DAOFactory.getThiSinhDAO();
for (ThiSinhXettuyen ts : thiSinhList) {
    dao.save(ts);  // Lưu từng bản ghi
}

// Import điểm thi
List<DiemThiXettuyen> diemThiList = excelService.importDiemThi("D:\\data\\diemthi.xlsx");
// Output:
// ✅ Import thành công 95 bản ghi điểm thi từ D:\data\diemthi.xlsx
// ⚠️ Lỗi khi xử lý dòng 42: ...
```

---

## 🎯 XetTuyenService

### Mục Đích
Xử lý logic tính toán **điểm xét tuyển** cho thí sinh dựa trên:
- Điểm thi các môn
- Tổ hợp môn của ngành
- Hệ số của từng môn
- Điểm ưu tiên

### Constructor
```java
XetTuyenService xetTuyenService = new XetTuyenService(
    diemThiDAO,      // Truy cập dữ liệu điểm thi
    diemCongDAO,     // Truy cập dữ liệu điểm cộng
    bangQuyDoiDAO,   // Truy cập bảng quy đổi ưu tiên
    nguyenVongDAO,   // Cập nhật kết quả nguyện vọng
    nganhToHopDAO    // Lấy tổ hợp môn của ngành
);
```

---

### Hàm Chính: `tinhDiemXetTuyenCuaThiSinh(String cccd)`

#### Quy Trình (6 Bước)

```
1. Lấy dữ liệu điểm thi từ database
                    ↓
2. Lấy danh sách nguyện vọng của thí sinh
                    ↓
3. Cho MỖI nguyện vọng:
   3.1 Lấy tổ hợp môn của ngành
   3.2 Tính ĐIỂM TỔ HỢP (diem_thxt)
   3.3 Lấy ĐIỂM ƯU TIÊN (diem_utqd)
   3.4 Tính ĐIỂM CỘNG (diem_cong)
   3.5 Tính ĐIỂM XÉT TUYỂN = diem_thxt + diem_utqd
                    ↓
4. Update kết quả vào database
                    ↓
5. Log kết quả
```

#### Ví Dụ Sử Dụng
```java
// Tính điểm cho thí sinh có CCCD = "0123456789"
xetTuyenService.tinhDiemXetTuyenCuaThiSinh("0123456789");

// OUTPUT:
// 📊 Bắt đầu tính điểm xét tuyển cho: 0123456789
//   ✓ Nguyện vọng 1 | Ngành: CNTT | Điểm xét tuyển: 7.85
//   ✓ Nguyện vọng 2 | Ngành: KTDN | Điểm xét tuyển: 7.62
// ✅ Tính điểm xét tuyển thành công cho thí sinh: 0123456789
```

---

### Công Thức Tính Điểm

#### 1. Điểm Tổ Hợp (diem_thxt)
```
diem_thxt = (Mon1 × Hs1 + Mon2 × Hs2 + Mon3 × Hs3) / (Hs1 + Hs2 + Hs3)
```

**Ví dụ:**
- Ngành CNTT chọn tổ họ A00: Toán (HS=2), Lý (HS=1), Hoá (HS=1)
- Điểm: Toán 8.0, Lý 7.5, Hoá 7.0
- Tính: (8.0×2 + 7.5×1 + 7.0×1) / (2+1+1) = (16 + 7.5 + 7) / 4 = 30.5 / 4 = **7.625**

#### 2. Điểm Ưu Tiên (diem_utqd)
```
Lấy từ bảng quy đổi dựa trên:
- Đối tượng tuyển sinh (kinh tế khó khăn, dân tộc, v.v.)
- Khu vực (khu A, B, C)
- Các chính sách ưu tiên khác
```

#### 3. Điểm Xét Tuyển Cuối Cùng
```
DIEM_XET_TUYEN = diem_thxt + diem_utqd
```

**Ví dụ:**
```
diem_thxt = 7.625
diem_utqd = 0.75 (ưu tiên dân tộc)
DIEM_XET_TUYEN = 7.625 + 0.75 = 8.375
```

---

### Các Hàm Hỗ Trợ (Private)

#### `tinhDiemToHop(...)`
Tính điểm tổ hợp theo công thức trên

#### `tinhDiemCong(...)`
Tính tổng điểm 3 môn (chưa có hệ số)

#### `layDiemUuTien(...)`
Lấy điểm ưu tiên (mở rộng sau)

#### `layDiemMon(DiemThiXettuyen, String tenMon)`
Lấy điểm của một môn cụ thể
```java
// Hỗ trợ các môn: TO, LI, HO, SI, SU, DI, VA, N1, CNCN, CNNN, TI, KTPL
Double diem = layDiemMon(diemThi, "TO");  // Lấy điểm Toán
```

---

### Hàm Mở Rộng

#### `tinhDiemXetTuyenChoAllThiSinh(List<ThiSinhXettuyen>)`
Tính điểm cho tất cả thí sinh hàng loạt

```java
List<ThiSinhXettuyen> allThiSinh = thiSinhDAO.findAll(ThiSinhXettuyen.class);
xetTuyenService.tinhDiemXetTuyenChoAllThiSinh(allThiSinh);

// OUTPUT:
// 📊 Bắt đầu tính điểm xét tuyển cho 1000 thí sinh...
// ✅ Hoàn thành! Thành công: 995, Thất bại: 5
```

#### `resetDiemXetTuyenCuaThiSinh(String cccd)`
Xoá điểm xét tuyển (reset lại để tính lại)

```java
xetTuyenService.resetDiemXetTuyenCuaThiSinh("0123456789");
// ✅ Reset điểm xét tuyển thành công cho: 0123456789
```

---

## 📊 Sơ Đồ Luồng Dữ Liệu

```
┌─────────────────────────────────────────────────────────┐
│                  EXCEL (.xlsx)                           │
│  thisinh.xlsx | diemthi.xlsx | nguyenvong.xlsx          │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
        ┌────────────────────────────┐
        │  ExcelImportService.import()│
        │  - Đọc từng cell            │
        │  - Ép kiểu an toàn         │
        │  - Log lỗi từng dòng        │
        └────────────┬────────────────┘
                     │
                     ↓
    ┌────────────────────────────────────┐
    │  List<Entity>                      │
    │  - List<ThiSinhXettuyen>           │
    │  - List<DiemThiXettuyen>           │
    │  - List<NguyenVongXettuyen>        │
    └────────────┬────────────────────────┘
                 │
                 ↓
    ┌────────────────────────────────────┐
    │  DAO.save() - Lưu từng bản ghi    │
    └────────────┬────────────────────────┘
                 │
                 ↓
      ┌──────────────────────────┐
      │   MySQL DATABASE         │
      │  xt_thisinh              │
      │  xt_diemthi              │
      │  xt_nguyenvong           │
      └──────────────┬───────────┘
                     │
                     ↓
   ┌─────────────────────────────────────┐
   │  XetTuyenService.tinhDiem...()      │
   │  - Lấy dữ liệu từ database         │
   │  - Tính điểm tổ hợp                │
   │  - Tính điểm ưu tiên               │
   │  - Cập nhật kết quả                │
   └─────────────────┬───────────────────┘
                     │
                     ↓
      ┌──────────────────────────┐
      │   MySQL DATABASE         │
      │  xt_nguyenvong           │
      │  UPDATE:                 │
      │  - diem_thxt             │
      │  - diem_utqd             │
      │  - diem_xettuyen         │
      │  - nv_ketqua             │
      └──────────────────────────┘
```

---

## ⚡ Xử Lý Exception

### ExcelImportService
```java
try {
    FileInputStream file = new FileInputStream(filePath);
    Workbook workbook = new XSSFWorkbook(file);
    
    // Xử lý từng dòng trong try-catch riêng
    for (row) {
        try {
            // Xử lý cell
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi dòng " + i + ": " + e.getMessage());
            // Tiếp tục dòng tiếp theo
        }
    }
} catch (IOException e) {
    System.err.println("❌ Lỗi khi đọc file: " + e.getMessage());
}
```

### XetTuyenService
```java
try {
    // Lấy dữ liệu từ dao
    for (nguyenVong) {
        try {
            // Tính toán
        } catch (Exception e) {
            System.err.println("❌ Lỗi nguyện vọng " + ...);
            // Tiếp tục nguyện vọng tiếp theo
        }
    }
} catch (Exception e) {
    System.err.println("❌ Lỗi toàn cục: " + e.getMessage());
}
```

---

## 📦 Maven Dependencies (pom.xml)

```xml
<!-- Apache POI - Đọc/ghi Excel -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.3</version>
</dependency>

<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>

<!-- Log4j - Logging (Optional) -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.20.0</version>
</dependency>
```

---

## 🔄 Quy Trình Hoàn Chỉnh

### Bước 1: Chuẩn Bị File Excel
```
D:\data\
├── thisinh.xlsx       # Header + 100 dòng dữ liệu
├── diemthi.xlsx       # Header + 100 dòng dữ liệu
└── nguyenvong.xlsx    # Header + 300 dòng dữ liệu
```

### Bước 2: Import Excel
```java
ExcelImportService excel = new ExcelImportService();
List<ThiSinhXettuyen> ts = excel.importThiSinh("D:\\data\\thisinh.xlsx");
List<DiemThiXettuyen> dt = excel.importDiemThi("D:\\data\\diemthi.xlsx");
List<NguyenVongXettuyen> nv = excel.importNguyenVong("D:\\data\\nguyenvong.xlsx");
```

### Bước 3: Lưu vào Database
```java
ThiSinhDAO tDAO = DAOFactory.getThiSinhDAO();
DiemThiDAO dtDAO = DAOFactory.getDiemThiDAO();
NguyenVongDAO nvDAO = DAOFactory.getNguyenVongDAO();

for (ThiSinhXettuyen t : ts) tDAO.save(t);
for (DiemThiXettuyen d : dt) dtDAO.save(d);
for (NguyenVongXettuyen n : nv) nvDAO.save(n);
```

### Bước 4: Tính Điểm
```java
XetTuyenService xet = new XetTuyenService(...);
xet.tinhDiemXetTuyenChoAllThiSinh(ts);
```

### Bước 5: Kiểm tra Kết quả
```java
List<NguyenVongXettuyen> result = nvDAO.findByCCCD("0123456789");
for (NguyenVongXettuyen r : result) {
    System.out.println(r.getDiemXettuyen());
}
```

---

## ✅ Best Practices

✅ **Luôn kiểm tra null** trước khi xử lý  
✅ **Log chi tiết** để dễ debug  
✅ **Transaction safety** - rollback khi có lỗi  
✅ **Phân trang** khi import file lớn  
✅ **Validate dữ liệu** trước khi lưu  
✅ **Close resource** (File, Workbook) đúng cách  

---

## 🚀 Chạy ServiceExample

```bash
# Từ terminal
mvn clean compile
mvn exec:java -Dexec.mainClass="com.xettuyen.service.ServiceExample"

# Hoặc chạy trực tiếp từ IDE
Right-click → Run As → Java Application
```

---

**Tác Giả**: Senior Java Developer  
**Pattern**: Service Layer + Excel Import + Business Logic  
**Framework**: Hibernate + Apache POI  
**Ngôn Ngữ**: Java 11+
