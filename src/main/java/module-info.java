module com.example.themannyhub {
    // Required JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Required for JSON parsing
    requires org.json;

    // Export packages that JavaFX needs to access
    opens com.example.themannyhub.controllers to javafx.fxml;
    opens com.example.themannyhub.models to javafx.fxml;
    opens com.example.themannyhub to javafx.graphics, javafx.fxml;

    // Export packages for other modules to use
    exports com.example.themannyhub;
    exports com.example.themannyhub.controllers;
    exports com.example.themannyhub.models;
    exports com.example.themannyhub.services;
    exports com.example.themannyhub.data;
    exports com.example.themannyhub.utils;
}