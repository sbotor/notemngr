package pl.polsl.lab.szymonbotor.notemanager.view;

import pl.polsl.lab.szymonbotor.notemanager.model.Note;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * View for printing the note modification page.
 * @author Szymon Botor
 * @version 1.0
 */
public class NoteBootstrapView extends BootstrapView {

    /**
     * Main constructor of the class.
     * @param servlet servlet serving as the view context.
     */
    public NoteBootstrapView(HttpServlet servlet) {
        super(servlet);
    }

    /**
     * Method printing the main note form for modification and deletion.
     * @param out PrintWriter to write to.
     * @param note currently open note.
     */
    public void printNoteForm(PrintWriter out, Note note) {
        out.println("<form method=\"POST\" class=\"col-6 m-3\">");
        out.println("<h3 class=\"mb-3\">" + note.getName() + "</h3>");

        out.println("<div class=\"mb-3\">");
        out.println("<textarea class=\"form-control\" id=\"content\" name=\"content\" rows=\"3\">");
        out.println(note.getContent() + "</textarea>");
        out.println("</div>");

        out.println(
                "<button type=\"button\" class=\"btn btn-success float-end mt-1\" data-bs-toggle=\"modal\" data-bs-target=\"#saveModal\">Save</button>");
        this.printPasswordModal(out, "save", note.getName());

        out.println(
                "<button type=\"button\" class=\"btn btn-danger mt-5\" data-bs-toggle=\"modal\" data-bs-target=\"#removeModal\">Remove note</button>");
        this.printPasswordModal(out, "remove", note.getName());

        out.println("</form>");

        out.println("<a href=\"/NoteManager\" class=\"btn btn-secondary m-5 col-auto\">Home</a>");
    }

    /**
     * Method used to print a password Bootstrap Modal during deletion or modification. It appends the num parameter
     * to the modal name so that multiple modals can be used.
     * @param out PrintWriter to write to.
     * @param button button name specifying deletion (<i>remove</i>) or modification (<i>save</i>).
     * @param noteName currently open note name.
     * @param num item number if multiple modals are used.
     */
    public void printPasswordModal(PrintWriter out, String button, String noteName, String num) {

        String modalId = "modal",
                buttonText = "Button",
                passName = "pass";
        if (button.equals("save")) {
            modalId = "saveModal" + num;
            buttonText = "Save";
            passName = "savePass";
        } else if (button.equals("remove")) {
            modalId = "removeModal" + num;
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
        out.println("<button type=\"submit\" class=\"btn btn-primary mt-1\" name=\"note\" id=\"" + button +
                "\" value=\"" + noteName + "\">" + buttonText + "</button>");

        out.println("</div></div></div></div></div>");
    }

    /**
     * Method used to print a password Bootstrap Modal during deletion or modification.
     * @param out PrintWriter to write to.
     * @param button button name specifying deletion (<i>remove</i>) or modification (<i>save</i>).
     * @param noteName currently open note name.
     */
    public void printPasswordModal(PrintWriter out, String button, String noteName) {

        printPasswordModal(out, button, noteName, "");
    }

    /**
     * Prints a whole page asking for a password.
     * @param response HttpResponse to write to.
     * @throws IOException Thrown when an IO error occurs.
     */
    public void printPasswordForm(HttpServletResponse response) throws IOException {

        try (PrintWriter out = beginPage(response, "Password needed")) {
            printFromFile(out, "/forms/password.html");
            endPage();
        }
    }

}
