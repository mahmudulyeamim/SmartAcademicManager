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

public class SignUpController implements Initializable {

    @FXML
    private TextField signupUsername;
    @FXML
    private PasswordField signupPassword;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label errorLabel;

    private DatabaseManager dbManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbManager = new DatabaseManager();

        if(!dbManager.connectToUserDatabase()) {
            System.exit(1);
        }
    }

    @FXML
    void handleBackButton(ActionEvent actionEvent) {
        try {
            SceneController.changeScene("LoginScreen.fxml", actionEvent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    void handleDoneButton(ActionEvent actionEvent) throws IOException {
        Student student = new Student();
        student.setUsername(signupUsername.getText());
        student.setPassword(signupPassword.getText());
        String confirmedPassword = confirmPassword.getText();

        if (student.getUsername().isEmpty() || student.getPassword().isEmpty() || confirmedPassword.isEmpty()) {
            errorLabel.setText("Username or password is required");
        }
        else if(student.getPassword().length() < 3){
            errorLabel.setText("Invalid password. At least 3 characters needed");
        }
        else if(!student.getPassword().equals(confirmedPassword)) {
            errorLabel.setText("Again confirm your password");
        }
        else {
            try {
                if(!dbManager.signup(student.getUsername(), student.getPassword())) {
                    errorLabel.setText("Username is taken");
                    return;
                };
                SceneController.changeScene("LoginScreen.fxml", actionEvent);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
