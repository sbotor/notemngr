package pl.polsl.lab.szymonbotor.notemanager.view;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * View implementing Bootstrap 5 methods.
 * @author Szymon Botor
 * @version 2.0
 */
public class BootstrapView {

    /**
     * Content type of the HTTP response.
     */
    public static String CONTENT_TYPE = "text/html;charset=UTF-8";

    /**
     * Servlet serving as the view context.
     */
    private final HttpServlet servlet;

    /**
     * PrintWriter object bound to the view.
     */
    protected PrintWriter out = null;

    /**
     * Main constructor of the class.
     * @param servlet servlet serving as the view context.
     */
    public BootstrapView(HttpServlet servlet) {
        this.servlet = servlet;
    }

    /**
     * Method used to print the beginning of a page with the Bootstrap CSS attached.
     * @param response response to write to.
     * @param title page title.
     * @return PrintWriter object of the HttpResponse passed as a parameter.
     * @throws IOException Thrown when an IO error occurs.
     */
    public PrintWriter beginPage(HttpServletResponse response, String title) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        out = response.getWriter();

        try {
            out.println("<!doctype html>");
            out.println("<html lang=\"en\">");

            out.println("<head>");
            out.println("<meta charset=\"utf-8\">");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
            
            out.println(
                    "<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3\" crossorigin=\"anonymous\">");
            out.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css\">");
            
            out.println("<title>" + title + "</title>");
            out.println("</head>");

            out.println("<body>");
            return out;
        }
        catch (Exception e) {
            out.close();
            throw e;
        }
    }

    /**
     * Method printing the end of the page including Bootstrap JavaScript. It closes the bound PrintWriter object.
     */
    public void endPage() {
        try {
            out.println("<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p\" crossorigin=\"anonymous\"></script>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // TODO
    public void openDiv(String classStr) {
        out.println("<div class=\"" + classStr + "\">");
    }

    // TODO
    public void openDiv() {
        out.println("<div>");
    }

    // TODO
    public void closeDiv() {
        out.println("</div>");
    }

    // TODO
    public void println(String line) {
        out.println(line);
    }

    // TODO
    public void print(String str) {
       out.print(str);
    }

    // TODO
    public String getHomeButton() {
        return "<a href=\"/NoteManager\" class=\"col-auto btn btn-secondary mt-3 mb-3\">Home</a>";
    }

    // TODO
    public String getUserButton() {
        return "<a href=\"/NoteManager/user\" class=\"col-auto btn btn-secondary mt-3 mb-3\">My notes</a>";
    }

    /**
     * Convenience method used to print a whole page with a specified message.
     * @param response HttpResponse to write to.
     * @param title page title.
     * @param header message header.
     * @param message message content.
     * @throws IOException Thrown when an IO error occurs.
     */
    public void printMessage(HttpServletResponse response, String title, String header, String message) throws IOException {
        try (PrintWriter out = beginPage(response, title)) {
            openDiv("container");

            out.println("<h1 class=\"row\">" + header + "</h1>");
            openDiv("row mb-3");

            out.println(message);
            closeDiv();

            out.println("<a href=\"/NoteManager\" class=\"btn btn-secondary\">Home</a>");
            closeDiv();

            endPage();
        }
    }

    /**
     * Convenience method used to print a whole page with an error.
     * @param response HttpResponse to write to.
     * @param message error message.
     * @throws IOException Thrown when an IO error occurs.
     */
    public void printError(HttpServletResponse response, String message) throws IOException {
        printMessage(response, "Error", "Error", message);
    }

    /**
     * This method prints a portion of text specified by the filename to the PrintWriter passed as a parameter.
     * @param filename input filename.
     * @param out output PrintWriter object.
     * @throws FileNotFoundException Thrown when the input file cannot be found.
     */
    public void printFromFile(PrintWriter out, String filename) throws FileNotFoundException {
        String realPath = servlet.getServletContext().getRealPath(filename);

        try (Scanner scanner = new Scanner(new File(realPath))) {
            while (scanner.hasNextLine()) {
                out.println(scanner.nextLine());
            }
        }
    }

    /**
     * This method prints a portion of text specified by the filename to the bound PrintWriter.
     * @param filename input filename.
     * @throws FileNotFoundException Thrown when the input file cannot be found.
     */
    public void printFromFile(String filename) throws FileNotFoundException {
        printFromFile(out, filename);
    }

    /**
     * Sets the PrintWriter of the view.
     * @param out new PrintWriter to write into.
     */
    public void setWriter(PrintWriter out) {
        this.out = out;
    }
}
