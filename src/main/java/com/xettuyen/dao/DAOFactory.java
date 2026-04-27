package com.xettuyen.dao;

import com.xettuyen.util.HibernateUtil;
import org.hibernate.SessionFactory;

/**
 * DAOFactory - Factory Pattern
 * Cung cấp các instance của tất cả DAO classes
 * Quản lý việc tạo và cấp phát DAO instances
 */
public class DAOFactory {
    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    
    // Cache các DAO instances (Optional - nếu muốn dùng singleton pattern)
    private static BangQuyDoiDAO bangQuyDoiDAO;
    private static DiemCongDAO diemCongDAO;
    private static DiemThiDAO diemThiDAO;
    private static NganhDAO nganhDAO;
    private static NganhToHopDAO nganhToHopDAO;
    private static NguyenVongDAO nguyenVongDAO;
    private static ThiSinhDAO thiSinhDAO;
    private static ToHopMonDAO toHopMonDAO;
    private static UserDAO userDAO;

    /**
     * Lấy instance của BangQuyDoiDAO
     */
    public static BangQuyDoiDAO getBangQuyDoiDAO() {
        if (bangQuyDoiDAO == null) {
            bangQuyDoiDAO = new BangQuyDoiDAO(sessionFactory);
        }
        return bangQuyDoiDAO;
    }

    /**
     * Lấy instance của DiemCongDAO
     */
    public static DiemCongDAO getDiemCongDAO() {
        if (diemCongDAO == null) {
            diemCongDAO = new DiemCongDAO(sessionFactory);
        }
        return diemCongDAO;
    }

    /**
     * Lấy instance của DiemThiDAO
     */
    public static DiemThiDAO getDiemThiDAO() {
        if (diemThiDAO == null) {
            diemThiDAO = new DiemThiDAO(sessionFactory);
        }
        return diemThiDAO;
    }

    /**
     * Lấy instance của NganhDAO
     */
    public static NganhDAO getNganhDAO() {
        if (nganhDAO == null) {
            nganhDAO = new NganhDAO(sessionFactory);
        }
        return nganhDAO;
    }

    /**
     * Lấy instance của NganhToHopDAO
     */
    public static NganhToHopDAO getNganhToHopDAO() {
        if (nganhToHopDAO == null) {
            nganhToHopDAO = new NganhToHopDAO(sessionFactory);
        }
        return nganhToHopDAO;
    }

    /**
     * Lấy instance của NguyenVongDAO
     */
    public static NguyenVongDAO getNguyenVongDAO() {
        if (nguyenVongDAO == null) {
            nguyenVongDAO = new NguyenVongDAO(sessionFactory);
        }
        return nguyenVongDAO;
    }

    /**
     * Lấy instance của ThiSinhDAO
     */
    public static ThiSinhDAO getThiSinhDAO() {
        if (thiSinhDAO == null) {
            thiSinhDAO = new ThiSinhDAO(sessionFactory);
        }
        return thiSinhDAO;
    }

    /**
     * Lấy instance của ToHopMonDAO
     */
    public static ToHopMonDAO getToHopMonDAO() {
        if (toHopMonDAO == null) {
            toHopMonDAO = new ToHopMonDAO(sessionFactory);
        }
        return toHopMonDAO;
    }

    /**
     * Lấy instance của UserDAO
     */
    public static UserDAO getUserDAO() {
        if (userDAO == null) {
            userDAO = new UserDAO();
        }
        return userDAO;
    }

    /**
     * Reset tất cả DAO instances (sử dụng khi cần tạo mới)
     */
    public static void reset() {
        bangQuyDoiDAO = null;
        diemCongDAO = null;
        diemThiDAO = null;
        nganhDAO = null;
        nganhToHopDAO = null;
        nguyenVongDAO = null;
        thiSinhDAO = null;
        toHopMonDAO = null;
        userDAO = null;
    }
}
