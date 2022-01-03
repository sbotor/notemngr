package pl.polsl.lab.szymonbotor.notemanager.servlets;

import java.io.File;

public abstract class BaseNoteServlet extends BootstrapServlet {
    
    protected final File notesDir = new File("C:\\Users\\sotor\\OneDrive\\Documents\\repos\\NoteManager\\notes");

    protected final File getNoteFile(String name) {
        return null;
    }
}
