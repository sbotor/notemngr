package pl.polsl.lab.szymonbotor.notemanager.controller;

import pl.polsl.lab.szymonbotor.notemanager.entities.User;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.model.AES;
import pl.polsl.lab.szymonbotor.notemanager.model.Hash;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

import java.util.List;

/**
 * Class for managing user entities including storing them in an HTTP session.
 * @author Szymon Botor
 * @version 1.0
 */
public class UserController extends EntityController {

    /**
     * Name of the user session attribute.
     */
    public static final String USER_ATTR = "user";

    /**
     * Name of the AES session attribute.
     */
    public static final String AES_ATTR = "aes";

    /**
     * Maximum time in seconds that the session should stay active.
     */
    public static final int MAX_INACTIVE_INTERVAL = 5 * 60;

    /**
     * User entity bound to the controller instance.
     */
    private User user;

    /**
     * AES object bound to this controller instance. Used for encryption and decryption.
     */
    private AES aes;

    /**
     * HTTP session bound to this controller instance.
     */
    private HttpSession session;

    /**
     * Constructor creating a controler and binding an HTTP session.
     * The User entity and the appropriate AES object are fetched from the session.
     * @param session HTTP session used by the controller.
     */
    public UserController(HttpSession session) {
        this.session = session;
        this.user = fetchUser();
        this.aes = fetchAES();
    }

    /**
     * Finds a User entity by the passed username.
     * @param username User's username to look for.
     * @return found User object or null if the User does not exist.
     */
    public static User findByUsername(String username) {
        beginTransaction();

        try {
            String queryString = "SELECT u FROM User u WHERE u.username = :username";
            TypedQuery<User> query = MANAGER.createQuery(queryString, User.class);
            query.setParameter("username", username);

            List<User> found = query.getResultList();
            if (found.size() == 1) {
                commitIfActive();
                return found.get(0);
            } else {
                rollbackIfActive();
                return null;
            }
            
        } catch (PersistenceException e) {
            e.printStackTrace();
            rollbackIfActive();
            return null;
        }
    }

    /**
     * Creates a new User entity not saving it to the database.
     * @param username User's username.
     * @param password User's password as a plain text.
     * @param aes AES object representing the encryption parameters.
     * @return created User object or null if unsuccessful.
     */
    public static User createUser(String username, String password, AES aes) {

        String hashedPassword;
        try {
            hashedPassword = Hash.fromPlain(password).toString();
        } catch (CryptException e) {
            return null;
        }

        User newUser = new User(username, hashedPassword);
        newUser.setSalt(Hash.bytesToString(aes.getSalt()));
        if (persist(newUser)) {
            return newUser;
        } else {
            return null;
        }
    }

    /**
     * Saves the passed User and AES as session attributes.
     * Binds the objects to this controller instance.
     * @param user User object to save.
     * @param aes AES object to save.
     */
    public void storeUserData(User user, AES aes) {
        storeUser(user);
        
        session.setAttribute(AES_ATTR, aes);
        this.aes = aes;
        
        session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
    }

    /**
     * Stores the passed User object as a session attribute.
     * Binds the passed user to this controller instance.
     * @param user User to save.
     */
    public void storeUser(User user) {
        session.setAttribute(USER_ATTR, user);
        session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);

        this.user = user;
    }

    /**
     * Finds the User stored in the bound session.
     * Attribute name specified by <code>USER_ATTR</code>.
     * @return Found user or null if not present.
     */
    private User fetchUser() {
        return (User) session.getAttribute(USER_ATTR);
    }

    /**
     * Removes the stored User and AES from the bound session if present.
     * Also removes a stored Note entity.
     */
    public void clearUserData() {
        if (fetchUser() != null) {
            session.removeAttribute(USER_ATTR);
        }

        if (fetchAES() != null) {
            session.removeAttribute(AES_ATTR);
        }

        session.removeAttribute(NoteController.NOTE_ATTR);
    }

    /**
     * Checks if the controller's User and AES objects are not null.
     * This does not actually checks whether the user's password is correct.
     * @return true if the User and AES objects are not null, false otherwise.
     */
    public boolean isAuthenticated() {
        return (user != null && aes != null);
    }

    /**
     * Finds the AES stored in the bound session.
     * Attribute name specified by <code>AES_ATTR</code>.
     * @return Found AES or null if not present.
     */
    private AES fetchAES() {
        return (AES) session.getAttribute(AES_ATTR);
    }

    /**
     * Gets the bound user.
     * @return currently bound User entity.
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the bound AES.
     * @return currently bound AES object used for encryption and decryption.
     */
    public AES getAES() {
        return aes;
    }

    /**
     * Gets the bound HTTP session.
     * @return currently bound session.
     */
    public HttpSession getSession() {
        return session;
    }
}
