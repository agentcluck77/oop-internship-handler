import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class StudentControllerTest {
    private StudentController controller;
    private Student student;
    private InternshipManager internshipManager;
    private ApplicationManager applicationManager;
    private FilterService filterService;

    @BeforeEach
    void setUp() {
        student = new Student("U1234567A", "password", "Test Student", 2, "CSC");
        internshipManager = new InternshipManager();
        applicationManager = new ApplicationManager();
        filterService = new FilterService();
    }

    private StudentController buildController(TestConsoleUI ui) {
        return new StudentController(student, internshipManager, applicationManager, filterService, ui);
    }

    @Test
    void viewInternships_filtersByEligibilityAndVisibility() {
        Internship eligible = TestFixtures.makeInternship("Eligible Role", "Basic", "CSC", "rep");
        Internship wrongMajor = TestFixtures.makeInternship("Wrong Major", "Basic", "EEE", "rep");
        Internship advanced = TestFixtures.makeInternship("Advanced Role", "Advanced", "CSC", "rep");
        Internship invisible = TestFixtures.makeInternship("Hidden Role", "Basic", "CSC", "rep");
        invisible.setVisible(false);

        internshipManager.addInternship(eligible);
        internshipManager.addInternship(wrongMajor);
        internshipManager.addInternship(advanced);
        internshipManager.addInternship(invisible);

        TestConsoleUI ui = new TestConsoleUI(Collections.emptyList());
        controller = buildController(ui);
        controller.viewInternships();

        long internshipsShown = ui.getMessages().stream()
            .filter(msg -> msg.startsWith("INTERNSHIP"))
            .count();

        assertEquals(1, internshipsShown, "Only eligible and visible internships should be listed");
        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.contains("Eligible Role")));
        assertFalse(ui.getMessages().stream().anyMatch(msg -> msg.contains("Wrong Major")));
        assertFalse(ui.getMessages().stream().anyMatch(msg -> msg.contains("Advanced Role")));
        assertFalse(ui.getMessages().stream().anyMatch(msg -> msg.contains("Hidden Role")));
    }

    @Test
    void applyForInternship_requiresEligibleListings() {
        // Student major CSC but only EEE listing exists -> message should state no internships.
        Internship wrongMajor = TestFixtures.makeInternship("EEE Role", "Basic", "EEE", "rep");
        internshipManager.addInternship(wrongMajor);

        TestConsoleUI ui = new TestConsoleUI(Collections.singletonList("1"));
        controller = buildController(ui);
        controller.applyForInternship();

        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.contains("No internships available")));
        assertEquals(0, applicationManager.getApplicationCount(student.getUserId()));
    }

    @Test
    void viewApplications_showsStatusEvenWhenInternshipHidden() {
        Internship internship = TestFixtures.makeInternship("Invisible Later", "Basic", "CSC", "rep");
        internshipManager.addInternship(internship);
        applicationManager.applyForInternship(student, internship);
        internship.setVisible(false);

        TestConsoleUI ui = new TestConsoleUI(Collections.emptyList());
        controller = buildController(ui);
        controller.viewMyApplications();

        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.startsWith("APPLICATION")));
        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.contains("Invisible Later")));
    }

    @Test
    void acceptPlacement_withdrawsOtherApplicationsAndUpdatesSlots() {
        Internship first = TestFixtures.makeInternship("Offer One", "Basic", "CSC", "rep");
        Internship second = TestFixtures.makeInternship("Offer Two", "Basic", "CSC", "rep");
        internshipManager.addInternship(first);
        internshipManager.addInternship(second);

        applicationManager.applyForInternship(student, first);
        applicationManager.applyForInternship(student, second);
        Application firstApp = applicationManager.getApplicationsForStudent(student.getUserId()).get(0);
        Application secondApp = applicationManager.getApplicationsForStudent(student.getUserId()).get(1);
        applicationManager.updateApplicationStatus(firstApp, "Successful");
        applicationManager.updateApplicationStatus(secondApp, "Successful");

        TestConsoleUI ui = new TestConsoleUI(Collections.singletonList("1"));
        controller = buildController(ui);
        controller.acceptPlacement();

        assertTrue(firstApp.isPlacementAccepted());
        assertEquals("Withdrawn", secondApp.getStatus(), "Other applications should be withdrawn");
        assertEquals(first.getTotalSlots() - 1, first.getAvailableSlots(), "Slots must decrease on acceptance");
    }
}

