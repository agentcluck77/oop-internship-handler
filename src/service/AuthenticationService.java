package service;
import model.*;
import repository.*;
import util.ValidationUtil;

public class AuthenticationService {
    private UserRepository userRepo;

    public AuthenticationService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User login(String userId, String password) {
        User user = userRepo.findById(userId);
        if (user == null) {
            return null;
        }
        if (!user.getPassword().equals(password)) {
            return null;
        }
        if (user instanceof CompanyRep) {
            CompanyRep rep = (CompanyRep) user;
            if (!rep.isApproved()) {
                return null; 
            }
        }
        return user;
    }

    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (!user.getPassword().equals(oldPassword)) {
            return false;
        }
        if (!ValidationUtil.isValidPassword(newPassword)) {
            return false;
        }
        user.setPassword(newPassword);
        return true;
    }
    
    public String getPasswordChangeError(String newPassword) {
        if (!ValidationUtil.isValidPassword(newPassword)) {
            return ValidationUtil.getPasswordValidationError();
        }
        return null;
    }

    public String getLoginError(String userId, String password) {
        User user = userRepo.findById(userId);
        if (user == null) {
            return "Invalid ID: User not found!";
        }
        if (!user.getPassword().equals(password)) {
            return "Incorrect password!";
        }
        if (user instanceof CompanyRep && !((CompanyRep) user).isApproved()) {
            return "Account not approved by Career Center Staff!";
        }
        return null;
    }
}
