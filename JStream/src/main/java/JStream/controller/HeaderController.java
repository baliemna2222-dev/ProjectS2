package JStream.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.Film;
import JStream.entity.MyListManager;
import JStream.entity.Season;
import JStream.entity.Serie;
import JStream.entity.Session;
import JStream.entity.WatchStatus;
import JStream.service.EpisodeProgressService;
import JStream.service.FeaturedService;
import JStream.service.FilmProgressService;
import JStream.service.MylistService;
import JStream.utils.ImageUtil;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class HeaderController {
	@FXML private Button profile;
	@FXML private StackPane profileMenu;
	@FXML private ImageView profileImage;
	@FXML private Label uploadLabel;
	// Add this field to your controller
	private boolean ignoreTextChange = false;
	@FXML private Button btnMenuLogout, btnMenuSettings, btnMenuMyList, btnMenuLastWatched;

	private Image defaultImage = new Image("/assets/images/profile.png");

    /** -------------------- HEADER BUTTONS -------------------- **/
    @FXML private Button btnHome, btnMovies, btnSeries, btnMyList;
    @FXML private Rectangle lineHome, lineMovies, lineSeries, lineMyList;
    private Button activeButton;
    private Rectangle activeLine;
    @FXML HBox rootPane;
    @FXML private ImageView logoImage;
    private Timeline autoSlide;
    /** -------------------- SEARCH -------------------- **/
    @FXML private TextField searchInput;
    private boolean searchOpened = false;
    private Popup suggestionsPopup = new Popup();
    private VBox suggestionsContent = new VBox();
    @FXML Button clearBtn ;
    private FeaturedService featuredService = new FeaturedService();
    private EpisodeProgressService episodeProgressService = new EpisodeProgressService();
    @FXML
    private StackPane searchStack;
    @FXML
    private Button btnResearch;
    FilmProgressService filmProgressService = new FilmProgressService();

    @FXML
    private void clearSearchInput() {
        searchInput.clear();
    }
    /** -------------------- BELL NOTIFICATION -------------------- **/
    @FXML private StackPane bellContainer;
    @FXML private ImageView bellIcon;
    private Circle notificationDot;
    private boolean isNotificationVisible = false;

    private static String lastActive = "HOME";

    @FXML
    private void initialize() {

        // Logo
        logoImage.setImage(new Image(getClass().getResourceAsStream("/assets/images/logo/Raksha.png")));

        // Bell notification
        setupBellNotification();

        // Search suggestions
        setupSearchSuggestions();

        // Page navigation
        setupButton(btnHome, lineHome, this::goToHomepage);
        setupButton(btnMovies, lineMovies, this::goToFilmView);
        setupButton(btnSeries, lineSeries, this::goToSeriesView);
        setupButton(btnMyList, lineMyList, this::goToMyListView);

        // Activate last active
        Platform.runLater(() -> {
            switch (lastActive) {
                case "Movies" -> activate(btnMovies, lineMovies);
                case "Series" -> activate(btnSeries, lineSeries);
                case "My List" -> activate(btnMyList, lineMyList);
                default -> activate(btnHome, lineHome);
            }
        });
        StackPane.setAlignment(clearBtn, Pos.CENTER_RIGHT);
        StackPane.setMargin(clearBtn, new Insets(0, 5, 0, 0));
        clearBtn.setPickOnBounds(true); // ensures clicks register even if background transparent
        // Hide X initially
        clearBtn.setVisible(false);

        // Show X only when text exists
        searchInput.textProperty().addListener((obs, oldText, newText) -> {
            clearBtn.setVisible(!newText.isEmpty() && searchInput.getPrefWidth() > 0);
        });

        // Clear text & collapse field
        clearBtn.setOnAction(e -> {
            searchInput.clear();
        });

        // Optional: hover effect
        clearBtn.setOnMouseEntered(e -> clearBtn.setStyle("-fx-text-fill: Blue;-fx-background-color: transparent;"));
        clearBtn.setOnMouseExited(e -> clearBtn.setStyle("-fx-text-fill: #888;-fx-background-color: transparent;"));

    }

    /** -------------------- HEADER BUTTON METHODS -------------------- **/
    private void activate(Button btn, Rectangle line) {
        activeButton = btn;
        activeLine = line;
        setButtonActive(btn);
        line.setWidth(btn.getWidth());
    }

    private void setupButton(Button btn, Rectangle line, Runnable action) {
        line.setFill(Color.DODGERBLUE);
        line.setHeight(3);

        btn.setOnMouseEntered(e -> {
            if (btn != activeButton) {
                setButtonHover(btn);
                animateLine(line, btn.getWidth());
            }
        });

        btn.setOnMouseExited(e -> {
            if (btn != activeButton) {
                setButtonInactive(btn);
                animateLine(line, 0);
            }
        });

        btn.setOnAction(e -> {
            if (activeButton != null && activeButton != btn) {
                setButtonInactive(activeButton);
                animateLine(activeLine, 0);
            }

            activeButton = btn;
            activeLine = line;
            setButtonActive(btn);
            line.setWidth(btn.getWidth());

            lastActive = btn.getText();
            if (action != null) action.run();
        });
    }

    private void setButtonActive(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 16;");
    }

    private void setButtonHover(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 16;");
    }

    private void setButtonInactive(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #cccccc; -fx-font-size: 16;");
    }

    private void animateLine(Rectangle line, double targetWidth) {
        Platform.runLater(() -> {
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(200), new KeyValue(line.widthProperty(), targetWidth))
            );
            timeline.play();
        });
    }

    /** -------------------- SEARCH METHODS -------------------- **/
    private void setupSearchSuggestions() {
        // Scrollable VBox
        ScrollPane scroll = new ScrollPane(suggestionsContent);
        scroll.setPrefWidth(300);
        scroll.setFitToWidth(true);

        // Dynamic height
        scroll.setPrefHeight(Region.USE_COMPUTED_SIZE);
        scroll.setMaxHeight(250); // 👈 limit max height (scroll appears after)
       

        // Make scroll and popup background black
        scroll.setStyle("-fx-background-color: #111; -fx-background-radius: 6; -fx-border-color: #222; -fx-border-radius: 6;");
        suggestionsContent.setStyle("-fx-background-color: #111; -fx-padding: 5; -fx-spacing: 2; -fx-background-radius: 6;");

        scroll.getStyleClass().add("custom-scroll"); // your scrollbar CSS
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        scroll.setStyle(
        		
        	    "-fx-background: black;" +           // outer
        	    "-fx-background-color: black;" +     // fix white
        	    "-fx-border-color: transparent;"
        	);

        	suggestionsContent.setStyle(
        	    "-fx-background-color: #0f172a;" +
        	    "-fx-padding: 6;" +
        	    "-fx-spacing: 4;"
        	);
        	scroll.getStylesheets().add(
        		    getClass().getResource("/view/css/scrollbar.css").toExternalForm()
        		);
        suggestionsPopup.getContent().add(scroll);
        suggestionsPopup.setAutoHide(true);

        // Typing listener
        searchInput.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) showLatestSearches();
            else showSuggestions(newText);
        });

        // Enter key
        searchInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String text = searchInput.getText().trim();
                if (!text.isEmpty()) {
                    // Add to memory AND save to database inside FeaturedService
                    featuredService.addToLatestSearch(text);

                    // Hide popup
                    suggestionsPopup.hide();
                }
               
            }
        });

        searchInput.textProperty().addListener((obs, oldText, newText) -> {
            if (ignoreTextChange) return; // ignore programmatic updates

            if (newText.isEmpty()) showLatestSearches();
            else showSuggestions(newText);
        });
    }

    private HBox createSuggestionBox(String title, String posterUrl, boolean isSearchResult) {

        HBox box = new HBox(10);
        box.setPrefWidth(300);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-background-color: transparent; -fx-padding: 8; -fx-background-radius: 6;");

        // Poster
        if (posterUrl != null) {
            ImageView poster = new ImageView();
            poster.setFitWidth(35);
            poster.setFitHeight(50);
            poster.setPreserveRatio(true);
            try { poster.setImage(new Image(posterUrl, true)); } catch (Exception ignored) {}
            box.getChildren().add(poster);
        }

        // Title
        Label lbl = new Label(title);
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
        lbl.setMaxWidth(180);
        lbl.setEllipsisString("...");
        lbl.setWrapText(false);
     // Add button (for search results)
        if (isSearchResult) {
            Button addBtn = new Button();
            addBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 14; -fx-font-weight: bold;");
            addBtn.setPadding(new Insets(2, 5, 2, 5));
           
            try {
                MylistService mylistService = new MylistService();
                FeaturedItem item = featuredService.getFeaturedByTitle(title);
                MyListManager.getInstance().addListener((filmId, serieId) -> {
                    Platform.runLater(() -> {
                        if (item != null) {
                            int currentFilmId = "film".equalsIgnoreCase(item.getType()) ? item.getId() : 0;
                            int currentSerieId = "serie".equalsIgnoreCase(item.getType()) ? item.getSerieId() : 0;

                            // If the updated item matches this card's current item, refresh the button
                            if (currentFilmId == filmId && currentSerieId == serieId) {
                                updateAddButton(addBtn, item);
                            }
                        }
                    });
                });
                if (item != null) {
                    boolean alreadyInList = mylistService.isInList(Session.getUserId(),
                                                                    item.getId(),
                                                                    item.getSerieId());
                    if (alreadyInList) {
                        addBtn.setText("✔");
                        addBtn.setTextFill(Color.BLUE);
                    } else {
                        addBtn.setText("+");
                        addBtn.setTextFill(Color.GRAY);
                    }

                }
               
                // Click to add/remove
                addBtn.setOnAction(e -> {
                    if (item != null) {
                        boolean inList = mylistService.isInList(Session.getUserId(), item.getId(), item.getSerieId());
                        if (!inList) {
                            mylistService.addItem(Session.getUserId(), item.getId(), item.getSerieId());
                        } else {
                            mylistService.removeItem(Session.getUserId(), item.getId(), item.getSerieId());
                        }

                        // Update this button immediately
                        updateAddButton(addBtn, item);
                       
                        // 🔥 Notify all other listeners
                        MyListManager.getInstance().notifyItemUpdated(item.getId(), item.getSerieId());
                    }
                });
            
            box.getChildren().add(addBtn);
         } catch (Exception ex) { ex.printStackTrace(); }
    }
        // Tooltip to show full text on hover
        javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(title);
        tooltip.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-font-size: 13;");
        javafx.scene.control.Tooltip.install(lbl, tooltip);

        box.getChildren().add(lbl);

        // Spacer for X button
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        box.getChildren().add(spacer);

        // X button (latest searches only)
        if (!isSearchResult) {
            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 12;");
            removeBtn.setOnMouseEntered(e -> removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: blue; -fx-font-size: 12;"));
            removeBtn.setOnMouseExited(e -> removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 12;"));
            removeBtn.setOnAction(e -> {
                featuredService.removeLatestSearch(title);
                showLatestSearches();
            });
            box.getChildren().add(removeBtn);
        }

        // Hover background without shifting text
        box.setOnMouseEntered(e -> box.setStyle(
                "-fx-background-color: #1e293b;" +
                "-fx-padding: 8;" +
                "-fx-background-radius: 6;"
        ));
        box.setOnMouseExited(e -> box.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-padding: 8;" +
                "-fx-background-radius: 6;"
        ));

        // Click action
        box.setOnMouseClicked(e -> {
            ignoreTextChange = true;           // ignore listener temporarily
            searchInput.setText(title);        // set text programmatically
            ignoreTextChange = false;          // restore listener

            if (isSearchResult) {
                featuredService.addToLatestSearch(title); // update DB/list
                try {
                    FeaturedItem item = featuredService.getFeaturedByTitle(title);
                    if (item != null) {
                        switch (item.getType().toLowerCase()) {
                            case "film" -> showFilmPopup(item);
                            case "serie" -> showSeriePopup(item);
                        }
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }

            suggestionsPopup.hide();
        });

        return box;
    }

   
    private void showSuggestions(String text) {
        suggestionsContent.getChildren().clear();

        List<FeaturedItem> items;
        try {
            items = featuredService.searchByTitle(text);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (items.isEmpty()) {
            Label empty = new Label("🔍 No results found");
            empty.setStyle(
                "-fx-text-fill: #e3f2fd;" +
                "-fx-font-size: 14;" +
                "-fx-padding: 20;"
            );
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);

            suggestionsContent.getChildren().add(empty);
        } else {
            for (FeaturedItem item : items) {
                suggestionsContent.getChildren().add(
                    createSuggestionBox(item.getTitle(), item.getPosterUrl(), true)
                );
            }
        }

        // 🔥 Update popup size and hide scrollbar if not needed
        Platform.runLater(() -> {
            suggestionsContent.applyCss();
            suggestionsContent.layout();

            double contentHeight = suggestionsContent.getHeight();
            double maxHeight = 250; // max height for scroll

            suggestionsPopup.setWidth(300);
            suggestionsPopup.setHeight(Math.min(contentHeight, maxHeight));

            // Show or hide vertical scrollbar based on content
            ScrollPane scroll = (ScrollPane) suggestionsPopup.getContent().get(0);
            scroll.setVbarPolicy(contentHeight > maxHeight ? ScrollPane.ScrollBarPolicy.AS_NEEDED : ScrollPane.ScrollBarPolicy.NEVER);

            showPopup();
        });
    }
    private void showLatestSearches() {
        suggestionsContent.getChildren().clear();
        List<String> latest = featuredService.getLatestSearches();

        if (latest.isEmpty()) {
            // Do NOT show the popup if the list is empty
            suggestionsPopup.hide();
            return;
        }

        // Add all latest searches
        for (String title : latest) {
            HBox box = createSuggestionBox(title, null, false);
            suggestionsContent.getChildren().add(box);
        }

        // 🔥 FORCE UI REFRESH (fixes scrollbar and layout)
        Platform.runLater(() -> {
            suggestionsContent.applyCss();
            suggestionsContent.layout();
            showPopup();
        });
    }
    private void updateAddButton(Button addBtn, FeaturedItem item) {
        if (addBtn == null || item == null) return;

        try {
            MylistService mylistService = new MylistService();
            boolean inList = mylistService.isInList(Session.getUserId(), item.getId(), item.getSerieId());

            // Set button text and color
            if (inList) {
                addBtn.setText("✔");
                addBtn.setTextFill(Color.BLUE); // or LIME if you prefer
            } else {
                addBtn.setText("+");
                addBtn.setTextFill(Color.GRAY);
            }

            // Ensure button padding and size are consistent
            addBtn.setPadding(new Insets(2, 5, 2, 5));
            addBtn.setMinWidth(25);
            addBtn.setMaxWidth(25);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
  
 // HeaderController.java
    @FXML
    public void toggleSearch(ActionEvent event) {  // must be public and @FXML
        if (searchInput.getPrefWidth() == 0) {
            expandSearchField();
        } else {
            collapseSearchField();
        }
    }

    private void expandSearchField() {
        Timeline expand = new Timeline(
            new KeyFrame(Duration.millis(250),
                new KeyValue(searchInput.prefWidthProperty(), 220),
                new KeyValue(searchInput.maxWidthProperty(), 220))
        );
        expand.setOnFinished(e -> searchInput.requestFocus());
        expand.play();
    }

    private void collapseSearchField() {
        Timeline collapse = new Timeline(
            new KeyFrame(Duration.millis(250),
                new KeyValue(searchInput.prefWidthProperty(), 0),
                new KeyValue(searchInput.maxWidthProperty(), 0))
        );
        collapse.play();
    }
    private void showPopup() {
        Platform.runLater(() -> {

            // 🔥 ensure layout is updated
            searchInput.applyCss();
            searchInput.layout();

            Bounds bounds = searchInput.localToScreen(searchInput.getBoundsInLocal());

            // update content size
            suggestionsContent.applyCss();
            suggestionsContent.layout();

            double height = Math.min(suggestionsContent.getHeight(), 250);

            suggestionsPopup.setWidth(searchInput.getWidth()); // 👈 SAME WIDTH as input
            suggestionsPopup.setHeight(height);

            if (!suggestionsPopup.isShowing()) {
                suggestionsPopup.show(
                    searchInput,
                    bounds.getMinX(),
                    bounds.getMaxY() + 5 // 👈 always directly under
                );
            } else {
                // 🔥 update position if already visible
                suggestionsPopup.setX(bounds.getMinX());
                suggestionsPopup.setY(bounds.getMaxY() + 5);
                
            }
        });
    }

    /** -------------------- BELL NOTIFICATION METHODS -------------------- **/
    private void setupBellNotification() {
        bellIcon.setImage(new Image(getClass().getResourceAsStream("/assets/images/bellwhiter.png")));

        notificationDot = new Circle(4, Color.BLUE);
        notificationDot.setTranslateX(10);
        notificationDot.setTranslateY(-12);
        notificationDot.setScaleX(0);
        notificationDot.setScaleY(0);
        bellContainer.getChildren().add(notificationDot);

        bellContainer.setOnMouseEntered(e -> showNotificationDot());
    }

    private void showNotificationDot() {
        if (!isNotificationVisible) {
            isNotificationVisible = true;
            notificationDot.setVisible(true);
            Platform.runLater(() -> {
                ScaleTransition bounce = new ScaleTransition(Duration.millis(500), notificationDot);
                bounce.setFromX(0); bounce.setFromY(0);
                bounce.setToX(1); bounce.setToY(1);
                bounce.setInterpolator(Interpolator.EASE_OUT);
                bounce.play();
            });
        }
    }

    /** -------------------- PAGE NAVIGATION -------------------- **/
    public void goToHomepage() { navigateTo("/view/fxml/HomePage.fxml"); }
    public void goToFilmView() { navigateTo("/view/fxml/FilmView.fxml"); }
    public void goToSeriesView() { navigateTo("/view/fxml/SeriesView.fxml"); }
    public void goToMyListView() { navigateTo("/view/fxml/MyList.fxml"); }

    private void navigateTo(String fxmlPath) {
        try {
            var url = getClass().getResource(fxmlPath);
            if (url == null) {
                System.out.println("❌ FXML NOT FOUND: " + fxmlPath);
                return;
            }
            Parent root = FXMLLoader.load(url);
            Scene scene = btnHome.getScene();
            scene.setRoot(root);
        } catch (Exception e) { e.printStackTrace(); }
    }
    public void showFilmPopup(FeaturedItem item) {
        Stage popup = new Stage();
        popup.initOwner(rootPane.getScene().getWindow());
        popup.initModality(Modality.WINDOW_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0); -fx-background-radius: 4;");
        root.setPadding(new Insets(20));
        root.setPrefSize(1000, 600);

        VBox content = new VBox(20);
        content.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 4; -fx-padding: 20;");
        content.setMaxWidth(Double.MAX_VALUE);

        // Close button
        Button close = new Button("✕");
        close.setStyle("-fx-background-color:#008cff; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:50%; -fx-padding:5 10;");
        close.setOnAction(e -> popup.close());

        HBox topBar = new HBox(close);
        topBar.setAlignment(Pos.TOP_RIGHT);
        content.getChildren().add(topBar);

        try {
            if ("film".equalsIgnoreCase(item.getType())) {
                Film film = featuredService.getFilmDetails(item.getId());

                HBox filmBox = new HBox(30);
                filmBox.setAlignment(Pos.TOP_LEFT);
                filmBox.setMaxWidth(Double.MAX_VALUE);

                // Poster
                ImageView poster = new ImageView();
                poster.setFitWidth(300);
                poster.setFitHeight(450);
                 poster.setImage(ImageUtil.load(film.getPoster_url()));
                // Right info column
                VBox right = new VBox(15);
                right.setAlignment(Pos.TOP_LEFT);
                right.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(right, Priority.ALWAYS);

                // Title image
                ImageView titleImage = new ImageView();
                titleImage.setFitHeight(150);
                titleImage.setPreserveRatio(true);
               
                titleImage.setImage(ImageUtil.load(film.getTitle_image_url()));
                // Stars
                HBox starsBox = new HBox(3);
                int fullStars = film.getRating();
                for (int i = 0; i < 5; i++) {
                    Label star = new Label("★");
                    star.setStyle("-fx-font-size:24; -fx-font-weight:bold;");
                    star.setTextFill(i < fullStars ? Color.DEEPSKYBLUE : Color.LIGHTGRAY);
                    starsBox.getChildren().add(star);
                }

                // Duration
                int totalMinutes = (int) film.getDuration();
                int hours = totalMinutes / 60;
                int minutes = totalMinutes % 60;
                Label duration = new Label("⏱ " + hours + "h " + minutes + "min");
                duration.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:16;");

                // Casting
                Label casting = new Label("Casting: " + (film.getCasting() != null ? film.getCasting() : ""));
                casting.setWrapText(true);
                casting.setMaxWidth(600);
                casting.setStyle("-fx-text-fill:#cccccc; -fx-font-size:16;");

                // Categories
                String cats = film.getCategories() != null ?
                        film.getCategories().stream().map(c -> c.getName()).reduce((a, b) -> a + " • " + b).orElse("")
                        : "";
                Label categories = new Label("Categories: " + cats);
                categories.setWrapText(true);
                categories.setMaxWidth(600);
                categories.setStyle("-fx-text-fill:#00aaff; -fx-font-size:16;");

                // Synopsis
                Label synopsis = new Label(film.getSynopsis() != null ? film.getSynopsis() : "");
                synopsis.setWrapText(true);
                synopsis.setMaxWidth(600);
                synopsis.setStyle("-fx-text-fill:#cccccc;-fx-font-size:16;");

                right.getChildren().addAll(titleImage, starsBox, duration, casting, categories, synopsis);
                filmBox.getChildren().addAll(poster, right);
                content.getChildren().add(filmBox);

                // Buttons
                Button trailer = new Button("Trailer");
                trailer.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-color: #00aaff;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-text-fill: #00aaff;" +
                        "-fx-font-size: 20;" +
                        "-fx-padding: 6 20;" +
                        "-fx-effect: dropshadow(gaussian,#00aaff,10,0,0,0);"
                );
                trailer.setOnAction(e -> showTrailerPopup(film, 0));

                Button play = new Button("▶");
                play.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-color: #00aaff;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-text-fill: #00aaff;" +
                        "-fx-font-size: 20;" +
                        "-fx-padding: 6 20;" +
                        "-fx-effect: dropshadow(gaussian,#00aaff,10,0,0,0);"
                );
                play.setOnAction(e -> goToLecturePageFilm(film.getFilm_id()));
                addHoverAnimation(close);
                addHoverAnimation(trailer);
                ScaleTransition pulse = new ScaleTransition(Duration.millis(800), play);
                pulse.setFromX(1); pulse.setFromY(1);
                pulse.setToX(1.10); pulse.setToY(1.10);
                pulse.setCycleCount(Animation.INDEFINITE);
                pulse.setAutoReverse(true);
                pulse.play();

                // Wrap buttons in HBox
                HBox buttonBox = new HBox(20, trailer, play);
                buttonBox.setAlignment(Pos.CENTER_RIGHT);
                buttonBox.setMaxWidth(Double.MAX_VALUE);

                content.getChildren().add(buttonBox);

                // Marquee effect for long labels
                for (Label lbl : new Label[]{casting, categories, synopsis}) {
                    lbl.widthProperty().addListener((obs, oldVal, newVal) -> {
                        if (lbl.getWidth() > 600) {
                            TranslateTransition marquee = new TranslateTransition(Duration.seconds(15), lbl);
                            marquee.setFromX(0);
                            marquee.setToX(-lbl.getWidth());
                            marquee.setCycleCount(Animation.INDEFINITE);
                            marquee.setInterpolator(Interpolator.LINEAR);
                            marquee.play();
                        }
                    });
                }
             // Status label
                Label statusLabel = new Label("NOT_STARTED"); // initial status
                statusLabel.setStyle(
                    "-fx-background-color: #008cff;" + // blue background
                    "-fx-text-fill: white;" +          // white text
                    "-fx-font-size: 14;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 4 10 4 10;" +        // top/right/bottom/left
                    "-fx-background-radius: 20;" +     // rounded pill shape
                    "-fx-border-radius: 20;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4,0,0,2);" // subtle shadow
                );
                // Load current status
                WatchStatus currentStatus = filmProgressService.getFilmStatus(Session.getUserId(), film.getFilm_id());
                statusLabel.setText(currentStatus.toString());

                // Optionally, change color depending on status
                switch (currentStatus) {
                    case NOT_STARTED -> statusLabel.setStyle(statusLabel.getStyle().replaceAll("-fx-background-color:.*?;", "-fx-background-color: #888888;"));
                    case IN_PROGRESS -> statusLabel.setStyle(statusLabel.getStyle().replaceAll("-fx-background-color:.*?;", "-fx-background-color:  #008cff;"));
                    case COMPLETED -> statusLabel.setStyle(statusLabel.getStyle().replaceAll("-fx-background-color:.*?;", "-fx-background-color:  #008cff;"));
                }
                // Add status label under the film info
                right.getChildren().add(statusLabel);

                // Play button action
                play.setOnAction(e -> {
                    int lastPosition = 0; // replace with actual current position if you track time

                    if (lastPosition >= film.getDuration()) {
                        // Film completed
                        filmProgressService.markCompleted(Session.getUserId(), film.getFilm_id(), (int)film.getDuration());
                        statusLabel.setText("COMPLETED");
                        statusLabel.setStyle(
                            statusLabel.getStyle().replaceAll("-fx-background-color:.*?;", "-fx-background-color: #008cff;")
                        );
                    } else {
                        // Film in progress
                        filmProgressService.markInProgress(Session.getUserId(), film.getFilm_id(), lastPosition);
                        statusLabel.setText("IN_PROGRESS");
                        statusLabel.setStyle(
                            statusLabel.getStyle().replaceAll("-fx-background-color:.*?;", "-fx-background-color: #008cff;")
                        );
                    }

                    goToLecturePageFilm(film.getFilm_id()); // start playback
                });
                root.getChildren().add(content);
                FadeTransition fade = new FadeTransition(Duration.millis(400), root);
                fade.setFromValue(0); fade.setToValue(1); fade.play();

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
    }
   	private void addHoverAnimation(Button btn) {
	    ScaleTransition up = new ScaleTransition(Duration.millis(120), btn);
	    up.setToX(1.15); up.setToY(1.15);

	    ScaleTransition down = new ScaleTransition(Duration.millis(120), btn);
	    down.setToX(1.0); down.setToY(1.0);

	    btn.setOnMouseEntered(e -> up.playFromStart());
	    btn.setOnMouseExited(e -> down.playFromStart());
	}
	private void goToLecturePageFilm(int filmId) {
	    try {
	    	FXMLLoader loader = new FXMLLoader(); loader.setLocation(getClass().getClassLoader().getResource("view/fxml/LecturePage.fxml")); Parent root = loader.load();
	        LecturePageController controller = loader.getController();
	        controller.initFilm(filmId);

	        Stage stage = new Stage();
	        stage.initOwner(rootPane.getScene().getWindow());
	        stage.initModality(Modality.WINDOW_MODAL);
	        stage.setScene(new Scene(root));
	        stage.setTitle("Lecture: Film");
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	  private void showTrailerPopup(Object item, int seasonIndex) {
	        String trailerUrl = null;

	        // 🎬 FILM
	        if (item instanceof Film) {
	            trailerUrl = ((Film) item).getVideo_url();
	        }
	        // 📺 SERIE → use selected season
	        else if (item instanceof Serie) {
	            Serie serie = (Serie) item;
	            if (serie.getSeasons() != null && !serie.getSeasons().isEmpty()) {
	                if (seasonIndex < 0 || seasonIndex >= serie.getSeasons().size()) seasonIndex = 0;
	                trailerUrl = serie.getSeasons().get(seasonIndex).getTrailerUrl();
	            }
	        }

	        if (trailerUrl == null) return;

	        // Get resource URL
	        URL videoUrl = getClass().getResource(trailerUrl);
	        if (videoUrl == null) {
	            System.out.println("Video file not found: " + trailerUrl);
	            return;
	        }

	        String videoPath = trailerUrl.startsWith("http") ? trailerUrl : videoUrl.toExternalForm();

	        WebView webView = new WebView();
	        webView.setPrefSize(1500, 700);

	        String html =
	            "<html><body style='margin:0; background:black;'>" +
	            "<video width='100%' height='100%' controls>" +
	            "<source src='" + videoPath + "' type='video/mp4'>" +
	            "Your browser does not support the video tag." +
	            "</video></body></html>";

	        webView.getEngine().loadContent(html);

	        // Screen bounds
	        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
	        double fullWidth = screenBounds.getWidth();
	        double fullHeight = screenBounds.getHeight();
	        double smallWidth = 1200;
	        double smallHeight = 600;

	        Stage popup = new Stage();
	        popup.initOwner(rootPane.getScene().getWindow());
	        popup.initModality(Modality.WINDOW_MODAL);
	        popup.setTitle("Trailer");
	        popup.initStyle(StageStyle.TRANSPARENT);

	        // Start fullscreen
	        popup.setWidth(fullWidth);
	        popup.setHeight(fullHeight);
	        popup.setX(0);
	        popup.setY(0);

	        StackPane root = new StackPane();
	        root.setStyle("-fx-background-color: rgba(0,0,0,0.85);");

	        VBox layout = new VBox(15);
	        layout.setStyle("-fx-background-color: rgba(0,0,0,0.2); -fx-background-radius:15; -fx-padding:15; -fx-alignment:center;");
	        layout.setPrefSize(fullWidth, fullHeight);

	        // Resize button
	        Button toggleSize = new Button("🗗");
	        toggleSize.setStyle("-fx-background-color:#008cff;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:50%;-fx-padding:5 8;");
	        addHoverAnimation(toggleSize);

	        // Exit button
	        Button exitButton = new Button("✕");
	        exitButton.setStyle("-fx-background-color:#008cff;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:50%;-fx-padding:5 8;");
	        exitButton.setOnAction(e -> popup.close());
	        addHoverAnimation(exitButton);

	        final boolean[] isFullScreen = {true};

	        toggleSize.setOnAction(e -> {
	            if (isFullScreen[0]) {
	                // SMALL MODE centered
	                popup.setWidth(smallWidth);
	                popup.setHeight(smallHeight);
	                popup.setX((screenBounds.getWidth() - smallWidth) / 2);
	                popup.setY((screenBounds.getHeight() - smallHeight) / 2);
	                layout.setPrefSize(smallWidth, smallHeight);
	                isFullScreen[0] = false;
	            } else {
	                // FULLSCREEN mode (cover full screen)
	                popup.setWidth(fullWidth);
	                popup.setHeight(fullHeight);
	                popup.setX(0);
	                popup.setY(0);
	                layout.setPrefSize(fullWidth, fullHeight);
	                isFullScreen[0] = true;
	            }
	        });

	        // Top bar
	        HBox topBar = new HBox(10, toggleSize, exitButton);
	        topBar.setAlignment(Pos.TOP_RIGHT);
	        topBar.setPadding(new Insets(10));
	        topBar.setPickOnBounds(false); // Fix click issue

	        layout.getChildren().addAll(topBar, webView);
	        root.getChildren().add(layout);

	        Scene scene = new Scene(root);
	        scene.setFill(Color.TRANSPARENT);
	        popup.setScene(scene);

	        popup.setOnHidden(e -> {
	            webView.getEngine().load(null); // stop video
	            if (autoSlide != null) autoSlide.play();
	        });

	        popup.showAndWait();
	    }
	     public void showSeriePopup(FeaturedItem item) {
	            if (item == null || !"serie".equalsIgnoreCase(item.getType())) return;

	            Serie serie;
	            try {
	                serie = featuredService.getFullSerie(item.getSerieId());
	                if (serie == null) return;
	            } catch (SQLException e) {
	                e.printStackTrace();
	                return;
	            }

	            Stage popup = new Stage();
	            popup.initOwner(rootPane.getScene().getWindow());
	            popup.initModality(Modality.WINDOW_MODAL);
	            popup.initStyle(StageStyle.TRANSPARENT);
	            
	            // ROOT STACKPANE
	            StackPane root = new StackPane();
	            root.setStyle("-fx-background-color: rgba(0,0,0,0.0);"); 
	            root.setTranslateY(50); 
	            root.setMaxWidth(1200); 
	            root.setMaxHeight(600);
	            root.setPadding(new Insets(30));
	            
	            // SCROLL PANE
	            ScrollPane scrollPane = new ScrollPane();
	            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	            scrollPane.setStyle("-fx-background: rgba(0,0,0,0.0); -fx-background-color:rgba(0,0,0,0.0);");
	            scrollPane.setFitToHeight(true);
	            scrollPane.setPrefHeight(500);
	            
	            HBox slider = new HBox(40); 
	            slider.setAlignment(Pos.CENTER_LEFT);
	            slider.setPadding(new Insets(50, 50, 50, 50));

	            List<Season> seasons = serie.getSeasons();
	            List<StackPane> cards = new ArrayList<>();
	            final int[] currentIndex = {0};

	            for (int i = 0; i < seasons.size(); i++) {
	                StackPane card = createSeasonCard(serie, i); // <-- pass index now
	                int index = i;

	                card.setOnMouseClicked(e -> {
	                    currentIndex[0] = index;
	                    updateSeasonSlider(cards, currentIndex[0]);
	                    centerSlide(scrollPane, card, slider);
	                });

	                cards.add(card);
	                slider.getChildren().add(card);
	            }

	            scrollPane.setContent(slider);
	            root.getChildren().add(scrollPane);

	            // --- MODERN BUTTONS ---
	            Button left = new Button("<");
	            styleSlideButton(left);
	            left.setOnAction(e -> {
	                if (currentIndex[0] > 0) {
	                    currentIndex[0]--;
	                    updateSeasonSlider(cards, currentIndex[0]);
	                    centerSlide(scrollPane, cards.get(currentIndex[0]), slider);
	                }
	            });

	            Button right = new Button(">");
	            styleSlideButton(right);
	            right.setOnAction(e -> {
	                if (currentIndex[0] < cards.size() - 1) {
	                    currentIndex[0]++;
	                    updateSeasonSlider(cards, currentIndex[0]);
	                    centerSlide(scrollPane, cards.get(currentIndex[0]), slider);
	                }
	            });

	            StackPane.setAlignment(left, Pos.CENTER_LEFT);
	            StackPane.setMargin(left, new Insets(0, 0, 0, 10));
	            StackPane.setAlignment(right, Pos.CENTER_RIGHT);
	            StackPane.setMargin(right, new Insets(0, 10, 0, 0));

	            root.getChildren().addAll(left, right);

	            // Hover effect for buttons
	            root.setOnMouseMoved(e -> {
	                double fadeDuration = 200;
	                if (e.getX() < 150) fadeButton(left, 1, fadeDuration);
	                else fadeButton(left, 0, fadeDuration);

	                if (e.getX() > root.getWidth() - 150) fadeButton(right, 1, fadeDuration);
	                else fadeButton(right, 0, fadeDuration);
	            });

	            // CLOSE BUTTON
	            Button close = new Button("✕");
	            addHoverAnimation(close);
	            close.setStyle("-fx-background-color:#008cff; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:50%; -fx-padding:5 10;");
	            close.setOnAction(e -> popup.close());
	            StackPane.setAlignment(close, Pos.TOP_RIGHT);
	            StackPane.setMargin(close, new Insets(20));
	            scrollPane.setPrefHeight(600); // taller
	            scrollPane.setPrefWidth(1400);  // slightly narrower than full width
	            scrollPane.setMaxWidth(1400);
	            scrollPane.setMaxHeight(600);
	            root.getChildren().add(close);

	            // Center first slide
	            currentIndex[0] = 0;
	            updateSeasonSlider(cards, currentIndex[0]);
	            Platform.runLater(() -> centerSlide(scrollPane, cards.get(0), slider));

	            // Fade in popup
	            root.setOpacity(0);
	            FadeTransition fade = new FadeTransition(Duration.millis(400), root);
	            fade.setToValue(1);
	            fade.play();

	            
	            Scene scene = new Scene(root); // no fixed size
	            scene.setFill(Color.TRANSPARENT);

	            popup.setScene(scene);
	            popup.initOwner(rootPane.getScene().getWindow());
	            popup.initModality(Modality.WINDOW_MODAL);
	            popup.initStyle(StageStyle.TRANSPARENT);

	            // make it full screen relative to owner
	            popup.setWidth(rootPane.getScene().getWidth());
	            popup.setHeight(rootPane.getScene().getHeight());

	            root.setMaxWidth(Double.MAX_VALUE);
	            root.setMaxHeight(Double.MAX_VALUE);
	            root.setPrefWidth(Double.MAX_VALUE);
	            root.setPrefHeight(Double.MAX_VALUE);
	            scene.setFill(Color.TRANSPARENT);
	            popup.setScene(scene);
	            popup.showAndWait();
	            ImageView seriesTitle = new ImageView();
	            seriesTitle.setFitHeight(80);
	            seriesTitle.setPreserveRatio(true);
	            String titleUrl = seasons.size() > 3 
	            	    ? seasons.get(3).getTitleUrl() 
	            	    : seasons.get(0).getTitleUrl();

	            	seriesTitle.setImage(ImageUtil.load(titleUrl));
	            // --- BOTTOM SPACE / Series description ---
	            Label seriesDescription = new Label(serie.getSynopsis() != null ? serie.getSynopsis() : "No description available");
	            seriesDescription.setWrapText(true);
	            seriesDescription.setTextFill(Color.LIGHTGRAY);
	            seriesDescription.setStyle("-fx-font-size:16;");
	            seriesDescription.setMaxWidth(1000);

	            // Wrap the scrollPane with VBox
	            VBox container = new VBox(20); // 20px spacing
	            container.setAlignment(Pos.TOP_CENTER);
	            container.getChildren().addAll(seriesTitle, scrollPane, seriesDescription);

	            // Replace root.getChildren().add(scrollPane) with:
	            root.getChildren().add(container);
	            
	        }

	        // Style buttons for modern look
	        private void styleSlideButton(Button btn) {
	            btn.setStyle("-fx-background-color: transparent;" +
	                         "-fx-text-fill: #00aaff;" +
	                         "-fx-font-size: 36;" +
	                         "-fx-font-weight: bold;" +
	                         "-fx-effect: dropshadow(gaussian, rgba(0,255,255,0.7), 10,0,0,0);");
	            btn.setOpacity(0); // hidden initially
	        }

	        // Fade helper
	      
	    // Helper: fade button to target opacity
	    private void fadeButton(Button button, double targetOpacity, double durationMs) {
	        Timeline timeline = new Timeline(
	                new KeyFrame(Duration.millis(durationMs),
	                        new KeyValue(button.opacityProperty(), targetOpacity, Interpolator.EASE_BOTH))
	        );
	        timeline.play();
	    }
	    // --- Update card scale & opacity ---
	    private void updateSeasonSlider(List<StackPane> cards, int currentIndex) {
	        for (int i = 0; i < cards.size(); i++) {
	            StackPane card = cards.get(i);
	            int offset = i - currentIndex;

	            double scale = 1.0;
	            double opacity = 1.0;

	            if (offset == 0) {
	                scale = 1.1;
	                opacity = 1.0;
	            } else if (Math.abs(offset) == 1) {
	                scale = 0.95;
	                opacity = 0.85;
	            } else if (Math.abs(offset) == 2) {
	                scale = 0.9;
	                opacity = 0.6;
	            } else {
	                scale = 0.85;
	                opacity = 0.4;
	            }

	            Timeline anim = new Timeline(
	                    new KeyFrame(Duration.millis(400),
	                            new KeyValue(card.scaleXProperty(), scale, Interpolator.EASE_BOTH),
	                            new KeyValue(card.scaleYProperty(), scale, Interpolator.EASE_BOTH),
	                            new KeyValue(card.opacityProperty(), opacity, Interpolator.EASE_BOTH)
	                    )
	            );
	            anim.play();
	        }
	    }

	    // --- Center clicked card ---
	    private void centerSlide(ScrollPane scrollPane, StackPane card, HBox slider) {
	        double scrollWidth = slider.getWidth();
	        double scrollPaneWidth = scrollPane.getViewportBounds().getWidth();
	        double cardCenter = card.getBoundsInParent().getMinX() + card.getBoundsInParent().getWidth() / 2.0;
	        double hValue = (cardCenter - scrollPaneWidth / 2) / (scrollWidth - scrollPaneWidth);
	        hValue = Math.min(Math.max(hValue, 0), 1);

	        Timeline anim = new Timeline(
	                new KeyFrame(Duration.millis(400),
	                        new KeyValue(scrollPane.hvalueProperty(), hValue, Interpolator.EASE_BOTH))
	        );
	        anim.play();
	    }
	    
	    private StackPane createSeasonCard(Serie serie, int seasonIndex) {
	        Season s = serie.getSeasons().get(seasonIndex);
	        StackPane card = new StackPane();
	        card.setPrefSize(700, 500);
	        card.setStyle(
	            "-fx-background-color: #111;" +
	            "-fx-background-radius: 12;" +
	            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 15,0.5,0,0);" +
	            "-fx-border-width: 2;" +
	            "-fx-border-radius: 12;"
	        );

	        StackPane contentWrapper = new StackPane();
	        contentWrapper.setPrefSize(700, 500);
	        card.getChildren().add(contentWrapper);

	        // ================= MAIN SEASON VIEW =================
	        HBox content = new HBox(20);
	        content.setPadding(new Insets(15));
	        content.setAlignment(Pos.CENTER);
	        StackPane.setAlignment(content, Pos.CENTER);

	        ImageView poster = new ImageView();
	        poster.setFitWidth(250);
	        poster.setFitHeight(350);
	         poster.setImage(ImageUtil.load(s.getPosterUrl()));

	        VBox infoBox = new VBox(10);
	        infoBox.setAlignment(Pos.CENTER);

	        Label title = new Label("Season " + s.getSeasonNum() + ": " + s.getTitle());
	        title.setStyle("-fx-text-fill:white;-fx-font-size:18;-fx-font-weight:bold;");

	        Label synopsis = new Label(s.getSynopsis() != null ? s.getSynopsis() : "No synopsis available");
	        synopsis.setWrapText(true);
	        synopsis.setMaxWidth(350);
	        synopsis.setStyle("-fx-text-fill:#ccc;-fx-font-size:16;");

	        Label status = new Label("Status: " + (s.getStatus() != null ? s.getStatus() : "Unknown") +
	                " | Episodes: " + (s.getEpisodes() != null ? s.getEpisodes().size() : 0));
	        status.setStyle("-fx-text-fill:#00aaff;");

	        // Stars
	        HBox starsBox = new HBox(3);
	        for (int i = 0; i < 5; i++) {
	            Label star = new Label("★");
	            int rating = s.getRating();
	            String color = i < rating ? "#00bfff" : "#888888"; 
	            star.setStyle("-fx-font-size:18; -fx-text-fill: " + color + ";");
	            starsBox.getChildren().add(star);
	            
	        }

	        // Buttons for trailer and episodes
	        Button trailerBtn = new Button("▶ Watch Trailer");
	        Button episodesBtn = new Button("Episodes");
	        trailerBtn.setFocusTraversable(false);
	        episodesBtn.setFocusTraversable(false);
	        styleModernButton(trailerBtn);
	        styleModernButton(episodesBtn);
	        addHoverAnimation(trailerBtn);
	        addHoverAnimation(episodesBtn);

	        HBox buttonsBox = new HBox(10, trailerBtn, episodesBtn);
	        infoBox.getChildren().addAll(title, synopsis, status, starsBox, buttonsBox);
	        content.getChildren().addAll(poster, infoBox);

	        // ================= EPISODES VIEW =================
	        VBox episodesView = new VBox(15);
	        episodesView.setPadding(new Insets(15));

	        Button backBtn = new Button("← Back");
	        backBtn.setFocusTraversable(false);
	        styleModernButton(backBtn);
	        addHoverAnimation(backBtn);

	        HBox episodesRow = new HBox(8);
	        episodesRow.setPadding(new Insets(10, 0, 10, 0));

	        // ================= EPISODE DETAILS =================
	        VBox episodeDetails = new VBox(20);
	        episodeDetails.setPadding(new Insets(15));
	        episodeDetails.setAlignment(Pos.TOP_CENTER);

	        VBox infoBoxs = new VBox(10);
	        infoBoxs.setAlignment(Pos.TOP_LEFT);

	        Label epTitle = new Label();
	        epTitle.setStyle("-fx-text-fill:white;-fx-font-size:18;-fx-font-weight:bold;");
	        Label epSynopsis = new Label();
	        epSynopsis.setWrapText(true);
	        epSynopsis.setMaxWidth(600);
	        epSynopsis.setStyle("-fx-text-fill:#ccc;-fx-font-size:16;");
	        Label epDuration = new Label();
	        Label epRelease = new Label();

	        Button backToEpisodes = new Button(" ← ");
	        backToEpisodes.setFocusTraversable(false);
	        styleModernButton(backToEpisodes);
	        addHoverAnimation(backToEpisodes);

	        infoBoxs.getChildren().addAll(backToEpisodes, epTitle, epSynopsis, epDuration, epRelease);

	        // Poster with play button overlay
	        ImageView cover = new ImageView();
	        cover.setFitWidth(400);
	        cover.setFitHeight(225);

	        Button play = new Button("▶");
	        play.setFocusTraversable(false);
	        play.setPrefSize(60, 60);
	        play.setStyle(
	            "-fx-background-color: rgba(0,0,0,0.6);" +
	            "-fx-background-radius: 30;" +
	            "-fx-border-radius: 30;" +
	            "-fx-border-color: #00aaff;" +
	            "-fx-border-width: 2;" +
	            "-fx-text-fill: #00aaff;" +
	            "-fx-font-size: 28;" +
	            "-fx-padding: 0;"
	        );

	        StackPane coverStack = new StackPane(cover, play);
	        coverStack.setAlignment(Pos.CENTER);

	        // ---- Hover Animation for BOTH ----
	        ScaleTransition hoverUp = new ScaleTransition(Duration.millis(150), coverStack);
	        hoverUp.setToX(1.05);
	        hoverUp.setToY(1.05);

	        ScaleTransition hoverDown = new ScaleTransition(Duration.millis(150), coverStack);
	        hoverDown.setToX(1.0);
	        hoverDown.setToY(1.0);

	        coverStack.setOnMouseEntered(e -> hoverUp.playFromStart());
	        coverStack.setOnMouseExited(e -> hoverDown.playFromStart());
	        episodeDetails.getChildren().addAll(infoBoxs, coverStack);

	        int userId = Session.getUserId();
	        Map<Integer, WatchStatus> progressMap = episodeProgressService.loadUserProgress(userId);

	        ScrollPane episodesScroll = new ScrollPane();
	        episodesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	        episodesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // hide default scrollbar
	        episodesScroll.setFitToWidth(true);
	        episodesScroll.setPrefHeight(400);
	        episodesScroll.setStyle("-fx-background-color: #111; -fx-background-insets: 0; -fx-padding: 0;");

	        VBox episodesList = new VBox(5);
	        episodesList.setPadding(new Insets(5));
	        episodesScroll.setContent(episodesList);

	        for (Episode ep : s.getEpisodes()) {
	            HBox epRow = new HBox(10);
	            epRow.setAlignment(Pos.CENTER_LEFT);
	            epRow.setPadding(new Insets(10));
	            epRow.setStyle(
	                "-fx-background-color: #1b1b1b;" +
	                "-fx-background-radius: 8;"
	            );

	            // Pump effect on hover
	            ScaleTransition hoverUpRow = new ScaleTransition(Duration.millis(150), epRow);
	            hoverUpRow.setToX(1.03);
	            hoverUpRow.setToY(1.03);
	            ScaleTransition hoverDownRow = new ScaleTransition(Duration.millis(150), epRow);
	            hoverDownRow.setToX(1.0);
	            hoverDownRow.setToY(1.0);
	            epRow.setOnMouseEntered(e -> hoverUpRow.playFromStart());
	            epRow.setOnMouseExited(e -> hoverDownRow.playFromStart());

	            // Episode number
	            Label epNum = new Label(String.valueOf(ep.getNumEpisode()));
	            epNum.setPrefWidth(30);
	            epNum.setStyle("-fx-text-fill:white;-fx-font-weight:bold;");

	            // Episode title
	            Label epName = new Label(ep.getTitle());
	            epName.setStyle("-fx-text-fill:white;-fx-font-size:14;");

	            // Episode rating stars
	           

	            HBox ratingBox = new HBox(2);
	            for (int i = 0; i < 5; i++) {
	            	    Label star = new Label("★");
	            	    int rate = ep.getRating();
	            	    String color = i < rate ? "#00bfff" : "#888888"; // DeepSkyBlue / Gray
	            	 star.setStyle("-fx-font-size:18; -fx-text-fill: " + color + ";");
	                ratingBox.getChildren().add(star);
	            }

	            // Spacer for status
	            Region spacer = new Region();
	            HBox.setHgrow(spacer, Priority.ALWAYS);

	            // Status label
	            WatchStatus statu = progressMap.getOrDefault(ep.getEpId(), WatchStatus.NOT_STARTED);
	            Label epStatus = new Label();
	            epStatus.setText(switch (statu) {
	                case NOT_STARTED -> "Not Started";
	                case IN_PROGRESS -> "In Progress";
	                case COMPLETED -> "Watched";
	            });
	            epStatus.setStyle(
	                "-fx-background-color: " + (statu == WatchStatus.NOT_STARTED ? "#555" : "#00aaff") + ";" +
	                "-fx-text-fill: white;" +
	                "-fx-font-weight: bold;" +
	                "-fx-font-size: 12px;" +
	                "-fx-background-radius: 8;" +
	                "-fx-padding: 2 8;"
	            );

	            // Click episode
	            epRow.setOnMouseClicked(ev -> {
	                try { cover.setImage(ImageUtil.load(ep.getCovertUrl())); } catch (Exception ignored) {}

	                epTitle.setText("Episode " + ep.getNumEpisode() + ": " + ep.getTitle());
	                epSynopsis.setText(ep.getResume());
	                int h = ep.getDuration() / 60;
	                int m = ep.getDuration() % 60;
	                epDuration.setText("Duration: " + (h > 0 ? h + "h " : "") + m + "m");
	                epRelease.setText("Released: " +
	                        (ep.getReleasedAt() != null ? ep.getReleasedAt().toLocalDateTime().toLocalDate() : "Unknown"));

	                // Status tag in details
	                Label statusTag = new Label(epStatus.getText());
	                statusTag.setStyle(
	                    "-fx-background-color: " + (epStatus.getText().equals("Not Started") ? "#555" : "#00aaff") + ";" +
	                    "-fx-text-fill:white;-fx-font-weight:bold;" +
	                    "-fx-font-size:12;" +
	                    "-fx-background-radius:8;" +
	                    "-fx-padding:2 6;"
	                );

	                HBox titleBox = new HBox(10, epTitle, statusTag);
	                titleBox.setAlignment(Pos.CENTER_LEFT);
	                HBox.setHgrow(statusTag, Priority.ALWAYS);

	                infoBoxs.getChildren().setAll(backToEpisodes, titleBox, epSynopsis, epDuration, epRelease);
	                contentWrapper.getChildren().setAll(episodeDetails);

	                // Play button updates status dynamically
	                play.setOnAction(e -> {
	                    goToLecturePageEpisode(s.getSerieId(), s.getSeasonNum(), ep.getNumEpisode());
	                    episodeProgressService.markInProgress(userId, ep.getEpId(), 0);

	                    epStatus.setText("In Progress");
	                    epStatus.setStyle(
	                        "-fx-background-color:#00aaff;" +
	                        "-fx-text-fill:white;" +
	                        "-fx-font-weight:bold;" +
	                        "-fx-font-size:12;" +
	                        "-fx-background-radius:8;" +
	                        "-fx-padding:2 8;"
	                    );
	                });
	            });

	            epRow.getChildren().addAll(epNum, epName, ratingBox, spacer, epStatus);
	            episodesList.getChildren().add(epRow);
	        }

	        
	        episodesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	        episodesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	        episodesScroll.setFitToWidth(true);

	        episodesScroll.setContent(episodesList);

	        // Custom scrollbar
	        ScrollBar customScrollBar = new ScrollBar();
	        customScrollBar.setOrientation(Orientation.VERTICAL);
	        customScrollBar.setMin(0);
	        customScrollBar.setMax(1);
	        customScrollBar.setPrefWidth(8); // thin modern bar
	        // Sync
	        customScrollBar.valueProperty().bindBidirectional(episodesScroll.vvalueProperty());
	        customScrollBar.setStyle("""
	        	    -fx-background-color: transparent;
	        	""");

	        	customScrollBar.lookupAll(".thumb").forEach(node -> {
	        	    node.setStyle("""
	        	        -fx-background-color: rgba(0,170,255,0.6);
	        	        -fx-background-radius: 10;
	        	    """);
	        	});

	        	customScrollBar.lookupAll(".track").forEach(node -> {
	        	    node.setStyle("""
	        	        -fx-background-color: rgba(255,255,255,0.05);
	        	        -fx-background-radius: 10;
	        	    """);
	        	});
	        	episodesScroll.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> updateThumbSize(episodesScroll, customScrollBar));
	        	episodesScroll.contentProperty().addListener((obs, oldVal, newVal) -> updateThumbSize(episodesScroll, customScrollBar));
	        	episodesScroll.setOnScroll(e -> {
	        	    double delta = e.getDeltaY() * 0.002;
	        	    episodesScroll.setVvalue(episodesScroll.getVvalue() - delta);
	        	});
	        	Platform.runLater(() -> {
	        	    Node thumb = customScrollBar.lookup(".thumb");
	        	    Node track = customScrollBar.lookup(".track");

	        	    if (thumb != null) {
	        	        thumb.setStyle("""
	        	            -fx-background-color: linear-gradient(to bottom, #00aaff, #008cff);
	        	            -fx-background-radius: 10;
	        	        """);
	        	    }

	        	    if (track != null) {
	        	        track.setStyle("""
	        	            -fx-background-color: rgba(255,255,255,0.05);
	        	            -fx-background-radius: 10;
	        	        """);
	        	    }
	        	});
	        	customScrollBar.setStyle("""
	        		    -fx-background-color: transparent; /* track background */
	        		    -fx-padding: 0;
	        		""");

	        		// Thumb & track via CSS pseudoclass
	        		customScrollBar.getStylesheets().add(getClass().getResource("/view/css/scrollbar.css").toExternalForm());
	        	Platform.runLater(() -> {
	        		updateThumbSize(episodesScroll, customScrollBar);
	        	    autoHideScrollbar(episodesScroll, customScrollBar);
	        	});
	        	
	        	// Layout
	        HBox scrollContainer = new HBox(5, episodesScroll, customScrollBar);
	        HBox.setHgrow(episodesScroll, Priority.ALWAYS);
	        // Replace original content with scrollContainer
	        contentWrapper.getChildren().setAll(scrollContainer);
	        episodesView.getChildren().addAll(backBtn, scrollContainer);
	        // ================= ACTIONS =================
	        trailerBtn.setOnAction(e -> showTrailerPopup(serie, seasonIndex));
	        episodesBtn.setOnAction(e -> contentWrapper.getChildren().setAll(episodesView));
	        backBtn.setOnAction(e -> contentWrapper.getChildren().setAll(content));
	        backToEpisodes.setOnAction(e -> contentWrapper.getChildren().setAll(episodesView));

	        contentWrapper.getChildren().setAll(content);
	        return card;
	    }
	    private void autoHideScrollbar(ScrollPane scrollPane, ScrollBar scrollBar) {
	        Node content = scrollPane.getContent();
	        if (content == null) return;

	        Runnable check = () -> {
	            double contentHeight = content.getLayoutBounds().getHeight();
	            double viewportHeight = scrollPane.getViewportBounds().getHeight();

	            boolean needScroll = contentHeight > viewportHeight + 1;

	            scrollBar.setVisible(needScroll);
	            scrollBar.setManaged(needScroll);
	        };

	        content.layoutBoundsProperty().addListener((obs, o, n) -> check.run());
	        scrollPane.viewportBoundsProperty().addListener((obs, o, n) -> check.run());

	        Platform.runLater(check);
	    }
	    private void updateThumbSize(ScrollPane scrollPane, ScrollBar scrollBar) {
	    	Node content = scrollPane.getContent();
	        if (content == null) return;

	        content.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
	            double contentHeight = newBounds.getHeight();
	            double viewportHeight = scrollPane.getViewportBounds().getHeight();

	            if (contentHeight <= 0) return;

	            double ratio = viewportHeight / contentHeight;

	            // clamp
	            ratio = Math.max(0.05, Math.min(ratio, 1.0));

	            scrollBar.setVisibleAmount(ratio);
	        });

	        scrollPane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
	            double contentHeight = content.getLayoutBounds().getHeight();
	            double viewportHeight = newVal.getHeight();

	            if (contentHeight <= 0) return;

	            double ratio = viewportHeight / contentHeight;
	            ratio = Math.max(0.05, Math.min(ratio, 1.0));

	            scrollBar.setVisibleAmount(ratio);
	        });
	    }
	    private void styleModernButton(Button btn) {
	        btn.setStyle(
	            "-fx-background-color: #0f172a;" +          // dark navy
	            "-fx-text-fill: #38bdf8;" +                 // neon blue
	            "-fx-font-weight: bold;" +
	            "-fx-background-radius: 8;" +
	            "-fx-border-radius: 8;" +
	            "-fx-border-color: #38bdf8;" +
	            "-fx-border-width: 1.5;" +
	            "-fx-padding: 6 14;" +
	            "-fx-cursor: hand;"
	        );


	    }
	    

	    	private void goToLecturePageEpisode(int serieId, int seasonNum, int episodeNum) {
	    	    try {
	    	    	FXMLLoader loader = new FXMLLoader(); loader.setLocation(getClass().getClassLoader().getResource("view/fxml/LecturePage.fxml")); Parent root = loader.load();
	    	        LecturePageController controller = loader.getController();
	    	        controller.initEpisode(serieId, seasonNum, episodeNum);

	    	        Stage stage = new Stage();
	    	        stage.initOwner(rootPane.getScene().getWindow());
	    	        stage.initModality(Modality.WINDOW_MODAL);
	    	        stage.setScene(new Scene(root));
	    	        stage.setTitle("Lecture: Episode");
	    	        stage.show();
	    	    } catch (IOException e) {
	    	        e.printStackTrace();
	    	    }
	    	}
	    	 public void showPopup(FeaturedItem item) {
	    	        CardController controller = new CardController();
	    	        if (item.getType().equalsIgnoreCase("film")) {
	    	            controller.showFilmPopup(item);
	    	        } else if (item.getType().equalsIgnoreCase("serie")) {
	    	            controller.showSeriePopup(item);
	    	        }
	    	    }
	    
	    	 
}