package hu.pte.mik.gymtracker.service;

import hu.pte.mik.gymtracker.domain.StrengthExercise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PRTrackerTest {

    private PRTracker tracker;
    private List<String> notifications;

    @BeforeEach
    void setUp() {
        tracker = new PRTracker();
        notifications = new ArrayList<>();
        tracker.addListener((name, previousBest, newBest) ->
                notifications.add(name + ":" + previousBest + "->" + newBest));
    }

    @Test
    void firstTimeIsNotARecord() {
        assertFalse(tracker.check(new StrengthExercise("Bench press", 3, 8, 80)));
        assertTrue(notifications.isEmpty());
    }

    @Test
    void beatingThePreviousBestNotifiesListeners() {
        tracker.check(new StrengthExercise("Bench press", 3, 8, 80));

        assertTrue(tracker.check(new StrengthExercise("bench press", 3, 8, 85)));
        assertEquals(List.of("bench press:1920.0->2040.0"), notifications);
    }

    @Test
    void equalVolumeIsNotARecord() {
        tracker.check(new StrengthExercise("Bench press", 3, 8, 80));

        assertFalse(tracker.check(new StrengthExercise("Bench press", 3, 8, 80)));
        assertTrue(notifications.isEmpty());
    }

    @Test
    void recordedHistoryIsSilentButCountsAsBest() {
        tracker.record(new StrengthExercise("Squat", 5, 5, 100));

        assertTrue(notifications.isEmpty());
        assertFalse(tracker.check(new StrengthExercise("Squat", 5, 5, 90)));
        assertTrue(tracker.check(new StrengthExercise("Squat", 5, 5, 110)));
    }
}
