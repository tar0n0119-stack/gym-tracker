package hu.pte.mik.gymtracker.service;

public interface PRListener {

    void onNewRecord(String exerciseName, double previousBest, double newBest);
}
