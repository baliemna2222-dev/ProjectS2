package JStream.service;

import JStream.dao.RatingDAO;
import JStream.entity.Rating;

public class RatingService {

    private final RatingDAO ratingDAO = new RatingDAO();

    //  SUBMIT
   
    public boolean submitRating(Rating rating) {
        return ratingDAO.upsert(rating);
    }
 
    //Moyenne des ratings users pour un film (sur 5). 
    public double getAverageForFilm(int filmId) {
        return ratingDAO.getAverageForFilm(filmId);
    }

    // Moyenne des ratings users pour un épisode précis (sur 5). 
    public double getAverageForEpisode(int episodeId) {
        return ratingDAO.getAverageForEpisode(episodeId);
    }

    //Moyenne des épisodes d'une saison (sur 5). 
    public double getAverageForSeason(int seasonId) {
        return ratingDAO.getAverageForSeason(seasonId);
    }

  
    public double getAverageForSerie(int serieId) {
        return ratingDAO.getAverageForSerie(serieId);
    }

 
    public Rating getUserRatingForFilm(int userId, int filmId) {
        return ratingDAO.getUserRatingForFilm(userId, filmId);
    }

    public Rating getUserRatingForEpisode(int userId, int episodeId) {
        return ratingDAO.getUserRatingForEpisode(userId, episodeId);
    }
}