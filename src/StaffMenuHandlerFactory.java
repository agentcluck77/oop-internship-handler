/**
 * Factory for creating StaffMenuHandler.
 * Follows Open/Closed Principle and Dependency Inversion Principle.
 */
public class StaffMenuHandlerFactory implements MenuHandlerFactory {
    private IUserManager userManager;
    private IInternshipManager internshipManager;
    private IApplicationManager applicationManager;
    private ConsoleUI ui;

    public StaffMenuHandlerFactory(IUserManager userManager,
                                  IInternshipManager internshipManager,
                                  IApplicationManager applicationManager,
                                  ConsoleUI ui) {
        this.userManager = userManager;
        this.internshipManager = internshipManager;
        this.applicationManager = applicationManager;
        this.ui = ui;
    }

    @Override
    public boolean canHandle(User user) {
        return user instanceof Staff;
    }

    @Override
    public MenuHandler create(User user) {
        Staff staff = (Staff) user;
        IFilterService filterService = new FilterService();
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
