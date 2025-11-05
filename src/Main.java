import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static UserManager userManager = new UserManager();
    private static InternshipManager internshipManager = new InternshipManager();
    private static ApplicationManager applicationManager = new ApplicationManager();

    // Hardcoded CSV file paths
    private static final String STUDENT_CSV_PATH = "students.csv";
    private static final String STAFF_CSV_PATH = "staff.csv";

    // Business rule constants 
    private static final int MAX_APPLICATIONS_PER_STUDENT = 3;
    private static final int MAX_INTERNSHIPS_PER_COMPANY = 5;
    private static final int MAX_SLOTS_PER_INTERNSHIP = 10;

    private static String filterStatus = null;
    private static String filterMajor = null;
    private static String filterLevel = null;
    private static String filterClosingDate = null;

    public static void main(String[] args) {
        System.out.println("=== Internship Placement Management System ===\n");

        // Load users from CSV files on startup
        loadUsersFromCSV();

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

    private static void loadUsersFromCSV() {
        // Load students
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_CSV_PATH))) {
            String line;
            boolean firstLine = true;
            int studentCount = 0;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }

                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String id = parts[0].trim();

                    // Validate student ID format
                    if (!isValidStudentId(id)) {
                        System.out.println("Warning: Invalid student ID format '" + id + "' - skipping entry. Expected format: U#######L");
                        continue;
                    }

                    String password = parts[1].trim();
                    String name = parts[2].trim();
                    String major = parts[3].trim();
                    int year = Integer.parseInt(parts[4].trim());

                    Student student = new Student(id, password, name, year, major);
                    userManager.addUser(student);
                    studentCount++;
                }
            }
            System.out.println("Loaded " + studentCount + " students from " + STUDENT_CSV_PATH);
        } catch (FileNotFoundException e) {
            System.out.println("Student CSV file not found: " + STUDENT_CSV_PATH);
        } catch (Exception e) {
            System.out.println("Error reading student file: " + e.getMessage());
        }

        // Load staff
        try (BufferedReader br = new BufferedReader(new FileReader(STAFF_CSV_PATH))) {
            String line;
            boolean firstLine = true;
            int staffCount = 0;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }

                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String id = parts[0].trim();
                    String password = parts[1].trim();
                    String email = parts[2].trim();
                    String name = parts[3].trim();
                    String department = parts[4].trim();

                    Staff staff = new Staff(id, password, name, department);
                    staff.setEmail(email);
                    userManager.addUser(staff);
                    staffCount++;
                }
            }
            System.out.println("Loaded " + staffCount + " staff members from " + STAFF_CSV_PATH);
        } catch (FileNotFoundException e) {
            System.out.println("Staff CSV file not found: " + STAFF_CSV_PATH);
        } catch (Exception e) {
            System.out.println("Error reading staff file: " + e.getMessage());
        }
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
        String email = scanner.nextLine().trim();

        // FIX: Improved email validation with regex pattern
        if (!isValidEmail(email)) {
            System.out.println("Invalid email format! Must contain @ and a domain.");
            return;
        }

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        // minimum password length requirement
        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters!");
            return;
        }
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty() || name.length() > 100) {
            System.out.println("Name must be between 1 and 100 characters!");
            return;
        }
        
        System.out.print("Enter Company Name: ");
        String company = scanner.nextLine().trim();
        if (company.isEmpty() || company.length() > 100) {
            System.out.println("Company name must be between 1 and 100 characters!");
            return;
        }
        
        System.out.print("Enter Department: ");
        String department = scanner.nextLine().trim();
        if (department.isEmpty() || department.length() > 100) {
            System.out.println("Department must be between 1 and 100 characters!");
            return;
        }
        
        System.out.print("Enter Position: ");
        String position = scanner.nextLine().trim();
        if (position.isEmpty() || position.length() > 100) {
            System.out.println("Position must be between 1 and 100 characters!");
            return;
        }

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
            System.out.println("6. Set Filters");
            System.out.println("7. Clear Filters");
            System.out.println("8. Change Password");
            System.out.println("9. Logout");
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
                    setFilters();
                    break;
                case 7:
                    clearFilters();
                    break;
                case 8:
                    if (changePassword(student)) {
                        System.out.println("Please login again with your new password.");
                        return;
                    }
                    break;
                case 9:
                    clearFilters(); // Clear filters on logout
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
            System.out.println("6. Set Filters");
            System.out.println("7. Clear Filters");
            System.out.println("8. Change Password");
            System.out.println("9. Logout");
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
                    setFilters();
                    break;
                case 7:
                    clearFilters();
                    break;
                case 8:
                    if (changePassword(rep)) {
                        System.out.println("Please login again with your new password.");
                        return;
                    }
                    break;
                case 9:
                    clearFilters(); // Clear filters on logout 
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
            System.out.println("6. Set Filters");
            System.out.println("7. Clear Filters");
            System.out.println("8. Change Password");
            System.out.println("9. Logout");
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
                    setFilters();
                    break;
                case 7:
                    clearFilters();
                    break;
                case 8:
                    if (changePassword(staff)) {
                        System.out.println("Please login again with your new password.");
                        return;
                    }
                    break;
                case 9:
                    clearFilters(); // Clear filters on logout
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    // Filter functions
    private static void setFilters() {
        System.out.println("\n=== Set Filters ===");
        System.out.print("Filter by Status (Pending/Approved/Rejected/Filled) or press Enter to skip: ");
        String status = scanner.nextLine().trim();
        filterStatus = status.isEmpty() ? null : status;

        System.out.print("Filter by Major (CSC/EEE/MAE) or press Enter to skip: ");
        String major = scanner.nextLine().trim();
        filterMajor = major.isEmpty() ? null : major;

        System.out.print("Filter by Level (Basic/Intermediate/Advanced) or press Enter to skip: ");
        String level = scanner.nextLine().trim();
        filterLevel = level.isEmpty() ? null : level;

        System.out.print("Filter by Closing Date (YYYY-MM-DD) or press Enter to skip: ");
        String date = scanner.nextLine().trim();
        filterClosingDate = date.isEmpty() ? null : date;

        System.out.println("Filters applied! They will persist across menu pages.");
    }

    private static void clearFilters() {
        filterStatus = null;
        filterMajor = null;
        filterLevel = null;
        filterClosingDate = null;
        System.out.println("All filters cleared!");
    }

    private static List<Internship> applyFilters(List<Internship> internships) {
        List<Internship> filtered = new ArrayList<>();

        for (Internship internship : internships) {
            boolean matches = true;

            if (filterStatus != null && !internship.getStatus().equalsIgnoreCase(filterStatus)) {
                matches = false;
            }
            if (filterMajor != null && !internship.getPreferredMajor().equalsIgnoreCase(filterMajor)) {
                matches = false;
            }
            if (filterLevel != null && !internship.getLevel().equalsIgnoreCase(filterLevel)) {
                matches = false;
            }
            if (filterClosingDate != null && !internship.getClosingDate().equals(filterClosingDate)) {
                matches = false;
            }

            if (matches) {
                filtered.add(internship);
            }
        }

        return filtered;
    }

    // Student functions
    private static void viewInternships(Student student) {
        List<Internship> internships = internshipManager.getInternshipsForStudent(student);
        internships = applyFilters(internships);

        if (internships.isEmpty()) {
            System.out.println("No internships available.");
            return;
        }

        System.out.println("\n=== Available Internships ===");
        displayActiveFilters();
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

    private static void displayActiveFilters() {
        if (filterStatus != null || filterMajor != null || filterLevel != null || filterClosingDate != null) {
            System.out.print("Active Filters: ");
            if (filterStatus != null) System.out.print("Status=" + filterStatus + " ");
            if (filterMajor != null) System.out.print("Major=" + filterMajor + " ");
            if (filterLevel != null) System.out.print("Level=" + filterLevel + " ");
            if (filterClosingDate != null) System.out.print("ClosingDate=" + filterClosingDate + " ");
            System.out.println();
        }
    }

    private static void applyForInternship(Student student) {
        if (applicationManager.getApplicationCount(student.getUserId()) >= MAX_APPLICATIONS_PER_STUDENT) {
            System.out.println("You already have " + MAX_APPLICATIONS_PER_STUDENT + " pending applications!");
            return;
        }

        List<Internship> internships = internshipManager.getInternshipsForStudent(student);
        internships = applyFilters(internships);

        if (internships.isEmpty()) {
            System.out.println("No internships available.");
            return;
        }

        viewInternships(student);
        System.out.print("\nEnter internship number to apply: ");
        int choice = getIntInput() - 1;

        if (choice >= 0 && choice < internships.size()) {
            Internship internship = internships.get(choice);

            // Check if already applied
            if (applicationManager.hasAppliedToInternship(student.getUserId(), internship.getId())) {
                System.out.println("You have already applied to this internship!");
                return;
            }

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

        // Students can view all internships they've applied to, even if visibility is turned off
        System.out.println("\n=== My Applications ===");
        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);
            Internship internship = app.getInternship();

            System.out.println((i + 1) + ". " + internship.getTitle());
            System.out.println("   Company: " + internship.getCompanyName());
            System.out.println("   Level: " + internship.getLevel());
            System.out.println("   Major: " + internship.getPreferredMajor());
            System.out.println("   Closing Date: " + internship.getClosingDate());
            System.out.println("   Application Status: " + app.getStatus());

            // Show visibility status - students can see this even when visibility is off
            if (!internship.isVisible()) {
                System.out.println("   [Currently hidden from public listing]");
            }

            if (app.isPlacementAccepted()) {
                System.out.println("   Placement: ACCEPTED");
            }
            if (app.getWithdrawalStatus() != null) {
                System.out.println("   Withdrawal Status: " + app.getWithdrawalStatus());
            }
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
            app.getInternship().decreaseAvailableSlots();
            System.out.println("Placement accepted! Other applications withdrawn.");
        } else {
            System.out.println("Invalid choice!");
        }
    }

    private static void requestWithdrawal(Student student) {
        List<Application> withdrawable = applicationManager.getWithdrawableApplications(student.getUserId());

        if (withdrawable.isEmpty()) {
            System.out.println("No applications available for withdrawal.");
            return;
        }

        System.out.println("\n=== Applications Available for Withdrawal ===");
        for (int i = 0; i < withdrawable.size(); i++) {
            Application app = withdrawable.get(i);
            System.out.println((i + 1) + ". " + app.getInternship().getTitle());
            System.out.println("   Company: " + app.getInternship().getCompanyName());
            System.out.println("   Status: " + app.getStatus());
            if (app.isPlacementAccepted()) {
                System.out.println("   Placement: ACCEPTED");
            }
        }

        System.out.print("\nEnter application number to withdraw: ");
        int choice = getIntInput() - 1;

        if (choice >= 0 && choice < withdrawable.size()) {
            System.out.print("Enter reason for withdrawal: ");
            String reason = scanner.nextLine();

            Application app = withdrawable.get(choice);
            if (applicationManager.requestWithdrawal(student.getUserId(), app.getId(), reason)) {
                System.out.println("Withdrawal request submitted for approval.");
            } else {
                System.out.println("Failed to submit withdrawal request.");
            }
        } else {
            System.out.println("Invalid choice!");
        }
    }

    // Company Rep functions
    private static void createInternship(CompanyRep rep) {
        if (internshipManager.getInternshipCountForCompany(rep.getUserId()) >= MAX_INTERNSHIPS_PER_COMPANY) {
            System.out.println("You already have " + MAX_INTERNSHIPS_PER_COMPANY + " internships!");
            return;
        }

        System.out.print("Enter Title: ");
        String title = scanner.nextLine().trim();
        // input length limits to prevent display issues
        if (title.isEmpty() || title.length() > 200) {
            System.out.println("Title must be between 1 and 200 characters!");
            return;
        }
        
        System.out.print("Enter Description: ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty() || description.length() > 1000) {
            System.out.println("Description must be between 1 and 1000 characters!");
            return;
        }

        String level;
        while (true) {
            System.out.print("Enter Level (Basic/Intermediate/Advanced): ");
            level = scanner.nextLine().trim();
            String levelLower = level.toLowerCase();
            if (levelLower.equals("basic") || levelLower.equals("intermediate") || levelLower.equals("advanced")) {
                level = levelLower.substring(0, 1).toUpperCase() + levelLower.substring(1);
                break;
            } else {
                System.out.println("Invalid level! Please enter Basic, Intermediate, or Advanced.");
            }
        }

        // validate major input 
        String major;
        while (true) {
            System.out.print("Enter Preferred Major (CSC/EEE/MAE): ");
            major = scanner.nextLine().trim().toUpperCase();
            if (major.equals("CSC") || major.equals("EEE") || major.equals("MAE")) {
                break;
            } else {
                System.out.println("Invalid major! Please enter CSC, EEE, or MAE.");
            }
        }

        String openDate;
        String closeDate;
        while (true) {
            System.out.print("Enter Opening Date (YYYY-MM-DD): ");
            openDate = scanner.nextLine();
            System.out.print("Enter Closing Date (YYYY-MM-DD): ");
            closeDate = scanner.nextLine();

            if (isClosingDateValid(openDate, closeDate)) {
                break;
            } else {
                System.out.println("Closing date must be after opening date! Please try again.");
            }
        }

        int slots;
        while (true) {
            System.out.print("Enter Number of Slots (max " + MAX_SLOTS_PER_INTERNSHIP + "): ");
            slots = getIntInput();
            if (slots > 0 && slots <= MAX_SLOTS_PER_INTERNSHIP) {
                break;
            } else {
                System.out.println("Slots must be between 1 and " + MAX_SLOTS_PER_INTERNSHIP + "!");
            }
        }

        Internship internship = new Internship(title, description, level, major,
                openDate, closeDate, rep.getCompanyName(), rep.getUserId(), slots);

        internshipManager.addInternship(internship);
        System.out.println("Internship created! Awaiting approval.");
    }

    private static boolean isClosingDateValid(String openDate, String closeDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate open = LocalDate.parse(openDate, formatter);
            LocalDate close = LocalDate.parse(closeDate, formatter);
            return close.isAfter(open);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static void viewMyInternships(CompanyRep rep) {
        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());
        internships = applyFilters(internships);

        if (internships.isEmpty()) {
            System.out.println("No internships created yet.");
            return;
        }

        System.out.println("\n=== My Internships ===");
        displayActiveFilters();
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
        // apply filters once before displaying to avoid index mismatch
        List<Internship> filteredInternships = applyFilters(internships);

        if (filteredInternships.isEmpty()) {
            System.out.println("No internships found.");
            return;
        }

        // display filtered list
        System.out.println("\n=== My Internships ===");
        displayActiveFilters();
        for (int i = 0; i < filteredInternships.size(); i++) {
            Internship internship = filteredInternships.get(i);
            System.out.println((i + 1) + ". " + internship.getTitle());
            System.out.println("   Status: " + internship.getStatus());
        }

        System.out.print("\nEnter internship number: ");
        int choice = getIntInput() - 1;

        if (choice >= 0 && choice < filteredInternships.size()) {
            Internship internship = filteredInternships.get(choice);
            List<Application> applications = applicationManager.getApplicationsForInternship(internship.getId());

            if (applications.isEmpty()) {
                System.out.println("No applications for this internship.");
                return;
            }

            System.out.println("\n=== Applications ===");
            for (int i = 0; i < applications.size(); i++) {
                Application app = applications.get(i);
                // add null check to prevent crash if student was deleted
                Student student = (Student) userManager.getUserById(app.getStudentId());
                if (student == null) {
                    System.out.println((i + 1) + ". [Unknown Student - ID: " + app.getStudentId() + "]");
                    System.out.println("   Status: " + app.getStatus());
                } else {
                    System.out.println((i + 1) + ". " + student.getName());
                    System.out.println("   Year: " + student.getYear() + ", Major: " + student.getMajor());
                    System.out.println("   Status: " + app.getStatus());
                }
            }
        }
    }

    private static void approveRejectApplication(CompanyRep rep) {
        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());
        // apply filters once before displaying to avoid index mismatch
        List<Internship> filteredInternships = applyFilters(internships);

        if (filteredInternships.isEmpty()) {
            System.out.println("No internships found.");
            return;
        }

        // Display the filtered list
        System.out.println("\n=== My Internships ===");
        displayActiveFilters();
        for (int i = 0; i < filteredInternships.size(); i++) {
            Internship internship = filteredInternships.get(i);
            System.out.println((i + 1) + ". " + internship.getTitle());
            System.out.println("   Status: " + internship.getStatus());
        }

        System.out.print("\nEnter internship number: ");
        int internChoice = getIntInput() - 1;

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
                System.out.println("No pending applications.");
                return;
            }

            System.out.println("\n=== Pending Applications ===");
            for (int i = 0; i < pending.size(); i++) {
                Application app = pending.get(i);
                // FIX: Add null check to prevent crash if student was deleted
                Student student = (Student) userManager.getUserById(app.getStudentId());
                if (student == null) {
                    System.out.println((i + 1) + ". [Unknown Student - ID: " + app.getStudentId() + "]");
                } else {
                    System.out.println((i + 1) + ". " + student.getName());
                }
            }

            System.out.print("\nEnter application number: ");
            int appChoice = getIntInput() - 1;

            if (appChoice >= 0 && appChoice < pending.size()) {
                System.out.print("Approve or Reject? (A/R): ");
                String decision = scanner.nextLine().toUpperCase();

                if (decision.equals("A")) {
                    applicationManager.updateApplicationStatus(pending.get(appChoice), "Successful");
                    System.out.println("Application approved! Student can now accept placement.");
                } else if (decision.equals("R")) {
                    applicationManager.updateApplicationStatus(pending.get(appChoice), "Unsuccessful");
                    System.out.println("Application rejected!");
                }
            }
        }
    }

    private static void toggleVisibility(CompanyRep rep) {
        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());
        // FIX: Apply filters ONCE before displaying to avoid index mismatch
        List<Internship> filteredInternships = applyFilters(internships);

        if (filteredInternships.isEmpty()) {
            System.out.println("No internships found.");
            return;
        }

        // Display the filtered list
        System.out.println("\n=== My Internships ===");
        displayActiveFilters();
        for (int i = 0; i < filteredInternships.size(); i++) {
            Internship internship = filteredInternships.get(i);
            System.out.println((i + 1) + ". " + internship.getTitle());
            System.out.println("   Status: " + internship.getStatus());
            System.out.println("   Visible: " + (internship.isVisible() ? "Yes" : "No"));
        }

        System.out.print("\nEnter internship number to toggle visibility: ");
        int choice = getIntInput() - 1;

        if (choice >= 0 && choice < filteredInternships.size()) {
            Internship internship = filteredInternships.get(choice);
            internship.toggleVisibility();
            System.out.println("Visibility toggled to: " + (internship.isVisible() ? "On" : "Off"));
        } else {
            System.out.println("Invalid choice!");
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
            // FIX: Add null check to prevent crash if student was deleted
            Student student = (Student) userManager.getUserById(app.getStudentId());
            if (student == null) {
                System.out.println((i + 1) + ". [Unknown Student - ID: " + app.getStudentId() + "]");
            } else {
                System.out.println((i + 1) + ". " + student.getName());
            }
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
                System.out.println("Withdrawal rejected! Student can still accept placement.");
            }
        }
    }

    private static void generateReport(Staff staff) {
        System.out.println("\n=== Generate Report (Using Current Filters) ===");

        List<Internship> filtered = internshipManager.generateReport(filterStatus, filterMajor, filterLevel);

        System.out.println("\n=== Report ===");
        displayActiveFilters();
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
        all = applyFilters(all);

        if (all.isEmpty()) {
            System.out.println("No internships in system.");
            return;
        }

        System.out.println("\n=== All Internships ===");
        displayActiveFilters();
        for (Internship internship : all) {
            System.out.println("- " + internship.getTitle());
            System.out.println("  Company: " + internship.getCompanyName());
            System.out.println("  Status: " + internship.getStatus());
            System.out.println("  Slots: " + internship.getAvailableSlots() + "/" + internship.getTotalSlots());
        }
    }

    // Common functions
    private static boolean changePassword(User user) {
        System.out.print("Enter current password: ");
        String currentPass = scanner.nextLine();

        if (!user.getPassword().equals(currentPass)) {
            System.out.println("Current password is incorrect!");
            return false;
        }

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match!");
            return false;
        }

        user.setPassword(newPassword);
        System.out.println("Password changed successfully!");
        return true;
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

    // helper method for better email validation
    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // must contain @ and have text before and after it
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return false;
        }
        // Must have a dot after @
        int lastDot = email.lastIndexOf('.');
        return lastDot > atIndex && lastDot < email.length() - 1;
    }

    // Validate student ID format: U + 7 digits + letter (e.g., U2345123F)
    private static boolean isValidStudentId(String studentId) {
        if (studentId == null || studentId.length() != 9) {
            return false;
        }
        // Must start with 'U'
        if (studentId.charAt(0) != 'U') {
            return false;
        }
        // Next 7 characters must be digits
        for (int i = 1; i <= 7; i++) {
            if (!Character.isDigit(studentId.charAt(i))) {
                return false;
            }
        }
        // Last character must be a letter
        return Character.isLetter(studentId.charAt(8));
    }
}