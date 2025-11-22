package model;
public class CompanyRep extends User {
    private String companyName;
    private String department;
    private String position;
    private boolean approved;

    public CompanyRep(String userId, String password, String name,
                      String companyName, String department, String position) {
        super(userId, password, name);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.approved = false;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}