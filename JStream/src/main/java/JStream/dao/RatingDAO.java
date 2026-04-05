package JStream.dao;

import JStream.entity.Rating;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RatingDAO {

    // ===== UPSERT RATING =====
    public boolean upsert(Rating rating) {
        String sql = "INSERT INTO ratings (user_id, film_id, serie_id, season_id, episode_id, note) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE note = VALUES(note), updated_at = NOW()";

        try (Connection conn = Database.getConnection()) {
            if (conn == null) return false;

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, rating.getUserID());
                ps.setObject(2, rating.getFilmID() > 0 ? rating.getFilmID() : null);
                ps.setObject(3, rating.getSerieID() > 0 ? rating.getSerieID() : null);
                ps.setObject(4, rating.getSeasonID() > 0 ? rating.getSeasonID() : null);
                ps.setObject(5, rating.getEpisodeID() > 0 ? rating.getEpisodeID() : null);
                ps.setInt(6, rating.getNote());

                int rows = ps.executeUpdate();
                
                // Optional: cascade update
                updateFilmAverage(conn, rating.getFilmID());
                updateEpisodeSeasonSerieCascade(conn, rating);

                conn.commit();
                conn.setAutoCommit(true);

                return rows > 0;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== CASCADE AVERAGES =====
    private void updateFilmAverage(Connection conn, int filmId) throws SQLException {
        if (filmId <= 0) return;
        String sql = "UPDATE film SET rating = ROUND((SELECT AVG(note) FROM ratings WHERE film_id=? AND episode_id=0)) WHERE film_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filmId);
            ps.setInt(2, filmId);
            ps.executeUpdate();
        }
    }

    private void updateEpisodeSeasonSerieCascade(Connection conn, Rating r) throws SQLException {
        if (r.getEpisodeID() > 0) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE episode SET rating = ROUND((SELECT AVG(note) FROM ratings WHERE episode_id=?)) WHERE ep_id=?")) {
                ps.setInt(1, r.getEpisodeID());
                ps.setInt(2, r.getEpisodeID());
                ps.executeUpdate();
            }
        }

        if (r.getSeasonID() > 0) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE season SET rating = ROUND((SELECT AVG(rating) FROM episode WHERE season_id=?)) WHERE season_id=?")) {
                ps.setInt(1, r.getSeasonID());
                ps.setInt(2, r.getSeasonID());
                ps.executeUpdate();
            }
        }

        if (r.getSerieID() > 0) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE serie SET rating = ROUND((SELECT AVG(rating) FROM season WHERE serie_id=?)) WHERE serie_id=?")) {
                ps.setInt(1, r.getSerieID());
                ps.setInt(2, r.getSerieID());
                ps.executeUpdate();
            }
        }
    }

    // ===== GET AVERAGES =====
    public double getAverageForFilm(int filmId) {
        return queryAvg("SELECT AVG(note) AS avg FROM ratings WHERE film_id=? AND episode_id=0", filmId);
    }

    public double getAverageForEpisode(int episodeId) {
        return queryAvg("SELECT AVG(note) AS avg FROM ratings WHERE episode_id=?", episodeId);
    }

    public double getAverageForSeason(int seasonId) {
        return queryAvg("SELECT AVG(note) AS avg FROM ratings WHERE season_id=? AND episode_id>0", seasonId);
    }

    public double getAverageForSerie(int serieId) {
        return queryAvg("SELECT AVG(note) AS avg FROM ratings WHERE serie_id=? AND episode_id>0", serieId);
    }

    // ===== GET USER RATING =====
    public Rating getUserRatingForFilm(int userId, int filmId) {
        return querySingle("SELECT * FROM ratings WHERE user_id=? AND film_id=? AND episode_id=0", userId, filmId);
    }

    public Rating getUserRatingForEpisode(int userId, int episodeId) {
        return querySingle("SELECT * FROM ratings WHERE user_id=? AND episode_id=?", userId, episodeId);
    }

    // ===== PRIVATE HELPERS =====
    private double queryAvg(String sql, int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double val = rs.getDouble("avg");
                    return rs.wasNull() ? 0.0 : val;
                }
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
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
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