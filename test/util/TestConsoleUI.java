import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Lightweight ConsoleUI implementation for unit tests.
 * Accepts a queue of predetermined inputs and records all outputs.
 */
public class TestConsoleUI implements ConsoleUI {
    private final Queue<String> scriptedInputs;
    private final List<String> messages;
    private final List<String> errors;

    public TestConsoleUI(List<String> scriptedInputs) {
        this.scriptedInputs = new LinkedList<>(scriptedInputs);
        this.messages = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    @Override
    public void displayMessage(String message) {
        messages.add(message);
    }

    @Override
    public void displayError(String message) {
        errors.add(message);
    }

    @Override
    public String getInput(String prompt) {
        if (scriptedInputs.isEmpty()) {
            throw new IllegalStateException("No scripted input remaining for prompt: " + prompt);
        }
        return scriptedInputs.poll();
    }

    @Override
    public int getIntInput(String prompt) {
        return Integer.parseInt(getInput(prompt));
    }

    @Override
    public void displayMenu(String title, List<String> options) {
        messages.add("MENU:" + title + ":" + options.toString());
    }

    @Override
    public void displayInternship(Internship internship, int index) {
        messages.add("INTERNSHIP:" + index + ":" + internship.getTitle());
    }

    @Override
    public void displayApplication(Application app, int index, Student student) {
        messages.add("APPLICATION:" + index + ":" + app.getInternship().getTitle() + ":" + app.getStatus());
    }

    @Override
    public void displayActiveFilters(String filterDisplay) {
        messages.add("FILTERS:" + filterDisplay);
    }

    @Override
    public void displaySeparator() {
        messages.add("SEPARATOR");
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<String> getErrors() {
        return errors;
    }
}

