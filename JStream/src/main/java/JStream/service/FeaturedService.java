package JStream.service;

import JStream.dao.FeaturedDAO;
import JStream.entity.FeaturedItem;
import JStream.entity.Category;
import JStream.entity.Episode;
import JStream.entity.Film;
import JStream.entity.Serie;
import JStream.entity.Session;
import JStream.entity.Season;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FeaturedService {

    private final FeaturedDAO dao;

    public FeaturedService() {
        Connection conn = Database.getConnection();
        dao = new FeaturedDAO(conn);
    }

    // ----------------------- Latest searches in session ----------------
    private final LinkedList<String> latestSearches = new LinkedList<>();
    private final int MAX_HISTORY = 5;

    public void addToLatestSearch(String title) {
        title = title.trim();
        if (title.isEmpty()) return;

        // Remove duplicate
        latestSearches.remove(title);

        // Add to front
        latestSearches.addFirst(title);

        // Keep max size
        if (latestSearches.size() > MAX_HISTORY) {
            latestSearches.removeLast();
        }

        // --- NEW: Save to database ---
        try {
            int userId = Session.getUserId(); // get current user
            dao.addLatestSearch(userId, title);           // call DAO method to insert
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   
    
    public List<FeaturedItem> searchByTitle(String title) throws SQLException {
        if (title == null || title.isEmpty()) return List.of();
        return dao.searchByTitle(title); // only search, no auto-add to latest
    }

 // FeaturedService.java
    public List<String> getLatestSearches() {
        try {
            int userId = Session.getUserId(); // get current user
            return dao.getLatestSearches(userId, 5); // fetch from DB (limit 5)
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Add a method to remove a search
    public void removeLatestSearch(String title) {
        // Remove from memory
        latestSearches.remove(title);

        // Remove from DB safely
        try {
            dao.deleteLatestSearch(Session.getUserId(), title);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 // Get a FeaturedItem by exact title
    public FeaturedItem getFeaturedByTitle(String title) throws SQLException {
        if (title == null || title.isEmpty()) return null;

        // Use DAO search
        List<FeaturedItem> results = dao.searchByTitle(title);

        // Return the first exact match if any
        for (FeaturedItem item : results) {
            if (item.getTitle().equalsIgnoreCase(title)) {
                return item;
            }
        }
        return results.isEmpty() ? null : results.get(0);
    }
    // ----------------------- Featured items -----------------------
    public List<FeaturedItem> getLatestFeatured(int limit) throws SQLException {
        return dao.getLatestFeatured(limit);
    }

    public Film getFilmDetails(int filmId) throws SQLException {
        return dao.getFilmDetails(filmId);
    }

    public Serie getFullSerie(int serieId) throws SQLException {
        return dao.getFullSerie(serieId);
    }

    public List<Season> getSeasonsBySerie(int serieId) throws SQLException {
        return dao.getSeasonsBySerie(serieId);
    }

    public Season getFullSeason(int seasonId) throws SQLException {
        return dao.getSeasonsBySerie(seasonId).stream()
                .filter(season -> season.getSeasonId() == seasonId)
                .findFirst().orElse(null);
    }

    public List<Episode> getEpisodesBySeason(int seasonId) throws SQLException {
        return dao.getEpisodesBySeason(seasonId);
    }

    public Episode getEpisodeDetails(int episodeId) throws SQLException {
        List<Episode> allEpisodes = dao.getEpisodesBySeason(episodeId); // adjust if needed
        for (Episode ep : allEpisodes) {
            if (ep.getEpId() == episodeId) return ep;
        }
        return null;
    }

    public List<Category> getCategoriesBySerie(int serieId) throws SQLException {
        return dao.getFullSerie(serieId).getCategories();
    }

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

    public List<FeaturedItem> getTopRated(int limit) {
        try {
            return dao.getTopRated(limit);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ----------------------- Filtered items -----------------------
    public List<FeaturedItem> getFilteredItems(
            Set<String> categories,
            Set<String> types,
            Set<Integer> years
    ) {
        try {
            return dao.getFilteredItems(
                    categories == null ? Set.of() : categories,
                    types == null ? Set.of() : types,
                    years == null ? Set.of() : years
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<FeaturedItem> getFilteredFilms(String categoryName, Date releaseAfter) {
        Set<String> categories = (categoryName == null || categoryName.isEmpty()) ? Set.of() : Set.of(categoryName);
        Set<String> types = Set.of("film");
        Set<Integer> years = (releaseAfter == null) ? Set.of() : Set.of(releaseAfter.toLocalDate().getYear());
        return getFilteredItems(categories, types, years);
    }

    public List<FeaturedItem> getFilteredSeries(String categoryName, Date releaseAfter) {
        Set<String> categories = (categoryName == null || categoryName.isEmpty()) ? Set.of() : Set.of(categoryName);
        Set<String> types = Set.of("serie");
        Set<Integer> years = (releaseAfter == null) ? Set.of() : Set.of(releaseAfter.toLocalDate().getYear());
        return getFilteredItems(categories, types, years);
    }

}