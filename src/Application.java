public class Application {
    private static int idCounter = 1;

    private int id;
    private String studentId;
    private Internship internship;
    private String status;
    private String withdrawalReason;
    private String withdrawalStatus;
    private boolean placementAccepted;

    public Application(String studentId, Internship internship) {
        this.id = idCounter++;
        this.studentId = studentId;
        this.internship = internship;
        this.status = "Pending";
        this.withdrawalReason = null;
        this.withdrawalStatus = null;
        this.placementAccepted = false;
    }

    public int getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public Internship getInternship() {
        return internship;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWithdrawalReason() {
        return withdrawalReason;
    }

    public void setWithdrawalReason(String withdrawalReason) {
        this.withdrawalReason = withdrawalReason;
    }

    public String getWithdrawalStatus() {
        return withdrawalStatus;
    }

    public void setWithdrawalStatus(String withdrawalStatus) {
        this.withdrawalStatus = withdrawalStatus;
    }

    public boolean isPlacementAccepted() {
        return placementAccepted;
    }

    public void setPlacementAccepted(boolean placementAccepted) {
        this.placementAccepted = placementAccepted;
    }
}