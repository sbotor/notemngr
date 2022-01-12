package pl.polsl.lab.szymonbotor.notemanager.controller;

import pl.polsl.lab.szymonbotor.notemanager.model.Note;
import pl.polsl.lab.szymonbotor.notemanager.model.NoteHistory;
import pl.polsl.lab.szymonbotor.notemanager.servlets.NoteServlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

/**
 * Controller class for cookie-based note history.
 * @author Szymon Botor
 * @version 1.0
 */
public class CookieHistoryController {

    /**
     * Note history of the controller.
     */
    private NoteHistory history;

    /**
     * HTTP request that the history is based on.
     */
    private HttpServletRequest request;

    /**
     * HTTP response that the history updates.
     */
    private HttpServletResponse response;

    /**
     * Main constructor of the class. It reads the recent notes from the request's cookies.
     * @param request request to read notes from.
     * @param response response to update cookies in.
     */
    public CookieHistoryController(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        readHistory();
    }

    /**
     * Adds a note to the history, or pushes an existing note to the top, and updates cookies.
     * @param note note to add to the history.
     */
    public void addNote(Note note) {
        history.add(note);
        saveHistory();
    }

    /**
     * Remove a note from history. If the note is not present then nothing happens.
     * @param note note to remove from history.
     */
    public void removeNote(Note note) {
        int noteIndex = history.getNotes().indexOf(note.getFile());
        if (noteIndex == -1) {
            return;
        }

        history.getNotes().remove(noteIndex);
        saveHistory();
    }

    /**
     * Updates history according to the request cookies.
     */
    private void readHistory() {
        history = new NoteHistory();

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("notes")) {
                //System.out.println("Notes from cookie: " + cookie.getValue());
                String[] noteNames = cookie.getValue().split(",");
                
                for (String noteName : noteNames) {
                    File noteFile = NoteServlet.getNoteFile(noteName.strip());
                    if (noteFile != null && noteFile.exists()) {
                        history.add(noteFile);
                    }
                }
                break;
            }
        }
    }

    /**
     * Updates response cookies according to the history.
     */
    private void saveHistory() {
        if (history.size() == 0) {
            Cookie cookie = new Cookie("notes", "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            return;
        }

        StringBuilder builder = new StringBuilder();
        ArrayList<File> list = history.getNotes();
        for (int i = list.size() - 1; i >= 0; i--) {
            Note note = new Note();
            note.setFile(list.get(i));
            builder.append(note.getName()).append(",");
        }

        Cookie cookie = new Cookie("notes", builder.toString());
        //System.out.println("Built cookie: " + builder);
        //cookie.setMaxAge(30 * 24 * 60 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * Gets the servlet request of the history.
     * @return HttpServletRequest of the history.
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Gets the servlet response of the history.
     * @return HttpServletResponse of the history.
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Returns the list of notes in the history.
     * @return ArrayList of note files.
     */
    public ArrayList<File> getNotes() {
        return history.getNotes();
    }
}
