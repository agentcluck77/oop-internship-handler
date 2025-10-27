import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static UserManager userManager = new UserManager();
    private static InternshipManager internshipManager = new InternshipManager();
    private static ApplicationManager applicationManager = new ApplicationManager();

    public static void main(String[] args) {
        System.out.println("=== Internship Placement Management System ===\n");

        // Initialize system with sample data
        initializeSystem();

        while (true) {
            System.out.println("\n1. Login");
            System.out.println("2. Register (Company Representative)");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    registerCompanyRep();
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void initializeSystem() {
        // Add sample students
        userManager.addUser(new Student("U1234567A", "password", "John Doe", 1, "CSC"));
        userManager.addUser(new Student("U2345678B", "password", "Jane Smith", 3, "EEE"));
        userManager.addUser(new Student("U3456789C", "password", "Bob Lee", 2, "MAE"));

        // Add sample staff
        userManager.addUser(new Staff("staff001", "password", "Admin User", "Career Center"));

        System.out.println("System initialized with sample users.");
    }

    private static void login() {
        System.out.print("Enter User ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = userManager.login(id, password);

        if (user == null) {
            System.out.println("Invalid credentials!");
            return;
        }

        System.out.println("Login successful! Welcome, " + user.getName());

        if (user instanceof Student) {
            studentMenu((Student) user);
        } else if (user instanceof CompanyRep) {
            companyRepMenu((CompanyRep) user);
        } else if (user instanceof Staff) {
            staffMenu((Staff) user);
        }
    }

    private static void registerCompanyRep() {
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Company Name: ");
        String company = scanner.nextLine();
        System.out.print("Enter Department: ");
        String department = scanner.nextLine();
        System.out.print("Enter Position: ");
        String position = scanner.nextLine();

        CompanyRep rep = new CompanyRep(email, password, name, company, department, position);
        userManager.addPendingCompanyRep(rep);

        System.out.println("Registration submitted! Awaiting approval.");
    }

    private static void studentMenu(Student student) {
        while (true) {
            System.out.println("\n=== Student Menu ===");
            System.out.println("1. View Internships");
            System.out.println("2. Apply for Internship");
            System.out.println("3. View My Applications");
            System.out.println("4. Accept Placement");
            System.out.println("5. Request Withdrawal");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
            System.out.print("Choose option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    viewInternships(student);
                    break;
                case 2:
                    applyForInternship(student);
                    break;
                case 3:
                    viewMyApplications(student);
                    break;
                case 4:
                    acceptPlacement(student);
                    break;
                case 5:
                    requestWithdrawal(student);
                    break;
                case 6:
                    changePassword(student);
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void companyRepMenu(CompanyRep rep) {
        while (true) {
            System.out.println("\n=== Company Representative Menu ===");
            System.out.println("1. Create Internship");
            System.out.println("2. View My Internships");
            System.out.println("3. View Applications");
            System.out.println("4. Approve/Reject Application");
            System.out.println("5. Toggle Internship Visibility");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
            System.out.print("Choose option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    createInternship(rep);
                    break;
                case 2:
                    viewMyInternships(rep);
                    break;
                case 3:
                    viewApplicationsForInternship(rep);
                    break;
                case 4:
                    approveRejectApplication(rep);
                    break;
                case 5:
                    toggleVisibility(rep);
                    break;
                case 6:
                    changePassword(rep);
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void staffMenu(Staff staff) {
        while (true) {
            System.out.println("\n=== Staff Menu ===");
            System.out.println("1. Approve/Reject Company Registration");
            System.out.println("2. Approve/Reject Internship");
            System.out.println("3. Approve/Reject Withdrawal Request");
            System.out.println("4. Generate Report");
            System.out.println("5. View All Internships");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
            System.out.print("Choose option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    approveRejectCompany(staff);
                    break;
                case 2:
                    approveRejectInternship(staff);
                    break;
                case 3:
                    approveRejectWithdrawal(staff);
                    break;
                case 4:
                    generateReport(staff);
                    break;
                case 5:
                    viewAllInternships(staff);
                    break;
                case 6:
                    changePassword(staff);
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    // Student functions
    private static void viewInternships(Student student) {
        List<Internship> internships = internshipManager.getInternshipsForStudent(student);

        if (internships.isEmpty()) {
            System.out.println("No internships available.");
            return;
        }

        System.out.println("\n=== Available Internships ===");
        for (int i = 0; i < internships.size(); i++) {
            Internship internship = internships.get(i);
            System.out.println((i + 1) + ". " + internship.getTitle());
            System.out.println("   Company: " + internship.getCompanyName());
            System.out.println("   Level: " + internship.getLevel());
            System.out.println("   Major: " + internship.getPreferredMajor());
            System.out.println("   Closing Date: " + internship.getClosingDate());
            System.out.println("   Available Slots: " + internship.getAvailableSlots());
        }
    }

    private static void applyForInternship(Student student) {
        if (applicationManager.getApplicationCount(student.getUserId()) >= 3) {
            System.out.println("You already have 3 pending applications!");
            return;
        }

        List<Internship> internships = internshipManager.getInternshipsForStudent(student);

        if (internships.isEmpty()) {
            System.out.println("No internships available.");
            return;
        }

        viewInternships(student);
        System.out.print("\nEnter internship number to apply: ");
        int choice = getIntInput() - 1;

        if (choice >= 0 && choice < internships.size()) {
            Internship internship = internships.get(choice);
            if (applicationManager.applyForInternship(student, internship)) {
                System.out.println("Application submitted successfully!");
            } else {
                System.out.println("Failed to apply. Check eligibility.");
            }
        } else {
            System.out.println("Invalid choice!");
        }
    }

    private static void viewMyApplications(Student student) {
        List<Application> applications = applicationManager.getApplicationsForStudent(student.getUserId());

        if (applications.isEmpty()) {
            System.out.println("No applications found.");
            return;
        }

        System.out.println("\n=== My Applications ===");
        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);
            System.out.println((i + 1) + ". " + app.getInternship().getTitle());
            System.out.println("   Status: " + app.getStatus());
            System.out.println("   Company: " + app.getInternship().getCompanyName());
        }
    }

    private static void acceptPlacement(Student student) {
        List<Application> successful = applicationManager.getSuccessfulApplications(student.getUserId());

        if (successful.isEmpty()) {
            System.out.println("No successful applications to accept.");
            return;
        }

        System.out.println("\n=== Successful Applications ===");
        for (int i = 0; i < successful.size(); i++) {
            Application app = successful.get(i);
            System.out.println((i + 1) + ". " + app.getInternship().getTitle());
        }

        System.out.print("\nEnter number to accept: ");
        int choice = getIntInput() - 1;

        if (choice >= 0 && choice < successful.size()) {
            Application app = successful.get(choice);
            applicationManager.acceptPlacement(student.getUserId(), app);
            System.out.println("Placement accepted! Other applications withdrawn.");
        } else {
            System.out.println("Invalid choice!");
        }
    }

    private static void requestWithdrawal(Student student) {
        System.out.print("Enter reason for withdrawal: ");
        String reason = scanner.nextLine();

        if (applicationManager.requestWithdrawal(student.getUserId(), reason)) {
            System.out.println("Withdrawal request submitted.");
        } else {
            System.out.println("No active placement to withdraw from.");
        }
    }

    // Company Rep functions
    private static void createInternship(CompanyRep rep) {
        if (internshipManager.getInternshipCountForCompany(rep.getUserId()) >= 5) {
            System.out.println("You already have 5 internships!");
            return;
        }

        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Description: ");
        String description = scanner.nextLine();
        System.out.print("Enter Level (Basic/Intermediate/Advanced): ");
        String level = scanner.nextLine();
        System.out.print("Enter Preferred Major (CSC/EEE/MAE): ");
        String major = scanner.nextLine();
        System.out.print("Enter Opening Date (YYYY-MM-DD): ");
        String openDate = scanner.nextLine();
        System.out.print("Enter Closing Date (YYYY-MM-DD): ");
        String closeDate = scanner.nextLine();
        System.out.print("Enter Number of Slots (max 10): ");
        int slots = getIntInput();

        Internship internship = new Internship(title, description, level, major,
                openDate, closeDate, rep.getCompanyName(), rep.getUserId(), slots);

        internshipManager.addInternship(internship);
        System.out.println("Internship created! Awaiting approval.");
    }

    private static void viewMyInternships(CompanyRep rep) {
        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());

        if (internships.isEmpty()) {
            System.out.println("No internships created yet.");
            return;
        }

        System.out.println("\n=== My Internships ===");
        for (int i = 0; i < internships.size(); i++) {
            Internship internship = internships.get(i);
            System.out.println((i + 1) + ". " + internship.getTitle());
            System.out.println("   Status: " + internship.getStatus());
            System.out.println("   Visible: " + (internship.isVisible() ? "Yes" : "No"));
            System.out.println("   Slots: " + internship.getAvailableSlots() + "/" + internship.getTotalSlots());
        }
    }

    private static void viewApplicationsForInternship(CompanyRep rep) {
        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());

        if (internships.isEmpty()) {
            System.out.println("No internships found.");
            return;
        }

        viewMyInternships(rep);
        System.out.print("\nEnter internship number: ");
        int choice = getIntInput() - 1;

        if (choice >= 0 && choice < internships.size()) {
            Internship internship = internships.get(choice);
            List<Application> applications = applicationManager.getApplicationsForInternship(internship.getId());

            if (applications.isEmpty()) {
                System.out.println("No applications for this internship.");
                return;
            }

            System.out.println("\n=== Applications ===");
            for (int i = 0; i < applications.size(); i++) {
                Application app = applications.get(i);
                Student student = (Student) userManager.getUserById(app.getStudentId());
                System.out.println((i + 1) + ". " + student.getName());
                System.out.println("   Year: " + student.getYear() + ", Major: " + student.getMajor());
                System.out.println("   Status: " + app.getStatus());
            }
        }
    }

    private static void approveRejectApplication(CompanyRep rep) {
        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());

        if (internships.isEmpty()) {
            System.out.println("No internships found.");
            return;
        }

        viewMyInternships(rep);
        System.out.print("\nEnter internship number: ");
        int internChoice = getIntInput() - 1;

        if (internChoice >= 0 && internChoice < internships.size()) {
            Internship internship = internships.get(internChoice);
            List<Application> applications = applicationManager.getApplicationsForInternship(internship.getId());

            List<Application> pending = new ArrayList<>();
            for (Application app : applications) {
                if (app.getStatus().equals("Pending")) {
                    pending.add(app);
                }
            }

            if (pending.isEmpty()) {
                System.out.println("No pending applications.");
                return;
            }

            System.out.println("\n=== Pending Applications ===");
            for (int i = 0; i < pending.size(); i++) {
                Application app = pending.get(i);
                Student student = (Student) userManager.getUserById(app.getStudentId());
                System.out.println((i + 1) + ". " + student.getName());
            }

            System.out.print("\nEnter application number: ");
            int appChoice = getIntInput() - 1;

            if (appChoice >= 0 && appChoice < pending.size()) {
                System.out.print("Approve or Reject? (A/R): ");
                String decision = scanner.nextLine().toUpperCase();

                if (decision.equals("A")) {
                    applicationManager.updateApplicationStatus(pending.get(appChoice), "Successful");
                    internship.decreaseAvailableSlots();
                    System.out.println("Application approved!");
                } else if (decision.equals("R")) {
                    applicationManager.updateApplicationStatus(pending.get(appChoice), "Unsuccessful");
                    System.out.println("Application rejected!");
                }
            }
        }
    }

    private static void toggleVisibility(CompanyRep rep) {
        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());

        if (internships.isEmpty()) {
            System.out.println("No internships found.");
            return;
        }

        viewMyInternships(rep);
        System.out.print("\nEnter internship number to toggle visibility: ");
        int choice = getIntInput() - 1;

        if (choice >= 0 && choice < internships.size()) {
            Internship internship = internships.get(choice);
            internship.toggleVisibility();
            System.out.println("Visibility toggled to: " + (internship.isVisible() ? "On" : "Off"));
        }
    }

    // Staff functions
    private static void approveRejectCompany(Staff staff) {
        List<CompanyRep> pending = userManager.getPendingCompanyReps();

        if (pending.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }

        System.out.println("\n=== Pending Registrations ===");
        for (int i = 0; i < pending.size(); i++) {
            CompanyRep rep = pending.get(i);
            System.out.println((i + 1) + ". " + rep.getName());
            System.out.println("   Company: " + rep.getCompanyName());
            System.out.println("   Email: " + rep.getUserId());
        }

        System.out.print("\nEnter number: ");
        int choice = getIntInput() - 1;

        if (choice >= 0 && choice < pending.size()) {
            System.out.print("Approve or Reject? (A/R): ");
            String decision = scanner.nextLine().toUpperCase();

            if (decision.equals("A")) {
                userManager.approveCompanyRep(pending.get(choice));
                System.out.println("Company approved!");
            } else if (decision.equals("R")) {
                userManager.rejectCompanyRep(pending.get(choice));
                System.out.println("Company rejected!");
            }
        }
    }

    private static void approveRejectInternship(Staff staff) {
        List<Internship> pending = internshipManager.getPendingInternships();

        if (pending.isEmpty()) {
            System.out.println("No pending internships.");
            return;
        }

        System.out.println("\n=== Pending Internships ===");
        for (int i = 0; i < pending.size(); i++) {
            Internship internship = pending.get(i);
            System.out.println((i + 1) + ". " + internship.getTitle());
            System.out.println("   Company: " + internship.getCompanyName());
            System.out.println("   Level: " + internship.getLevel());
        }

        System.out.print("\nEnter number: ");
        int choice = getIntInput() - 1;

        if (choice >= 0 && choice < pending.size()) {
            System.out.print("Approve or Reject? (A/R): ");
            String decision = scanner.nextLine().toUpperCase();

            Internship internship = pending.get(choice);
            if (decision.equals("A")) {
                internship.setStatus("Approved");
                internship.setVisible(true);
                System.out.println("Internship approved!");
            } else if (decision.equals("R")) {
                internship.setStatus("Rejected");
                System.out.println("Internship rejected!");
            }
        }
    }

    private static void approveRejectWithdrawal(Staff staff) {
        List<Application> withdrawals = applicationManager.getPendingWithdrawals();

        if (withdrawals.isEmpty()) {
            System.out.println("No pending withdrawals.");
            return;
        }

        System.out.println("\n=== Pending Withdrawals ===");
        for (int i = 0; i < withdrawals.size(); i++) {
            Application app = withdrawals.get(i);
            Student student = (Student) userManager.getUserById(app.getStudentId());
            System.out.println((i + 1) + ". " + student.getName());
            System.out.println("   Internship: " + app.getInternship().getTitle());
            System.out.println("   Reason: " + app.getWithdrawalReason());
        }

        System.out.print("\nEnter number: ");
        int choice = getIntInput() - 1;

        if (choice >= 0 && choice < withdrawals.size()) {
            System.out.print("Approve or Reject? (A/R): ");
            String decision = scanner.nextLine().toUpperCase();

            Application app = withdrawals.get(choice);
            if (decision.equals("A")) {
                applicationManager.approveWithdrawal(app);
                System.out.println("Withdrawal approved!");
            } else if (decision.equals("R")) {
                app.setWithdrawalStatus("Rejected");
                System.out.println("Withdrawal rejected!");
            }
        }
    }

    private static void generateReport(Staff staff) {
        System.out.println("\n=== Filter Options ===");
        System.out.print("Filter by Status? (Y/N): ");
        String filterStatus = scanner.nextLine().toUpperCase();
        String status = null;
        if (filterStatus.equals("Y")) {
            System.out.print("Enter Status (Pending/Approved/Rejected/Filled): ");
            status = scanner.nextLine();
        }

        System.out.print("Filter by Major? (Y/N): ");
        String filterMajor = scanner.nextLine().toUpperCase();
        String major = null;
        if (filterMajor.equals("Y")) {
            System.out.print("Enter Major: ");
            major = scanner.nextLine();
        }

        System.out.print("Filter by Level? (Y/N): ");
        String filterLevel = scanner.nextLine().toUpperCase();
        String level = null;
        if (filterLevel.equals("Y")) {
            System.out.print("Enter Level: ");
            level = scanner.nextLine();
        }

        List<Internship> filtered = internshipManager.generateReport(status, major, level);

        System.out.println("\n=== Report ===");
        System.out.println("Total Internships: " + filtered.size());
        for (Internship internship : filtered) {
            System.out.println("\n- " + internship.getTitle());
            System.out.println("  Company: " + internship.getCompanyName());
            System.out.println("  Status: " + internship.getStatus());
            System.out.println("  Level: " + internship.getLevel());
            System.out.println("  Major: " + internship.getPreferredMajor());
        }
    }

    private static void viewAllInternships(Staff staff) {
        List<Internship> all = internshipManager.getAllInternships();

        if (all.isEmpty()) {
            System.out.println("No internships in system.");
            return;
        }

        System.out.println("\n=== All Internships ===");
        for (Internship internship : all) {
            System.out.println("- " + internship.getTitle());
            System.out.println("  Company: " + internship.getCompanyName());
            System.out.println("  Status: " + internship.getStatus());
            System.out.println("  Slots: " + internship.getAvailableSlots() + "/" + internship.getTotalSlots());
        }
    }

    // Common functions
    private static void changePassword(User user) {
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        user.setPassword(newPassword);
        System.out.println("Password changed successfully!");
    }

    private static int getIntInput() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}