package pl.polsl.lab.szymonbotor.notemanager.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import pl.polsl.lab.szymonbotor.notemanager.enums.noteAddOption;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.model.Note;
import pl.polsl.lab.szymonbotor.notemanager.view.PasswordFXView;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class MainFXController {
    @FXML
    private TreeView<String> noteTree;
    @FXML
    private TextArea noteContent;
    @FXML
    private MenuButton addButton;

    @FXML
    private void newNoteClicked(ActionEvent event) {

    }

    @FXML
    private void openNoteClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open note");
        File inputFile = fileChooser.showOpenDialog(addButton.getScene().getWindow());

        if (inputFile != null && inputFile.exists()) {
            Note openedNote = readNote(inputFile);
        }
    }

    private String askForPassword() {
        PasswordFXView.launch();

        return new String("");
    }

    private Note readNote(File inputFile) {
        Note note = new Note();
        try {
            note.read(new FileInputStream(inputFile), askForPassword());
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidCryptModeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }
}