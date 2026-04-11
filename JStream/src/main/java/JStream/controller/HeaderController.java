package JStream.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.FeaturedItemProgress;
import JStream.entity.Film;
import JStream.entity.MyListManager;
import JStream.entity.NewEpisodeInfo;
import JStream.entity.Notification;
import JStream.entity.Season;
import JStream.entity.Serie;
import JStream.entity.Session;
import JStream.entity.UsernameChangeNotifier;
import JStream.entity.WatchStatus;
import JStream.service.EpisodeProgressService;
import JStream.service.FeaturedService;
import JStream.service.FilmProgressService;
import JStream.service.MylistService;
import JStream.service.NotificationService;
import JStream.service.UserService;
import JStream.utils.ImageUtil;
import JStream.utils.SecurityUtils;
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
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.stage.*;
import javafx.util.Duration;

public class HeaderController {

    // ── FXML FIELDS ──────────────────────────────────────────────────────────
    @FXML private HBox rootPane;
    @FXML private ImageView logoImage;

    // Nav buttons & underlines
    @FXML private Button btnHome, btnMovies, btnSeries, btnMyList, btnHistory;
    @FXML private Rectangle lineHome, lineMovies, lineSeries, lineMyList, lineHistory;
    @FXML private HBox buttonsBox;

    // Hamburger
    @FXML private Button btnHamburger;

    // Search
    @FXML private StackPane searchStack;
    @FXML private TextField searchInput;
    @FXML private Button clearBtn, btnResearch;

    // Bell
    @FXML private StackPane bellContainer;
    @FXML private ImageView bellIcon;
    @FXML private Circle notificationCircle;

    // Profile
    @FXML private Button profile;

    // ── PRIVATE STATE ────────────────────────────────────────────────────────
    private Button    activeButton;
    private Rectangle activeLine;
    private Timeline  autoSlide;
    private AudioClip bellSound;
    private boolean ignoreTextChange      = false;
    private boolean hamburgerMenuOpen     = false;
    private static final double COLLAPSE_WIDTH = 900;

    private Circle notificationDot;
    private Popup  profilePopup;
    private Popup  notificationPopup;
    private VBox   notificationListBox;
    private Label  notifBadgeLabel;
    private int    unreadCount = 0;

    private final Popup  suggestionsPopup   = new Popup();
    private final VBox   suggestionsContent = new VBox();

    // Track current page for restoration
    private static String lastActiveFxml = "/view/fxml/HomePage.fxml";

    // ── SERVICES ─────────────────────────────────────────────────────────────
    private final FeaturedService        featuredService        = new FeaturedService();
    private final EpisodeProgressService episodeProgressService = new EpisodeProgressService();
    private final FilmProgressService    filmProgressService    = new FilmProgressService(featuredService);
    private final UserService            userService            = new UserService();
    private final NotificationService    notificationService    = new NotificationService();

