package com.xettuyen.dao;

import com.xettuyen.entity.NguyenVongXettuyen;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.List;

/**
 * NguyenVongDAO - DAO cho Entity NguyenVongXettuyen
 */
public class NguyenVongDAO extends BaseDAO<NguyenVongXettuyen> {

    public NguyenVongDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Lấy danh sách nguyện vọng theo CCCD của thí sinh
     */
    public List<NguyenVongXettuyen> findByCCCD(String cccd) {
        try (Session session = sessionFactory.openSession()) {
            // Sử dụng LIKE để tìm kiếm một phần (ví dụ: nhập 0001 ra TS_0001)
            // Dùng LOWER để tìm kiếm không phân biệt chữ hoa, chữ thường
            String hql = "FROM NguyenVongXettuyen WHERE LOWER(nnCccd) LIKE LOWER(:cccd) ORDER BY nnCccd ASC, nvTt ASC";

            Query<NguyenVongXettuyen> query = session.createQuery(hql, NguyenVongXettuyen.class);

            // Gán tham số: bọc giá trị nhập vào giữa hai dấu %
            query.setParameter("cccd", "%" + cccd + "%");

            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm nguyện vọng theo CCCD: " + e.getMessage(), e);
        }
    }

    public NguyenVongXettuyen findByNvKeys(String nvKeys) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM NguyenVongXettuyen WHERE nvKeys = :nvKeys";
            return session.createQuery(hql, NguyenVongXettuyen.class).setParameter("nvKeys", nvKeys).uniqueResult(); // null nếu không tìm thấy
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm theo nvKeys: " + e.getMessage(), e);
        }
    }

    // Load tất cả có phân trang
    public List<NguyenVongXettuyen> findAllPaged(int offset, int limit) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM NguyenVongXettuyen ORDER BY nnCccd ASC, nvTt ASC", NguyenVongXettuyen.class).setFirstResult(offset).setMaxResults(limit).list();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi load nguyện vọng: " + e.getMessage(), e);
        }
    }

    // Đếm tổng để biết còn record không
    public long countAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT COUNT(*) FROM NguyenVongXettuyen", Long.class).uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm nguyện vọng: " + e.getMessage(), e);
        }
    }
}
