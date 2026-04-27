package com.xettuyen.service;

import com.xettuyen.dao.*;
import com.xettuyen.entity.*;
import java.util.List;

/**
 * NganhService - Service Layer xử lý nghiệp vụ cho Ngành
 */
public class NganhService {
    private NganhDAO nganhDAO = DAOFactory.getNganhDAO();
    private NganhToHopDAO nganhToHopDAO = DAOFactory.getNganhToHopDAO();

    /**
     * Lấy chi tiết ngành kèm danh sách tổ hợp
     */
    public NganhDetail getNganhDetail(String maNganh) {
        Nganh nganh = nganhDAO.findByMaNganh(maNganh);
        if (nganh == null) return null;

        List<NganhTohop> allNganhToHop = nganhToHopDAO.findAll(NganhTohop.class);
        List<NganhTohop> filteredList = allNganhToHop.stream()
            .filter(nt -> nt.getMaNganh().equals(maNganh))
            .toList();

        return new NganhDetail(nganh, filteredList);
    }

    /**
     * LƯU MỚI: Dùng cho NganhAddDialog
     */
    public boolean saveNganh(Nganh nganh) {
        try {
            if (nganh != null) {
                nganhDAO.save(nganh);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu ngành mới: " + e.getMessage());
            return false;
        }
    }

    /**
     * CẬP NHẬT: Dùng cho NganhEditDialog
     */
    public boolean updateNganh(Nganh nganh) {
        try {
            if (nganh != null && nganh.getIdNganh() != null) {
                nganhDAO.update(nganh); 
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật ngành: " + e.getMessage());
            return false;
        }
    }

    /**
     * XÓA: Dùng cho NganhPanel (icon thùng rác)
     */
    public boolean deleteNganh(Integer idNganh) {
        try {
            Nganh nganh = nganhDAO.findById(Nganh.class, idNganh);
            if (nganh != null) {
                nganhDAO.delete(nganh);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa ngành: " + e.getMessage());
            return false;
        }
    }

    /**
     * PHÂN TRANG: Lấy danh sách ngành theo trang
     */
    public List<Nganh> getNganhWithPagination(int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        return nganhDAO.findAllWithPagination(Nganh.class, offset, pageSize);
    }

    /**
     * ĐẾM: Lấy tổng số lượng ngành trong DB
     */
    public long getTotalNganh() {
        return nganhDAO.countAll(Nganh.class);
    }

    /**
     * Inner class - Cấu trúc dữ liệu trả về cho Dialog
     */
    public static class NganhDetail {
        public Nganh nganh;
        public List<NganhTohop> nganhToHopList;

        public NganhDetail(Nganh nganh, List<NganhTohop> nganhToHopList) {
            this.nganh = nganh;
            this.nganhToHopList = nganhToHopList;
        }
    }
    public boolean deleteNganhTohop(Integer id) {
    try {
        // Sử dụng nganhToHopDAO để xóa theo ID
        NganhTohop toDelete = nganhToHopDAO.findById(NganhTohop.class, id);
        if (toDelete != null) {
            nganhToHopDAO.delete(toDelete);
            return true;
        }
        return false;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
        }
    }
}