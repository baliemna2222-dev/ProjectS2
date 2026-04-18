package JStream.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.util.Duration;

import JStream.entity.ViewStat;
import JStream.service.DashboardService;

public class DashboardController {
    @FXML private Label totalUsersLabel;
    @FXML private Label totalFilmsLabel;
    @FXML private Label totalSeriesLabel;
    @FXML private Label totalCommentsLabel;
    @FXML private Label totalWatchSessionsLabel;
    @FXML private Label totalActiveViewersLabel;
    @FXML private PieChart                       categoryPieChart;
    @FXML private BarChart<String, Number>        topFilmsBarChart;
    @FXML private CategoryAxis                   filmsCategoryAxis;
    @FXML private NumberAxis                     filmsNumberAxis;
    @FXML private LineChart<String, Number>       signupLineChart;
    @FXML private CategoryAxis                   signupDateAxis;
    @FXML private NumberAxis                     signupCountAxis;
    @FXML private TableView<ViewStat>            topFilmsTable;
    
    @FXML private TableColumn<ViewStat, String>  filmTitleColumn;
    @FXML private TableColumn<ViewStat, Integer> filmViewsColumn;
    
    @FXML private TableView<ViewStat>            topSeriesTable;
    @FXML private TableColumn<ViewStat, String>  seriesTitleColumn;
    @FXML private TableColumn<ViewStat, Integer> seriesViewsColumn;
   private static final String BG_CARD      = "#0b1228";
    private static final String BG_CARD2     = "#0d1530";
    @FXML private VBox chartsContainer;
    private final DashboardService dashboardService = new DashboardService();

    private static final String BORDER_DIM   = "rgba(99,102,241,0.18)";
    private static final String TEXT_HINT    = "#94a3b8";
    private static final String[] PALETTE = {
        "#6366f1",  // indigo
        "#22d3ee",  // cyan
        "#a78bfa",  // violet
        "#34d399",  // emerald
        "#f472b6",  // pink
        "#fb923c",  // orange
        "#facc15",  // amber
        "#4ade80",  // lime
        "#818cf8",  // periwinkle
        "#f87171"   // rose
    };

    private static final String LINE_STROKE  = "#22d3ee";
    @FXML
    public void initialize() {
        styleAllCharts();
        loadDashboardStats();
        animateChartContainerReveal();
    }
    private void styleAllCharts() {
        styleBarChart();
        styleLineChart();
        stylePieChart();
    }

    private void styleBarChart() {
        if (topFilmsBarChart == null) return;

        topFilmsBarChart.setPrefHeight(440);
        topFilmsBarChart.setMinHeight(400);
        topFilmsBarChart.setMaxHeight(Double.MAX_VALUE);
        topFilmsBarChart.setLegendVisible(false);
        topFilmsBarChart.setVerticalGridLinesVisible(false);
        topFilmsBarChart.setHorizontalGridLinesVisible(true);
        topFilmsBarChart.setAnimated(true);
        topFilmsBarChart.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 20 24 16 24;"
        );

