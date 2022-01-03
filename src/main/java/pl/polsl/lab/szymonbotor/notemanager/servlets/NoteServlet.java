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

@WebServlet(name = "NoteServlet", urlPatterns = { "/note" })
public class NoteServlet extends BaseNoteServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String noteDir = request.getParameter("note");
        if (noteDir == null) {
            noteDir = request.getParameter("noteName");
            if (noteDir == null) {
                printError(response, "No note specified.");
                return;
            }
        }

        noteDir = getNoteFile(noteDir).getAbsolutePath();
        if (noteDir == null) {
            printError(response, "Invalid note name.");
            return;
        }

        String pass = request.getParameter("pass");
        if (pass == null) {
            pass = (String)request.getParameter("pass1");
            if (pass == null) {
                printError(response, "No password provided.");
                return;
            }
        }

        Note note = null;
        if (request.getParameter("save") != null) {
            String noteContent = request.getParameter("content");
            if (noteContent != null) {
                try {
                    note = changeNote(request);
                } catch (InvalidCryptModeException | CryptException | NoteTooLongException e) {
                    printError(response, e.getMessage());
                    return;
                }
            } else {
                printError(response, "Note content is null.");
                return;
            }
            // TODO: do a redirect or something
            return;
        } else if (request.getParameter("remove") != null) {
            removeNote(noteDir);
            // TODO: do a redirect or something
            return;
        }

        try {
            if (note == null) {
                note = new Note(noteDir, pass);
                if (note.getContent() == null) {
                    printError(response, "Invalid password.");
                    return;
                }
            }
        } catch (InvalidCryptModeException | CryptException e) {
            printError(response, e.getMessage());
            return;
        }

        try (PrintWriter out = beginPage(response, noteDir)) {

            printNoteForm(out, note);
            endPage(out);
        }
    }

    protected void printNoteForm(PrintWriter out, Note note) {
        out.println("<form method=\"POST\">");
        out.println("<h3 class=\"mb-2\">" +
                note.getFile().getName() + "</h3>");

        out.println("<div class=\"mb-3>");
        out.println("<label for=\"content\">");
        out.print("<input type=\"text\" id=\"content\"" +
                "name=\"content\" class=\"form-control\" value=\"");
        out.println(note.getContent() + "\">");
        out.println("</div>");

        out.println("<button type=\"submit\" class=\"btn btn-success\" name=\"save\">" +
                "Save</button>");
        out.println("</form>");

        out.println("<form method=\"POST\" class=\"mt-5\">");
        out.println("<button type=\"submit\" class=\"btn btn-danger\" name=\"remove\">" +
                "Remove note</button>");
        out.println("</form>");
    }

    protected boolean removeNote(String name) {
        return new File(name).delete();
    }

    protected Note changeNote(HttpServletRequest request)
            throws IOException, InvalidCryptModeException, CryptException, NoteTooLongException {
        Note note = new Note(request.getParameter("note"), request.getParameter("pass"));

        note.change(request.getParameter("content"));
        note.overwrite();

        return note;
    }

}
