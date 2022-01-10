package pl.polsl.lab.szymonbotor.notemanager.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidPasswordLengthException;
import pl.polsl.lab.szymonbotor.notemanager.model.PasswordGen;
import pl.polsl.lab.szymonbotor.notemanager.view.BootstrapView;

/**
 * Servlet managing the password generation page.
 * @author Szymon Botor
 * @version 1.0
 */
@WebServlet(name="PassGenServlet", urlPatterns = {"/generate"})
public class PassGenServlet extends HttpServlet {
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
            new BootstrapView(this).printError(response, "Invalid password length.");
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
            stringBuilder.append(" value=\"").append(generatedPass).append("\" readonly=\"true\"");
        } else {
            stringBuilder.append(" disabled=\"true\"");
        }
        stringBuilder.append(">");

        BootstrapView view = new BootstrapView(this);
        try (PrintWriter out = response.getWriter()) {
            response.setContentType("text/html;charset=UTF-8");

            view.printFromFile("/forms/generator.html", out);

            out.println("<div class=\"mb-3 row\">");

            out.println("<label for=\"generated\">Generated password</label>");
            out.println(stringBuilder.toString());

            out.println("</div></div>");

            view.endPage(out);
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

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
