package models;
import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    public static Connection connectToUsersDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:user.db");
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Connection connect(String databaseName) {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
