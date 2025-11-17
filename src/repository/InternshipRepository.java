package repository;
import model.*;
import java.util.List;

public interface InternshipRepository {
    Internship findById(int id);
    void save(Internship internship);
    List<Internship> findAll();
    List<Internship> findByRepId(String repId);
    List<Internship> getPendingInternships();
    boolean delete(int id);
}
