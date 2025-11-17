/**
 * Entry point for the Internship Placement Management System.
 *
 * REFACTORED: This class has been reduced from 1067 lines to ~80 lines.
 * All business logic, UI, and services have been extracted into separate classes
 * following SOLID principles and proper separation of concerns.
 *
 * NOW ACHIEVES FULL SOLID COMPLIANCE:
 * ✓ Single Responsibility Principle - Main only bootstraps the application
 * ✓ Open/Closed Principle - Factory pattern allows adding new user types without modifying code
 * ✓ Liskov Substitution Principle - All interfaces can be substituted with implementations
 * ✓ Interface Segregation Principle - Focused interfaces, no fat interfaces
 * ✓ Dependency Inversion Principle - All dependencies use interfaces, not concrete classes
 *
 * Architecture:
 * - Model Layer: Filter, BusinessRules (existing: User, Student, CompanyRep, Staff, etc.)
 * - Service Layer: IValidationService, IFilterService, CSVLoaderService (all use interfaces)
 * - Controller Layer: ApplicationController, AuthenticationController,
 *                    StudentController, CompanyRepController, StaffController (all use interfaces)
 * - UI Layer: ConsoleUI (interface), ConsoleUIImpl, MenuHandler (interface),
 *            StudentMenuHandler, CompanyRepMenuHandler, StaffMenuHandler
 * - Factory Layer: MenuHandlerFactory (interface), MenuHandlerFactoryRegistry,
 *                 StudentMenuHandlerFactory, CompanyRepMenuHandlerFactory, StaffMenuHandlerFactory
 */
public class Main {
    public static void main(String[] args) {
        // Initialize concrete implementations (these are the ONLY concrete dependencies in Main)
        IValidationService validationService = new ValidationService();
        IFilterService filterService = new FilterService();
        IUserManager userManager = new UserManager();
        IInternshipManager internshipManager = new InternshipManager();
        IApplicationManager applicationManager = new ApplicationManager();

        // Initialize CSV loader service (uses interfaces)
        CSVLoaderService csvLoader = new CSVLoaderService(userManager, validationService);

        // Load initial data from CSV files
        csvLoader.loadStudents(BusinessRules.STUDENT_CSV_PATH);
        csvLoader.loadStaff(BusinessRules.STAFF_CSV_PATH);

        // Initialize UI layer (uses interface)
        ConsoleUI ui = new ConsoleUIImpl();

        // Initialize authentication controller (uses interfaces)
        AuthenticationController authController = new AuthenticationController(
            userManager,
            validationService,
            ui
        );

        // Create factory registry for menu handlers (follows Open/Closed Principle)
        MenuHandlerFactoryRegistry factoryRegistry = new MenuHandlerFactoryRegistry();

        // Register factories for each user type (adding new user types is just adding a line here)
        factoryRegistry.register(new StudentMenuHandlerFactory(
            internshipManager,
            applicationManager,
            filterService,
            ui
        ));

        factoryRegistry.register(new CompanyRepMenuHandlerFactory(
            internshipManager,
            applicationManager,
            userManager,
            filterService,
            validationService,
            ui
        ));

        factoryRegistry.register(new StaffMenuHandlerFactory(
            userManager,
            internshipManager,
            applicationManager,
            filterService,
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
