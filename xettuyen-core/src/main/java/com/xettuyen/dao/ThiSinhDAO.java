package com.xettuyen.dao;

import com.xettuyen.entity.ThiSinhXettuyen;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ThiSinhDAO - DAO cho Entity ThiSinhXettuyen
 * Hỗ trợ các truy vấn đặc thù cho thí sinh
 */
public class ThiSinhDAO extends BaseDAO<ThiSinhXettuyen> {

    public ThiSinhDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Tìm thí sinh chính xác theo CCCD
     */
    public ThiSinhXettuyen findByCCCD(String cccd) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM ThiSinhXettuyen WHERE cccd = :cccd";
            Query<ThiSinhXettuyen> query = session.createQuery(hql, ThiSinhXettuyen.class);
            query.setParameter("cccd", cccd);
            ThiSinhXettuyen result = query.uniqueResult();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm thí sinh theo CCCD: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm kiếm thí sinh theo keyword (CCCD hoặc họ tên)
     * Hỗ trợ tìm kiếm tương đối với LIKE
     * 
     * @param keyword Từ khóa tìm kiếm
     * @param offset  Vị trí bắt đầu (0-based)
     * @param limit   Số lượng bản ghi
     */
    public List<ThiSinhXettuyen> searchByKeyword(String keyword, int offset, int limit) {
        try {
            Session session = sessionFactory.openSession();
            // HQL với LIKE để tìm kiếm trong cccd, ho, ten
            String hql = "FROM ThiSinhXettuyen WHERE CONCAT(ho, ' ', ten) LIKE :keyword OR cccd LIKE :keyword";
            Query<ThiSinhXettuyen> query = session.createQuery(hql, ThiSinhXettuyen.class);
            query.setParameter("keyword", "%" + keyword + "%");
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<ThiSinhXettuyen> result = query.list();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm thí sinh: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm kiếm thí sinh theo keyword và ngành trúng tuyển.
     * Nếu maNganh rỗng/null thì bỏ qua điều kiện lọc ngành.
     */
    public List<ThiSinhXettuyen> searchByKeywordAndNganh(String keyword, String maNganh, int offset, int limit) {
        try {
            Session session = sessionFactory.openSession();
            StringBuilder hql = new StringBuilder(
                    "FROM ThiSinhXettuyen WHERE (CONCAT(ho, ' ', ten) LIKE :keyword OR cccd LIKE :keyword)");

            boolean filterByNganh = maNganh != null && !maNganh.trim().isEmpty();
            if (filterByNganh) {
                hql.append(" AND LOWER(TRIM(nganhTrungTuyen)) = LOWER(TRIM(:maNganh))");
            }

            Query<ThiSinhXettuyen> query = session.createQuery(hql.toString(), ThiSinhXettuyen.class);
            query.setParameter("keyword", "%" + (keyword == null ? "" : keyword.trim()) + "%");
            if (filterByNganh) {
                query.setParameter("maNganh", maNganh.trim());
            }
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<ThiSinhXettuyen> result = query.list();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm thí sinh theo ngành: " + e.getMessage(), e);
        }
    }

    /**
     * Đếm kết quả tìm kiếm
     */
    public long countSearchResult(String keyword) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "SELECT COUNT(*) FROM ThiSinhXettuyen WHERE CONCAT(ho, ' ', ten) LIKE :keyword OR cccd LIKE :keyword";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("keyword", "%" + keyword + "%");
            Long count = query.uniqueResult();
            session.close();
            return count != null ? count : 0;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm kết quả tìm kiếm: " + e.getMessage(), e);
        }
    }

    /**
     * Đếm kết quả tìm kiếm theo keyword và ngành trúng tuyển.
     */
    public long countSearchResult(String keyword, String maNganh) {
        try {
            Session session = sessionFactory.openSession();
            StringBuilder hql = new StringBuilder(
                    "SELECT COUNT(*) FROM ThiSinhXettuyen WHERE (CONCAT(ho, ' ', ten) LIKE :keyword OR cccd LIKE :keyword)");

            boolean filterByNganh = maNganh != null && !maNganh.trim().isEmpty();
            if (filterByNganh) {
                hql.append(" AND LOWER(TRIM(nganhTrungTuyen)) = LOWER(TRIM(:maNganh))");
            }

            Query<Long> query = session.createQuery(hql.toString(), Long.class);
            query.setParameter("keyword", "%" + (keyword == null ? "" : keyword.trim()) + "%");
            if (filterByNganh) {
                query.setParameter("maNganh", maNganh.trim());
            }
            Long count = query.uniqueResult();
            session.close();
            return count != null ? count : 0;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm kết quả tìm kiếm theo ngành: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm thí sinh theo SBD
     */
    public ThiSinhXettuyen findBySoBaoDanh(String soBaoDanh) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM ThiSinhXettuyen WHERE soBaoDanh = :soBaoDanh";
            Query<ThiSinhXettuyen> query = session.createQuery(hql, ThiSinhXettuyen.class);
            query.setParameter("soBaoDanh", soBaoDanh);
            ThiSinhXettuyen result = query.uniqueResult();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm thí sinh theo SBD: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm kiếm nhanh phục vụ dashboard Home
     */
    public List<ThiSinhXettuyen> searchForHome(String keyword, int limit) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "FROM ThiSinhXettuyen " +
                    "WHERE CONCAT(ho, ' ', ten) LIKE :keyword OR cccd LIKE :keyword OR soBaoDanh LIKE :keyword " +
                    "ORDER BY idThiSinh DESC";
            Query<ThiSinhXettuyen> query = session.createQuery(hql, ThiSinhXettuyen.class);
            query.setParameter("keyword", "%" + keyword + "%");
            query.setMaxResults(limit);
            List<ThiSinhXettuyen> result = query.list();
            session.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm thí sinh cho Home: " + e.getMessage(), e);
        }
    }

    /**
     * Thống kê số lượng theo đối tượng
     */
    public Map<String, Long> countByDoiTuong() {
        return countGroupedByField("doiTuong");
    }

    /**
     * Thống kê số lượng theo khu vực
     */
    public Map<String, Long> countByKhuVuc() {
        return countGroupedByField("khuVuc");
    }

    private Map<String, Long> countGroupedByField(String fieldName) {
        try {
            Session session = sessionFactory.openSession();
            String hql = "SELECT COALESCE(" + fieldName + ", ''), COUNT(*) FROM ThiSinhXettuyen GROUP BY " + fieldName
                    + " ORDER BY " + fieldName;
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            List<Object[]> rawData = query.list();
            session.close();

            Map<String, Long> result = new LinkedHashMap<>();
            for (Object[] row : rawData) {
                String label = row[0] == null ? "" : row[0].toString().trim();
                if (label.isEmpty()) {
                    label = "Chưa cập nhật";
                }
                Long count = row[1] instanceof Long ? (Long) row[1] : 0L;
                result.put(label, count);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thống kê theo " + fieldName + ": " + e.getMessage(), e);
        }
    }
}
