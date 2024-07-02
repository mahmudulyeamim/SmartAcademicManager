package controllers;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import models.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class CourseAssignmentScreenController implements Initializable {

    @FXML
    private AnchorPane DeleteUpdateAnchorpane;

    @FXML
    private AnchorPane NoDeleteCourseButton;

    @FXML
    private Text ProfileDropdownUsername;

    @FXML
    private Text ProfileDropdownUsernameFirstLetter;

    @FXML
    private AnchorPane StudentProfileIcon;

    @FXML
    private Text TrackScreenCourseName;

    @FXML
    private AnchorPane YesDeleteCourseButton;

    @FXML
    private AnchorPane addConfirmationButton;

    @FXML
    private AnchorPane addCourseCard;

    @FXML
    private AnchorPane addNewElementButton;

    @FXML
    private TextField assignmentDescriptionTextField;

    @FXML
    private TextField assignmentDescriptionTextField1;

    @FXML
    private Label assignmentDescriptionWarningLabel;

    @FXML
    private Label assignmentDescriptionWarningLabel1;

    @FXML
    private TextField assignmentTitleTextField;

    @FXML
    private TextField assignmentTitleTextField1;

    @FXML
    private Label assignmentTitleWarningLabel;

    @FXML
    private Label assignmentTitleWarningLabel1;

    @FXML
    private AnchorPane attendance;

    @FXML
    private AnchorPane backToCourseScreenButton;

    @FXML
    private AnchorPane cancelConfirmationButton;

    @FXML
    private AnchorPane cancelConfirmationButton1;

    @FXML
    private GridPane courseContainer;

    @FXML
    private AnchorPane deleteCourseButton;

    @FXML
    private AnchorPane deleteCourseConfirmationCard;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private DatePicker dueDatePicker1;

    @FXML
    private Label dueDateWarningLabel;

    @FXML
    private Label dueDateWarningLabel1;

    @FXML
    private ComboBox<String> dueFormateComboBox;

    @FXML
    private ComboBox<String> dueFormateComboBox1;

    @FXML
    private ComboBox<String> dueHourComboBox;

    @FXML
    private AnchorPane booklist;

    @FXML
    private ComboBox<String> dueHourComboBox1;

    @FXML
    private ComboBox<String> dueMinComboBox;

    @FXML
    private ComboBox<String> dueMinComboBox1;

    @FXML
    private Label dueTimeWarningLabel;

    @FXML
    private Label dueTimeWarningLabel1;

    @FXML
    private ImageView editProfileArrow;

    @FXML
    private AnchorPane editProfileButton;

    @FXML
    private Text editProfileText;

    @FXML
    private Text emptyContainerWarning;

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
    private AnchorPane results;

    @FXML
    private AnchorPane summary;

    @FXML
    private AnchorPane todolist;

    @FXML
    private AnchorPane trackScreenButton;

    @FXML
    private AnchorPane updateConfirmationButton1;

    @FXML
    private AnchorPane updateCourseButton;

    @FXML
    private AnchorPane updateCourseCard1;

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

    private DatabaseManager dbManager;

    private boolean iconButtonPressed = true;

    private Course activeCourse;

    private List<Assignment> currentAssignments;

    private List<ImageView> currentVerticalThreeDots;

    private List<AnchorPane> currentCompleteButtons;

    private List<Text> currentButtonStatusText;

    private int assignmentNumberToDelete;
    private int assignmentNumberToUpdate;

    private String activeUsername;

    private final String[] twelveHour = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12"};
    private final String[] sixtyMinutes = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
            "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59"};
    private final String[] formatOfTime = {"AM", "PM"};

    String[] startColors = {"#07A952", "#CB1F1F"};
    String[] endColors = {"#05B958", "#E02A2A"};

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
            activeCourse = dbManager.activeCourse();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        TrackScreenCourseName.setText(activeCourse.getCourseCode());

        try {
            currentAssignments = new ArrayList<>(currentAssignmentsList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Collections.sort(currentAssignments, new AssignmentTimeComparator());

        initComboBox();

        if(currentAssignments.isEmpty()) {
            emptyContainerWarning.setVisible(true);
        }

        currentVerticalThreeDots = new ArrayList<>();
        currentCompleteButtons = new ArrayList<>();
        currentButtonStatusText = new ArrayList<>();

        int columns = 0;
        int rows = 1;


        try {
            for(Assignment assignment : currentAssignments) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/models/AssignmentCard.fxml"));
                AnchorPane assignmentCard = loader.load();
                AssignmentCardController assignmentCardController = loader.getController();
                ImageTextAnchorTriple imageTextAnchorTriple =  assignmentCardController.setData(assignment);
                currentVerticalThreeDots.add(imageTextAnchorTriple.getImageView());
                currentCompleteButtons.add(imageTextAnchorTriple.getAnchorPane());
                currentButtonStatusText.add(imageTextAnchorTriple.getText());

                courseContainer.add(assignmentCard, columns, rows++);
                GridPane.setMargin(assignmentCard, new Insets(14));
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

        handleEvents();
    }

    private void initComboBox() {
        dueHourComboBox.getItems().addAll(twelveHour);
        dueMinComboBox.getItems().addAll(sixtyMinutes);
        dueFormateComboBox.getItems().addAll(formatOfTime);

        dueFormateComboBox.setValue("AM");

        dueHourComboBox1.getItems().addAll(twelveHour);
        dueMinComboBox1.getItems().addAll(sixtyMinutes);
        dueFormateComboBox1.getItems().addAll(formatOfTime);
    }

    private void updateUser(MouseEvent mouseEvent) throws SQLException {
        Student newContent = new Student();
        newContent.setUsername(editUsernameTextField.getText());
        newContent.setPassword(editPasswordTextField.getText());

        dbManager.updateUser(newContent);

        try {
            SceneController.changeScene("CourseAssignmentScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Assignment> currentAssignmentsList() throws SQLException {
        return dbManager.currentAssignmentList(activeCourse);
    }

    private void backToCourseScreen(MouseEvent mouseEvent) {
        try {
            dbManager.updateCourseActiveStatus(activeCourse, 0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            SceneController.changeScene("CourseScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void enterTrackScreen(MouseEvent mouseEvent) {
        try {
            SceneController.changeScene("CourseTrackScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addNewAssignment(MouseEvent mouseEvent) throws SQLException {
        Assignment assignment = new Assignment();
        assignment.setItemTitle(assignmentTitleTextField.getText());
        assignment.setDescription(assignmentDescriptionTextField.getText());
        assignment.setDueDate(dueDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        assignment.setDueTime(dueHourComboBox.getValue() + ":" + dueMinComboBox.getValue() + " " + dueFormateComboBox.getValue());
        dbManager.addNewAssignment(assignment, activeCourse);

        try {
            SceneController.changeScene("CourseAssignmentScreen.fxml", mouseEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteAssignment(MouseEvent mouseEvent) throws SQLException {
        dbManager.deleteAssignment(currentAssignments.get(assignmentNumberToDelete), activeCourse);

        try {
            SceneController.changeScene("CourseAssignmentScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateAssignment(MouseEvent mouseEvent) throws SQLException {
        Assignment oldContent = currentAssignments.get(assignmentNumberToUpdate);

        Assignment newContent = new Assignment();
        newContent.setItemTitle(assignmentTitleTextField1.getText());
        newContent.setDescription(assignmentDescriptionTextField1.getText());
        newContent.setDueDate(dueDatePicker1.getValue().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        newContent.setDueTime(dueHourComboBox1.getValue() + ":" + dueMinComboBox1.getValue() + " " + dueFormateComboBox1.getValue());
        newContent.setStatus(oldContent.getStatus());

        dbManager.updateAssignment(oldContent, newContent, activeCourse);

        try {
            SceneController.changeScene("CourseAssignmentScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateCompleteButton() throws SQLException {
        Assignment oldContent = currentAssignments.get(assignmentNumberToUpdate);

        Assignment newContent = new Assignment();
        newContent.setItemTitle(oldContent.getItemTitle());
        newContent.setDescription(oldContent.getDescription());
        newContent.setDueDate(oldContent.getDueDate());
        newContent.setDueTime(oldContent.getDueTime());
        newContent.setStatus(!oldContent.getStatus());

        dbManager.updateAssignment(oldContent, newContent, activeCourse);

        if(!oldContent.getStatus()) {
            currentButtonStatusText.get(assignmentNumberToUpdate).setText("Submitted");
            currentCompleteButtons.get(assignmentNumberToUpdate).setStyle("-fx-background-color: linear-gradient(to right, " + startColors[0] + ", " + endColors[0] + ")");
        }
        else {
            currentButtonStatusText.get(assignmentNumberToUpdate).setText("Pending");
            currentCompleteButtons.get(assignmentNumberToUpdate).setStyle("-fx-background-color: linear-gradient(to right, " + startColors[1] + ", " + endColors[1] + ")");
        }

        oldContent.setStatus(newContent.getStatus());
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

        // add new task button clicked
        addNewElementButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            addCourseCard.setVisible(true);
        });

        // confirmation button clicked
        cancelConfirmationButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            assignmentTitleTextField.clear();
            assignmentDescriptionTextField.clear();
            dueDatePicker.setValue(null);
            dueHourComboBox.setValue(null);
            dueMinComboBox.setValue(null);
            dueFormateComboBox.setValue(null);

            dueFormateComboBox.setValue("AM");

        });
        addConfirmationButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            addCourseCard.setVisible(true);
            assignmentTitleWarningLabel.setVisible(assignmentTitleTextField.getText().isEmpty());
            assignmentDescriptionWarningLabel.setVisible(assignmentDescriptionTextField.getText().isEmpty());
            dueDateWarningLabel.setVisible(dueDatePicker.getValue() == null);
            dueTimeWarningLabel.setVisible(dueHourComboBox.getValue() == null ||
                    dueMinComboBox.getValue() == null || dueFormateComboBox.getValue() == null);

            if(!assignmentTitleTextField.getText().isEmpty() && !assignmentDescriptionTextField.getText().isEmpty() &&
                    dueDatePicker.getValue() != null && dueHourComboBox.getValue() != null &&
                    dueMinComboBox.getValue() != null && dueFormateComboBox.getValue() != null) {
                init();
                try {
                    addNewAssignment(mouseEvent);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // vertical three dots
        for(int i = 0; i < currentVerticalThreeDots.size(); i++) {
            int assignmentNumber = i;

            currentVerticalThreeDots.get(assignmentNumber).addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> {
                currentVerticalThreeDots.get(assignmentNumber).setFitHeight(22);
                currentVerticalThreeDots.get(assignmentNumber).setFitWidth(22);
            });
        }

        for(int i = 0; i < currentVerticalThreeDots.size(); i++) {
            int assignmentNumber = i;

            currentVerticalThreeDots.get(assignmentNumber).addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
                currentVerticalThreeDots.get(assignmentNumber).setFitHeight(20);
                currentVerticalThreeDots.get(assignmentNumber).setFitWidth(20);
            });
        }

        for(int i = 0; i < currentVerticalThreeDots.size(); i++) {
            int assignmentNumber = i;

            currentVerticalThreeDots.get(assignmentNumber).addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                init();
                DeleteUpdateAnchorpane.setVisible(true);
                assignmentNumberToDelete = assignmentNumber;
                assignmentNumberToUpdate = assignmentNumber;
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
                deleteAssignment(mouseEvent);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        NoDeleteCourseButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> init());

        // update
        updateCourseButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();

            Assignment assignment = currentAssignments.get(assignmentNumberToUpdate);

            assignmentTitleTextField1.setText(assignment.getItemTitle());
            assignmentDescriptionTextField1.setText(assignment.getDescription());
            dueDatePicker1.setValue(LocalDate.parse(assignment.getDueDate(), DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
            dueHourComboBox1.setValue(assignment.getDueTime().substring(0, 2));
            dueMinComboBox1.setValue(assignment.getDueTime().substring(3, 5));
            dueFormateComboBox1.setValue(assignment.getDueTime().substring(6));

            updateCourseCard1.setVisible(true);
        });

        cancelConfirmationButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            assignmentTitleTextField1.clear();
            assignmentDescriptionTextField1.clear();
            dueDatePicker1.setValue(null);
            dueHourComboBox1.setValue(null);
            dueMinComboBox1.setValue(null);
            dueFormateComboBox1.setValue(null);
        });

        updateConfirmationButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            updateCourseCard1.setVisible(true);

            updateCourseCard1.setVisible(true);
            assignmentTitleWarningLabel1.setVisible(assignmentTitleTextField1.getText().isEmpty());
            assignmentDescriptionWarningLabel1.setVisible(assignmentDescriptionTextField1.getText().isEmpty());
            dueDateWarningLabel1.setVisible(dueDatePicker1.getValue() == null);
            dueTimeWarningLabel1.setVisible(dueHourComboBox1.getValue() == null ||
                    dueMinComboBox1.getValue() == null || dueFormateComboBox1.getValue() == null);

            if(!assignmentTitleTextField1.getText().isEmpty() && !assignmentDescriptionTextField1.getText().isEmpty() &&
                    dueDatePicker1.getValue() != null && dueHourComboBox1.getValue() != null &&
                    dueMinComboBox1.getValue() != null && dueFormateComboBox1.getValue() != null) {
                init();
                try {
                    updateAssignment(mouseEvent);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // complete button handling
        for(int i = 0; i < currentCompleteButtons.size(); i++) {
            int assignmentNumber = i;

            currentCompleteButtons.get(assignmentNumber).addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                assignmentNumberToUpdate = assignmentNumber;
                try {
                    updateCompleteButton();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // navigation inside the course
        backToCourseScreenButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            backToCourseScreen(mouseEvent);
        });

        trackScreenButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            enterTrackScreen(mouseEvent);
        });
    }

    void init() {
        profileDropdown.setVisible(false);
        DeleteUpdateAnchorpane.setVisible(false);
        addCourseCard.setVisible(false);
        deleteCourseConfirmationCard.setVisible(false);
        updateCourseCard1.setVisible(false);

        assignmentTitleWarningLabel.setVisible(false);
        assignmentDescriptionWarningLabel.setVisible(false);
        dueDateWarningLabel.setVisible(false);
        dueTimeWarningLabel.setVisible(false);

        assignmentTitleWarningLabel1.setVisible(false);
        assignmentDescriptionWarningLabel1.setVisible(false);
        dueDateWarningLabel1.setVisible(false);
        dueTimeWarningLabel1.setVisible(false);

        EditProfileCard.setVisible(false);
        editPasswordWarningLabel.setVisible(false);
    }
}
