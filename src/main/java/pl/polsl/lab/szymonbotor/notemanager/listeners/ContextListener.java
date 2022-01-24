package pl.polsl.lab.szymonbotor.notemanager.listeners;

import pl.polsl.lab.szymonbotor.notemanager.controller.EntityController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * TODO
 */
@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        EntityManagerFactory emf = EntityController.FACTORY;
        EntityManager em = EntityController.MANAGER;

        if (em.isOpen()) {
            em.close();
        }

        if (emf.isOpen()) {
            emf.close();
        }
    }
}
