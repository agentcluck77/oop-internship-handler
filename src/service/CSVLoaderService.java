package service;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import model.Staff;
import model.Student;
import ui.ConsoleUI;

/**
 * Loads user data from CSV files into the application.
 */
public class CSVLoaderService {
    private IUserManager userManager;
    private IValidationService validationService;
    private ConsoleUI ui;

    public CSVLoaderService(IUserManager userManager,
                            IValidationService validationService,
                            ConsoleUI ui) {
        this.userManager = userManager;
        this.validationService = validationService;
        this.ui = ui;
    }

    /**
     * Load students from a CSV file.
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
                        ui.displayError("Invalid student ID format '" + id +
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
            ui.displayMessage("Loaded " + studentCount + " students from " + filePath);
        } catch (FileNotFoundException e) {
            ui.displayError("Student CSV file not found: " + filePath);
        } catch (Exception e) {
            ui.displayError("Error reading student file: " + e.getMessage());
        }
    }

    /**
     * Load staff from a CSV file.
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
            ui.displayMessage("Loaded " + staffCount + " staff members from " + filePath);
        } catch (FileNotFoundException e) {
            ui.displayError("Staff CSV file not found: " + filePath);
        } catch (Exception e) {
            ui.displayError("Error reading staff file: " + e.getMessage());
        }
    }
}
