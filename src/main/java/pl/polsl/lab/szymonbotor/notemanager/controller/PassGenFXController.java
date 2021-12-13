package pl.polsl.lab.szymonbotor.notemanager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCharacterException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidPasswordLengthException;
import pl.polsl.lab.szymonbotor.notemanager.model.PasswordGen;

/**
 * Controller class of the password generator view.
 * @author Szymon Botor
 * @version 1.0
 */
public class PassGenFXController {

    /**
     * Check box controlling whether the generated password should include uppercase letters.
     */
    @FXML
    private CheckBox uppercaseCheckBox;
    /**
     * Check box controlling whether the generated password should include digits.
     */
    @FXML
    private CheckBox digitsCheckBox;
    /**
     * Check box controlling whether the generated password should include special symbols.
     */
    @FXML
    private CheckBox otherSymbols;

    /**
     * Text field in which the generated password appears. It is read only.
     */
    @FXML
    private TextField generatedPassword;
    /**
     * Text field in which the password length should be specified.
     */
    @FXML
    private TextField passLenField;

    /**
     * Constructor of the PassGenFXController class.
     */
    public PassGenFXController() {
    }

    /**
     * This method is called when the generate button is clicked. A new password is generated and displayed according to the specified parameters.
     * If the password length is incorrect (empty, contains non-digits or the value is too long) an error is displayed.
     * @see PassGenFXController#generatedPassword
     * @see PassGenFXController#passLenField
     */
    @FXML
    private void generateButtonClicked() {
        String symbolStr = "";

        if (uppercaseCheckBox.isSelected()) {
            symbolStr = symbolStr + "u";
        }
        if (digitsCheckBox.isSelected()) {
            symbolStr = symbolStr + "d";
        }
        if (otherSymbols.isSelected()) {
            symbolStr = symbolStr + "o";
        }

        try {
            int passLen = Integer.parseInt(passLenField.getText());
            PasswordGen passGen = new PasswordGen(passLen, symbolStr);
            generatedPassword.setText(passGen.generate());
        } catch (InvalidCharacterException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        } catch (InvalidPasswordLengthException | NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid password length.");
            alert.showAndWait();
        }
    }

    /**
     * This method is called when the copy button is clicked. If the password has been generated it is copied to the clipboard.
     */
    @FXML
    private void copyButtonClicked() {
        String password = generatedPassword.getText();
        if (!password.isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(password);
            clipboard.setContent(content);
        }
    }
}
