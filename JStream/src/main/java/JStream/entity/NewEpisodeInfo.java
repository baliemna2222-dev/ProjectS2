package JStream.entity;

public class NewEpisodeInfo {

    private int    serieId;
    private String serieTitle;
    private int    seasonNum;
    private int    epNum;
    private String epTitle;
    private int    epId;

    public NewEpisodeInfo() {}

    public NewEpisodeInfo(int serieId, String serieTitle, int seasonNum,
                          int epNum, String epTitle, int epId) {
        this.serieId    = serieId;
        this.serieTitle = serieTitle;
        this.seasonNum  = seasonNum;
        this.epNum      = epNum;
        this.epTitle    = epTitle;
        this.epId       = epId;
    }

    public int    getSerieId()                     { return serieId; }
    public void   setSerieId(int serieId)          { this.serieId = serieId; }
    public String getSerieTitle()                  { return serieTitle; }
    public void   setSerieTitle(String serieTitle) { this.serieTitle = serieTitle; }
    public int    getSeasonNum()                   { return seasonNum; }
    public void   setSeasonNum(int seasonNum)      { this.seasonNum = seasonNum; }
    public int    getEpNum()                       { return epNum; }
    public void   setEpNum(int epNum)              { this.epNum = epNum; }
    public String getEpTitle()                     { return epTitle; }
    public void   setEpTitle(String epTitle)       { this.epTitle = epTitle; }
    public int    getEpId()                        { return epId; }
    public void   setEpId(int epId)                { this.epId = epId; }

    @Override
    public String toString() {
        return "NewEpisodeInfo{serieId=" + serieId + ", serieTitle='" + serieTitle +
               "', seasonNum=" + seasonNum + ", epNum=" + epNum +
               ", epTitle='" + epTitle + "', epId=" + epId + "}";
    }
}