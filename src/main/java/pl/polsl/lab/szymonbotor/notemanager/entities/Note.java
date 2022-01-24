package pl.polsl.lab.szymonbotor.notemanager.entities;

import pl.polsl.lab.szymonbotor.notemanager.enums.CryptMode;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.model.AES;
import pl.polsl.lab.szymonbotor.notemanager.model.Authenticator;
import pl.polsl.lab.szymonbotor.notemanager.model.Hash;

import javax.persistence.*;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * TODO
 */
@Entity
@Table(
        name = "notes",
        uniqueConstraints = { @UniqueConstraint(name="", columnNames = {"user", "name"}) }
)
public class Note implements Serializable {

    /**
     * Maximum number of characters in the note.
     */
    public static final int MAX_NOTE_LENGTH = 1000;

    /**
     * Maximum number of characters in the note name.
     */
    public static final int MAX_NAME_LENGTH = 32;

    private static final long serialVersionUID = 1L;

    /**
     * Note ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Owner of the note.
     */
    @ManyToOne(targetEntity = User.class, optional = false)
    private User user;

    /**
     * The contents of the note. Can be encrypted with AES according to the owner's password.
     */
    @Column(length = MAX_NOTE_LENGTH)
    private String content;

    /**
     * The name of the note.
     */
    @Column(length = MAX_NAME_LENGTH)
    private String name;

    /**
     * Whether the note is encrypted or not.
     */
    @Column(nullable = false)
    private boolean encrypted;

    /**
     * Cryptographic salt for note encryption.
     */
    @Column(length = AES.SALT_LENGTH)
    private String salt;

    /**
     * Initialization vector for encryption.
     */
    @Column(length = AES.IV_LENGTH)
    private String iv;

    // TODO
    public Optional<String> decrypt(String password) throws InvalidCryptModeException, CryptException {

        Authenticator auth = new Authenticator(password.getBytes(StandardCharsets.UTF_8));
        if (!auth.authenticate(password)) {
            return Optional.empty();
        }

        byte[] saltBytes = Hash.stringToBytes(salt),
                ivBytes = Hash.stringToBytes(iv);
        AES aes = new AES(password, saltBytes, ivBytes);

        return Optional.of(aes.decrypt(content.getBytes(StandardCharsets.UTF_8)));
    }

    // TODO
    public boolean encrypt(String password, String newContent) throws CryptException, InvalidCryptModeException {

        Authenticator auth = new Authenticator(password.getBytes(StandardCharsets.UTF_8));
        if (!auth.authenticate(password)) {
            return false;
        }

        AES aes = new AES(password, CryptMode.ENCRYPTION);
        content = Hash.bytesToString(aes.encrypt(newContent));
        return true;
    }

    /**
     * Gets the note ID.
     * @return note ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the note ID.
     * @param id new note ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the owner of the note.
     * @return the user that owns the note.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the owner of the note
     * @param user the user that should own the note.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Checks if the note is encrypted with AES.
     * @return true if the note is encrypted, false otherwise.
     */
    public boolean isEncrypted() {
        return encrypted;
    }

    /**
     * Sets the encryption status of the note.
     * @param encrypted true if the note is encrypted false otherwise.
     */
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    /**
     * Gets the salt of the note.
     * @return note's salt.
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Sets the note's salt.
     * @param salt new salt.
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * Gets the note's initialization vector.
     * @return note's initialization vector.
     */
    public String getIv() {
        return iv;
    }

    /**
     * Sets the note's initialization vector.
     * @param iv new IV of the array.
     */
    public void setIv(String iv) {
        this.iv = iv;
    }
}
