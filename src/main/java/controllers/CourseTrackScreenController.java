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
import models.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CourseTrackScreenController implements Initializable {

    @FXML
    private AnchorPane DeleteUpdateAnchorpane;

    @FXML
    private AnchorPane NoDeleteCourseButton;

    @FXML
    private AnchorPane booklist;

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
    private AnchorPane assignmentsScreenButton;

    @FXML
    private AnchorPane attendance;

    @FXML
    private AnchorPane backToCourseScreenButton;

    @FXML
    private AnchorPane cancelConfirmationButton;

    @FXML
    private AnchorPane cancelConfirmationButton1;

    @FXML
    private Text cancelText3;

    @FXML
    private TextField chapterNameTextField;

    @FXML
    private TextField chapterNameTextField1;

    @FXML
    private Label chapterNameWarningLabel;

    @FXML
    private Label chapterNameWarningLabel1;

    @FXML
    private GridPane courseContainer;

    @FXML
    private AnchorPane deleteCourseButton;

    @FXML
    private AnchorPane deleteCourseConfirmationCard;

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
    private AnchorPane materialsScreenButton;

    @FXML
    private AnchorPane profileDropdown;

    @FXML
    private AnchorPane results;

    @FXML
    private AnchorPane summary;

    @FXML
    private AnchorPane todolist;

    @FXML
    private TextField topicNameTextField;

    @FXML
    private TextField topicNameTextField1;

    @FXML
    private Label topicNameWarningLabel;

    @FXML
    private Label topicNameWarningLabel1;

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

    private String activeUsername;

    private DatabaseManager dbManager;

    private boolean iconButtonPressed = true;

    private Course activeCourse;

    private List<Track> currentTrack;

    private List<ImageView> currentVerticalThreeDots;

    private List<AnchorPane> currentCompleteButtons;

    private List<Text> currentButtonStatusText;

    private int trackItemNumberToDelete;

    private int trackItemNumberToUpdate;

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

        init();

        try {
            currentTrack = new ArrayList<>(currentTrackList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(currentTrack.isEmpty()) {
            emptyContainerWarning.setVisible(true);
        }

        currentVerticalThreeDots = new ArrayList<>();
        currentCompleteButtons = new ArrayList<>();
        currentButtonStatusText = new ArrayList<>();

        int columns = 0;
        int rows = 1;


        try {
            for(Track track : currentTrack) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/models/TrackCard.fxml"));
                AnchorPane TrackCard = loader.load();
                TrackCardController trackCardController = loader.getController();
                ImageTextAnchorTriple imageTextAnchorTriple =  trackCardController.setData(track);
                currentVerticalThreeDots.add(imageTextAnchorTriple.getImageView());
                currentCompleteButtons.add(imageTextAnchorTriple.getAnchorPane());
                currentButtonStatusText.add(imageTextAnchorTriple.getText());

                courseContainer.add(TrackCard, columns, rows++);
                GridPane.setMargin(TrackCard, new Insets(4));
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


        try {
            activeCourse = dbManager.activeCourse();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        TrackScreenCourseName.setText(activeCourse.getCourseCode());

        String activeUsername;
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
            SceneController.changeScene("CourseTrackScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private void enterAssignmentsScreen(MouseEvent mouseEvent) {
        try {
            SceneController.changeScene("CourseAssignmentScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Track> currentTrackList() throws SQLException {
        return dbManager.currentTrack(activeCourse);
    }

    private void addNewTrack(MouseEvent mouseEvent) throws SQLException {
        Track track = new Track();
        track.setChapterName(chapterNameTextField.getText());
        track.setItemTitle(topicNameTextField.getText());

        dbManager.addNewTrack(track, activeCourse);

        try {
            SceneController.changeScene("CourseTrackScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteTrack(MouseEvent mouseEvent) throws SQLException {
        dbManager.deleteTrack(currentTrack.get(trackItemNumberToDelete), activeCourse);

        try {
            SceneController.changeScene("CourseTrackScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateTrack(MouseEvent mouseEvent) throws SQLException {
        Track oldContent = currentTrack.get(trackItemNumberToUpdate);

        Track newContent = new Track();
        newContent.setChapterName(chapterNameTextField1.getText());
        newContent.setItemTitle(topicNameTextField1.getText());
        newContent.setStatus(oldContent.getStatus());

        dbManager.updateTrack(oldContent, newContent, activeCourse);

        try {
            SceneController.changeScene("CourseTrackScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateCompleteButton() throws SQLException {
        Track oldContent = currentTrack.get(trackItemNumberToUpdate);

        Track newContent = new Track();
        newContent.setChapterName(oldContent.getChapterName());
        newContent.setItemTitle(oldContent.getItemTitle());
        newContent.setStatus(!oldContent.getStatus());

        dbManager.updateTrack(oldContent, newContent, activeCourse);

        if(!oldContent.getStatus()) {
            currentButtonStatusText.get(trackItemNumberToUpdate).setText("Studied");
            currentCompleteButtons.get(trackItemNumberToUpdate).setStyle("-fx-background-color: linear-gradient(to right, " + startColors[0] + ", " + endColors[0] + ")");
        }
        else {
            currentButtonStatusText.get(trackItemNumberToUpdate).setText("Pending");
            currentCompleteButtons.get(trackItemNumberToUpdate).setStyle("-fx-background-color: linear-gradient(to right, " + startColors[1] + ", " + endColors[1] + ")");
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

        // add new course button clicked
        addNewElementButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            addCourseCard.setVisible(true);
        });

        // confirmation button clicked
        cancelConfirmationButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            chapterNameTextField.clear();
            topicNameTextField.clear();
        });
        addConfirmationButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            addCourseCard.setVisible(true);
            chapterNameWarningLabel.setVisible(chapterNameTextField.getText().isEmpty());
            topicNameWarningLabel.setVisible(topicNameTextField.getText().isEmpty());
            if(!chapterNameTextField.getText().isEmpty() && !topicNameTextField.getText().isEmpty()) {
                init();
                try {
                    addNewTrack(mouseEvent);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // vertical three dots
        for(int i = 0; i < currentVerticalThreeDots.size(); i++) {
            int trackItemNumber = i;

            currentVerticalThreeDots.get(trackItemNumber).addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> {
                currentVerticalThreeDots.get(trackItemNumber).setFitHeight(22);
                currentVerticalThreeDots.get(trackItemNumber).setFitWidth(22);
            });
        }

        for(int i = 0; i < currentVerticalThreeDots.size(); i++) {
            int trackItemNumber = i;

            currentVerticalThreeDots.get(trackItemNumber).addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
                currentVerticalThreeDots.get(trackItemNumber).setFitHeight(20);
                currentVerticalThreeDots.get(trackItemNumber).setFitWidth(20);
            });
        }

        for(int i = 0; i < currentVerticalThreeDots.size(); i++) {
            int trackItemNumber = i;

            currentVerticalThreeDots.get(trackItemNumber).addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                init();
                DeleteUpdateAnchorpane.setVisible(true);
                trackItemNumberToDelete = trackItemNumber;
                trackItemNumberToUpdate = trackItemNumber;
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
                deleteTrack(mouseEvent);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        NoDeleteCourseButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> init());

        // update course
        updateCourseButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();

            Track track = currentTrack.get(trackItemNumberToUpdate);
            chapterNameTextField1.setText(track.getChapterName());
            topicNameTextField1.setText(track.getItemTitle());

            updateCourseCard1.setVisible(true);
        });

        cancelConfirmationButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            chapterNameTextField1.clear();
            topicNameTextField1.clear();
        });

        updateConfirmationButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            updateCourseCard1.setVisible(true);
            chapterNameWarningLabel1.setVisible(chapterNameTextField1.getText().isEmpty());
            topicNameWarningLabel1.setVisible(topicNameTextField1.getText().isEmpty());
            if(!chapterNameTextField1.getText().isEmpty() && !topicNameTextField1.getText().isEmpty()) {
                init();
                try {
                    updateTrack(mouseEvent);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // complete button handling
        for(int i = 0; i < currentCompleteButtons.size(); i++) {
            int trackItemNumber = i;

            currentCompleteButtons.get(trackItemNumber).addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                trackItemNumberToUpdate = trackItemNumber;
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

        assignmentsScreenButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            enterAssignmentsScreen(mouseEvent);
        });
    }

    void init() {
        profileDropdown.setVisible(false);
        DeleteUpdateAnchorpane.setVisible(false);
        addCourseCard.setVisible(false);
        deleteCourseConfirmationCard.setVisible(false);
        updateCourseCard1.setVisible(false);
        chapterNameWarningLabel.setVisible(false);
        topicNameWarningLabel.setVisible(false);
        chapterNameWarningLabel1.setVisible(false);
        topicNameWarningLabel1.setVisible(false);

        EditProfileCard.setVisible(false);
        editPasswordWarningLabel.setVisible(false);
    }
}
