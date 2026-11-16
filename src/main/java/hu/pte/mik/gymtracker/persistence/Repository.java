package hu.pte.mik.gymtracker.persistence;

import hu.pte.mik.gymtracker.domain.Identifiable;

import java.util.List;
import java.util.Optional;

public interface Repository<T extends Identifiable> {

    List<T> findAll();

    Optional<T> findById(int id);

    T save(T item);

    boolean delete(int id);
}
