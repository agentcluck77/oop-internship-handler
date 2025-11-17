import java.util.*;

public class InternshipManager implements IInternshipManager {
    private List<Internship> internships;

    public InternshipManager() {
        this.internships = new ArrayList<>();
    }

    public void addInternship(Internship internship) {
        internships.add(internship);
    }

    public List<Internship> getAllInternships() {
        return new ArrayList<>(internships);
    }

    public List<Internship> getInternshipsForStudent(Student student) {
        List<Internship> result = new ArrayList<>();

        for (Internship internship : internships) {
            if (internship.getStatus().equals("Approved") &&
                    internship.isVisible() &&
                    internship.getPreferredMajor().equals(student.getMajor()) &&
                    student.canApplyForLevel(internship.getLevel()) &&
                    internship.getAvailableSlots() > 0 &&
                    !internship.getStatus().equals("Filled")) {
                result.add(internship);
            }
        }

        Collections.sort(result, new Comparator<Internship>() {
            public int compare(Internship i1, Internship i2) {
                return i1.getTitle().compareTo(i2.getTitle());
            }
        });

        return result;
    }

    public List<Internship> getInternshipsForCompany(String repId) {
        List<Internship> result = new ArrayList<>();

        for (Internship internship : internships) {
            if (internship.getRepId().equals(repId)) {
                result.add(internship);
            }
        }

        return result;
    }

    public int getInternshipCountForCompany(String repId) {
        int count = 0;
        for (Internship internship : internships) {
            if (internship.getRepId().equals(repId)) {
                count++;
            }
        }
        return count;
    }

    public List<Internship> getPendingInternships() {
        List<Internship> result = new ArrayList<>();

        for (Internship internship : internships) {
            if (internship.getStatus().equals("Pending")) {
                result.add(internship);
            }
        }

        return result;
    }

    public List<Internship> generateReport(String status, String major, String level) {
        List<Internship> result = new ArrayList<>();

        for (Internship internship : internships) {
            boolean matches = true;

            if (status != null && !internship.getStatus().equals(status)) {
                matches = false;
            }

            if (major != null && !internship.getPreferredMajor().equals(major)) {
                matches = false;
            }

            if (level != null && !internship.getLevel().equals(level)) {
                matches = false;
            }

            if (matches) {
                result.add(internship);
            }
        }

        Collections.sort(result, new Comparator<Internship>() {
            public int compare(Internship i1, Internship i2) {
                return i1.getTitle().compareTo(i2.getTitle());
            }
        });

        return result;
    }

    public Internship getInternshipById(int id) {
        for (Internship internship : internships) {
            if (internship.getId() == id) {
                return internship;
            }
        }
        return null;
    }
}