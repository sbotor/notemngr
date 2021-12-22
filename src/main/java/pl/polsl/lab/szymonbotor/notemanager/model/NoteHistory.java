package pl.polsl.lab.szymonbotor.notemanager.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Class for storing and managing note history.
 * @author Szymon Botor
 * @version 2.0
 */
public class NoteHistory {
    /**
     * Maximum number of items in the note history. The actual limit of a NoteHistory object can be lower, but never higher.
     */
    public static final int MAX_ITEMS = 64;

    /**
     * This method is used to get the directory of the history file.
     * @return Directory of the history file.
     */
    public String getFileDir() {
        return filePath.toString();
    }

    /**
     * This method is used to get the Path object of the
     * history file.
     * @return Path object of the history file.
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * This method is used to set the directory of the history file with a String.
     * @param fileDir String of the new directory of the history file.
     */
    public void setFileDir(String fileDir) {
        this.filePath = Paths.get(fileDir);
    }

    /**
     * This method is used to set the directory of the history file with a Path object.
     * @param filePath Path of the new directory of the history file.
     */
    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Path to the file that the history is stored in.
     */
    private Path filePath;

    /**
     * This method is used to get the collection of notes in the history.
     * @return Collection with the saved notes.
     */
    public ArrayList<File> getNotes() {
        return notes;
    }

    /**
     * This method is used to get the size of the note history collection.
     * @return number of elements in the history.
     */
    public int size() {
        return notes.size();
    }

    /**
     * This is the collection of saved notes.
     */
    private ArrayList<File> notes;

    /**
     * This is the actual upper limit of possible items in the history.
     * It can never be higher than MAX_ITEMS. The default limit is equal to MAX_ITEMS.
     */
    private int itemLimit;

    /**
     * Constructor of the NoteHistory class creating an empty collection with no file directory attached.
     */
    public NoteHistory() {
        notes = new ArrayList<File>();
        filePath = null;
        itemLimit = MAX_ITEMS;
    }

    /**
     * Constructor of the NoteHistory class. It reads file directories from a text file
     * from the most recent one and stores them in the same order. Each directory should be in
     * a separate line. The file is created if it does not exist.
     * @param filename Directory to the history file.
     * @throws IOException Thrown when a problem occurs during file creation.
     */
    public NoteHistory(String filename) throws IOException {
        this();
        read(filename);
    }

    /**
     * This method is used to get a note directory from the note collection.
     * @param index index in the note collection.
     * @return directory to the note at the provided index.
     */
    public File get(int index) {
        return notes.get(index);
    }

    /**
     * This is the method used to read history from a file. Existing history is cleared.
     * @param filename file directory to read from. The file is created if it does not exist.
     * @throws IOException Thrown when a problem occurs during file creation.
     */
    public void read(String filename) throws IOException {
        filePath = Paths.get(filename);
        read();
    }

    /**
     * This is the method used to read history from a file. Existing history is cleared.
     * Previously used file directory is used.
     * @throws IOException Thrown when a problem occurs during file creation.
     */
    public void read() throws IOException {
        notes = new ArrayList<File>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath.toString()));
            String line = reader.readLine();

            while (line != null && notes.size() < itemLimit) {
                notes.add(new File(line));
                line = reader.readLine();
            }
            reader.close();
        }
        catch (FileNotFoundException ex) {
            Files.createFile(filePath.toAbsolutePath());
        }
    }

    /**
     * This method is used to add a note to the history. If the current note count is
     * equal to the max allowed count the oldest note is removed.
     * @param note Note to be added to the history.
     * @throws IllegalArgumentException Thrown when the note's file directory is empty.
     */
    public void add(Note note) throws IllegalArgumentException {

        if (note!= null && note.getFile() != null) {
            add(note.getFile());
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * This method is used to add a note directory to the history. If the current note count is
     * equal to the max allowed count the oldest note is removed.
     * @param str note directory to be added to the history.
     * @throws IllegalArgumentException Thrown when the note's file directory is empty.
     */
    public void add(String str) throws IllegalArgumentException {
        if (str != null && !"".equals(str)) {
            add(new File(str));
        } else {
            throw new IllegalArgumentException("The note file dir is empty.");
        }
    }

    /**
     * This method is used to add a note directory to the history. If the current note count is
     * equal to the max allowed count the oldest note is removed.
     * @param file note directory to be added to the history.
     * @throws IllegalArgumentException Thrown when the note's file directory is empty.
     */
    public void add(File file) throws IllegalArgumentException {
        if (!"".equals(file.getAbsolutePath())) {
            int elementIndx = notes.indexOf(file);
            if (elementIndx != -1) {
                notes.remove(elementIndx);
            }

            notes.add(0, file);
            if (notes.size() > itemLimit) {
                notes.remove(notes.size() - 1);
            }
        } else {
            throw new IllegalArgumentException("The note file dir is empty.");
        }
    }

    /**
     * This method is used to save the history to the history file using the specified directory.
     * The file is created if it does not exist.
     * @param filename directory of the output file.
     * @throws IOException Thrown when a problem occurs during file creation.
     */
    public void save(String filename) throws IOException{
        filePath = Paths.get(filename);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString(), false));

            for (File note : notes) {
                writer.write(note.getAbsolutePath() + "\n");
            }
            writer.close();
        }
        catch (FileNotFoundException ex) {
            new File(filePath.toString()).createNewFile();
        }
    }

    /**
     * This method is used to save the history to the history file.
     * The file is created if it does not exist.
     * @throws IOException Thrown when a problem occurs during file creation.
     */
    public void save() throws IOException{
        save(filePath.toString());
    }

    /**
     * This method is used to get the current item limit of the history.
     * @return Maximum number of items that the history can hold.
     */
    public int getItemLimit() {
        return itemLimit;
    }

    /**
     * This method is used to set the upper limit of items in the history.
     * If the argument is lower than 0 then the limit is set to 0.
     * If it is higher than MAX_ITEMS then the it is set to MAX_ITEMS.
     * @param itemLimit new item limit.
     */
    public void setItemLimit(int itemLimit) {
        if (itemLimit > MAX_ITEMS) {
            this.itemLimit = MAX_ITEMS;
        } else if (itemLimit < 0) {
            this.itemLimit = 0;
        } else {
            this.itemLimit = itemLimit;
        }

    }
}
