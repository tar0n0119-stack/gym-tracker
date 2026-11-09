package hu.aron.gymtracker.persistence;

import hu.aron.gymtracker.domain.Identifiable;

import java.util.List;
import java.util.Optional;

public interface Repository<T extends Identifiable> {

    List<T> findAll();

    Optional<T> findById(int id);

    T save(T item);

    boolean delete(int id);
}
