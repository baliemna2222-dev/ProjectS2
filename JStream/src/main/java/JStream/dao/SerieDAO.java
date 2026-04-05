package JStream.dao;

import JStream.entity.Category;
import JStream.entity.Serie;
import JStream.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SerieDAO {

    // ===== INSERT =====
    public boolean insertSerie(Serie serie) {
        String sql = "INSERT INTO serie (title, synopsis, casting, covert_url, title_url, age_rating) VALUES (?,?,?,?,?,?)";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // start transaction

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, serie.getTitle());
                ps.setString(2, serie.getSynopsis());
                ps.setString(3, serie.getCasting());
                ps.setString(4, serie.getCovertUrl());
                ps.setString(5, serie.getTitleUrl());
                ps.setString(6, serie.getAge_rating());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        serie.setSerieId(rs.getInt(1));
                        insertSerieCategories(conn, serie.getSerieId(), serie.getCategories());
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== UPDATE =====
    public boolean updateSerie(Serie serie) {
        String sql = "UPDATE serie SET title=?, synopsis=?, casting=?, covert_url=?, title_url=?, age_rating=? WHERE serie_id=?";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, serie.getTitle());
                ps.setString(2, serie.getSynopsis());
                ps.setString(3, serie.getCasting());
                ps.setString(4, serie.getCovertUrl());
                ps.setString(5, serie.getTitleUrl());
                ps.setString(6, serie.getAge_rating());
                ps.setInt(7, serie.getSerieId());

                boolean updated = ps.executeUpdate() > 0;

                if (updated && serie.getCategories() != null) {
                    deleteSerieCategories(conn, serie.getSerieId());
                    insertSerieCategories(conn, serie.getSerieId(), serie.getCategories());
                }

                conn.commit();
                return updated;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== DELETE =====
    public boolean deleteSerie(int serieId) {
        String sql = "DELETE FROM serie WHERE serie_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serieId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== GET ALL =====
    public List<Serie> getAllSeries() {
        List<Serie> list = new ArrayList<>();
        String sql = "SELECT s.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
                "FROM serie s " +
                "LEFT JOIN serie_category sc ON s.serie_id = sc.serie_id " +
                "LEFT JOIN category c ON sc.category_id = c.category_id " +
                "GROUP BY s.serie_id ORDER BY s.created_at DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== GET BY ID =====
    public Serie getSerieById(int serieId) {
        String sql = "SELECT s.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
                "FROM serie s " +
                "LEFT JOIN serie_category sc ON s.serie_id = sc.serie_id " +
                "LEFT JOIN category c ON sc.category_id = c.category_id " +
                "WHERE s.serie_id = ? GROUP BY s.serie_id";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serieId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== SEARCH =====
    public List<Serie> searchSeries(String keyword) {
        List<Serie> list = new ArrayList<>();
        String sql = "SELECT DISTINCT s.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
                "FROM serie s " +
                "LEFT JOIN serie_category sc ON s.serie_id = sc.serie_id " +
                "LEFT JOIN category c ON sc.category_id = c.category_id " +
                "WHERE s.title LIKE ? OR c.name LIKE ? " +
                "GROUP BY s.serie_id ORDER BY s.title";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== HELPERS =====
    private void insertSerieCategories(Connection conn, int serieId, List<Category> categories) throws SQLException {
        if (categories == null || categories.isEmpty()) return;

        String sql = "INSERT IGNORE INTO serie_category (serie_id, category_id) VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Category c : categories) {
                ps.setInt(1, serieId);
                ps.setInt(2, c.getCategory_id());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteSerieCategories(Connection conn, int serieId) throws SQLException {
        String sql = "DELETE FROM serie_category WHERE serie_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serieId);
            ps.executeUpdate();
        }
    }

    private Serie mapRow(ResultSet rs) throws SQLException {
        Serie serie = new Serie();
        serie.setSerieId(rs.getInt("serie_id"));
        serie.setTitle(rs.getString("title"));
        serie.setSynopsis(rs.getString("synopsis"));
        serie.setCasting(rs.getString("casting"));
        serie.setCovertUrl(rs.getString("covert_url"));
        serie.setTitleUrl(rs.getString("title_url"));
        serie.setAge_rating(rs.getString("age_rating"));
        serie.setRating(rs.getInt("rating"));
        serie.setCreatedAt(rs.getTimestamp("created_at"));
        serie.setUpdatedAt(rs.getTimestamp("updated_at"));

        List<Category> cats = new ArrayList<>();
        String catStr = rs.getString("categories");
        if (catStr != null) {
            for (String name : catStr.split(",")) {
                Category c = new Category();
                c.setName(name.trim());
                cats.add(c);
            }
        }
        serie.setCategories(cats);
        return serie;
    }
}