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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private final WorkoutService service;
    private final Scanner scanner;

    public ConsoleMenu(WorkoutService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> logWorkout();
                    case "2" -> listWorkouts();
                    case "3" -> deleteWorkout();
                    case "0" -> running = !saveAndExit();
                    default -> System.out.println("Unknown option, try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (DateTimeParseException e) {
                System.out.println("Please enter the date as yyyy-mm-dd.");
            } catch (InvalidExerciseException | InvalidWorkoutException | WorkoutNotFoundException e) {
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
        System.out.print("Choose: ");
    }

    private void logWorkout() {
        System.out.print("Date (yyyy-mm-dd, empty for today): ");
        String dateText = scanner.nextLine().trim();
        LocalDate date = dateText.isEmpty() ? LocalDate.now() : LocalDate.parse(dateText);
        Workout workout = new Workout(date);

        boolean adding = true;
        while (adding) {
            System.out.println("Add exercise: 1. Strength  2. Cardio  3. Bodyweight  0. Done");
            System.out.print("Choose: ");
            String type = scanner.nextLine().trim();
            switch (type) {
                case "1", "2", "3" -> workout.addExercise(readExercise(type));
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

    private Exercise readExercise(String type) {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        return switch (type) {
            case "1" -> {
                System.out.print("Sets: ");
                int sets = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Reps: ");
                int reps = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Weight (kg): ");
                double weight = Double.parseDouble(scanner.nextLine().trim());
                yield new StrengthExercise(name, sets, reps, weight);
            }
            case "2" -> {
                System.out.print("Distance (km): ");
                double distance = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Duration (min): ");
                int duration = Integer.parseInt(scanner.nextLine().trim());
                yield new CardioExercise(name, distance, duration);
            }
            default -> {
                System.out.print("Sets: ");
                int sets = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Reps: ");
                int reps = Integer.parseInt(scanner.nextLine().trim());
                yield new BodyweightExercise(name, sets, reps);
            }
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
        System.out.print("Workout id to delete: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
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
            System.out.print("Exit without saving? (y/n): ");
            return scanner.nextLine().trim().equalsIgnoreCase("y");
        }
    }
}
