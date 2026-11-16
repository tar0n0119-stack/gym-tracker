package hu.pte.mik.gymtracker.ui;

import hu.pte.mik.gymtracker.domain.Workout;
import hu.pte.mik.gymtracker.persistence.CsvWorkoutStorage;
import hu.pte.mik.gymtracker.persistence.InMemoryRepository;
import hu.pte.mik.gymtracker.service.PRTracker;
import hu.pte.mik.gymtracker.service.WorkoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConsoleMenuTest {

    @TempDir
    Path tempDir;

    private CsvWorkoutStorage storage;
    private WorkoutService service;

    @BeforeEach
    void setUp() {
        storage = new CsvWorkoutStorage(tempDir.resolve("workouts.csv"));
        service = new WorkoutService(new InMemoryRepository<>(), storage, new PRTracker());
    }

    @Test
    void readingFromClosedInputThrowsInputClosedException() {
        InputReader reader = new InputReader(new Scanner(""));

        assertThrows(InputClosedException.class, () -> reader.readText("Name: "));
    }

    @Test
    void closedInputSavesLoggedWorkoutsAndStopsTheMenu() {
        runMenu("1\n\n3\nPull-up\n4\n10\n0\n");

        List<Workout> saved = storage.load();
        assertEquals(1, saved.size());
        assertEquals("Pull-up", saved.get(0).getExercises().get(0).getName());
    }

    @Test
    void nameWithSemicolonIsAskedAgain() {
        runMenu("1\n\n3\nPull;up\nPull-up\n4\n10\n0\n0\n");

        List<Workout> saved = storage.load();
        assertEquals(1, saved.size());
        assertEquals("Pull-up", saved.get(0).getExercises().get(0).getName());
    }

    private void runMenu(String input) {
        new ConsoleMenu(service, new InputReader(new Scanner(input))).run();
    }
}
