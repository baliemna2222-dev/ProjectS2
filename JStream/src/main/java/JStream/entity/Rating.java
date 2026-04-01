package JStream.entity;

import java.sql.Timestamp;

public class Rating {

    private int rating_id;
    private int userID;
    private int filmID;       // > 0 si film,    sinon 0
    private int serieID;      // > 0 si série,   sinon 0
    private int episodeID;    // > 0 si épisode, sinon 0  
    private int seasonID;     // > 0 si saison,  sinon 0  
    private int note;         // 1..5
    private Timestamp created_at;
    private Timestamp updated_at;

    public Rating() {}

    public static Rating forFilm(int userId, int filmId, int note) {
        Rating r = new Rating();
        r.userID    = userId;
        r.filmID    = filmId;
        r.serieID   = 0;
        r.episodeID = 0;
        r.seasonID  = 0;
        r.note      = note;
        return r;
    }

    public static Rating forEpisode(int userId, int serieId, int seasonId, int episodeId, int note) {
        Rating r = new Rating();
        r.userID    = userId;
        r.filmID    = 0;
        r.serieID   = serieId;
        r.seasonID  = seasonId;
        r.episodeID = episodeId;
        r.note      = note;
        return r;
    }

    public Rating(int rating_id, int userID, int filmID, int serieID,
                  int episodeID, int seasonID, int note,
                  Timestamp created_at, Timestamp updated_at) {
        this.rating_id  = rating_id;
        this.userID     = userID;
        this.filmID     = filmID;
        this.serieID    = serieID;
        this.episodeID  = episodeID;
        this.seasonID   = seasonID;
        this.note       = note;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getRating_id()           { return rating_id; }
    public void setRating_id(int v)     { this.rating_id = v; }

    public int getUserID()              { return userID; }
    public void setUserID(int v)        { this.userID = v; }

    public int getFilmID()              { return filmID; }
    public void setFilmID(int v)        { this.filmID = v; }

    public int getSerieID()             { return serieID; }
    public void setSerieID(int v)       { this.serieID = v; }

    public int getEpisodeID()           { return episodeID; }
    public void setEpisodeID(int v)     { this.episodeID = v; }

    public int getSeasonID()            { return seasonID; }
    public void setSeasonID(int v)      { this.seasonID = v; }

    public int getNote()                { return note; }
    public void setNote(int v)          { this.note = v; }

    public Timestamp getCreated_at()    { return created_at; }
    public void setCreated_at(Timestamp v) { this.created_at = v; }

    public Timestamp getUpdated_at()    { return updated_at; }
    public void setUpdated_at(Timestamp v) { this.updated_at = v; }
}