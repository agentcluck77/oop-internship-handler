package service;
import java.util.*;

import model.CompanyRep;
import model.User;

public class UserManager implements IUserManager {
    private List<User> users;
    private List<CompanyRep> pendingCompanyReps;

    public UserManager() {
        this.users = new ArrayList<>();
        this.pendingCompanyReps = new ArrayList<>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void addPendingCompanyRep(CompanyRep rep) {
        pendingCompanyReps.add(rep);
    }

    public User login(String userId, String password) {
        for (User user : users) {
            if (user.getUserId().equals(userId) && user.getPassword().equals(password)) {
                return user;
            }
        }

        for (CompanyRep rep : pendingCompanyReps) {
            if (rep.getUserId().equals(userId) && rep.getPassword().equals(password)) {
                return rep;
            }
        }
        return null;
    }

    public User getUserById(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public List<CompanyRep> getPendingCompanyReps() {
        return new ArrayList<>(pendingCompanyReps);
    }

    public void approveCompanyRep(CompanyRep rep) {
        rep.setApproved(true);
        users.add(rep);
        pendingCompanyReps.remove(rep);
    }

    public void rejectCompanyRep(CompanyRep rep) {
        pendingCompanyReps.remove(rep);
    }
}