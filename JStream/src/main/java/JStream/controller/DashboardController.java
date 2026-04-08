package JStream.controller;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import JStream.entity.ViewStat;
import JStream.service.DashboardService;

public class DashboardController {

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label totalFilmsLabel;

    @FXML
    private Label totalSeriesLabel;

    @FXML
    private Label totalCommentsLabel;

    @FXML
    private Label totalWatchSessionsLabel;

    @FXML
    private Label totalActiveViewersLabel;

    @FXML
    private TableView<ViewStat> topFilmsTable;

    @FXML
    private TableColumn<ViewStat, String> filmTitleColumn;

    @FXML
    private TableColumn<ViewStat, Integer> filmViewsColumn;

    @FXML
    private TableView<ViewStat> topSeriesTable;

    @FXML
    private TableColumn<ViewStat, String> seriesTitleColumn;

    @FXML
    private TableColumn<ViewStat, Integer> seriesViewsColumn;

    @FXML
    private PieChart categoryPieChart;

    @FXML
    private BarChart<String, Number> topFilmsBarChart;

    @FXML
    private CategoryAxis filmsCategoryAxis;

    @FXML
    private NumberAxis filmsNumberAxis;

    @FXML
    private LineChart<String, Number> signupLineChart;

    @FXML
    private CategoryAxis signupDateAxis;

    @FXML
    private NumberAxis signupCountAxis;

    private final DashboardService dashboardService = new DashboardService();

    @FXML
    public void initialize() {
        setupTables();
        loadDashboardStats();
    }

    private void setupTables() {
        if (filmTitleColumn != null) {
            filmTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        }
        if (filmViewsColumn != null) {
            filmViewsColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        }
        if (seriesTitleColumn != null) {
            seriesTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        }
        if (seriesViewsColumn != null) {
            seriesViewsColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        }
    }

    private void loadDashboardStats() {
        int users = dashboardService.getTotalUsers();
        int films = dashboardService.getTotalFilms();
        int series = dashboardService.getTotalSeries();
        int comments = dashboardService.getTotalComments();
        int sessions = dashboardService.getTotalWatchSessions();
        int activeUsers = dashboardService.getDistinctWatchUsers();

        updateCard(totalUsersLabel, users);
        updateCard(totalFilmsLabel, films);
        updateCard(totalSeriesLabel, series);
        updateCard(totalCommentsLabel, comments);
        updateCard(totalWatchSessionsLabel, sessions);
        updateCard(totalActiveViewersLabel, activeUsers);

        loadTopWatched();
        loadCharts();
    }

    private void loadCharts() {
        // Pie chart: distribution des films par catégorie
        java.util.List<ViewStat> categories = dashboardService.getFilmCategoryDistribution();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (ViewStat s : categories) {
            pieData.add(new PieChart.Data(s.getTitle(), s.getCount()));
        }
        if (categoryPieChart != null) {
            categoryPieChart.setData(pieData);
            categoryPieChart.setTitle("Film Distribution by Category");
        }

        // Bar chart: Top 5 most watched films
        java.util.List<ViewStat> topFilms = dashboardService.getTopWatchedFilms(5);
        if (topFilmsBarChart != null) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Views");
            for (ViewStat s : topFilms) {
                series.getData().add(new XYChart.Data<>(s.getTitle(), s.getCount()));
            }
            topFilmsBarChart.getData().clear();
            topFilmsBarChart.getData().add(series);
            topFilmsBarChart.setTitle("Top 5 Most Watched Films");
        }

        // Line chart: number of registrations per day (last 7 days)
        java.util.List<ViewStat> signupStats = dashboardService.getUserSignupsByDay(7);
        if (signupLineChart != null) {
            XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
            lineSeries.setName("Sign-ups");
            for (ViewStat s : signupStats) {
                lineSeries.getData().add(new XYChart.Data<>(s.getTitle(), s.getCount()));
            }
            signupLineChart.getData().clear();
            signupLineChart.getData().add(lineSeries);
            signupLineChart.setTitle("Registrations per Day (7 days)");
        }
    }

    private void loadTopWatched() {
        List<ViewStat> topFilms = dashboardService.getTopWatchedFilms(5);
        List<ViewStat> topSeries = dashboardService.getTopWatchedSeries(5);

        if (topFilmsTable != null) {
            topFilmsTable.getItems().setAll(topFilms);
        }
        if (topSeriesTable != null) {
            topSeriesTable.getItems().setAll(topSeries);
        }
    }

    private void updateCard(Label label, int value) {
        if (label != null) {
            label.setText(String.valueOf(value));
        }
    }
}