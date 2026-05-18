package com.xettuyen.dao;

import com.xettuyen.entity.NganhTohop;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
}
