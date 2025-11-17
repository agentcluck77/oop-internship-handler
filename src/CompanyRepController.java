import java.util.ArrayList;
import java.util.List;

/**
 * Controller for company representative operations.
 * Extracted from Main.java lines 594-851 to follow Single Responsibility Principle.
 * NOW FOLLOWS DEPENDENCY INVERSION PRINCIPLE using interfaces.
 */
public class CompanyRepController {
    private CompanyRep rep;
    private IInternshipManager internshipManager;
    private IApplicationManager applicationManager;
    private IUserManager userManager;
    private IFilterService filterService;
    private IValidationService validationService;
    private ConsoleUI ui;

    public CompanyRepController(CompanyRep rep,
                               IInternshipManager internshipManager,
                               IApplicationManager applicationManager,
                               IUserManager userManager,
                               IFilterService filterService,
                               IValidationService validationService,
                               ConsoleUI ui) {
        this.rep = rep;
        this.internshipManager = internshipManager;
        this.applicationManager = applicationManager;
        this.userManager = userManager;
        this.filterService = filterService;
        this.validationService = validationService;
        this.ui = ui;
    }

    /**
     * Create a new internship
     * Extracted from Main.java lines 595-672
     */
    public void createInternship() {
        if (internshipManager.getInternshipCountForCompany(rep.getUserId()) >= BusinessRules.MAX_INTERNSHIPS_PER_COMPANY) {
            ui.displayMessage("You already have " + BusinessRules.MAX_INTERNSHIPS_PER_COMPANY + " internships!");
            return;
        }

        String title = ui.getInput("Enter Title: ").trim();
        if (!validationService.isValidFieldLength(title, 1, BusinessRules.MAX_TITLE_LENGTH)) {
            ui.displayError("Title must be between 1 and " + BusinessRules.MAX_TITLE_LENGTH + " characters!");
            return;
        }

        String description = ui.getInput("Enter Description: ").trim();
        if (!validationService.isValidFieldLength(description, 1, BusinessRules.MAX_DESCRIPTION_LENGTH)) {
            ui.displayError("Description must be between 1 and " + BusinessRules.MAX_DESCRIPTION_LENGTH + " characters!");
            return;
        }

        String level;
        while (true) {
            level = ui.getInput("Enter Level (Basic/Intermediate/Advanced): ").trim();
            if (validationService.isValidLevel(level)) {
                String levelLower = level.toLowerCase();
                level = levelLower.substring(0, 1).toUpperCase() + levelLower.substring(1);
                break;
            } else {
                ui.displayError("Invalid level! Please enter Basic, Intermediate, or Advanced.");
            }
        }

        String major;
        while (true) {
            major = ui.getInput("Enter Preferred Major (CSC/EEE/MAE): ").trim().toUpperCase();
            if (validationService.isValidMajor(major)) {
                break;
            } else {
                ui.displayError("Invalid major! Please enter CSC, EEE, or MAE.");
            }
        }

        String openDate;
        String closeDate;
        while (true) {
            openDate = ui.getInput("Enter Opening Date (YYYY-MM-DD): ");
            closeDate = ui.getInput("Enter Closing Date (YYYY-MM-DD): ");

            if (validationService.isClosingDateValid(openDate, closeDate)) {
                break;
            } else {
                ui.displayError("Closing date must be after opening date! Please try again.");
            }
        }

        int slots;
        while (true) {
            slots = ui.getIntInput("Enter Number of Slots (max " + BusinessRules.MAX_SLOTS_PER_INTERNSHIP + "): ");
            if (slots > 0 && slots <= BusinessRules.MAX_SLOTS_PER_INTERNSHIP) {
                break;
            } else {
                ui.displayError("Slots must be between 1 and " + BusinessRules.MAX_SLOTS_PER_INTERNSHIP + "!");
            }
        }

        Internship internship = new Internship(title, description, level, major,
                openDate, closeDate, rep.getCompanyName(), rep.getUserId(), slots);

        internshipManager.addInternship(internship);
        ui.displayMessage("Internship created! Awaiting approval.");
    }

