package hu.pte.mik.gymtracker.persistence;

import hu.pte.mik.gymtracker.domain.BodyweightExercise;
import hu.pte.mik.gymtracker.domain.CardioExercise;
import hu.pte.mik.gymtracker.domain.StrengthExercise;
import hu.pte.mik.gymtracker.domain.Workout;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvWorkoutStorageTest {

    @TempDir
    Path tempDir;

    @Test
    void savedWorkoutsCanBeLoadedBack() {
        Workout workout = new Workout(LocalDate.of(2026, 11, 1));
        workout.setId(3);
        workout.addExercise(new StrengthExercise("Bench press", 3, 8, 82.5));
        workout.addExercise(new CardioExercise("Running", 5.2, 31));
        workout.addExercise(new BodyweightExercise("Pull-up", 4, 10));
        CsvWorkoutStorage storage = new CsvWorkoutStorage(tempDir.resolve("data").resolve("workouts.csv"));

        storage.save(List.of(workout));
        List<Workout> loaded = storage.load();

        assertEquals(1, loaded.size());
        Workout copy = loaded.get(0);
        assertEquals(3, copy.getId());
        assertEquals(LocalDate.of(2026, 11, 1), copy.getDate());
        assertEquals(3, copy.getExercises().size());
        assertEquals(workout.getTotalVolume(), copy.getTotalVolume(), 0.001);
    }

    @Test
    void missingFileGivesEmptyList() {
        CsvWorkoutStorage storage = new CsvWorkoutStorage(tempDir.resolve("missing.csv"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void corruptLineIsReportedWithLineNumber() throws IOException {
        Path file = tempDir.resolve("broken.csv");
        Files.writeString(file, "WORKOUT;1;2026-11-01\nSTRENGTH;Bench press;3;abc;80\n");
        CsvWorkoutStorage storage = new CsvWorkoutStorage(file);

        StorageException e = assertThrows(StorageException.class, storage::load);
        assertTrue(e.getMessage().contains("line 2"));
    }

    @Test
    void nameWithSeparatorIsRejectedAndOldFileIsKept() throws IOException {
        Path file = tempDir.resolve("workouts.csv");
        CsvWorkoutStorage storage = new CsvWorkoutStorage(file);
        Workout valid = new Workout(LocalDate.of(2026, 11, 1));
        valid.setId(1);
        valid.addExercise(new BodyweightExercise("Pull-up", 4, 10));
        storage.save(List.of(valid));
        String before = Files.readString(file);

        Workout broken = new Workout(LocalDate.of(2026, 11, 2));
        broken.setId(2);
        broken.addExercise(new BodyweightExercise("Push;up", 3, 20));

        assertThrows(StorageException.class, () -> storage.save(List.of(valid, broken)));
        assertEquals(before, Files.readString(file));
        assertEquals(1, storage.load().size());
    }

    @Test
    void exerciseBeforeWorkoutIsRejected() throws IOException {
        Path file = tempDir.resolve("broken.csv");
        Files.writeString(file, "CARDIO;Running;5.0;30\n");
        CsvWorkoutStorage storage = new CsvWorkoutStorage(file);

        assertThrows(StorageException.class, storage::load);
    }
}
