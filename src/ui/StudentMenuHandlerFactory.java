package ui;
import model.Student;
import model.User;
import service.FilterService;
import service.IApplicationManager;
import service.IFilterService;
import service.IInternshipManager;
import controller.StudentController;

/**
 * Factory for creating StudentMenuHandler.
 * Follows Open/Closed Principle and Dependency Inversion Principle.
 */
public class StudentMenuHandlerFactory implements MenuHandlerFactory {
    private IInternshipManager internshipManager;
    private IApplicationManager applicationManager;
    private ConsoleUI ui;

    public StudentMenuHandlerFactory(IInternshipManager internshipManager,
                                    IApplicationManager applicationManager,
                                    ConsoleUI ui) {
        this.internshipManager = internshipManager;
        this.applicationManager = applicationManager;
        this.ui = ui;
    }

    @Override
    public boolean canHandle(User user) {
        return user instanceof Student;
    }

    @Override
    public MenuHandler create(User user) {
        Student student = (Student) user;
        IFilterService filterService = new FilterService();
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
