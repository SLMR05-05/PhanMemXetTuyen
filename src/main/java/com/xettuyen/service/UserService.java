package com.xettuyen.service;

import com.xettuyen.dao.DAOFactory;
import com.xettuyen.dao.UserDAO;
import com.xettuyen.entity.User;
import java.util.Date;
import java.util.List;

/**
 * UserService - xử lý nghiệp vụ cho User UI.
 */
public class UserService {
    private final UserDAO userDAO = DAOFactory.getUserDAO();

    /**
     * Tìm kiếm user có phân trang.
     */
    public SearchResult<User> searchUsers(String keyword, int pageNumber, int pageSize) {
        int safePage = Math.max(1, pageNumber);
        int safePageSize = Math.max(1, pageSize);
        int offset = (safePage - 1) * safePageSize;

        List<User> data = userDAO.searchByKeyword(keyword, offset, safePageSize);
        long totalRecords = userDAO.countSearchResult(keyword);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / safePageSize));

        int boundedPage = Math.min(safePage, totalPages);
        return new SearchResult<>(data, boundedPage, safePageSize, totalRecords, totalPages);
    }

    /**
     * Lấy thông tin user theo id.
     */
    public User getUserById(Integer id) {
        if (id == null) {
            return null;
        }
        return userDAO.findById(User.class, id);
    }

    /**
     * Cập nhật thông tin user theo id.
     */
    public void updateUserInfo(Integer userId, User updatedUser) {
        if (userId == null || updatedUser == null) {
            throw new RuntimeException("Dữ liệu cập nhật không hợp lệ.");
        }

        User existing = userDAO.findById(User.class, userId);
        if (existing == null) {
            throw new RuntimeException("Không tìm thấy tài khoản cần cập nhật.");
        }

        String username = safeTrim(updatedUser.getUsername());
        if (username.isEmpty()) {
            throw new RuntimeException("Username không được để trống.");
        }

        User byUsername = userDAO.findByUsername(username);
        if (byUsername != null && !byUsername.getId().equals(userId)) {
            throw new RuntimeException("Username đã tồn tại. Vui lòng chọn username khác.");
        }

        existing.setUsername(username);
        existing.setPassword(safeTrim(updatedUser.getPassword()));
        existing.setEmail(safeTrim(updatedUser.getEmail()));
        existing.setFullName(safeTrim(updatedUser.getFullName()));
        existing.setRole(safeTrim(updatedUser.getRole()));
        existing.setIsActive(updatedUser.getIsActive() != null ? updatedUser.getIsActive() : Boolean.FALSE);
        existing.setUpdatedAt(new Date());

        if (existing.getRole() == null || existing.getRole().isEmpty()) {
            existing.setRole("user");
        }

        userDAO.updateUser(existing);
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * DTO kết quả phân trang.
     */
    public static class SearchResult<T> {
        public List<T> data;
        public int currentPage;
        public int pageSize;
        public long totalRecords;
        public int totalPages;

        public SearchResult(List<T> data, int currentPage, int pageSize, long totalRecords, int totalPages) {
            this.data = data;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
            this.totalRecords = totalRecords;
            this.totalPages = totalPages;
        }
    }
}
