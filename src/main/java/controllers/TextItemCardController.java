package controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import models.ImageTextAnchorTriple;
import models.ToDoList;

public class TextItemCardController {
    @FXML
    private AnchorPane CompleteButton;

    @FXML
    private Text buttonStatusText;

    @FXML
    private Text ItemTextDescription;

    @FXML
    private AnchorPane TextItemCard;

    @FXML
    private ImageView verticalThreeDots;

    private final String[] startColors = {"#07A952", "#CB1F1F"};
    private final String[] endColors = {"#05B958", "#E02A2A"};
    private final String[] bgColors = {"#FFFFFF", "#F3FAFF"};

    public ImageTextAnchorTriple setData(ToDoList todolist, int colorPicker) {
        ItemTextDescription.setText(todolist.getItemTitle());
        if(todolist.getStatus()) {
            buttonStatusText.setText("Completed");
            CompleteButton.setStyle("-fx-background-color: linear-gradient(to right, " + startColors[0] + ", " + endColors[0] + ")");
        }
        else {
            buttonStatusText.setText("Pending");
            CompleteButton.setStyle("-fx-background-color: linear-gradient(to right, " + startColors[1] + ", " + endColors[1] + ")");
        }
        TextItemCard.setStyle("-fx-background-color: " + bgColors[colorPicker]);
        ImageTextAnchorTriple imageTextAnchorTriple = new ImageTextAnchorTriple();
        imageTextAnchorTriple.setImageView(verticalThreeDots);
        imageTextAnchorTriple.setText(buttonStatusText);
        imageTextAnchorTriple.setAnchorPane(CompleteButton);

        return imageTextAnchorTriple;
    }
}
