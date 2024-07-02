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
import models.Book;
import models.DatabaseManager;
import models.Student;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BookListController implements Initializable{

    private DatabaseManager dbManager;

    private List<ImageView> currentVerticalThreeDots;

    @FXML
    private GridPane bookCardContainer;
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
    private TextField BookNameTextField;

    @FXML
    private TextField CourseNameTextField;

    @FXML
    private TextField BookURLTextField;

    @FXML
    private TextField BookNameUpdate;
    @FXML
    private TextField BookURLUpdate;

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
    private AnchorPane attendance;

    @FXML
    private AnchorPane todolist;

    @FXML
    private Text usernameFirstLetter;

    @FXML
    private Text ProfileDropdownUsername;

    @FXML
    private Text ProfileDropdownUsernameFirstLetter;

    @FXML
    private AnchorPane EditProfileCard;

    @FXML
    private TextField editUsernameTextField;

    @FXML
    private TextField editPasswordTextField;

    @FXML
    private AnchorPane cancelEditProfileButton;

    @FXML
    private AnchorPane saveEditProfileButton;

    @FXML
    private Label editPasswordWarningLabel;

    private boolean iconButtonPressed = true;

    private int  taskNumberToDelete;

    private  int taskNumberToUpdate;

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

        try {
            activeUsername = dbManager.activeUsername();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        usernameFirstLetter.setText(activeUsername.substring(0, 1).toUpperCase());
        ProfileDropdownUsername.setText(activeUsername);
        ProfileDropdownUsernameFirstLetter.setText(activeUsername.substring(0, 1).toUpperCase());


        init();
        init_Book();

        // event-handling
        handleEvents();
    }

    private void init_Book() {

        List<Book> currentBooks;

        try {
            currentBooks = new ArrayList<>(currentBookList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        currentVerticalThreeDots = new ArrayList<>();

        int columns = 0;
        int rows = 1;
        int colorPicker = 0;

        try {
            for (Book item : currentBooks) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/models/BookCard.fxml"));
                AnchorPane BookCard = loader.load();
                BookCardController bookCardController = loader.getController();
                bookCardController.setData(item, colorPicker);
                ImageView newVerticalThreeDots = bookCardController.setData(item, colorPicker++);
                currentVerticalThreeDots.add(newVerticalThreeDots);

                colorPicker = (colorPicker + 1) % 5;

                bookCardContainer.add(BookCard, columns, rows++);
                GridPane.setMargin(BookCard, new Insets(5));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUser(MouseEvent mouseEvent) throws SQLException {
        Student newContent = new Student();
        newContent.setUsername(editUsernameTextField.getText());
        newContent.setPassword(editPasswordTextField.getText());

        dbManager.updateUser(newContent);

        try {
            SceneController.changeScene("Booklist.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Book> currentBookList() throws SQLException {
        return dbManager.currentBookList();
    }


    private void addNewBook(MouseEvent mouseEvent) throws SQLException {
        Book item = new Book();
        item.setBookName(BookNameTextField.getText());
        item.setBookURL(BookURLTextField.getText());
        item.setCourseName(CourseNameTextField.getText());

        dbManager.addNewBook(item);

        try {
            SceneController.changeScene("Booklist.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteBook(MouseEvent mouseEvent) throws SQLException {
        dbManager.deleteBook(currentBookList().get(taskNumberToDelete));

        try {
            SceneController.changeScene("Booklist.fxml", mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateBook(MouseEvent mouseEvent) throws SQLException {
        Book oldContent = currentBookList().get(taskNumberToUpdate);

        Book newContent = new Book();
        newContent.setBookName(BookNameUpdate.getText());
        newContent.setBookURL(BookURLUpdate.getText());


        dbManager.updateBook(oldContent, newContent);

        try {
            SceneController.changeScene("Booklist.fxml", mouseEvent);
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

        todolist.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                init();
                SceneController.changeScene("ToDoListScreen.fxml", mouseEvent);
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
            BookNameTextField.clear();
            CourseNameTextField.clear();
            BookURLTextField.clear();
        });
        addConfirmationButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            addCourseCard.setVisible(true);
            if(BookNameTextField.getText().isEmpty()||CourseNameTextField.getText().isEmpty()||BookURLTextField.getText().isEmpty())
                taskNameWarningLabel.setVisible(true);
            else {
                init();
                try {
                    addNewBook(mouseEvent);
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
                deleteBook(mouseEvent);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        NoDeleteCourseButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> init());

        // update
        updateCourseButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();

            Book item;
            try {
                item = currentBookList().get(taskNumberToUpdate);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            BookNameUpdate.setText(item.getBookName());
            BookURLUpdate.setText(item.getBookURL());
            updateCourseCard1.setVisible(true);
        });

        cancelConfirmationButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            BookNameUpdate.clear();
            BookURLUpdate.clear();

        });

        updateConfirmationButton1.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            init();
            updateCourseCard1.setVisible(true);
            if(BookURLUpdate.getText().isEmpty()||BookNameUpdate.getText().isEmpty())
                taskNameWarningLabel1.setVisible(true);
            else{
                init();
                try {
                    updateBook(mouseEvent);
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
        taskNameWarningLabel.setVisible(false);
        taskNameWarningLabel1.setVisible(false);

        EditProfileCard.setVisible(false);
        editPasswordWarningLabel.setVisible(false);
    }


}
