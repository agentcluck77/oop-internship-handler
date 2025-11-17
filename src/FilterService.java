import java.util.List;

/**
 * Service for managing internship filters.
 * Extracted from Main.java lines 22-25, 368-422 to follow Single Responsibility Principle.
 */
public class FilterService implements IFilterService {
    private Filter currentFilter;

    public FilterService() {
        this.currentFilter = new Filter();
    }

    /**
     * Set filters based on user input
     * Extracted from Main.java lines 368-387
     */
    public void setFilters(String status, String major, String level, String closingDate) {
        currentFilter.setStatus(status);
        currentFilter.setMajor(major);
        currentFilter.setLevel(level);
        currentFilter.setClosingDate(closingDate);
    }

    /**
     * Clear all active filters
     * Extracted from Main.java lines 389-395
     */
    public void clearFilters() {
        currentFilter.clear();
    }

    /**
     * Apply current filters to a list of internships
     */
    public List<Internship> applyFilters(List<Internship> internships) {
        return currentFilter.apply(internships);
    }

    /**
     * Get display string for active filters
     */
    public String getActiveFiltersDisplay() {
        return currentFilter.getDisplayString();
    }

    /**
     * Check if any filters are currently active
     */
    public boolean hasActiveFilters() {
        return currentFilter.hasActiveFilters();
    }

    /**
     * Get the current filter object
     */
    public Filter getCurrentFilter() {
        return currentFilter;
    }
}
