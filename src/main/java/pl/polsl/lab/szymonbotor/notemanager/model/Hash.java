package pl.polsl.lab.szymonbotor.notemanager.model;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A convenience class for implementing SHA-256 algorithm
 * and general byte and hex string conversions.
 * @author Szymon Botor
 * @version 1.0
 */
public class Hash {

    /**
     * Byte array with the hash.
     */
    protected byte[] hash;

    /**
     * Returns the hash as an array of bytes.
     * @return array of bytes representing the hash.
     */
    public byte[] getBytes() {
        return this.hash;
    }

    /**
     * Constructor creating a Hash object from a byte array.
     * @param hashed byte array with the hash.
     */
    protected Hash(byte[] hashed) {
        this.hash = hashed;
    }

    /**
     * A static method used for hashing a provided text by passing it through SHA-256.
     * @param text text to be hashed.
     * @return array of bytes representing the hash of the text.
     * @throws CryptException This exception is thrown when an error occurs during hashing.
     */
    public static byte[] hashText(String text) throws CryptException {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new CryptException(e.getMessage());
        }
    }

    /**
     * Creates a new Hash object from a plain text string.
     * @param plain text string to hash.
     * @return new Hash with the hashed string.
     * @throws CryptException Thrown when a cryptographic error occurs.
     */
    public static Hash fromPlain(String plain) throws CryptException {
        return new Hash(hashText(plain));
    }

    /**
     * Creates a new Hash object from the hashed string.
     * @param hashString hashed string to convert to a Hash object.
     * @return new Hash from the passed string.
     */
    public static Hash fromString(String hashString) {
        if (hashString.length() != 64) {
            throw new IllegalArgumentException("Invalid hash string length.");
        }

        return new Hash(stringToBytes(hashString));
    }

    /**
     * Creates a new Hash object from the hash bytes.
     * @param bytes byte array of the hash.
     * @return new Hash from the passed bytes.
     */
    public static Hash fromBytes(byte[] bytes) {
        return new Hash(bytes);
    }

    /**
     * Returns a hex string representation of the passed bytes.
     * @param bytes byte array representing the string.
     * @return hex string representation of the passed bytes.
     */
    public static String bytesToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder(2 * bytes.length);
        for (byte bt : bytes) {
            String hex = Integer.toHexString(0xFF & bt);
            if (hex.length() == 1) {
                builder.append('0');
            }
            builder.append(hex);
        }

        return builder.toString();
    }

    /**
     * Returns a byte array representation of the passed hex string.
     * @param string string-represented hex.
     * @return array of bytes from the string.
     */
    public static byte[] stringToBytes(String string) {
        int len = string.length();
        byte[] bytes = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = byteFromHex(string.charAt(i), string.charAt(i + 1));
        }

        return bytes;
    }

    /**
     * Converts two characters representing two hex values to their byte representation.
     * @param hex1 first hex digit.
     * @param hex2 second hex digit.
     * @return byte representing the appropriate hex value.
     */
    private static byte byteFromHex(char hex1, char hex2) {
        int retVal = Character.digit(hex1, 16) << 4;
        retVal += Character.digit(hex2, 16);

        return (byte) retVal;
    }

    @Override
    public String toString() {
        return bytesToString(hash);
    }
}
