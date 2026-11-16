package hu.pte.mik.gymtracker.ui;

import hu.pte.mik.gymtracker.domain.BodyweightExercise;
import hu.pte.mik.gymtracker.domain.CardioExercise;
import hu.pte.mik.gymtracker.domain.Exercise;
import hu.pte.mik.gymtracker.domain.StrengthExercise;
import hu.pte.mik.gymtracker.domain.Workout;
import hu.pte.mik.gymtracker.domain.exception.InvalidExerciseException;
import hu.pte.mik.gymtracker.domain.exception.InvalidWorkoutException;
import hu.pte.mik.gymtracker.persistence.StorageException;
import hu.pte.mik.gymtracker.service.WorkoutNotFoundException;
import hu.pte.mik.gymtracker.service.WorkoutService;
import hu.pte.mik.gymtracker.service.WorkoutSort;

import java.util.List;

public class ConsoleMenu {

    private final WorkoutService service;
    private final InputReader input;

    public ConsoleMenu(WorkoutService service, InputReader input) {
        this.service = service;
        this.input = input;
    }

    public void run() {
        try {
            runMenuLoop();
        } catch (InputClosedException e) {
            saveOnInputClosed();
        }
    }

    private void runMenuLoop() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = input.readText("Choose: ");
            try {
                switch (choice) {
                    case "1" -> logWorkout();
                    case "2" -> listWorkouts();
                    case "3" -> deleteWorkout();
                    case "4" -> sortWorkouts();
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
        System.out.println("4. Sort workouts");
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
        String name = readExerciseName();
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

    private String readExerciseName() {
        while (true) {
            String name = input.readText("Name: ");
            if (service.isValidExerciseName(name)) {
                return name;
            }
            System.out.println("The name cannot contain ';', try again.");
        }
    }

    private void listWorkouts() {
        printWorkouts(service.listWorkouts());
    }

    private void sortWorkouts() {
        WorkoutSort[] options = WorkoutSort.values();
        System.out.println("Sort by:");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i].getLabel());
        }
        int choice = input.readInt("Choose: ");
        if (choice < 1 || choice > options.length) {
            System.out.println("Unknown option, try again.");
            return;
        }
        boolean descending = input.confirm("Descending order?");
        printWorkouts(service.listWorkouts(options[choice - 1], descending));
    }

    private void printWorkouts(List<Workout> workouts) {
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

    private void saveOnInputClosed() {
        System.out.println();
        try {
            service.saveData();
            System.out.println("Input closed. Data saved.");
        } catch (StorageException e) {
            System.out.println("Input closed. Could not save data: " + e.getMessage());
        }
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
