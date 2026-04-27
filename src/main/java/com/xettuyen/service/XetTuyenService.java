package com.xettuyen.service;

import com.xettuyen.dao.*;
import com.xettuyen.entity.*;
import java.util.*;

/**
 * XetTuyenService - Xử lý logic tính điểm xét tuyển
 * Lớp này chứa các quy tắc kinh doanh về tính toán điểm tuyển sinh
 */
public class XetTuyenService {
    private DiemThiDAO diemThiDAO;
    private DiemCongDAO diemCongDAO;
    private BangQuyDoiDAO bangQuyDoiDAO;
    private NguyenVongDAO nguyenVongDAO;
    private NganhToHopDAO nganhToHopDAO;

    /**
     * Constructor - nhận vào các DAO cần thiết
     */
    public XetTuyenService(DiemThiDAO diemThiDAO, DiemCongDAO diemCongDAO,
                          BangQuyDoiDAO bangQuyDoiDAO, NguyenVongDAO nguyenVongDAO,
                          NganhToHopDAO nganhToHopDAO) {
        this.diemThiDAO = diemThiDAO;
        this.diemCongDAO = diemCongDAO;
        this.bangQuyDoiDAO = bangQuyDoiDAO;
        this.nguyenVongDAO = nguyenVongDAO;
        this.nganhToHopDAO = nganhToHopDAO;
    }