        filmsCategoryAxis.setStyle(
            "-fx-tick-label-fill: " + TEXT_HINT + ";" +
            "-fx-font-size: 12px;" +
            "-fx-tick-label-font-family: 'Segoe UI';"
        );
        filmsNumberAxis.setStyle(
            "-fx-tick-label-fill: " + TEXT_HINT + ";" +
            "-fx-font-size: 12px;"
        );
        filmsCategoryAxis.setTickLabelRotation(12);
    }

    private void styleLineChart() {
        if (signupLineChart == null) return;

        signupLineChart.setPrefHeight(420);
        signupLineChart.setMinHeight(380);
        signupLineChart.setMaxHeight(Double.MAX_VALUE);
        signupLineChart.setLegendVisible(false);
        signupLineChart.setCreateSymbols(true);
        signupLineChart.setVerticalGridLinesVisible(false);
        signupLineChart.setHorizontalGridLinesVisible(true);
        signupLineChart.setAnimated(true);
        signupLineChart.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 20 24 16 24;"
        );
        signupDateAxis.setStyle(
            "-fx-tick-label-fill: " + TEXT_HINT + ";" +
            "-fx-font-size: 12px;"
        );
        signupCountAxis.setStyle(
            "-fx-tick-label-fill: " + TEXT_HINT + ";" +
            "-fx-font-size: 12px;"
        );
        signupDateAxis.setTickLabelRotation(15);
    }
    private void stylePieChart() {
        if (categoryPieChart == null) return;

        categoryPieChart.setPrefHeight(440);
        categoryPieChart.setMinHeight(400);
        categoryPieChart.setMaxHeight(Double.MAX_VALUE);
        categoryPieChart.setLegendVisible(true);
        categoryPieChart.setLabelsVisible(true);
        categoryPieChart.setAnimated(true);
        categoryPieChart.setStartAngle(90);
        categoryPieChart.setStyle( "-fx-background-color: " + BG_CARD + ";" +"-fx-background-radius: 16;" +  "-fx-padding: 20 24 16 24;");
         
    }

    private void loadDashboardStats() {
        animateCount(totalUsersLabel,         dashboardService.getTotalUsers());
        animateCount(totalFilmsLabel,         dashboardService.getTotalFilms());
        animateCount(totalSeriesLabel,        dashboardService.getTotalSeries());
        animateCount(totalCommentsLabel,      dashboardService.getTotalComments());
        animateCount(totalWatchSessionsLabel, dashboardService.getTotalWatchSessions());
        animateCount(totalActiveViewersLabel, dashboardService.getDistinctWatchUsers());

        loadTables();
        loadCharts();
    }
    private void animateCount(Label label, int target) {
        if (label == null) return;
        label.setText("0");

        int steps    = 40;
        int stepMs   = 30;
        final int[]  current = {0};
        Timeline tl = new Timeline();
        for (int i = 1; i <= steps; i++) {
            final int step = i;
            tl.getKeyFrames().add(new KeyFrame(Duration.millis((long) step * stepMs), e -> {
                int val = (int) Math.round(target * easeOut((double) step / steps));
                current[0] = val;
                label.setText(formatNumber(val));
            }));
        }
       
        tl.getKeyFrames().add(new KeyFrame(Duration.millis(steps * stepMs + 50L),  // Ensure exact final value
            e -> label.setText(formatNumber(target))));
        tl.play();
    }

   private double easeOut(double t) { return 1 - Math.pow(1 - t, 3); }

    private String formatNumber(int n) {
        if (n >= 1_000_000) return String.format("%.1fM", n / 1_000_000.0);
        if (n >= 1_000)     return String.format("%.1fK", n / 1_000.0);
        return String.valueOf(n);
    }
    private void loadTables() {
        styleTable(topFilmsTable);
        styleTable(topSeriesTable);

        if (topFilmsTable != null) {
            filmTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            filmViewsColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
            topFilmsTable.setItems(FXCollections.observableArrayList(
                dashboardService.getTopWatchedFilms(5)));
            topFilmsTable.setFixedCellSize(38);
            topFilmsTable.setPrefHeight(38 * 5 + 44);
        }
        if (topSeriesTable != null) {
            seriesTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            seriesViewsColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
            topSeriesTable.setItems(FXCollections.observableArrayList(
                dashboardService.getTopWatchedSeries(5)));
            topSeriesTable.setFixedCellSize(38);
            topSeriesTable.setPrefHeight(38 * 5 + 44);
        }
    }
    private void styleTable(TableView<ViewStat> table) {
        if (table == null) return;
        table.setStyle(  "-fx-background-color: " + BG_CARD2 + ";" +  "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER_DIM + ";" + "-fx-border-radius: 12;" +
            "-fx-table-cell-border-color: rgba(255,255,255,0.05);"
        );
       
        table.getColumns().forEach(col ->  col.setStyle("-fx-text-fill: " + TEXT_HINT + "; -fx-font-size: 12px;"));         
    }

    private void loadCharts() {
        loadBarChart();
        loadLineChart();
        loadPieChart();
    }
    private void loadBarChart() {
        if (topFilmsBarChart == null) return;

        List<ViewStat> topFilms = dashboardService.getTopWatchedFilms(8);

        // Axis setup
        int maxViews = topFilms.stream().mapToInt(ViewStat::getCount).max().orElse(5);
        filmsNumberAxis.setAutoRanging(false);
        filmsNumberAxis.setLowerBound(0);
        filmsNumberAxis.setUpperBound(roundUp(maxViews));
        filmsNumberAxis.setTickUnit(Math.max(1, roundUp(maxViews) / 6.0));
        filmsNumberAxis.setMinorTickVisible(false);
        filmsNumberAxis.setLabel("Views");
        filmsCategoryAxis.setLabel("");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Views");
        for (ViewStat s : topFilms) {
            String label = s.getTitle().length() > 16
                ? s.getTitle().substring(0, 14) + "…"
                : s.getTitle();
            series.getData().add(new XYChart.Data<>(label, s.getCount()));
        }
        topFilmsBarChart.getData().clear();
        topFilmsBarChart.getData().add(series);
        topFilmsBarChart.setCategoryGap(22);
        topFilmsBarChart.setBarGap(0);

        Platform.runLater(() -> decorateBars(series, topFilms));
    }
    private void decorateBars(XYChart.Series<String, Number> series, List<ViewStat> source) {
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<String, Number> bar = series.getData().get(i);
            if (bar.getNode() == null) continue;

            String color     = PALETTE[i % PALETTE.length];
            int    views     = source.get(i).getCount();
            String fullTitle = source.get(i).getTitle();
            bar.getNode().setStyle(
                "-fx-background-color: linear-gradient(to bottom, " + color + " 0%, " +
                adjustAlpha(color, 0.55) + " 100%);" +
                "-fx-background-radius: 8 8 2 2;" +
                "-fx-background-insets: 0 2 0 2;"
            );
            TranslateTransition rise = new TranslateTransition(Duration.millis(500 + i * 60L), bar.getNode());
            rise.setFromY(30); rise.setToY(0);
            FadeTransition fade = new FadeTransition(Duration.millis(400 + i * 60L), bar.getNode());
            fade.setFromValue(0); fade.setToValue(1);
            new ParallelTransition(rise, fade).play();

            if (bar.getNode() instanceof StackPane pane) {
                Label lbl = new Label(views + (views == 1 ? " view" : " views"));
                lbl.setStyle( "-fx-text-fill: " + color + ";" + "-fx-font-size: 11px;" +"-fx-font-weight: bold;");
                
                pane.getChildren().add(lbl);
                StackPane.setAlignment(lbl, Pos.TOP_CENTER);
                StackPane.setMargin(lbl, new Insets(-22, 0, 0, 0));
                pane.setOnMouseEntered(e -> pane.setStyle(pane.getStyle() +"-fx-effect: dropshadow(gaussian," + color + ",18,0.5,0,0);")); //hover glow effect
                    
                pane.setOnMouseExited(e -> pane.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, " + color + " 0%, " +
                    adjustAlpha(color, 0.55) + " 100%);" +
                    "-fx-background-radius: 8 8 2 2;" +
                    "-fx-background-insets: 0 2 0 2;"
                ));
            }
            installTooltip(bar.getNode(),
                fullTitle + "\n" + views + (views == 1 ? " view" : " views"), color);
        }
    }
    private void loadLineChart() {
        if (signupLineChart == null) return;

        List<ViewStat>       rawStats = dashboardService.getUserSignupsByDay(14); // 2 weeks
        DateTimeFormatter    dbFmt    = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter    dispFmt  = DateTimeFormatter.ofPattern("MM/dd");

        // Fill 14 days, zeroing missing entries
        Map<String, Integer> dayMap = new LinkedHashMap<>();
        for (int i = 13; i >= 0; i--)
            dayMap.put(LocalDate.now().minusDays(i).format(dbFmt), 0);
        for (ViewStat s : rawStats)
            if (dayMap.containsKey(s.getTitle()))
                dayMap.put(s.getTitle(), s.getCount());

        int maxSig = dayMap.values().stream().mapToInt(v -> v).max().orElse(5);
        signupCountAxis.setAutoRanging(false);
        signupCountAxis.setLowerBound(0);
        signupCountAxis.setUpperBound(roundUp(maxSig));
        signupCountAxis.setTickUnit(Math.max(1, roundUp(maxSig) / 6.0));
        signupCountAxis.setMinorTickVisible(false);
        signupCountAxis.setLabel("Sign-ups");
        signupDateAxis.setLabel("Date");

        XYChart.Series<String, Number> line = new XYChart.Series<>();
        line.setName("Sign-ups");
        for (Map.Entry<String, Integer> e : dayMap.entrySet()) {
            String disp = LocalDate.parse(e.getKey(), dbFmt).format(dispFmt);
            line.getData().add(new XYChart.Data<>(disp, e.getValue()));
        }

        signupLineChart.getData().clear();
        signupLineChart.getData().add(line);

        Platform.runLater(() -> decorateLine(line));
    }

    private void decorateLine(XYChart.Series<String, Number> line) {
        Node lineNode = signupLineChart.lookup(".chart-series-line");
        if (lineNode != null) {
            lineNode.setStyle(
                "-fx-stroke: " + LINE_STROKE + ";" +
                "-fx-stroke-width: 2.8px;" +
                "-fx-effect: dropshadow(gaussian," + LINE_STROKE + ",10,0.4,0,0);"
            );
        }

        // Symbols + tooltips
        for (int i = 0; i < line.getData().size(); i++) {
            XYChart.Data<String, Number> pt = line.getData().get(i);
            Node sym = pt.getNode();
            if (sym == null) continue;

            sym.setStyle(
                "-fx-background-color: " + LINE_STROKE + ", #07091a;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 5px;" +
                "-fx-effect: dropshadow(gaussian," + LINE_STROKE + ",8,0.5,0,0);"
            );
            sym.setScaleX(0); sym.setScaleY(0);
            ScaleTransition pop = new ScaleTransition(Duration.millis(300 + i * 40L), sym);
            pop.setToX(1); pop.setToY(1);
            Interpolator.SPLINE(0.25, 0.1, 0.25, 1.0); 
            PauseTransition delay = new PauseTransition(Duration.millis(i * 55L));
            delay.setOnFinished(e -> pop.play());
            delay.play();
            sym.setOnMouseEntered(e -> {
                ScaleTransition grow = new ScaleTransition(Duration.millis(150), sym);
                grow.setToX(1.6); grow.setToY(1.6); grow.play();
            });
            sym.setOnMouseExited(e -> {  ScaleTransition shrink = new ScaleTransition(Duration.millis(150), sym);
                shrink.setToX(1.0); shrink.setToY(1.0); shrink.play();
            });
              
            int val = pt.getYValue().intValue();
            installTooltip(sym,
                pt.getXValue() + "\n" + val + " sign-up" + (val != 1 ? "s" : ""),
                LINE_STROKE);
        }
    }
   private void loadPieChart() {
        if (categoryPieChart == null) return;

        List<ViewStat> dist = dashboardService.getFilmCategoryDistribution();
        double total = dist.stream().mapToInt(ViewStat::getCount).sum();

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        for (ViewStat s : dist)
            data.add(new PieChart.Data(s.getTitle(), s.getCount()));

        categoryPieChart.setData(data);

        Platform.runLater(() -> decoratePie(data, total));
    }

    private void decoratePie(ObservableList<PieChart.Data> data, double total) {
        for (int i = 0; i < data.size(); i++) {
            PieChart.Data slice = data.get(i);
            if (slice.getNode() == null) continue;

            String color  = PALETTE[i % PALETTE.length];
            double pct    = total > 0 ? (slice.getPieValue() / total) * 100 : 0;
            slice.getNode().setStyle("-fx-pie-color: " + color + ";");
            slice.getNode().setOnMouseEntered(e -> {slice.getNode().setStyle(  "-fx-pie-color: " + color + ";" +
                    "-fx-effect: dropshadow(gaussian," + color + ",20,0.6,0,0);"
                );
           
                ScaleTransition grow = new ScaleTransition(Duration.millis(180), slice.getNode());
                grow.setToX(1.06); grow.setToY(1.06); grow.play();
            });
            slice.getNode().setOnMouseExited(e -> {
                slice.getNode().setStyle("-fx-pie-color: " + color + ";");
                ScaleTransition shrink = new ScaleTransition(Duration.millis(180), slice.getNode());
                shrink.setToX(1.0); shrink.setToY(1.0); shrink.play();
            });
            installTooltip(slice.getNode(),
                slice.getName() + "\n" +
                (int) slice.getPieValue() + " film" + (slice.getPieValue() != 1 ? "s" : "") +
                String.format("  (%.1f%%)", pct),
                color
            );
        }
        Platform.runLater(() -> {
            for (Node n : categoryPieChart.lookupAll(".chart-legend-item")) {
                if (n instanceof Label lbl) {
                    lbl.setStyle(  "-fx-text-fill: " + TEXT_HINT + ";" +
                        "-fx-font-size: 12px;"
                    );
                }
            }
        });
    }
    private void animateChartContainerReveal() {
        if (chartsContainer == null) return;
        List<Node> children = chartsContainer.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            child.setOpacity(0);
            child.setTranslateY(32);
            PauseTransition     pause = new PauseTransition(Duration.millis(i * 120L));
            FadeTransition      fade  = new FadeTransition(Duration.millis(500), child);
            TranslateTransition slide = new TranslateTransition(Duration.millis(500), child);
            fade.setToValue(1);
            slide.setToY(0);
            slide.setInterpolator(Interpolator.EASE_OUT);

            pause.setOnFinished(e -> new ParallelTransition(fade, slide).play());
            pause.play();
        }
    }
