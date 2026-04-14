package JStream.entity;

import java.sql.Timestamp;

public class Category {
	private int category_id;
	private String name;
	private String description;
	private Timestamp created_at;
	public Category() {
		super();
	}
	public Category(int category_id, String name, String description, Timestamp created_at) {
		super();
		this.category_id = category_id;
		this.name = name;
		this.description = description;
		this.created_at = created_at;
	}
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}
	@Override
	public String toString() {
		return "Category [category_id=" + category_id + ", name=" + name + ", description=" + description
				+ ", created_at=" + created_at + "]";
	}
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof Category)) return false;
	    Category c = (Category) o;
	    return category_id == c.category_id;
	}

	@Override
	public int hashCode() {
	    return Integer.hashCode(category_id);
	}
}
