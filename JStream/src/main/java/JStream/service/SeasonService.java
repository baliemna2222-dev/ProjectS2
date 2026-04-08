package JStream.service;

import JStream.dao.SeasonDAO;
import JStream.entity.Season;
import java.util.List;

public class SeasonService {
    private final SeasonDAO seasonDAO;

    public SeasonService() {
        this.seasonDAO = new SeasonDAO();
    }

    public void addSeason(Season season) {
        seasonDAO.addSeason(season);
    }
    public void updateSeason(Season serie){
        seasonDAO.updateSeason( serie);
    }
    public void deleteSeason(int id){
        seasonDAO.deleteSeason(id);
    }
    public List<Season> getSeasonsBySerie(int serieId) {
        return seasonDAO.getSeasonsBySerie(serieId);
    }
}