package JStream.service;

import JStream.dao.EpisodeDAO;
import JStream.entity.Episode;

public class EpisodeService {

    private final EpisodeDAO episodeDAO = new EpisodeDAO();

   
    public Episode getNextEpisode(int seasonId, int currentNumEpisode) {
        return episodeDAO.getNextEpisode(seasonId, currentNumEpisode);
    }

 
    public Episode getEpisodeById(int epId) {
        return episodeDAO.getEpisodeById(epId);
    }

    public int getSerieIdBySeasonId(int seasonId) {
        return episodeDAO.getSerieIdBySeasonId(seasonId);
    }
}