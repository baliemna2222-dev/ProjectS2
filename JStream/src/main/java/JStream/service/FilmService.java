package JStream.service;

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
        filmDAO.addFilm(film);
    }

    public void deleteFilm(int id) {
        filmDAO.deleteFilm(id);
    }
}