package pl.polsl.lab.szymonbotor.notemanager.enums;

public enum noteAddOption {
    NEW("New"),
    OPEN("Open");

    private final String str;

    noteAddOption(String text) {
        str = text;
    }

    @Override
    public String toString() {
        return str;
    }
}
