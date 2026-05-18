package com.xettuyen.dao;

import com.xettuyen.entity.NganhTohop;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

/**
 * NganhToHopDAO - DAO cho Entity NganhTohop
 */
public class NganhToHopDAO extends BaseDAO<NganhTohop> {

    public NganhToHopDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Tìm mối quan hệ ngành - tổ hợp theo mã ngành và mã tổ hợp
     */
    public NganhTohop findByMaNganhAndMaToHop(String maNganh, String maToHop) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM NganhTohop WHERE maNganh = :maNganh AND maTohop = :maToHop";
            Query<NganhTohop> query = session.createQuery(hql, NganhTohop.class);
            query.setParameter("maNganh", maNganh);
            query.setParameter("maToHop", maToHop);
            NganhTohop result = query.uniqueResult();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm ngành - tổ hợp: " + e.getMessage(), e);
        }
    }

    public List<NganhTohop> findByMaNganh(String maNganh) {
        try (Session session = sessionFactory.openSession()) {
            // Sử dụng HQL để lấy tất cả tổ hợp thuộc về mã ngành
            String hql = "FROM NganhTohop n WHERE n.maNganh = :maNganh";
            return session.createQuery(hql, NganhTohop.class).setParameter("maNganh", maNganh).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public long countByMaTohop(String maTohop) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT COUNT(*) FROM NganhTohop WHERE maTohop = :maTohop";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("maTohop", maTohop);
            Long count = query.uniqueResult();
            return count == null ? 0L : count;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm mapping ngành - tổ hợp: " + e.getMessage(), e);
        }
    }

}
