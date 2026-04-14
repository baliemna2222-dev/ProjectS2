package JStream.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import JStream.utils.Database;

public class DashboardDAO {

    public int countUsers() {
        return count("users");
    }

    public int countFilms() {
        return count("film");
    }

    public int countSeries() {
        return count("serie");
    }

    public int countComments() {
        return count("comments");
    }

    public int countWatchHistory() {
        int total = 0;
        String sql = "SELECT (SELECT COUNT(*) FROM film_progress) + (SELECT COUNT(*) FROM episode_progress)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    public int countDistinctWatchUsers() {
        int total = 0;
        String sql = "SELECT COUNT(DISTINCT user_id) FROM (" +
                     "SELECT user_id FROM film_progress " +
                     "UNION " +
                     "SELECT user_id FROM episode_progress" +
                     ") AS users";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    public java.util.List<JStream.entity.ViewStat> getTopWatchedFilms(int limit) {
        java.util.List<JStream.entity.ViewStat> topFilms = new java.util.ArrayList<>();
        String sql = "SELECT f.title, COUNT(fp.film_id) AS views " +
                     "FROM film_progress fp " +
                     "JOIN film f ON fp.film_id = f.film_id " +
                     "GROUP BY f.title " +
                     "ORDER BY views DESC " +
                     "LIMIT ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                topFilms.add(new JStream.entity.ViewStat(rs.getString("title"), rs.getInt("views")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return topFilms;
    }

    public java.util.List<JStream.entity.ViewStat> getFilmCategoryDistribution() {
        java.util.List<JStream.entity.ViewStat> categories = new java.util.ArrayList<>();
        String sql = "SELECT c.name, COUNT(fc.film_id) AS count " +
                     "FROM film_category fc " +
                     "JOIN category c ON fc.category_id = c.category_id " +
                     "GROUP BY c.name";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(new JStream.entity.ViewStat(rs.getString("name"), rs.getInt("count")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    public java.util.List<JStream.entity.ViewStat> getUserSignupsByDay(int days) {
        java.util.List<JStream.entity.ViewStat> signups = new java.util.ArrayList<>();
        String sql = "SELECT DATE(created_at) AS day, COUNT(*) AS count " +
                     "FROM users " +
                     "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                     "GROUP BY DATE(created_at) " +
                     "ORDER BY DATE(created_at)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, days);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                signups.add(new JStream.entity.ViewStat(rs.getString("day"), rs.getInt("count")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signups;
    }

    public java.util.List<JStream.entity.ViewStat> getTopWatchedSeries(int limit) {
        java.util.List<JStream.entity.ViewStat> topSeries = new java.util.ArrayList<>();
        String sql = "SELECT s.title, COUNT(ep.ep_id) AS views " +
                "FROM episode_progress ep " +
                "JOIN episode e ON ep.ep_id = e.ep_id " +
                "JOIN season se ON e.season_id = se.season_id " +
                "JOIN serie s ON se.serie_id = s.serie_id " +
                "GROUP BY s.title " +
                "ORDER BY views DESC " +
                "LIMIT ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                topSeries.add(new JStream.entity.ViewStat(rs.getString("title"), rs.getInt("views")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return topSeries;
    }

    private int count(String table) {
        int total = 0;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement("SELECT COUNT(*) FROM " + table)) {

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }
}