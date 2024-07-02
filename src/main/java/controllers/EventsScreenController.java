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
import models.DatabaseManager;
import models.Event;
import models.Student;
import models.EventTimeComparator;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class EventsScreenController implements Initializable {

    @FXML
    private AnchorPane DeleteUpdateAnchorpane;

    @FXML
    private AnchorPane NoDeleteCourseButton;

    @FXML
    private AnchorPane StudentProfileIcon;

    @FXML
    private AnchorPane YesDeleteCourseButton;

    @FXML
    private AnchorPane addConfirmationButton;

    @FXML
    private AnchorPane updateConfirmationButton1;

    @FXML
    private AnchorPane addCourseCard;

    @FXML
    private AnchorPane updateCourseCard1;

    @FXML
    private AnchorPane addNewElementButton;

    @FXML
    private AnchorPane attendance;

    @FXML
    private AnchorPane cancelConfirmationButton;

    @FXML
    private AnchorPane cancelConfirmationButton1;

    @FXML
    private GridPane courseContainer;

    @FXML
    private AnchorPane courses;

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
    private ComboBox<String> endFormateComboBox;

    @FXML
    private ComboBox<String> endFormateComboBox1;

    @FXML
    private ComboBox<String> endHourComboBox;

    @FXML
    private ComboBox<String> endHourComboBox1;

    @FXML
    private ComboBox<String> endMinComboBox;

    @FXML
    private ComboBox<String> endMinComboBox1;

    @FXML
    private DatePicker eventDatePicker;

    @FXML
    private DatePicker eventDatePicker1;

    @FXML
    private TextField eventNameTextField;

    @FXML
    private TextField eventNameTextField1;

    @FXML
    private ComboBox<String> eventTypeComboBox;

    @FXML
    private ComboBox<String> eventTypeComboBox1;

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
    private ComboBox<String> startFormateComboBox;

    @FXML
    private ComboBox<String> startFormateComboBox1;

    @FXML
    private ComboBox<String> startHourComboBox;

    @FXML
    private ComboBox<String> startHourComboBox1;

    @FXML
    private ComboBox<String> startMinComboBox;

    @FXML
    private ComboBox<String> startMinComboBox1;

    @FXML
    private AnchorPane summary;

    @FXML
    private AnchorPane todolist;

    @FXML
    private AnchorPane updateCourseButton;

    @FXML
    private Label endTimeWarningLabel;

    @FXML
    private Label endTimeWarningLabel1;

    @FXML
    private Label eventDateWarningLabel;

    @FXML
    private Label eventDateWarningLabel1;

    @FXML
    private Label eventNameWarningLabel;

    @FXML
    private Label eventNameWarningLabel1;

    @FXML
    private Label startTimeWarningLabel;

    @FXML
    private Label startTimeWarningLabel1;

    @FXML
    private Label eventTypeWarningLabel;

    @FXML
    private Label eventTypeWarningLabel1;

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
    private AnchorPane saveEditProfileButton;

    @FXML
    private Text usernameFirstLetter;

    @FXML
    private Text ProfileDropdownUsername;

    @FXML
    private Text ProfileDropdownUsernameFirstLetter;

    @FXML
    private AnchorPane booklist;

    private boolean iconButtonPressed;

    private List<Event> currentEvents;

    private List<ImageView> currentVerticalThreeDots;

    private DatabaseManager dbManager;

    private final String[] typeOfEvents = {"Class", "Lab", "Exam", "Others"};
    private final String[] twelveHour = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12"};
    private final String[] sixtyMinutes = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
            "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59"};
    private final String[] formatOfTime = {"AM", "PM"};

    private int eventNumberToDelete;
    private int eventNumberToUpdate;

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

        initComboBox();

        iconButtonPressed = true;

        currentEvents = new ArrayList<>(currentEventsList());
        Collections.sort(currentEvents, new EventTimeComparator());

        currentVerticalThreeDots = new ArrayList<>();

        int columns = 0;
        int rows = 1;
        int colorPicker = 0;

        try {
            for(Event event : currentEvents) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/models/EventsCard.fxml"));
                AnchorPane eventCard = loader.load();
                EventsCardController eventsCardController = loader.getController();
                ImageView newVerticalThreeDots = eventsCardController.setData(event, colorPicker++);
                currentVerticalThreeDots.add(newVerticalThreeDots);

                if (columns == 2) {
                    columns = 0;
                    rows++;
                }

                if (colorPicker == 5) {
                    colorPicker = 0;
                }

                courseContainer.add(eventCard, columns++, rows);
                GridPane.setMargin(eventCard, new Insets(7));
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

    private void initComboBox() {
        eventTypeComboBox.getItems().addAll(typeOfEvents);
        startHourComboBox.getItems().addAll(twelveHour);
        startMinComboBox.getItems().addAll(sixtyMinutes);
        startFormateComboBox.getItems().addAll(formatOfTime);
        endHourComboBox.getItems().addAll(twelveHour);
        endMinComboBox.getItems().addAll(sixtyMinutes);
        endFormateComboBox.getItems().addAll(formatOfTime);

        startFormateComboBox.setValue("AM");
        endFormateComboBox.setValue("AM");

        eventTypeComboBox1.getItems().addAll(typeOfEvents);
        startHourComboBox1.getItems().addAll(twelveHour);
        startMinComboBox1.getItems().addAll(sixtyMinutes);
        startFormateComboBox1.getItems().addAll(formatOfTime);
        endHourComboBox1.getItems().addAll(twelveHour);
        endMinComboBox1.getItems().addAll(sixtyMinutes);
        endFormateComboBox1.getItems().addAll(formatOfTime);
    }

    private void updateUser(MouseEvent mouseEvent) throws SQLException {
        Student newContent = new Student();
        newContent.setUsername(editUsernameTextField.getText());
        newContent.setPassword(editPasswordTextField.getText());

        dbManager.updateUser(newContent);

        try {
            SceneController.changeScene("EventsScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Event> currentEventsList() {
        List<Event> list = new ArrayList<>();
        try {
            list = dbManager.currentEventsList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void addNewEvent(MouseEvent mouseEvent) throws SQLException {
        Event event = new Event();
        event.setEventName(eventNameTextField.getText());
        event.setEventType(eventTypeComboBox.getValue());
        event.setEventDate(eventDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        event.setStartTime(startHourComboBox.getValue() + ":" + startMinComboBox.getValue() + " " + startFormateComboBox.getValue());
        event.setEndTime(endHourComboBox.getValue() + ":" + endMinComboBox.getValue() + " " + endFormateComboBox.getValue());
        dbManager.addNewEvent(event);

        try {
            SceneController.changeScene("EventsScreen.fxml", mouseEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteTask(MouseEvent mouseEvent) throws SQLException {
        dbManager.deleteEvent(currentEvents.get(eventNumberToDelete));

        try {
            SceneController.changeScene("EventsScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateEvent(MouseEvent mouseEvent) throws SQLException {
        Event oldContent = currentEvents.get(eventNumberToUpdate);

        Event newContent = new Event();
        newContent.setEventName(eventNameTextField1.getText());
        newContent.setEventType(eventTypeComboBox1.getValue());
        newContent.setEventDate(eventDatePicker1.getValue().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        newContent.setStartTime(startHourComboBox1.getValue() + ":" + startMinComboBox1.getValue() + " " + startFormateComboBox1.getValue());
        newContent.setEndTime(endHourComboBox1.getValue() + ":" + endMinComboBox1.getValue() + " " + endFormateComboBox1.getValue());

        dbManager.updateEvent(oldContent, newContent);

        try {
            SceneController.changeScene("EventsScreen.fxml", mouseEvent);
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

        courses.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                init();
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

        results.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("ResultScreen.fxml", mouseEvent);
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

        todolist.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                SceneController.changeScene("ToDoListScreen.fxml", mouseEvent);
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

        // add new task button clicked
        addNewElementButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            addCourseCard.setVisible(true);
        });

        // confirmation button clicked
        cancelConfirmationButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            eventNameTextField.clear();
            eventDatePicker.setValue(null);
            eventTypeComboBox.setValue(null);
            startHourComboBox.setValue(null);
            startMinComboBox.setValue(null);
            startFormateComboBox.setValue(null);
            endHourComboBox.setValue(null);
            endMinComboBox.setValue(null);
            endFormateComboBox.setValue(null);

            startFormateComboBox.setValue("AM");
            endFormateComboBox.setValue("AM");

        });
        addConfirmationButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            addCourseCard.setVisible(true);
            eventNameWarningLabel.setVisible(eventNameTextField.getText().isEmpty());
            eventTypeWarningLabel.setVisible(eventTypeComboBox.getValue() == null);
            eventDateWarningLabel.setVisible(eventDatePicker.getValue() == null);
            startTimeWarningLabel.setVisible(startHourComboBox.getValue() == null ||
                    startMinComboBox.getValue() == null || startFormateComboBox.getValue() == null);
            endTimeWarningLabel.setVisible(endHourComboBox.getValue() == null ||
                    endMinComboBox.getValue() == null || endFormateComboBox.getValue() == null);

            if(!eventNameTextField.getText().isEmpty() && eventTypeComboBox.getValue() != null &&
                    eventDatePicker.getValue() != null && startHourComboBox.getValue() != null &&
                    startMinComboBox.getValue() != null && startFormateComboBox.getValue() != null &&
                    endHourComboBox.getValue() != null && endMinComboBox.getValue() != null &&
                    endFormateComboBox.getValue() != null) {
                init();
                try {
                    addNewEvent(mouseEvent);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // vertical three dots
        for(int i = 0; i < currentVerticalThreeDots.size(); i++) {
            int taskNumber = i;

            currentVerticalThreeDots.get(taskNumber).addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> {
                currentVerticalThreeDots.get(taskNumber).setFitHeight(22);
                currentVerticalThreeDots.get(taskNumber).setFitWidth(22);
            });
        }

        for(int i = 0; i < currentVerticalThreeDots.size(); i++) {
            int taskNumber = i;

            currentVerticalThreeDots.get(taskNumber).addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
                currentVerticalThreeDots.get(taskNumber).setFitHeight(20);
                currentVerticalThreeDots.get(taskNumber).setFitWidth(20);
            });
        }

        for(int i = 0; i < currentVerticalThreeDots.size(); i++) {
            int eventNumber = i;

            currentVerticalThreeDots.get(eventNumber).addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                init();
                DeleteUpdateAnchorpane.setVisible(true);
                eventNumberToDelete = eventNumber;
                eventNumberToUpdate = eventNumber;
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
                deleteTask(mouseEvent);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        NoDeleteCourseButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> init());

        // update
        updateCourseButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();

            Event event;
            event = currentEvents.get(eventNumberToUpdate);

            eventNameTextField1.setText(event.getEventName());
            eventTypeComboBox1.setValue(event.getEventType());
            eventDatePicker1.setValue(LocalDate.parse(event.getEventDate(), DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
            startHourComboBox1.setValue(event.getStartTime().substring(0, 2));
            startMinComboBox1.setValue(event.getStartTime().substring(3, 5));
            startFormateComboBox1.setValue(event.getStartTime().substring(6));
            endHourComboBox1.setValue(event.getEndTime().substring(0, 2));
            endMinComboBox1.setValue(event.getEndTime().substring(3, 5));
            endFormateComboBox1.setValue(event.getEndTime().substring(6));


            updateCourseCard1.setVisible(true);
        });

        cancelConfirmationButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            eventNameTextField1.clear();
            eventTypeComboBox1.setValue(null);
            startHourComboBox1.setValue(null);
            startMinComboBox1.setValue(null);
            startFormateComboBox1.setValue(null);
            endHourComboBox1.setValue(null);
            endMinComboBox1.setValue(null);
            endFormateComboBox1.setValue(null);

            startFormateComboBox1.setValue("AM");
            endFormateComboBox1.setValue("AM");
        });

        updateConfirmationButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            updateCourseCard1.setVisible(true);

            eventNameWarningLabel1.setVisible(eventNameTextField1.getText().isEmpty());
            eventTypeWarningLabel1.setVisible(eventTypeComboBox1.getValue() == null);
            eventDateWarningLabel1.setVisible(eventDatePicker1.getValue() == null);
            startTimeWarningLabel1.setVisible(startHourComboBox1.getValue() == null ||
                    startMinComboBox1.getValue() == null || startFormateComboBox1.getValue() == null);
            endTimeWarningLabel1.setVisible(endHourComboBox1.getValue() == null ||
                    endMinComboBox1.getValue() == null || endFormateComboBox1.getValue() == null);

            if(!eventNameTextField1.getText().isEmpty() && eventTypeComboBox1.getValue() != null &&
                    eventDatePicker1.getValue() != null && startHourComboBox1.getValue() != null &&
                    startMinComboBox1.getValue() != null && startFormateComboBox1.getValue() != null &&
                    endHourComboBox1.getValue() != null && endMinComboBox1.getValue() != null &&
                    endFormateComboBox1.getValue() != null) {
                init();
                try {
                    updateEvent(mouseEvent);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void init() {
        profileDropdown.setVisible(false);
        DeleteUpdateAnchorpane.setVisible(false);
        addCourseCard.setVisible(false);
        deleteCourseConfirmationCard.setVisible(false);
        updateCourseCard1.setVisible(false);

        eventNameWarningLabel.setVisible(false);
        eventTypeWarningLabel.setVisible(false);
        eventDateWarningLabel.setVisible(false);
        startTimeWarningLabel.setVisible(false);
        endTimeWarningLabel.setVisible(false);

        eventNameWarningLabel1.setVisible(false);
        eventTypeWarningLabel1.setVisible(false);
        eventDateWarningLabel1.setVisible(false);
        startTimeWarningLabel1.setVisible(false);
        endTimeWarningLabel1.setVisible(false);

        EditProfileCard.setVisible(false);
        editPasswordWarningLabel.setVisible(false);
    }
}
