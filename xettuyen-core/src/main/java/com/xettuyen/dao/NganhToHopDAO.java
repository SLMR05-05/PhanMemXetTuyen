package com.xettuyen.dao;

import com.xettuyen.entity.NganhTohop;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * NganhToHopDAO - DAO cho Entity NganhTohop
 */
public class NganhToHopDAO extends BaseDAO<NganhTohop> {

    public NganhToHopDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Execute a callback within a transaction and session managed by this DAO.
     */
    public <R> R executeInTransaction(Function<org.hibernate.Session, R> callback) {
        org.hibernate.Transaction tx = null;
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            R result = callback.apply(session);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
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

    /**
     * Cập nhật trực tiếp độ lệch theo tbKeys, không load entity lên bộ nhớ.
     */
    public int updateDoLechByTbKeys(String tbKeys, Double doLech) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            MutationQuery query = session.createMutationQuery(
                    "UPDATE NganhTohop nt SET nt.doLech = :doLech WHERE nt.tbKeys = :tbKeys");
            query.setParameter("doLech", doLech);
            query.setParameter("tbKeys", tbKeys);
            int affectedRows = query.executeUpdate();
            transaction.commit();
            return affectedRows;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(
                    "Lỗi khi cập nhật độ lệch cho tbKeys " + tbKeys + ": " + e.getMessage(), e);
        }
    }

    /**
     * Session-aware update for doLech used in batch operations.
     */
    public int updateDoLechByTbKeys(org.hibernate.Session session, String tbKeys, Double doLech) {
        try {
            org.hibernate.query.MutationQuery query = session.createMutationQuery(
                    "UPDATE NganhTohop nt SET nt.doLech = :doLech WHERE nt.tbKeys = :tbKeys");
            query.setParameter("doLech", doLech);
            query.setParameter("tbKeys", tbKeys);
            return query.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Lỗi khi cập nhật độ lệch cho tbKeys " + tbKeys + ": " + e.getMessage(), e);
        }
    }

    /**
     * Upsert a NganhTohop record by tbKeys using the provided session.
     * If a record with the same tbKeys exists, update its fields; otherwise insert
     * new.
     */
    public boolean upsertByTbKeys(org.hibernate.Session session, NganhTohop entry) {
        try {
            String hql = "SELECT n.id FROM NganhTohop n WHERE n.tbKeys = :tbKeys";
            org.hibernate.query.Query<Integer> q = session.createQuery(hql, Integer.class);
            q.setParameter("tbKeys", entry.getTbKeys());
            Integer existingId = q.uniqueResult();
            if (existingId != null) {
                // Update existing by id
                org.hibernate.query.MutationQuery uq = session.createMutationQuery(
                    "UPDATE NganhTohop n SET n.maNganh = :maNganh, n.maTohop = :maTohop, "
                        + "n.thMon1 = :thMon1, n.hsMon1 = :hsMon1, n.thMon2 = :thMon2, n.hsMon2 = :hsMon2, "
                        + "n.thMon3 = :thMon3, n.hsMon3 = :hsMon3, n.doLech = :doLech, "
                        + "n.n1 = :n1, n.to = :to, n.li = :li, n.ho = :ho, n.si = :si, n.va = :va, n.su = :su, n.di = :di, n.ti = :ti, n.khac = :khac, n.ktpl = :ktpl "
                        + "WHERE n.id = :id");
                uq.setParameter("maNganh", entry.getMaNganh());
                uq.setParameter("maTohop", entry.getMaTohop());
                uq.setParameter("thMon1", entry.getThMon1());
                uq.setParameter("hsMon1", entry.getHsMon1());
                uq.setParameter("thMon2", entry.getThMon2());
                uq.setParameter("hsMon2", entry.getHsMon2());
                uq.setParameter("thMon3", entry.getThMon3());
                uq.setParameter("hsMon3", entry.getHsMon3());
                uq.setParameter("doLech", entry.getDoLech());
                uq.setParameter("n1", entry.getN1());
                uq.setParameter("to", entry.getTo());
                uq.setParameter("li", entry.getLi());
                uq.setParameter("ho", entry.getHo());
                uq.setParameter("si", entry.getSi());
                uq.setParameter("va", entry.getVa());
                uq.setParameter("su", entry.getSu());
                uq.setParameter("di", entry.getDi());
                uq.setParameter("ti", entry.getTi());
                uq.setParameter("khac", entry.getKhac());
                uq.setParameter("ktpl", entry.getKtpl());
                uq.setParameter("id", existingId);
                uq.executeUpdate();
                return false;
            } else {
                // Insert new
                session.persist(entry);
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi upsert NganhTohop tbKeys=" + entry.getTbKeys() + ": " + e.getMessage(), e);
        }
    }

}
