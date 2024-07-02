package controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import models.Book;
import javafx.scene.text.Text;
public class BookCardController {


    @FXML
    private Text BookName;

    @FXML
    private Text BookURL;

    @FXML
    private Text CourseName;


    @FXML
    private AnchorPane sidecover;

    @FXML
    private ImageView verticalThreeDots;

    private final String[] startColors = {"#A70E7D", "#3F666D", "#235B00", "#4C17A4", "#900C3F"};
    private final String[] endColors = {"#C83EA2", "#568B95", "#46871E", "#6C3DBD", "#C70039"};


    public ImageView setData(Book item, int colorPicker) {

        BookName.setText(item.getBookName());
        CourseName.setText(item.getCourseName());
        BookURL.setText(item.getBookURL());

        sidecover.setStyle("-fx-background-color: linear-gradient(to top left, " + startColors[colorPicker] + ", " + endColors[colorPicker] + ")");

        return verticalThreeDots;
    }
}

