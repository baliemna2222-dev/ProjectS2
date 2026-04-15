package JStream.entity;

import java.sql.Timestamp;
import java.util.List;

public class Season {

    private int seasonId;        // primary key
    private int serieId;         // foreign key to Serie
    private int seasonNum;       // e.g., 1, 2, 3...
    private String title;        // season title
    private String synopsis;     // season synopsis
    private String trailerUrl;   // trailer or preview 
    private String posterUrl;    // poster image
    private String titleUrl;     // logo/title image
    private String imageUrl;     // general image
    private Timestamp createdAt; // creation timestamp
    private int plannedEpisodes; // total number of episodes planned
    private String status;       // "Ongoing" or "Completed"
    private double rating;       // average rating for this season
    private List<Episode> episodes; // optional, can be loaded from DB

    public Season() {}

    public Season(int seasonId, int serieId, int seasonNum, String title, String synopsis,
                  String trailerUrl, String posterUrl, String titleUrl, String imageUrl,
                  Timestamp createdAt, int plannedEpisodes, String status,
                  double rating, List<Episode> episodes) {
        this.seasonId = seasonId;
        this.serieId = serieId;
        this.seasonNum = seasonNum;
        this.title = title;
        this.synopsis = synopsis;
        this.trailerUrl = trailerUrl;
        this.posterUrl = posterUrl;
        this.titleUrl = titleUrl;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.plannedEpisodes = plannedEpisodes;
        this.status = status;
        this.rating = rating;
        this.episodes = episodes;
    }

    // Getters and setters
    public int getSeasonId() { return seasonId; }
    public void setSeasonId(int seasonId) { this.seasonId = seasonId; }

    public int getSerieId() { return serieId; }
    public void setSerieId(int serieId) { this.serieId = serieId; }

    public int getSeasonNum() { return seasonNum; }
    public void setSeasonNum(int seasonNum) { this.seasonNum = seasonNum; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getTitleUrl() { return titleUrl; }
    public void setTitleUrl(String titleUrl) { this.titleUrl = titleUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getPlannedEpisodes() { return plannedEpisodes; }
    public void setPlannedEpisodes(int plannedEpisodes) { this.plannedEpisodes = plannedEpisodes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public List<Episode> getEpisodes() { return episodes; }
    public void setEpisodes(List<Episode> episodes) { this.episodes = episodes; }

    @Override
    public String toString() {
        return "Season{" +
                "seasonId=" + seasonId +
                ", serieId=" + serieId +
                ", seasonNum=" + seasonNum +
                ", title='" + title + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", trailerUrl='" + trailerUrl + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", titleUrl='" + titleUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", plannedEpisodes=" + plannedEpisodes +
                ", status='" + status + '\'' +
                ", rating=" + rating +
                ", episodes=" + episodes +
                '}';
    }
}