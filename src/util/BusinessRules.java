package util;
/**
 * Centralized business rule constants for the application.
 */
public class BusinessRules {
    // Business rule constants
    public static final int MAX_APPLICATIONS_PER_STUDENT = 3;
    public static final int MAX_INTERNSHIPS_PER_COMPANY = 5;
    public static final int MAX_SLOTS_PER_INTERNSHIP = 10;

    // CSV file paths
    public static final String STUDENT_CSV_PATH = "students.csv";
    public static final String STAFF_CSV_PATH = "staff.csv";

    // Validation constants
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_FIELD_LENGTH = 100;
    public static final int MAX_TITLE_LENGTH = 200;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    public static final int STUDENT_ID_LENGTH = 9;

    // Private constructor to prevent instantiation
    private BusinessRules() {
        throw new AssertionError("Cannot instantiate BusinessRules class");
    }
}
