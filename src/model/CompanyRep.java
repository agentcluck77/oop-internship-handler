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

    public String getCompanyName() { return companyName; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    
    @Override
    public String getRole() { return "COMPANY_REP"; }
}
