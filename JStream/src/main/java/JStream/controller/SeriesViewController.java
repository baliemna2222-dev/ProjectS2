package JStream.controller;

import java.io.IOException;
import java.util.*;
import JStream.entity.Category;
import JStream.entity.FeaturedItem;
import JStream.service.FeaturedService;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class SeriesViewController {

    @FXML
    private VBox categoryContainer; // Container for carousels, filters, results

    private HBox seriesFilterContainer;
    private VBox seriesResultsContainer;
    private final FeaturedService featuredService = new FeaturedService();
    private final Set<String> selectedSeriesCategories = new HashSet<>();
    private final Set<Integer> selectedSeriesYears = new HashSet<>();

    @FXML
    private void initialize() {
        loadSeriesCarousels();
    }

    // ---------------- LOAD CAROUSELS ----------------
    private void loadSeriesCarousels() {
        try {
            List<Category> categories = featuredService.getAllCategories();

            // Load filter bar
            loadSeriesFilterCarousel(categories);

            // Normal categories (series only)
            for (Category category : categories) {
                List<FeaturedItem> items = new ArrayList<>(featuredService.getItemsByCategory(category.getName()));
                items.removeIf(item -> !item.getType().equalsIgnoreCase("serie"));

                if (!items.isEmpty()) {
                    Node carousel = loadCarousel(items, category.getName(), false);
                    categoryContainer.getChildren().add(carousel);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Node loadCarousel(List<FeaturedItem> items, String title, boolean topRated) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Carousel.fxml"));
        Node carouselNode = loader.load();
        CarouselController controller = loader.getController();

        if (topRated) controller.loadTopRatedInfinite(items);
        else controller.loadItems(items);

        controller.setCategoryTitle(title);
        return carouselNode;
    }

    // ---------------- SERIES FILTER CAROUSEL ----------------
    private void loadSeriesFilterCarousel(List<Category> categories) {
        seriesFilterContainer = new HBox(20);
        seriesFilterContainer.setPadding(new Insets(15, 0, 30, 0));
        seriesFilterContainer.setAlignment(Pos.CENTER_LEFT);

        var createButton = (java.util.function.Function<String, Button>) name -> {
            Button btn = new Button(name);

            String normal = """
                -fx-background-color: rgba(20,20,30,0.9);
                -fx-text-fill: #1E90FF;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-padding: 10 22;
                -fx-background-radius: 25;
                -fx-border-radius: 25;
                -fx-border-color: #1E90FF;
                -fx-border-width: 1.5;
                -fx-effect: dropshadow(gaussian, rgba(56,189,248,0.2), 8,0,0,0);
            """;

            String hover = """
                -fx-background-color:#1E90FF;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-padding: 10 22;
                -fx-background-radius: 25;
                -fx-border-radius: 25;
                -fx-effect: dropshadow(gaussian, #38bdf8, 15,0.5,0,0);
            """;

            String selected = """
                -fx-background-color: linear-gradient(to right, #00aaff, #008cff);
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-padding: 10 22;
                -fx-background-radius: 25;
                -fx-border-radius: 25;
                -fx-effect: dropshadow(gaussian, #00aaff, 20,0.6,0,0);
            """;

            btn.setStyle(normal);

            btn.setOnMouseEntered(e -> { if (!btn.getStyleClass().contains("selected")) btn.setStyle(hover); });
            btn.setOnMouseExited(e -> { if (!btn.getStyleClass().contains("selected")) btn.setStyle(normal); });

            btn.setOnAction(e -> {
                boolean isSelected = btn.getStyleClass().contains("selected");
                if (isSelected) {
                    btn.getStyleClass().remove("selected");
                    btn.setStyle(normal);
                    removeSeriesFilter(name);
                } else {
                    btn.getStyleClass().add("selected");
                    btn.setStyle(selected);
                    addSeriesFilter(name);
                }
                refreshSeriesFilteredContent();
            });

            return btn;
        };

        // Categories
        for (Category cat : categories) {
            seriesFilterContainer.getChildren().add(createButton.apply(cat.getName()));
        }

        // Years
        for (int year = 2026; year >= 2000; year--) {
            seriesFilterContainer.getChildren().add(createButton.apply(String.valueOf(year)));
        }

        // ScrollPane
        ScrollPane filterScroll = new ScrollPane(seriesFilterContainer);
        filterScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        filterScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        filterScroll.setFitToHeight(true);
        filterScroll.setStyle("-fx-background-color: transparent;");

        // Arrows
        Button left = new Button("<");
        Button right = new Button(">");

        styleArrowButton(left);
        styleArrowButton(right);

        left.setOnAction(e -> filterScroll.setHvalue(filterScroll.getHvalue() - 0.3));
        right.setOnAction(e -> filterScroll.setHvalue(filterScroll.getHvalue() + 0.3));

        left.setTranslateY(-10);
        right.setTranslateY(-10);

        // Carousel wrapper
        HBox carousel = new HBox(15);
        carousel.setAlignment(Pos.CENTER);
        carousel.setPadding(new Insets(10, 30, 20, 30));
        carousel.setStyle("-fx-background-color: black;");

        HBox.setHgrow(filterScroll, Priority.ALWAYS);
        filterScroll.setMaxWidth(Double.MAX_VALUE);

        carousel.getChildren().addAll(left, filterScroll, right);

        // Show arrows only on hover
        carousel.setOnMouseEntered(e -> {
            left.setOpacity(1);
            right.setOpacity(1);
        });
        carousel.setOnMouseExited(e -> {
            left.setOpacity(0);
            right.setOpacity(0);
        });

        categoryContainer.getChildren().add(carousel);
        Pane divider = new Pane();
        divider.setPrefHeight(3);
        divider.setMaxWidth(Double.MAX_VALUE);

        divider.setStyle("""
            -fx-background-color: linear-gradient(to right, transparent, #0a1f44, #1e3a8a, #0a1f44, transparent);
            -fx-background-radius: 2;
            -fx-effect: dropshadow(gaussian, rgba(30,58,138,0.7), 10, 0.4, 0, 0);
        """);
        VBox.setMargin(divider, new Insets(10, 60, 20, 60));
        categoryContainer.getChildren().add(divider);
        // Series results container
        seriesResultsContainer = new VBox();
        seriesResultsContainer.setSpacing(15);
        seriesResultsContainer.setPadding(new Insets(20, 20, 20, 20));
        seriesResultsContainer.setStyle("-fx-background-color: black;");
        categoryContainer.getChildren().add(seriesResultsContainer);

        refreshSeriesFilteredContent();
    }

    private void styleArrowButton(Button btn) {
        btn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-font-size: 28;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, gray, 12,0.5,0,0);
        """);

        btn.setOpacity(0);

        btn.setOnMouseEntered(e -> btn.setOpacity(1));
        btn.setOnMouseExited(e -> btn.setOpacity(0));
    }

    // ---------------- SERIES FILTER LOGIC ----------------
    private void addSeriesFilter(String value) {
        value = value.toLowerCase();
        if (value.equals("serie")) return; // type is always series
        if (value.matches("\\d+")) selectedSeriesYears.add(Integer.parseInt(value));
        else selectedSeriesCategories.add(value);
    }

    private void removeSeriesFilter(String value) {
        value = value.toLowerCase();
        if (value.equals("serie")) return;
        if (value.matches("\\d+")) selectedSeriesYears.remove(Integer.parseInt(value));
        else selectedSeriesCategories.remove(value);
    }

    // ---------------- REFRESH SERIES RESULTS ----------------
    private void refreshSeriesFilteredContent() {
        try {
            seriesResultsContainer.getChildren().clear();

            if (selectedSeriesCategories.isEmpty() && selectedSeriesYears.isEmpty()) {
                seriesResultsContainer.setVisible(false);
                seriesResultsContainer.setManaged(false);
                return;
            }

            List<FeaturedItem> items = featuredService.getFilteredItems(
                    selectedSeriesCategories,
                    Set.of("serie"),
                    selectedSeriesYears
            );

            if (!items.isEmpty()) {
                TilePane grid = new TilePane();
                grid.setHgap(20);
                grid.setVgap(25);
                grid.setPrefColumns(5);
                grid.setPadding(new Insets(10));
                grid.setStyle("-fx-background-color: black;");

                for (FeaturedItem item : items) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Card.fxml"));
                    Node card = loader.load();

                    CardController controller = loader.getController();
                    controller.setItem(item);

                    addHoverEffect(card);
                    grid.getChildren().add(card);
                }

                ScrollPane scroll = new ScrollPane(grid);
                scroll.setFitToWidth(true);
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scroll.setStyle("-fx-background: black; -fx-background-color: black;");

                seriesResultsContainer.getChildren().add(scroll);
            } else {
                Label empty = new Label("😞 No series found");
                empty.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
                StackPane wrapper = new StackPane(empty);
                wrapper.setPrefHeight(500);
                wrapper.setAlignment(Pos.CENTER);
                seriesResultsContainer.getChildren().add(wrapper);
            }

            seriesResultsContainer.setVisible(true);
            seriesResultsContainer.setManaged(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------- CARD HOVER EFFECT ----------------
    private void addHoverEffect(Node card) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(180), card);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(180), card);
        scaleDown.setToX(1);
        scaleDown.setToY(1);

        card.setOnMouseEntered(e -> {
            scaleUp.playFromStart();
            card.setViewOrder(-1); 
        });

        card.setOnMouseExited(e -> {
            scaleDown.playFromStart();
            card.setViewOrder(0);
        });
    }
}