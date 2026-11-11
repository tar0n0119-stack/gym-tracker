package hu.aron.gymtracker.service;

import hu.aron.gymtracker.domain.Exercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PRTracker {

    private final Map<String, Double> bestVolumes = new HashMap<>();
    private final List<PRListener> listeners = new ArrayList<>();

    public void addListener(PRListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        listeners.add(listener);
    }

    public void removeListener(PRListener listener) {
        listeners.remove(listener);
    }

    public void record(Exercise exercise) {
        bestVolumes.merge(keyOf(exercise), exercise.calculateVolume(), Math::max);
    }

    public boolean check(Exercise exercise) {
        String key = keyOf(exercise);
        double volume = exercise.calculateVolume();
        Double previousBest = bestVolumes.get(key);

        if (previousBest != null && volume <= previousBest) {
            return false;
        }
        bestVolumes.put(key, volume);
        if (previousBest == null) {
            return false;
        }
        for (PRListener listener : listeners) {
            listener.onNewRecord(exercise.getName(), previousBest, volume);
        }
        return true;
    }

    private String keyOf(Exercise exercise) {
        return exercise.getType() + ":" + exercise.getName().toLowerCase();
    }
}
