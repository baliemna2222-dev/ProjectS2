package JStream.dao;

import JStream.entity.FeaturedItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class FilmDAO {

    private final Connection connection;

    public FilmDAO(Connection connection) {
        this.connection = connection;
    }

    public List<FeaturedItem> getLatestFeatured(int limit) throws SQLException {
        List<FeaturedItem> featured = new ArrayList<>();

        // --- Latest films with multiple categories ---
        String filmSql =
            "SELECT f.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
            "FROM film f " +
            "JOIN film_category fc ON f.film_id = fc.film_id " +
            "JOIN category c ON fc.category_id = c.category_id " +
            "GROUP BY f.film_id " +
            "ORDER BY f.release_date DESC LIMIT ?";
        try (PreparedStatement ps = connection.prepareStatement(filmSql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Convert comma-separated string to List<String>
                List<String> categories = List.of(rs.getString("categories").split(","));
                
                featured.add(new FeaturedItem(
                        rs.getInt("film_id"),
                        rs.getString("title"),
                        rs.getString("synopsis"),
                        rs.getString("video_url"),
                        rs.getString("image_url"),
                        rs.getString("title_image_url"),
                        rs.getString("poster_url"),
                        categories,                        // ✅ multiple categories
                        rs.getString("age_rating"),
                        rs.getInt("rating")
                ));
            }
        }

        // --- Latest ongoing seasons with multiple categories ---
        String seasonSql =
                "SELECT s.*, se.serie_id, se.title ,se.age_rating AS serie_title,age_rating, " +
                "       GROUP_CONCAT(c.name SEPARATOR ',') AS categories, " +
                "       COALESCE(MAX(e.num_episode), 0) AS last_episode " +
                "FROM season s " +
                "JOIN serie se ON s.serie_id = se.serie_id " +
                "JOIN serie_category sc ON se.serie_id = sc.serie_id " +
                "JOIN category c ON sc.category_id = c.category_id " +
                "LEFT JOIN episode e ON e.season_id = s.season_id " +
                "WHERE s.season_id = ( " +
                "    SELECT s2.season_id " +
                "    FROM season s2 " +
                "    LEFT JOIN episode e2 ON e2.season_id = s2.season_id " +
                "    WHERE s2.serie_id = s.serie_id " +
                "    GROUP BY s2.season_id " +
                "    ORDER BY MAX(e2.released_at) DESC " +
                "    LIMIT 1 " +
                ") " +
                "GROUP BY s.season_id " +
                "ORDER BY MAX(e.released_at) DESC " +
                "LIMIT ?";
        try (PreparedStatement ps = connection.prepareStatement(seasonSql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	 List<String> categories = new ArrayList<>(new LinkedHashSet<>(List.of(rs.getString("categories").split(","))));
            	   
                featured.add(new FeaturedItem(
                        rs.getInt("season_id"),
                        rs.getInt("serie_id"),
                        rs.getString("serie_title"),
                        rs.getString("synopsis"),
                        rs.getString("trailer_url"),
                        rs.getString("image_url"),
                        rs.getString("title_url"),
                        rs.getString("poster_url"),
                        categories,    
                        rs.getString("age_rating"),// ✅ multiple categories
                        rs.getInt("rating"),
                        rs.getString("status"),
                        rs.getInt("season_num"),
                        rs.getInt("last_episode")
                ));
            }
        }

        return featured;
    }
}