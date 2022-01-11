package pl.polsl.lab.szymonbotor.notemanager.servlets;

import pl.polsl.lab.szymonbotor.notemanager.controller.CookieHistoryController;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.NoteTooLongException;
import pl.polsl.lab.szymonbotor.notemanager.model.Note;
import pl.polsl.lab.szymonbotor.notemanager.view.NoteBootstrapView;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet used to open, display and modify notes.
 * @author Szymon Botor
 * @version 1.0
 */
@WebServlet(name = "NoteServlet", urlPatterns = { "/note" })
public class NoteServlet extends HttpServlet {

    /**
     * View responsible for page rendering.
     */
    NoteBootstrapView view = null;

    /**
     * Directory in which the note files are saved.
     */
    protected static final File notesDir = new File("C:\\Users\\sotor\\OneDrive\\Documents\\repos\\NoteManager\\notes");

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        request.getParameterMap().keySet().forEach(attr -> {
//            System.out.println(attr + ", " + request.getParameterMap().get(attr)[0]);
//        });
//        System.out.println("----------");

        view = new NoteBootstrapView(this);

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

//        if (request.getCookies() != null) {
//            for (Cookie cookie : request.getCookies()) {
//                System.out.println(cookie.getName() + ", " + cookie.getValue());
//            }
//            System.out.println("-----------");
//        } else {
//            System.out.println("No cookies.");
//        }
//        Cookie cookie = new Cookie("test", java.time.LocalTime.now().toString());
//        cookie.setMaxAge(30 * 24 * 60 * 60);
//        cookie.setPath("/NoteManager/history");
//        response.addCookie(cookie);

        CookieHistoryController history = new CookieHistoryController(request, response);
        System.out.println(note.getName());
        history.addNote(note);

        try (PrintWriter out = view.beginPage(response, note.getName())) {
            view.printNoteForm(out, note);
            view.endPage(out);
        }
    }

    /**
     * Returns the file associated with the specified note name.
     * If the name is an entire path only the filename is used.
     * @param name note name
     * @return File object pointing to the note.
     */
    public static File getNoteFile(String name) {

        String filename = new File(name).getName();

        if (filename.length() == 0) {
            return null;
        }

        if (!filename.endsWith(Note.FILE_EXTENSION)) {
            filename = filename + Note.FILE_EXTENSION;
        }

        return new File(notesDir, filename);
    }

    /**
     * Used to get a Note object if none was passed as an Attribute. It deals with note creation, modification and deletion.
     * @param request servlet request.
     * @param response servlet response.
     * @return Note object if a note was found, null otherwise.
     * @throws IOException Thrown when an IO error occurs.
     */
    protected Note findNote(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String removePass = request.getParameter("removePass"),
                savePass = request.getParameter("savePass");
        if (removePass != null && !removePass.isBlank()) {
            return removeNote(request, response);
        } else if (savePass != null && !savePass.isBlank()) {
            return changeNote(request, response);
        }

        String noteDir = request.getParameter("note");
        if (noteDir == null) {
            noteDir = (String) request.getAttribute("noteNameAttr");
            if (noteDir == null) {
                noteDir = request.getParameter("newNote");
                if (noteDir == null) {
                    view.printError(response, "No note specified.");
                    return null;
                }

                return create(request, response);
            }
        }
        File noteFile = getNoteFile(noteDir);

        if (noteFile == null || !noteFile.exists()) {
            view.printError(response, "The note does not exist.");
            return null;
        }

        String pass = request.getParameter("pass");
        if (pass == null) {
            request.setAttribute("noteNameAttr", noteDir);
            view.printPasswordForm(response);
            return null;
        }

        try {
            Note note = new Note(noteFile.getAbsolutePath(), pass);
            if (note.getContent() == null) {
                view.printError(response, "Invalid password during note opening.");
                return null;
            }

            return note;
        } catch (InvalidCryptModeException | CryptException e) {
            view.printError(response, e.getMessage());
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
            view.printError(response, "Invalid note name.");
            return null;
        }

        if (noteFile.exists()) {
            view.printError(response, "A note with that name already exists.");
            return null;
        }

        String pass1 = request.getParameter("pass1"), pass2 = request.getParameter("pass2");
        if (pass1 == null || pass2 == null) {
            view.printError(response, "Invalid password for note creation.");
            return null;
        }
        if (!pass1.equals(pass2)) {
            view.printError(response, "Passwords do not match.");
            return null;
        }

        try {
            Note note = new Note();
            note.save(noteFile.getAbsolutePath(), pass1);
            //printMessage(response, "Note created", note.getName(), "Note created successfuly.");
            return note;
        } catch (InvalidCryptModeException | CryptException e) {
            view.printError(response, e.getMessage());
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
            view.printError(response, "No password provided.");
            return null;
        }

        Note note;
        try {
            note = new Note(getNoteFile(request.getParameter("note")).getAbsolutePath(), pass);
            if (note.getContent() == null) {
                view.printError(response, "Invalid password during note modification.");
                return null;
            }

            note.change(request.getParameter("content"));
            note.overwrite();
            return note;
        } catch (IOException | InvalidCryptModeException | CryptException | NoteTooLongException e) {
            view.printError(response, e.getMessage());
            return null;
        } catch (NullPointerException e) {
            view.printError(response, "A problem occurred during note modification.");
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
            view.printError(response, "No password provided.");
        }

        Note note;
        try {
            note = new Note(getNoteFile(request.getParameter("note")).getAbsolutePath(), pass);
            if (note.getContent() == null) {
                view.printError(response, "Invalid password during deletion.");
            }

            if (note.getFile().delete()) {
                CookieHistoryController history = new CookieHistoryController(request, response);
                history.removeNote(note);
                view.printMessage(response, "Note removed", note.getName(), "The note has been successfully removed.");
            } else {
                view.printError(response, "Problem deleting the note.");
            }
            return null;
        } catch (IOException | InvalidCryptModeException | CryptException e) {
            view.printError(response, e.getMessage());
        } catch (NullPointerException e) {
            view.printError(response, "A problem occurred during note modification.");
            return null;
        }

        return null;
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet used to open, display and modify notes.";
    }
}
