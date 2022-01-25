package pl.polsl.lab.szymonbotor.notemanager.controller;

import pl.polsl.lab.szymonbotor.notemanager.entities.User;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.model.AES;
import pl.polsl.lab.szymonbotor.notemanager.model.Hash;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * TODO
 */
public class UserController extends EntityController {

    // TODO
    private User user;

    // TODO
    public UserController() {
        user = null;
    }

    // TODO
    public UserController(User user) {
        this.user = user;
    }

    // TODO
    public User findByUsername(String username) {
        beginTransaction();

        try {
            String queryString = "SELECT u FROM User u WHERE u.username = :username";
            TypedQuery<User> query = MANAGER.createQuery(queryString, User.class);
            query.setParameter("username", username);

            user =  query.getSingleResult();
            commitIfActive();
            return user;
        } catch (PersistenceException e) {
            e.printStackTrace();
            rollbackIfActive();
            return null;
        }
    }

    // TODO
    public User createUser(String username, String password) {

        String hashedPassword;
        try {
            hashedPassword = Hash.fromPlain(password).toString();
        } catch (CryptException e) {
            return null;
        }

        User newUser = new User(username, hashedPassword);
        if (persist(newUser)) {
            user = newUser;
            return user;
        } else {
            return null;
        }
    }

    // TODO
    public void storeUserData(HttpSession session, String password) {
        storeUser(session);

        try {
            AES aes = new AES(password);
            session.setAttribute("aes", aes);
        } catch (CryptException e) {
            e.printStackTrace();
        }

        session.setMaxInactiveInterval(5 * 60);
    }

    // TODO
    public void storeUser(HttpSession session) {
        session.setAttribute("user", user);
    }

    // TODO
    public User fetchUser(HttpSession session) {
        user = (User) session.getAttribute("user");
        return user;
    }

    // TODO
    public static AES fetchAES(HttpSession session) {
        return (AES) session.getAttribute("aes");
    }

    // TODO
    public User getUser() {
        return user;
    }

    // TODO
    public void setUser(User user) {
        this.user = user;
    }
}
