import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FullWorkflowIntegrationTest {

    @Test
    void happyPath_endToEndScenario_throughConsoleMenus() {
        List<String> inputs = Arrays.asList(
            // Register company representative
            "2",
            "rep@company.com",
            "repPass1",
            "Jane Rep",
            "TechCorp",
            "HR",
            "Manager",
            // Staff login to approve rep
            "1",
            "staff001",
            "admin123",
            "1",
            "1",
            "A",
            "9",
            // Company rep logs in and creates internship
            "1",
            "rep@company.com",
            "repPass1",
            "1",
            "AI Intern",
            "Work on AI tasks",
            "Basic",
            "CSC",
            "2025-01-01",
            "2025-12-31",
            "2",
            "11",
            // Staff approves internship
            "1",
            "staff001",
            "admin123",
            "2",
            "1",
            "A",
            "9",
            // Student logs in, views, applies
            "1",
            "U1234567A",
            "pass123",
            "1",
            "2",
            "1",
            "9",
            // Company rep approves application
            "1",
            "rep@company.com",
            "repPass1",
            "6",
            "1",
            "1",
            "A",
            "11",
            // Student accepts placement
            "1",
            "U1234567A",
            "pass123",
            "4",
            "1",
            "9",
            // Exit application
            "3"
        );

        TestConsoleUI ui = new TestConsoleUI(inputs);
        IValidationService validationService = new ValidationService();
        IUserManager userManager = new UserManager();
        IInternshipManager internshipManager = new InternshipManager();
        IApplicationManager applicationManager = new ApplicationManager();

        Staff staff = new Staff("staff001", "admin123", "Admin User", "Career Center");
        userManager.addUser(staff);
        Student student = new Student("U1234567A", "pass123", "John Doe", 2, "CSC");
        userManager.addUser(student);

        AuthenticationController authController = new AuthenticationController(userManager, validationService, ui);
        MenuHandlerFactoryRegistry registry = new MenuHandlerFactoryRegistry();
        registry.register(new StudentMenuHandlerFactory(internshipManager, applicationManager, ui));
        registry.register(new CompanyRepMenuHandlerFactory(internshipManager, applicationManager, userManager, validationService, ui));
        registry.register(new StaffMenuHandlerFactory(userManager, internshipManager, applicationManager, ui));

        ApplicationController app = new ApplicationController(ui, authController, registry);
        app.run();

        CompanyRep rep = (CompanyRep) userManager.getUserById("rep@company.com");
        assertNotNull(rep);
        assertTrue(rep.isApproved());

        List<Internship> internships = internshipManager.getInternshipsForCompany(rep.getUserId());
        assertEquals(1, internships.size());
        Internship internship = internships.get(0);
        assertEquals("Approved", internship.getStatus());
        assertTrue(internship.isVisible());
        assertEquals(1, internship.getAvailableSlots());

        List<Application> applications = applicationManager.getApplicationsForStudent(student.getUserId());
        assertEquals(1, applications.size());
        Application application = applications.get(0);
        assertEquals("Successful", application.getStatus());
        assertTrue(application.isPlacementAccepted());
    }
}

