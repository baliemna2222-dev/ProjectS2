package JStream.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Serie {
    private int serieId;
    private String title;
    private String synopsis;
    private String casting;
    private String covertUrl;
    private String titleUrl;
    private List<Category> categories;  // ✅ multiple categories

    private List<Season> seasons;
    private Timestamp createdAt;
    private Timestamp updatedAt; 
    private int rating;         // from 1-5
    private String age_rating;  // Age rating like "16+" or "PG-13"

    // Default constructor
    public Serie() {
        this.seasons = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    // Full constructor
    public Serie(int serieId, String title, String synopsis, String casting, String covertUrl,
                 List<Category> categories, Timestamp createdAt, Timestamp updatedAt,
                 int rating, String age_rating,String titleUrl) {
        this.serieId = serieId;
        this.titleUrl=titleUrl;
        this.title = title;
        this.synopsis = synopsis;
        this.casting = casting;
        this.covertUrl = covertUrl;
        this.categories = categories != null ? categories : new ArrayList<>();
        this.seasons = new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.rating = rating;
        this.age_rating = age_rating;
    }

    public String getTitleUrl() {
		return titleUrl;
	}

	public void setTitleUrl(String titleUrl) {
		this.titleUrl = titleUrl;
	}

	// Getters and setters
    public int getSerieId() { return serieId; }
    public void setSerieId(int serieId) { this.serieId = serieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public String getCasting() { return casting; }
    public void setCasting(String casting) { this.casting = casting; }

    public String getCovertUrl() { return covertUrl; }
    public void setCovertUrl(String covertUrl) { this.covertUrl = covertUrl; }

    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }

    public List<Season> getSeasons() { return seasons; }
    public void setSeasons(List<Season> seasons) { this.seasons = seasons; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getAge_rating() { return age_rating; }
    public void setAge_rating(String age_rating) { this.age_rating = age_rating; }

    // Helper method to display categories as comma-separated string
    public String getCategoriesAsString() {
        if (categories == null || categories.isEmpty()) return "";
        return String.join(", ", categories.stream().map(Category::getName).toList());
    }

    @Override
    public String toString() {
        return "Serie [serieId=" + serieId + ", title=" + title + ", synopsis=" + synopsis +
               ", casting=" + casting + ", covertUrl=" + covertUrl +
               ", categories=" + getCategoriesAsString() +
               ", seasons=" + seasons +
               ", createdAt=" + createdAt + ", updatedAt=" + updatedAt +
               ", rating=" + rating + ", age_rating=" + age_rating + "]";
    }
}