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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO
 * @author sotor
 */
@WebServlet(name = "NewNoteServlet", urlPatterns = {"/newNote"})
public class NewNoteServlet extends UserServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.processRequest(request, response);

        User user = userCont.getUser();
        AES aes = userCont.getAES();

        if (user == null || aes == null) {

            userCont.clearUserData();
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
        
        Note newNote = NoteController.createNote(newName, aes, user);

        notes.add(newNote);
        UserController.persist(user);
        userCont.storeUser(user);

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

            view.printFromFile("forms/newNoteName.html");
            view.endPage();
        }
    }
}
