import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationManagerTest {
    private ApplicationManager applicationManager;
    private Student student;
    private Internship baseInternship;

    @BeforeEach
    void setUp() {
        applicationManager = new ApplicationManager();
        student = new Student("U1234567A", "secret", "Alice", 3, "CSC");
        baseInternship = new Internship(
            "AI Intern",
            "Work on AI tasks",
            "Basic",
            "CSC",
            "2025-01-01",
            "2026-01-01",
            "TechCorp",
            "rep1",
            3
        );
        baseInternship.setStatus("Approved");
        baseInternship.setVisible(true);
    }

    @Test
    void applyForInternship_allowsEligibleStudent() {
        boolean applied = applicationManager.applyForInternship(student, baseInternship);

        assertTrue(applied, "Eligible student should be able to apply");
        assertEquals(1, applicationManager.getApplicationCount(student.getUserId()));
    }

    @Test
    void applyForInternship_blocksWhenMaximumReached() {
        for (int i = 0; i < BusinessRules.MAX_APPLICATIONS_PER_STUDENT; i++) {
            Internship internship = new Internship(
                "Role " + i,
                "Description",
                "Basic",
                "CSC",
                "2025-01-01",
                "2026-01-01",
                "TechCorp",
                "rep1",
                3
            );
            internship.setStatus("Approved");
            internship.setVisible(true);
            assertTrue(applicationManager.applyForInternship(student, internship));
        }

        Internship extra = new Internship(
            "Extra Role",
            "Description",
            "Basic",
            "CSC",
            "2025-01-01",
            "2026-01-01",
            "TechCorp",
            "rep1",
            3
        );
        extra.setStatus("Approved");
        extra.setVisible(true);

        assertFalse(applicationManager.applyForInternship(student, extra),
            "Fourth pending application should be rejected");
    }

    @Test
    void acceptPlacement_withdrawsOtherApplications() {
        Internship otherInternship = new Internship(
            "Data Intern",
            "Analyze data",
            "Basic",
            "CSC",
            "2025-01-01",
            "2026-01-01",
            "DataCorp",
            "rep2",
            2
        );
        otherInternship.setStatus("Approved");
        otherInternship.setVisible(true);

        assertTrue(applicationManager.applyForInternship(student, baseInternship));
        assertTrue(applicationManager.applyForInternship(student, otherInternship));

        Application accepted = applicationManager.getApplicationsForStudent(student.getUserId()).get(0);
        applicationManager.acceptPlacement(student.getUserId(), accepted);

        assertTrue(accepted.isPlacementAccepted());

        Application other = applicationManager.getApplicationsForStudent(student.getUserId()).get(1);
        assertEquals("Withdrawn", other.getStatus(), "Other applications should be withdrawn");
    }

    @Test
    void approveWithdrawal_restoresSlotWhenPlacementAccepted() {
        applicationManager.applyForInternship(student, baseInternship);
        Application application = applicationManager.getApplicationsForStudent(student.getUserId()).get(0);

        baseInternship.decreaseAvailableSlots();
        application.setPlacementAccepted(true);

        applicationManager.approveWithdrawal(application);

        assertEquals(baseInternship.getTotalSlots(), baseInternship.getAvailableSlots(),
            "Slot count should restore after withdrawal approval");
        assertEquals("Withdrawn", application.getStatus());
        assertFalse(application.isPlacementAccepted());
    }

    @Test
    void removeApplicationsForInternship_clearsAssociatedEntries() {
        applicationManager.applyForInternship(student, baseInternship);
        applicationManager.removeApplicationsForInternship(baseInternship.getId());

        assertTrue(applicationManager.getApplicationsForInternship(baseInternship.getId()).isEmpty());
    }
}

