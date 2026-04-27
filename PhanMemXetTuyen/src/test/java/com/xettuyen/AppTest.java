package com.xettuyen;

import com.xettuyen.dao.UserDAO;
import com.xettuyen.dao.DAOFactory;
import com.xettuyen.entity.User;
import com.xettuyen.util.HibernateUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.hibernate.Session;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    /**
     * Test đăng nhập với thông tin đúng
     */
    public void testLoginWithCorrectCredentials()
    {
        System.out.println("\n========== TEST: Đăng nhập với thông tin ĐÚNG ==========");
        
        try {
            // 1. Kiểm tra xem admin user có tồn tại không
            Session session = HibernateUtil.getSessionFactory().openSession();
            User testUser = session.find(User.class, 1); // id = 1
            session.close();
            
            if (testUser != null) {
                System.out.println("✅ Tìm thấy user trong DB:");
                System.out.println("   - Username: " + testUser.getUsername());
                System.out.println("   - Password: " + testUser.getPassword());
                System.out.println("   - IsActive: " + testUser.getIsActive());
                System.out.println("   - Role: " + testUser.getRole());
                
                // 2. Thử đăng nhập với UserDAO
                UserDAO userDAO = DAOFactory.getUserDAO();
                User loginResult = userDAO.login(testUser.getUsername(), testUser.getPassword());
                
                if (loginResult != null) {
                    System.out.println("✅ ĐẠT: Đăng nhập thành công!");
                    System.out.println("   - User returned: " + loginResult.getUsername());
                    assertTrue("Login should return user", true);
                } else {
                    System.err.println("❌ FAIL: Đăng nhập thất bại - userDAO.login() trả về null");
                    System.err.println("   - Nguyên nhân: Có thể là lỗi query HQL");
                    assertTrue("Login should return user", false);
                }
            } else {
                System.out.println("⚠️ SKIP: Không tìm thấy user trong DB (Database chưa có dữ liệu test)");
            }
            
        } catch (Exception e) {
            System.err.println("❌ EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            assertTrue("Should not throw exception", false);
        }
    }

    /**
     * Test đăng nhập với mật khẩu sai
     */
    public void testLoginWithWrongPassword()
    {
        System.out.println("\n========== TEST: Đăng nhập với mật khẩu SAI ==========");
        
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            User testUser = session.find(User.class, 1);
            session.close();
            
            if (testUser != null) {
                UserDAO userDAO = DAOFactory.getUserDAO();
                User loginResult = userDAO.login(testUser.getUsername(), "wrongpassword123");
                
                if (loginResult == null) {
                    System.out.println("✅ ĐẠT: Đăng nhập thất bại như kỳ vọng (mật khẩu sai)");
                    assertTrue("Should reject wrong password", true);
                } else {
                    System.err.println("❌ FAIL: Đăng nhập thành công với mật khẩu sai!");
                    assertTrue("Should reject wrong password", false);
                }
            } else {
                System.out.println("⚠️ SKIP: Không tìm thấy user trong DB");
            }
            
        } catch (Exception e) {
            System.err.println("❌ EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            assertTrue("Should not throw exception", false);
        }
    }

    /**
     * Test đăng nhập với username không tồn tại
     */
    public void testLoginWithNonExistentUsername()
    {
        System.out.println("\n========== TEST: Đăng nhập với username KHÔNG TỒN TẠI ==========");
        
        try {
            UserDAO userDAO = DAOFactory.getUserDAO();
            User loginResult = userDAO.login("nonexistent@user.com", "anypassword");
            
            if (loginResult == null) {
                System.out.println("✅ ĐẠT: Đăng nhập thất bại như kỳ vọng (user không tồn tại)");
                assertTrue("Should reject non-existent user", true);
            } else {
                System.err.println("❌ FAIL: Đăng nhập thành công với user không tồn tại!");
                assertTrue("Should reject non-existent user", false);
            }
            
        } catch (Exception e) {
            System.err.println("❌ EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            assertTrue("Should not throw exception", false);
        }
    }
}
