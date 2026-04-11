package JStream.dao;

import JStream.entity.NewEpisodeInfo;
import JStream.entity.Notification;
import JStream.utils.Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public boolean isFirstLogin(int userId) {
        String sql = "SELECT first_login_done FROM user_meta WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return !rs.getBoolean("first_login_done");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void markFirstLoginDone(int userId) {
        String sql = "INSERT INTO user_meta (user_id, first_login_done) VALUES (?, TRUE) " +
                     "ON DUPLICATE KEY UPDATE first_login_done = TRUE";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void addNotification(int userId, String title, String body, String type) {
        if (notificationExists(userId, title, body)) return;
        String sql = "INSERT INTO notifications (user_id, title, body, type) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, body);
            ps.setString(4, type);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private boolean notificationExists(int userId, String title, String body) {
        String sql = "SELECT id FROM notifications WHERE user_id = ? AND title = ? AND body = ? LIMIT 1";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, body);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Notification> getNotifications(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT id, title, body, type, is_read, created_at " +
                     "FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 50";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Notification(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("body"),
                    rs.getString("type"),
                    rs.getBoolean("is_read"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public void markRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void markAllRead(int userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public List<NewEpisodeInfo> getNewEpisodesForUser(int userId) {
        List<NewEpisodeInfo> result = new ArrayList<>();
        String sql =
            "SELECT DISTINCT " +
            "  se.serie_id       AS serie_id, " +
            "  se.title          AS serie_title, " +
            "  sa.season_num     AS season_number, " +
            "  e.ep_id           AS ep_id, " +
            "  e.num_episode     AS episode_number, " +
            "  e.title           AS ep_title " +
            "FROM episode e " +
            "JOIN season sa ON e.season_id = sa.season_id " +
            "JOIN serie  se ON sa.serie_id = se.serie_id " +
            "WHERE sa.serie_id IN ( " +
            "    SELECT serie_id FROM my_list " +
            "    WHERE user_id = ? AND serie_id IS NOT NULL " +
            ") " +
            "AND e.released_at > COALESCE( " +
            "    (SELECT last_ep_check FROM user_meta WHERE user_id = ?), " +
            "    DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            ") " +
            "ORDER BY e.released_at DESC " +
            "LIMIT 10";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new NewEpisodeInfo(
                    rs.getInt("serie_id"),
                    rs.getString("serie_title"),
                    rs.getInt("season_number"),
                    rs.getInt("episode_number"),
                    rs.getString("ep_title"),
                    rs.getInt("ep_id")
                ));
            }
            updateLastEpCheck(userId);
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }
 // In NotificationDAO — only update AFTER notifications are sent
    private void updateLastEpCheck(int userId) {
        String sql = "INSERT INTO user_meta (user_id, last_ep_check) VALUES (?, NOW()) " +
                     "ON DUPLICATE KEY UPDATE last_ep_check = NOW()";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
 // ================= DELETE ONE =================
    public void deleteNotification(int notificationId) {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= DELETE ALL =================
    public void deleteAll(int userId) {
        String sql = "DELETE FROM notifications WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}