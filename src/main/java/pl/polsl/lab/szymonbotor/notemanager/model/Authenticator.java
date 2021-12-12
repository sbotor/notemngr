package pl.polsl.lab.szymonbotor.notemanager.model;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Class used during password authentication process. It implements the SHA-256 algorithm.
 * @author Szymon Botor
 * @version 1.1
 */
public class Authenticator {
    /**
     * Hash of the original password obtained through passing it through SHA-256.
     */
    private byte[] hash;
    
    /**
     * Constructor used to create an instance of the Authenticator class with a provided hash value.
     * @param passwordHash the original password hash for comparison.
     */
    public Authenticator(byte[] passwordHash) {
        hash = passwordHash;
    }
    
    /**
     * Authentication method. It compares the provided password with the hashed original.
     * @param password input password to be hashed and compared against the original.
     * @return true if the hashes are equal. False otherwise.
     * @throws CryptException This exception is thrown when an error occurs during hashing.
     */
    public boolean authenticate(String password) throws CryptException {
        return Arrays.equals(hash, hashPassword(password));
    }
    
    /**
     * A static method used for hashing a provided password by passing it through SHA-256.
     * @param password password to be hashed.
     * @return array of bytes representing the hash of the password.
     * @throws CryptException This exception is thrown when an error occurs during hashing.
     */
    public static byte[] hashPassword(String password) throws CryptException {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new CryptException(e.getMessage());
        }
    }
}
