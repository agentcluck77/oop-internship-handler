public class Internship {
    private static int idCounter = 1;

    private int id;
    private String title;
    private String description;
    private String level;
    private String preferredMajor;
    private String openingDate;
    private String closingDate;
    private String status;
    private String companyName;
    private String repId;
    private int totalSlots;
    private int availableSlots;
    private boolean visible;

    public Internship(String title, String description, String level,
                      String preferredMajor, String openingDate, String closingDate,
                      String companyName, String repId, int totalSlots) {
        this.id = idCounter++;
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.status = "Pending";
        this.companyName = companyName;
        this.repId = repId;
        this.totalSlots = totalSlots;
        this.availableSlots = totalSlots;
        this.visible = false;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLevel() {
        return level;
    }

    public String getPreferredMajor() {
        return preferredMajor;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getRepId() {
        return repId;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void decreaseAvailableSlots() {
        if (availableSlots > 0) {
            availableSlots--;
            if (availableSlots == 0) {
                status = "Filled";
            }
        }
    }

    public void increaseAvailableSlots() {
        if (availableSlots < totalSlots) {
            availableSlots++;
            if (status.equals("Filled")) {
                status = "Approved";
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void toggleVisibility() {
        this.visible = !this.visible;
    }

    public boolean isOpenForApplications() {
        return status.equals("Approved") && !status.equals("Filled") && availableSlots > 0;
    }
}