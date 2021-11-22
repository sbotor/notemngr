package pl.polsl.lab.szymonbotor.notemanager.view;

import org.w3c.dom.ranges.Range;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.NoteTooLongException;
import java.io.Console;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import pl.polsl.lab.szymonbotor.notemanager.model.*;

/**
 * Console view class of the whole program. It is used as the main user interface during runtime.
 * @author Szymon Botor
 * @version 1.1
 */
public class ConsoleView {
    
    /**
     * A scanner object used to get input from the user.
     */
    private Scanner scanner;
    
    /**
     * Constructor of the ConsoleView class.
     */
    public ConsoleView() {
        scanner = new Scanner(System.in);
    }
    
    /**
     * A static method printing a message to the standard output stream.
     * @param msg message to print.
     */
    public static void display(String msg) {
        System.out.println(msg);
    }
    
    /**
     * Method used to generate a password with a PasswordGen object and display it on the standard output.
     * It also enables the user to generate a new password with the same parameters or end the program.
     * @param passGen PasswordGen object used to generate passwords.
     */
    public void display(PasswordGen passGen) {
        display("Generated password: ".concat(passGen.generate()));
        while (true) {
            display("\"r\" - regenerate\n\"e\" - exit");
            String choice = scanner.nextLine().strip().toLowerCase();
            
            switch (choice) {
                case "r":
                case "regenerate":
                    display("\nGenerated password: ".concat(passGen.generate()));
                    break;
                case "e":
                case "exit":
                case "q":
                case "quit":
                case "end":
                    return;
                default:
                    display("\nUrecognised command.");
                    break;
            }
        }
    }
    
    /**
     * Method used to display contents of a note passed as a parameter.
     * @param note decrypted note to display.
     */
    public boolean display(Note note) {
        if (note == null) {
            display("Error: the note does not exist.");
            return false;
        }
        
        display("Note content:");
        display(note.getContent());

        return getBinaryAnswer("Do you want to edit the note? Y/n");
    }

    /**
     * This method is used to get a yes/no answer from user using the provided message.
     * @param msg message to show to the user.
     * @return true if the answer was "yes", false if it was "no".
     */
    private boolean getBinaryAnswer(String msg) {
        while (true) {
            display(msg);
            String choice = scanner.nextLine().strip().toLowerCase();

            switch (choice) {
                case "y":
                case "yes":
                    return true;
                case "n":
                case "no":
                    return false;
                default:
                    display("Unrecognised command.\n");
            }
        }
    }

    private String fetchFileDirWithHistory(NoteHistory history) {

        display("Previously used notes:");
        Map<Integer, String> noteMap = IntStream.range(0, history.getNotes().size())
                .boxed()
                .collect(Collectors.toMap(i -> i + 1, i -> history.getNotes().get(i)));

        noteMap.forEach((k, v) -> {
            String filename = Paths.get(v).getFileName().toString();
            filename = filename.substring(0, filename.lastIndexOf(".note"));
            System.out.println(k + ". " + filename + " (" + v + ")");
        });

        display("Select file number or filename.");
        String choice = scanner.nextLine();
        try {
            int noteNum = Integer.parseInt(choice);

            while (noteNum > history.getNotes().size() || noteNum < 1) {
                display("Invalid note number. Try again.");
                choice = scanner.nextLine();
                noteNum = Integer.parseInt(choice);
            }
            return noteMap.get(noteNum);
        } catch (NumberFormatException ex) {
            return choice;
        }
    }
    
    /**
     * Method used to get a directory of a note from the user using standard i/o.
     * @return provided file directory.
     */
    public String fetchFileDir(NoteHistory history) {
        if (history != null && history.getNotes().size() != 0) {
            return fetchFileDirWithHistory(history);
        }

        display("Choose file:");
        return scanner.nextLine().strip();
    }
    
    /**
     * Method used to get the length of a password to generate using standard i/o from the user.
     * @return provided length in string format.
     */
    public String fetchPasswordLength() {
        display("Number of characters in the password:");
        
        return scanner.nextLine().strip();
    }
    
    /**
     * Method used to get additional password symbols to include in generation from the user using standard i/o.
     * @return provided symbols.
     */
    public String fetchPasswordSymbols() {
        display("Symbols to include in addition to lowercase letters (without spaces):");
        display("\"u\" - uppercase letters\n\"d\" - digits\nOther symbols (\"o\" to include all): !@#$%^&*-_+,.?");
        
        return scanner.nextLine().strip();
    }
    
