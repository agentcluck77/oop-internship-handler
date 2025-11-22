package controller;
import java.util.ArrayList;
import java.util.List;

import model.Application;
import model.CompanyRep;
import model.Internship;
import model.Student;
import service.IApplicationManager;
import service.IFilterService;
import service.IInternshipManager;
import service.IUserManager;
import service.IValidationService;
import ui.ConsoleUI;
import util.BusinessRules;

/**
 * Handles company representative workflows such as posting internships and reviewing applications.
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
     * Create a new internship posting for the representative's company.
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
     * View internships owned by the representative.
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
     * View applications for one of the representative's internships.
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
     * Approve or reject student applications.
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
     * Toggle internship visibility.
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
     * Edit an existing internship (allowed only before approval).
     */
    public void editInternship() {
        Internship internship = selectInternship("edit");
        if (internship == null) {
            return;
        }

        if (!canModify(internship)) {
            ui.displayError("Only Pending or Rejected internships can be edited.");
            return;
        }

        updateInternshipDetails(internship);
        ui.displayMessage("Internship updated successfully!");
    }

    /**
     * Delete an existing internship (allowed only before approval).
     */
    public void deleteInternship() {
        Internship internship = selectInternship("delete");
        if (internship == null) {
            return;
        }

        if (!canModify(internship)) {
            ui.displayError("Only Pending or Rejected internships can be deleted.");
            return;
        }

        applicationManager.removeApplicationsForInternship(internship.getId());
        internshipManager.removeInternship(internship);
        ui.displayMessage("Internship deleted.");
    }

    private Internship selectInternship(String actionVerb) {
        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());
        internships = filterService.applyFilters(internships);

        if (internships.isEmpty()) {
            ui.displayMessage("No internships available to " + actionVerb + ".");
            return null;
        }

        ui.displayMessage("\n=== My Internships ===");
        ui.displayActiveFilters(filterService.getActiveFiltersDisplay());

        for (int i = 0; i < internships.size(); i++) {
            Internship internship = internships.get(i);
            ui.displayMessage((i + 1) + ". " + internship.getTitle());
            ui.displayMessage("   Status: " + internship.getStatus());
        }

        int choice = ui.getIntInput("\nEnter internship number to " + actionVerb + ": ") - 1;
        if (choice < 0 || choice >= internships.size()) {
            ui.displayError("Invalid choice!");
            return null;
        }

        return internships.get(choice);
    }

    private boolean canModify(Internship internship) {
        return internship.getStatus().equals("Pending") || internship.getStatus().equals("Rejected");
    }

    private void updateInternshipDetails(Internship internship) {
        String title = ui.getInput("Enter Title (leave blank to keep '" + internship.getTitle() + "'): ").trim();
        if (!title.isEmpty()) {
            if (!validationService.isValidFieldLength(title, 1, BusinessRules.MAX_TITLE_LENGTH)) {
                ui.displayError("Invalid title length. Keeping previous value.");
            } else {
                internship.setTitle(title);
            }
        }

        String description = ui.getInput("Enter Description (leave blank to keep current): ").trim();
        if (!description.isEmpty()) {
            if (!validationService.isValidFieldLength(description, 1, BusinessRules.MAX_DESCRIPTION_LENGTH)) {
                ui.displayError("Invalid description length. Keeping previous value.");
            } else {
                internship.setDescription(description);
            }
        }

        String level = ui.getInput("Enter Level (Basic/Intermediate/Advanced) or press Enter to keep " + internship.getLevel() + ": ").trim();
        if (!level.isEmpty()) {
            if (validationService.isValidLevel(level)) {
                String levelLower = level.toLowerCase();
                internship.setLevel(levelLower.substring(0, 1).toUpperCase() + levelLower.substring(1));
            } else {
                ui.displayError("Invalid level. Keeping previous value.");
            }
        }

        String major = ui.getInput("Enter Preferred Major (CSC/EEE/MAE) or press Enter to keep " + internship.getPreferredMajor() + ": ").trim();
        if (!major.isEmpty()) {
            String upperMajor = major.toUpperCase();
            if (validationService.isValidMajor(upperMajor)) {
                internship.setPreferredMajor(upperMajor);
            } else {
                ui.displayError("Invalid major. Keeping previous value.");
            }
        }

        String openDate = ui.getInput("Enter Opening Date (YYYY-MM-DD) or press Enter to keep " + internship.getOpeningDate() + ": ").trim();
        String closeDate = ui.getInput("Enter Closing Date (YYYY-MM-DD) or press Enter to keep " + internship.getClosingDate() + ": ").trim();
        if (!openDate.isEmpty() || !closeDate.isEmpty()) {
            if (openDate.isEmpty() || closeDate.isEmpty()) {
                ui.displayError("Both dates must be provided to update. Keeping previous values.");
            } else if (validationService.isClosingDateValid(openDate, closeDate)) {
                internship.setOpeningDate(openDate);
                internship.setClosingDate(closeDate);
            } else {
                ui.displayError("Invalid date range. Keeping previous values.");
            }
        }

        String slotsInput = ui.getInput("Enter Number of Slots (max " + BusinessRules.MAX_SLOTS_PER_INTERNSHIP +
            ") or press Enter to keep " + internship.getTotalSlots() + ": ").trim();
        if (!slotsInput.isEmpty()) {
            try {
                int slots = Integer.parseInt(slotsInput);
                if (slots > 0 && slots <= BusinessRules.MAX_SLOTS_PER_INTERNSHIP) {
                    internship.setTotalSlots(slots);
                } else {
                    ui.displayError("Invalid slot count. Keeping previous value.");
                }
            } catch (NumberFormatException e) {
                ui.displayError("Invalid slot number. Keeping previous value.");
            }
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
