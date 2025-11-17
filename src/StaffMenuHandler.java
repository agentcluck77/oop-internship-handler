import java.util.Arrays;

/**
 * Menu handler for staff interactions.
 */
public class StaffMenuHandler implements MenuHandler {
    private ConsoleUI ui;
    private StaffController controller;

    public StaffMenuHandler(ConsoleUI ui, StaffController controller) {
        this.ui = ui;
        this.controller = controller;
    }

    @Override
    public void show() {
        ui.displayMenu("Staff Menu", Arrays.asList(
            "Approve/Reject Company Registration",
            "Approve/Reject Internship",
            "Approve/Reject Withdrawal Request",
            "Generate Report",
            "View All Internships",
            "Set Filters",
            "Clear Filters",
            "Change Password",
            "Logout"
        ));
    }

    /**
     * Handle menu choice.
     * @return true to continue, false to logout
     */
    @Override
    public boolean handleChoice() {
        int choice = ui.getIntInput("Choose option: ");

        switch (choice) {
            case 1:
                controller.approveRejectCompany();
                return true;
            case 2:
                controller.approveRejectInternship();
                return true;
            case 3:
                controller.approveRejectWithdrawal();
                return true;
            case 4:
                controller.generateReport();
                return true;
            case 5:
                controller.viewAllInternships();
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
