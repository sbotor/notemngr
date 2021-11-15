package pl.polsl.lab.szymonbotor.notemanager.exceptions;

/**
 * Exception class thrown when the PasswordGen class encounters an invalid password length.
 * @author Szymon Botor
 * @version 1.0
 */
public class InvalidPasswordLengthException extends Exception {
    
    /**
     * Default constructor of the exception.
     */
    public InvalidPasswordLengthException() {
        
    }
    
     /**
     * Constructor accepting a custom message.
     * @param message text to display as the exception message.
     */
    public InvalidPasswordLengthException(String message) {
        super(message);
    }
    
}
