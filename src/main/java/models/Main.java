package models;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Parent root;

        DatabaseManager dbManager = new DatabaseManager();
        if(!dbManager.connectToUserDatabase()) {
            System.exit(1);
        }

        if(dbManager.activeUsername() == null) {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginScreen.fxml")));
        }
        else {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("DashboardScreen.fxml")));
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Smart Academic Manager");
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}