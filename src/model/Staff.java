package model;

public class Staff extends User {
    private String staffDepartment;

    public Staff(String userId, String password, String name, String staffDepartment) {
        super(userId, password, name);
        this.staffDepartment = staffDepartment;
    }

    public String getStaffDepartment() { return staffDepartment; }
    
    @Override
    public String getRole() { return "STAFF"; }
}
