package JStream.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import JStream.entity.Actor;
import JStream.entity.Category;
import JStream.entity.Comment;
import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.Film;
import JStream.entity.MyListManager;
import JStream.entity.NewEpisodeInfo;
import JStream.entity.Notification;
import JStream.entity.Rating;
import JStream.entity.Season;
import JStream.entity.Serie;
import JStream.entity.Session;
import JStream.service.ActorService;
import JStream.service.CommentService;
import JStream.service.FeaturedService;
import JStream.service.MylistService;
import JStream.service.NotificationService;
import JStream.service.RatingService;
import JStream.service.UserService;
import JStream.utils.ImageUtil;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
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
import javafx.scene.control.Tooltip;
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
    private final RatingService      ratingService      = new RatingService();
    private final CommentService     commentService     = new CommentService();
    private final MylistService      mylistService      = new MylistService();
    private final ActorService       actorService       = new ActorService();
    private final UserService        userService        = new UserService();
    private final FeaturedService    featuredService    = new FeaturedService();
    private final NotificationService notificationService = new NotificationService();

    // ── Resolved context ──────────────────────────────────────────────────────
    private Integer resolvedFilmId    = null;
    private Integer resolvedSerieId   = null;
    private Integer resolvedSeasonId  = null;
    private Integer resolvedEpisodeId = null;

    // ── Star state ────────────────────────────────────────────────────────────
    private int     selectedStarNote  = 0;
    private Label[] interactiveLabels = new Label[5];

    // ── Current item ──────────────────────────────────────────────────────────
    private String       currentTrailerUrl;
    private String       currentVideoUrl;
    private FeaturedItem currentItem;

    // ── Episode / serie context ───────────────────────────────────────────────
    private Serie   currentSerie;
    private Episode currentEpisode;
    private int     currentSeasonNum;

    // ── Comment pagination ────────────────────────────────────────────────────
    private int           commentScrollIndex = 0;
    private List<Comment> currentComments    = new ArrayList<>();

    // ── Cache ─────────────────────────────────────────────────────────────────
    private List<FeaturedItem> cachedSimilarFilms  = null;
    private Integer            cachedSimilarFilmId = null;

    // ── Notification state ────────────────────────────────────────────────────
    private AudioClip bellSound;
    private Popup     notificationPopup;
    private VBox      notificationListBox;
    private Label     notifBadgeLabel;
    private int       unreadCount = 0;
    private Timeline  notifPeriodicCheck;

    // ── FXML — Navbar ─────────────────────────────────────────────────────────
    @FXML private ImageView logoNav, bellIcon;
    @FXML private Button    btnHistory, btnMyList;
    @FXML private StackPane bellContainer;
    @FXML private Circle    notificationCircle;
    @FXML private Button    btnBack, playButton, profileBtn;
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
    @FXML private Button    tabOverview, tabTrailers;

    // ── FXML — Reviews ────────────────────────────────────────────────────────
    @FXML private HBox     commentsContainer;
    @FXML private VBox     interactiveStarsBox;
    @FXML private TextArea commentInput;
    @FXML private Button   btnSubmitComment;
    @FXML private Button   addToListButton;
    @FXML private VBox     watchNextContainer;

    // ── Comment slot state ────────────────────────────────────────────────────
    private final VBox[] commentSlots = new VBox[3];
    private Button arrowLeft, arrowRight;

    // =========================================================================
    //  INITIALIZE
    // =========================================================================
    @FXML
    public void initialize() {
        setupNavbar();
        buildNotificationPopup();
        loadNotifications();
        startPeriodicNotifCheck();
        setupTabLogic();

        if (btnBack != null) {
            addButtonInteractions(btnBack);
            btnBack.setOnAction(e -> handleBackAction());
        }

        if (btnHistory != null) {
            addButtonInteractions(btnHistory);
            btnHistory.setOnAction(e -> navigateWithActiveTab("/view/fxml/MyHistory.fxml"));
        }

        if (btnMyList != null) {
            addButtonInteractions(btnMyList);
            btnMyList.setOnAction(e -> navigateWithActiveTab("/view/fxml/MyList.fxml"));
        }

        if (addToListButton  != null) addToListButton.setOnAction(e -> handleAddToList());
        if (posterImage      != null) addHoverEffect(posterImage);
        if (playButton       != null) addHoverEffect(playButton);
        if (btnSubmitComment != null) addButtonInteractions(btnSubmitComment);

        if (mainContainer != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(900), mainContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }

        initCommentSlots();
        populateStars(0);

        // Scroll to top when page loads
        if (mainScrollPane != null) {
            Platform.runLater(() -> mainScrollPane.setVvalue(0));
        }
    }

    // =========================================================================
    //  NAVBAR SETUP
    // =========================================================================
    private void setupNavbar() {
        try {
            if (logoNav  != null) logoNav.setImage(
                new Image(getClass().getResourceAsStream("/assets/images/logo/Raksha.png")));
            if (bellIcon != null) bellIcon.setImage(
                new Image(getClass().getResourceAsStream("/assets/images/bellwhiter.png")));
        } catch (Exception ignored) {}

        // Load bell sound
        try {
            bellSound = new AudioClip(
                getClass().getResource("/assets/sounds/notification.mp3").toString());
            bellSound.setVolume(0.6);
        } catch (Exception ignored) {}

        // Bell click → open/close panel (same as HeaderController)
        if (bellContainer != null) {
            bellContainer.setOnMouseClicked(e -> {
                if (notificationPopup != null && notificationPopup.isShowing()) {
                    notificationPopup.hide();
                } else {
                    checkNewEpisodeNotifications(Session.getUserId());
                    unreadCount = notificationService.getUnreadCount(Session.getUserId());
                    updateBadge();
                    refreshNotificationPanel();
                    if (notificationPopup != null && bellContainer.getScene() != null) {
                        Bounds b = bellContainer.localToScreen(bellContainer.getBoundsInLocal());
                        notificationPopup.show(bellContainer, b.getMaxX() - 340, b.getMaxY() + 10);
                    }
                }
            });
            bellContainer.setCursor(javafx.scene.Cursor.HAND);
        }
    }

    // =========================================================================
    //  NOTIFICATION POPUP  (mirrors HeaderController exactly)
    // =========================================================================
    private void buildNotificationPopup() {
        notificationPopup = new Popup();
        notificationPopup.setAutoHide(true);

        VBox root = new VBox(0);
        root.setPrefWidth(340);
        root.setMaxHeight(420);
        root.setStyle(
            "-fx-background-color: #0d1117;" +
            "-fx-border-color: #21262d;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 24, 0.4, 0, 6);");

        // ── Header ────────────────────────────────────────────────────────────
        HBox header = new HBox();
        header.setPadding(new Insets(14, 16, 12, 16));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #161b22; -fx-background-radius: 12 12 0 0;");

        Label titleLbl = new Label("Notifications");
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");

        notifBadgeLabel = new Label("");
        notifBadgeLabel.setStyle(
            "-fx-background-color: #008cff; -fx-text-fill: white; -fx-font-size: 10;" +
            "-fx-font-weight: bold; -fx-padding: 2 7; -fx-background-radius: 20;");
        notifBadgeLabel.setVisible(false);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button markAllRead = new Button("Mark all read");
        markAllRead.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #008cff;" +
            "-fx-font-size: 11; -fx-cursor: hand;");
        markAllRead.setOnAction(e -> {
            notificationService.markAllRead(Session.getUserId());
            unreadCount = 0;
            updateBadge();
            refreshNotificationPanel();
        });

        Button deleteAllBtn = new Button("Clear all");
        deleteAllBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #ff4d4f;" +
            "-fx-font-size: 11; -fx-cursor: hand;");
        deleteAllBtn.setOnAction(e -> {
            notificationService.deleteAll(Session.getUserId());
            unreadCount = 0;
            updateBadge();
            refreshNotificationPanel();
        });

        header.getChildren().addAll(
            titleLbl, new HBox(6, notifBadgeLabel),
            headerSpacer, markAllRead, deleteAllBtn);

        // ── List ──────────────────────────────────────────────────────────────
        notificationListBox = new VBox(0);
        notificationListBox.setStyle("-fx-background-color: #0d1117;");

        ScrollPane scroll = new ScrollPane(notificationListBox);
        scroll.setFitToWidth(true);
        scroll.setMaxHeight(350);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: #0d1117; -fx-background-color: #0d1117;");
        try {
            scroll.getStylesheets().add(
                getClass().getResource("/view/css/scrollbar.css").toExternalForm());
        } catch (Exception ignored) {}

        root.getChildren().addAll(header, scroll);
        notificationPopup.getContent().add(root);
    }

    private void loadNotifications() {
        int userId = Session.getUserId();
        if (notificationService.isFirstLogin(userId)) {
            notificationService.addNotification(userId,
                "👋 Welcome to Raksha!",
                "Your account is set up. Start exploring movies and series.",
                "WELCOME");
            notificationService.markFirstLoginDone(userId);
            Platform.runLater(this::playBellAndShake);
        }
        checkNewEpisodeNotifications(userId);
        unreadCount = notificationService.getUnreadCount(userId);
        updateBadge();
    }

    private void checkNewEpisodeNotifications(int userId) {
        try {
            List<NewEpisodeInfo> newEps = notificationService.getNewEpisodesForUser(userId);
            if (!newEps.isEmpty()) {
                for (NewEpisodeInfo info : newEps) {
                    notificationService.addNotification(userId,
                        "🎬 New episode: " + info.getSerieTitle(),
                        "Season " + info.getSeasonNum() + ", Episode " + info.getEpNum() +
                        " — \"" + info.getEpTitle() + "\" is now available!",
                        "NEW_EPISODE");
                }
                Platform.runLater(() -> {
                    playBellAndShake();
                    unreadCount = notificationService.getUnreadCount(userId);
                    updateBadge();
                });
            }
        } catch (Exception ignored) {}
    }

    private void startPeriodicNotifCheck() {
        notifPeriodicCheck = new Timeline(new KeyFrame(Duration.seconds(60), e ->
            checkNewEpisodeNotifications(Session.getUserId())));
        notifPeriodicCheck.setCycleCount(javafx.animation.Animation.INDEFINITE);
        notifPeriodicCheck.play();
    }

    private void refreshNotificationPanel() {
        if (notificationListBox == null) return;
        notificationListBox.getChildren().clear();
        List<Notification> list = notificationService.getNotifications(Session.getUserId());

        if (list.isEmpty()) {
            Label empty = new Label("No notifications yet");
            empty.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13; -fx-padding: 30;");
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            notificationListBox.getChildren().add(empty);
            return;
        }

        for (Notification n : list) {
            VBox row = new VBox(3);
            row.setPadding(new Insets(12, 16, 12, 16));
            boolean isUnread = !n.isRead();
            String rowBg = isUnread
                ? "-fx-background-color: rgba(0,140,255,0.07);"
                : "-fx-background-color: transparent;";
            row.setStyle(rowBg + "-fx-cursor: hand;");

            HBox topLine = new HBox(8);
            topLine.setAlignment(Pos.CENTER_LEFT);
            if (isUnread) topLine.getChildren().add(new Circle(4, Color.DODGERBLUE));

            Label notifTitle = new Label(n.getTitle());
            notifTitle.setStyle(
                "-fx-text-fill: " + (isUnread ? "white" : "#c9d1d9") + ";" +
                "-fx-font-size: 13; -fx-font-weight: " + (isUnread ? "bold" : "normal") + ";");
            notifTitle.setWrapText(true);
            notifTitle.setMaxWidth(240);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button deleteBtn = new Button("✕");
            final String DEL_BASE  = "-fx-background-color: transparent; -fx-text-fill: #8b949e; -fx-font-size: 12; -fx-cursor: hand;";
            final String DEL_HOVER = "-fx-background-color: transparent; -fx-text-fill: #ff4d4f; -fx-font-size: 12; -fx-cursor: hand;";
            deleteBtn.setStyle(DEL_BASE);
            deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(DEL_HOVER));
            deleteBtn.setOnMouseExited (e -> deleteBtn.setStyle(DEL_BASE));
            deleteBtn.setOnAction(e -> {
                notificationService.deleteNotification(n.getId());
                notificationListBox.getChildren().remove(row);
                if (!n.isRead()) { unreadCount = Math.max(0, unreadCount - 1); updateBadge(); }
            });

            topLine.getChildren().addAll(notifTitle, spacer, deleteBtn);

            Label bodyLbl = new Label(n.getBody());
            bodyLbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11;");
            bodyLbl.setWrapText(true); bodyLbl.setMaxWidth(300);

            Label timeLbl = new Label(formatNotifTime(n.getCreatedAt()));
            timeLbl.setStyle("-fx-text-fill: #484f58; -fx-font-size: 10;");

            row.getChildren().addAll(topLine, bodyLbl, timeLbl);

            Region div = new Region();
            div.setPrefHeight(1);
            div.setStyle("-fx-background-color: #21262d;");

            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #161b22; -fx-cursor: hand;"));
            row.setOnMouseExited (e -> row.setStyle(rowBg + "-fx-cursor: hand;"));
            row.setOnMouseClicked(e -> {
                notificationService.markRead(n.getId());
                refreshNotificationPanel();
                unreadCount = Math.max(0, unreadCount - 1);
                updateBadge();
            });

            notificationListBox.getChildren().addAll(row, div);
        }
    }

    private void updateBadge() {
        Platform.runLater(() -> {
            if (notificationCircle != null)
                notificationCircle.setVisible(unreadCount > 0);
            if (notifBadgeLabel != null) {
                notifBadgeLabel.setVisible(unreadCount > 0);
                notifBadgeLabel.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
            }
        });
    }

    private void playBellAndShake() {
        try { if (bellSound != null) bellSound.play(); } catch (Exception ignored) {}
        if (bellIcon == null) return;
        new Timeline(
            new KeyFrame(Duration.ZERO,         new KeyValue(bellIcon.rotateProperty(),   0)),
            new KeyFrame(Duration.millis(100),   new KeyValue(bellIcon.rotateProperty(), -10)),
            new KeyFrame(Duration.millis(200),   new KeyValue(bellIcon.rotateProperty(),  10)),
            new KeyFrame(Duration.millis(300),   new KeyValue(bellIcon.rotateProperty(), -15)),
            new KeyFrame(Duration.millis(400),   new KeyValue(bellIcon.rotateProperty(),  15)),
            new KeyFrame(Duration.millis(500),   new KeyValue(bellIcon.rotateProperty(), -20)),
            new KeyFrame(Duration.millis(600),   new KeyValue(bellIcon.rotateProperty(),  20)),
            new KeyFrame(Duration.millis(700),   new KeyValue(bellIcon.rotateProperty(), -15)),
            new KeyFrame(Duration.millis(800),   new KeyValue(bellIcon.rotateProperty(),  15)),
            new KeyFrame(Duration.millis(900),   new KeyValue(bellIcon.rotateProperty(), -10)),
            new KeyFrame(Duration.millis(1000),  new KeyValue(bellIcon.rotateProperty(),   0))
        ).play();
    }

    private String formatNotifTime(java.time.LocalDateTime dt) {
        if (dt == null) return "";
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        long minutes = java.time.Duration.between(dt, now).toMinutes();
        if (minutes < 1)    return "just now";
        if (minutes < 60)   return minutes + "m ago";
        if (minutes < 1440) return (minutes / 60) + "h ago";
        return dt.format(java.time.format.DateTimeFormatter.ofPattern("MMM d"));
    }

    // =========================================================================
    //  INIT — FILM
    // =========================================================================
    public void initFilm(int filmId) {
        if (episodeInfoLabel != null) episodeInfoLabel.setVisible(false);

        try {
            Film film = featuredService.getFilmDetail(filmId);
            if (film == null) return;

            // =========================
            // RAW DATA (kept as String)
            // =========================
            String posterUrl      = film.getPoster_url();
            String imageUrl       = film.getImage_url();
            String titleImageUrl  = film.getTitle_image_url();
            String trailerUrl     = film.getTrailer_url();
            String videoUrl       = film.getVideo_url();

            this.currentTrailerUrl = trailerUrl;
            this.currentVideoUrl   = videoUrl;

            this.currentItem = new FeaturedItem(
                film.getFilm_id(),
                film.getTitle(),
                film.getSynopsis(),
                trailerUrl,
                imageUrl,
                titleImageUrl,
                posterUrl,
                film.getCategories() != null
                    ? film.getCategories().stream().map(c -> c.getName()).collect(Collectors.toList())
                    : new ArrayList<>(),
                film.getAge_rating(),
                film.getRating(),
                film.getRelease_date() != null ? film.getRelease_date().getYear() : 0,
                film.getDirector()
            );

            // =========================
            // IDS
            // =========================
            this.resolvedFilmId    = filmId;
            this.resolvedSerieId   = null;
            this.resolvedSeasonId  = null;
            this.resolvedEpisodeId = null;

            // =========================
            // CACHE RESET
            // =========================
            if (!Integer.valueOf(filmId).equals(cachedSimilarFilmId)) {
                cachedSimilarFilms = null;
                cachedSimilarFilmId = null;
            }

            // =========================
            // UI UPDATE (IMPORTANT PART)
            // =========================
            updateUI(
                posterUrl,
                film.getTitle(),
                film.getSynopsis(),
                film.getDuration() + " min",
                film.getRating(),
                film.getCasting(),
                film.getDirector(),
                film.getPosterV_url(),
                null,
                videoUrl,
                0
            );

            // =========================
            // LABELS
            // =========================
            if (yearLabel != null && film.getRelease_date() != null) {
                yearLabel.setText(String.valueOf(film.getRelease_date().getYear()));
            }

            if (ageRatingLabel != null) {
                ageRatingLabel.setText(
                    film.getAge_rating() != null ? film.getAge_rating() : ""
                );
            }

            if (addToListButton != null) {
                syncAddButton();
            }

            // =========================
            // RATING
            // =========================
            Rating prior = ratingService.getUserRatingForFilm(Session.getUserId(), filmId);

            setupReviewSection(prior != null ? prior.getNote() : 0);

            if (prior != null) {
                lockStars();
            }

            // =========================
            // EXTRA DATA
            // =========================
            loadCast();
            populateWatchNext();

            if (mainScrollPane != null) {
                Platform.runLater(() -> mainScrollPane.setVvalue(0));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    //  INIT — EPISODE
    // =========================================================================
    public void initEpisode(int serieId, int seasonNum, int epId) {
        try {

            this.currentSerie = featuredService.getSerieDetail(serieId);
            this.currentSeasonNum = seasonNum;

            if (currentSerie == null) return;

            this.currentEpisode = findEpisodeById(currentSerie, epId);
            if (currentEpisode == null) return;

            Season matchedSeason = null;

            for (Season s : currentSerie.getSeasons()) {
                if (s.getSeasonNum() == seasonNum) {
                    matchedSeason = s;
                    break;
                }
            }

            // =========================
            // SAFE RAW DATA EXTRACTION
            // =========================
            String posterUrl = null;
            String coverUrl = null;
            String trailerUrl = null;

            if (matchedSeason != null) {
                posterUrl = matchedSeason.getPosterUrl();
                coverUrl = matchedSeason.getImageUrl();
                trailerUrl = matchedSeason.getTrailerUrl();
            }

            this.currentTrailerUrl = trailerUrl;
            this.currentVideoUrl = currentEpisode.getVideoUrl();

            // =========================
            // FEATURED ITEM CREATION
            // =========================
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
                    ? currentSerie.getCategories()
                        .stream()
                        .map(Category::getName)
                        .collect(Collectors.toList())
                    : new ArrayList<>(),
                currentSerie.getAge_rating(),
                currentSerie.getRating(),
                null,
                seasonNum,
                currentEpisode.getNumEpisode(),
                currentSerie.getCreatedAt() != null
                    ? currentSerie.getCreatedAt().toLocalDateTime().getYear()
                    : 0,
                currentSerie.getDirector()
            );

            // =========================
            // IDS
            // =========================
            this.resolvedFilmId = null;
            this.resolvedSerieId = serieId;
            this.resolvedSeasonId = currentEpisode.getSeasonId();
            this.resolvedEpisodeId = currentEpisode.getEpId();

            // =========================
            // RATING LOGIC
            // =========================
            double freshAvg = ratingService.getAverageForEpisode(currentEpisode.getEpId());
            double displayRating = freshAvg > 0 ? freshAvg : currentSerie.getRating();

            // =========================
            // UI UPDATE (IMPORTANT)
            // =========================
            updateUI(
                posterUrl,
                currentEpisode.getTitle(),
                currentEpisode.getResume() != null
                    ? currentEpisode.getResume()
                    : currentSerie.getSynopsis(),
                currentEpisode.getDuration() + " min",
                (int) displayRating,
                currentSerie.getCasting(),
                currentSerie.getDirector(),
                coverUrl,
                "S" + seasonNum + " · E" + currentEpisode.getNumEpisode(),
                currentEpisode.getVideoUrl(),
                currentEpisode.getEpId()
            );

            // =========================
            // LABELS
            // =========================
            if (yearLabel != null && currentSerie.getCreatedAt() != null) {
                yearLabel.setText(
                    String.valueOf(currentSerie.getCreatedAt().toLocalDateTime().getYear())
                );
            }

            if (ageRatingLabel != null) {
                ageRatingLabel.setText(
                    currentSerie.getAge_rating() != null ? currentSerie.getAge_rating() : ""
                );
            }

            if (scoreLabel != null) {
                scoreLabel.setText(
                    freshAvg > 0
                        ? String.format("%.1f", freshAvg)
                        : String.valueOf(currentSerie.getRating())
                );
            }

            // =========================
            // STARS + UI STATE
            // =========================
            populateStars(displayRating);

            if (addToListButton != null) {
                syncAddButton();
            }

            // =========================
            // USER RATING
            // =========================
            Rating prior = ratingService.getUserRatingForEpisode(
                Session.getUserId(),
                currentEpisode.getEpId()
            );

            setupReviewSection(prior != null ? prior.getNote() : 0);

            if (prior != null) {
                lockStars();
            }

            // =========================
            // EXTRA DATA
            // =========================
            loadCast();
            populateWatchNext();

            if (mainScrollPane != null) {
                Platform.runLater(() -> mainScrollPane.setVvalue(0));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    //  UPDATE UI
    // =========================================================================
    public void updateUI(String poster, String title, String desc,
                         String duration, int rating, String cast,
                         String director, String bgImagePath,
                         String epInfo, String video, int epId) {

        if (titleLabel       != null) titleLabel.setText(title);
        if (descriptionLabel != null) descriptionLabel.setText(desc);
        if (durationLabel    != null) durationLabel.setText(duration);
        if (starringLabel    != null) starringLabel.setText(cast     != null ? cast     : "—");
        if (directorLabel    != null) directorLabel.setText(director != null ? director : "—");
        if (scoreLabel       != null) scoreLabel.setText(String.valueOf(rating));

        if (yearLabel != null && currentItem != null && currentItem.getReleaseYear() > 0)
            yearLabel.setText(String.valueOf(currentItem.getReleaseYear()));
        if (categoriesLabel != null && currentItem != null)
            categoriesLabel.setText(currentItem.getCategoriesAsString());

        if (episodeInfoLabel != null) {
            if (epInfo != null) { episodeInfoLabel.setText(epInfo); episodeInfoLabel.setVisible(true); }
            else                  episodeInfoLabel.setVisible(false);
        }
        if (bgImagePath != null && backgroundImage != null) {
            backgroundImage.setImage(ImageUtil.load(bgImagePath));
        }

        if (poster != null && posterImage != null) {
            posterImage.setImage(ImageUtil.load(poster));
        }
        populateStars(rating);

        if (playButton != null) {
            playButton.setOnAction(e -> {
                if (currentItem == null) return;
                if (resolvedFilmId != null) {
                    if (currentVideoUrl != null) openVideoPlayer(currentVideoUrl, title, null);
                } else {
                    if (video != null && epId > 0) openVideoPlayer(video, title, epId);
                }
            });
        }
    }

    public void updateUI(String title, String resume, String duration,
                         int rating, String casting,
                         String episodeLabel, String videoUrl, int epId) {
        if (currentSerie == null) return;
        String posterUrl = findSeasonPoster(currentSerie, currentSeasonNum);
        String coverUrl  = findSeasonCover(currentSerie,  currentSeasonNum);
        updateUI(posterUrl, title, resume, duration, rating, casting,
                 currentSerie.getDirector(), coverUrl, episodeLabel, videoUrl, epId);
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
        if (videoUrl == null || videoUrl.trim().isEmpty()) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/VideoPlayer.fxml"));
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
            } else {
                ctrl.setContext(null, epId);
                ctrl.setEpisodeContext(currentEpisode.getSeasonId(), currentEpisode.getNumEpisode());
            }

            Scene scene = new Scene(root);
            scene.setFill(Color.BLACK);
            stage.setScene(scene);
            javafx.geometry.Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
            stage.setX(screen.getMinX()); stage.setY(screen.getMinY());
            stage.setWidth(screen.getWidth()); stage.setHeight(screen.getHeight());
            stage.show();
            ctrl.startPlayback();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // =========================================================================
    //  NAVIGATION  — syncs HeaderController.lastActiveFxml so the header
    //  highlights the correct tab when the target page loads
    // =========================================================================
    @FXML
    private void handleBackAction() {
        stopPeriodicNotifCheck();
        HeaderController.lastActiveFxml = "/view/fxml/HomePage.fxml";
        navigateTo("/view/fxml/HomePage.fxml");
    }

    private void navigateWithActiveTab(String fxmlPath) {
        stopPeriodicNotifCheck();
        HeaderController.lastActiveFxml = fxmlPath;
        navigateTo(fxmlPath);
    }

    private void navigateTo(String fxmlPath) {
        try {
            URL loc = getClass().getResource(fxmlPath);
            if (loc == null) { System.err.println("FXML not found: " + fxmlPath); return; }
            Parent root = new FXMLLoader(loc).load();
            Stage stage = (Stage) (btnBack != null
                ? btnBack.getScene().getWindow()
                : mainContainer.getScene().getWindow());
            stage.getScene().setRoot(root);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** Stop the periodic notification timer when leaving this page. */
    private void stopPeriodicNotifCheck() {
        if (notifPeriodicCheck != null) notifPeriodicCheck.stop();
    }

    // =========================================================================
    //  MY LIST
    // =========================================================================
    private void handleAddToList() {
        if (currentItem == null) return;
        int userId  = Session.getUserId();
        int filmId  = resolvedFilmId  != null ? resolvedFilmId  : 0;
        int serieId = resolvedSerieId != null ? resolvedSerieId : 0;

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
        int filmId  = resolvedFilmId  != null ? resolvedFilmId  : 0;
        int serieId = resolvedSerieId != null ? resolvedSerieId : 0;
        boolean inList = mylistService.isInList(userId, filmId, serieId);

        if (inList) {
            addToListButton.setText("✔  Added");
            addToListButton.setStyle(
                "-fx-background-color: linear-gradient(to right,#00aaff,#005fb8);" +
                "-fx-text-fill: #02040a; -fx-font-size: 14px; -fx-font-weight: bold;" +
                "-fx-background-radius: 28; -fx-border-color: transparent;" +
                "-fx-border-radius: 28; -fx-cursor: hand;");
        } else {
            addToListButton.setText("+ My List");
            addToListButton.setStyle(
                "-fx-background-color: rgba(255,255,255,0.07); -fx-text-fill: white;" +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 28;" +
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

        final String ACTIVE   = "-fx-background-color:transparent; -fx-text-fill:white; -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand; -fx-padding:4 0;";
        final String INACTIVE = "-fx-background-color:transparent; -fx-text-fill:#4e5670; -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand; -fx-padding:4 0;";

        tabOverview.setOnAction(e -> {
            lineOverview.setVisible(true); lineTrailers.setVisible(false);
            tabOverview.setStyle(ACTIVE);  tabTrailers.setStyle(INACTIVE);
        });

        tabTrailers.setOnAction(e -> {
            lineOverview.setVisible(false); lineTrailers.setVisible(true);
            tabTrailers.setStyle(ACTIVE);   tabOverview.setStyle(INACTIVE);
            if (currentTrailerUrl != null && !currentTrailerUrl.isBlank())
                showTrailerPopup(currentTrailerUrl);
        });
    }

    // =========================================================================
    //  STARS — display
    // =========================================================================
    private void populateStars(double rawAvg) {
        if (starsBox == null) return;
        starsBox.getChildren().clear();
        double val = Math.max(0, Math.min(5, rawAvg));

        for (int i = 1; i <= 5; i++) {
            StackPane starPane = new StackPane();
            starPane.setMinSize(28, 28);
            Label empty  = new Label("★");
            empty.setStyle("-fx-text-fill:#1a2035; -fx-font-size:22px;");
            Label filled = new Label("★");
            double fill = Math.min(1.0, Math.max(0.0, val - (i - 1)));

            if (fill >= 1.0) {
                filled.setStyle("-fx-text-fill:#00d4ff; -fx-font-size:22px; -fx-effect:dropshadow(three-pass-box,rgba(0,212,255,0.9),16,0,0,0);");
                starPane.getChildren().addAll(empty, filled);
            } else if (fill > 0) {
                filled.setStyle("-fx-text-fill:#00d4ff; -fx-font-size:22px; -fx-effect:dropshadow(three-pass-box,rgba(0,212,255,0.5),10,0,0,0);");
                filled.setClip(new Rectangle(fill * 22, 28));
                starPane.getChildren().addAll(empty, filled);
            } else {
                starPane.getChildren().add(empty);
            }
            starsBox.getChildren().add(starPane);
        }

        if (rawAvg > 0) {
            Label numericScore = new Label(String.format("%.1f", rawAvg));
            numericScore.setStyle("-fx-text-fill: rgba(0,212,255,0.7); -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 0 0 0 6;");
            starsBox.getChildren().add(numericScore);
        }
    }

    // =========================================================================
    //  STARS — interactive
    // =========================================================================
    private void setupInteractiveStars(int preselected) {
        if (interactiveStarsBox == null) return;
        interactiveStarsBox.getChildren().clear();
        selectedStarNote = preselected;

        Label ratingHint = new Label(preselected > 0 ? ratingLabel(preselected) : "Tap to rate");
        ratingHint.setStyle("-fx-text-fill: #3e4560; -fx-font-size: 11px; -fx-font-style: italic;");

        HBox starsRow = new HBox(6);
        starsRow.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < 5; i++) {
            Label star = new Label(preselected > i ? "★" : "☆");
            star.setStyle(preselected > i
                ? "-fx-text-fill:#00d4ff; -fx-font-size:30px; -fx-cursor:hand; -fx-effect:dropshadow(three-pass-box,rgba(0,212,255,0.9),14,0,0,0);"
                : "-fx-text-fill:#1e2535; -fx-font-size:30px; -fx-cursor:hand;");
            interactiveLabels[i] = star;
            final int idx = i + 1;

            star.setOnMouseEntered(e -> {
                paintInteractive(idx);
                ratingHint.setText(ratingLabel(idx));
                ratingHint.setStyle("-fx-text-fill: #00d4ff; -fx-font-size: 11px; -fx-font-weight: bold;");

                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(120), star);
                scaleUp.setToX(1.25);
                scaleUp.setToY(1.25);
                scaleUp.playFromStart();
            });

            star.setOnMouseExited(e -> {
                paintInteractive(selectedStarNote);
                ratingHint.setText(selectedStarNote > 0 ? ratingLabel(selectedStarNote) : "Tap to rate");
                ratingHint.setStyle("-fx-text-fill: #3e4560; -fx-font-size: 11px; -fx-font-style: italic;");

                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(120), star);
                scaleDown.setToX(1.0);
                scaleDown.setToY(1.0);
                scaleDown.playFromStart();
            });
            star.setOnMouseClicked(e -> {
                selectedStarNote = idx;
                paintInteractive(selectedStarNote);
                ratingHint.setText("★ " + ratingLabel(selectedStarNote));
                ratingHint.setStyle("-fx-text-fill: #00d4ff; -fx-font-size: 11px; -fx-font-weight: bold;");
                ScaleTransition bounce = new ScaleTransition(Duration.millis(100), star);
                bounce.setToX(1.4); bounce.setToY(1.4);
                bounce.setAutoReverse(true); bounce.setCycleCount(2);
                bounce.play();
            });
            starsRow.getChildren().add(star);
        }
        interactiveStarsBox.getChildren().addAll(starsRow, ratingHint);
        if (preselected > 0) paintInteractive(preselected);
    }

    private String ratingLabel(int stars) {
        return switch (stars) {
            case 1 -> "Poor"; case 2 -> "Fair"; case 3 -> "Good";
            case 4 -> "Great"; case 5 -> "Outstanding!"; default -> "Tap to rate";
        };
    }

    private void paintInteractive(int upTo) {
        for (int i = 0; i < interactiveLabels.length; i++) {
            if (interactiveLabels[i] == null) continue;
            if (i < upTo) {
                interactiveLabels[i].setText("★");
                interactiveLabels[i].setStyle("-fx-text-fill:#00d4ff; -fx-font-size:30px; -fx-cursor:hand; -fx-effect:dropshadow(three-pass-box,rgba(0,212,255,0.9),14,0,0,0);");
            } else {
                interactiveLabels[i].setText("☆");
                interactiveLabels[i].setStyle("-fx-text-fill:#1e2535; -fx-font-size:30px; -fx-cursor:hand;");
            }
        }
    }

    private void lockStars() {
        if (interactiveStarsBox == null) return;
        for (Label star : interactiveLabels) {
            if (star == null) continue;
            star.setOnMouseEntered(null); star.setOnMouseExited(null); star.setOnMouseClicked(null);
            star.setStyle(star.getStyle().replace("-fx-cursor:hand;", "-fx-cursor:default;") + " -fx-opacity:0.75;");
        }
        Label locked = new Label("✔  You've rated this");
        locked.setStyle(
            "-fx-background-color: rgba(0,212,255,0.08); -fx-border-color: rgba(0,212,255,0.2);" +
            "-fx-border-radius: 20; -fx-background-radius: 20;" +
            "-fx-text-fill: rgba(0,212,255,0.6); -fx-font-size: 11px;" +
            "-fx-font-weight: bold; -fx-padding: 3 10;");
        interactiveStarsBox.getChildren().removeIf(n -> n instanceof Label);
        interactiveStarsBox.getChildren().add(locked);
    }

    // =========================================================================
    //  SUBMIT REVIEW
    // =========================================================================
    private void handleSubmitReview() {
        int userId = Session.getUserId();

        if (selectedStarNote > 0) {
            boolean alreadyRated = resolvedFilmId != null
                ? ratingService.getUserRatingForFilm(userId, resolvedFilmId) != null
                : resolvedEpisodeId != null && ratingService.getUserRatingForEpisode(userId, resolvedEpisodeId) != null;

            if (!alreadyRated) {
                Rating rating = null;
                if (resolvedFilmId != null)
                    rating = Rating.forFilm(userId, resolvedFilmId, selectedStarNote);
                else if (resolvedSerieId != null && resolvedSeasonId != null && resolvedEpisodeId != null)
                    rating = Rating.forEpisode(userId, resolvedSerieId, resolvedSeasonId, resolvedEpisodeId, selectedStarNote);

                if (rating != null && ratingService.submitRating(rating)) {
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
            int filmId = resolvedFilmId != null ? resolvedFilmId : 0;
            int epId   = resolvedEpisodeId != null ? resolvedEpisodeId : 0;
            Comment comment = new Comment(0, userId, filmId, epId, content, false, null, null);
            if (commentService.postComment(comment)) {
                if (commentInput != null) commentInput.clear();
                List<Comment> updated = resolvedFilmId != null
                    ? commentService.getCommentsForFilm(resolvedFilmId)
                    : resolvedEpisodeId != null
                        ? commentService.getCommentsForEpisode(resolvedEpisodeId)
                        : new ArrayList<>();
                loadComments(updated);
            }
        }
    }

    // =========================================================================
    //  REVIEW SECTION
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
    //  COMMENTS
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
            if (commentScrollIndex + 3 < currentComments.size()) { commentScrollIndex++; updateVisibleComments(); }
        });

        commentsContainer.getChildren().add(arrowLeft);
        for (int i = 0; i < 3; i++) {
            commentSlots[i] = new VBox(12);
            commentSlots[i].setPrefWidth(290); commentSlots[i].setMaxWidth(290);
            commentSlots[i].setMinHeight(130);
            commentSlots[i].setStyle(
                "-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius: 16;" +
                "-fx-padding: 16 18; -fx-border-color: rgba(0,212,255,0.08);" +
                "-fx-border-radius: 16; -fx-border-width: 1;");
            commentsContainer.getChildren().add(commentSlots[i]);
        }
        commentsContainer.getChildren().add(arrowRight);
    }

    private void updateVisibleComments() {
        boolean canLeft  = commentScrollIndex > 0;
        boolean canRight = commentScrollIndex + 3 < currentComments.size();

        arrowLeft.setDisable(!canLeft);   arrowLeft.setOpacity(canLeft   ? 1.0 : 0.25);
        arrowRight.setDisable(!canRight); arrowRight.setOpacity(canRight ? 1.0 : 0.25);

        for (int i = 0; i < 3; i++) {
            VBox slot = commentSlots[i];
            slot.getChildren().clear();
            int dataIndex = commentScrollIndex + i;

            if (dataIndex < currentComments.size()) {
                Comment c = currentComments.get(dataIndex);
                slot.setVisible(true); slot.setManaged(true);

                String username = userService.getUsernameById(c.getUserID());
                String initials = username.length() >= 2
                    ? username.substring(0, 2).toUpperCase() : username.toUpperCase();

                Label avatar = new Label(initials);
                avatar.setMinSize(40, 40); avatar.setMaxSize(40, 40);
                avatar.setAlignment(Pos.CENTER);
                avatar.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right,rgba(0,212,255,0.3),rgba(0,128,224,0.15));" +
                    "-fx-text-fill: #00d4ff; -fx-font-size: 14px; -fx-font-weight: bold;" +
                    "-fx-background-radius: 50%; -fx-border-color: rgba(0,212,255,0.3);" +
                    "-fx-border-radius: 50%; -fx-border-width: 1.5;");

                Label nameLabel = new Label(username);
                nameLabel.setStyle("-fx-text-fill: #d0d8e8; -fx-font-size: 13px; -fx-font-weight: bold;");

                Label date = new Label(c.getCreates_at() != null
                    ? c.getCreates_at().toLocalDateTime()
                           .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")) : "");
                date.setStyle("-fx-text-fill: #2e3850; -fx-font-size: 10px;");

                VBox nameCol = new VBox(2, nameLabel, date);

                Button flagBtn = new Button("⚑");
                flagBtn.setFocusTraversable(false);
                final String FLAG_BASE   = "-fx-background-color: transparent; -fx-text-fill: #2a3045; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 0;";
                final String FLAG_HOVER  = "-fx-background-color: transparent; -fx-text-fill: #ff4444; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 0;";
                final String FLAG_ACTIVE = "-fx-background-color: transparent; -fx-text-fill: #ff4444; -fx-font-size: 12px; -fx-padding: 0;";

                if (c.isFlagged()) {
                    flagBtn.setStyle(FLAG_ACTIVE); flagBtn.setDisable(true);
                    flagBtn.setTooltip(new Tooltip("Reported"));
                } else {
                    flagBtn.setStyle(FLAG_BASE);
                    flagBtn.setOnMouseEntered(e -> flagBtn.setStyle(FLAG_HOVER));
                    flagBtn.setOnMouseExited (e -> flagBtn.setStyle(FLAG_BASE));
                    flagBtn.setOnAction(e -> {
                        if (commentService.flagComment(c.getComment_id())) {
                            flagBtn.setStyle(FLAG_ACTIVE); flagBtn.setDisable(true);
                            flagBtn.setTooltip(new Tooltip("Reported — pending admin review"));
                            c.setFlagged(true);
                        }
                    });
                }
                if (c.getUserID() == Session.getUserId()) { flagBtn.setVisible(false); flagBtn.setManaged(false); }

                Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
                HBox userRow = new HBox(10, avatar, nameCol, spacer, flagBtn);
                userRow.setAlignment(Pos.CENTER_LEFT);

                Region divider = new Region();
                divider.setPrefHeight(1);
                divider.setStyle("-fx-background-color: rgba(0,212,255,0.06);");

                Label body = new Label(c.getContent());
                body.setWrapText(true); body.setMaxWidth(262);
                body.setStyle("-fx-text-fill: #8a96b0; -fx-font-size: 13px; -fx-line-spacing: 5;");

                slot.getChildren().addAll(userRow, divider, body);
            } else {
                slot.setVisible(false); slot.setManaged(false);
            }
        }
    }

    private void loadComments(List<Comment> comments) {
        if (commentsContainer == null) return;
        commentsContainer.getChildren().removeIf(n -> n instanceof Label);
        currentComments    = comments != null ? comments : new ArrayList<>();
        commentScrollIndex = 0;
        if (arrowLeft == null) initCommentSlots();

        if (currentComments.isEmpty()) {
            for (VBox slot : commentSlots)
                if (slot != null) { slot.setVisible(false); slot.setManaged(false); }
            arrowLeft.setVisible(false); arrowRight.setVisible(false);
            Label empty = new Label("No reviews yet — be the first to watch and rate!");
            empty.setStyle("-fx-text-fill:#2e3850; -fx-font-size:13px; -fx-font-style:italic;");
            commentsContainer.getChildren().add(1, empty);
        } else {
            arrowLeft.setVisible(true); arrowRight.setVisible(true);
            updateVisibleComments();
        }
    }

    private Button buildArrowButton(String symbol, boolean enabled) {
        Button btn = new Button(symbol);
        final String BASE  = "-fx-background-color:rgba(0,212,255,0.08); -fx-text-fill:#00d4ff; -fx-font-size:16px; -fx-background-radius:50%; -fx-min-width:36px; -fx-min-height:36px; -fx-cursor:hand; -fx-border-color:rgba(0,212,255,0.25); -fx-border-radius:50%;";
        final String HOVER = "-fx-background-color:rgba(0,212,255,0.22); -fx-text-fill:white;   -fx-font-size:16px; -fx-background-radius:50%; -fx-min-width:36px; -fx-min-height:36px; -fx-cursor:hand; -fx-border-color:#00d4ff;             -fx-border-radius:50%;";
        btn.setStyle(BASE);
        btn.setDisable(!enabled); btn.setOpacity(enabled ? 1.0 : 0.25);
        btn.setOnMouseEntered(e -> { if (!btn.isDisabled()) btn.setStyle(HOVER); });
        btn.setOnMouseExited (e -> btn.setStyle(BASE));
        return btn;
    }

    // =========================================================================
    //  CAST
    // =========================================================================
    private void loadCast() {
        if (castBox == null) return;
        castBox.getChildren().clear();
        List<Actor> actors = resolvedFilmId != null
            ? actorService.getActorsByFilm(resolvedFilmId)
            : resolvedSerieId != null
                ? actorService.getActorsBySerie(resolvedSerieId)
                : new ArrayList<>();

        if (actors.isEmpty()) {
            Label none = new Label("No cast info available");
            none.setStyle("-fx-text-fill:#3e4560; -fx-font-size:13px; -fx-font-style:italic;");
            castBox.getChildren().add(none); return;
        }
        for (Actor actor : actors) castBox.getChildren().add(createActorCard(actor));
    }

    private VBox createActorCard(Actor actor) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(90);

        Node avatar;

        String photoUrl = actor.getPhotoUrl();

        if (photoUrl != null && !photoUrl.isBlank()) {

            ImageView iv = new ImageView(ImageUtil.load(photoUrl));
            iv.setFitWidth(60);
            iv.setFitHeight(60);
            iv.setPreserveRatio(false);

            iv.setClip(new Circle(30, 30, 30));

            Circle ring = new Circle(30);
            ring.setFill(Color.TRANSPARENT);
            ring.setStroke(Color.web("#00d4ff", 0.3));
            ring.setStrokeWidth(1.5);

            StackPane photoPane = new StackPane(iv, ring);
            photoPane.setMinSize(60, 60);
            photoPane.setMaxSize(60, 60);

            avatar = photoPane;

        } else {
            avatar = buildInitialsAvatar(actor.getName());
        }

        Label nameLabel = new Label(actor.getName());
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(88);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setStyle("-fx-text-fill: #7a84a0; -fx-font-size: 11px; -fx-font-weight: bold;");

        if (actor.getRoleName() != null && !actor.getRoleName().isBlank()) {
            Label roleLabel = new Label(actor.getRoleName());
            roleLabel.setWrapText(true);
            roleLabel.setMaxWidth(88);
            roleLabel.setAlignment(Pos.CENTER);
            roleLabel.setStyle("-fx-text-fill:#2e4060; -fx-font-size:10px;");

            card.getChildren().addAll(avatar, nameLabel, roleLabel);
        } else {
            card.getChildren().addAll(avatar, nameLabel);
        }

        addHoverEffect(card);
        return card;
    }
    private Label buildInitialsAvatar(String name) {
        String initials = name.contains(" ")
            ? "" + name.charAt(0) + name.charAt(name.indexOf(' ') + 1)
            : name.substring(0, Math.min(2, name.length()));
        Label avatar = new Label(initials.toUpperCase());
        avatar.setMinSize(60, 60); avatar.setMaxSize(60, 60);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(
            "-fx-background-color: rgba(0,212,255,0.15); -fx-text-fill: #00d4ff;" +
            "-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 50%;" +
            "-fx-border-color: rgba(0,212,255,0.3); -fx-border-radius: 50%;");
        avatar.setEffect(new DropShadow(16, Color.web("#00d4ff", 0.22)));
        return avatar;
    }

    // =========================================================================
    //  TRAILER POPUP
    // =========================================================================
    private void showTrailerPopup(String url) {
        if (url == null || url.isBlank()) return;
        String videoPath;
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file:"))
            videoPath = url;
        else {
            URL resource = getClass().getResource(url.startsWith("/") ? url : "/" + url);
            videoPath = resource != null ? resource.toExternalForm() : new java.io.File(url).toURI().toString();
        }

        String html =
            "<!DOCTYPE html><html><head><style>" +
            "* { margin:0; padding:0; box-sizing:border-box; }" +
            "body { background:#000; overflow:hidden; display:flex; flex-direction:column; width:100vw; height:100vh; font-family:'Segoe UI',sans-serif; }" +
            "video { flex:1; width:100%; min-height:0; object-fit:contain; display:block; cursor:pointer; }" +
            "#bar { background: linear-gradient(to top, rgba(0,0,0,0.98), rgba(0,20,40,0.85)); padding: 6px 18px 10px; display:flex; flex-direction:column; gap:7px; }" +
            "#prog-wrap { position:relative; height:4px; background:rgba(255,255,255,0.08); border-radius:4px; cursor:pointer; transition:height 0.15s; }" +
            "#prog-wrap:hover { height:6px; }" +
            "#prog-buf { position:absolute; height:100%; border-radius:4px; background:rgba(0,140,255,0.2); width:0%; pointer-events:none; }" +
            "#prog-fill { position:absolute; height:100%; border-radius:4px; background:linear-gradient(to right,#002b55,#00aaff); width:0%; pointer-events:none; }" +
            "#prog-thumb { position:absolute; top:50%; width:13px; height:13px; background:#00aaff; border-radius:50%; transform:translate(-50%,-50%); box-shadow:0 0 10px rgba(0,170,255,0.9); left:0%; opacity:0; transition:opacity 0.15s; pointer-events:none; }" +
            "#prog-wrap:hover #prog-thumb { opacity:1; }" +
            "#row { display:flex; align-items:center; gap:10px; }" +
            ".btn { background:rgba(0,0,0,0.6); border:none; cursor:pointer; color:#00aaff; font-size:13px; border-radius:7px; padding:4px 9px; transition:0.2s; display:flex; align-items:center; justify-content:center; min-width:32px; height:30px; }" +
            ".btn:hover { background:rgba(0,140,255,0.25); color:#fff; } .btn:active { background:rgba(0,100,180,0.4); }" +
            "#time { font-size:11px; color:rgba(180,200,255,0.75); min-width:105px; }" +
            "#vol-wrap { display:flex; align-items:center; gap:6px; }" +
            "#vol { -webkit-appearance:none; width:72px; height:3px; background:rgba(255,255,255,0.15); border-radius:3px; outline:none; cursor:pointer; }" +
            "#vol::-webkit-slider-thumb { -webkit-appearance:none; width:12px; height:12px; background:#00aaff; border-radius:50%; }" +
            "#spacer { flex:1; }" +
            "#speed { background:rgba(0,0,0,0.7); border:none; color:#00aaff; font-size:11px; padding:4px 7px; border-radius:6px; cursor:pointer; outline:none; }" +
            "#tip { position:fixed; bottom:72px; left:50%; transform:translateX(-50%); background:rgba(0,0,0,0.85); color:#00aaff; font-size:11px; padding:4px 14px; border-radius:20px; opacity:0; transition:opacity 0.25s; pointer-events:none; }" +
            "::-webkit-scrollbar { display:none; }" +
            "</style></head><body>" +
            "<video id='v'><source src='" + videoPath + "' type='video/mp4'></video>" +
            "<div id='bar'>" +
            "<div id='prog-wrap' onmousedown='seekStart(event)' onmousemove='seekHover(event)'><div id='prog-buf'></div><div id='prog-fill'></div><div id='prog-thumb'></div></div>" +
            "<div id='row'>" +
            "<button class='btn' onclick='togglePlay()' id='playBtn'>&#9654;</button>" +
            "<button class='btn' onclick='skip(-10)'>&#9198; 10</button>" +
            "<button class='btn' onclick='skip(10)'>10 &#9197;</button>" +
            "<div id='vol-wrap'><button class='btn' onclick='toggleMute()' id='muteBtn'>&#128266;</button><input id='vol' type='range' min='0' max='1' step='0.02' value='1' oninput='setVol(this.value)'></div>" +
            "<span id='time'>0:00 / 0:00</span><div id='spacer'></div>" +
            "<select id='speed' onchange='setSpeed(this.value)'><option value='0.5'>0.5×</option><option value='0.75'>0.75×</option><option value='1' selected>1×</option><option value='1.25'>1.25×</option><option value='1.5'>1.5×</option><option value='2'>2×</option></select>" +
            "</div></div><div id='tip'></div>" +
            "<script>" +
            "var v=document.getElementById('v'),pFill=document.getElementById('prog-fill'),pBuf=document.getElementById('prog-buf'),pThumb=document.getElementById('prog-thumb'),playBtn=document.getElementById('playBtn'),timeEl=document.getElementById('time'),tip=document.getElementById('tip'),seeking=false;" +
            "v.addEventListener('timeupdate',function(){if(!v.duration||seeking)return;var p=(v.currentTime/v.duration*100).toFixed(2)+'%';pFill.style.width=p;pThumb.style.left=p;timeEl.textContent=fmt(v.currentTime)+' / '+fmt(v.duration);});" +
            "v.addEventListener('progress',function(){if(!v.duration||!v.buffered.length)return;pBuf.style.width=(v.buffered.end(v.buffered.length-1)/v.duration*100)+'%';});" +
            "v.addEventListener('play',function(){playBtn.textContent='⏸';});v.addEventListener('pause',function(){playBtn.textContent='▶';});v.addEventListener('ended',function(){playBtn.textContent='↺';});v.addEventListener('click',togglePlay);" +
            "function togglePlay(){if(v.ended){v.currentTime=0;v.play();}else if(v.paused)v.play();else v.pause();}" +
            "function skip(s){v.currentTime=Math.max(0,Math.min(v.duration||0,v.currentTime+s));}" +
            "function seekAt(e){var r=document.getElementById('prog-wrap').getBoundingClientRect();v.currentTime=Math.max(0,Math.min(1,(e.clientX-r.left)/r.width))*(v.duration||0);}" +
            "function seekStart(e){seeking=true;seekAt(e);document.addEventListener('mousemove',seekAt);document.addEventListener('mouseup',function up(){seeking=false;document.removeEventListener('mousemove',seekAt);document.removeEventListener('mouseup',up);});}" +
            "function seekHover(e){var r=document.getElementById('prog-wrap').getBoundingClientRect();tip.textContent=fmt(Math.max(0,(e.clientX-r.left)/r.width)*v.duration);tip.style.opacity='1';tip.style.left=e.clientX+'px';}" +
            "document.getElementById('prog-wrap').addEventListener('mouseleave',function(){tip.style.opacity='0';});" +
            "function setVol(val){v.volume=parseFloat(val);v.muted=val==0;}function toggleMute(){v.muted=!v.muted;document.getElementById('vol').value=v.muted?0:v.volume;}function setSpeed(val){v.playbackRate=parseFloat(val);}" +
            "function fmt(s){var h=Math.floor(s/3600),m=Math.floor((s%3600)/60),ss=Math.floor(s%60);return (h>0?h+':':'')+(h>0&&m<10?'0':'')+m+':'+(ss<10?'0':'')+ss;}" +
            "document.addEventListener('keydown',function(e){if(e.code==='Space'){e.preventDefault();togglePlay();}if(e.code==='ArrowRight')skip(5);if(e.code==='ArrowLeft')skip(-5);});" +
            "v.play().catch(function(){});" +
            "</script></body></html>";

        javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
        webView.setPrefSize(1500, 700);
        webView.getEngine().loadContent(html);

        javafx.geometry.Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
        double fullW = screen.getWidth(), fullH = screen.getHeight();
        double smallW = 1200, smallH = 660;

        Stage popup = new Stage();
        popup.initOwner(mainContainer.getScene().getWindow());
        popup.initModality(Modality.WINDOW_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.setWidth(fullW); popup.setHeight(fullH); popup.setX(0); popup.setY(0);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.88);");

        VBox card = new VBox(0);
        card.setMaxSize(fullW - 40, fullH - 40); card.setPrefSize(fullW - 40, fullH - 40);
        card.setStyle(
            "-fx-background-color: #07090f; -fx-background-radius: 14;" +
            "-fx-border-color: rgba(0,212,255,0.18); -fx-border-width: 1.5; -fx-border-radius: 14;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.95),60,0.7,0,10);");

        Rectangle cardClip = new Rectangle();
        cardClip.setArcWidth(28); cardClip.setArcHeight(28);
        card.layoutBoundsProperty().addListener((o, ov, nv) -> { cardClip.setWidth(nv.getWidth()); cardClip.setHeight(nv.getHeight()); });
        card.setClip(cardClip);

        HBox cardBar = new HBox(8);
        cardBar.setAlignment(Pos.CENTER_RIGHT); cardBar.setPadding(new Insets(9, 12, 9, 16));
        cardBar.setMinHeight(42); cardBar.setMaxHeight(42);
        cardBar.setStyle("-fx-background-color: #0a0e1a; -fx-border-color: transparent transparent rgba(0,212,255,0.1) transparent; -fx-border-width: 0 0 1 0;");

        Region d1 = buildWindowDot("#1e3a5f"), d2 = buildWindowDot("#2c5282"), d3 = buildWindowDot("#4a90d9");
        Region barSpacer = new Region(); HBox.setHgrow(barSpacer, Priority.ALWAYS);

        Button btnResize = buildPopupCtrlBtn("⊡", false);
        Button btnClose  = buildPopupCtrlBtn("✕", true);
        btnClose.setOnAction(e -> { webView.getEngine().load(null); popup.close(); resetTrailerTabs(); });

        final boolean[] isSmall = {false};
        btnResize.setOnAction(e -> {
            if (!isSmall[0]) { card.setMaxSize(smallW, smallH); card.setPrefSize(smallW, smallH); isSmall[0] = true; btnResize.setText("⊞"); }
            else             { card.setMaxSize(fullW-40, fullH-40); card.setPrefSize(fullW-40, fullH-40); isSmall[0] = false; btnResize.setText("⊡"); }
        });

        cardBar.getChildren().addAll(d1, d2, d3, barSpacer, btnResize, btnClose);
        VBox.setVgrow(webView, Priority.ALWAYS);
        card.getChildren().addAll(cardBar, webView);

        root.setOnMouseClicked(e -> { if (e.getTarget() == root) { webView.getEngine().load(null); popup.close(); resetTrailerTabs(); } });
        root.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);

        popup.setOnHidden(e -> { webView.getEngine().load(null); resetTrailerTabs(); });
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.setAlwaysOnTop(true);
        popup.show();
    }

    private void resetTrailerTabs() {
        if (lineOverview != null) lineOverview.setVisible(true);
        if (lineTrailers != null) lineTrailers.setVisible(false);
        final String ACTIVE   = "-fx-background-color:transparent; -fx-text-fill:white;   -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand; -fx-padding:4 0;";
        final String INACTIVE = "-fx-background-color:transparent; -fx-text-fill:#4e5670; -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand; -fx-padding:4 0;";
        if (tabOverview != null) tabOverview.setStyle(ACTIVE);
        if (tabTrailers != null) tabTrailers.setStyle(INACTIVE);
    }

    private Button buildPopupCtrlBtn(String symbol, boolean isClose) {
        Button btn = new Button(symbol);
        btn.setPrefSize(30, 30); btn.setMinSize(30, 30); btn.setMaxSize(30, 30);
        String base    = "-fx-background-radius:7;-fx-border-radius:7;-fx-border-width:1;-fx-font-size:12;-fx-padding:0;";
        String normal  = base + "-fx-background-color:#0d1829;-fx-border-color:#1e3a5f;-fx-text-fill:#4a90d9;";
        String hover   = base + (isClose ? "-fx-background-color:#c0392b;-fx-border-color:#e74c3c;-fx-text-fill:white;" : "-fx-background-color:#162238;-fx-border-color:#2c5282;-fx-text-fill:#7eb8f7;");
        String pressed = base + (isClose ? "-fx-background-color:#922b21;-fx-border-color:#c0392b;-fx-text-fill:white;" : "-fx-background-color:#0a1520;-fx-border-color:#1e3a5f;-fx-text-fill:#4a90d9;");
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));  btn.setOnMouseExited(e  -> btn.setStyle(normal));
        btn.setOnMousePressed(e -> btn.setStyle(pressed)); btn.setOnMouseReleased(e -> btn.setStyle(hover));
        return btn;
    }

    private Region buildWindowDot(String color) {
        Region d = new Region();
        d.setPrefSize(9, 9); d.setMinSize(9, 9); d.setMaxSize(9, 9);
        d.setStyle("-fx-background-color:" + color + ";-fx-background-radius:5;");
        HBox.setMargin(d, new Insets(0, 1, 0, 0));
        return d;
    }

    // =========================================================================
    //  ANIMATION HELPERS
    // =========================================================================
    private void addHoverEffect(Node node) {
        ScaleTransition in  = new ScaleTransition(Duration.millis(180), node); in.setToX(1.05);  in.setToY(1.05);
        ScaleTransition out = new ScaleTransition(Duration.millis(180), node); out.setToX(1.0); out.setToY(1.0);
        DropShadow glow = new DropShadow(22, Color.web("#2d54ff", 0.55));
        node.setOnMouseEntered(e -> { in.play();  node.setEffect(glow); });
        node.setOnMouseExited (e -> { out.play(); node.setEffect(null); });
    }

    private void addButtonInteractions(Button btn) {
        ScaleTransition up   = new ScaleTransition(Duration.millis(90), btn); up.setToX(1.1);  up.setToY(1.1);
        ScaleTransition down = new ScaleTransition(Duration.millis(90), btn); down.setToX(1.0); down.setToY(1.0);
        DropShadow glow = new DropShadow(18, Color.web("#00d4ff", 0.75)); glow.setSpread(0.1);
        btn.setOnMouseEntered(e  -> { up.play();           btn.setEffect(glow); });
        btn.setOnMouseExited (e  -> { down.play();         btn.setEffect(null); });
        btn.setOnMousePressed (e -> { btn.setScaleX(0.95); btn.setScaleY(0.95); });
        btn.setOnMouseReleased(e -> { btn.setScaleX(1.1);  btn.setScaleY(1.1);  });
    }

    private void pumpButton(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(140), btn);
        st.setFromX(1.0); st.setFromY(1.0); st.setToX(1.18); st.setToY(1.18);
        st.setAutoReverse(true); st.setCycleCount(2); st.play();
    }

    // =========================================================================
    //  WATCH NEXT SECTION  (final fix — see previous submission for comments)
    // =========================================================================
    private void populateWatchNext() {
        if (watchNextContainer == null) return;
        watchNextContainer.getChildren().clear();

        VBox section = new VBox(24);
        section.setStyle(
            "-fx-padding: 40 64 56 64;" +
            "-fx-background-color: rgba(0,212,255,0.015);" +
            "-fx-border-color: rgba(0,212,255,0.07) transparent transparent transparent;" +
            "-fx-border-width: 1 0 0 0;");

        Rectangle accent = new Rectangle(3, 24);
        accent.setFill(Color.web("#00d4ff")); accent.setArcWidth(3); accent.setArcHeight(3);

        VBox titleCol = new VBox(3);
        titleCol.setStyle("-fx-padding: 0 0 0 14;");
        String sectionTitle = resolvedFilmId != null ? "More like this" : "More episodes";
        String sectionSub   = resolvedFilmId != null ? "Based on similar categories"
            : "Continue watching · " + (currentSerie != null ? currentSerie.getTitle() : "");
        Label titleLbl = new Label(sectionTitle);
        titleLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label subLbl = new Label(sectionSub);
        subLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #3e4560;");
        titleCol.getChildren().addAll(titleLbl, subLbl);

        HBox header = new HBox(0, accent, titleCol);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox cardsRow = new HBox(16);
        cardsRow.setAlignment(Pos.TOP_LEFT);
        cardsRow.setStyle("-fx-padding: 12 8 16 8;");
        cardsRow.setPickOnBounds(false);

        ScrollPane scrollPane = new ScrollPane(cardsRow);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(false);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        Button scrollLeft  = buildCarouselNavBtn(false);
        Button scrollRight = buildCarouselNavBtn(true);
        scrollLeft.setDisable(true);

        scrollRight.setOnAction(e -> {
            double viewport = scrollPane.getViewportBounds().getWidth();
            double content  = cardsRow.getBoundsInLocal().getWidth();
            if (content <= viewport) return;
            double page = viewport / content;
            double target = Math.min(1.0, scrollPane.getHvalue() + page);
            if (target > 0.98) target = 1.0;
            animateScroll(scrollPane, target);
        });
        scrollLeft.setOnAction(e -> {
            double viewport = scrollPane.getViewportBounds().getWidth();
            double content  = cardsRow.getBoundsInLocal().getWidth();
            if (content <= viewport) return;
            double page = viewport / content;
            double target = Math.max(0.0, scrollPane.getHvalue() - page);
            if (target < 0.02) target = 0.0;
            animateScroll(scrollPane, target);
        });
        scrollPane.hvalueProperty().addListener((obs, o, n) -> {
            double v = n.doubleValue();
            scrollLeft.setDisable(v <= 0.01);
            scrollRight.setDisable(v >= 0.99);
        });

        HBox navRow = new HBox(10, scrollLeft, scrollRight);
        navRow.setAlignment(Pos.CENTER_RIGHT);

        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header, Priority.ALWAYS);
        headerRow.getChildren().addAll(header, navRow);

        if (resolvedFilmId != null) buildSimilarFilmCards(cardsRow, scrollRight);
        else                        buildEpisodeCards(cardsRow, scrollRight);

        Platform.runLater(scrollPane::layout);
        section.getChildren().addAll(headerRow, scrollPane);
        watchNextContainer.getChildren().add(section);
    }

    private Button buildCarouselNavBtn(boolean isRight) {
        String chevron = isRight ? "›" : "‹";
        Label icon = new Label(chevron);
        icon.setMouseTransparent(true);
        icon.setStyle("-fx-font-size: 24px; -fx-font-weight: 300; -fx-text-fill: #00d4ff; -fx-padding: 0 0 1 0;");
        StackPane pill = new StackPane(icon);
        pill.setPrefSize(44, 44); pill.setMaxSize(44, 44); pill.setMouseTransparent(true);

        Button btn = new Button(); btn.setGraphic(pill);
        btn.setPrefSize(44, 44); btn.setMinSize(44, 44); btn.setMaxSize(44, 44);
        btn.setFocusTraversable(false);

        final String BASE     = "-fx-background-color: rgba(8,16,38,0.75); -fx-background-radius: 22; -fx-border-color: rgba(0,212,255,0.25); -fx-border-radius: 22; -fx-border-width: 1; -fx-cursor: hand; -fx-padding: 0;";
        final String HOVER    = "-fx-background-color: rgba(0,212,255,0.16); -fx-background-radius: 22; -fx-border-color: rgba(0,212,255,0.8); -fx-border-radius: 22; -fx-border-width: 1.5; -fx-cursor: hand; -fx-padding: 0; -fx-effect: dropshadow(gaussian, rgba(0,212,255,0.4), 16, 0.2, 0, 0);";
        final String PRESSED  = "-fx-background-color: rgba(0,212,255,0.28); -fx-background-radius: 22; -fx-border-color: #00d4ff; -fx-border-radius: 22; -fx-border-width: 1.5; -fx-cursor: hand; -fx-padding: 0;";
        final String DISABLED = "-fx-background-color: rgba(8,16,38,0.4); -fx-background-radius: 22; -fx-border-color: rgba(255,255,255,0.06); -fx-border-radius: 22; -fx-border-width: 1; -fx-padding: 0;";
        final String ICON_ON  = "-fx-font-size:24px;-fx-font-weight:300;-fx-text-fill:#00d4ff;-fx-padding:0 0 1 0;";
        final String ICON_HI  = "-fx-font-size:24px;-fx-font-weight:300;-fx-text-fill:white;-fx-padding:0 0 1 0;";
        final String ICON_OFF = "-fx-font-size:24px;-fx-font-weight:300;-fx-text-fill:rgba(0,212,255,0.2);-fx-padding:0 0 1 0;";

        btn.setStyle(BASE);
        btn.setOnMouseEntered(e -> { if (!btn.isDisabled()) { btn.setStyle(HOVER);    icon.setStyle(ICON_HI);  } });
        btn.setOnMouseExited (e -> { if (!btn.isDisabled()) { btn.setStyle(BASE);     icon.setStyle(ICON_ON);  } else { btn.setStyle(DISABLED); icon.setStyle(ICON_OFF); } });
        btn.setOnMousePressed(e -> { if (!btn.isDisabled())   btn.setStyle(PRESSED); });
        btn.setOnMouseReleased(e -> { if (!btn.isDisabled())  btn.setStyle(HOVER); });
        btn.disabledProperty().addListener((obs, was, now) -> { btn.setStyle(now ? DISABLED : BASE); icon.setStyle(now ? ICON_OFF : ICON_ON); });
        return btn;
    }

    private void animateScroll(ScrollPane sp, double target) {
        new Timeline(new KeyFrame(Duration.millis(380),
            new KeyValue(sp.hvalueProperty(), target, Interpolator.EASE_BOTH))).play();
    }

    private void buildSimilarFilmCards(HBox cardsRow, Button scrollRight) {
        if (cachedSimilarFilms == null || !Integer.valueOf(resolvedFilmId).equals(cachedSimilarFilmId)) {
            List<String> cats = currentItem != null ? currentItem.getCategoryNames() : new ArrayList<>();
            cachedSimilarFilms  = featuredService.getSimilarFilms(resolvedFilmId, cats, 14);
            cachedSimilarFilmId = resolvedFilmId;
        }
        if (cachedSimilarFilms.isEmpty()) {
            cardsRow.getChildren().add(buildEmptyCard("No similar films found", "Try exploring other categories"));
            scrollRight.setDisable(true); return;
        }
        scrollRight.setDisable(false);
        for (FeaturedItem item : cachedSimilarFilms) {
            cardsRow.getChildren().add(createWatchNextCard(
                item.getPosterUrl(), item.getTitle(), item.getCategoriesAsString(),
                item.getReleaseYear() > 0 ? String.valueOf(item.getReleaseYear()) : "",
                item.getRating(), "film", () -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/LecturePage.fxml"));
                        Parent root = loader.load();
                        LecturePageController ctrl = loader.getController();
                        ((Stage) watchNextContainer.getScene().getWindow()).getScene().setRoot(root);
                        ctrl.initFilm(item.getId());
                    } catch (Exception ex) { ex.printStackTrace(); }
                }));
        }
    }

    private void buildEpisodeCards(HBox cardsRow, Button scrollRight) {
        if (currentSerie == null) return;

        Map<Integer, List<Episode>> bySeason = new LinkedHashMap<>();

        for (Season s : currentSerie.getSeasons()) {

            List<Episode> eps = s.getEpisodes().stream()
                .filter(ep -> currentEpisode == null || ep.getEpId() != currentEpisode.getEpId())
                .collect(Collectors.toList());

            if (!eps.isEmpty()) {
                bySeason.put(s.getSeasonNum(), eps);
            }
        }

        if (bySeason.isEmpty()) {
            cardsRow.getChildren().add(
                buildEmptyCard("No other episodes", "You're up to date!")
            );
            scrollRight.setDisable(true);
            return;
        }

        scrollRight.setDisable(false);

        for (Map.Entry<Integer, List<Episode>> entry : bySeason.entrySet()) {

            int seasonNum = entry.getKey();

            VBox seasonDivider = new VBox();
            seasonDivider.setAlignment(Pos.BOTTOM_LEFT);
            seasonDivider.setPrefSize(80, 240);
            seasonDivider.setMaxSize(80, 240);
            seasonDivider.setStyle("-fx-padding: 0 0 8 0;");

            Label seasonLabel = new Label("Season\n" + seasonNum);
            seasonLabel.setStyle(
                "-fx-text-fill: rgba(0,212,255,0.5); " +
                "-fx-font-size: 11px; -fx-font-weight: bold;" +
                "-fx-text-alignment: center;"
            );
            seasonLabel.setAlignment(Pos.CENTER);
            seasonLabel.setWrapText(true);

            Rectangle seasonLine = new Rectangle(2, 60);
            seasonLine.setFill(Color.web("#00d4ff", 0.2));
            seasonLine.setArcWidth(2);
            seasonLine.setArcHeight(2);

            seasonDivider.getChildren().addAll(seasonLine, seasonLabel);
            cardsRow.getChildren().add(seasonDivider);

            String posterUrl = findSeasonPoster(currentSerie, seasonNum);

            for (Episode ep : entry.getValue()) {

                final int finalSeasonNum = seasonNum;

                cardsRow.getChildren().add(
                    createWatchNextCard(
                        posterUrl,   // OK ONLY if createWatchNextCard uses ImageUtil
                        ep.getTitle(),
                        "S" + seasonNum + "  ·  E" + ep.getNumEpisode(),
                        ep.getDuration() + " min",
                        0,
                        "episode",
                        () -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                    getClass().getResource("/view/fxml/LecturePage.fxml")
                                );

                                Parent root = loader.load();
                                LecturePageController ctrl = loader.getController();

                                ((Stage) watchNextContainer.getScene().getWindow())
                                    .getScene()
                                    .setRoot(root);

                                ctrl.initEpisode(
                                    currentSerie.getSerieId(),
                                    finalSeasonNum,
                                    ep.getEpId()
                                );

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    )
                );
            }
            }
    }

    private VBox buildEmptyCard(String line1, String line2) {
        VBox card = new VBox(8); card.setAlignment(Pos.CENTER); card.setPrefSize(220, 140);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.02); -fx-background-radius: 16; -fx-border-color: rgba(0,212,255,0.06); -fx-border-radius: 16; -fx-border-width: 1;");
        Label l1 = new Label(line1); l1.setStyle("-fx-text-fill: #3e4560; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label l2 = new Label(line2); l2.setStyle("-fx-text-fill: #2a3045; -fx-font-size: 11px; -fx-font-style: italic;");
        card.getChildren().addAll(l1, l2); return card;
    }

    private VBox createWatchNextCard(String posterUrl, String title, String subtitle,
                                     String meta, int rating, String type, Runnable onClick) {
        VBox card = new VBox();
        card.setPrefWidth(180); card.setMaxWidth(180); card.setMinHeight(250);
        card.setAlignment(Pos.TOP_CENTER);

        final String BASE_STYLE  = "-fx-background-color: rgba(20,25,45,0.55); -fx-background-radius: 20; -fx-border-color: rgba(255,255,255,0.05); -fx-border-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 18, 0.2, 0, 6); -fx-cursor: hand;";
        final String HOVER_STYLE = "-fx-background-color: rgba(0,212,255,0.12); -fx-background-radius: 20; -fx-border-color: rgba(0,212,255,0.6); -fx-border-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,212,255,0.4), 35, 0.3, 0, 10); -fx-cursor: hand;";
        card.setStyle(BASE_STYLE);

        StackPane imagePane = new StackPane();
        imagePane.setPrefSize(180, 160); imagePane.setMaxSize(180, 160);
        Region placeholder = new Region(); placeholder.setPrefSize(180, 160);
        placeholder.setStyle("-fx-background-color: linear-gradient(to bottom, #0d1526, #070d18); -fx-background-radius: 20 20 0 0;");
        imagePane.getChildren().add(placeholder);
        if (posterUrl != null && !posterUrl.isBlank()) {
            ImageView iv = new ImageView(ImageUtil.load(posterUrl));

            iv.setFitWidth(180);
            iv.setFitHeight(160);
            iv.setPreserveRatio(false);

            Rectangle clip = new Rectangle(180, 160);
            clip.setArcWidth(20);
            clip.setArcHeight(20);

            iv.setClip(clip);

            imagePane.getChildren().add(iv);
        }

        Region gradient = new Region(); gradient.setPrefSize(180, 160); gradient.setMouseTransparent(true);
        gradient.setStyle("-fx-background-color: linear-gradient(to bottom, transparent 30%, rgba(0,0,0,0.9) 100%); -fx-background-radius: 20 20 0 0;");
        imagePane.getChildren().add(gradient);

        StackPane playWrapper = new StackPane();
        playWrapper.setPrefSize(48, 48); playWrapper.setMaxSize(48, 48); playWrapper.setMouseTransparent(true);
        playWrapper.setStyle("-fx-background-color: rgba(0,0,0,0.62); -fx-background-radius: 100; -fx-border-color: rgba(255,255,255,0.25); -fx-border-radius: 100;");
        Label playIcon = new Label("▶"); playIcon.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-padding: 0 0 0 2;");
        playWrapper.getChildren().add(playIcon); playWrapper.setOpacity(0);
        StackPane.setAlignment(playWrapper, Pos.CENTER); imagePane.getChildren().add(playWrapper);

        Label typeBadge = new Label(type.equals("episode") ? "EP" : "FILM"); typeBadge.setMouseTransparent(true);
        typeBadge.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-text-fill: #00d4ff; -fx-font-size: 9px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 3 8;");
        StackPane.setAlignment(typeBadge, Pos.TOP_LEFT); StackPane.setMargin(typeBadge, new Insets(10, 0, 0, 10));
        imagePane.getChildren().add(typeBadge);

        if (rating > 0) {
            Label ratingBadge = new Label("★ " + rating); ratingBadge.setMouseTransparent(true);
            ratingBadge.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 3 8;");
            StackPane.setAlignment(ratingBadge, Pos.TOP_RIGHT); StackPane.setMargin(ratingBadge, new Insets(10, 10, 0, 0));
            imagePane.getChildren().add(ratingBadge);
        }

        VBox info = new VBox(6); info.setPadding(new Insets(12)); info.setAlignment(Pos.TOP_LEFT);
        Label titleLbl = new Label(title); titleLbl.setWrapText(true); titleLbl.setMaxWidth(160);
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label subLbl = new Label(subtitle); subLbl.setWrapText(true); subLbl.setMaxWidth(160);
        subLbl.setStyle("-fx-text-fill: #00d4ff; -fx-font-size: 10px; -fx-opacity: 0.85;");
        info.getChildren().addAll(titleLbl, subLbl);
        if (meta != null && !meta.isBlank()) {
            Label metaLbl = new Label(meta); metaLbl.setStyle("-fx-text-fill: #7a8199; -fx-font-size: 10px;");
            info.getChildren().add(metaLbl);
        }
        card.getChildren().addAll(imagePane, info);

        // Three-flag click guard (drag threshold + Platform.runLater)
        final double   DRAG_THRESHOLD = 6.0;
        final boolean[] hovering = {false};
        final boolean[] dragged  = {false};
        final double[]  pressXY  = {0.0, 0.0};

        card.setOnMouseEntered(e -> {
            hovering[0] = true; card.setStyle(HOVER_STYLE);
            FadeTransition ft = new FadeTransition(Duration.millis(140), playWrapper); ft.setToValue(1.0); ft.play();
            ScaleTransition sc = new ScaleTransition(Duration.millis(180), card); sc.setToX(1.05); sc.setToY(1.05);
            TranslateTransition mv = new TranslateTransition(Duration.millis(180), card); mv.setToY(-6);
            new ParallelTransition(sc, mv).play();
        });
        card.setOnMouseExited(e -> {
            hovering[0] = false; card.setStyle(BASE_STYLE);
            FadeTransition ft = new FadeTransition(Duration.millis(140), playWrapper); ft.setToValue(0.0); ft.play();
            ScaleTransition sc = new ScaleTransition(Duration.millis(180), card); sc.setToX(1.0); sc.setToY(1.0);
            TranslateTransition mv = new TranslateTransition(Duration.millis(180), card); mv.setToY(0);
            new ParallelTransition(sc, mv).play();
        });
        card.setOnMousePressed(e -> {
            pressXY[0] = e.getSceneX(); pressXY[1] = e.getSceneY(); dragged[0] = false;
            card.setScaleX(0.97); card.setScaleY(0.97);
        });
        card.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - pressXY[0], dy = e.getSceneY() - pressXY[1];
            if (Math.sqrt(dx*dx + dy*dy) > DRAG_THRESHOLD) dragged[0] = true;
        });
        card.setOnMouseReleased(e -> {
            card.setScaleX(hovering[0] ? 1.05 : 1.0);
            card.setScaleY(hovering[0] ? 1.05 : 1.0);
            if (hovering[0] && !dragged[0]) Platform.runLater(onClick);
        });
        return card;
    }
}