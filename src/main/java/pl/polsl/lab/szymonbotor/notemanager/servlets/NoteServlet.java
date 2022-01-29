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
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.model.AES;

/**
 * Servlet responsible for note modification.
 * @author Szymon Botor
 * @version 1.0
 */
@WebServlet(name = "NoteServlet", urlPatterns = { "/note" })
public class NoteServlet extends UserServlet {

    /**
     * Note controller bound to this servlet.
     */
    protected NoteController noteCont;

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        super.processRequest(request, response);

        noteCont = new NoteController(request.getSession());

        if (!userCont.isAuthenticated()) {
            view.printError(response, "The session has expired. Log in again.");
            userCont.clearUserData();
            return;
        }

        String noteIdStr = request.getParameter("openId"),
            removeIdStr = request.getParameter("removeId");
        Note note;
        if (removeIdStr != null && !removeIdStr.isBlank()) {
            long noteId;
            try {
                noteId = Long.parseLong(removeIdStr);
            } catch (NumberFormatException e) {
                view.printError(response, "Invalid note ID.");
                return;
            }
            note = NoteController.findNote(noteId);
            noteCont.storeNote(note);
            if (note == null) {
                view.printError(response, "Cannot find the specified note.");
                return;
            }

            remove(note);
            response.sendRedirect("/NoteManager/user");
            return;

        } else if (noteIdStr == null || noteIdStr.isBlank()) {
            note = noteCont.getNote();
            if (note == null) {
                view.printError(response, "No note specified.");
                return;
            }

        } else {
            long noteId;
            try {
                noteId = Long.parseLong(noteIdStr); 
            } catch (NumberFormatException e) {
                view.printError(response, "Invalid note ID.");
                return;
            }

            note = NoteController.findNote(noteId);
            if (note == null) {
                view.printError(response, "Cannot find the specified note.");
                return;
            }

            noteCont.storeNote(note);
        }

        if (noteCont.getNote().getUser().getId() != userCont.getUser().getId()) {
            view.printError(response, "This note does not belong to the current user.");
            return;
        }
        
        if (makeActions(request, response)) {
            request.getRequestDispatcher("note.jsp").forward(request, response);
        }
    }

    /**
     * Removes a note from the database and session.
     * Updates the current user in the database as well as the session.
     * @param note note object to remove.
     */
    private void remove(Note note) {
        User user = userCont.getUser();

        user.getNotes().remove(note);
        UserController.persist(user);
        userCont.storeUser(user);

        NoteController.remove(noteCont.getNote());
        noteCont.clearNote();
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

        String action = request.getParameter("save");
        if (action != null && !action.isBlank()) {
            String content = request.getParameter("content");
            if (content == null) {
                view.printError(response, "No note content provided.");
                return false;
            }

            AES aes = userCont.getAES();
            try {
                Note note = noteCont.modifyNote(aes, content);
                NoteController.persist(note);
                noteCont.storeNote(note);
                return true;
            } catch (InvalidCryptModeException | CryptException e) {
                e.printStackTrace();
                view.printError(response, "Problem saving the note.");
                return false;
            }
        }

        action = request.getParameter("remove");
        if (action != null && !action.isBlank()) {
            Note note = noteCont.getNote();
            remove(note);
            response.sendRedirect("/NoteManager/user");
            return true;
        }

        return true;
    }
}
