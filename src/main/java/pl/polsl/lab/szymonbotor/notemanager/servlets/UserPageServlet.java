package pl.polsl.lab.szymonbotor.notemanager.servlets;

import pl.polsl.lab.szymonbotor.notemanager.controller.NoteController;
import pl.polsl.lab.szymonbotor.notemanager.controller.UserController;
import pl.polsl.lab.szymonbotor.notemanager.entities.Note;
import pl.polsl.lab.szymonbotor.notemanager.entities.User;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.model.AES;
import pl.polsl.lab.szymonbotor.notemanager.view.UserPageView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet responsible for the main user page including the note list.
 * @author Szymon Botor
 * @version 1.0
 */
@WebServlet(name = "UserPageServlet", urlPatterns = {"/user"})
public class UserPageServlet extends UserServlet {

    /**
     * View bound to the servlet used for page rendering.
     */
    protected UserPageView view;

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.processRequest(request, response);
        view = new UserPageView(this);

        request.removeAttribute(NoteController.NOTE_ATTR);

        User user = null;
        if (!userCont.isAuthenticated()) {

            user = findUser(request, response);
            if (user == null) {
                view.printError(response, "Cannot find the specified user.");
                return;
            }
        }
        
        user = userCont.getUser();

        if (makeActions(request, response)) {
            view.printPage(response, user.getUsername(), user);
        }        
    }

    /**
     * Tries to find the user creating one from the HTTP parameters if needed.
     * @param request servlet request.
     * @param response servlet response.
     * @return found or created user. If no user was found or creation was impossible returns null.
     * @throws IOException Thrown when an IO error occurs.
     * @throws ServletException Thrown if a servlet exception occurs.
     */
    protected User findUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username"),
                password = request.getParameter("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            view.printError(response, "The session has expired and no username/password was provided.");
            return null;
        }

        User user = UserController.findByUsername(username);
        if (user == null) {
            view.printError(response, "Cannot find the specified user.");
            return null;
        }

        try {
            if (user.authenticate(password)) {
                AES aes = AES.fromUserEntity(user, password);
                userCont.storeUserData(user, aes);
                return user;
            } else {
                view.printError(response, "Invalid password.");
            }
        } catch (CryptException e) {
            view.printError(response, "Problem authenticating the user.");
        }

        return null;
    }

    /**
     * Checks if an action should be taken before rendering the page
     * performs it accordingly. The action can make the page unrenderable
     * (for example a note can be deleted).
     * @param request servlet request.
     * @param response servlet response.
     * @return true if the a note page should be rendered.
     * @throws IOException Thrown when an IO error occurs.
     */
    private boolean makeActions(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String action = request.getParameter("removeId");
        if (action != null && !action.isBlank()) {
            long noteId;
            Note note;
            try {
                noteId = Long.parseLong(action);
                note = NoteController.findNote(noteId);
                if (note == null)  {
                    view.printError(response, "Cannot find the specified note.");
                    return false;
                }
            } catch (NumberFormatException e) {
                view.printError(response, "Invalid note ID.");
                return true;
            }

            if (userCont.getUser().getId() == note.getUser().getId()) {
                NoteController.remove(note);
                return false;
            } else {
                view.printError(response, "The note does not belong to the current user.");
                return false;
            }
        }

        return true;
    }
}
