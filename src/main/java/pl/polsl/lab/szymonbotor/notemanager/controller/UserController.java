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
 * TODO
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

    // TODO
    private User user;

    // TODO
    private AES aes;

    // TODO
    private HttpSession session;


    // TODO
    public UserController(HttpSession session) {

        this.session = session;

        this.user = fetchUser();
        this.aes = fetchAES();
    }

    // TODO
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

    // TODO
    public static User createUser(String username, String password) {

        String hashedPassword;
        try {
            hashedPassword = Hash.fromPlain(password).toString();
        } catch (CryptException e) {
            return null;
        }

        User newUser = new User(username, hashedPassword);
        if (persist(newUser)) {
            return newUser;
        } else {
            return null;
        }
    }

    // TODO
    public static boolean validatePassword(String password) {
        return true; // TODO: validate
    }

    // TODO
    public void storeUserData(User user, String password) {
        storeUser(user);

        try {
            AES aes = new AES(password);
            session.setAttribute(AES_ATTR, aes);
        } catch (CryptException e) {
            e.printStackTrace();
        }

        session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
    }

    // TODO
    public void storeUser(User user) {
        session.setAttribute(USER_ATTR, user);
        session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);

        this.user = user;
    }

    // TODO
    private User fetchUser() {
        return (User) session.getAttribute(USER_ATTR);
    }

    // TODO
    public void clearUserData() {
        if (fetchUser() != null) {
            session.removeAttribute(USER_ATTR);
        }

        if (fetchAES() != null) {
            session.removeAttribute(AES_ATTR);
        }
    }

    // TODO
    public boolean isAuthenticated() {
        return (user != null && aes != null);
    }

    // TODO
    private AES fetchAES() {
        return (AES) session.getAttribute(AES_ATTR);
    }

    // TODO
    public User getUser() {
        return user;
    }

    // TODO
    public AES getAES() {
        return aes;
    }

    // TODO
    public HttpSession getSession() {
        return session;
    }
}
