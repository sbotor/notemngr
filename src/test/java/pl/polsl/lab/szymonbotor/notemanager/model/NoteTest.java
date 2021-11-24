package pl.polsl.lab.szymonbotor.notemanager.model;

import org.junit.jupiter.api.*;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

    private static final String password = "!paSswOrd_123";

    private static final Path existingPath = Path.of("existingNote.note");

    private static final Path newPath = Path.of("newNote.note");

    private static final String plainText = "Test string. This string is used for testing. I will now present some symbols: 123890!@#$%-()=.";

    private static  byte[] salt;

    private static byte[] iv;

    private static byte[] hash;

    private static byte[] encryptedText;

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

    @Test
    void testReadWhenDoesNotExist() throws InvalidCryptModeException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {

        // Given
        Note note = new Note();

        // When
        boolean testSuccess = false;
        try {
            testSuccess = note.read(newPath.toString(), password);
        } catch (IOException e) {
            testSuccess = true;
        }

        // Then
        assertTrue(testSuccess);
    }

    @Test
    void testReadWhenExistsAndPasswordCorrect() throws InvalidCryptModeException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {

        // Given
        Note note = new Note();

        // When
        boolean testSuccess = false;
        try {
           testSuccess = note.read(existingPath.toString(), password);
        } catch (IOException e) {
            testSuccess = false;
        }

        // Then
        assertTrue(testSuccess);
    }

    @Test
    void testReadWhenExistsAndPasswordIncorrect() throws InvalidCryptModeException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {

        // Given
        Note note = new Note();

        // When
        boolean testSuccess = false;
        try {
            testSuccess = !note.read(existingPath.toString(), "@incorrect_password$890");
        } catch (IOException e) {
            testSuccess = false;
        }

        // Then
        assertTrue(testSuccess);
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(existingPath);
        Files.deleteIfExists(newPath);
    }
}