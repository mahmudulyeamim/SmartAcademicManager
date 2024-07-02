package controllers;

import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import models.DatabaseManager;
import models.ResultSheet;
import models.Student;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class ResultsScreenController implements Initializable {

    @FXML
    private AnchorPane StudentProfileIcon;

    @FXML
    private AnchorPane attendance;
    @FXML
    private AnchorPane courses;
    @FXML
    private ImageView editProfileArrow;
    @FXML
    private AnchorPane editProfileButton;
    @FXML
    private Text editProfileText;
    @FXML
    private AnchorPane events;
    @FXML
    private ImageView logoutArrow;
    @FXML
    private AnchorPane logoutButton;
    @FXML
    private Text logoutText;
    @FXML
    private AnchorPane profileDropdown;
    @FXML
    private AnchorPane todolist;

    @FXML
    private TableView<ResultSheet> table;
    @FXML
    private TableColumn<ResultSheet, String> courseName;
    @FXML
    private TableColumn<ResultSheet, Double> evaluation;
    @FXML
    private TableColumn<ResultSheet, Double> finalExam;
    @FXML
    private TableColumn<ResultSheet, String> grade;
    @FXML
    private TableColumn<ResultSheet, Double> gradePoint;

    @FXML
    private TextField txt_cName;
    @FXML
    private TextField txt_evalMarks;
    @FXML
    private TextField txt_finalXMarks;
    @FXML
    private TextField txt_grade;
    @FXML
    private TextField txt_gradePoint;

    @FXML
    private AnchorPane booklist;

    @FXML
    private AnchorPane summary;

    @FXML
    private TextField editUsernameTextField;

    @FXML
    private TextField editPasswordTextField;

    @FXML
    private AnchorPane EditProfileCard;

    @FXML
    private AnchorPane saveEditProfileButton;

    @FXML
    private AnchorPane cancelEditProfileButton;

    @FXML
    private Label editPasswordWarningLabel;

    @FXML
    private Text usernameFirstLetter;

    @FXML
    private Text ProfileDropdownUsername;

    @FXML
    private Text ProfileDropdownUsernameFirstLetter;

    private boolean iconButtonPressed = true;

    ObservableList<ResultSheet> list;

    private DatabaseManager dbManager;

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

        courseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        evaluation.setCellValueFactory(new PropertyValueFactory<>("evaluation"));
        finalExam.setCellValueFactory(new PropertyValueFactory<>("finalExam"));
        grade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradePoint.setCellValueFactory(new PropertyValueFactory<>("gradePoint"));

        try {
            activeUsername = dbManager.activeUsername();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        usernameFirstLetter.setText(activeUsername.substring(0, 1).toUpperCase());
        ProfileDropdownUsername.setText(activeUsername);
        ProfileDropdownUsernameFirstLetter.setText(activeUsername.substring(0, 1).toUpperCase());

        handleEvents();

        try {
            setupTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateUser(MouseEvent mouseEvent) throws SQLException {
        Student newContent = new Student();
        newContent.setUsername(editUsernameTextField.getText());
        newContent.setPassword(editPasswordTextField.getText());

        dbManager.updateUser(newContent);

        try {
            SceneController.changeScene("ResultScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void handleEvents() {
        summary.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("DashboardScreen.fxml", mouseEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        courses.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("CourseScreen.fxml", mouseEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        attendance.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("AttendanceScreen.fxml", mouseEvent);
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
    @FXML
    void update(){
        ObservableList<ResultSheet> currentData = table.getItems();

        if(txt_cName.getText().isEmpty() || txt_evalMarks.getText().isEmpty() ||
            txt_finalXMarks.getText().isEmpty() || txt_grade.getText().isEmpty() ||
            txt_gradePoint.getText().isEmpty()) {
            return;
        }

        String currentCourse = txt_cName.getText();

        try{
            for (ResultSheet course : currentData) {
                if (Objects.equals(course.getCourseName(), currentCourse)) {
                    course.setEvaluation(Float.parseFloat(txt_evalMarks.getText()));
                    course.setFinalExam(Float.parseFloat(txt_finalXMarks.getText()));
                    course.setGrade(txt_grade.getText());
                    course.setGradePoint(Float.parseFloat(txt_gradePoint.getText()));

                    dbManager.updateResult(course);

                    table.setItems(currentData);
                    table.refresh();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        txt_cName.clear();
        txt_evalMarks.clear();
        txt_finalXMarks.clear();
        txt_grade.clear();
        txt_gradePoint.clear();
    }

    private void setupTable() throws SQLException {
        list = dbManager.getDataUsers();
        table.getItems().addAll(list);
    }
    @FXML
    public void rowClick() {
        ResultSheet clickResultSheet = table.getSelectionModel().getSelectedItem();
        txt_cName.setText(String.valueOf(clickResultSheet.getCourseName()));
        txt_evalMarks.setText(String.valueOf(clickResultSheet.getEvaluation()));
        txt_finalXMarks.setText(String.valueOf(clickResultSheet.getFinalExam()));
        txt_grade.setText(String.valueOf(clickResultSheet.getGrade()));
        txt_gradePoint.setText(String.valueOf(clickResultSheet.getGradePoint()));
    }

    void init() {
        profileDropdown.setVisible(false);
        EditProfileCard.setVisible(false);
        editPasswordWarningLabel.setVisible(false);
    }

}

