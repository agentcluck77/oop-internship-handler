package service;
import model.*;
import repository.*;
import java.util.*;

public class CareerCenterService {
    private UserRepository userRepo;
    private InternshipRepository internshipRepo;
    private ApplicationRepository applicationRepo;

    public CareerCenterService(UserRepository userRepo, InternshipRepository internshipRepo,
                               ApplicationRepository applicationRepo) {
        this.userRepo = userRepo;
        this.internshipRepo = internshipRepo;
        this.applicationRepo = applicationRepo;
    }

    public List<CompanyRep> getPendingCompanyReps() {
        return userRepo.getPendingCompanyReps();
    }

    public void approveCompanyRep(CompanyRep rep, UserManager userManager) {
        userManager.approveCompanyRep(rep);
    }

    public void rejectCompanyRep(CompanyRep rep, UserManager userManager) {
        userManager.rejectCompanyRep(rep);
    }

    public List<Internship> getPendingInternships() {
        return internshipRepo.getPendingInternships();
    }

    public void approveInternship(Internship internship) {
        internship.setStatus("Approved");
        internship.setVisible(true);
    }

    public void rejectInternship(Internship internship) {
        internship.setStatus("Rejected");
    }

    public List<Application> getPendingWithdrawals() {
        return applicationRepo.getPendingWithdrawals();
    }

    public String approveWithdrawal(Application application) {
        application.setWithdrawalPending(false);
        application.setStatus("Withdrawn");
        if (application.isPlacementAccepted()) {
            application.getInternship().decrementFilledSlots();
            application.setPlacementAccepted(false);
        }
        return "Withdrawal approved";
    }

    public String rejectWithdrawal(Application application) {
        application.setWithdrawalPending(false);
        application.setWithdrawalReason(null);
        return "Withdrawal rejected";
    }

    public List<Internship> filterInternships(String status, String major, String company, String level) {
        List<Internship> result = new ArrayList<>();
        for (Internship i : internshipRepo.findAll()) {
            boolean matches = true;
            if (status != null && !status.isEmpty() && !i.getStatus().equalsIgnoreCase(status)) {
                matches = false;
            }
            if (major != null && !major.isEmpty() && !i.getPreferredMajor().equalsIgnoreCase(major)) {
                matches = false;
            }
            if (company != null && !company.isEmpty() && !i.getCompanyName().equalsIgnoreCase(company)) {
                matches = false;
            }
            if (level != null && !level.isEmpty() && !i.getLevel().equalsIgnoreCase(level)) {
                matches = false;
            }
            if (matches) {
                result.add(i);
            }
        }
        return result;
    }
}
