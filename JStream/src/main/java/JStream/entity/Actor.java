package JStream.entity;

public class Actor {

    private int    actorId;
    private String name;
    private String photoUrl;
    private String roleName;   // rôle dans le film/série courant (depuis la table pivot)

    public Actor() {}

    public Actor(int actorId, String name, String photoUrl, String roleName) {
        this.actorId  = actorId;
        this.name     = name;
        this.photoUrl = photoUrl;
        this.roleName = roleName;
    }

    public int    getActorId()          { return actorId; }
    public void   setActorId(int v)     { this.actorId = v; }

    public String getName()             { return name; }
    public void   setName(String v)     { this.name = v; }

    public String getPhotoUrl()         { return photoUrl; }
    public void   setPhotoUrl(String v) { this.photoUrl = v; }

    public String getRoleName()         { return roleName; }
    public void   setRoleName(String v) { this.roleName = v; }

    @Override
    public String toString() {
        return "Actor{id=" + actorId + ", name='" + name + "', role='" + roleName + "'}";
    }
}