package JStream.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import JStream.entity.WatchStatus;

public class EpisodeProgressDAO {

    private final Connection connection;

    public EpisodeProgressDAO(Connection connection) {
        this.connection = connection;
    }

    // ----------------- Load all episode progress for a user -----------------
    public Map<Integer, WatchStatus> getProgressForUser(int userId) {
        Map<Integer, WatchStatus> progressMap = new HashMap<>();
        String sql = "SELECT ep_id, status FROM episode_progress WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int epId = rs.getInt("ep_id");
                String dbStatus = rs.getString("status");
                WatchStatus status = WatchStatus.NOT_STARTED;
                if (dbStatus != null) {
                    status = WatchStatus.valueOf(dbStatus);
                }
                progressMap.put(epId, status);
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
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        String sql = "UPDATE episode_progress SET status='COMPLETED', last_position=? " +
                     "WHERE user_id=? AND ep_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, lastPosition);
            ps.setInt(2, userId);
            ps.setInt(3, epId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Get status for a single episode -----------------
    public WatchStatus getEpisodeStatus(int userId, int epId) {
        String sql = "SELECT status FROM episode_progress WHERE user_id=? AND ep_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, epId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dbStatus = rs.getString("status");
                if (dbStatus != null) return WatchStatus.valueOf(dbStatus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return WatchStatus.NOT_STARTED;
    }

}
