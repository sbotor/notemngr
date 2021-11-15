package pl.polsl.lab.szymonbotor.notemanager.controller;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidPasswordLengthException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCharacterException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import pl.polsl.lab.szymonbotor.notemanager.view.ConsoleView;
import pl.polsl.lab.szymonbotor.notemanager.model.*;

/**
 * The main controller class of the whole program in the console.
 * @author Szymon Botor
 * @version 1.0
 */
public class Controller {
    
    /**
     * The default constructor for the controller class.
     */
    public Controller() {
        
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
        
        if (args.length < 1) {
            args = view.fetchArgs();
        }
        
        switch (args[0]) {
            // Open an encrypted note.
            case "-o":
            case "-O": {
                if (args.length < 2) {
                    args = new String[] {"-o", view.fetchFileDir() };
                }
                try {
                    Note note = view.openNote(args[1]);
                    if (note == null) {
                        return;
                    } else {
                        view.display(note);
                    }
                }
                catch (IOException | InvalidPathException ex) {
                    view.display("Cannot open file \"".concat(args[1]).concat("\"."));
                }
                catch (NoSuchAlgorithmException |
                        InvalidKeySpecException |
                        NoSuchPaddingException |
                        InvalidKeyException |
                        InvalidAlgorithmParameterException |
                        IllegalBlockSizeException |
                        BadPaddingException ex) {
                    view.display("Error during decryption. ".concat(ex.getMessage()));
                }
                break;
            }
            // Create a new note.
            case "-c":
            case "-C": {
                Note note = new Note();
                try {
                    if (view.editNote(note)) {
                        view.saveNote(note);
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
                    view.display("Error during encryption. ".concat(ex.getMessage()));
                }
                break;
            }
            // Generate a password.
            case "-g":
            case "-G":
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
