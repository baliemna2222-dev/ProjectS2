package JStream.entity;

import java.util.List;
import java.util.stream.Collectors;

public class FeaturedItem {
    private int id;                 // film_id or season_id
    private String type;            // "film" or "serie"
    private String title;
    private String synopsis;
    private String trailerUrl;
    private String mainImageUrl;
    private String titleImageUrl;
    private String posterUrl;
    private List<String> categoryNames; // ✅ multiple categories
    private String ageRating;
    private int rating;             // 0-5 stars
    private String seasonStatus;    // Only for series/season
    private int seasonNumber;  
    private int lastEpisodeNumber;  // Only for series
    private int serieId;            // Only for series

    // Constructor for films
    public FeaturedItem(int id, String title, String synopsis, String trailerUrl,
                        String mainImageUrl, String titleImageUrl, String posterUrl,
                        List<String> categoryNames, String ageRating, int rating) {
        this.id = id;
        this.type = "film";
        this.title = title;
        this.synopsis = synopsis;
        this.trailerUrl = trailerUrl;
        this.mainImageUrl = mainImageUrl;
        this.titleImageUrl = titleImageUrl;
        this.posterUrl = posterUrl;
        this.categoryNames = categoryNames;
        this.ageRating = ageRating;
        this.rating = rating;
    }

    // Constructor for series (season)
    public FeaturedItem(int seasonId, int serieId, String title, String synopsis, String trailerUrl,
                        String mainImageUrl, String titleImageUrl, String posterUrl,
                        List<String> categoryNames,String ageRating, int rating, String seasonStatus,
                        int seasonNumber, int lastEpisodeNumber) {
        this.id = seasonId;
        this.serieId = serieId;
        this.type = "serie";
        this.title = title;
        this.synopsis = synopsis;
        this.trailerUrl = trailerUrl;
        this.mainImageUrl = mainImageUrl;
        this.titleImageUrl = titleImageUrl;
        this.posterUrl = posterUrl;
        this.ageRating=ageRating;
        this.categoryNames = categoryNames;
        this.rating = rating;
        this.seasonStatus = seasonStatus;
        this.seasonNumber = seasonNumber;
        this.lastEpisodeNumber = lastEpisodeNumber;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }

    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }

    public String getTitleImageUrl() { return titleImageUrl; }
    public void setTitleImageUrl(String titleImageUrl) { this.titleImageUrl = titleImageUrl; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public List<String> getCategoryNames() { return categoryNames; }
    public void setCategoryNames(List<String> categoryNames) { this.categoryNames = categoryNames; }

    // Helper: returns categories as a comma-separated string
    public String getCategoriesAsString() {
        if (categoryNames == null || categoryNames.isEmpty()) return "";
        return categoryNames.stream().collect(Collectors.joining(" | "));
    }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getSeasonStatus() { return seasonStatus; }
    public void setSeasonStatus(String seasonStatus) { this.seasonStatus = seasonStatus; }

    public int getSeasonNumber() { return seasonNumber; }
    public void setSeasonNumber(int seasonNumber) { this.seasonNumber = seasonNumber; }

    public int getLastEpisodeNumber() { return lastEpisodeNumber; }
    public void setLastEpisodeNumber(int lastEpisodeNumber) { this.lastEpisodeNumber = lastEpisodeNumber; }

    public int getSerieId() { return serieId; }
    public void setSerieId(int serieId) { this.serieId = serieId; }
}