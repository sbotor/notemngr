package pl.polsl.lab.szymonbotor.notemanager.servlets;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.model.Note;

@WebServlet(name="CreateNoteServlet", urlPatterns = {"/create"})
public class CreateNoteServlet extends BaseNoteServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String noteName = request.getParameter("noteName");
        if (noteName == null || noteName.length() == 0) {
            printError(response, "Note name cannot be empty.");
            return;
        }
    
        File noteFile = getNoteFile(noteName);
        if (noteFile == null) {
            printError(response, "Invalid note name.");
            return;
        }

        if (noteFile.exists()) {
            printError(response, "A note with that name already exists.");
            return;
        }

        String pass1 = request.getParameter("pass1"), pass2 = request.getParameter("pass2");
        if (pass1 != null && pass2 != null) {
            if (!pass1.equals(pass2)) {
                printError(response, "Passwords do not match");
                return;
            }
        } else {
            printError(response, "Password is null.");
            return;
        }

        Note note = new Note();
        try {
            note.save(noteFile.toString(), pass1);
            System.out.println(note.getFile().getAbsolutePath());
        } catch (InvalidCryptModeException | CryptException e) {
            printError(response, e.getMessage());
            return;
        }

        getServletContext().getRequestDispatcher("/note").forward(request, response);
    }
}
