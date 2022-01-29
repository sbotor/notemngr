package pl.polsl.lab.szymonbotor.notemanager.controller;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import pl.polsl.lab.szymonbotor.notemanager.entities.Note;
import pl.polsl.lab.szymonbotor.notemanager.entities.User;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.CryptException;
import pl.polsl.lab.szymonbotor.notemanager.exceptions.InvalidCryptModeException;
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
     * Creates a new Note object not saving it to the database.
     * @param name note name.
     * @param aes AES object used to encrypt the note.
     * @param user User entity that the note should belong to.
     * @return newly created Note object.
     * @throws CryptException Thrown when a cryptographic exception occurs.
     * @throws InvalidCryptModeException When the AES object is decryption-only.
     */
    public static Note createNote(String name, User user, AES aes) throws InvalidCryptModeException, CryptException {

        Note newNote = new Note(name);
        newNote.setUser(user);
        newNote.setContent(Hash.bytesToString(aes.encrypt("")));
        newNote.setIV(Hash.bytesToString(aes.getIV()));

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

    /**
     * Removes the note attribute from the session if it exists.
     */
    public void clearNote() {
        if (fetchNote() != null) {
            session.removeAttribute(NOTE_ATTR);
        }
    }

    /**
     * Modifies the note according to the passed AES parameters.
     * Binds the note to this instance and returns it.
     * @param note note to modify and bind.
     * @param aes AES object representing the encryption parameters.
     * @param content new note content as a plain string.
     * @return modified note.
     * @throws InvalidCryptModeException Thrown when a decryption-only AES object is used.
     * @throws CryptException Thrown when a cryptographic exception occurs.
     */
    public Note modifyNote(Note note, AES aes, String content) throws InvalidCryptModeException, CryptException {
        aes.regenerateIv();
        note.setIV(Hash.bytesToString(aes.getIV()));

        note.setContent(Hash.bytesToString(aes.encrypt(content)));

        this.note = note;
        return this.note;
    }

    /**
     * Modifies the currently bound note according to the passed AES parameters.
     * @param aes AES object with encryption parameters.
     * @param content new note content as a plain string.
     * @return modified note.
     * @throws InvalidCryptModeException Thrown when a decryption-only AES object is used.
     * @throws CryptException Thrown when a cryptographic exception occurs.
     */
    public Note modifyNote(AES aes, String content) throws InvalidCryptModeException, CryptException {
        return modifyNote(this.note, aes, content);
    }

    /**
     * Decrypts the currently bound note and returns the content.
     * @param aes AES object with decryption parameters.
     * @return decrypted content of the note or null if there was a problem.
     */
    public String getDecryptedContent(AES aes) {
        
        byte[] iv = Hash.stringToBytes(note.getIV());
        try {
            AES newAes = aes.cloneWithIV(iv);
            return decrypt(newAes);
        } catch (CryptException | InvalidCryptModeException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convenience method used to decrypt a note.
     * @param aes AES object with decryption parameters.
     * @return decrypted note content.
     * @throws InvalidCryptModeException Thrown when an encryption-only AES object is used.
     * @throws CryptException Thrown when a cryptographic exception occurs.
     */
    private String decrypt(AES aes) throws InvalidCryptModeException, CryptException {
        byte[] bytes = Hash.stringToBytes(note.getContent());
        return aes.decrypt(bytes);
    }

    /**
     * Used to get a note saved in the bound session.
     * Uses attribute name specified in <code>NOTE_ATTR</code>.
     * @return Found note or null if no note was bound to the session.
     */
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
