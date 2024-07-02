package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import models.Attendance;

public class AttendanceCardController {

    @FXML
    private AnchorPane AttendanceCard;

    @FXML
    private Text attendedClasses;

    @FXML
    private Text courseCode;

    @FXML
    private Text courseName;

    @FXML
    private Text teacherName;

    @FXML
    private Text percentage;

    @FXML
    private Text totalClasses;

    private final String[] bgColors = {"#FFFFFF", "#F3FAFF"};

    public void setData(Attendance item, int colorPicker) {
        courseName.setText(item.getCourseName());
        courseCode.setText(item.getCourseCode());
        teacherName.setText(item.getCourseTeacherName());
        totalClasses.setText(String.valueOf(item.getTotalClasses()));
        attendedClasses.setText(String.valueOf(item.getAttendedClasses()));
        int Tclass=item.getTotalClasses();
        if(Tclass==0)
            percentage.setText("100");
        else
            percentage.setText(String.valueOf((item.getAttendedClasses()*100)/item.getTotalClasses()));
        AttendanceCard.setStyle("-fx-background-color: " + bgColors[colorPicker]);
    }
}

