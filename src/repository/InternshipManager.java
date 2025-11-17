package repository;
import model.*;
import java.util.*;

public class InternshipManager implements InternshipRepository {
    private List<Internship> internships = new ArrayList<>();

    @Override
    public Internship findById(int id) {
        for (Internship i : internships) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    @Override
    public void save(Internship internship) {
        Internship existing = findById(internship.getId());
        if (existing == null) {
            internships.add(internship);
        }
    }

    @Override
    public List<Internship> findAll() {
        return new ArrayList<>(internships);
    }

    @Override
    public List<Internship> findByRepId(String repId) {
        List<Internship> result = new ArrayList<>();
        for (Internship i : internships) {
            if (i.getRepId().equals(repId)) {
                result.add(i);
            }
        }
        return result;
    }

    @Override
    public List<Internship> getPendingInternships() {
        List<Internship> result = new ArrayList<>();
        for (Internship i : internships) {
            if (i.getStatus().equals("Pending")) {
                result.add(i);
            }
        }
        return result;
    }

    @Override
    public boolean delete(int id) {
        Internship internship = findById(id);
        if (internship != null && internship.canEdit()) {
            internships.remove(internship);
            return true;
        }
        return false;
    }
}
