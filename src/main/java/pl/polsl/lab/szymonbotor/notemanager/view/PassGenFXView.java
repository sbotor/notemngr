package pl.polsl.lab.szymonbotor.notemanager.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * This class represents the view of the password generator. It loads the fxml file and creates a window from it.
 */
public class PassGenFXView extends Stage {

    /**
     * Constructor of the view class.
     * @throws IOException When the specified fxml file could not be loaded.
     */
    public PassGenFXView() throws IOException {
        super();

        URL sceneURL = PassGenFXView.class.getResource("PassGenFXView.fxml");
        if (sceneURL == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load password generator view file \"PassGenFXView.fxml\".");
            alert.showAndWait();
            throw new IOException("Could not load password generator view file \"PassGenFXView.fxml\".");
        }

        setTitle("Password generator");
        setScene(new Scene(FXMLLoader.load(sceneURL)));
    }

}
