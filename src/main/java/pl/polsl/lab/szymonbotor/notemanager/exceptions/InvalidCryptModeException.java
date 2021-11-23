package pl.polsl.lab.szymonbotor.notemanager.exceptions;

/**
 * Exception class thrown when a decryption method on an encryption AES object is used or vice versa.
 * @author Szymon Botor
 * @version 1.0
 */
public class InvalidCryptModeException extends Exception {
    /**
     * Default constructor of the exception.
     */
    public InvalidCryptModeException() {

    }

    /**
     * Constructor accepting a custom message.
     * @param message text to display as the exception message.
     */
    public InvalidCryptModeException(String message) {
        super(message);
    }
}
