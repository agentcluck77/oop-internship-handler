import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class CompanyRepControllerTest {
    private CompanyRep rep;
    private InternshipManager internshipManager;
    private ApplicationManager applicationManager;
    private UserManager userManager;
    private FilterService filterService;
    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        rep = TestFixtures.makeCompanyRep("rep@corp.com", true);
        internshipManager = new InternshipManager();
        applicationManager = new ApplicationManager();
        userManager = new UserManager();
        filterService = new FilterService();
        validationService = new ValidationService();
    }

    private CompanyRepController buildController(TestConsoleUI ui) {
        return new CompanyRepController(
            rep,
            internshipManager,
            applicationManager,
            userManager,
            filterService,
            validationService,
            ui
        );
    }

    @Test
    void createInternship_successfulFlow() {
        TestConsoleUI ui = new TestConsoleUI(Arrays.asList(
            "AI Project",
            "Work on AI systems",
            "Basic",
            "CSC",
            "2025-01-01",
            "2025-12-31",
            "2"
        ));

        CompanyRepController controller = buildController(ui);
        controller.createInternship();

        assertEquals(1, internshipManager.getInternshipCountForCompany(rep.getUserId()));
        Internship created = internshipManager.getInternshipsForCompany(rep.getUserId()).get(0);
        assertEquals("AI Project", created.getTitle());
    }

    @Test
    void createInternship_preventedWhenMaximumReached() {
        for (int i = 0; i < BusinessRules.MAX_INTERNSHIPS_PER_COMPANY; i++) {
            Internship internship = TestFixtures.makeInternship("Role " + i, "Basic", "CSC", rep.getUserId());
            internshipManager.addInternship(internship);
        }

        TestConsoleUI ui = new TestConsoleUI(Collections.emptyList());
        CompanyRepController controller = buildController(ui);
        controller.createInternship();

        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.contains("already have " + BusinessRules.MAX_INTERNSHIPS_PER_COMPANY)));
    }

    @Test
    void viewMyInternships_showsStatusForEachListing() {
        Internship pending = TestFixtures.makeInternship("Pending Role", "Basic", "CSC", rep.getUserId());
        pending.setStatus("Pending");
        Internship approved = TestFixtures.makeInternship("Approved Role", "Basic", "CSC", rep.getUserId());
        approved.setStatus("Approved");
        internshipManager.addInternship(pending);
        internshipManager.addInternship(approved);

        TestConsoleUI ui = new TestConsoleUI(Collections.emptyList());
        CompanyRepController controller = buildController(ui);
        controller.viewMyInternships();

        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.contains("Pending Role")));
        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.contains("Status: Pending")));
        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.contains("Approved Role")));
        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.contains("Status: Approved")));
    }

    @Test
    void toggleVisibility_keepsAccessForRepresentative() {
        Internship internship = TestFixtures.makeInternship("Visibility Test", "Basic", "CSC", rep.getUserId());
        internshipManager.addInternship(internship);

        TestConsoleUI toggleUI = new TestConsoleUI(Collections.singletonList("1"));
        CompanyRepController controller = buildController(toggleUI);
        controller.toggleVisibility();

        assertFalse(internship.isVisible());
        assertTrue(toggleUI.getMessages().stream().anyMatch(msg -> msg.contains("Visibility toggled")));

        TestConsoleUI viewUI = new TestConsoleUI(Collections.emptyList());
        controller = buildController(viewUI);
        controller.viewMyInternships();

        assertTrue(viewUI.getMessages().stream().anyMatch(msg -> msg.contains("Visibility Test")));
        assertTrue(viewUI.getMessages().stream().anyMatch(msg -> msg.contains("Visible: No")));
    }

    @Test
    void approveRejectApplication_updatesApplicationStatus() {
        Internship internship = TestFixtures.makeInternship("Review Role", "Basic", "CSC", rep.getUserId());
        internshipManager.addInternship(internship);

        Student student = TestFixtures.makeStudent(2, "CSC");
        userManager.addUser(student);
        applicationManager.applyForInternship(student, internship);

        TestConsoleUI ui = new TestConsoleUI(Arrays.asList("1", "1", "A"));
        CompanyRepController controller = buildController(ui);
        controller.approveRejectApplication();

        Application application = applicationManager.getApplicationsForInternship(internship.getId()).get(0);
        assertEquals("Successful", application.getStatus());
    }

    @Test
    void editInternship_updatesFieldsForPending() {
        Internship internship = TestFixtures.makeInternship("Old Title", "Basic", "CSC", rep.getUserId());
        internship.setStatus("Pending");
        internship.setVisible(false);
        internshipManager.addInternship(internship);

        TestConsoleUI ui = new TestConsoleUI(Arrays.asList(
            "1", // select internship
            "New Title",
            "",
            "",
            "",
            "",
            "",
            ""
        ));

        CompanyRepController controller = buildController(ui);
        controller.editInternship();

        assertEquals("New Title", internship.getTitle());
    }

    @Test
    void editInternship_blockedWhenApproved() {
        Internship internship = TestFixtures.makeInternship("Approved Title", "Basic", "CSC", rep.getUserId());
        internship.setStatus("Approved");
        internshipManager.addInternship(internship);

        TestConsoleUI ui = new TestConsoleUI(Arrays.asList(
            "1", // select
            "New Title",
            "",
            "",
            "",
            "",
            "",
            ""
        ));

        CompanyRepController controller = buildController(ui);
        controller.editInternship();

        assertEquals("Approved Title", internship.getTitle());
        assertTrue(ui.getErrors().stream().anyMatch(msg -> msg.contains("Only Pending or Rejected internships can be edited")));
    }

    @Test
    void deleteInternship_removesPendingPosting() {
        Internship internship = TestFixtures.makeInternship("Delete Me", "Basic", "CSC", rep.getUserId());
        internship.setStatus("Pending");
        internship.setVisible(false);
        internshipManager.addInternship(internship);

        TestConsoleUI ui = new TestConsoleUI(Collections.singletonList("1"));
        CompanyRepController controller = buildController(ui);
        controller.deleteInternship();

        assertTrue(internshipManager.getInternshipsForCompany(rep.getUserId()).isEmpty());
    }

    @Test
    void deleteInternship_blockedWhenApproved() {
        Internship internship = TestFixtures.makeInternship("Cannot Delete", "Basic", "CSC", rep.getUserId());
        internship.setStatus("Approved");
        internshipManager.addInternship(internship);

        TestConsoleUI ui = new TestConsoleUI(Collections.singletonList("1"));
        CompanyRepController controller = buildController(ui);
        controller.deleteInternship();

        assertFalse(internshipManager.getInternshipsForCompany(rep.getUserId()).isEmpty());
        assertTrue(ui.getErrors().stream().anyMatch(msg -> msg.contains("Only Pending or Rejected internships can be deleted")));
    }
}

