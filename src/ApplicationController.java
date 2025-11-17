import java.util.Arrays;

/**
 * Main application controller that manages the overall application flow.
 * Extracted from Main.java lines 27-55 to follow Single Responsibility Principle.
 * NOW FOLLOWS OPEN/CLOSED PRINCIPLE using factory registry pattern.
 */
public class ApplicationController {
    private ConsoleUI ui;
    private AuthenticationController authController;
    private MenuHandlerFactoryRegistry factoryRegistry;

    public ApplicationController(ConsoleUI ui,
                                AuthenticationController authController,
                                MenuHandlerFactoryRegistry factoryRegistry) {
        this.ui = ui;
        this.authController = authController;
        this.factoryRegistry = factoryRegistry;
    }

    /**
     * Run the main application loop
     * Extracted from Main.java lines 27-54
     */
    public void run() {
        ui.displayMessage("=== Internship Placement Management System ===\n");

        while (true) {
            ui.displayMenu("Main Menu", Arrays.asList(
                "Login",
                "Register (Company Representative)",
                "Exit"
            ));

            int choice = ui.getIntInput("Choose option: ");

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    authController.registerCompanyRep();
                    break;
                case 3:
                    ui.displayMessage("Goodbye!");
                    return;
                default:
                    ui.displayError("Invalid option!");
            }
        }
    }

    /**
     * Handle login and route to appropriate menu
     */
    private void handleLogin() {
        User user = authController.login();
        if (user == null) {
            return; // Login failed
        }

        // Use factory registry to create appropriate menu handler
        // This follows Open/Closed Principle - no need to modify this code when adding new user types
        MenuHandler menuHandler = factoryRegistry.createMenuHandler(user);

        // Run menu loop
        while (true) {
            menuHandler.show();
            boolean continueMenu = menuHandler.handleChoice();
            if (!continueMenu) {
                break; // User logged out
            }
        }
    }
}
