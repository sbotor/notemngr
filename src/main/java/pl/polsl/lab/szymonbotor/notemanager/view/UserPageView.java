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

    // TODO
    public void printPage(HttpServletResponse response, String title, User user) throws IOException {
        try (PrintWriter writer = beginPage(response, title)) {

            openDiv("container");

            println(getHomeButton());
            println("<h3 class=\"row mb-5\">Hello, " + user.getUsername() + "</h3>");
            printNoteList(user.getNotes());

            println("<form method=\"POST\" action=\"removeUser\"");
            println("<button type=\"submit\" class=\"row col-2 btn btn-danger mt-5 disabled\" name=\"removeId\" value=\"" +
                    user.getId() + "\" disabled>Remove account</button>");
            println("</form>");

            closeDiv();
            endPage();
        }
    }

    // TODO
    private void printNoteList(Set<Note> notes) {

        openDiv("row");
        println("<h4 class=\"col-3 mb-3\">Your notes</h4>");
        println("<a href=\"note\" class=\"btn btn-success col-auto disabled\">Add new</a>");
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

    // TODO
    private void printSortedNotes(Note[] sortedNotes) {
        println("<form method=\"POST\" action=\"note\">");
        println("<ul class=\"list-group\">");
        for (Note note : sortedNotes) {
            println("<li class=\"list-group-item container col-8\">");

            println("<p class=\"col-5\">" + note.getName() + "</p>");
            println("<button type=\"submit\" class=\"btn btn-primary col-1 disabled\" " +
                    "name=\"openId\" value=\"" + note.getId() + "\" disabled>Open</button>");
            println("<button type=\"submit\" class=\"btn btn-danger col-1 disabled\" " +
                    "name=\"removeId\" value=\"" + note.getId() + "\" disabled>Remove</button>");

            println("</li>");
        }
        println("</ul>");
        println("</form>");
    }
}
