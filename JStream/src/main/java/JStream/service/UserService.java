package JStream.service;

import JStream.dao.UserDAO;
import JStream.entity.User;
import JStream.utils.SecurityUtils;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final UserDAO userDAO;

    // ===== Constructor =====
    public UserService() {
        this.userDAO = new UserDAO();
    }

    // ================= REGISTER =================
    public boolean register(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) return false;
        if (email == null || email.trim().isEmpty()) return false;
        if (password == null || password.isEmpty()) return false;
        
        if (userDAO.usernameExists(username)) {
            System.out.println("❌ Username already exists");
            return false;
        }
        if (userDAO.emailExists(email)) {
            System.out.println("❌ Email already exists");
            return false;
        }

        // Hash password before storing
        String hashedPassword = SecurityUtils.hashPassword(password);

        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setPassword(hashedPassword);
        user.setRole(JStream.entity.UserRole.USER);
        return userDAO.insertUser(user);
    }

    // ================= LOGIN =================
    public User login(String username, String plainPassword) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && BCrypt.checkpw(plainPassword, user.getPassword())) {
            return user;
        }
        return null;
    }

    // ================= EXISTENCE CHECKS =================
    public boolean usernameExists(String username) {
        return username != null && !username.isEmpty() && userDAO.usernameExists(username.trim());
    }

    public boolean emailExists(String email) {
        return email != null && !email.isEmpty() && userDAO.emailExists(email.trim());
    }

    // ================= PROFILE =================
    public String getProfilePhoto(int userId) {
        return userDAO.getProfilePhotoPath(userId);
    }
    public String getUsernameById(int userId) {
        return new UserDAO().getUsernameById(userId);
    }
    public String getEmail(int userId) {
        return userDAO.getEmail(userId);
    }

    public boolean updateUsername(int userId, String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) return false;
        if (userDAO.usernameExists(newUsername.trim())) {
            System.out.println("❌ Username already exists");
            return false;
        }
        return userDAO.updateUsername(userId, newUsername.trim());
    }

    public boolean updateProfilePhoto(int userId, String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) return false;
        return userDAO.updateProfilePhoto(userId, imagePath.trim());
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) return false;

        // hash the new password once
        String hashedPassword = SecurityUtils.hashPassword(newPassword);

        // save hashed password to database
        return userDAO.updateUserPassword(userId, hashedPassword);
    }
    // ================= FORGOT PASSWORD =================
    public boolean sendVerificationCode(String email) {
        if (!emailExists(email)) return false;

        String code = EmailService.generateCode();
        return EmailService.sendVerificationEmail(email, code);
    }

    public boolean verifyCode(String inputCode) {
        String expected = EmailService.getLastGeneratedCode();
        return expected != null && expected.equals(inputCode != null ? inputCode.trim() : "");
    }

    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        return userDAO.getUserByEmail(email.trim());
    }
    public boolean createUser(String username, String email, String password, JStream.entity.UserRole role) {
        if (userDAO.usernameExists(username)) return false;
        if (userDAO.emailExists(email)) return false;

        String hashedPassword = SecurityUtils.hashPassword(password);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRole(role != null ? role : JStream.entity.UserRole.USER);

        return userDAO.insertUser(user);
    }

 // Récupérer un utilisateur par son username
    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }
 // ================= ADMIN : GET ALL USERS =================
    public java.util.List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public void updateUserRole(User user) {
        userDAO.updateUserRole(user);
    }

    public void deleteUser(int id) {
        userDAO.deleteUser(id);
    }

  


   
}