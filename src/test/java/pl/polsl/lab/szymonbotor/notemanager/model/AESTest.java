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

class AESTest {

    private String rawPassword;
    private String plainText;

    @BeforeEach
    void prepare() {
        rawPassword = "!paSswOrd_123";
        plainText = "Test string. This string is used for testing. I will now present some symbols: 123890!@#$%-()=.";
    }

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