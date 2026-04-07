package JStream.entity;

import java.sql.Timestamp;

public class Comment {
    private int comment_id;
    private int userID;
    private int filmID;
    private int epID;
    private String content;
    private boolean flagged;
    private Timestamp creates_at;
    private Timestamp updated_at;

    public Comment() {}

    public Comment(int comment_id, int userID, int filmID, int epID,
                   String content, boolean flagged,
                   Timestamp creates_at, Timestamp updated_at) {
        this.comment_id  = comment_id;
        this.userID      = userID;
        this.filmID      = filmID;
        this.epID        = epID;
        this.content     = content;
        this.flagged     = flagged;
        this.creates_at  = creates_at;
        this.updated_at  = updated_at;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int getComment_id()                      { return comment_id; }
    public void setComment_id(int comment_id)       { this.comment_id = comment_id; }

    public int getUserID()                          { return userID; }
    public void setUserID(int userID)               { this.userID = userID; }

    public int getFilmID()                          { return filmID; }
    public void setFilmID(int filmID)               { this.filmID = filmID; }

    public int getEpID()                            { return epID; }   // ← was wrongly named getSerieID()
    public void setEpID(int epID)                   { this.epID = epID; }

    public String getContent()                      { return content; }
    public void setContent(String content)          { this.content = content; }

    public boolean isFlagged()                      { return flagged; }
    public void setFlagged(boolean flagged)         { this.flagged = flagged; }

    public Timestamp getCreates_at()                { return creates_at; }
    public void setCreates_at(Timestamp creates_at) { this.creates_at = creates_at; }

    public Timestamp getUpdated_at()                { return updated_at; }
    public void setUpdated_at(Timestamp updated_at) { this.updated_at = updated_at; }

    // ── Helper ────────────────────────────────────────────────────────────────

    public boolean isForFilm()    { return filmID > 0; }
    public boolean isForEpisode() { return epID   > 0; }
}