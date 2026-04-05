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
        
        this.dao = new EpisodeProgressDAO();
    }

    // ----------------- Load all episode progress for a user -----------------
    public Map<Integer, WatchStatus> loadUserProgress(int userId) {
        try {
            return dao.getProgressForUser(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(); // safe fallback
        }
    }

    // ----------------- Mark an episode as in progress -----------------
    public void markInProgress(int userId, int epId, int lastPosition) {
        try {
            dao.setInProgress(userId, epId, lastPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------- Mark an episode as completed -----------------
    public void markCompleted(int userId, int epId, int lastPosition) {
        try {
            dao.setCompleted(userId, epId, lastPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------- Get status of a single episode -----------------
    public WatchStatus getEpisodeStatus(int userId, int epId) {
        try {
            return dao.getEpisodeStatus(userId, epId);
        } catch (Exception e) {
            e.printStackTrace();
            return WatchStatus.NOT_STARTED;
        }
    }

    // ----------------- Get last position -----------------
    public int getEpisodeLastPosition(int userId, int epId) {
        try {
            return dao.getLastPosition(userId, epId); // ✅ reuse same DAO
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}