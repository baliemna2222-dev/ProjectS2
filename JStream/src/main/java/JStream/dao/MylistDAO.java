package JStream.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	}
