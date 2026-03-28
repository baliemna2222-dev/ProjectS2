package JStream.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
public class HomepageController {
	    @FXML
	    private VBox categoryContainer;       // Your main container
	           // Holds filter buttons
	    private ScrollPane filterScrollPane;  // ScrollPane wrapping filters
	    private VBox resultsContainer;        // Holds filtered content

	    private final FeaturedService featuredService = new FeaturedService();

	   
	    private Set<String> selectedCategories = new HashSet<>();
	    private Set<String> selectedTypes = new HashSet<>();
	    private Set<Integer> selectedYears = new HashSet<>();
	    @FXML
	    private HBox filterContainer;
	    @FXML
	    private void initialize() {
	    	
	            loadCarouselsByCategory();
	            
         
	}
	  
	   
	    private void updateNormalCategoryCarouselsVisibility() {
	        boolean anyFilterSelected = !selectedCategories.isEmpty() 
	                                    || !selectedTypes.isEmpty() 
	                                    || !selectedYears.isEmpty();

	        for (Node carousel : normalCategoryCarousels) {
	            carousel.setVisible(!anyFilterSelected);
	            carousel.setManaged(!anyFilterSelected); // also remove from layout space
	        }
	    }

	    // ---------------- REFRESH FILTERED CONTENT ----------------
	    private void loadFilterCarousel(List<Category> categories) {

	        // ---------------- FILTER BAR ----------------
	        filterContainer = new HBox(20);
	        filterContainer.setPadding(new Insets(15, 20, 30, 20));
	        filterContainer.setAlignment(Pos.CENTER_LEFT);

	        // 🎯 Button creator
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
	                -fx-cursor: hand;
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

	            btn.setOnMouseEntered(e -> {
	                if (!btn.getStyleClass().contains("selected")) btn.setStyle(hover);
	            });

	            btn.setOnMouseExited(e -> {
	                if (!btn.getStyleClass().contains("selected")) btn.setStyle(normal);
	            });

	            btn.setOnAction(e -> {
	                boolean isSelected = btn.getStyleClass().contains("selected");

	                if (isSelected) {
	                    btn.getStyleClass().remove("selected");
	                    btn.setStyle(normal);
	                    removeFilter(name);
	                } else {
	                    btn.getStyleClass().add("selected");
	                    btn.setStyle(selected);
	                    addFilter(name);
	                }

	                refreshFilteredContent();  // updates results and normal carousels
	            });

	            return btn;
	        };

	        // Types
	        filterContainer.getChildren().addAll(
	            createButton.apply("Film"),
	            createButton.apply("Serie")
	        );

	        // Categories
	        for (Category cat : categories) {
	            filterContainer.getChildren().add(createButton.apply(cat.getName()));
	        }

	        // Years
	        for (int year = 2026; year >= 2000; year--) {
	            filterContainer.getChildren().add(createButton.apply(String.valueOf(year)));
	        }

	        // Scroll + Arrows
	        filterScrollPane = new ScrollPane(filterContainer);
	        filterScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	        filterScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	        
	        filterScrollPane.setFitToHeight(true);
	        filterScrollPane.setStyle("-fx-background-color: transparent;");
	        
	        Button left = new Button("<");
	        Button right = new Button(">");
	        styleArrowButton(left);
	        styleArrowButton(right);

	        left.setOnAction(e -> filterScrollPane.setHvalue(filterScrollPane.getHvalue() - 0.3));
	        right.setOnAction(e -> filterScrollPane.setHvalue(filterScrollPane.getHvalue() + 0.3));

	     // 🔥 Arrows OUTSIDE the buttons
	        HBox carousel = new HBox(15);
	        carousel.setAlignment(Pos.CENTER);

	        // Give space on left/right (this is what you want)
	        carousel.setPadding(new Insets(10, 30, 20, 30));
	        carousel.setStyle("-fx-background-color: black;");
	        left.setTranslateY(-10);
	        right.setTranslateY(-10);
	        // Let scroll take available space
	        HBox.setHgrow(filterScrollPane, Priority.ALWAYS);
	        filterScrollPane.setMaxWidth(Double.MAX_VALUE);

	        // Add in correct order
	        carousel.getChildren().addAll(left, filterScrollPane, right);
	        // Add filter carousel at top
	        categoryContainer.getChildren().add(carousel);
	        
