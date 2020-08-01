package Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserModel {
    SimpleStringProperty ID =  new SimpleStringProperty();
    SimpleStringProperty username =  new SimpleStringProperty();
    SimpleStringProperty password =  new SimpleStringProperty();
    SimpleStringProperty isAdmin =  new SimpleStringProperty();

    public UserModel(String ID, String username, String password, String isAdmin) {
        this.ID = new SimpleStringProperty(ID);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.isAdmin = new SimpleStringProperty(isAdmin);
    }

    public String getID() {
        return ID.get();
    }

    public SimpleStringProperty IDProperty() {
        return ID;
    }

    public void setID(String ID) {
        this.ID.set(ID);
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getIsAdmin() {
        return isAdmin.get();
    }

    public SimpleStringProperty isAdminProperty() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin.set(isAdmin);
    }
    public static ObservableList<UserModel> listaKorisnika () {
        ObservableList<UserModel> lista = FXCollections.observableArrayList();
        Database DB = new Database();
        ResultSet rs = DB.select("SELECT * FROM user");
        try {
            while (rs.next()) {
                lista.add(new UserModel(rs.getString("ID"),rs.getString("username"), rs.getString("password"), rs.getString("isAdmin")));
            }
        } catch (SQLException ex) {
            System.out.println("Nastala je greška prilikom iteriranja: " + ex.getMessage());
        }
        return lista;
    }
}
