package pl.polsl.lab.szymonbotor.notemanager.servlets;

import java.io.File;

import pl.polsl.lab.szymonbotor.notemanager.model.Note;

/**
 * Abstract class serving as a base for all the servlets managing Notes.
 * @author Szymon Botor
 * @version 1.0
 */
public abstract class BaseNoteServlet extends BootstrapServlet {
    
    /**
     * Directory in which the note files are saved.
     */
    protected final File notesDir = new File("C:\\Users\\sotor\\OneDrive\\Documents\\repos\\NoteManager\\notes");

    /**
     * Returns the file associated with the specified note name.
     * If the name is an entire path only the filename is used.
     * @param name note name
     * @return File object pointing to the note.
     */
    protected File getNoteFile(String name) {
        
        String filename = new File(name).getName();

        if (filename == null || filename.length() == 0) {
            return null;
        }
        
        if (!filename.endsWith(Note.FILE_EXTENSION)) {
            filename = filename + Note.FILE_EXTENSION;
        }

        return new File(notesDir, filename);
    }
}
