package com.xettuyen.dao;

import com.xettuyen.entity.Nganh;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;
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

    /**
     * Cập nhật trực tiếp tổ hợp gốc theo mã ngành, không load entity lên bộ nhớ.
     */
    public int updateTohopGocByMaNganh(String maNganh, String nTohopGoc) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            MutationQuery query = session.createMutationQuery(
                    "UPDATE Nganh n SET n.nTohopGoc = :nTohopGoc WHERE n.maNganh = :maNganh");
            query.setParameter("nTohopGoc", nTohopGoc);
            query.setParameter("maNganh", maNganh);
            int affectedRows = query.executeUpdate();
            transaction.commit();
            return affectedRows;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(
                    "Lỗi khi cập nhật tổ hợp gốc cho ngành " + maNganh + ": " + e.getMessage(), e);
        }
    }

    /**
     * Session-aware update used for batch operations: does not commit/rollback the
     * passed session.
     */
    public int updateTohopGocByMaNganh(org.hibernate.Session session, String maNganh, String nTohopGoc) {
        try {
            org.hibernate.query.MutationQuery query = session.createMutationQuery(
                    "UPDATE Nganh n SET n.nTohopGoc = :nTohopGoc WHERE n.maNganh = :maNganh");
            query.setParameter("nTohopGoc", nTohopGoc);
            query.setParameter("maNganh", maNganh);
            return query.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Lỗi khi cập nhật tổ hợp gốc cho ngành " + maNganh + ": " + e.getMessage(), e);
        }
    }
}
