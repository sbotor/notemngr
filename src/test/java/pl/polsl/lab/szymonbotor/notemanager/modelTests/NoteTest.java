package pl.polsl.lab.szymonbotor.notemanager.modelTests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.NoteTooLongException;
import pl.polsl.lab.szymonbotor.notemanager.model.AES;
import pl.polsl.lab.szymonbotor.notemanager.model.Authenticator;
import pl.polsl.lab.szymonbotor.notemanager.model.Note;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the testing class for the Note class.
 * @author Szymon Botor
 * @version 1.0
 */
class NoteTest {

    /**
     * The password used in note encryption and decryption.
     */
    private static final String password = "!paSswOrd_123";

    /**
     * Path object pointing to the existing note file.
     */
    private static final Path existingPath = Path.of("existingNote.note");

    /**
     * Path object pointing to the nonexistent note file.
     */
    private static final Path newPath = Path.of("newNote.note");

    /**
     * Text used in note encryption and decryption.
     */
    private static final String plainText = "Test string. This string is used for testing. I will now present some symbols: 123890!@#$%-()=.";

    /**
     * Cryptographic salt needed for AES encryption/decryption.
     */
    private static  byte[] salt;

    /**
     * Initialisation vector needed for AES encryption/decryption.
     */
    private static byte[] iv;

    /**
     * Hashed password needed in authorisation.
     */
    private static byte[] hash;

    /**
     * Text encrypted using AES.
     */
    private static byte[] encryptedText;

    /**
     * This method is used to set up the salt, initialisation vector, password hash and encrypted text before all tests.
     * @throws InvalidCryptModeException Thrown when an AES object created to encrypt is used to decrypt or vice versa.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     */
    @BeforeAll
    static void init() throws NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidCryptModeException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        AES aes = new AES(password);

        salt = aes.getSalt();
        iv = aes.getIV();
        hash = Authenticator.hashPassword(password);
        encryptedText = aes.encrypt(plainText);
    }

    /**
     * This method is used to prepare the files before every test.
     * @throws IOException Thrown when an error occurs during file IO.
     */
    @BeforeEach
    void prepare() throws IOException {
        Files.deleteIfExists(existingPath);
        Files.deleteIfExists(newPath);

        byte[] outArray = new byte[hash.length + salt.length + iv.length + encryptedText.length];
        System.arraycopy(hash, 0, outArray, 0, hash.length);
        System.arraycopy(salt, 0, outArray, hash.length, salt.length);
        System.arraycopy(iv, 0, outArray, hash.length + salt.length, iv.length);
        System.arraycopy(encryptedText, 0, outArray, hash.length + salt.length + iv.length, encryptedText.length);

        Files.createFile(existingPath);
        Files.write(existingPath, outArray, StandardOpenOption.APPEND);
    }

    /**
     * This test is used to check input when the file does not exist.
     * @throws InvalidCryptModeException Thrown when an AES object created to encrypt is used to decrypt or vice versa.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     */
    @Test
    void testReadWhenFileDoesNotExist() throws InvalidCryptModeException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {

        // Given

        // When
        boolean testSuccess = false;
        Note note;
        try {
            note = new Note(newPath.toString(), password);
        } catch (IOException e) {
            testSuccess = true;
        }

        // Then
        assertTrue(testSuccess);
    }

    /**
     * This test is used to check input when the file exists and the provided password is correct.
     * @throws InvalidCryptModeException Thrown when an AES object created to encrypt is used to decrypt or vice versa.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws IOException Thrown when an error occurs during file IO.
     */
    @Test
    void testReadWhenFileExistsAndPasswordCorrect() throws InvalidCryptModeException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException,
            InvalidKeyException, IOException {

        // Given

        // When
        Note note = new Note(existingPath.toString(), password);

        // Then
        assertEquals(plainText, note.getContent());
    }

    /**
     * This test is used to check input when the file exists and the provided password is incorrect.
     * @throws InvalidCryptModeException Thrown when an AES object created to encrypt is used to decrypt or vice versa.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws IOException Thrown when an error occurs during file IO.
     */
    @Test
    void testReadWhenFileExistsAndPasswordIncorrect() throws InvalidCryptModeException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException,
            InvalidKeyException, IOException {

        // Given

        // When
        Note note = new Note(existingPath.toString(), "@incorrect_password#890");

        // Then
        assertEquals(null, note.getContent());
    }

    /**
     * This method is used to test changing the note content when the new content is not higher than the maximum allowed length.
     * @param newContent new content of the note.
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "test$note1234", "test$note1234 secondWord", "test$note1234 secondWord _thirdwOrd"})
    void testChangeWhenParamsCorrect(String newContent) {
        // Given
        Note note = new Note();

        // When
        try {
            note.change(newContent);
        } catch (NoteTooLongException e) {
            e.printStackTrace();
        }

        // Then
        assertEquals(newContent, note.getContent());
    }

    /**
     * This method is used to test changing the note content when the new content is higher than the maximum allowed length.
     */
    @Test
    void testChangeWhenContentTooLong() {
        // Given
        String str = String.valueOf(new char[Note.MAX_NOTE_SIZE + 1]);
        Note note = new Note();

        // When
        boolean testSuccess = false;
        try {
            note.change(str);
            testSuccess = false;
        } catch (NoteTooLongException e) {
            testSuccess = true;
        }

        // Then
        assertTrue(testSuccess);
    }

    /**
     * This method is used to test file output when the file already exists.
     * @throws InvalidCryptModeException Thrown when an AES object created to encrypt is used to decrypt or vice versa.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws IOException Thrown when an error occurs during file IO.
     */
    @Test
    void testSaveWhenFileExists() throws InvalidCryptModeException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, IOException,
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        // Given
        Note note = new Note(existingPath.toString(), password);

        // When
        note.save(note.getFileDir(), password);

        // Then
        Note newNote = new Note(existingPath.toString(), password);

        assertEquals(plainText, newNote.getContent());
    }

    /**
     * This method is used to test file output when the file does not already exist.
     * @throws InvalidCryptModeException Thrown when an AES object created to encrypt is used to decrypt or vice versa.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws IOException Thrown when an error occurs during file IO.
     */
    @Test
    void testSaveWhenFileDoesNotExists() throws InvalidCryptModeException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, IOException,
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        // Given
        Note note = new Note(existingPath.toString(), password);

        // When
        note.save(newPath.toString(), password);

        // Then
        Note newNote = new Note(existingPath.toString(), password);

        assertEquals(plainText, newNote.getContent());
    }

    /**
     * This method is used to clean up after all the tests.
     * @throws IOException Thrown when an error occurs during file IO.
     */
    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(existingPath);
        Files.deleteIfExists(newPath);
    }
}