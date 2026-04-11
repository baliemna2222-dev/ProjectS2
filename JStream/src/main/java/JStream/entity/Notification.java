package JStream.entity;

import java.time.LocalDateTime;

public class Notification {

    private int           id;
    private int           userId;
    private String        title;
    private String        body;
    private String        type;
    private boolean       read;
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(int id, String title, String body, String type,
                        boolean read, LocalDateTime createdAt) {
        this.id        = id;
        this.title     = title;
        this.body      = body;
        this.type      = type;
        this.read      = read;
        this.createdAt = createdAt;
    }

    public Notification(int userId, String title, String body, String type) {
        this.userId = userId;
        this.title  = title;
        this.body   = body;
        this.type   = type;
        this.read   = false;
    }

    public int           getId()                               { return id; }
    public void          setId(int id)                         { this.id = id; }
    public int           getUserId()                           { return userId; }
    public void          setUserId(int userId)                 { this.userId = userId; }
    public String        getTitle()                            { return title; }
    public void          setTitle(String title)                { this.title = title; }
    public String        getBody()                             { return body; }
    public void          setBody(String body)                  { this.body = body; }
    public String        getType()                             { return type; }
    public void          setType(String type)                  { this.type = type; }
    public boolean       isRead()                              { return read; }
    public void          setRead(boolean read)                 { this.read = read; }
    public LocalDateTime getCreatedAt()                        { return createdAt; }
    public void          setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Notification{id=" + id + ", userId=" + userId +
               ", type='" + type + "', title='" + title +
               "', read=" + read + ", createdAt=" + createdAt + "}";
    }
}