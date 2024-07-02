module models {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires sqlite.jdbc;

    opens models to javafx.fxml;
    exports models;
    opens controllers to javafx.fxml;
    exports controllers;
}