/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.lab.szymonbotor.notemanager.servlets;

import pl.polsl.lab.szymonbotor.notemanager.controller.UserController;
import pl.polsl.lab.szymonbotor.notemanager.entities.User;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.view.BootstrapView;
import pl.polsl.lab.szymonbotor.notemanager.view.UserPageView;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sotor
 */
@WebServlet(name = "NewUserServlet", urlPatterns = {"/newUser"})
public class NewUserServlet extends HttpServlet {

    /**
     * TODO
     */
    UserController userCont = null;

    // TODO
    BootstrapView view = null;

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

        userCont = new UserController();
        view = new BootstrapView(this);

        String username = request.getParameter("newUsername"),
                pass1 = request.getParameter("pass1"),
                pass2 = request.getParameter("pass2");

        if (username == null || username.isBlank() ||
            pass1 == null || pass1.isBlank() ||
            pass2 == null || pass2.isBlank()) {

            view.printError(response, "Invalid user form");
            return;
        }

        if (userCont.findByUsername(username) != null) {
            view.printError(response, "User already exists.");
            return;
        }

        if (!pass1.equals(pass2)) {
            view.printError(response, "Passwords do not match.");
            return;
        }

        if (userCont.createUser(username, pass1) != null) {
            view.printMessage(response, "New user created", username, "User created successfully.");
        } else {
            view.printError(response, "Could not create the user.");
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
