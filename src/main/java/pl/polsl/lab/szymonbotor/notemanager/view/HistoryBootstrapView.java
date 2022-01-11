package pl.polsl.lab.szymonbotor.notemanager.view;

import pl.polsl.lab.szymonbotor.notemanager.controller.CookieHistoryController;
import pl.polsl.lab.szymonbotor.notemanager.model.Note;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Note history view with Bootstrap 5 methods.
 * @author Szymon Botor
 * @version 1.0
 */
public class HistoryBootstrapView extends BootstrapView {

    /**
     * Main constructor of the class.
     * @param servlet servlet serving as the view context.
     */
    public HistoryBootstrapView(HttpServlet servlet) {
        super(servlet);
    }

    /**
     * Prints the entire note history page.
     * @param history controller with the note history to print.
     * @throws IOException Thrown when an IO error occurs.
     */
    public void printHistory(CookieHistoryController history) throws IOException {
        try (PrintWriter out = beginPage(history.getResponse(), "Note history")) {
            out.println("<div class=\"container\" mt-3>");
            out.println("<div class=\"row\"><a href=\"/NoteManager\" class=\"btn btn-secondary col-auto mt-3\">Home</a></div>");
            out.println("<h3 class=\"row mt-3\">Recent notes</h3>");

            if (history.getNotes().size() > 0) {
                out.println("<form method=\"POST\">");

                ArrayList<File> noteFiles = history.getNotes();
                for (int i = 0; i < noteFiles.size(); i++) {
                    File noteFile = noteFiles.get(i);

                    out.println("<div class=\"row mb-2\">");

                    out.println("<div class=\"col-auto\">");
                    Note note = new Note();
                    note.setFile(noteFile);
                    out.println(note.getName());
                    out.println("</div>");

                    out.println("<div class=\"col-auto\">");
                    out.println("<a href=\"note?note=" + note.getName() + "\" class=\"btn btn-success\">Open</a>");
                    out.println("</div>");

                    out.println("<div class=\"col-auto\">");
                    printRemoveButtonAndModal(out, note.getName(), i);
                    out.println("</div>");

                    out.println("</div>");
                }

                out.println("</form>");
            } else {
                out.println("<h5 class=\"row\">No recent notes</h5>");
            }

            out.println("</div>");
            endPage(out);
        }
    }

    /**
     * Prints the remove button and appropriate modal for the specified note name.
     * @param out response writer.
     * @param noteName note name.
     * @param index note index in the history used to distinguish modals from one another in the list.
     */
    private void printRemoveButtonAndModal(PrintWriter out, String noteName, int index) {
        out.println(
                "<button type=\"button\" class=\"btn btn-danger\" data-bs-toggle=\"modal\" data-bs-target=\"#removeModal" +
                        index + "\">Remove</button>");

        NoteBootstrapView view = new NoteBootstrapView(servlet);
        view.printPasswordModal(out, "remove", noteName, String.valueOf(index));
    }
}
