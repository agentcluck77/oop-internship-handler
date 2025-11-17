/**
 * Factory interface for creating MenuHandlers.
 * Follows Open/Closed Principle - new user types can be added without modifying existing code.
 */
public interface MenuHandlerFactory {
    /**
     * Create a MenuHandler for the given user
     * @param user The authenticated user
     * @return MenuHandler appropriate for the user type
     */
    MenuHandler create(User user);

    /**
     * Check if this factory can handle the given user type
     * @param user The user to check
     * @return true if this factory can create a handler for this user type
     */
    boolean canHandle(User user);
}
