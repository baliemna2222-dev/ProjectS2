package JStream.entity;

import java.security.Timestamp;

public class WatchList {
	private int id ;
	private int userID;
	private int filmID;
	private int serieID;
	private Timestamp addedAt;
	
	public WatchList() {}

	public WatchList(int id, int userID, int filmID, int serieID, Timestamp addedAt) {
		this.id = id;
		this.userID = userID;
		this.filmID = filmID;
		this.serieID = serieID;
		this.addedAt = addedAt;
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

	public int getSerieID() {
		return serieID;
	}

	public void setSerieID(int serieID) {
		this.serieID = serieID;
	}

	public Timestamp getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(Timestamp addedAt) {
		this.addedAt = addedAt;
	}
	

}
