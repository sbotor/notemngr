package pl.polsl.lab.szymonbotor.notemanager.model;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.NoteTooLongException;

import java.io.*;
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
    public static final int MAX_NOTE_SIZE = 255;

    public static final String FILE_EXTENSION = ".note";

    /**
     * The content of the note.
     */
    private String content;

    /**
     * This method is used to set the file directory of the note using the specified String.
     * @param fileDir String with the desired directory.
     */
    public void setFileDir(String fileDir) {
        this.fileDir = Path.of(fileDir);
    }

    /**
     * This method is used to set the file directory of the note using the specified Path object.
     * @param fileDir Path with the desired directory.
     */
    public void setFileDir(Path fileDir) {
        this.fileDir = fileDir;
    }

    /**
     * Path to the file that the note was created from or saved into.
     */
    private Path fileDir;
    
    /**
     * Default constructor of the Note class.
     */
    public Note() {
        content = null;
        fileDir = null;
    }

    /**
     * Constructor creating a note by opening an encrypted note from a file and decrypting it with the given password.
     * @param fileName directory to the encrypted note file. It should have a .note extension. If not then it will be appended.
     * @param password password used to encrypt the note needed for decryption.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    public Note(String fileName, String password)
            throws IOException, InvalidCryptModeException, CryptException {

        read(fileName, password);
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
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    private boolean read(FileInputStream inpStream, String password)
            throws IOException, InvalidCryptModeException, CryptException {

        byte[] passHash = inpStream.readNBytes(32);
        byte[] salt = inpStream.readNBytes(8);

        Authenticator auth = new Authenticator(passHash);
        if (auth.authenticate(password)) {
            byte[] iv = inpStream.readNBytes(16);
            AES aes = new AES(password, salt, iv);

            content = aes.decrypt(inpStream.readAllBytes());

            inpStream.close();
            return true;
        }

        return false;
    }

    /**
     * This method is used to open an encrypted note from a file and decrypt it with the given password.
     * @param fileName directory to the encrypted note file. It should have a .note extension. If not then it will be appended.
     * @param password password used to encrypt the note needed for decryption.
     * @return true if the authentication and decryption succeeded. False otherwise.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    public boolean read(String fileName, String password)
            throws IOException, InvalidCryptModeException, CryptException {

        if (!fileName.endsWith(FILE_EXTENSION)) {
            fileName = fileName + FILE_EXTENSION;
        }
        Path newFileDir = Paths.get(fileName);

        FileInputStream inpStream = new FileInputStream(newFileDir.toString());
        boolean successful = false;

        successful = read(inpStream, password);
        inpStream.close();

        if (successful) {
            fileDir = newFileDir;
        }

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
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    private void save(FileOutputStream outStream, String password)
            throws IOException, InvalidCryptModeException, CryptException {

        byte[] passHash = new byte[0];
        passHash = Authenticator.hashPassword(password);
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
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    public void save(String fileName, String password)
            throws IOException, InvalidCryptModeException, CryptException {

        if (!fileName.endsWith(FILE_EXTENSION)) {
            fileName = fileName + FILE_EXTENSION;
        }
        Path newFileDir = Paths.get(fileName);

        FileOutputStream outStream = new FileOutputStream(newFileDir.toString());
        save(outStream, password);
        outStream.close();

        fileDir = newFileDir;
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
