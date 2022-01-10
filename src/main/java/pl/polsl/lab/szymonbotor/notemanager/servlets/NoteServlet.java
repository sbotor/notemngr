package pl.polsl.lab.szymonbotor.notemanager.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.NoteTooLongException;
import pl.polsl.lab.szymonbotor.notemanager.model.Note;
import pl.polsl.lab.szymonbotor.notemanager.view.BootstrapView;

/**
 * Servlet used to open, display and modify notes.
 * @author Szymon Botor
 * @version 1.0
 */
@WebServlet(name = "NoteServlet", urlPatterns = { "/note" })
public class NoteServlet extends BaseNoteServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        request.getParameterMap().keySet().forEach(attr -> {
//            System.out.println(attr + ", " + request.getParameterMap().get(attr)[0]);
//        });
//        System.out.println("----------");

        Note note = (Note) request.getAttribute("noteAttr");
        if (note == null) {
            note = findNote(request, response);
            if (note == null || !note.hasFile()) {
                return;
            }

            request.setAttribute("noteAttr", note);
            request.getRequestDispatcher("/note").forward(request, response);
            return;
        }

        BootstrapView view = new BootstrapView(this);
        try (PrintWriter out = view.beginPage(response, note.getName())) {
            view.printNoteForm(out, note);

            view.endPage(out);
        }
    }

    /**
     * Prints a whole page asking for a password.
     * @param response HttpResponse to write to.
     * @throws IOException Thrown when an IO error occurs.
     */
    protected void printPasswordForm(HttpServletResponse response) throws IOException {

        BootstrapView view = new BootstrapView(this);
        try (PrintWriter out = view.beginPage(response, "Password needed")) {
            view.printFromFile("/forms/password.html", out);
            view.endPage(out);
        }
    }

    /**
     * Used to get a Note object if none was passed as an Attribute. It deals with note creation, modification and deletion.
     * @param request servlet request.
     * @param response servlet response.
     * @return Note object if a note was found, null otherwise.
     * @throws IOException Thrown when an IO error occurs.
     */
    protected Note findNote(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (request.getParameter("remove") != null && request.getParameter("removePass") != null) {
            return removeNote(request, response);
        } else if (request.getParameter("save") != null && request.getParameter("savePass") != null) {
            return changeNote(request, response);
        }

        String noteDir = request.getParameter("note");
        if (noteDir == null) {
            noteDir = (String) request.getAttribute("noteNameAttr");
            if (noteDir == null) {
                noteDir = request.getParameter("newNote");
                if (noteDir == null) {
                    new BootstrapView(this).printError(response, "No note specified.");
                    return null;
                }

                return create(request, response);
            }
        }
        File noteFile = getNoteFile(noteDir);

        if (!noteFile.exists()) {
            new BootstrapView(this).printError(response, "The note does not exist.");
            return null;
        }

        String pass = request.getParameter("pass");
        if (pass == null) {
            request.setAttribute("noteNameAttr", noteDir);
            printPasswordForm(response);
            return null;
        }

        try {
            Note note = new Note(noteFile.getAbsolutePath(), pass);
            if (note.getContent() == null) {
                new BootstrapView(this).printError(response, "Invalid password during note opening.");
                return null;
            }
            
            return note;
        } catch (InvalidCryptModeException | CryptException e) {
            new BootstrapView(this).printError(response, e.getMessage());
            return null;
        }
    }

    /**
     * Method used to create a new note according to the parameters in the request. Needs a password with confirmation.
     * @param request servlet request.
     * @param response servlet response.
     * @return newly created note if successful, null otherwise.
     * @throws IOException Thrown when an IO error occurs.
     */
    protected Note create(HttpServletRequest request, HttpServletResponse response) throws IOException {

        File noteFile = getNoteFile(request.getParameter("newNote"));
        if (noteFile == null) {
            new BootstrapView(this).printError(response, "Invalid note name.");
            return null;
        }

        if (noteFile.exists()) {
            new BootstrapView(this).printError(response, "A note with that name already exists.");
            return null;
        }

        String pass1 = request.getParameter("pass1"), pass2 = request.getParameter("pass2");
        if (pass1 == null || pass2 == null) {
            new BootstrapView(this).printError(response, "Invalid password for note creation.");
            return null;
        }
        if (!pass1.equals(pass2)) {
            new BootstrapView(this).printError(response, "Passwords do not match.");
            return null;
        }

        try {
            Note note = new Note();
            note.save(noteFile.getAbsolutePath(), pass1);
            //printMessage(response, "Note created", note.getName(), "Note created successfuly.");
            return note;
        } catch (InvalidCryptModeException | CryptException e) {
            new BootstrapView(this).printError(response, e.getMessage());
            return null;
        }
    }

    /**
     * Method used to change a note according to the parameters in the request. Needs a password.
     * @param request servlet request.
     * @param response servlet response.
     * @return changed Note if successful, null otherwise.
     * @throws IOException Thrown when an IO error occurs.
     */
    protected Note changeNote(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pass = request.getParameter("savePass");
        if (pass == null) {
            new BootstrapView(this).printError(response, "No password provided.");
            return null;
        }
        
        Note note = null;
        try {
            note = new Note(getNoteFile(request.getParameter("save")).getAbsolutePath(), pass);
            if (note.getContent() == null) {
                new BootstrapView(this).printError(response, "Invalid password during note modification.");
                return null;
            }

            note.change(request.getParameter("content"));
            note.overwrite();
            return note;
        } catch (IOException | InvalidCryptModeException | CryptException | NoteTooLongException e) {
            new BootstrapView(this).printError(response, e.getMessage());
            return null;
        }
    }

    /**
     * Method used to remove a note specified by the request. Needs a password.
     * @param request servlet request.
     * @param response servlet response.
     * @return newly created note if successful, null otherwise.
     * @throws IOException Thrown when an IO error occurs.
     */
    protected Note removeNote(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pass = request.getParameter("removePass");
        if (pass == null) {
            new BootstrapView(this).printError(response, "No password provided.");
        }
        
        Note note = null;
        try {
            note = new Note(getNoteFile(request.getParameter("remove")).getAbsolutePath(), pass);
            if (note.getContent() == null) {
                new BootstrapView(this).printError(response, "Invalid password during deletion.");
            }

            if (note.getFile().delete()) {
                new BootstrapView(this).printMessage(response, "Note removed", note.getName(), "The note has been successfully removed.");
            } else {
                new BootstrapView(this).printError(response, "Problem deleting the note.");
            }
            return null;
        } catch (IOException | InvalidCryptModeException | CryptException e) {
            new BootstrapView(this).printError(response, e.getMessage());
        }

        return null;
    }
}
