package hu.aron.gymtracker.ui;

import hu.aron.gymtracker.domain.BodyweightExercise;
import hu.aron.gymtracker.domain.CardioExercise;
import hu.aron.gymtracker.domain.Exercise;
import hu.aron.gymtracker.domain.StrengthExercise;
import hu.aron.gymtracker.domain.Workout;
import hu.aron.gymtracker.domain.exception.InvalidExerciseException;
import hu.aron.gymtracker.domain.exception.InvalidWorkoutException;
import hu.aron.gymtracker.persistence.StorageException;
import hu.aron.gymtracker.service.WorkoutNotFoundException;
import hu.aron.gymtracker.service.WorkoutService;

import java.util.List;

public class ConsoleMenu {

    private final WorkoutService service;
    private final InputReader input;

    public ConsoleMenu(WorkoutService service, InputReader input) {
        this.service = service;
        this.input = input;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = input.readText("Choose: ");
            try {
                switch (choice) {
                    case "1" -> logWorkout();
                    case "2" -> listWorkouts();
                    case "3" -> deleteWorkout();
                    case "0" -> running = !saveAndExit();
                    default -> System.out.println("Unknown option, try again.");
                }
            } catch (InvalidWorkoutException | WorkoutNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Gym Tracker ===");
        System.out.println("1. Log workout");
        System.out.println("2. List workouts");
        System.out.println("3. Delete workout");
        System.out.println("0. Save and exit");
    }

    private void logWorkout() {
        Workout workout = new Workout(input.readDate("Date (yyyy-mm-dd, empty for today): "));

        boolean adding = true;
        while (adding) {
            System.out.println("Add exercise: 1. Strength  2. Cardio  3. Bodyweight  0. Done");
            String type = input.readText("Choose: ");
            switch (type) {
                case "1", "2", "3" -> addExercise(workout, type);
                case "0" -> adding = false;
                default -> System.out.println("Unknown option, try again.");
            }
        }

        if (workout.getExercises().isEmpty()) {
            System.out.println("Workout has no exercises, it was not saved.");
            return;
        }
        Workout saved = service.logWorkout(workout);
        System.out.println("Saved: " + saved);
    }

    private void addExercise(Workout workout, String type) {
        try {
            Exercise exercise = readExercise(type);
            workout.addExercise(exercise);
            System.out.println("Added: " + exercise);
        } catch (InvalidExerciseException e) {
            System.out.println("Exercise not added: " + e.getMessage());
        }
    }

    private Exercise readExercise(String type) {
        String name = input.readText("Name: ");
        return switch (type) {
            case "1" -> new StrengthExercise(name,
                    input.readInt("Sets: "),
                    input.readInt("Reps: "),
                    input.readDouble("Weight (kg): "));
            case "2" -> new CardioExercise(name,
                    input.readDouble("Distance (km): "),
                    input.readInt("Duration (min): "));
            default -> new BodyweightExercise(name,
                    input.readInt("Sets: "),
                    input.readInt("Reps: "));
        };
    }

    private void listWorkouts() {
        List<Workout> workouts = service.listWorkouts();
        if (workouts.isEmpty()) {
            System.out.println("No workouts logged yet.");
            return;
        }
        for (Workout workout : workouts) {
            System.out.println(workout);
            for (Exercise exercise : workout.getExercises()) {
                System.out.println("    " + exercise);
            }
        }
    }

    private void deleteWorkout() {
        int id = input.readInt("Workout id to delete: ");
        service.deleteWorkout(id);
        System.out.println("Workout #" + id + " deleted.");
    }

    private boolean saveAndExit() {
        try {
            service.saveData();
            System.out.println("Data saved. Bye!");
            return true;
        } catch (StorageException e) {
            System.out.println("Could not save data: " + e.getMessage());
            return input.confirm("Exit without saving?");
        }
    }
}
