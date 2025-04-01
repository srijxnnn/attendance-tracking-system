import db.DatabaseConnection;
import pages.auth.UserAuthentication;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        new UserAuthentication().setVisible(true);
    }
}
