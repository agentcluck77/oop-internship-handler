import java.util.List;

/**
 * Interface for internship management operations.
 * Follows Dependency Inversion Principle - controllers depend on this abstraction.
 */
public interface IInternshipManager {
    void addInternship(Internship internship);
    List<Internship> getAllInternships();
    List<Internship> getInternshipsForStudent(Student student);
    List<Internship> getInternshipsForCompany(String repId);
    int getInternshipCountForCompany(String repId);
    List<Internship> getPendingInternships();
    List<Internship> generateReport(String status, String major, String level);
    Internship getInternshipById(int id);
    void removeInternship(Internship internship);
}
