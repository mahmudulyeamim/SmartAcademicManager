package controllers;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import models.Course;
import models.DatabaseManager;
import models.ImageTextPair;
import models.Student;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CourseScreenController implements Initializable {
    @FXML
    private AnchorPane summary;

    @FXML
    private AnchorPane attendance;

    @FXML
    private AnchorPane events;

    @FXML
    private AnchorPane results;

    @FXML
    private AnchorPane todolist;

    @FXML
    private GridPane courseContainer;

    @FXML
    private AnchorPane StudentProfileIcon;

    @FXML
    private AnchorPane profileDropdown;

    @FXML
    private AnchorPane editProfileButton;

    @FXML
    private ImageView editProfileArrow;

    @FXML
    private Text editProfileText;

    @FXML
    private ImageView logoutArrow;

    @FXML
    private AnchorPane logoutButton;

    @FXML
    private Text logoutText;

    @FXML
    private AnchorPane addConfirmationButton;

    @FXML
    private AnchorPane addCourseCard;

    @FXML
    private AnchorPane cancelConfirmationButton;

    @FXML
    private AnchorPane addNewElementButton;

    @FXML
    private TextField courseCodeTextField;

    @FXML
    private TextField courseNameTextField;

    @FXML
    private TextField courseTeacherTextField;

    @FXML
    private Label courseNameWarningLabel;

    @FXML
    private Label courseCodeWarningLabel;

    @FXML
    private Label courseTeacherWarningLabel;

    @FXML
    private AnchorPane DeleteUpdateAnchorpane;

    @FXML
    private AnchorPane updateCourseButton;

    @FXML
    private AnchorPane deleteCourseButton;

    @FXML
    private AnchorPane deleteCourseConfirmationCard;

    @FXML
    private AnchorPane YesDeleteCourseButton;

    @FXML
    private AnchorPane NoDeleteCourseButton;

    @FXML
    private AnchorPane updateConfirmationButton1;

    @FXML
    private AnchorPane updateCourseCard1;

    @FXML
    private AnchorPane cancelConfirmationButton1;

    @FXML
    private TextField courseCodeTextField1;

    @FXML
    private TextField courseNameTextField1;

    @FXML
    private TextField courseTeacherTextField1;

    @FXML
    private Label courseNameWarningLabel1;

    @FXML
    private Label courseCodeWarningLabel1;

    @FXML
    private Label courseTeacherWarningLabel1;

    @FXML
    private AnchorPane EditProfileCard;

    @FXML
    private AnchorPane cancelEditProfileButton;

    @FXML
    private TextField editPasswordTextField;

    @FXML
    private TextField editUsernameTextField;

    @FXML
    private Label editPasswordWarningLabel;

    @FXML
    private Label courseAddWarningLabel;

    @FXML
    private Label courseAddWarningLabel1;

    @FXML
    private AnchorPane saveEditProfileButton;

    @FXML
    private Text usernameFirstLetter;

    @FXML
    private Text ProfileDropdownUsername;

    @FXML
    private Text ProfileDropdownUsernameFirstLetter;

    @FXML
    private AnchorPane booklist;


    private List<Course> currentCourse;

    private List<ImageView> currentVerticalThreeDots;

    private List<Text> currentCourseNameTexts;

    private int courseNumberToDelete;

    private int courseNumberToUpdate;

    private boolean iconButtonPressed = true;

    private String activeUsername;

    private DatabaseManager dbManager;

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

        try {
            currentCourse = new ArrayList<>(currentCourseList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        currentVerticalThreeDots = new ArrayList<>();
        currentCourseNameTexts = new ArrayList<>();

        int columns = 0;
        int rows = 1;
        int colorPicker = 0;

        try {
            for(Course course : currentCourse) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/models/CourseCard.fxml"));
                AnchorPane courseCard = loader.load();
                CourseCardController courseCardController = loader.getController();
                ImageTextPair imageTextPair = courseCardController.setData(course, colorPicker++);
                currentVerticalThreeDots.add(imageTextPair.getImageView());
                currentCourseNameTexts.add(imageTextPair.getText());

                if (columns == 2) {
                    columns = 0;
                    rows++;
                }

                if (colorPicker == 5) {
                    colorPicker = 0;
                }

                courseContainer.add(courseCard, columns++, rows);
                GridPane.setMargin(courseCard, new Insets(14));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private void updateUser(MouseEvent mouseEvent) throws SQLException {
        Student newContent = new Student();
        newContent.setUsername(editUsernameTextField.getText());
        newContent.setPassword(editPasswordTextField.getText());

        dbManager.updateUser(newContent);

        try {
            SceneController.changeScene("CourseScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Course> currentCourseList() throws SQLException {
        return dbManager.currentCourseList();
    }

    private void addNewCourse(MouseEvent mouseEvent) throws SQLException {
        Course course = new Course();
        course.setCourseName(courseNameTextField.getText());
        course.setCourseCode(courseCodeTextField.getText());
        course.setCourseTeacherName(courseTeacherTextField.getText());

        if(!dbManager.addNewCourse(course)) {
            courseAddWarningLabel.setVisible(true);
            return;
        }

        try {
            SceneController.changeScene("CourseScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteCourse(MouseEvent mouseEvent) throws SQLException {
        dbManager.deleteCourse(currentCourse.get(courseNumberToDelete));

        try {
            SceneController.changeScene("CourseScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateCourse(MouseEvent mouseEvent) throws SQLException {
        Course oldContent = currentCourse.get(courseNumberToUpdate);

        Course newContent = new Course();
        newContent.setCourseName(courseNameTextField1.getText());
        newContent.setCourseCode(courseCodeTextField1.getText());
        newContent.setCourseTeacherName(courseTeacherTextField1.getText());

        if(!dbManager.updateCourse(oldContent, newContent)) {
            courseAddWarningLabel1.setVisible(true);
            return;
        }

        try {
            SceneController.changeScene("CourseScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void enterCourse(int courseNumber, MouseEvent mouseEvent) {
        try {
            dbManager.updateCourseActiveStatus(currentCourseList().get(courseNumber), 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            SceneController.changeScene("CourseTrackScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleEvents() {

        // sidebar navigation
        summary.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                init();
                SceneController.changeScene("DashboardScreen.fxml", mouseEvent);
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
            init();
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

        // add new course button clicked
        addNewElementButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            addCourseCard.setVisible(true);
        });

        // confirmation button clicked
        cancelConfirmationButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            courseNameTextField.clear();
            courseCodeTextField.clear();
            courseTeacherTextField.clear();
        });
        addConfirmationButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            addCourseCard.setVisible(true);
            courseNameWarningLabel.setVisible(courseNameTextField.getText().isEmpty());
            courseCodeWarningLabel.setVisible(courseCodeTextField.getText().isEmpty());
            courseTeacherWarningLabel.setVisible(courseTeacherTextField.getText().isEmpty());
            if(!courseNameTextField.getText().isEmpty() && !courseCodeTextField.getText().isEmpty() && !courseTeacherTextField.getText().isEmpty()) {
                try {
                    addNewCourse(mouseEvent);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // vertical three dots
        for(int i = 0; i < currentVerticalThreeDots.size(); i++) {
            int courseNumber = i;

            currentVerticalThreeDots.get(courseNumber).addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> {
                currentVerticalThreeDots.get(courseNumber).setFitHeight(22);
                currentVerticalThreeDots.get(courseNumber).setFitWidth(22);
            });
        }

        for(int i = 0; i < currentVerticalThreeDots.size(); i++) {
            int courseNumber = i;

            currentVerticalThreeDots.get(courseNumber).addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
                currentVerticalThreeDots.get(courseNumber).setFitHeight(20);
                currentVerticalThreeDots.get(courseNumber).setFitWidth(20);
            });
        }

        for(int i = 0; i < currentVerticalThreeDots.size(); i++) {
            int courseNumber = i;

            currentVerticalThreeDots.get(courseNumber).addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                init();
                DeleteUpdateAnchorpane.setVisible(true);
                courseNumberToDelete = courseNumber;
                courseNumberToUpdate = courseNumber;
            });
        }

        // delete course
        deleteCourseButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            deleteCourseConfirmationCard.setVisible(true);
        });

        YesDeleteCourseButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            try {
                deleteCourse(mouseEvent);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        NoDeleteCourseButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> init());

        // update course
        updateCourseButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();

            Course course = currentCourse.get(courseNumberToUpdate);
            courseNameTextField1.setText(course.getCourseName());
            courseCodeTextField1.setText(course.getCourseCode());
            courseTeacherTextField1.setText(course.getCourseTeacherName());

            updateCourseCard1.setVisible(true);
        });

        cancelConfirmationButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            courseNameTextField1.clear();
            courseCodeTextField1.clear();
            courseTeacherTextField1.clear();
        });

        updateConfirmationButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            updateCourseCard1.setVisible(true);
            courseNameWarningLabel1.setVisible(courseNameTextField1.getText().isEmpty());
            courseCodeWarningLabel1.setVisible(courseCodeTextField1.getText().isEmpty());
            courseTeacherWarningLabel1.setVisible(courseTeacherTextField1.getText().isEmpty());
            if(!courseNameTextField1.getText().isEmpty() && !courseCodeTextField1.getText().isEmpty() && !courseTeacherTextField1.getText().isEmpty()) {
                try {
                    updateCourse(mouseEvent);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        for(int i = 0; i < currentCourseNameTexts.size(); i++) {
            int courseNumber = i;

            currentCourseNameTexts.get(courseNumber).addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> enterCourse(courseNumber, mouseEvent));
        }

    }

    void init() {
        profileDropdown.setVisible(false);
        courseNameWarningLabel.setVisible(false);
        courseCodeWarningLabel.setVisible(false);
        courseTeacherWarningLabel.setVisible(false);
        DeleteUpdateAnchorpane.setVisible(false);
        addCourseCard.setVisible(false);
        deleteCourseConfirmationCard.setVisible(false);
        updateCourseCard1.setVisible(false);
        courseNameWarningLabel.setVisible(false);
        courseCodeWarningLabel.setVisible(false);
        courseTeacherWarningLabel.setVisible(false);
        courseNameWarningLabel1.setVisible(false);
        courseCodeWarningLabel1.setVisible(false);
        courseTeacherWarningLabel1.setVisible(false);
        EditProfileCard.setVisible(false);
        editPasswordWarningLabel.setVisible(false);

        courseAddWarningLabel.setVisible(false);
        courseAddWarningLabel1.setVisible(false);
    }

}

