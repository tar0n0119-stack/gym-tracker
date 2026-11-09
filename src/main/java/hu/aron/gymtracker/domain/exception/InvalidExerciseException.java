package hu.aron.gymtracker.domain.exception;

public class InvalidExerciseException extends RuntimeException {

    public InvalidExerciseException(String message) {
        super(message);
    }
}
