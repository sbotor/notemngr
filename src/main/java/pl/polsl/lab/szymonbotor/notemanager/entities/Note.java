package pl.polsl.lab.szymonbotor.notemanager.entities;

import pl.polsl.lab.szymonbotor.notemanager.model.AES;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Note entity class representing a note encrypted with AES.
 * @author Szymon Botor
 * @version 1.0
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

    /**
     * The length of the IV hex string.
     */
    public static final int IV_LENGTH = AES.IV_LENGTH * 2;

    private static final long serialVersionUID = 1L;

    /**
     * Note ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

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
    @Column(length = MAX_NAME_LENGTH, nullable = false)
    private String name;

    /**
     * Initialization vector for encryption.
     */
    @Column(length = IV_LENGTH, nullable = false)
    private String iv;

    /**
     * A default constructor initializing everything to null.
     */
    public Note() {
        user = null;
        name = null;
        iv = null;
    }

    /**
     * A default constructor initializing everything to null and naming the
     * note according to the <code>name</code> parameter.
     * @param name note name.
     */
    public Note(String name) {
        super();

        this.name = name;
    }

    /**
     * Gets the note ID.
     * @return note ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the note ID.
     * @param id new note ID.
     */
    public void setId(long id) {
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
     * Gets the note's initialization vector.
     * @return note's initialization vector.
     */
    public String getIV() {
        return iv;
    }

    /**
     * Sets the note's initialization vector.
     * @param iv new IV of the array.
     */
    public void setIV(String iv) {
        this.iv = iv;
    }

    /**
     * Gets the name of the note.
     * @return the note's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the note.
     * @param name new note name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the note content. Does not perform encryption.
     * @param content new note content.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the note content. Does not perform decryption.
     * @return content of the note.
     */
    public String getContent() {
        return this.content;
    }
}
