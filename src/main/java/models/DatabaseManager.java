package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    Connection connectionToUserDatabase;
    Connection connectionToActiveDatabase;
    Statement statement;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

    public String activeUsername() throws SQLException {
        try {
            String query = "SELECT * FROM users";
            statement = connectionToUserDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                int loginStatus = resultSet.getInt("loginStatus");
                if(loginStatus == 1) {
                    return resultSet.getString("username");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        return null;
    }

    public Student activeUser() throws SQLException {
        try {
            String query = "SELECT * FROM users";
            statement = connectionToUserDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                int loginStatus = resultSet.getInt("loginStatus");
                if(loginStatus == 1) {
                    Student student = new Student();
                    student.setUsername(resultSet.getString("username"));
                    student.setPassword(resultSet.getString("password"));
                    return student;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        return null;
    }

    public boolean connectToUserDatabase() {
        connectionToUserDatabase = Database.connectToUsersDatabase();
        return connectionToUserDatabase != null;
    }

    public boolean connectToActiveDatabase() throws SQLException {
        connectionToActiveDatabase = Database.connect(activeUsername());
        return connectionToActiveDatabase != null;
    }

    public boolean login(String username, String password) throws SQLException {
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }

        String searchQuery = "SELECT * FROM users WHERE username = ? AND password = ?";

        try {
            preparedStatement = connectionToUserDatabase.prepareStatement(searchQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {

                String updateQuery = "UPDATE users SET loginStatus = ? WHERE username = ?";
                preparedStatement = connectionToUserDatabase.prepareStatement(updateQuery);
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();

                return true;
            }
            else {
                return false;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            preparedStatement.close();
            resultSet.close();
        }
    }

    public boolean signup(String username, String password) throws SQLException {
        String query ="INSERT INTO users (username, password, loginStatus) VALUES(?, ?, ?)";

        try {
            preparedStatement = connectionToUserDatabase.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, 0);
            preparedStatement.executeUpdate();

            duplicateDatabase(username);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            preparedStatement.close();
        }

        return true;
    }

    private void duplicateDatabase(String username) throws SQLException {
        try {
            Connection sourceConnection = Database.connect("student");
            Connection destinationConnection = Database.connect(username);

            assert sourceConnection != null;
            Statement sourceStatement = sourceConnection.createStatement();
            resultSet = sourceStatement.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table'");

            List<String> tableNames = new ArrayList<>();
            while (resultSet.next()) {
                tableNames.add(resultSet.getString("name"));
            }

            tableNames.remove(0);

            assert destinationConnection != null;
            Statement destinationStatement = destinationConnection.createStatement();

            for (String tableName : tableNames) {
                resultSet = sourceStatement.executeQuery("SELECT sql FROM sqlite_master WHERE type = 'table' AND name = '" + tableName +"'");
                String tableSchema = resultSet.getString("sql");
                destinationStatement.executeUpdate(tableSchema);
            }

            sourceConnection.close();
            destinationConnection.close();
            sourceStatement.close();
            destinationStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
        }
    }

    public void updateUser(Student newContent) throws SQLException {
        try {
            Student oldContent = activeUser();

            String query = "UPDATE users SET username = ?, password = ? WHERE username = ?";
            preparedStatement = connectionToUserDatabase.prepareStatement(query);
            preparedStatement.setString(1, newContent.getUsername());
            preparedStatement.setString(2, newContent.getPassword());
            preparedStatement.setString(3, oldContent.getUsername());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public void logout(){
        try {
            String username = activeUsername();

            String query = "UPDATE users SET loginStatus = ? WHERE username = ?";
            preparedStatement = connectionToUserDatabase.prepareStatement(query);
            preparedStatement.setInt(1, 0);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Course> currentCourseList() throws SQLException {
        List <Course> list = new ArrayList<>();
        try {
            String query = "SELECT * FROM current_courses";
            statement = connectionToActiveDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                Course course = new Course();
                course.setCourseName(resultSet.getString("courseName"));
                course.setCourseCode(resultSet.getString("courseCode"));
                course.setCourseTeacherName(resultSet.getString("courseTeacherName"));
                list.add(course);

                updateCourseActiveStatus(course, 0);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        return list;
    }

    public boolean addNewCourse(Course course) throws SQLException {
        try {
            String courseName = course.getCourseName();
            String courseCode = course.getCourseCode();
            String courseTeacherName = course.getCourseTeacherName();

            String query = "INSERT INTO current_courses (courseName, courseCode, courseTeacherName, activeStatus) VALUES(?, ?, ?, ?)";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, courseName);
            preparedStatement.setString(2, courseCode);
            preparedStatement.setString(3, courseTeacherName);
            preparedStatement.setInt(4, 0);
            preparedStatement.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            preparedStatement.close();
        }

        return true;
    }

    public void deleteCourse(Course course) throws SQLException {
        try {
            String query = "DELETE FROM current_courses WHERE courseCode = ?";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, course.getCourseCode());
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public boolean updateCourse(Course oldContent, Course newContent) throws SQLException {
        try {
            String courseName = newContent.getCourseName();
            String courseCode = newContent.getCourseCode();
            String courseTeacherName = newContent.getCourseTeacherName();

            String oldCourseCode = oldContent.getCourseCode();

            String query = "UPDATE current_courses SET courseName = ?, courseCode = ?, courseTeacherName = ? WHERE courseCode = ?";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, courseName);
            preparedStatement.setString(2, courseCode);
            preparedStatement.setString(3, courseTeacherName);
            preparedStatement.setString(4, oldCourseCode);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            preparedStatement.close();
        }

        return true;
    }

    public void updateCourseActiveStatus(Course course, int status) throws SQLException {
        try {

            String query = "UPDATE current_courses SET activeStatus = ? WHERE courseCode = ?";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setInt(1, status);
            preparedStatement.setString(2, course.getCourseCode());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public Course activeCourse() throws SQLException {
        try {
            String query = "SELECT * FROM current_courses";
            statement = connectionToActiveDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                int activeStatus = resultSet.getInt("activeStatus");
                if(activeStatus == 1) {
                    Course course = new Course();
                    course.setCourseName(resultSet.getString("courseName"));
                    course.setCourseCode(resultSet.getString("courseCode"));
                    course.setCourseTeacherName(resultSet.getString("courseTeacherName"));
                    return course;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        return null;
    }

    public List<Assignment> currentAssignmentList(Course course) throws SQLException {
        List <Assignment> list = new ArrayList<>();
        try {
            String query = "SELECT * FROM current_assignments";
            statement = connectionToActiveDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                String courseCode = resultSet.getString("courseCode");
                if(courseCode.equals(course.getCourseCode())) {
                    Assignment assignment = new Assignment();

                    assignment.setItemTitle(resultSet.getString("assignmentName"));
                    assignment.setDescription(resultSet.getString("assignmentDescription"));
                    assignment.setDueDate(resultSet.getString("dueDate"));
                    assignment.setDueTime(resultSet.getString("dueTime"));
                    assignment.setStatus(resultSet.getInt("status") == 1);

                    list.add(assignment);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        return list;
    }

    public void addNewAssignment(Assignment assignment, Course activeCourse) throws SQLException {
        try {

            String query = "INSERT INTO current_assignments (courseCode, assignmentName, assignmentDescription, dueDate, dueTime, status) VALUES(?, ?, ?, ?, ?, ?)";

            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, activeCourse.getCourseCode());
            preparedStatement.setString(2, assignment.getItemTitle());
            preparedStatement.setString(3, assignment.getDescription());
            preparedStatement.setString(4, assignment.getDueDate());
            preparedStatement.setString(5, assignment.getDueTime());
            preparedStatement.setInt(6, 0);

            preparedStatement.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public void deleteAssignment(Assignment assignment, Course activeCourse) throws SQLException {
        try {
            String query = "DELETE FROM current_assignments WHERE courseCode = ? AND assignmentName = ? AND assignmentDescription = ? AND dueDate = ? AND dueTime = ?";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, activeCourse.getCourseCode());
            preparedStatement.setString(2, assignment.getItemTitle());
            preparedStatement.setString(3, assignment.getDescription());
            preparedStatement.setString(4, assignment.getDueDate());
            preparedStatement.setString(5, assignment.getDueTime());
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public void updateAssignment(Assignment oldContent, Assignment newContent, Course activeCourse) throws SQLException {
        try {
            String query = "UPDATE current_assignments SET assignmentName = ?, assignmentDescription = ?, dueDate = ?, dueTime = ?, status = ? WHERE courseCode = ? AND assignmentName = ? AND assignmentDescription = ?";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, newContent.getItemTitle());
            preparedStatement.setString(2, newContent.getDescription());
            preparedStatement.setString(3, newContent.getDueDate());
            preparedStatement.setString(4, newContent.getDueTime());
            if(newContent.getStatus()) {
                preparedStatement.setInt(5, 1);
            }
            else {
                preparedStatement.setInt(5, 0);
            }
            preparedStatement.setString(6, activeCourse.getCourseCode());
            preparedStatement.setString(7, oldContent.getItemTitle());
            preparedStatement.setString(8, oldContent.getDescription());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public List<Track> currentTrack(Course activeCourse) throws SQLException {
        List <Track> list = new ArrayList<>();
        try {
            String query = "SELECT * FROM current_track";
            statement = connectionToActiveDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                String courseCode = resultSet.getString("courseCode");
                if(courseCode.equals(activeCourse.getCourseCode())) {
                    Track track = new Track();
                    track.setItemTitle(resultSet.getString("topicName"));
                    track.setStatus(resultSet.getInt("status") == 1);
                    track.setChapterName(resultSet.getString("chapterName"));

                    list.add(track);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        return list;
    }

    public void addNewTrack(Track track, Course activeCourse) throws SQLException {
        try {

            String query = "INSERT INTO current_track (courseCode, chapterName, topicName, status) VALUES(?, ?, ?, ?)";

            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, activeCourse.getCourseCode());
            preparedStatement.setString(2, track.getChapterName());
            preparedStatement.setString(3, track.getItemTitle());
            preparedStatement.setInt(4, 0);

            preparedStatement.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public void deleteTrack(Track track, Course activeCourse) throws SQLException {
        try {
            String query = "DELETE FROM current_track WHERE courseCode = ? AND chapterName = ? AND topicName = ?";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, activeCourse.getCourseCode());
            preparedStatement.setString(2, track.getChapterName());
            preparedStatement.setString(3, track.getItemTitle());
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public void updateTrack(Track oldContent, Track newContent, Course activeCourse) throws SQLException {
        try {
            String query = "UPDATE current_track SET chapterName = ?, topicName = ?, status = ? WHERE courseCode = ? AND chapterName = ? AND topicName = ?";

            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, newContent.getChapterName());
            preparedStatement.setString(2, newContent.getItemTitle());
            if(newContent.getStatus()) {
                preparedStatement.setInt(3, 1);
            }
            else {
                preparedStatement.setInt(3, 0);
            }
            preparedStatement.setString(4, activeCourse.getCourseCode());
            preparedStatement.setString(5, oldContent.getChapterName());
            preparedStatement.setString(6, oldContent.getItemTitle());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public List<ToDoList> currentToDoList() throws SQLException {
        List <ToDoList> list = new ArrayList<>();
        try {
            String query = "SELECT * FROM current_todolist";
            statement = connectionToActiveDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                ToDoList item = new ToDoList();
                item.setItemTitle(resultSet.getString("Task"));
                item.setStatus(resultSet.getInt("Status") == 1);
                list.add(item);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        return list;
    }

    public void addNewTask(ToDoList item) throws SQLException {
        try {
            String taskName = item.getItemTitle();
            int status = 0;

            String query = "INSERT INTO current_todolist (Task, Status) VALUES(?, ?)";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, taskName);
            preparedStatement.setInt(2, status);
            preparedStatement.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public void deleteTask(ToDoList item) throws SQLException {
        try {
            String query = "DELETE FROM current_todolist WHERE Task = ?";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, item.getItemTitle());
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public void updateTask(ToDoList oldContent, ToDoList newContent) throws SQLException {
        try {
            String taskName = newContent.getItemTitle();
            int status;
            if(newContent.getStatus()) {
                status = 1;
            }
            else {
                status = 0;
            }

            String oldTaskName = oldContent.getItemTitle();

            String query = "UPDATE current_todolist SET Task = ?, Status = ? WHERE Task = ?";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, taskName);
            preparedStatement.setInt(2, status);
            preparedStatement.setString(3, oldTaskName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public List<Event> currentEventsList() throws SQLException {
        List <Event> list = new ArrayList<>();
        try {
            String query = "SELECT * FROM current_events";
            statement = connectionToActiveDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                Event event = new Event();
                event.setEventName(resultSet.getString("eventName"));
                event.setEventType(resultSet.getString("eventType"));
                event.setStartTime(resultSet.getString("startTime"));
                event.setEndTime(resultSet.getString("endTime"));
                event.setEventDate(resultSet.getString("eventDate"));
                list.add(event);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        return list;
    }

    public void addNewEvent(Event event) throws SQLException {
        try {
            String query = "INSERT INTO current_events (eventName, eventType, startTime, endTime, eventDate) VALUES(?, ?, ?, ?, ?)";

            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, event.getEventName());
            preparedStatement.setString(2, event.getEventType());
            preparedStatement.setString(3, event.getStartTime());
            preparedStatement.setString(4, event.getEndTime());
            preparedStatement.setString(5, event.getEventDate());

            preparedStatement.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public void deleteEvent(Event event) throws SQLException {
        try {
            String query = "DELETE FROM current_events WHERE eventName = ? AND eventType = ? AND startTime = ? AND endTime = ? AND eventDate = ?";

            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, event.getEventName());
            preparedStatement.setString(2, event.getEventType());
            preparedStatement.setString(3, event.getStartTime());
            preparedStatement.setString(4, event.getEndTime());
            preparedStatement.setString(5, event.getEventDate());

            preparedStatement.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public void updateEvent(Event oldContent, Event newContent) throws SQLException {
        try {
            String query = "UPDATE current_events SET eventName = ?, eventType = ?, startTime = ?, endTime = ?, eventDate = ? WHERE eventName = ? AND eventType = ? AND startTime = ? AND endTime = ? AND eventDate = ?";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);

            preparedStatement.setString(1, newContent.getEventName());
            preparedStatement.setString(2, newContent.getEventType());
            preparedStatement.setString(3, newContent.getStartTime());
            preparedStatement.setString(4, newContent.getEndTime());
            preparedStatement.setString(5, newContent.getEventDate());

            preparedStatement.setString(6, oldContent.getEventName());
            preparedStatement.setString(7, oldContent.getEventType());
            preparedStatement.setString(8, oldContent.getStartTime());
            preparedStatement.setString(9, oldContent.getEndTime());
            preparedStatement.setString(10, oldContent.getEventDate());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public void updateResult(ResultSheet resultSheet) throws SQLException {
        try {
            String query = "UPDATE current_courses SET Internal_Evaluation = ?, Final_Exam = ?, Grade = ?, Grade_Point = ? WHERE courseName = ?";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setDouble(1, resultSheet.getEvaluation());
            preparedStatement.setDouble(2, resultSheet.getFinalExam());
            preparedStatement.setString(3, resultSheet.getGrade());
            preparedStatement.setDouble(4, resultSheet.getGradePoint());
            preparedStatement.setString(5, resultSheet.getCourseName());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public ObservableList<ResultSheet> getDataUsers() throws SQLException {
        ObservableList<ResultSheet> list = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM current_courses";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                list.add(new ResultSheet(resultSet.getString("courseName"), resultSet.getFloat("Internal_Evaluation"), resultSet.getFloat("Final_Exam"), resultSet.getString("Grade"),resultSet.getFloat("Grade_Point")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            preparedStatement.close();
            resultSet.close();
        }

        return list;
    }

    public List<Attendance> currentAttendancelist() throws SQLException {
        List <Attendance> list = new ArrayList<>();
        try {
            String query = "SELECT * FROM current_courses";
            statement = connectionToActiveDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                Attendance item = new Attendance();
                item.setCourseName(resultSet.getString("courseName"));
                item.setAttendedClasses(resultSet.getInt("attendance"));
                item.setCourseCode(resultSet.getString("courseCode"));
                item.setCourseTeacherName(resultSet.getString("courseTeacherName"));
                item.setTotalClasses(resultSet.getInt("Total_Attendance"));
                list.add(item);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        return list;
    }

    public boolean isCourseFound(String courseName) throws SQLException {

        boolean courseFound = false;
        try {
            String sql = "SELECT * FROM current_courses WHERE courseName = ?";
            preparedStatement  = connectionToActiveDatabase.prepareStatement(sql);
            preparedStatement.setString(1, courseName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                courseFound = true;
            }
        } catch (SQLException ex) {
            System.out.println("Data retrieval failed!");
            ex.printStackTrace();
        }finally {
            preparedStatement.close();
            resultSet.close();
        }
        return courseFound;
    }

    public void UpdateAttendance (int attendance,int totalClasses, String courseName) throws SQLException {
        try {
            String sql = "UPDATE current_courses SET attendance = ?, Total_Attendance = ? WHERE courseName = ?";
            preparedStatement  = connectionToActiveDatabase.prepareStatement(sql);
            preparedStatement.setInt(1, attendance);
            preparedStatement.setInt(2,totalClasses);
            preparedStatement.setString(3, courseName);
            preparedStatement.executeUpdate();
            System.out.println("Data updated in database: " + courseName + ", " + attendance+" "+totalClasses);
        } catch (SQLException ex) {
            System.out.println("Data updating failed!");
            ex.printStackTrace();
        }finally {
            preparedStatement.close();
        }
    }

    public BarChart<String, Number> loadAttendance() throws SQLException {
        BarChart<String, Number> barChart = null;
        try {
            // Load data from database
            String query = "SELECT * FROM current_courses";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());

            while (resultSet.next()) {
                float percentage ;
                String courseName = resultSet.getString("courseName");
                int attendance = resultSet.getInt("attendance");
                int totalClasses = resultSet.getInt("Total_Attendance");
                if(totalClasses==0)
                    percentage=100;
                else
                percentage= (attendance*100)/totalClasses;
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(courseName);
                series.getData().add(new XYChart.Data<>(courseName, percentage));
                barChart.getData().add(series);
            }

            System.out.println("Data loaded from database!");
        } catch (SQLException ex) {
            System.out.println("Data loading failed!");
            ex.printStackTrace();
        }finally {
            preparedStatement.close();
            resultSet.close();
        }

        return barChart;
    }

    public List<Book> currentBookList() throws SQLException {
        List <Book> list = new ArrayList<>();
        try {
            String query = "SELECT * FROM BookList";
            statement = connectionToActiveDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                Book item = new Book();
                item.setCourseName(resultSet.getString("CourseName"));
                item.setBookName(resultSet.getString("BookTitle"));
                item.setBookURL(resultSet.getString("BookURL"));
                list.add(item);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        return list;
    }

    public void addNewBook(Book item) throws SQLException {
        try {
            String bookName = item.getBookName();
            String CourseName = item.getCourseName();
            String BookURL = item.getBookURL();
            int status = 0;

            String query = "INSERT INTO BookList (CourseName, BookTitle, BookURL) VALUES(?, ?, ?)";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, CourseName);
            preparedStatement.setString(2, bookName);
            preparedStatement.setString(3, BookURL);

            preparedStatement.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public void deleteBook(Book item) throws SQLException {
        try {
            String query = "DELETE FROM BookList WHERE BookTitle = ?";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, item.getBookName());
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public void updateBook(Book oldContent, Book newContent) throws SQLException {
        try {
            String BookName = newContent.getBookName();
            String BookURL = newContent.getBookURL();


            String oldBookName = oldContent.getBookName();
            String oldCourseName = oldContent.getCourseName();
            String oldBookURL = oldContent.getBookURL();

            String query = "UPDATE BookList SET BookTitle = ?, BookURL = ? WHERE CourseName = ?";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            preparedStatement.setString(1, BookName);
            preparedStatement.setString(2, BookURL);
            preparedStatement.setString(3, oldCourseName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
        }
    }

    public int attendancePercentage() throws SQLException {
        int totalAttendedClasses = 0;
        int totalClasses = 0;

        try {
            // Load data from database
            String query = "SELECT * FROM current_courses";
            preparedStatement = connectionToActiveDatabase.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                totalAttendedClasses += resultSet.getInt("attendance");
                totalClasses += resultSet.getInt("Total_Attendance");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            preparedStatement.close();
            resultSet.close();
        }

        if(totalClasses==0)return 100;

        int percentage = totalAttendedClasses * 100 / totalClasses;

        return percentage;
    }

    public int taskPercentage() throws SQLException {
        int doneTask = 0;
        int totalTask = 0;
        try {
            String query = "SELECT * FROM current_todolist";
            statement = connectionToActiveDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                totalTask++;
                if(resultSet.getInt("Status") == 1) {
                    doneTask++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        if(totalTask==0)
            return 100;

        int percentage = doneTask * 100 / totalTask;
        return percentage;
    }

    public int eventsToday() throws SQLException {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        String formattedCurrentDate = currentDate.format(formatter);

        int count = 0;

        try {
            String query = "SELECT * FROM current_events";
            statement = connectionToActiveDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                if(resultSet.getString("eventDate").equals(formattedCurrentDate)) {
                    count++;
                }

            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        return count;
    }

    public List<Event> upcomingEvents() throws SQLException {
        List<Event> list = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        LocalDate formattedCurrentDate = LocalDate.parse(currentDate.format(formatter), formatter);

        try {
            String query = "SELECT * FROM current_events";
            statement = connectionToActiveDatabase.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()) {
                String date = resultSet.getString("eventDate");
                LocalDate eventDate = LocalDate.parse(date, formatter);

                if(eventDate.isBefore(formattedCurrentDate)) {
                    continue;
                }

                Event event = new Event();
                event.setEventName(resultSet.getString("eventName"));
                event.setEventType(resultSet.getString("eventType"));
                event.setStartTime(resultSet.getString("startTime"));
                event.setEndTime(resultSet.getString("endTime"));
                event.setEventDate(resultSet.getString("eventDate"));
                list.add(event);

            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
            resultSet.close();
        }

        return list;
    }
}
