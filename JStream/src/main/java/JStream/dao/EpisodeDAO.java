package JStream.dao;

import JStream.entity.Episode;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EpisodeDAO {

    // ===== INSERT =====
    public boolean insertEpisode(Episode episode) {
        String sql = "INSERT INTO episode (season_id, num_episode, title, duration, resume, " +
                     "video_url, covert_url, released_at) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1,    episode.getSeasonId());
            ps.setInt(2,    episode.getNumEpisode());
            ps.setString(3, episode.getTitle());
            ps.setInt(4,    episode.getDuration()); 
            ps.setString(5, episode.getResume());
            ps.setString(6, episode.getVideoUrl());
            ps.setString(7, episode.getCovertUrl());
            ps.setTimestamp(8, episode.getReleasedAt());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) episode.setEpId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== UPDATE =====
    public boolean updateEpisode(Episode episode) {
        String sql = "UPDATE episode SET title=?, duration=?, resume=?, video_url=?, " +
                     "covert_url=?, released_at=? WHERE ep_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, episode.getTitle());
            ps.setInt(2,    episode.getDuration());
            ps.setString(3, episode.getResume());
            ps.setString(4, episode.getVideoUrl());
            ps.setString(5, episode.getCovertUrl());
            ps.setTimestamp(6, episode.getReleasedAt());
            ps.setInt(7,    episode.getEpId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== DELETE =====
    public boolean deleteEpisode(int epId) {
        String sql = "DELETE FROM episode WHERE ep_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, epId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== GET ALL EPISODES OF A SEASON =====
    public List<Episode> getEpisodesBySeason(int seasonId) {
        List<Episode> list = new ArrayList<>();
        String sql = "SELECT * FROM episode WHERE season_id = ? ORDER BY num_episode";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, seasonId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== GET BY ID =====
    public Episode getEpisodeById(int epId) {
        String sql = "SELECT * FROM episode WHERE ep_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, epId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== NEXT EPISODE (for binge-watching auto-play) =====
    public Episode getNextEpisode(int seasonId, int currentNumEpisode) {
        System.out.println("Recherche épisode après : S" + seasonId + " E" + currentNumEpisode);
        String sql = "SELECT * FROM episode " +
                     "WHERE season_id = ? AND num_episode > ? " +
                     "ORDER BY num_episode ASC LIMIT 1";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, seasonId);
            ps.setInt(2, currentNumEpisode);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Episode ep = mapRow(rs);
                System.out.println("Épisode trouvé : " + ep.getTitle());
                return ep;
            } else {
                System.out.println("Aucun épisode suivant trouvé dans cette saison.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Episode mapRow(ResultSet rs) throws SQLException {
        Episode ep = new Episode();
        ep.setEpId(rs.getInt("ep_id"));
        ep.setSeasonId(rs.getInt("season_id"));
        ep.setNumEpisode(rs.getInt("num_episode"));
        ep.setTitle(rs.getString("title"));
        ep.setDuration(rs.getInt("duration"));
        ep.setResume(rs.getString("resume"));
        ep.setVideoUrl(rs.getString("video_url"));
        ep.setCovertUrl(rs.getString("covert_url"));
        ep.setRating(rs.getInt("rating"));
        ep.setCreatedAt(rs.getTimestamp("created_at"));
        ep.setReleasedAt(rs.getTimestamp("released_at"));
        return ep;
    }
}