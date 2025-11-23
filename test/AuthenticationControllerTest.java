import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.CompanyRep;
import model.Student;
import model.User;
import service.ApplicationManager;
import service.FilterService;
import service.InternshipManager;
import service.UserManager;
import service.ValidationService;
import controller.AuthenticationController;
import controller.StudentController;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationControllerTest {
    private AuthenticationController controller;
    private UserManager userManager;
    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        validationService = new ValidationService();
    }

    @Test
    void validUserLoginRoutesToDashboard() {
        Student student = new Student("U1234567A", "password", "Student One", 2, "CSC");
        userManager.addUser(student);

        TestConsoleUI ui = new TestConsoleUI(Arrays.asList("U1234567A", "password"));
        controller = new AuthenticationController(userManager, validationService, ui);

        User loggedIn = controller.login();

        assertNotNull(loggedIn);
        assertEquals(student, loggedIn);
        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.contains("Login successful")));
    }

    @Test
    void invalidIdShowsMeaningfulError() {
        Student student = new Student("U1234567A", "password", "Student One", 2, "CSC");
        userManager.addUser(student);

        TestConsoleUI ui = new TestConsoleUI(Arrays.asList("invalid", "password"));
        controller = new AuthenticationController(userManager, validationService, ui);

        User loggedIn = controller.login();

        assertNull(loggedIn);
        assertTrue(ui.getErrors().stream().anyMatch(msg -> msg.contains("Invalid credentials")));
    }

    @Test
    void incorrectPasswordDenied() {
        Student student = new Student("U1234567A", "password", "Student One", 2, "CSC");
        userManager.addUser(student);

        TestConsoleUI ui = new TestConsoleUI(Arrays.asList("U1234567A", "wrongpass"));
        controller = new AuthenticationController(userManager, validationService, ui);

        User loggedIn = controller.login();

        assertNull(loggedIn);
        assertTrue(ui.getErrors().stream().anyMatch(msg -> msg.contains("Invalid credentials")));
    }

    @Test
    void passwordChangeRequiresRelogin() {
        Student student = new Student("U1234567A", "password", "Student One", 2, "CSC");
        student.setPassword("password");
        userManager.addUser(student);

        TestConsoleUI uiLogin = new TestConsoleUI(Arrays.asList("U1234567A", "password"));
        controller = new AuthenticationController(userManager, validationService, uiLogin);
        assertNotNull(controller.login());

        TestConsoleUI uiPasswordChange = new TestConsoleUI(Arrays.asList("password", "newpass", "newpass"));
        StudentController studentController = new StudentController(
            student,
            new InternshipManager(),
            new ApplicationManager(),
            new FilterService(),
            uiPasswordChange
        );
        assertTrue(studentController.changePassword());

        TestConsoleUI uiLoginNew = new TestConsoleUI(Arrays.asList("U1234567A", "newpass"));
        controller = new AuthenticationController(userManager, validationService, uiLoginNew);
        assertNotNull(controller.login(), "Should log in with new password");
    }

    @Test
    void companyRepCannotLoginUntilApproved() {
        CompanyRep rep = TestFixtures.makeCompanyRep("pending@corp.com", false);
        userManager.addPendingCompanyRep(rep);

        TestConsoleUI ui = new TestConsoleUI(Arrays.asList("pending@corp.com", "pass"));
        controller = new AuthenticationController(userManager, validationService, ui);

        User loggedIn = controller.login();

        assertNull(loggedIn);
        assertTrue(ui.getMessages().stream().anyMatch(msg -> msg.contains("awaiting staff approval")));
    }
}

