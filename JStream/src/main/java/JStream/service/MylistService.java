package JStream.service;

import JStream.dao.MylistDAO;
import JStream.utils.Database;

public class MylistService {

	    private MylistDAO dao;

	    public MylistService() {
	        dao = new MylistDAO(Database.getConnection());
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
	}