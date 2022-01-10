package pl.polsl.lab.szymonbotor.notemanager.servlets;

import pl.polsl.lab.szymonbotor.notemanager.model.Note;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract class implementing methods utilizing Bootstrap 5.
 * @author Szymon Botor
 * @version 1.0
 */
public abstract class BaseNoteServlet extends HttpServlet {

    /**
     * Directory in which the note files are saved.
     */
    protected final File notesDir = new File("C:\\Users\\sotor\\OneDrive\\Documents\\repos\\NoteManager\\notes");

    /**
     * Returns the file associated with the specified note name.
     * If the name is an entire path only the filename is used.
     * @param name note name
     * @return File object pointing to the note.
     */
    protected File getNoteFile(String name) {

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
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods. Needs to be overriden in a subclass.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;

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
        return "Abstract Bootstrap servlet.";
    }
}
