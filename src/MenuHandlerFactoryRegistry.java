import java.util.ArrayList;
import java.util.List;

/**
 * Registry for MenuHandlerFactories.
 * Follows Open/Closed Principle - new user types registered without modifying code.
 */
public class MenuHandlerFactoryRegistry {
    private List<MenuHandlerFactory> factories;

    public MenuHandlerFactoryRegistry() {
        this.factories = new ArrayList<>();
    }

    /**
     * Register a new factory
     */
    public void register(MenuHandlerFactory factory) {
        factories.add(factory);
    }

    /**
     * Create MenuHandler for the given user
     * @throws IllegalStateException if no factory can handle the user type
     */
    public MenuHandler createMenuHandler(User user) {
        for (MenuHandlerFactory factory : factories) {
            if (factory.canHandle(user)) {
                return factory.create(user);
            }
        }
        throw new IllegalStateException("No factory registered for user type: " + user.getClass().getName());
    }
}
