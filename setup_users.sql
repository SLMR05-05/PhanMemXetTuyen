-- ========================================
-- SQL SETUP FOR USERS TABLE
-- ========================================

-- 1. Tạo bảng users (nếu chưa tồn tại)
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    role VARCHAR(20) DEFAULT 'user',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Xóa dữ liệu cũ (nếu muốn reset)
-- TRUNCATE TABLE users;

-- 3. Insert test accounts

-- Admin account
INSERT INTO users (username, password, email, full_name, role, is_active) 
VALUES ('admin', 'admin123', 'admin@xettuyen.edu.vn', 'Administrator', 'admin', true);

-- User accounts
INSERT INTO users (username, password, email, full_name, role, is_active) 
VALUES ('user1', 'user123', 'user1@xettuyen.edu.vn', 'Nguyễn Văn A', 'user', true);

INSERT INTO users (username, password, email, full_name, role, is_active) 
VALUES ('user2', 'pass456', 'user2@xettuyen.edu.vn', 'Trần Thị B', 'user', true);

INSERT INTO users (username, password, email, full_name, role, is_active) 
VALUES ('user3', 'secure789', 'user3@xettuyen.edu.vn', 'Lê Văn C', 'user', true);

-- 4. Kiểm tra dữ liệu
SELECT * FROM users;

-- ========================================
-- THOẠI
-- ========================================

-- Xem tất cả user
SELECT id, username, email, full_name, role, is_active FROM users;

-- Tìm user theo username
SELECT * FROM users WHERE username = 'admin';

-- Cập nhật user
UPDATE users SET email = 'admin_new@xettuyen.edu.vn' WHERE username = 'admin';

-- Disable user
UPDATE users SET is_active = false WHERE username = 'user1';

-- Xóa user (soft delete)
DELETE FROM users WHERE username = 'user_test';

-- ========================================
-- TEST ACCOUNTS
-- ========================================

-- Admin
-- Username: admin
-- Password: admin123
-- Email: admin@xettuyen.edu.vn

-- User 1
-- Username: user1
-- Password: user123
-- Email: user1@xettuyen.edu.vn

-- User 2
-- Username: user2
-- Password: pass456
-- Email: user2@xettuyen.edu.vn

-- User 3
-- Username: user3
-- Password: secure789
-- Email: user3@xettuyen.edu.vn
