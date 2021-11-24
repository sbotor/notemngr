package pl.polsl.lab.szymonbotor.notemanager.model;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidPasswordLengthException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCharacterException;
import java.util.Set;
import java.util.HashSet;
import java.security.SecureRandom;

/**
 * Class used during password generation. It uses the provided symbols to generate a random password of a specified length.
 * @author Szymon Botor
 * @version 1.1
 */
public class PasswordGen {
    
    /**
     * Constant string of all valid special symbols that can be used during generation.
     */
    public static final String SPECIAL_SYMBOLS = "!@#$%^&*-_+,.?";
    
    /**
     * Constant value of maximum password length.
     */
    public static final int MAX_PASSWORD_LENGTH = 64;
    
    /**
     * Value indicating if uppercase letters are allowed in the generation process.
     */
    private boolean allowUppercase;
    
    /**
     * Value indicating if digits are allowed in the generation process.
     */
    private boolean allowDigits;
    
    /**
     * Value indicating if all the valid special symbols are allowed in the generation process.
     */
    private boolean allowAllSpecial;
    
    /**
     * The length of the password to generate.
     */
    private int charCount;
    
    /**
     * A set of characters to generate the password from.
     */
    private Set<Character> allowedSymbols;
    
    /**
     * The constructor of the PasswordGen class.
     * @param length length of the password to generate. Should be in (1, 64&gt;.
     * @param symbols additional symbols to include in the generation process:<br>
     * [u]ppercase letters<br>
     * [d]igits<br>
     * [o]ther symbols and/or any of the valid other symbols: !@#$%^&amp;*-_+,.?
     * @throws InvalidPasswordLengthException Thrown if the provided length is less than 1 or more than MAX_PASSWORD_LENGTH.
     * @throws InvalidCharacterException Thrown if any of the provided symbols is an invalid character.
     */
    public PasswordGen(int length, String symbols) throws InvalidPasswordLengthException,
            InvalidCharacterException {
        
        allowUppercase = false;
        allowDigits = false;
        allowAllSpecial = false;
        allowedSymbols = new HashSet();
        
        parseSymbols(symbols);
        
        for (char c = 'a'; c < 'z'; c++) {
            allowedSymbols.add(c);
        }
        
        if (length > 0 && length <= MAX_PASSWORD_LENGTH) {
            charCount = length;
        } else {
            throw new InvalidPasswordLengthException(
                    String.format("Password length of %d is invalid. Should be <%d, %d>.",
                    length, 0, MAX_PASSWORD_LENGTH));
        }
    }
    
    /**
     * Method used to generate a new password based on the parameters of the internal state of the PasswordGen object.
     * @return generated password.
     */
    public String generate() {
        char[] generated = new char[charCount];
        SecureRandom random = new SecureRandom();
        
        for (int i = 0; i < charCount; i++) {
            int offset = random.nextInt(allowedSymbols.size()),
                    counter = 0;
            
            for (char x : allowedSymbols) {
                if (counter == offset) {
                    generated[i] = x;
                    break;
                }
                counter++;
            }
        }
        
        return String.valueOf(generated);
    }
    
    /**
     * Method used to parse the provided additional symbols.
     * @param symbols provided symbols without spaces.
     * @throws InvalidCharacterException Thrown if an invalid character is encountered.
     */
    private void parseSymbols(String symbols) throws InvalidCharacterException {
        for (int i = 0; i < symbols.length(); i++) {
            if (allowUppercase && allowDigits && allowAllSpecial) {
                break;
            }
            
            switch (symbols.charAt(i)) {
                case 'd':
                case 'D':
                    allowDigits = true;
                    for (char c = '0'; c < '9'; c++) {
                        allowedSymbols.add(c);
                    }
                    break;
                case 'u':
                case 'U':
                    allowUppercase = true;
                    for (char c = 'A'; c < 'Z'; c++) {
                        allowedSymbols.add(c);
                    }
                    break;
                case 'o':
                case 'O':
                    allowAllSpecial = true;
                    for (int cIndx = 0; cIndx < SPECIAL_SYMBOLS.length(); cIndx++) {
                        allowedSymbols.add(SPECIAL_SYMBOLS.charAt(cIndx));
                    }
                    break;
                case '!':
                case '@':
                case '#':
                case '$':
                case '%':
                case '^':
                case '&':
                case '*':
                case '-':
                case '_':
                case '+':
                case ',':
                case '.':
                case '?':
                    allowedSymbols.add(symbols.charAt(i));
                    break;
                default:
                    throw new InvalidCharacterException(String.format(
                            "Character %c is invalid.", symbols.charAt(i)));
            }
        }
    }
}
