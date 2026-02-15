package JStream.entity;

import java.security.Timestamp;

public class History {
	private int id;
	private int userID;
	private int filmID;
	private int episodeID;
	private int progressionSecondes;
	private boolean completed;
	private Timestamp watchedAt;
	private Timestamp updatedAt;
	public History() {}
	public History(int id, int userID, int filmID, int episodeID, int progressionSecondes, boolean completed,
			Timestamp watchedAt, Timestamp updatedAt) {
		this.id = id;
		this.userID = userID;
		this.filmID = filmID;
		this.episodeID = episodeID;
		this.progressionSecondes = progressionSecondes;
		this.completed = completed;
		this.watchedAt = watchedAt;
		this.updatedAt = updatedAt;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getEpisodeID() {
		return episodeID;
	}
	public void setEpisodeID(int episodeID) {
		this.episodeID = episodeID;
	}
	public int getProgressionSecondes() {
		return progressionSecondes;
	}
	public void setProgressionSecondes(int progressionSecondes) {
		this.progressionSecondes = progressionSecondes;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public Timestamp getWatchedAt() {
		return watchedAt;
	}
	public void setWatchedAt(Timestamp watchedAt) {
		this.watchedAt = watchedAt;
	}
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	


}
