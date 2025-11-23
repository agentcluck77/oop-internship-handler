package controller;
import java.util.List;

import model.Application;
import model.CompanyRep;
import model.Filter;
import model.Internship;
import model.Staff;
import model.Student;
import service.IApplicationManager;
import service.IFilterService;
import service.IInternshipManager;
import service.IUserManager;
import ui.ConsoleUI;

/**
 * Coordinates staff capabilities such as approvals, reporting, and filters.
 */
public class StaffController {
    private Staff staff;
    private IUserManager userManager;
    private IInternshipManager internshipManager;
    private IApplicationManager applicationManager;
    private IFilterService filterService;
    private ConsoleUI ui;

    public StaffController(Staff staff,
                          IUserManager userManager,
                          IInternshipManager internshipManager,
                          IApplicationManager applicationManager,
                          IFilterService filterService,
                          ConsoleUI ui) {
        this.staff = staff;
        this.userManager = userManager;
        this.internshipManager = internshipManager;
        this.applicationManager = applicationManager;
        this.filterService = filterService;
        this.ui = ui;
    }

    /**
     * Approve or reject company representative registrations.
     */
    public void approveRejectCompany() {
        List<CompanyRep> pending = userManager.getPendingCompanyReps();

        if (pending.isEmpty()) {
            ui.displayMessage("No pending registrations.");
            return;
        }

        ui.displayMessage("\n=== Pending Registrations ===");
        for (int i = 0; i < pending.size(); i++) {
            CompanyRep rep = pending.get(i);
            ui.displayMessage((i + 1) + ". " + rep.getName());
            ui.displayMessage("   Company: " + rep.getCompanyName());
            ui.displayMessage("   Email: " + rep.getUserId());
        }

        int choice = ui.getIntInput("\nEnter number: ") - 1;

        if (choice >= 0 && choice < pending.size()) {
            String decision = ui.getInput("Approve or Reject? (A/R): ").toUpperCase();

            if (decision.equals("A")) {
                userManager.approveCompanyRep(pending.get(choice));
                ui.displayMessage("Company approved!");
            } else if (decision.equals("R")) {
                userManager.rejectCompanyRep(pending.get(choice));
                ui.displayMessage("Company rejected!");
            }
        }
    }

    /**
     * Approve or reject internship postings.
     */
    public void approveRejectInternship() {
        List<Internship> pending = internshipManager.getPendingInternships();

        if (pending.isEmpty()) {
            ui.displayMessage("No pending internships.");
            return;
        }

        ui.displayMessage("\n=== Pending Internships ===");
        for (int i = 0; i < pending.size(); i++) {
            Internship internship = pending.get(i);
            ui.displayMessage((i + 1) + ". " + internship.getTitle());
            ui.displayMessage("   Company: " + internship.getCompanyName());
            ui.displayMessage("   Level: " + internship.getLevel());
        }

        int choice = ui.getIntInput("\nEnter number: ") - 1;

        if (choice >= 0 && choice < pending.size()) {
            String decision = ui.getInput("Approve or Reject? (A/R): ").toUpperCase();

            Internship internship = pending.get(choice);
            if (decision.equals("A")) {
                internship.setStatus("Approved");
                internship.setVisible(true);
                ui.displayMessage("Internship approved!");
            } else if (decision.equals("R")) {
                internship.setStatus("Rejected");
                ui.displayMessage("Internship rejected!");
            }
        }
    }

    /**
     * Approve or reject withdrawal requests.
     */
    public void approveRejectWithdrawal() {
        List<Application> withdrawals = applicationManager.getPendingWithdrawals();

        if (withdrawals.isEmpty()) {
            ui.displayMessage("No pending withdrawals.");
            return;
        }

        ui.displayMessage("\n=== Pending Withdrawals ===");
        for (int i = 0; i < withdrawals.size(); i++) {
            Application app = withdrawals.get(i);
            Student student = (Student) userManager.getUserById(app.getStudentId());
            if (student == null) {
                ui.displayMessage((i + 1) + ". [Unknown Student - ID: " + app.getStudentId() + "]");
            } else {
                ui.displayMessage((i + 1) + ". " + student.getName());
            }
            ui.displayMessage("   Internship: " + app.getInternship().getTitle());
            ui.displayMessage("   Reason: " + app.getWithdrawalReason());
        }

        int choice = ui.getIntInput("\nEnter number: ") - 1;

        if (choice >= 0 && choice < withdrawals.size()) {
            String decision = ui.getInput("Approve or Reject? (A/R): ").toUpperCase();

            Application app = withdrawals.get(choice);
            if (decision.equals("A")) {
                applicationManager.approveWithdrawal(app);
                ui.displayMessage("Withdrawal approved!");
            } else if (decision.equals("R")) {
                app.setWithdrawalStatus("Rejected");
                ui.displayMessage("Withdrawal rejected! Student can still accept placement.");
            }
        }
    }

    /**
     * Generate a filtered internship report.
     */
    public void generateReport() {
        ui.displayMessage("\n=== Generate Report (Using Current Filters) ===");

        Filter currentFilter = filterService.getCurrentFilter();
        List<Internship> filtered = internshipManager.generateReport(
            currentFilter.getStatus(),
            currentFilter.getMajor(),
            currentFilter.getLevel()
        );

        ui.displayMessage("\n=== Report ===");
        ui.displayActiveFilters(filterService.getActiveFiltersDisplay());
        ui.displayMessage("Total Internships: " + filtered.size());

        for (Internship internship : filtered) {
            ui.displayMessage("\n- " + internship.getTitle());
            ui.displayMessage("  Company: " + internship.getCompanyName());
            ui.displayMessage("  Status: " + internship.getStatus());
            ui.displayMessage("  Level: " + internship.getLevel());
            ui.displayMessage("  Major: " + internship.getPreferredMajor());
        }
    }

    /**
     * View all internships in the system.
     */
    public void viewAllInternships() {
        List<Internship> all = internshipManager.getAllInternships();
        all = filterService.applyFilters(all);

        if (all.isEmpty()) {
            ui.displayMessage("No internships in system.");
            return;
        }

        ui.displayMessage("\n=== All Internships ===");
        ui.displayActiveFilters(filterService.getActiveFiltersDisplay());

        for (Internship internship : all) {
            ui.displayMessage("- " + internship.getTitle());
            ui.displayMessage("  Company: " + internship.getCompanyName());
            ui.displayMessage("  Status: " + internship.getStatus());
            ui.displayMessage("  Slots: " + internship.getAvailableSlots() + "/" + internship.getTotalSlots());
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
     * Change the staff member's password.
     * @return true if password changed successfully (requires re-login)
     */
    public boolean changePassword() {
        String currentPass = ui.getInput("Enter current password: ");

        if (!staff.getPassword().equals(currentPass)) {
            ui.displayError("Current password is incorrect!");
            return false;
        }

        String newPassword = ui.getInput("Enter new password: ");
        String confirmPassword = ui.getInput("Confirm new password: ");

        if (!newPassword.equals(confirmPassword)) {
            ui.displayError("Passwords do not match!");
            return false;
        }

        staff.setPassword(newPassword);
        ui.displayMessage("Password changed successfully!");
        return true;
    }
}
