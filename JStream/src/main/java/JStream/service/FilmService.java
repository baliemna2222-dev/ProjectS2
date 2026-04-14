package JStream.service;

import java.util.ArrayList;
import java.util.List;
import JStream.dao.FilmDAO;
import JStream.entity.Film;

public class FilmService {

    private FilmDAO filmDAO;

    public FilmService() {
        this.filmDAO = new FilmDAO();
    }

    public List<Film> getAllFilms() {
        return filmDAO.getAllFilms();
    }

    public void addFilm(Film film) {
        filmDAO.insertFilm(film);
    }

    public boolean updateFilm(Film film) {
        return filmDAO.updateFilm(film);
    }

    public void deleteFilm(int id) {
        filmDAO.deleteFilm(id);
    }

    public List<Film> searchFilms(String keyword) {
        return filmDAO.searchFilms(keyword);
    }
    public List<String> validateFilm(Film film) {

        List<String> errors = new ArrayList<>();

        // ───────────────────────── TITLE ─────────────────────────
        if (film.getTitle() == null || film.getTitle().trim().isEmpty()) {
            errors.add("Title is required");
        } else if (film.getTitle().length() < 2) {
            errors.add("Title must be at least 2 characters");
        }

        // ───────────────────────── DIRECTOR ─────────────────────────
        if (film.getDirector() == null || film.getDirector().trim().isEmpty()) {
            errors.add("Director is required");
        }

        // ───────────────────────── SYNOPSIS ─────────────────────────
        if (film.getSynopsis() == null || film.getSynopsis().trim().isEmpty()) {
            errors.add("Synopsis is required");
        } else if (film.getSynopsis().length() < 10) {
            errors.add("Synopsis must be at least 10 characters");
        }

        // ───────────────────────── CASTING ─────────────────────────
        if (film.getCasting() == null || film.getCasting().trim().isEmpty()) {
            errors.add("Casting is required");
        }

        // ───────────────────────── DURATION ─────────────────────────
        if (film.getDuration() <= 0) {
            errors.add("Duration must be greater than 0");
        } else if (film.getDuration() > 600) {
            errors.add("Duration is too long");
        }

        // ───────────────────────── AGE RATING ─────────────────────────
        if (film.getAge_rating() == null || film.getAge_rating().trim().isEmpty()) {
            errors.add("Age rating is required");
        }

        // ───────────────────────── RATING ─────────────────────────
        if (film.getRating() < 0 || film.getRating() > 10) {
            errors.add("Rating must be between 0 and 10");
        }

        // ───────────────────────── VIDEO ─────────────────────────
        if (film.getVideo_url() == null || film.getVideo_url().trim().isEmpty()) {
            errors.add("Video URL is required");
        }

        // ───────────────────────── TRAILER ─────────────────────────
        if (film.getTrailer_url() == null || film.getTrailer_url().trim().isEmpty()) {
            errors.add("Trailer URL is required");
        }

        // ───────────────────────── IMAGES ─────────────────────────
        if (film.getImage_url() == null || film.getImage_url().trim().isEmpty()) {
            errors.add("Cover image is required");
        }

        if (film.getPoster_url() == null || film.getPoster_url().trim().isEmpty()) {
            errors.add("Poster (horizontal) is required");
        }

        if (film.getPosterV_url() == null || film.getPosterV_url().trim().isEmpty()) {
            errors.add("Poster (vertical) is required");
        }

        if (film.getTitle_image_url() == null || film.getTitle_image_url().trim().isEmpty()) {
            errors.add("Title image is required");
        }

        // ───────────────────────── RELEASE DATE ─────────────────────────
        if (film.getRelease_date() == null) {
            errors.add("Release date is required");
        }

        // ───────────────────────── CATEGORIES ─────────────────────────
        if (film.getCategories() == null || film.getCategories().isEmpty()) {
            errors.add("At least one category must be selected");
        }

        // ───────────────────────── BUSINESS RULE (OPTIONAL) ─────────────────────────
        if (film.getTitle() != null && film.getTitle().length() > 100) {
            errors.add("Title is too long (max 100 characters)");
        }

        return errors;
    }
}