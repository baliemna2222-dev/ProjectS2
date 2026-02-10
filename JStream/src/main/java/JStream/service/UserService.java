package JStream.service;

import java.util.Random;
import JStream.dao.UserDAO;
import JStream.entity.User;
import JStream.utils.SecurityUtils;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    // Verification code
    private String currentCode;
    private long codeExpiry;

    // ================= REGISTER =================
    public boolean register(String username, String email, String password) {

        if (userDAO.usernameExists(username)) {
            System.out.println("Username already exists");
            return false; // username already taken
        }

        if (userDAO.emailExists(email)) {
            System.out.println("Email already exists");
            return false; // email already taken
        }

        // Hash password with BCrypt
        String hashedPassword = SecurityUtils.hashPassword(password);

        // Insert user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        return userDAO.insertUser(user);
    }

    // ================= LOGIN =================
    public User login(String username, String password) {
        // Get user by username
        User user = userDAO.getUserByUsername(username);
        if (user != null) {
            // Compare entered password with hashed password
            if (SecurityUtils.checkPassword(password, user.getPassword())) {
                return user; // login success
            }
        }
        return null; // login failed
    }
    public boolean usernameExists(String username) {
        return userDAO.usernameExists(username);
    }

    public boolean emailExists(String email) {
        return userDAO.emailExists(email);
    }

    // ================= FORGOT PASSWORD =================
    public boolean sendVerificationCode(String email) {
        if (!userDAO.emailExists(email)) return false;

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
}
