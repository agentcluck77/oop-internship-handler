import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Service for loading user data from CSV files.
 * Extracted from Main.java lines 57-129 to follow Single Responsibility Principle.
 * NOW FOLLOWS DEPENDENCY INVERSION PRINCIPLE using interfaces.
 */
public class CSVLoaderService {
    private IUserManager userManager;
    private IValidationService validationService;

    public CSVLoaderService(IUserManager userManager, IValidationService validationService) {
        this.userManager = userManager;
        this.validationService = validationService;
    }

    /**
     * Load students from CSV file
     * Extracted from Main.java lines 58-95
     */
    public void loadStudents(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
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
                    if (!validationService.isValidStudentId(id)) {
                        System.out.println("Warning: Invalid student ID format '" + id +
                            "' - skipping entry. Expected format: U#######L");
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
            System.out.println("Loaded " + studentCount + " students from " + filePath);
        } catch (FileNotFoundException e) {
            System.out.println("Student CSV file not found: " + filePath);
        } catch (Exception e) {
            System.out.println("Error reading student file: " + e.getMessage());
        }
    }

    /**
     * Load staff from CSV file
     * Extracted from Main.java lines 98-128
     */
    public void loadStaff(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
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
            System.out.println("Loaded " + staffCount + " staff members from " + filePath);
        } catch (FileNotFoundException e) {
            System.out.println("Staff CSV file not found: " + filePath);
        } catch (Exception e) {
            System.out.println("Error reading staff file: " + e.getMessage());
        }
    }
}
