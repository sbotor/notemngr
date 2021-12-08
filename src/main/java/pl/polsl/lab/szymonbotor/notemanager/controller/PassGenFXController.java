package pl.polsl.lab.szymonbotor.notemanager.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
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
        } catch (InvalidPasswordLengthException | InvalidCharacterException e) {
            e.printStackTrace();
        }
    }
}
