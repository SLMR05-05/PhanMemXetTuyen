package com.xettuyen.dao;

import com.xettuyen.entity.BangQuydoi;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;

/**
 * BangQuyDoiDAO - DAO cho Entity BangQuydoi
 */
public class BangQuyDoiDAO extends BaseDAO<BangQuydoi> {

    public BangQuyDoiDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public BangQuydoi findRangeForDGNL(String maToHop, double x) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM BangQuydoi b WHERE b.dPhuongThuc = 'DGNL' " + "AND b.dTohop = :maToHop "
                    + "AND :x > b.dDiemA AND :x <= b.dDiemB";
            return session.createQuery(hql, BangQuydoi.class).setParameter("maToHop", maToHop)
                    .setParameter("x", BigDecimal.valueOf(x)).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tìm khoảng quy đổi cho một môn VSAT theo phương pháp nội suy tuyến tính
     * 
     * @param maMon     Mã môn (TO, LI, VA, etc.)
     * @param vsatScore Điểm VSAT gốc (thang 150)
     * @return BangQuydoi chứa khoảng a, b, c, d phù hợp với điểm đó
     */
    public BangQuydoi findRangeForVSAT(String maMon, double vsatScore) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM BangQuydoi b WHERE b.dPhuongThuc = 'VSAT' " +
                    "AND b.dMon = :mon " +
                    "AND :score > b.dDiemA AND :score <= b.dDiemB";
            return session.createQuery(hql, BangQuydoi.class)
                    .setParameter("mon", maMon)
                    .setParameter("score", BigDecimal.valueOf(vsatScore))
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
