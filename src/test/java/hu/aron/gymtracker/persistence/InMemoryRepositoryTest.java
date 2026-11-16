package hu.aron.gymtracker.persistence;

import hu.aron.gymtracker.domain.Workout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryRepositoryTest {

    private Repository<Workout> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
    }

    @Test
    void saveAssignsIncreasingIds() {
        Workout first = repository.save(new Workout(LocalDate.now()));
        Workout second = repository.save(new Workout(LocalDate.now()));

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
    }

    @Test
    void findByIdReturnsSavedWorkout() {
        Workout saved = repository.save(new Workout(LocalDate.now()));

        assertTrue(repository.findById(saved.getId()).isPresent());
        assertFalse(repository.findById(99).isPresent());
    }

    @Test
    void deleteRemovesWorkoutOnlyOnce() {
        Workout saved = repository.save(new Workout(LocalDate.now()));

        assertTrue(repository.delete(saved.getId()));
        assertFalse(repository.delete(saved.getId()));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void existingIdIsKeptAndNewIdsContinueAfterIt() {
        Workout loaded = new Workout(LocalDate.now());
        loaded.setId(7);
        repository.save(loaded);

        Workout fresh = repository.save(new Workout(LocalDate.now()));

        assertEquals(7, loaded.getId());
        assertEquals(8, fresh.getId());
    }

    @Test
    void findAllReturnsACopy() {
        repository.save(new Workout(LocalDate.now()));
        repository.findAll().clear();

        assertEquals(1, repository.findAll().size());
    }
}