    /**
     * Tính điểm xét tuyển cho thí sinh
     * 
     * Quá trình:
     * 1. Lấy thông tin điểm thi của thí sinh
     * 2. Lấy các nguyện vọng của thí sinh
     * 3. Cho mỗi nguyện vọng, tính điểm theo tổ hợp môn của ngành
     * 4. Tính điểm ưu tiên từ bảng quy đổi
     * 5. Update kết quả điểm vào bảng nguyện vọng
     * 
     * @param cccd CCCD của thí sinh cần tính điểm
     */
    public void tinhDiemXetTuyenCuaThiSinh(String cccd) {
        try {
            // Bước 1: Lấy điểm thi của thí sinh
            List<DiemThiXettuyen> diemThiList = diemThiDAO.findByCCCD(cccd);
            if (diemThiList == null || diemThiList.isEmpty()) {
                System.err.println("❌ Không tìm thấy điểm thi cho thí sinh: " + cccd);
                return;
            }
            
            DiemThiXettuyen diemThi = diemThiList.get(0);
            
            // Bước 2: Lấy danh sách nguyện vọng của thí sinh
            List<NguyenVongXettuyen> nguyenVongs = nguyenVongDAO.findByCCCD(cccd);
            if (nguyenVongs == null || nguyenVongs.isEmpty()) {
                System.err.println("⚠️ Thí sinh " + cccd + " không có nguyện vọng nào");
                return;
            }
            
            System.out.println("📊 Bắt đầu tính điểm xét tuyển cho: " + cccd);
            
            // Bước 3: Xử lý từng nguyện vọng
            for (NguyenVongXettuyen nguyenVong : nguyenVongs) {
                try {
                    String maNganh = nguyenVong.getNvMaNganh();
                    
                    // Bước 3.1: Lấy thông tin tổ hợp của ngành
                    String matohop = diemThi.getDPhuongThuc();  // Giả định phương thức chứa tổ hợp
                    
                    NganhTohop nganhToHop = nganhToHopDAO.findByMaNganhAndMaToHop(maNganh, matohop);
                    if (nganhToHop == null) {
                        System.err.println("⚠️ Không tìm thấy tổ hợp cho ngành: " + maNganh + ", tổ: " + matohop);
                        continue;
                    }
                    
                    // Bước 3.2: Tính điểm tổ hợp (diem_thxt)
                    Double diemThxt = tinhDiemToHop(diemThi, nganhToHop);
                    nguyenVong.setDiemThxt(diemThxt);
                    
                    // Bước 3.3: Lấy điểm cộng ưu tiên
                    double diemUtien = layDiemUuTien(diemThi, nganhToHop);
                    nguyenVong.setDiemUtqd(diemUtien);
                    
                    // Bước 3.4: Tính điểm cộng (tổng 3 môn chưa tính môn chính)
                    Double diemCong = tinhDiemCong(diemThi, nganhToHop);
                    nguyenVong.setDiemCong(diemCong);
                    
                    // Bước 3.5: Tính điểm xét tuyển cuối cùng
                    Double diemXettuyen = diemThxt + diemUtien;
                    nguyenVong.setDiemXettuyen(diemXettuyen);
                    
                    // Bước 3.6: Cập nhật kết quả
                    nguyenVongDAO.update(nguyenVong);
                    
                    System.out.println(String.format("  ✓ Nguyện vọng %d | Ngành: %s | Điểm xét tuyển: %.2f",
                            nguyenVong.getNvTt(), maNganh, diemXettuyen));
                    
                } catch (Exception e) {
                    System.err.println("❌ Lỗi khi tính điểm nguyện vọng " + nguyenVong.getNvTt() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("✅ Tính điểm xét tuyển thành công cho thí sinh: " + cccd);
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi toàn cục khi tính điểm xét tuyển: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tính điểm tổ hợp môn (diem_thxt)
     * Công thức: (Mon1 × Hs1 + Mon2 × Hs2 + Mon3 × Hs3) / (Hs1 + Hs2 + Hs3)
     */
    private Double tinhDiemToHop(DiemThiXettuyen diemThi, NganhTohop nganhToHop) {
        try {
            // Lấy các môn và hệ số từ tổ hợp
            String mon1 = nganhToHop.getThMon1();
            Integer hs1 = nganhToHop.getHsMon1() != null ? nganhToHop.getHsMon1() : 1;
            
            String mon2 = nganhToHop.getThMon2();
            Integer hs2 = nganhToHop.getHsMon2() != null ? nganhToHop.getHsMon2() : 1;
            
            String mon3 = nganhToHop.getThMon3();
            Integer hs3 = nganhToHop.getHsMon3() != null ? nganhToHop.getHsMon3() : 1;
            
            // Lấy điểm các môn từ bảng điểm thi
            Double diemMon1 = layDiemMon(diemThi, mon1);
            Double diemMon2 = layDiemMon(diemThi, mon2);
            Double diemMon3 = layDiemMon(diemThi, mon3);
            
            // Tính điểm trung bình có trọng số
            double tong = (diemMon1 * hs1) + (diemMon2 * hs2) + (diemMon3 * hs3);
            double tongHeSo = hs1 + hs2 + hs3;
            double diemTrungBinh = tong / tongHeSo;
            
            return diemTrungBinh;
            
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi tính điểm tổ hợp: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Tính điểm cộng (tổng 3 môn chưa tính hệ số)
     * Được dùng để so sánh bước trung gian
     */
    private Double tinhDiemCong(DiemThiXettuyen diemThi, NganhTohop nganhToHop) {
        try {
            String mon1 = nganhToHop.getThMon1();
            String mon2 = nganhToHop.getThMon2();
            String mon3 = nganhToHop.getThMon3();
            
            Double diemMon1 = layDiemMon(diemThi, mon1);
            Double diemMon2 = layDiemMon(diemThi, mon2);
            Double diemMon3 = layDiemMon(diemThi, mon3);
            
            return diemMon1 + diemMon2 + diemMon3;
            
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi tính điểm cộng: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Lấy điểm ưu tiên từ bảng quy đổi
     * Ưu tiên có thể dựa trên đối tượng tuyển sinh (đối tượng chính sách)
     */
    private double layDiemUuTien(DiemThiXettuyen diemThi, NganhTohop nganhToHop) {
        try {
            // Có thể mở rộng để lấy từ bảng quy đổi theo quy tắc cụ thể
            // Hiện tại - mặc định 0, có thể thêm logic sau
            return 0.0;
            
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi lấy điểm ưu tiên: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Lấy điểm của một môn học cụ thể từ bảng điểm thi
     * @param diemThi Bảng điểm thi
     * @param tenMon Tên môn (TO, LI, HO, SI, SU, DI, VA, v.v.)
     * @return Điểm của môn, mặc định 0.0 nếu không tìm thấy
     */
    private Double layDiemMon(DiemThiXettuyen diemThi, String tenMon) {
        if (tenMon == null || tenMon.trim().isEmpty()) {
            return 0.0;
        }
        
        tenMon = tenMon.toUpperCase().trim();
        
        switch (tenMon) {
            case "TO":
                return diemThi.getTo() != null ? diemThi.getTo() : 0.0;
            case "LI":
                return diemThi.getLi() != null ? diemThi.getLi() : 0.0;
            case "HO":
                return diemThi.getHo() != null ? diemThi.getHo() : 0.0;
            case "SI":
                return diemThi.getSi() != null ? diemThi.getSi() : 0.0;
            case "SU":
                return diemThi.getSu() != null ? diemThi.getSu() : 0.0;
            case "DI":
                return diemThi.getDi() != null ? diemThi.getDi() : 0.0;
            case "VA":
                return diemThi.getVa() != null ? diemThi.getVa() : 0.0;
            case "N1":
                return diemThi.getN1Thi() != null ? diemThi.getN1Thi() : 0.0;
            case "CNCN":
                return diemThi.getCncn() != null ? diemThi.getCncn() : 0.0;
            case "CNNN":
                return diemThi.getCnnn() != null ? diemThi.getCnnn() : 0.0;
            case "TI":
                return diemThi.getTi() != null ? diemThi.getTi() : 0.0;
            case "KTPL":
                return diemThi.getKtpl() != null ? diemThi.getKtpl() : 0.0;
            default:
                System.err.println("⚠️ Môn không xác định: " + tenMon);
                return 0.0;
        }
    }

    /**
     * Tính điểm xét tuyển cho tất cả thí sinh
     * (Dùng khi muốn tính hàng loạt)
     */
    public void tinhDiemXetTuyenChoAllThiSinh(List<ThiSinhXettuyen> thiSinhList) {
        if (thiSinhList == null || thiSinhList.isEmpty()) {
            System.out.println("⚠️ Danh sách thí sinh trống!");
            return;
        }
        
        System.out.println("📊 Bắt đầu tính điểm xét tuyển cho " + thiSinhList.size() + " thí sinh...");
        
        int success = 0;
        int failed = 0;
        
        for (ThiSinhXettuyen thiSinh : thiSinhList) {
            try {
                tinhDiemXetTuyenCuaThiSinh(thiSinh.getCccd());
                success++;
            } catch (Exception e) {
                System.err.println("❌ Tính điểm thất bại cho " + thiSinh.getCccd() + ": " + e.getMessage());
                failed++;
            }
        }
        
        System.out.println("✅ Hoàn thành! Thành công: " + success + ", Thất bại: " + failed);
    }

    /**
     * Xoá điểm xét tuyển của thí sinh (reset lại)
     */
    public void resetDiemXetTuyenCuaThiSinh(String cccd) {
        try {
            List<NguyenVongXettuyen> nguyenVongs = nguyenVongDAO.findByCCCD(cccd);
            
            for (NguyenVongXettuyen nv : nguyenVongs) {
                nv.setDiemThxt(null);
                nv.setDiemUtqd(null);
                nv.setDiemCong(null);
                nv.setDiemXettuyen(null);
                nv.setNvKetqua(null);
                nguyenVongDAO.update(nv);
            }
            
            System.out.println("✅ Reset điểm xét tuyển thành công cho: " + cccd);
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi reset điểm: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
