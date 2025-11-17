package repository;
import model.*;
import java.util.List;

public interface UserRepository {
    User findById(String userId);
    void save(User user);
    List<User> findAll();
    List<CompanyRep> getPendingCompanyReps();
    List<Student> getAllStudents();
    List<CompanyRep> getAllCompanyReps();
    List<Staff> getAllStaff();
}
