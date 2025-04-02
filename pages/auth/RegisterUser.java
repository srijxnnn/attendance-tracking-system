package pages.auth;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterUser
{
    int userID;
    String userName, userEmail, role;

    public void register(String username,String email,String password, String role)
    {
        String query="INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";

        try(Connection conn= DatabaseConnection.getInstance();
            PreparedStatement pstmt=conn.prepareStatement(query))
        {


            pstmt.setString(1,username);
            pstmt.setString(2,email);
            pstmt.setString(3,password);
            pstmt.setString(4, role);


            int rowsInserted=pstmt.executeUpdate();

            if(rowsInserted>0)
            {
                System.out.println("A new user was inserted successfully!");
            }

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public boolean login(String email, String password)
    {
        String query="SELECT * FROM users WHERE email = ? AND password = ?";

        try(Connection conn=DatabaseConnection.getInstance();
            PreparedStatement pstmt=conn.prepareStatement(query))
        {

            pstmt.setString(1,email);
            pstmt.setString(2,password);

            ResultSet rs=pstmt.executeQuery();

            if(rs.next())
            {
                this.userID=rs.getInt("id");
                this.userName = rs.getString("username");
                this.userEmail = rs.getString("email");
                this.role=rs.getString("role");
                return true;
            }

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }
    public int getUserID()
    {
        return this.userID;
    }
    public String getUserName()
    {
        return this.userName;
    }
    public String getUserEmail()
    {
        return this.userEmail;
    }
    public String getRoleType()
    {
        return this.role;
    }    
}