	        // Results container
	        resultsContainer = new VBox();
	        resultsContainer.setSpacing(15);
	        resultsContainer.setPadding(new Insets(20, 60, 20, 60)); // 👈 equal left/right space
	        resultsContainer.setStyle("-fx-background-color: black;");
	        categoryContainer.getChildren().add(resultsContainer);
	        refreshFilteredContent(); // initial load
	    }

	    // ---------------- REFRESH FILTERED CONTENT ----------------
	    private void refreshFilteredContent() {
	        try {
	            resultsContainer.getChildren().clear(); // remove old results

	            if (selectedCategories.isEmpty() && selectedTypes.isEmpty() && selectedYears.isEmpty()) {
	                resultsContainer.setVisible(false);
	                resultsContainer.setManaged(false);
	                updateNormalCategoryCarouselsVisibility();
	                return;
	            }

	            List<FeaturedItem> items = featuredService.getFilteredItems(
	                    selectedCategories, selectedTypes, selectedYears
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

	                    addHoverEffect(card); // pump effect
	                    grid.getChildren().add(card);
	                }

	                ScrollPane scroll = new ScrollPane(grid);
	                scroll.setFitToWidth(true);
	                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	                scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	                scroll.setStyle("-fx-background: black; -fx-background-color: black;");

	                resultsContainer.getChildren().add(scroll);
	            } else {
	            	Label empty = new Label("😞 No results found");
	            	empty.setStyle("""
	            	    -fx-text-fill: white;
	            	    -fx-font-size: 28px;
	            	    -fx-font-weight: bold;
	            	""");

	            	// Wrapper to center it
	            	StackPane emptyWrapper = new StackPane(empty);
	            	emptyWrapper.setPrefHeight(500); // 👈 gives vertical space
	            	emptyWrapper.setAlignment(Pos.CENTER);

	            	resultsContainer.getChildren().add(emptyWrapper);
	            }

	            resultsContainer.setVisible(true);
	            resultsContainer.setManaged(true);
	            updateNormalCategoryCarouselsVisibility();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    // ---------------- HOVER EFFECT ----------------
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
	    // 🎯 HANDLE FILTER LOGIC
	    private void addFilter(String value) {
	        value = value.toLowerCase();

	        if (value.equals("film") || value.equals("serie")) {
	            selectedTypes.add(value);
	        } else if (value.matches("\\d+")) {
	            selectedYears.add(Integer.parseInt(value));
	        } else {
	            selectedCategories.add(value);
	        }
	    }

	    private void removeFilter(String value) {
	        value = value.toLowerCase();

	        if (value.equals("film") || value.equals("serie")) {
	            selectedTypes.remove(value);
	        } else if (value.matches("\\d+")) {
	            selectedYears.remove(Integer.parseInt(value));
	        } else {
	            selectedCategories.remove(value);
	        }
	    }


	    // 🎨 ARROW STYLE
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
	    @FXML
	    private VBox normalCarouselsContainer; 
	    private final List<Node> normalCategoryCarousels = new ArrayList<>();
	    private void loadCarouselsByCategory() {
	        try {
	            List<Category> categories = featuredService.getAllCategories();
	            List<FeaturedItem> topRated = featuredService.getTopRated(10);

	            // 🔥 TOP RATED
	            if (!topRated.isEmpty()) {
	                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Carousel.fxml"));
	                Node carouselNode = loader.load();

	                CarouselController controller = loader.getController();
	                controller.loadTopRatedInfinite(topRated);
	                controller.setCategoryTitle("🔥 Top Rated");

	                categoryContainer.getChildren().add(carouselNode);

	                loadFilterCarousel(categories);
	            }

	            // 🔥 NORMAL CATEGORIES
	            for (Category category : categories) {
	                List<FeaturedItem> items = featuredService.getItemsByCategory(category.getName());
	                if (items.isEmpty()) continue;

	                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Carousel.fxml"));
	                Node carouselNode = loader.load();

	                CarouselController carouselController = loader.getController();
	                carouselController.loadItems(items);
	                carouselController.setCategoryTitle(category.getName());

	                categoryContainer.getChildren().add(carouselNode);

	                // Store the node so we can toggle visibility later
	                normalCategoryCarousels.add(carouselNode);
	            }

	            // ✅ Correctly set visibility based on filters
	            updateNormalCategoryCarouselsVisibility();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
  
   
   
    
}
