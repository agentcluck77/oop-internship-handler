import java.util.List;

/**
 * Interface for filter service operations.
 * Follows Dependency Inversion Principle - controllers depend on this abstraction.
 */
public interface IFilterService {
    void setFilters(String status, String major, String level, String closingDate);
    void clearFilters();
    List<Internship> applyFilters(List<Internship> internships);
    String getActiveFiltersDisplay();
    boolean hasActiveFilters();
    Filter getCurrentFilter();
}
