package com.xettuyen.service;

import com.xettuyen.entity.DiemCongXettuyen;
import com.xettuyen.entity.ThiSinhXettuyen;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ExcelImportService - Xử lý import dữ liệu từ file Excel (.xlsx)
 * Sử dụng Apache POI để đọc file
 */
public class ExcelImportService {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

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
                    
                    diemThi.setCccd(getCellValueAsString(row, 0));
                    diemThi.setSoBaoDanh(getCellValueAsString(row, 1));
                    
                    // Các môn thi (kiểu numeric - Double)
                    diemThi.setTo(getCellValueAsDouble(row, 2));      // Toán
                    diemThi.setLi(getCellValueAsDouble(row, 3));      // Lý
                    diemThi.setHo(getCellValueAsDouble(row, 4));      // Hoá
                    diemThi.setSi(getCellValueAsDouble(row, 5));      // Sinh
                    diemThi.setSu(getCellValueAsDouble(row, 6));      // Sử
                    diemThi.setDi(getCellValueAsDouble(row, 7));      // Địa
                    diemThi.setVa(getCellValueAsDouble(row, 8));      // Văn
                    
                    // Các cột khác
                    diemThi.setN1Thi(getCellValueAsDouble(row, 9));   // N1-Thi
                    diemThi.setCncn(getCellValueAsDouble(row, 10));   // CNCN
                    diemThi.setCnnn(getCellValueAsDouble(row, 11));   // CNNN
                    diemThi.setTi(getCellValueAsDouble(row, 12));     // TI
                    diemThi.setKtpl(getCellValueAsDouble(row, 13));   // KTPL
                    
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
            
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = 0;
            
            // Bỏ qua dòng header (dòng 0)
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                
                if (row == null || isRowEmpty(row)) {
                    continue;
                }
                
                try {
                    com.xettuyen.entity.DiemCongXettuyen diemCong = new com.xettuyen.entity.DiemCongXettuyen();
                    
                    diemCong.setTsCccd(getCellValueAsString(row, 0));
                    diemCong.setMaNganh(getCellValueAsString(row, 1));
                    diemCong.setMaTohop(getCellValueAsString(row, 2));
                    diemCong.setPhuongThuc(getCellValueAsString(row, 3));
                    diemCong.setDiemCc(getCellValueAsDouble(row, 4));
                    diemCong.setDiemUtxt(getCellValueAsDouble(row, 5));
                    diemCong.setDiemTong(diemCong.getDiemCc() + diemCong.getDiemUtxt());
                    diemCong.setDcKeys(diemCong.getTsCccd() + "_" + diemCong.getMaNganh());
                    
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
    public java.util.List<com.xettuyen.entity.NguyenVongXettuyen> importNguyenVong(String filePath) {
        List<com.xettuyen.entity.NguyenVongXettuyen> nguyenVongList = new ArrayList<>();
        
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
                    com.xettuyen.entity.NguyenVongXettuyen nguyenVong = new com.xettuyen.entity.NguyenVongXettuyen();
                    
                    nguyenVong.setNnCccd(getCellValueAsString(row, 0));        // CCCD
                    nguyenVong.setNvMaNganh(getCellValueAsString(row, 1));     // Mã ngành
                    nguyenVong.setNvTt(getCellValueAsInteger(row, 2));         // Thứ tự
                    nguyenVong.setTtPhuongThuc(getCellValueAsString(row, 3));  // Phương thức
                    nguyenVong.setTtThm(getCellValueAsString(row, 4));         // Ghi chú
                    
                    // Tạo key duy nhất nếu chưa có
                    if (nguyenVong.getNvKeys() == null || nguyenVong.getNvKeys().isEmpty()) {
                        nguyenVong.setNvKeys(nguyenVong.getNnCccd() + "_" + nguyenVong.getNvTt());
                    }
                    
                    nguyenVongList.add(nguyenVong);
                    rowCount++;
                    
                } catch (Exception e) {
                    System.err.println("⚠️ Lỗi khi xử lý dòng " + (i + 1) + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("✅ Import thành công " + rowCount + " nguyện vọng từ " + filePath);
            
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
            return 0.0;
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
