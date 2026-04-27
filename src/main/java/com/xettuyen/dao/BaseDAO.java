package com.xettuyen.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

/**
 * BaseDAO - Generic DAO Pattern
 * Cung cấp các hàm CRUD cơ bản cho tất cả Entity
 * @param <T> Kiểu Entity
 */
public abstract class BaseDAO<T> {
    protected SessionFactory sessionFactory;

    public BaseDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

/**
     * Lưu danh sách nhiều entities cùng lúc (Batch Insert)
     */
public void saveAll(List<T> entities) {
    Session session = null;
    Transaction transaction = null;
    try {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        
        int count = 0;
        for (T entity : entities) {
            session.save(entity);
            // Batch processing: Cứ 50 dòng thì đẩy xuống DB một lần để tránh treo kết nối
            if (++count % 50 == 0) {
                session.flush();
                session.clear();
            }
        }
        
        transaction.commit(); // Lưu thật sự vào MySQL
    } catch (Exception e) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback(); // Nếu lỗi thì hủy hết để tránh rác DB
        }
        System.err.println("❌ Lỗi nghiêm trọng khi lưu vào DB: " + e.getMessage());
        e.printStackTrace();
    } finally {
        if (session != null && session.isOpen()) {
            session.close(); // Chỉ đóng session ở đây, sau khi đã xong hết
        }
    }
}
    /**
     * Lưu một entity mới vào database
     */
    public void save(T entity) {
        Transaction transaction = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Lỗi khi lưu dữ liệu: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật một entity hiện có
     */
    public void update(T entity) {
        Transaction transaction = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Lỗi khi cập nhật dữ liệu: " + e.getMessage(), e);
        }
    }

    /**
     * Xóa một entity khỏi database
     */
    public void delete(T entity) {
        Transaction transaction = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.delete(entity);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Lỗi khi xóa dữ liệu: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm entity theo ID
     */
/**
 * Tìm entity theo ID (Sửa lại từ int thành Serializable để nhận cả String và int)
 */
public T findById(Class<T> clazz, java.io.Serializable id) {
    try {
        Session session = sessionFactory.openSession();
        T entity = session.get(clazz, id);
        session.close();
        return entity;
    } catch (Exception e) {
        throw new RuntimeException("Lỗi khi tìm dữ liệu theo ID: " + e.getMessage(), e);
    }
}

    /**
     * Lấy tất cả các entity
     */
    public List<T> findAll(Class<T> clazz) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM " + clazz.getSimpleName();
            Query<T> query = session.createQuery(hql, clazz);
            List<T> result = query.list();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy toàn bộ dữ liệu: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy danh sách entity có hỗ trợ phân trang
     * @param clazz Lớp Entity
     * @param offset Vị trí bắt đầu (0-based)
     * @param limit Số lượng bản ghi
     */
    public List<T> findAllWithPagination(Class<T> clazz, int offset, int limit) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM " + clazz.getSimpleName();
            Query<T> query = session.createQuery(hql, clazz);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<T> result = query.list();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy dữ liệu có phân trang: " + e.getMessage(), e);
        }
    }

    /**
     * Đếm tổng số entity
     */
    public long countAll(Class<T> clazz) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "SELECT COUNT(*) FROM " + clazz.getSimpleName();
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.uniqueResult();
            session.close();
            return count != null ? count : 0;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm dữ liệu: " + e.getMessage(), e);
        }
    }
}
