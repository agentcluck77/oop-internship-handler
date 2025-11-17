/**
 * Interface for validation service operations.
 * Follows Dependency Inversion Principle - controllers depend on this abstraction.
 */
public interface IValidationService {
    boolean isValidEmail(String email);
    boolean isValidStudentId(String studentId);
    boolean isClosingDateValid(String openDate, String closeDate);
    boolean isValidPassword(String password);
    boolean isValidFieldLength(String field, int min, int max);
    boolean isValidMajor(String major);
    boolean isValidLevel(String level);
}
