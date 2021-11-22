package pl.polsl.lab.szymonbotor.notemanager.model;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

/**
 * Class for storing and managing note history.
 * @author Szymon Botor
 * @version 1.0
 */
public class NoteHistory {
    /**
     * Maximum number of items in the note history.
     */
    private static final int MAX_ITEMS = 10;

    /**
     * This method is used to get the directory of the history file.
     * @return Directory of the history file.
     */
    public String getFileDir() {
        return fileDir.toString();
    }

    /**
     * This method is used to get the Path object of the
     * history file.
     * @return Path object of the history file.
     */
    public Path getFilePath() {
        return fileDir;
    }

    /**
     * This method is used to set the directory of the history file.
     * @param fileDir New directory of the history file.
     */
    public void setFileDir(String fileDir) {
        this.fileDir = Paths.get(fileDir);
    }

    /**
     * Path to the file that the history is stored in.
     */
    private Path fileDir;

    /**
     * This method is used to get the collection of notes in the history.
     * @return Collection with the saved notes.
     */
    public Vector<String> getNotes() {
        return notes;
    }

    /**
     * This is the collection of saved notes.
     */
    private Vector<String> notes;

    /**
     * Constructor of the NoteHistory class. It reads file directories from a text file
     * from the most recent one and stores them in the same order. Each directory should be in
     * a separate line. The file is created if it does not exist.
     * @param file Directory to the history file.
     * @throws IOException Thrown when a problem occurs during file creation.
     */
    public NoteHistory(String file) throws IOException {
        fileDir = Paths.get(file);
        notes = new Vector<String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileDir.toString()));
            String line = reader.readLine();

            while (line != null && notes.size() < MAX_ITEMS) {
                notes.add(line);
                line = reader.readLine();
            }
            reader.close();
        }
        catch (FileNotFoundException ex) {
            new File(fileDir.toString()).createNewFile();
        }
    }

    /**
     * This method is used to get a note directory from the note collection.
     * @param index index in the note collection.
     * @return directory to the note at the provided index.
     */
    public String get(int index) {
        return notes.get(index);
    }

    /**
     * This method is used to add a note to the history. If the current note count is
     * equal to the max allowed count the oldest note is removed.
     * @param note Note to be added to the history.
     * @throws IllegalArgumentException Thrown when the note's file directory is empty.
     */
    public void add(Note note) throws IllegalArgumentException {

        if (note.getFilePath() != null && !"".equals(note.getFileDir())) {
            int elementIndx = notes.indexOf(note.getFileDir());
            if (elementIndx != -1) {
                notes.remove(elementIndx);
            }

            notes.add(0, note.getFileDir());
            if (notes.size() > MAX_ITEMS) {
                notes.remove(notes.size() - 1);
            }
        } else {
            throw new IllegalArgumentException("The note file dir is empty.");
        }
    }

    /**
     * This method is used to save the history to the history file.
     * The file is created if it does not exist.
     * @throws IOException Thrown when a problem occurs during file creation.
     */
    public void save() throws IOException{
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileDir.toString(), false));

            for (String noteDir : notes) {
                writer.write(noteDir + "\n");
            }
            writer.close();
        }
        catch (FileNotFoundException ex) {
            new File(fileDir.toString()).createNewFile();
        }
    }
}
