package hu.pte.mik.gymtracker;

import hu.pte.mik.gymtracker.domain.Workout;
import hu.pte.mik.gymtracker.persistence.CsvWorkoutStorage;
import hu.pte.mik.gymtracker.persistence.InMemoryRepository;
import hu.pte.mik.gymtracker.persistence.StorageException;
import hu.pte.mik.gymtracker.service.PRTracker;
import hu.pte.mik.gymtracker.service.WorkoutService;
import hu.pte.mik.gymtracker.ui.ConsoleMenu;
import hu.pte.mik.gymtracker.ui.ConsoleNotifier;
import hu.pte.mik.gymtracker.ui.InputReader;

import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        CsvWorkoutStorage storage = new CsvWorkoutStorage(Path.of("data", "workouts.csv"));
        PRTracker prTracker = new PRTracker();
        prTracker.addListener(new ConsoleNotifier());
        WorkoutService service = new WorkoutService(new InMemoryRepository<Workout>(), storage, prTracker);

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
