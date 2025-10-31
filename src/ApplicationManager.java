import java.util.*;

public class ApplicationManager {
    private List<Application> applications;

    public ApplicationManager() {
        this.applications = new ArrayList<>();
    }

    public boolean applyForInternship(Student student, Internship internship) {
        if (!student.canApplyForLevel(internship.getLevel())) {
            return false;
        }

        if (getApplicationCount(student.getUserId()) >= 3) {
            return false;
        }

        if (!internship.isOpenForApplications()) {
            return false;
        }

        // Check if already applied to this internship
        if (hasAppliedToInternship(student.getUserId(), internship.getId())) {
            return false;
        }

        Application application = new Application(student.getUserId(), internship);
        applications.add(application);
        return true;
    }

    public boolean hasAppliedToInternship(String studentId, int internshipId) {
        for (Application app : applications) {
            if (app.getStudentId().equals(studentId) &&
                    app.getInternship().getId() == internshipId) {
                return true;
            }
        }
        return false;
    }

    public int getApplicationCount(String studentId) {
        int count = 0;
        for (Application app : applications) {
            if (app.getStudentId().equals(studentId) &&
                    app.getStatus().equals("Pending")) {
                count++;
            }
        }
        return count;
    }

    public List<Application> getApplicationsForStudent(String studentId) {
        List<Application> result = new ArrayList<>();

        for (Application app : applications) {
            if (app.getStudentId().equals(studentId)) {
                result.add(app);
            }
        }

        return result;
    }

    public List<Application> getSuccessfulApplications(String studentId) {
        List<Application> result = new ArrayList<>();

        for (Application app : applications) {
            if (app.getStudentId().equals(studentId) &&
                    app.getStatus().equals("Successful") &&
                    !app.isPlacementAccepted()) {
                result.add(app);
            }
        }

        return result;
    }

    public List<Application> getApplicationsForInternship(int internshipId) {
        List<Application> result = new ArrayList<>();

        for (Application app : applications) {
            if (app.getInternship().getId() == internshipId) {
                result.add(app);
            }
        }

        return result;
    }

    public void updateApplicationStatus(Application application, String status) {
        application.setStatus(status);
    }

    public void acceptPlacement(String studentId, Application acceptedApp) {
        acceptedApp.setPlacementAccepted(true);

        for (Application app : applications) {
            if (app.getStudentId().equals(studentId) &&
                    app.getId() != acceptedApp.getId() &&
                    (app.getStatus().equals("Pending") || app.getStatus().equals("Successful"))) {
                app.setStatus("Withdrawn");
            }
        }
    }

    public boolean requestWithdrawal(String studentId, String reason) {
        for (Application app : applications) {
            if (app.getStudentId().equals(studentId) &&
                    app.isPlacementAccepted()) {
                app.setWithdrawalReason(reason);
                app.setWithdrawalStatus("Pending");
                return true;
            }
        }
        return false;
    }

    public List<Application> getPendingWithdrawals() {
        List<Application> result = new ArrayList<>();

        for (Application app : applications) {
            if (app.getWithdrawalStatus() != null &&
                    app.getWithdrawalStatus().equals("Pending")) {
                result.add(app);
            }
        }

        return result;
    }

    public void approveWithdrawal(Application application) {
        application.setWithdrawalStatus("Approved");
        application.setPlacementAccepted(false);
        application.setStatus("Withdrawn");
        application.getInternship().increaseAvailableSlots();
    }
}