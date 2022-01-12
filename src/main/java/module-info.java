module pl.polsl.lab.szymonbotor.notemanager {
    requires transitive jakarta.jakartaee.api;

    exports pl.polsl.lab.szymonbotor.notemanager.model;
    exports pl.polsl.lab.szymonbotor.notemanager.view;
    exports pl.polsl.lab.szymonbotor.notemanager.controller;
    exports pl.polsl.lab.szymonbotor.notemanager.exceptions;
    exports pl.polsl.lab.szymonbotor.notemanager.enums;
}