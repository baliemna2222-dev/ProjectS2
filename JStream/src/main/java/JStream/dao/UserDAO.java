package JStream.dao;

import JStream.entity.User;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // ===== Sign Up =====
    public boolean insertUser(User user) {
        String sql = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword()); // already hashed
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String getUsernameById(int userId) {
        String sql = "SELECT username FROM users WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("username");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "User #" + userId;
    }
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                // add any other fields your User entity has
                return user;
            }

        } catch (SQLException e) {
            System.err.println("❌ getUserByEmail error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password")); // hashed password
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== Email exists =====
    public boolean emailExists(String email) {
        String sql = "SELECT user_id FROM users WHERE email=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== Username exists =====
    public boolean usernameExists(String username) {
        String sql = "SELECT user_id FROM users WHERE username=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        
    }

    // ===== Update username =====
    public boolean updateUsername(int userId, String newUsername) {
        String sql = "UPDATE users SET username = ? WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newUsername);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== Update profile photo =====
    public boolean updateProfilePhoto(int userId, String imagePath) {
        String sql = "UPDATE users SET profile_photo = ? WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, imagePath);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== Get profile photo path =====
    public String getProfilePhotoPath(int userId) {
        String sql = "SELECT profile_photo FROM users WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("profile_photo");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== Get email =====
    public String getEmail(int userId) {
        String sql = "SELECT email FROM users WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("email");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== Update password =====
    public boolean updateUserPassword(int userId, String hashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}