package com.xettuyen.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.xettuyen.entity.ToHop;
import com.xettuyen.util.HibernateUtil;

public class ToHopDAO {

    public List<ToHop> getAll(int page, int size) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        List<ToHop> list = session.createQuery("FROM ToHop", ToHop.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .list();

        session.close();
        return list;
    }

    public void insert(ToHop th) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.save(th);

        tx.commit();
        session.close();
    }

    public void update(ToHop th) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.update(th);

        tx.commit();
        session.close();
    }

    public void delete(String id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        ToHop th = session.get(ToHop.class, id);
        if (th != null) session.delete(th);

        tx.commit();
        session.close();
    }
}