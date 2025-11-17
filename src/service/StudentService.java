package service;
import model.*;
import repository.*;
import java.util.*;

public class StudentService {
    private InternshipRepository internshipRepo;
    private ApplicationRepository applicationRepo;

    public StudentService(InternshipRepository internshipRepo, ApplicationRepository applicationRepo) {
        this.internshipRepo = internshipRepo;
        this.applicationRepo = applicationRepo;
    }

    public List<Internship> getAvailableInternships(Student student) {
        List<Internship> result = new ArrayList<>();
        for (Internship internship : internshipRepo.findAll()) {
            if (canStudentSeeInternship(student, internship)) {
                result.add(internship);
            }
        }
        return result;
    }

    private boolean canStudentSeeInternship(Student student, Internship internship) {
        if (!internship.isVisible() || !internship.getStatus().equals("Approved")) {
            return false;
        }
        return student.canApplyForLevel(internship.getLevel());
    }

    public List<Application> getStudentApplications(String studentId) {
        return applicationRepo.findByStudentId(studentId);
    }

    public String applyForInternship(Student student, Internship internship) {

        if (!canStudentSeeInternship(student, internship)) {
            return "You are not eligible for this internship";
        }

        if (!internship.isOpenForApplications()) {
            return "Internship is not open for applications";
        }


        List<Application> existing = applicationRepo.findByStudentId(student.getUserId());
        int pendingCount = 0;
        for (Application app : existing) {
            if (app.getStatus().equals("Pending")) {
                pendingCount++;
            }
            if (app.getInternship().getId() == internship.getId()) {
                return "You have already applied for this internship";
            }
        }

        if (pendingCount >= 3) {
            return "You can only have 3 pending applications at a time";
        }

        for (Application app : existing) {
            if (app.isPlacementAccepted()) {
                return "You have already accepted a placement";
            }
        }

        Application application = new Application(student.getUserId(), internship);
        applicationRepo.save(application);
        return "Application submitted successfully";
    }

    public String acceptPlacement(Student student, Application application) {
        if (!application.getStudentId().equals(student.getUserId())) {
            return "This application does not belong to you";
        }

        if (!application.getStatus().equals("Successful")) {
            return "Can only accept successful applications";
        }

        application.setPlacementAccepted(true);
        application.getInternship().incrementFilledSlots();

        List<Application> allApps = applicationRepo.findByStudentId(student.getUserId());
        for (Application app : allApps) {
            if (app.getId() != application.getId() && !app.getStatus().equals("Withdrawn")) {
                app.setStatus("Withdrawn");
                if (app.getStatus().equals("Successful") && app.isPlacementAccepted()) {
                    app.getInternship().decrementFilledSlots();
                }
            }
        }

        return "Placement accepted successfully! Other applications have been withdrawn.";
    }

    public String requestWithdrawal(Application application, String reason) {
        if (application.isWithdrawalPending()) {
            return "Withdrawal request already pending";
        }
        application.setWithdrawalReason(reason);
        application.setWithdrawalPending(true);
        return "Withdrawal request submitted for staff approval";
    }
}
