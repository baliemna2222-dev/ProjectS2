package JStream.service;

import java.util.List;

import JStream.dao.EpisodeDAO;
import JStream.entity.Episode;

public class EpisodeService {

    private  EpisodeDAO episodeDAO = new EpisodeDAO();

   
    public Episode getNextEpisode(int seasonId, int currentNumEpisode) {
        return episodeDAO.getNextEpisode(seasonId, currentNumEpisode);
    }

 
    public Episode getEpisodeById(int epId) {
        return episodeDAO.getEpisodeById(epId);
    }

    public int getSerieIdBySeasonId(int seasonId) {
        return episodeDAO.getSerieIdBySeasonId(seasonId);
    }
    public EpisodeService() {
        this.episodeDAO = new EpisodeDAO();
    }

    public void addEpisode(Episode episode) {
        episodeDAO.addEpisode(episode);
    }

    public List<Episode> getEpisodesBySeason(int seasonId) {
        return episodeDAO.getEpisodesBySeason(seasonId);
    }
}