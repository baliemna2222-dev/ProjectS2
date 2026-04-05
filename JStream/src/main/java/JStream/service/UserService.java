package JStream.service;

import java.util.Random;

import JStream.dao.UserDAO;
import JStream.entity.User;
import JStream.utils.SecurityUtils;

public class UserService {

    private final UserDAO userDAO;

    // Verification code for forgot password
    private String currentCode;
    private long codeExpiry;
    
    // ===== Constructor =====
    public UserService() {
        this.userDAO = new UserDAO(); // DAO is thread-safe with HikariCP
    }
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        return userDAO.getUserByEmail(email.trim());
    }
    // ================= REGISTER =================
    public boolean register(String username, String email, String password) {
        if (userDAO.usernameExists(username)) {
            System.out.println("Username already exists");
            return false;
        }
        if (userDAO.emailExists(email)) {
            System.out.println("Email already exists");
            return false;
        }

        // Hash password
        String hashedPassword = SecurityUtils.hashPassword(password);

        // Create user object
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        // Insert user
        return userDAO.insertUser(user);
    }

    // ================= LOGIN =================
    public User login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && SecurityUtils.checkPassword(password, user.getPassword())) {
            return user; // login success
        }
        return null; // login failed
    }

    // ================= CHECK EXISTENCE =================
    public boolean usernameExists(String username) {
        return userDAO.usernameExists(username);
    }

    public boolean emailExists(String email) {
        return userDAO.emailExists(email);
    }

    // ================= FORGOT PASSWORD =================
    public boolean sendVerificationCode(String email) {
        // Check email exists in DB
        if (!emailExists(email)) return false;

        // Generate + send
        String code = EmailService.generateCode();
        return EmailService.sendVerificationEmail(email, code);
    }

    public boolean verifyCode(String inputCode) {
        String expected = EmailService.getLastGeneratedCode();
        return expected != null && expected.equals(inputCode.trim());
    }

    // ================= PROFILE =================
    public String getProfilePhoto(int userId) {
        return userDAO.getProfilePhotoPath(userId);
    }

    public String getEmail(int userId) {
        return userDAO.getEmail(userId);
    }

    public boolean updateUsername(int userId, String newUsername) {
        if (userDAO.usernameExists(newUsername)) {
            System.out.println("Username already exists!");
            return false;
        }
        return userDAO.updateUsername(userId, newUsername);
    }

    public boolean updateProfilePhoto(int userId, String imagePath) {
        return userDAO.updateProfilePhoto(userId, imagePath);
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        // Hash the new password before storing
        String hashedPassword = SecurityUtils.hashPassword(newPassword);
        return userDAO.updateUserPassword(userId, hashedPassword);
    }
}