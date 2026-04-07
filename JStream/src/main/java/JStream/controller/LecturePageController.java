package JStream.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import JStream.entity.Comment;
import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.Film;
import JStream.entity.MyListManager;
import JStream.entity.Rating;
import JStream.entity.Season;
import JStream.entity.Serie;
import JStream.entity.Session;
import JStream.service.CommentService;
import JStream.service.UserService; 
import JStream.service.FeaturedService;
import JStream.service.MylistService;
import JStream.service.RatingService;
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
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
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

    // ── Services ──────────────────────────────────────────────────────────────
    private final RatingService  ratingService  = new RatingService();
    private final CommentService commentService = new CommentService();
    private final MylistService  mylistService  = new MylistService();

    // ── Resolved context (populated in initFilm / initEpisode) ───────────────
    private Integer resolvedFilmId    = null;
    private Integer resolvedSerieId   = null;
    private Integer resolvedSeasonId  = null;
    private Integer resolvedEpisodeId = null;

    // ── Interactive-star state ────────────────────────────────────────────────
    private int     selectedStarNote  = 0;
    private Label[] interactiveLabels = new Label[5];

    // ── Current item / trailer ────────────────────────────────────────────────
    private String       currentTrailerUrl;
    private FeaturedItem currentItem;

    // ── Episode/serie context (for VideoPlayerController callbacks) ───────────
    private Serie   currentSerie;
    private Episode currentEpisode;
    private int     currentSeasonNum;

    // ── Comment pagination ────────────────────────────────────────────────────
    private int           commentScrollIndex = 0;
    private List<Comment> currentComments    = new java.util.ArrayList<>();

    // ── Notification system ───────────────────────────────────────────────────
    private AudioClip bellSound;
    private final Popup notificationPopup   = new Popup();
    private final VBox  notificationContent = new VBox();
    private boolean     isNotificationVisible = false;

    // ── FXML — Navbar ─────────────────────────────────────────────────────────
    @FXML private ImageView logoNav, bellIcon;
    @FXML private Button    btnMostWatched, btnMyList;
    @FXML private StackPane bellContainer;
    @FXML private Circle    notificationCircle;
    @FXML private Button    btnBack, playButton, btnNotification, profileBtn;
    @FXML private javafx.scene.control.ScrollPane mainScrollPane;
    // ── FXML — Hero ───────────────────────────────────────────────────────────
    @FXML private VBox      mainContainer;
    @FXML private ImageView backgroundImage, posterImage;
    @FXML private Label     titleLabel, scoreLabel, yearLabel, durationLabel,
                            ageRatingLabel, descriptionLabel;
    @FXML private Label     starringLabel, directorLabel, categoriesLabel, episodeInfoLabel;
    @FXML private HBox      starsBox, castBox;

    // ── FXML — Tabs ───────────────────────────────────────────────────────────
    @FXML private Rectangle lineOverview, lineTrailers;
    @FXML private Button    tabOverview,  tabTrailers;

    // ── FXML — Reviews ────────────────────────────────────────────────────────
    @FXML private HBox     commentsContainer;
    @FXML private HBox     interactiveStarsBox;
    @FXML private TextArea commentInput;
    @FXML private Button   btnSubmitComment;
    @FXML private Button   addToListButton;
    private final JStream.service.UserService userService = new JStream.service.UserService();
    @FXML private VBox watchNextContainer;
    private final FeaturedService featuredService = new FeaturedService();
    // =========================================================================
    //  INITIALIZE
    // =========================================================================
    @FXML
    public void initialize() {
        setupNavbar();
        setupNotificationSystem();
        setupTabLogic();

        if (btnBack != null) {
            addButtonInteractions(btnBack);
            btnBack.setOnAction(e -> handleBackAction());
        }

        if (btnNotification != null) {
            btnNotification.setOnAction(e -> {
                if (isNotificationVisible) hideNotification();
                showPopup();
            });
        }

        if (btnMostWatched != null) {
            addButtonInteractions(btnMostWatched);
            btnMostWatched.setOnAction(e -> navigateTo("/view/fxml/MyHistory.fxml"));
        }

        if (btnMyList != null) {
            addButtonInteractions(btnMyList);
            btnMyList.setOnAction(e -> navigateTo("/view/fxml/MyList.fxml"));
        }

        if (addToListButton != null) addToListButton.setOnAction(e -> handleAddToList());
        if (posterImage     != null) addHoverEffect(posterImage);
        if (playButton      != null) addHoverEffect(playButton);
        if (btnSubmitComment != null) addButtonInteractions(btnSubmitComment);

        // Entrance fade-in
        if (mainContainer != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(900), mainContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }

        populateStars(0);
        
    }

    // =========================================================================
    //  INIT — FILM
    // =========================================================================
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

            this.resolvedFilmId    = filmId;
            this.resolvedSerieId   = null;
            this.resolvedSeasonId  = null;
            this.resolvedEpisodeId = null;

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
            if (addToListButton != null) syncAddButton();

            Rating prior = ratingService.getUserRatingForFilm(Session.getUserId(), filmId);
            setupReviewSection(prior != null ? prior.getNote() : 0);
            if (prior != null) lockStars();
            loadCast();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    //  INIT — EPISODE
    // =========================================================================
    public void initEpisode(int serieId, int seasonNum, int epId) {
        try {
            this.currentSerie     = new FeaturedService().getFullSerie(serieId);
            this.currentSeasonNum = seasonNum;
            if (currentSerie == null) return;

            this.currentEpisode = findEpisodeById(currentSerie, epId);
            if (currentEpisode == null) {
                System.err.println("❌ Episode not found for epId=" + epId);
                return;
            }

            // Resolve season assets
            Season matchedSeason = null;
            for (Season s : currentSerie.getSeasons()) {
                if (s.getSeasonNum() == seasonNum) { matchedSeason = s; break; }
            }

            String posterUrl  = matchedSeason != null ? matchedSeason.getPosterUrl()  : null;
            String coverUrl   = matchedSeason != null ? matchedSeason.getImageUrl()   : null;
            String trailerUrl = matchedSeason != null ? matchedSeason.getTrailerUrl() : null;

            this.currentTrailerUrl = trailerUrl;

            this.currentItem = new FeaturedItem(
                currentEpisode.getSeasonId(),
                currentSerie.getSerieId(),
                currentSerie.getTitle(),
                currentSerie.getSynopsis(),
                trailerUrl, coverUrl,
                currentSerie.getTitleUrl(),
                posterUrl,
                currentSerie.getCategories() != null
                    ? currentSerie.getCategories().stream()
                          .map(c -> c.getName())
                          .collect(java.util.stream.Collectors.toList())
                    : new java.util.ArrayList<>(),
                currentSerie.getAge_rating(),
                currentSerie.getRating(),
                null, seasonNum,
                currentEpisode.getNumEpisode()
            );

            this.resolvedFilmId    = null;
            this.resolvedSerieId   = serieId;
            this.resolvedSeasonId  = currentEpisode.getSeasonId();
            this.resolvedEpisodeId = currentEpisode.getEpId();

            double freshAvg      = ratingService.getAverageForEpisode(currentEpisode.getEpId());
            double displayRating = freshAvg > 0 ? freshAvg : currentSerie.getRating();

            updateUI(
                posterUrl,
                currentEpisode.getTitle(),
                currentEpisode.getResume() != null
                    ? currentEpisode.getResume()
                    : currentSerie.getSynopsis(),
                currentEpisode.getDuration() + " min",
                (int) displayRating,
                currentSerie.getCasting(),
                coverUrl,
                "S" + seasonNum + " · E" + currentEpisode.getNumEpisode(),
                currentEpisode.getVideoUrl(),
                currentEpisode.getEpId()
            );

            if (scoreLabel != null) scoreLabel.setText(
                freshAvg > 0
                    ? String.format("%.1f", freshAvg)
                    : String.valueOf(currentSerie.getRating()));

            populateStars(displayRating);
            if (addToListButton != null) syncAddButton();

            Rating prior = ratingService.getUserRatingForEpisode(
                Session.getUserId(), currentEpisode.getEpId());
            setupReviewSection(prior != null ? prior.getNote() : 0);
            if (prior != null) lockStars();
            loadCast();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    //  UPDATE UI
    // =========================================================================
    public void updateUI(String poster, String title, String desc,
                         String duration, int rating, String cast,
                         String imgPath, String epInfo,
                         String video, int epId) {

        if (titleLabel       != null) titleLabel.setText(title);
        if (descriptionLabel != null) descriptionLabel.setText(desc);
        if (durationLabel    != null) durationLabel.setText(duration);
        if (starringLabel    != null) starringLabel.setText(cast != null ? cast : "—");

        // Episode pill visibility
        if (episodeInfoLabel != null) {
            if (epInfo != null) {
                episodeInfoLabel.setText(epInfo);
                episodeInfoLabel.setVisible(true);
            } else {
                episodeInfoLabel.setVisible(false);
            }
        }

        // Background blur image
        if (imgPath != null && backgroundImage != null) {
            try { backgroundImage.setImage(new Image(imgPath, true)); }
            catch (Exception ignored) {}
        }

        // Poster
        if (poster != null && posterImage != null) {
            try { posterImage.setImage(new Image(poster, true)); }
            catch (Exception ignored) {}
        }

        populateStars(rating);

        // Wire play button
        if (playButton != null) {
            playButton.setOnAction(e -> {
                if (currentItem == null) return;

                if ("film".equalsIgnoreCase(currentItem.getType())) {
                    if (currentTrailerUrl != null)
                        openVideoPlayer(currentTrailerUrl, title, null);
                    else
                        System.out.println("⚠️ Film video URL missing!");

                } else if ("serie".equalsIgnoreCase(currentItem.getType())) {
                    if (video != null && epId > 0)
                        openVideoPlayer(video, title, epId);
                    else
                        System.out.println("⚠️ Episode video not loaded!");
                }
            });
        }
    }

    /** Overload called by VideoPlayerController when launching the next episode */
    public void updateUI(String title, String resume, String duration,
                         int rating, String casting,
                         String episodeLabel, String videoUrl, int epId) {

        String posterUrl = findSeasonPoster(currentSerie, currentSeasonNum);
        String coverUrl  = findSeasonCover(currentSerie,  currentSeasonNum);
        updateUI(posterUrl, title, resume, duration, rating, casting,
                 coverUrl, episodeLabel, videoUrl, epId);
    }

    // =========================================================================
    //  EPISODE HELPERS
    // =========================================================================
    private Episode findEpisodeById(Serie serie, int epId) {
        for (Season s : serie.getSeasons())
            for (Episode ep : s.getEpisodes())
                if (ep.getEpId() == epId) return ep;
        return null;
    }

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

    // =========================================================================
    //  VIDEO PLAYER
    // =========================================================================
    private void openVideoPlayer(String videoUrl, String title, Integer epId) {
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            System.err.println("❌ Video URL is null or empty!");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/fxml/VideoPlayer.fxml"));
            Parent root = loader.load();
            VideoPlayerController ctrl = loader.getController();

            Stage stage = new Stage();
            stage.initOwner(mainContainer.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);

            ctrl.setStage(stage);
            ctrl.loadVideo(videoUrl, title);
            ctrl.setParentController(this);

            if (epId == null) {
                ctrl.setContext(currentItem.getId(), null);
                System.out.println("🎬 Film mode (ID: " + currentItem.getId() + ")");
            } else {
                ctrl.setContext(null, epId);
                ctrl.setEpisodeContext(
                    currentEpisode.getSeasonId(), currentEpisode.getNumEpisode());
                System.out.println("📺 Episode mode (epId: " + epId + ")");
            }

            Scene scene = new Scene(root);
            scene.setFill(Color.BLACK);
            stage.setScene(scene);

            javafx.geometry.Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
            stage.setX(screen.getMinX()); stage.setY(screen.getMinY());
            stage.setWidth(screen.getWidth()); stage.setHeight(screen.getHeight());

            stage.show();
            ctrl.startPlayback();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    //  NAVIGATION
    // =========================================================================
    private void navigateTo(String fxmlPath) {
        try {
            URL loc = getClass().getResource(fxmlPath);
            if (loc == null) { System.err.println("FXML not found: " + fxmlPath); return; }
            Parent root = new FXMLLoader(loc).load();
            ((Stage) btnBack.getScene().getWindow()).getScene().setRoot(root);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleBackAction() {
        try {
            Parent root = new FXMLLoader(
                getClass().getResource("/view/fxml/HomePage.fxml")).load();
            ((Stage) btnBack.getScene().getWindow()).getScene().setRoot(root);
        } catch (java.io.IOException e) { e.printStackTrace(); }
    }

    // =========================================================================
    //  MY LIST
    // =========================================================================
    private void handleAddToList() {
        if (currentItem == null) return;
        int userId  = Session.getUserId();
        int filmId  = "film".equalsIgnoreCase(currentItem.getType())  ? currentItem.getId()      : 0;
        int serieId = "serie".equalsIgnoreCase(currentItem.getType()) ? currentItem.getSerieId() : 0;

        if (mylistService.isInList(userId, filmId, serieId))
            mylistService.removeItem(userId, filmId, serieId);
        else
            mylistService.addItem(userId, filmId, serieId);

        syncAddButton();
        MyListManager.getInstance().notifyItemUpdated(filmId, serieId);
    }

    /** Syncs the addToListButton label + style to match current DB state */
    private void syncAddButton() {
        if (addToListButton == null || currentItem == null) return;

        int userId  = Session.getUserId();
        int filmId  = "film".equalsIgnoreCase(currentItem.getType())  ? currentItem.getId()      : 0;
        int serieId = "serie".equalsIgnoreCase(currentItem.getType()) ? currentItem.getSerieId() : 0;
        boolean inList = mylistService.isInList(userId, filmId, serieId);

        if (inList) {
            addToListButton.setText("✔  Added");
            addToListButton.setStyle(
                "-fx-background-color: linear-gradient(to right,#00aaff,#005fb8);" +
                "-fx-text-fill: #02040a;" +
                "-fx-font-size: 14px; -fx-font-weight: bold;" +
                "-fx-background-radius: 28;" +
                "-fx-border-color: transparent; -fx-border-radius: 28;" +
                "-fx-cursor: hand;");
        } else {
            addToListButton.setText("+ My List");
            addToListButton.setStyle(
                "-fx-background-color: rgba(255,255,255,0.07);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px; -fx-font-weight: bold;" +
                "-fx-background-radius: 28;" +
                "-fx-border-color: rgba(255,255,255,0.18); -fx-border-radius: 28;" +
                "-fx-cursor: hand;");
        }
        pumpButton(addToListButton);
    }

    // =========================================================================
    //  TABS
    // =========================================================================
    private void setupTabLogic() {
        if (tabOverview == null || tabTrailers == null) return;

        final String ACTIVE   =
            "-fx-background-color:transparent; -fx-text-fill:white;" +
            "-fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand; -fx-padding:4 0;";
        final String INACTIVE =
            "-fx-background-color:transparent; -fx-text-fill:#4e5670;" +
            "-fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand; -fx-padding:4 0;";

        tabOverview.setOnAction(e -> {
            lineOverview.setVisible(true);
            lineTrailers.setVisible(false);
            tabOverview.setStyle(ACTIVE);
            tabTrailers.setStyle(INACTIVE);
        });

        tabTrailers.setOnAction(e -> {
            lineOverview.setVisible(false);
            lineTrailers.setVisible(true);
            tabTrailers.setStyle(ACTIVE);
            tabOverview.setStyle(INACTIVE);

            if (currentTrailerUrl != null && !currentTrailerUrl.isEmpty())
                showTrailerPopup(currentTrailerUrl);
            else
                System.out.println("⚠️ No trailer URL for this content.");
        });
    }

    // =========================================================================
    //  STARS — display (matches FXML starsBox)
    // =========================================================================
    private void populateStars(double rawAvg) {
        if (starsBox == null) return;
        starsBox.getChildren().clear();

        double val = Math.max(0, Math.min(5, rawAvg));

        for (int i = 1; i <= 5; i++) {
            Label star = new Label();
            if (i <= val) {
                star.setText("★");
                star.setStyle(
                    "-fx-text-fill:#00d4ff; -fx-font-size:20px;" +
                    "-fx-effect:dropshadow(three-pass-box,rgba(0,212,255,0.8),14,0,0,0);");
            } else if (i - val < 1.0) {
                // Half-star
                star.setText("★");
                star.setStyle(
                    "-fx-text-fill:#00d4ff; -fx-font-size:20px; -fx-opacity:0.38;" +
                    "-fx-effect:dropshadow(three-pass-box,rgba(0,212,255,0.3),8,0,0,0);");
            } else {
                star.setText("☆");
                star.setStyle("-fx-text-fill:#1e2535; -fx-font-size:20px;");
            }
            starsBox.getChildren().add(star);
        }
    }

    // =========================================================================
    //  STARS — interactive (matches FXML interactiveStarsBox)
    // =========================================================================
    private void setupInteractiveStars(int preselected) {
        if (interactiveStarsBox == null) return;
        interactiveStarsBox.getChildren().clear();
        selectedStarNote = preselected;

        for (int i = 0; i < 5; i++) {
            Label star = new Label("☆");
            star.setStyle("-fx-text-fill:#1e2535; -fx-font-size:28px; -fx-cursor:hand;");
            interactiveLabels[i] = star;

            final int idx = i + 1;
            star.setOnMouseEntered(e -> paintInteractive(idx));
            star.setOnMouseExited(e  -> paintInteractive(selectedStarNote));
            star.setOnMouseClicked(e -> {
                selectedStarNote = idx;
                paintInteractive(selectedStarNote);
            });
            interactiveStarsBox.getChildren().add(star);
        }
        if (preselected > 0) paintInteractive(preselected);
    }

    private void paintInteractive(int upTo) {
        for (int i = 0; i < interactiveLabels.length; i++) {
            if (interactiveLabels[i] == null) continue;
            if (i < upTo) {
                interactiveLabels[i].setText("★");
                interactiveLabels[i].setStyle(
                    "-fx-text-fill:#00d4ff; -fx-font-size:28px; -fx-cursor:hand;" +
                    "-fx-effect:dropshadow(three-pass-box,rgba(0,212,255,0.9),14,0,0,0);");
            } else {
                interactiveLabels[i].setText("☆");
                interactiveLabels[i].setStyle(
                    "-fx-text-fill:#1e2535; -fx-font-size:28px; -fx-cursor:hand;");
            }
        }
    }

    // =========================================================================
    //  SUBMIT REVIEW
    // =========================================================================
    private void handleSubmitReview() {
        int     userId           = Session.getUserId();
        boolean ratingSubmitted  = false;
        boolean commentSubmitted = false;

        // ── 1. Rating — only if not already rated ────────────────────────────────
        if (selectedStarNote > 0) {
            boolean alreadyRated = false;

            if (resolvedFilmId != null) {
			    alreadyRated = ratingService.getUserRatingForFilm(userId, resolvedFilmId) != null;
			} else if (resolvedEpisodeId != null) {
			    alreadyRated = ratingService.getUserRatingForEpisode(userId, resolvedEpisodeId) != null;
			}

            if (alreadyRated) {
                System.out.println("⚠️ Already rated — skipping.");
            } else {
                Rating rating = null;

                if (resolvedFilmId != null) {
                    rating = Rating.forFilm(userId, resolvedFilmId, selectedStarNote);

                } else if (resolvedSerieId   != null
                        && resolvedSeasonId  != null
                        && resolvedEpisodeId != null) {
                    rating = Rating.forEpisode(userId,
                        resolvedSerieId, resolvedSeasonId, resolvedEpisodeId, selectedStarNote);
                } else {
                    System.err.println("❌ Incomplete rating context.");
                }

                if (rating != null) {
                    ratingSubmitted = ratingService.submitRating(rating);
                    System.out.println(ratingSubmitted
                        ? "✅ Rating submitted: " + selectedStarNote + "/5"
                        : "❌ Rating FAILED");
                }

                if (ratingSubmitted) {
                    double newAvg = resolvedFilmId != null
                        ? ratingService.getAverageForFilm(resolvedFilmId)
                        : resolvedEpisodeId != null
                            ? ratingService.getAverageForEpisode(resolvedEpisodeId)
                            : ratingService.getAverageForSerie(resolvedSerieId);

                    populateStars(newAvg);
                    if (scoreLabel != null) scoreLabel.setText(String.format("%.1f", newAvg));
                    lockStars();
                }
            }
        }

     // ── 2. Comment — always allowed ───────────────────────────────────────────
        String content = commentInput != null ? commentInput.getText().trim() : "";

        if (!content.isEmpty()) {
            int filmId = resolvedFilmId    != null ? resolvedFilmId    : 0;
            int epId   = resolvedEpisodeId != null ? resolvedEpisodeId : 0;

            Comment comment = new Comment(0, userId, filmId, epId, content, false, null, null);
            commentSubmitted = commentService.postComment(comment);
            System.out.println(commentSubmitted ? "✅ Comment posted" : "❌ Comment FAILED");

            if (commentSubmitted) {
                if (commentInput != null) commentInput.clear();

                List<Comment> updated;
                if (resolvedFilmId != null) {
                    updated = commentService.getCommentsForFilm(resolvedFilmId);
                } else if (resolvedEpisodeId != null) {
                    updated = commentService.getCommentsForEpisode(resolvedEpisodeId);
                } else {
                    updated = new java.util.ArrayList<>();
                }
                loadComments(updated);
            }
        }

        // ── 3. Nothing submitted ──────────────────────────────────────────────────
        if (!ratingSubmitted && !commentSubmitted
                && selectedStarNote == 0 && content.isEmpty()) {
            System.out.println("⚠️ Nothing to submit.");
        }
    }

    private void lockStars() {
        if (interactiveStarsBox == null) return;
        for (Label star : interactiveLabels) {
            if (star == null) continue;
            star.setOnMouseEntered(null);
            star.setOnMouseExited(null);
            star.setOnMouseClicked(null);
            star.setStyle(star.getStyle() + "; -fx-cursor:default; -fx-opacity:0.6;");
        }
        // Optional: show a small label
        Label locked = new Label("✔ You've already rated this");
        locked.setStyle("-fx-text-fill:#2e3850; -fx-font-size:11px; -fx-font-style:italic;");
        interactiveStarsBox.getChildren().add(locked);
    }
    // =========================================================================
    //  REVIEW SECTION SETUP
    // =========================================================================
    private void setupReviewSection(int preselectedStars) {
        setupInteractiveStars(preselectedStars);

        if (btnSubmitComment != null)
            btnSubmitComment.setOnAction(e -> handleSubmitReview());

        List<Comment> existing;
        if (resolvedFilmId != null) {
            existing = commentService.getCommentsForFilm(resolvedFilmId);
        } else if (resolvedEpisodeId != null) {
            existing = commentService.getCommentsForEpisode(resolvedEpisodeId);
        
        } else {
            existing = new java.util.ArrayList<>();
        }
        loadComments(existing);
    }

    // =========================================================================
    //  COMMENTS — load & paginate (renders into FXML commentsContainer HBox)
    // =========================================================================
 // ── Keep fixed references to the 3 card slots + arrows ──────────────────────
    private final VBox[]   commentSlots = new VBox[3];
    private Button         arrowLeft, arrowRight;

    private void initCommentSlots() {
        commentsContainer.getChildren().clear();
        commentsContainer.setAlignment(Pos.CENTER_LEFT);

        arrowLeft  = buildArrowButton("❮", false);
        arrowRight = buildArrowButton("❯", false);

        arrowLeft.setOnAction(e -> {
            if (commentScrollIndex > 0) { commentScrollIndex--; updateVisibleComments(); }
        });
        arrowRight.setOnAction(e -> {
            if (commentScrollIndex + 3 < currentComments.size()) {
                commentScrollIndex++;
                updateVisibleComments();
            }
        });

        commentsContainer.getChildren().add(arrowLeft);

        for (int i = 0; i < 3; i++) {
            commentSlots[i] = new VBox(10);
            commentSlots[i].setPrefWidth(300);
            commentSlots[i].setMaxWidth(300);
            commentSlots[i].setMinHeight(120);
            commentSlots[i].setStyle(
                "-fx-background-color:rgba(255,255,255,0.03);" +
                "-fx-background-radius:14;" +
                "-fx-padding:18 20;" +
                "-fx-border-color:rgba(0,212,255,0.1);" +
                "-fx-border-radius:14;");
            commentsContainer.getChildren().add(commentSlots[i]);
        }

        commentsContainer.getChildren().add(arrowRight);
    }

    private void updateVisibleComments() {
        boolean canLeft  = commentScrollIndex > 0;
        boolean canRight = commentScrollIndex + 3 < currentComments.size();

        arrowLeft.setDisable(!canLeft);
        arrowLeft.setOpacity(canLeft ? 1.0 : 0.25);
        arrowRight.setDisable(!canRight);
        arrowRight.setOpacity(canRight ? 1.0 : 0.25);

        for (int i = 0; i < 3; i++) {
            VBox slot = commentSlots[i];
            slot.getChildren().clear();

            int dataIndex = commentScrollIndex + i;
            if (dataIndex < currentComments.size()) {
                Comment c = currentComments.get(dataIndex);
                slot.setVisible(true);
                slot.setManaged(true);

                // ── Avatar + Username row ─────────────────────────────────────────
                String username = getUsernameById(c.getUserID());
                String initials = username.length() >= 2
                    ? username.substring(0, 2).toUpperCase()
                    : username.toUpperCase();

                // Avatar circle with initials
                Label avatar = new Label(initials);
                avatar.setMinSize(36, 36);
                avatar.setMaxSize(36, 36);
                avatar.setAlignment(Pos.CENTER);
                avatar.setStyle(
                    "-fx-background-color: rgba(0,212,255,0.18);" +
                    "-fx-text-fill: #00d4ff;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 50%;");

                Label nameLabel = new Label(username);
                nameLabel.setStyle(
                    "-fx-text-fill: #c0c8d8;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;");

             // Flag button
                Button flagBtn = new Button("⚑");
                flagBtn.setFocusTraversable(false); 

                final String FLAG_DEFAULT =
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #3e4560;" +
                    "-fx-font-size: 13px;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 0;";
                final String FLAG_ACTIVE =
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #ff4444;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 0;";
                final String FLAG_HOVER =
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #ff4444;" +
                    "-fx-font-size: 13px;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 0;";

                // ── Restore flagged state from DB ─────────────────────────────────────
                if (c.isFlagged()) {
                    flagBtn.setStyle(FLAG_ACTIVE);
                    flagBtn.setDisable(true);
                    flagBtn.setTooltip(new javafx.scene.control.Tooltip("Reported — pending admin review"));
                } else {
                    flagBtn.setStyle(FLAG_DEFAULT);
                    flagBtn.setOnMouseEntered(e -> flagBtn.setStyle(FLAG_HOVER));
                    flagBtn.setOnMouseExited(e  -> flagBtn.setStyle(FLAG_DEFAULT));
                    flagBtn.setOnAction(e -> handleFlagComment(c.getComment_id(), flagBtn));
                }

                // Hide flag button for own comments
                if (c.getUserID() == Session.getUserId()) {
                    flagBtn.setVisible(false);
                    flagBtn.setManaged(false);
                }
                // Spacer to push flag to the right
                Region spacer = new Region();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                HBox userRow = new HBox(10, avatar, nameLabel, spacer, flagBtn);
                userRow.setAlignment(Pos.CENTER_LEFT);

                // ── Thin divider ──────────────────────────────────────────────────
                Rectangle divider = new Rectangle(262, 1);
                divider.setFill(Color.web("#00d4ff", 0.07));

                // ── Date ──────────────────────────────────────────────────────────
                Label date = new Label(c.getCreates_at() != null
                    ? c.getCreates_at().toLocalDateTime()
                           .format(java.time.format.DateTimeFormatter
                                   .ofPattern("dd MMM yyyy · HH:mm"))
                    : "");
                date.setStyle(
                    "-fx-text-fill: #2e3850;" +
                    "-fx-font-size: 10px;" +
                    "-fx-font-weight: bold;");

                // ── Body ──────────────────────────────────────────────────────────
                Label body = new Label(c.getContent());
                body.setWrapText(true);
                body.setMaxWidth(262);
                body.setStyle(
                    "-fx-text-fill: #8a96b0;" +
                    "-fx-font-size: 13px;" +
                    "-fx-line-spacing: 4;");

                slot.getChildren().addAll(userRow, divider, date, body);

            } else {
                slot.setVisible(false);
                slot.setManaged(false);
            }
        }
    }
    private String getUsernameById(int userId) {
        return userService.getUsernameById(userId);
    }

	private void loadComments(List<Comment> comments) {
        if (commentsContainer == null) return;
        currentComments    = comments != null ? comments : new java.util.ArrayList<>();
        commentScrollIndex = 0;

        // Only build the DOM structure once
        if (arrowLeft == null) initCommentSlots();

        // Show empty message if needed
        if (currentComments.isEmpty()) {
            for (VBox slot : commentSlots) { slot.setVisible(false); slot.setManaged(false); }
            arrowLeft.setVisible(false);
            arrowRight.setVisible(false);

            // Add empty label only if not already there
            if (commentsContainer.getChildren().stream()
                    .noneMatch(n -> n instanceof Label)) {
                Label empty = new Label("No reviews yet — be the first to watch and rate!");
                empty.setStyle("-fx-text-fill:#2e3850; -fx-font-size:13px; -fx-font-style:italic;");
                commentsContainer.getChildren().add(1, empty);
            }
            return;
        }

        // Remove empty label if it was added before
        commentsContainer.getChildren().removeIf(n -> n instanceof Label);
        arrowLeft.setVisible(true);
        arrowRight.setVisible(true);

        updateVisibleComments();
    }

	private void handleFlagComment(int commentId, Button flagBtn) {
	    boolean flagged = commentService.flagComment(commentId);
	    if (flagged) {
	        // ── Update button immediately ─────────────────────────────────────
	        flagBtn.setText("⚑");
	        flagBtn.setStyle(
	            "-fx-background-color: transparent;" +
	            "-fx-text-fill: #ff4444;" +
	            "-fx-font-size: 13px;" +
	            "-fx-padding: 0;");
	        flagBtn.setDisable(true);
	        flagBtn.setFocusTraversable(false);
	        flagBtn.setTooltip(
	            new javafx.scene.control.Tooltip("Reported — pending admin review"));

	        // ── Update in-memory list so isFlagged() returns true on next render ─
	        currentComments.stream()
	            .filter(c -> c.getComment_id() == commentId)
	            .findFirst()
	            .ifPresent(c -> c.setFlagged(true));
	    }
	}

    private Button buildArrowButton(String symbol, boolean enabled) {
        Button btn = new Button(symbol);
        final String BASE =
            "-fx-background-color:rgba(0,212,255,0.08);" +
            "-fx-text-fill:#00d4ff; -fx-font-size:16px;" +
            "-fx-background-radius:50%; -fx-min-width:36px; -fx-min-height:36px;" +
            "-fx-cursor:hand; -fx-border-color:rgba(0,212,255,0.25); -fx-border-radius:50%;";
        final String HOVER =
            "-fx-background-color:rgba(0,212,255,0.22);" +
            "-fx-text-fill:white; -fx-font-size:16px;" +
            "-fx-background-radius:50%; -fx-min-width:36px; -fx-min-height:36px;" +
            "-fx-cursor:hand; -fx-border-color:#00d4ff; -fx-border-radius:50%;";

        btn.setStyle(BASE);
        btn.setDisable(!enabled);
        btn.setOpacity(enabled ? 1.0 : 0.25);
        btn.setOnMouseEntered(e -> { if (enabled) btn.setStyle(HOVER); });
        btn.setOnMouseExited(e  -> btn.setStyle(BASE));
        return btn;
    }

  

    // =========================================================================
    //  CAST
    // =========================================================================
    private void loadCast() {
        if (castBox == null) return;
        castBox.getChildren().clear();

        // Get casting string from current item
        String casting = null;
        if (currentItem != null) {
            if ("film".equalsIgnoreCase(currentItem.getType()) && resolvedFilmId != null) {
                try {
                    Film film = new FeaturedService().getFilmDetails(resolvedFilmId);
                    if (film != null) casting = film.getCasting();
                } catch (SQLException e) { e.printStackTrace(); }
            } else if ("serie".equalsIgnoreCase(currentItem.getType()) && currentSerie != null) {
                casting = currentSerie.getCasting();
            }
        }

        if (casting == null || casting.trim().isEmpty()) {
            Label none = new Label("No cast info available");
            none.setStyle("-fx-text-fill:#3e4560; -fx-font-size:13px; -fx-font-style:italic;");
            castBox.getChildren().add(none);
            return;
        }

        // Parse comma-separated actor names
        String[] actors = casting.split(",");
        for (String actor : actors) {
            String name = actor.trim();
            if (!name.isEmpty())
                castBox.getChildren().add(createActorCard(name));
        }
    }

    private VBox createActorCard(String name) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(90);

        // Initials avatar
        String initials = name.contains(" ")
            ? String.valueOf(name.charAt(0)) +
              String.valueOf(name.charAt(name.indexOf(' ') + 1))
            : name.substring(0, Math.min(2, name.length()));

        Label avatar = new Label(initials.toUpperCase());
        avatar.setMinSize(60, 60);
        avatar.setMaxSize(60, 60);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(
            "-fx-background-color: rgba(0,212,255,0.15);" +
            "-fx-text-fill: #00d4ff;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 50%;" +
            "-fx-border-color: rgba(0,212,255,0.3);" +
            "-fx-border-radius: 50%;");
        avatar.setEffect(new DropShadow(16, Color.web("#00d4ff", 0.22)));

        Label nameLabel = new Label(name);
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(88);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setStyle(
            "-fx-text-fill: #7a84a0;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;");

        card.getChildren().addAll(avatar, nameLabel);
        addHoverEffect(card);
        return card;
    }

    // =========================================================================
    //  NAVBAR & NOTIFICATIONS
    // =========================================================================
    private void setupNavbar() {
        try {
            if (logoNav  != null) logoNav.setImage(
                new Image(getClass().getResourceAsStream("/assets/images/logo/Raksha.png")));
            if (bellIcon != null) bellIcon.setImage(
                new Image(getClass().getResourceAsStream("/assets/images/bellwhiter.png")));
        } catch (Exception ignored) {}
    }

    private void setupNotificationSystem() {
        try {
            bellSound = new AudioClip(
                getClass().getResource("/assets/sounds/notification.mp3").toString());
        } catch (Exception ignored) {}

        notificationContent.setStyle(
            "-fx-background-color:#0b1120;" +
            "-fx-background-radius:12;" +
            "-fx-padding:18;" +
            "-fx-spacing:10;" +
            "-fx-border-color:rgba(0,212,255,0.13);" +
            "-fx-border-radius:12;");
        notificationContent.setPrefWidth(260);

        Label popTitle = new Label("Notifications");
        popTitle.setStyle("-fx-text-fill:white; -fx-font-weight:bold; -fx-font-size:14px;");
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
        RotateTransition rt = new RotateTransition(Duration.millis(90), bellIcon);
        rt.setFromAngle(-12); rt.setToAngle(12);
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

        ParallelTransition hide  = new ParallelTransition();
        FadeTransition     fade  = new FadeTransition(Duration.millis(260), notificationCircle);
        ScaleTransition    scale = new ScaleTransition(Duration.millis(260), notificationCircle);
        fade.setFromValue(1.0); fade.setToValue(0.0);
        scale.setToX(0.0); scale.setToY(0.0);
        hide.getChildren().addAll(fade, scale);
        hide.setOnFinished(e -> {
            notificationCircle.setVisible(false);
            notificationCircle.setScaleX(1); notificationCircle.setScaleY(1);
            notificationCircle.setOpacity(1);
        });
        hide.play();
    }

    private void showPopup() {
        if (bellContainer == null) return;
        Bounds b = bellContainer.localToScreen(bellContainer.getBoundsInLocal());
        notificationPopup.show(bellContainer, b.getMinX() - 215, b.getMaxY() + 12);
    }

    // =========================================================================
    //  TRAILER POPUP
    // =========================================================================
    private void showTrailerPopup(String url) {
        try {
            URL videoUrl = getClass().getResource(url);
            String videoPath = (url.startsWith("http") || videoUrl == null)
                ? url : videoUrl.toExternalForm();

            javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
            webView.setPrefSize(1400, 680);
            webView.getEngine().loadContent(
                "<html><body style='margin:0;background:#000;'>" +
                "<video width='100%' height='100%' controls autoplay>" +
                "<source src='" + videoPath + "' type='video/mp4'></video>" +
                "</body></html>");

            javafx.geometry.Rectangle2D screen =
                javafx.stage.Screen.getPrimary().getBounds();
            double fw = screen.getWidth(), fh = screen.getHeight();
            double sw = 1200,             sh = 620;

            Stage popup = new Stage();
            popup.initOwner(btnBack.getScene().getWindow());
            popup.initModality(Modality.WINDOW_MODAL);
            popup.initStyle(StageStyle.TRANSPARENT);
            popup.setWidth(fw); popup.setHeight(fh);
            popup.setX(0); popup.setY(0);

            StackPane root = new StackPane();
            root.setStyle("-fx-background-color:rgba(0,0,0,0.88);");

            VBox layout = new VBox(14);
            layout.setStyle(
                "-fx-background-color:rgba(0,0,0,0.15);" +
                "-fx-background-radius:16; -fx-padding:16; -fx-alignment:center;");
            layout.setPrefSize(fw, fh);

            // Tab-reset helper
            Runnable resetTabs = () -> {
                if (lineOverview != null) lineOverview.setVisible(true);
                if (lineTrailers != null) lineTrailers.setVisible(false);
                if (tabOverview  != null) tabOverview.setStyle(
                    "-fx-background-color:transparent; -fx-text-fill:white;" +
                    "-fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand; -fx-padding:4 0;");
                if (tabTrailers  != null) tabTrailers.setStyle(
                    "-fx-background-color:transparent; -fx-text-fill:#4e5670;" +
                    "-fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand; -fx-padding:4 0;");
            };

            String btnStyle =
                "-fx-background-color:rgba(0,140,255,0.85); -fx-text-fill:white;" +
                "-fx-font-weight:bold; -fx-background-radius:50%; -fx-padding:5 9;";

            Button toggleSize = new Button("🗗");
            Button exit       = new Button("✕");
            toggleSize.setStyle(btnStyle);
            exit.setStyle(btnStyle);

            exit.setOnAction(ev -> {
                webView.getEngine().load(null);
                popup.close();
                resetTabs.run();
            });

            final boolean[] full = {true};
            toggleSize.setOnAction(ev -> {
                if (full[0]) {
                    popup.setWidth(sw); popup.setHeight(sh);
                    popup.setX((fw - sw) / 2); popup.setY((fh - sh) / 2);
                    layout.setPrefSize(sw, sh); full[0] = false;
                } else {
                    popup.setWidth(fw); popup.setHeight(fh);
                    popup.setX(0); popup.setY(0);
                    layout.setPrefSize(fw, fh); full[0] = true;
                }
            });

            HBox topBar = new HBox(10, toggleSize, exit);
            topBar.setAlignment(Pos.TOP_RIGHT);
            topBar.setPadding(new javafx.geometry.Insets(8));
            topBar.setPickOnBounds(false);

            layout.getChildren().addAll(topBar, webView);
            root.getChildren().add(layout);
            popup.setOnHidden(ev -> { webView.getEngine().load(null); resetTabs.run(); });

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popup.setScene(scene);
            popup.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // =========================================================================
    //  ANIMATION HELPERS
    // =========================================================================
    private void addHoverEffect(Node node) {
        ScaleTransition in  = new ScaleTransition(Duration.millis(180), node);
        ScaleTransition out = new ScaleTransition(Duration.millis(180), node);
        in.setToX(1.05);  in.setToY(1.05);
        out.setToX(1.0);  out.setToY(1.0);

        DropShadow glow = new DropShadow(22, Color.web("#2d54ff", 0.55));
        node.setOnMouseEntered(e -> { in.play();  node.setEffect(glow); });
        node.setOnMouseExited(e  -> { out.play(); node.setEffect(null); });
    }

    private void addButtonInteractions(Button btn) {
        ScaleTransition up   = new ScaleTransition(Duration.millis(90), btn);
        ScaleTransition down = new ScaleTransition(Duration.millis(90), btn);
        up.setToX(1.1);  up.setToY(1.1);
        down.setToX(1.0); down.setToY(1.0);

        DropShadow glow = new DropShadow(18, Color.web("#00d4ff", 0.75));
        glow.setSpread(0.1);

        btn.setOnMouseEntered(e  -> { up.play();           btn.setEffect(glow); });
        btn.setOnMouseExited(e   -> { down.play();         btn.setEffect(null); });
        btn.setOnMousePressed(e  -> { btn.setScaleX(0.95); btn.setScaleY(0.95); });
        btn.setOnMouseReleased(e -> { btn.setScaleX(1.1);  btn.setScaleY(1.1);  });
    }

    private void pumpButton(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(140), btn);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.18);  st.setToY(1.18);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }


}