package pl.polsl.lab.szymonbotor.notemanager.model.tests;

import org.junit.jupiter.api.Test;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.model.Authenticator;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the testing class for the Authenticator class.
 * @author Szymon Botor
 * @version 1.0
 */
class AuthenticatorTest {

    /**
     * This test is used to check if the authorisation will be denied by using the incorrect password.
     */
    @Test
    void testAuthWhenWrongPassword() {
        // Given
        String originalPassword = "!password_123",
                newPassword = "wrong_password";

        // When
        boolean testSuccess = false;
        try {
            byte[] passHash = Authenticator.hashPassword(originalPassword);
            Authenticator auth = new Authenticator(passHash);

            testSuccess = !auth.authenticate(newPassword);
        }
        catch (CryptException e) {
            e.printStackTrace();
        }

        // Then
        assertTrue(testSuccess, "Authorisation did not succeed.");
    }

    /**
     * This test is used to check if the authorisation will be granted by using the correct password.
     */
    @Test
    void testAuthWhenPasswordCorrect() {
        // Given
        String originalPassword = "!password_123",
                newPassword = "!password_123";

        // When
        boolean testSuccess = false;
        try {
            byte[] passHash = Authenticator.hashPassword(originalPassword);
            Authenticator auth = new Authenticator(passHash);

            testSuccess = auth.authenticate(newPassword);
        }
        catch (CryptException e) {
            e.printStackTrace();
        }

        // Then
        assertTrue(testSuccess, "Authorisation did not succeed.");
    }

    /**
     * This test is used to check if the authorisation will be granted if the password is empty and correct.
     */
    @Test
    void testAuthWhenPasswordEmpty() {
        // Given
        String originalPassword = "",
                newPassword = "";

        // When
        boolean testSuccess = false;
        try {
            byte[] passHash = Authenticator.hashPassword(originalPassword);
            Authenticator auth = new Authenticator(passHash);

            testSuccess = auth.authenticate(newPassword);
        }
        catch (CryptException e) {
            e.printStackTrace();
        }

        // Then
        assertTrue(testSuccess, "Authorisation did not succeed.");
    }
}