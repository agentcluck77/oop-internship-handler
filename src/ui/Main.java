package ui;

import model.*;
import repository.*;
import service.*;
import util.*;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static UserManager userManager = new UserManager();
    private static InternshipManager internshipManager = new InternshipManager();
    private static ApplicationManager applicationManager = new ApplicationManager();
    private static AuthenticationService authService;
    private static StudentService studentService;
    private static CompanyRepService companyRepService;
    private static CareerCenterService careerCenterService;
    private static Session session = new Session();

    public static void main(String[] args) {
        initializeServices();
        loadInitialData();
        
        System.out.println("=== Welcome to Internship Placement Management System ===");
        
        while (true) {
            if (!session.isLoggedIn()) {
                showMainMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private static void initializeServices() {
        authService = new AuthenticationService(userManager);
        studentService = new StudentService(internshipManager, applicationManager);
        companyRepService = new CompanyRepService(internshipManager, applicationManager);
        careerCenterService = new CareerCenterService(userManager, internshipManager, applicationManager);
    }

    private static void loadInitialData() {
        FileLoader.loadStudents("students.csv", userManager);
        FileLoader.loadStaff("staff.csv", userManager);
    }

    private static void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Login");
        System.out.println("2. Register as Company Representative");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1 -> handleLogin();
            case 2 -> handleCompanyRepRegistration();
            case 3 -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid option");
        }
    }

    private static void handleLogin() {
        System.out.print("User ID: ");
        String userId = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        User user = authService.login(userId, password);
        
        if (user != null) {
            session.setCurrentUser(user);
            System.out.println("Login successful! Welcome, " + user.getName());
        } else {
            String error = authService.getLoginError(userId, password);
            System.out.println("Login failed: " + (error != null ? error : "Invalid credentials"));
        }
    }

    private static void handleCompanyRepRegistration() {
        System.out.println("\n=== Company Representative Registration ===");
        
        String userId;
        while (true) {
            System.out.print("Corporate Email (User ID): ");
            userId = scanner.nextLine().trim();
            if (ValidationUtil.isCorporateEmail(userId)) {
                break;
            }
            System.out.println("Error! " + ValidationUtil.getEmailValidationError());
        }
        
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Company Name: ");
        String companyName = scanner.nextLine().trim();
        System.out.print("Department: ");
        String department = scanner.nextLine().trim();
        System.out.print("Position: ");
        String position = scanner.nextLine().trim();
        
        String password;
        while (true) {
            System.out.print("Password (min 6 characters): ");
            password = scanner.nextLine().trim();
            if (ValidationUtil.isValidPassword(password)) {
                break;
            }
            System.out.println("Error! " + ValidationUtil.getPasswordValidationError());
        }
        
        CompanyRep rep = new CompanyRep(userId, password, name, companyName, department, position);
        userManager.save(rep);
        System.out.println("Registration successful! Your account will be activated after staff approval.");
    }

    private static void showUserMenu() {
        User user = session.getCurrentUser();
        
        switch (user.getRole()) {
            case "STUDENT" -> showStudentMenu((Student) user);
            case "COMPANY_REP" -> showCompanyRepMenu((CompanyRep) user);
            case "STAFF" -> showStaffMenu((Staff) user);
        }
    }

    private static void showStudentMenu(Student student) {
        System.out.println("\n=== Student Menu ===");
        System.out.println("1. View Available Internships");
        System.out.println("2. My Applications");
        System.out.println("3. Apply for Internship");
        System.out.println("4. Accept Placement");
        System.out.println("5. Request Withdrawal");
        System.out.println("6. Change Password");
        System.out.println("7. Logout");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1 -> viewAvailableInternships(student);
            case 2 -> viewMyApplications(student);
            case 3 -> applyForInternship(student);
            case 4 -> acceptPlacement(student);
            case 5 -> requestWithdrawal(student);
            case 6 -> changePassword();
            case 7 -> session.logout();
            default -> System.out.println("Invalid option");
        }
    }

    private static void viewAvailableInternships(Student student) {
        var internships = studentService.getAvailableInternships(student);
        if (internships.isEmpty()) {
            System.out.println("No internships available for your profile.");
            return;
        }
        System.out.println("\n=== Available Internships ===");
        for (Internship i : internships) {
            System.out.println("\nID: " + i.getId());
            System.out.println("Title: " + i.getTitle());
            System.out.println("Company: " + i.getCompanyName());
            System.out.println("Level: " + i.getLevel());
            System.out.println("Available Slots: " + i.getAvailableSlots() + "/" + i.getTotalSlots());
            System.out.println("Closing Date: " + i.getClosingDate());
        }
    }

    private static void viewMyApplications(Student student) {
        var applications = studentService.getStudentApplications(student.getUserId());
        if (applications.isEmpty()) {
            System.out.println("You have no applications.");
            return;
        }
        System.out.println("\n=== My Applications ===");
        for (Application app : applications) {
            System.out.println("\nApplication ID: " + app.getId());
            System.out.println("Internship: " + app.getInternship().getTitle());
            System.out.println("Company: " + app.getInternship().getCompanyName());
            System.out.println("Status: " + app.getStatus());
            if (app.isWithdrawalPending()) {
                System.out.println("Withdrawal: PENDING");
            }
        }
    }

    private static void applyForInternship(Student student) {
        System.out.print("Enter Internship ID: ");
        int internshipId = getIntInput();
        
        Internship internship = internshipManager.findById(internshipId);
        if (internship == null) {
            System.out.println("Internship not found.");
            return;
        }
        
        String result = studentService.applyForInternship(student, internship);
        System.out.println(result);
    }

    private static void acceptPlacement(Student student) {
        var applications = studentService.getStudentApplications(student.getUserId());
        var successful = applications.stream()
            .filter(app -> app.getStatus().equals("Successful") && !app.isPlacementAccepted())
            .toList();
        
        if (successful.isEmpty()) {
            System.out.println("No successful applications to accept.");
            return;
        }
        
        System.out.println("\n=== Successful Applications ===");
        for (Application app : successful) {
            System.out.println(app.getId() + ". " + app.getInternship().getTitle());
        }
        
        System.out.print("Enter Application ID to accept: ");
        int appId = getIntInput();
        
        Application app = applicationManager.findById(appId);
        if (app != null) {
            String result = studentService.acceptPlacement(student, app);
            System.out.println(result);
        } else {
            System.out.println("Application not found.");
        }
    }

    private static void requestWithdrawal(Student student) {
        var applications = studentService.getStudentApplications(student.getUserId());
        if (applications.isEmpty()) {
            System.out.println("You have no applications.");
            return;
        }
        
        System.out.println("\n=== Your Applications ===");
        for (Application app : applications) {
            if (!app.getStatus().equals("Withdrawn") && !app.isWithdrawalPending()) {
                System.out.println(app.getId() + ". " + app.getInternship().getTitle() + " - " + app.getStatus());
            }
        }
        
        System.out.print("Enter Application ID to withdraw: ");
        int appId = getIntInput();
        System.out.print("Reason for withdrawal: ");
        String reason = scanner.nextLine();
        
        Application app = applicationManager.findById(appId);
        if (app != null) {
            String result = studentService.requestWithdrawal(app, reason);
            System.out.println(result);
        } else {
            System.out.println("Application not found.");
        }
    }

    private static void showCompanyRepMenu(CompanyRep rep) {
        System.out.println("\n=== Company Representative Menu ===");
        System.out.println("1. My Internships");
        System.out.println("2. Create Internship");
        System.out.println("3. Edit Internship");
        System.out.println("4. Delete Internship");
        System.out.println("5. View Applications");
        System.out.println("6. Approve/Reject Application");
        System.out.println("7. Toggle Internship Visibility");
        System.out.println("8. Change Password");
        System.out.println("9. Logout");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1 -> viewRepInternships(rep);
            case 2 -> createInternship(rep);
            case 3 -> editInternship(rep);
            case 4 -> deleteInternship(rep);
            case 5 -> viewInternshipApplications(rep);
            case 6 -> approveRejectApplication(rep);
            case 7 -> toggleVisibility(rep);
            case 8 -> changePassword();
            case 9 -> session.logout();
            default -> System.out.println("Invalid option");
        }
    }

    private static void viewRepInternships(CompanyRep rep) {
        var internships = companyRepService.getRepInternships(rep.getUserId());
        if (internships.isEmpty()) {
            System.out.println("You have no internships.");
            return;
        }
        System.out.println("\n=== My Internships ===");
        for (Internship i : internships) {
            System.out.println("\nID: " + i.getId());
            System.out.println("Title: " + i.getTitle());
            System.out.println("Status: " + i.getStatus());
            System.out.println("Visible: " + (i.isVisible() ? "Yes" : "No"));
            System.out.println("Filled Slots: " + i.getFilledSlots() + "/" + i.getTotalSlots());
        }
    }

    private static void createInternship(CompanyRep rep) {
        System.out.println("\n=== Create Internship ===");
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        
        // Validate level with reprompting
        String level;
        while (true) {
            System.out.print("Level (Basic/Intermediate/Advanced): ");
            String levelInput = scanner.nextLine();
            try {
                level = ValidationUtil.parseLevel(levelInput);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Error! " + e.getMessage());
            }
        }
        
        System.out.print("Preferred Major (CSC/EEE/MAE): ");
        String major = scanner.nextLine();
        
        // Validate dates with reprompting
        java.time.LocalDate openingDateObj;
        java.time.LocalDate closingDateObj;
        
        while (true) {
            System.out.print("Opening Date (YYYY-MM-DD): ");
            String openingDateInput = scanner.nextLine();
            try {
                openingDateObj = ValidationUtil.parseDate(openingDateInput);
                break;
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Error! " + ValidationUtil.getDateValidationError());
            }
        }
        
        while (true) {
            System.out.print("Closing Date (YYYY-MM-DD): ");
            String closingDateInput = scanner.nextLine();
            try {
                closingDateObj = ValidationUtil.parseDate(closingDateInput);
                if (!ValidationUtil.isClosingDateValid(openingDateObj, closingDateObj)) {
                    System.out.println("Error! Closing date must be after opening date");
                    continue;
                }
                break;
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Error! " + ValidationUtil.getDateValidationError());
            }
        }
        
        System.out.print("Total Slots (max 10): ");
        int slots = getIntInput();
        
        String result = companyRepService.createInternship(rep, title, description, level, major,
                                                          openingDateObj.toString(), closingDateObj.toString(), slots);
        System.out.println(result);
    }

    private static void editInternship(CompanyRep rep) {
        viewRepInternships(rep);
        System.out.print("\nEnter Internship ID to edit: ");
        int id = getIntInput();
        
        Internship internship = internshipManager.findById(id);
        if (internship == null || !internship.getRepId().equals(rep.getUserId())) {
            System.out.println("Internship not found or not yours.");
            return;
        }
        
        System.out.print("New Title: ");
        String title = scanner.nextLine();
        System.out.print("New Description: ");
        String description = scanner.nextLine();
        
        String result = companyRepService.updateInternship(internship, title, description);
        System.out.println(result);
    }

    private static void deleteInternship(CompanyRep rep) {
        viewRepInternships(rep);
        System.out.print("\nEnter Internship ID to delete: ");
        int id = getIntInput();
        
        Internship internship = internshipManager.findById(id);
        if (internship == null || !internship.getRepId().equals(rep.getUserId())) {
            System.out.println("Internship not found or not yours.");
            return;
        }
        
        String result = companyRepService.deleteInternship(id);
        System.out.println(result);
    }

    private static void viewInternshipApplications(CompanyRep rep) {
        viewRepInternships(rep);
        System.out.print("\nEnter Internship ID to view applications: ");
        int id = getIntInput();
        
        Internship internship = internshipManager.findById(id);
        if (internship == null || !internship.getRepId().equals(rep.getUserId())) {
            System.out.println("Internship not found or not yours.");
            return;
        }
        
        var applications = companyRepService.getInternshipApplications(id);
        if (applications.isEmpty()) {
            System.out.println("No applications.");
            return;
        }
        
        System.out.println("\n=== Applications ===");
        for (Application app : applications) {
            System.out.println("\nApp ID: " + app.getId());
            System.out.println("Student ID: " + app.getStudentId());
            System.out.println("Status: " + app.getStatus());
        }
    }

    private static void approveRejectApplication(CompanyRep rep) {
        System.out.print("Enter Application ID: ");
        int appId = getIntInput();
        
        Application app = applicationManager.findById(appId);
        if (app == null || !app.getInternship().getRepId().equals(rep.getUserId())) {
            System.out.println("Application not found or not yours.");
            return;
        }
        
        System.out.println("1. Approve");
        System.out.println("2. Reject");
        System.out.print("Choose: ");
        int choice = getIntInput();
        
        String result = (choice == 1) ? companyRepService.approveApplication(app)
                                       : companyRepService.rejectApplication(app);
        System.out.println(result);
    }

    private static void toggleVisibility(CompanyRep rep) {
        viewRepInternships(rep);
        System.out.print("\nEnter Internship ID to toggle visibility: ");
        int id = getIntInput();
        
        Internship internship = internshipManager.findById(id);
        if (internship == null || !internship.getRepId().equals(rep.getUserId())) {
            System.out.println("Internship not found or not yours.");
            return;
        }
        
        companyRepService.toggleVisibility(internship);
        System.out.println("Visibility toggled. Now: " + (internship.isVisible() ? "Visible" : "Hidden"));
    }

    private static void showStaffMenu(Staff staff) {
        System.out.println("\n=== Career Center Staff Menu ===");
        System.out.println("1. Approve/Reject Company Representatives");
        System.out.println("2. Approve/Reject Internships");
        System.out.println("3. Approve/Reject Withdrawal Requests");
        System.out.println("4. Generate Reports");
        System.out.println("5. Change Password");
        System.out.println("6. Logout");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1 -> approveRejectCompanyReps();
            case 2 -> approveRejectInternships();
            case 3 -> approveRejectWithdrawals();
            case 4 -> generateReports();
            case 5 -> changePassword();
            case 6 -> session.logout();
            default -> System.out.println("Invalid option");
        }
    }

    private static void approveRejectCompanyReps() {
        var pending = careerCenterService.getPendingCompanyReps();
        if (pending.isEmpty()) {
            System.out.println("No pending company representatives.");
            return;
        }
        
        System.out.println("\n=== Pending Company Representatives ===");
        for (int i = 0; i < pending.size(); i++) {
            CompanyRep rep = pending.get(i);
            System.out.println((i + 1) + ". " + rep.getName() + " - " + rep.getCompanyName());
        }
        
        System.out.print("Enter number to process (0 to cancel): ");
        int choice = getIntInput() - 1;
        
        if (choice >= 0 && choice < pending.size()) {
            CompanyRep rep = pending.get(choice);
            System.out.println("1. Approve");
            System.out.println("2. Reject");
            System.out.print("Choose: ");
            int action = getIntInput();
            
            if (action == 1) {
                careerCenterService.approveCompanyRep(rep, userManager);
                System.out.println("Company representative approved!");
            } else if (action == 2) {
                careerCenterService.rejectCompanyRep(rep, userManager);
                System.out.println("Company representative rejected!");
            }
        }
    }

    private static void approveRejectInternships() {
        var pending = careerCenterService.getPendingInternships();
        if (pending.isEmpty()) {
            System.out.println("No pending internships.");
            return;
        }
        
        System.out.println("\n=== Pending Internships ===");
        for (int i = 0; i < pending.size(); i++) {
            Internship internship = pending.get(i);
            System.out.println((i + 1) + ". " + internship.getTitle() + " - " + internship.getCompanyName());
        }
        
        System.out.print("Enter number to process (0 to cancel): ");
        int choice = getIntInput() - 1;
        
        if (choice >= 0 && choice < pending.size()) {
            Internship internship = pending.get(choice);
            System.out.println("1. Approve");
            System.out.println("2. Reject");
            System.out.print("Choose: ");
            int action = getIntInput();
            
            if (action == 1) {
                careerCenterService.approveInternship(internship);
                System.out.println("Internship approved!");
            } else if (action == 2) {
                careerCenterService.rejectInternship(internship);
                System.out.println("Internship rejected!");
            }
        }
    }

    private static void approveRejectWithdrawals() {
        var pending = careerCenterService.getPendingWithdrawals();
        if (pending.isEmpty()) {
            System.out.println("No pending withdrawals.");
            return;
        }
        
        System.out.println("\n=== Pending Withdrawal Requests ===");
        for (int i = 0; i < pending.size(); i++) {
            Application app = pending.get(i);
            System.out.println((i + 1) + ". Student: " + app.getStudentId() + 
                             " | Internship: " + app.getInternship().getTitle());
            System.out.println("   Reason: " + app.getWithdrawalReason());
        }
        
        System.out.print("Enter number to process (0 to cancel): ");
        int choice = getIntInput() - 1;
        
        if (choice >= 0 && choice < pending.size()) {
            Application app = pending.get(choice);
            System.out.println("1. Approve");
            System.out.println("2. Reject");
            System.out.print("Choose: ");
            int action = getIntInput();
            
            String result = (action == 1) ? careerCenterService.approveWithdrawal(app)
                                           : careerCenterService.rejectWithdrawal(app);
            System.out.println(result);
        }
    }

    private static void generateReports() {
        System.out.println("\n=== Generate Report ===");
        System.out.print("Filter by Status (or press Enter to skip): ");
        String status = scanner.nextLine().trim();
        System.out.print("Filter by Major (or press Enter to skip): ");
        String major = scanner.nextLine().trim();
        System.out.print("Filter by Company (or press Enter to skip): ");
        String company = scanner.nextLine().trim();
        System.out.print("Filter by Level (or press Enter to skip): ");
        String level = scanner.nextLine().trim();
        
        var internships = careerCenterService.filterInternships(
            status.isEmpty() ? null : status,
            major.isEmpty() ? null : major,
            company.isEmpty() ? null : company,
            level.isEmpty() ? null : level
        );
        
        if (internships.isEmpty()) {
            System.out.println("No internships match the filters.");
            return;
        }
        
        System.out.println("\n=== Report Results ===");
        for (Internship i : internships) {
            System.out.println("\nID: " + i.getId());
            System.out.println("Title: " + i.getTitle());
            System.out.println("Company: " + i.getCompanyName());
            System.out.println("Status: " + i.getStatus());
            System.out.println("Level: " + i.getLevel());
            System.out.println("Major: " + i.getPreferredMajor());
            System.out.println("Slots: " + i.getFilledSlots() + "/" + i.getTotalSlots());
        }
    }

    private static void changePassword() {
        User user = session.getCurrentUser();
        System.out.print("Current Password: ");
        String oldPassword = scanner.nextLine();
        
        String newPassword;
        while (true) {
            System.out.print("New Password (min 6 characters): ");
            newPassword = scanner.nextLine();
            System.out.print("Confirm New Password: ");
            String confirmPassword = scanner.nextLine();
            
            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Error! Passwords do not match! Try again.");
                continue;
            }
            
            if (!ValidationUtil.isValidPassword(newPassword)) {
                System.out.println("Error! " + ValidationUtil.getPasswordValidationError());
                continue;
            }
            
            break;
        }
        
        if (authService.changePassword(user, oldPassword, newPassword)) {
            System.out.println("Password changed successfully! Please login again.");
            session.logout();
        } else {
            System.out.println("Error! Current password is incorrect!");
        }
    }

    private static int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}
