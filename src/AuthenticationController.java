/**
 * Controller for authentication operations (login and registration).
 * Extracted from Main.java lines 131-206 to follow Single Responsibility Principle.
 * NOW FOLLOWS DEPENDENCY INVERSION PRINCIPLE using interfaces.
 */
public class AuthenticationController {
    private IUserManager userManager;
    private IValidationService validationService;
    private ConsoleUI ui;

    public AuthenticationController(IUserManager userManager,
                                    IValidationService validationService,
                                    ConsoleUI ui) {
        this.userManager = userManager;
        this.validationService = validationService;
        this.ui = ui;
    }

    /**
     * Handle user login
     * Extracted from Main.java lines 131-153
     * @return authenticated User or null if login failed
     */
    public User login() {
        String id = ui.getInput("Enter User ID: ");
        String password = ui.getInput("Enter Password: ");

        User user = userManager.login(id, password);

        if (user == null) {
            ui.displayError("Invalid credentials!");
            return null;
        }

        ui.displayMessage("Login successful! Welcome, " + user.getName());
        return user;
    }

    /**
     * Register a new company representative
     * Extracted from Main.java lines 155-206
     */
    public void registerCompanyRep() {
        String email = ui.getInput("Enter Email: ").trim();

        if (!validationService.isValidEmail(email)) {
            ui.displayError("Invalid email format! Must contain @ and a domain.");
            return;
        }

        String password = ui.getInput("Enter Password: ");

        if (!validationService.isValidPassword(password)) {
            ui.displayError("Password must be at least " + BusinessRules.MIN_PASSWORD_LENGTH + " characters!");
            return;
        }

        String name = ui.getInput("Enter Name: ").trim();
        if (!validationService.isValidFieldLength(name, 1, BusinessRules.MAX_FIELD_LENGTH)) {
            ui.displayError("Name must be between 1 and " + BusinessRules.MAX_FIELD_LENGTH + " characters!");
            return;
        }

        String company = ui.getInput("Enter Company Name: ").trim();
        if (!validationService.isValidFieldLength(company, 1, BusinessRules.MAX_FIELD_LENGTH)) {
            ui.displayError("Company name must be between 1 and " + BusinessRules.MAX_FIELD_LENGTH + " characters!");
            return;
        }

        String department = ui.getInput("Enter Department: ").trim();
        if (!validationService.isValidFieldLength(department, 1, BusinessRules.MAX_FIELD_LENGTH)) {
            ui.displayError("Department must be between 1 and " + BusinessRules.MAX_FIELD_LENGTH + " characters!");
            return;
        }

        String position = ui.getInput("Enter Position: ").trim();
        if (!validationService.isValidFieldLength(position, 1, BusinessRules.MAX_FIELD_LENGTH)) {
            ui.displayError("Position must be between 1 and " + BusinessRules.MAX_FIELD_LENGTH + " characters!");
            return;
        }

        CompanyRep rep = new CompanyRep(email, password, name, company, department, position);
        userManager.addPendingCompanyRep(rep);

        ui.displayMessage("Registration submitted! Awaiting approval.");
    }
}
