package pl.polsl.lab.szymonbotor.notemanager.exceptions;

/**
 * Exception class thrown when the PasswordGen class encounters an invalid character in the provided symbols.
 * @author Szymon Botor
 * @version 1.0
 */
public class InvalidCharacterException extends Exception {
    
    /**
     * Default constructor of the exception.
     */
    public InvalidCharacterException() {
        
    }
    
    /**
     * Constructor accepting a custom message.
     * @param message text to display as the exception message.
     */
    public InvalidCharacterException(String message) {
        super(message);
    }
}
