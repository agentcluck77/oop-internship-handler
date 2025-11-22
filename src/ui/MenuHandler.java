package ui;
/**
 * Interface for menu handling behavior.
 * Follows Open/Closed Principle - easy to add new menu types without modifying existing code.
 */
public interface MenuHandler {
    /**
     * Display the menu to the user
     */
    void show();

    /**
     * Handle user's menu choice
     * @return true to continue showing menu, false to logout/exit
     */
    boolean handleChoice();
}
