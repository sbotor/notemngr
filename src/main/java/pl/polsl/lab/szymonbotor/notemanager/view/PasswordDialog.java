package pl.polsl.lab.szymonbotor.notemanager.view;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;

/**
 * This is the view class of the dialog asking the user for a password to an existing note.
 * It extends the built-in Dialog class returning a String which is the provided password.
 */
public class PasswordDialog extends Dialog<String> {

    /**
     * Constructor of the dialog.
     * @param title dialog title.
     * @param header dialog header text.
     */
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
