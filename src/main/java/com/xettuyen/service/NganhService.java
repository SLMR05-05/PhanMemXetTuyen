package com.xettuyen.service;

import com.xettuyen.dao.*;
import com.xettuyen.entity.*;
import java.util.List;
import java.util.stream.Collectors;

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
            .collect(Collectors.toList());

        return new NganhDetail(nganh, nganhToHopList);
    }

    /**
     * Tạo ngành mới
     */
    public void createNganh(String maNganh, String tenNganh, int chiTieu) {
        Nganh nganh = new Nganh();
        nganh.setMaNganh(maNganh);
        nganh.setTenNganh(tenNganh);
        nganh.setNChiTieu(chiTieu);
        nganhDAO.save(nganh);
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
