package JStream.entity;

import java.security.Timestamp;
import java.time.LocalDateTime;

public class Episode {
	private int ep_id ;
	private int seasonID;
	private int numEpisode;
	private LocalDateTime duration;
	private String resume;
	private String video_url;
	private String covert_url;
	private Timestamp created_at;
	private Timestamp updated_at;
	public Episode() {}
	public Episode(int ep_id, int seasonID, int numEpisode, LocalDateTime duration, String resume, String video_url,
			String covert_url, Timestamp created_at, Timestamp updated_at) {
		super();
		this.ep_id = ep_id;
		this.seasonID = seasonID;
		this.numEpisode = numEpisode;
		this.duration = duration;
		this.resume = resume;
		this.video_url = video_url;
		this.covert_url = covert_url;
		this.created_at = created_at;
		this.updated_at = updated_at;
	}
	public int getEp_id() {return ep_id;}
	public void setEp_id(int ep_id) {this.ep_id = ep_id;}
	public int getSeasonID() {return seasonID;}
	public void setSeasonID(int seasonID) {this.seasonID = seasonID;}
	public int getNumEpisode() {return numEpisode;}
	public void setNumEpisode(int numEpisode) {this.numEpisode = numEpisode;}
	public LocalDateTime getDuration() {return duration;}
	public void setDuration(LocalDateTime duration) {this.duration = duration;}
	public String getResume() {return resume;}
	public void setResume(String resume) {this.resume = resume;}
	public String getVideo_url() {return video_url;}
	public void setVideo_url(String video_url) {this.video_url = video_url;}
	public String getCovert_url() {return covert_url;}
	public void setCovert_url(String covert_url) {this.covert_url = covert_url;}
	public Timestamp getCreated_at() {return created_at;}
	public void setCreated_at(Timestamp created_at) {this.created_at = created_at;}
	public Timestamp getUpdated_at() {return updated_at;}
	public void setUpdated_at(Timestamp updated_at) {this.updated_at = updated_at;}
	

}
