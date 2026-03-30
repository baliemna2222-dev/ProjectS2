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

    // ===== (insert or update progress) =====
    public boolean upsert(History history) {
        String checkSql = "SELECT id FROM history WHERE user_id=? AND film_id=? AND episode_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {

            check.setInt(1, history.getUserID());
            check.setInt(2, history.getFilmID());
            check.setInt(3, history.getEpisodeID());
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                String upd = "UPDATE history SET progression_secondes=?, completed=?, updated_at=NOW() WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(upd)) {
                    ps.setInt(1, history.getProgressionSecondes());
                    ps.setBoolean(2, history.isCompleted());
                    ps.setInt(3, rs.getInt("id"));
                    return ps.executeUpdate() > 0;
                }
            } else {
                String ins = "INSERT INTO history (user_id, film_id, episode_id, progression_secondes, completed) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, history.getUserID());
                    ps.setInt(2, history.getFilmID());
                    ps.setInt(3, history.getEpisodeID());
                    ps.setInt(4, history.getProgressionSecondes());
                    ps.setBoolean(5, history.isCompleted());
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        ResultSet gen = ps.getGeneratedKeys();
                        if (gen.next()) history.setId(gen.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== GET ALL HISTORY FOR A USER =====
    public List<History> getHistoryByUser(int userId) {
        List<History> list = new ArrayList<>();
        String sql = "SELECT * FROM history WHERE user_id = ? ORDER BY updated_at DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== GET PROGRESS FOR A SPECIFIC FILM =====
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

    // ===== GET PROGRESS FOR A SPECIFIC EPISODE =====
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

    // ===== FIRST UNWATCHED EPISODE IN A SEASON (smart resume) =====
    public int getFirstUnwatchedEpisodeId(int userId, int seasonId) {
        String sql = "SELECT e.ep_id FROM episode e " +
                     "LEFT JOIN history h ON h.episode_id = e.ep_id AND h.user_id = ? AND h.completed = TRUE " +
                     "WHERE e.season_id = ? AND h.id IS NULL " +
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
        return -1;
    }

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