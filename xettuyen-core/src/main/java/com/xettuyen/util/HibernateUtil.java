package com.xettuyen.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import io.github.cdimascio.dotenv.Dotenv;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static Dotenv dotenv;

    static {
        try {
            // Thêm .ignoreIfMissing() để tránh crash ứng dụng khi không có file .env
            dotenv = Dotenv.configure().ignoreIfMissing().load();

            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");

            String dbUser = dotenv.get("DB_USERNAME");
            String dbPassword = dotenv.get("DB_PASSWORD");
            
            // Chỉ ghi đè nếu trong file .env thực sự có giá trị
            if (dbUser != null && !dbUser.trim().isEmpty()) {
                configuration.setProperty("hibernate.connection.username", dbUser);
            }
            if (dbPassword != null) { // Password có thể rỗng nên không check isEmpty
                configuration.setProperty("hibernate.connection.password", dbPassword);
            }

            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo Hibernate SessionFactory: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError("Không thể khởi tạo Hibernate: " + e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}