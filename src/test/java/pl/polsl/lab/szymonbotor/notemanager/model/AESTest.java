package pl.polsl.lab.szymonbotor.notemanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.polsl.lab.szymonbotor.notemanager.enums.CryptMode;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the testing class for the AES class.
 * @author Szymon Botor
 * @version 1.0
 */
class AESTest {

    /**
     * This is the password used in testing.
     */
    private String rawPassword;
    /**
     * This is the text that is encrypted during testing.
     */
    private String plainText;

    /**
     * This method is used to prepare the password and text before every test.
     */
    @BeforeEach
    void prepare() {
        rawPassword = "!paSswOrd_123";
        plainText = "Test string. This string is used for testing. I will now present some symbols: 123890!@#$%-()=.";
    }

    /**
     * This method tests encryption and decryption while using the correct password.
     */
    @Test
    void testEncryptionAndDecryptionWhenCorrect() {
        // Given

        // When
        String decryptedText = null;
        try {
            AES aes = new AES(rawPassword);
            byte[] cipherText = aes.encrypt(plainText);

            aes = new AES(rawPassword, aes.getSalt(), aes.getIV());
            decryptedText = aes.decrypt(cipherText);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException |
                NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | BadPaddingException |
                IllegalBlockSizeException | InvalidCryptModeException ex) {

            fail();
        }

        // Then
        assertEquals(plainText, decryptedText);
    }

    /**
     * This method is used to test encryption and decryption on a single object created using
     * CryptMode.BOTH.
     */
    @Test
    void testEncryptionAndDecryptionWhenBothMode() {
        // Given

        // When
        String decryptedText = null;
        try {
            AES aes = new AES(rawPassword, CryptMode.BOTH);
            byte[] cipherText = aes.encrypt(plainText);

            decryptedText = aes.decrypt(cipherText);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException |
                NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | BadPaddingException |
                IllegalBlockSizeException | InvalidCryptModeException ex) {

            fail();
        }

        // Then
        assertEquals(plainText, decryptedText);
    }

    /**
     * This method is used to test an object created in decryption only mode against using for encryption.
     */
    @Test
    void testEncryptionWhenDecryptionMode() {
        // Given

        // When
        boolean testSuccess = false;
        try {
            AES aes = new AES(rawPassword);
            aes = new AES(rawPassword, aes.getSalt(), aes.getIV());
            aes.encrypt(plainText);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException |
                NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | BadPaddingException |
                IllegalBlockSizeException ex) {

            testSuccess = false;
        }
        catch (InvalidCryptModeException ex) {
            testSuccess = true;
        }

        // Then
        assertTrue(testSuccess);
    }

    /**
     * This method is used to test an object created in encryption only mode against using for decryption.
     */
    @Test
    void testDecryptionWhenEncryptionMode() {
        // Given

        // When
        boolean testSuccess = false;
        try {
            AES aes = new AES(rawPassword);
            aes.decrypt(new byte[256]);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException |
                NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | BadPaddingException |
                IllegalBlockSizeException ex) {

            testSuccess = false;
        }
        catch (InvalidCryptModeException ex) {
            testSuccess = true;
        }

        // Then
        assertTrue(testSuccess);
    }

    /**
     * This method is used to test encryption and decryption on an empty input string.
     */
    @Test
    void testEncryptionAndDecryptionWhenTextEmpty() {
        // Given
        plainText = "";

        // When
        String decryptedText = null;
        try {
            AES aes = new AES(rawPassword);
            byte[] cipherText = aes.encrypt(plainText);

            aes = new AES(rawPassword, aes.getSalt(), aes.getIV());
            decryptedText = aes.decrypt(cipherText);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException |
                NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | BadPaddingException |
                IllegalBlockSizeException | InvalidCryptModeException ex) {

            fail();
        }

        // Then
        assertEquals(plainText, decryptedText);
    }

    /**
     * This method is used to test encryption and decryption with an empty password.
     */
    @Test
    void testEncryptionDecryptionWhenPasswordEmpty() {
        // Given
        rawPassword = "";

        // When
        String decryptedText = null;
        try {
            AES aes = new AES(rawPassword);
            byte[] cipherText = aes.encrypt(plainText);

            aes = new AES(rawPassword, aes.getSalt(), aes.getIV());
            decryptedText = aes.decrypt(cipherText);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException |
                NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | BadPaddingException |
                IllegalBlockSizeException | InvalidCryptModeException ex) {

            fail();
        }

        // Then
        assertEquals(plainText, decryptedText);
    }

    /**
     * This method is used to test encryption and decryption with an empty password and input string.
     */
    @Test
    void testEncryptionDecryptionWhenPasswordAndTextEmpty() {
        // Given
        rawPassword = "";
        plainText = "";

        // When
        String decryptedText = null;
        try {
            AES aes = new AES(rawPassword);
            byte[] cipherText = aes.encrypt(plainText);

            aes = new AES(rawPassword, aes.getSalt(), aes.getIV());
            decryptedText = aes.decrypt(cipherText);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException |
                NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | BadPaddingException |
                IllegalBlockSizeException | InvalidCryptModeException ex) {

            fail();
        }

        // Then
        assertEquals(plainText, decryptedText);
    }
}