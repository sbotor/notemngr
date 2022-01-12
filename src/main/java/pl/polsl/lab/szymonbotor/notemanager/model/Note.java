package pl.polsl.lab.szymonbotor.notemanager.model;

import pl.polsl.lab.szymonbotor.notemanager.enums.CryptMode;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.NoteTooLongException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class representing a text note. The contents can be encrypted and saved to a file, or a file can be decrypted and read into a Note object.
 * @author Szymon Botor
 * @version 2.2
 */
public class Note {
    
    /**
     * Constant value representing the maximum size of a note in characters.
     */
    public static final int MAX_NOTE_SIZE = 1000;

    /**
     * This is the file extension of the note files.
     */
    public static final String FILE_EXTENSION = ".note";

    /**
     * This is the total header length of the note files.
     */
    public static final int HEADER_LENGTH = 32 + AES.IV_LENGTH + AES.SALT_LENGTH;

    /**
     * The content of the note.
     */
    private String content;

    /**
     * The file that the note was created from or saved into.
     */
    private File file;

    /**
     * This is the hashed password used during encryption and decryption.
     */
    private byte[] passHash;

    /**
     * this is the AES object used during encryption and decryption of the note.
     */
    private AES aes;

    /**
     * This value is used to determine if the note was saved since the last change.
     */
    private boolean saved;
    
    /**
     * Default constructor of the Note class.
     */
    public Note() {
        content = "";
        file = null;
        saved = true;
        aes = null;
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
        this();
        if (!read(fileName, password)) {
            content = null;
        }
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
     * @return true if the operation was successful and the user was authenticated, false otherwise.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    private boolean read(FileInputStream inpStream, String password)
            throws IOException, InvalidCryptModeException, CryptException {

        byte[] newPassHash = inpStream.readNBytes(32);
        byte[] salt = inpStream.readNBytes(AES.SALT_LENGTH);

        Authenticator auth = new Authenticator(newPassHash);
        if (auth.authenticate(password)) {
            byte[] iv = inpStream.readNBytes(AES.IV_LENGTH);

            AES newAes = new AES(password, salt, iv, CryptMode.BOTH);
            String newContent = newAes.decrypt(inpStream.readAllBytes());

            passHash = newPassHash;
            content = newContent;
            aes = newAes;
            return true;
        }

        return false;
    }

    /**
     * This method is used to open an encrypted note from a file and decrypt it with the given password.
     * @param filename directory to the encrypted note file. It should have a .note extension. If not then it will be appended.
     * @param password password used to encrypt the note needed for decryption.
     * @return true if the authentication and decryption succeeded. False otherwise.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    public boolean read(String filename, String password)
            throws IOException, InvalidCryptModeException, CryptException {

        if (!filename.endsWith(FILE_EXTENSION)) {
            filename = filename + FILE_EXTENSION;
        }
        File newFile = new File(filename);

        FileInputStream inpStream = new FileInputStream(newFile);
        boolean successful = read(inpStream, password);
        inpStream.close();

        if (successful) {
            file = newFile;
            saved = true;
        }

        return successful;
    }

    /**
     * This method is used to save the encrypted note to a file using the provided password.
     * The bytes of the file are divided into segments starting from 0:<br>
     * - Bytes 0-31: password hash<br>
     * - Bytes 32-39: salt<br>
     * - Bytes 40-55: initialisation vector<br>
     * - Bytes 56-end: encrypted note content
     * @param filename directory to the output file
     * @param password password to use as a base in encryption. The same password must be provided during decryption to successfully decrypt the note.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    public void save(String filename, String password)
            throws IOException, InvalidCryptModeException, CryptException {

        if (!filename.endsWith(FILE_EXTENSION)) {
            filename = filename + FILE_EXTENSION;
        }
        File newFile = new File(filename);

        byte[] newPassHash = Authenticator.hashPassword(password);
        aes = new AES(password, CryptMode.BOTH);
        byte[] salt = aes.getSalt();
        byte[] iv = aes.getIV();
        byte[] cipherText = aes.encrypt(content);

        byte[] buffer = createBuffer(newPassHash, salt, iv, cipherText);
        save(newFile, buffer);

        passHash = newPassHash;
        file = newFile;
        saved = true;
    }

    /**
     * This is the private method used to save a note to a file. The file is truncated.
     * @param outFile output note file.
     * @param buffer data to write to the file.
     * @throws IOException This exception is thrown when the output stream could not be opened or another IO error occurs.
     */
    private void save(File outFile, byte[] buffer) throws IOException {
        FileOutputStream outStream = new FileOutputStream(outFile);
        if (outStream.getChannel().isOpen()) {
            outStream.write(buffer);
            outStream.close();
        } else {
            outStream.close();
            throw new IOException("Cannot save the note.");
        }
    }

    /**
     * This method is used to create the output buffer with the specified data.
     * @param hash hashed password of the note.
     * @param salt cryptographic salt used to encrypt the note.
     * @param iv initialization vector of the encryption.
     * @param cipherText encrypted text to save.
     * @return created buffer ready for file output.
     */
    private byte[] createBuffer(byte[] hash, byte[] salt, byte[] iv, byte[] cipherText) {
        byte[] buffer = new byte[HEADER_LENGTH + cipherText.length];
        int bufferPos = 0;

        System.arraycopy(hash, 0, buffer, bufferPos, hash.length);
        bufferPos += hash.length;
        System.arraycopy(salt, 0, buffer, bufferPos, salt.length);
        bufferPos += salt.length;
        System.arraycopy(iv, 0, buffer, bufferPos, iv.length);
        bufferPos += iv.length;
        System.arraycopy(cipherText, 0, buffer, bufferPos, cipherText.length);

        return buffer;
    }

    /**
     * This method is used to overwrite an opened note.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * @throws InvalidCryptModeException This exception is thrown when a decryption method on an encryption AES object is used or vice versa.
     * @throws CryptException This exception is thrown when a cryptographic exception occurs.
     */
    public void overwrite() throws IOException, InvalidCryptModeException, CryptException {
        FileOutputStream outStream = new FileOutputStream(file);

        byte[] cipherText = aes.encrypt(content);

        outStream.write(passHash);
        outStream.write(aes.getSalt());
        outStream.write(aes.getIV());
        outStream.write(cipherText);

        outStream.close();
        saved = true;
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
        saved = false;
    }

    /**
     * Sets the note File to the specified object.
     * @param file File object to be set as the note File.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * This method is used to get the last open/save File of the note that was used.
     * @return Last File that the note was opened from or saved to.
     */
    public File getFile() {
        return file;
    }

    /**
     * This method used to check if the note has been saved.
     * @return true if the note has been saved since the last change.
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * This method is used to determine if the note has a File.
     * @return true if the note File is not null. False otherwise.
     */
    public boolean hasFile() {
        return file != null;
    }

    /**
     * Used to get the note filename without the file extension.
     * @return note filename or null if the note does not have a file.
     */
    public String getName() {
        
        if (!hasFile()) {
            return null;
        }

        return getFile().getName().split("\\.")[0];
    }
}
