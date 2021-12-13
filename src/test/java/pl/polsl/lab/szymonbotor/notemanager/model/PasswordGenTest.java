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

/**
 * This is the testing class for the PasswordGen class.
 * @author Szymon Botor
 * @version 1.1
 */
class PasswordGenTest {

    /**
     * This method is used to test symbol parsing when the symbol list string is empty.
     */
    @Test
    void parseSymbolsWhenEmpty() {
        // Given
        final int passSize = 5;
        final String symbols = "";

        // When
        boolean testSuccess = false;
        try {
            new PasswordGen(passSize, symbols);
            testSuccess = true;
        }
        catch (InvalidCharacterException | InvalidPasswordLengthException ignored) {
        }

        // Then
        assertTrue(testSuccess, "A new PasswordGen object was not successfully created.");
    }

    /**
     * This method is used to test symbol parsing when the symbol list string is not empty and has only correct symbols.
     */
    @Test
    void parseSymbolsWhenNotEmptyAndCorrect() {
        // Given
        final int passSize = 5;
        final String symbols = "Du!@#$%^&*-_o+,.?dUduo";

        // When
        boolean testSuccess = false;
        try {
            new PasswordGen(passSize, symbols);
            testSuccess = true;
        }
        catch (InvalidCharacterException | InvalidPasswordLengthException ignored) {
        }

        // Then
        assertTrue(testSuccess, "A new PasswordGen object was not successfully created.");
    }

    /**
     * This method is used to test symbol parsing when the symbol list string is not empty and has incorrect symbols.
     */
    @Test
    void parseSymbolsWhenNotEmptyAndIncorrect() {
        // Given
        final int passSize = 5;
        final String symbols = "Du!so9=..\\";

        // When
        boolean testSuccess = false;
        try {
            new PasswordGen(passSize, symbols);
        }
        catch (InvalidCharacterException ex) {
            testSuccess = true;
        }
        catch (InvalidPasswordLengthException ignored) {
        }

        // Then
        assertTrue(testSuccess, "InvalidCharacterException was not thrown.");
    }

    /**
     * This method is used to test the behaviour when the desired password length is too low.
     */
    @Test
    void generateWhenCountTooLow() {
        // Given
        final int passSize = 0;
        final String symbols = "Du!@#$%^&*-_o+,.?dUduo";

        // When
        boolean testSuccess = false;
        try {
            new PasswordGen(passSize, symbols);
        }
        catch (InvalidCharacterException ignored) {
        }
        catch (InvalidPasswordLengthException ex) {
            testSuccess = true;
        }

        // Then
        assertTrue(testSuccess, "InvalidPasswordLengthException not thrown.");
    }

    /**
     * This method is used to test the behaviour when the desired password length is too high.
     */
    @Test
    void generateWhenCountTooHigh() {
        // Given
        final int passSize = PasswordGen.MAX_PASSWORD_LENGTH + 1;
        final String symbols = "Du!@#$%^&*-_o+,.?dUduo";

        // When
        boolean testSuccess = false;
        try {
            new PasswordGen(passSize, symbols);
        }
        catch (InvalidCharacterException ignored) {
        }
        catch (InvalidPasswordLengthException ex) {
            testSuccess = true;
        }

        // Then
        assertTrue(testSuccess, "InvalidPasswordLengthException not thrown.");
    }

    /**
     * This method is used to test the behaviour when the parameters are correct.
     * @param passSize size of the password to generate.
     * @param symbols a string symbolising the list of symbols to pick from during generation.
     * @throws InvalidCharacterException Thrown when the generator encounters an invalid symbol in the symbol string.
     * @throws InvalidPasswordLengthException Thrown when the password length is incorrect.
     * @see PasswordGenTest#intAndStringParamSource()
     */
    @ParameterizedTest
    @MethodSource("intAndStringParamSource")
    void generateWhenCorrectParameters(int passSize, String symbols) throws InvalidCharacterException, InvalidPasswordLengthException {
        // Given

        // When
        String generated = "";
        PasswordGen passGen = new PasswordGen(passSize, symbols);
        generated = passGen.generate();

        // Then
        assertEquals(passSize, generated.length(), "Generated and expected password lengths are not equal.");
    }

    /**
     * This is the factory method for the generatePasswordWhenCorrectParameters test.
     * @return a stream of the generated arguments.
     * @see PasswordGenTest#generateWhenCorrectParameters(int, String)
     */
    static Stream<Arguments> intAndStringParamSource() {
        int[] intArray = new int[] {1, 8, 10, 17, 25, 60, PasswordGen.MAX_PASSWORD_LENGTH};
        String[] stringArray = new String[] {"", "d", "u", "o", "+", "*+", PasswordGen.SPECIAL_SYMBOLS, "@#$%^dduoodd"};

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