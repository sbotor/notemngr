package pl.polsl.lab.szymonbotor.notemanager.view;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Class representing a dialog asking the user for a new password. The password has to be specified twice.
 * The user can open a password generator window from the dialog to generate a password. The class extends the
 * built-in Dialog class returning a String array of size two holding the provided passwords. The dialog performs no
 * checking on itself.
 * @author Szymon Botor
 * @version 1.0
 */
public class NewPasswordDialog extends Dialog<String[]> {

    /**
     * Constructor of the dialog class.
     * @param title dialog title.
     * @param header dialog header text.
     * @param passGenView password generator view to be opened when the user wants to generate a password.
     */
    public NewPasswordDialog(String title, String header, PassGenFXView passGenView) {
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

        Button generateButton = new Button("Generate password");
        if (passGenView == null) {
            generateButton.setDisable(true);
        } else {
            generateButton.setOnAction(actionEvent -> {
                passGenView.hide();
                passGenView.show();
            });
        }
        grid.add(generateButton, 0, 2);

        getDialogPane().setContent(grid);
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new String[] { passField.getText(), repeatPassField.getText() };
            }
            return null;
        });
    }
}
