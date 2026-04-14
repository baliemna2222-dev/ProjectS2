package JStream.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import JStream.entity.ViewStat;
import JStream.service.DashboardService;

public class DashboardController {

    // ── FXML Stat cards ───────────────────────────────────────────────────────
    @FXML private Label totalUsersLabel;
    @FXML private Label totalFilmsLabel;
    @FXML private Label totalSeriesLabel;
    @FXML private Label totalCommentsLabel;
    @FXML private Label totalWatchSessionsLabel;
    @FXML private Label totalActiveViewersLabel;

    // ── Charts ────────────────────────────────────────────────────────────────
    @FXML private PieChart categoryPieChart;

    @FXML private BarChart<String, Number>  topFilmsBarChart;
    @FXML private CategoryAxis              filmsCategoryAxis;
    @FXML private NumberAxis                filmsNumberAxis;

    @FXML private LineChart<String, Number> signupLineChart;
    @FXML private CategoryAxis              signupDateAxis;
    @FXML private NumberAxis                signupCountAxis;

    // ── Tables ────────────────────────────────────────────────────────────────
    @FXML private TableView<ViewStat>           topFilmsTable;
    @FXML private TableColumn<ViewStat, String>  filmTitleColumn;
    @FXML private TableColumn<ViewStat, Integer> filmViewsColumn;

    @FXML private TableView<ViewStat>           topSeriesTable;
    @FXML private TableColumn<ViewStat, String>  seriesTitleColumn;
    @FXML private TableColumn<ViewStat, Integer> seriesViewsColumn;

    private final DashboardService dashboardService = new DashboardService();

    // ── Palette (applied via CSS-friendly inline style where needed) ──────────
    // These are used to color individual bars / line symbols programmatically.
    private static final String[] BAR_COLORS = {
        "#6366f1", // indigo
        "#22d3ee", // cyan
        "#a78bfa", // violet
        "#34d399", // emerald
        "#f472b6"  // pink
    };
    private static final String[] PIE_COLORS = {
        "#6366f1", "#22d3ee", "#a78bfa", "#34d399",
        "#f472b6", "#fb923c", "#facc15", "#4ade80"
    };
    private static final String LINE_COLOR      = "#22d3ee";
    private static final String LINE_SYMBOL_CSS =
        "-fx-background-color: #22d3ee, white;" +
        "-fx-background-radius: 6px;" +
        "-fx-padding: 5px;";

