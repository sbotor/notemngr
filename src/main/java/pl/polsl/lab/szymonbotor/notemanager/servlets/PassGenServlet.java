package pl.polsl.lab.szymonbotor.notemanager.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidPasswordLengthException;
import pl.polsl.lab.szymonbotor.notemanager.model.PasswordGen;

/**
 * Servlet managing the password generation page.
 * @author Szymon Botor
 * @version 1.0
 */
@WebServlet(name="PassGenServlet", urlPatterns = {"/generate"})
public class PassGenServlet extends BootstrapServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String lenString = request.getParameter("length");
        if (lenString == null) {
            printForm(response);
            return;
        }

        try {
            int passLength = Integer.parseInt(lenString);
            boolean useUpper = false, useDigits = false, useOther = false;
            
            if (request.getParameter("upper") != null) {
                useUpper = true;
            }
            if (request.getParameter("digits") != null) {
                useDigits = true;
            }
            if (request.getParameter("other") != null) {
                useOther = true;
            }

            PasswordGen passGen = new PasswordGen(passLength, useUpper, useDigits, useOther);
            printForm(passGen.generate(), response);
        } catch (NumberFormatException | InvalidPasswordLengthException e) {
            printError(response, "Invalid password length.");
        }
    }

    /**
     * Method printing the generator form.
     * @param generatedPass generated password. Null if none was generated.
     * @param response servlet response to write to.
     * @throws IOException Thrown when an IO error occurs.
     */
    protected void printForm(String generatedPass, HttpServletResponse response) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append("<input type=\"text\" disabled=\"true\" class=\"form-control\" id=\"generated\" name=\"generated\"");
        if (generatedPass != null) {
            stringBuilder.append(" value=\"" + generatedPass + "\" readonly=\"true\"");
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
    protected void printForm(HttpServletResponse response) throws IOException {
        printForm(null, response);
    }

}
