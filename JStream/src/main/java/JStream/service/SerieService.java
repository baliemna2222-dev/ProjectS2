package JStream.service;

import java.util.List;

import JStream.dao.SerieDAO;
import JStream.entity.Serie;

public class SerieService {
	   private final SerieDAO serieDAO = new SerieDAO();

	   

	    public Serie getSerieById(int s) {
	        return serieDAO.getSerieById(s);
	    }
	    public List<Serie> getAllSeries(){
	        return serieDAO.getAllSeries();
	    }

	    public void addSerie(Serie serie){
	        serieDAO.addSerie(serie);
	    }

	    public void deleteSerie(int id){
	        serieDAO.deleteSerie(id);
	    }
}
