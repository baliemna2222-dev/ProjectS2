package JStream.dao;

import JStream.entity.Rating;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class RatingDAO {

    // ===== UPSERT (one rating per user per content) =====
    public boolean upsert(Rating rating) {
        String checkSql = "SELECT rating_id FROM ratings WHERE user_id=? AND film_id=? AND serie_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {

            check.setInt(1, rating.getUserID());
            check.setInt(2, rating.getFilmID());
            check.setInt(3, rating.getSerieID());
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                String upd = "UPDATE ratings SET note=?, updated_at=NOW() WHERE rating_id=?";
                try (PreparedStatement ps = conn.prepareStatement(upd)) {
                    ps.setInt(1, rating.getNote());
                    ps.setInt(2, rs.getInt("rating_id"));
                    return ps.executeUpdate() > 0;
                }
            } else {
                String ins = "INSERT INTO ratings (user_id, film_id, serie_id, note) VALUES (?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, rating.getUserID());
                    ps.setInt(2, rating.getFilmID());
                    ps.setInt(3, rating.getSerieID());
                    ps.setInt(4, rating.getNote());
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        ResultSet gen = ps.getGeneratedKeys();
                        if (gen.next()) rating.setRating_id(gen.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== AVERAGE RATING FOR A FILM =====
    public double getAverageForFilm(int filmId) {
        String sql = "SELECT AVG(note) AS avg FROM ratings WHERE film_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, filmId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("avg");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // ===== AVERAGE RATING FOR A SERIE =====
    public double getAverageForSerie(int serieId) {
        String sql = "SELECT AVG(note) AS avg FROM ratings WHERE serie_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serieId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("avg");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // ===== GET USER'S RATING FOR A FILM =====
    public Rating getUserRatingForFilm(int userId, int filmId) {
        String sql = "SELECT * FROM ratings WHERE user_id=? AND film_id=?";
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

    private Rating mapRow(ResultSet rs) throws SQLException {
        return new Rating(
            rs.getInt("rating_id"),
            rs.getInt("user_id"),
            rs.getInt("film_id"),
            rs.getInt("serie_id"),
            rs.getInt("note"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}