package JStream.entity;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Serie {
	private int serieId;
	private String title;
	private String synopsis;
	private String casting;
	private String covertUrl;
	private Category category;
	private List<Season> seasons;
	private Timestamp createdAt;
	private Timestamp updatedAt; 
	public Serie() {}
	public Serie(int serieId, String title, String synopsis,String casting, String covertUrl, Category category,
			Timestamp createdAt,Timestamp updatedAt) {
		this.serieId = serieId;
		this.title = title;
		this.synopsis = synopsis;
		this.casting = casting;
		this.covertUrl = covertUrl;
		this.category = category;
		seasons=new ArrayList<Season>();
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	public int getSerieId() {
		return serieId;
	}
	public void setSerie_id(int serie_id) {
		this.serieId = serieId;
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
	public String getCovertUrl() {
		return covertUrl;
	}
	public void setCovertUrl(String covertUrl) {
		this.covertUrl = covertUrl;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public List<Season> getSeasons() {
		return seasons;
	}
	public void setSeasons(List<Season> seasons) {
		this.seasons = seasons;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreated_at(Timestamp createdAt) {
		this.createdAt = createdAt;
	}	
	public String getCasting() {
		return casting;
	}
	public void setCasting(String casting) {
		this.casting = casting;
	}
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdated_at(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	

}
