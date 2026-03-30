

    package JStream.service;

    import JStream.dao.EpisodeProgressDAO;
import JStream.dao.FeaturedDAO;
import JStream.dao.FilmProgressDAO;
    import JStream.entity.Episode;
    import JStream.entity.FeaturedItemProgress;
    import JStream.entity.FeaturedItem;
    import JStream.entity.WatchStatus;
    import JStream.utils.Database;

    import java.sql.Connection;
    import java.sql.SQLException;
    import java.util.*;

    public class HistoryService {
    	
    	public HistoryService() {
			
		}

		public List<FeaturedItemProgress> getItemsWithProgress(int userId) throws SQLException {
    	    List<FeaturedItemProgress> result = new ArrayList<>();

    	    FilmProgressDAO filmDAO = new FilmProgressDAO(Database.getConnection());
    	    EpisodeProgressDAO epDAO = new EpisodeProgressDAO(Database.getConnection());
    	    FeaturedService featuredService = new FeaturedService();

    	    // ----------------- Films -----------------
    	    List<Integer> watchedFilmIds = filmDAO.getWatchedFilmIds(userId);
    	    for (int filmId : watchedFilmIds) {
    	        FeaturedItem filmItem = featuredService.getFilmById(filmId);
    	        if (filmItem != null) {
    	            WatchStatus status = filmDAO.getFilmStatus(userId, filmId);
    	            int lastPos = filmDAO.getLastPosition(userId, filmId);
    	            result.add(new FeaturedItemProgress(filmItem, status, lastPos));
    	        }
    	    }

    	    // ----------------- Episodes -----------------
    	    Map<Integer, WatchStatus> epProgressMap = epDAO.getProgressForUser(userId);

    	    // Map seriesId -> list of episode progress
    	    Map<Integer, List<Episode>> serieMap = new HashMap<>();
    	    for (int epId : epProgressMap.keySet()) {
    	        Episode ep = featuredService.getEpisodeDetails(epId); // make sure you have this method
    	        if (ep != null) {
    	            int seasonId = ep.getSeasonId(); 
    	            int serieId = featuredService.getSerieIdBySeason(seasonId); // NEW method
    	            serieMap.computeIfAbsent(serieId, k -> new ArrayList<>()).add(ep);
    	        }
    	    }

    	    // ----------------- Build series progress -----------------
    	    for (Integer serieId : serieMap.keySet()) {
    	        FeaturedItem serieItem = featuredService.getSerieById(serieId);
    	        if (serieItem != null) {
    	            List<Episode> episodes = serieMap.get(serieId);

    	            int maxLastPos = 0;
    	            WatchStatus overallStatus = WatchStatus.NOT_STARTED;

    	            for (Episode ep : episodes) {
    	                WatchStatus epStatus = epProgressMap.getOrDefault(ep.getEpId(), WatchStatus.NOT_STARTED);
    	                int epLastPos = epDAO.getLastPosition(userId, ep.getEpId());

    	                if (epLastPos > maxLastPos) maxLastPos = epLastPos;

    	                if (epStatus == WatchStatus.COMPLETED) overallStatus = WatchStatus.COMPLETED;
    	                else if (epStatus == WatchStatus.IN_PROGRESS && overallStatus != WatchStatus.COMPLETED) {
    	                    overallStatus = WatchStatus.IN_PROGRESS;
    	                }
    	            }

    	            result.add(new FeaturedItemProgress(serieItem, overallStatus, maxLastPos));
    	        }
    	    }

    	    return result;
    	}
    	
    }
