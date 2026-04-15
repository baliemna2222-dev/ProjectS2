package JStream.dao;

import JStream.entity.Actor;
import JStream.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActorDAO {

    // =========================================================================
    //  READ – fetch actors linked to a film or serie
    // =========================================================================

    public List<Actor> getActorsByFilm(int filmId) {
        String sql =
            "SELECT a.actor_id, a.name, a.photo_url, fa.role_name " +
            "FROM actor a " +
            "JOIN film_actor fa ON a.actor_id = fa.actor_id " +
            "WHERE fa.film_id = ? " +
            "ORDER BY a.name";
        return queryActors(sql, filmId);
    }

    public List<Actor> getActorsBySerie(int serieId) {
        String sql =
            "SELECT a.actor_id, a.name, a.photo_url, sa.role_name " +
            "FROM actor a " +
            "JOIN serie_actor sa ON a.actor_id = sa.actor_id " +
            "WHERE sa.serie_id = ? " +
            "ORDER BY a.name";
        return queryActors(sql, serieId);
    }

    /** Search actors by name (for autocomplete / picker). */
    public List<Actor> searchActorsByName(String query) {
        String sql = "SELECT actor_id, name, photo_url, NULL AS role_name " +
                     "FROM actor WHERE name LIKE ? ORDER BY name LIMIT 20";
        List<Actor> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Actor getActorById(int actorId) {
        String sql = "SELECT actor_id, name, photo_url, NULL AS role_name FROM actor WHERE actor_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, actorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // =========================================================================
    //  CRUD – actor table
    // =========================================================================

    public int insertActor(Actor actor) {
        String sql = "INSERT INTO actor (name, photo_url) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, actor.getName());
            ps.setString(2, actor.getPhotoUrl());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                actor.setActorId(id);
                return id;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public boolean updateActor(Actor actor) {
        String sql = "UPDATE actor SET name = ?, photo_url = ? WHERE actor_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, actor.getName());
            ps.setString(2, actor.getPhotoUrl());
            ps.setInt(3, actor.getActorId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteActor(int actorId) {
        // pivot rows are removed by ON DELETE CASCADE
        String sql = "DELETE FROM actor WHERE actor_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, actorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // =========================================================================
    //  PIVOT – link / unlink
    // =========================================================================

    public boolean linkToFilm(int filmId, int actorId, String roleName) {
        String sql = "INSERT IGNORE INTO film_actor (film_id, actor_id, role_name) VALUES (?, ?, ?)";
        return executeLink(sql, filmId, actorId, roleName);
    }

    public boolean updateFilmRole(int filmId, int actorId, String roleName) {
        String sql = "UPDATE film_actor SET role_name = ? WHERE film_id = ? AND actor_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleName);
            ps.setInt(2, filmId);
            ps.setInt(3, actorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean linkToSerie(int serieId, int actorId, String roleName) {
        String sql = "INSERT IGNORE INTO serie_actor (serie_id, actor_id, role_name) VALUES (?, ?, ?)";
        return executeLink(sql, serieId, actorId, roleName);
    }

    public boolean updateSerieRole(int serieId, int actorId, String roleName) {
        String sql = "UPDATE serie_actor SET role_name = ? WHERE serie_id = ? AND actor_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleName);
            ps.setInt(2, serieId);
            ps.setInt(3, actorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean unlinkFromFilm(int filmId, int actorId) {
        return executeDelete("DELETE FROM film_actor WHERE film_id=? AND actor_id=?", filmId, actorId);
    }

    public boolean unlinkFromSerie(int serieId, int actorId) {
        return executeDelete("DELETE FROM serie_actor WHERE serie_id=? AND actor_id=?", serieId, actorId);
    }

    // =========================================================================
    //  Private helpers
    // =========================================================================

    private List<Actor> queryActors(String sql, int id) {
        List<Actor> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private boolean executeLink(String sql, int id1, int id2, String role) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id1);
            ps.setInt(2, id2);
            ps.setString(3, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private boolean executeDelete(String sql, int id1, int id2) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id1);
            ps.setInt(2, id2);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Actor mapRow(ResultSet rs) throws SQLException {
        return new Actor(
            rs.getInt("actor_id"),
            rs.getString("name"),
            rs.getString("photo_url"),
            rs.getString("role_name")
        );
    }
}