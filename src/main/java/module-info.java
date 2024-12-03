module com.example.pimperium {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;

    opens pimperium.controllers to javafx.fxml;
    opens pimperium.views to javafx.fxml;
    
    exports pimperium.controllers;
    exports pimperium.views;
    exports pimperium.models;
    exports pimperium.commands;
    exports pimperium.players;
    exports pimperium.elements;
    exports pimperium.utils;
}