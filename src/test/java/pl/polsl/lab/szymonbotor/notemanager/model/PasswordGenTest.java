package pl.polsl.lab.szymonbotor.notemanager.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCharacterException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidPasswordLengthException;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGenTest {

    @Test
    void parseSymbolsWhenEmpty() {
        // Given
        final int passSize = 5;
        final String symbols = "";

        // When
        boolean testSuccess = false;
        try {
            PasswordGen passGen = new PasswordGen(passSize, symbols);
            testSuccess = true;
        }
        catch (InvalidCharacterException | InvalidPasswordLengthException ex) {
            testSuccess = false;
        }

        // Then
        assertTrue(testSuccess);
    }

    @Test
    void parseSymbolsWhenNotEmptyAndCorrect() {
        // Given
        final int passSize = 5;
        final String symbols = "Du!@#$%^&*-_o+,.?dUduo";

        // When
        boolean testSuccess = false;
        try {
            PasswordGen passGen = new PasswordGen(passSize, symbols);
            testSuccess = passGen != null;
        }
        catch (InvalidCharacterException | InvalidPasswordLengthException ex) {
            testSuccess = false;
        }

        // Then
        assertTrue(testSuccess);
    }

    @Test
    void parseSymbolsWhenNotEmptyAndIncorrect() {
        // Given
        final int passSize = 5;
        final String symbols = "Du!so9=..\\";

        // When
        boolean testSuccess = false;
        try {
            PasswordGen passGen = new PasswordGen(passSize, symbols);
            testSuccess = false;
        }
        catch (InvalidCharacterException ex) {
            testSuccess = true;
        }
        catch (InvalidPasswordLengthException ex) {
            testSuccess = false;
        }

        // Then
        assertTrue(testSuccess);
    }

    @Test
    void generateCharacterWhenCountTooLow() {
        // Given
        final int passSize = 0;
        final String symbols = "Du!@#$%^&*-_o+,.?dUduo";

        // When
        boolean testSuccess = false;
        try {
            PasswordGen passGen = new PasswordGen(passSize, symbols);
            testSuccess = false;
        }
        catch (InvalidCharacterException ex) {
            testSuccess = false;
        }
        catch (InvalidPasswordLengthException ex) {
            testSuccess = true;
        }

        // Then
        assertTrue(testSuccess);
    }

    @Test
    void generateCharacterWhenCountTooHigh() {
        // Given
        final int passSize = PasswordGen.MAX_PASSWORD_LENGTH + 1;
        final String symbols = "Du!@#$%^&*-_o+,.?dUduo";

        // When
        boolean testSuccess = false;
        try {
            PasswordGen passGen = new PasswordGen(passSize, symbols);
            testSuccess = false;
        }
        catch (InvalidCharacterException ex) {
            testSuccess = false;
        }
        catch (InvalidPasswordLengthException ex) {
            testSuccess = true;
        }

        // Then
        assertTrue(testSuccess);
    }

    @ParameterizedTest
    @MethodSource("intAndStringParamSource")
    void generatePasswordWhenCorrectParameters(int passSize, String symbols) {
        // Given

        // When
        String generated = "";
        try {
            PasswordGen passGen = new PasswordGen(passSize, symbols);
            generated = passGen.generate();
        }
        catch (InvalidCharacterException | InvalidPasswordLengthException ex) {
            fail();
        }

        // Then
        assertEquals(passSize, generated.length());
    }

    static Stream<Arguments> intAndStringParamSource() {
        int[] intArray = new int[] {1, 2, 3, 7, 8, 10, 17, 20, 25, 40, 50, 60, PasswordGen.MAX_PASSWORD_LENGTH};
        String[] stringArray = new String[] {"", "d", "u", "o", "+", "*+", ".", "@#$%^dduoodd", "@#&"};

        Arguments[] args = new Arguments[intArray.length * stringArray.length];

        int c = 0;
        for (int num : intArray) {
            for (String str : stringArray) {
                args[c++] = Arguments.of(num, str);
            }
        }

        return Arrays.stream(args);
    }
}