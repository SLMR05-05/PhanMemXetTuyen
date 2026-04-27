package com.xettuyen.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * HibernateUtil - Utility class để quản lý Hibernate SessionFactory
 * Sử dụng Singleton pattern để đảm bảo chỉ có một SessionFactory duy nhất
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;

    /**
     * Static block để khởi tạo SessionFactory một lần khi class được load
     */
    static {
        try {
            // Đọc cấu hình từ hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            
            // Tạo SessionFactory từ configuration
            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo Hibernate SessionFactory: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể khởi tạo Hibernate: " + e);
        }
    }

    /**
     * Lấy SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Shutdown SessionFactory khi ứng dụng kết thúc
     */
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
