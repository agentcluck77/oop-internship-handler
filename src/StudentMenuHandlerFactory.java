/**
 * Factory for creating StudentMenuHandler.
 * Follows Open/Closed Principle and Dependency Inversion Principle.
 */
public class StudentMenuHandlerFactory implements MenuHandlerFactory {
    private IInternshipManager internshipManager;
    private IApplicationManager applicationManager;
    private IFilterService filterService;
    private ConsoleUI ui;

    public StudentMenuHandlerFactory(IInternshipManager internshipManager,
                                    IApplicationManager applicationManager,
                                    IFilterService filterService,
                                    ConsoleUI ui) {
        this.internshipManager = internshipManager;
        this.applicationManager = applicationManager;
        this.filterService = filterService;
        this.ui = ui;
    }

    @Override
    public boolean canHandle(User user) {
        return user instanceof Student;
    }

    @Override
    public MenuHandler create(User user) {
        Student student = (Student) user;
        StudentController controller = new StudentController(
            student,
            internshipManager,
            applicationManager,
            filterService,
            ui
        );
        return new StudentMenuHandler(ui, controller);
    }
}
