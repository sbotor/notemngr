package pl.polsl.lab.szymonbotor.notemanager.controller;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.util.Optional;

/**
 * TODO
 */
public class EntityController {

    /**
     * TODO
     */
    public static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("pl.polsl.lab.szymonbotor.notemanager");

    /**
     *
     */
    protected final EntityManager em = EMF.createEntityManager();

    /**
     * Persist an entity in the database.
     * @param entity entity to save to the database.
     * @return if the persistence was successful.
     */
    public boolean persist(Object entity) {
        em.getTransaction().begin();

        try {
            em.persist(entity);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }

        return true;
    }

    /**
     * Removes an entity from the database.
     * @param entity entity to be removed from the database.
     * @return if the removal was successful.
     */
    public boolean remove(Object entity) {
        em.getTransaction().begin();

        try {
            em.remove(entity);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }

        return true;
    }
}
