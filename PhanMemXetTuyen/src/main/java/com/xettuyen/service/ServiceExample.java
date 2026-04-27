package com.xettuyen.service;

import com.xettuyen.dao.*;
import com.xettuyen.entity.*;
import com.xettuyen.util.HibernateUtil;
import java.util.List;

/**
 * ServiceExample - Ví dụ sử dụng Service Layer
 * Minh hoạ cách sử dụng ExcelImportService và XetTuyenService
 */
public class ServiceExample {
    
    public static void main(String[] args) {
        try {
            // ============== KHỞI TẠO ==============
            System.out.println("🚀 KHỞI ĐỘNG ỨNG DỤNG\n");
            
            // Lấy DAOs từ Factory
            ThiSinhDAO thiSinhDAO = DAOFactory.getThiSinhDAO();
            DiemThiDAO diemThiDAO = DAOFactory.getDiemThiDAO();
            DiemCongDAO diemCongDAO = DAOFactory.getDiemCongDAO();
            NguyenVongDAO nguyenVongDAO = DAOFactory.getNguyenVongDAO();
            BangQuyDoiDAO bangQuyDoiDAO = DAOFactory.getBangQuyDoiDAO();
            NganhToHopDAO nganhToHopDAO = DAOFactory.getNganhToHopDAO();
            
            // ============== IMPORT TỪ EXCEL ==============
            System.out.println("\n📥 BƯỚC 1: IMPORT DỮ LIỆU TỪ EXCEL\n");
            
            ExcelImportService excelService = new ExcelImportService();
            
            // Import thí sinh
            List<ThiSinhXettuyen> thiSinhList = excelService.importThiSinh(
                "D:\\data\\thisinh.xlsx"
            );
            
            // Import điểm thi
            List<DiemThiXettuyen> diemThiList = excelService.importDiemThi(
                "D:\\data\\diemthi.xlsx"
            );
            
            // Import nguyện vọng
            List<NguyenVongXettuyen> nguyenVongList = excelService.importNguyenVong(
                "D:\\data\\nguyenvong.xlsx"
            );
            
            // ============== LƯU VÀO DATABASE ==============
            System.out.println("\n💾 BƯỚC 2: LƯU VÀO DATABASE\n");
            
            // Lưu thí sinh
            System.out.println("Lưu " + thiSinhList.size() + " thí sinh...");
            for (ThiSinhXettuyen ts : thiSinhList) {
                thiSinhDAO.save(ts);
            }
            System.out.println("✅ Lưu thí sinh thành công!");
            
            // Lưu điểm thi
            System.out.println("\nLưu " + diemThiList.size() + " bản ghi điểm thi...");
            for (DiemThiXettuyen dt : diemThiList) {
                diemThiDAO.save(dt);
            }
            System.out.println("✅ Lưu điểm thi thành công!");
            
            // Lưu nguyện vọng
            System.out.println("\nLưu " + nguyenVongList.size() + " nguyện vọng...");
            for (NguyenVongXettuyen nv : nguyenVongList) {
                nguyenVongDAO.save(nv);
            }
            System.out.println("✅ Lưu nguyện vọng thành công!");
            
            // ============== TÍNH ĐIỂM XÉT TUYỂN ==============
            System.out.println("\n📊 BƯỚC 3: TÍNH ĐIỂM XÉT TUYỂN\n");
            
            XetTuyenService xetTuyenService = new XetTuyenService(
                diemThiDAO, diemCongDAO, bangQuyDoiDAO, nguyenVongDAO, nganhToHopDAO
            );
            
            // Cách 1: Tính cho một thí sinh cụ thể
            String cccdThiSinh = "123456789";
            System.out.println("Tính điểm cho thí sinh: " + cccdThiSinh);
            xetTuyenService.tinhDiemXetTuyenCuaThiSinh(cccdThiSinh);
            
            // Cách 2: Tính cho tất cả thí sinh
            System.out.println("\n\nTính điểm cho tất cả thí sinh...");
            xetTuyenService.tinhDiemXetTuyenChoAllThiSinh(thiSinhList);
            
            // ============== TRUY VẤN KẾT QUẢ ==============
            System.out.println("\n\n🔍 BƯỚC 4: TRUY VẤN KẾT QUẢ\n");
            
            // Lấy nguyện vọng của thí sinh
            List<NguyenVongXettuyen> ketQua = nguyenVongDAO.findByCCCD(cccdThiSinh);
            
            System.out.println("Kết quả xét tuyển cho thí sinh " + cccdThiSinh + ":");
            for (NguyenVongXettuyen nv : ketQua) {
                System.out.println(String.format(
                    "  NV %d: %s | Điểm: %.2f | Kết quả: %s",
                    nv.getNvTt(),
                    nv.getNvMaNganh(),
                    nv.getDiemXettuyen() != null ? nv.getDiemXettuyen() : 0,
                    nv.getNvKetqua() != null ? nv.getNvKetqua() : "Chưa có"
                ));
            }
            
            // ============== TÌMKIẾM ==============
            System.out.println("\n\n🔎 BƯỚC 5: TÌMKIẾM\n");
            
            // Tìm kiếm thí sinh
            ThiSinhService thiSinhService = new ThiSinhService();
            ThiSinhService.SearchResult<ThiSinhXettuyen> searchResult = 
                thiSinhService.searchThiSinh("Nguyễn", 1, 10);
            
            System.out.println("Tìm text 'Nguyễn':");
            System.out.println("  Trang: " + searchResult.currentPage);
            System.out.println("  Tổng: " + searchResult.totalRecords + " kết quả");
            System.out.println("  Tổng trang: " + searchResult.totalPages);
            System.out.println("  Kết quả trang 1: " + searchResult.data.size());
            
            // ============== THỐNG KÊ ==============
            System.out.println("\n\n📈 BƯỚC 6: THỐNG KÊ\n");
            
            long totalThiSinh = thiSinhDAO.countAll(ThiSinhXettuyen.class);
            long totalDiemThi = diemThiDAO.countAll(DiemThiXettuyen.class);
            long totalNguyenVong = nguyenVongDAO.countAll(NguyenVongXettuyen.class);
            
            System.out.println("Tổng thí sinh: " + totalThiSinh);
            System.out.println("Tổng bản ghi điểm thi: " + totalDiemThi);
            System.out.println("Tổng nguyện vọng: " + totalNguyenVong);
            
            System.out.println("\n✅ HOÀN THÀNH!");
            
        } catch (Exception e) {
            System.err.println("❌ LỖI: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng SessionFactory
            HibernateUtil.shutdown();
        }
    }
}
