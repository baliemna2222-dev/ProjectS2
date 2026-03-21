package JStream.dao;

import JStream.entity.Category;
import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.Film;
import JStream.entity.Season;
import JStream.entity.Serie;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class FeaturedDAO {

    private final Connection connection;

    public FeaturedDAO(Connection connection) {
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
        	    "SELECT " +
        	    "    s.season_id, " +
        	    "    s.status, " +
        	    "    s.season_num, " +
        	    "    s.poster_url, " +
        	    "    s.trailer_url, " +
        	    "    se.serie_id, " +
        	    "    se.title AS serie_title, " +
        	    "    se.title_url, " +
        	    "    se.synopsis, " +
        	    "    se.rating, " +
        	    "    se.covert_url, " +
        	    "    se.age_rating, " +
        	    "    GROUP_CONCAT(DISTINCT c.name SEPARATOR ',') AS categories, " +
        	    "    COALESCE(MAX(e.num_episode), 0) AS last_episode " +
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
        	        List<String> categories = new ArrayList<>(
        	            new LinkedHashSet<>(List.of(rs.getString("categories").split(",")))
        	        );

        	        featured.add(new FeaturedItem(
        	            rs.getInt("season_id"),
        	            rs.getInt("serie_id"),
        	            rs.getString("serie_title"),   // from serie
        	            rs.getString("synopsis"),      // from serie
        	            rs.getString("trailer_url"),   // from season
        	            rs.getString("covert_url"),    // from serie
        	            rs.getString("title_url"),     // from serie
        	            rs.getString("poster_url"),    // from season
        	            categories,
        	            rs.getString("age_rating"),    // from serie
        	            rs.getInt("rating"),           // from serie
        	            rs.getString("status"),        // from season
        	            rs.getInt("season_num"),
        	            rs.getInt("last_episode")
        	        ));
        	    }
        	}

        	return featured;
    }

    // ----------------------- Full film details -----------------------
    public Film getFilmDetails(int filmId) throws SQLException {
        String sql =
            "SELECT f.*, c.category_id, c.name AS category_name " +
            "FROM film f " +
            "LEFT JOIN film_category fc ON f.film_id = fc.film_id " +
            "LEFT JOIN category c ON fc.category_id = c.category_id " +
            "WHERE f.film_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, filmId);
        ResultSet rs = ps.executeQuery();

        Film film = null;
        List<Category> categories = new ArrayList<>();

        while (rs.next()) {
            if (film == null) {
                film = new Film();
                film.setFilm_id(rs.getInt("film_id"));
                film.setTitle(rs.getString("title"));
                film.setSynopsis(rs.getString("synopsis"));
                film.setCasting(rs.getString("casting"));
                film.setVideo_url(rs.getString("video_url"));
                film.setImage_url(rs.getString("image_url"));
                film.setTitle_image_url(rs.getString("title_image_url"));
                film.setPoster_url(rs.getString("poster_url"));
                if (rs.getTimestamp("release_date") != null) {
                    film.setRelease_date(rs.getTimestamp("release_date").toLocalDateTime());
                }
                film.setUpdated_at(rs.getTimestamp("updated_at"));
                film.setDuration(rs.getDouble("duration"));
                film.setAge_rating(rs.getString("age_rating"));
                film.setRating(rs.getInt("rating"));
            }
            if (rs.getInt("category_id") != 0) {
                Category cat = new Category();
                cat.setCategory_id(rs.getInt("category_id"));
                cat.setName(rs.getString("category_name"));
                categories.add(cat);
            }
        }
        if (film != null) film.setCategories(categories);
        return film;
    }
    
    public Serie getFullSerie(int serieId) throws SQLException {
        // --- Get main serie info ---
        String sql = "SELECT * FROM serie WHERE serie_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, serieId);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) return null;

        Serie serie = new Serie();
        serie.setSerieId(serieId);
        serie.setTitle(rs.getString("title"));
        serie.setTitleUrl(rs.getString("title_url"));   // ✅ add this line
        serie.setSynopsis(rs.getString("synopsis"));
        serie.setCasting(rs.getString("casting"));
        serie.setCovertUrl(rs.getString("covert_url"));
        serie.setAge_rating(rs.getString("age_rating"));
        serie.setRating(rs.getInt("rating"));
        serie.setCreatedAt(rs.getTimestamp("created_at"));
        serie.setUpdatedAt(rs.getTimestamp("updated_at"));

        // --- Load categories directly into the Serie object ---
        List<Category> categories = new ArrayList<>();
        String catSql = "SELECT c.category_id, c.name " +
                        "FROM serie_category sc " +
                        "JOIN category c ON sc.category_id = c.category_id " +
                        "WHERE sc.serie_id = ?";
        try (PreparedStatement psCat = connection.prepareStatement(catSql)) {
            psCat.setInt(1, serieId);
            ResultSet rsCat = psCat.executeQuery();
            while (rsCat.next()) {
                Category cat = new Category();
                cat.setCategory_id(rsCat.getInt("category_id"));
                cat.setName(rsCat.getString("name"));
                categories.add(cat);
            }
        }
        serie.setCategories(categories);

        // --- Load seasons and their episodes ---
        serie.setSeasons(getSeasonsBySerie(serieId));

        return serie;
    }
    // ----------------------- Full season details -----------------------
    public List<Season> getSeasonsBySerie(int serieId) throws SQLException {
        List<Season> seasons = new ArrayList<>();
        String sql = "SELECT * FROM season WHERE serie_id = ? ORDER BY season_num";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, serieId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Season season = new Season();
            int seasonId = rs.getInt("season_id");

            season.setSeasonId(seasonId);
            season.setSerieId(serieId);
            season.setSynopsis(rs.getString("synopsis"));
            season.setSeasonNum(rs.getInt("season_num"));
            season.setTitle(rs.getString("title"));
            season.setTrailerUrl(rs.getString("trailer_url"));
            season.setPosterUrl(rs.getString("poster_url"));
            season.setTitleUrl(rs.getString("title_url"));
            season.setImageUrl(rs.getString("image_url"));
            season.setStatus(rs.getString("status"));
            season.setPlannedEpisodes(rs.getInt("planned_episodes"));
            season.setRating(rs.getInt("rating"));

            // --- Load episodes for this season ---
            season.setEpisodes(getEpisodesBySeason(seasonId));

            seasons.add(season);
        }
        return seasons;
    }

    // ----------------------- Episodes of a season -----------------------
    public List<Episode> getEpisodesBySeason(int seasonId) throws SQLException {
        List<Episode> episodes = new ArrayList<>();
        String sql = "SELECT * FROM episode WHERE season_id = ? ORDER BY num_episode";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, seasonId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Episode ep = new Episode();
            ep.setEpId(rs.getInt("ep_id"));
            ep.setSeasonId(seasonId);
            ep.setNumEpisode(rs.getInt("num_episode"));
            ep.setTitle(rs.getString("title"));
            ep.setResume(rs.getString("resume"));
            ep.setVideoUrl(rs.getString("video_url"));
            ep.setCovertUrl(rs.getString("covert_url"));
            ep.setRating(rs.getInt("rating"));
            ep.setCreatedAt(rs.getTimestamp("created_at"));
            ep.setReleasedAt(rs.getTimestamp("released_at"));
            ep.setDuration(rs.getInt("duration"));
            episodes.add(ep);
        }
        return episodes;
    }
    public List<FeaturedItem> getItemsByCategory(String categoryName) throws SQLException {
        List<FeaturedItem> items = new ArrayList<>();

        // ---------------- FILMS ----------------
        String filmSql =
            "SELECT f.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
            "FROM film f " +
            "JOIN film_category fc ON f.film_id = fc.film_id " +
            "JOIN category c ON fc.category_id = c.category_id " +
            "WHERE c.name = ? " +
            "GROUP BY f.film_id";

        try (PreparedStatement ps = connection.prepareStatement(filmSql)) {
            ps.setString(1, categoryName);
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

        // ---------------- SERIES (latest season) ----------------
        String seasonSql =
            "SELECT " +
            "    s.season_id, " +
            "    s.poster_url, " +
            "    s.trailer_url, " +
            "    s.season_num, " +
            "    s.status, " +
            "    se.serie_id, " +
            "    se.title AS serie_title, " +
            "    se.synopsis, " +
            "    se.title_url, " +
            "    se.covert_url, " +
            "    se.age_rating, " +
            "    se.rating, " +
            "    GROUP_CONCAT(DISTINCT c.name SEPARATOR ',') AS categories, " +
            "    COALESCE(MAX(e.num_episode),0) AS last_episode " +
            "FROM season s " +
            "JOIN serie se ON s.serie_id = se.serie_id " +
            "JOIN serie_category sc ON se.serie_id = sc.serie_id " +
            "JOIN category c ON sc.category_id = c.category_id " +
            "LEFT JOIN episode e ON e.season_id = s.season_id " +
            "WHERE c.name = ? " +
            "AND s.season_id = ( " +
            "   SELECT s2.season_id " +
            "   FROM season s2 " +
            "   LEFT JOIN episode e2 ON e2.season_id = s2.season_id " +
            "   WHERE s2.serie_id = s.serie_id " +
            "   GROUP BY s2.season_id " +
            "   ORDER BY MAX(e2.released_at) DESC " +
            "   LIMIT 1 " +
            ") " +
            "GROUP BY s.season_id";

        try (PreparedStatement ps = connection.prepareStatement(seasonSql)) {
            ps.setString(1, categoryName);
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
                    rs.getString("poster_url"), // ✅ latest season poster
                    categories,
                    rs.getString("age_rating"),
                    rs.getInt("rating"),
                    rs.getString("status"),
                    rs.getInt("season_num"),
                    rs.getInt("last_episode")
                ));
            }
        }

        return items;
    }
 // Get all categories from the database
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category ORDER BY name"; // you can change order if you want

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category cat = new Category();
                cat.setCategory_id(rs.getInt("category_id"));
                cat.setName(rs.getString("name"));
                categories.add(cat);
            }
        }
        return categories;
    }
    }