    // ── Init ──────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        styleCharts();
        loadDashboardStats();
    }

    // ── Global chart styling applied before data loads ────────────────────────
    private void styleCharts() {
        // ── Bar chart ──
        if (topFilmsBarChart != null) {
            topFilmsBarChart.setPrefHeight(340);
            topFilmsBarChart.setMinHeight(300);
            topFilmsBarChart.setLegendVisible(false);
            topFilmsBarChart.setVerticalGridLinesVisible(false);
            topFilmsBarChart.setHorizontalGridLinesVisible(true);
            topFilmsBarChart.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-plot-background-color: transparent;"
            );
            filmsCategoryAxis.setStyle("-fx-tick-label-fill: #94a3b8; -fx-font-size: 12px;");
            filmsNumberAxis.setStyle("-fx-tick-label-fill: #94a3b8; -fx-font-size: 12px;");
        }

        // ── Line chart ──
        if (signupLineChart != null) {
            signupLineChart.setPrefHeight(320);
            signupLineChart.setMinHeight(280);
            signupLineChart.setLegendVisible(false);
            signupLineChart.setCreateSymbols(true);
            signupLineChart.setVerticalGridLinesVisible(false);
            signupLineChart.setHorizontalGridLinesVisible(true);
            signupLineChart.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-plot-background-color: transparent;"
            );
            signupDateAxis.setStyle("-fx-tick-label-fill: #94a3b8; -fx-font-size: 12px;");
            signupCountAxis.setStyle("-fx-tick-label-fill: #94a3b8; -fx-font-size: 12px;");
        }

        // ── Pie chart ──
        if (categoryPieChart != null) {
            categoryPieChart.setPrefHeight(320);
            categoryPieChart.setMinHeight(280);
            categoryPieChart.setLegendVisible(true);
            categoryPieChart.setLabelsVisible(true);
            categoryPieChart.setStyle("-fx-background-color: transparent;");
        }
    }

    // ── Stats cards ───────────────────────────────────────────────────────────
    private void loadDashboardStats() {
        updateCard(totalUsersLabel,         dashboardService.getTotalUsers());
        updateCard(totalFilmsLabel,         dashboardService.getTotalFilms());
        updateCard(totalSeriesLabel,        dashboardService.getTotalSeries());
        updateCard(totalCommentsLabel,      dashboardService.getTotalComments());
        updateCard(totalWatchSessionsLabel, dashboardService.getTotalWatchSessions());
        updateCard(totalActiveViewersLabel, dashboardService.getDistinctWatchUsers());
        loadTables();
        loadCharts();
    }

    private void updateCard(Label label, int value) {
        if (label != null) label.setText(String.valueOf(value));
    }

    // ── Tables ────────────────────────────────────────────────────────────────
    private void loadTables() {
        if (topFilmsTable != null) {
            filmTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            filmViewsColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
            topFilmsTable.setItems(FXCollections.observableArrayList(
                dashboardService.getTopWatchedFilms(5)));
            topFilmsTable.setFixedCellSize(36);
            topFilmsTable.setPrefHeight(36 * 5 + 40);
        }
        if (topSeriesTable != null) {
            seriesTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            seriesViewsColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
            topSeriesTable.setItems(FXCollections.observableArrayList(
                dashboardService.getTopWatchedSeries(5)));
            topSeriesTable.setFixedCellSize(36);
            topSeriesTable.setPrefHeight(36 * 5 + 40);
        }
    }

    // ── Charts ────────────────────────────────────────────────────────────────
    private void loadCharts() {
        loadPieChart();
        loadBarChart();
        loadLineChart();
    }

    // ── Pie chart ─────────────────────────────────────────────────────────────
    private void loadPieChart() {
        if (categoryPieChart == null) return;

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (ViewStat s : dashboardService.getFilmCategoryDistribution())
            pieData.add(new PieChart.Data(s.getTitle(), s.getCount()));

        categoryPieChart.setData(pieData);
        categoryPieChart.setAnimated(true);
        categoryPieChart.setStartAngle(90);

        Platform.runLater(() -> {
            int i = 0;
            for (PieChart.Data d : categoryPieChart.getData()) {
                // Apply custom color to each slice
                String color = PIE_COLORS[i % PIE_COLORS.length];
                if (d.getNode() != null) {
                    d.getNode().setStyle("-fx-pie-color: " + color + ";");
                }

                // Rich tooltip
                String tooltip = String.format("%s\n%d films (%.1f%%)",
                    d.getName(),
                    (int) d.getPieValue(),
                    (d.getPieValue() / pieData.stream()
                        .mapToDouble(PieChart.Data::getPieValue).sum()) * 100
                );
                Tooltip tt = new Tooltip(tooltip);
                tt.setShowDelay(Duration.millis(100));
                tt.setStyle(
                    "-fx-background-color: #1e293b;" +
                    "-fx-text-fill: #f1f5f9;" +
                    "-fx-font-size: 12px;" +
                    "-fx-background-radius: 8;" +
                    "-fx-padding: 8 12;"
                );
                Tooltip.install(d.getNode(), tt);
                i++;
            }
        });
    }

    // ── Bar chart ─────────────────────────────────────────────────────────────
    private void loadBarChart() {
        if (topFilmsBarChart == null) return;

        List<ViewStat> topFilms = dashboardService.getTopWatchedFilms(5);

        // Axis configuration — integers only, clean upper bound
        int maxViews = topFilms.stream().mapToInt(ViewStat::getCount).max().orElse(5);
        filmsNumberAxis.setAutoRanging(false);
        filmsNumberAxis.setLowerBound(0);
        filmsNumberAxis.setUpperBound(maxViews + 1);
        filmsNumberAxis.setTickUnit(Math.max(1, maxViews / 5));
        filmsNumberAxis.setMinorTickVisible(false);
        filmsNumberAxis.setLabel("Views");

        filmsCategoryAxis.setLabel("Film");
        filmsCategoryAxis.setTickLabelRotation(15);

        // Build series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Views");

        for (ViewStat s : topFilms) {
            String shortTitle = s.getTitle().length() > 14
                ? s.getTitle().substring(0, 12) + "…"
                : s.getTitle();
            series.getData().add(new XYChart.Data<>(shortTitle, s.getCount()));
        }

        topFilmsBarChart.setAnimated(true);
        topFilmsBarChart.getData().clear();
        topFilmsBarChart.getData().add(series);
        topFilmsBarChart.setCategoryGap(28);
        topFilmsBarChart.setBarGap(0);

        // Color bars, add view-count labels above, and rich tooltips
        Platform.runLater(() -> {
            for (int i = 0; i < series.getData().size(); i++) {
                XYChart.Data<String, Number> bar = series.getData().get(i);
                int    views     = topFilms.get(i).getCount();
                String fullTitle = topFilms.get(i).getTitle();
                String color     = BAR_COLORS[i % BAR_COLORS.length];

                Node node = bar.getNode();
                if (node != null) {
                    // Rounded-top bar with custom color
                    node.setStyle(
                        "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 6 6 0 0;" +
                        "-fx-bar-fill: " + color + ";"
                    );

                    // Value label centered above bar
                    if (node instanceof StackPane pane) {
                        Label lbl = new Label(String.valueOf(views));
                        lbl.setStyle(
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: " + color + ";" +
                            "-fx-padding: 0 0 4 0;"
                        );
                        pane.getChildren().add(lbl);
                        StackPane.setAlignment(lbl, Pos.TOP_CENTER);
                        StackPane.setMargin(lbl, new Insets(-20, 0, 0, 0));
                    }

                    // Rich tooltip
                    Tooltip tt = new Tooltip(fullTitle + "\n" + views + " view" + (views != 1 ? "s" : ""));
                    tt.setShowDelay(Duration.millis(80));
                    tt.setStyle(
                        "-fx-background-color: #1e293b;" +
                        "-fx-text-fill: #f1f5f9;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12;"
                    );
                    Tooltip.install(node, tt);
                }
            }
        });
    }

    // ── Line chart ────────────────────────────────────────────────────────────
    private void loadLineChart() {
        if (signupLineChart == null) return;

        List<ViewStat> rawStats = dashboardService.getUserSignupsByDay(7);

        // Fill all 7 days (0 where no data)
        DateTimeFormatter dbFmt   = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dispFmt = DateTimeFormatter.ofPattern("MM/dd");
        Map<String, Integer> dayMap = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--)
            dayMap.put(LocalDate.now().minusDays(i).format(dbFmt), 0);
        for (ViewStat s : rawStats)
            if (dayMap.containsKey(s.getTitle()))
                dayMap.put(s.getTitle(), s.getCount());

        // Axis — integers only
        int maxSignups = dayMap.values().stream().mapToInt(v -> v).max().orElse(5);
        signupCountAxis.setAutoRanging(false);
        signupCountAxis.setLowerBound(0);
        signupCountAxis.setUpperBound(maxSignups + 1);
        signupCountAxis.setTickUnit(Math.max(1, maxSignups / 5));
        signupCountAxis.setMinorTickVisible(false);
        signupCountAxis.setLabel("Sign-ups");

        signupDateAxis.setLabel("Date");
        signupDateAxis.setTickLabelRotation(20);

        // Build series
        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        lineSeries.setName("Sign-ups");
        for (Map.Entry<String, Integer> e : dayMap.entrySet()) {
            String displayDate = LocalDate.parse(e.getKey(), dbFmt).format(dispFmt);
            lineSeries.getData().add(new XYChart.Data<>(displayDate, e.getValue()));
        }

        signupLineChart.setAnimated(true);
        signupLineChart.getData().clear();
        signupLineChart.getData().add(lineSeries);

        // Color the line and style symbols + tooltips
        Platform.runLater(() -> {
            // Style the line stroke
            Node lineNode = signupLineChart.lookup(".chart-series-line");
            if (lineNode != null) {
                lineNode.setStyle(
                    "-fx-stroke: " + LINE_COLOR + ";" +
                    "-fx-stroke-width: 2.5px;"
                );
            }

            for (XYChart.Data<String, Number> d : lineSeries.getData()) {
                Node symbol = d.getNode();
                if (symbol != null) {
                    // Styled dot
                    symbol.setStyle(LINE_SYMBOL_CSS);

                    // Rich tooltip
                    String tooltipText = d.getXValue() + "\n" +
                        d.getYValue() + " sign-up" + (d.getYValue().intValue() != 1 ? "s" : "");
                    Tooltip tt = new Tooltip(tooltipText);
                    tt.setShowDelay(Duration.millis(80));
                    tt.setStyle(
                        "-fx-background-color: #1e293b;" +
                        "-fx-text-fill: #f1f5f9;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12;"
                    );
                    Tooltip.install(symbol, tt);
                }
            }
        });
    }
}