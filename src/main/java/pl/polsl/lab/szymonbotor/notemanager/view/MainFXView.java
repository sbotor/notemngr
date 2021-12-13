package pl.polsl.lab.szymonbotor.notemanager.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * This is the view class of the main application window.
 */
public class MainFXView extends Application {

    /**
     * Constructor of the MainFXView class.
     */
    public MainFXView() {
    }

    /**
     * This method is used to load and create the window of the application.
     * @param stage stage to load the JavaFX scene into.
     */
    @Override
    public void start(Stage stage) {
        URL sceneURL = MainFXView.class.getResource("MainFXView.fxml");
        if (sceneURL == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load main view file \"MainFXView.fxml\".");
            alert.showAndWait();
            return;
        }

        Scene scene;
        try {
            scene = new Scene(FXMLLoader.load(sceneURL));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load main view file \"MainFXView.fxml\".");
            alert.showAndWait();
            return;
        }
        stage.setTitle("NoteManager");
        stage.setScene(scene);

        stage.show();
    }

    /**
     * Method used to launch the application.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}