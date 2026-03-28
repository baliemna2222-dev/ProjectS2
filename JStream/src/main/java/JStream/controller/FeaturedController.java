package JStream.controller;

import JStream.entity.Category;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
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
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeaturedController {

    @FXML private StackPane rootPane;
    @FXML private ImageView heroBackground;
    @FXML private ImageView heroTitleImage;
    @FXML private Label heroDescription;
    @FXML private HBox heroStars;
    @FXML private HBox typeTag;    
    @FXML private Label heroCategories;
    @FXML private Label heroAge;
    @FXML private Label lastEpisodeLabel;    // Only for series
    @FXML private Label seriesStatusLabel;   // Only for series
    @FXML private Button playButton;
    @FXML private Button watchTrailerButton;
    @FXML private Button addToListButton;
    @FXML public Rectangle rect1, rect2, rect3, rect4, rect5;
    @FXML public Label typeLabel;
    @FXML private Rectangle typeLine;
    private FeaturedService featuredService;
    private List<FeaturedItem> latestItems;
    private int currentIndex = 0;
    private Timeline autoSlide;
    private MylistService myListService;
    private FeaturedItem currentItem;
    private EpisodeProgressService episodeProgressService = new EpisodeProgressService();
    FilmProgressService filmProgressService = new FilmProgressService();
    @FXML
    public void initialize() {
        featuredService = new FeaturedService();
        myListService = new MylistService();
        
        MyListManager.getInstance().addListener((filmId, serieId) -> {
            Platform.runLater(() -> {
                if (currentItem != null) {
                    int currentFilmId = "film".equalsIgnoreCase(currentItem.getType()) ? currentItem.getId() : 0;
                    int currentSerieId = "serie".equalsIgnoreCase(currentItem.getType()) ? currentItem.getSerieId() : 0;

                    // If the updated item matches this controller's current item, refresh the button
                    if (currentFilmId == filmId && currentSerieId == serieId) {
                        updateAddButton(addToListButton, currentItem);
                    }
                }
            });
        });
        setupCarousel();
        DropShadow soft = new DropShadow();
        soft.setColor(Color.BLACK);
        soft.setRadius(5);
        soft.setOffsetX(1);
        soft.setOffsetY(1);

        typeLabel.setEffect(soft);
    }
   
    private void pumpButton(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }
    private void handleAddToList() {
        if (currentItem == null) return;

        int userId = Session.getUserId();
        int filmId = "film".equalsIgnoreCase(currentItem.getType()) ? currentItem.getId() : 0;
        int serieId = "serie".equalsIgnoreCase(currentItem.getType()) ? currentItem.getSerieId() : 0;

        boolean alreadyAdded = myListService.isInList(userId, filmId, serieId);

        if (alreadyAdded) {
            myListService.removeItem(userId, filmId, serieId);
        } else {
            myListService.addItem(userId, filmId, serieId);
        }

        // Update this controller's button immediately
        updateAddButton(addToListButton, currentItem);

        // Notify all other controllers to update
        MyListManager.getInstance().notifyItemUpdated(filmId, serieId);
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
            pumpButton(button);
        } else {
            button.setText("➕ My List");
            button.setStyle("-fx-background-color:transparent;-fx-border-color:#00aaff;-fx-border-width:2;-fx-text-fill:#00aaff;-fx-font-size:16;-fx-padding:6 20;-fx-background-radius:2;");
            pumpButton(button);
        }
    }
    private void setupCarousel() {
        try {
            latestItems = featuredService.getLatestFeatured(4);
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
    	// Hero background
    	heroBackground.setImage(ImageUtil.load(item.getMainImageUrl()));

    	// Title image
    	if (item.getTitleImageUrl() != null && !item.getTitleImageUrl().isEmpty()) {
    	    heroTitleImage.setImage(ImageUtil.load(item.getTitleImageUrl()));
    	    heroTitleImage.setVisible(true);
    	} else {
    	    heroTitleImage.setImage(ImageUtil.load(null)); // safe fallback
    	    heroTitleImage.setVisible(false);
    	}

        // Description always
        heroDescription.setText(item.getSynopsis() != null ? item.getSynopsis() : "");

        // Categories
        heroCategories.setText(item.getCategoriesAsString() != null ? item.getCategoriesAsString() : "");

        // Age rating
        String age = item.getAgeRating();
        boolean hasAge = age != null && !age.isBlank();

        heroAge.setText(hasAge ? age : "");
        heroAge.setVisible(hasAge);
        heroAge.setManaged(hasAge);
        // Stars
        heroStars.getChildren().clear();
        int fullStars = item.getRating(); // 0-5

        for (int i = 0; i < 5; i++) {
            Text star = new Text("★");
            star.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");
            star.setFill(i < fullStars ? Color.DEEPSKYBLUE : Color.LIGHTGRAY); // use setFill for Text
            heroStars.getChildren().add(star);
        }
        typeLabel.setText("     "+item.getType().toUpperCase());
        
        // Series labels
        boolean isSerie = "serie".equalsIgnoreCase(item.getType());

     // Last Episode
     String lastEp = isSerie 
         ? "S" + item.getSeasonNumber() + ":E" + item.getLastEpisodeNumber()
         : "";

     boolean hasLastEp = isSerie 
         && item.getSeasonNumber() != 0 
         && item.getLastEpisodeNumber() != 0;

     lastEpisodeLabel.setText(hasLastEp ? lastEp : "");
     lastEpisodeLabel.setVisible(hasLastEp);
     lastEpisodeLabel.setManaged(hasLastEp);

     // Series Status
     String status = item.getSeasonStatus();
     boolean hasStatus = isSerie && status != null && !status.isBlank();

     seriesStatusLabel.setText(hasStatus ? status : "");
     seriesStatusLabel.setVisible(hasStatus);
     seriesStatusLabel.setManaged(hasStatus);

        // Trailer button
     boolean hasTrailer = item.getTrailerUrl() != null && !item.getTrailerUrl().isBlank();
     watchTrailerButton.setVisible(true);   // always visible
     watchTrailerButton.setManaged(true);   // ensure layout includes it
     watchTrailerButton.setDisable(!hasTrailer); // disable if no trailer
        currentItem = item;
        updateAddButton(addToListButton,currentItem);
    }

    private void highlightAndShow(Rectangle[] rects, int index) {
        currentIndex = index;

        // --- Indicator animation ---
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

        // --- Nodes that fade + slide ---
        Node[] contentNodes = {heroTitleImage, heroDescription, heroStars, heroCategories,
                heroAge, lastEpisodeLabel, seriesStatusLabel, playButton, watchTrailerButton, addToListButton};

        // --- Fade out old content & background ---
        ParallelTransition fadeOut = new ParallelTransition();
        FadeTransition bgFadeOut = new FadeTransition(Duration.millis(400), heroBackground);
        bgFadeOut.setToValue(0);
        fadeOut.getChildren().add(bgFadeOut);

        for (Node node : contentNodes) {
            TranslateTransition slideLeft = new TranslateTransition(Duration.millis(700), node);
            slideLeft.setByX(-200);
            FadeTransition fade = new FadeTransition(Duration.millis(700), node);
            fade.setToValue(0);
            fadeOut.getChildren().addAll(slideLeft, fade);
        }

        // --- Slide out typeTag HBox separately (left → right, no fade) ---
        TranslateTransition typeSlideOut = new TranslateTransition(Duration.millis(700), typeTag);
        typeSlideOut.setByX(200); // move right
        typeSlideOut.play();

        fadeOut.play();

        fadeOut.setOnFinished(e -> {
            // --- Update content after fade out ---
            displayFeatured(nextItem);

            // --- Reset positions & opacity for slide-in ---
            heroBackground.setOpacity(0);
            for (Node node : contentNodes) {
                node.setOpacity(0);
                node.setTranslateX(-700); // <-- slide in from LEFT now
            }

            // typeTag HBox starts left for slide-in
            typeTag.setTranslateX(200);

            // --- Fade in background ---
            FadeTransition bgFadeIn = new FadeTransition(Duration.millis(600), heroBackground);
            bgFadeIn.setToValue(1);

            // --- Slide in other content (with fade) ---
            ParallelTransition slideIn = new ParallelTransition();
            for (Node node : contentNodes) {
                TranslateTransition slide = new TranslateTransition(Duration.millis(600), node);
                slide.setToX(0); // move to original position
                FadeTransition fade = new FadeTransition(Duration.millis(600), node);
                fade.setToValue(1);
                slideIn.getChildren().addAll(slide, fade);
            }

            // --- Slide in typeTag HBox separately (no fade) ---
            TranslateTransition typeSlideIn = new TranslateTransition(Duration.millis(600), typeTag);
            typeSlideIn.setToX(0);

            // --- Play all together ---
            new ParallelTransition(bgFadeIn, slideIn, typeSlideIn).play();
        });
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
        webView.setPrefSize(1500, 700);

        // Video HTML with controls (autoplay optional)
        String html =
            "<html><body style='margin:0; background:black;'>" +
            "<video width='100%' height='100%' controls>" +
            "<source src='" + videoUrl.toExternalForm() + "' type='video/mp4'>" +
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
        popup.setTitle("Trailer - " + currentItem.getTitle());
        popup.initStyle(StageStyle.TRANSPARENT);

        // Start fullscreen (cover whole screen)
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

        // Top bar with buttons
        HBox topBar = new HBox(10, toggleSize, exitButton);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(10));
        topBar.setPickOnBounds(false); // Important for click

        // Add to list button
        Button addToList = new Button("➕ My List");
        addToList.setStyle("-fx-background-color:transparent;-fx-border-color:#00aaff;-fx-border-width:2;-fx-text-fill:#00aaff;-fx-font-size:16;-fx-padding:6 20;-fx-background-radius:4;");

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

        updateAddButton(addToList, currentItem);
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

            updateAddButton(addToList, currentItem);
            updateAddButton(addToListButton, this.currentItem);
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
            if (autoSlide != null) autoSlide.play();
        });

        popup.showAndWait();
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
            int rating = (int) Math.round(ep.getRating());

            HBox ratingBox = new HBox(2);
            for (int i = 0; i < 5; i++) {
                Label star = new Label("★");
                star.setStyle("-fx-font-size:12;");
                star.setTextFill(i < rating ? Color.DEEPSKYBLUE : Color.GRAY);
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
                try { cover.setImage(new Image(ep.getCovertUrl())); } catch (Exception ignored) {}

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