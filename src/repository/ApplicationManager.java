package repository;
import model.*;
import java.util.*;

public class ApplicationManager implements ApplicationRepository {
    private List<Application> applications = new ArrayList<>();

    @Override
    public Application findById(int id) {
        for (Application app : applications) {
            if (app.getId() == id) {
                return app;
            }
        }
        return null;
    }

    @Override
    public void save(Application application) {
        Application existing = findById(application.getId());
        if (existing == null) {
            applications.add(application);
        }
    }

    @Override
    public List<Application> findAll() {
        return new ArrayList<>(applications);
    }

    @Override
    public List<Application> findByStudentId(String studentId) {
        List<Application> result = new ArrayList<>();
        for (Application app : applications) {
            if (app.getStudentId().equals(studentId)) {
                result.add(app);
            }
        }
        return result;
    }

    @Override
    public List<Application> findByInternshipId(int internshipId) {
        List<Application> result = new ArrayList<>();
        for (Application app : applications) {
            if (app.getInternship().getId() == internshipId) {
                result.add(app);
            }
        }
        return result;
    }

    @Override
    public List<Application> getPendingWithdrawals() {
        List<Application> result = new ArrayList<>();
        for (Application app : applications) {
            if (app.isWithdrawalPending()) {
                result.add(app);
            }
        }
        return result;
    }
}
