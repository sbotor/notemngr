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

        try (PrintWriter out = beginPage(response, note.getName())) {
            printNoteForm(out, note);

            endPage(out);
        }
    }

    /**
     * Prints a whole page asking for a password.
     * @param response HttpResponse to write to.
     * @throws IOException Thrown when an IO error occurs.
     */
    protected void printPasswordForm(HttpServletResponse response) throws IOException {
        
        try (PrintWriter out = beginPage(response, "Password needed")) {
            printFromFile("/forms/password.html", out);
            endPage(out);
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

        if (request.getParameter("save") != null) {
            return changeNote(request, response);
        } else if (request.getParameter("remove") != null) {
            return removeNote(request, response);
        }

        String noteDir = request.getParameter("note");
        if (noteDir == null) {
            noteDir = (String) request.getAttribute("noteNameAttr");
            if (noteDir == null) {
                noteDir = request.getParameter("newNote");
                if (noteDir == null) {
                    printError(response, "No note specified.");
                    return null;
                }

                return create(request, response);
            }
        }
        File noteFile = getNoteFile(noteDir);

        if (!noteFile.exists()) {
            printError(response, "The note does not exist.");
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
                printError(response, "Invalid password.");
                return null;
            }
            
            return note;
        } catch (InvalidCryptModeException | CryptException e) {
            printError(response, e.getMessage());
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
            printError(response, "Invalid note name.");
            return null;
        }

        if (noteFile.exists()) {
            printError(response, "A note with that name already exists.");
            return null;
        }

        String pass1 = request.getParameter("pass1"), pass2 = request.getParameter("pass2");
        if (pass1 == null || pass2 == null) {
            printError(response, "Invalid password for note creation.");
            return null;
        }
        if (!pass1.equals(pass2)) {
            printError(response, "Passwords do not match.");
            return null;
        }

        try {
            Note note = new Note();
            note.save(noteFile.getAbsolutePath(), pass1);
            //printMessage(response, "Note created", note.getName(), "Note created successfuly.");
            return note;
        } catch (InvalidCryptModeException | CryptException e) {
            printError(response, e.getMessage());
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
            printError(response, "No password provided.");
            return null;
        }
        
        Note note = null;
        try {
            note = new Note(getNoteFile(request.getParameter("save")).getAbsolutePath(), pass);
            if (note.getContent() == null) {
                printError(response, "Invalid password.");
                return null;
            }

            note.change(request.getParameter("content"));
            note.overwrite();
            return note;
        } catch (IOException | InvalidCryptModeException | CryptException | NoteTooLongException e) {
            printError(response, e.getMessage());
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
            printError(response, "No password provided.");
        }

        System.out.print(pass);
        
        Note note = null;
        try {
            note = new Note(getNoteFile(request.getParameter("remove")).getAbsolutePath(), pass);
            if (note.getContent() == null) {
                printError(response, "Invalid password.");
            }

            if (note.getFile().delete()) {
                printMessage(response, "Note removed", note.getName(), "The note has been successfully removed.");
            } else {
                printError(response, "Problem deleting the note.");
            }
            return null;
        } catch (IOException | InvalidCryptModeException | CryptException e) {
            printError(response, e.getMessage());
        }

        return null;
    }

    /**
     * Method printing the main note form for modification and deletion.
     * @param out PrintWriter to write to.
     * @param note currently open note.
     */
    protected void printNoteForm(PrintWriter out, Note note) {
        out.println("<form method=\"POST\" class=\"col-6 m-3\">");
        out.println("<h3 class=\"mb-3\">" + note.getName() + "</h3>");

        out.println("<div class=\"mb-3\">");
        out.println("<textarea class=\"form-control\" id=\"content\" name=\"content\" rows=\"3\">");
        out.println(note.getContent() + "</textarea>");
        out.println("</div>");

        out.println(
                "<button type=\"button\" class=\"btn btn-success float-end mt-1\" data-bs-toggle=\"modal\" data-bs-target=\"#saveModal\">Save</button>");
        printPasswordModal(out, "save", note);

        out.println(
                "<button type=\"button\" class=\"btn btn-danger mt-5\" data-bs-toggle=\"modal\" data-bs-target=\"#removeModal\">Remove note</button>");
        printPasswordModal(out, "remove", note);

        out.println("</form>");

        out.println("<a href=\"/NoteManager\" class=\"btn btn-secondary m-5 col-auto\">Home</a>");
    }

    /**
     * Method used to print a password Bootstrap Modal during deletion or modification.
     * @param out PrintWriter to write to.
     * @param button button name specifing deletion (<i>remove</i>) or modification (<i>save</i>).
     * @param note currently open note.
     */
    protected void printPasswordModal(PrintWriter out, String button, Note note) {

        String modalId = "modal",
            buttonText = "Button",
            passName = "pass";
        if (button.equals("save")) {
            modalId = "saveModal";
            buttonText = "Save";
            passName = "savePass";
        } else if (button.equals("remove")) {
            modalId = "removeModal";
            buttonText = "Remove";
            passName = "removePass";
        }

        out.println("<div class=\"modal fade\" id=\"" + modalId + "\" tabindex=\"-1\"" +
                "aria-labelledby=\"" + modalId + "Label\" aria-hidden=\"true\">");
        out.println("<div class=\"modal-dialog\">");
        out.println("<div class=\"modal-content\">");
        out.println("<div class=\"modal-header\">");
        out.println("<h5 class=\"modal-title\" id=\"" + modalId + "Label\">Password needed</h5>");
        out.println(
                "<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"modal\" aria-label=\"Close\"></button></div>");

        out.println("<div class=\"modal-body\">");
        out.println("<div class=\"mb-3\">");
        out.println("<label for=\"" + passName + "\">Password</label>");
        out.println("<input type=\"password\" id=\"" + passName + "\" name=\"" + passName + "\" class=\"form-control\">");
        out.println("</div>");

        out.println("<div class=\"modal-footer\">");
        out.println("<button type=\"button\" class=\"btn btn-secondary\" data-bs-dismiss=\"modal\">Close</button>");
        out.println("<button type=\"submit\" class=\"btn btn-primary mt-1\" name=\"" + button + "\" id=\"" + button +
                "\" value=\"" + note.getName() + "\">" + buttonText + "</button>");

        out.println("</div></div></div></div></div>");

    }
}
