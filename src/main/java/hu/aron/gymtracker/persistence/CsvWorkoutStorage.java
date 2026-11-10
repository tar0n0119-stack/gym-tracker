package hu.aron.gymtracker.persistence;

import hu.aron.gymtracker.domain.BodyweightExercise;
import hu.aron.gymtracker.domain.CardioExercise;
import hu.aron.gymtracker.domain.Exercise;
import hu.aron.gymtracker.domain.StrengthExercise;
import hu.aron.gymtracker.domain.Workout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

    public List<Workout> load() {
        List<Workout> workouts = new ArrayList<>();
        if (!Files.exists(file)) {
            return workouts;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            Workout current = null;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(SEPARATOR, -1);
                try {
                    if (parts[0].equals(WORKOUT_TAG)) {
                        current = parseWorkout(parts);
                        workouts.add(current);
                    } else if (current == null) {
                        throw new StorageException("exercise found before any workout");
                    } else {
                        current.addExercise(parseExercise(parts));
                    }
                } catch (RuntimeException e) {
                    throw new StorageException("Corrupt data in " + file + " at line " + lineNumber
                            + ": " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new StorageException("Could not read workouts from " + file, e);
        }
        return workouts;
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

    private Workout parseWorkout(String[] parts) {
        requireFieldCount(parts, 3);
        try {
            Workout workout = new Workout(LocalDate.parse(parts[2]));
            workout.setId(Integer.parseInt(parts[1]));
            return workout;
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new StorageException("invalid workout id or date");
        }
    }

    private Exercise parseExercise(String[] parts) {
        try {
            return switch (parts[0]) {
                case "STRENGTH" -> {
                    requireFieldCount(parts, 5);
                    yield new StrengthExercise(parts[1], Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]), Double.parseDouble(parts[4]));
                }
                case "CARDIO" -> {
                    requireFieldCount(parts, 4);
                    yield new CardioExercise(parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3]));
                }
                case "BODYWEIGHT" -> {
                    requireFieldCount(parts, 4);
                    yield new BodyweightExercise(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                }
                default -> throw new StorageException("unknown exercise type '" + parts[0] + "'");
            };
        } catch (NumberFormatException e) {
            throw new StorageException("invalid number");
        }
    }

    private void requireFieldCount(String[] parts, int expected) {
        if (parts.length != expected) {
            throw new StorageException("expected " + expected + " fields but found " + parts.length);
        }
    }
}
