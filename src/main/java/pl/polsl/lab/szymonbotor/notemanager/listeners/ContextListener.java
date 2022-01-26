package pl.polsl.lab.szymonbotor.notemanager.listeners;

import pl.polsl.lab.szymonbotor.notemanager.controller.EntityController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Main servlet context listener responsible for closing EntityManagers and their factories after server shutdown.
 */
@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        EntityManagerFactory emf = EntityController.getFactory();
        EntityManager em = EntityController.getManager();

        if (em.isOpen()) {
            em.close();
        }

        if (emf.isOpen()) {
            emf.close();
        }
    }
}
