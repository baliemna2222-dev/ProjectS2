package JStream.dao;

import JStream.entity.Season;
import JStream.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeasonDAO {

    // ===== INSERT =====
    public boolean insertSeason(Season season) {
        String sql = "INSERT INTO season (serie_id, season_num, title, synopsis, trailer_url, " +
                "poster_url, title_url, image_url, planned_episodes, status) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false); 

            ps.setInt(1, season.getSerieId());
            ps.setInt(2, season.getSeasonNum());
            ps.setString(3, season.getTitle());
            ps.setString(4, season.getSynopsis());
            ps.setString(5, season.getTrailerUrl());
            ps.setString(6, season.getPosterUrl());
            ps.setString(7, season.getTitleUrl());
            ps.setString(8, season.getImageUrl());
            ps.setInt(9, season.getPlannedEpisodes());
            ps.setString(10, season.getStatus());

            int rows = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) season.setSeasonId(rs.getInt(1));
            }

            conn.commit();
            conn.setAutoCommit(true);

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== UPDATE =====
    public boolean updateSeason(Season season) {
        String sql = "UPDATE season SET title=?, synopsis=?, trailer_url=?, poster_url=?, " +
                "title_url=?, image_url=?, planned_episodes=?, status=?, rating=? " +
                "WHERE season_id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, season.getTitle());
            ps.setString(2, season.getSynopsis());
            ps.setString(3, season.getTrailerUrl());
            ps.setString(4, season.getPosterUrl());
            ps.setString(5, season.getTitleUrl());
            ps.setString(6, season.getImageUrl());
            ps.setInt(7, season.getPlannedEpisodes());
            ps.setString(8, season.getStatus());
            ps.setDouble(9, season.getRating());
            ps.setInt(10, season.getSeasonId());
            
            

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== DELETE =====
    public boolean deleteSeason(int seasonId) {
        String sql = "DELETE FROM season WHERE season_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, seasonId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== GET ALL SEASONS OF A SERIE =====
    public List<Season> getSeasonsBySerie(int serieId) {
        List<Season> list = new ArrayList<>();
        String sql = "SELECT * FROM season WHERE serie_id=? ORDER BY season_num";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===== GET BY ID =====
    public Season getSeasonById(int seasonId) {
        String sql = "SELECT * FROM season WHERE season_id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, seasonId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ===== HELPER =====
    private Season mapRow(ResultSet rs) throws SQLException {
        Season s = new Season();
        s.setSeasonId(rs.getInt("season_id"));
        s.setSerieId(rs.getInt("serie_id"));
        s.setSeasonNum(rs.getInt("season_num"));
        s.setTitle(rs.getString("title"));
        s.setSynopsis(rs.getString("synopsis"));
        s.setTrailerUrl(rs.getString("trailer_url"));
        s.setPosterUrl(rs.getString("poster_url"));
        s.setTitleUrl(rs.getString("title_url"));
        s.setImageUrl(rs.getString("image_url"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setPlannedEpisodes(rs.getInt("planned_episodes"));
        s.setStatus(rs.getString("status"));
        s.setRating(rs.getDouble("rating"));
        return s;
    }
    public void addSeason(Season season) {
        String sql = "INSERT INTO season (serie_id, season_num, title, synopsis, trailer_url, poster_url, title_url, image_url, planned_episodes, status, rating) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, season.getSerieId());
            stmt.setInt(2, season.getSeasonNum());
            stmt.setString(3, season.getTitle());
            stmt.setString(4, season.getSynopsis());
            stmt.setString(5, season.getTrailerUrl());
            stmt.setString(6, season.getPosterUrl());
            stmt.setString(7, season.getTitleUrl());
            stmt.setString(8, season.getImageUrl());
            stmt.setInt(9, season.getPlannedEpisodes());
            stmt.setString(10, season.getStatus());
            stmt.setDouble(11, season.getRating());

            stmt.executeUpdate();
            System.out.println("✅ Saison ajoutée avec succès !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}