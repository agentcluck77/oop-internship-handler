import java.util.List;

/**
 * Interface for user management operations.
 * Follows Dependency Inversion Principle - controllers depend on this abstraction.
 */
public interface IUserManager {
    void addUser(User user);
    void addPendingCompanyRep(CompanyRep rep);
    User login(String userId, String password);
    User getUserById(String userId);
    List<CompanyRep> getPendingCompanyReps();
    void approveCompanyRep(CompanyRep rep);
    void rejectCompanyRep(CompanyRep rep);
}
