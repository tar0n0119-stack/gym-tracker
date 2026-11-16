package hu.pte.mik.gymtracker.ui;

public class InputClosedException extends RuntimeException {

    public InputClosedException() {
        super("Console input was closed");
    }
}
