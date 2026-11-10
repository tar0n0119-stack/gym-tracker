package hu.aron.gymtracker.service;

import hu.aron.gymtracker.domain.Workout;
import hu.aron.gymtracker.persistence.CsvWorkoutStorage;
import hu.aron.gymtracker.persistence.Repository;

import java.util.Collections;
import java.util.List;

public class WorkoutService {

    private final Repository<Workout> repository;
    private final CsvWorkoutStorage storage;

    public WorkoutService(Repository<Workout> repository, CsvWorkoutStorage storage) {
        if (repository == null || storage == null) {
            throw new IllegalArgumentException("Repository and storage are required");
        }
        this.repository = repository;
        this.storage = storage;
    }

    public Workout logWorkout(Workout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("Workout cannot be null");
        }
        return repository.save(workout);
    }

    public Workout findWorkout(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new WorkoutNotFoundException(id));
    }

    public List<Workout> listWorkouts() {
        List<Workout> workouts = repository.findAll();
        Collections.sort(workouts);
        return workouts;
    }

    public void deleteWorkout(int id) {
        if (!repository.delete(id)) {
            throw new WorkoutNotFoundException(id);
        }
    }

    public int loadData() {
        List<Workout> loaded = storage.load();
        for (Workout workout : loaded) {
            repository.save(workout);
        }
        return loaded.size();
    }

    public void saveData() {
        storage.save(repository.findAll());
    }
}
