package Models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoggedUser {
    public static boolean LogIn (String username, String password) {
        Database db = new Database();

        PreparedStatement ps = db.exec("SELECT * FROM user WHERE username =? AND "
                + "password=?");
        try {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

                return true;
            } else {
                return false;
            }
        } catch ( SQLException ex) {
            System.out.println("Error: "+ex.getMessage());
            return false;
        }
    }
}
