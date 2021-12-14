package pl.polsl.lab.szymonbotor.notemanager.controller;

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

/**
 * This class is the controller of the main application window.
 * @author Szymon Botor
 * @version 1.0
 */
public class MainFXController {
    /**
     * This is the prefix appended to note's name in the history list if it is not found on disk.
     */
    public static final String MISSING_FILE_PREF = "[MISSING]";

    /**
     * The text displayed in the history list if no history could be loaded.
     */
    private static final String HISTORY_ERROR_MSG = "Could not load note history.";

    /**
     * The view of the password generator.
     */
    private static PassGenFXView passGen;
    /**
     * Controller of note file IO.
     */
    private NoteFileFXController noteFileController;

    /**
     * The note that is currently open.
     */
    private Note note = null;
    /**
     * Path to the note history file.
     */
    private final Path historyPath = Path.of("history.txt");
    /**
     * Note history representing recently opened or saved notes.
     */
    private NoteHistory history;
    /**
     * Boolean value representing if the history is empty. If no history was loaded it is also set to true.
     */
    private boolean historyEmpty;

    /**
     * This is the ListView object in which note history is displayed.
     */
    @FXML
    private ListView<String> noteList;

    /**
     * This label is used to display the name and path of the currently opened note or "NewNote" if
     * no note is opened.
     */
    @FXML
    private Label contentLabel;
    /**
     * This is the area in which the note content is modified and displayed.
     */
    @FXML
    private TextArea noteContent;

    /**
     * This button is used to add a note to the history and open it. It has two options:
     * "New" (create a new note) and "Open" (open an existing note).
     */
    @FXML
    private MenuButton addButton;
    /**
     * This button is used to save the note to the disk to the file that it was previously opened from/saved to.
     * If the note does not have a file it works as the saveAsButton.
     * @see MainFXController#saveAsButton
     */
    @FXML
    private Button saveButton;
    /**
     * This button is used to save the note to a file specified by the user.
     */
    @FXML
    private Button saveAsButton;
    /**
     * This button opens the password generator window.
     */
    @FXML
    private Button passGenButton;

    /**
     * Constructor of the MainFXController class.
     */
    public MainFXController() {
    }

    /**
     * This method is called after all the @FXML fields have been injected.
     * It performs all the necessary initializations.
     */
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

    /**
     * This method initializes the window's buttons ensuring they are at the right position.
     */
    private void initButtons() {
        ButtonBar.setButtonData(addButton, ButtonBar.ButtonData.LEFT);
        ButtonBar.setButtonData(saveButton, ButtonBar.ButtonData.LEFT);
        ButtonBar.setButtonData(saveAsButton, ButtonBar.ButtonData.LEFT);
        ButtonBar.setButtonData(passGenButton, ButtonBar.ButtonData.RIGHT);
    }

    /**
     * This method creates the context menu and binds it to the noteHistory ListView.
     * @see MainFXController#noteList
     */
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

    /**
     * This method is called when a new note has to be created for example after clicking the
     * "New" option of the addButton. If the note is not saved the user is asked if they want to save it.
     * @see MainFXController#addButton
     */
    @FXML
    private void newNoteClicked() {
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

    /**
     * This method is used to save the current note to disk if it is not saved.
     * If the note has no file path attached then the user is asked to provide one.
     * @see MainFXController#saveButton
     */
    @FXML
    private void saveButtonClicked() {
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

    /**
     * This method is used to save the current note to a user-specified file.
     * @see MainFXController#saveAsButton
     */
    @FXML
    private void saveAsButtonClicked() {
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

    /**
     * This method is used to open a note from disk. The user is asked to specify a file.
     * @see MainFXController#addButton
     */
    @FXML
    private void openNoteClicked() {
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

    /**
     * This method opens the password generator window after the passGenButton is clicked.
     * @see MainFXController#passGenButton
     * @see MainFXController#passGen
     */
    @FXML
    private void generateButtonClicked() {
        if (!passGen.isShowing()) {
            passGen.show();
        }
    }

    /**
     * This method updates the contentLabel according to the current note.
     * @see MainFXController#contentLabel
     */
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

    /**
     * This method is used to reload the noteList view according to the current history.
     * @see MainFXController#noteList
     * @see MainFXController#history
     */
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

    /**
     * This method is used to save the note history to disk and reload the noteList view.
     * @throws IOException Thrown when a file error occurs during history saving.
     * @see MainFXController#noteList
     * @see MainFXController#history
     */
    private void updateHistoryAndReloadList() throws IOException {
        history.save();
        reloadNoteList();
    }

    /**
     * This method is used to add a new note to the note history, save it to disk and reload the noteList view.
     * @param note note to add to the note history.
     * @throws IOException Thrown when a file error occurs during history saving.
     * @see MainFXController#noteList
     * @see MainFXController#history
     */
    private void updateHistoryAndReloadList(Note note) throws IOException {
        history.add(note);
        updateHistoryAndReloadList();
    }

    /**
     * This method is used to open a note from the history. The note is
     * specified by the passed parameter.
     * @param itemIndex index of the note to be opened from the current note history.
     * @see MainFXController#history
     */
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

    /**
     * This method is used to remove a note from the history. The note is
     * specified by the passed parameter.
     * @param itemIndex index of the note to be removed from the current note history.
     * @see MainFXController#history
     */
    private void removeNote(int itemIndex) {
        File removedNote = history.get(itemIndex);
        try {
            if (note.hasFile() && note.getFile().getAbsolutePath().equals(removedNote.getAbsolutePath())) {
                newNoteClicked();
            }
            history.getNotes().remove(itemIndex);
            updateHistoryAndReloadList();
            updateNoteInfo();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * This method is used to delete a note from the history and remove it from disk.
     * The note is specified by the passed parameter.
     * @param itemIndex index of the note to be deleted from the current note history and from disk.
     * @see MainFXController#history
     */
    private void deleteNote(int itemIndex) {
        File deletedNote = history.get(itemIndex);
        try {
            boolean deleted = noteFileController.deleteNote(deletedNote);
            if (note.hasFile() &&
                    note.getFile().getAbsolutePath().equals(deletedNote.getAbsolutePath()) &&
                    deleted) {
                newNoteClicked();
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

    /**
     * This method is used to get the scene that the controller is attached to.
     * @return Scene object that the current controller is bound to. To find the scene it uses the
     * noteList view.
     * @see MainFXController#noteList
     */
    public Scene getScene() {
        return noteList.getScene();
    }

    /**
     * This method is used to get the view of the password generator bound to the controller.
     * @return PassGenFXView object that the controller uses to display the password generator view.
     */
    public PassGenFXView getPassGenView() {
        return passGen;
    }
}