package pl.polsl.lab.szymonbotor.notemanager.controller;

import pl.polsl.lab.szymonbotor.notemanager.model.Note;
import pl.polsl.lab.szymonbotor.notemanager.model.NoteHistory;
import pl.polsl.lab.szymonbotor.notemanager.servlets.NoteServlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * TODO
 */
public class SessionHistoryController {

    /**
     * TODO
     */
    private NoteHistory history;

    /**
     * TODO
     */
    private HttpServletRequest request;

    /**
     * TODO
     */
    private HttpServletResponse response;

    /**
     * TODO
     */
    public SessionHistoryController(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        readHistory();
    }

    /**
     * TODO
     * @param note
     */
    public void addNote(Note note) {
        history.add(note);
        saveHistory();
    }

    /**
     * TODO
     * @param note
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
     * TODO
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
     * TODO
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
        for (File noteFile : history.getNotes()) {
            Note note = new Note();
            note.setFile(noteFile);
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
