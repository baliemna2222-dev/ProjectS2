package JStream.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import JStream.entity.User;
import JStream.utils.Database;

public class UserDAO {

    private final Connection conn; // final connection inside DAO

    // ===== Constructor opens connection =====
    public UserDAO() throws SQLException {
        this.conn = Database.getConnection();
        System.out.println("Database connection opened for UserDAO");
    }

    // ===== Sign Up =====
    public boolean insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword()); // password already hashed
            stmt.executeUpdate();
            return true;
        }
    }

    // ===== Login by username =====
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password")); // hash for verification
                    return user;
                }
            }
        }
        return null;
    }

    // ===== Email exists =====
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE email=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ===== Username exists =====
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
 // ===== Update username =====
    public boolean updateUsername(int userId, String newUsername) throws SQLException {
        String sql = "UPDATE users SET username = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newUsername);
            stmt.setInt(2, userId);
            int updated = stmt.executeUpdate();
            return updated > 0;
        }
    }
 // ===== Update profile photo =====
    public boolean updateProfilePhoto(int userId, String imagePath) throws SQLException {
        String sql = "UPDATE users SET profile_photo = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, imagePath);
            stmt.setInt(2, userId);
            int updated = stmt.executeUpdate();
            return updated > 0;
        }
    }

    public String getProfilePhotoPath(int userId) throws SQLException {
        String sql = "SELECT profile_photo FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("profile_photo");
                } else {
                    return null;
                }
            }
        }
    }
    // ===== Update user password =====
    public boolean updateUserPassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword); // optionally hash it
            stmt.setInt(2, userId);
            int updated = stmt.executeUpdate();
            return updated > 0;
        }
    }
    // ===== Close connection =====
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed for UserDAO");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}