package JStream.service;

import JStream.dao.EpisodeProgressDAO;
import JStream.dao.FilmProgressDAO;
import JStream.entity.WatchStatus;
import JStream.utils.Database;

import java.sql.Connection;
import java.sql.SQLException;

public class FilmProgressService {

    private final FilmProgressDAO filmProgressDAO;

    public FilmProgressService() {
    	Connection conn = Database.getConnection();
        this.filmProgressDAO = new FilmProgressDAO(conn);
    }

    // ----------------- Get film watch status -----------------
    public WatchStatus getFilmStatus(int userId, int filmId) {
        return filmProgressDAO.getFilmStatus(userId, filmId);
    }

    // ----------------- Set film in progress -----------------
    public void markInProgress(int userId, int filmId, int lastPosition) {
        filmProgressDAO.setInProgress(userId, filmId, lastPosition);
    }

    // ----------------- Set film completed -----------------
    public void markCompleted(int userId, int filmId, int lastPosition) {
        filmProgressDAO.setCompleted(userId, filmId, lastPosition);
    }
    public int getLastPosition(int userId, int filmId) {
    	  try {
              return filmProgressDAO.getLastPosition(userId, filmId);
          } catch (SQLException e) {
              e.printStackTrace();
              return 0; // fallback (start from beginning)
          }
      
    }
    public boolean exists(int userId, int filmId) {
        try {
            return filmProgressDAO.exists(userId, filmId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}