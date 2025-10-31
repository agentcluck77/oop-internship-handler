public class Student extends User {
    private int year;
    private String major;

    public Student(String userId, String password, String name, int year, String major) {
        super(userId, password, name);
        this.year = year;
        this.major = major;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public boolean canApplyForLevel(String level) {
        if (year <= 2) {
            return level.equals("Basic");
        } else {
            return true;
        }
    }
}