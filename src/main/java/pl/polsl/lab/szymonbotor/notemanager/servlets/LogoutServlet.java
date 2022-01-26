/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.lab.szymonbotor.notemanager.servlets;

import pl.polsl.lab.szymonbotor.notemanager.entities.User;
import pl.polsl.lab.szymonbotor.notemanager.model.AES;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet responsible for logging out the current user.
 * @author Szymon Botor
 * @version 1.0
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends UserServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.processRequest(request, response);

        User user = userCont.getUser();
        AES aes = userCont.getAES();

        if (user != null || aes != null) {
            userCont.clearUserData();
        }

        response.sendRedirect("/NoteManager");
    }
}
