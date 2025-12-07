package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String url = "jdbc:mysql://localhost:3306/habit_tracker";
    private static final String user = "root"; 
    private static final String password = "Phuc2006@"; 

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    // public static void main(String[] args) {
    //     getConnection();
    // }
}
