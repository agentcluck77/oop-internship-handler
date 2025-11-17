import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InternshipManagerTest {
    private InternshipManager internshipManager;

    @BeforeEach
    void setUp() {
        internshipManager = new InternshipManager();
    }

    @Test
    void getInternshipCountForCompany_ignoresRejectedOrFilled() {
        Internship approved = new Internship("Approved", "Desc", "Basic", "CSC",
            "2025-01-01", "2026-01-01", "TechCorp", "rep", 2);
        approved.setStatus("Approved");

        Internship pending = new Internship("Pending", "Desc", "Basic", "CSC",
            "2025-01-01", "2026-01-01", "TechCorp", "rep", 2);
        pending.setStatus("Pending");

        Internship rejected = new Internship("Rejected", "Desc", "Basic", "CSC",
            "2025-01-01", "2026-01-01", "TechCorp", "rep", 2);
        rejected.setStatus("Rejected");

        Internship filled = new Internship("Filled", "Desc", "Basic", "CSC",
            "2025-01-01", "2026-01-01", "TechCorp", "rep", 2);
        filled.setStatus("Filled");

        internshipManager.addInternship(approved);
        internshipManager.addInternship(pending);
        internshipManager.addInternship(rejected);
        internshipManager.addInternship(filled);

        assertEquals(2, internshipManager.getInternshipCountForCompany("rep"),
            "Only active or pending postings should count toward the limit");
    }
}

