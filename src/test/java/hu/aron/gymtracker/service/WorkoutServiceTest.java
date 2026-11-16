package hu.aron.gymtracker.service;

import hu.aron.gymtracker.domain.BodyweightExercise;
import hu.aron.gymtracker.domain.Exercise;
import hu.aron.gymtracker.domain.StrengthExercise;
import hu.aron.gymtracker.domain.Workout;
import hu.aron.gymtracker.persistence.CsvWorkoutStorage;
import hu.aron.gymtracker.persistence.InMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkoutServiceTest {

    @TempDir
    Path tempDir;

    private WorkoutService service;

    @BeforeEach
    void setUp() {
        service = new WorkoutService(new InMemoryRepository<>(),
                new CsvWorkoutStorage(tempDir.resolve("workouts.csv")), new PRTracker());
    }

    @Test
    void unknownWorkoutCannotBeFoundOrDeleted() {
        assertThrows(WorkoutNotFoundException.class, () -> service.findWorkout(42));
        assertThrows(WorkoutNotFoundException.class, () -> service.deleteWorkout(42));
    }

    @Test
    void workoutsCanBeSortedByVolumeDescending() {
        Workout light = workoutWith(new BodyweightExercise("Push-up", 3, 20), 2);
        Workout heavy = workoutWith(new StrengthExercise("Squat", 5, 5, 100), 1);
        service.logWorkout(light);
        service.logWorkout(heavy);

        List<Workout> sorted = service.listWorkouts(WorkoutSort.TOTAL_VOLUME, true);

        assertEquals(heavy.getId(), sorted.get(0).getId());
        assertEquals(light.getId(), sorted.get(1).getId());
    }

    @Test
    void savedDataIsAvailableAfterRestart() {
        service.logWorkout(workoutWith(new StrengthExercise("Deadlift", 3, 5, 140), 0));
        service.saveData();

        WorkoutService restarted = new WorkoutService(new InMemoryRepository<>(),
                new CsvWorkoutStorage(tempDir.resolve("workouts.csv")), new PRTracker());

        assertEquals(1, restarted.loadData());
        assertEquals(1, restarted.listWorkouts().size());
    }

    private Workout workoutWith(Exercise exercise, int daysAgo) {
        Workout workout = new Workout(LocalDate.now().minusDays(daysAgo));
        workout.addExercise(exercise);
        return workout;
    }
}
