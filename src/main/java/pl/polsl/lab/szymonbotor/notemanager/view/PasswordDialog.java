package pl.polsl.lab.szymonbotor.notemanager.view;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;

public class PasswordDialog extends Dialog<String> {

    public PasswordDialog(String title, String header) {
        super();
        setTitle(title);
        setHeaderText(header);
        setGraphic(null);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField passField = new PasswordField();
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(10);
        box.getChildren().addAll(new Label("Password: "), passField);
        getDialogPane().setContent(box);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return passField.getText();
            }
            return null;
        });
    }
}
