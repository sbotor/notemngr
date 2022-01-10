package pl.polsl.lab.szymonbotor.notemanager.view;

import pl.polsl.lab.szymonbotor.notemanager.controller.SessionHistoryController;
import pl.polsl.lab.szymonbotor.notemanager.model.Note;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class HistoryBootstrapView extends BootstrapView {

    /**
     * TODO
     * @param servlet
     */
    public HistoryBootstrapView(HttpServlet servlet) {
        super(servlet);
    }

    /**
     * TODO
     * @param history
     * @throws IOException
     */
    public void printHistory(SessionHistoryController history) throws IOException {
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
                    printOpenButton(out, note.getName());
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
     * TODO
     * @param out
     * @param noteName
     * @param index
     */
    private void printRemoveButtonAndModal(PrintWriter out, String noteName, int index) {
        out.println(
                "<button type=\"button\" class=\"btn btn-danger\" data-bs-toggle=\"modal\" data-bs-target=\"#removeModal" +
                        index + "\">Remove</button>");

        NoteBootstrapView view = new NoteBootstrapView(servlet);
        view.printPasswordModal(out, "remove", noteName, String.valueOf(index));
    }

    /**
     * TODO
     * @param out
     * @param noteName
     */
    private void printOpenButton(PrintWriter out, String noteName) {
        out.println("<a href=\"note?note=" + noteName + "\" class=\"btn btn-success\">Open</a>");
    }
}
