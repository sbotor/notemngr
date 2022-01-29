package pl.polsl.lab.szymonbotor.notemanager.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.polsl.lab.szymonbotor.notemanager.controller.NoteController;
import pl.polsl.lab.szymonbotor.notemanager.controller.UserController;
import pl.polsl.lab.szymonbotor.notemanager.entities.Note;
import pl.polsl.lab.szymonbotor.notemanager.entities.User;

/**
 * Servlet responsible for removing a user account and all of their notes.
 * @author Szymon Botor
 * @version 1.0
 */
@WebServlet(name="RemoveUserServlet", urlPatterns = {"/removeUser"})
public class RemoveUserServlet extends UserServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        super.processRequest(request, response);

        if (!userCont.isAuthenticated()) {
            view.printError(response, "No user specified.");
            return;
        }

        User user = userCont.getUser();
        for (Note note : user.getNotes()) {
            NoteController.remove(note);
        }

        user.getNotes().clear();
        UserController.persist(user);
        UserController.remove(user);
        userCont.clearUserData();

        response.sendRedirect("/NoteManager");
    }
    
}
