package com.xettuyen.service;

import com.xettuyen.entity.DiemCongXettuyen;
import com.xettuyen.entity.DiemThiXettuyen;
import com.xettuyen.entity.ThiSinhXettuyen;
import com.xettuyen.util.HibernateUtil;
import com.xettuyen.entity.NguyenVongXettuyen;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * ExcelImportService - Xử lý import dữ liệu từ file Excel (.xlsx)
 * Sử dụng Apache POI để đọc file
 */
public class ExcelImportService {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    private static class DsThiSinhRow {
        private String cccd;
        private String ho;
        private String ten;
        private String ngaySinh;
        private String gioiTinh;
        private String doiTuong;
        private String khuVuc;
        private String noiSinh;
        private Double to;
        private Double va;
        private Double li;
        private Double hoMon;
        private Double si;
        private Double su;
        private Double di;
        private Double n1Thi;
        private Double n1Cc;
        private Double cncn;
        private Double cnnn;
        private Double ti;
        private Double ktpl;
        private Double nl1;
        private Double nk1;
        private Double nk2;
    }

    /**
     * Import danh sách thí sinh và điểm thi từ file Excel "Ds thi sinh.xlsx".
     * Dữ liệu sẽ được lưu trong một transaction; nếu có lỗi ở bất kỳ dòng nào thì rollback toàn bộ.
     *
     * @param filePath đường dẫn file Excel
     * @return số dòng dữ liệu đã import thành công
     */
    public int importThiSinhVaDiemThi(String filePath) {
        List<DsThiSinhRow> rows = readDsThiSinhRows(filePath);
        if (rows.isEmpty()) {
            return 0;
        }

        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            int importedRows = 0;
            for (DsThiSinhRow row : rows) {
                ThiSinhXettuyen thiSinh = new ThiSinhXettuyen();
                thiSinh.setCccd(row.cccd);
                thiSinh.setHo(row.ho);
                thiSinh.setTen(row.ten);
                thiSinh.setNgaySinh(row.ngaySinh);
                thiSinh.setGioiTinh(row.gioiTinh);
                thiSinh.setDoiTuong(row.doiTuong);
                thiSinh.setKhuVuc(row.khuVuc);
                thiSinh.setNoiSinh(row.noiSinh);
                session.persist(thiSinh);

                DiemThiXettuyen diemThi = new DiemThiXettuyen();
                diemThi.setCccd(row.cccd);
                diemThi.setTo(row.to);
                diemThi.setVa(row.va);
                diemThi.setLi(row.li);
                diemThi.setHo(row.hoMon);
                diemThi.setSi(row.si);
                diemThi.setSu(row.su);
                diemThi.setDi(row.di);
                diemThi.setN1Thi(row.n1Thi);
                diemThi.setN1Cc(row.n1Cc);
                diemThi.setCncn(row.cncn);
                diemThi.setCnnn(row.cnnn);
                diemThi.setTi(row.ti);
                diemThi.setKtpl(row.ktpl);
                diemThi.setNl1(row.nl1);
                diemThi.setNk1(row.nk1);
                diemThi.setNk2(row.nk2);
                session.persist(diemThi);

                importedRows++;
            }

            transaction.commit();
            return importedRows;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Lỗi khi import Excel, dữ liệu đã được rollback: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Import danh sách thí sinh từ file Excel
     * Định dạng Excel: [CCCD] [SoBaoDanh] [Ho] [Ten] [NgaySinh] [DienThoai] [GioiTinh] [Email] [NoiSinh] [DoiTuong] [KhuVuc]
     *
     * @param filePath Đường dẫn đến file Excel
     * @return Danh sách thí sinh đã import
     */
    public List<ThiSinhXettuyen> importThiSinh(String filePath) {
        List<ThiSinhXettuyen> thiSinhList = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = 0;

            // Bỏ qua dòng header (dòng 0)
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);

                // Bỏ qua row null hoặc row trống
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                try {
                    ThiSinhXettuyen thiSinh = new ThiSinhXettuyen();

                    // Đọc từng cell - kiểm tra null và ép kiểu an toàn
                    thiSinh.setCccd(getCellValueAsString(row, 0));
                    thiSinh.setSoBaoDanh(getCellValueAsString(row, 1));
                    thiSinh.setHo(getCellValueAsString(row, 2));
                    thiSinh.setTen(getCellValueAsString(row, 3));
                    thiSinh.setNgaySinh(getCellValueAsString(row, 4));
                    thiSinh.setDienThoai(getCellValueAsString(row, 5));
                    thiSinh.setGioiTinh(getCellValueAsString(row, 6));
                    thiSinh.setEmail(getCellValueAsString(row, 7));
                    thiSinh.setNoiSinh(getCellValueAsString(row, 8));
                    thiSinh.setDoiTuong(getCellValueAsString(row, 9));
                    thiSinh.setKhuVuc(getCellValueAsString(row, 10));

                    thiSinhList.add(thiSinh);
                    rowCount++;

                } catch (Exception e) {
                    // Log lỗi nhưng tiếp tục đọc dòng tiếp theo
                    System.err.println("⚠️ Lỗi khi xử lý dòng " + (i + 1) + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("✅ Import thành công " + rowCount + " thí sinh từ " + filePath);

        } catch (IOException e) {
            System.err.println("❌ Lỗi khi đọc file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Lỗi không xác định khi import thí sinh: " + e.getMessage());
            e.printStackTrace();
        }

        return thiSinhList;
    }

    private List<DsThiSinhRow> readDsThiSinhRows(String filePath) {
        List<DsThiSinhRow> rows = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                DsThiSinhRow data = new DsThiSinhRow();
                data.cccd = getCellValueAsString(row, 1);
                if (data.cccd == null || data.cccd.trim().isEmpty()) {
                    throw new IllegalArgumentException("Dòng " + (i + 1) + " thiếu CCCD");
                }

                String[] nameParts = splitFullName(getCellValueAsString(row, 2));
                data.ho = nameParts[0];
                data.ten = nameParts[1];
                data.ngaySinh = getCellValueAsString(row, 3);
                data.gioiTinh = getCellValueAsString(row, 4);
                data.doiTuong = getCellValueAsString(row, 5);
                data.khuVuc = getCellValueAsString(row, 6);
                data.noiSinh = getCellValueAsString(row, 35);

                data.to = getCellValueAsDouble(row, 7);
                data.va = getCellValueAsDouble(row, 8);
                data.li = getCellValueAsDouble(row, 9);
                data.hoMon = getCellValueAsDouble(row, 10);
                data.si = getCellValueAsDouble(row, 11);
                data.su = getCellValueAsDouble(row, 12);
                data.di = getCellValueAsDouble(row, 13);
                data.n1Thi = getCellValueAsDouble(row, 15);
                data.n1Cc = data.n1Thi;
                data.ktpl = getCellValueAsDouble(row, 17);
                data.ti = getCellValueAsDouble(row, 18);
                data.cncn = getCellValueAsDouble(row, 19);
                data.cnnn = getCellValueAsDouble(row, 20);
                data.nl1 = getCellValueAsDouble(row, 32);
                data.nk1 = getCellValueAsDouble(row, 22);
                data.nk2 = getCellValueAsDouble(row, 23);

                rows.add(data);
            }

            return rows;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage(), e);
        }
    }

    private String[] splitFullName(String fullName) {
        String normalized = fullName == null ? "" : fullName.trim().replaceAll("\\s+", " ");
        if (normalized.isEmpty()) {
            return new String[] {"", ""};
        }

        int lastSpaceIndex = normalized.lastIndexOf(' ');
        if (lastSpaceIndex < 0) {
            return new String[] {"", normalized};
        }

        return new String[] {
            normalized.substring(0, lastSpaceIndex).trim(),
            normalized.substring(lastSpaceIndex + 1).trim()
        };
    }

    /**
     * Import danh sách điểm thi từ file Excel
     * Định dạng Excel: [CCCD] [SoBaoDanh] [Toan] [Ly] [Hoa] [Sinh] [SuAn] [DiaLi] [VanAn] [N1-Thi] [CNCN] [CNNN] [TI] [KTPL]
     *
     * @param filePath Đường dẫn đến file Excel
     * @return Danh sách điểm thi đã import
     */
    public java.util.List<com.xettuyen.entity.DiemThiXettuyen> importDiemThi(String filePath) {
        List<com.xettuyen.entity.DiemThiXettuyen> diemThiList = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = 0;

            // Bỏ qua dòng header (dòng 0)
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                try {
                    com.xettuyen.entity.DiemThiXettuyen diemThi = new com.xettuyen.entity.DiemThiXettuyen();

                    diemThi.setIdDiemThi(getCellValueAsInteger(row, 0));
                    diemThi.setCccd(getCellValueAsString(row, 1));

                    // Các môn thi (kiểu numeric - Double)
                    diemThi.setTo(getCellValueAsDouble(row, 7));      // Toán
                    diemThi.setVa(getCellValueAsDouble(row, 8));      // Văn
                    diemThi.setLi(getCellValueAsDouble(row, 9));      // Lý
                    diemThi.setHo(getCellValueAsDouble(row, 10));      // Hoá
                    diemThi.setSi(getCellValueAsDouble(row, 11));      // Sinh
                    diemThi.setSu(getCellValueAsDouble(row, 12));      // Sử
                    diemThi.setDi(getCellValueAsDouble(row, 13));      // Địa


                    // Các cột khác
                    diemThi.setN1Thi(getCellValueAsDouble(row, 15));   // N1-Thi
                    // diemThi.setLoaiChungChi(getCellValueAsString(row, 16));
                    diemThi.setCncn(getCellValueAsDouble(row, 19));   // CNCN
                    diemThi.setCnnn(getCellValueAsDouble(row, 20));   // CNNN
                    diemThi.setTi(getCellValueAsDouble(row, 18));     // TI
                    diemThi.setKtpl(getCellValueAsDouble(row, 17));   // KTPL

                    diemThi.setNk1(getCellValueAsDouble(row, 22));
                    diemThi.setNk2(getCellValueAsDouble(row, 23));
                    diemThi.setDPhuongThuc("4");
                    diemThiList.add(diemThi);
                    rowCount++;

                } catch (Exception e) {
                    System.err.println("⚠️ Lỗi khi xử lý dòng " + (i + 1) + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("✅ Import thành công " + rowCount + " bản ghi điểm thi từ " + filePath);

        } catch (IOException e) {
            System.err.println("❌ Lỗi khi đọc file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Lỗi không xác định khi import điểm thi: " + e.getMessage());
            e.printStackTrace();
        }

        return diemThiList;
    }

    public java.util.List<com.xettuyen.entity.DiemCongXettuyen> importDiemCong(String filePath) {
        List<com.xettuyen.entity.DiemCongXettuyen> diemCongList = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(1);
            int rowCount = 0;

            // Bỏ qua dòng header (dòng 0)
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                try {
                    com.xettuyen.entity.DiemCongXettuyen diemCong = new com.xettuyen.entity.DiemCongXettuyen();

                    diemCong.setTsCccd(getCellValueAsString(row, 1));
                    //diemCong.setMaNganh(getCellValueAsString(row, 6));
                    //diemCong.setMaTohop(getCellValueAsString(row, 5));
                    diemCong.setPhuongThuc(getCellValueAsString(row, 4));
                    diemCong.setDiemCc(getCellValueAsDouble(row, 7));
                    diemCong.setDiemUtxt(getCellValueAsDouble(row, 8));
                    double d1 = (diemCong.getDiemCc() != null) ? diemCong.getDiemCc() : 0;
                    double d2 = (diemCong.getDiemUtxt() != null) ? diemCong.getDiemUtxt() : 0;
                    diemCong.setDiemTong(d1 + d2);
                    diemCong.setGhiChu(getCellValueAsString(row, 2) + " - " + getCellValueAsString(row, 3));
                    diemCong.setDcKeys(diemCong.getTsCccd() + "_" + diemCong.getMaNganh() + "_" + diemCong.getMaTohop());

                    diemCongList.add(diemCong);
                    rowCount++;

                } catch (Exception e) {
                    System.err.println("⚠️ Lỗi khi xử lý dòng " + (i + 1) + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("✅ Import thành công " + rowCount + " bản ghi điểm cộng từ " + filePath);

        } catch (IOException e) {
            System.err.println("❌ Lỗi khi đọc file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Lỗi không xác định khi import điểm cộng: " + e.getMessage());
            e.printStackTrace();
        }

        return diemCongList;
    }

    /**
     * Import danh sách nguyện vọng từ file Excel
     * Định dạng Excel: [CCCD] [MaNganh] [ThuTu] [PhuongThuc] [Ghi Chu]
     *
     * @param filePath Đường dẫn đến file Excel
     * @return Danh sách nguyện vọng đã import
     */
    public List<NguyenVongXettuyen> importNguyenVong(String filePath) {
        List<NguyenVongXettuyen> nguyenVongList = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(file)) {
            // ✅ Chỉ lấy 2 sheet cần thiết theo tên
            String[] sheetNames = {"Sheet1", "Sheet2"};
            for (String sheetName : sheetNames) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    System.err.println("⚠️ Không tìm thấy sheet: " + sheetName + " — bỏ qua.");
                    continue;
                }
                System.out.println("📄 Đang xử lý sheet: " + sheetName);
                int rowCount = 0;
                for (int i = 5; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null || isRowEmpty(row)) continue;
                    String firstCell = getCellValueAsString(row, 0);
                    if (firstCell.contains("Tổng") || firstCell.contains("Cộng")) break;
                    try {
                        NguyenVongXettuyen nguyenVong = new NguyenVongXettuyen();
                        String cccd = getCellValueAsString(row, 1);
                        String maNganh = getCellValueAsString(row, 5);
                        Integer nvTt = getCellValueAsInteger(row, 2);
                        String nvTuyenThang = getCellValueAsString(row, 7);
                        if (cccd.isEmpty() || maNganh.isEmpty() || nvTt == null || nvTt == 0) {
                            System.err.println("⚠️ [" + sheetName + "] Bỏ qua dòng " + (i + 1) + ": thiếu dữ liệu bắt buộc");
                            continue;
                        }
                        nguyenVong.setNnCccd(cccd);
                        nguyenVong.setNvMaNganh(maNganh);
                        nguyenVong.setNvTt(nvTt);
                        nguyenVong.setDiemThxt(null);
                        nguyenVong.setDiemUtqd(null);
                        nguyenVong.setDiemCong(null);
                        nguyenVong.setDiemXettuyen(null);
                        nguyenVong.setNvKetqua("Chờ xét");
                        nguyenVong.setNvKeys(cccd + "_" + maNganh + "_" + (nvTuyenThang.isEmpty() ? "PT4" : "PT1"));
                        nguyenVong.setTtPhuongThuc(nvTuyenThang.isEmpty() ? "PT4" : "PT1");
                        nguyenVong.setTtThm(null);
                        nguyenVongList.add(nguyenVong);
                        rowCount++;
                    } catch (Exception e) {
                        System.err.println("⚠️ [" + sheetName + "] Lỗi tại dòng " + (i + 1) + ": " + e.getMessage());
                    }
                }
                System.out.println("✅ [" + sheetName + "] Đọc được " + rowCount + " nguyện vọng.");
            }
            System.out.println("🎯 Tổng cộng: " + nguyenVongList.size() + " nguyện vọng từ tất cả sheet.");
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi đọc file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Lỗi không xác định khi import nguyện vọng: " + e.getMessage());
            e.printStackTrace();
        }
        return nguyenVongList;
    }

    /**
     * Kiểm tra xem row có toàn bộ cell trống hay không
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) return true;

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * Lấy giá trị cell dưới dạng String an toàn
     */
    private String getCellValueAsString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);

        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getRichStringCellValue().getString().trim();
            case NUMERIC:
                // Nếu là số, chuyển về string
                if (DateUtil.isCellDateFormatted(cell)) {
                    return dateFormat.format(cell.getDateCellValue());
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * Lấy giá trị cell dưới dạng Double an toàn
     */
    private Double getCellValueAsDouble(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);

        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case STRING:
                    String value = cell.getStringCellValue().trim();
                    return value.isEmpty() ? 0.0 : Double.parseDouble(value);
                case BOOLEAN:
                    return cell.getBooleanCellValue() ? 1.0 : 0.0;
                default:
                    return 0.0;
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Không thể chuyển '" + cell + "' sang kiểu Double, sử dụng giá trị mặc định 0.0");
            return 0.0;
        }
    }

    /**
     * Lấy giá trị cell dưới dạng Integer an toàn
     */
    private Integer getCellValueAsInteger(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);

        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return 0;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (int) cell.getNumericCellValue();
                case STRING:
                    String value = cell.getStringCellValue().trim();
                    return value.isEmpty() ? 0 : Integer.parseInt(value);
                case BOOLEAN:
                    return cell.getBooleanCellValue() ? 1 : 0;
                default:
                    return 0;
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Không thể chuyển '" + cell + "' sang kiểu Integer, sử dụng giá trị mặc định 0");
            return 0;
        }
    }
}
