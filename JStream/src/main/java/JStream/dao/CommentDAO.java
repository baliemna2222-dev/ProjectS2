package JStream.dao;

import JStream.entity.Comment;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    // ===== INSERT =====
    public boolean insertComment(Comment comment) {
        String sql = "INSERT INTO comments (user_id, film_id, serie_id, content) VALUES (?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1,    comment.getUserID());
            ps.setInt(2,    comment.getFilmID());
            ps.setInt(3,    comment.getSerieID());
            ps.setString(4, comment.getContent());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) comment.setComment_id(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== DELETE =====
    public boolean deleteComment(int commentId) {
        String sql = "DELETE FROM comments WHERE comment_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, commentId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== FLAG a comment as inappropriate (user reports) =====
    public boolean flagComment(int commentId) {
        String sql = "UPDATE comments SET flagged=TRUE WHERE comment_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, commentId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== GET COMMENTS FOR A FILM =====
    public List<Comment> getCommentsByFilm(int filmId) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE film_id=? AND flagged=FALSE ORDER BY created_at DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, filmId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== GET COMMENTS FOR A SERIE =====
    public List<Comment> getCommentsBySerie(int serieId) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE serie_id=? AND flagged=FALSE ORDER BY created_at DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serieId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== GET ALL FLAGGED (admin moderation) ====
    public List<Comment> getFlaggedComments() {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE flagged=TRUE ORDER BY created_at DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Comment mapRow(ResultSet rs) throws SQLException {
        return new Comment(
            rs.getInt("comment_id"),
            rs.getInt("user_id"),
            rs.getInt("film_id"),
            rs.getInt("serie_id"),
            rs.getString("content"),
            rs.getBoolean("flagged"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}