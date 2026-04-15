package JStream.dao;

import JStream.entity.Rating;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingDAO {

   
	public boolean upsert(Rating rating) {
	    String sql = "INSERT INTO ratings (user_id, film_id, serie_id, season_id, episode_id, note) " +
	                 "VALUES (?, ?, ?, ?, ?, ?) " +
	                 "ON DUPLICATE KEY UPDATE note = VALUES(note), updated_at = NOW()";

	    try (Connection conn = Database.getConnection()) {
	        if (conn == null) return false;
	        conn.setAutoCommit(false);

	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setInt(1, rating.getUserID());
	            ps.setInt(2, rating.getFilmID());    // 0 if not a film
	            ps.setInt(3, rating.getSerieID());   // 0 if not a serie
	            ps.setInt(4, rating.getSeasonID());  // 0 if not a season
	            ps.setInt(5, rating.getEpisodeID()); // 0 if not an episode
	            ps.setDouble(6, rating.getNote());

	            System.out.println("🔍 Inserting rating: user=" + rating.getUserID()
	                + " film=" + rating.getFilmID()
	                + " serie=" + rating.getSerieID()
	                + " season=" + rating.getSeasonID()
	                + " episode=" + rating.getEpisodeID()
	                + " note=" + rating.getNote());

	            int rows = ps.executeUpdate();
	            System.out.println("🔍 Rows affected: " + rows);

	            if (rows > 0) {
	                if (rating.getFilmID() > 0) {
	                    updateFilmAverage(conn, rating.getFilmID());
	                } else if (rating.getEpisodeID() > 0) {
	                    updateEpisodeSeasonSerieCascade(conn, rating);
	                }
	            }

	            conn.commit();
	            return rows > 0;

	        } catch (SQLException e) {
	            conn.rollback();
	            System.err.println("❌ SQL Error code: " + e.getErrorCode());
	            System.err.println("❌ SQL State: " + e.getSQLState());
	            System.err.println("❌ Message: " + e.getMessage());
	            e.printStackTrace();
	            return false;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	private void updateFilmAverage(Connection conn, int filmId) throws SQLException {
	    String sql = "UPDATE film SET rating = " +
	                 "  (SELECT AVG(note) FROM ratings WHERE film_id=? AND episode_id=0)" +
	                 " WHERE film_id = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, filmId);
	        ps.setInt(2, filmId);
	        ps.executeUpdate();
	    }
	}
	private void updateEpisodeSeasonSerieCascade(Connection conn, Rating r) throws SQLException {

	    // 1. Episode : Précision totale
	    try (PreparedStatement ps = conn.prepareStatement(
	            "UPDATE episode SET rating = (" +
	            "  SELECT AVG(note) FROM ratings WHERE episode_id = ?" +
	            ") WHERE ep_id = ?")) {
	        ps.setInt(1, r.getEpisodeID());
	        ps.setInt(2, r.getEpisodeID());
	        ps.executeUpdate();
	    }

	    // 2. Season : Précision totale
	    try (PreparedStatement ps = conn.prepareStatement(
	            "UPDATE season SET rating = (" +
	            "  SELECT AVG(note) FROM ratings WHERE episode_id IN (" +
	            "    SELECT ep_id FROM (SELECT ep_id FROM episode WHERE season_id = ?) AS eps" +
	            "  ) AND episode_id > 0" +
	            ") WHERE season_id = ?")) {
	        ps.setInt(1, r.getSeasonID());
	        ps.setInt(2, r.getSeasonID());
	        ps.executeUpdate();
	    }

	    // 3. Serie : Précision totale
	    try (PreparedStatement ps = conn.prepareStatement(
	            "UPDATE serie SET rating = (" +
	            "  SELECT AVG(note) FROM ratings WHERE episode_id IN (" +
	            "    SELECT ep_id FROM (" +
	            "      SELECT e.ep_id FROM episode e " +
	            "      JOIN season s ON e.season_id = s.season_id " +
	            "      WHERE s.serie_id = ?" +
	            "    ) AS eps" +
	            "  ) AND episode_id > 0" +
	            ") WHERE serie_id = ?")) {
	        ps.setInt(1, r.getSerieID());
	        ps.setInt(2, r.getSerieID());
	        ps.executeUpdate();
	    }
	}
    // ── Moyennes ─────────────────────────────────────────────────────────────
    public double getAverageForFilm(int filmId) {
        return queryAvg(
            "SELECT AVG(note) AS avg FROM ratings WHERE film_id=? AND episode_id=0",
            filmId);
    }

    public double getAverageForSeason(int seasonId) {
        return queryAvg(
            "SELECT AVG(note) AS avg FROM ratings WHERE season_id=? AND episode_id > 0",
            seasonId);
    }

    public double getAverageForSerie(int serieId) {
        return queryAvg(
            "SELECT AVG(note) AS avg FROM ratings WHERE serie_id=? AND episode_id > 0",
            serieId);
    }

    public Rating getUserRatingForFilm(int userId, int filmId) {
        return querySingle(
            "SELECT * FROM ratings WHERE user_id=? AND film_id=? AND episode_id=0",
            userId, filmId);
    }
 

    public double getAverageForEpisode(int episodeId) {
        return queryAvg(
            "SELECT AVG(note) AS avg FROM ratings WHERE episode_id=?",
            episodeId);
    }


    public Rating getUserRatingForEpisode(int userId, int episodeId) {
        return querySingle(
            "SELECT * FROM ratings WHERE user_id=? AND episode_id=?",
            userId, episodeId);
    }

    // ── Helpers privés ────────────────────────────────────────────────────────

    private double queryAvg(String sql, int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double v = rs.getDouble("avg");
                // getDouble retourne 0.0 si NULL — on vérifie wasNull
                if (rs.wasNull()) return 0.0;
                return v;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private Rating querySingle(String sql, int p1, int p2) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p1);
            ps.setInt(2, p2);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Rating mapRow(ResultSet rs) throws SQLException {
        return new Rating(
            rs.getInt("rating_id"),
            rs.getInt("user_id"),
            rs.getInt("film_id"),
            rs.getInt("serie_id"),
            rs.getInt("episode_id"),
            rs.getInt("season_id"),
            rs.getDouble("note"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}