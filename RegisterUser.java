import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterUser 
{
    
    public void register(String username, String email, String password) 
    {
        String url="jdbc:mysql://localhost:3306/Mydb";
        String user="root";
        String dbPassword="MySQL@Espi0000";

        String query="INSERT INTO User (UserName, Email, Password) VALUES (?, ?, ?)";

        try(Connection conn=DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement pstmt=conn.prepareStatement(query)) 
        {

            
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            
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
        String url = "jdbc:mysql://localhost:3306/Mydb";
        String user = "root";
        String dbPassword = "MySQL@Espi0000";

        String query = "SELECT * FROM User WHERE Email = ? AND Password = ?";

        try(Connection conn=DriverManager.getConnection(url, user, dbPassword);
            PreparedStatement pstmt=conn.prepareStatement(query)) 
        {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) 
            {
                return true;
            }

        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return false;
    }
}
