package pl.polsl.lab.szymonbotor.notemanager.model;

import pl.polsl.lab.szymonbotor.notemanager.enums.CryptMode;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class implementing the Advanced Encryption Standard algorithm. It is used to encrypt and decrypt notes.
 * @author Szymon Botor
 * @version 1.1
 */
public class AES {
    /**
     * Constant value representing the number of iterations in the AES algorithm.
     */
    public static final int ITER_COUNT = 65536;
    
    /**
     * Constant value representing the length of the encryption key in bits.
     */
    public static final int KEY_LENGTH = 256;

    /**
     * Constant value representing size of the initialisation vector.
     */
    public static final int IV_LENGTH = 16;

    /**
     * Constant value representing size of the cryptographic salt in bytes.
     */
    public static final int SALT_LENGTH = 8;

    /**
     * This is an enum used to determine whether the object is used to encrypt, decrypt or both.
     */
    public final CryptMode cryptMode;

    /**
     * Secret key used in encryption and decryption.
     */
    private SecretKey key;
    
    /**
     * Initialisation vector used in encryption and decryption. Either randomly generated (encryption)
     * or provided (decryption).
     */
    private IvParameterSpec iv;
    
    /**
     * Value of 8 bytes used during secret key generation. Either randomly generated during (encryption)
     * or provided (decryption).
     */
    private byte[] salt;
    
    /**
     * Constructor used during encryption. It generates a new random initialisation vector and salt.
     * @param password password to be used as a base for the secret key. Can be empty.
     * @throws CryptException This exception is thrown when a cryptographic error occurs.
     */
    public AES(String password)
            throws CryptException {
        
        this(password, CryptMode.ENCRYPTION);
    }

    /**
     * Constructor used to create an object using the specified mode (encryption, decryption or both).
     * It generates a new random initialisation vector and salt.
     * @param password password to be used as a base for the secret key. Can be empty.
     * @param mode enum representing the type of operation available for the object.
     * @throws CryptException This exception is thrown when a cryptographic error occurs.
     */
    public AES(String password, CryptMode mode)
            throws CryptException {

        cryptMode = mode;

        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            regenerateSalt();

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITER_COUNT, KEY_LENGTH);
            key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

            regenerateIv();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CryptException(e.getMessage());
        }
    }
    
    /**
     * Constructor used during decryption. It needs to be supplied with salt and initialisation vector
     * generated during the encryption process.
     * @param password password to be used as a base for the secret key. Can be empty.
     * @param salt previously generated cryptographic salt.
     * @param ivArray previously generated initialisation vector.
     * @throws CryptException This exception is thrown when a cryptographic error occurs.
     */
    public AES(String password, byte[] salt, byte[] ivArray)
            throws CryptException {
        this(password, salt, ivArray, CryptMode.DECRYPTION);
    }

    /**
     * Constructor used to create an object with the provided salt and initialisation vector using the specified
     * mode (encryption, decryption or both).
     * @param password password to be used as a base for the secret key. Can be empty.
     * @param salt previously generated cryptographic salt.
     * @param ivArray previously generated initialisation vector.
     * @param mode enum representing the type of operation available for the object.
     * @throws CryptException This exception is thrown when a cryptographic error occurs.
     */
    public AES(String password, byte[] salt, byte[] ivArray, CryptMode mode)
            throws CryptException {

        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITER_COUNT, KEY_LENGTH);

            key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            iv = new IvParameterSpec(ivArray);
            this.salt = salt;
            cryptMode = mode;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CryptException(e.getMessage());
        }
    }

    /**
     * Generates a new value of the initialization vector of the AES.
     */
    public void regenerateIv() {
        byte[] ivArray = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(ivArray);
        iv = new IvParameterSpec(ivArray);
    }

    /**
     * Generates a new value of the salt of the AES.
     */
    public void regenerateSalt() {
        salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
    }

    /**
     * This method is used to get the initialisation vector.
     * @return current initialisation vector of the AES instance.
     */
    public byte[] getIV() {
        return iv.getIV();
    }

    /**
     * This method is used to set the initialisation vector.
     * @param newIV new initialisation vector.
     */
    public void setIV(byte[] newIV) {
        iv = new IvParameterSpec(newIV);
    }
    
    /**
     * This method is used to get the cryptographic salt.
     * @return current cryptographic salt of the AES instance.
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * This method is used to set the cryptographic salt.
     * @param newSalt new cryptographic salt.
     */
    public void setSalt(byte[] newSalt) {
        salt = newSalt;
    }
    
    /**
     * This method is used to encrypt text provided as the argument using AES.
     * @param data string of data to encrypt (plaintext).
     * @return encrypted array of bytes (ciphertext).
     * @throws InvalidCryptModeException This exception is thrown when the method is called on an object set up to decrypt.
     * @throws CryptException Thrown when a cryptographic error occurs.
     */
    public byte[] encrypt(String data)
            throws InvalidCryptModeException, CryptException {

        if (cryptMode == CryptMode.DECRYPTION) {
            throw new InvalidCryptModeException("Encrypt called on a decryption only AES object.");
        } else {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                return cipher.doFinal(data.getBytes());
            } catch (InvalidKeyException |
                    InvalidAlgorithmParameterException |
                    IllegalBlockSizeException |
                    BadPaddingException |
                    NoSuchAlgorithmException |
                    NoSuchPaddingException e) {
                throw new CryptException(e.getMessage());
            }
        }
    }
    
    /**
     * This method is used to decrypt data provided as the argument using AES.
     * @param data array of bytes do decrypt (ciphertext).
     * @return decrypted text (plaintext).
     * @throws InvalidCryptModeException This exception is thrown when the method is called on an object set up to encrypt.
     * @throws CryptException This exception is thrown when a cryptographic error occurs.
     */
    public String decrypt(byte[] data)
            throws InvalidCryptModeException, CryptException {

        if (cryptMode == CryptMode.ENCRYPTION) {
            throw new InvalidCryptModeException("Decrypt called on an encryption only AES object.");
        } else {
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
                byte[] plainText = cipher.doFinal(data);
                return new String(plainText);
            } catch (NoSuchAlgorithmException |
                    NoSuchPaddingException |
                    InvalidKeyException |
                    InvalidAlgorithmParameterException |
                    IllegalBlockSizeException |
                    BadPaddingException e) {
                throw new CryptException(e.getMessage());
            }
        }
    }
}
