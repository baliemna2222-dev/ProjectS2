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
	        serieDAO.insertSerie(serie);
	    }
	    public void updateSerie(Serie serie){
	        serieDAO.updateSerie( serie);
	    }
	    public void deleteSerie(int id){
	        serieDAO.deleteSerie(id);
	    }
}
