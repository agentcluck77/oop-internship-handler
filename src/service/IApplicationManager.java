package service;
import java.util.List;

import model.Application;
import model.Internship;
import model.Student;

/**
 * Interface for application management operations.
 * Follows Dependency Inversion Principle - controllers depend on this abstraction.
 */
public interface IApplicationManager {
    boolean applyForInternship(Student student, Internship internship);
    boolean hasAppliedToInternship(String studentId, int internshipId);
    int getApplicationCount(String studentId);
    List<Application> getApplicationsForStudent(String studentId);
    List<Application> getSuccessfulApplications(String studentId);
    List<Application> getApplicationsForInternship(int internshipId);
    void updateApplicationStatus(Application application, String status);
    void acceptPlacement(String studentId, Application acceptedApp);
    boolean requestWithdrawal(String studentId, int applicationId, String reason);
    List<Application> getWithdrawableApplications(String studentId);
    List<Application> getPendingWithdrawals();
    void approveWithdrawal(Application application);
    void removeApplicationsForInternship(int internshipId);
}
