package hu.aron.gymtracker.domain.exception;

public class InvalidWorkoutException extends RuntimeException {

    public InvalidWorkoutException(String message) {
        super(message);
    }
}
