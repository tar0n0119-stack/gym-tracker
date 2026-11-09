package hu.aron.gymtracker.domain;

import hu.aron.gymtracker.domain.exception.InvalidWorkoutException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Workout implements Identifiable, Comparable<Workout> {

    private int id;
    private final LocalDate date;
    private final List<Exercise> exercises = new ArrayList<>();

    public Workout(LocalDate date) {
        if (date == null) {
            throw new InvalidWorkoutException("Workout date cannot be empty");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new InvalidWorkoutException("Workout date cannot be in the future");
        }
        this.date = date;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        if (id <= 0) {
            throw new InvalidWorkoutException("Workout id must be positive");
        }
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void addExercise(Exercise exercise) {
        if (exercise == null) {
            throw new InvalidWorkoutException("Exercise cannot be null");
        }
        exercises.add(exercise);
    }

    public List<Exercise> getExercises() {
        return Collections.unmodifiableList(exercises);
    }

    public double getTotalVolume() {
        double total = 0;
        for (Exercise exercise : exercises) {
            total += exercise.calculateVolume();
        }
        return total;
    }

    @Override
    public int compareTo(Workout other) {
        return this.date.compareTo(other.date);
    }

    @Override
    public String toString() {
        return String.format("#%d Workout on %s (%d exercises, total volume: %.1f)",
                id, date, exercises.size(), getTotalVolume());
    }
}