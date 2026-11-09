package hu.aron.gymtracker.persistence;

import hu.aron.gymtracker.domain.Identifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<T extends Identifiable> implements Repository<T> {

    private final Map<Integer, T> store = new HashMap<>();
    private int nextId = 1;

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<T> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public T save(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (item.getId() == 0) {
            item.setId(nextId++);
        } else {
            nextId = Math.max(nextId, item.getId() + 1);
        }
        store.put(item.getId(), item);
        return item;
    }

    @Override
    public boolean delete(int id) {
        return store.remove(id) != null;
    }
}
