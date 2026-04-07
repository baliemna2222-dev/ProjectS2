package JStream.dao;

import JStream.entity.Category;
import JStream.entity.Film;
import JStream.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmDAO {

    // ===== INSERT (Zidna trailer_url w posterV_url) =====
    public boolean insertFilm(Film film) {
        String sql = "INSERT INTO film (title, synopsis, casting, trailer_url, video_url, image_url, " +
                     "title_image_url, poster_url, posterV_url, release_date, duration, age_rating, rating) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, film.getTitle());
            ps.setString(2, film.getSynopsis());
            ps.setString(3, film.getCasting());
            ps.setString(4, film.getTrailer_url()); 
            ps.setString(5, film.getVideo_url());
            ps.setString(6, film.getImage_url());
            ps.setString(7, film.getTitle_image_url());
            ps.setString(8, film.getPoster_url());
            ps.setString(9, film.getPosterV_url()); 
            ps.setObject(10, film.getRelease_date());
            ps.setDouble(11, film.getDuration());
            ps.setString(12, film.getAge_rating());
            ps.setInt(13, film.getRating());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    film.setFilm_id(rs.getInt(1));
                    insertFilmCategories(conn, film.getFilm_id(), film.getCategories());
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== UPDATE (Zidna el jdod) =====
    public boolean updateFilm(Film film) {
        String sql = "UPDATE film SET title=?, synopsis=?, casting=?, trailer_url=?, video_url=?, image_url=?, " +
                     "title_image_url=?, poster_url=?, posterV_url=?, release_date=?, duration=?, age_rating=?, rating=? " +
                     "WHERE film_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1,  film.getTitle());
            ps.setString(2,  film.getSynopsis());
            ps.setString(3,  film.getCasting());
            ps.setString(4,  film.getTrailer_url());
            ps.setString(5,  film.getVideo_url());
            ps.setString(6,  film.getImage_url());
            ps.setString(7,  film.getTitle_image_url());
            ps.setString(8,  film.getPoster_url());
            ps.setString(9,  film.getPosterV_url());
            ps.setObject(10, film.getRelease_date());
            ps.setDouble(11, film.getDuration());
            ps.setString(12, film.getAge_rating());
            ps.setInt(13,    film.getRating());
            ps.setInt(14,    film.getFilm_id());

            boolean updated = ps.executeUpdate() > 0;
            if (updated && film.getCategories() != null) {
                deleteFilmCategories(conn, film.getFilm_id());
                insertFilmCategories(conn, film.getFilm_id(), film.getCategories());
            }
            return updated;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== DELETE (Ma yetbaddelch) =====
    public boolean deleteFilm(int filmId) {
        String sql = "DELETE FROM film WHERE film_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filmId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== GET ALL (Badelna f-el mapRow) =====
    public List<Film> getAllFilms() {
        List<Film> list = new ArrayList<>();
        String sql = "SELECT f.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
                     "FROM film f " +
                     "LEFT JOIN film_category fc ON f.film_id = fc.film_id " +
                     "LEFT JOIN category c ON fc.category_id = c.category_id " +
                     "GROUP BY f.film_id ORDER BY f.release_date DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== GET BY ID =====
    public Film getFilmById(int filmId) {
        String sql = "SELECT f.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
                     "FROM film f " +
                     "LEFT JOIN film_category fc ON f.film_id = fc.film_id " +
                     "LEFT JOIN category c ON fc.category_id = c.category_id " +
                     "WHERE f.film_id = ? GROUP BY f.film_id";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filmId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== SEARCH (title, category name, or year) =====
    public List<Film> searchFilms(String keyword) {
        List<Film> list = new ArrayList<>();
        String sql = "SELECT DISTINCT f.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
                     "FROM film f " +
                     "LEFT JOIN film_category fc ON f.film_id = fc.film_id " +
                     "LEFT JOIN category c ON fc.category_id = c.category_id " +
                     "WHERE f.title LIKE ? OR c.name LIKE ? OR YEAR(f.release_date) LIKE ? " +
                     "GROUP BY f.film_id ORDER BY f.title";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== HELPERS (Baddelna el mapping hna) =====
    private void insertFilmCategories(Connection conn, int filmId, List<Category> categories) throws SQLException {
        if (categories == null || categories.isEmpty()) return;
        String sql = "INSERT IGNORE INTO film_category (film_id, category_id) VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Category c : categories) {
                ps.setInt(1, filmId);
                ps.setInt(2, c.getCategory_id());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteFilmCategories(Connection conn, int filmId) throws SQLException {
        String sql = "DELETE FROM film_category WHERE film_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filmId);
            ps.executeUpdate();
        }
    }

    private Film mapRow(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setFilm_id(rs.getInt("film_id"));
        film.setTitle(rs.getString("title"));
        film.setSynopsis(rs.getString("synopsis"));
        film.setCasting(rs.getString("casting"));
        
        film.setTrailer_url(rs.getString("trailer_url")); 
        film.setPosterV_url(rs.getString("posterV_url"));

        film.setVideo_url(rs.getString("video_url"));
        film.setImage_url(rs.getString("image_url"));
        film.setTitle_image_url(rs.getString("title_image_url"));
        film.setPoster_url(rs.getString("poster_url"));
        film.setDuration(rs.getDouble("duration"));
        film.setAge_rating(rs.getString("age_rating"));
        film.setRating(rs.getInt("rating"));
        film.setUpdated_at(rs.getTimestamp("updated_at"));
        
        if (rs.getTimestamp("release_date") != null)
            film.setRelease_date(rs.getTimestamp("release_date").toLocalDateTime());

        List<Category> cats = new ArrayList<>();
        String catStr = rs.getString("categories");
        if (catStr != null && !catStr.isEmpty()) {
            for (String name : catStr.split(",")) {
                Category c = new Category();
                c.setName(name.trim());
                cats.add(c);
            }
        }
        film.setCategories(cats);
        return film;
    }
}