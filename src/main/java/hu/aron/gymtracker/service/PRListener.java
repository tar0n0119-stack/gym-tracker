package hu.aron.gymtracker.service;

public interface PRListener {

    void onNewRecord(String exerciseName, double previousBest, double newBest);
}
