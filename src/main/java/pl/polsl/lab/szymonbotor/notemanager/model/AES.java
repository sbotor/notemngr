package pl.polsl.lab.szymonbotor.notemanager.model;

import pl.polsl.lab.szymonbotor.notemanager.enums.CryptMode;
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
 * @version 1.0
 */
public class AES {
    /**
     * Constant value representing the number of iterations in the AES algorithm.
     */
    private static final int ITER_COUNT = 65536;
    
    /**
     * Constant value representing the length of the encryption key in bits.
     */
    private static final int KEY_LENGTH = 256;
    
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
     * @param password password to be used as a base for the secret key.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     */
    public AES(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        
        cryptMode = CryptMode.ENCRYPTION;

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        
        salt = new byte[8];
        new SecureRandom().nextBytes(salt);
        
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITER_COUNT, KEY_LENGTH);
        key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        
        byte[] ivArray = new byte[16];
        new SecureRandom().nextBytes(ivArray);
        iv = new IvParameterSpec(ivArray);
    }
    
    /**
     * Constructor used during decryption. It needs to be supplied with salt and initialisation vector
     * generated during the encryption process.
     * @param password password to be used as a base for the secret key.
     * @param salt previously generated cryptographic salt.
     * @param ivArray previously generated initialisation vector.
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws InvalidKeySpecException This is the exception for invalid key specifications.
     */
    public AES(String password, byte[] salt, byte[] ivArray)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        cryptMode = CryptMode.DECRYPTION;

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITER_COUNT, KEY_LENGTH);
        key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        
        iv = new IvParameterSpec(ivArray);
    }
    
    /**
     * This method is used to get the initialisation vector.
     * @return current initialisation vector of the AES instance.
     */
    public byte[] getIV() {
        return iv.getIV();
    }
    
    /**
     * This method is used to get the cryptographic salt.
     * @return current cryptographic salt of the AES instance.
     */
    public byte[] getSalt() {
        return salt;
    }
    
    /**
     * This method is used to encrypt text provided as the argument using AES.
     * @param data string of data to encrypt (plaintext).
     * @return encrypted array of bytes (ciphertext).
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws InvalidCryptModeException This exception is thrown when the method is called on an object set up to decrypt.
     */
    public byte[] encrypt(String data)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, InvalidCryptModeException {

        if (cryptMode == CryptMode.ENCRYPTION) {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            byte[] retVal = cipher.doFinal(data.getBytes());

            return retVal;
        } else {
            throw new InvalidCryptModeException("Encrypt called on a decryption AES object.");
        }
    }
    
    /**
     * This method is used to decrypt data provided as the argument using AES.
     * @param data array of bytes do decrypt (ciphertext).
     * @return decrypted text (plaintext).
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws NoSuchPaddingException This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws InvalidKeyException This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     * @throws InvalidAlgorithmParameterException This is the exception for invalid or inappropriate algorithm parameters.
     * @throws IllegalBlockSizeException This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws BadPaddingException This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws InvalidCryptModeException This exception is thrown when the method is called on an object set up to encrypt.
     */
    public String decrypt(byte[] data)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, InvalidCryptModeException {

        if (cryptMode == CryptMode.DECRYPTION) {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] plainText = cipher.doFinal(data);

            return new String(plainText);
        } else {
            throw new InvalidCryptModeException("Decrypt called on an encryption AES object.");
        }
    }
}
