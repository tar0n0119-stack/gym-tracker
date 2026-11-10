package hu.aron.gymtracker.service;

public class WorkoutNotFoundException extends RuntimeException {

    public WorkoutNotFoundException(int id) {
        super("No workout found with id " + id);
    }
}
