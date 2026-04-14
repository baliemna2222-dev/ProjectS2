package JStream.service;

import JStream.dao.ActorDAO;
import JStream.entity.Actor;

import java.util.List;

public class ActorService {

    private final ActorDAO actorDAO = new ActorDAO();

    public List<Actor> getActorsByFilm(int filmId) {
        return actorDAO.getActorsByFilm(filmId);
    }

    public List<Actor> getActorsBySerie(int serieId) {
        return actorDAO.getActorsBySerie(serieId);
    }
} 