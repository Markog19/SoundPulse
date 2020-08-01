import Models.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Application.launch;

public class App extends Application {
    public void start(Stage primaryStage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Views/Login.fxml"));

        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Prijavite se na sustav!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
