package controllers;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import models.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardScreenController implements Initializable {
    @FXML
    private AnchorPane EditProfileCard;

    @FXML
    private AnchorPane StudentProfileIcon;

    @FXML
    private AnchorPane attendance;

    @FXML
    private AnchorPane attendance1;

    @FXML
    private AnchorPane cancelEditProfileButton;

    @FXML
    private AnchorPane courses;

    @FXML
    private TextField editPasswordTextField;

    @FXML
    private Label editPasswordWarningLabel;

    @FXML
    private ImageView editProfileArrow;

    @FXML
    private AnchorPane editProfileButton;

    @FXML
    private Text editProfileText;

    @FXML
    private TextField editUsernameTextField;

    @FXML
    private AnchorPane events;

    @FXML
    private AnchorPane events1;

    @FXML
    private AnchorPane events2;

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
    private AnchorPane saveEditProfileButton;

    @FXML
    private AnchorPane todolist;

    @FXML
    private AnchorPane todolist1;

    @FXML
    private AnchorPane todolist2;

    @FXML
    private Text usernameFirstLetter;

    @FXML
    private Text ProfileDropdownUsername;

    @FXML
    private Text ProfileDropdownUsernameFirstLetter;

    @FXML
    private  Text eventsToday;

    @FXML
    private Text attendancePercentage;

    @FXML
    private Text taskPercentage;

    @FXML
    private AnchorPane booklist;

    @FXML
    private Text EventName;

    @FXML
    private Text EventTime;

    @FXML
    private Text EventType;

    @FXML
    private Text EventDate;

    @FXML
    private Text EventMonth;

    @FXML
    private AnchorPane EventCard;

    @FXML
    private Text EventName1;

    @FXML
    private Text EventTime1;

    @FXML
    private Text EventType1;

    @FXML
    private Text EventDate1;

    @FXML
    private Text EventMonth1;

    @FXML
    private AnchorPane EventCard1;

    @FXML
    private Text EventName2;

    @FXML
    private Text EventTime2;

    @FXML
    private Text EventType2;

    @FXML
    private Text EventDate2;

    @FXML
    private Text EventMonth2;

    @FXML
    private AnchorPane EventCard2;

    @FXML
    private AnchorPane CompleteButton;

    @FXML
    private Text ItemTextDescription;

    @FXML
    private AnchorPane taskCard;

    @FXML
    private AnchorPane CompleteButton1;

    @FXML
    private Text ItemTextDescription1;

    @FXML
    private AnchorPane taskCard1;

    @FXML
    private AnchorPane CompleteButton2;

    @FXML
    private Text ItemTextDescription2;

    @FXML
    private AnchorPane taskCard2;

    @FXML
    private AnchorPane CompleteButton3;

    @FXML
    private Text ItemTextDescription3;

    @FXML
    private AnchorPane taskCard3;

    @FXML
    private Text attendancePercentageSign;

    @FXML
    private Text taskPercentageSign;

    private final String[] startColors = {"#07A952", "#CB1F1F"};
    private final String[] endColors = {"#05B958", "#E02A2A"};

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
            activeUsername = dbManager.activeUsername();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        usernameFirstLetter.setText(activeUsername.substring(0, 1).toUpperCase());
        ProfileDropdownUsername.setText(activeUsername);
        ProfileDropdownUsernameFirstLetter.setText(activeUsername.substring(0, 1).toUpperCase());

        try {
            int percentage = dbManager.attendancePercentage();
            if(percentage >= 100) {
                attendancePercentageSign.setLayoutX(195);
            }
            else {
                attendancePercentageSign.setLayoutX(175);
            }
            attendancePercentage.setText(Integer.toString(percentage));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            int percentage = dbManager.taskPercentage();
            if(percentage >= 100) {
                taskPercentageSign.setLayoutX(195);
            }
            else {
                taskPercentageSign.setLayoutX(175);
            }
            taskPercentage.setText(Integer.toString(percentage));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            eventsToday.setText(Integer.toString(dbManager.eventsToday()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            upcomingEvents();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            updateToDoList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // event-handling
        handleEvents();
    }

    private void updateUser(MouseEvent mouseEvent) throws SQLException {
        Student newContent = new Student();
        newContent.setUsername(editUsernameTextField.getText());
        newContent.setPassword(editPasswordTextField.getText());

        dbManager.updateUser(newContent);

        try {
            SceneController.changeScene("DashboardScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void upcomingEvents() throws SQLException {
        List<Event> list = dbManager.upcomingEvents();

        Collections.sort(list, new EventTimeComparator());

        if(list.size() >= 1) {
            EventCard.setVisible(true);
            Event event = list.get(0);

            EventName.setText(event.getEventName());
            EventType.setText(event.getEventType());

            String date = event.getEventDate();

            EventDate.setText(date.substring(0, 2));
            EventMonth.setText(date.substring(3, 6));

            EventTime.setText("•" + event.getStartTime() + " - " + event.getEndTime());
        }

        if(list.size() >= 2) {
            EventCard1.setVisible(true);
            Event event = list.get(1);

            EventName1.setText(event.getEventName());
            EventType1.setText(event.getEventType());

            String date = event.getEventDate();

            EventDate1.setText(date.substring(0, 2));
            EventMonth1.setText(date.substring(3, 6));

            EventTime1.setText("•" + event.getStartTime() + " - " + event.getEndTime());
        }

        if(list.size() >= 3) {
            EventCard2.setVisible(true);
            Event event = list.get(2);

            EventName2.setText(event.getEventName());
            EventType2.setText(event.getEventType());

            String date = event.getEventDate();

            EventDate2.setText(date.substring(0, 2));
            EventMonth2.setText(date.substring(3, 6));

            EventTime2.setText("•" + event.getStartTime() + " - " + event.getEndTime());
        }
    }

    private void updateToDoList() throws SQLException {
        List<ToDoList> list = dbManager.currentToDoList();

        if(list.size() >= 1) {
            taskCard.setVisible(true);

            ToDoList todolist = list.get(0);

            ItemTextDescription.setText(todolist.getItemTitle());
            if(todolist.getStatus()) {
                CompleteButton.setStyle("-fx-background-color: linear-gradient(to right, " + startColors[0] + ", " + endColors[0] + ")");
            }
            else {
                CompleteButton.setStyle("-fx-background-color: linear-gradient(to right, " + startColors[1] + ", " + endColors[1] + ")");
            }
        }
        if(list.size() >= 2) {
            taskCard1.setVisible(true);

            ToDoList todolist = list.get(1);

            ItemTextDescription1.setText(todolist.getItemTitle());
            if(todolist.getStatus()) {
                CompleteButton1.setStyle("-fx-background-color: linear-gradient(to right, " + startColors[0] + ", " + endColors[0] + ")");
            }
            else {
                CompleteButton1.setStyle("-fx-background-color: linear-gradient(to right, " + startColors[1] + ", " + endColors[1] + ")");
            }
        }

        if(list.size() >= 3) {
            taskCard2.setVisible(true);

            ToDoList todolist = list.get(2);

            ItemTextDescription2.setText(todolist.getItemTitle());
            if(todolist.getStatus()) {
                CompleteButton2.setStyle("-fx-background-color: linear-gradient(to right, " + startColors[0] + ", " + endColors[0] + ")");
            }
            else {
                CompleteButton2.setStyle("-fx-background-color: linear-gradient(to right, " + startColors[1] + ", " + endColors[1] + ")");
            }
        }

        if(list.size() >= 4) {
            taskCard3.setVisible(true);

            ToDoList todolist = list.get(3);

            ItemTextDescription3.setText(todolist.getItemTitle());
            if(todolist.getStatus()) {
                CompleteButton3.setStyle("-fx-background-color: linear-gradient(to right, " + startColors[0] + ", " + endColors[0] + ")");
            }
            else {
                CompleteButton3.setStyle("-fx-background-color: linear-gradient(to right, " + startColors[1] + ", " + endColors[1] + ")");
            }
        }
    }

    private void handleEvents() {

        // sidebar navigation
        courses.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("CourseScreen.fxml", mouseEvent);
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

        events1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("EventsScreen.fxml", mouseEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        events2.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("EventsScreen.fxml", mouseEvent);
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

        attendance1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
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

        todolist.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("ToDoListScreen.fxml", mouseEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        todolist1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("ToDoListScreen.fxml", mouseEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        todolist2.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
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

    private void init() {
        profileDropdown.setVisible(false);
        EditProfileCard.setVisible(false);
        editPasswordWarningLabel.setVisible(false);
    }
}
