package hu.aron.gymtracker;

import hu.aron.gymtracker.domain.Workout;
import hu.aron.gymtracker.persistence.CsvWorkoutStorage;
import hu.aron.gymtracker.persistence.InMemoryRepository;
import hu.aron.gymtracker.persistence.StorageException;
import hu.aron.gymtracker.service.WorkoutService;
import hu.aron.gymtracker.ui.ConsoleMenu;
import hu.aron.gymtracker.ui.InputReader;

import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        CsvWorkoutStorage storage = new CsvWorkoutStorage(Path.of("data", "workouts.csv"));
        WorkoutService service = new WorkoutService(new InMemoryRepository<Workout>(), storage);

        try {
            int count = service.loadData();
            System.out.println("Loaded " + count + " workout(s).");
        } catch (StorageException e) {
            System.out.println("Could not load saved data: " + e.getMessage());
            System.out.println("Starting with an empty history.");
        }

        new ConsoleMenu(service, new InputReader(new Scanner(System.in))).run();
    }
}
