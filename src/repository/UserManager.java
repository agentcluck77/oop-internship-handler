package repository;
import model.*;
import java.util.*;

public class UserManager implements UserRepository {
    private List<User> users = new ArrayList<>();
    private List<CompanyRep> pendingReps = new ArrayList<>();

    @Override
    public User findById(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        for (CompanyRep rep : pendingReps) {
            if (rep.getUserId().equals(userId)) {
                return rep;
            }
        }
        return null;
    }

    @Override
    public void save(User user) {
        User existing = findById(user.getUserId());
        if (existing != null) {
            return; 
        }
        if (user instanceof CompanyRep) {
            CompanyRep rep = (CompanyRep) user;
            if (!rep.isApproved()) {
                pendingReps.add(rep);
            } else {
                users.add(user);
            }
        } else {
            users.add(user);
        }
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public List<CompanyRep> getPendingCompanyReps() {
        return new ArrayList<>(pendingReps);
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        for (User u : users) {
            if (u instanceof Student) {
                students.add((Student) u);
            }
        }
        return students;
    }

    @Override
    public List<CompanyRep> getAllCompanyReps() {
        List<CompanyRep> reps = new ArrayList<>();
        for (User u : users) {
            if (u instanceof CompanyRep) {
                reps.add((CompanyRep) u);
            }
        }
        return reps;
    }

    @Override
    public List<Staff> getAllStaff() {
        List<Staff> staff = new ArrayList<>();
        for (User u : users) {
            if (u instanceof Staff) {
                staff.add((Staff) u);
            }
        }
        return staff;
    }

    public void approveCompanyRep(CompanyRep rep) {
        rep.setApproved(true);
        pendingReps.remove(rep);
        users.add(rep);
    }

    public void rejectCompanyRep(CompanyRep rep) {
        pendingReps.remove(rep);
    }
}
