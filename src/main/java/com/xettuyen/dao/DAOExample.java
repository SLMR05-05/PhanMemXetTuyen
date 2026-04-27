package com.xettuyen.dao;

import com.xettuyen.entity.*;
import java.util.List;

/**
 * DAOExample - Ví dụ sử dụng DAO Layer
 * Minh hoạ cách sử dụng các DAO classes trong ứng dụng
 */
public class DAOExample {
    
    public static void main(String[] args) {
        // ============== THÍSINHXETTUYEN ==============
        System.out.println("===== ThiSinh DAO Examples =====");
        ThiSinhDAO thiSinhDAO = DAOFactory.getThiSinhDAO();
        
        // Ví dụ 1: Tìm thí sinh theo CCCD
        ThiSinhXettuyen thiSinh = thiSinhDAO.findByCCCD("123456789");
        if (thiSinh != null) {
            System.out.println("Tìm được: " + thiSinh.getTen());
        }
        
        // Ví dụ 2: Tìm kiếm thí sinh theo keyword (có phân trang)
        List<ThiSinhXettuyen> searchResults = thiSinhDAO.searchByKeyword("Nguyễn", 0, 10);
        System.out.println("Tìm được " + searchResults.size() + " kết quả");
        
        // Ví dụ 3: Lấy tất cả thí sinh
        List<ThiSinhXettuyen> allThiSinh = thiSinhDAO.findAll(ThiSinhXettuyen.class);
        System.out.println("Tổng thí sinh: " + allThiSinh.size());
        
        // ============== NGANH ==============
        System.out.println("\n===== Nganh DAO Examples =====");
        NganhDAO nganhDAO = DAOFactory.getNganhDAO();
        
        // Tìm ngành theo mã
        Nganh nganh = nganhDAO.findByMaNganh("CNTT");
        if (nganh != null) {
            System.out.println("Tên ngành: " + nganh.getTenNganh());
        }
        
        // ============== DIEMTHI ==============
        System.out.println("\n===== DiemThi DAO Examples =====");
        DiemThiDAO diemThiDAO = DAOFactory.getDiemThiDAO();
        
        // Lấy điểm thi của thí sinh
        List<DiemThiXettuyen> diemThi = diemThiDAO.findByCCCD("123456789");
        System.out.println("Thí sinh có " + diemThi.size() + " bản ghi điểm thi");
        
        // ============== DIEMCONG ==============
        System.out.println("\n===== DiemCong DAO Examples =====");
        DiemCongDAO diemCongDAO = DAOFactory.getDiemCongDAO();
        
        // Lấy điểm cộng của thí sinh
        List<DiemCongXettuyen> diemCong = diemCongDAO.findByCCCD("123456789");
        System.out.println("Thí sinh có " + diemCong.size() + " bản ghi điểm cộng");
        
        // ============== NGUYENVONG ==============
        System.out.println("\n===== NguyenVong DAO Examples =====");
        NguyenVongDAO nguyenVongDAO = DAOFactory.getNguyenVongDAO();
        
        // Lấy nguyện vọng của thí sinh (sắp xếp theo thứ tự)
        List<NguyenVongXettuyen> nguyenVongs = nguyenVongDAO.findByCCCD("123456789");
        System.out.println("Thí sinh có " + nguyenVongs.size() + " nguyện vọng");
        
        // ============== NGANHOHOP ==============
        System.out.println("\n===== NganhToHop DAO Examples =====");
        NganhToHopDAO nganhToHopDAO = DAOFactory.getNganhToHopDAO();
        
        // Tìm mối quan hệ ngành - tổ hợp
        NganhTohop nganhToHop = nganhToHopDAO.findByMaNganhAndMaToHop("CNTT", "A00");
        if (nganhToHop != null) {
            System.out.println("Mối quan hệ tìm được!");
        }
        
        // ============== CRUD OPERATIONS ==============
        System.out.println("\n===== CRUD Operations Examples =====");
        
        // Ví dụ: Tạo mới thí sinh
        ThiSinhXettuyen newThiSinh = new ThiSinhXettuyen();
        newThiSinh.setCccd("987654321");
        newThiSinh.setHo("Trần");
        newThiSinh.setTen("Minh");
        newThiSinh.setEmail("minh@example.com");
        // thiSinhDAO.save(newThiSinh);
        // System.out.println("Thêm thí sinh mới thành công!");
        
        // Lấy thí sinh vừa thêm theo ID
        ThiSinhXettuyen retrieved = thiSinhDAO.findById(ThiSinhXettuyen.class, 1);
        if (retrieved != null) {
            System.out.println("Lấy thí sinh theo ID: " + retrieved.getTen());
        }
        
        // ============== PAGINATION ==============
        System.out.println("\n===== Pagination Examples =====");
        
        // Lấy 10 thí sinh đầu tiên
        List<ThiSinhXettuyen> page1 = thiSinhDAO.findAllWithPagination(ThiSinhXettuyen.class, 0, 10);
        System.out.println("Trang 1: " + page1.size() + " bản ghi");
        
        // Tổng số thí sinh
        long totalThiSinh = thiSinhDAO.countAll(ThiSinhXettuyen.class);
        System.out.println("Tổng cộng: " + totalThiSinh + " thí sinh");
        
        // Đóng SessionFactory khi kết thúc
        // HibernateUtil.shutdown();
    }
}
