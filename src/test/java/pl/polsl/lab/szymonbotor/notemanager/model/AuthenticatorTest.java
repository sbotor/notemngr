package pl.polsl.lab.szymonbotor.notemanager.model;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticatorTest {

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
        catch (NoSuchAlgorithmException ex) {
            testSuccess = false;
        }

        // Then
        assertTrue(testSuccess);
    }

    @Test
    void testAuthWhenCorrectPassword() {
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
        catch (NoSuchAlgorithmException ex) {
            testSuccess = false;
        }

        // Then
        assertTrue(testSuccess);
    }

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
        catch (NoSuchAlgorithmException ex) {
            testSuccess = false;
        }

        // Then
        assertTrue(testSuccess);
    }
}