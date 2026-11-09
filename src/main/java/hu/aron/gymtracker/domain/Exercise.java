package hu.aron.gymtracker.domain;

import hu.aron.gymtracker.domain.exception.InvalidExerciseException;

public abstract class Exercise {

    private final String name;

    protected Exercise(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidExerciseException("Exercise name cannot be empty");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    public abstract double calculateVolume();

    public abstract String getType();

    protected static void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new InvalidExerciseException(fieldName + " must be greater than zero (got " + value + ")");
        }
    }

    protected static void requirePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new InvalidExerciseException(fieldName + " must be greater than zero (got " + value + ")");
        }
    }

    @Override
    public String toString() {
        return getType() + " - " + name;
    }
}
