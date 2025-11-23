import model.CompanyRep;
import model.Internship;
import model.Staff;
import model.Student;

public final class TestFixtures {
    private TestFixtures() {}

    public static Student makeStudent(int year, String major) {
        return new Student("U1234567A", "password", "Test Student", year, major);
    }

    public static CompanyRep makeCompanyRep(String id, boolean approved) {
        CompanyRep rep = new CompanyRep(id, "pass", "Rep Name", "TechCorp", "HR", "Manager");
        rep.setApproved(approved);
        return rep;
    }

    public static Staff makeStaff() {
        return new Staff("staff001", "staffpass", "Staff Admin", "Career Center");
    }

    public static Internship makeInternship(String title, String level, String major, String repId) {
        Internship internship = new Internship(
            title,
            "Description",
            level,
            major,
            "2025-01-01",
            "2026-01-01",
            "TechCorp",
            repId,
            2
        );
        internship.setStatus("Approved");
        internship.setVisible(true);
        return internship;
    }
}

