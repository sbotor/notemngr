package pl.polsl.lab.szymonbotor.notemanager.view;

import pl.polsl.lab.szymonbotor.notemanager.entities.Note;
import pl.polsl.lab.szymonbotor.notemanager.entities.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Set;

public class UserPageView extends BootstrapView {

    /**
     * Main constructor of the class.
     * @param servlet servlet serving as the view context.
     */
    public UserPageView(HttpServlet servlet) {
        super(servlet);
    }

    /**
     * Method for printing the entire user page.
     * @param response servlet response.
     * @param title page title.
     * @param user User entity that the page should be based on.
     * @throws IOException Thrown when an IO error occurs.
     */
    public void printPage(HttpServletResponse response, String title, User user) throws IOException {
        try (PrintWriter writer = beginPage(response, title)) {

            openDiv("container");

            println(getHomeButton());
            println("<h3 class=\"row mb-5\">Hello, " + user.getUsername() + "</h3>");
            printNoteList(user.getNotes());

            println("<a href=\"removeUser\" class=\"row col-2 btn btn-danger mt-5\">Remove account</a>");

            closeDiv();
            endPage();
        }
    }

    /**
     * Prints the user notes.
     * @param notes a set of user notes.
     */
    private void printNoteList(Set<Note> notes) {

        openDiv("row mb-3");
        println("<h4 class=\"col-3 mb-3\">Your notes</h4>");
        println("<a href=\"/NoteManager/newNote\" class=\"btn btn-success col-auto\">Add new</a>");
        closeDiv();

        if (notes == null || notes.isEmpty()) {
            println("<h5>None</h5>");
            return;
        }

        Note[] sortedNotes = notes.stream()
                .sorted(Comparator.comparing(Note::getName)).
                toArray(Note[]::new);

        openDiv("container row");
        printSortedNotes(sortedNotes);
        closeDiv();
    }

    /**
     * Used to print the array of notes to the output.
     * @param sortedNotes array of notes that should be sorted.
     */
    private void printSortedNotes(Note[] sortedNotes) {
        println("<form method=\"POST\" action=\"note\">");
        println("<ul class=\"list-group row col-5\">");
        for (Note note : sortedNotes) {
            println("<li class=\"list-group-item row\">");
            println("<button type=\"submit\" class=\"btn btn-link col-7\" " +
                    "name=\"openId\" value=\"" + note.getId() + "\">" + note.getName() + "</button>");
            println("<button type=\"submit\" class=\"btn btn-outline-danger col-auto ms-1 float-end\" " +
                    "name=\"removeId\" value=\"" + note.getId() + "\">Remove</button>");
            println("</li>");

            println("</li>");
        }
        println("</ul>");
        println("</form>");
    }
}
