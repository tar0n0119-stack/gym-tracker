package hu.aron.gymtracker.domain;

public class CardioExercise extends Exercise {

    private final double distanceKm;
    private final int durationMin;

    public CardioExercise(String name, double distanceKm, int durationMin) {
        super(name);
        requirePositive(distanceKm, "Distance");
        requirePositive(durationMin, "Duration");
        this.distanceKm = distanceKm;
        this.durationMin = durationMin;
    }

    public double getDistanceKm() { return distanceKm; }
    public int getDurationMin() { return durationMin; }

    public double getPaceMinPerKm() {
        return durationMin / distanceKm;
    }

    @Override
    public double calculateVolume() {
        return distanceKm;
    }

    @Override
    public String getType() {
        return "CARDIO";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(": %.2f km in %d min", distanceKm, durationMin);
    }
}