    // ─────────────────────────────────────────────────────────────────────────
    // INITIALIZE
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    private void initialize() {
        logoImage.setImage(new Image(getClass().getResourceAsStream("/assets/images/logo/Raksha.png")));

        loadUserProfile();
        setupProfilePopup();
        setupProfileButtonAction();
        setupBellNotification();
        setupSearchBar();
        setupSearchSuggestions();
        setupNavButtons();
        setupHamburgerMenu();
        setupResponsiveLayout();

        // Restore last active tab based on fxml path
        Platform.runLater(() -> restoreActiveTab(lastActiveFxml));

        // Load notifications after UI is ready
        Platform.runLater(this::loadNotifications);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RESPONSIVE LAYOUT
    // ─────────────────────────────────────────────────────────────────────────
    private void setupResponsiveLayout() {
        Platform.runLater(() -> {
            Scene scene = rootPane.getScene();
            if (scene != null) {
                scene.widthProperty().addListener((obs, oldVal, newVal) -> {
                    updateResponsiveLayout(newVal.doubleValue());
                });
                updateResponsiveLayout(scene.getWidth());
            } else {
                rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        newScene.widthProperty().addListener((o, ov, nv) -> updateResponsiveLayout(nv.doubleValue()));
                        updateResponsiveLayout(newScene.getWidth());
                    }
                });
            }
        });
    }

    private void updateResponsiveLayout(double width) {
        boolean collapsed = width < COLLAPSE_WIDTH;
        buttonsBox.setVisible(!collapsed);
        buttonsBox.setManaged(!collapsed);
        if (btnHamburger != null) {
            btnHamburger.setVisible(collapsed);
            btnHamburger.setManaged(collapsed);
            // ✅ Push hamburger to the far right
            if (collapsed) {
                HBox.setHgrow(btnHamburger, Priority.ALWAYS);
                btnHamburger.setMaxWidth(Double.MAX_VALUE);
                btnHamburger.setAlignment(Pos.CENTER_RIGHT);
                btnHamburger.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: white;" +
                    "-fx-font-size: 22; -fx-cursor: hand; -fx-padding: 4 10;" +
                    "-fx-alignment: CENTER_RIGHT;"
                );
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HAMBURGER MENU
    // ─────────────────────────────────────────────────────────────────────────
    private Popup hamburgerPopup;
    private void setupHamburgerMenu() {
        if (btnHamburger == null) return;
        btnHamburger.setText("☰");
        btnHamburger.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: white;" +
            "-fx-font-size: 22; -fx-cursor: hand; -fx-padding: 4 10;"
        );
        btnHamburger.setVisible(false);
        btnHamburger.setManaged(false);

        // ── Overlay (dark transparent background) ────────────────────────────
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.55);");
        overlay.setVisible(false);
        overlay.setPickOnBounds(true);

        // ── Drawer panel (20% width, slides from right) ───────────────────────
        VBox drawer = new VBox(0);
        drawer.setStyle(
            "-fx-background-color: #0d1117;" +
            "-fx-border-color: #21262d;" +
            "-fx-border-width: 0 0 0 1;"
        );
        drawer.setAlignment(Pos.TOP_LEFT);

        // Drawer header
        HBox drawerHeader = new HBox();
        drawerHeader.setPadding(new Insets(20, 16, 16, 20));
        drawerHeader.setAlignment(Pos.CENTER_LEFT);
        drawerHeader.setStyle("-fx-background-color: #161b22;");

        Label drawerTitle = new Label("Menu");
        drawerTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

        Region drawerSpacer = new Region();
        HBox.setHgrow(drawerSpacer, Priority.ALWAYS);

        Button closeDrawer = new Button("✕");
        closeDrawer.setStyle(
            "-fx-background-color: #21262d; -fx-text-fill: #8b949e; -fx-font-size: 12;" +
            "-fx-background-radius: 50%; -fx-min-width: 28; -fx-min-height: 28;" +
            "-fx-max-width: 28; -fx-max-height: 28; -fx-padding: 0; -fx-cursor: hand;"
        );
        closeDrawer.setOnMouseEntered(e -> closeDrawer.setStyle(
            "-fx-background-color: #30363d; -fx-text-fill: white; -fx-font-size: 12;" +
            "-fx-background-radius: 50%; -fx-min-width: 28; -fx-min-height: 28;" +
            "-fx-max-width: 28; -fx-max-height: 28; -fx-padding: 0; -fx-cursor: hand;"
        ));
        closeDrawer.setOnMouseExited(e -> closeDrawer.setStyle(
            "-fx-background-color: #21262d; -fx-text-fill: #8b949e; -fx-font-size: 12;" +
            "-fx-background-radius: 50%; -fx-min-width: 28; -fx-min-height: 28;" +
            "-fx-max-width: 28; -fx-max-height: 28; -fx-padding: 0; -fx-cursor: hand;"
        ));
        drawerHeader.getChildren().addAll(drawerTitle, drawerSpacer, closeDrawer);

        Region headerDivider = new Region();
        headerDivider.setPrefHeight(1);
        headerDivider.setStyle("-fx-background-color: #21262d;");

        // Nav items
        String[][] items = {
            {"🏠", "Home",    "/view/fxml/HomePage.fxml"},
            {"🎬", "Movies",  "/view/fxml/FilmView.fxml"},
            {"📺", "Series",  "/view/fxml/SeriesView.fxml"},
            {"📋", "My List", "/view/fxml/MyList.fxml"},
            {"⌛", "History", "/view/fxml/MyHistory.fxml"},
        };

        VBox navItems = new VBox(4);
        navItems.setPadding(new Insets(12, 8, 12, 8));

        for (String[] item : items) {
            HBox row = new HBox(14);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle("-fx-background-color: transparent; -fx-background-radius: 10; -fx-cursor: hand;");

            Label icon = new Label(item[0]);
            icon.setStyle("-fx-font-size: 18; -fx-text-fill: #8b949e;");
            icon.setMinWidth(28);

            Label lbl = new Label(item[1]);
            lbl.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 15;");

            boolean isActive = lastActiveFxml.equals(item[2]);
            if (isActive) {
                lbl.setStyle("-fx-text-fill: white; -fx-font-size: 15; -fx-font-weight: bold;");
                icon.setStyle("-fx-font-size: 18; -fx-text-fill: #008cff;");
                row.setStyle(
                    "-fx-background-color: rgba(0,140,255,0.12); -fx-background-radius: 10;" +
                    "-fx-border-color: rgba(0,140,255,0.25); -fx-border-width: 1; -fx-border-radius: 10; -fx-cursor: hand;"
                );
            }

            // Active indicator bar
            Region activeBar = new Region();
            activeBar.setPrefWidth(3);
            activeBar.setPrefHeight(24);
            activeBar.setStyle("-fx-background-color: #008cff; -fx-background-radius: 2;");
            activeBar.setVisible(isActive);

            row.getChildren().addAll(activeBar, icon, lbl);

            String fxmlPath = item[2];
            row.setOnMouseEntered(e -> {
                if (!lastActiveFxml.equals(fxmlPath))
                    row.setStyle("-fx-background-color: #161b22; -fx-background-radius: 10; -fx-cursor: hand;");
            });
            row.setOnMouseExited(e -> {
                boolean active = lastActiveFxml.equals(fxmlPath);
                row.setStyle(active
                    ? "-fx-background-color: rgba(0,140,255,0.12); -fx-background-radius: 10; -fx-border-color: rgba(0,140,255,0.25); -fx-border-width: 1; -fx-border-radius: 10; -fx-cursor: hand;"
                    : "-fx-background-color: transparent; -fx-background-radius: 10; -fx-cursor: hand;"
                );
            });
            row.setOnMouseClicked(e -> {
                closeDrawerAnim(overlay, drawer);
                navigateTo(fxmlPath);
            });
            navItems.getChildren().add(row);
        }

        drawer.getChildren().addAll(drawerHeader, headerDivider, navItems);

        // ── Layout: overlay + drawer aligned to RIGHT ─────────────────────────
        StackPane.setAlignment(drawer, Pos.CENTER_RIGHT);
        overlay.getChildren().add(drawer);

        // Close on overlay click
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) closeDrawerAnim(overlay, drawer);
        });
        closeDrawer.setOnAction(e -> closeDrawerAnim(overlay, drawer));

        // ── Attach to scene ───────────────────────────────────────────────────
        btnHamburger.setOnAction(e -> {
            Scene scene = btnHamburger.getScene();
            if (scene == null) return;

            Parent sceneRoot = scene.getRoot();
            if (!(sceneRoot instanceof StackPane)) {
                // Wrap existing root in a StackPane
                StackPane wrapper = new StackPane();
                wrapper.getChildren().add(sceneRoot);
                scene.setRoot(wrapper);
                sceneRoot = wrapper;
            }

            StackPane rootStack = (StackPane) sceneRoot;

            // Bind drawer width to 20% of scene
            drawer.prefWidthProperty().bind(scene.widthProperty().multiply(0.20));
            drawer.maxWidthProperty().bind(scene.widthProperty().multiply(0.20));
            drawer.prefHeightProperty().bind(scene.heightProperty());
            overlay.prefWidthProperty().bind(scene.widthProperty());
            overlay.prefHeightProperty().bind(scene.heightProperty());

            if (!rootStack.getChildren().contains(overlay)) {
                rootStack.getChildren().add(overlay);
            }

            // Slide in from right
            overlay.setVisible(true);
            overlay.setOpacity(0);
            drawer.setTranslateX(drawer.getPrefWidth() > 0 ? drawer.getPrefWidth() : 300);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), overlay);
            fadeIn.setToValue(1);
            fadeIn.play();

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), drawer);
            slideIn.setToX(0);
            slideIn.setInterpolator(Interpolator.EASE_OUT);
            slideIn.play();
        });
    }

    private void closeDrawerAnim(StackPane overlay, VBox drawer) {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(280), drawer);
        slideOut.setToX(drawer.getWidth());
        slideOut.setInterpolator(Interpolator.EASE_IN);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(280), overlay);
        fadeOut.setToValue(0);

        ParallelTransition pt = new ParallelTransition(slideOut, fadeOut);
        pt.setOnFinished(e -> overlay.setVisible(false));
        pt.play();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEARCH BAR
    // ─────────────────────────────────────────────────────────────────────────
    private void setupSearchBar() {
        StackPane.setAlignment(clearBtn, Pos.CENTER_RIGHT);
        StackPane.setMargin(clearBtn, new Insets(0, 5, 0, 0));
        clearBtn.setPickOnBounds(true);
        clearBtn.setVisible(false);

        searchInput.textProperty().addListener((obs, oldVal, newVal) ->
            clearBtn.setVisible(!newVal.isEmpty() && searchInput.getPrefWidth() > 0)
        );

        clearBtn.setOnAction(e -> searchInput.clear());
        clearBtn.setOnMouseEntered(e -> clearBtn.setStyle("-fx-text-fill: #00bfff; -fx-background-color: transparent;"));
        clearBtn.setOnMouseExited (e -> clearBtn.setStyle("-fx-text-fill: #888;    -fx-background-color: transparent;"));

        addHoverAnimation(btnResearch);
        addHoverAnimation(profile);
    }
    
    @FXML
    public void toggleSearch(ActionEvent event) {
        if (searchInput.getPrefWidth() == 0) {
            Timeline expand = new Timeline(new KeyFrame(Duration.millis(250),
                new KeyValue(searchInput.prefWidthProperty(), 220),
                new KeyValue(searchInput.maxWidthProperty(),  220)
            ));
            expand.setOnFinished(e -> searchInput.requestFocus());
            expand.play();
        } else {
            Timeline collapse = new Timeline(new KeyFrame(Duration.millis(250),
                new KeyValue(searchInput.prefWidthProperty(), 0),
                new KeyValue(searchInput.maxWidthProperty(),  0)
            ));
            collapse.play();
        }
    }

    @FXML
    private void clearSearchInput() { searchInput.clear(); }

    // ─────────────────────────────────────────────────────────────────────────
    // SEARCH SUGGESTIONS
    // ─────────────────────────────────────────────────────────────────────────
    private void setupSearchSuggestions() {
        ScrollPane scroll = new ScrollPane(suggestionsContent);
        scroll.setPrefWidth(300);
        scroll.setFitToWidth(true);
        scroll.setMaxHeight(250);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle(
            "-fx-background: #0f172a;" +
            "-fx-background-color: #0f172a;" +
            "-fx-border-color: #1e293b;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
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

        searchInput.textProperty().addListener((obs, oldVal, newVal) -> {
            if (ignoreTextChange) return;
            if (newVal.isEmpty()) showLatestSearches();
            else showSuggestions(newVal);
        });

        searchInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String text = searchInput.getText().trim();
                if (!text.isEmpty()) {
                    featuredService.addToLatestSearch(text);
                    suggestionsPopup.hide();
                }
            }
        });
    }

    private void showSuggestions(String text) {
        suggestionsContent.getChildren().clear();

        List<FeaturedItem> items;
        try { items = featuredService.searchByTitle(text); }
        catch (Exception e) { e.printStackTrace(); return; }

        if (items.isEmpty()) {
            Label empty = new Label("🔍 No results found");
            empty.setStyle("-fx-text-fill: #e3f2fd; -fx-font-size: 14; -fx-padding: 20;");
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            suggestionsContent.getChildren().add(empty);
        } else {
            for (FeaturedItem item : items)
                suggestionsContent.getChildren().add(createSuggestionBox(item.getTitle(), item.getPosterUrl(), true));
        }

        Platform.runLater(() -> {
            suggestionsContent.applyCss();
            suggestionsContent.layout();
            double contentH = suggestionsContent.getHeight();
            double maxH     = 250;
            ScrollPane sp   = (ScrollPane) suggestionsPopup.getContent().get(0);
            sp.setVbarPolicy(contentH > maxH ? ScrollPane.ScrollBarPolicy.AS_NEEDED : ScrollPane.ScrollBarPolicy.NEVER);
            suggestionsPopup.setWidth(300);
            suggestionsPopup.setHeight(Math.min(contentH, maxH));
            showSuggestionsPopup();
        });
    }

    private void showLatestSearches() {
        suggestionsContent.getChildren().clear();
        List<String> latest = featuredService.getLatestSearches();
        if (latest.isEmpty()) { suggestionsPopup.hide(); return; }
        for (String title : latest)
            suggestionsContent.getChildren().add(createSuggestionBox(title, null, false));
        Platform.runLater(() -> {
            suggestionsContent.applyCss();
            suggestionsContent.layout();
            showSuggestionsPopup();
        });
    }

    private void showSuggestionsPopup() {
        searchInput.applyCss();
        searchInput.layout();
        Bounds bounds = searchInput.localToScreen(searchInput.getBoundsInLocal());
        suggestionsContent.applyCss();
        suggestionsContent.layout();
        double height = Math.min(suggestionsContent.getHeight(), 250);
        suggestionsPopup.setWidth(searchInput.getWidth());
        suggestionsPopup.setHeight(height);
        if (!suggestionsPopup.isShowing()) {
            suggestionsPopup.show(searchInput, bounds.getMinX(), bounds.getMaxY() + 5);
        } else {
            suggestionsPopup.setX(bounds.getMinX());
            suggestionsPopup.setY(bounds.getMaxY() + 5);
        }
    }

    private HBox createSuggestionBox(String title, String posterUrl, boolean isSearchResult) {
        HBox box = new HBox(10);
        box.setPrefWidth(300);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-background-color: transparent; -fx-padding: 8; -fx-background-radius: 6;");

        if (posterUrl != null) {
            ImageView poster = new ImageView();
            poster.setFitWidth(35);
            poster.setFitHeight(50);
            poster.setPreserveRatio(true);
            try { poster.setImage(new Image(posterUrl, true)); } catch (Exception ignored) {}
            box.getChildren().add(poster);
        }

        Label lbl = new Label(title);
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
        lbl.setMaxWidth(180);
        lbl.setEllipsisString("...");
        lbl.setWrapText(false);
        Tooltip tooltip = new Tooltip(title);
        tooltip.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-font-size: 13;");
        Tooltip.install(lbl, tooltip);
        box.getChildren().add(lbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        box.getChildren().add(spacer);

        if (isSearchResult) {
            Button addBtn = new Button();
            addBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 14; -fx-font-weight: bold;");
            addBtn.setPadding(new Insets(2, 5, 2, 5));
            addBtn.setMinWidth(25);
            addBtn.setMaxWidth(25);
            try {
                MylistService mylistService = new MylistService();
                FeaturedItem item = featuredService.getFeaturedByTitle(title);
                if (item != null) {
                    updateAddButton(addBtn, item);
                    MyListManager.getInstance().addListener((filmId, serieId) -> Platform.runLater(() -> {
                        int cFilmId  = "film".equalsIgnoreCase(item.getType())  ? item.getId()      : 0;
                        int cSerieId = "serie".equalsIgnoreCase(item.getType()) ? item.getSerieId() : 0;
                        if (cFilmId == filmId && cSerieId == serieId) updateAddButton(addBtn, item);
                    }));
                    addBtn.setOnAction(e -> {
                        boolean inList = mylistService.isInList(Session.getUserId(), item.getId(), item.getSerieId());
                        if (inList) mylistService.removeItem(Session.getUserId(), item.getId(), item.getSerieId());
                        else        mylistService.addItem  (Session.getUserId(), item.getId(), item.getSerieId());
                        updateAddButton(addBtn, item);
                        MyListManager.getInstance().notifyItemUpdated(item.getId(), item.getSerieId());
                    });
                }
            } catch (Exception ex) { ex.printStackTrace(); }
            box.getChildren().add(addBtn);
        } else {
            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 12;");
            removeBtn.setOnMouseEntered(e -> removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #00bfff; -fx-font-size: 12;"));
            removeBtn.setOnMouseExited (e -> removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 12;"));
            removeBtn.setOnAction(e -> { featuredService.removeLatestSearch(title); showLatestSearches(); });
            box.getChildren().add(removeBtn);
        }

        box.setOnMouseEntered(e -> box.setStyle("-fx-background-color: #1e293b; -fx-padding: 8; -fx-background-radius: 6;"));
        box.setOnMouseExited (e -> box.setStyle("-fx-background-color: transparent; -fx-padding: 8; -fx-background-radius: 6;"));

        box.setOnMouseClicked(e -> {
            ignoreTextChange = true;
            searchInput.setText(title);
            ignoreTextChange = false;
            try {
                FeaturedItem item = featuredService.getFeaturedByTitle(title);
                if (item != null) {
                    featuredService.addToLatestSearch(title);
                    switch (item.getType().toLowerCase()) {
                        case "film"  -> showFilmPopup(item);
                        case "serie" -> showSeriePopup(item);
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); }
            suggestionsPopup.hide();
        });

        return box;
    }

    private void updateAddButton(Button addBtn, FeaturedItem item) {
        if (addBtn == null || item == null) return;
        try {
            boolean inList = new MylistService().isInList(Session.getUserId(), item.getId(), item.getSerieId());
            addBtn.setText(inList ? "✔" : "+");
            addBtn.setTextFill(inList ? Color.DODGERBLUE : Color.GRAY);
            addBtn.setPadding(new Insets(2, 5, 2, 5));
            addBtn.setMinWidth(25);
            addBtn.setMaxWidth(25);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BELL NOTIFICATION
    // ─────────────────────────────────────────────────────────────────────────
    private void setupBellNotification() {
        bellIcon.setImage(new Image(getClass().getResourceAsStream("/assets/images/bellwhiter.png")));

        // Load bell sound
        try {
            bellSound = new AudioClip(
                getClass().getResource("/assets/sounds/notification.mp3").toString()
            );
            bellSound.setVolume(0.6);
        } catch (Exception e) {
            System.err.println("Bell sound not found: " + e.getMessage());
        }

        if (notificationCircle != null) {
            notificationCircle.setVisible(false);
        }

        buildNotificationPopup();

        // ✅ No sound on click — just open the panel
        bellContainer.setOnMouseClicked(e -> {
            if (notificationPopup.isShowing()) {
                notificationPopup.hide();
            } else {
                checkNewEpisodeNotifications(Session.getUserId());
                unreadCount = notificationService.getUnreadCount(Session.getUserId());
                updateBadge();
                refreshNotificationPanel();
                Bounds b = bellContainer.localToScreen(bellContainer.getBoundsInLocal());
                notificationPopup.show(bellContainer, b.getMaxX() - 340, b.getMaxY() + 10);
            }
        });

        bellContainer.setCursor(javafx.scene.Cursor.HAND);

        // ✅ Start a background timer that checks every 60 seconds for new episodes
        Timeline periodicCheck = new Timeline(
            new KeyFrame(Duration.seconds(10), e -> {
                checkNewEpisodeNotifications(Session.getUserId());
            })
        );
        periodicCheck.setCycleCount(Animation.INDEFINITE);
        periodicCheck.play();
    }
 // ─────────────────────────────────────────────────────────────────────────
 // BELL SOUND & ANIMATION
 // ─────────────────────────────────────────────────────────────────────────
 private void playBellSound() {
     try {
         if (bellSound != null) bellSound.play();
     } catch (Exception ignored) {}
 }

 private void shakeBell() {
     new Timeline(
         new KeyFrame(Duration.ZERO,            new KeyValue(bellIcon.rotateProperty(),   0)),
         new KeyFrame(Duration.millis(100),     new KeyValue(bellIcon.rotateProperty(), -10)),
         new KeyFrame(Duration.millis(200),     new KeyValue(bellIcon.rotateProperty(),  10)),
         new KeyFrame(Duration.millis(300),     new KeyValue(bellIcon.rotateProperty(), -15)),
         new KeyFrame(Duration.millis(400),     new KeyValue(bellIcon.rotateProperty(),  15)),
         new KeyFrame(Duration.millis(500),     new KeyValue(bellIcon.rotateProperty(), -20)),
         new KeyFrame(Duration.millis(600),     new KeyValue(bellIcon.rotateProperty(),  20)),
         new KeyFrame(Duration.millis(700),     new KeyValue(bellIcon.rotateProperty(), -15)),
         new KeyFrame(Duration.millis(800),     new KeyValue(bellIcon.rotateProperty(),  15)),
         new KeyFrame(Duration.millis(900),     new KeyValue(bellIcon.rotateProperty(), -10)),
         new KeyFrame(Duration.millis(1000),    new KeyValue(bellIcon.rotateProperty(),   0))
     ).play();
 }
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
	        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 24, 0.4, 0, 6);"
	    );

	    // ---------------- HEADER ----------------
	    HBox header = new HBox();
	    header.setPadding(new Insets(14, 16, 12, 16));
	    header.setAlignment(Pos.CENTER_LEFT);
	    header.setStyle("-fx-background-color: #161b22; -fx-background-radius: 12 12 0 0;");

	    Label titleLbl = new Label("Notifications");
	    titleLbl.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");

	    notifBadgeLabel = new Label("");
	    notifBadgeLabel.setStyle(
	        "-fx-background-color: #008cff; -fx-text-fill: white; -fx-font-size: 10;" +
	        "-fx-font-weight: bold; -fx-padding: 2 7; -fx-background-radius: 20;"
	    );
	    notifBadgeLabel.setVisible(false);

	    Region headerSpacer = new Region();
	    HBox.setHgrow(headerSpacer, Priority.ALWAYS);

	    // ✔ Mark all read
	    Button markAllRead = new Button("Mark all read");
	    markAllRead.setStyle(
	        "-fx-background-color: transparent; -fx-text-fill: #008cff; -fx-font-size: 11; -fx-cursor: hand;"
	    );
	    markAllRead.setOnAction(e -> {
	        notificationService.markAllRead(Session.getUserId());
	        unreadCount = 0;
	        updateBadge();
	        refreshNotificationPanel();
	    });

	    // 🗑 Clear all
	    Button deleteAllBtn = new Button("Clear all");
	    deleteAllBtn.setStyle(
	        "-fx-background-color: transparent; -fx-text-fill: #ff4d4f; -fx-font-size: 11; -fx-cursor: hand;"
	    );
	    deleteAllBtn.setOnAction(e -> {
	        notificationService.deleteAll(Session.getUserId());
	        unreadCount = 0;
	        updateBadge();
	        refreshNotificationPanel();
	    });

	    header.getChildren().addAll(
	        titleLbl,
	        new HBox(6, notifBadgeLabel),
	        headerSpacer,
	        markAllRead,
	        deleteAllBtn
	    );

	    // ---------------- LIST ----------------
	    notificationListBox = new VBox(0);
	    notificationListBox.setStyle("-fx-background-color: #0d1117;");

	    ScrollPane scroll = new ScrollPane(notificationListBox);
	    scroll.setFitToWidth(true);
	    scroll.setMaxHeight(350);
	    scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
	    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	    scroll.setStyle("-fx-background: #0d1117; -fx-background-color: #0d1117;");

	    try {
	        scroll.getStylesheets().add(getClass().getResource("/view/css/scrollbar.css").toExternalForm());
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
	            "WELCOME"
	        );
	        notificationService.markFirstLoginDone(userId);

	        Platform.runLater(() -> {
	            playBellSound();
	            shakeBell();
	        });
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
	                    "NEW_EPISODE"
	                );
	            }

	            Platform.runLater(() -> {
	                playBellSound();
	                shakeBell();
	                unreadCount = notificationService.getUnreadCount(userId);
	                updateBadge();
	            });
	        }

	    } catch (Exception ignored) {}
	}

	private void refreshNotificationPanel() {
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

	        // ---------------- TOP LINE ----------------
	        HBox topLine = new HBox(8);
	        topLine.setAlignment(Pos.CENTER_LEFT);

	        if (isUnread) {
	            Circle dot = new Circle(4, Color.DODGERBLUE);
	            topLine.getChildren().add(dot);
	        }

	        Label titleLbl = new Label(n.getTitle());
	        titleLbl.setStyle(
	            "-fx-text-fill: " + (isUnread ? "white" : "#c9d1d9") + ";" +
	            "-fx-font-size: 13; -fx-font-weight: " + (isUnread ? "bold" : "normal") + ";"
	        );
	        titleLbl.setWrapText(true);
	        titleLbl.setMaxWidth(240);

	        Region spacer = new Region();
	        HBox.setHgrow(spacer, Priority.ALWAYS);

	        // ❌ Delete button
	        Button deleteBtn = new Button("✕");
	        deleteBtn.setStyle(
	            "-fx-background-color: transparent;" +
	            "-fx-text-fill: #8b949e;" +
	            "-fx-font-size: 12;" +
	            "-fx-cursor: hand;"
	        );

	        deleteBtn.setOnMouseEntered(e ->
	            deleteBtn.setStyle(
	                "-fx-background-color: transparent;" +
	                "-fx-text-fill: #ff4d4f;" +
	                "-fx-font-size: 12;" +
	                "-fx-cursor: hand;"
	            )
	        );

	        deleteBtn.setOnMouseExited(e ->
	            deleteBtn.setStyle(
	                "-fx-background-color: transparent;" +
	                "-fx-text-fill: #8b949e;" +
	                "-fx-font-size: 12;" +
	                "-fx-cursor: hand;"
	            )
	        );

	        deleteBtn.setOnAction(e -> {
	            notificationService.deleteNotification(n.getId());
	            notificationListBox.getChildren().removeAll(row);

	            if (!n.isRead()) {
	                unreadCount = Math.max(0, unreadCount - 1);
	                updateBadge();
	            }
	        });

	        topLine.getChildren().addAll(titleLbl, spacer, deleteBtn);

	        // ---------------- BODY ----------------
	        Label bodyLbl = new Label(n.getBody());
	        bodyLbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11;");
	        bodyLbl.setWrapText(true);
	        bodyLbl.setMaxWidth(300);

	        Label timeLbl = new Label(formatNotifTime(n.getCreatedAt()));
	        timeLbl.setStyle("-fx-text-fill: #484f58; -fx-font-size: 10;");

	        row.getChildren().addAll(topLine, bodyLbl, timeLbl);

	        Region div = new Region();
	        div.setPrefHeight(1);
	        div.setStyle("-fx-background-color: #21262d;");

	        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #161b22; -fx-cursor: hand;"));
	        row.setOnMouseExited(e -> row.setStyle(rowBg + "-fx-cursor: hand;"));

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
	        if (notificationCircle != null) {
	            notificationCircle.setVisible(unreadCount > 0);
	        }
	        if (notifBadgeLabel != null) {
	            notifBadgeLabel.setVisible(unreadCount > 0);
	            notifBadgeLabel.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
	        }
	    });
	}

	private String formatNotifTime(LocalDateTime dt) {
	    if (dt == null) return "";

	    LocalDateTime now = LocalDateTime.now();
	    long minutes = java.time.Duration.between(dt, now).toMinutes();

	    if (minutes < 1) return "just now";
	    if (minutes < 60) return minutes + "m ago";
	    if (minutes < 1440) return (minutes / 60) + "h ago";

	    return dt.format(DateTimeFormatter.ofPattern("MMM d"));
	}

    // ─────────────────────────────────────────────────────────────────────────
    // NAV BUTTONS
    // ─────────────────────────────────────────────────────────────────────────
    private void setupNavButtons() {
        setupButton(btnHome,    lineHome,    this::goToHomepage,    "/view/fxml/HomePage.fxml");
        setupButton(btnMovies,  lineMovies,  this::goToFilmView,    "/view/fxml/FilmView.fxml");
        setupButton(btnSeries,  lineSeries,  this::goToSeriesView,  "/view/fxml/SeriesView.fxml");
        setupButton(btnMyList,  lineMyList,  this::goToMyListView,  "/view/fxml/MyList.fxml");
        if (btnHistory != null)
            setupButton(btnHistory, lineHistory, this::goToMyHistoryView, "/view/fxml/MyHistory.fxml");
    }

    private void restoreActiveTab(String fxmlPath) {
        switch (fxmlPath) {
            case "/view/fxml/FilmView.fxml"    -> activate(btnMovies,  lineMovies);
            case "/view/fxml/SeriesView.fxml"  -> activate(btnSeries,  lineSeries);
            case "/view/fxml/MyList.fxml"      -> activate(btnMyList,  lineMyList);
            case "/view/fxml/MyHistory.fxml"   -> { if (btnHistory != null) activate(btnHistory, lineHistory); }
            default                            -> activate(btnHome,    lineHome);
        }
    }

    private void activate(Button btn, Rectangle line) {
        if (activeButton != null && activeButton != btn) {
            setButtonInactive(activeButton);
            animateLine(activeLine, 0);
        }
        activeButton = btn;
        activeLine   = line;
        setButtonActive(btn);
        Platform.runLater(() -> animateLine(line, btn.getWidth()));
    }

    private void setupButton(Button btn, Rectangle line, Runnable action, String fxmlPath) {
        if (btn == null) return;
        if (line != null) {
            line.setFill(Color.DODGERBLUE);
            line.setHeight(3);
        }

        btn.setOnMouseEntered(e -> {
            if (btn != activeButton) { setButtonHover(btn); if (line != null) animateLine(line, btn.getWidth()); }
        });
        btn.setOnMouseExited(e -> {
            if (btn != activeButton) { setButtonInactive(btn); if (line != null) animateLine(line, 0); }
        });
        btn.setOnAction(e -> {
            if (activeButton != null && activeButton != btn) {
                setButtonInactive(activeButton);
                if (activeLine != null) animateLine(activeLine, 0);
            }
            activeButton = btn;
            activeLine   = line;
            setButtonActive(btn);
            if (line != null) line.setWidth(btn.getWidth());
            if (action != null) action.run();
        });
    }

    private void setButtonActive  (Button btn) { if (btn != null) btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: white;   -fx-font-size: 16;"); }
    private void setButtonHover   (Button btn) { if (btn != null) btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: white;   -fx-font-size: 16;"); }
    private void setButtonInactive(Button btn) { if (btn != null) btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #cccccc; -fx-font-size: 16;"); }

    private void animateLine(Rectangle line, double targetWidth) {
        if (line == null) return;
        Platform.runLater(() -> new Timeline(
            new KeyFrame(Duration.millis(200), new KeyValue(line.widthProperty(), targetWidth))
        ).play());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NAVIGATION
    // ─────────────────────────────────────────────────────────────────────────
    public void goToHomepage()      { navigateTo("/view/fxml/HomePage.fxml"); }
    public void goToFilmView()      { navigateTo("/view/fxml/FilmView.fxml"); }
    public void goToSeriesView()    { navigateTo("/view/fxml/SeriesView.fxml"); }
    public void goToMyListView()    { navigateTo("/view/fxml/MyList.fxml"); }
    public void goToMyHistoryView() { navigateTo("/view/fxml/MyHistory.fxml"); }

    private void navigateTo(String fxmlPath) {
        if (fxmlPath.equals(lastActiveFxml)) return;

        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) { System.err.println("FXML not found: " + fxmlPath); return; }

            Parent newRoot = FXMLLoader.load(url);
            lastActiveFxml = fxmlPath;

            // Get the scene from any known node
            Scene scene = btnHome.getScene();
            if (scene == null) scene = rootPane.getScene();

            scene.setRoot(newRoot);

            // Scroll reset
            Platform.runLater(() -> {
                newRoot.lookupAll(".scroll-pane").forEach(node -> ((ScrollPane) node).setVvalue(0));
                newRoot.lookupAll(".list-view").forEach(node  -> ((ListView<?>) node).scrollTo(0));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ─────────────────────────────────────────────────────────────────────────
    // PROFILE
    // ─────────────────────────────────────────────────────────────────────────
    private void loadUserProfile() {
        String path = userService.getProfilePhoto(Session.getUserId());
        if (path != null && !path.isEmpty()) applyProfileImage(profile, path);
    }

    private void applyProfileImage(Button btn, String imagePath) {
        try {
            ImageView view = new ImageView(new Image(imagePath, false));
            view.setFitWidth(45);
            view.setFitHeight(45);
            view.setPreserveRatio(false);
            view.setSmooth(true);
            view.setClip(new Circle(22.5, 22.5, 22.5));
            btn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            btn.setGraphic(view);
            btn.setText("");
        } catch (Exception ignored) {}
    }

    private void setupProfileButtonAction() {
        profile.setOnAction(e -> {
            if (profilePopup.isShowing()) {
                profilePopup.hide();
            } else {
                Bounds b = profile.localToScreen(profile.getBoundsInLocal());
                profilePopup.show(profile, b.getMinX(), b.getMaxY() + 5);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PROFILE POPUP
    // ─────────────────────────────────────────────────────────────────────────
    private void setupProfilePopup() {
        profilePopup = new Popup();
        profilePopup.setAutoHide(true);

        VBox root = new VBox(0);
        root.setPrefWidth(280);
        root.setStyle(
            "-fx-background-color: #0d1117;" +
            "-fx-border-color: #21262d;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0.3, 0, 6);"
        );

        VBox banner = new VBox(8);
        banner.setPadding(new Insets(20, 20, 16, 20));
        banner.setStyle("-fx-background-color: #161b22; -fx-background-radius: 12 12 0 0;");

        StackPane avatarStack = new StackPane();
        avatarStack.setPrefSize(72, 72);
        avatarStack.setMaxSize(72, 72);

        String imagePath = userService.getProfilePhoto(Session.getUserId());
        if (imagePath == null || imagePath.isEmpty()) imagePath = "/assets/images/profile.png";

        ImageView popupImg = new ImageView(new Image(imagePath));
        popupImg.setFitWidth(72); popupImg.setFitHeight(72);
        popupImg.setPreserveRatio(false); popupImg.setSmooth(true);
        popupImg.setClip(new Circle(36, 36, 36));

        StackPane editOverlay = new StackPane();
        editOverlay.setPrefSize(72, 72);
        editOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.55); -fx-background-radius: 50%;");
        editOverlay.setOpacity(0);
        Label cameraIcon = new Label("✎");
        cameraIcon.setStyle("-fx-text-fill: white; -fx-font-size: 18;");
        editOverlay.getChildren().add(cameraIcon);
        editOverlay.setCursor(javafx.scene.Cursor.HAND);

        avatarStack.getChildren().addAll(popupImg, editOverlay);

        FadeTransition fadeIn  = new FadeTransition(Duration.millis(150), editOverlay); fadeIn.setToValue(1);
        FadeTransition fadeOut2 = new FadeTransition(Duration.millis(150), editOverlay); fadeOut2.setToValue(0);
        avatarStack.setOnMouseEntered(e -> fadeIn.playFromStart());
        avatarStack.setOnMouseExited (e -> fadeOut2.playFromStart());

        avatarStack.setOnMouseClicked(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose Profile Picture");
            fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            Window owner = profilePopup.getOwnerWindow();
            File file = fc.showOpenDialog(owner);
            if (file != null) {
                String url = file.toURI().toString();
                Image newImg = new Image(url);
                ImageView newView = new ImageView(newImg);
                newView.setFitWidth(72); newView.setFitHeight(72);
                newView.setPreserveRatio(false); newView.setSmooth(true);
                newView.setClip(new Circle(36, 36, 36));
                avatarStack.getChildren().set(0, newView);
                applyProfileImage(profile, url);
                userService.updateProfilePhoto(Session.getUserId(), url);
                Session.setProfileImagePath(url);
            }
        });

        applyProfileImage(profile, imagePath);

        Label usernameLabel = new Label(Session.getUsername());
        usernameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15; -fx-font-weight: bold;");
        UsernameChangeNotifier.addListener(usernameLabel::setText);

        String email = userService.getEmail(Session.getUserId());
        Label emailLabel = new Label(email != null ? email : "");
        emailLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12;");

        HBox avatarRow = new HBox(14, avatarStack, new VBox(4, usernameLabel, emailLabel));
        avatarRow.setAlignment(Pos.CENTER_LEFT);
        ((VBox) avatarRow.getChildren().get(1)).setAlignment(Pos.CENTER_LEFT);
        banner.getChildren().add(avatarRow);

        Region div1 = new Region();
        div1.setPrefHeight(1);
        div1.setStyle("-fx-background-color: #21262d;");

        VBox menu = new VBox(2);
        menu.setPadding(new Insets(8, 8, 8, 8));
        menu.getChildren().addAll(
            makeMenuItem("☰",  "My List",    () -> { profilePopup.hide(); goToMyListView();    }),
            makeMenuItem("⌛",  "My History", () -> { profilePopup.hide(); goToMyHistoryView(); }),
            makeMenuItem("⚙",  "Settings",   () -> showSettingsPopup())
        );

        Region div2 = new Region();
        div2.setPrefHeight(1);
        div2.setStyle("-fx-background-color: #21262d;");

        HBox logoutRow = new HBox();
        logoutRow.setPadding(new Insets(8, 8, 8, 8));
        Button btnLogout = new Button("Sign out");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #f85149; -fx-font-size: 13;" +
            "-fx-alignment: CENTER_LEFT; -fx-padding: 8 12; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        btnLogout.setOnMouseEntered(e -> btnLogout.setStyle(
            "-fx-background-color: rgba(248,81,73,0.1); -fx-text-fill: #f85149; -fx-font-size: 13;" +
            "-fx-alignment: CENTER_LEFT; -fx-padding: 8 12; -fx-background-radius: 8; -fx-cursor: hand;"
        ));
        btnLogout.setOnMouseExited(e -> btnLogout.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #f85149; -fx-font-size: 13;" +
            "-fx-alignment: CENTER_LEFT; -fx-padding: 8 12; -fx-background-radius: 8; -fx-cursor: hand;"
        ));
        btnLogout.setOnAction(e -> Platform.exit());
        HBox.setHgrow(btnLogout, Priority.ALWAYS);
        logoutRow.getChildren().add(btnLogout);

        root.getChildren().addAll(banner, div1, menu, div2, logoutRow);
        profilePopup.getContent().add(root);
    }

    private HBox makeMenuItem(String icon, String label, Runnable action) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;");

        Label ico = new Label(icon);
        ico.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 14;");
        ico.setMinWidth(20);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 13;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label arrow = new Label("›");
        arrow.setStyle("-fx-text-fill: #484f58; -fx-font-size: 16;");
        row.getChildren().addAll(ico, lbl, spacer, arrow);

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #161b22; -fx-background-radius: 8; -fx-cursor: hand;"));
        row.setOnMouseExited (e -> row.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;"));
        row.setOnMouseClicked(e -> action.run());
        return row;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SETTINGS POPUP
    // ─────────────────────────────────────────────────────────────────────────
    private void showSettingsPopup() {
        profilePopup.hide();

        Stage settingsStage = new Stage();
        settingsStage.initOwner(profile.getScene().getWindow());
        settingsStage.initModality(Modality.WINDOW_MODAL);
        settingsStage.initStyle(StageStyle.TRANSPARENT);
        settingsStage.setTitle("Settings");

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.65);");
        overlay.setPrefSize(profile.getScene().getWidth(), profile.getScene().getHeight());

        VBox card = new VBox(0);
        card.setMaxWidth(480);
        card.setMaxHeight(500);
        card.setStyle(
            "-fx-background-color: #0d1117; -fx-border-color: #30363d; -fx-border-width: 1;" +
            "-fx-border-radius: 14; -fx-background-radius: 14;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 30, 0.4, 0, 8);"
        );

        HBox header = new HBox();
        header.setPadding(new Insets(20, 20, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
            "-fx-background-color: #161b22; -fx-background-radius: 14 14 0 0;" +
            "-fx-border-color: transparent transparent #21262d transparent; -fx-border-width: 1;"
        );

        Label titleLabel = new Label("Account settings");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
        Region headerSpacer = new Region(); HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
            "-fx-background-color: #21262d; -fx-text-fill: #8b949e; -fx-font-size: 12;" +
            "-fx-background-radius: 50%; -fx-min-width: 28; -fx-min-height: 28;" +
            "-fx-max-width: 28; -fx-max-height: 28; -fx-padding: 0; -fx-cursor: hand;"
        );
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(
            "-fx-background-color: #30363d; -fx-text-fill: white; -fx-font-size: 12;" +
            "-fx-background-radius: 50%; -fx-min-width: 28; -fx-min-height: 28;" +
            "-fx-max-width: 28; -fx-max-height: 28; -fx-padding: 0; -fx-cursor: hand;"
        ));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(
            "-fx-background-color: #21262d; -fx-text-fill: #8b949e; -fx-font-size: 12;" +
            "-fx-background-radius: 50%; -fx-min-width: 28; -fx-min-height: 28;" +
            "-fx-max-width: 28; -fx-max-height: 28; -fx-padding: 0; -fx-cursor: hand;"
        ));
        closeBtn.setOnAction(e -> settingsStage.close());
        header.getChildren().addAll(titleLabel, headerSpacer, closeBtn);

        VBox body = new VBox(20);
        body.setPadding(new Insets(24));

        Label sectionLabel = new Label("PROFILE");
        sectionLabel.setStyle("-fx-text-fill: #484f58; -fx-font-size: 11; -fx-font-weight: bold;");

        VBox usernameGroup = makeSettingsField("Username", Session.getUsername());
        TextField tfUsername = (TextField) ((VBox) usernameGroup).getChildren().get(1);

        Label usernameErr = new Label();
        usernameErr.setStyle("-fx-text-fill: #f85149; -fx-font-size: 12;");
        usernameErr.setVisible(false);
        usernameErr.setManaged(false);

        VBox passwordGroup = makeSettingsFieldPassword("New password", "Leave blank to keep current");

        Region div = new Region(); div.setPrefHeight(1);
        div.setStyle("-fx-background-color: #21262d;");

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle(
            "-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-font-size: 13;" +
            "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #30363d;" +
            "-fx-border-width: 1; -fx-padding: 8 18; -fx-cursor: hand;"
        );
        btnCancel.setOnAction(e -> settingsStage.close());

        Button btnSave = new Button("Save changes");
        btnSave.setStyle(
            "-fx-background-color: #008cff; -fx-text-fill: white; -fx-font-size: 13;" +
            "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand;"
        );
        btnSave.setOnMouseEntered(e -> btnSave.setStyle(
            "-fx-background-color: #339eff; -fx-text-fill: white; -fx-font-size: 13;" +
            "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand;"
        ));
        btnSave.setOnMouseExited(e -> btnSave.setStyle(
            "-fx-background-color: #008cff; -fx-text-fill: white; -fx-font-size: 13;" +
            "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand;"
        ));

        PasswordField pfPassword = (PasswordField) ((VBox) passwordGroup).getChildren().get(1);

        HBox successBanner = new HBox(8);
        successBanner.setPadding(new Insets(10, 14, 10, 14));
        successBanner.setStyle(
            "-fx-background-color: rgba(35,134,54,0.15); -fx-background-radius: 8;" +
            "-fx-border-color: #238636; -fx-border-width: 1; -fx-border-radius: 8;"
        );
        successBanner.setVisible(false);
        successBanner.setManaged(false);
        Label successLabel = new Label("Changes saved successfully");
        successLabel.setStyle("-fx-text-fill: #3fb950; -fx-font-size: 13;");
        successBanner.getChildren().add(successLabel);

        btnSave.setOnAction(e -> {
            boolean saved = false;
            usernameErr.setVisible(false); usernameErr.setManaged(false);

            String newUsername = tfUsername.getText().trim();
            if (!newUsername.isEmpty() && !newUsername.equals(Session.getUsername())) {
                if (userService.usernameExists(newUsername)) {
                    usernameErr.setText("Username already taken");
                    usernameErr.setVisible(true); usernameErr.setManaged(true);
                } else {
                    userService.updateUsername(Session.getUserId(), newUsername);
                    Session.setUsername(newUsername);
                    UsernameChangeNotifier.notifyAllListeners(newUsername);
                    saved = true;
                }
            }

            String newPassword = pfPassword.getText();
            if (!newPassword.isEmpty()) {
                userService.updateUserPassword(Session.getUserId(), newPassword);
                pfPassword.clear();
                saved = true;
            }

            if (saved) {
                successBanner.setVisible(true); successBanner.setManaged(true);
                new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
                    successBanner.setVisible(false); successBanner.setManaged(false);
                })).play();
            }
        });

        actions.getChildren().addAll(btnCancel, btnSave);
        body.getChildren().addAll(sectionLabel, usernameGroup, usernameErr, passwordGroup, successBanner, div, actions);
        card.getChildren().addAll(header, body);
        overlay.getChildren().add(card);
        overlay.setOnMouseClicked(e -> { if (e.getTarget() == overlay) settingsStage.close(); });

        Scene scene = new Scene(overlay);
        scene.setFill(Color.TRANSPARENT);
        settingsStage.setScene(scene);
        settingsStage.showAndWait();
    }

    private VBox makeSettingsField(String labelText, String initialValue) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12;");
        TextField tf = new TextField(initialValue);
        tf.setStyle(
            "-fx-background-color: #010409; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d;" +
            "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-font-size: 13; -fx-padding: 10 12;"
        );
        tf.setMaxWidth(Double.MAX_VALUE);
        tf.setOnMouseEntered(e -> tf.setStyle(
            "-fx-background-color: #010409; -fx-text-fill: #c9d1d9; -fx-border-color: #8b949e;" +
            "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13; -fx-padding: 10 12;"
        ));
        tf.setOnMouseExited(e -> tf.setStyle(
            "-fx-background-color: #010409; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d;" +
            "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13; -fx-padding: 10 12;"
        ));
        return new VBox(6, lbl, tf);
    }

    private VBox makeSettingsFieldPassword(String labelText, String promptText) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12;");
        PasswordField pf = new PasswordField();
        pf.setPromptText(promptText);
        pf.setStyle(
            "-fx-background-color: #010409; -fx-text-fill: #c9d1d9; -fx-prompt-text-fill: #484f58;" +
            "-fx-border-color: #30363d; -fx-border-width: 1; -fx-border-radius: 8;" +
            "-fx-background-radius: 8; -fx-font-size: 13; -fx-padding: 10 12;"
        );
        pf.setMaxWidth(Double.MAX_VALUE);
        pf.setOnMouseEntered(e -> pf.setStyle(
            "-fx-background-color: #010409; -fx-text-fill: #c9d1d9; -fx-prompt-text-fill: #484f58;" +
            "-fx-border-color: #8b949e; -fx-border-width: 1; -fx-border-radius: 8;" +
            "-fx-background-radius: 8; -fx-font-size: 13; -fx-padding: 10 12;"
        ));
        pf.setOnMouseExited(e -> pf.setStyle(
            "-fx-background-color: #010409; -fx-text-fill: #c9d1d9; -fx-prompt-text-fill: #484f58;" +
            "-fx-border-color: #30363d; -fx-border-width: 1; -fx-border-radius: 8;" +
            "-fx-background-radius: 8; -fx-font-size: 13; -fx-padding: 10 12;"
        ));
        return new VBox(6, lbl, pf);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FILM POPUP
    // ─────────────────────────────────────────────────────────────────────────
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

        Button close = new Button("✕");
        close.setStyle("-fx-background-color:#008cff; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:50%; -fx-padding:5 10;");
        close.setOnAction(e -> popup.close());
        addHoverAnimation(close);

        HBox topBar = new HBox(close);
        topBar.setAlignment(Pos.TOP_RIGHT);
        content.getChildren().add(topBar);

        try {
            if ("film".equalsIgnoreCase(item.getType())) {
                Film film = featuredService.getFilmDetails(item.getId());
                if (film == null) return;

                HBox filmBox = new HBox(30);
                filmBox.setAlignment(Pos.TOP_LEFT);
                filmBox.setMaxWidth(Double.MAX_VALUE);

                ImageView poster = new ImageView();
                poster.setFitWidth(300); poster.setFitHeight(450);
                try { poster.setImage(new Image(film.getPoster_url())); } catch (Exception ignored) {}

                VBox right = new VBox(15);
                right.setAlignment(Pos.TOP_LEFT);
                right.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(right, Priority.ALWAYS);

                ImageView titleImage = new ImageView();
                titleImage.setFitHeight(150); titleImage.setPreserveRatio(true);
                try { titleImage.setImage(new Image(film.getTitle_image_url())); } catch (Exception ignored) {}

                HBox starsBox = new HBox(3);
                for (int i = 0; i < 5; i++) {
                    Label star = new Label("★");
                    star.setStyle("-fx-font-size:24; -fx-font-weight:bold;");
                    star.setTextFill(i < film.getRating() ? Color.DEEPSKYBLUE : Color.LIGHTGRAY);
                    starsBox.getChildren().add(star);
                }

                int totalMin = (int) film.getDuration();
                Label duration = new Label("⏱ " + totalMin / 60 + "h " + totalMin % 60 + "min");
                duration.setStyle("-fx-text-fill:#aaaaaa; -fx-font-size:16;");

                Label casting = new Label("Casting: " + (film.getCasting() != null ? film.getCasting() : ""));
                casting.setWrapText(true); casting.setMaxWidth(600);
                casting.setStyle("-fx-text-fill:#cccccc; -fx-font-size:16;");

                String cats = film.getCategories() != null
                    ? film.getCategories().stream().map(c -> c.getName()).reduce((a, b) -> a + " • " + b).orElse("") : "";
                Label categories = new Label("Categories: " + cats);
                categories.setWrapText(true); categories.setMaxWidth(600);
                categories.setStyle("-fx-text-fill:#00aaff; -fx-font-size:16;");

                Label synopsis = new Label(film.getSynopsis() != null ? film.getSynopsis() : "");
                synopsis.setWrapText(true); synopsis.setMaxWidth(600);
                synopsis.setStyle("-fx-text-fill:#cccccc; -fx-font-size:16;");

                int userId = Session.getUserId();
                int filmId = film.getFilm_id();
                int dur    = (int) film.getDuration();
                WatchStatus status;
                if (!filmProgressService.exists(userId, filmId))                        status = WatchStatus.NOT_STARTED;
                else if (filmProgressService.getLastPosition(userId, filmId) >= dur - 2) status = WatchStatus.COMPLETED;
                else                                                                      status = WatchStatus.IN_PROGRESS;

                Label statusLabel = new Label(status.toString());
                String statusColor = switch (status) {
                    case NOT_STARTED -> "#777777";
                    case IN_PROGRESS -> "#008cff";
                    case COMPLETED   -> "#00c853";
                };
                statusLabel.setStyle(
                    "-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;" +
                    "-fx-padding: 4 10; -fx-background-radius: 20; -fx-border-radius: 20;" +
                    "-fx-background-color: " + statusColor + ";"
                );

                right.getChildren().addAll(titleImage, starsBox, duration, casting, categories, synopsis, statusLabel);
                filmBox.getChildren().addAll(poster, right);
                content.getChildren().add(filmBox);

                Button trailer = new Button("Trailer");
                Button play    = new Button("▶");
                for (Button b : new Button[]{trailer, play}) {
                    b.setStyle(
                        "-fx-background-color: transparent; -fx-background-radius: 30;" +
                        "-fx-border-color: #00aaff; -fx-border-width: 2; -fx-border-radius: 50%;" +
                        "-fx-text-fill: #00aaff; -fx-font-size: 20; -fx-padding: 6 20;" +
                        "-fx-effect: dropshadow(gaussian,#00aaff,10,0,0,0);"
                    );
                }
                trailer.setOnAction(e -> showTrailerPopup(film, 0));
                play.setOnAction(e -> { popup.close(); goToLecturePageFilm(filmId); });
                addHoverAnimation(trailer);

                ScaleTransition pulse = new ScaleTransition(Duration.millis(800), play);
                pulse.setFromX(1); pulse.setFromY(1);
                pulse.setToX(1.10); pulse.setToY(1.10);
                pulse.setCycleCount(Animation.INDEFINITE);
                pulse.setAutoReverse(true);
                pulse.play();

                HBox buttonBox = new HBox(20, trailer, play);
                buttonBox.setAlignment(Pos.CENTER_RIGHT);
                content.getChildren().add(buttonBox);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }

        root.getChildren().add(content);
        FadeTransition fade = new FadeTransition(Duration.millis(400), root);
        fade.setFromValue(0); fade.setToValue(1); fade.play();

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SERIE POPUP
    // ─────────────────────────────────────────────────────────────────────────
    public void showSeriePopup(FeaturedItem item) {
        if (item == null || !"serie".equalsIgnoreCase(item.getType())) return;

        Serie serie;
        try {
            serie = featuredService.getFullSerie(item.getSerieId());
            if (serie == null) return;
        } catch (SQLException e) { e.printStackTrace(); return; }

        Stage popup = new Stage();
        popup.initOwner(rootPane.getScene().getWindow());
        popup.initModality(Modality.WINDOW_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.0);");
        root.setMaxWidth(Double.MAX_VALUE);
        root.setMaxHeight(Double.MAX_VALUE);
        root.setPrefWidth(Double.MAX_VALUE);
        root.setPrefHeight(Double.MAX_VALUE);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(600);
        scrollPane.setPrefWidth(1400);
        scrollPane.setMaxWidth(1400);
        scrollPane.setMaxHeight(500);

        HBox slider = new HBox(40);
        slider.setAlignment(Pos.CENTER_LEFT);
        slider.setPadding(new Insets(50));

        List<Season> seasons = serie.getSeasons();
        List<StackPane> cards = new ArrayList<>();
        final int[] currentIndex = {0};

        for (int i = 0; i < seasons.size(); i++) {
            StackPane card = createSeasonCard(serie, i, popup);
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

        Button left  = new Button("<");
        Button right = new Button(">");
        styleSlideButton(left);
        styleSlideButton(right);

        left.setOnAction(e -> {
            if (currentIndex[0] > 0) {
                currentIndex[0]--;
                updateSeasonSlider(cards, currentIndex[0]);
                centerSlide(scrollPane, cards.get(currentIndex[0]), slider);
            }
        });
        right.setOnAction(e -> {
            if (currentIndex[0] < cards.size() - 1) {
                currentIndex[0]++;
                updateSeasonSlider(cards, currentIndex[0]);
                centerSlide(scrollPane, cards.get(currentIndex[0]), slider);
            }
        });

        StackPane.setAlignment(left,  Pos.CENTER_LEFT);  StackPane.setMargin(left,  new Insets(0, 0, 0, 10));
        StackPane.setAlignment(right, Pos.CENTER_RIGHT); StackPane.setMargin(right, new Insets(0, 10, 0, 0));

        root.setOnMouseMoved(e -> {
            fadeButton(left,  e.getX() < 150                   ? 1 : 0, 200);
            fadeButton(right, e.getX() > root.getWidth() - 150 ? 1 : 0, 200);
        });

        Button close = new Button("✕");
        close.setStyle("-fx-background-color:#008cff; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:50%; -fx-padding:5 10;");
        close.setOnAction(e -> popup.close());
        addHoverAnimation(close);
        StackPane.setAlignment(close, Pos.TOP_RIGHT);
        StackPane.setMargin(close, new Insets(20));
        root.getChildren().addAll(left, right, close);

        updateSeasonSlider(cards, 0);
        Platform.runLater(() -> centerSlide(scrollPane, cards.get(0), slider));

        FadeTransition fade = new FadeTransition(Duration.millis(400), root);
        fade.setFromValue(0); fade.setToValue(1); fade.play();

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popup.setWidth(rootPane.getScene().getWidth());
        popup.setHeight(rootPane.getScene().getHeight());
        popup.setScene(scene);
        popup.showAndWait();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEASON CARD
    // ─────────────────────────────────────────────────────────────────────────
    private StackPane createSeasonCard(Serie serie, int seasonIndex, Stage popup) {
        Season s = serie.getSeasons().get(seasonIndex);

        StackPane card = new StackPane();
        card.setPrefSize(740, 420);
        card.setStyle(
            "-fx-background-color: #07090f;" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: rgba(56,189,248,0.18);" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 18;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.85), 40, 0.6, 0, 8);"
        );

        Rectangle clip = new Rectangle(740, 420);
        clip.setArcWidth(36); clip.setArcHeight(36);
        card.setClip(clip);

        StackPane contentWrapper = new StackPane();
        contentWrapper.setPrefSize(740, 420);
        card.getChildren().add(contentWrapper);

        // ── POSTER ───────────────────────────────────────────────────
        ImageView poster = new ImageView();
        poster.setFitWidth(300);
        poster.setFitHeight(420);
        poster.setPreserveRatio(true);
        poster.setSmooth(true);
        poster.setCache(true);

        try {
            poster.setImage(new Image(s.getPosterUrl(), true));
        } catch (Exception ignored) {}

        StackPane posterWrapper = new StackPane(poster);
        posterWrapper.setPrefSize(300, 420);
        posterWrapper.setMaxSize(300, 420);
        posterWrapper.setAlignment(Pos.CENTER);

        Rectangle posterClip = new Rectangle(300, 420);
        posterClip.setArcWidth(20);
        posterClip.setArcHeight(20);
        posterWrapper.setClip(posterClip);

        poster.setScaleX(1.05);
        poster.setScaleY(1.05);

        Region posterFade = new Region();
        posterFade.setPrefSize(300, 420);
        posterFade.setStyle(
            "-fx-background-color: linear-gradient(to right," +
            "  transparent 0%, rgba(7,9,15,0.55) 70%, rgba(7,9,15,1.0) 100%);"
        );

        StackPane posterPane = new StackPane(posterWrapper, posterFade);
        posterPane.setPrefSize(300, 420);
        posterPane.setMaxSize(300, 420);
        StackPane.setAlignment(posterPane, Pos.CENTER_LEFT);

        // ── INFO PANEL ───────────────────────────────────────────────
        VBox infoBox = new VBox(12);
        infoBox.setPadding(new Insets(28, 24, 24, 18));
        infoBox.setAlignment(Pos.TOP_LEFT);
        infoBox.setMaxWidth(420);
        infoBox.setPrefWidth(420);
        StackPane.setAlignment(infoBox, Pos.CENTER_RIGHT);

        Label badge = new Label("SEASON " + s.getSeasonNum());
        badge.setStyle(
            "-fx-background-color: rgba(56,189,248,0.12);" +
            "-fx-border-color: rgba(56,189,248,0.45);" +
            "-fx-border-width: 1; -fx-border-radius: 4; -fx-background-radius: 4;" +
            "-fx-text-fill: #38bdf8; -fx-font-size: 10px; -fx-font-weight: bold;" +
            "-fx-letter-spacing: 3; -fx-padding: 4 10;"
        );

        Label title = new Label(s.getTitle() != null ? s.getTitle() : "Untitled Season");
        title.setStyle(
            "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;" +
            "-fx-wrap-text: true;"
        );
        title.setMaxWidth(380); title.setWrapText(true);

        HBox starsBox = new HBox(4);
        starsBox.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < 5; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-font-size: 15px; -fx-text-fill: " +
                (i < s.getRating() ? "#38bdf8;" : "rgba(255,255,255,0.15);"));
            starsBox.getChildren().add(star);
        }

        String statusText = s.getStatus() != null ? s.getStatus() : "Unknown";
        boolean isOngoing = statusText.equalsIgnoreCase("Ongoing");
        int epCount = s.getEpisodes() != null ? s.getEpisodes().size() : 0;

        Label statusPill = new Label(statusText.toUpperCase());
        statusPill.setStyle(
            "-fx-background-color: " + (isOngoing ? "rgba(34,197,94,0.15)" : "rgba(148,163,184,0.12)") + ";" +
            "-fx-border-color: "     + (isOngoing ? "rgba(34,197,94,0.5)"  : "rgba(148,163,184,0.3)")  + ";" +
            "-fx-border-width: 1; -fx-border-radius: 20; -fx-background-radius: 20;" +
            "-fx-text-fill: "        + (isOngoing ? "#4ade80" : "#94a3b8") + ";" +
            "-fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 1.5; -fx-padding: 3 10;"
        );

        Label episodeCount = new Label(epCount + " Episodes");
        episodeCount.setStyle("-fx-text-fill: rgba(148,163,184,0.75); -fx-font-size: 13px;");

        Label synopsis = new Label(s.getSynopsis() != null ? s.getSynopsis() : "No synopsis available.");
        synopsis.setWrapText(true); synopsis.setMaxWidth(370);
        synopsis.setStyle(
            "-fx-text-fill: rgba(203,213,225,0.7); -fx-font-size: 12px; -fx-line-spacing: 3;"
        );

        Region divider = new Region();
        divider.setPrefHeight(1); divider.setMaxHeight(1);
        divider.setStyle(
            "-fx-background-color: linear-gradient(to right, transparent, rgba(56,189,248,0.4), transparent);"
        );

        Button trailerBtn  = new Button("▶  Trailer");
        Button episodesBtn = new Button("≡  Episodes");
        styleCardButton(trailerBtn,  false);
        styleCardButton(episodesBtn, true);
        addHoverAnimation(trailerBtn);
        addHoverAnimation(episodesBtn);

        ScaleTransition epPulse = new ScaleTransition(Duration.millis(900), episodesBtn);
        epPulse.setFromX(1.0); epPulse.setFromY(1.0);
        epPulse.setToX(1.05);  epPulse.setToY(1.05);
        epPulse.setAutoReverse(true);
        epPulse.setCycleCount(Animation.INDEFINITE);
        epPulse.play();

        infoBox.getChildren().addAll(
            badge, title, starsBox,
            new HBox(10, statusPill, episodeCount) {{ setAlignment(Pos.CENTER_LEFT); }},
            synopsis, divider,
            new HBox(12, trailerBtn, episodesBtn) {{ setAlignment(Pos.CENTER_LEFT); }}
        );

        // ── MAIN VIEW ────────────────────────────────────────────────
        HBox mainView = new HBox(posterPane, infoBox);
        mainView.setAlignment(Pos.CENTER_LEFT);
        mainView.setPrefSize(740, 420);

        // ── EPISODES LIST VIEW ───────────────────────────────────────
        VBox episodesView = new VBox(0);
        episodesView.setPrefSize(740, 420);
        episodesView.setStyle("-fx-background-color: #07090f;");

        // Header
        Button backToMain = new Button("←");
        backToMain.setStyle(
            "-fx-background-color: rgba(56,189,248,0.1); -fx-background-radius: 50%;" +
            "-fx-border-color: rgba(56,189,248,0.35); -fx-border-radius: 50%; -fx-border-width: 1;" +
            "-fx-text-fill: #38bdf8; -fx-font-size: 18px; -fx-font-weight: bold;" +
            "-fx-padding: 4 10; -fx-cursor: hand;"
        );
        addHoverAnimation(backToMain);

        Label epHeaderTitle = new Label("Season " + s.getSeasonNum() + " — Episodes");
        epHeaderTitle.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");

        Label epHeaderCount = new Label(epCount + " episodes");
        epHeaderCount.setStyle("-fx-text-fill: #38bdf8; -fx-font-size: 12px;");

        Region epHeaderSpacer = new Region();
        HBox.setHgrow(epHeaderSpacer, Priority.ALWAYS);

        HBox epHeader = new HBox(12, backToMain, epHeaderTitle, epHeaderSpacer, epHeaderCount);
        epHeader.setAlignment(Pos.CENTER_LEFT);
        epHeader.setPadding(new Insets(16, 24, 12, 24));
        epHeader.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #0d1117, transparent);" +
            "-fx-border-color: transparent transparent rgba(56,189,248,0.18) transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        // Episode rows container
        VBox episodesList = new VBox(6);
        episodesList.setPadding(new Insets(10, 8, 16, 16));

        // ── ScrollPane — built-in bars hidden, wheel handled manually ──
        ScrollPane episodesScroll = new ScrollPane(episodesList);
        episodesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        episodesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        episodesScroll.setFitToWidth(true);
        episodesScroll.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;" +
            "-fx-padding: 0;"
        );
        VBox.setVgrow(episodesScroll, Priority.ALWAYS);
        episodesScroll.setOnScroll(e ->
            episodesScroll.setVvalue(
                Math.max(0, Math.min(1,
                    episodesScroll.getVvalue() - e.getDeltaY() * 0.003))
            )
        );

        // ── Custom slim scrollbar ──────────────────────────────────────
        ScrollBar customScrollBar = new ScrollBar();
        customScrollBar.setOrientation(Orientation.VERTICAL);
        customScrollBar.setMin(0);
        customScrollBar.setMax(1);
        customScrollBar.setValue(0);
        customScrollBar.setUnitIncrement(0.05);
        customScrollBar.setBlockIncrement(0.2);

        // Lock width to exactly 6px
        customScrollBar.setPrefWidth(6);
        customScrollBar.setMinWidth(6);
        customScrollBar.setMaxWidth(6);

        

        // Load scrollbar.css so .thumb gets the blue-black pill look
        customScrollBar.getStylesheets().add(
            getClass().getResource("/view/css/scrollbar.css").toExternalForm()
        );

        // Two-way bind: dragging thumb ↔ scrolling pane
        customScrollBar.valueProperty().bindBidirectional(episodesScroll.vvalueProperty());

        // Adjust thumb size to reflect visible portion of content
        episodesScroll.viewportBoundsProperty().addListener((o, ov, nv) ->
            updateThumbSize(episodesScroll, customScrollBar));

        // Style the thumb directly after CSS is applied
        Platform.runLater(() -> {
            Node thumb = customScrollBar.lookup(".thumb");
            if (thumb != null) thumb.setStyle(
                "-fx-background-color: #1e3a5f;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: transparent;"
            );
            Node track = customScrollBar.lookup(".track");
            if (track != null) track.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;"
            );
            // Hide arrow buttons
            for (String sel : new String[]{".increment-button", ".decrement-button"}) {
                Node btn = customScrollBar.lookup(sel);
                if (btn != null) btn.setStyle(
                    "-fx-pref-width: 0; -fx-pref-height: 0;" +
                    "-fx-min-width: 0; -fx-min-height: 0;" +
                    "-fx-max-width: 0; -fx-max-height: 0;" +
                    "-fx-background-color: transparent;"
                );
            }
            // Hover: brighten thumb to accent blue
            if (thumb != null) {
                thumb.setOnMouseEntered(ev -> thumb.setStyle(
                    "-fx-background-color: #4a90d9;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: transparent;"
                ));
                thumb.setOnMouseExited(ev -> thumb.setStyle(
                    "-fx-background-color: #1e3a5f;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: transparent;"
                ));
            }
        });

        // ── Episode detail pane ───────────────────────────────────────
        int userId = Session.getUserId();
        Map<Integer, WatchStatus> progressMap = episodeProgressService.loadUserProgress(userId);

        StackPane episodeDetailPane = new StackPane();
        episodeDetailPane.setPrefSize(740, 420);
        episodeDetailPane.setStyle("-fx-background-color: #07090f;");

        for (Episode ep : s.getEpisodes()) {
            HBox row = buildEpisodeRow(
                ep, s, progressMap, contentWrapper,
                episodeDetailPane, episodesView, popup, userId
            );
            episodesList.getChildren().add(row);
        }

        // scrollRow: ScrollPane fills width, slim bar on the right edge
        HBox scrollRow = new HBox(0, episodesScroll, customScrollBar);
        HBox.setHgrow(episodesScroll, Priority.ALWAYS);
        VBox.setVgrow(scrollRow, Priority.ALWAYS);

        episodesView.getChildren().addAll(epHeader, scrollRow);

        // ── WIRE ACTIONS ─────────────────────────────────────────────
        trailerBtn.setOnAction(e  -> showTrailerPopup(serie, seasonIndex));
        episodesBtn.setOnAction(e -> switchView(contentWrapper, episodesView));
        backToMain.setOnAction(e  -> switchView(contentWrapper, mainView));

        contentWrapper.getChildren().setAll(mainView);
        return card;
    }
    // ═══════════════════════ EPISODE ROW ═══════════════════════
    private HBox buildEpisodeRow(
            Episode ep, Season s,
            Map<Integer, WatchStatus> progressMap,
            StackPane contentWrapper, StackPane detailPane,
            VBox episodesView, Stage popup, int userId) {

        WatchStatus status = progressMap.getOrDefault(ep.getEpId(), WatchStatus.NOT_STARTED);

        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));

        String baseStyle  = "-fx-background-color: rgba(15,20,32,0.6); -fx-background-radius: 10; -fx-border-color: rgba(56,189,248,0.07); -fx-border-width: 1; -fx-border-radius: 10; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: rgba(56,189,248,0.08); -fx-background-radius: 10; -fx-border-color: rgba(56,189,248,0.25); -fx-border-width: 1; -fx-border-radius: 10; -fx-cursor: hand;";
        row.setStyle(baseStyle);
        row.setOnMouseEntered(e -> row.setStyle(hoverStyle));
        row.setOnMouseExited (e -> row.setStyle(baseStyle));

        Label numBadge = new Label(String.format("%02d", ep.getNumEpisode()));
        numBadge.setPrefSize(34, 34); numBadge.setMinSize(34, 34);
        numBadge.setAlignment(Pos.CENTER);
        numBadge.setStyle(
            "-fx-background-color: rgba(56,189,248,0.12); -fx-background-radius: 17;" +
            "-fx-border-color: rgba(56,189,248,0.3); -fx-border-radius: 17; -fx-border-width: 1;" +
            "-fx-text-fill: #38bdf8; -fx-font-size: 12px; -fx-font-weight: bold;"
        );

        VBox textCol = new VBox(4);
        HBox.setHgrow(textCol, Priority.ALWAYS);
        Label epTitleLbl = new Label(ep.getTitle());
        epTitleLbl.setStyle("-fx-text-fill: rgba(226,232,240,0.95); -fx-font-size: 14px; -fx-font-weight: bold;");
        HBox miniStars = new HBox(2);
        int rating = (int) Math.round(ep.getRating());
        for (int i = 0; i < 5; i++) {
            Label st = new Label("★");
            st.setStyle("-fx-font-size: 10px; -fx-text-fill: " + (i < rating ? "#38bdf8" : "rgba(255,255,255,0.12)") + ";");
            miniStars.getChildren().add(st);
        }
        textCol.getChildren().addAll(epTitleLbl, miniStars);

        int h = ep.getDuration() / 60, m = ep.getDuration() % 60;
        Label duration = new Label((h > 0 ? h + "h " : "") + m + "m");
        duration.setStyle("-fx-text-fill: rgba(148,163,184,0.6); -fx-font-size: 12px;");

        String pillText   = switch (status) { case COMPLETED -> "✓ Watched"; case IN_PROGRESS -> "▶ In Progress"; default -> "Not Started"; };
        String pillBg     = switch (status) { case COMPLETED -> "rgba(34,197,94,0.15)";  case IN_PROGRESS -> "rgba(56,189,248,0.15)";  default -> "rgba(100,116,139,0.12)"; };
        String pillBorder = switch (status) { case COMPLETED -> "rgba(34,197,94,0.5)";   case IN_PROGRESS -> "rgba(56,189,248,0.45)";  default -> "rgba(100,116,139,0.25)"; };
        String pillFg     = switch (status) { case COMPLETED -> "#4ade80"; case IN_PROGRESS -> "#38bdf8"; default -> "#64748b"; };

        Label statusPill = new Label(pillText);
        statusPill.setStyle(
            "-fx-background-color: " + pillBg + "; -fx-border-color: " + pillBorder + ";" +
            "-fx-border-width: 1; -fx-border-radius: 20; -fx-background-radius: 20;" +
            "-fx-text-fill: " + pillFg + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 9;"
        );
        Label arrow = new Label("›");
        arrow.setStyle("-fx-text-fill: rgba(56,189,248,0.4); -fx-font-size: 20px;");

        row.getChildren().addAll(numBadge, textCol, duration, statusPill, arrow);
        row.setOnMouseClicked(ev -> {
            populateEpisodeDetail(detailPane, ep, s, contentWrapper, episodesView, popup, userId);
            switchView(contentWrapper, detailPane);
        });
        return row;
    }

    // ═══════════════════════ EPISODE DETAIL ═══════════════════════
    private void populateEpisodeDetail(
            StackPane detailPane, Episode ep, Season s,
            StackPane contentWrapper, VBox episodesView,
            Stage popup, int userId) {

        detailPane.getChildren().clear();

        ImageView cover = new ImageView();
        cover.setFitWidth(740); cover.setFitHeight(280); cover.setPreserveRatio(false);
        try { cover.setImage(new Image(ep.getCovertUrl(), true)); } catch (Exception ignored) {}

        Region coverGradient = new Region();
        coverGradient.setPrefSize(740, 280);
        coverGradient.setStyle(
            "-fx-background-color: linear-gradient(to bottom, rgba(7,9,15,0) 0%, rgba(7,9,15,0.6) 55%, rgba(7,9,15,1.0) 100%);"
        );

        StackPane coverPane = new StackPane(cover, coverGradient);
        coverPane.setMaxSize(740, 280);
        StackPane.setAlignment(coverPane, Pos.TOP_CENTER);
        coverPane.setMouseTransparent(true);

        Button play = new Button("▶");
        play.setPrefSize(64, 64);
        play.setStyle(
            "-fx-background-color: rgba(56,189,248,0.18); -fx-background-radius: 32;" +
            "-fx-border-color: #38bdf8; -fx-border-radius: 32; -fx-border-width: 2;" +
            "-fx-text-fill: white; -fx-font-size: 22px; -fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(56,189,248,0.55), 22, 0.4, 0, 0);"
        );

        ScaleTransition pulse = new ScaleTransition(Duration.millis(900), play);
        pulse.setFromX(1.0); pulse.setFromY(1.0);
        pulse.setToX(1.12);  pulse.setToY(1.12);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        play.setOnMouseEntered(e -> { pulse.stop(); play.setScaleX(1.18); play.setScaleY(1.18); });
        play.setOnMouseExited (e -> { play.setScaleX(1.0); play.setScaleY(1.0); pulse.play(); });

        StackPane.setAlignment(play, Pos.CENTER);
        StackPane.setMargin(play, new Insets(0, 0, 160, 0));

        VBox infoPanel = new VBox(8);
        infoPanel.setPadding(new Insets(0, 28, 20, 28));
        infoPanel.setAlignment(Pos.TOP_LEFT);
        StackPane.setAlignment(infoPanel, Pos.BOTTOM_CENTER);

        Button backBtn = new Button("← Episodes");
        backBtn.setStyle("-fx-background-color: rgba(56,189,248,0.1); -fx-border-color: rgba(56,189,248,0.35); -fx-text-fill: #38bdf8;");
        addHoverAnimation(backBtn);

        Label epNum   = new Label("EPISODE " + ep.getNumEpisode());
        Label epTitle = new Label(ep.getTitle());
        int hh = ep.getDuration() / 60, mm = ep.getDuration() % 60;
        Label metaLabel = new Label(hh + "h " + mm + "m");
        Label synopsis  = new Label(ep.getResume() != null ? ep.getResume() : "No synopsis available.");
        synopsis.setWrapText(true);
        infoPanel.getChildren().addAll(backBtn, epNum, epTitle, metaLabel, synopsis);

        play.setOnAction(e -> {
            try {
                if (popup != null) popup.close();
                goToLecturePageEpisode(s.getSerieId(), s.getSeasonNum(), ep.getEpId());
            } catch (Exception ex) { ex.printStackTrace(); }
        });
        backBtn.setOnAction(e -> switchView(contentWrapper, episodesView));

        detailPane.getChildren().addAll(coverPane, infoPanel, play);
        play.toFront();
    }

    // ═══════════════════════ VIEW SWITCHER ═══════════════════════
    private void switchView(StackPane wrapper, Node target) {
        if (!wrapper.getChildren().isEmpty()) {
            Node current = wrapper.getChildren().get(0);
            if (current == target) return;
            FadeTransition out = new FadeTransition(Duration.millis(160), current);
            out.setToValue(0);
            out.setOnFinished(e -> {
                wrapper.getChildren().setAll(target);
                target.setOpacity(0);
                FadeTransition in = new FadeTransition(Duration.millis(200), target);
                in.setToValue(1);
                in.play();
            });
            out.play();
        } else {
            wrapper.getChildren().setAll(target);
        }
    }

    private void styleCardButton(Button btn, boolean primary) {
        if (primary) {
            btn.setStyle(
                "-fx-background-color: #38bdf8; -fx-text-fill: #07090f; -fx-font-weight: bold;" +
                "-fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(56,189,248,0.45), 16, 0.3, 0, 0);"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: rgba(56,189,248,0.08); -fx-text-fill: #38bdf8;" +
                "-fx-font-size: 13px; -fx-background-radius: 8; -fx-border-color: rgba(56,189,248,0.35);" +
                "-fx-border-width: 1; -fx-border-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;"
            );
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TRAILER POPUP
    // ─────────────────────────────────────────────────────────────────────────
	private void showTrailerPopup(Object item, int seasonIndex) {
        String trailerUrl = null;

        if (item instanceof Film) {
            trailerUrl = ((Film) item).getTrailer_url();
        } else if (item instanceof Serie) {
            Serie serie = (Serie) item;
            if (serie.getSeasons() != null && !serie.getSeasons().isEmpty()) {
                if (seasonIndex < 0 || seasonIndex >= serie.getSeasons().size()) seasonIndex = 0;
                trailerUrl = serie.getSeasons().get(seasonIndex).getTrailerUrl();
            }
        }

        if (trailerUrl == null) return;

        URL videoUrl = getClass().getResource(trailerUrl);
        if (videoUrl == null) { System.out.println("Video file not found: " + trailerUrl); return; }
        String videoPath = trailerUrl.startsWith("http") ? trailerUrl : videoUrl.toExternalForm();

        // ── Modern HTML5 player with custom controls ──────────────────
        String html =
        		"<!DOCTYPE html><html><head><style>" +

        		"  * { margin:0; padding:0; box-sizing:border-box; }" +

        		"  body { background:#000; overflow:hidden; display:flex; flex-direction:column;" +
        		"         width:100vw; height:100vh; font-family:'Segoe UI',sans-serif; }" +

        		"  video { flex:1; width:100%; min-height:0; object-fit:contain; display:block; cursor:pointer; }" +

        		/* ================= CONTROLS BAR ================= */
        		"  #bar {" +
        		"    background: linear-gradient(to top, rgba(0,0,0,0.98), rgba(0,20,40,0.85));" +
        		"    padding: 6px 18px 10px;" +
        		"    display:flex; flex-direction:column; gap:7px;" +
        		"  }" +

        		/* ================= PROGRESS BAR ================= */
        		"  #prog-wrap {" +
        		"    position:relative; height:4px; background:rgba(255,255,255,0.08);" +
        		"    border-radius:4px; cursor:pointer; transition:height 0.15s;" +
        		"  }" +

        		"  #prog-wrap:hover { height:6px; }" +

        		"  #prog-buf { position:absolute; height:100%; border-radius:4px;" +
        		"    background:rgba(0,140,255,0.2); width:0%; pointer-events:none; }" +

        		"  #prog-fill { position:absolute; height:100%; border-radius:4px;" +
        		"    background:linear-gradient(to right,#002b55,#00aaff); width:0%; pointer-events:none; }" +

        		"  #prog-thumb { position:absolute; top:50%; width:13px; height:13px;" +
        		"    background:#00aaff; border-radius:50%; transform:translate(-50%,-50%);" +
        		"    box-shadow:0 0 10px rgba(0,170,255,0.9); left:0%;" +
        		"    opacity:0; transition:opacity 0.15s; pointer-events:none; }" +

        		"  #prog-wrap:hover #prog-thumb { opacity:1; }" +

        		/* ================= ROW ================= */
        		"  #row { display:flex; align-items:center; gap:10px; }" +

        		"  .btn {" +
        		"    background:rgba(0,0,0,0.6);" +
        		"    border:none;" +
        		"    cursor:pointer;" +
        		"    color:#00aaff;" +
        		"    font-size:13px;" +
        		"    border-radius:7px;" +
        		"    padding:4px 9px;" +
        		"    transition:0.2s;" +
        		"    display:flex; align-items:center; justify-content:center;" +
        		"    min-width:32px; height:30px;" +
        		"  }" +

        		"  .btn:hover { background:rgba(0,140,255,0.25); color:#ffffff; }" +
        		"  .btn:active { background:rgba(0,100,180,0.4); }" +

        		/* ================= TIME ================= */
        		"  #time { font-size:11px; color:rgba(180,200,255,0.75); min-width:105px; letter-spacing:0.3px; }" +

        		/* ================= VOLUME ================= */
        		"  #vol-wrap { display:flex; align-items:center; gap:6px; }" +

        		"  #vol { -webkit-appearance:none; width:72px; height:3px;" +
        		"    background:rgba(255,255,255,0.15); border-radius:3px; outline:none; cursor:pointer; }" +

        		"  #vol::-webkit-slider-thumb { -webkit-appearance:none; width:12px; height:12px;" +
        		"    background:#00aaff; border-radius:50%; box-shadow:0 0 6px rgba(0,170,255,0.7); }" +

        		"  #spacer { flex:1; }" +

        		/* ================= SPEED ================= */
        		"  #speed { background:rgba(0,0,0,0.7); border:none;" +
        		"    color:#00aaff; font-size:11px; padding:4px 7px; border-radius:6px;" +
        		"    cursor:pointer; outline:none; }" +

        		"  #speed:hover { background:rgba(0,140,255,0.2); }" +

        		/* ================= TOOLTIP ================= */
        		"  #tip { position:fixed; bottom:72px; left:50%; transform:translateX(-50%);" +
        		"    background:rgba(0,0,0,0.85); border:none;" +
        		"    color:#00aaff; font-size:11px; padding:4px 14px; border-radius:20px;" +
        		"    opacity:0; transition:opacity 0.25s; pointer-events:none; }" +

        		"  ::-webkit-scrollbar { display:none; }" +

        		"</style></head><body>" +

        		"<video id='v'><source src='" + videoPath + "' type='video/mp4'></video>" +

        		"<div id='bar'>" +
        		"  <div id='prog-wrap' onmousedown='seekStart(event)' onmousemove='seekHover(event)'>" +
        		"    <div id='prog-buf'></div>" +
        		"    <div id='prog-fill'></div>" +
        		"    <div id='prog-thumb'></div>" +
        		"  </div>" +

        		"  <div id='row'>" +
        		"    <button class='btn' onclick='togglePlay()' id='playBtn'>&#9654;</button>" +
        		"    <button class='btn' onclick='skip(-10)'>&#9198; 10</button>" +
        		"    <button class='btn' onclick='skip(10)'>10 &#9197;</button>" +

        		"    <div id='vol-wrap'>" +
        		"      <button class='btn' onclick='toggleMute()' id='muteBtn'>&#128266;</button>" +
        		"      <input id='vol' type='range' min='0' max='1' step='0.02' value='1' oninput='setVol(this.value)'>" +
        		"    </div>" +

        		"    <span id='time'>0:00 / 0:00</span>" +

        		"    <div id='spacer'></div>" +

        		"    <select id='speed' onchange='setSpeed(this.value)'>" +
        		"      <option value='0.25'>0.25×</option>" +
        		"      <option value='0.5'>0.5×</option>" +
        		"      <option value='0.75'>0.75×</option>" +
        		"      <option value='1' selected>1×</option>" +
        		"      <option value='1.25'>1.25×</option>" +
        		"      <option value='1.5'>1.5×</option>" +
        		"      <option value='2'>2×</option>" +
        		"    </select>" +
        		"  </div>" +
        		"</div>" +

        		"<div id='tip'></div>" +

        		"<script>" +
        		"var v=document.getElementById('v')," +
        		"pFill=document.getElementById('prog-fill')," +
        		"pBuf=document.getElementById('prog-buf')," +
        		"pThumb=document.getElementById('prog-thumb')," +
        		"playBtn=document.getElementById('playBtn')," +
        		"muteBtn=document.getElementById('muteBtn')," +
        		"timeEl=document.getElementById('time')," +
        		"tip=document.getElementById('tip')," +
        		"seeking=false, tipTimer;" +

        		"v.addEventListener('timeupdate',function(){" +
        		"  if(!v.duration||seeking)return;" +
        		"  var p=(v.currentTime/v.duration*100).toFixed(2)+'%';" +
        		"  pFill.style.width=p; pThumb.style.left=p;" +
        		"  timeEl.textContent=fmt(v.currentTime)+' / '+fmt(v.duration);" +
        		"});" +

        		"v.addEventListener('progress',function(){" +
        		"  if(!v.duration||!v.buffered.length)return;" +
        		"  pBuf.style.width=(v.buffered.end(v.buffered.length-1)/v.duration*100)+'%';" +
        		"});" +

        		"v.addEventListener('play',function(){playBtn.textContent='⏸';});" +
        		"v.addEventListener('pause',function(){playBtn.textContent='▶';});" +
        		"v.addEventListener('ended',function(){playBtn.textContent='↺';});" +
        		"v.addEventListener('click',togglePlay);" +

        		"function togglePlay(){if(v.ended){v.currentTime=0;v.play();}else if(v.paused)v.play();else v.pause();}" +
        		"function skip(s){v.currentTime=Math.max(0,Math.min(v.duration||0,v.currentTime+s));toast((s>0?'+':'')+s+'s');}" +

        		"function seekAt(e){" +
        		"var r=document.getElementById('prog-wrap').getBoundingClientRect();" +
        		"var ratio=Math.max(0,Math.min(1,(e.clientX-r.left)/r.width));" +
        		"v.currentTime=ratio*(v.duration||0);" +
        		"}" +

        		"function seekStart(e){" +
        		"seeking=true;seekAt(e);" +
        		"document.addEventListener('mousemove',seekAt);" +
        		"document.addEventListener('mouseup',function up(){" +
        		"seeking=false;" +
        		"document.removeEventListener('mousemove',seekAt);" +
        		"document.removeEventListener('mouseup',up);" +
        		"});" +
        		"}" +

        		"function seekHover(e){" +
        		"var r=document.getElementById('prog-wrap').getBoundingClientRect();" +
        		"var t=Math.max(0,(e.clientX-r.left)/r.width)*v.duration;" +
        		"tip.textContent=fmt(t);" +
        		"tip.style.opacity='1';tip.style.left=e.clientX+'px';" +
        		"}" +

        		"document.getElementById('prog-wrap').addEventListener('mouseleave',function(){tip.style.opacity='0';});" +

        		"function setVol(val){v.volume=parseFloat(val);v.muted=val==0;}" +
        		"function toggleMute(){v.muted=!v.muted;document.getElementById('vol').value=v.muted?0:v.volume;}" +
        		"function setSpeed(val){v.playbackRate=parseFloat(val);}" +

        		"function fmt(s){var h=Math.floor(s/3600),m=Math.floor((s%3600)/60),ss=Math.floor(s%60);" +
        		"return (h>0?h+':':'')+(h>0&&m<10?'0':'')+m+':'+(ss<10?'0':'')+ss;}" +

        		"document.addEventListener('keydown',function(e){" +
        		"if(e.code==='Space'){e.preventDefault();togglePlay();}" +
        		"if(e.code==='ArrowRight')skip(5);" +
        		"if(e.code==='ArrowLeft')skip(-5);" +
        		"});" +

        		"v.play().catch(function(){});" +
        		"</script></body></html>";

        WebView webView = new WebView();
        webView.setPrefSize(1500, 700);
        webView.getEngine().loadContent(html);

        // ── Stage ────────────────────────────────────────────────────
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double fullWidth  = screenBounds.getWidth();
        double fullHeight = screenBounds.getHeight();
        double smallWidth = 1200, smallHeight = 660;

        Stage popup = new Stage();
        popup.initOwner(rootPane.getScene().getWindow());
        popup.initModality(Modality.WINDOW_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.setWidth(fullWidth); popup.setHeight(fullHeight);
        popup.setX(0); popup.setY(0);

        // ── Backdrop ─────────────────────────────────────────────────
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.88);");

        // ── Card ─────────────────────────────────────────────────────
        VBox card = new VBox(0);
        card.setMaxSize(fullWidth - 40, fullHeight - 40);
        card.setPrefSize(fullWidth - 40, fullHeight - 40);
        card.setStyle(
            "-fx-background-color: #07090f;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: rgba(56,189,248,0.18);" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 14;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.95),60,0.7,0,10);"
        );

        Rectangle cardClip = new Rectangle();
        cardClip.setArcWidth(28); cardClip.setArcHeight(28);
        card.layoutBoundsProperty().addListener((o, ov, nv) -> {
            cardClip.setWidth(nv.getWidth());
            cardClip.setHeight(nv.getHeight());
        });
        card.setClip(cardClip);

        // ── Card top bar ─────────────────────────────────────────────
        HBox cardBar = new HBox(8);
        cardBar.setAlignment(Pos.CENTER_RIGHT);
        cardBar.setPadding(new Insets(9, 12, 9, 16));
        cardBar.setMinHeight(42); cardBar.setMaxHeight(42);
        cardBar.setStyle(
            "-fx-background-color: #0a0e1a;" +
            "-fx-border-color: transparent transparent rgba(56,189,248,0.1) transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Region d1 = minidot("#1e3a5f"), d2 = minidot("#2c5282"), d3 = minidot("#4a90d9");
        Region barSpacer = new Region(); HBox.setHgrow(barSpacer, Priority.ALWAYS);

        Button btnResize = popupCtrlBtn("⊡", false);
        Button btnClose  = popupCtrlBtn("✕", true);
        btnClose.setOnAction(e -> popup.close());

        final boolean[] isSmall = {false};
        btnResize.setOnAction(e -> {
            if (!isSmall[0]) {
                card.setMaxSize(smallWidth, smallHeight);
                card.setPrefSize(smallWidth, smallHeight);
                isSmall[0] = true; btnResize.setText("⊞");
            } else {
                card.setMaxSize(fullWidth - 40, fullHeight - 40);
                card.setPrefSize(fullWidth - 40, fullHeight - 40);
                isSmall[0] = false; btnResize.setText("⊡");
            }
        });

        cardBar.getChildren().addAll(d1, d2, d3, barSpacer, btnResize, btnClose);
        VBox.setVgrow(webView, Priority.ALWAYS);
        card.getChildren().addAll(cardBar, webView);

        root.setOnMouseClicked(e -> { if (e.getTarget() == root) popup.close(); });
        root.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        java.net.URL cssUrl = getClass().getResource("/style/scrollbar.css");
        if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

        popup.setScene(scene);
        popup.setAlwaysOnTop(true);
        popup.toFront();
        popup.requestFocus();
        popup.setOnHidden(e -> {
            webView.getEngine().load(null);
            if (autoSlide != null) autoSlide.play();
        });
        popup.showAndWait();
}

    private Button popupCtrlBtn(String symbol, boolean isClose) {
        Button btn = new Button(symbol);
        btn.setPrefSize(30, 30); btn.setMinSize(30, 30); btn.setMaxSize(30, 30);
        btn.setCursor(Cursor.HAND);
        String base = "-fx-background-radius:7;-fx-border-radius:7;-fx-border-width:1;-fx-font-size:12;-fx-padding:0;";
        String normal  = base + "-fx-background-color:#0d1829;-fx-border-color:#1e3a5f;-fx-text-fill:#4a90d9;";
        String hover   = base + (isClose
            ? "-fx-background-color:#c0392b;-fx-border-color:#e74c3c;-fx-text-fill:white;"
            : "-fx-background-color:#162238;-fx-border-color:#2c5282;-fx-text-fill:#7eb8f7;");
        String pressed = base + (isClose
            ? "-fx-background-color:#922b21;-fx-border-color:#c0392b;-fx-text-fill:white;"
            : "-fx-background-color:#0a1520;-fx-border-color:#1e3a5f;-fx-text-fill:#4a90d9;");
        btn.setStyle(normal);
        btn.setOnMouseEntered(e  -> btn.setStyle(hover));
        btn.setOnMouseExited(e   -> btn.setStyle(normal));
        btn.setOnMousePressed(e  -> btn.setStyle(pressed));
        btn.setOnMouseReleased(e -> btn.setStyle(hover));
        return btn;
    }

    private Region minidot(String color) {
        Region d = new Region();
        d.setPrefSize(9, 9); d.setMinSize(9, 9); d.setMaxSize(9, 9);
        d.setStyle("-fx-background-color:" + color + ";-fx-background-radius:5;");
        HBox.setMargin(d, new Insets(0, 1, 0, 0));
        return d;
    }
    // ─────────────────────────────────────────────────────────────────────────
    // LECTURE PAGE NAVIGATION
    // ─────────────────────────────────────────────────────────────────────────
    private void goToLecturePageFilm(int filmId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/fxml/LecturePage.fxml"));
            Parent root = loader.load();
            ((LecturePageController) loader.getController()).initFilm(filmId);
            ((Stage) rootPane.getScene().getWindow()).getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void goToLecturePageEpisode(int serieId, int seasonNum, int episodeNum) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/fxml/LecturePage.fxml"));
            Parent root = loader.load();
            ((LecturePageController) loader.getController()).initEpisode(serieId, seasonNum, episodeNum);
            ((Stage) rootPane.getScene().getWindow()).getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEASON SLIDER HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private void updateSeasonSlider(List<StackPane> cards, int currentIndex) {
        for (int i = 0; i < cards.size(); i++) {
            int offset = i - currentIndex;
            double scale   = offset == 0 ? 1.1 : Math.abs(offset) == 1 ? 0.95 : Math.abs(offset) == 2 ? 0.9 : 0.85;
            double opacity = offset == 0 ? 1.0 : Math.abs(offset) == 1 ? 0.85 : Math.abs(offset) == 2 ? 0.6 : 0.4;
            StackPane card = cards.get(i);
            new Timeline(new KeyFrame(Duration.millis(400),
                new KeyValue(card.scaleXProperty(),  scale,   Interpolator.EASE_BOTH),
                new KeyValue(card.scaleYProperty(),  scale,   Interpolator.EASE_BOTH),
                new KeyValue(card.opacityProperty(), opacity, Interpolator.EASE_BOTH)
            )).play();
        }
    }

    private void centerSlide(ScrollPane scrollPane, StackPane card, HBox slider) {
        double scrollWidth     = slider.getWidth();
        double scrollPaneWidth = scrollPane.getViewportBounds().getWidth();
        double cardCenter      = card.getBoundsInParent().getMinX() + card.getBoundsInParent().getWidth() / 2.0;
        double hValue = Math.min(Math.max((cardCenter - scrollPaneWidth / 2) / (scrollWidth - scrollPaneWidth), 0), 1);
        new Timeline(new KeyFrame(Duration.millis(400),
            new KeyValue(scrollPane.hvalueProperty(), hValue, Interpolator.EASE_BOTH)
        )).play();
    }

    private void styleSlideButton(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #00aaff; -fx-font-size: 36;" +
            "-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,255,255,0.7), 10,0,0,0);"
        );
        btn.setOpacity(0);
    }

    private void fadeButton(Button btn, double targetOpacity, double durationMs) {
        new Timeline(new KeyFrame(Duration.millis(durationMs),
            new KeyValue(btn.opacityProperty(), targetOpacity, Interpolator.EASE_BOTH)
        )).play();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SCROLLBAR HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private void updateThumbSize(ScrollPane scrollPane, ScrollBar scrollBar) {
        Node content = scrollPane.getContent();
        if (content == null) return;
        content.layoutBoundsProperty().addListener((obs, o, n) -> {
            double ratio = Math.min(Math.max(scrollPane.getViewportBounds().getHeight() / n.getHeight(), 0.05), 1.0);
            scrollBar.setVisibleAmount(ratio);
        });
        scrollPane.viewportBoundsProperty().addListener((obs, o, n) -> {
            double contentH = content.getLayoutBounds().getHeight();
            if (contentH <= 0) return;
            scrollBar.setVisibleAmount(Math.min(Math.max(n.getHeight() / contentH, 0.05), 1.0));
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STYLE HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private void addHoverAnimation(Button btn) {
        ScaleTransition up   = new ScaleTransition(Duration.millis(120), btn); up.setToX(1.15);  up.setToY(1.15);
        ScaleTransition down = new ScaleTransition(Duration.millis(120), btn); down.setToX(1.0); down.setToY(1.0);
        btn.setOnMouseEntered(e -> up.playFromStart());
        btn.setOnMouseExited (e -> down.playFromStart());
    }

    public void showPopup(FeaturedItem item) {
        if (item.getType().equalsIgnoreCase("film"))       showFilmPopup(item);
        else if (item.getType().equalsIgnoreCase("serie")) showSeriePopup(item);
    }
}