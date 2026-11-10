package hu.aron.gymtracker.ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputReader {

    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt) {
        while (true) {
            String text = readText(prompt);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("'" + text + "' is not a whole number, try again.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            String text = readText(prompt).replace(',', '.');
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                System.out.println("'" + text + "' is not a number, try again.");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            String text = readText(prompt);
            if (text.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(text);
            } catch (DateTimeParseException e) {
                System.out.println("Use the format yyyy-mm-dd, try again.");
            }
        }
    }

    public boolean confirm(String prompt) {
        return readText(prompt + " (y/n): ").equalsIgnoreCase("y");
    }
}
