package pl.polsl.lab.szymonbotor.notemanager.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class NoteHistoryTest {

    private static final Path existingPath = Path.of("existingInpHistory.txt");

    private static final Path newPath = Path.of("newInpHistory.txt");

    private static final String noteString = "c:\\folder$\\notes\\$note$";

    private String[] noteArray;

    static String[] prepareStrings(int start, int end) {
        return IntStream.range(start, end)
                .mapToObj(i -> noteString.replace("$", Integer.toString(end - i - 1)))
                .toArray(String[]::new);
    }

    static String[] prepareStringsReverse(int start, int end) {
        return IntStream.range(start, end)
                .mapToObj(i -> noteString.replace("$", Integer.toString(i)))
                .toArray(String[]::new);
    }

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

    @Test
    void testInputWhenDoesNotExist() throws IOException {
        // Given
        Files.deleteIfExists(existingPath);

        // When
        NoteHistory history = new NoteHistory(existingPath.toString());

        // Then
        assertEquals(0, history.getNotes().size());
    }

    @Test
    void testInputWhenExistsAndCorrectLength() {
        // Given

        // When
        NoteHistory history = null;
        try {
            history = new NoteHistory(existingPath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Then
        assertArrayEquals(noteArray, history.getNotes().toArray());
    }

    @Test
    void testInputWhenExistsAndTooLong() throws IOException {
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
        assertArrayEquals(noteArray, history.getNotes().toArray());
    }

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

    @Test
    void testAddWhenNotDuplicate() throws IOException {
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

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    void testAddWhenDuplicate(int elementIndx) throws IOException {
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

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, 5, 9, 11, 32})
    void testSaveWhenExists(int strCount) throws IOException {
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

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, 5, 9, 11, 32})
    void testSaveWhenDoesNotExist(int strCount) throws IOException {
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

    @AfterAll
    static void cleanup() throws IOException{
        Files.deleteIfExists(existingPath);
        Files.deleteIfExists(newPath);
    }
}