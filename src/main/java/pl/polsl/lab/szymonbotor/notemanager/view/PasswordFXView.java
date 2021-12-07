package pl.polsl.lab.szymonbotor.notemanager.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PasswordFXView extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(MainFXView.class.getResource("PasswordFXView.fxml")));
        stage.setTitle("Password needed");
        stage.setScene(scene);

        stage.show();
    }
}
