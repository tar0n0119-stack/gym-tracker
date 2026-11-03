package hu.aron.gymtracker.domain;

public class BodyweightExercise extends Exercise {

    private final int sets;
    private final int reps;

    public BodyweightExercise(String name, int sets, int reps) {
        super(name);
        if (sets <= 0 || reps <= 0) {
            throw new IllegalArgumentException("Sets and reps must be greater than zero");
        }
        this.sets = sets;
        this.reps = reps;
    }

    public int getSets() { return sets; }
    public int getReps() { return reps; }

    @Override
    public double calculateVolume() {
        return sets * reps;
    }

    @Override
    public String getType() {
        return "BODYWEIGHT";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(": %d x %d", sets, reps);
    }
}