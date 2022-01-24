package pl.polsl.lab.szymonbotor.notemanager.controller;

import pl.polsl.lab.szymonbotor.notemanager.entities.User;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * TODO
 */
public class UserController extends EntityController {

    // TODO
    public User findByUsername(String username) {
        em.getTransaction().begin();

        try {
            Query query = em.createQuery("SELECT u FROM users WHERE username=" + username + ";");
            User user = (User) query.getSingleResult();

            return user;
        } catch (PersistenceException e) {
            e.printStackTrace();
            em.getTransaction().rollback();

            return null;
        } finally {
            em.close();
        }
    }

    // TODO
    public User createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

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
            if (cookie.getName() == "password") {
                password = cookie.getValue();
                break;
            }
        }

        return password;
    }
}
