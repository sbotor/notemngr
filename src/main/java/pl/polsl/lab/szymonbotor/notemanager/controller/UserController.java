package pl.polsl.lab.szymonbotor.notemanager.controller;

import pl.polsl.lab.szymonbotor.notemanager.entities.User;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.model.Hash;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO
 */
public class UserController extends EntityController {

    // TODO
    public User findByUsername(String username) {
        beginTransaction();

        try {
            String queryString = "SELECT u FROM User u WHERE u.username = :username";
            TypedQuery<User> query = MANAGER.createQuery(queryString, User.class);
            query.setParameter("username", username);

            User user =  query.getSingleResult();
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

        User user = new User(username, hashedPassword);
        if (persist(user)) {
            return user;
        } else {
            return null;
        }
    }

    // TODO
    public void storePassword(HttpServletResponse response, String password) {
        Cookie cookie = new Cookie("password", password);
        cookie.setPath("/");
        cookie.setMaxAge(5 * 60);
        response.addCookie(cookie);
    }

    // TODO
    public String fetchPassword(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }

        String password = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("password")) {
                password = cookie.getValue();
                break;
            }
        }

        return password;
    }
}
