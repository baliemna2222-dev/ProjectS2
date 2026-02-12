package JStream.entity;

import java.security.Timestamp;

public class Comment {
	private int comment_id;
	private int userID;
	private int filmID;
	private int serieID;
	private String content;
	private boolean Signal;
	private Timestamp creates_at;
	private Timestamp updated_at;
	public Comment() {}
	public Comment(int comment_id, int userID, int filmID, int serieID, String content, boolean signal,
			Timestamp creates_at, Timestamp updated_at) {
		super();
		this.comment_id = comment_id;
		this.userID = userID;
		this.filmID = filmID;
		this.serieID = serieID;
		this.content = content;
		Signal = signal;
		this.creates_at = creates_at;
		this.updated_at = updated_at;
	}
	public int getComment_id() {return comment_id;}
	public void setComment_id(int comment_id) {this.comment_id = comment_id;}
	
	public int getUserID() {return userID;}
	public void setUserID(int userID) {this.userID = userID;}
	
	public int getFilmID() {return filmID;}
	public void setFilmID(int filmID) {this.filmID = filmID;}
	
	public int getSerieID() {return serieID;}
	public void setSerieID(int serieID) {this.serieID = serieID;}
	
	public String getContent() {return content;}
	public void setContent(String content) {this.content = content;}
	
	public boolean isSignal() {return Signal;}
	public void setSignal(boolean signal) {Signal = signal;}
	
	public Timestamp getCreates_at() {return creates_at;}
	public void setCreates_at(Timestamp creates_at) {this.creates_at = creates_at;}
	
	public Timestamp getUpdated_at() {return updated_at;}
	public void setUpdated_at(Timestamp updated_at) {this.updated_at = updated_at;}
	
	

}
