package pl.polsl.lab.szymonbotor.notemanager.controller;

import javax.persistence.*;

/**
 * Base entity controller class with basic methods for adding, removing and finding objects in the database.
 * @author Szymon Botor
 * @version 1.0
 */
public class EntityController {

    /**
     * Entity manager factory responsible for creating an Entity Manager.
     */
    protected static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("pl.polsl.lab.szymonbotor.notemanager");

    /**
     * Entity manager responsible for managing all entities.
     */
    protected static final EntityManager MANAGER = FACTORY.createEntityManager();

    /**
     * Persist an entity in the database.
     * @param entity entity to save to the database.
     * @return if the persistence was successful.
     */
    public static boolean persist(Object entity) {
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
    public static boolean remove(Object entity) {
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

    /**
     * Begins a new transaction if none is present.
     * @return a begun transaction or an existing one.
     */
    protected static EntityTransaction beginTransaction() {
        EntityTransaction transaction = MANAGER.getTransaction();
        
        if (transaction.isActive()) {
            return transaction;
        }

        transaction.begin();
        return transaction;
    }

    /**
     * Commits a transaction if one is active.
     */
    protected static void commitIfActive() {
        EntityTransaction transaction = MANAGER.getTransaction();

        if (transaction.isActive()) {
            transaction.commit();
        }
    }

    /**
     * Rolls back a transaction if one is active.
     */
    protected static void rollbackIfActive() {
        EntityTransaction transaction = MANAGER.getTransaction();

        if (transaction.isActive()) {
            transaction.rollback();
        }
    }

    /**
     * Gets the entity manager of the class.
     * @return the EntityManager object.
     */
    public static EntityManager getManager() {
        return MANAGER;
    }

    /**
     * Gets the entity manager factory of the class.
     * @return the EntityManagerFactory object.
     */
    public static EntityManagerFactory getFactory() {
        return FACTORY;
    }
}
