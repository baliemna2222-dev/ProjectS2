package JStream.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import JStream.service.FeaturedService;
import JStream.service.MylistService;
import JStream.service.RatingService;
import JStream.service.UserService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    private final RatingService   ratingService   = new RatingService();
    private final CommentService  commentService  = new CommentService();
    private final MylistService   mylistService   = new MylistService();
    private final FeaturedService featuredService = new FeaturedService();
    private final UserService     userService     = new UserService();

    // ── Resolved context ──────────────────────────────────────────────────────
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

    // ── Episode/serie context ─────────────────────────────────────────────────
    private Serie   currentSerie;
    private Episode currentEpisode;
    private int     currentSeasonNum;

    // ── Comment pagination ────────────────────────────────────────────────────
    private int           commentScrollIndex = 0;
    private List<Comment> currentComments    = new ArrayList<>();

    // ── Notification system ───────────────────────────────────────────────────
    private AudioClip bellSound;
    private final Popup notificationPopup    = new Popup();
    private final VBox  notificationContent  = new VBox();
    private boolean     isNotificationVisible = false;

    // ── FXML — Navbar ─────────────────────────────────────────────────────────
    @FXML private ImageView logoNav, bellIcon;
    @FXML private Button    btnMostWatched, btnMyList;
    @FXML private StackPane bellContainer;
    @FXML private Circle    notificationCircle;
    @FXML private Button    btnBack, playButton, btnNotification, profileBtn;
    @FXML private ScrollPane mainScrollPane;

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

    // ── FXML — Watch Next ────────────────────────────────────────────────────
    @FXML private VBox watchNextContainer;

    // ── Comment slots (DOM built once) ───────────────────────────────────────
    private final VBox[] commentSlots = new VBox[3];
    private Button arrowLeft, arrowRight;

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

        if (btnNotification != null)
            btnNotification.setOnAction(e -> {
                if (isNotificationVisible) hideNotification();
                showPopup();
            });

        if (btnMostWatched != null) {
            addButtonInteractions(btnMostWatched);
            btnMostWatched.setOnAction(e -> navigateTo("/view/fxml/MyHistory.fxml"));
        }

        if (btnMyList != null) {
            addButtonInteractions(btnMyList);
            btnMyList.setOnAction(e -> navigateTo("/view/fxml/MyList.fxml"));
        }

        if (addToListButton  != null) addToListButton.setOnAction(e -> handleAddToList());
        if (posterImage      != null) addHoverEffect(posterImage);
        if (playButton       != null) addHoverEffect(playButton);
        if (btnSubmitComment != null) addButtonInteractions(btnSubmitComment);

        // Entrance fade-in
        if (mainContainer != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(900), mainContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }

        // NOTE: populateStars() is intentionally NOT called here.
        // It is called in initFilm() / initEpisode() with the real average rating,
        // preventing the stars from being overwritten with zeros after the data loads.
    }

    // =========================================================================
    //  INIT — FILM
    // =========================================================================
    public void initFilm(int filmId) {
        if (episodeInfoLabel != null) episodeInfoLabel.setVisible(false);

        try {
            Film film = featuredService.getFilmDetails(filmId);
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
                    ? film.getCategories().stream().map(c -> c.getName()).collect(Collectors.toList())
                    : new ArrayList<>(),
                film.getAge_rating(),
                film.getRating()
            );

            this.resolvedFilmId    = filmId;
            this.resolvedSerieId   = null;
            this.resolvedSeasonId  = null;
            this.resolvedEpisodeId = null;

            // Always use the fresh DB average; fall back to stored rating
            double freshAvg      = ratingService.getAverageForFilm(filmId);
            double displayRating = freshAvg > 0 ? freshAvg : film.getRating();

            updateUI(
                film.getPoster_url(),
                film.getTitle(),
                film.getSynopsis(),
                film.getDuration() + " min",
                displayRating,
                film.getCasting(),
                film.getImage_url(),
                null,
                film.getVideo_url(),
                0
            );

            if (scoreLabel != null)
                scoreLabel.setText(freshAvg > 0
                    ? String.format("%.1f", freshAvg)
                    : String.valueOf(film.getRating()));

            if (addToListButton != null) syncAddButton();

            Rating prior = ratingService.getUserRatingForFilm(Session.getUserId(), filmId);
            setupReviewSection(prior != null ? prior.getNote() : 0);
            if (prior != null) lockStars();

            loadCast();
            loadWatchNextFilms();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    //  INIT — EPISODE
    // =========================================================================
    public void initEpisode(int serieId, int seasonNum, int epId) {
        try {
            this.currentSerie     = featuredService.getFullSerie(serieId);
            this.currentSeasonNum = seasonNum;
            if (currentSerie == null) return;

            this.currentEpisode = findEpisodeById(currentSerie, epId);
            if (currentEpisode == null) {
                System.err.println("❌ Episode not found for epId=" + epId);
                return;
            }

            Season matchedSeason = null;
            for (Season s : currentSerie.getSeasons())
                if (s.getSeasonNum() == seasonNum) { matchedSeason = s; break; }

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
                    ? currentSerie.getCategories().stream().map(c -> c.getName()).collect(Collectors.toList())
                    : new ArrayList<>(),
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

            // Pass displayRating as double — no int cast, preserves half-star precision
            updateUI(
                posterUrl,
                currentEpisode.getTitle(),
                currentEpisode.getResume() != null
                    ? currentEpisode.getResume()
                    : currentSerie.getSynopsis(),
                currentEpisode.getDuration() + " min",
                displayRating,
                currentSerie.getCasting(),
                coverUrl,
                "S" + seasonNum + " · E" + currentEpisode.getNumEpisode(),
                currentEpisode.getVideoUrl(),
                currentEpisode.getEpId()
            );

            if (scoreLabel != null)
                scoreLabel.setText(freshAvg > 0
                    ? String.format("%.1f", freshAvg)
                    : String.valueOf(currentSerie.getRating()));

            if (addToListButton != null) syncAddButton();

            Rating prior = ratingService.getUserRatingForEpisode(Session.getUserId(), currentEpisode.getEpId());
            setupReviewSection(prior != null ? prior.getNote() : 0);
            if (prior != null) lockStars();

            loadCast();
            loadWatchNextEpisodes();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    //  UPDATE UI  (double rating — preserves decimal precision for half-stars)
    // =========================================================================
    public void updateUI(String poster, String title, String desc,
                         String duration, double rating, String cast,
                         String imgPath, String epInfo,
                         String video, int epId) {

        if (titleLabel       != null) titleLabel.setText(title);
        if (descriptionLabel != null) descriptionLabel.setText(desc);
        if (durationLabel    != null) durationLabel.setText(duration);
        if (starringLabel    != null) starringLabel.setText(cast != null ? cast : "—");

        if (episodeInfoLabel != null) {
            if (epInfo != null) {
                episodeInfoLabel.setText(epInfo);
                episodeInfoLabel.setVisible(true);
            } else {
                episodeInfoLabel.setVisible(false);
            }
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

        if (playButton != null) {
            playButton.setOnAction(e -> {
                if (currentItem == null) return;
                if ("film".equalsIgnoreCase(currentItem.getType())) {
                    if (currentTrailerUrl != null) openVideoPlayer(currentTrailerUrl, title, null);
                    else System.out.println("⚠️ Film video URL missing!");
                } else if ("serie".equalsIgnoreCase(currentItem.getType())) {
                    if (video != null && epId > 0) openVideoPlayer(video, title, epId);
                    else System.out.println("⚠️ Episode video not loaded!");
                }
            });
        }
    }

    /** Overload used by VideoPlayerController when advancing to next episode */
    public void updateUI(String title, String resume, String duration,
                         int rating, String casting,
                         String episodeLabel, String videoUrl, int epId) {
        String posterUrl = findSeasonPoster(currentSerie, currentSeasonNum);
        String coverUrl  = findSeasonCover(currentSerie,  currentSeasonNum);
        updateUI(posterUrl, title, resume, duration, (double) rating, casting,
                 coverUrl, episodeLabel, videoUrl, epId);
    }

    // =========================================================================
    //  NEXT EPISODE LOGIC
    // =========================================================================

    /**
     * Returns the episode that follows the current one within the serie.
     * Checks next episode in same season first, then first episode of next season.
     * Returns null if this is the last episode of the last season.
     * Does NOT mutate any state — safe to call as a peek.
     */
    public Episode getNextEpisode() {
        if (currentSerie == null || currentEpisode == null) return null;

        List<Season> seasons = currentSerie.getSeasons();
        if (seasons == null || seasons.isEmpty()) return null;

        int seasonIndex = -1;
        for (int i = 0; i < seasons.size(); i++) {
            if (seasons.get(i).getSeasonNum() == currentSeasonNum) {
                seasonIndex = i;
                break;
            }
        }
        if (seasonIndex < 0) return null;

        List<Episode> episodes = seasons.get(seasonIndex).getEpisodes();
        if (episodes == null) return null;

        int epIndex = -1;
        for (int i = 0; i < episodes.size(); i++) {
            if (episodes.get(i).getEpId() == currentEpisode.getEpId()) {
                epIndex = i;
                break;
            }
        }
        if (epIndex < 0) return null;

        // Next in same season
        if (epIndex + 1 < episodes.size())
            return episodes.get(epIndex + 1);

        // First episode of next season
        if (seasonIndex + 1 < seasons.size()) {
            Season nextSeason = seasons.get(seasonIndex + 1);
            List<Episode> nextEps = nextSeason.getEpisodes();
            if (nextEps != null && !nextEps.isEmpty())
                return nextEps.get(0);
        }

        return null; // End of series
    }

    /**
     * Returns the season number for a given episode (used when crossing season boundary).
     */
    private int getSeasonNumForEpisode(Episode ep) {
        if (currentSerie == null) return currentSeasonNum;
        for (Season s : currentSerie.getSeasons())
            if (s.getEpisodes() != null)
                for (Episode e : s.getEpisodes())
                    if (e.getEpId() == ep.getEpId()) return s.getSeasonNum();
        return currentSeasonNum;
    }

    /**
     * Advances to the next episode and reloads the full page.
     * Called by the "Next Episode" button or VideoPlayerController.
     */
    public void navigateToNextEpisode() {
        Episode next = getNextEpisode();
        if (next == null) {
            System.out.println("ℹ️ No next episode — end of series.");
            return;
        }

        // Update season tracking in case we crossed a season boundary
        int nextSeasonNum = getSeasonNumForEpisode(next);
        this.currentSeasonNum  = nextSeasonNum;
        this.currentEpisode    = next;
        this.resolvedEpisodeId = next.getEpId();
        this.resolvedSeasonId  = next.getSeasonId();

        initEpisode(resolvedSerieId, currentSeasonNum, next.getEpId());
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
    //  WATCH NEXT — FILMS (loads featured content in background thread)
    // =========================================================================
    private void loadWatchNextFilms() {
        if (watchNextContainer == null) return;
        watchNextContainer.getChildren().clear();

        new Thread(() -> {
            try {
                List<FeaturedItem> items = featuredService.getTopRated(15);
                if (items == null) items = new ArrayList<>();

                final int currentId = resolvedFilmId != null ? resolvedFilmId : -1;
                List<FeaturedItem> filtered = items.stream()
                    .filter(it -> !(("film".equalsIgnoreCase(it.getType())) && it.getId() == currentId))
                    .limit(12)
                    .collect(Collectors.toList());

                List<FeaturedItem> finalItems = filtered;
                Platform.runLater(() -> buildWatchNextCarousel(finalItems));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // =========================================================================
    //  WATCH NEXT — EPISODES (flat list of all other episodes in this serie)
    // =========================================================================
    private void loadWatchNextEpisodes() {
        if (watchNextContainer == null || currentSerie == null) return;
        watchNextContainer.getChildren().clear();

        List<EpisodeCard> cards = new ArrayList<>();
        final int currentEpId = resolvedEpisodeId != null ? resolvedEpisodeId : -1;

        for (Season s : currentSerie.getSeasons()) {
            if (s.getEpisodes() == null) continue;
            for (Episode ep : s.getEpisodes()) {
                if (ep.getEpId() == currentEpId) continue;
                cards.add(new EpisodeCard(ep, s));
            }
        }

        Platform.runLater(() -> buildWatchNextEpisodeCarousel(cards));
    }

    /** Lightweight holder for episode + its season */
    private static class EpisodeCard {
        final Episode episode;
        final Season  season;
        EpisodeCard(Episode e, Season s) { episode = e; season = s; }
    }

    // =========================================================================
    //  CAROUSEL — FILMS / FEATURED
    // =========================================================================
    private void buildWatchNextCarousel(List<FeaturedItem> items) {
        if (watchNextContainer == null) return;
        watchNextContainer.getChildren().clear();
        if (items.isEmpty()) return;

        Label header = new Label("Watch Next");
        header.setStyle(
            "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 12 0;");

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 8, 16, 8));

        for (FeaturedItem item : items)
            row.getChildren().add(buildFeaturedCard(item));

        ScrollPane scroll = buildCarouselScrollPane(row);

        Button left  = buildCarouselArrow("❮");
        Button right = buildCarouselArrow("❯");
        left.setOnAction(e  -> animateScroll(scroll, scroll.getHvalue(), Math.max(0,   scroll.getHvalue() - 0.25)));
        right.setOnAction(e -> animateScroll(scroll, scroll.getHvalue(), Math.min(1.0, scroll.getHvalue() + 0.25)));

        HBox arrowBar = new HBox(8, left, right);
        arrowBar.setAlignment(Pos.CENTER_RIGHT);
        arrowBar.setPadding(new Insets(0, 8, 6, 0));

        watchNextContainer.getChildren().addAll(header, arrowBar, scroll);
    }

    private VBox buildFeaturedCard(FeaturedItem item) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(160);
        card.setMaxWidth(160);
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.04);" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 0 0 12 0;" +
            "-fx-border-color: rgba(0,212,255,0.08);" +
            "-fx-border-radius: 14;" +
            "-fx-cursor: hand;");

        ImageView poster = new ImageView();
        poster.setFitWidth(160);
        poster.setFitHeight(100);
        poster.setPreserveRatio(false);
        String url = item.getPosterUrl() != null ? item.getPosterUrl() : item.getPosterUrl();
        if (url != null) {
            try { poster.setImage(new Image(url, true)); } catch (Exception ignored) {}
        }
        Rectangle clip = new Rectangle(160, 100);
        clip.setArcWidth(14); clip.setArcHeight(14);
        poster.setClip(clip);

        Label title = new Label(item.getTitle());
        title.setWrapText(true);
        title.setMaxWidth(140);
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-text-fill: #c0c8d8; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 4 8 0 8;");

        String typeText = "film".equalsIgnoreCase(item.getType()) ? "FILM" : "SÉRIE";
        Label badge = new Label(typeText);
        badge.setStyle(
            "-fx-background-color: rgba(0,212,255,0.15);" +
            "-fx-text-fill: #00d4ff;" +
            "-fx-font-size: 9px; -fx-font-weight: bold;" +
            "-fx-background-radius: 4; -fx-padding: 2 6;");

        card.getChildren().addAll(poster, title, badge);
        applyCardHover(card);

        card.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/fxml/LecturePage.fxml"));
                Parent root = loader.load();
                LecturePageController ctrl = loader.getController();
                ((Stage) watchNextContainer.getScene().getWindow()).getScene().setRoot(root);

                if ("film".equalsIgnoreCase(item.getType())) {
                    ctrl.initFilm(item.getId());
                } else {
                    Serie s = featuredService.getFullSerie(item.getSerieId());
                    if (s != null && s.getSeasons() != null && !s.getSeasons().isEmpty()) {
                        Season firstSeason = s.getSeasons().get(0);
                        if (firstSeason.getEpisodes() != null && !firstSeason.getEpisodes().isEmpty()) {
                            ctrl.initEpisode(item.getSerieId(),
                                firstSeason.getSeasonNum(),
                                firstSeason.getEpisodes().get(0).getEpId());
                        }
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        return card;
    }

    // =========================================================================
    //  CAROUSEL — EPISODES
    // =========================================================================
    private void buildWatchNextEpisodeCarousel(List<EpisodeCard> cards) {
        if (watchNextContainer == null) return;
        watchNextContainer.getChildren().clear();
        if (cards.isEmpty()) return;

        Label header = new Label("More Episodes");
        header.setStyle(
            "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 12 0;");

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 8, 16, 8));

        for (EpisodeCard ec : cards)
            row.getChildren().add(buildEpisodeCard(ec));

        ScrollPane scroll = buildCarouselScrollPane(row);

        Button left  = buildCarouselArrow("❮");
        Button right = buildCarouselArrow("❯");
        left.setOnAction(e  -> animateScroll(scroll, scroll.getHvalue(), Math.max(0,   scroll.getHvalue() - 0.25)));
        right.setOnAction(e -> animateScroll(scroll, scroll.getHvalue(), Math.min(1.0, scroll.getHvalue() + 0.25)));

        HBox arrowBar = new HBox(8, left, right);
        arrowBar.setAlignment(Pos.CENTER_RIGHT);
        arrowBar.setPadding(new Insets(0, 8, 6, 0));

        // "Next Episode" quick-access button (shown when there is a next ep)
        Episode nextEp = getNextEpisode();
        if (nextEp != null) {
            int nextSeasonNum = getSeasonNumForEpisode(nextEp);
            String nextLabel  = "S" + nextSeasonNum + " · E" + nextEp.getNumEpisode()
                              + " — " + nextEp.getTitle();
            Button nextBtn = new Button("▶  Next: " + nextLabel);
            nextBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #00aaff, #005fb8);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-font-weight: bold;" +
                "-fx-background-radius: 28;" +
                "-fx-border-color: transparent; -fx-border-radius: 28;" +
                "-fx-cursor: hand; -fx-padding: 10 24;");
            nextBtn.setOnAction(ev -> navigateToNextEpisode());
            addButtonInteractions(nextBtn);

            HBox nextRow = new HBox(nextBtn);
            nextRow.setAlignment(Pos.CENTER_LEFT);
            nextRow.setPadding(new Insets(0, 0, 14, 0));

            watchNextContainer.getChildren().addAll(header, nextRow, arrowBar, scroll);
        } else {
            watchNextContainer.getChildren().addAll(header, arrowBar, scroll);
        }
    }

    private VBox buildEpisodeCard(EpisodeCard ec) {
        Episode ep     = ec.episode;
        Season  season = ec.season;

        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(200);
        card.setMaxWidth(200);
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.04);" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 0 0 14 0;" +
            "-fx-border-color: rgba(0,212,255,0.08);" +
            "-fx-border-radius: 14;" +
            "-fx-cursor: hand;");

        ImageView thumb = new ImageView();
        thumb.setFitWidth(200);
        thumb.setFitHeight(110);
        thumb.setPreserveRatio(false);
        String thumbUrl = season.getPosterUrl() != null ? season.getPosterUrl() : season.getImageUrl();
        if (thumbUrl != null) {
            try { thumb.setImage(new Image(thumbUrl, true)); } catch (Exception ignored) {}
        }
        Rectangle clip = new Rectangle(200, 110);
        clip.setArcWidth(14); clip.setArcHeight(14);
        thumb.setClip(clip);

        Label badge = new Label("S" + season.getSeasonNum() + " · E" + ep.getNumEpisode());
        badge.setStyle(
            "-fx-background-color: rgba(0,212,255,0.15);" +
            "-fx-text-fill: #00d4ff;" +
            "-fx-font-size: 9px; -fx-font-weight: bold;" +
            "-fx-background-radius: 4; -fx-padding: 2 6;");
        VBox.setMargin(badge, new Insets(8, 0, 0, 10));

        Label title = new Label(ep.getTitle());
        title.setWrapText(true);
        title.setMaxWidth(180);
        title.setStyle("-fx-text-fill: #c0c8d8; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0 10;");

        Label dur = new Label(ep.getDuration() + " min");
        dur.setStyle("-fx-text-fill: #3e4560; -fx-font-size: 11px; -fx-padding: 0 10;");

        card.getChildren().addAll(thumb, badge, title, dur);
        applyCardHover(card);

        card.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/fxml/LecturePage.fxml"));
                Parent root = loader.load();
                LecturePageController ctrl = loader.getController();
                ((Stage) watchNextContainer.getScene().getWindow()).getScene().setRoot(root);
                ctrl.initEpisode(currentSerie.getSerieId(), season.getSeasonNum(), ep.getEpId());
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        return card;
    }

    // =========================================================================
    //  CAROUSEL HELPERS
    // =========================================================================
    private void applyCardHover(VBox card) {
        DropShadow glow = new DropShadow(18, Color.web("#00d4ff", 0.5));
        ScaleTransition scaleIn  = new ScaleTransition(Duration.millis(160), card);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(160), card);
        scaleIn.setToX(1.04);  scaleIn.setToY(1.04);
        scaleOut.setToX(1.0);  scaleOut.setToY(1.0);

        card.setOnMouseEntered(e -> { scaleIn.play();  card.setEffect(glow); });
        card.setOnMouseExited(e  -> { scaleOut.play(); card.setEffect(null); });
    }

    private ScrollPane buildCarouselScrollPane(HBox row) {
        ScrollPane scroll = new ScrollPane(row);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setFitToHeight(true);
        scroll.setPannable(true);
        scroll.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;");
        HBox.setHgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    private Button buildCarouselArrow(String symbol) {
        Button btn = new Button(symbol);
        final String BASE =
            "-fx-background-color: rgba(0,212,255,0.10);" +
            "-fx-text-fill: #00d4ff; -fx-font-size: 14px;" +
            "-fx-background-radius: 50%; -fx-min-width: 32px; -fx-min-height: 32px;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: rgba(0,212,255,0.25); -fx-border-radius: 50%;";
        final String HOVER =
            "-fx-background-color: rgba(0,212,255,0.25);" +
            "-fx-text-fill: white; -fx-font-size: 14px;" +
            "-fx-background-radius: 50%; -fx-min-width: 32px; -fx-min-height: 32px;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: #00d4ff; -fx-border-radius: 50%;";

        btn.setStyle(BASE);
        btn.setOnMouseEntered(e -> btn.setStyle(HOVER));
        btn.setOnMouseExited(e  -> btn.setStyle(BASE));
        return btn;
    }

    /** Smooth animated horizontal scroll */
    private void animateScroll(ScrollPane scroll, double from, double to) {
        javafx.animation.Timeline tl = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(Duration.ZERO,
                new javafx.animation.KeyValue(scroll.hvalueProperty(), from,
                    javafx.animation.Interpolator.EASE_BOTH)),
            new javafx.animation.KeyFrame(Duration.millis(350),
                new javafx.animation.KeyValue(scroll.hvalueProperty(), to,
                    javafx.animation.Interpolator.EASE_BOTH))
        );
        tl.play();
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
                "-fx-text-fill: #02040a; -fx-font-size: 14px; -fx-font-weight: bold;" +
                "-fx-background-radius: 28; -fx-border-color: transparent; -fx-border-radius: 28;" +
                "-fx-cursor: hand;");
        } else {
            addToListButton.setText("+ My List");
            addToListButton.setStyle(
                "-fx-background-color: rgba(255,255,255,0.07);" +
                "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;" +
                "-fx-background-radius: 28; -fx-border-color: rgba(255,255,255,0.18); -fx-border-radius: 28;" +
                "-fx-cursor: hand;");
        }
        pumpButton(addToListButton);
    }

    // =========================================================================
    //  TABS
    // =========================================================================
    private void setupTabLogic() {
        if (tabOverview == null || tabTrailers == null) return;

        final String ACTIVE =
            "-fx-background-color:transparent; -fx-text-fill:white;" +
            "-fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand; -fx-padding:4 0;";
        final String INACTIVE =
            "-fx-background-color:transparent; -fx-text-fill:#4e5670;" +
            "-fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand; -fx-padding:4 0;";

        tabOverview.setOnAction(e -> {
            lineOverview.setVisible(true);  lineTrailers.setVisible(false);
            tabOverview.setStyle(ACTIVE);   tabTrailers.setStyle(INACTIVE);
        });

        tabTrailers.setOnAction(e -> {
            lineOverview.setVisible(false); lineTrailers.setVisible(true);
            tabTrailers.setStyle(ACTIVE);   tabOverview.setStyle(INACTIVE);
            if (currentTrailerUrl != null && !currentTrailerUrl.isEmpty())
                showTrailerPopup(currentTrailerUrl);
            else
                System.out.println("⚠️ No trailer URL.");
        });
    }

    // =========================================================================
    //  STARS — display (works with double values for half-star precision)
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
                // Half-star — only rendered because rating is a double now
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
    //  STARS — interactive
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

        if (selectedStarNote > 0) {
            boolean alreadyRated = false;
            if (resolvedFilmId    != null) alreadyRated = ratingService.getUserRatingForFilm(userId, resolvedFilmId) != null;
            else if (resolvedEpisodeId != null) alreadyRated = ratingService.getUserRatingForEpisode(userId, resolvedEpisodeId) != null;

            if (alreadyRated) {
                System.out.println("⚠️ Already rated — skipping.");
            } else {
                Rating rating = null;
                if (resolvedFilmId != null) {
                    rating = Rating.forFilm(userId, resolvedFilmId, selectedStarNote);
                } else if (resolvedSerieId != null && resolvedSeasonId != null && resolvedEpisodeId != null) {
                    rating = Rating.forEpisode(userId, resolvedSerieId, resolvedSeasonId, resolvedEpisodeId, selectedStarNote);
                } else {
                    System.err.println("❌ Incomplete rating context.");
                }

                if (rating != null) {
                    ratingSubmitted = ratingService.submitRating(rating);
                    System.out.println(ratingSubmitted ? "✅ Rating submitted: " + selectedStarNote + "/5" : "❌ Rating FAILED");
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

        String content = commentInput != null ? commentInput.getText().trim() : "";
        if (!content.isEmpty()) {
            int filmId = resolvedFilmId    != null ? resolvedFilmId    : 0;
            int epId   = resolvedEpisodeId != null ? resolvedEpisodeId : 0;

            Comment comment = new Comment(0, userId, filmId, epId, content, false, null, null);
            commentSubmitted = commentService.postComment(comment);
            System.out.println(commentSubmitted ? "✅ Comment posted" : "❌ Comment FAILED");

            if (commentSubmitted) {
                if (commentInput != null) commentInput.clear();
                List<Comment> updated = resolvedFilmId != null
                    ? commentService.getCommentsForFilm(resolvedFilmId)
                    : resolvedEpisodeId != null
                        ? commentService.getCommentsForEpisode(resolvedEpisodeId)
                        : new ArrayList<>();
                loadComments(updated);
            }
        }

        if (!ratingSubmitted && !commentSubmitted && selectedStarNote == 0 && content.isEmpty())
            System.out.println("⚠️ Nothing to submit.");
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

        List<Comment> existing = resolvedFilmId != null
            ? commentService.getCommentsForFilm(resolvedFilmId)
            : resolvedEpisodeId != null
                ? commentService.getCommentsForEpisode(resolvedEpisodeId)
                : new ArrayList<>();
        loadComments(existing);
    }

    // =========================================================================
    //  COMMENTS — load & paginate
    // =========================================================================
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

                String username = getUsernameById(c.getUserID());
                String initials = username.length() >= 2
                    ? username.substring(0, 2).toUpperCase()
                    : username.toUpperCase();

                Label avatar = new Label(initials);
                avatar.setMinSize(36, 36);
                avatar.setMaxSize(36, 36);
                avatar.setAlignment(Pos.CENTER);
                avatar.setStyle(
                    "-fx-background-color: rgba(0,212,255,0.18);" +
                    "-fx-text-fill: #00d4ff;" +
                    "-fx-font-size: 13px; -fx-font-weight: bold;" +
                    "-fx-background-radius: 50%;");

                Label nameLabel = new Label(username);
                nameLabel.setStyle(
                    "-fx-text-fill: #c0c8d8; -fx-font-size: 13px; -fx-font-weight: bold;");

                Button flagBtn = new Button("⚑");
                flagBtn.setFocusTraversable(false);

                final String FLAG_DEFAULT =
                    "-fx-background-color: transparent; -fx-text-fill: #3e4560; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 0;";
                final String FLAG_ACTIVE =
                    "-fx-background-color: transparent; -fx-text-fill: #ff4444; -fx-font-size: 13px; -fx-padding: 0;";
                final String FLAG_HOVER =
                    "-fx-background-color: transparent; -fx-text-fill: #ff4444; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 0;";

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

                if (c.getUserID() == Session.getUserId()) {
                    flagBtn.setVisible(false);
                    flagBtn.setManaged(false);
                }

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox userRow = new HBox(10, avatar, nameLabel, spacer, flagBtn);
                userRow.setAlignment(Pos.CENTER_LEFT);

                Rectangle divider = new Rectangle(262, 1);
                divider.setFill(Color.web("#00d4ff", 0.07));

                Label date = new Label(c.getCreates_at() != null
                    ? c.getCreates_at().toLocalDateTime()
                           .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm"))
                    : "");
                date.setStyle("-fx-text-fill: #2e3850; -fx-font-size: 10px; -fx-font-weight: bold;");

                Label body = new Label(c.getContent());
                body.setWrapText(true);
                body.setMaxWidth(262);
                body.setStyle("-fx-text-fill: #8a96b0; -fx-font-size: 13px; -fx-line-spacing: 4;");

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
        currentComments    = comments != null ? comments : new ArrayList<>();
        commentScrollIndex = 0;

        if (arrowLeft == null) initCommentSlots();

        if (currentComments.isEmpty()) {
            for (VBox slot : commentSlots) { slot.setVisible(false); slot.setManaged(false); }
            arrowLeft.setVisible(false);
            arrowRight.setVisible(false);

            if (commentsContainer.getChildren().stream().noneMatch(n -> n instanceof Label)) {
                Label empty = new Label("No reviews yet — be the first to watch and rate!");
                empty.setStyle("-fx-text-fill:#2e3850; -fx-font-size:13px; -fx-font-style:italic;");
                commentsContainer.getChildren().add(1, empty);
            }
            return;
        }

        commentsContainer.getChildren().removeIf(n -> n instanceof Label);
        arrowLeft.setVisible(true);
        arrowRight.setVisible(true);
        updateVisibleComments();
    }

    private void handleFlagComment(int commentId, Button flagBtn) {
        boolean flagged = commentService.flagComment(commentId);
        if (flagged) {
            flagBtn.setText("⚑");
            flagBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #ff4444; -fx-font-size: 13px; -fx-padding: 0;");
            flagBtn.setDisable(true);
            flagBtn.setFocusTraversable(false);
            flagBtn.setTooltip(new javafx.scene.control.Tooltip("Reported — pending admin review"));

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
        btn.setOnMouseEntered(e -> { if (!btn.isDisabled()) btn.setStyle(HOVER); });
        btn.setOnMouseExited(e  -> btn.setStyle(BASE));
        return btn;
    }

    // =========================================================================
    //  CAST
    // =========================================================================
    private void loadCast() {
        if (castBox == null) return;
        castBox.getChildren().clear();

        String casting = null;
        if (currentItem != null) {
            if ("film".equalsIgnoreCase(currentItem.getType()) && resolvedFilmId != null) {
                try {
                    Film film = featuredService.getFilmDetails(resolvedFilmId);
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

        for (String actor : casting.split(",")) {
            String name = actor.trim();
            if (!name.isEmpty()) castBox.getChildren().add(createActorCard(name));
        }
    }

    private VBox createActorCard(String name) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(90);

        String initials = name.contains(" ")
            ? String.valueOf(name.charAt(0)) + String.valueOf(name.charAt(name.indexOf(' ') + 1))
            : name.substring(0, Math.min(2, name.length()));

        Label avatar = new Label(initials.toUpperCase());
        avatar.setMinSize(60, 60);
        avatar.setMaxSize(60, 60);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(
            "-fx-background-color: rgba(0,212,255,0.15);" +
            "-fx-text-fill: #00d4ff; -fx-font-size: 18px; -fx-font-weight: bold;" +
            "-fx-background-radius: 50%;" +
            "-fx-border-color: rgba(0,212,255,0.3); -fx-border-radius: 50%;");
        avatar.setEffect(new DropShadow(16, Color.web("#00d4ff", 0.22)));

        Label nameLabel = new Label(name);
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(88);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setStyle(
            "-fx-text-fill: #7a84a0; -fx-font-size: 11px; -fx-font-weight: bold;");

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
            "-fx-background-radius:12; -fx-padding:18; -fx-spacing:10;" +
            "-fx-border-color:rgba(0,212,255,0.13); -fx-border-radius:12;");
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

        FadeTransition  fade  = new FadeTransition(Duration.millis(260), notificationCircle);
        ScaleTransition scale = new ScaleTransition(Duration.millis(260), notificationCircle);
        fade.setFromValue(1.0); fade.setToValue(0.0);
        scale.setToX(0.0); scale.setToY(0.0);

        ParallelTransition hide = new ParallelTransition(fade, scale);
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

            javafx.geometry.Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
            double fw = screen.getWidth(), fh = screen.getHeight();
            double sw = 1200, sh = 620;

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

            exit.setOnAction(ev -> { webView.getEngine().load(null); popup.close(); resetTabs.run(); });

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
            topBar.setPadding(new Insets(8));
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