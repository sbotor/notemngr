module pl.polsl.lab.szymonbotor.notemanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    exports pl.polsl.lab.szymonbotor.notemanager.view;
    opens pl.polsl.lab.szymonbotor.notemanager.view to javafx.fxml;
    exports pl.polsl.lab.szymonbotor.notemanager.controller;
    opens pl.polsl.lab.szymonbotor.notemanager.controller to javafx.fxml;
}