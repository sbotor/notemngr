package pl.polsl.lab.szymonbotor.notemanager.controller;

import pl.polsl.lab.szymonbotor.notemanager.entities.Note;
import pl.polsl.lab.szymonbotor.notemanager.entities.User;
import pl.polsl.lab.szymonbotor.notemanager.model.AES;
import pl.polsl.lab.szymonbotor.notemanager.model.Hash;

public class NoteController extends EntityController {

    // TODO
    private Note note;

    public NoteController() {
        note = null;
    }

    // TODO
    public Note createNote(String name, AES aes) {

        aes.regenerateIv();
        aes.regenerateSalt();

        Note newNote = new Note(name);
        newNote.setIV(Hash.bytesToString(aes.getIV()));
        newNote.setSalt(Hash.bytesToString(aes.getSalt()));

        note = newNote;
        return newNote;
    }
}
