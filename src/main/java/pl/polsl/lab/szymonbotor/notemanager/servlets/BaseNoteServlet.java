package pl.polsl.lab.szymonbotor.notemanager.servlets;

import java.io.File;

import pl.polsl.lab.szymonbotor.notemanager.model.Note;

public abstract class BaseNoteServlet extends BootstrapServlet {
    
    protected final File notesDir = new File("C:\\Users\\sotor\\OneDrive\\Documents\\repos\\NoteManager\\notes");

    protected final File getNoteFile(String name) {
        
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
