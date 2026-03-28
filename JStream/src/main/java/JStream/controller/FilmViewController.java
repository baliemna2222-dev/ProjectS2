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

public class FilmViewController {

    @FXML
    private VBox categoryContainer; // Container for carousels, filters, results

    private HBox filmFilterContainer;
    private VBox filmResultsContainer;
    private final FeaturedService featuredService = new FeaturedService();
    private final Set<String> selectedFilmCategories = new HashSet<>();
    private final Set<Integer> selectedFilmYears = new HashSet<>();

    @FXML
    private void initialize() {
        loadFilmCarousels();
    }

    // ---------------- LOAD CAROUSELS ----------------
    private void loadFilmCarousels() {
        try {
            List<Category> categories = featuredService.getAllCategories();
           
                // Load filter bar after top rated
                loadFilmFilterCarousel(categories);
            

            // Normal categories (films only)
            for (Category category : categories) {
                List<FeaturedItem> items = new ArrayList<>(featuredService.getItemsByCategory(category.getName()));
                items.removeIf(item -> !item.getType().equalsIgnoreCase("film"));

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
    
    // ---------------- FILM FILTER CAROUSEL ----------------
    private void loadFilmFilterCarousel(List<Category> categories) {
        filmFilterContainer = new HBox(20);
        filmFilterContainer.setPadding(new Insets(15, 0, 30, 0));
        filmFilterContainer.setAlignment(Pos.CENTER_LEFT);

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
                    removeFilmFilter(name);
                } else {
                    btn.getStyleClass().add("selected");
                    btn.setStyle(selected);
                    addFilmFilter(name);
                }
                refreshFilmFilteredContent();
            });

            return btn;
        };


        // Categories
        for (Category cat : categories) {
            filmFilterContainer.getChildren().add(createButton.apply(cat.getName()));
        }

        // Years
        for (int year = 2026; year >= 2000; year--) {
            filmFilterContainer.getChildren().add(createButton.apply(String.valueOf(year)));
        }

     // ScrollPane
        ScrollPane filterScroll = new ScrollPane(filmFilterContainer);
        filterScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        filterScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        filterScroll.setFitToHeight(true);
        filterScroll.setStyle("-fx-background-color: transparent;");

        // Arrows
        Button left = new Button("<");
        Button right = new Button(">");

        styleArrowButton(left);
        styleArrowButton(right);

        // Scroll actions
        left.setOnAction(e -> filterScroll.setHvalue(filterScroll.getHvalue() - 0.3));
        right.setOnAction(e -> filterScroll.setHvalue(filterScroll.getHvalue() + 0.3));

        // Move arrows slightly up
        left.setTranslateY(-10);
        right.setTranslateY(-10);

        // Wrapper (THIS IS THE KEY PART 🔥)
        HBox carousel = new HBox(15);
        carousel.setAlignment(Pos.CENTER);
        carousel.setPadding(new Insets(10, 30, 20, 30)); 
        carousel.setStyle("-fx-background-color: black;");

        // Let ScrollPane expand
        HBox.setHgrow(filterScroll, Priority.ALWAYS);
        filterScroll.setMaxWidth(Double.MAX_VALUE);

        // Add everything (THIS LINE IS CRITICAL ❗)
        carousel.getChildren().addAll(left, filterScroll, right);

        // Add to main container
       
        categoryContainer.getChildren().add(carousel);

        // Film results container
        filmResultsContainer = new VBox();
        filmResultsContainer.setSpacing(15);
        filmResultsContainer.setPadding(new Insets(20, 20, 20, 20));
        filmResultsContainer.setStyle("-fx-background-color: black;");
        categoryContainer.getChildren().add(filmResultsContainer);

        refreshFilmFilteredContent();
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

    // ---------------- FILM FILTER LOGIC ----------------
    private void addFilmFilter(String value) {
        value = value.toLowerCase();
        if (value.equals("film")) return; // type is always film
        if (value.matches("\\d+")) selectedFilmYears.add(Integer.parseInt(value));
        else selectedFilmCategories.add(value);
    }

    private void removeFilmFilter(String value) {
        value = value.toLowerCase();
        if (value.equals("film")) return;
        if (value.matches("\\d+")) selectedFilmYears.remove(Integer.parseInt(value));
        else selectedFilmCategories.remove(value);
    }

    // ---------------- REFRESH FILM RESULTS ----------------
    private void refreshFilmFilteredContent() {
        try {
            filmResultsContainer.getChildren().clear();

            if (selectedFilmCategories.isEmpty() && selectedFilmYears.isEmpty()) {
                filmResultsContainer.setVisible(false);
                filmResultsContainer.setManaged(false);
                return;
            }

            List<FeaturedItem> items = featuredService.getFilteredItems(
                    selectedFilmCategories,
                    Set.of("film"),
                    selectedFilmYears
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

                filmResultsContainer.getChildren().add(scroll);
            } else {
                Label empty = new Label("😞 No films found");
                empty.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
                StackPane wrapper = new StackPane(empty);
                wrapper.setPrefHeight(500);
                wrapper.setAlignment(Pos.CENTER);
                filmResultsContainer.getChildren().add(wrapper);
            }

            filmResultsContainer.setVisible(true);
            filmResultsContainer.setManaged(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------- FILTER BAR WITH ARROWS ----------------
    private void addHoverEffect(Node card) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(180), card);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(180), card);
        scaleDown.setToX(1);
        scaleDown.setToY(1);

        card.setOnMouseEntered(e -> {
            scaleUp.playFromStart();
            card.setViewOrder(-1); // bring front
        });

        card.setOnMouseExited(e -> {
            scaleDown.playFromStart();
            card.setViewOrder(0);
        });
    }
}