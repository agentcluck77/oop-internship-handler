/**
 * Factory for creating StaffMenuHandler.
 * Follows Open/Closed Principle and Dependency Inversion Principle.
 */
public class StaffMenuHandlerFactory implements MenuHandlerFactory {
    private IUserManager userManager;
    private IInternshipManager internshipManager;
    private IApplicationManager applicationManager;
    private IFilterService filterService;
    private ConsoleUI ui;

    public StaffMenuHandlerFactory(IUserManager userManager,
                                  IInternshipManager internshipManager,
                                  IApplicationManager applicationManager,
                                  IFilterService filterService,
                                  ConsoleUI ui) {
        this.userManager = userManager;
        this.internshipManager = internshipManager;
        this.applicationManager = applicationManager;
        this.filterService = filterService;
        this.ui = ui;
    }

    @Override
    public boolean canHandle(User user) {
        return user instanceof Staff;
    }

    @Override
    public MenuHandler create(User user) {
        Staff staff = (Staff) user;
        StaffController controller = new StaffController(
            staff,
            userManager,
            internshipManager,
            applicationManager,
            filterService,
            ui
        );
        return new StaffMenuHandler(ui, controller);
    }
}
