module tips {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires junit;

    opens com.csce247 to javafx.fxml;
    exports com.csce247;

    opens com.model to javafx.fxml;
    exports com.model;
}
