package pl.polsl.lab.szymonbotor.notemanager.controller;

import javax.persistence.*;

/**
 * TODO
 */
public class EntityController {

    /**
     * TODO
     */
    public static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("pl.polsl.lab.szymonbotor.notemanager");

    /**
     *
     */
    public static final EntityManager MANAGER = FACTORY.createEntityManager();

    /**
     * Persist an entity in the database.
     * @param entity entity to save to the database.
     * @return if the persistence was successful.
     */
    public boolean persist(Object entity) {
        beginTransaction();

        try {
            MANAGER.persist(entity);
            commitIfActive();
        } catch (PersistenceException e) {
            e.printStackTrace();
            rollbackIfActive();
            return false;
        }

        return true;
    }

    /**
     * Removes an entity from the database.
     * @param entity entity to be removed from the database.
     * @return if the removal was successful.
     */
    public boolean remove(Object entity) {
        beginTransaction();

        try {
            MANAGER.remove(entity);
            commitIfActive();
        } catch (PersistenceException e) {
            e.printStackTrace();
            rollbackIfActive();
            return false;
        }

        return true;
    }

    // TODO
    protected EntityTransaction beginTransaction() {
        EntityTransaction transaction = MANAGER.getTransaction();
        transaction.begin();
        return transaction;
    }

    // TODO
    protected void commitIfActive() {
        EntityTransaction transaction = MANAGER.getTransaction();

        if (transaction.isActive()) {
            transaction.commit();
        }
    }

    // TODO
    protected void rollbackIfActive() {
        EntityTransaction transaction = MANAGER.getTransaction();

        if (transaction.isActive()) {
            transaction.rollback();
        }
    }
}
