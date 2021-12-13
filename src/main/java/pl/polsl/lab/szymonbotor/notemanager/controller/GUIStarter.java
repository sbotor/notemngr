package pl.polsl.lab.szymonbotor.notemanager.controller;

import pl.polsl.lab.szymonbotor.notemanager.view.MainFXView;

/**
 * This class is used to start the whole JavaFX application.
 * @author Szymon Botor
 * @version 1.0
 */
public class GUIStarter {

    /**
     * Constructor of the GUIStarter class.
     */
    public GUIStarter() {
    }

    /**
     * This method is the main static method used to start the JavaFX application.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        MainFXView.main(args);
    }
}
