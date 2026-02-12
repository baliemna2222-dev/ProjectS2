package JStream.entity;

import java.security.Timestamp;
import java.time.LocalDateTime;

public class Film {
	private int film_id;
	private String title;
	private String synopsis;
	private String casting;
	private String video_url;
	private String image_url; //l'image d'aperçu affichée avant de cliquer sur un film/série/épisode (image de couverture)
	private Category category;
	private LocalDateTime release_date;
	private Timestamp updated_at;
	private double duration;
	public Film() {
		super();
	}
	public Film(int film_id, String title, String synopsis,String casting, String video_url, String image_url, Category category,
			LocalDateTime release_date,Timestamp updated_at, double duration) {
		super();
		this.film_id = film_id;
		this.title = title;
		this.synopsis = synopsis;
		this.casting = casting;
		this.video_url = video_url;
		this.image_url = image_url;
		this.category = category;
		this.release_date = release_date;
		this.updated_at = updated_at;
		this.duration = duration;
	}
	public int getFilm_id() {
		return film_id;
	}
	public void setFilm_id(int film_id) {
		this.film_id = film_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSynopsis() {
		return synopsis;
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}
	public String getCasting() {
		return casting;
	}
	public void setCasting(String casting) {
		this.casting = casting;
	}
	public String getVideo_url() {
		return video_url;
	}
	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public LocalDateTime getRelease_date() {
		return release_date;
	}
	public void setRelease_date(LocalDateTime release_date) {
		this.release_date = release_date;
	}
	public Timestamp getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Timestamp updated_at) {
		this.updated_at = updated_at;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	@Override
	public String toString() {
		return "Film [film_id=" + film_id + ", title=" + title + ", synopsis=" + synopsis + ", casting=" + casting
				+ ", video_url=" + video_url + ", image_url=" + image_url + ", category=" + category + ", release_date="
				+ release_date + ", updated_at=" + updated_at + ", duration=" + duration + "]";
	}

	

}
