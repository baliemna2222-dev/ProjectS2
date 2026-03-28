package JStream.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import JStream.entity.FeaturedItem;

public class MylistDAO {

		private Connection conn;
	    public MylistDAO(Connection conn) {
	        this.conn = conn;
	    }

	    public boolean addToList(int userId, int filmId, int serieId) {

	        String check = "SELECT * FROM my_list WHERE user_id=? AND film_id=? AND serie_id=?";
	        String insert = "INSERT INTO my_list(user_id, film_id, serie_id) VALUES(?,?,?)";

	        try (PreparedStatement ps = conn.prepareStatement(check)) {

	            ps.setInt(1, userId);
	            ps.setInt(2, filmId);
	            ps.setInt(3, serieId);

	            ResultSet rs = ps.executeQuery();

	            if (rs.next()) return false; // already exists

	            try (PreparedStatement insertPs = conn.prepareStatement(insert)) {
	                insertPs.setInt(1, userId);
	                insertPs.setInt(2, filmId);
	                insertPs.setInt(3, serieId);
	                insertPs.executeUpdate();
	                return true;
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	        return false;
	    }
	    public boolean isInList(int userId, int filmId, int serieId) {
	        String sql = "SELECT COUNT(*) FROM my_list WHERE user_id = ? AND film_id = ? AND serie_id = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, userId);
	            stmt.setInt(2, filmId);
	            stmt.setInt(3, serieId);
	            ResultSet rs = stmt.executeQuery();
	            if(rs.next()) {
	                return rs.getInt(1) > 0;
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return false;
	    }
	  
	    public boolean removeItem(int userId, int filmId, int serieId) {
	        String sql = "DELETE FROM my_list WHERE user_id = ? AND film_id = ? AND serie_id = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, userId);
	            stmt.setInt(2, filmId);
	            stmt.setInt(3, serieId);
	            int affected = stmt.executeUpdate();
	            return affected > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return false;
	    }
	    public List<FeaturedItem> getItemsByUser(int userId) {
	        List<FeaturedItem> items = new ArrayList<>();

	        try {
	            // --- Films in my list ---
	            String filmSql = "SELECT f.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
	                             "FROM my_list ml " +
	                             "JOIN film f ON ml.film_id = f.film_id " +
	                             "LEFT JOIN film_category fc ON f.film_id = fc.film_id " +
	                             "LEFT JOIN category c ON fc.category_id = c.category_id " +
	                             "WHERE ml.user_id = ? AND ml.film_id != 0 " +
	                             "GROUP BY f.film_id";
	            try (PreparedStatement ps = conn.prepareStatement(filmSql)) {
	                ps.setInt(1, userId);
	                ResultSet rs = ps.executeQuery();
	                while (rs.next()) {
	                    List<String> categories = List.of(rs.getString("categories").split(","));
	                    items.add(new FeaturedItem(
	                        rs.getInt("film_id"),
	                        rs.getString("title"),
	                        rs.getString("synopsis"),
	                        rs.getString("video_url"),
	                        rs.getString("image_url"),
	                        rs.getString("title_image_url"),
	                        rs.getString("poster_url"),
	                        categories,
	                        rs.getString("age_rating"),
	                        rs.getInt("rating")
	                    ));
	                }
	            }

	            // --- Series in my list (latest season only) ---
	            String seriesSql = "SELECT s.season_id, s.poster_url, s.trailer_url, s.season_num, s.status, " +
	                               "se.serie_id, se.title AS serie_title, se.synopsis, se.title_url, se.covert_url, se.age_rating, se.rating, " +
	                               "GROUP_CONCAT(DISTINCT c.name SEPARATOR ',') AS categories, " +
	                               "COALESCE(MAX(e.num_episode),0) AS last_episode " +
	                               "FROM my_list ml " +
	                               "JOIN season s ON ml.serie_id = s.serie_id " +
	                               "JOIN serie se ON s.serie_id = se.serie_id " +
	                               "JOIN serie_category sc ON se.serie_id = sc.serie_id " +
	                               "JOIN category c ON sc.category_id = c.category_id " +
	                               "LEFT JOIN episode e ON e.season_id = s.season_id " +
	                               "WHERE ml.user_id = ? AND ml.serie_id != 0 " +
	                               "AND s.season_id = ( " +
	                               "   SELECT s2.season_id FROM season s2 " +
	                               "   LEFT JOIN episode e2 ON e2.season_id = s2.season_id " +
	                               "   WHERE s2.serie_id = s.serie_id " +
	                               "   GROUP BY s2.season_id ORDER BY MAX(e2.released_at) DESC LIMIT 1" +
	                               ") " +
	                               "GROUP BY s.season_id ORDER BY se.rating DESC";

	            try (PreparedStatement ps = conn.prepareStatement(seriesSql)) {
	                ps.setInt(1, userId);
	                ResultSet rs = ps.executeQuery();
	                while (rs.next()) {
	                    List<String> categories = new ArrayList<>(
	                        new LinkedHashSet<>(List.of(rs.getString("categories").split(",")))
	                    );
	                    items.add(new FeaturedItem(
	                        rs.getInt("season_id"),
	                        rs.getInt("serie_id"),
	                        rs.getString("serie_title"),
	                        rs.getString("synopsis"),
	                        rs.getString("trailer_url"),
	                        rs.getString("covert_url"),
	                        rs.getString("title_url"),
	                        rs.getString("poster_url"),
	                        categories,
	                        rs.getString("age_rating"),
	                        rs.getInt("rating"),
	                        rs.getString("status"),
	                        rs.getInt("season_num"),
	                        rs.getInt("last_episode")
	                    ));
	                }
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	        return items;
	    }
	}
