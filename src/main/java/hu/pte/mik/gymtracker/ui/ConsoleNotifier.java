package hu.pte.mik.gymtracker.ui;

import hu.pte.mik.gymtracker.service.PRListener;

public class ConsoleNotifier implements PRListener {

    @Override
    public void onNewRecord(String exerciseName, double previousBest, double newBest) {
        System.out.printf("New PR on %s! %.1f -> %.1f%n", exerciseName, previousBest, newBest);
    }
}
