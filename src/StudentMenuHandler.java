import java.util.Arrays;

/**
 * Menu handler for student interface.
 * Extracted from Main.java lines 208-259 to separate UI from business logic.
 */
public class StudentMenuHandler implements MenuHandler {
    private ConsoleUI ui;
    private StudentController controller;

    public StudentMenuHandler(ConsoleUI ui, StudentController controller) {
        this.ui = ui;
        this.controller = controller;
    }

    @Override
    public void show() {
        ui.displayMenu("Student Menu", Arrays.asList(
            "View Internships",
            "Apply for Internship",
            "View My Applications",
            "Accept Placement",
            "Request Withdrawal",
            "Set Filters",
            "Clear Filters",
            "Change Password",
            "Logout"
        ));
    }

    /**
     * Handle menu choice
     * Extracted from Main.java lines 222-258
     * @return true to continue, false to logout
     */
    @Override
    public boolean handleChoice() {
        int choice = ui.getIntInput("Choose option: ");

        switch (choice) {
            case 1:
                controller.viewInternships();
                return true;
            case 2:
                controller.applyForInternship();
                return true;
            case 3:
                controller.viewMyApplications();
                return true;
            case 4:
                controller.acceptPlacement();
                return true;
            case 5:
                controller.requestWithdrawal();
                return true;
            case 6:
                controller.setFilters();
                return true;
            case 7:
                controller.clearFilters();
                return true;
            case 8:
                if (controller.changePassword()) {
                    ui.displayMessage("Please login again with your new password.");
                    return false; // logout
                }
                return true;
            case 9:
                controller.clearFilters(); // Clear filters on logout
                return false; // logout
            default:
                ui.displayError("Invalid option!");
                return true;
        }
    }
}
