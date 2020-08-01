package Controllers;

import Models.Database;
import Models.LoggedUser;
import Models.UserModel;
import com.sun.javafx.logging.PlatformLogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class LoginController implements Initializable {
    @FXML
    TextField usernameText;
    @FXML
    PasswordField passwordText;
    @FXML
    Label statusLabel;
    public void logIn(ActionEvent e) throws SQLException, ClassNotFoundException {
        String username = usernameText.getText();
        String password = passwordText.getText();

        if (username.equals("") || password.equals("")) {
            statusLabel.setText("Morate unijeti sve vrijednosti!");
        } else {
            if (LoggedUser.LogIn(username, password)) {
                try {
                    Database DB = new Database();
                    statusLabel.setTextFill(Color.GREEN);
                    statusLabel.setText("Uspješno ste se prijavili");
                    System.out.println("AAA");
                    PreparedStatement ps = DB.exec("SELECT * FROM user WHERE username =?");
                    ps.setString(1,username);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        System.out.println("AAA");

                        int id = rs.getInt("isAdmin");
                        System.out.println(id);
                        if (id == 1) {
                            Parent root;
                            root = FXMLLoader.load(getClass().getClassLoader().getResource("Views/app.fxml"));
                            Stage stage = new Stage();
                            stage.setTitle("Prikaz studenta");
                            stage.setScene(new Scene(root, 1120, 600));
                            stage.show();
                        } if(id == 0) {
                            System.out.println("AAA");
                            Parent root;
                            root = FXMLLoader.load(getClass().getClassLoader().getResource("Views/app.fxml"));
                            System.out.println("AAA");
                            Stage stage = new Stage();
                            stage.setTitle("Prikaz studenta");
                            stage.setScene(new Scene(root, 600, 600));
                            stage.show();
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LoginController.class.getName());
                }
            } else {
                statusLabel.setText("Korisnicki podatci nisu ispravni!");
            }
        }
    }

    public void initialize(URL url, ResourceBundle rb) {


        // TODO
    }
}
