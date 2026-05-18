package com.xettuyen.dao;

import com.xettuyen.entity.User;
import com.xettuyen.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * UserDAO - Quản lý thao tác User
 * 
 * Extends: BaseDAO<User>
 */
public class UserDAO extends BaseDAO<User> {

    public UserDAO() {
        super(HibernateUtil.getSessionFactory());
    }


    /**
     * Kiểm tra đăng nhập - Tìm user theo username và password
     * 
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return User object nếu tìm thấy, null nếu không
     */
    public User login(String username, String password) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            String hql = "FROM User u WHERE u.username = :username AND u.password = :password AND u.isActive = true";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            
            List<User> result = query.list();
            
            if (result != null && !result.isEmpty()) {
                System.out.println("✅ Đăng nhập thành công: " + username);
                return result.get(0);
            } else {
                System.err.println("❌ Đăng nhập thất bại: Username hoặc password sai");
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi đăng nhập: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Tìm user theo username
     * 
     * @param username Tên đăng nhập
     * @return User object nếu tìm thấy, null nếu không
     */
    public User findByUsername(String username) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            String hql = "FROM User u WHERE u.username = :username";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("username", username);
            
            List<User> result = query.list();
            
            if (result != null && !result.isEmpty()) {
                return result.get(0);
            }
            return null;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm user: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Lấy tất cả user còn hoạt động
     * 
     * @return List tất cả user active
     */
    public List<User> getAllActiveUsers() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            String hql = "FROM User u WHERE u.isActive = true ORDER BY u.id DESC";
            Query<User> query = session.createQuery(hql, User.class);
            
            return query.list();
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lấy danh sách user: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Tìm kiếm user theo từ khóa (username/email/full name/role) có phân trang
     *
     * @param keyword Từ khóa tìm kiếm
     * @param offset Vị trí bắt đầu
     * @param limit Số bản ghi tối đa
     * @return Danh sách user phù hợp
     */
    public List<User> searchByKeyword(String keyword, int offset, int limit) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();

            String hql = "FROM User u " +
                    "WHERE (:keyword = '' OR " +
                    "LOWER(u.username) LIKE :search OR " +
                    "LOWER(u.email) LIKE :search OR " +
                    "LOWER(u.fullName) LIKE :search OR " +
                    "LOWER(u.role) LIKE :search) " +
                    "ORDER BY u.id DESC";

            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("keyword", normalizedKeyword);
            query.setParameter("search", "%" + normalizedKeyword + "%");
            query.setFirstResult(Math.max(0, offset));
            query.setMaxResults(Math.max(1, limit));
            return query.list();
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm kiếm user: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Đếm tổng số user khớp từ khóa tìm kiếm
     *
     * @param keyword Từ khóa tìm kiếm
     * @return Tổng số bản ghi
     */
    public long countSearchResult(String keyword) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();

            String hql = "SELECT COUNT(u) FROM User u " +
                    "WHERE (:keyword = '' OR " +
                    "LOWER(u.username) LIKE :search OR " +
                    "LOWER(u.email) LIKE :search OR " +
                    "LOWER(u.fullName) LIKE :search OR " +
                    "LOWER(u.role) LIKE :search)";

            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("keyword", normalizedKeyword);
            query.setParameter("search", "%" + normalizedKeyword + "%");
            Long count = query.uniqueResult();
            return count == null ? 0 : count;
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi đếm user: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Tạo user mới
     * 
     * @param user User object cần tạo
     * @return True nếu thành công, false nếu thất bại
     */
    public boolean createUser(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Kiểm tra username đã tồn tại chưa
            User existing = findByUsername(user.getUsername());
            if (existing != null) {
                System.err.println("❌ Username '" + user.getUsername() + "' đã tồn tại");
                return false;
            }
            
            session.persist(user);
            transaction.commit();
            
            System.out.println("✅ Tạo user mới thành công: " + user.getUsername());
            return true;
            
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("❌ Lỗi khi tạo user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Cập nhật user
     * 
     * @param user User object cần cập nhật
     * @return True nếu thành công
     */
    public boolean updateUser(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            session.merge(user);
            transaction.commit();
            
            System.out.println("✅ Cập nhật user thành công: " + user.getUsername());
            return true;
            
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("❌ Lỗi khi cập nhật user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Xóa user (set isActive = false)
     * 
     * @param userId ID của user
     * @return True nếu thành công
     */
    public boolean deleteUser(Integer userId) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            User user = session.find(User.class, userId);
            if (user != null) {
                user.setIsActive(false);
                session.merge(user);
                transaction.commit();
                
                System.out.println("✅ Xóa user thành công: " + user.getUsername());
                return true;
            }
            return false;
            
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("❌ Lỗi khi xóa user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }
}
