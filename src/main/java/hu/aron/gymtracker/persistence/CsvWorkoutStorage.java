package hu.aron.gymtracker.persistence;

import hu.aron.gymtracker.domain.BodyweightExercise;
import hu.aron.gymtracker.domain.CardioExercise;
import hu.aron.gymtracker.domain.Exercise;
import hu.aron.gymtracker.domain.StrengthExercise;
import hu.aron.gymtracker.domain.Workout;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvWorkoutStorage {

    private static final String SEPARATOR = ";";
    private static final String WORKOUT_TAG = "WORKOUT";

    private final Path file;

    public CsvWorkoutStorage(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        this.file = file;
    }

    public void save(List<Workout> workouts) {
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                for (Workout workout : workouts) {
                    writer.write(String.join(SEPARATOR, WORKOUT_TAG,
                            String.valueOf(workout.getId()), workout.getDate().toString()));
                    writer.newLine();
                    for (Exercise exercise : workout.getExercises()) {
                        writer.write(toLine(exercise));
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            throw new StorageException("Could not save workouts to " + file, e);
        }
    }

    private String toLine(Exercise exercise) {
        if (exercise instanceof StrengthExercise s) {
            return String.join(SEPARATOR, s.getType(), s.getName(),
                    String.valueOf(s.getSets()), String.valueOf(s.getReps()), String.valueOf(s.getWeightKg()));
        }
        if (exercise instanceof CardioExercise c) {
            return String.join(SEPARATOR, c.getType(), c.getName(),
                    String.valueOf(c.getDistanceKm()), String.valueOf(c.getDurationMin()));
        }
        if (exercise instanceof BodyweightExercise b) {
            return String.join(SEPARATOR, b.getType(), b.getName(),
                    String.valueOf(b.getSets()), String.valueOf(b.getReps()));
        }
        throw new StorageException("Unknown exercise type: " + exercise.getType());
    }
}
