package com.xettuyen.dao;

import com.xettuyen.entity.DiemCongXettuyen;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.List;

/**
 * DiemCongDAO - DAO cho Entity DiemCongXettuyen
 */
public class DiemCongDAO extends BaseDAO<DiemCongXettuyen> {

    public DiemCongDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Lấy danh sách điểm cộng theo CCCD của thí sinh
     */
    public List<DiemCongXettuyen> findByCCCD(String cccd) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM DiemCongXettuyen WHERE tsCccd = :cccd";
            Query<DiemCongXettuyen> query = session.createQuery(hql, DiemCongXettuyen.class);
            query.setParameter("cccd", cccd);
            List<DiemCongXettuyen> result = query.list();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm điểm cộng theo CCCD: " + e.getMessage(), e);
        }
    }
}
