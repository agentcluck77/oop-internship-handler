import java.util.List;
import java.util.Scanner;

/**
 * Console-based implementation of the ConsoleUI interface.
 * Extracted from Main.java to separate UI concerns from business logic.
 */
public class ConsoleUIImpl implements ConsoleUI {
    private Scanner scanner;

    public ConsoleUIImpl() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayError(String message) {
        System.out.println(message);
    }

    @Override
    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Get integer input with validation
     * Extracted from Main.java lines 1023-1032
     */
    @Override
    public int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    @Override
    public void displayMenu(String title, List<String> options) {
        System.out.println("\n=== " + title + " ===");
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
    }

    /**
     * Display internship details
     * Extracted from Main.java lines 438-444
     */
    @Override
    public void displayInternship(Internship internship, int index) {
        System.out.println((index + 1) + ". " + internship.getTitle());
        System.out.println("   Company: " + internship.getCompanyName());
        System.out.println("   Level: " + internship.getLevel());
        System.out.println("   Major: " + internship.getPreferredMajor());
        System.out.println("   Closing Date: " + internship.getClosingDate());
        System.out.println("   Available Slots: " + internship.getAvailableSlots());
    }

    @Override
    public void displayApplication(Application app, int index, Student student) {
        Internship internship = app.getInternship();

        System.out.println((index + 1) + ". " + internship.getTitle());
        System.out.println("   Company: " + internship.getCompanyName());
        System.out.println("   Level: " + internship.getLevel());
        System.out.println("   Major: " + internship.getPreferredMajor());
        System.out.println("   Closing Date: " + internship.getClosingDate());
        System.out.println("   Application Status: " + app.getStatus());

        // Show visibility status
        if (!internship.isVisible()) {
            System.out.println("   [Currently hidden from public listing]");
        }

        if (app.isPlacementAccepted()) {
            System.out.println("   Placement: ACCEPTED");
        }
        if (app.getWithdrawalStatus() != null) {
            System.out.println("   Withdrawal Status: " + app.getWithdrawalStatus());
        }
    }

    @Override
    public void displayActiveFilters(String filterDisplay) {
        if (filterDisplay != null && !filterDisplay.isEmpty()) {
            System.out.println(filterDisplay);
        }
    }

    @Override
    public void displaySeparator() {
        System.out.println("-----------------------------------");
    }
}
