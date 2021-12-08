package pl.polsl.lab.szymonbotor.notemanager.exceptions;

/**
 * Exception class thrown when a cryptographic exception occurs.
 * @author Szymon Botor
 * @version 1.0
 */
public class CryptException extends Exception {

    /**
     * Default constructor of the exception.
     */
    public CryptException() {

    }

    /**
     * Constructor accepting a custom message.
     * @param message text to display as the exception message.
     */
    public CryptException(String message) {
        super(message);
    }
}
