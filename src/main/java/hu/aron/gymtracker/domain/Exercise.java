package hu.aron.gymtracker.domain;

public abstract class Exercise {

    private final String name;

    protected Exercise(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Exercise name cannot be empty");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    public abstract double calculateVolume();

    public abstract String getType();

    @Override
    public String toString() {
        return getType() + " - " + name;
    }
}