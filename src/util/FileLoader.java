package util;
import model.*;
import repository.*;
import java.io.*;
import java.util.*;

public class FileLoader {
    public static void loadStudents(String filename, UserManager userManager) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; 
                }
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String userId = parts[0].trim();
                    String password = parts[1].trim();
                    String name = parts[2].trim();
                    String major = parts[3].trim();
                    int year = Integer.parseInt(parts[4].trim());
                    Student student = new Student(userId, password, name, year, major);
                    userManager.save(student);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading students: " + e.getMessage());
        }
    }

    public static void loadStaff(String filename, UserManager userManager) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; 
                }
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String userId = parts[0].trim();
                    String password = parts[1].trim();
                    String email = parts[2].trim(); 
                    String name = parts[3].trim();
                    String department = parts[4].trim();
                    Staff staff = new Staff(userId, password, name, department);
                    userManager.save(staff);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading staff: " + e.getMessage());
        }
    }
}
