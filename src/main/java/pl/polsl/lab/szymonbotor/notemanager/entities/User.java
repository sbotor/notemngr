package pl.polsl.lab.szymonbotor.notemanager.entities;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.model.Authenticator;
import pl.polsl.lab.szymonbotor.notemanager.model.Hash;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;

/**
 * User entity. TODO
 * @author sotor
 * @version 1.0
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    /**
     * Maximum number of characters in the username.
     */
    public static final int MAX_USERNAME_LENGTH = 32;

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new user with null fields.
     */
    public User() {
        this.username = null;
        this.password = null;

        this.notes = null;
    }

    /**
     * Creates a user with the specified username and password and null note set.
     * @param username new username for the user.
     * @param password new password for the user.
     */
    public User(String username, String password) {
        this();

        this.username = username;
        this.password = password;
    }

    /**
     * User ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Unique username for the user.
     */
    @Column(unique = true, length = MAX_USERNAME_LENGTH, nullable = false)
    private String username;

    /**
     * User password encrypted with SHA-256.
     */
    @Column(length = 64, nullable = false)
    private String password;

    /**
     * User notes.
     */
    @OneToMany(targetEntity = Note.class, mappedBy = "user")
    private Set<Note> notes;

    // TODO
    public boolean authenticate(String password) throws CryptException {
        Authenticator auth = new Authenticator(Hash.stringToBytes(this.password));
        return auth.authenticate(password);
    }

    /**
     * Gets the user ID.
     * @return user ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user ID.
     * @param id new user ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user's username.
     * @return user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets a new username for the user.
     * @param username new username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the save user password hash.
     * @return string-represented hash of the user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user password.
     * @param password new string-represented hash of the user password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's notes.
     * @return all notes belonging to the user.
     */
    public Set<Note> getNotes() {
        return notes;
    }
}
