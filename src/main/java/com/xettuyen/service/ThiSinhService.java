package com.xettuyen.service;

import com.xettuyen.dao.*;
import com.xettuyen.entity.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ThiSinhService - Service Layer (Business Logic)
 * Tầng này gọi DAO để truy cập dữ liệu
 * Thích hợp cho các quy tắc kinh doanh phức tạp
 */
public class ThiSinhService {
    private ThiSinhDAO thiSinhDAO = DAOFactory.getThiSinhDAO();
    private DiemThiDAO diemThiDAO = DAOFactory.getDiemThiDAO();
    private DiemCongDAO diemCongDAO = DAOFactory.getDiemCongDAO();
    private NguyenVongDAO nguyenVongDAO = DAOFactory.getNguyenVongDAO();

    /**
     * Lấy thông tin đầy đủ của thí sinh (bao gồm điểm, nguyện vọng)
     */
    public ThiSinhInfo getThiSinhFullInfo(String cccd) {
        ThiSinhXettuyen thiSinh = thiSinhDAO.findByCCCD(cccd);
        if (thiSinh == null) {
            return null;
        }

        List<DiemThiXettuyen> diemThi = diemThiDAO.findByCCCD(cccd);
        List<DiemCongXettuyen> diemCong = diemCongDAO.findByCCCD(cccd);
        List<NguyenVongXettuyen> nguyenVongs = nguyenVongDAO.findByCCCD(cccd);

        return new ThiSinhInfo(thiSinh, diemThi, diemCong, nguyenVongs);
    }

    /**
     * Tạo thí sinh mới
     */
    public void createThiSinh(String cccd, String ho, String ten, String email) {
        ThiSinhXettuyen thiSinh = new ThiSinhXettuyen();
        thiSinh.setCccd(cccd);
        thiSinh.setHo(ho);
        thiSinh.setTen(ten);
        thiSinh.setEmail(email);
        thiSinhDAO.save(thiSinh);
    }

    /**
     * Cập nhật email thí sinh
     */
    public void updateThiSinhEmail(String cccd, String newEmail) {
        ThiSinhXettuyen thiSinh = thiSinhDAO.findByCCCD(cccd);
        if (thiSinh != null) {
            thiSinh.setEmail(newEmail);
            thiSinhDAO.update(thiSinh);
        }
    }

    /**
     * Cập nhật thông tin hồ sơ của thí sinh theo ID.
     * ID là khóa định danh và không được phép thay đổi qua form chỉnh sửa.
     */
    public void updateThiSinhInfo(Integer idThiSinh, ThiSinhXettuyen updatedThiSinh) {
        ThiSinhXettuyen thiSinh = thiSinhDAO.findById(ThiSinhXettuyen.class, idThiSinh);
        if (thiSinh == null || updatedThiSinh == null) {
            return;
        }

        thiSinh.setCccd(updatedThiSinh.getCccd());
        thiSinh.setSoBaoDanh(updatedThiSinh.getSoBaoDanh());
        thiSinh.setHo(updatedThiSinh.getHo());
        thiSinh.setTen(updatedThiSinh.getTen());
        thiSinh.setNgaySinh(updatedThiSinh.getNgaySinh());
        thiSinh.setDienThoai(updatedThiSinh.getDienThoai());
        thiSinh.setGioiTinh(updatedThiSinh.getGioiTinh());
        thiSinh.setEmail(updatedThiSinh.getEmail());
        thiSinh.setNoiSinh(updatedThiSinh.getNoiSinh());
        thiSinh.setDoiTuong(updatedThiSinh.getDoiTuong());
        thiSinh.setKhuVuc(updatedThiSinh.getKhuVuc());
        thiSinh.setUpdatedAt(new Date());

        thiSinhDAO.update(thiSinh);
    }

    /**
     * Tìm kiếm thí sinh với phân trang (ứng dụng UI)
     */
    public SearchResult<ThiSinhXettuyen> searchThiSinh(String keyword, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        List<ThiSinhXettuyen> data = thiSinhDAO.searchByKeyword(keyword, offset, pageSize);
        long totalRecords = thiSinhDAO.countSearchResult(keyword);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        return new SearchResult<>(data, pageNumber, pageSize, totalRecords, totalPages);
    }

    /**
     * Xóa thí sinh
     */
    public void deleteThiSinh(String cccd) {
        ThiSinhXettuyen thiSinh = thiSinhDAO.findByCCCD(cccd);
        if (thiSinh != null) {
            thiSinhDAO.delete(thiSinh);
        }
    }

