package controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import models.ImageTextAnchorTriple;
import models.Track;

public class TrackCardController {
    @FXML
    private AnchorPane CompleteButton;

    @FXML
    private Text buttonStatusText;

    @FXML
    private AnchorPane TextItemCard;

    @FXML
    private Text chapterName;

    @FXML
    private Text topicName;

    @FXML
    private ImageView verticalThreeDots;

    private final String[] startColors = {"#07A952", "#CB1F1F"};
    private final String[] endColors = {"#05B958", "#E02A2A"};
    private final String[] bgColors = {"#FFFFFF", "#F3FAFF"};

    public ImageTextAnchorTriple setData(Track track) {
        chapterName.setText(track.getChapterName());
        topicName.setText(track.getItemTitle());
        if(track.getStatus()) {
            buttonStatusText.setText("Studied");
            CompleteButton.setStyle("-fx-background-color: linear-gradient(to right, " + startColors[0] + ", " + endColors[0] + ")");
        }
        else {
            buttonStatusText.setText("Pending");
            CompleteButton.setStyle("-fx-background-color: linear-gradient(to right, " + startColors[1] + ", " + endColors[1] + ")");
        }
        TextItemCard.setStyle("-fx-background-color: " + bgColors[0]);

        ImageTextAnchorTriple imageTextAnchorTriple = new ImageTextAnchorTriple();
        imageTextAnchorTriple.setImageView(verticalThreeDots);
        imageTextAnchorTriple.setAnchorPane(CompleteButton);
        imageTextAnchorTriple.setText(buttonStatusText);

        return imageTextAnchorTriple;
    }
}