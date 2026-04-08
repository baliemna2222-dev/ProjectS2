package JStream.service;

import JStream.dao.DashboardDAO;

public class DashboardService {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public int getTotalUsers() {
        return dashboardDAO.countUsers();
    }

    public int getTotalFilms() {
        return dashboardDAO.countFilms();
    }

    public java.util.List<JStream.entity.ViewStat> getFilmCategoryDistribution() {
        return dashboardDAO.getFilmCategoryDistribution();
    }

    public java.util.List<JStream.entity.ViewStat> getUserSignupsByDay(int days) {
        return dashboardDAO.getUserSignupsByDay(days);
    }

    public int getTotalSeries() {
        return dashboardDAO.countSeries();
    }

    public int getTotalComments() {
        return dashboardDAO.countComments();
    }

    public int getTotalWatchSessions() {
        return dashboardDAO.countWatchHistory();
    }

    public int getDistinctWatchUsers() {
        return dashboardDAO.countDistinctWatchUsers();
    }

    public java.util.List<JStream.entity.ViewStat> getTopWatchedFilms(int limit) {
        return dashboardDAO.getTopWatchedFilms(limit);
    }

    public java.util.List<JStream.entity.ViewStat> getTopWatchedSeries(int limit) {
        return dashboardDAO.getTopWatchedSeries(limit);
    }
}