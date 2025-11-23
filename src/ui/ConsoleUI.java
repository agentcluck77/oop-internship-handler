package ui;
import java.util.List;

import model.Application;
import model.Internship;
import model.Student;

/**
 * Interface for console user interface operations.
 * Abstracts all console I/O to enable testability and follow Dependency Inversion Principle.
 */
public interface ConsoleUI {
    /**
     * Display a regular message to the user
     */
    void displayMessage(String message);

    /**
     * Display an error message to the user
     */
    void displayError(String message);

    /**
     * Get string input from user with a prompt
     */
    String getInput(String prompt);

    /**
     * Get integer input from user with validation
     */
    int getIntInput(String prompt);

    /**
     * Display a menu with title and options
     */
    void displayMenu(String title, List<String> options);

    /**
     * Display internship details
     */
    void displayInternship(Internship internship, int index);

    /**
     * Display application details
     */
    void displayApplication(Application app, int index, Student student);

    /**
     * Display active filters
     */
    void displayActiveFilters(String filterDisplay);

    /**
     * Display a separator line
     */
    void displaySeparator();
}
