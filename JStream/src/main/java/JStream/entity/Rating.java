package JStream.entity;

import java.security.Timestamp;

public class Rating {
	private int rating_id;
	private int userID;
	private int filmID;
	private int serieID;
	private int note;
	private Timestamp creates_at;
	private Timestamp updated_at;
	
	public Rating() {}
	public Rating(int rating_id, int userID, int filmID, int serieID, int note, Timestamp creates_at,
			Timestamp updated_at) {
		this.rating_id = rating_id;
		this.userID = userID;
		this.filmID = filmID;
		this.serieID = serieID;
		this.note = note;
		this.creates_at = creates_at;
		this.updated_at = updated_at;
	}
	public int getRating_id() {
		return rating_id;
	}
	public void setRating_id(int rating_id) {
		this.rating_id = rating_id;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public int getFilmID() {
		return filmID;
	}
	public void setFilmID(int filmID) {
		this.filmID = filmID;
	}
	public int getSerieID() {
		return serieID;
	}
	public void setSerieID(int serieID) {
		this.serieID = serieID;
	}
	public int getNote() {
		return note;
	}
	public void setNote(int note) {
		this.note = note;
	}
	public Timestamp getCreates_at() {
		return creates_at;
	}
	public void setCreates_at(Timestamp creates_at) {
		this.creates_at = creates_at;
	}
	public Timestamp getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Timestamp updated_at) {
		this.updated_at = updated_at;
	}
	

}
