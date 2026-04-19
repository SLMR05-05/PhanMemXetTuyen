package com.xettuyen.dao;

import com.xettuyen.entity.ThiSinhXettuyen;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.List;

/**
 * ThiSinhDAO - DAO cho Entity ThiSinhXettuyen
 * Hỗ trợ các truy vấn đặc thù cho thí sinh
 */
public class ThiSinhDAO extends BaseDAO<ThiSinhXettuyen> {

    public ThiSinhDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Tìm thí sinh chính xác theo CCCD
     */
    public ThiSinhXettuyen findByCCCD(String cccd) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM ThiSinhXettuyen WHERE cccd = :cccd";
            Query<ThiSinhXettuyen> query = session.createQuery(hql, ThiSinhXettuyen.class);
            query.setParameter("cccd", cccd);
            ThiSinhXettuyen result = query.uniqueResult();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm thí sinh theo CCCD: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm kiếm thí sinh theo keyword (CCCD hoặc họ tên)
     * Hỗ trợ tìm kiếm tương đối với LIKE
     * @param keyword Từ khóa tìm kiếm
     * @param offset Vị trí bắt đầu (0-based)
     * @param limit Số lượng bản ghi
     */
    public List<ThiSinhXettuyen> searchByKeyword(String keyword, int offset, int limit) {
        try {
            Session session = sessionFactory.openSession();
            // HQL với LIKE để tìm kiếm trong cccd, ho, ten
            String hql = "FROM ThiSinhXettuyen WHERE CONCAT(ho, ' ', ten) LIKE :keyword OR cccd LIKE :keyword";
            Query<ThiSinhXettuyen> query = session.createQuery(hql, ThiSinhXettuyen.class);
            query.setParameter("keyword", "%" + keyword + "%");
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<ThiSinhXettuyen> result = query.list();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm thí sinh: " + e.getMessage(), e);
        }
    }

    /**
     * Đếm kết quả tìm kiếm
     */
    public long countSearchResult(String keyword) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "SELECT COUNT(*) FROM ThiSinhXettuyen WHERE CONCAT(ho, ' ', ten) LIKE :keyword OR cccd LIKE :keyword";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("keyword", "%" + keyword + "%");
            Long count = query.uniqueResult();
            session.close();
            return count != null ? count : 0;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm kết quả tìm kiếm: " + e.getMessage(), e);
        }
    }
}
