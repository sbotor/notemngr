package pl.polsl.lab.szymonbotor.notemanager.controller;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidPasswordLengthException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCharacterException;

import java.io.IOException;
import java.nio.file.InvalidPathException;

import pl.polsl.lab.szymonbotor.notemanager.view.ConsoleView;
import pl.polsl.lab.szymonbotor.notemanager.model.*;

/**
 * The main controller class of the whole program in the console.
 * @author Szymon Botor
 * @version 1.2
 */
public class ConsoleController {

    /**
     * Directory to the history file.
     */
    private static final String HISTORY_DIR = "history.txt";

    /**
     * This is the ConsoleView object of the controller.
     */
    private static final ConsoleView view = new ConsoleView();

    /**
     * This is the NoteHistory object of the controller. It is used to store recent notes, if successfully read from a file.
     */
    private static NoteHistory noteHistory = null;

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
            case "-o":
                openNote(args);
                break;
            // Create a new note.
            case "-c":
                createNote(args);
                break;
            // Generate a password.
            case "-g":
                generatePassword(args);
                break;
            default:
                view.display("Unrecognised parameters. Try again.");
        }
    }

    /**
     * This is a static method used to open a note and then edit it and save
     * based on user input.
     * @param args arguments such as command line parameters from main().
     * @see ConsoleController#main(String[])
     */
    private static void openNote(String[] args) {
        if (args.length < 2) {
            args = new String[] {"-o", view.fetchFileDir(noteHistory) };
        }

        try {
            Note note = null;
            try {
                note = view.openNote(args[1]);
            }
            catch (InvalidCryptModeException ex) {
                view.display(ex.getMessage());
            }

            if (note != null) {
                try {
                    noteHistory.add(note);
                    if (view.display(note)) {
                        view.editNote(note);
                        view.saveNote(note, note.getFile().toString());
                    }
                    noteHistory.save();
                }
                catch (IOException ex) {
                    view.display("Warning: cannot save or create the note history file.\n");
                }
                catch (IllegalArgumentException ex) {
                    view.display("The note file directory is empty.");
                }
                catch (InvalidCryptModeException ex) {
                    view.display(ex.getMessage());
                }
            }
        }
        catch (IOException | InvalidPathException ex) {
            view.display("Cannot open file \"" + args[1] + "\".");
        } catch (CryptException e) {
            view.display("Error during decryption. " + e.getMessage());
        }
    }

    /**
     * This is a static method used to create a new note and save it
     * based on user input.
     * @param args arguments such as command line parameters from main().
     * @see ConsoleController#main(String[])
     */
    private static void createNote(String[] args) {
        Note note = new Note();
        try {
            if (view.editNote(note)) {
                try {
                    view.saveNote(note);
                }
                catch (IOException ex) {
                    throw ex;
                }
                catch (InvalidCryptModeException ex) {
                    view.display(ex.getMessage());
                } catch (CryptException e) {
                    view.display("Error during encryption. " + e.getMessage());
                }

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
    }

    /**
     * This is a static method used to generate a new password.
     * @param args arguments such as command line parameters from main().
     * @see ConsoleController#main(String[])
     */
    private static void generatePassword(String[] args) {
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
    }
}
