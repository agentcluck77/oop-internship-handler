import controller.ApplicationController;
import service.ApplicationManager;
import service.CSVLoaderService;
import service.IApplicationManager;
import service.IInternshipManager;
import service.IUserManager;
import service.IValidationService;
import service.InternshipManager;
import service.UserManager;
import service.ValidationService;
import ui.CompanyRepMenuHandlerFactory;
import ui.ConsoleUI;
import ui.ConsoleUIImpl;
import ui.MenuHandlerFactoryRegistry;
import ui.StaffMenuHandlerFactory;
import ui.StudentMenuHandlerFactory;
import util.BusinessRules;
import controller.AuthenticationController;

/**
 * Bootstraps the Internship Placement Management System.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize console UI first so shared services can report through it
        ConsoleUI ui = new ConsoleUIImpl();

        // Initialize concrete implementations
        IValidationService validationService = new ValidationService();
        IUserManager userManager = new UserManager();
        IInternshipManager internshipManager = new InternshipManager();
        IApplicationManager applicationManager = new ApplicationManager();

        // Initialize CSV loader service (uses interfaces)
        CSVLoaderService csvLoader = new CSVLoaderService(userManager, validationService, ui);

        // Load initial data from CSV files
        csvLoader.loadStudents(BusinessRules.STUDENT_CSV_PATH);
        csvLoader.loadStaff(BusinessRules.STAFF_CSV_PATH);

        // Initialize authentication controller (uses interfaces)
        AuthenticationController authController = new AuthenticationController(
            userManager,
            validationService,
            ui
        );

        // Create factory registry for menu handlers (follows Open/Closed Principle)
        MenuHandlerFactoryRegistry factoryRegistry = new MenuHandlerFactoryRegistry();

        // Register factories for each user type
        factoryRegistry.register(new StudentMenuHandlerFactory(
            internshipManager,
            applicationManager,
            ui
        ));

        factoryRegistry.register(new CompanyRepMenuHandlerFactory(
            internshipManager,
            applicationManager,
            userManager,
            validationService,
            ui
        ));

        factoryRegistry.register(new StaffMenuHandlerFactory(
            userManager,
            internshipManager,
            applicationManager,
            ui
        ));

        // Initialize main application controller (uses interfaces and factory)
        ApplicationController app = new ApplicationController(
            ui,
            authController,
            factoryRegistry
        );

        // Run the application
        app.run();
    }
}