    /**
     * Method used to get all arguments from the user using standard i/o if none were provided via the command line.
     * @return array of provided arguments.
     */
    public String[] fetchArgs(NoteHistory history) {
        
        display("Insufficient argument count. Choose what to do:");
        String choice = "";
        
        while(true) {
            display("\"-o\" - Open note\n\"-c\" - Create note\n\"-g\" - Generate password");
            choice = scanner.nextLine().strip().toLowerCase();

            String[] args;
            switch (choice) {
                case "-c":
                case "c":
                    args = new String[1];
                    args[0] = "-c";
                    return args;
                case "-o":
                case "o":
                    args = new String[2];
                    args[0] = "-o";
                    args[1] = fetchFileDir(history);
                    return args;
                case "-g":
                case "g":
                    args = new String[3];
                    args[0] = "-g";
                    while (true) {
                        args[1] = fetchPasswordLength();
                        try {
                            if (Integer.parseInt(args[1]) > 0) {
                                break;
                            } else {
                                display("Character count too low.");
                            }
                        }
                        catch (NumberFormatException ex) {
                            display("Invalid character count.");
                        }
                    }
                    args[2] = fetchPasswordSymbols();
                    return args;
                default:
                    display("\nUnrecognised command. Try again.");
            }
        }
    }
    
    /**
     * Method used to read a password from the user. If the Console object needed for hiding the password
     * cannot be created it uses the scanner object and the password is visible. This can happen if running in
     * the integrated terminal.
     * @return provided password as a character array.
     */
    private char[] readPassword() {
        
        Console console = System.console();
        
        if (console != null) {
            return console.readPassword();
        }
        return scanner.nextLine().toCharArray();
    }
    
    /**
     * Method used to open a note from a file. It asks the user to provide a password and tries to authenticate
     * based on the opened file. After three unsuccessful tries the method exits.
     * @param filename directory to the note file.
     * @return Note object if the opening and authentication was successful, otherwise it returns null.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     */
    public Note openNote(String filename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        
        Note note = new Note();  
        int tryCount = 0;
        while (tryCount < 3) {
            display("Password:");
            char[] password = readPassword();
            
            if (note.read(filename, new String(password))) {
                return note;
            }
            
            display("Invalid password. Try again.\n");
            tryCount++;
        }
        
        display("Too many tries. The program will now exit.");
        return null;
    }
    
    /**
     * Method used during note edit. It changes the content of the note and asks the user if they want to save the note.
     * @param note Note object to edit the contents of.
     * @return true if the user agreed to save the note, otherwise false.
     */
    public boolean editNote(Note note) {
        while (true) {
            display("Write a new note. Press Enter to end.\n");
            try {
                note.change(scanner.nextLine());
                break;
            }
            catch (NoteTooLongException ex) {
                display(ex.getMessage());
            }
        }
        
        return getBinaryAnswer("Do you want to save the note? Y/n");
    }
    
    /**
     * Method used to encrypt a note and save it to a file. It asks the user for a file directory
     * and a new password for encryption.
     * @param note Note object to save the contents of.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     */
    public void saveNote(Note note)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        
        display("Output directory:");
        String outDir = scanner.nextLine().strip();
        
        while (true) {
            display("Set a password:");
            char[] password = readPassword();
            display("Repeat password:");
            char[] rPassword = readPassword();
            
            if (Arrays.equals(password, rPassword)) {
                note.save(outDir, new String(password));
                display("Saved successfully.");
                return;
            }
            
            display("Passwords do not match. Try again.\n");
        }
    }

    /**
     * Method used to encrypt a note and save it to a predetermined file. It asks the user for a
     * new password for encryption.
     * @param note Note object to save the contents of.
     * @param outDir directory to save the note to.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     */
    public void saveNote(Note note, String outDir)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {

        while (true) {
            display("Set a password:");
            char[] password = readPassword();
            display("Repeat password:");
            char[] rPassword = readPassword();

            if (Arrays.equals(password, rPassword)) {
                note.save(outDir, new String(password));
                display("Saved successfully.");
                return;
            }

            display("Passwords do not match. Try again.\n");
        }
    }
}
