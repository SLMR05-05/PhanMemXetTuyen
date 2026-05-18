package com.xettuyen.dao;

import com.xettuyen.entity.NguyenVongXettuyen;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.List;

/**
 * NguyenVongDAO - DAO cho Entity NguyenVongXettuyen
 */
public class NguyenVongDAO extends BaseDAO<NguyenVongXettuyen> {

    public NguyenVongDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Lấy danh sách nguyện vọng theo CCCD của thí sinh
     */
    public List<NguyenVongXettuyen> findByCCCD(String cccd) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM NguyenVongXettuyen WHERE nnCccd = :cccd ORDER BY nvTt ASC";
            Query<NguyenVongXettuyen> query = session.createQuery(hql, NguyenVongXettuyen.class);
            query.setParameter("cccd", cccd);
            List<NguyenVongXettuyen> result = query.list();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm nguyện vọng theo CCCD: " + e.getMessage(), e);
        }
    }
}
