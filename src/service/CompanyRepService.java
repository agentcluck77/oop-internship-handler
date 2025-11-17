package service;
import model.*;
import repository.*;
import java.util.*;

public class CompanyRepService {
    private InternshipRepository internshipRepo;
    private ApplicationRepository applicationRepo;

    public CompanyRepService(InternshipRepository internshipRepo, ApplicationRepository applicationRepo) {
        this.internshipRepo = internshipRepo;
        this.applicationRepo = applicationRepo;
    }

    public List<Internship> getRepInternships(String repId) {
        return internshipRepo.findByRepId(repId);
    }

    public String createInternship(CompanyRep rep, String title, String description, String level,
                                    String preferredMajor, String openingDate, String closingDate, int totalSlots) {
        List<Internship> existing = internshipRepo.findByRepId(rep.getUserId());
        if (existing.size() >= 5) {
            return "You can only create up to 5 internship opportunities";
        }

        if (totalSlots > 10) {
            return "Maximum 10 slots allowed per internship";
        }

        Internship internship = new Internship(title, description, level, preferredMajor,
                                              openingDate, closingDate, rep.getCompanyName(), rep.getUserId(), totalSlots);
        internshipRepo.save(internship);
        return "Internship created successfully (Pending approval)";
    }

    public String updateInternship(Internship internship, String title, String description) {
        if (!internship.canEdit()) {
            return "Cannot edit approved internships";
        }
        internship.setTitle(title);
        internship.setDescription(description);
        return "Internship updated successfully";
    }

    public String deleteInternship(int internshipId) {
        if (internshipRepo.delete(internshipId)) {
            return "Internship deleted successfully";
        }
        return "Cannot delete approved internships";
    }

    public List<Application> getInternshipApplications(int internshipId) {
        return applicationRepo.findByInternshipId(internshipId);
    }

    public String approveApplication(Application application) {
        if (!application.getStatus().equals("Pending")) {
            return "Can only approve pending applications";
        }
        application.setStatus("Successful");
        return "Application approved";
    }

    public String rejectApplication(Application application) {
        if (!application.getStatus().equals("Pending")) {
            return "Can only reject pending applications";
        }
        application.setStatus("Unsuccessful");
        return "Application rejected";
    }

    public void toggleVisibility(Internship internship) {
        internship.toggleVisibility();
    }
}
