package models;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://www.papademas.net:3307/510fp?autoReconnect=true&useSSL=false";
    private static final String USER = "fp510";
    private static final String PASSWORD = "510";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM user_accountMT WHERE username = ? AND password_hash = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // In practice, use hashed passwords
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // If a row is returned, authentication is successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}