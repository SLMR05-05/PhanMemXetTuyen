package com.xettuyen;

import com.xettuyen.ui.LoginForm;
import javax.swing.*;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        // Cấu hình Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Khởi động LoginForm
        SwingUtilities.invokeLater(() -> {
            new LoginForm();
        });
    }
    

    
    /**
     * App - Main Entry Point
     * Khởi động Hệ Thống Xét Tuyển
     */
}
