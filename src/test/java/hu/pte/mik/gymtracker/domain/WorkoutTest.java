package hu.pte.mik.gymtracker.domain;

import hu.pte.mik.gymtracker.domain.exception.InvalidWorkoutException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkoutTest {

    @Test
    void totalVolumeSumsAllExerciseTypes() {
        Workout workout = new Workout(LocalDate.now());
        workout.addExercise(new StrengthExercise("Bench press", 3, 8, 80));
        workout.addExercise(new CardioExercise("Running", 5, 30));
        workout.addExercise(new BodyweightExercise("Pull-up", 4, 10));

        assertEquals(1965.0, workout.getTotalVolume(), 0.001);
    }

    @Test
    void futureDateIsRejected() {
        assertThrows(InvalidWorkoutException.class, () -> new Workout(LocalDate.now().plusDays(1)));
    }

    @Test
    void nullExerciseIsRejected() {
        Workout workout = new Workout(LocalDate.now());
        assertThrows(InvalidWorkoutException.class, () -> workout.addExercise(null));
    }

    @Test
    void exerciseListCannotBeModifiedFromOutside() {
        Workout workout = new Workout(LocalDate.now());
        assertThrows(UnsupportedOperationException.class,
                () -> workout.getExercises().add(new BodyweightExercise("Dip", 3, 10)));
    }

    @Test
    void workoutsAreOrderedByDate() {
        Workout older = new Workout(LocalDate.now().minusDays(5));
        Workout newer = new Workout(LocalDate.now());

        assertTrue(older.compareTo(newer) < 0);
        assertTrue(newer.compareTo(older) > 0);
    }
}
