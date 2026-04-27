package com.xettuyen.dao;

import com.xettuyen.entity.Nganh;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

/**
 * NganhDAO - DAO cho Entity Nganh
 */
public class NganhDAO extends BaseDAO<Nganh> {

    public NganhDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Tìm ngành theo mã ngành
     */
    public Nganh findByMaNganh(String maNganh) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM Nganh WHERE maNganh = :maNganh";
            Query<Nganh> query = session.createQuery(hql, Nganh.class);
            query.setParameter("maNganh", maNganh);
            Nganh result = query.uniqueResult();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm ngành theo mã: " + e.getMessage(), e);
        }
    }
}
