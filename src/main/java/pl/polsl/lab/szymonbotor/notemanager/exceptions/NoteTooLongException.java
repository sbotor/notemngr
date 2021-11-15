package pl.polsl.lab.szymonbotor.notemanager.exceptions;

/**
 * Exception class thrown when the new content of a note is too long.
 * @author Szymon Botor
 * @version 1.0
 */
public class NoteTooLongException extends Exception {
    
    /**
     * Default constructor of the exception.
     */
    public NoteTooLongException() {
        
    }
    
    /**
     * Constructor accepting a custom message.
     * @param message text to display as the exception message.
     */
    public NoteTooLongException(String message) {
        super(message);
    }
}
