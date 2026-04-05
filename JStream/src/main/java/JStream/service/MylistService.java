package JStream.service;

import java.util.List;

import JStream.dao.MylistDAO;
import JStream.entity.FeaturedItem;

public class MylistService {

    private final MylistDAO dao;

    public MylistService() {
        dao = new MylistDAO(); // No connection passed; DAO handles it internally
    }

    public boolean addItem(int userId, int filmId, int serieId) {
        return dao.addToList(userId, filmId, serieId);
    }

    public boolean isInList(int userId, int filmId, int serieId) {
        return dao.isInList(userId, filmId, serieId);
    }

    public boolean removeItem(int userId, int filmId, int serieId) {
        return dao.removeItem(userId, filmId, serieId);
    }

    public List<FeaturedItem> getUserList(int userId) {
        return dao.getItemsByUser(userId);
    }
}