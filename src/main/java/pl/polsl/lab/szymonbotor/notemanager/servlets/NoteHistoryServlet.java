/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.lab.szymonbotor.notemanager.servlets;

import pl.polsl.lab.szymonbotor.notemanager.controller.CookieHistoryController;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
import pl.polsl.lab.szymonbotor.notemanager.model.Note;
import pl.polsl.lab.szymonbotor.notemanager.view.HistoryBootstrapView;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet controlling a cookie-based note history list.
 * @author Szymon Botor
 * @version 1.0
 */
@WebServlet(name = "NoteHistoryServlet", urlPatterns = {"/history"})
public class NoteHistoryServlet extends HttpServlet {

    /**
     * View responsible for page rendering.
     */
    HistoryBootstrapView view = null;

    /**
     * Cookie controller responsible for the note history.
     */
    CookieHistoryController history = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        view = new HistoryBootstrapView(this);
        history = new CookieHistoryController(request, response);

        String[] removePasswords = request.getParameterValues("removePass");
        String removePass = null;
        if (removePasswords != null) {
            for (String pass : removePasswords) {
                if (pass != null && !pass.isBlank()) {
                    removePass = pass;
                    break;
                }
            }
        }

        //System.out.println(removePass);

        if (removePass != null && !removePass.isBlank()) {
            removeNote(request, response, removePass);
            response.sendRedirect("NoteManager/history");
            return;
        }

        view.printHistory(history);
    }

    /**
     * Removes a note from the server and updates cookies.
     * @param request servlet request.
     * @param response servlet response.
     * @param pass note password.
     * @throws IOException Thrown when an IO error occurs.
     */
    protected void removeNote(HttpServletRequest request, HttpServletResponse response, String pass) throws IOException {
        String noteName = request.getParameter("note");

        // System.out.println(noteName + ", " + pass);

        File noteFile = NoteServlet.getNoteFile(noteName);
        if (noteFile != null && noteFile.exists()) {
            try {
                Note note = new Note(noteFile.getAbsolutePath(), pass);
                if (note.getContent() == null) {
                    view.printError(response, "Invalid password.");
                } else {
                    noteFile.delete();
                    history.removeNote(note);
                    view.printMessage(response, "Note deleted", note.getName(), "Note deleted.");
                }
            } catch (InvalidCryptModeException | CryptException e) {
                view.printError(response, e.getMessage());
            }
        } else {
            view.printError(response, "The note does not exist.");
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
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
        return "Note history servlet.";
    }

}
