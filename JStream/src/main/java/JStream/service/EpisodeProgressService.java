package JStream.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import JStream.dao.EpisodeProgressDAO;
import JStream.entity.WatchStatus;
import JStream.utils.Database;

public class EpisodeProgressService {

    private final EpisodeProgressDAO dao;

    // No-args constructor — DAO created internally
    public EpisodeProgressService() {
    	Connection conn = Database.getConnection();
        this.dao = new EpisodeProgressDAO(conn);
    }

    // ----------------- Load all episode progress for a user -----------------
    public Map<Integer, WatchStatus> loadUserProgress(int userId) {
        return dao.getProgressForUser(userId);
    }

    // ----------------- Mark an episode as in progress -----------------
    public void markInProgress(int userId, int epId, int lastPosition) {
        dao.setInProgress(userId, epId, lastPosition);
    }

    // ----------------- Mark an episode as completed -----------------
    public void markCompleted(int userId, int epId, int lastPosition) {
        dao.setCompleted(userId, epId, lastPosition);
    } 

    // ----------------- Get status of a single episode -----------------
    public WatchStatus getEpisodeStatus(int userId, int epId) {
        return dao.getEpisodeStatus(userId, epId);
    }
    public int getEpisodeLastPosition(int userId, int epId) {
        try {
            EpisodeProgressDAO epDAO = new EpisodeProgressDAO(Database.getConnection());
            return epDAO.getLastPosition(userId, epId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

}