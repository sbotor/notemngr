/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.lab.szymonbotor.notemanager.servlets;

import pl.polsl.lab.szymonbotor.notemanager.controller.UserController;
import pl.polsl.lab.szymonbotor.notemanager.entities.User;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.view.UserPageView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * TODO
 * @author sotor
 */
@WebServlet(name = "UserPageServlet", urlPatterns = {"/user"})
public class UserPageServlet extends HttpServlet {

    /**
     * View responsible for page rendering.
     */
    UserPageView view = null;

    /**
     * TODO
     */
    UserController userCont = null;

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

        view = new UserPageView(this);
        userCont = new UserController();
        HttpSession session = request.getSession();

        User user = userCont.fetchUser(session);
        if (user == null) {

            System.out.println("No user or AES in session" + request.getSession().getId());

            user = findUser(request, response);
            if (user == null) {
                return;
            }
        }

        view.printPage(response, user.getUsername(), user);
    }

    // TODO
    protected User findUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username"),
                password = request.getParameter("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            view.printError(response, "No username/password.");
            return null;
        }

        User user = userCont.findByUsername(username);
        if (user == null) {
            view.printError(response, "Cannot find the specified user.");
            return null;
        }

        try {
            if (user.authenticate(password)) {
                userCont.setUser(user);
                userCont.storeUserData(request.getSession(), password);
                return user;
            } else {
                view.printError(response, "Invalid password.");
            }
        } catch (CryptException e) {
            view.printError(response, "Problem authenticating the user.");
        }

        return null;
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
