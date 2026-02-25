module com.csce247 {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;

    opens com.csce247 to javafx.fxml;
    exports com.csce247;

    opens com.model to javafx.fxml;
    exports com.model;
}
