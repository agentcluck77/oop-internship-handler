package repository;
import model.*;
import java.util.List;

public interface ApplicationRepository {
    Application findById(int id);
    void save(Application application);
    List<Application> findAll();
    List<Application> findByStudentId(String studentId);
    List<Application> findByInternshipId(int internshipId);
    List<Application> getPendingWithdrawals();
}
