package JStream.service;

import JStream.dao.FilmProgressDAO;
import JStream.entity.FeaturedItem;
import JStream.entity.WatchStatus;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for film progress management.
 * Uses FilmProgressDAO internally.
 */
public class FilmProgressService {

    private final FilmProgressDAO filmProgressDAO;
    private final FeaturedService featuredService;

    // ----------------- Constructor -----------------
    public FilmProgressService(FeaturedService featuredService) {
        this.featuredService = featuredService;

        try {
            // DAO uses Database.getConnection internally
            this.filmProgressDAO = new FilmProgressDAO();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize FilmProgressService: " + e.getMessage(), e);
        }
    }

    // ----------------- Get all watched films -----------------
    public List<FeaturedItem> getWatchedFilms(int userId) {
        List<FeaturedItem> films = new ArrayList<>();
        try {
            List<Integer> watchedFilmIds = filmProgressDAO.getWatchedFilmIds(userId);
            for (int filmId : watchedFilmIds) {
                FeaturedItem film = featuredService.getFilmById(filmId);
                if (film != null) films.add(film);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return films;
    }

    // ----------------- Get film watch status -----------------
    public WatchStatus getFilmStatus(int userId, int filmId) {
        try {
            return filmProgressDAO.getFilmStatus(userId, filmId);
        } catch (Exception e) {
            e.printStackTrace();
            return WatchStatus.NOT_STARTED;
        }
    }

    // ----------------- Get last watched position -----------------
    public int getLastPosition(int userId, int filmId) {
        try {
            return filmProgressDAO.getLastPosition(userId, filmId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ----------------- Check if progress exists -----------------
    public boolean exists(int userId, int filmId) {
        try {
            return filmProgressDAO.exists(userId, filmId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ----------------- Mark film in progress -----------------
    public void setInProgress(int userId, int filmId, int lastPosition) {
        try {
            filmProgressDAO.setInProgress(userId, filmId, lastPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------- Mark film completed -----------------
    public void setCompleted(int userId, int filmId, int lastPosition) {
        try {
            filmProgressDAO.setCompleted(userId, filmId, lastPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}