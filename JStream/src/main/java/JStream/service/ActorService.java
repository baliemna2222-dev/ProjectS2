package JStream.service;

import JStream.dao.ActorDAO;
import JStream.entity.Actor;

import java.util.List;

public class ActorService {

    private final ActorDAO actorDAO = new ActorDAO();

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<Actor> getActorsByFilm(int filmId) {
        return actorDAO.getActorsByFilm(filmId);
    }

    public List<Actor> getActorsBySerie(int serieId) {
        return actorDAO.getActorsBySerie(serieId);
    }

    public List<Actor> searchActorsByName(String query) {
        return actorDAO.searchActorsByName(query);
    }

    public Actor getActorById(int actorId) {
        return actorDAO.getActorById(actorId);
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public Actor createActor(String name, String photoUrl) {
        Actor actor = new Actor(0, name, photoUrl, null);
        int id = actorDAO.insertActor(actor);
        actor.setActorId(id);
        return actor;
    }

    public boolean updateActor(Actor actor) {
        return actorDAO.updateActor(actor);
    }

    public boolean deleteActor(int actorId) {
        return actorDAO.deleteActor(actorId);
    }

    // ── FILM links ────────────────────────────────────────────────────────────

    public boolean linkToFilm(int filmId, int actorId, String roleName) {
        return actorDAO.linkToFilm(filmId, actorId, roleName);
    }

    public boolean updateFilmRole(int filmId, int actorId, String roleName) {
        return actorDAO.updateFilmRole(filmId, actorId, roleName);
    }

    public boolean unlinkFromFilm(int filmId, int actorId) {
        return actorDAO.unlinkFromFilm(filmId, actorId);
    }

    // ── SERIE links ───────────────────────────────────────────────────────────

    public boolean linkToSerie(int serieId, int actorId, String roleName) {
        return actorDAO.linkToSerie(serieId, actorId, roleName);
    }

    public boolean updateSerieRole(int serieId, int actorId, String roleName) {
        return actorDAO.updateSerieRole(serieId, actorId, roleName);
    }

    public boolean unlinkFromSerie(int serieId, int actorId) {
        return actorDAO.unlinkFromSerie(serieId, actorId);
    }
}