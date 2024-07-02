package controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import models.Assignment;
import models.ImageTextAnchorTriple;

import java.util.Random;

public class AssignmentCardController {
    @FXML
    private AnchorPane CompleteButton;

    @FXML
    private Text buttonStatusText;

    @FXML
    private Text assignmentDescription;

    @FXML
    private Text assignmentDueTime;

    @FXML
    private Text assignmentName;

    @FXML
    private AnchorPane sideCover;

    @FXML
    private ImageView verticalThreeDots;

    private final String[] startColorsOfCompleteButton = {"#07A952", "#CB1F1F"};
    private final String[] endColorsOfCompleteButton = {"#05B958", "#E02A2A"};

    private final String[] startColorsOfSideCover = {"#F66E39", "#23D63C", "#E31414"};
    private final String[] endColorsOfSideCover = {"#FF7640", "#21E23C", "#F21B1B"};

    public ImageTextAnchorTriple setData(Assignment assignment) {
        assignmentName.setText(assignment.getItemTitle());
        assignmentDescription.setText(assignment.getDescription());

        String s = assignment.getDueDate().substring(0, 2) + " " +
                assignment.getDueDate().substring(3, 6);

        assignmentDueTime.setText("Due " + s + ", " + assignment.getDueTime());

        if(assignment.getStatus()) {
            buttonStatusText.setText("Submitted");
            CompleteButton.setStyle("-fx-background-color: linear-gradient(to right, " + startColorsOfCompleteButton[0] + ", " + endColorsOfCompleteButton[0] + ")");
        }
        else {
            buttonStatusText.setText("Pending");
            CompleteButton.setStyle("-fx-background-color: linear-gradient(to right, " + startColorsOfCompleteButton[1] + ", " + endColorsOfCompleteButton[1] + ")");
        }

        Random random = new Random();
        int index = random.nextInt(3);
        sideCover.setStyle("-fx-background-color: linear-gradient(to right, " + startColorsOfSideCover[index] + ", " + endColorsOfSideCover[index] + ")");

        ImageTextAnchorTriple imageTextAnchorTriple = new ImageTextAnchorTriple();
        imageTextAnchorTriple.setImageView(verticalThreeDots);
        imageTextAnchorTriple.setAnchorPane(CompleteButton);
        imageTextAnchorTriple.setText(buttonStatusText);

        return imageTextAnchorTriple;
    }
}