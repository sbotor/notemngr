package pl.polsl.lab.szymonbotor.notemanager.model;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.NoteTooLongException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Class representing a text note. The contents can be encrypted and saved to a file, or a file can be decrypted and read into a Note object.
 * @author Szymon Botor
 * @version 1.0
 */
public class Note {
    
    /**
     * Constant value representing the maximum size of a note in characters.
     */
    private static final int MAX_NOTE_SIZE = 255;
    
    /**
     * The content of the note.
     */
    private String content;
    
    /**
     * Buffer used during file input/output.
     */
    private byte[] fileBuffer;
    
    /**
     * Hash of the password used during note encryption/decryption obtained with SHA-256.
     */
    private byte[] passHash;
    
    /**
     * Cryptographic salt used during note encryption/decryption.
     */
    private byte[] salt;
    
    /**
     * Initialisation vector used during note encryption/decryption.
     */
    private byte[] iv;

    /**
     * Name of the file that the note was created from or saved into even if it was not successful.
     */
    private String fileDir;
    
    /**
     * Default constructor of the Note class.
     */
    public Note() {
        content = null;
        fileBuffer = null;
        passHash = null;
        salt = null;
        iv = null;
    }
    
    /**
     * This method is used to get the content of the note.
     * @return string representing the content of the note.
     */
    public String getContent() {
        return content;
    }
    
    /**
     * This method is used to open an encrypted note from a file and decrypt it with the given password.
     * @param file directory to the encrypted note file.
     * @param password password used to encrypt the note needed for decryption.
     * @return true if the authentication and decryption succeeded. False otherwise.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     */
    
    public boolean open(String file, String password)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        
        fileDir = file;

        fileBuffer = Files.readAllBytes(Paths.get(file));
        passHash = Arrays.copyOfRange(fileBuffer, 0, 32);
        salt = Arrays.copyOfRange(fileBuffer, 32, 40);

        Authenticator auth = new Authenticator(passHash);
        if (auth.authenticate(password)) {
            iv = Arrays.copyOfRange(fileBuffer, 40, 56);
            AES aes = new AES(password, salt, iv);
            
            content = aes.decrypt(Arrays.copyOfRange(fileBuffer, 56, fileBuffer.length));
            return content != null;
        }
        
        return false;
    }
    
    /**
     * This method is used to save the encrypted note to a file using the provided password.
     * The bytes of the file are divided into segments starting from 0:<br>
     * - Bytes 0-31: password hash<br>
     * - Bytes 32-39: salt<br>
     * - Bytes 40-55: initialisation vector<br>
     * - Bytes 56-end: encrypted note content
     * @param file directory to the output file
     * @param password password to use as a base in encryption. The same password must be provided during decryption to successfully decrypt the note.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     */
    public void save(String file, String password)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        
        fileDir = file;

        passHash = Authenticator.hashPassword(password);
        
        AES aes = new AES(password);
        byte[] cipherText = aes.encrypt(content);
        iv = aes.getIV();
        salt = aes.getSalt();
        
        fileBuffer = new byte[56 + cipherText.length];
        System.arraycopy(passHash, 0, fileBuffer, 0, 32);
        System.arraycopy(salt, 0, fileBuffer, 32, 8);
        System.arraycopy(iv, 0, fileBuffer, 40, 16);
        System.arraycopy(cipherText, 0, fileBuffer, 56, cipherText.length);
        
        Files.write(Paths.get(file.concat(".note")), fileBuffer);
    }
    
    /**
     * This method is used to change the content of the note. It performs a check on the length of the new note, throwing an exception if it is too long.
     * @param str new note content.
     * @throws NoteTooLongException If the new content is longer than MAX_NOTE_SIZE.
     */
    public void change(String str) throws NoteTooLongException {
        if (str.length() > MAX_NOTE_SIZE) {
            throw new NoteTooLongException(String.format("Note size of %d is too long. The limit is %d.",
                    str.length(), MAX_NOTE_SIZE));
        }
        
        content = str;
    }

    /**
     * This method is used to get the last open/save directory of the note that was used (even if unsuccessful).
     * @return Last directory that the note was opened from or saved to (even if unsuccessful).
     */
    public String getFileDir() {
        return fileDir;
    }
}
