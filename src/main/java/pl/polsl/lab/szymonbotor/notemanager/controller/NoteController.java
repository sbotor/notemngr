package pl.polsl.lab.szymonbotor.notemanager.controller;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import pl.polsl.lab.szymonbotor.notemanager.entities.Note;
import pl.polsl.lab.szymonbotor.notemanager.entities.User;
import pl.polsl.lab.szymonbotor.notemanager.model.AES;
import pl.polsl.lab.szymonbotor.notemanager.model.Hash;

public class NoteController extends EntityController {

    /**
     * Name of the Note session attribute.
     */
    public static final String NOTE_ATTR = "note";

    /**
     * The HTTP session bound to the controller.
     */
    private HttpSession session;

    /**
     * The note that the controller operates on.
     */
    private Note note;

    /**
     * Constructor creating a controller and binding the specified session.
     * @param session HTTP session possibly containing user data.
     */
    public NoteController(HttpSession session) {
        this.session = session;
        this.note = fetchNote();
    }

    /**
     * TODO: possible changes
     * Creates a new Note object not saving it to the database.
     * @param name note name.
     * @param aes 
     * @param user
     * @return newly created Note object.
     */
    public static Note createNote(String name, AES aes, User user) {

        aes.regenerateIv();
        aes.regenerateSalt();

        Note newNote = new Note(name);
        newNote.setIV(Hash.bytesToString(aes.getIV()));
        newNote.setSalt(Hash.bytesToString(aes.getSalt()));
        newNote.setUser(user);

        return newNote;
    }

    /**
     * Finds the Note entity specified by the passed ID.
     * @param id ID of the note to find in the database.
     * @return the Note object if one was found, null otherwise.
     */
    public static Note findNote(long id) {
        beginTransaction();

        try {
            Note found = MANAGER.find(Note.class, id);
            if (found != null) {
                commitIfActive();
            } else {
                rollbackIfActive();
            }
            return found;
        } catch (PersistenceException e) {
            e.printStackTrace();
            rollbackIfActive();
            return null;
        }
    }

    /**
     * Stores the passed note as a session parameter. Binds the note to the current controller instance.
     * @param note the note to save.
     */
    public void storeNote(Note note) {
        session.setAttribute(NOTE_ATTR, note);
        session.setMaxInactiveInterval(UserController.MAX_INACTIVE_INTERVAL);

        this.note = note;
    }

    // TODO: rename
    /**
     * 
     */
    public void clearNote() {
        if (fetchNote() != null) {
            session.removeAttribute(NOTE_ATTR);
        }
    }

    // TODO
    public void persistNote(Note note) {
        // TODO
    }

    // TODO
    private Note fetchNote() {
        return (Note) session.getAttribute(NOTE_ATTR);
    }

    /**
     * Gets the currently bound HTTP session.
     * @return the current HTTP session.
     */
    public HttpSession getSession() {
        return session;
    }

    /**
     * Gets the currently bound Note object.
     * @return currently bound note.
     */
    public Note getNote() {
        return note;
    }
}
