package pl.polsl.lab.szymonbotor.notemanager.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Szymon Botor
 * @version 1.0
 */
@WebServlet(name = "OpenNoteServlet", urlPatterns = {"/open"})
public class OpenNoteServlet extends BaseNoteServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String noteDir = request.getParameter("note"), noteFilename = null;
        if (noteDir != null) {
            File noteFile = getNoteFile(noteDir);
            if (noteFile == null) {
                printError(response, "Invalid note name.");
            }
            if (noteFile.exists() == false) {
                printError(response, "Note \"" + noteFilename + "\" does not exist.");
            }
            noteDir = noteFile.getAbsolutePath();
        } else {
            printError(response, "Note name cannot be empty.");
        }

        String pass = request.getParameter("pass");
        if (pass == null) {
            try (PrintWriter out = beginPage(response, "Password needed")) {
                printPasswordForm(out);
                endPage(out);
            }
        } else {
            getServletContext().getRequestDispatcher("/note").forward(request, response);
        }
    }

    protected void printPasswordForm(PrintWriter out) throws IOException {
        String formFilename = getServletContext().getRealPath("/forms/password.html");
        
        try (Scanner scanner = new Scanner(new File(formFilename))) {
            while (scanner.hasNextLine()) {
                out.println(scanner.nextLine());
            }
        }
    }
}
