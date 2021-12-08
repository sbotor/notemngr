package pl.polsl.lab.szymonbotor.notemanager.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.NoteTooLongException;
import pl.polsl.lab.szymonbotor.notemanager.model.Note;
import pl.polsl.lab.szymonbotor.notemanager.model.NoteHistory;
import pl.polsl.lab.szymonbotor.notemanager.view.MainFXView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class MainFXController {

    private static Stage generatorStage;

    private Note note = null;
    boolean saved = true;
    // TODO: make this a new class (maybe extending NoteHistory) that checks if the files still exist
    NoteHistory history;

    @FXML
    private TreeView<String> noteTree;
    @FXML
    private Label contentLabel;
    @FXML
    private TextArea noteContent;

    @FXML
    private ButtonBar upperButtonBar;
    @FXML
    private MenuButton addButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button passGenButton;

    @FXML
    private void initialize() {
        noteContent.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                changeSavedState(false);
            }
        });

        ButtonBar.setButtonData(addButton, ButtonBar.ButtonData.LEFT);
        ButtonBar.setButtonData(saveButton, ButtonBar.ButtonData.LEFT);
        ButtonBar.setButtonData(passGenButton, ButtonBar.ButtonData.RIGHT);

        readHistory("history.txt");

        try {
            initGenerator();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGenerator() throws IOException {
        Scene scene = new Scene(FXMLLoader.load(MainFXView.class.getResource("PassGenFXView.fxml")));
        generatorStage = new Stage();
        generatorStage.setTitle("Password generator");
        generatorStage.setScene(scene);
    }

    @FXML
    private void newNoteClicked(ActionEvent event) {
        try {
            askAndSave();
        } catch (NoteTooLongException | InvalidCryptModeException | CryptException | IOException e) {
            e.printStackTrace();
        }

        note = null;
        noteContent.clear();

        changeSavedState(true);
    }

    @FXML
    private void saveButtonClicked(ActionEvent event) {
        try {
            askAndSave();
        } catch (NoteTooLongException | InvalidCryptModeException | CryptException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openNoteClicked(ActionEvent event) {
        try {
            askAndSave();
        } catch (NoteTooLongException | InvalidCryptModeException | CryptException | IOException e) {
            e.printStackTrace();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Open note");
        File inputFile = fileChooser.showOpenDialog(addButton.getScene().getWindow());

        if (inputFile == null) {
            return;
        }

        try {
            Optional<Note> newNote = readNote(inputFile);
            if (newNote.isPresent()) {
                note = newNote.get();
                noteContent.setText(note.getContent());
            }
        } catch (InvalidCryptModeException | CryptException | IOException e) {
            e.printStackTrace();
        }

        changeSavedState(true);
    }

    @FXML
    private void generateButtonClicked(ActionEvent event) {
        if (!generatorStage.isShowing()) {
            generatorStage.show();
        }
    }

    private void readHistory(String filename) {
        TreeItem<String> rootItem = new TreeItem<String>("Note history");
        rootItem.setExpanded(true);
        noteTree.setRoot(rootItem);

        try {
            history = new NoteHistory(filename);

            for (String note : history.getNotes()) {
                File noteFile = Paths.get(note).toFile();
                TreeItem<String> item = new TreeItem<String>(noteFile.getName());
                item.getChildren().add(new TreeItem<String>("Path: " + noteFile.getAbsolutePath()));
                rootItem.getChildren().add(item);
            }

        } catch (IOException e) {
            e.printStackTrace();
            history = new NoteHistory();
        }
    }

    private void askAndSave() throws NoteTooLongException, InvalidCryptModeException, CryptException, IOException {
        if (!saved && getYesOrNo("Save note", "Do you want to save the current note?", null)) {
            String saveDir = System.getProperty("user.dir");
            String fileName = "";
            if (note != null) {
                saveDir = note.getFilePath().toAbsolutePath().getParent().toString();
                fileName = note.getFilePath().getFileName().toString();
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(saveDir));
            fileChooser.setInitialFileName(fileName);
            fileChooser.setTitle("Save note");
            File outputFile = fileChooser.showSaveDialog(addButton.getScene().getWindow());

            if (outputFile == null) {
                return;
            } else if (saveNoteAs(outputFile)) {
                changeSavedState(true);
            }
        }
    }

    // TODO: This should probably be changed according to the saveNoteAs TODO
    private Optional<String> askForText(String title, String header, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        dialog.setGraphic(null);

        return dialog.showAndWait();
    }

    private Optional<Note> readNote(File inputFile) throws InvalidCryptModeException, CryptException, IOException {
        Optional<String> password =  askForText("Password needed",
                "Enter password to view the note.", "Password:");

        if (password.isPresent()) {
            return Optional.of(new Note(inputFile.getPath(), password.get()));
        } else {
            return Optional.empty();
        }
    }

    // TODO: Create a form for the new password with optional generation
    private boolean saveNoteAs(File outputFile) throws NoteTooLongException, InvalidCryptModeException, CryptException, IOException {
        if (note == null) {
            note = new Note();
        }

        Optional<String> password = askForText("Password needed",
                "Enter a new password for the note.", "Password");

        if (password.isPresent()) {
            note.change(noteContent.getText());
            note.save(outputFile.getPath(), password.get());
            return true;
        } else {
            return false;
        }
    }

    private void changeSavedState(boolean value) {
        String noteInfo;
        if (note != null) {
            noteInfo = "Note: " + note.getFileDir();
        } else {
            noteInfo = "New Note";
        }

        if (!value) {
            saved = false;
            contentLabel.setText(noteInfo + "*");
        } else {
            saved = true;
            contentLabel.setText(noteInfo);
            history.add(note);
            try {
                history.save("history.txt");
                readHistory("history.txt");
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean getYesOrNo(String title, String header, String content) {
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        ObservableList<ButtonType> buttons = dialog.getDialogPane().getButtonTypes();
        ButtonType confirmButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        buttons.add(confirmButton);
        buttons.add(new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE));

        Optional<ButtonType> choice = dialog.showAndWait();

        if (choice.isPresent() && choice.get() == confirmButton) {
            return true;
        } else {
            return false;
        }
    }
}