package pl.polsl.lab.szymonbotor.notemanager.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.NoteTooLongException;
import pl.polsl.lab.szymonbotor.notemanager.model.Note;
import pl.polsl.lab.szymonbotor.notemanager.model.NoteHistory;
import pl.polsl.lab.szymonbotor.notemanager.view.PassGenFXView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class MainFXController {
    public static final String MISSING_FILE_PREF = "[MISSING]";
    private static final String HISTORY_ERROR_MSG = "Could not load note history.";

    private static PassGenFXView passGen;
    private NoteFileFXController noteFileController;

    private Note note = null;
    private final Path historyPath = Path.of("history.txt");
    private NoteHistory history;
    private boolean historyEmpty;

    @FXML
    private ListView<String> noteList;

    @FXML
    private Label contentLabel;
    @FXML
    private TextArea noteContent;

    @FXML
    private MenuButton addButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button saveAsButton;
    @FXML
    private Button passGenButton;

    @FXML
    private void initialize() {

        note = new Note();
        noteContent.textProperty().addListener((observableValue, s, t1) -> {
            try {
                note.change(noteContent.getText());
            } catch (NoteTooLongException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
            updateNoteInfo();
        });
        noteFileController = new NoteFileFXController(this);

        initButtons();

        passGen = null;
        try {
            passGen =  new PassGenFXView();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }

        history = null;
        try {
            history = new NoteHistory(historyPath.toAbsolutePath().toString());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
        reloadNoteList();

        initNoteListContextMenu();
    }

    private void initButtons() {
        ButtonBar.setButtonData(addButton, ButtonBar.ButtonData.LEFT);
        ButtonBar.setButtonData(saveButton, ButtonBar.ButtonData.LEFT);
        ButtonBar.setButtonData(saveAsButton, ButtonBar.ButtonData.LEFT);
        ButtonBar.setButtonData(passGenButton, ButtonBar.ButtonData.RIGHT);
    }

    private void initNoteListContextMenu() {
        MenuItem open = new MenuItem("Open");
        open.setOnAction(actionEvent -> {
            openFromNoteList(noteList.getFocusModel().getFocusedIndex());
        });

        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(actionEvent -> {
            int index = noteList.getFocusModel().getFocusedIndex();
            removeNote(index);
        });

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(actionEvent -> {
            deleteNote(noteList.getFocusModel().getFocusedIndex());
        });

        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(open, remove, delete);
        menu.setOnShown(windowEvent -> {
            String item = noteList.getFocusModel().getFocusedItem();
            if (!item.startsWith(MISSING_FILE_PREF) && !historyEmpty) {
                open.setDisable(false);
                remove.setDisable(false);
                delete.setDisable(false);
            } else if (item.startsWith(MISSING_FILE_PREF)) {
                open.setDisable(true);
                remove.setDisable(false);
                delete.setDisable(true);
            } else {
                open.setDisable(true);
                remove.setDisable(true);
                delete.setDisable(true);
            }
        });

        noteList.setContextMenu(menu);

        reloadNoteList();
    }

    @FXML
    private void newNoteClicked(ActionEvent event) {
        try {
            if (note.isSaved() || noteFileController.save(note, true)) {
                if (note.hasFile()) {
                    updateHistoryAndReloadList(note);
                }

                noteContent.clear();
                note = new Note();
                updateNoteInfo();
            }
        } catch (InvalidCryptModeException | CryptException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void saveButtonClicked(ActionEvent event) {
        try {
            if (!note.isSaved() && noteFileController.save(note, false)) {
                if (note.hasFile()) {
                    updateHistoryAndReloadList(note);
                    updateNoteInfo();
                }
            }
        } catch (InvalidCryptModeException | CryptException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void saveAsButtonClicked(ActionEvent event) {
        try {
            if (noteFileController.saveAs(note, false)) {
                // If saving was not canceled
                updateHistoryAndReloadList(note);
                updateNoteInfo();
            }
        } catch (InvalidCryptModeException | CryptException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void openNoteClicked(ActionEvent event) {
        try {
            if (note.isSaved() || noteFileController.save(note, true)) {
                if (note.hasFile()) {
                    updateHistoryAndReloadList(note);
                }

                Optional<Note> newNote = noteFileController.openNote();
                if (newNote.isPresent()) {
                    noteContent.setText(newNote.get().getContent());
                    note = newNote.get();
                    updateHistoryAndReloadList(note);
                    updateNoteInfo();
                }
            }
        } catch (InvalidCryptModeException | CryptException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void generateButtonClicked(ActionEvent event) {
        if (!passGen.isShowing()) {
            passGen.show();
        }
    }

    private void updateNoteInfo() {
        String noteName, noteDir;
        if (note.hasFile()) {
            noteName = note.getFilePath().getFileName().toString();
            noteDir = "(" + note.getFilePath().toAbsolutePath() + ")";
        } else {
            noteName = "New Note";
            noteDir = "";
        }

        if (!note.isSaved()) {
            contentLabel.setText(noteName + "* " + noteDir);
        } else {
            contentLabel.setText(noteName + " " + noteDir);
        }
    }

    private void reloadNoteList() {
        noteList.getItems().clear();

        if (history == null) {
            noteList.getItems().add(HISTORY_ERROR_MSG);
            historyEmpty = true;
            return;
        } else if (history.getNotes().isEmpty()) {
            noteList.getItems().add("History is empty");
            historyEmpty = true;
            return;
        }

        for (File noteFile: history.getNotes()) {
            String fileCatalog = Path.of(noteFile.getAbsolutePath()).getParent().toString();
            String fileName = noteFile.getName();

            if (!noteFile.exists()) {
                fileName = MISSING_FILE_PREF + " " + fileName;
            }

            noteList.getItems().add(fileName + " (" + fileCatalog + ")");
        }
        historyEmpty = false;
    }

    private void updateHistoryAndReloadList() throws IOException {
        history.save();
        reloadNoteList();
    }

    private void updateHistoryAndReloadList(Note note) throws IOException {
        history.add(note);
        updateHistoryAndReloadList();
    }

    private void openFromNoteList(int itemIndex) {
        File file = history.get(itemIndex);

        try {
            if (note.isSaved() || noteFileController.save(note, true)) {
                if (note.hasFile()) {
                    updateHistoryAndReloadList(note);
                }
                Optional<Note> newNote = noteFileController.openNote(file);
                if (newNote.isPresent()) {
                    noteContent.setText(newNote.get().getContent());
                    note = newNote.get();
                    updateHistoryAndReloadList(note);
                    updateNoteInfo();
                }
            }
        } catch (InvalidCryptModeException | CryptException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    private void removeNote(int itemIndex) {
        File removedNote = history.get(itemIndex);
        try {
            if (note.hasFile() && note.getFile().getAbsolutePath().equals(removedNote.getAbsolutePath())) {
                newNoteClicked(null);
            }
            history.getNotes().remove(itemIndex);
            updateHistoryAndReloadList();
            updateNoteInfo();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    private void deleteNote(int itemIndex) {
        File deletedNote = history.get(itemIndex);
        try {
            boolean deleted = noteFileController.deleteNote(deletedNote);
            if (note.hasFile() &&
                    note.getFile().getAbsolutePath().equals(deletedNote.getAbsolutePath()) &&
                    deleted) {
                newNoteClicked(null);
            }
            if (deleted) {
                history.getNotes().remove(itemIndex);
                updateHistoryAndReloadList();
                updateNoteInfo();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    public Scene getScene() {
        return noteList.getScene();
    }
}