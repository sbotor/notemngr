package pl.polsl.lab.szymonbotor.notemanager.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.polsl.lab.szymonbotor.notemanager.controller.NoteController;
import pl.polsl.lab.szymonbotor.notemanager.entities.Note;

// TODO
@WebServlet(name="NoteServlet", urlPatterns = {"/note"})
public class NoteServlet extends UserServlet {

    // TODO
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

        Note note = noteCont.getNote();
        if (note == null) {
            String noteIdStr = request.getParameter("openId");
            int noteId;
            try {
                noteId = Integer.parseInt(noteIdStr);
                // TODO: check if the user owns the note
            } catch (NumberFormatException e) {
                view.printError(response, "Problem opening the note.");
                return;
            }

            note = NoteController.findNote(noteId);
            if (note == null) {
                view.printError(response, "Cannot find the specified note.");
                return;
            }

            noteCont.storeNote(note);
        }

        request.getRequestDispatcher("note.jsp").forward(request, response);
    }
}
