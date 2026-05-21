package com.xettuyen.dao;

import com.xettuyen.entity.TohopMonthi;

import java.util.List;
import com.xettuyen.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

/**
 * ToHopMonDAO - DAO cho Entity TohopMonthi
 */
public class ToHopMonDAO extends BaseDAO<TohopMonthi> {

    public ToHopMonDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<TohopMonthi> getAll(int page, int size) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<TohopMonthi> list = session.createQuery("FROM TohopMonthi", TohopMonthi.class)
                .setFirstResult((page - 1) * size).setMaxResults(size).list();
        session.close();
        return list;
    }

    public TohopMonthi findByMaTohop(String maTohop) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM TohopMonthi WHERE maTohop = :maTohop";
            Query<TohopMonthi> query = session.createQuery(hql, TohopMonthi.class);
            query.setParameter("maTohop", maTohop);
            TohopMonthi result = query.uniqueResult();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm tổ hợp theo mã: " + e.getMessage(), e);
        }
    }

    public List<TohopMonthi> searchByKeyword(String keyword, int offset, int limit) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM TohopMonthi WHERE lower(maTohop) LIKE :keyword " +
                    "OR lower(mon1) LIKE :keyword " +
                    "OR lower(mon2) LIKE :keyword " +
                    "OR lower(mon3) LIKE :keyword " +
                    "OR lower(tenTohop) LIKE :keyword";
            Query<TohopMonthi> query = session.createQuery(hql, TohopMonthi.class);
            query.setParameter("keyword", "%" + normalize(keyword) + "%");
            query.setFirstResult(Math.max(0, offset));
            query.setMaxResults(Math.max(1, limit));
            List<TohopMonthi> result = query.list();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm tổ hợp: " + e.getMessage(), e);
        }
    }

    public long countSearchResult(String keyword) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "SELECT COUNT(*) FROM TohopMonthi WHERE lower(maTohop) LIKE :keyword " +
                    "OR lower(mon1) LIKE :keyword " +
                    "OR lower(mon2) LIKE :keyword " +
                    "OR lower(mon3) LIKE :keyword " +
                    "OR lower(tenTohop) LIKE :keyword";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("keyword", "%" + normalize(keyword) + "%");
            Long count = query.uniqueResult();
            session.close();
            return count != null ? count : 0L;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm kết quả tìm kiếm tổ hợp: " + e.getMessage(), e);
        }
    }

    private String normalize(String keyword) {
        return keyword == null ? "" : keyword.trim().toLowerCase();
    }
}
