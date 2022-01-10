package pl.polsl.lab.szymonbotor.notemanager.view;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * TODO
 */
public class PassGenBootstrapView extends BootstrapView {

    /**
     * TODO
     * @param servlet
     */
    public PassGenBootstrapView(HttpServlet servlet) {
        super(servlet);
    }

    /**
     * Method printing the generator form.
     * @param generatedPass generated password. Null if none was generated.
     * @param response servlet response to write to.
     * @throws IOException Thrown when an IO error occurs.
     */
    public void printForm(String generatedPass, HttpServletResponse response) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<input type=\"text\" disabled=\"true\" class=\"form-control\" id=\"generated\" name=\"generated\"");
        if (generatedPass != null) {
            stringBuilder.append(" value=\"").append(generatedPass).append("\" readonly=\"true\"");
        } else {
            stringBuilder.append(" disabled=\"true\"");
        }
        stringBuilder.append(">");

        try (PrintWriter out = response.getWriter()) {
            response.setContentType("text/html;charset=UTF-8");

            printFromFile("/forms/generator.html", out);

            out.println("<div class=\"mb-3 row\">");

            out.println("<label for=\"generated\">Generated password</label>");
            out.println(stringBuilder.toString());

            out.println("</div></div>");

            endPage(out);
        }
    }

    /**
     * Method used to print a clean generation form with no generated password.
     * @param response servlet response to write to.
     * @throws IOException Thrown when an IO error occurs.
     */
    public void printForm(HttpServletResponse response) throws IOException {
        printForm(null, response);
    }

}
