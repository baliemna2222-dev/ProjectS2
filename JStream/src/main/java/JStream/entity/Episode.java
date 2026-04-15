package JStream.entity;

import java.sql.Timestamp;

public class Episode {

    private int epId;           // Primary key
    private int seasonId;       // Foreign key to Season
    private int numEpisode;     // Episode number in the season
    private String title;       // Episode title
    private int duration;  // Episode duration
    private String resume;      // Episode summary / synopsis
    private String videoUrl;    // Video file URL
    private String covertUrl;   // Cover image /
    private double rating;      // Episode rating
    private Timestamp createdAt;
    private Timestamp releasedAt;

    public Episode() {}

    public Episode(int epId, int seasonId, int numEpisode, String title, int duration,
                   String resume, String videoUrl, String covertUrl,
                   double rating, Timestamp createdAt, Timestamp updatedAt) {
        this.epId = epId;
        this.seasonId = seasonId;
        this.numEpisode = numEpisode;
        this.title = title;
        this.duration = duration;
        this.resume = resume;
        this.videoUrl = videoUrl;
        this.covertUrl = covertUrl;
        this.rating = rating;
        this.createdAt = createdAt;
        this.releasedAt = updatedAt;
    }

    // Getters and Setters
    public int getEpId() { return epId; }
    public void setEpId(int epId) { this.epId = epId; }

    public int getSeasonId() { return seasonId; }
    public void setSeasonId(int seasonId) { this.seasonId = seasonId; }

    public int getNumEpisode() { return numEpisode; }
    public void setNumEpisode(int numEpisode) { this.numEpisode = numEpisode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getCovertUrl() { return covertUrl; }
    public void setCovertUrl(String covertUrl) { this.covertUrl = covertUrl; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return releasedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.releasedAt = updatedAt; }

    @Override
    public String toString() {
        return "Episode{" +
                "epId=" + epId +
                ", seasonId=" + seasonId +
                ", numEpisode=" + numEpisode +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", resume='" + resume + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", covertUrl='" + covertUrl + '\'' +
                ", rating=" + rating +
                ", createdAt=" + createdAt +
                ", updatedAt=" + releasedAt +
                '}';
    }

	public Timestamp getReleasedAt() {
		return releasedAt;
	}

	public void setReleasedAt(Timestamp releasedAt) {
		this.releasedAt = releasedAt;
	}
}