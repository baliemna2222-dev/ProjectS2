package JStream.dao;

import JStream.entity.Category;
import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.Film;
import JStream.entity.Season;
import JStream.entity.Serie;
import JStream.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FeaturedDAO {

	public List<FeaturedItem> getLatestFeatured(int limit) throws SQLException {
	    List<FeaturedItem> featured = new ArrayList<>();

	    // -------- FILMS --------
	    String filmSql =
	        "SELECT f.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
	        "FROM film f " +
	        "JOIN film_category fc ON f.film_id = fc.film_id " +
	        "JOIN category c ON fc.category_id = c.category_id " +
	        "GROUP BY f.film_id " +
	        "ORDER BY f.release_date DESC LIMIT ?";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement ps = conn.prepareStatement(filmSql)) {

	        ps.setInt(1, limit);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            List<String> categories = List.of(rs.getString("categories").split(","));

	            featured.add(new FeaturedItem(
	                    rs.getInt("film_id"),
	                    rs.getString("title"),
	                    rs.getString("synopsis"),
	                    rs.getString("trailer_url"),
	                    rs.getString("image_url"),
	                    rs.getString("title_image_url"),
	                    rs.getString("poster_url"),
	                    categories,
	                    rs.getString("age_rating"),
	                    rs.getInt("rating")
	            ));
	        }
	    }

	    // -------- SEASONS --------
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

	    try (Connection conn = Database.getConnection();
	         PreparedStatement ps = conn.prepareStatement(seasonSql)) {

	        ps.setInt(1, limit);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            List<String> categories = new ArrayList<>(
	                new LinkedHashSet<>(List.of(rs.getString("categories").split(",")))
	            );

	            featured.add(new FeaturedItem(
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

	    Film film = null;
	    List<Category> categories = new ArrayList<>();

	    try (Connection conn = Database.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, filmId);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            if (film == null) {
	                film = new Film();
	                film.setFilm_id(rs.getInt("film_id"));
	                film.setTitle(rs.getString("title"));
	                film.setSynopsis(rs.getString("synopsis"));
	                film.setCasting(rs.getString("casting"));
	                film.setVideo_url(rs.getString("video_url"));
	                film.setTrailer_url(rs.getString("trailer_url"));   // ✅ NEW
	                film.setImage_url(rs.getString("image_url"));
	                film.setTitle_image_url(rs.getString("title_image_url"));
	                film.setPoster_url(rs.getString("poster_url"));
	                film.setPosterV_url(rs.getString("poster_v_url"));  // ✅ NEW

	                if (rs.getTimestamp("release_date") != null)
	                    film.setRelease_date(rs.getTimestamp("release_date").toLocalDateTime());

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
	    }

	    if (film != null) film.setCategories(categories);
	    return film;
	}
    
	public Serie getFullSerie(int serieId) throws SQLException {

	    Serie serie = null;

	    // -------- MAIN SERIE --------
	    String sql = "SELECT * FROM serie WHERE serie_id = ?";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, serieId);
	        ResultSet rs = ps.executeQuery();

	        if (!rs.next()) return null;

	        serie = new Serie();
	        serie.setSerieId(serieId);
	        serie.setTitle(rs.getString("title"));
	        serie.setTitleUrl(rs.getString("title_url"));
	        serie.setSynopsis(rs.getString("synopsis"));
	        serie.setCasting(rs.getString("casting"));
	        serie.setCovertUrl(rs.getString("covert_url"));
	        serie.setAge_rating(rs.getString("age_rating"));
	        serie.setRating(rs.getInt("rating"));
	        serie.setCreatedAt(rs.getTimestamp("created_at"));
	        serie.setUpdatedAt(rs.getTimestamp("updated_at"));

	        // -------- CATEGORIES --------
	        List<Category> categories = new ArrayList<>();

	        String catSql =
	            "SELECT c.category_id, c.name " +
	            "FROM serie_category sc " +
	            "JOIN category c ON sc.category_id = c.category_id " +
	            "WHERE sc.serie_id = ?";

	        try (PreparedStatement psCat = conn.prepareStatement(catSql)) {
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
	    }

	    // -------- SEASONS (IMPORTANT) --------
	    // This stays OUTSIDE because your method probably opens its own connection
	    serie.setSeasons(getSeasonsBySerie(serieId));

	    return serie;
	}
    // ----------------------- Full season details -----------------------
	public List<Season> getSeasonsBySerie(int serieId) throws SQLException {
	    List<Season> seasons = new ArrayList<>();
	    String sql = "SELECT * FROM season WHERE serie_id = ? ORDER BY season_num";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

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

	            // ✅ SAFE: this method will open its own connection
	            season.setEpisodes(getEpisodesBySeason(seasonId));

	            seasons.add(season);
	        }
	    }

	    return seasons;
	}

    // ----------------------- Episodes of a season -----------------------
	public List<Episode> getEpisodesBySeason(int seasonId) throws SQLException {
	    List<Episode> episodes = new ArrayList<>();
	    String sql = "SELECT * FROM episode WHERE season_id = ? ORDER BY num_episode";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

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
	    }

	    return episodes;
	}
	public List<FeaturedItem> getItemsByCategory(String categoryName) throws SQLException {
	    List<FeaturedItem> items = new ArrayList<>();

	    try (Connection conn = Database.getConnection()) {

	        // ---------------- FILMS ----------------
	        String filmSql =
	            "SELECT f.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
	            "FROM film f " +
	            "JOIN film_category fc ON f.film_id = fc.film_id " +
	            "JOIN category c ON fc.category_id = c.category_id " +
	            "WHERE c.name = ? " +
	            "GROUP BY f.film_id";

	        try (PreparedStatement ps = conn.prepareStatement(filmSql)) {
	            ps.setString(1, categoryName);
	            ResultSet rs = ps.executeQuery();

	            while (rs.next()) {
	                List<String> categories = List.of(rs.getString("categories").split(","));

	                items.add(new FeaturedItem(
	                    rs.getInt("film_id"),
	                    rs.getString("title"),
	                    rs.getString("synopsis"),
	                    rs.getString("trailer_url"),
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

	        try (PreparedStatement ps = conn.prepareStatement(seasonSql)) {
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
	    }

	    return items;
	}
 // Get all categories from the database
	public List<Category> getAllCategories() throws SQLException {
	    List<Category> categories = new ArrayList<>();
	    String sql = "SELECT * FROM category ORDER BY name";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
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
	public List<FeaturedItem> getTopRated(int limit) throws SQLException {
	    List<FeaturedItem> items = new ArrayList<>();

	    // -------- TOP FILMS --------
	    String filmSql =
	        "SELECT f.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
	        "FROM film f " +
	        "JOIN film_category fc ON f.film_id = fc.film_id " +
	        "JOIN category c ON fc.category_id = c.category_id " +
	        "GROUP BY f.film_id " +
	        "ORDER BY f.rating DESC " +
	        "LIMIT ?";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement ps = conn.prepareStatement(filmSql)) {

	        ps.setInt(1, limit);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                List<String> categories = List.of(rs.getString("categories").split(","));
	                items.add(new FeaturedItem(
	                    rs.getInt("film_id"),
	                    rs.getString("title"),
	                    rs.getString("synopsis"),
	                    rs.getString("trailer_url"),
	                    rs.getString("image_url"),
	                    rs.getString("title_image_url"),
	                    rs.getString("poster_url"),
	                    categories,
	                    rs.getString("age_rating"),
	                    rs.getInt("rating")
	                ));
	            }
	        }

	        // -------- TOP SERIES (latest season only) --------
	        String seasonSql =
	            "SELECT " +
	            "    s.season_id, s.poster_url, s.trailer_url, s.season_num, s.status, " +
	            "    se.serie_id, se.title AS serie_title, se.synopsis, se.title_url, se.covert_url, " +
	            "    se.age_rating, se.rating, " +
	            "    GROUP_CONCAT(DISTINCT c.name SEPARATOR ',') AS categories, " +
	            "    COALESCE(MAX(e.num_episode),0) AS last_episode " +
	            "FROM season s " +
	            "JOIN serie se ON s.serie_id = se.serie_id " +
	            "JOIN serie_category sc ON se.serie_id = sc.serie_id " +
	            "JOIN category c ON sc.category_id = c.category_id " +
	            "LEFT JOIN episode e ON e.season_id = s.season_id " +
	            "WHERE s.season_id = ( " +
	            "   SELECT s2.season_id " +
	            "   FROM season s2 " +
	            "   LEFT JOIN episode e2 ON e2.season_id = s2.season_id " +
	            "   WHERE s2.serie_id = s.serie_id " +
	            "   GROUP BY s2.season_id " +
	            "   ORDER BY MAX(e2.released_at) DESC " +
	            "   LIMIT 1 " +
	            ") " +
	            "GROUP BY s.season_id " +
	            "ORDER BY se.rating DESC " +
	            "LIMIT ?";

	        try (PreparedStatement ps2 = conn.prepareStatement(seasonSql)) {
	            ps2.setInt(1, limit);
	            try (ResultSet rs2 = ps2.executeQuery()) {
	                while (rs2.next()) {
	                    List<String> categories = new ArrayList<>(
	                        new LinkedHashSet<>(List.of(rs2.getString("categories").split(",")))
	                    );

	                    items.add(new FeaturedItem(
	                        rs2.getInt("season_id"),
	                        rs2.getInt("serie_id"),
	                        rs2.getString("serie_title"),
	                        rs2.getString("synopsis"),
	                        rs2.getString("trailer_url"),
	                        rs2.getString("covert_url"),
	                        rs2.getString("title_url"),
	                        rs2.getString("poster_url"),
	                        categories,
	                        rs2.getString("age_rating"),
	                        rs2.getInt("rating"),
	                        rs2.getString("status"),
	                        rs2.getInt("season_num"),
	                        rs2.getInt("last_episode")
	                    ));
	                }
	            }
	        }
	    }

	    // 🔥 SORT EVERYTHING TOGETHER (mix films + series)
	    items.sort((a, b) -> Integer.compare(b.getRating(), a.getRating()));

	    // 🔥 LIMIT FINAL RESULT
	    return items.stream().limit(limit).toList();
	}
	public List<FeaturedItem> getFilteredItems(
	        Set<String> categories,  // can be empty
	        Set<String> types,       // can be empty (film, serie)
	        Set<Integer> years       // can be empty
	) throws SQLException {

	    List<FeaturedItem> items = new ArrayList<>();
	    boolean filterFilms = types.isEmpty() || types.stream().anyMatch(t -> t.equalsIgnoreCase("film"));
	    boolean filterSeries = types.isEmpty() || types.stream().anyMatch(t -> t.equalsIgnoreCase("serie"));

	    try (Connection connection = Database.getConnection()) {  // <-- get connection here

	        // ---------------- FILMS ----------------
	        if (filterFilms) {
	            StringBuilder filmSql = new StringBuilder(
	                "SELECT f.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
	                "FROM film f " +
	                "JOIN film_category fc ON f.film_id = fc.film_id " +
	                "JOIN category c ON fc.category_id = c.category_id " +
	                "WHERE 1=1 "
	            );

	            if (!categories.isEmpty()) {
	                filmSql.append(" AND c.name IN (")
	                       .append(String.join(",", Collections.nCopies(categories.size(), "?")))
	                       .append(") ");
	            }

	            if (!years.isEmpty()) {
	                filmSql.append(" AND YEAR(f.release_date) IN (")
	                       .append(String.join(",", Collections.nCopies(years.size(), "?")))
	                       .append(") ");
	            }

	            filmSql.append(" GROUP BY f.film_id ORDER BY f.release_date DESC");

	            try (PreparedStatement ps = connection.prepareStatement(filmSql.toString())) {
	                int index = 1;
	                for (String cat : categories) ps.setString(index++, cat);
	                for (Integer year : years) ps.setInt(index++, year);

	                ResultSet rs = ps.executeQuery();
	                while (rs.next()) {
	                    List<String> filmCategories = List.of(rs.getString("categories").split(","));
	                    items.add(new FeaturedItem(
	                        rs.getInt("film_id"),
	                        rs.getString("title"),
	                        rs.getString("synopsis"),
	                        rs.getString("trailer_url"),
	                        rs.getString("image_url"),
	                        rs.getString("title_image_url"),
	                        rs.getString("poster_url"),
	                        filmCategories,
	                        rs.getString("age_rating"),
	                        rs.getInt("rating")
	                    ));
	                }
	            }
	        }

	        // ---------------- SERIES ----------------
	        if (filterSeries) {
	            StringBuilder seriesSql = new StringBuilder(
	                "SELECT s.season_id, s.poster_url, s.trailer_url, s.season_num, s.status, " +
	                "se.serie_id, se.title AS serie_title, se.synopsis, se.title_url, se.covert_url, se.age_rating, se.rating, " +
	                "GROUP_CONCAT(DISTINCT c.name SEPARATOR ',') AS categories, " +
	                "COALESCE(MAX(e.num_episode),0) AS last_episode, MAX(e.released_at) AS last_release, COUNT(e.ep_id) AS episode_count " +
	                "FROM season s " +
	                "JOIN serie se ON s.serie_id = se.serie_id " +
	                "JOIN serie_category sc ON se.serie_id = sc.serie_id " +
	                "JOIN category c ON sc.category_id = c.category_id " +
	                "LEFT JOIN episode e ON e.season_id = s.season_id " +
	                "WHERE 1=1 "
	            );

	            if (!categories.isEmpty()) {
	                seriesSql.append(" AND c.name IN (")
	                         .append(String.join(",", Collections.nCopies(categories.size(), "?")))
	                         .append(") ");
	            }

	            if (!years.isEmpty()) {
	                seriesSql.append(" AND YEAR(se.created_at) IN (")
	                         .append(String.join(",", Collections.nCopies(years.size(), "?")))
	                         .append(") ");
	            }

	            seriesSql.append(
	                "AND s.season_id = ( " +
	                "   SELECT s2.season_id " +
	                "   FROM season s2 " +
	                "   LEFT JOIN episode e2 ON e2.season_id = s2.season_id " +
	                "   WHERE s2.serie_id = s.serie_id " +
	                "   GROUP BY s2.season_id " +
	                "   ORDER BY MAX(e2.released_at) DESC LIMIT 1" +
	                ") " +
	                "GROUP BY s.season_id ORDER BY last_release DESC, episode_count DESC, se.rating DESC"
	            );

	            try (PreparedStatement ps = connection.prepareStatement(seriesSql.toString())) {
	                int index = 1;
	                for (String cat : categories) ps.setString(index++, cat);
	                for (Integer year : years) ps.setInt(index++, year);

	                ResultSet rs = ps.executeQuery();
	                while (rs.next()) {
	                    List<String> serieCategories = new ArrayList<>(
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
	                        serieCategories,
	                        rs.getString("age_rating"),
	                        rs.getInt("rating"),
	                        rs.getString("status"),
	                        rs.getInt("season_num"),
	                        rs.getInt("last_episode")
	                    ));
	                }
	            }
	        }

	    } // connection auto-closed

	    // 🔥 Sort all by rating
	    items.sort((a, b) -> Integer.compare(b.getRating(), a.getRating()));

	    return items;
	}
	public List<FeaturedItem> searchByTitle(String query) throws SQLException {
	    List<FeaturedItem> results = new ArrayList<>();
	    String likeQuery = "%" + query + "%";

	    try (Connection connection = Database.getConnection()) { // <-- get connection here

	        // ---------------- FILMS ----------------
	        String filmSql = "SELECT f.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
	                         "FROM film f " +
	                         "LEFT JOIN film_category fc ON f.film_id = fc.film_id " +
	                         "LEFT JOIN category c ON fc.category_id = c.category_id " +
	                         "WHERE f.title LIKE ? " +
	                         "GROUP BY f.film_id";

	        try (PreparedStatement ps = connection.prepareStatement(filmSql)) {
	            ps.setString(1, likeQuery);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) {
	                List<String> categories = rs.getString("categories") != null
	                        ? List.of(rs.getString("categories").split(","))
	                        : new ArrayList<>();

	                results.add(new FeaturedItem(
	                    rs.getInt("film_id"),
	                    rs.getString("title"),
	                    rs.getString("synopsis"),
	                    rs.getString("trailer_url"),
	                    rs.getString("image_url"),
	                    rs.getString("title_image_url"),
	                    rs.getString("poster_url"),
	                    categories,
	                    rs.getString("age_rating"),
	                    rs.getInt("rating")
	                ));
	            }
	        }

	        // ---------------- SERIES ----------------
	        String seriesSql = "SELECT s.season_id, s.poster_url, s.trailer_url, s.season_num, s.status, " +
	                           "se.serie_id, se.title AS serie_title, se.synopsis, se.title_url, se.covert_url, se.age_rating, se.rating, " +
	                           "GROUP_CONCAT(DISTINCT c.name SEPARATOR ',') AS categories, " +
	                           "COALESCE(MAX(e.num_episode),0) AS last_episode " +
	                           "FROM season s " +
	                           "JOIN serie se ON s.serie_id = se.serie_id " +
	                           "LEFT JOIN serie_category sc ON se.serie_id = sc.serie_id " +
	                           "LEFT JOIN category c ON sc.category_id = c.category_id " +
	                           "LEFT JOIN episode e ON e.season_id = s.season_id " +
	                           "WHERE se.title LIKE ? " +
	                           "AND s.season_id = ( " +
	                           "   SELECT s2.season_id FROM season s2 " +
	                           "   LEFT JOIN episode e2 ON e2.season_id = s2.season_id " +
	                           "   WHERE s2.serie_id = s.serie_id " +
	                           "   GROUP BY s2.season_id ORDER BY MAX(e2.released_at) DESC LIMIT 1" +
	                           ") " +
	                           "GROUP BY s.season_id";

	        try (PreparedStatement ps = connection.prepareStatement(seriesSql)) {
	            ps.setString(1, likeQuery);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) {
	                List<String> categories = rs.getString("categories") != null
	                        ? new ArrayList<>(new LinkedHashSet<>(List.of(rs.getString("categories").split(","))))
	                        : new ArrayList<>();

	                results.add(new FeaturedItem(
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
	    } // <-- connection auto-closed here

	    return results;
	}
	// ----------------- Latest Searches -----------------
	public void addLatestSearch(int userId, String title) throws SQLException {
	    title = title.trim();
	    if (title.isEmpty()) return;

	    try (Connection connection = Database.getConnection()) {

	        // 1. Remove duplicate
	        String deleteDuplicate = "DELETE FROM latest_search WHERE user_id = ? AND title = ?";
	        try (PreparedStatement ps = connection.prepareStatement(deleteDuplicate)) {
	            ps.setInt(1, userId);
	            ps.setString(2, title);
	            ps.executeUpdate();
	        }

	        // 2. Insert new search
	        String insertSql = "INSERT INTO latest_search(user_id, title) VALUES (?, ?)";
	        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
	            ps.setInt(1, userId);
	            ps.setString(2, title);
	            ps.executeUpdate();
	        }

	        // 3. Keep ONLY 5 latest searches
	        String deleteOld =
	            "DELETE FROM latest_search WHERE user_id = ? AND searched_at NOT IN (" +
	            "SELECT searched_at FROM (" +
	            "SELECT searched_at FROM latest_search WHERE user_id = ? ORDER BY searched_at DESC LIMIT 5" +
	            ") AS temp)";

	        try (PreparedStatement ps = connection.prepareStatement(deleteOld)) {
	            ps.setInt(1, userId);
	            ps.setInt(2, userId);
	            ps.executeUpdate();
	        }
	    }
	}

	public List<String> getLatestSearches(int userId, int limit) throws SQLException {
	    List<String> searches = new ArrayList<>();
	    String sql = "SELECT title FROM latest_search WHERE user_id=? ORDER BY searched_at DESC LIMIT ?";

	    try (Connection connection = Database.getConnection();
	         PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setInt(1, userId);
	        ps.setInt(2, limit);
	        ResultSet rs = ps.executeQuery();
	        while(rs.next()) searches.add(rs.getString("title"));
	    }

	    return searches;
	}

	public void deleteLatestSearch(int userId, String title) throws SQLException {
	    String sql = "DELETE FROM latest_search WHERE user_id = ? AND title = ?";
	    try (Connection connection = Database.getConnection();
	         PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setInt(1, userId);
	        ps.setString(2, title);
	        ps.executeUpdate();
	    }
	}

	// ----------------- Get FeaturedItem by Film ID -----------------
	public FeaturedItem getFilmById(int filmId) throws SQLException {
	    String sql = "SELECT f.*, GROUP_CONCAT(c.name SEPARATOR ',') AS categories " +
	                 "FROM film f " +
	                 "LEFT JOIN film_category fc ON f.film_id = fc.film_id " +
	                 "LEFT JOIN category c ON fc.category_id = c.category_id " +
	                 "WHERE f.film_id = ? " +
	                 "GROUP BY f.film_id";

	    try (Connection connection = Database.getConnection();
	         PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setInt(1, filmId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            List<String> categories = rs.getString("categories") != null
	                    ? List.of(rs.getString("categories").split(","))
	                    : new ArrayList<>();

	            return new FeaturedItem(
	                    rs.getInt("film_id"),
	                    rs.getString("title"),
	                    rs.getString("synopsis"),
	                    rs.getString("trailor_url"),
	                    rs.getString("image_url"),
	                    rs.getString("title_image_url"),
	                    rs.getString("poster_url"),
	                    categories,
	                    rs.getString("age_rating"),
	                    rs.getInt("rating")
	            );
	        }
	    }
	    return null;
	}

	// ----------------- Get Serie Info by Season -----------------
	public int getSerieIdBySeason(int seasonId) throws SQLException {
	    String sql = "SELECT serie_id FROM season WHERE season_id = ?";
	    try (Connection connection = Database.getConnection();
	         PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setInt(1, seasonId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("serie_id"); 
	        }
	    }
	    return -1; 
	}

	public Serie getSerieBySeason(int seasonId) throws SQLException {
	    int serieId = getSerieIdBySeason(seasonId);
	    if (serieId == -1) return null;
	    return getFullSerie(serieId); // reuse existing method
	}

	// ----------------- Get FeaturedItem by Serie ID -----------------
	public FeaturedItem getSerieById(int serieId) throws SQLException {
	    String sql =
	        "SELECT s.season_id, s.poster_url, s.trailer_url, s.season_num, s.status, " +
	        "se.serie_id, se.title AS serie_title, se.synopsis, se.title_url, se.covert_url, " +
	        "se.age_rating, se.rating, " +
	        "GROUP_CONCAT(DISTINCT c.name SEPARATOR ',') AS categories, " +
	        "COALESCE(MAX(e.num_episode),0) AS last_episode " +
	        "FROM season s " +
	        "JOIN serie se ON s.serie_id = se.serie_id " +
	        "LEFT JOIN serie_category sc ON se.serie_id = sc.serie_id " +
	        "LEFT JOIN category c ON sc.category_id = c.category_id " +
	        "LEFT JOIN episode e ON e.season_id = s.season_id " +
	        "WHERE se.serie_id = ? " +
	        "AND s.season_id = ( " +
	        "    SELECT s2.season_id FROM season s2 " +
	        "    LEFT JOIN episode e2 ON e2.season_id = s2.season_id " +
	        "    WHERE s2.serie_id = s.serie_id " +
	        "    GROUP BY s2.season_id " +
	        "    ORDER BY MAX(e2.released_at) DESC LIMIT 1" +
	        ") " +
	        "GROUP BY s.season_id";

	    try (Connection connection = Database.getConnection();
	         PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setInt(1, serieId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            List<String> categories = rs.getString("categories") != null
	                    ? new ArrayList<>(new LinkedHashSet<>(List.of(rs.getString("categories").split(","))))
	                    : new ArrayList<>();

	            return new FeaturedItem(
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
	            );
	        }
	    }
	    return null;
	}
}
