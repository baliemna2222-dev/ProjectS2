package JStream.controller;

import java.net.URL;
import java.sql.SQLException;

import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.Film;
import JStream.entity.MyListManager;
import JStream.entity.Season;
import JStream.entity.Serie;
import JStream.entity.Session;
import JStream.service.FeaturedService;
import JStream.service.MylistService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class LecturePageController {

    @FXML private String currentTrailerUrl;
    @FXML private Button addToListButton;
    private MylistService mylistService = new MylistService();
    private FeaturedItem currentItem;

    // --- NAVBAR ---
    @FXML private ImageView logoNav, bellIcon;
    @FXML private Button btnMostWatched, btnMyList;
    @FXML private StackPane bellContainer;
    @FXML private Circle notificationCircle;
    @FXML private Button btnBack, playButton, btnNotification, profileBtn;

    // --- HERO & CONTAINERS ---
    @FXML private VBox mainContainer;
    @FXML private ImageView backgroundImage, posterImage;
    @FXML private Label titleLabel, scoreLabel, yearLabel, durationLabel,
                        ageRatingLabel, descriptionLabel;
    @FXML private Label starringLabel, directorLabel, categoriesLabel, episodeInfoLabel;
    @FXML private HBox starsBox, castBox;

    // --- TABS ---
    @FXML private Rectangle lineOverview, lineTrailers;
    @FXML private Button tabOverview, tabTrailers;

    private AudioClip bellSound;
    private Popup notificationPopup = new Popup();
    private VBox notificationContent = new VBox();
    private boolean isNotificationVisible = false;

    // --- Episode context (kept for VideoPlayer callbacks) ---
    private Serie  currentSerie;
    private Episode currentEpisode;
    private int     currentSeasonNum;

    // ============================================================
    //  INITIALIZE
    // ============================================================
    @FXML
    public void initialize() {
        setupNavbar();
        setupNotificationSystem();
        setupTabLogic();

        if (btnBack != null) btnBack.setOnAction(e -> handleBackAction());

        if (btnNotification != null) {
            btnNotification.setOnAction(e -> {
                if (isNotificationVisible) hideNotification();
                showPopup();
            });
        }

        if (mainContainer != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), mainContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }

        populateStars(0);

        if (addToListButton != null) addToListButton.setOnAction(e -> handleAddToList());
        if (posterImage  != null) addHoverEffect(posterImage);
        if (playButton   != null) addHoverEffect(playButton);
        if (btnBack      != null) addButtonInteractions(btnBack);

        if (btnMostWatched != null) {
            addButtonInteractions(btnMostWatched);
            btnMostWatched.setOnAction(e -> navigateTo("/view/fxml/MyHistory.fxml"));
        }

        if (btnMyList != null) {
            addButtonInteractions(btnMyList);
            btnMyList.setOnAction(e -> navigateTo("/view/fxml/MyList.fxml"));
        }

        loadCast();
    }

    // ============================================================
    //  INIT — FILM
    // ============================================================
    public void initFilm(int filmId) {
        if (episodeInfoLabel != null) episodeInfoLabel.setVisible(false);

        try {
            Film film = new FeaturedService().getFilmDetails(filmId);
            if (film == null) return;

            this.currentTrailerUrl = film.getVideo_url();
            this.currentItem = new FeaturedItem(
                film.getFilm_id(),
                film.getTitle(),
                film.getSynopsis(),
                film.getVideo_url(),
                film.getImage_url(),
                film.getTitle_image_url(),
                film.getPoster_url(),
                film.getCategories() != null
                    ? film.getCategories().stream()
                          .map(c -> c.getName())
                          .collect(java.util.stream.Collectors.toList())
                    : new java.util.ArrayList<>(),
                film.getAge_rating(),
                film.getRating()
            );

            updateUI(
                film.getPoster_url(),
                film.getTitle(),
                film.getSynopsis(),
                film.getDuration() + " min",
                film.getRating(),
                film.getCasting(),
                film.getImage_url(),
                null,
                film.getVideo_url(),
                0
            );

            if (scoreLabel      != null) scoreLabel.setText(String.valueOf(film.getRating()));
            if (addToListButton != null) updateAddButton(addToListButton, currentItem);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    //  INIT — EPISODE  (receives epId, NOT numEpisode)
    // ============================================================
    public void initEpisode(int serieId, int seasonNum, int epId) {
        try {
            this.currentSerie      = new FeaturedService().getFullSerie(serieId);
            this.currentSeasonNum  = seasonNum;

            if (currentSerie == null) return;

            // ✅ Find episode by ep_id (NOT numEpisode)
            this.currentEpisode = findEpisodeById(currentSerie, epId);

            if (currentEpisode == null) {
                System.err.println("❌ Episode not found for epId=" + epId);
                return;
            }

            // Find the matching season for trailer / poster
            Season matchedSeason = null;
            for (Season s : currentSerie.getSeasons()) {
                if (s.getSeasonNum() == seasonNum) {
                    matchedSeason = s;
                    break;
                }
            }

            String posterUrl = matchedSeason != null ? matchedSeason.getPosterUrl()  : null;
            String coverUrl  = matchedSeason != null ? matchedSeason.getImageUrl()   : null;
            String trailerUrl= matchedSeason != null ? matchedSeason.getTrailerUrl() : null;

            this.currentTrailerUrl = trailerUrl;

            this.currentItem = new FeaturedItem(
                currentEpisode.getSeasonId(),
                currentSerie.getSerieId(),
                currentSerie.getTitle(),
                currentSerie.getSynopsis(),
                trailerUrl,
                coverUrl,
                currentSerie.getTitleUrl(),
                posterUrl,
                currentSerie.getCategories() != null
                    ? currentSerie.getCategories().stream()
                          .map(c -> c.getName())
                          .collect(java.util.stream.Collectors.toList())
                    : new java.util.ArrayList<>(),
                currentSerie.getAge_rating(),
                currentSerie.getRating(),
                null,
                seasonNum,
                currentEpisode.getNumEpisode()
            );

            updateUI(
                posterUrl,
                currentEpisode.getTitle(),
                currentEpisode.getResume() != null
                    ? currentEpisode.getResume()
                    : currentSerie.getSynopsis(),
                currentEpisode.getDuration() + " min",
                currentSerie.getRating(),
                currentSerie.getCasting(),
                coverUrl,
                // ✅ Display numEpisode (not epId) in the label
                "S" + seasonNum + " - E" + currentEpisode.getNumEpisode(),
                currentEpisode.getVideoUrl(),
                currentEpisode.getEpId()
            );

            if (scoreLabel      != null) scoreLabel.setText(String.valueOf(currentSerie.getRating()));
            if (addToListButton != null) updateAddButton(addToListButton, currentItem);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    //  UPDATE UI  (called also from VideoPlayerController)
    // ============================================================
    public void updateUI(String poster, String title, String desc,
                         String duration, int rating, String cast,
                         String imgPath, String epInfo,
                         String video, int epId) {

        if (titleLabel       != null) titleLabel.setText(title);
        if (descriptionLabel != null) descriptionLabel.setText(desc);
        if (durationLabel    != null) durationLabel.setText(duration);
        if (starringLabel    != null) starringLabel.setText(cast != null ? cast : "");

        if (epInfo != null && episodeInfoLabel != null) {
            episodeInfoLabel.setText(epInfo);
            episodeInfoLabel.setVisible(true);
        } else if (episodeInfoLabel != null) {
            episodeInfoLabel.setVisible(false);
        }

        if (imgPath != null && backgroundImage != null) {
            try { backgroundImage.setImage(new Image(imgPath, true)); }
            catch (Exception ignored) {}
        }

        if (poster != null && posterImage != null) {
            try { posterImage.setImage(new Image(poster, true)); }
            catch (Exception ignored) {}
        }

        populateStars(rating);

        // ── Play button wiring ──
        if (playButton != null) {
            playButton.setOnAction(e -> {
                if (currentItem == null) return;

                if ("film".equalsIgnoreCase(currentItem.getType())) {
                    if (currentTrailerUrl != null)
                        openVideoPlayer(currentTrailerUrl, title, null);

                } else if ("serie".equalsIgnoreCase(currentItem.getType())) {
                    if (video != null && epId > 0)
                        openVideoPlayer(video, title, epId);
                }
            });
        }
    }

    // Overload used by VideoPlayerController.launchNextEpisode
    public void updateUI(String title, String resume, String duration,
                         int rating, String casting,
                         String episodeLabel, String videoUrl, int epId) {

        // When called from next-episode flow, poster/cover stay as-is
        String posterUrl = (currentEpisode != null && currentSerie != null)
            ? findSeasonPoster(currentSerie, currentSeasonNum) : null;
        String coverUrl  = (currentEpisode != null && currentSerie != null)
            ? findSeasonCover(currentSerie, currentSeasonNum)  : null;

        updateUI(posterUrl, title, resume, duration, rating, casting,
                 coverUrl, episodeLabel, videoUrl, epId);
    }

    // ============================================================
    //  FIND EPISODE BY EP_ID  ✅
    // ============================================================
    private Episode findEpisodeById(Serie serie, int epId) {
        for (Season s : serie.getSeasons()) {
            for (Episode ep : s.getEpisodes()) {
                if (ep.getEpId() == epId) return ep;
            }
        }
        return null;
    }

    // ============================================================
    //  SEASON HELPERS
    // ============================================================
    private String findSeasonPoster(Serie serie, int seasonNum) {
        if (serie == null || serie.getSeasons() == null) return null;
        for (Season s : serie.getSeasons())
            if (s.getSeasonNum() == seasonNum) return s.getPosterUrl();
        return null;
    }

    private String findSeasonCover(Serie serie, int seasonNum) {
        if (serie == null || serie.getSeasons() == null) return null;
        for (Season s : serie.getSeasons())
            if (s.getSeasonNum() == seasonNum) return s.getImageUrl();
        return null;
    }

    // ============================================================
    //  OPEN VIDEO PLAYER
    // ============================================================
    private void openVideoPlayer(String videoUrl, String title, Integer epId) {
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            System.err.println("❌ Video URL is null or empty!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/fxml/VideoPlayer.fxml"));
            Parent root = loader.load();
            VideoPlayerController controller = loader.getController();

            Stage videoStage = new Stage();
            videoStage.initOwner(mainContainer.getScene().getWindow());
            videoStage.initModality(Modality.APPLICATION_MODAL);
            videoStage.initStyle(StageStyle.TRANSPARENT);

            controller.setStage(videoStage);
            controller.loadVideo(videoUrl, title);
            controller.setParentController(this);

            if (epId == null) {
                // 🎬 FILM
                controller.setContext(currentItem.getId(), null);
                System.out.println("🎬 Film mode (ID: " + currentItem.getId() + ")");
            } else {
                // 📺 EPISODE
                controller.setContext(null, epId);
                controller.setEpisodeContext(
                    currentEpisode.getSeasonId(),
                    currentEpisode.getNumEpisode()
                );
                System.out.println("📺 Episode mode (epId: " + epId + ")");
            }

            Scene scene = new Scene(root);
            scene.setFill(Color.BLACK);
            videoStage.setScene(scene);

            javafx.geometry.Rectangle2D screen =
                javafx.stage.Screen.getPrimary().getBounds();
            videoStage.setX(screen.getMinX());
            videoStage.setY(screen.getMinY());
            videoStage.setWidth(screen.getWidth());
            videoStage.setHeight(screen.getHeight());

            videoStage.show();
            controller.startPlayback();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    //  NAVIGATION
    // ============================================================
    private void navigateTo(String fxmlPath) {
        try {
            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) {
                System.err.println("FXML not found: " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackAction() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/fxml/HomePage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    //  MY LIST
    // ============================================================
    private void handleAddToList() {
        if (currentItem == null) return;

        int userId  = Session.getUserId();
        int filmId  = "film".equalsIgnoreCase(currentItem.getType())  ? currentItem.getId()      : 0;
        int serieId = "serie".equalsIgnoreCase(currentItem.getType()) ? currentItem.getSerieId() : 0;

        boolean already = mylistService.isInList(userId, filmId, serieId);
        if (already) mylistService.removeItem(userId, filmId, serieId);
        else         mylistService.addItem(userId, filmId, serieId);

        updateAddButton(addToListButton, currentItem);
        MyListManager.getInstance().notifyItemUpdated(filmId, serieId);
    }

    private void updateAddButton(Button button, FeaturedItem item) {
        int userId  = Session.getUserId();
        int filmId  = "film".equalsIgnoreCase(item.getType())  ? item.getId()      : 0;
        int serieId = "serie".equalsIgnoreCase(item.getType()) ? item.getSerieId() : 0;

        if (mylistService.isInList(userId, filmId, serieId)) {
            button.setText("✔ added");
            button.setStyle(
                "-fx-background-color:#00aaff; -fx-text-fill:white;" +
                "-fx-background-radius:25; -fx-cursor:hand;" +
                "-fx-border-color:rgba(255,255,255,0);");
        } else {
            button.setText("+ My List");
            button.setStyle(
                "-fx-background-color:rgba(255,255,255,0.08); -fx-text-fill:white;" +
                "-fx-background-radius:25; -fx-cursor:hand;" +
                "-fx-border-color:rgba(255,255,255,0);");
        }
        pumpButton(button);
    }

    // ============================================================
    //  TABS
    // ============================================================
    private void setupTabLogic() {
        if (tabOverview == null || tabTrailers == null) return;

        tabOverview.setOnAction(e -> {
            lineOverview.setVisible(true);
            lineTrailers.setVisible(false);
            tabOverview.setStyle(
                "-fx-background-color:transparent; -fx-text-fill:white; -fx-font-weight:bold;");
            tabTrailers.setStyle(
                "-fx-background-color:transparent; -fx-text-fill:#7a80a0;");
        });

        tabTrailers.setOnAction(e -> {
            lineOverview.setVisible(false);
            lineTrailers.setVisible(true);
            tabTrailers.setStyle(
                "-fx-background-color:transparent; -fx-text-fill:white; -fx-font-weight:bold;");
            tabOverview.setStyle(
                "-fx-background-color:transparent; -fx-text-fill:#7a80a0;");

            if (currentTrailerUrl != null && !currentTrailerUrl.isEmpty())
                showTrailerPopup(currentTrailerUrl);
            else
                System.out.println("⚠️ No trailer URL for this content.");
        });
    }

    // ============================================================
    //  STARS
    // ============================================================
    private void populateStars(double rating) {
        if (starsBox == null) return;
        starsBox.getChildren().clear();
        for (int i = 1; i <= 5; i++) {
            Label star = new Label("★");
            star.setStyle(i <= rating
                ? "-fx-text-fill:#00d4ff; -fx-font-size:22px;" +
                  "-fx-effect:dropshadow(three-pass-box,rgba(0,212,255,0.8),15,0,0,0);"
                : "-fx-text-fill:#2a3140; -fx-font-size:22px;");
            starsBox.getChildren().add(star);
        }
    }

    // ============================================================
    //  NAVBAR / NOTIFICATIONS
    // ============================================================
    private void setupNavbar() {
        try {
            if (logoNav  != null)
                logoNav.setImage(new Image(
                    getClass().getResourceAsStream("/assets/images/logo/Raksha.png")));
            if (bellIcon != null)
                bellIcon.setImage(new Image(
                    getClass().getResourceAsStream("/assets/images/bellwhiter.png")));
        } catch (Exception ignored) {}
    }

    private void setupNotificationSystem() {
        try {
            bellSound = new AudioClip(
                getClass().getResource("/assets/sounds/notification.mp3").toString());
        } catch (Exception ignored) {}

        notificationContent.setStyle(
            "-fx-background-color:#111111; -fx-background-radius:8;" +
            "-fx-padding:15; -fx-spacing:10;");
        notificationContent.setPrefWidth(250);
        Label popTitle = new Label("Notifications");
        popTitle.setStyle("-fx-text-fill:white; -fx-font-weight:bold;");
        notificationContent.getChildren().add(popTitle);
        notificationPopup.getContent().add(notificationContent);
        notificationPopup.setAutoHide(true);

        if (bellContainer != null) {
            bellContainer.setOnMouseEntered(e -> {
                shakeBell();
                if (bellSound != null) bellSound.play();
                showNotificationDot();
                showPopup();
            });
        }
    }

    private void shakeBell() {
        if (bellIcon == null) return;
        RotateTransition rt = new RotateTransition(Duration.millis(100), bellIcon);
        rt.setFromAngle(-10); rt.setToAngle(10);
        rt.setCycleCount(4); rt.setAutoReverse(true);
        rt.play();
    }

    private void showNotificationDot() {
        if (!isNotificationVisible && notificationCircle != null) {
            notificationCircle.setVisible(true);
            isNotificationVisible = true;
        }
    }

    private void hideNotification() {
        if (notificationCircle == null || !isNotificationVisible) return;
        isNotificationVisible = false;

        ParallelTransition hide = new ParallelTransition();
        FadeTransition  fade  = new FadeTransition(Duration.millis(300), notificationCircle);
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), notificationCircle);
        fade.setFromValue(1.0); fade.setToValue(0.0);
        scale.setToX(0); scale.setToY(0);
        hide.getChildren().addAll(fade, scale);
        hide.setOnFinished(e -> {
            notificationCircle.setVisible(false);
            notificationCircle.setScaleX(1);
            notificationCircle.setScaleY(1);
            notificationCircle.setOpacity(1);
        });
        hide.play();
    }

    private void showPopup() {
        if (bellContainer == null) return;
        Bounds bounds = bellContainer.localToScreen(bellContainer.getBoundsInLocal());
        notificationPopup.show(bellContainer, bounds.getMinX() - 200, bounds.getMaxY() + 10);
    }

    // ============================================================
    //  CAST
    // ============================================================
    private void loadCast() {
        if (castBox == null) return;
        castBox.getChildren().clear();
        for (int i = 0; i < 5; i++)
            castBox.getChildren().add(createActorCard("Actor Name", "/assets/images/profile.png"));
    }

    private VBox createActorCard(String name, String imgPath) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        try {
            ImageView img = new ImageView(
                new Image(getClass().getResourceAsStream(imgPath)));
            img.setFitWidth(80); img.setFitHeight(80);
            img.setClip(new Circle(40, 40, 40));
            Label n = new Label(name);
            n.setStyle("-fx-text-fill:white; -fx-font-size:12px;");
            card.getChildren().addAll(img, n);
            addHoverEffect(card);
        } catch (Exception ignored) {}
        return card;
    }

    // ============================================================
    //  TRAILER POPUP
    // ============================================================
    private void showTrailerPopup(String url) {
        try {
            URL videoUrl = getClass().getResource(url);
            String videoPath = (url.startsWith("http") || videoUrl == null)
                ? url : videoUrl.toExternalForm();

            javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
            webView.setPrefSize(1500, 700);
            webView.getEngine().loadContent(
                "<html><body style='margin:0;background:black;'>" +
                "<video width='100%' height='100%' controls autoplay>" +
                "<source src='" + videoPath + "' type='video/mp4'>" +
                "</video></body></html>");

            javafx.geometry.Rectangle2D screen =
                javafx.stage.Screen.getPrimary().getBounds();
            double fw = screen.getWidth(), fh = screen.getHeight();
            double sw = 1200, sh = 600;

            Stage popup = new Stage();
            popup.initOwner(btnBack.getScene().getWindow());
            popup.initModality(Modality.WINDOW_MODAL);
            popup.initStyle(StageStyle.TRANSPARENT);
            popup.setWidth(fw); popup.setHeight(fh);
            popup.setX(0); popup.setY(0);

            StackPane root = new StackPane();
            root.setStyle("-fx-background-color:rgba(0,0,0,0.85);");

            VBox layout = new VBox(15);
            layout.setStyle(
                "-fx-background-color:rgba(0,0,0,0.2);" +
                "-fx-background-radius:15; -fx-padding:15; -fx-alignment:center;");
            layout.setPrefSize(fw, fh);

            Button toggleSize = new Button("🗗");
            toggleSize.setStyle(
                "-fx-background-color:#008cff; -fx-text-fill:white;" +
                "-fx-font-weight:bold; -fx-background-radius:50%; -fx-padding:5 8;");

            Button exit = new Button("✕");
            exit.setStyle(
                "-fx-background-color:#008cff; -fx-text-fill:white;" +
                "-fx-font-weight:bold; -fx-background-radius:50%; -fx-padding:5 8;");

            Runnable resetTabs = () -> {
                if (lineOverview != null) lineOverview.setVisible(true);
                if (lineTrailers != null) lineTrailers.setVisible(false);
                if (tabOverview  != null) tabOverview.setStyle(
                    "-fx-background-color:transparent; -fx-text-fill:white; -fx-font-weight:bold;");
                if (tabTrailers  != null) tabTrailers.setStyle(
                    "-fx-background-color:transparent; -fx-text-fill:#7a80a0;");
            };

            exit.setOnAction(ev -> {
                webView.getEngine().load(null);
                popup.close();
                resetTabs.run();
            });

            final boolean[] full = {true};
            toggleSize.setOnAction(ev -> {
                if (full[0]) {
                    popup.setWidth(sw); popup.setHeight(sh);
                    popup.setX((screen.getWidth() - sw) / 2);
                    popup.setY((screen.getHeight() - sh) / 2);
                    layout.setPrefSize(sw, sh);
                    full[0] = false;
                } else {
                    popup.setWidth(fw); popup.setHeight(fh);
                    popup.setX(0); popup.setY(0);
                    layout.setPrefSize(fw, fh);
                    full[0] = true;
                }
            });

            HBox topBar = new HBox(10, toggleSize, exit);
            topBar.setAlignment(Pos.TOP_RIGHT);
            topBar.setPadding(new javafx.geometry.Insets(10));
            topBar.setPickOnBounds(false);

            layout.getChildren().addAll(topBar, webView);
            root.getChildren().add(layout);

            popup.setOnHidden(ev -> {
                webView.getEngine().load(null);
                resetTabs.run();
            });

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popup.setScene(scene);
            popup.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ============================================================
    //  ANIMATIONS / HELPERS
    // ============================================================
    private void addHoverEffect(Node node) {
        ScaleTransition in  = new ScaleTransition(Duration.millis(200), node);
        ScaleTransition out = new ScaleTransition(Duration.millis(200), node);
        in.setToX(1.05); in.setToY(1.05);
        out.setToX(1.0);  out.setToY(1.0);
        node.setOnMouseEntered(e -> {
            in.play();
            node.setEffect(new javafx.scene.effect.DropShadow(25, Color.web("#2d54ff", 0.6)));
        });
        node.setOnMouseExited(e -> { out.play(); node.setEffect(null); });
    }

    private void addButtonInteractions(Button btn) {
        ScaleTransition up   = new ScaleTransition(Duration.millis(100), btn);
        ScaleTransition down = new ScaleTransition(Duration.millis(100), btn);
        up.setToX(1.1); up.setToY(1.1);
        down.setToX(1.0); down.setToY(1.0);

        javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
        glow.setColor(Color.web("#00d4ff", 0.8));
        glow.setRadius(20); glow.setSpread(0.12);

        btn.setOnMouseEntered(e -> { up.play();   btn.setEffect(glow); });
        btn.setOnMouseExited(e  -> { down.play(); btn.setEffect(null); });
        btn.setOnMousePressed(e  -> { btn.setScaleX(0.95); btn.setScaleY(0.95); });
        btn.setOnMouseReleased(e -> { btn.setScaleX(1.1);  btn.setScaleY(1.1);  });
    }

    private void pumpButton(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.2);   st.setToY(1.2);
        st.setAutoReverse(true); st.setCycleCount(2);
        st.play();
    }
}