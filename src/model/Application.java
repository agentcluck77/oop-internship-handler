package model;

public class Application {
    private static int idCounter = 1;
    
    private int id;
    private String studentId;
    private Internship internship;
    private String status;
    private String withdrawalReason;
    private boolean withdrawalPending;
    private boolean placementAccepted;

    public Application(String studentId, Internship internship) {
        this.id = idCounter++;
        this.studentId = studentId;
        this.internship = internship;
        this.status = "Pending";
        this.withdrawalReason = null;
        this.withdrawalPending = false;
        this.placementAccepted = false;
    }

    public int getId() { return id; }
    public String getStudentId() { return studentId; }
    public Internship getInternship() { return internship; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getWithdrawalReason() { return withdrawalReason; }
    public void setWithdrawalReason(String reason) { this.withdrawalReason = reason; }
    public boolean isWithdrawalPending() { return withdrawalPending; }
    public void setWithdrawalPending(boolean pending) { this.withdrawalPending = pending; }
    public boolean isPlacementAccepted() { return placementAccepted; }
    public void setPlacementAccepted(boolean accepted) { this.placementAccepted = accepted; }
}
