package JStream.service;

import java.sql.SQLException;
import java.util.Random;

import JStream.dao.UserDAO;
import JStream.entity.User;
import JStream.utils.SecurityUtils;

public class UserService {

    private final UserDAO userDAO;

    // Verification code
    private String currentCode;
    private long codeExpiry;

    // ===== Constructor =====
    public UserService() {
        UserDAO dao = null;
        try {
            dao = new UserDAO(); // DAO opens its own connection
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.userDAO = dao;
    }

    // ================= REGISTER =================
    public boolean register(String username, String email, String password) {
        try {
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

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= LOGIN =================
    public User login(String username, String password) {
        try {
            User user = userDAO.getUserByUsername(username);
            if (user != null && SecurityUtils.checkPassword(password, user.getPassword())) {
                return user; // login success
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // login failed
    }

    public boolean usernameExists(String username) {
        try {
            return userDAO.usernameExists(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {
        try {
            return userDAO.emailExists(email);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= FORGOT PASSWORD =================
    public boolean sendVerificationCode(String email) {
        try {
            if (!userDAO.emailExists(email)) return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Generate 6-digit code
        currentCode = String.format("%06d", new Random().nextInt(1000000));
        codeExpiry = System.currentTimeMillis() + 15 * 60 * 1000; // 15 minutes

        // Send email
        EmailService.sendVerificationCode(email, currentCode);
        return true;
    }

    public boolean verifyCode(String code) {
        if (currentCode == null) return false;
        if (System.currentTimeMillis() > codeExpiry) return false;
        return currentCode.equals(code);
    }
    // Get profile image path
    public String getProfilePhoto(int userId) {
        try {
            return userDAO.getProfilePhotoPath(userId);
        } catch (SQLException e) { 
            e.printStackTrace();
            return null;
        }
    }
 // ===== Update username =====
    public boolean updateUsername(int userId, String newUsername) {
        try {
            // Check if new username already exists
            if (userDAO.usernameExists(newUsername)) {
                System.out.println("Username already exists!");
                return false;
            }
            return userDAO.updateUsername(userId, newUsername);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateProfilePhoto(int userId, String imagePath) {
        try {
            return userDAO.updateProfilePhoto(userId, imagePath);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update user password
    public boolean updateUserPassword(int userId, String newPassword) {
        try {
            return userDAO.updateUserPassword(userId, newPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // ================= CLEAN UP =================
    public void close() {
        if (userDAO != null) {
            userDAO.close(); // close DAO connection
        }
    }
}