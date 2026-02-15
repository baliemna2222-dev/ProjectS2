package JStream.entity;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Serie {
	private int serie_id;
	private String title;
	private String synopsis;
	private String casting;
	private String covert_url;
	private Category category;
	private List<Season> seasons;
	private Timestamp created_at;
	private Timestamp updated_at; 
	public Serie() {}
	public Serie(int serie_id, String title, String synopsis,String casting, String covert_url, Category category,
			Timestamp created_at,Timestamp updated_at) {
		this.serie_id = serie_id;
		this.title = title;
		this.synopsis = synopsis;
		this.casting = casting;
		this.covert_url = covert_url;
		this.category = category;
		seasons=new ArrayList<Season>();
		this.created_at = created_at;
		this.updated_at = updated_at;
	}
	public int getSerie_id() {
		return serie_id;
	}
	public void setSerie_id(int serie_id) {
		this.serie_id = serie_id;
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
	public String getCovert_url() {
		return covert_url;
	}
	public void setCovert_url(String covert_url) {
		this.covert_url = covert_url;
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
	public Timestamp getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}	
	public String getCasting() {
		return casting;
	}
	public void setCasting(String casting) {
		this.casting = casting;
	}
	public Timestamp getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Timestamp updated_at) {
		this.updated_at = updated_at;
	}
	
	

}
