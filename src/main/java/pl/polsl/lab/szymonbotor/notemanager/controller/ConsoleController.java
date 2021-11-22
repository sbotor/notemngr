package pl.polsl.lab.szymonbotor.notemanager.controller;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidPasswordLengthException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCharacterException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Locale;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import pl.polsl.lab.szymonbotor.notemanager.view.ConsoleView;
import pl.polsl.lab.szymonbotor.notemanager.model.*;

/**
 * The main controller class of the whole program in the console.
 * @author Szymon Botor
 * @version 1.1
 */
public class ConsoleController {

    /**
     * Directory to the history file.
     */
    private static final String HISTORY_DIR = "history.txt";

    /**
     * The default constructor for the controller class.
     */
    public ConsoleController() {

    }

    /**
     * The main method of the controller. The arguments can be provided by the command line, otherwise the user is asked to input them in the console.<br>
     * Three modes of operation can be specified by using appropriate switches and arguments:<br>
     * - Open encrypted note: -o directory<br>
     * - Create a note: -c<br>
     * - Generate a password: -g length [symbols]<br>
     * The parameters will be fetched via console if a required argument is not provided. Any additional parameters are ignored.<br>
     * The symbols should be provided without spaces in any order. If none are present only lowercase letters are used for generation. Available symbols:<br>
     * - Digits: d<br>
     * - Uppercase letters: u<br>
     * - Any combination of the following symbols (o to use all): !@#$%^&amp;*-_+,.?
     * @param args command line parameters.
     */
    public static void main(String[] args) {

        ConsoleView view = new ConsoleView();
        NoteHistory noteHistory = null;

        try {
            noteHistory = new NoteHistory(HISTORY_DIR);
        }
        catch (IOException ex) {
            view.display("Warning: cannot open or create the note history file.\n");
        }

        if (args.length < 1) {
            args = view.fetchArgs(noteHistory);
        }

        switch (args[0].toLowerCase()) {
            // Open an encrypted note.
            case "-o": {
                if (args.length < 2) {
                    args = new String[] {"-o", view.fetchFileDir(noteHistory) };
                }
                try {
                    Note note = view.openNote(args[1]);
                    if (note != null) {
                        try {
                            noteHistory.add(note);
                            if (view.display(note)) {
                                view.editNote(note);
                                view.saveNote(note, note.getFileDir());
                            }
                            noteHistory.save();
                        }
                        catch (IOException ex) {
                            view.display("Warning: cannot save or create the note history file.\n");
                        }
                        catch (IllegalArgumentException ex) {
                            view.display("The note file directory is empty.");
                        }
                    }
                }
                catch (IOException | InvalidPathException ex) {
                    view.display("Cannot open file \"" + args[1] + "\".");
                }
                catch (NoSuchAlgorithmException |
                        InvalidKeySpecException |
                        NoSuchPaddingException |
                        InvalidKeyException |
                        InvalidAlgorithmParameterException |
                        IllegalBlockSizeException |
                        BadPaddingException ex) {
                    view.display("Error during decryption. " + ex.getMessage());
                }
                break;
            }
            // Create a new note.
            case "-c": {
                Note note = new Note();
                try {
                    if (view.editNote(note)) {
                        view.saveNote(note);
                        try {
                            noteHistory.add(note);
                            noteHistory.save();
                        }
                        catch (IOException ex) {
                            view.display("Warning: cannot save or create the note history file.\n");
                        }
                        catch (IllegalArgumentException ex) {
                            view.display("The note file directory is empty.");
                        }
                    }
                }
                catch (IOException | InvalidPathException ex) {
                    view.display("Cannot write to the output file.");
                }
                catch (NoSuchAlgorithmException |
                        InvalidKeySpecException |
                        NoSuchPaddingException |
                        InvalidKeyException |
                        InvalidAlgorithmParameterException |
                        IllegalBlockSizeException |
                        BadPaddingException ex) {
                    view.display("Error during encryption. " + ex.getMessage());
                }
                break;
            }
            // Generate a password.
            case "-g":
                try {
                    if (args.length < 2) {
                        args = new String[] {
                            "-g",
                            view.fetchPasswordLength(),
                            view.fetchPasswordSymbols()
                        };
                    }
                    
                    PasswordGen passGen = new PasswordGen(Integer.parseInt(args[1]), args[2]);
                    view.display(passGen);
                }
                catch (NumberFormatException ex) {
                    view.display("Invalid character count format during password generation.");
                }
                catch (InvalidPasswordLengthException |
                        InvalidCharacterException ex) {
                    view.display(ex.getMessage());
                }
                break;
            default:
                view.display("Unrecognised parameters. Try again.");
                break;
        }
    }
}
