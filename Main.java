import db.DatabaseConnection;

import java.sql.*;

public class Main {
    public static void main(String[] args) {

        try {
            Connection conn = DatabaseConnection.getInstance();
            System.out.println("Connected!");

            // Query execution
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

            // Process result
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
            }
            rs.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
