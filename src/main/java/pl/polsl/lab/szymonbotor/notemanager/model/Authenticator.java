package pl.polsl.lab.szymonbotor.notemanager.model;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;

import java.util.Arrays;

/**
 * Class used during password authentication process. It implements the SHA-256 algorithm.
 * @author Szymon Botor
 * @version 1.1
 */
public class Authenticator extends Hash {
    
    /**
     * Constructor used to create an instance of the Authenticator class with a provided hash value.
     * @param passwordHash the original password hash for comparison.
     */
    public Authenticator(byte[] passwordHash) {
        super(passwordHash);
    }
    
    /**
     * Authentication method. It compares the provided password with the hashed original.
     * @param password input password to be hashed and compared against the original.
     * @return true if the hashes are equal. False otherwise.
     * @throws CryptException This exception is thrown when an error occurs during hashing.
     */
    public boolean authenticate(String password) throws CryptException {
        return Arrays.equals(this.hash, hashText(password));
    }
}
