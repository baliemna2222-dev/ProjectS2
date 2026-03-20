package JStream.controller;

import JStream.entity.Category;
import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.Film;
import JStream.entity.Season;
import JStream.entity.Serie;
import JStream.entity.Session;
import JStream.service.FeaturedService;
import JStream.service.MylistService;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeaturedController {

    @FXML private StackPane rootPane;
    @FXML private ImageView heroBackground;
    @FXML private ImageView heroTitleImage;
    @FXML private Label heroDescription;
    @FXML private HBox heroStars;
    @FXML private Label heroCategories;
    @FXML private Label heroAge;
    @FXML private Label lastEpisodeLabel;    // Only for series
    @FXML private Label seriesStatusLabel;   // Only for series
    @FXML private Button playButton;
    @FXML private Button watchTrailerButton;
    @FXML private Button addToListButton;
    @FXML public Rectangle rect1, rect2, rect3, rect4, rect5;
    
    private FeaturedService featuredService;
    private List<FeaturedItem> latestItems;
    private int currentIndex = 0;
    private Timeline autoSlide;
    private MylistService myListService;
    private FeaturedItem currentItem;
    
    @FXML
    public void initialize() {
        featuredService = new FeaturedService();
        myListService = new MylistService();
        setupCarousel();
    }
    private void handleAddToList() {
        if (currentItem == null) return;

        int userId = Session.getUserId();
        int filmId = currentItem.getType().equalsIgnoreCase("film") ? currentItem.getId() : 0;
        int serieId = currentItem.getType().equalsIgnoreCase("serie") ? currentItem.getSerieId() : 0;

        boolean alreadyAdded = myListService.isInList(userId, filmId, serieId);

        if (alreadyAdded) {
            myListService.removeItem(userId, filmId, serieId);
        } else {
            myListService.addItem(userId, filmId, serieId);
        }

        updateAddButton(addToListButton,currentItem);
    }
   
    private void updateAddButton(Button button, FeaturedItem item) {
        if(item == null) return;

        int userId = Session.getUserId();
        int filmId = item.getType().equalsIgnoreCase("film") ? item.getId() : 0;
        int serieId = item.getType().equalsIgnoreCase("serie") ? item.getSerieId() : 0;

        boolean alreadyAdded = myListService.isInList(userId, filmId, serieId);

        if(alreadyAdded) {
            button.setText("✔ Added");
            button.setStyle("-fx-background-color:#00aaff;-fx-font-weight:bold;-fx-text-fill:white;-fx-font-size:16;-fx-padding:6 25;-fx-background-radius:2;");
        } else {
            button.setText("➕ My List");
            button.setStyle("-fx-background-color:transparent;-fx-border-color:#00aaff;-fx-border-width:2;-fx-text-fill:#00aaff;-fx-font-size:16;-fx-padding:6 20;-fx-background-radius:2;");
        }
    }
    private void setupCarousel() {
        try {
            latestItems = featuredService.getLatestFeatured(5);
            if (latestItems == null || latestItems.isEmpty()) return;

            Rectangle[] rects = {rect1, rect2, rect3, rect4, rect5};
            highlightAndShow(rects, 0);

            for (int i = 0; i < rects.length; i++) {
                final int index = i;
                rects[i].setOnMouseClicked(e -> {
                    highlightAndShow(rects, index);
                    if (autoSlide != null) autoSlide.playFromStart();
                });
            }

            autoSlide = new Timeline(new KeyFrame(Duration.seconds(7), e -> {
                currentIndex = (currentIndex + 1) % latestItems.size();
                highlightAndShow(rects, currentIndex);
            }));
            autoSlide.setCycleCount(Timeline.INDEFINITE);
            autoSlide.play();

            // --- Add hover "pump" effect to all hero buttons ---
            Button[] buttons = {playButton, watchTrailerButton, addToListButton};
            for (Button btn : buttons) {
                btn.setOnMouseEntered(e -> {
                    autoSlide.pause(); // pause carousel on hover
                    ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
                    st.setToX(1.08);
                    st.setToY(1.08);
                    st.play();
                });
                btn.setOnMouseExited(e -> {
                    autoSlide.play(); // resume carousel
                    ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
                    st.setToX(1.0);
                    st.setToY(1.0);
                    st.play();
                });
            }

            watchTrailerButton.setOnAction(e -> {
                if (autoSlide != null) autoSlide.pause();
                showTrailerPopup();
            });
            playButton.setOnAction(e -> {
                if (autoSlide != null) autoSlide.pause(); // pause carousel

                // Open the correct popup based on type
                String type = currentItem.getType().toLowerCase();
                if (type.equals("film")) {
                    showFilmPopup(currentItem);    // open film popup
                } else if (type.equals("serie")) {
                    showSeriePopup(currentItem);   // open series popup
                }
            });
            addToListButton.setOnAction(e -> handleAddToList());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayFeatured(FeaturedItem item) {
        // Background
        try {
            if (item.getMainImageUrl() != null && !item.getMainImageUrl().isEmpty())
                heroBackground.setImage(new javafx.scene.image.Image(item.getMainImageUrl()));
            else
                heroBackground.setImage(null); // placeholder
        } catch (Exception e) { heroBackground.setImage(null); }

        // Title image
        try {
            if (item.getTitleImageUrl() != null && !item.getTitleImageUrl().isEmpty()) {
                heroTitleImage.setImage(new javafx.scene.image.Image(item.getTitleImageUrl()));
                heroTitleImage.setVisible(true);
            } else {
                heroTitleImage.setImage(null);
                heroTitleImage.setVisible(false);
            }
        } catch (Exception e) { heroTitleImage.setVisible(false); }

        // Description always
        heroDescription.setText(item.getSynopsis() != null ? item.getSynopsis() : "");

        // Categories
        heroCategories.setText(item.getCategoriesAsString() != null ? item.getCategoriesAsString() : "");

        // Age rating
        heroAge.setText(item.getAgeRating() != null ? item.getAgeRating() : "");
        heroAge.setVisible(item.getAgeRating() != null && !item.getAgeRating().isEmpty());

        // Stars
        heroStars.getChildren().clear();
        int fullStars = item.getRating();
        for (int i = 0; i < 5; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-font-size:20;-fx-font-weight:bold;");
            star.setTextFill(i < fullStars ? Color.DEEPSKYBLUE : Color.LIGHTGRAY);
            heroStars.getChildren().add(star);
        }

        // Series labels
        if ("serie".equalsIgnoreCase(item.getType())) {
            lastEpisodeLabel.setText("S" + item.getSeasonNumber() + ":E" + item.getLastEpisodeNumber());
            seriesStatusLabel.setText(item.getSeasonStatus());
            lastEpisodeLabel.setVisible(true);
            seriesStatusLabel.setVisible(true);
        } else {
            lastEpisodeLabel.setVisible(false);
            seriesStatusLabel.setVisible(false);
        }

        // Trailer button
        watchTrailerButton.setVisible(item.getTrailerUrl() != null && !item.getTrailerUrl().isEmpty());
    
        currentItem = item;
        updateAddButton(addToListButton,currentItem);
    }

    private void highlightAndShow(Rectangle[] rects, int index) {
        currentIndex = index;

        ParallelTransition indicatorAnim = new ParallelTransition();
        for (int i = 0; i < rects.length; i++) {
            Rectangle rect = rects[i];
            FillTransition fill = new FillTransition(Duration.millis(250), rect);
            fill.setToValue(i == index ? Color.WHITE : Color.web("#888888"));
            ScaleTransition scale = new ScaleTransition(Duration.millis(250), rect);
            scale.setToX(i == index ? 1.5 : 1.0);
            scale.setToY(i == index ? 1.5 : 1.0);
            indicatorAnim.getChildren().addAll(fill, scale);
        }
        indicatorAnim.play();

        if (latestItems == null || index >= latestItems.size()) return;
        FeaturedItem nextItem = latestItems.get(index);

        Node[] contentNodes = {heroTitleImage, heroDescription, heroStars, heroCategories,
                heroAge, lastEpisodeLabel, seriesStatusLabel, playButton, watchTrailerButton, addToListButton};

        // --- Fade out old content & background ---
        ParallelTransition fadeOut = new ParallelTransition();
        FadeTransition bgFadeOut = new FadeTransition(Duration.millis(400), heroBackground);
        bgFadeOut.setToValue(0);
        fadeOut.getChildren().add(bgFadeOut);

        for (Node node : contentNodes) {
            TranslateTransition slideLeft = new TranslateTransition(Duration.millis(700), node);
            slideLeft.setByX(-200); // slide left while fading
            FadeTransition fade = new FadeTransition(Duration.millis(700), node);
            fade.setToValue(0);
            fadeOut.getChildren().addAll(slideLeft, fade);
        }

        fadeOut.setOnFinished(e -> {
            // --- Update content after fade out ---
            displayFeatured(nextItem);

            // --- Reset positions & opacity for slide-in ---
            heroBackground.setOpacity(0);
            for (Node node : contentNodes) {
                node.setOpacity(0);
                node.setTranslateX(700); // start from right
            }

            // --- Fade in background ---
            FadeTransition bgFadeIn = new FadeTransition(Duration.millis(600), heroBackground);
            bgFadeIn.setToValue(1);

            // --- Slide in text/content from right with fade ---
            ParallelTransition slideIn = new ParallelTransition();
            for (Node node : contentNodes) {
                TranslateTransition slide = new TranslateTransition(Duration.millis(600), node);
                slide.setToX(0); // move to original position
                FadeTransition fade = new FadeTransition(Duration.millis(600), node);
                fade.setToValue(1);
                slideIn.getChildren().addAll(slide, fade);
            }

           
            new ParallelTransition(bgFadeIn, slideIn).play();
        });

        fadeOut.play();
    }
    private void showTrailerPopup(Object item, int seasonIndex) {

        String trailerUrl = null;
        

        // 🎬 FILM
        if (item instanceof Film) {
            Film film = (Film) item;
            trailerUrl = film.getVideo_url();
            
        }

        // 📺 SERIE → use selected season
        else if (item instanceof Serie) {
            Serie serie = (Serie) item;

            if (serie.getSeasons() != null && !serie.getSeasons().isEmpty()) {

                // ✅ secure index
                if (seasonIndex < 0 || seasonIndex >= serie.getSeasons().size()) {
                    seasonIndex = 0; // fallback
                }

                Season season = serie.getSeasons().get(seasonIndex);

                trailerUrl = season.getTrailerUrl();
                
            }
        }

        
        // --- Get resource URL properly ---
        URL videoUrl = getClass().getResource(trailerUrl);
        if (videoUrl == null) {
            System.out.println("Video file not found: " + trailerUrl);
            return;
        }
      
        String videoPath;
        try {
            if (trailerUrl.startsWith("http://") || trailerUrl.startsWith("https://")) {
                videoPath = trailerUrl; // use directly for web URLs
            } else {
              
                URL resourceUrl = getClass().getResource(trailerUrl);
                if (resourceUrl == null) {
                    System.out.println("Video file not found: " + trailerUrl);
                    videoPath = null;
                } else {
                    videoPath = resourceUrl.toExternalForm();
                }
            }
        } catch (Exception ex) {
            System.out.println("Error loading video: " + ex.getMessage());
            videoPath = null;
        }
        WebView webView = new WebView();
        webView.setPrefSize(800, 500);

        String html =
            "<html><body style='margin:0; background:black;'>" +
            "<video width='100%' height='100%' controls autoplay>" +
            "<source src='" + videoPath + "' type='video/mp4'>" +
            "Your browser does not support the video tag." +
            "</video></body></html>";

        webView.getEngine().loadContent(html);

        Stage popup = new Stage();
        popup.initOwner(rootPane.getScene().getWindow());
        popup.initModality(Modality.WINDOW_MODAL);
        popup.setTitle("Trailer - " + currentItem.getTitle());
        popup.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.85);");

        VBox layout = new VBox(15);
        layout.setStyle("-fx-background-color: rgba(0,0,0,0.2); -fx-background-radius:15; -fx-padding:15; -fx-alignment:center;");
        layout.setPrefSize(900, 600);

        Button exitButton = new Button("✕");
        exitButton.setStyle("-fx-background-color:#008cff;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:50%;-fx-padding:5 8;");
        exitButton.setOnAction(e -> popup.close());
        addHoverAnimation(exitButton);
        HBox topBar = new HBox(exitButton);
        topBar.setAlignment(Pos.TOP_RIGHT);
        layout.getChildren().addAll(topBar, webView);
        root.getChildren().add(layout);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        popup.setScene(scene);
        popup.setOnHidden(e -> {
            webView.getEngine().load(null); // stop video
            if (autoSlide != null) autoSlide.play(); // resume carousel
        });

        popup.showAndWait();
    }

    private void showTrailerPopup() {
        if (latestItems == null || latestItems.isEmpty()) return;
        FeaturedItem currentItem = latestItems.get(currentIndex);

        // Check if trailer exists
        if (currentItem.getTrailerUrl() == null || currentItem.getTrailerUrl().isEmpty()) {
            System.out.println("No trailer available for " + currentItem.getTitle());
            return;
        }

        URL videoUrl = getClass().getResource(currentItem.getTrailerUrl());
        if (videoUrl == null) return;

        WebView webView = new WebView();
        webView.setPrefSize(800, 500);

        String html =
            "<html><body style='margin:0; background:black;'>" +
            "<video width='100%' height='100%' controls autoplay>" +
            "<source src='" + videoUrl.toExternalForm() + "' type='video/mp4'>" +
            "Your browser does not support the video tag." +
            "</video></body></html>";

        webView.getEngine().loadContent(html);

        Stage popup = new Stage();
        popup.initOwner(rootPane.getScene().getWindow());
        popup.initModality(Modality.WINDOW_MODAL);
        popup.setTitle("Trailer - " + currentItem.getTitle());
        popup.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.85);");

        VBox layout = new VBox(15);
        layout.setStyle("-fx-background-color: rgba(0,0,0,0.2); -fx-background-radius:15; -fx-padding:15; -fx-alignment:center;");
        layout.setPrefSize(900, 600);

        Button exitButton = new Button("✕");
        exitButton.setStyle("-fx-background-color:#008cff;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:50%;-fx-padding:5 8;");
        exitButton.setOnAction(e -> popup.close());
        addHoverAnimation(exitButton);
        HBox topBar = new HBox(exitButton);
        topBar.setAlignment(Pos.TOP_RIGHT);

        
        Button addToList = new Button("➕ My List");
        addToList.setStyle("-fx-background-color:transparent;-fx-border-color:#00aaff;-fx-border-width:2;-fx-text-fill:#00aaff;-fx-font-size:16;-fx-padding:6 20;-fx-background-radius:4;");

        // --- Add hover "pump" effect to popup buttons ---
        Button[] popupButtons = {addToList};
        for (Button btn : popupButtons) {
            btn.setOnMouseEntered(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
                st.setToX(1.08);
                st.setToY(1.08);
                st.play();
            });
            btn.setOnMouseExited(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            });
        }
     // Initialize popup "Add to List" button state
        updateAddButton(addToList, currentItem);

        // Handle click
        addToList.setOnAction(e -> {
            int userId = Session.getUserId();
            int filmId = currentItem.getType().equalsIgnoreCase("film") ? currentItem.getId() : 0;
            int serieId = currentItem.getType().equalsIgnoreCase("serie") ? currentItem.getSerieId() : 0;

            boolean alreadyAdded = myListService.isInList(userId, filmId, serieId);

            if (alreadyAdded) {
                myListService.removeItem(userId, filmId, serieId);
            } else {
                myListService.addItem(userId, filmId, serieId);
            }

            // Update both popup and main button
            updateAddButton(addToList, currentItem);
            updateAddButton(addToListButton,this.currentItem);
        });

        HBox buttons = new HBox(10, addToList);
        buttons.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(topBar, webView, buttons);
        root.getChildren().add(layout);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        popup.setScene(scene);
        popup.setOnHidden(e -> {
            webView.getEngine().load(null); // stop video
            if (autoSlide != null) autoSlide.play(); // resume carousel
        });

        popup.showAndWait();
    }
    private void showFilmPopup(FeaturedItem item) {
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
                try { poster.setImage(new Image(film.getPoster_url())); } catch (Exception ex) {}

                // Right info column
                VBox right = new VBox(15);
                right.setAlignment(Pos.TOP_LEFT);
                right.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(right, Priority.ALWAYS);

                // Title image
                ImageView titleImage = new ImageView();
                titleImage.setFitHeight(150);
                titleImage.setPreserveRatio(true);
                try { titleImage.setImage(new Image(film.getTitle_image_url())); } catch (Exception ex) {}

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
  
        private void showSeriePopup(FeaturedItem item) {
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
            try {
                seriesTitle.setImage(new Image(seasons.get(3).getTitleUrl()));
            } catch (Exception e) {}

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
        try { poster.setImage(new Image(s.getPosterUrl())); } catch (Exception ignored) {}

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
            star.setStyle("-fx-font-size:18;");
            star.setTextFill(i < s.getRating() ? Color.DEEPSKYBLUE : Color.GRAY);
            starsBox.getChildren().add(star);
        }

        // Buttons
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
        episodesRow.setPadding(new Insets(10,0,10,0));

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

        // ================= EPISODE BUTTONS =================
        for (Episode ep : s.getEpisodes()) {
            Button epBtn = new Button("" + ep.getNumEpisode());
            epBtn.setPrefSize(40, 40);
            epBtn.setFocusTraversable(false);
            styleModernButton(epBtn);
            addHoverAnimation(epBtn);

            epBtn.setOnAction(ev -> {
                try { cover.setImage(new Image(ep.getCovertUrl())); } catch (Exception ignored) {}
                epTitle.setText("Episode " + ep.getNumEpisode() + ": " + ep.getTitle());
                epSynopsis.setText(ep.getResume());
                int h = ep.getDuration() / 60;
                int m = ep.getDuration() % 60;
                epDuration.setText("Duration: " + (h > 0 ? h + "h " : "") + m + "m");
                epRelease.setText("Released: " + 
                    (ep.getReleasedAt() != null ? ep.getReleasedAt().toLocalDateTime().toLocalDate() : "Unknown"));
                contentWrapper.getChildren().setAll(episodeDetails);
                play.setOnAction(e -> {
                    // optional
                   goToLecturePageEpisode(s.getSerieId(),s.getSeasonNum(),ep.getNumEpisode()); // <-- method to open lecture page
               });
            });

            episodesRow.getChildren().add(epBtn);
        }

        episodesView.getChildren().addAll(backBtn, episodesRow);

        // ================= ACTIONS =================
     // Set trailer button action
        trailerBtn.setOnAction(e -> {
            // Pass the serie and seasonIndex
            showTrailerPopup(serie, seasonIndex);
        });
        episodesBtn.setOnAction(e -> contentWrapper.getChildren().setAll(episodesView));
        backBtn.setOnAction(e -> contentWrapper.getChildren().setAll(content));
        backToEpisodes.setOnAction(e -> contentWrapper.getChildren().setAll(episodesView));

       
        contentWrapper.getChildren().setAll(content);
        return card;
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
    	// ----- Helper: Hover animation for buttons -----
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
    }