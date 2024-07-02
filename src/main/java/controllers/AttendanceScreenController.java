package controllers;


import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import models.Attendance;
import models.DatabaseManager;
import models.Student;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AttendanceScreenController implements Initializable {

    @FXML
    private AnchorPane inputPane;

    @FXML
    private TextField updateCourseNameTextField;

    @FXML
    private TextField updateAttendanceTextField;

    @FXML
    public BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private ImageView cross;
    @FXML
    private ImageView detailscross;


    @FXML
    private Label errorLabel;

    private DatabaseManager dbManager;

    @FXML
    private AnchorPane Details;

    @FXML
    private GridPane courseContainer2;

    @FXML
    private AnchorPane StudentProfileIcon;

    @FXML
    private ImageView editProfileArrow;

    @FXML
    private AnchorPane editProfileButton;

    @FXML
    private TextField editPasswordTextField;

    @FXML
    private Label editPasswordWarningLabel;


    @FXML
    private Text editProfileText;

    @FXML
    private TextField editUsernameTextField;

    @FXML
    private AnchorPane courses;

    @FXML
    private AnchorPane saveEditProfileButton;
    @FXML
    private AnchorPane cancelEditProfileButton;

    @FXML
    private AnchorPane events;

    @FXML
    private ImageView logoutArrow;

    @FXML
    private AnchorPane logoutButton;

    @FXML
    private Text logoutText;

    @FXML
    private TextField updateTotalclassesTextField;

    @FXML
    private AnchorPane profileDropdown;

    @FXML
    private AnchorPane results;

    @FXML
    private AnchorPane summary;


    @FXML
    private AnchorPane todolist;

    @FXML
    private AnchorPane booklist;

    private boolean iconButtonPressed = true;

    @FXML
    private AnchorPane EditProfileCard;

    @FXML
    private Text usernameFirstLetter;

    @FXML
    private Text ProfileDropdownUsername;

    @FXML
    private Text ProfileDropdownUsernameFirstLetter;

    private String activeUsername;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        dbManager = new DatabaseManager();

        if(!dbManager.connectToUserDatabase()) {
            System.exit(1);
        }

        try {
            if(!dbManager.connectToActiveDatabase()) {
                System.exit(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        init();
        try {
            loadData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        xAxis.setTickLabelsVisible(false);
        cross.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleCancelButtonAction();
            }
        });

        detailscross.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                closeDetailsPane();
            }
        });

        try {
            activeUsername = dbManager.activeUsername();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        usernameFirstLetter.setText(activeUsername.substring(0, 1).toUpperCase());
        ProfileDropdownUsername.setText(activeUsername);
        ProfileDropdownUsernameFirstLetter.setText(activeUsername.substring(0, 1).toUpperCase());

        // event-handling
        handleEvents();
    }

    private void init_Card(){
        List<Attendance> courseDetails;

        try {
            courseDetails = new ArrayList<>(currentAttendancelist());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        int columns = 0;
        int rows = 1;
        int colorPicker = 0;


        try {
            for(Attendance item : courseDetails) {

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/models/AttendanceCard.fxml"));
                AnchorPane AttendanceCard = loader.load();
                AttendanceCardController attendanceCardController = loader.getController();
                attendanceCardController.setData(item, colorPicker++);


                if (colorPicker == 2) {
                    colorPicker = 0;
                }

                courseContainer2.add(AttendanceCard, columns, rows++);
                GridPane.setMargin(AttendanceCard, new Insets(2));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Attendance> currentAttendancelist() throws SQLException {
        return dbManager.currentAttendancelist();
    }

    public void openDetailsPane() {
        init_Card();
        Details.setVisible(!Details.isVisible());
    }

    public void closeDetailsPane() {
        Details.setVisible(!Details.isVisible());

    }

    public void openInputPane() {
        init();
        showErrorMessage("");
        inputPane.setVisible(!inputPane.isVisible());

    }

    public void handleCancelButtonAction() {
        init();
        inputPane.setVisible(false);
    }


    @FXML
    private void handleUpdateButton() throws SQLException {

        String courseName = updateCourseNameTextField.getText();
        String attendanceStr = updateAttendanceTextField.getText();
        String totalClassesStr=updateTotalclassesTextField.getText();

        if (courseName.isEmpty() || attendanceStr.isEmpty() || totalClassesStr.isEmpty()) {
            showErrorMessage("*Please fill in all fields!");
            return;
        }

        int attendance,totalClasses;
        try {
            attendance = Integer.parseInt(attendanceStr);
            totalClasses=Integer.parseInt(totalClassesStr);
        } catch (NumberFormatException ex) {
            showErrorMessage("*Attendance must be an integer!");
            return;
        }

        boolean courseFound;
        courseFound = dbManager.isCourseFound(courseName);

        if (!courseFound) {
            showErrorMessage("*Course not found!");
            return;
        }

        showErrorMessage("");

        dbManager.UpdateAttendance(attendance,totalClasses, courseName);

        for (XYChart.Series<String, Number> series : barChart.getData()) {
            if (series.getName().equals(courseName)) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    data.setYValue((attendance*100)/totalClasses);
                }
                break;
            }
        }

        // Hide update pane
        inputPane.setVisible(false);
    }



    public void loadData() throws SQLException {

        barChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        BarChart<String, Number> barChart = dbManager.loadAttendance();
        if (barChart != null) {
            this.barChart.getData().clear();
            this.barChart.getData().addAll(barChart.getData());
        }
    }



    private void showErrorMessage(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }


    private void updateUser(MouseEvent mouseEvent) throws SQLException {
        Student newContent = new Student();
        newContent.setUsername(editUsernameTextField.getText());
        newContent.setPassword(editPasswordTextField.getText());

        dbManager.updateUser(newContent);

        try {
            SceneController.changeScene("AttendanceScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void handleEvents() {

        // sidebar navigation
        summary.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                init();
                SceneController.changeScene("DashboardScreen.fxml", mouseEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        courses.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                init();
                SceneController.changeScene("CourseScreen.fxml", mouseEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        results.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("ResultScreen.fxml", mouseEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        events.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("EventsScreen.fxml", mouseEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        todolist.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                init();
                SceneController.changeScene("ToDoListScreen.fxml", mouseEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        booklist.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("Booklist.fxml", mouseEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // profileIconClick
        StudentProfileIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            profileDropdown.setVisible(iconButtonPressed);
            iconButtonPressed ^= true;
        });

        // profileDropdownInfoHover

        // edit profile
        editProfileButton.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> {
            editProfileText.pseudoClassStateChanged(PseudoClass.getPseudoClass("hovered"), true);
            editProfileArrow.setTranslateX(4);
        });
        editProfileButton.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
            editProfileText.pseudoClassStateChanged(PseudoClass.getPseudoClass("hovered"), false);
            editProfileArrow.setTranslateX(0);
        });
        editProfileButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            try {
                Student student = dbManager.activeUser();
                editUsernameTextField.setText(student.getUsername());
                editPasswordTextField.setText(student.getPassword());

                EditProfileCard.setVisible(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        cancelEditProfileButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            editUsernameTextField.clear();
            editPasswordTextField.clear();
            iconButtonPressed ^= true;
        });
        saveEditProfileButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            EditProfileCard.setVisible(true);
            editPasswordWarningLabel.setVisible(editPasswordTextField.getText().isEmpty());

            if(!editUsernameTextField.getText().isEmpty() && !editPasswordTextField.getText().isEmpty()) {
                if(editUsernameTextField.getText().equals(activeUsername)) {
                    init();
                    try {
                        updateUser(mouseEvent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });



        // logout
        logoutButton.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> {
            logoutText.pseudoClassStateChanged(PseudoClass.getPseudoClass("hovered"), true);
            logoutArrow.setTranslateX(4);
        });
        logoutButton.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
            logoutText.pseudoClassStateChanged(PseudoClass.getPseudoClass("hovered"), false);
            logoutArrow.setTranslateX(0);
        });
        logoutButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            dbManager.logout();
            try {
                SceneController.changeScene("LoginScreen.fxml", mouseEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void init(){
        Details.setVisible(false);
        inputPane.setVisible(false);
        profileDropdown.setVisible(false);

        updateCourseNameTextField.clear();
        updateAttendanceTextField.clear();
        updateTotalclassesTextField.clear();

        EditProfileCard.setVisible(false);
    }
}


