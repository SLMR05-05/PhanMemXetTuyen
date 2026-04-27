package com.xettuyen.dao;

import com.xettuyen.entity.DiemThiXettuyen;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.List;

/**
 * DiemThiDAO - DAO cho Entity DiemThiXettuyen
 */
public class DiemThiDAO extends BaseDAO<DiemThiXettuyen> {

    public DiemThiDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Lấy danh sách điểm thi theo CCCD của thí sinh
     */
    public List<DiemThiXettuyen> findByCCCD(String cccd) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM DiemThiXettuyen WHERE cccd = :cccd";
            Query<DiemThiXettuyen> query = session.createQuery(hql, DiemThiXettuyen.class);
            query.setParameter("cccd", cccd);
            List<DiemThiXettuyen> result = query.list();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm điểm thi theo CCCD: " + e.getMessage(), e);
        }
    }
}
