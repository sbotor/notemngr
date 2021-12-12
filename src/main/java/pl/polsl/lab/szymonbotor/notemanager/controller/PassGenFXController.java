package pl.polsl.lab.szymonbotor.notemanager.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCharacterException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidPasswordLengthException;
import pl.polsl.lab.szymonbotor.notemanager.model.PasswordGen;

public class PassGenFXController {

    @FXML
    private CheckBox uppercaseCheckBox;
    @FXML
    private CheckBox digitsCheckBox;
    @FXML
    private CheckBox otherSymbols;

    @FXML
    private TextField generatedPassword;
    @FXML
    private TextField passLenField;

    @FXML
    private void generateButtonClicked(ActionEvent event) {
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

    @FXML
    private void copyButtonClicked(ActionEvent event) {
        String password = generatedPassword.getText();
        if (!password.isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(password);
            clipboard.setContent(content);
        }
    }
}
