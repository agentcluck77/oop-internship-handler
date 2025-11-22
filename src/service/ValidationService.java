package service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import util.BusinessRules;

/**
 * Provides validation helpers for user input.
 */
public class ValidationService implements IValidationService {

    /**
     * Validate email format.
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Must contain @ and have text before and after it
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return false;
        }
        // Must have a dot after @
        int lastDot = email.lastIndexOf('.');
        return lastDot > atIndex && lastDot < email.length() - 1;
    }

    /**
     * Validate student ID format: U + 7 digits + letter (e.g., U2345123F).
     */
    public boolean isValidStudentId(String studentId) {
        if (studentId == null || studentId.length() != BusinessRules.STUDENT_ID_LENGTH) {
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

    /**
     * Validate closing date is after opening date.
     */
    public boolean isClosingDateValid(String openDate, String closeDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate open = LocalDate.parse(openDate, formatter);
            LocalDate close = LocalDate.parse(closeDate, formatter);
            return close.isAfter(open);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validate password meets minimum length requirement
     */
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= BusinessRules.MIN_PASSWORD_LENGTH;
    }

    /**
     * Validate field length is within bounds
     */
    public boolean isValidFieldLength(String field, int min, int max) {
        if (field == null) {
            return false;
        }
        int length = field.trim().length();
        return length >= min && length <= max;
    }

    /**
     * Validate major is one of the accepted values
     */
    public boolean isValidMajor(String major) {
        if (major == null) {
            return false;
        }
        String upperMajor = major.toUpperCase();
        return upperMajor.equals("CSC") || upperMajor.equals("EEE") || upperMajor.equals("MAE");
    }

    /**
     * Validate level is one of the accepted values
     */
    public boolean isValidLevel(String level) {
        if (level == null) {
            return false;
        }
        String lowerLevel = level.toLowerCase();
        return lowerLevel.equals("basic") || lowerLevel.equals("intermediate") || lowerLevel.equals("advanced");
    }
}
