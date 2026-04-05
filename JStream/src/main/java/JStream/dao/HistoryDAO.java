package JStream.dao;

import JStream.entity.History;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {

    // ===== INSERT OR UPDATE PROGRESS =====
    public boolean upsert(History history) {
        String selectSql = "SELECT id FROM history WHERE user_id=? AND film_id=? AND episode_id=?";
        String updateSql = "UPDATE history SET progression_secondes=?, completed=?, updated_at=NOW() WHERE id=?";
        String insertSql = "INSERT INTO history (user_id, film_id, episode_id, progression_secondes, completed) VALUES (?,?,?,?,?)";

        try (Connection conn = Database.getConnection()) {
            if (conn == null) return false;

            // Check if history exists
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, history.getUserID());
                ps.setInt(2, history.getFilmID());
                ps.setInt(3, history.getEpisodeID());
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    // UPDATE
                    try (PreparedStatement updPs = conn.prepareStatement(updateSql)) {
                        updPs.setInt(1, history.getProgressionSecondes());
                        updPs.setBoolean(2, history.isCompleted());
                        updPs.setInt(3, rs.getInt("id"));
                        return updPs.executeUpdate() > 0;
                    }
                } else {
                    // INSERT
                    try (PreparedStatement insPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                        insPs.setInt(1, history.getUserID());
                        insPs.setInt(2, history.getFilmID());
                        insPs.setInt(3, history.getEpisodeID());
                        insPs.setInt(4, history.getProgressionSecondes());
                        insPs.setBoolean(5, history.isCompleted());
                        int rows = insPs.executeUpdate();

                        if (rows > 0) {
                            ResultSet gen = insPs.getGeneratedKeys();
                            if (gen.next()) history.setId(gen.getInt(1));
                            return true;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ===== GET FULL HISTORY FOR USER =====
    public List<History> getHistoryByUser(int userId) {
        List<History> list = new ArrayList<>();
        String sql = "SELECT * FROM history WHERE user_id=? ORDER BY updated_at DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===== GET PROGRESS FOR A FILM =====
    public History getProgressByFilm(int userId, int filmId) {
        String sql = "SELECT * FROM history WHERE user_id=? AND film_id=? AND episode_id=0";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ===== GET PROGRESS FOR AN EPISODE =====
    public History getProgressByEpisode(int userId, int episodeId) {
        String sql = "SELECT * FROM history WHERE user_id=? AND episode_id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, episodeId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ===== GET FIRST UNWATCHED EPISODE IN A SEASON =====
    public int getFirstUnwatchedEpisodeId(int userId, int seasonId) {
        String sql = "SELECT e.ep_id FROM episode e " +
                     "LEFT JOIN history h ON h.episode_id = e.ep_id AND h.user_id=? AND h.completed=TRUE " +
                     "WHERE e.season_id=? AND h.id IS NULL " +
                     "ORDER BY e.num_episode LIMIT 1";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, seasonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("ep_id");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Not found
    }

    // ===== MAP ROW TO HISTORY ENTITY =====
    private History mapRow(ResultSet rs) throws SQLException {
        return new History(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getInt("film_id"),
            rs.getInt("episode_id"),
            rs.getInt("progression_secondes"),
            rs.getBoolean("completed"),
            rs.getTimestamp("watched_at"),
            rs.getTimestamp("updated_at")
        );
    }
}