package JStream.dao;

import JStream.entity.Film;
import JStream.entity.Category;
import JStream.utils.Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FilmDAO {

    // ── CREATE ────────────────────────────────────────────────────
    public void save(Film film) {
        String sql = """
            INSERT INTO films (title, synopsis, casting, video_url, image_url,
                               categorie_id, release_date, duration)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, film.getTitle());
            ps.setString(2, film.getSynopsis());
            ps.setString(3, film.getCasting());
            ps.setString(4, film.getVideo_url());
            ps.setString(5, film.getImage_url());
            ps.setInt   (6, film.getCategory().getCategory_id());
            ps.setObject(7, film.getRelease_date());   // LocalDateTime → DATETIME
            ps.setDouble(8, film.getDuration());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) film.setFilm_id(keys.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ── READ — tous ───────────────────────────────────────────────
    public List<Film> findAll() {
        List<Film> list = new ArrayList<>();
        String sql = """
            SELECT f.*, c.id AS cat_id, c.name AS cat_name, c.description AS cat_desc
            FROM films f
            LEFT JOIN categories c ON f.categorie_id = c.id
            ORDER BY f.title
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── READ — par id ─────────────────────────────────────────────
    public Film findById(int id) {
        String sql = """
            SELECT f.*, c.id AS cat_id, c.name AS cat_name, c.description AS cat_desc
            FROM films f
            LEFT JOIN categories c ON f.categorie_id = c.id
            WHERE f.film_id = ?
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── READ — par catégorie ──────────────────────────────────────
    public List<Film> findByCategorie(int categorieId) {
        List<Film> list = new ArrayList<>();
        String sql = """
            SELECT f.*, c.id AS cat_id, c.name AS cat_name, c.description AS cat_desc
            FROM films f
            LEFT JOIN categories c ON f.categorie_id = c.id
            WHERE f.categorie_id = ?
            ORDER BY f.title
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categorieId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── READ — recherche par titre ────────────────────────────────
    public List<Film> search(String keyword) {
        List<Film> list = new ArrayList<>();
        String sql = """
            SELECT f.*, c.id AS cat_id, c.name AS cat_name, c.description AS cat_desc
            FROM films f
            LEFT JOIN categories c ON f.categorie_id = c.id
            WHERE f.title LIKE ?
            ORDER BY f.title
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────
    public void update(Film film) {
        String sql = """
            UPDATE films
            SET title = ?, synopsis = ?, casting = ?, video_url = ?, image_url = ?,
                categorie_id = ?, release_date = ?, duration = ?
            WHERE film_id = ?
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, film.getTitle());
            ps.setString(2, film.getSynopsis());
            ps.setString(3, film.getCasting());
            ps.setString(4, film.getVideo_url());
            ps.setString(5, film.getImage_url());
            ps.setInt   (6, film.getCategory().getCategory_id());
            ps.setObject(7, film.getRelease_date());
            ps.setDouble(8, film.getDuration());
            ps.setInt   (9, film.getFilm_id());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ── DELETE ────────────────────────────────────────────────────
    public void delete(int id) {
        String sql = "DELETE FROM films WHERE film_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ── Helper : ResultSet → objet Film ──────────────────────────
    private Film mapRow(ResultSet rs) throws SQLException {

        // Reconstruire l'objet Category depuis le JOIN
        Category category = new Category(
            rs.getInt("cat_id"),
            rs.getString("cat_name"),
            rs.getString("cat_desc"),
            null
        );

        // Convertir DATETIME → LocalDateTime
        LocalDateTime releaseDate = null;
        Timestamp ts = rs.getTimestamp("release_date");
        if (ts != null) releaseDate = ts.toLocalDateTime();

        return new Film(
            rs.getInt("film_id"),
            rs.getString("title"),
            rs.getString("synopsis"),
            rs.getString("casting"),
            rs.getString("video_url"),
            rs.getString("image_url"),
            category,
            releaseDate,
            rs.getTimestamp("updated_at"),
            rs.getDouble("duration")
        );
    }
}