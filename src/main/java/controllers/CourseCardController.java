package controllers;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import models.Course;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import models.ImageTextPair;

public class CourseCardController {
    @FXML
    private Text courseName;

    @FXML
    private Text courseCode;

    @FXML
    private Text courseTeacherName;

    @FXML
    private AnchorPane topCover;

    @FXML
    private ImageView verticalThreeDots;

    private final String[] startColors = {"#A70E7D", "#3F666D", "#235B00", "#4C17A4", "#900C3F"};
    private final String[] endColors = {"#C83EA2", "#568B95", "#46871E", "#6C3DBD", "#C70039"};

    public ImageTextPair setData(Course course, int colorPicker) {
        courseName.setText(course.getCourseName());
        courseCode.setText(course.getCourseCode());
        courseTeacherName.setText(course.getCourseTeacherName());
        topCover.setStyle("-fx-background-color: " + startColors[colorPicker]);
        topCover.setStyle("-fx-background-color: linear-gradient(to top, " + startColors[colorPicker] + ", " + endColors[colorPicker] + ")");
        ImageTextPair imageTextPair = new ImageTextPair();
        imageTextPair.setImageView(verticalThreeDots);
        imageTextPair.setText(courseName);
        return imageTextPair;
    }
}
