package pl.polsl.lab.szymonbotor.notemanager.model.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.polsl.lab.szymonbotor.notemanager.model.Note;
import pl.polsl.lab.szymonbotor.notemanager.model.NoteHistory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the testing class for the NoteHistory class.
 * @author Szymon Botor
 * @version 1.0
 */
class NoteHistoryTest {

    /**
     * Path object pointing to the file that should exist.
     */
    private static final Path existingPath = Path.of("existingInpHistory.txt");

    /**
     * Path object pointing to the file that should not exist.
     */
    private static final Path newPath = Path.of("newInpHistory.txt");

    /**
     * This is the string template for the note directory string.
     */
    private static final String noteString = "c:\\folder$\\notes\\$note$";

    /**
     * This is the array storing the note directory strings.
     */
    private String[] noteArray;

    /**
     * This is the static method used to prepare a string array using the provided string template.
     * @param start starting index of the note template parameter. Inclusive.
     * @param end ending index of the note template parameter. Exclusive.
     * @return the constructed array.
     */
    static String[] prepareStrings(int start, int end) {
        return IntStream.range(start, end)
                .mapToObj(i -> noteString.replace("$", Integer.toString(end - i - 1)))
                .toArray(String[]::new);
    }

    /**
     * This is the static method used to prepare a string array in reverse using the provided string template.
     * @param start starting index of the note template parameter. Inclusive.
     * @param end ending index of the note template parameter. Exclusive.
     * @return the constructed array.
     */
    static String[] prepareStringsReverse(int start, int end) {
        return IntStream.range(start, end)
                .mapToObj(i -> noteString.replace("$", Integer.toString(i)))
                .toArray(String[]::new);
    }

    /**
     * This method is used to prepare the files and array before every test.
     * @throws IOException Thrown when an error occurs during file IO.
     */
    @BeforeEach
    void prepare() throws IOException {
        Files.deleteIfExists(existingPath);
        Files.deleteIfExists(newPath);

        // Prepare file
        noteArray = prepareStrings(0, NoteHistory.MAX_ITEMS);

        Files.createFile(existingPath);
        for (String str : noteArray) {
            Files.writeString(existingPath, str + "\n", StandardOpenOption.APPEND);
        }
    }

    /**
     * This test is used to check the input when the input file does not exist.
     * @throws IOException When an error occurs during file IO.
     */
    @Test
    void testInputWhenFileDoesNotExist() throws IOException {
        // Given
        Files.deleteIfExists(existingPath);

        // When
        NoteHistory history = new NoteHistory(existingPath.toString());

        // Then
        assertEquals(0, history.getNotes().size(), "The history is not empty.");
    }

    /**
     * This test is used to check the input when the input file exists
     * and its length is not higher than the max history capacity.
     */
    @Test
    void testInputWhenFileExistsAndCorrectLength() {
        // Given

        // When
        NoteHistory history = null;
        try {
            history = new NoteHistory(existingPath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Then
        assertArrayEquals(noteArray, history.getNotes().toArray(), "The history is different than expected.");
    }

    /**
     * This test is used to check the input when the input file exists
     * and its length is not higher than the max history capacity.
     * @throws IOException When an error occurs during file IO.
     */
    @Test
    void testInputWhenFileExistsAndTooLong() throws IOException {
        // Given
        String[] additionalArray = prepareStrings(NoteHistory.MAX_ITEMS, 2 * NoteHistory.MAX_ITEMS);

        for (String str : additionalArray) {
            Files.writeString(existingPath, str + "\n", StandardOpenOption.APPEND);
        }

        // When
        NoteHistory history = null;
        try {
            history = new NoteHistory(existingPath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Then
        assertArrayEquals(noteArray, history.getNotes().toArray(), "The history is different than expected.");
    }

    /**
     * This method is used to test adding a string when it is null or empty.
     * @throws IOException When an error occurs during file IO.
     */
    @Test
    void testAddWhenNoteDirNullOrEmpty() throws IOException {
        // Given
        Note note = new Note();
        NoteHistory history = new NoteHistory(newPath.toString());

        // When
        boolean testSuccess = false;
        try {
            history.add(note);
            history.add("");
            testSuccess = false;
        } catch (IllegalArgumentException e) {
            testSuccess = true;
        }

        // Then
        assertTrue(testSuccess);
    }

    /**
     * This method is used to test adding a correct string when the history does not already contain it.
     * @throws IOException When an error occurs during file IO.
     */
    @Test
    void testAddWhenTextNotDuplicate() throws IOException {
        // Given
        String newElement = "c:\\testNotes\\testnote";
        NoteHistory history = new NoteHistory(existingPath.toString());

        String[] newNoteArray = new String[noteArray.length];
        System.arraycopy(noteArray, 0, newNoteArray,1, noteArray.length - 1);
        newNoteArray[0] = newElement;

        // When
        history.add(newElement);

        // Then
        assertArrayEquals(newNoteArray, history.getNotes().toArray());
    }

    /**
     * This method is used to test adding a correct string when the history already contains it.
     * @param elementIndx index of the element to re-add to the history.
     * @throws IOException When an error occurs during file IO.
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    void testAddWhenTextDuplicate(int elementIndx) throws IOException {
        // Given
        String newElement = noteArray[elementIndx];
        NoteHistory history = new NoteHistory(existingPath.toString());

        String[] newNoteArray = new String[noteArray.length];
        newNoteArray[0] = noteArray[elementIndx];
        System.arraycopy(noteArray, 0, newNoteArray,1, elementIndx);
        System.arraycopy(noteArray, elementIndx + 1, newNoteArray, elementIndx + 1, noteArray.length - elementIndx - 1);

        // When
        history.add(newElement);

        // Then
        assertArrayEquals(newNoteArray, history.getNotes().toArray());
    }

    /**
     * This method is used to check file output when the file already exists.
     * @param strCount number of string added to the saved history.
     * @throws IOException When an error occurs during file IO.
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, 5, 9, 11, 32})
    void testSaveWhenFileExists(int strCount) throws IOException {
        // Given
        NoteHistory history = new NoteHistory();
        String[] testStrings = prepareStringsReverse(0, strCount);

        for (String str : testStrings) {
            history.add(str);
        }

        // When
        history.save(existingPath.toString());

        // Then
        NoteHistory newHistory = new NoteHistory(existingPath.toString());

        assertArrayEquals(history.getNotes().toArray(), newHistory.getNotes().toArray());
    }

    /**
     * This method is used to check file output when the file does not exist.
     * @param strCount number of string added to the saved history.
     * @throws IOException When an error occurs during file IO.
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, 5, 9, 11, 32})
    void testSaveWhenFileDoesNotExist(int strCount) throws IOException {
        // Given
        NoteHistory history = new NoteHistory();
        String[] testStrings = prepareStringsReverse(0, strCount);

        for (String str : testStrings) {
            history.add(str);
        }

        // When
        history.save(newPath.toString());

        // Then
        NoteHistory newHistory = new NoteHistory(newPath.toString());

        assertArrayEquals(history.getNotes().toArray(), newHistory.getNotes().toArray());
    }

    /**
     * This method is used to clean up after all tests are done.
     * @throws IOException When an error occurs during file IO.
     */
    @AfterAll
    static void cleanup() throws IOException{
        Files.deleteIfExists(existingPath);
        Files.deleteIfExists(newPath);
    }
}