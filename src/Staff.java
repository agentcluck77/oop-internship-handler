public class Staff extends User {
    private String department;

    public Staff(String userId, String password, String name, String department) {
        super(userId, password, name);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}