package JStream.entity;

import java.sql.Timestamp;

public class Rating {
    private int rating_id;
    private int userID;
    private int filmID;
    private int serieID;
    private int note;
    private Timestamp created_at;
    private Timestamp updated_at;

    public Rating(){}

    public Rating(int rating_id, int userID, int filmID, int serieID,
                  int note, Timestamp created_at, Timestamp updated_at) {
        this.rating_id = rating_id;
        this.userID = userID;
        this.filmID = filmID;
        this.serieID = serieID;
        this.note = note;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getRating_id() { return rating_id; }
    public void setRating_id(int rating_id) { this.rating_id = rating_id; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public int getFilmID() { return filmID; }
    public void setFilmID(int filmID) { this.filmID = filmID; }

    public int getSerieID() { return serieID; }
    public void setSerieID(int serieID) { this.serieID = serieID; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    public Timestamp getCreated_at() { return created_at; }
    public void setCreated_at(Timestamp created_at) { this.created_at = created_at; }

    public Timestamp getUpdated_at() { return updated_at; }
    public void setUpdated_at(Timestamp updated_at) { this.updated_at = updated_at; }
}