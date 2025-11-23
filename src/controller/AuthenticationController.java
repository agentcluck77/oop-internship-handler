package controller;
import model.CompanyRep;
import model.User;
import service.IUserManager;
import service.IValidationService;
import ui.ConsoleUI;
import util.BusinessRules;

/**
 * Handles user login and company representative registration.
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
     * Authenticate a user and return the corresponding domain object.
     * @return authenticated User or null if login failed
     */
    public User login() {
            String id = ui.getInput("Enter User ID: ");
            String password = ui.getInput("Enter Password: ");

            // Step 1: Attempt the standard login (returns User on success, null on ANY failure)
            User user = userManager.login(id, password);

            if (user == null) {
                // Step 2: If login failed, determine the specific reason using helper functions.
                // Check if the User ID exists in the approved list.
                User approvedUser = userManager.getUserById(id);
                boolean isPending = false;
                
                // Check pending list explicitly, as the user could be unapproved.
                for (CompanyRep rep : userManager.getPendingCompanyReps()) {
                    if (rep.getUserId().equals(id)) {
                        isPending = true;
                        break;
                    }
                }

                if (approvedUser != null || isPending) {
                    // SCENARIO: User ID was found (approved or pending), but login failed. 
                    // This definitively means the password was wrong.
                    ui.displayError("Invalid credentials: Incorrect Password!");
                } else {
                    // SCENARIO: User ID was not found anywhere in the system.
                    ui.displayError("Invalid credentials: User ID not found!");
                }
                return null;
            }
            
            // Step 3: Successful Login Logic (Includes the Company Rep approval check)
            if (user instanceof CompanyRep rep) { // Using pattern matching for cleaner code
                if (!rep.isApproved()) {
                    ui.displayMessage("Your registration is awaiting staff approval.");
                    return null;
                }
            }

            ui.displayMessage("Login successful! Welcome, " + user.getName());
            return user;
        }

    /**
     * Register a new company representative account.
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
