package pl.polsl.lab.szymonbotor.notemanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @Test
    void testEncryptionAndDecryptionWhenCorrect() {
        // Given
        String rawPassword = "!password_123",
        plainText = "Test string. This string is used for testing. I will now present some symbols: 123890!@#$%-()=.";

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
    void testEncryptionWhenDecryptionMode() {
        // Given
        String rawPassword = "!password_123",
                plainText = "Test string. This string is used for testing. I will now present some symbols: 123890!@#$%-()=.";

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
        String rawPassword = "!password_123";

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
}