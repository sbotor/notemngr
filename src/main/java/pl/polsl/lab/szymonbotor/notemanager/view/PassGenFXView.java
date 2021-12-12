package pl.polsl.lab.szymonbotor.notemanager.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PassGenFXView extends Stage {

    public PassGenFXView() throws IOException {
        super();
        setTitle("Password generator");
        setScene(new Scene(FXMLLoader.load(MainFXView.class.getResource("PassGenFXView.fxml"))));
    }

}
