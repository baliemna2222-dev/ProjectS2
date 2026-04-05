package JStream.dao;

import JStream.entity.WatchStatus;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EpisodeProgressDAO {

    // ----------------- Load all episode progress for a user -----------------
    public Map<Integer, WatchStatus> getProgressForUser(int userId) {
        Map<Integer, WatchStatus> progressMap = new HashMap<>();
        String sql = "SELECT ep_id, status FROM episode_progress WHERE user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int epId = rs.getInt("ep_id");
                    String dbStatus = rs.getString("status");
                    WatchStatus status = (dbStatus != null) ? WatchStatus.valueOf(dbStatus) : WatchStatus.NOT_STARTED;
                    progressMap.put(epId, status);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return progressMap;
    }

    // ----------------- Mark episode in progress -----------------
    public void setInProgress(int userId, int epId, int lastPosition) {
        String sql = "INSERT INTO episode_progress (user_id, ep_id, status, last_position) " +
                     "VALUES (?, ?, 'IN_PROGRESS', ?) " +
                     "ON DUPLICATE KEY UPDATE status='IN_PROGRESS', last_position=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, epId);
            ps.setInt(3, lastPosition);
            ps.setInt(4, lastPosition);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Mark episode completed -----------------
    public void setCompleted(int userId, int epId, int lastPosition) {
        String sql = "INSERT INTO episode_progress (user_id, ep_id, status, last_position) " +
                     "VALUES (?, ?, 'COMPLETED', ?) " +
                     "ON DUPLICATE KEY UPDATE status='COMPLETED', last_position=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, epId);
            ps.setInt(3, lastPosition);
            ps.setInt(4, lastPosition);

            ps.executeUpdate();
            System.out.println("✅ Episode " + epId + " marked as COMPLETED for user " + userId);

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in setCompleted: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ----------------- Get status for a single episode -----------------
    public WatchStatus getEpisodeStatus(int userId, int epId) {
        String sql = "SELECT status FROM episode_progress WHERE user_id = ? AND ep_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, epId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbStatus = rs.getString("status");
                    return (dbStatus != null) ? WatchStatus.valueOf(dbStatus) : WatchStatus.NOT_STARTED;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return WatchStatus.NOT_STARTED;
    }

    // ----------------- Get last watched position -----------------
    public int getLastPosition(int userId, int epId) {
        String sql = "SELECT last_position FROM episode_progress WHERE user_id = ? AND ep_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, epId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("last_position");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}