//helper
    private void installTooltip(Node node, String text, String accent) {
        if (node == null) return;
        Tooltip tt = new Tooltip(text);
        tt.setShowDelay(Duration.millis(60));
        tt.setHideDelay(Duration.millis(200));
        tt.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        tt.setStyle( "-fx-background-color: #111827;" +
            "-fx-text-fill: #e2e8f0;" +
            "-fx-font-size: 12px;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + accent + ";" +
            "-fx-border-width: 0 0 0 3;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 10 14 10 14;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.55),16,0,0,4);"
        );
           
        Tooltip.install(node, tt);
    }

    private String adjustAlpha(String hex, double factor) {
        try {
            Color c = Color.web(hex);
            int r = (int) Math.round(c.getRed()   * 255 * factor);
            int g = (int) Math.round(c.getGreen() * 255 * factor);
            int b = (int) Math.round(c.getBlue()  * 255 * factor);
            return String.format("#%02x%02x%02x", r, g, b);
        } catch (Exception e) {
            return hex;
        }
    }
    private int roundUp(int value) {
        if (value <= 0) return 5;
        if (value <= 10)  return (int) (Math.ceil(value / 2.0)  * 2);
        if (value <= 50)  return (int) (Math.ceil(value / 5.0)  * 5);
        if (value <= 200) return (int) (Math.ceil(value / 10.0) * 10);
        if (value <= 500) return (int) (Math.ceil(value / 25.0) * 25);
        return (int) (Math.ceil(value / 50.0) * 50);
    }
}