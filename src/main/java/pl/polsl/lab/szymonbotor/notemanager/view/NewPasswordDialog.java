package pl.polsl.lab.szymonbotor.notemanager.view;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;

public class NewPasswordDialog extends Dialog<String[]> {

    public NewPasswordDialog(String title, String header) {
        super();

        setTitle(title);
        setHeaderText(header);
        setGraphic(null);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField passField = new PasswordField();
        PasswordField repeatPassField = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Password: "), 0, 0);
        grid.add(passField, 1, 0);
        grid.add(new Label("Repeat password: "), 0, 1);
        grid.add(repeatPassField, 1, 1);
        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new String[] { passField.getText(), repeatPassField.getText() };
            }
            return null;
        });
    }
}
