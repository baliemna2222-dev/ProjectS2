package JStream.dao;

import JStream.entity.WatchStatus;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilmProgressDAO {

    // ----------------- Get all watched film IDs -----------------
    public List<Integer> getWatchedFilmIds(int userId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT film_id FROM film_progress WHERE user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("film_id"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ----------------- Get film watch status -----------------
    public WatchStatus getFilmStatus(int userId, int filmId) {
        String sql = "SELECT watch_status FROM film_progress WHERE user_id = ? AND film_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("watch_status");
                    if (status != null) return WatchStatus.valueOf(status);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return WatchStatus.NOT_STARTED;
    }

    // ----------------- Get last watched position -----------------
    public int getLastPosition(int userId, int filmId) {
        String sql = "SELECT last_position FROM film_progress WHERE user_id = ? AND film_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("last_position");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ----------------- Check if film progress exists -----------------
    public boolean exists(int userId, int filmId) {
        String sql = "SELECT 1 FROM film_progress WHERE user_id = ? AND film_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ----------------- Mark film in progress (upsert) -----------------
    public void setInProgress(int userId, int filmId, int lastPosition) {
        String sql = "INSERT INTO film_progress (user_id, film_id, last_position, watch_status) " +
                     "VALUES (?, ?, ?, 'IN_PROGRESS') " +
                     "ON DUPLICATE KEY UPDATE last_position = ?, watch_status = 'IN_PROGRESS'";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            ps.setInt(3, lastPosition);
            ps.setInt(4, lastPosition);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Mark film completed -----------------
    public void setCompleted(int userId, int filmId, int lastPosition) {
        String sql = "UPDATE film_progress SET watch_status = 'COMPLETED', last_position = ? " +
                     "WHERE user_id = ? AND film_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lastPosition);
            ps.setInt(2, userId);
            ps.setInt(3, filmId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                // Insert if the record does not exist yet
                setInProgress(userId, filmId, lastPosition);
                // Then mark as completed
                setCompleted(userId, filmId, lastPosition);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}