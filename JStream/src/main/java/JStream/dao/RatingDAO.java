package JStream.dao;

import JStream.entity.Rating;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RatingDAO {

   
	public boolean upsert(Rating rating) {
        // On utilise COALESCE ou on gère les 0 comme des NULL pour les IDs de séries/films
        String sql = "INSERT INTO ratings (user_id, film_id, serie_id, season_id, episode_id, note) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE note = VALUES(note), updated_at = NOW()";

        try (Connection conn = Database.getConnection()) {
            if (conn == null) return false;

            // Désactiver l'auto-commit pour gérer la transaction manuellement
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, rating.getUserID());
                
                // Gestion des nulls : si l'ID est 0, on met NULL dans la DB
                if (rating.getFilmID() > 0) ps.setInt(2, rating.getFilmID()); else ps.setNull(2, java.sql.Types.INTEGER);
                if (rating.getSerieID() > 0) ps.setInt(3, rating.getSerieID()); else ps.setNull(3, java.sql.Types.INTEGER);
                if (rating.getSeasonID() > 0) ps.setInt(4, rating.getSeasonID()); else ps.setNull(4, java.sql.Types.INTEGER);
                if (rating.getEpisodeID() > 0) ps.setInt(5, rating.getEpisodeID()); else ps.setNull(5, java.sql.Types.INTEGER);
                
                ps.setInt(6, rating.getNote());

                int rows = ps.executeUpdate();
                
                // --- LE POINT CRITIQUE ---
                conn.commit(); 
                // -------------------------
                
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback(); // Annule tout en cas d'erreur
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateFilmAverage(Connection conn, int filmId) throws SQLException {
        String sql = "UPDATE film SET rating = ROUND(" +
                     "  (SELECT AVG(note) FROM ratings WHERE film_id = ? AND episode_id = 0)" +
                     ") WHERE film_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filmId);
            ps.setInt(2, filmId);
            ps.executeUpdate();
        }
    }

    private void updateEpisodeSeasonSerieCascade(Connection conn, Rating r) throws SQLException {
        // 1. Episode
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE episode SET rating = ROUND(" +
                "  (SELECT AVG(note) FROM ratings WHERE episode_id = ?)" +
                ") WHERE ep_id = ?")) {
            ps.setInt(1, r.getEpisodeID());
            ps.setInt(2, r.getEpisodeID());
            ps.executeUpdate();
        }
        // 2. Season = AVG des épisodes
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE season SET rating = ROUND(" +
                "  (SELECT AVG(rating) FROM episode WHERE season_id = ?)" +
                ") WHERE season_id = ?")) {
            ps.setInt(1, r.getSeasonID());
            ps.setInt(2, r.getSeasonID());
            ps.executeUpdate();
        }
        // 3. Serie = AVG des saisons
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE serie SET rating = ROUND(" +
                "  (SELECT AVG(rating) FROM season WHERE serie_id = ?)" +
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

    public double getAverageForEpisode(int episodeId) {
        return queryAvg(
            "SELECT AVG(note) AS avg FROM ratings WHERE episode_id=?",
            episodeId);
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
            rs.getInt("note"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}