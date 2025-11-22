package controller;
import java.util.List;

import model.Application;
import model.Internship;
import model.Student;
import service.IApplicationManager;
import service.IFilterService;
import service.IInternshipManager;
import ui.ConsoleUI;
import util.BusinessRules;

/**
 * Handles student workflows such as browsing internships and managing applications.
 */
public class StudentController {
    private Student student;
    private IInternshipManager internshipManager;
    private IApplicationManager applicationManager;
    private IFilterService filterService;
    private ConsoleUI ui;

    public StudentController(Student student,
                            IInternshipManager internshipManager,
                            IApplicationManager applicationManager,
                            IFilterService filterService,
                            ConsoleUI ui) {
        this.student = student;
        this.internshipManager = internshipManager;
        this.applicationManager = applicationManager;
        this.filterService = filterService;
        this.ui = ui;
    }

    /**
     * Display internships available to the current student.
     */
    public void viewInternships() {
        List<Internship> internships = internshipManager.getInternshipsForStudent(student);
        internships = filterService.applyFilters(internships);

        if (internships.isEmpty()) {
            ui.displayMessage("No internships available.");
            return;
        }

        ui.displayMessage("\n=== Available Internships ===");
        ui.displayActiveFilters(filterService.getActiveFiltersDisplay());

        for (int i = 0; i < internships.size(); i++) {
            ui.displayInternship(internships.get(i), i);
        }
    }

    /**
     * Apply for one of the currently visible internships.
     */
    public void applyForInternship() {
        if (applicationManager.getApplicationCount(student.getUserId()) >= BusinessRules.MAX_APPLICATIONS_PER_STUDENT) {
            ui.displayMessage("You already have " + BusinessRules.MAX_APPLICATIONS_PER_STUDENT + " pending applications!");
            return;
        }

        List<Internship> internships = internshipManager.getInternshipsForStudent(student);
        internships = filterService.applyFilters(internships);

        if (internships.isEmpty()) {
            ui.displayMessage("No internships available.");
            return;
        }

        viewInternships();
        int choice = ui.getIntInput("\nEnter internship number to apply: ") - 1;

        if (choice >= 0 && choice < internships.size()) {
            Internship internship = internships.get(choice);

            // Check if already applied
            if (applicationManager.hasAppliedToInternship(student.getUserId(), internship.getId())) {
                ui.displayMessage("You have already applied to this internship!");
                return;
            }

            if (applicationManager.applyForInternship(student, internship)) {
                ui.displayMessage("Application submitted successfully!");
            } else {
                ui.displayMessage("Failed to apply. Check eligibility.");
            }
        } else {
            ui.displayError("Invalid choice!");
        }
    }

    /**
     * Show all applications submitted by the student.
     */
    public void viewMyApplications() {
        List<Application> applications = applicationManager.getApplicationsForStudent(student.getUserId());

        if (applications.isEmpty()) {
            ui.displayMessage("No applications found.");
            return;
        }

        ui.displayMessage("\n=== My Applications ===");
        for (int i = 0; i < applications.size(); i++) {
            ui.displayApplication(applications.get(i), i, student);
        }
    }

    /**
     * Accept one of the student's successful placement offers.
     */
    public void acceptPlacement() {
        List<Application> successful = applicationManager.getSuccessfulApplications(student.getUserId());

        if (successful.isEmpty()) {
            ui.displayMessage("No successful applications to accept.");
            return;
        }

        ui.displayMessage("\n=== Successful Applications ===");
        for (int i = 0; i < successful.size(); i++) {
            Application app = successful.get(i);
            ui.displayMessage((i + 1) + ". " + app.getInternship().getTitle());
        }

        int choice = ui.getIntInput("\nEnter number to accept: ") - 1;

        if (choice >= 0 && choice < successful.size()) {
            Application app = successful.get(choice);
            applicationManager.acceptPlacement(student.getUserId(), app);
            app.getInternship().decreaseAvailableSlots();
            ui.displayMessage("Placement accepted! Other applications withdrawn.");
        } else {
            ui.displayError("Invalid choice!");
        }
    }

    /**
     * Request withdrawal from an application.
     */
    public void requestWithdrawal() {
        List<Application> withdrawable = applicationManager.getWithdrawableApplications(student.getUserId());

        if (withdrawable.isEmpty()) {
            ui.displayMessage("No applications available for withdrawal.");
            return;
        }

        ui.displayMessage("\n=== Applications Available for Withdrawal ===");
        for (int i = 0; i < withdrawable.size(); i++) {
            Application app = withdrawable.get(i);
            ui.displayMessage((i + 1) + ". " + app.getInternship().getTitle());
            ui.displayMessage("   Company: " + app.getInternship().getCompanyName());
            ui.displayMessage("   Status: " + app.getStatus());
            if (app.isPlacementAccepted()) {
                ui.displayMessage("   Placement: ACCEPTED");
            }
        }

        int choice = ui.getIntInput("\nEnter application number to withdraw: ") - 1;

        if (choice >= 0 && choice < withdrawable.size()) {
            String reason = ui.getInput("Enter reason for withdrawal: ");

            Application app = withdrawable.get(choice);
            if (applicationManager.requestWithdrawal(student.getUserId(), app.getId(), reason)) {
                ui.displayMessage("Withdrawal request submitted for approval.");
            } else {
                ui.displayError("Failed to submit withdrawal request.");
            }
        } else {
            ui.displayError("Invalid choice!");
        }
    }

    /**
     * Set filters for internship viewing
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
     * Clear all active filters
     */
    public void clearFilters() {
        filterService.clearFilters();
        ui.displayMessage("All filters cleared!");
    }

    /**
     * Change the student's password.
     * @return true if password changed successfully (requires re-login)
     */
    public boolean changePassword() {
        String currentPass = ui.getInput("Enter current password: ");

        if (!student.getPassword().equals(currentPass)) {
            ui.displayError("Current password is incorrect!");
            return false;
        }

        String newPassword = ui.getInput("Enter new password: ");
        String confirmPassword = ui.getInput("Confirm new password: ");

        if (!newPassword.equals(confirmPassword)) {
            ui.displayError("Passwords do not match!");
            return false;
        }

        student.setPassword(newPassword);
        ui.displayMessage("Password changed successfully!");
        return true;
    }
}
