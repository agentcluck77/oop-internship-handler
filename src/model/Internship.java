package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    private int filledSlots;
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
        this.totalSlots = Math.min(totalSlots, 10); // Max 10 slots
        this.filledSlots = 0;
        this.visible = true;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLevel() { return level; }
    public String getPreferredMajor() { return preferredMajor; }
    public String getOpeningDate() { return openingDate; }
    public String getClosingDate() { return closingDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCompanyName() { return companyName; }
    public String getRepId() { return repId; }
    public int getTotalSlots() { return totalSlots; }
    public int getFilledSlots() { return filledSlots; }
    public int getAvailableSlots() { return totalSlots - filledSlots; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void toggleVisibility() { this.visible = !this.visible; }

    public void incrementFilledSlots() {
        if (filledSlots < totalSlots) {
            filledSlots++;
            if (filledSlots == totalSlots) {
                status = "Filled";
            }
        }
    }

    public void decrementFilledSlots() {
        if (filledSlots > 0) {
            filledSlots--;
            if (status.equals("Filled")) {
                status = "Approved";
            }
        }
    }

    public boolean canEdit() {
        return status.equals("Pending");
    }

    public boolean isOpenForApplications() {
        if (!status.equals("Approved") || getAvailableSlots() <= 0 || !visible) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd");
            LocalDate closeDate = LocalDate.parse(closingDate, formatter);
            LocalDate today = LocalDate.now();
            return !today.isAfter(closeDate);
        } catch (Exception e) {
            return false;
        }
    }
}
