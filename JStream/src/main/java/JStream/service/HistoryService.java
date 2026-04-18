package JStream.service;

import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.FeaturedItemProgress;
import JStream.entity.WatchStatus;

import java.util.*;

public class HistoryService {

    private final FilmProgressService filmProgressService;
    private final EpisodeProgressService episodeProgressService;
    private final FeaturedService featuredService;

    // ------------------- Constructor -------------------
    public HistoryService(FilmProgressService filmProgressService,
                          EpisodeProgressService episodeProgressService,
                          FeaturedService featuredService) {
        this.filmProgressService = filmProgressService;
        this.episodeProgressService = episodeProgressService;
        this.featuredService = featuredService;
    }

    public List<FeaturedItemProgress> getItemsWithProgress(int userId) {
        List<FeaturedItemProgress> result = new ArrayList<>();

        // ----------------- Films -----------------
        try {
            List<FeaturedItem> watchedFilms = filmProgressService.getWatchedFilms(userId);
            for (FeaturedItem filmItem : watchedFilms) {
                try {
                    WatchStatus status = filmProgressService.getFilmStatus(userId, filmItem.getId());
                    int lastPos = filmProgressService.getLastPosition(userId, filmItem.getId());
                    result.add(new FeaturedItemProgress(filmItem, status, lastPos));
                } catch (Exception e) {
                    e.printStackTrace(); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // log if getting watched films fails
        }

        // ----------------- Episodes / Series -----------------
        Map<Integer, WatchStatus> epProgressMap;
        try {
            epProgressMap = episodeProgressService.loadUserProgress(userId);
        } catch (Exception e) {
            e.printStackTrace();
            epProgressMap = new HashMap<>();
        }
        Map<Integer, List<Episode>> serieMap = new HashMap<>();
        for (int epId : epProgressMap.keySet()) {
            try {
                Episode ep = featuredService.getEpisodeDetails(epId);
                if (ep == null) continue;

                int seasonId = ep.getSeasonId();
                int serieId = featuredService.getSerieIdBySeason(seasonId);
                serieMap.computeIfAbsent(serieId, k -> new ArrayList<>()).add(ep);
            } catch (Exception e) {
                e.printStackTrace(); // log per episode error
            }
        }
        for (Map.Entry<Integer, List<Episode>> entry : serieMap.entrySet()) {
            try {
                int serieId = entry.getKey();
                FeaturedItem serieItem = featuredService.getSerieById(serieId);
                if (serieItem == null) continue;

                List<Episode> episodes = entry.getValue();
                int maxLastPos = 0;
                WatchStatus overallStatus = WatchStatus.NOT_STARTED;
                boolean allCompleted = true;

                for (Episode ep : episodes) {
                    try {
                        WatchStatus epStatus = epProgressMap.getOrDefault(ep.getEpId(), WatchStatus.NOT_STARTED);
                        int epLastPos = episodeProgressService.getEpisodeLastPosition(userId, ep.getEpId());

                        maxLastPos = Math.max(maxLastPos, epLastPos);

                        if (epStatus == WatchStatus.IN_PROGRESS) overallStatus = WatchStatus.IN_PROGRESS;
                        if (epStatus != WatchStatus.COMPLETED) allCompleted = false;
                    } catch (Exception e) {
                        e.printStackTrace(); // log per episode
                    }
                }

                if (allCompleted && !episodes.isEmpty()) overallStatus = WatchStatus.COMPLETED;

                result.add(new FeaturedItemProgress(serieItem, overallStatus, maxLastPos));
            } catch (Exception e) {
                e.printStackTrace(); // log per series error
            }
        }

        return result;
    }
}