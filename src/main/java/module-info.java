module pl.polsl.lab.szymonbotor.notemanager {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.base;

    requires transitive jakarta.jakartaee.api;

    exports pl.polsl.lab.szymonbotor.notemanager.model;
    exports pl.polsl.lab.szymonbotor.notemanager.view;
    opens pl.polsl.lab.szymonbotor.notemanager.view to javafx.fxml;
    exports pl.polsl.lab.szymonbotor.notemanager.controller;
    opens pl.polsl.lab.szymonbotor.notemanager.controller to javafx.fxml;
    exports pl.polsl.lab.szymonbotor.notemanager.exceptions;
    exports pl.polsl.lab.szymonbotor.notemanager.enums;
}