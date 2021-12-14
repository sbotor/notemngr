package pl.polsl.lab.szymonbotor.notemanager.controller;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.model.Note;
import pl.polsl.lab.szymonbotor.notemanager.view.NewPasswordDialog;
import pl.polsl.lab.szymonbotor.notemanager.view.PasswordDialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Controller class watching over note file IO.
 * @author Szymon Botor
 * @version 1.0
 */
public class NoteFileFXController {

    /**
     * Parent controller that this file controller is bound to.
     */
    private final MainFXController parent;

    /**
     * Filter used by the FileChooser class during file directory input.
     */
    FileChooser.ExtensionFilter noteFileFilter;

    /**
     * Constructor of the NoteFileFXController.
     * @param parentController main controller that this file controller is bound to.
     */
    public NoteFileFXController(MainFXController parentController) {
        parent = parentController;
        noteFileFilter = new FileChooser.ExtensionFilter("Notes", "*" + Note.FILE_EXTENSION);
    }

    /**
     * This method is used to read a note from a specified file.
     * The user is asked for the password in a loop.
     * @param file note file to read from.
     * @return optional Note object that exists if the user was authenticated and the file was read.
     * @throws IOException Thrown when the note could not be opened.
     * @throws InvalidCryptModeException This exception is thrown when the note is decrypted by an AES object set to encryption only.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    public Optional<Note> openNote(File file) throws InvalidCryptModeException, CryptException, IOException {
        Optional<String> password =  askForPassword("Password needed",
                "Enter password to view the note.");

        if (password.isPresent()) {
            Note newNote = new Note();
            while (!newNote.read(file.getPath(), password.get())) {
                password =  askForPassword("Incorrect password",
                        "Incorrect password. Try again.");

                if (password.isEmpty()) {
                    return Optional.empty();
                }
            }

            return Optional.of(newNote);
        }

        return Optional.empty();
    }

    /**
     * This method is used to read a note from a file that is provided by the user with a dialog.
     * It asks the user for the password in a loop.
     * @return optional Note object that exists if the user was authenticated and the file was read.
     * @throws IOException Thrown when the note could not be opened.
     * @throws InvalidCryptModeException This exception is thrown when the note is decrypted by an AES object set to encryption only.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    public Optional<Note> openNote() throws InvalidCryptModeException, CryptException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Open note");
        fileChooser.getExtensionFilters().add(noteFileFilter);

        File inputFile = fileChooser.showOpenDialog(parent.getScene().getWindow());
        if (inputFile == null) {
            return Optional.empty();
        }

        return openNote(inputFile);
    }

    /**
     * This method is used to save a note to a file that the note is bound to.
     * Otherwise, the user is asked to provide a file path.
     * @param note note to save.
     * @param confirmation if the user should be asked if they want to save.
     * @return false if the saving was interrupted. True if it was successful or the user did not want to save.
     * @throws IOException Thrown when the note could not be saved.
     * @throws InvalidCryptModeException This exception is thrown when the note is encrypted by an AES object set to decryption only.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    public boolean save(Note note, boolean confirmation) throws InvalidCryptModeException, CryptException, IOException {
        if (note.hasFile()) {
            if (!confirmation || askToSave()) {
                note.overwrite();
            }
            return true;
        } else {
            return saveAs(note, confirmation);
        }
    }

    /**
     * This method is used to save a note if it is not saved to a file that will be specified during saving.
     @param note note to save.
     @param confirmation if the user should be asked if they want to save.
     * @return false if the saving was interrupted. True if it was successful or the user did not want to save.
     * @throws IOException Thrown when the note could not be saved.
     * @throws InvalidCryptModeException This exception is thrown when the note is encrypted by an AES object set to decryption only.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    public boolean saveAs(Note note, boolean confirmation) throws InvalidCryptModeException, CryptException, IOException {
        if (!confirmation || askToSave()) {
            return saveNoteAs(note);
        }

        return true;
    }

    /**
     * Private method used during saving to a new file.
     * @param note note to save.
     * @return false if the saving was interrupted. True if it was successful or the user did not want to save.
     * @throws IOException Thrown when the note could not be saved.
     * @throws InvalidCryptModeException This exception is thrown when the note is encrypted by an AES object set to decryption only.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    private boolean saveNoteAs(Note note) throws InvalidCryptModeException, CryptException, IOException {
        String saveDir, fileName;
        if (note.hasFile()) {
            Path notePath = Path.of(note.getFile().toString());
            saveDir = notePath.getParent().toString();
            fileName = notePath.getFileName().toString();
        } else {
            saveDir = System.getProperty("user.dir");
            fileName = "";
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(saveDir));
        fileChooser.setInitialFileName(fileName);
        fileChooser.setTitle("Save note");
        fileChooser.getExtensionFilters().add(noteFileFilter);

        File outputFile = fileChooser.showSaveDialog(parent.getScene().getWindow());
        if (outputFile == null) {
            return false;
        }

        Optional<String> password = askForNewPasswordLoop();
        if (password.isPresent()) {
            note.save(outputFile.getAbsolutePath(), password.get());
            return true;
        }
        return false;
    }

    /**
     * Static method used to acquire an answer from the user if they want to save the note with a dialog.
     * @return true if the user chooses to save the note, false otherwise.
     */
    private static boolean askToSave() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Save note");
        dialog.setHeaderText("Do you want to save the current note?");
        dialog.setContentText(null);

        ObservableList<ButtonType> buttons = dialog.getDialogPane().getButtonTypes();
        ButtonType confirmButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        buttons.add(confirmButton);
        buttons.add(new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE));

        Optional<ButtonType> choice = dialog.showAndWait();

        return choice.isPresent() && choice.get() == confirmButton;
    }

    /**
     * Method used to get a password to an existing note file by using a special dialog.
     * @param title dialog title.
     * @param header dialog header.
     * @return optional String value that exists if the user inputs a password. It is empty if the operation is canceled.
     */
    private Optional<String> askForPassword(String title, String header) {
        PasswordDialog dialog = new PasswordDialog(title, header);
        return dialog.showAndWait();
    }

    /**
     * Method used to get a new password for a note. The password has to be provided twice.
     * If the two passwords do not match the user is asked again in a loop.
     * @return optional String value that exists if the user inputs a password. It is empty if the operation is canceled.
     */
    private Optional<String> askForNewPasswordLoop() {
        NewPasswordDialog dialog = new NewPasswordDialog("New password needed",
                "Enter a new password for the note.", parent.getPassGenView());
        Optional<String[]> result = dialog.showAndWait();

        if (result.isPresent()) {
            while (!(result.get()[0]).equals(result.get()[1])) {
                dialog = new NewPasswordDialog("New password needed",
                        "Passwords do not match. Try again.", parent.getPassGenView());
                result = dialog.showAndWait();

                if (result.isEmpty()) {
                    return Optional.empty();
                }
            }
            return Optional.of(result.get()[0]);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Method used to delete note from disk.
     * @param file note file to remove from disk.
     * @return true if the note was successfully deleted from disk.
     */
    public boolean deleteNote(File file) {
        return (!file.exists() || file.delete());
    }
}
