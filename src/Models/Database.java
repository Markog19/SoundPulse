package Models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class Database extends DBConnection {

    private Statement query;
    private PreparedStatement execQuery;

    public Database () {
        super();
    }

    public ResultSet select (String sql) {
        try {
            this.query = this.connection.createStatement();
            return this.query.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println ("Query Error " + e.getMessage());
            return null;
        }
    }

    public PreparedStatement exec (String sql) {
        try {
            this.execQuery = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            return this.execQuery;
        } catch (SQLException e) {
            System.out.println("Query Error " + e.getMessage());
        }
        return null;
    }
}

