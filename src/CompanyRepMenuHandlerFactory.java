/**
 * Factory for creating CompanyRepMenuHandler.
 * Follows Open/Closed Principle and Dependency Inversion Principle.
 */
public class CompanyRepMenuHandlerFactory implements MenuHandlerFactory {
    private IInternshipManager internshipManager;
    private IApplicationManager applicationManager;
    private IUserManager userManager;
    private IFilterService filterService;
    private IValidationService validationService;
    private ConsoleUI ui;

    public CompanyRepMenuHandlerFactory(IInternshipManager internshipManager,
                                       IApplicationManager applicationManager,
                                       IUserManager userManager,
                                       IFilterService filterService,
                                       IValidationService validationService,
                                       ConsoleUI ui) {
        this.internshipManager = internshipManager;
        this.applicationManager = applicationManager;
        this.userManager = userManager;
        this.filterService = filterService;
        this.validationService = validationService;
        this.ui = ui;
    }

    @Override
    public boolean canHandle(User user) {
        return user instanceof CompanyRep;
    }

    @Override
    public MenuHandler create(User user) {
        CompanyRep rep = (CompanyRep) user;
        CompanyRepController controller = new CompanyRepController(
            rep,
            internshipManager,
            applicationManager,
            userManager,
            filterService,
            validationService,
            ui
        );
        return new CompanyRepMenuHandler(ui, controller);
    }
}
