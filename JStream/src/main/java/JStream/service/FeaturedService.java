package JStream.service;

import JStream.dao.FeaturedDAO;
import JStream.entity.Category;
import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.Film;
import JStream.entity.Season;
import JStream.entity.Serie;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeaturedService {

    private final FeaturedDAO dao;

    public FeaturedService() {
        Connection conn = Database.getConnection();
        dao = new FeaturedDAO(conn);
        
    }
    
   
    // ----------------------- Featured items -----------------------
    public List<FeaturedItem> getLatestFeatured(int limit) throws SQLException {
        return dao.getLatestFeatured(limit);
    }

    // ----------------------- Film details -----------------------
    public Film getFilmDetails(int filmId) throws SQLException {
        return dao.getFilmDetails(filmId);
    }

    // ----------------------- Full Serie details -----------------------
    public Serie getFullSerie(int serieId) throws SQLException {
        return dao.getFullSerie(serieId);
    }

    // ----------------------- Seasons of a Serie -----------------------
    public List<Season> getSeasonsBySerie(int serieId) throws SQLException {
        return dao.getSeasonsBySerie(serieId);
    }

    // ----------------------- Full season details -----------------------
    public Season getFullSeason(int seasonId) throws SQLException {
        // Optionally, you could load Serie info if needed
        return dao.getSeasonsBySerie(seasonId).stream()
                .filter(season -> season.getSeasonId() == seasonId)
                .findFirst().orElse(null);
    }

    // ----------------------- Episodes of a season -----------------------
    public List<Episode> getEpisodesBySeason(int seasonId) throws SQLException {
        return dao.getEpisodesBySeason(seasonId);
    }

    // ----------------------- Single episode details -----------------------
    public Episode getEpisodeDetails(int episodeId) throws SQLException {
        // Create a DAO method getEpisodeById is recommended, but we can fallback:
        List<Episode> allEpisodes = dao.getEpisodesBySeason(episodeId); // might need seasonId instead
        for (Episode ep : allEpisodes) {
            if (ep.getEpId() == episodeId) return ep;
        }
        return null;
    }

    // ----------------------- Serie categories -----------------------
    public List<Category> getCategoriesBySerie(int serieId) throws SQLException {
        // Helper if you want to get categories separately
        return dao.getFullSerie(serieId).getCategories();
    }
 // Get all categories
    public List<Category> getAllCategories() {
        try {
            return dao.getAllCategories();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<FeaturedItem> getItemsByCategory(String categoryName) {
        try {
            return dao.getItemsByCategory(categoryName); 
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}