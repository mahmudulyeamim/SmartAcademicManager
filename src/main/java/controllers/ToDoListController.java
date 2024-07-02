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
import models.DatabaseManager;
import models.ToDoList;
import models.ImageTextAnchorTriple;
import models.Student;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ToDoListController implements Initializable {

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
    private AnchorPane addCourseCard;

    @FXML
    private AnchorPane addNewElementButton;

    @FXML
    private AnchorPane cancelConfirmationButton;

    @FXML
    private AnchorPane cancelConfirmationButton1;

    @FXML
    private GridPane courseContainer;

    @FXML
    private TextField taskNameTextField;

    @FXML
    private TextField taskNameTextField1;

    @FXML
    private Label taskNameWarningLabel;

    @FXML
    private Label taskNameWarningLabel1;

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
    private AnchorPane courses;

    @FXML
    private AnchorPane updateConfirmationButton1;

    @FXML
    private AnchorPane updateCourseButton;

    @FXML
    private AnchorPane updateCourseCard1;

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

    @FXML
    private AnchorPane attendance;

    private  List<ImageView> currentVerticalThreeDots;

    private List<AnchorPane> currentCompleteButtons;

    private List<Text> currentButtonStatusText;

    private DatabaseManager dbManager;

    private boolean iconButtonPressed = true;

    private int  taskNumberToDelete;

    private  int taskNumberToUpdate;

    private String activeUsername;

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

        init();

        List<ToDoList> toDoList;

        try {
            toDoList = new ArrayList<>(currentToDoList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        currentVerticalThreeDots = new ArrayList<>();
        currentCompleteButtons = new ArrayList<>();
        currentButtonStatusText = new ArrayList<>();

        int columns = 0;
        int rows = 1;
        int colorPicker = 0;


        try {
            for(ToDoList item : toDoList) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/models/TextItemCard.fxml"));
                AnchorPane TextItemCard = loader.load();
                TextItemCardController textItemCardController = loader.getController();
                ImageTextAnchorTriple imageTextAnchorTriple =  textItemCardController.setData(item, colorPicker++);
                currentVerticalThreeDots.add(imageTextAnchorTriple.getImageView());
                currentButtonStatusText.add(imageTextAnchorTriple.getText());
                currentCompleteButtons.add(imageTextAnchorTriple.getAnchorPane());

                if (colorPicker == 2) {
                    colorPicker = 0;
                }

                courseContainer.add(TextItemCard, columns, rows++);
                GridPane.setMargin(TextItemCard, new Insets(2));
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
            SceneController.changeScene("ToDoListScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ToDoList> currentToDoList() throws SQLException {
        return dbManager.currentToDoList();
    }

    private void addNewTask(MouseEvent mouseEvent) throws SQLException {
        ToDoList item = new ToDoList();
        item.setItemTitle(taskNameTextField.getText());

        dbManager.addNewTask(item);

        try {
            SceneController.changeScene("ToDoListScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteTask(MouseEvent mouseEvent) throws SQLException {
        dbManager.deleteTask(currentToDoList().get(taskNumberToDelete));

        try {
            SceneController.changeScene("ToDoListScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateTask(MouseEvent mouseEvent) throws SQLException {
        ToDoList oldContent = currentToDoList().get(taskNumberToUpdate);

        ToDoList newContent = new ToDoList();
        newContent.setItemTitle(taskNameTextField1.getText());
        newContent.setStatus(oldContent.getStatus());

        dbManager.updateTask(oldContent, newContent);

        try {
            SceneController.changeScene("ToDoListScreen.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateCompleteButton() throws SQLException {
        ToDoList oldContent = currentToDoList().get(taskNumberToUpdate);

        ToDoList newContent = new ToDoList();
        newContent.setItemTitle(oldContent.getItemTitle());
        newContent.setStatus(!oldContent.getStatus());

        dbManager.updateTask(oldContent, newContent);

        if(!oldContent.getStatus()) {
            currentButtonStatusText.get(taskNumberToUpdate).setText("Completed");
            currentCompleteButtons.get(taskNumberToUpdate).setStyle("-fx-background-color: linear-gradient(to right, " + startColors[0] + ", " + endColors[0] + ")");
        }
        else {
            currentButtonStatusText.get(taskNumberToUpdate).setText("Pending");
            currentCompleteButtons.get(taskNumberToUpdate).setStyle("-fx-background-color: linear-gradient(to right, " + startColors[1] + ", " + endColors[1] + ")");
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
            taskNameTextField.clear();
        });
        addConfirmationButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            addCourseCard.setVisible(true);
            taskNameWarningLabel.setVisible(taskNameTextField.getText().isEmpty());
            if(!taskNameTextField.getText().isEmpty()) {
                init();
                try {
                    addNewTask(mouseEvent);
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
            int taskNumber = i;

            currentVerticalThreeDots.get(taskNumber).addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                init();
                DeleteUpdateAnchorpane.setVisible(true);
                taskNumberToDelete = taskNumber;
                taskNumberToUpdate = taskNumber;
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

            ToDoList item;
            try {
                item = currentToDoList().get(taskNumberToUpdate);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            taskNameTextField1.setText(item.getItemTitle());

            updateCourseCard1.setVisible(true);
        });

        cancelConfirmationButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            taskNameTextField1.clear();
        });

        updateConfirmationButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            updateCourseCard1.setVisible(true);
            taskNameWarningLabel1.setVisible(taskNameTextField1.getText().isEmpty());
            if(!taskNameTextField1.getText().isEmpty()) {
                init();
                try {
                    updateTask(mouseEvent);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // complete button handling
        for(int i = 0; i < currentCompleteButtons.size(); i++) {
            int taskNumber = i;

            currentCompleteButtons.get(taskNumber).addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                taskNumberToUpdate = taskNumber;
                try {
                    updateCompleteButton();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

    }

    private void init() {
        profileDropdown.setVisible(false);
        DeleteUpdateAnchorpane.setVisible(false);
        addCourseCard.setVisible(false);
        deleteCourseConfirmationCard.setVisible(false);
        updateCourseCard1.setVisible(false);
        taskNameWarningLabel.setVisible(false);
        taskNameWarningLabel1.setVisible(false);
        EditProfileCard.setVisible(false);
        editPasswordWarningLabel.setVisible(false);
    }
}
