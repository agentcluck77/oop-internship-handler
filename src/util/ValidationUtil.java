package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ValidationUtil {
    
    // List of personal email domains to reject
    private static final Set<String> PERSONAL_EMAIL_DOMAINS = new HashSet<>(Arrays.asList(
        "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "icloud.com"
    ));
    
    private static final Set<String> VALID_INTERNSHIP_LEVELS = new HashSet<>(Arrays.asList(
        "BASIC", "INTERMEDIATE", "ADVANCED"
    ));
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    

    public static boolean isCorporateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        email = email.trim().toLowerCase();
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return false;
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            return false;
        }
        String domain = email.substring(atIndex + 1).toLowerCase();
        
        return !PERSONAL_EMAIL_DOMAINS.contains(domain);
    }
    

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        
        return password.length() >= 6 && !password.trim().isEmpty();
    }
    

    public static String parseLevel(String levelInput) {
        if (levelInput == null || levelInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Level cannot be empty");
        }
        
        String normalized = levelInput.trim().toUpperCase();
        
        if (!VALID_INTERNSHIP_LEVELS.contains(normalized)) {
            throw new IllegalArgumentException("Invalid level. Must be: Basic, Intermediate, or Advanced");
        }
        
        return normalized.charAt(0) + normalized.substring(1).toLowerCase();
    }
    

    public static LocalDate parseDate(String dateString) throws DateTimeParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new DateTimeParseException("Date cannot be empty!", dateString, 0);
        }
        return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
    }
    

    public static boolean isClosingDateValid(LocalDate openingDate, LocalDate closingDate) {
        if (openingDate == null || closingDate == null) {
            return false;
        }
        return closingDate.isAfter(openingDate);
    }
    

    public static String getEmailValidationError() {
        return "Invalid email. Must be a corporate email (not gmail, yahoo, hotmail, etc)";
    }
    

    public static String getPasswordValidationError() {
        return "Invalid password. Must be at least 6 characters long and not empty";
    }
    

    public static String getLevelValidationError() {
        return "Invalid level. Must be one of: Basic, Intermediate, Advanced";
    }
    

    public static String getDateValidationError() {
        return "Invalid date format. Must be YYYY-MM-DD (e.g., 2025-12-31)";
    }
}
