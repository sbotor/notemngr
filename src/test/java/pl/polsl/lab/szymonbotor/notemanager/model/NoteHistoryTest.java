package pl.polsl.lab.szymonbotor.notemanager.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class NoteHistoryTest {

    private static final Path existingPath = Path.of("existingInpHistory.txt");

    private static final Path newPath = Path.of("newInpHistory.txt");

    private static final String noteString = "c:\\folder$\\notes\\$note$$";

    private String[] noteArray;

    @BeforeEach
    void prepare() throws IOException {
        Files.deleteIfExists(existingPath);
        Files.deleteIfExists(newPath);

        // Prepare file
        noteArray = IntStream.range(0, NoteHistory.MAX_ITEMS)
                .mapToObj(i -> noteString.replace("$", Integer.toString(i)))
                .toArray(String[]::new);

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
    void testInputWhenExistsAndTooLong() {
        fail("Not implemented.");
    }

    @Test
    void testInputWhenDoesNotExists() {
        fail("Not implemented.");
    }

    @Test
    void testAddWhenNoteDirNull() {
        fail("Not implemented.");
    }

    @Test
    void testAddWhenNoteDirNotNull() {
        fail("Not implemented.");
    }

    @Test
    void testSaveWhenExists() {
        fail("Not implemented.");
    }

    @Test
    void testSaveWhenDoesNotExist() {
        fail("Not implemented.");
    }

    @AfterAll
    static void cleanup() throws IOException{
        Files.deleteIfExists(existingPath);
        Files.deleteIfExists(newPath);
    }
}