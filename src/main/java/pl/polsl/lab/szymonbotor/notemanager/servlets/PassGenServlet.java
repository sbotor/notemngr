package pl.polsl.lab.szymonbotor.notemanager.servlets;

import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidPasswordLengthException;
import pl.polsl.lab.szymonbotor.notemanager.model.PasswordGen;
import pl.polsl.lab.szymonbotor.notemanager.view.PassGenBootstrapView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet managing the password generation page.
 * @author Szymon Botor
 * @version 1.0
 */
@WebServlet(name="PassGenServlet", urlPatterns = {"/generate"})
public class PassGenServlet extends HttpServlet {

    /**
     * View responsible for page rendering.
     */
    PassGenBootstrapView view = null;

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

        view = new PassGenBootstrapView(this);

        String lenString = request.getParameter("length");
        if (lenString == null) {
            view.printForm(response);
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
            view.printForm(passGen.generate(), response);
        } catch (NumberFormatException | InvalidPasswordLengthException e) {
            view.printError(response, "Invalid password length.");
        }
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
