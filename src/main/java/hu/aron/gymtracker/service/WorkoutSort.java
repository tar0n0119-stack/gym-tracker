package hu.aron.gymtracker.service;

import hu.aron.gymtracker.domain.Workout;

import java.util.Comparator;

public enum WorkoutSort {

    DATE("Date", Comparator.naturalOrder()),
    TOTAL_VOLUME("Total volume", Comparator.comparingDouble(Workout::getTotalVolume)),
    EXERCISE_COUNT("Number of exercises", Comparator.comparingInt(workout -> workout.getExercises().size()));

    private final String label;
    private final Comparator<Workout> comparator;

    WorkoutSort(String label, Comparator<Workout> comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    public String getLabel() {
        return label;
    }

    public Comparator<Workout> getComparator() {
        return comparator;
    }
}
