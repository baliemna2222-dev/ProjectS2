package JStream.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import JStream.entity.WatchStatus;

public class FilmProgressDAO {

    private final Connection connection;

    public FilmProgressDAO(Connection connection) {
        this.connection = connection;
    }
    public List<Integer> getWatchedFilmIds(int userId) {
        List<Integer> list = new ArrayList<>();

        String sql = "SELECT film_id FROM film_progress WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getInt("film_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    // ----------------- Get film status -----------------
    public WatchStatus getFilmStatus(int userId, int filmId) {
        String sql = "SELECT watch_status FROM film_progress WHERE user_id=? AND film_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dbStatus = rs.getString("watch_status");
                if (dbStatus != null) return WatchStatus.valueOf(dbStatus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return WatchStatus.NOT_STARTED;
    }

    // ----------------- Mark film in progress -----------------
    public void setInProgress(int userId, int filmId, int lastPosition) {
        String sql = "INSERT INTO film_progress (user_id, film_id, last_position, watch_status) " +
                     "VALUES (?, ?, ?, 'IN_PROGRESS') " +
                     "ON DUPLICATE KEY UPDATE last_position=?, watch_status='IN_PROGRESS'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        // Thabbet elli watch_status maktouba s7i7a kima f-el Database mte3ek
        String sql = "UPDATE film_progress SET watch_status = 'COMPLETED', last_position = ? " +
                     "WHERE user_id = ? AND film_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, lastPosition);
            ps.setInt(2, userId);
            ps.setInt(3, filmId);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows updated: " + rowsAffected); // Ken tal9a 0, m3netha el WHERE ghalta
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //----------------------------------------------------
    public int getLastPosition(int userId, int filmId) throws SQLException {
        String sql = "SELECT last_position FROM film_progress WHERE user_id = ? AND film_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("last_position");
            }
        } 
        return 0; // Ken ma l9ach chay, yabda mel sfer
    }
    public boolean exists(int userId, int filmId) throws SQLException {
        String sql = "SELECT 1 FROM film_progress WHERE user_id = ? AND film_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true if found
        }
    }
}