package pl.polsl.lab.szymonbotor.notemanager.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticatorTest {

    Authenticator auth;

    @ParameterizedTest
    @CsvSource({})
    static void testHashing(String input, byte[] expected) {

    }

    @Test
    void testAuthentication() {

    }
}