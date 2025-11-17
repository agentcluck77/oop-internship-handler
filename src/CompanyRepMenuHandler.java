import java.util.Arrays;

/**
 * Menu handler for company representative interface.
 * Extracted from Main.java lines 261-312 to separate UI from business logic.
 */
public class CompanyRepMenuHandler implements MenuHandler {
    private ConsoleUI ui;
    private CompanyRepController controller;

    public CompanyRepMenuHandler(ConsoleUI ui, CompanyRepController controller) {
        this.ui = ui;
        this.controller = controller;
    }

    @Override
    public void show() {
        ui.displayMenu("Company Representative Menu", Arrays.asList(
            "Create Internship",
            "View My Internships",
            "View Applications",
            "Approve/Reject Application",
            "Toggle Internship Visibility",
            "Set Filters",
            "Clear Filters",
            "Change Password",
            "Logout"
        ));
    }

    /**
     * Handle menu choice
     * Extracted from Main.java lines 275-311
     * @return true to continue, false to logout
     */
    @Override
    public boolean handleChoice() {
        int choice = ui.getIntInput("Choose option: ");

        switch (choice) {
            case 1:
                controller.createInternship();
                return true;
            case 2:
                controller.viewMyInternships();
                return true;
            case 3:
                controller.viewApplicationsForInternship();
                return true;
            case 4:
                controller.approveRejectApplication();
                return true;
            case 5:
                controller.toggleVisibility();
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
