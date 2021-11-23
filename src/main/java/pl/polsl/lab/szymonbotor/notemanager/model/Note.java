package pl.polsl.lab.szymonbotor.notemanager.model;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.NoteTooLongException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Class representing a text note. The contents can be encrypted and saved to a file, or a file can be decrypted and read into a Note object.
 * @author Szymon Botor
 * @version 1.1
 */
public class Note {
    
    /**
     * Constant value representing the maximum size of a note in characters.
     */
    private static final int MAX_NOTE_SIZE = 255;

    public static final String FILE_EXTENSION = ".note";

    /**
     * The content of the note.
     */
    private String content;

    /**
     * Path to the file that the note was created from or saved into even if it was not successful.
     */
    private Path fileDir;
    
    /**
     * Default constructor of the Note class.
     */
    public Note() {
        content = null;
    }
    
    /**
     * This method is used to get the content of the note.
     * @return string representing the content of the note.
     */
    public String getContent() {
        return content;
    }

    /**
     * This method is used to read an encrypted note from an input stream.
     * @param inpStream the stream to read data from.
     * @param password password used to encrypt the note.
     * @return true if the operation was successful, false otherwise.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     */
    public boolean read(InputStream inpStream, String password)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, InvalidCryptModeException {

        byte[] passHash = inpStream.readNBytes(32);
        byte[] salt = inpStream.readNBytes(8);

        Authenticator auth = new Authenticator(passHash);
        if (auth.authenticate(password)) {
            byte[] iv = inpStream.readNBytes(16);
            AES aes = new AES(password, salt, iv);

            content = aes.decrypt(inpStream.readAllBytes());

            inpStream.close();
            return content != null;
        }

        return false;
    }

    /**
     * This method is used to open an encrypted note from a file and decrypt it with the given password.
     * @param fileName directory to the encrypted note file. It should have a .note extension. If not then it will be appended.
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
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     */
    public boolean read(String fileName, String password)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, InvalidCryptModeException {

        if (!fileName.endsWith(FILE_EXTENSION)) {
            fileName = fileName + FILE_EXTENSION;
        }
        fileDir = Paths.get(fileName);

        InputStream inpStream = Files.newInputStream(fileDir);

        boolean successful = read(inpStream, password);

        inpStream.close();
        return successful;
    }

    /**
     * This method is used to save the encrypted note to an output stream using the provided password.
     * The bytes of the file are divided into segments starting from 0:<br>
     * - Bytes 0-31: password hash<br>
     * - Bytes 32-39: salt<br>
     * - Bytes 40-55: initialisation vector<br>
     * - Bytes 56-end: encrypted note content
     * @param outStream output stream to save to.
     * @param password password to use as a base in encryption. The same password must be provided during decryption to successfully decrypt the note.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     */
    public void save(OutputStream outStream, String password)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, InvalidCryptModeException {

        byte[] passHash = Authenticator.hashPassword(password);

        AES aes = new AES(password);
        byte[] cipherText = aes.encrypt(content);
        byte[] iv = aes.getIV();
        byte[] salt = aes.getSalt();

        outStream.write(passHash);
        outStream.write(salt);
        outStream.write(iv);
        outStream.write(cipherText);
    }

    /**
     * This method is used to save the encrypted note to a file using the provided password.
     * The bytes of the file are divided into segments starting from 0:<br>
     * - Bytes 0-31: password hash<br>
     * - Bytes 32-39: salt<br>
     * - Bytes 40-55: initialisation vector<br>
     * - Bytes 56-end: encrypted note content
     * @param fileName directory to the output file
     * @param password password to use as a base in encryption. The same password must be provided during decryption to successfully decrypt the note.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     */
    public void save(String fileName, String password)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, InvalidCryptModeException {

        if (!fileName.endsWith(FILE_EXTENSION)) {
            fileName = fileName + FILE_EXTENSION;
        }
        fileDir = Paths.get(fileName);

        OutputStream outStream = Files.newOutputStream(fileDir);

        save(outStream, password);

        outStream.close();
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
     * This method is used to get the last open/save directory (as String)
     * of the note that was used (even if unsuccessful).
     * @return Last directory that the note was opened from or saved to (even if unsuccessful).
     */
    public String getFileDir() {
        return fileDir.toString();
    }

    /**
     * This method is used to get the last open/save directory (as a Path object)
     * of the note that was used (even if unsuccessful).
     * @return Path object of the directory.
     */
    public Path getFilePath() {
        return fileDir;
    }
}
