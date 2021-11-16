package pl.polsl.lab.szymonbotor.notemanager.model;

import java.io.*;
import java.util.Vector;

// TODO: comments and javadoc

/**
 * Class for storing and managing note history.
 * @author Szymon Botor
 * @version 1.0
 */
public class NoteHistory {
    private static final int MAX_ITEMS = 10;

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    private String fileDir;

    public Vector<String> getNotes() {
        return notes;
    }

    private Vector<String> notes;

    public NoteHistory(String file) throws IOException {
        fileDir = file;
        notes = new Vector<String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileDir));
            String line = reader.readLine();

            while (line != null && notes.size() < MAX_ITEMS) {
                notes.add(line);
                line = reader.readLine();
            }
            reader.close();
        }
        catch (FileNotFoundException ex) {
            new File(fileDir).createNewFile();
        }
    }

    public void add(Note note) {
        if (notes.size() == MAX_ITEMS) {
            notes.remove(notes.size() - 1);
        }

        if (note.getFileDir() != null) {
            notes.add(0, note.getFileDir() + ".note");
        } else {
            // TODO: maybe a new exception???
        }
    }

    public void save() throws IOException{
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileDir, false));

            for (String noteDir : notes) {
                writer.write(noteDir + "\n");
            }
            writer.close();
        }
        catch (FileNotFoundException ex) {
            new File(fileDir).createNewFile();
        }
    }
}
