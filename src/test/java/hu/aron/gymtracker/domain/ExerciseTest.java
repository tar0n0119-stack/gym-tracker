package hu.aron.gymtracker.domain;

import hu.aron.gymtracker.domain.exception.InvalidExerciseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExerciseTest {

    @Test
    void strengthVolumeIsSetsTimesRepsTimesWeight() {
        Exercise bench = new StrengthExercise("Bench press", 3, 8, 80);
        assertEquals(1920.0, bench.calculateVolume(), 0.001);
    }

    @Test
    void cardioVolumeIsDistance() {
        Exercise run = new CardioExercise("Running", 5.5, 30);
        assertEquals(5.5, run.calculateVolume(), 0.001);
    }

    @Test
    void bodyweightVolumeIsSetsTimesReps() {
        Exercise pullUp = new BodyweightExercise("Pull-up", 4, 10);
        assertEquals(40.0, pullUp.calculateVolume(), 0.001);
    }

    @Test
    void nameIsTrimmed() {
        Exercise squat = new StrengthExercise("  Squat  ", 5, 5, 100);
        assertEquals("Squat", squat.getName());
    }

    @Test
    void blankNameIsRejected() {
        assertThrows(InvalidExerciseException.class, () -> new BodyweightExercise("   ", 3, 10));
    }

    @Test
    void zeroSetsAreRejected() {
        InvalidExerciseException e = assertThrows(InvalidExerciseException.class,
                () -> new StrengthExercise("Bench press", 0, 8, 80));
        assertTrue(e.getMessage().contains("Sets"));
    }

    @Test
    void negativeWeightIsRejected() {
        assertThrows(InvalidExerciseException.class, () -> new StrengthExercise("Bench press", 3, 8, -5));
    }

    @Test
    void zeroDistanceIsRejected() {
        assertThrows(InvalidExerciseException.class, () -> new CardioExercise("Running", 0, 30));
    }
}
