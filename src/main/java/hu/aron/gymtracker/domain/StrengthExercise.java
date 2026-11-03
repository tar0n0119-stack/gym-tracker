package hu.aron.gymtracker.domain;

public class StrengthExercise extends Exercise {

    private final int sets;
    private final int reps;
    private final double weightKg;

    public StrengthExercise(String name, int sets, int reps, double weightKg) {
        super(name);
        if (sets <= 0 || reps <= 0) {
            throw new IllegalArgumentException("Sets and reps must be greater than zero");
        }
        if (weightKg < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
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