package model;
public class Staff extends User {
    private String department;
    private String email;

    public Staff(String userId, String password, String name, String department) {
        super(userId, password, name);
        this.department = department;
        this.email = "";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}