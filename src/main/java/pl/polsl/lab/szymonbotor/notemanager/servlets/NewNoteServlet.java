/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.lab.szymonbotor.notemanager.servlets;

import pl.polsl.lab.szymonbotor.notemanager.controller.NoteController;
import pl.polsl.lab.szymonbotor.notemanager.controller.UserController;
import pl.polsl.lab.szymonbotor.notemanager.entities.Note;
import pl.polsl.lab.szymonbotor.notemanager.entities.User;
import pl.polsl.lab.szymonbotor.notemanager.model.AES;
import pl.polsl.lab.szymonbotor.notemanager.model.Hash;
import pl.polsl.lab.szymonbotor.notemanager.view.BootstrapView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * TODO
 * @author sotor
 */
@WebServlet(name = "NewNoteServlet", urlPatterns = {"/newNote"})
public class NewNoteServlet extends HttpServlet {

    /**
     * View responsible for page rendering.
     */
    BootstrapView view = null;

    /**
     * TODO
     */
    UserController userCont = null;

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

        view = new BootstrapView(this);
        userCont = new UserController();
        HttpSession session = request.getSession();

        User user = userCont.fetchUser(session);
        AES aes = UserController.fetchAES(session);

        if (user == null || aes == null) {
            view.printError(response, "The session has expired. Log in again.");
            return;
        }

        String newName = request.getParameter("newName");
        if (newName == null || newName.isBlank()) {
            printNameForm(request, response);
            return;
        }

        Set<Note> notes = user.getNotes();
        if (notes == null || notes.isEmpty()) {
            notes = new HashSet<Note>();
            user.setNotes(notes);
        }

        NoteController noteCont = new NoteController();
        Note newNote = noteCont.createNote(newName, aes);
        notes.add(newNote);

        userCont.persist(user);
        userCont.storeUser(session);

        try (PrintWriter out = view.beginPage(response, "Note created")) {

            view.openDiv("container");
            view.println("<h4 class=\"col-auto\">Note created</h4>");
            view.println(view.getUserButton());

            view.endPage();
        }
    }

    // TODO
    private void printNameForm(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try (PrintWriter out = view.beginPage(response, "New note")) {

            view.openDiv("container");

            // TODO
            view.println("<form method=\"POST\">");
            view.println("<label for=\"newName\"");

            view.println("</form>");
            view.openDiv();
            view.endPage();
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
        return "Short description";
    }

}
