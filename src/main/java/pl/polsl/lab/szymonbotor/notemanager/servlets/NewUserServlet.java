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
public class NewUserServlet extends UserServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.processRequest(request, response);

        String username = request.getParameter("newUsername"),
                pass1 = request.getParameter("pass1"),
                pass2 = request.getParameter("pass2");

        if (username == null || username.isBlank() ||
                pass1 == null || pass1.isBlank() ||
                pass2 == null || pass2.isBlank()) {

            view.printError(response, "Invalid user form");
            return;
        }

        if (UserController.findByUsername(username) != null) {
            view.printError(response, "User already exists.");
            return;
        }

        if (!pass1.equals(pass2)) {
            view.printError(response, "Passwords do not match.");
            return;
        }

        if (UserController.createUser(username, pass1) != null) {
            view.printMessage(response, "New user created", username, "User created successfully.");
        } else {
            view.printError(response, "Could not create the user.");
        }
    }
}
