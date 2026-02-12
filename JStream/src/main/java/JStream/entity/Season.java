package JStream.entity;

import java.security.Timestamp;
import java.util.List;

public class Season {
	private int season_id;
	private int season_num;
	private int serieID;
	private String title;
	private List<Episode> episodes;
	private Timestamp created_at;
	public Season() {}
	public Season(int season_id, int season_num, int serieID, String title, List<Episode> episodes,
			Timestamp created_at) {
		super();
		this.season_id = season_id;
		this.season_num = season_num;
		this.serieID = serieID;
		this.title = title;
		this.episodes = episodes;
		this.created_at = created_at;
	}
	public int getSeason_id() {	return season_id;}
	public void setSeason_id(int season_id) {this.season_id = season_id;}
	public int getSeason_num() {return season_num;}
	public void setSeason_num(int season_num) {this.season_num = season_num;}
	public String getTitle() {return title;}
	public void setTitle(String title) {this.title = title;}
	public List<Episode> getEpisodes() {return episodes;}
	public void setEpisodes(List<Episode> episodes) {this.episodes = episodes;}
	public Timestamp getCreated_at() {return created_at;}
	public void setCreated_at(Timestamp created_at) {this.created_at = created_at;}
	@Override
	public String toString() {
		return "Season [season_id=" + season_id + ", season_num=" + season_num + ", serie=" + serieID + ", title=" + title
				+ ", episodes=" + episodes + ", created_at=" + created_at + "]";
	}
	public int getSerieID() {return serieID;}
	public void setSerieID(int serieID) {this.serieID = serieID;}
	
	

}
