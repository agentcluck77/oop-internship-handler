package model;

public class Student extends User {
    private int yearOfStudy;
    private String major;

    public Student(String userId, String password, String name, int yearOfStudy, String major) {
        super(userId, password, name);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
    }

    public int getYearOfStudy() { return yearOfStudy; }
    public String getMajor() { return major; }
    
    @Override
    public String getRole() { return "STUDENT"; }
    
    public boolean canApplyForLevel(String level) {
        if (yearOfStudy <= 2) {
            return level.equalsIgnoreCase("Basic");
        }
        return true; // Year 3-4 can apply for any level
    }
}
