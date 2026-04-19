package com.xettuyen.service;

import com.xettuyen.dao.*;
import com.xettuyen.entity.*;
import java.util.Date;
import java.util.List;

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
}
