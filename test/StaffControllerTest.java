import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class StaffControllerTest {
    private Staff staff;
    private UserManager userManager;
    private InternshipManager internshipManager;
    private ApplicationManager applicationManager;
    private FilterService filterService;

    @BeforeEach
    void setUp() {
        staff = TestFixtures.makeStaff();
        userManager = new UserManager();
        internshipManager = new InternshipManager();
        applicationManager = new ApplicationManager();
        filterService = new FilterService();
    }

    private StaffController buildController(TestConsoleUI ui) {
        return new StaffController(
            staff,
            userManager,
            internshipManager,
            applicationManager,
            filterService,
            ui
        );
    }

    @Test
    void approveRejectInternship_setsStatusAndVisibility() {
        Internship internship = TestFixtures.makeInternship("Pending Approval", "Basic", "CSC", "rep");
        internship.setStatus("Pending");
        internship.setVisible(false);
        internshipManager.addInternship(internship);

        TestConsoleUI ui = new TestConsoleUI(Arrays.asList("1", "A"));
        StaffController controller = buildController(ui);
        controller.approveRejectInternship();

        assertEquals("Approved", internship.getStatus());
        assertTrue(internship.isVisible());
    }

    @Test
    void approveRejectWithdrawal_updatesStatusAndSlots() {
        Internship internship = TestFixtures.makeInternship("Withdrawal Test", "Basic", "CSC", "rep");
        internshipManager.addInternship(internship);

        Student student = TestFixtures.makeStudent(2, "CSC");
        userManager.addUser(student);
        applicationManager.applyForInternship(student, internship);
        Application application = applicationManager.getApplicationsForStudent(student.getUserId()).get(0);
        application.setPlacementAccepted(true);
        internship.decreaseAvailableSlots();
        applicationManager.requestWithdrawal(student.getUserId(), application.getId(), "Need change");

        TestConsoleUI ui = new TestConsoleUI(Arrays.asList("1", "A"));
        StaffController controller = buildController(ui);
        controller.approveRejectWithdrawal();

        assertEquals("Approved", application.getWithdrawalStatus());
        assertEquals("Withdrawn", application.getStatus());
        assertEquals(internship.getTotalSlots(), internship.getAvailableSlots());
    }

    @Test
    void generateReport_respectsActiveFilters() {
        Internship approvedCSC = TestFixtures.makeInternship("CSC Approved", "Basic", "CSC", "rep");
        Internship approvedEEE = TestFixtures.makeInternship("EEE Approved", "Basic", "EEE", "rep");
        Internship pendingCSC = TestFixtures.makeInternship("CSC Pending", "Basic", "CSC", "rep");
        pendingCSC.setStatus("Pending");
        internshipManager.addInternship(approvedCSC);
        internshipManager.addInternship(approvedEEE);
        internshipManager.addInternship(pendingCSC);

        filterService.setFilters("Approved", "CSC", null, null);

        TestConsoleUI ui = new TestConsoleUI(Collections.emptyList());
        StaffController controller = buildController(ui);
        controller.generateReport();

        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.contains("Total Internships: 1")));
        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.contains("CSC Approved")));
        assertFalse(ui.getMessages().stream().anyMatch(msg -> msg.contains("EEE Approved")));
        assertFalse(ui.getMessages().stream().anyMatch(msg -> msg.contains("CSC Pending")));
    }
}

