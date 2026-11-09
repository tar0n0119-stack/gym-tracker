package hu.aron.gymtracker.domain;

import hu.aron.gymtracker.domain.exception.InvalidExerciseException;

public class StrengthExercise extends Exercise {

    private final int sets;
    private final int reps;
    private final double weightKg;

    public StrengthExercise(String name, int sets, int reps, double weightKg) {
        super(name);
        requirePositive(sets, "Sets");
        requirePositive(reps, "Reps");
        if (weightKg < 0) {
            throw new InvalidExerciseException("Weight cannot be negative (got " + weightKg + ")");
        }
        this.sets = sets;
        this.reps = reps;
        this.weightKg = weightKg;
    }

    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public double getWeightKg() { return weightKg; }

    @Override
    public double calculateVolume() {
        return sets * reps * weightKg;
    }

    @Override
    public String getType() {
        return "STRENGTH";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(": %d x %d @ %.1f kg", sets, reps, weightKg);
    }
}