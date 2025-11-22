package model;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates filter criteria for internships.
 */
public class Filter {
    private String status;
    private String major;
    private String level;
    private String closingDate;

    public Filter() {
        this.status = null;
        this.major = null;
        this.level = null;
        this.closingDate = null;
    }

    // Setters
    public void setStatus(String status) {
        this.status = (status == null || status.trim().isEmpty()) ? null : status.trim();
    }

    public void setMajor(String major) {
        this.major = (major == null || major.trim().isEmpty()) ? null : major.trim();
    }

    public void setLevel(String level) {
        this.level = (level == null || level.trim().isEmpty()) ? null : level.trim();
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = (closingDate == null || closingDate.trim().isEmpty()) ? null : closingDate.trim();
    }

    // Getters
    public String getStatus() {
        return status;
    }

    public String getMajor() {
        return major;
    }

    public String getLevel() {
        return level;
    }

    public String getClosingDate() {
        return closingDate;
    }

    /**
     * Clear all filters
     */
    public void clear() {
        this.status = null;
        this.major = null;
        this.level = null;
        this.closingDate = null;
    }

    /**
     * Check if any filters are active
     */
    public boolean hasActiveFilters() {
        return status != null || major != null || level != null || closingDate != null;
    }

    /**
     * Apply filters to a list of internships.
     */
    public List<Internship> apply(List<Internship> internships) {
        List<Internship> filtered = new ArrayList<>();

        for (Internship internship : internships) {
            boolean matches = true;

            if (status != null && !internship.getStatus().equalsIgnoreCase(status)) {
                matches = false;
            }
            if (major != null && !internship.getPreferredMajor().equalsIgnoreCase(major)) {
                matches = false;
            }
            if (level != null && !internship.getLevel().equalsIgnoreCase(level)) {
                matches = false;
            }
            if (closingDate != null && !internship.getClosingDate().equals(closingDate)) {
                matches = false;
            }

            if (matches) {
                filtered.add(internship);
            }
        }

        return filtered;
    }

    /**
     * Get display string for active filters.
     */
    public String getDisplayString() {
        if (!hasActiveFilters()) {
            return "";
        }

        StringBuilder sb = new StringBuilder("Active Filters: ");
        if (status != null) sb.append("Status=").append(status).append(" ");
        if (major != null) sb.append("Major=").append(major).append(" ");
        if (level != null) sb.append("Level=").append(level).append(" ");
        if (closingDate != null) sb.append("ClosingDate=").append(closingDate).append(" ");

        return sb.toString();
    }
}
