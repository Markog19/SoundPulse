package Models;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private String host;
    private String user;
    private String password;
    private String database;

    protected Connection connection;

    public DBConnection() {
        this.host = "localhost";
        this.user = "root";
        this.password = "";
        this.database = "db";
        this.connect();
    }

    public DBConnection(String host, String user, String lozinka, String baza) {
        this.host = host;
        this.user = user;
        this.password = lozinka;
        this.database = baza;
        this.connect();
    }

    public void connect () {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = (Connection) DriverManager.getConnection("jdbc:mysql://"+this.host+"/"+this.database +"?" + "user="+this.user +"&password="+this.password);
        } catch (ClassNotFoundException e) {
            System.out.println ("No JAR for MYSQL...");
        } catch (SQLException e) {
            System.out.println ("Database Connection Error...");
        }
    }

    public void disconnect () {
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println ("System failed to close the connection...");
        }
    }
}
