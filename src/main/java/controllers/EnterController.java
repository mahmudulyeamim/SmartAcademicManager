package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class EnterController implements Initializable {

    @FXML
    private Label nameLabel;

    static String username;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameLabel.setText("Hello "+ username+"!");
    }

    public void handleEnterButton(ActionEvent actionEvent) {
        try {
            SceneController.changeScene("DashboardScreen.fxml", actionEvent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