    /**
     * View company's internships
     * Extracted from Main.java lines 685-703
     */
    public void viewMyInternships() {
        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());
        internships = filterService.applyFilters(internships);

        if (internships.isEmpty()) {
            ui.displayMessage("No internships created yet.");
            return;
        }

        ui.displayMessage("\n=== My Internships ===");
        ui.displayActiveFilters(filterService.getActiveFiltersDisplay());

        for (int i = 0; i < internships.size(); i++) {
            Internship internship = internships.get(i);
            ui.displayMessage((i + 1) + ". " + internship.getTitle());
            ui.displayMessage("   Status: " + internship.getStatus());
            ui.displayMessage("   Visible: " + (internship.isVisible() ? "Yes" : "No"));
            ui.displayMessage("   Slots: " + internship.getAvailableSlots() + "/" + internship.getTotalSlots());
        }
    }

    /**
     * View applications for a specific internship
     * Extracted from Main.java lines 705-751
     */
    public void viewApplicationsForInternship() {
        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());
        List<Internship> filteredInternships = filterService.applyFilters(internships);

        if (filteredInternships.isEmpty()) {
            ui.displayMessage("No internships found.");
            return;
        }

        ui.displayMessage("\n=== My Internships ===");
        ui.displayActiveFilters(filterService.getActiveFiltersDisplay());

        for (int i = 0; i < filteredInternships.size(); i++) {
            Internship internship = filteredInternships.get(i);
            ui.displayMessage((i + 1) + ". " + internship.getTitle());
            ui.displayMessage("   Status: " + internship.getStatus());
        }

        int choice = ui.getIntInput("\nEnter internship number: ") - 1;

        if (choice >= 0 && choice < filteredInternships.size()) {
            Internship internship = filteredInternships.get(choice);
            List<Application> applications = applicationManager.getApplicationsForInternship(internship.getId());

            if (applications.isEmpty()) {
                ui.displayMessage("No applications for this internship.");
                return;
            }

            ui.displayMessage("\n=== Applications ===");
            for (int i = 0; i < applications.size(); i++) {
                Application app = applications.get(i);
                Student student = (Student) userManager.getUserById(app.getStudentId());
                if (student == null) {
                    ui.displayMessage((i + 1) + ". [Unknown Student - ID: " + app.getStudentId() + "]");
                    ui.displayMessage("   Status: " + app.getStatus());
                } else {
                    ui.displayMessage((i + 1) + ". " + student.getName());
                    ui.displayMessage("   Year: " + student.getYear() + ", Major: " + student.getMajor());
                    ui.displayMessage("   Status: " + app.getStatus());
                }
            }
        }
    }

    /**
     * Approve or reject an application
     * Extracted from Main.java lines 753-819
     */
    public void approveRejectApplication() {
        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());
        List<Internship> filteredInternships = filterService.applyFilters(internships);

        if (filteredInternships.isEmpty()) {
            ui.displayMessage("No internships found.");
            return;
        }

        ui.displayMessage("\n=== My Internships ===");
        ui.displayActiveFilters(filterService.getActiveFiltersDisplay());

        for (int i = 0; i < filteredInternships.size(); i++) {
            Internship internship = filteredInternships.get(i);
            ui.displayMessage((i + 1) + ". " + internship.getTitle());
            ui.displayMessage("   Status: " + internship.getStatus());
        }

        int internChoice = ui.getIntInput("\nEnter internship number: ") - 1;

        if (internChoice >= 0 && internChoice < filteredInternships.size()) {
            Internship internship = filteredInternships.get(internChoice);
            List<Application> applications = applicationManager.getApplicationsForInternship(internship.getId());

            List<Application> pending = new ArrayList<>();
            for (Application app : applications) {
                if (app.getStatus().equals("Pending")) {
                    pending.add(app);
                }
            }

            if (pending.isEmpty()) {
                ui.displayMessage("No pending applications.");
                return;
            }

            ui.displayMessage("\n=== Pending Applications ===");
            for (int i = 0; i < pending.size(); i++) {
                Application app = pending.get(i);
                Student student = (Student) userManager.getUserById(app.getStudentId());
                if (student == null) {
                    ui.displayMessage((i + 1) + ". [Unknown Student - ID: " + app.getStudentId() + "]");
                } else {
                    ui.displayMessage((i + 1) + ". " + student.getName());
                }
            }

            int appChoice = ui.getIntInput("\nEnter application number: ") - 1;

            if (appChoice >= 0 && appChoice < pending.size()) {
                String decision = ui.getInput("Approve or Reject? (A/R): ").toUpperCase();

                if (decision.equals("A")) {
                    applicationManager.updateApplicationStatus(pending.get(appChoice), "Successful");
                    ui.displayMessage("Application approved! Student can now accept placement.");
                } else if (decision.equals("R")) {
                    applicationManager.updateApplicationStatus(pending.get(appChoice), "Unsuccessful");
                    ui.displayMessage("Application rejected!");
                }
            }
        }
    }

    /**
     * Toggle internship visibility
     * Extracted from Main.java lines 821-851
     */
    public void toggleVisibility() {
        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());
        List<Internship> filteredInternships = filterService.applyFilters(internships);

        if (filteredInternships.isEmpty()) {
            ui.displayMessage("No internships found.");
            return;
        }

        ui.displayMessage("\n=== My Internships ===");
        ui.displayActiveFilters(filterService.getActiveFiltersDisplay());

        for (int i = 0; i < filteredInternships.size(); i++) {
            Internship internship = filteredInternships.get(i);
            ui.displayMessage((i + 1) + ". " + internship.getTitle());
            ui.displayMessage("   Status: " + internship.getStatus());
            ui.displayMessage("   Visible: " + (internship.isVisible() ? "Yes" : "No"));
        }

        int choice = ui.getIntInput("\nEnter internship number to toggle visibility: ") - 1;

        if (choice >= 0 && choice < filteredInternships.size()) {
            Internship internship = filteredInternships.get(choice);
            internship.toggleVisibility();
            ui.displayMessage("Visibility toggled to: " + (internship.isVisible() ? "On" : "Off"));
        } else {
            ui.displayError("Invalid choice!");
        }
    }

    /**
     * Set filters
     */
    public void setFilters() {
        ui.displayMessage("\n=== Set Filters ===");
        String status = ui.getInput("Filter by Status (Pending/Approved/Rejected/Filled) or press Enter to skip: ");
        String major = ui.getInput("Filter by Major (CSC/EEE/MAE) or press Enter to skip: ");
        String level = ui.getInput("Filter by Level (Basic/Intermediate/Advanced) or press Enter to skip: ");
        String date = ui.getInput("Filter by Closing Date (YYYY-MM-DD) or press Enter to skip: ");

        filterService.setFilters(status, major, level, date);
        ui.displayMessage("Filters applied! They will persist across menu pages.");
    }

    /**
     * Clear filters
     */
    public void clearFilters() {
        filterService.clearFilters();
        ui.displayMessage("All filters cleared!");
    }

    /**
     * Change password
     * @return true if password changed successfully (requires re-login)
     */
    public boolean changePassword() {
        String currentPass = ui.getInput("Enter current password: ");

        if (!rep.getPassword().equals(currentPass)) {
            ui.displayError("Current password is incorrect!");
            return false;
        }

        String newPassword = ui.getInput("Enter new password: ");
        String confirmPassword = ui.getInput("Confirm new password: ");

        if (!newPassword.equals(confirmPassword)) {
            ui.displayError("Passwords do not match!");
            return false;
        }

        rep.setPassword(newPassword);
        ui.displayMessage("Password changed successfully!");
        return true;
    }
}