    /**
     * Lấy dữ liệu dashboard cho HomePanel
     */
    public HomeDashboardData getHomeDashboardData() {
        long totalThiSinh = thiSinhDAO.countAll(ThiSinhXettuyen.class);
        Map<String, Long> byDoiTuong = thiSinhDAO.countByDoiTuong();
        Map<String, Long> byKhuVuc = thiSinhDAO.countByKhuVuc();
        return new HomeDashboardData(totalThiSinh, byDoiTuong, byKhuVuc);
    }

    /**
     * Tìm kiếm danh sách thí sinh hiển thị nhanh trên HomePanel
     */
    public List<ThiSinhXettuyen> searchThiSinhForHome(String keyword, int limit) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        return thiSinhDAO.searchForHome(safeKeyword, limit);
    }

    /**
     * Lấy chi tiết 01 thí sinh kèm điểm theo phương thức
     */
    public CandidateDetail getCandidateDetail(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        String normalized = keyword.trim();
        ThiSinhXettuyen thiSinh = thiSinhDAO.findByCCCD(normalized);
        if (thiSinh == null) {
            thiSinh = thiSinhDAO.findBySoBaoDanh(normalized);
        }
        if (thiSinh == null) {
            List<ThiSinhXettuyen> quickMatches = thiSinhDAO.searchForHome(normalized, 1);
            if (!quickMatches.isEmpty()) {
                thiSinh = quickMatches.get(0);
            }
        }
        if (thiSinh == null) {
            return null;
        }

        List<DiemThiXettuyen> allScores = diemThiDAO.findByCCCD(thiSinh.getCccd());
        Map<String, List<DiemThiXettuyen>> groupedScores = new LinkedHashMap<>();
        groupedScores.put("THPT", new java.util.ArrayList<>());
        groupedScores.put("DGNL", new java.util.ArrayList<>());
        groupedScores.put("VSAT", new java.util.ArrayList<>());

        for (DiemThiXettuyen score : allScores) {
            String method = score.getDPhuongThuc();
            if ("4".equals(method)) {
                groupedScores.get("THPT").add(score);
            } else if ("2".equals(method)) {
                groupedScores.get("DGNL").add(score);
            } else if ("3".equals(method)) {
                groupedScores.get("VSAT").add(score);
            }
        }

        return new CandidateDetail(thiSinh,
                groupedScores.get("THPT"),
                groupedScores.get("DGNL"),
                groupedScores.get("VSAT"));
    }

    /**
     * Inner class - Lưu thông tin toàn bộ thí sinh
     */
    public static class ThiSinhInfo {
        public ThiSinhXettuyen thiSinh;
        public List<DiemThiXettuyen> diemThi;
        public List<DiemCongXettuyen> diemCong;
        public List<NguyenVongXettuyen> nguyenVongs;

        public ThiSinhInfo(ThiSinhXettuyen thiSinh, List<DiemThiXettuyen> diemThi,
                List<DiemCongXettuyen> diemCong, List<NguyenVongXettuyen> nguyenVongs) {
            this.thiSinh = thiSinh;
            this.diemThi = diemThi;
            this.diemCong = diemCong;
            this.nguyenVongs = nguyenVongs;
        }
    }

    /**
     * Generic SearchResult class - dùng cho phân trang bất kỳ Entity nào
     */
    public static class SearchResult<T> {
        public List<T> data;
        public int currentPage;
        public int pageSize;
        public long totalRecords;
        public int totalPages;

        public SearchResult(List<T> data, int currentPage, int pageSize, long totalRecords, int totalPages) {
            this.data = data;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
            this.totalRecords = totalRecords;
            this.totalPages = totalPages;
        }
    }

    public static class HomeDashboardData {
        public long totalThiSinh;
        public Map<String, Long> byDoiTuong;
        public Map<String, Long> byKhuVuc;

        public HomeDashboardData(long totalThiSinh, Map<String, Long> byDoiTuong, Map<String, Long> byKhuVuc) {
            this.totalThiSinh = totalThiSinh;
            this.byDoiTuong = byDoiTuong;
            this.byKhuVuc = byKhuVuc;
        }
    }

    public static class CandidateDetail {
        public ThiSinhXettuyen thiSinh;
        public List<DiemThiXettuyen> thptScores;
        public List<DiemThiXettuyen> dgnlScores;
        public List<DiemThiXettuyen> vsatScores;

        public CandidateDetail(ThiSinhXettuyen thiSinh,
                List<DiemThiXettuyen> thptScores,
                List<DiemThiXettuyen> dgnlScores,
                List<DiemThiXettuyen> vsatScores) {
            this.thiSinh = thiSinh;
            this.thptScores = thptScores;
            this.dgnlScores = dgnlScores;
            this.vsatScores = vsatScores;
        }
    }
}
