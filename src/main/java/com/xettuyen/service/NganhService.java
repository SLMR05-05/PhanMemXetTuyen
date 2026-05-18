package com.xettuyen.service;

import com.xettuyen.dao.*;
import com.xettuyen.entity.*;
import java.util.List;

/**
 * NganhService - Service Layer cho Nganh
 * Minh hoạ cách viết Service cho các entities khác
 */
public class NganhService {
    private NganhDAO nganhDAO = DAOFactory.getNganhDAO();
    private NganhToHopDAO nganhToHopDAO = DAOFactory.getNganhToHopDAO();

    /**
     * Lấy thông tin ngành và danh sách tổ hợp môn của ngành đó
     */
    public NganhDetail getNganhDetail(String maNganh) {
        Nganh nganh = nganhDAO.findByMaNganh(maNganh);
        if (nganh == null) {
            return null;
        }

        // Lấy tất cả tổ hợp của ngành này
        List<NganhTohop> allNganhToHop = nganhToHopDAO.findAll(NganhTohop.class);
        List<NganhTohop> nganhToHopList = allNganhToHop.stream()
                .filter(nt -> nt.getMaNganh().equals(maNganh))
                .toList();

        return new NganhDetail(nganh, nganhToHopList);
    }

    /**
     * Lưu ngành mới.
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
     * Tạo ngành mới theo từng trường dữ liệu.
     */
    public void createNganh(String maNganh, String tenNganh, int chiTieu) {
        Nganh nganh = new Nganh();
        nganh.setMaNganh(maNganh);
        nganh.setTenNganh(tenNganh);
        nganh.setNChiTieu(chiTieu);
        nganhDAO.save(nganh);
    }

    /**
     * Cập nhật ngành hiện có.
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
     * Xóa ngành theo ID.
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
     * Xóa một tổ hợp môn của ngành.
     */
    public boolean deleteNganhTohop(Integer id) {
        try {
            NganhTohop toDelete = nganhToHopDAO.findById(NganhTohop.class, id);
            if (toDelete != null) {
                nganhToHopDAO.delete(toDelete);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa tổ hợp ngành: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách ngành với phân trang
     */
    public List<Nganh> getNganhWithPagination(int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        return nganhDAO.findAllWithPagination(Nganh.class, offset, pageSize);
    }

    /**
     * Lấy tổng số ngành
     */
    public long getTotalNganh() {
        return nganhDAO.countAll(Nganh.class);
    }

    /**
     * Inner class - chi tiết ngành
     */
    public static class NganhDetail {
        public Nganh nganh;
        public List<NganhTohop> nganhToHopList;

        public NganhDetail(Nganh nganh, List<NganhTohop> nganhToHopList) {
            this.nganh = nganh;
            this.nganhToHopList = nganhToHopList;
        }
    }
}
