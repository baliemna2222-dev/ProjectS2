package JStream.service;

import JStream.dao.SerieDAO;
import JStream.entity.Serie;

public class SerieService {
	   private final SerieDAO serieDAO = new SerieDAO();

	   

	    public Serie getSerieById(int s) {
	        return serieDAO.getSerieById(s);
	    }
}
