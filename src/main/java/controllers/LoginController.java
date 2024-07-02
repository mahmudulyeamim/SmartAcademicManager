package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.DatabaseManager;
import models.Student;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label errorLabel;

    private DatabaseManager dbManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbManager = new DatabaseManager();

        if(!dbManager.connectToUserDatabase()) {
            System.exit(1);
        }

        try {
            if(dbManager.activeUsername() != null) {
                dbManager.logout();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void login(ActionEvent actionEvent) {
        Student student = new Student();
        student.setUsername(username.getText());
        student.setPassword(password.getText());
        try {
            if (dbManager.login(student.getUsername(), student.getPassword())) {
                EnterController.username = student.getUsername();
                SceneController.changeScene("EnterScreen.fxml", actionEvent);
            }
            else {
                errorLabel.setText("Invalid username or password!");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignupButton(ActionEvent actionEvent) throws IOException {
        SceneController.changeScene("SignupScreen.fxml", actionEvent);
    }
}
