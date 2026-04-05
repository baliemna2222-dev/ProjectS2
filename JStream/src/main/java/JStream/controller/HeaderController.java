package JStream.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.FeaturedItemProgress;
import JStream.entity.Film;
import JStream.entity.MyListManager;
import JStream.entity.Season;
import JStream.entity.Serie;
import JStream.entity.Session;
import JStream.entity.UsernameChangeNotifier;
import JStream.entity.WatchStatus;
import JStream.service.EpisodeProgressService;
import JStream.service.FeaturedService;
import JStream.service.FilmProgressService;
import JStream.service.MylistService;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import javafx.util.Duration;

public class HeaderController {

    // ── FXML FIELDS ──────────────────────────────────────────────────────────
    @FXML private HBox rootPane;
    @FXML private ImageView logoImage;

    // Nav buttons & underlines
    @FXML private Button btnHome, btnMovies, btnSeries, btnMyList;
    @FXML private Rectangle lineHome, lineMovies, lineSeries, lineMyList;

    // Search
    @FXML private StackPane searchStack;
    @FXML private TextField searchInput;
    @FXML private Button clearBtn, btnResearch;

    // Bell
    @FXML private StackPane bellContainer;
    @FXML private ImageView bellIcon;

    // Profile
    @FXML private Button profile;

    // ── PRIVATE STATE ────────────────────────────────────────────────────────
    private Button    activeButton;
    private Rectangle activeLine;
    private Timeline  autoSlide;

    private boolean ignoreTextChange = false;
    private boolean searchOpened     = false;
    private boolean isNotificationVisible = false;

    private Circle notificationDot;
    private Popup  profilePopup;
    private final Popup  suggestionsPopup   = new Popup();
    private final VBox   suggestionsContent = new VBox();

    private static String lastActive = "HOME";

    // ── SERVICES ─────────────────────────────────────────────────────────────
    private final FeaturedService        featuredService        = new FeaturedService();
    private final EpisodeProgressService episodeProgressService = new EpisodeProgressService();
    private final FilmProgressService    filmProgressService    = new FilmProgressService(featuredService);
    private final UserService            userService            = new UserService();

    // ─────────────────────────────────────────────────────────────────────────
    // INITIALIZE
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    private void initialize() {
        logoImage.setImage(new Image(getClass().getResourceAsStream("/assets/images/logo/Raksha.png")));

        loadUserProfile();
        setupProfilePopup();          // build popup once
        setupProfileButtonAction();   // wire the header button
        setupBellNotification();
        setupSearchBar();
        setupSearchSuggestions();
        setupNavButtons();

        // Restore last active tab
        Platform.runLater(() -> {
            switch (lastActive) {
                case "Movies"  -> activate(btnMovies, lineMovies);
                case "Series"  -> activate(btnSeries, lineSeries);
                case "My List" -> activate(btnMyList, lineMyList);
                default        -> activate(btnHome,   lineHome);
            }
        });
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

        // Single unified text listener
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

        // Poster image (only for search results)
        if (posterUrl != null) {
            ImageView poster = new ImageView();
            poster.setFitWidth(35);
            poster.setFitHeight(50);
            poster.setPreserveRatio(true);
            try { poster.setImage(new Image(posterUrl, true)); } catch (Exception ignored) {}
            box.getChildren().add(poster);
        }

        // Title label with tooltip
        Label lbl = new Label(title);
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
        lbl.setMaxWidth(180);
        lbl.setEllipsisString("...");
        lbl.setWrapText(false);

        Tooltip tooltip = new Tooltip(title);
        tooltip.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-font-size: 13;");
        Tooltip.install(lbl, tooltip);

        box.getChildren().add(lbl);

        // Spacer to push buttons to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        box.getChildren().add(spacer);

        if (isSearchResult) {
            // Add/remove button for search results
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
            // Remove button for latest searches
            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 12;");
            removeBtn.setOnMouseEntered(e -> removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #00bfff; -fx-font-size: 12;"));
            removeBtn.setOnMouseExited (e -> removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 12;"));
            removeBtn.setOnAction(e -> { featuredService.removeLatestSearch(title); showLatestSearches(); });
            box.getChildren().add(removeBtn);
        }

        // Hover effect
        box.setOnMouseEntered(e -> box.setStyle("-fx-background-color: #1e293b; -fx-padding: 8; -fx-background-radius: 6;"));
        box.setOnMouseExited (e -> box.setStyle("-fx-background-color: transparent; -fx-padding: 8; -fx-background-radius: 6;"));

        // Click: fetch FeaturedItem and show details, works for both search results and latest searches
        box.setOnMouseClicked(e -> {
            ignoreTextChange = true;
            searchInput.setText(title);
            ignoreTextChange = false;

            try {
                FeaturedItem item = featuredService.getFeaturedByTitle(title);
                if (item != null) {
                    // Add to latest searches
                    featuredService.addToLatestSearch(title);

                    // Show popup depending on type
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

        notificationDot = new Circle(4, Color.DODGERBLUE);
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
            ScaleTransition bounce = new ScaleTransition(Duration.millis(500), notificationDot);
            bounce.setFromX(0); bounce.setFromY(0);
            bounce.setToX(1);   bounce.setToY(1);
            bounce.setInterpolator(Interpolator.EASE_OUT);
            bounce.play();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NAV BUTTONS
    // ─────────────────────────────────────────────────────────────────────────
    private void setupNavButtons() {
        setupButton(btnHome,    lineHome,    this::goToHomepage);
        setupButton(btnMovies,  lineMovies,  this::goToFilmView);
        setupButton(btnSeries,  lineSeries,  this::goToSeriesView);
        setupButton(btnMyList,  lineMyList,  this::goToMyListView);
    }

    private void activate(Button btn, Rectangle line) {
        activeButton = btn;
        activeLine   = line;
        setButtonActive(btn);
        line.setWidth(btn.getWidth());
    }

    private void setupButton(Button btn, Rectangle line, Runnable action) {
        line.setFill(Color.DODGERBLUE);
        line.setHeight(3);

        btn.setOnMouseEntered(e -> {
            if (btn != activeButton) { setButtonHover(btn); animateLine(line, btn.getWidth()); }
        });
        btn.setOnMouseExited(e -> {
            if (btn != activeButton) { setButtonInactive(btn); animateLine(line, 0); }
        });
        btn.setOnAction(e -> {
            if (activeButton != null && activeButton != btn) {
                setButtonInactive(activeButton);
                animateLine(activeLine, 0);
            }
            activeButton = btn;
            activeLine   = line;
            setButtonActive(btn);
            line.setWidth(btn.getWidth());
            lastActive = btn.getText();
            if (action != null) action.run();
        });
    }

    private void setButtonActive  (Button btn) { btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: white;   -fx-font-size: 16;"); }
    private void setButtonHover   (Button btn) { btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: white;   -fx-font-size: 16;"); }
    private void setButtonInactive(Button btn) { btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #cccccc; -fx-font-size: 16;"); }

    private void animateLine(Rectangle line, double targetWidth) {
        Platform.runLater(() -> new Timeline(
            new KeyFrame(Duration.millis(200), new KeyValue(line.widthProperty(), targetWidth))
        ).play());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NAVIGATION
    // ─────────────────────────────────────────────────────────────────────────
    public void goToHomepage()    { navigateTo("/view/fxml/HomePage.fxml"); }
    public void goToFilmView()    { navigateTo("/view/fxml/FilmView.fxml"); }
    public void goToSeriesView()  { navigateTo("/view/fxml/SeriesView.fxml"); }
    public void goToMyListView()  { navigateTo("/view/fxml/MyList.fxml"); }
    public void goToMyHistoryView() { navigateTo("/view/fxml/MyHistory.fxml"); }

    private void navigateTo(String fxmlPath) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) { System.err.println("FXML not found: " + fxmlPath); return; }
            Parent root = FXMLLoader.load(url);

         // Set root
         btnHome.getScene().setRoot(root);

         // Force scroll to top for ScrollPane or ListView
         Platform.runLater(() -> {
             root.lookupAll(".scroll-pane").forEach(node -> {
                 ((ScrollPane) node).setVvalue(0);  // scroll to top
             });
             root.lookupAll(".list-view").forEach(node -> {
                 ((ListView<?>) node).scrollTo(0);  // scroll to first item
             });
         });
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PROFILE
    // ─────────────────────────────────────────────────────────────────────────
    private void loadUserProfile() {
        String path = userService.getProfilePhoto(Session.getUserId());
        if (path != null && !path.isEmpty()) applyProfileImage(profile, path);
    }

    private void applyProfileImage(Button btn, String imagePath) {
        ImageView view = new ImageView(new Image(imagePath, false));
        view.setFitWidth(45);
        view.setFitHeight(45);
        view.setPreserveRatio(false);
        view.setSmooth(true);
        view.setClip(new Circle(22.5, 22.5, 22.5));
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        btn.setGraphic(view);
        btn.setText("");
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
 // PROFILE POPUP  (replace the entire setupProfilePopup method)
 // ─────────────────────────────────────────────────────────────────────────
 private void setupProfilePopup() {
     profilePopup = new Popup();
     profilePopup.setAutoHide(true);

     // ── Root container ──
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

     // ── TOP BANNER (avatar + name + email) ──
     VBox banner = new VBox(8);
     banner.setPadding(new Insets(20, 20, 16, 20));
     banner.setStyle("-fx-background-color: #161b22; -fx-background-radius: 12 12 0 0;");

     // Avatar with edit overlay
     StackPane avatarStack = new StackPane();
     avatarStack.setPrefSize(72, 72);
     avatarStack.setMaxSize(72, 72);

     String imagePath = userService.getProfilePhoto(Session.getUserId());
     if (imagePath == null || imagePath.isEmpty()) imagePath = "/assets/images/profile.png";

     ImageView popupImg = new ImageView(new Image(imagePath));
     popupImg.setFitWidth(72); popupImg.setFitHeight(72);
     popupImg.setPreserveRatio(false); popupImg.setSmooth(true);
     popupImg.setClip(new Circle(36, 36, 36));

     // Edit overlay (camera icon)
     StackPane editOverlay = new StackPane();
     editOverlay.setPrefSize(72, 72);
     editOverlay.setStyle(
         "-fx-background-color: rgba(0,0,0,0.55);" +
         "-fx-background-radius: 50%;"
     );
     editOverlay.setOpacity(0);
     Label cameraIcon = new Label("✎");
     cameraIcon.setStyle("-fx-text-fill: white; -fx-font-size: 18;");
     editOverlay.getChildren().add(cameraIcon);
     editOverlay.setCursor(javafx.scene.Cursor.HAND);

     avatarStack.getChildren().addAll(popupImg, editOverlay);

     // Hover fade overlay
     FadeTransition fadeIn  = new FadeTransition(Duration.millis(150), editOverlay); fadeIn.setToValue(1);
     FadeTransition fadeOut = new FadeTransition(Duration.millis(150), editOverlay); fadeOut.setToValue(0);
     avatarStack.setOnMouseEntered(e -> fadeIn.playFromStart());
     avatarStack.setOnMouseExited (e -> fadeOut.playFromStart());

     // ── Click: file chooser that stays in-app ──
     avatarStack.setOnMouseClicked(e -> {
         FileChooser fc = new FileChooser();
         fc.setTitle("Choose Profile Picture");
         fc.getExtensionFilters().add(
             new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
         );
         // ✅ Use the popup's own window as owner so it doesn't leave the app
         Window owner = profilePopup.getOwnerWindow();
         File file = fc.showOpenDialog(owner);
         if (file != null) {
             String url = file.toURI().toString();
             Image newImg = new Image(url);
             // Update popup avatar
             ImageView newView = new ImageView(newImg);
             newView.setFitWidth(72); newView.setFitHeight(72);
             newView.setPreserveRatio(false); newView.setSmooth(true);
             newView.setClip(new Circle(36, 36, 36));
             avatarStack.getChildren().set(0, newView);
             // Update header button
             applyProfileImage(profile, url);
             // Save
             userService.updateProfilePhoto(Session.getUserId(), url);
             Session.setProfileImagePath(url);
         }
     });

     // Also apply initial image to header button
     applyProfileImage(profile, imagePath);

     Label usernameLabel = new Label(Session.getUsername());
     usernameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15; -fx-font-weight: bold;");
     UsernameChangeNotifier.addListener(usernameLabel::setText);

     String email = userService.getEmail(Session.getUserId()); // add this method if missing
     Label emailLabel = new Label(email != null ? email : "");
     emailLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12;");

     HBox avatarRow = new HBox(14, avatarStack, new VBox(4, usernameLabel, emailLabel));
     avatarRow.setAlignment(Pos.CENTER_LEFT);
     ((VBox) avatarRow.getChildren().get(1)).setAlignment(Pos.CENTER_LEFT);

     banner.getChildren().add(avatarRow);

     // ── DIVIDER ──
     Region div1 = new Region();
     div1.setPrefHeight(1);
     div1.setStyle("-fx-background-color: #21262d;");

     // ── MENU ITEMS ──
     VBox menu = new VBox(2);
     menu.setPadding(new Insets(8, 8, 8, 8));

     menu.getChildren().addAll(
         makeMenuItem("☰",  "My List",    () -> { profilePopup.hide(); goToMyListView();    }),
         makeMenuItem("⌛",  "My History", () -> { profilePopup.hide(); goToMyHistoryView(); }),
         makeMenuItem("⚙",  "Settings",   () -> showSettingsPopup())
     );

     // ── DIVIDER ──
     Region div2 = new Region();
     div2.setPrefHeight(1);
     div2.setStyle("-fx-background-color: #21262d;");

     // ── LOGOUT ──
     HBox logoutRow = new HBox();
     logoutRow.setPadding(new Insets(8, 8, 8, 8));
     Button btnLogout = new Button("Sign out");
     btnLogout.setMaxWidth(Double.MAX_VALUE);
     btnLogout.setStyle(
         "-fx-background-color: transparent;" +
         "-fx-text-fill: #f85149;" +
         "-fx-font-size: 13;" +
         "-fx-alignment: CENTER_LEFT;" +
         "-fx-padding: 8 12;" +
         "-fx-background-radius: 8;" +
         "-fx-cursor: hand;"
     );
     btnLogout.setOnMouseEntered(e -> btnLogout.setStyle(
         "-fx-background-color: rgba(248,81,73,0.1);" +
         "-fx-text-fill: #f85149; -fx-font-size: 13;" +
         "-fx-alignment: CENTER_LEFT; -fx-padding: 8 12;" +
         "-fx-background-radius: 8; -fx-cursor: hand;"
     ));
     btnLogout.setOnMouseExited(e -> btnLogout.setStyle(
         "-fx-background-color: transparent;" +
         "-fx-text-fill: #f85149; -fx-font-size: 13;" +
         "-fx-alignment: CENTER_LEFT; -fx-padding: 8 12;" +
         "-fx-background-radius: 8; -fx-cursor: hand;"
     ));
     btnLogout.setOnAction(e -> Platform.exit());
     HBox.setHgrow(btnLogout, Priority.ALWAYS);
     logoutRow.getChildren().add(btnLogout);

     root.getChildren().addAll(banner, div1, menu, div2, logoutRow);
     profilePopup.getContent().add(root);
 }

 // Helper: single menu item row
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
 // SETTINGS POPUP  (replace the entire showSettingsPopup method)
 // ─────────────────────────────────────────────────────────────────────────
 private void showSettingsPopup() {
     profilePopup.hide();

     Stage settingsStage = new Stage();
     settingsStage.initOwner(profile.getScene().getWindow());
     settingsStage.initModality(Modality.WINDOW_MODAL);
     settingsStage.initStyle(StageStyle.TRANSPARENT);
     settingsStage.setTitle("Settings");

     // ── Root overlay ──
     StackPane overlay = new StackPane();
     overlay.setStyle("-fx-background-color: rgba(0,0,0,0.65);");
     overlay.setPrefSize(
    		    profile.getScene().getWidth(),
    		    profile.getScene().getHeight()
    		);

     // ── Modal card ──
     VBox card = new VBox(0);
     card.setMaxWidth(480);
     card.setMaxHeight(500);
     card.setStyle(
         "-fx-background-color: #0d1117;" +
         "-fx-border-color: #30363d;" +
         "-fx-border-width: 1;" +
         "-fx-border-radius: 14;" +
         "-fx-background-radius: 14;" +
         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 30, 0.4, 0, 8);"
     );

     // ── Header ──
     HBox header = new HBox();
     header.setPadding(new Insets(20, 20, 16, 24));
     header.setAlignment(Pos.CENTER_LEFT);
     header.setStyle(
         "-fx-background-color: #161b22;" +
         "-fx-background-radius: 14 14 0 0;" +
         "-fx-border-color: transparent transparent #21262d transparent;" +
         "-fx-border-width: 1;"
     );

     Label titleLabel = new Label("Account settings");
     titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

     Region headerSpacer = new Region(); HBox.setHgrow(headerSpacer, Priority.ALWAYS);

     Button closeBtn = new Button("✕");
     closeBtn.setStyle(
         "-fx-background-color: #21262d;" +
         "-fx-text-fill: #8b949e;" +
         "-fx-font-size: 12;" +
         "-fx-background-radius: 50%;" +
         "-fx-min-width: 28; -fx-min-height: 28;" +
         "-fx-max-width: 28; -fx-max-height: 28;" +
         "-fx-padding: 0; -fx-cursor: hand;"
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

     // ── Body ──
     VBox body = new VBox(20);
     body.setPadding(new Insets(24));

     // Section: Profile info
     Label sectionLabel = new Label("PROFILE");
     sectionLabel.setStyle("-fx-text-fill: #484f58; -fx-font-size: 11; -fx-font-weight: bold;");

     // Username field
     VBox usernameGroup = makeSettingsField("Username", Session.getUsername());
     TextField tfUsername = (TextField) ((VBox) usernameGroup).getChildren().get(1);

     Label usernameErr = new Label();
     usernameErr.setStyle("-fx-text-fill: #f85149; -fx-font-size: 12;");
     usernameErr.setVisible(false);
     usernameErr.setManaged(false);

     // Password field
     VBox passwordGroup = makeSettingsFieldPassword("New password", "Leave blank to keep current");

     Region div = new Region(); div.setPrefHeight(1);
     div.setStyle("-fx-background-color: #21262d;");

     // ── Action buttons ──
     HBox actions = new HBox(10);
     actions.setAlignment(Pos.CENTER_RIGHT);

     Button btnCancel = new Button("Cancel");
     btnCancel.setStyle(
         "-fx-background-color: #21262d;" +
         "-fx-text-fill: #c9d1d9;" +
         "-fx-font-size: 13;" +
         "-fx-background-radius: 8;" +
         "-fx-border-radius: 8;" +
         "-fx-border-color: #30363d;" +
         "-fx-border-width: 1;" +
         "-fx-padding: 8 18;" +
         "-fx-cursor: hand;"
     );
     btnCancel.setOnMouseEntered(e -> btnCancel.setStyle(
         "-fx-background-color: #30363d; -fx-text-fill: white; -fx-font-size: 13;" +
         "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #30363d;" +
         "-fx-border-width: 1; -fx-padding: 8 18; -fx-cursor: hand;"
     ));
     btnCancel.setOnMouseExited(e -> btnCancel.setStyle(
         "-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-font-size: 13;" +
         "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #30363d;" +
         "-fx-border-width: 1; -fx-padding: 8 18; -fx-cursor: hand;"
     ));
     btnCancel.setOnAction(e -> settingsStage.close());

     Button btnSave = new Button("Save changes");
     btnSave.setStyle(
    		    "-fx-background-color: #008cff;" +  // base blue
    		    "-fx-text-fill: white;" +
    		    "-fx-font-size: 13;" +
    		    "-fx-font-weight: bold;" +
    		    "-fx-background-radius: 8;" +
    		    "-fx-border-radius: 8;" +
    		    "-fx-padding: 8 18;" +
    		    "-fx-cursor: hand;"
    		);
    		btnSave.setOnMouseEntered(e -> btnSave.setStyle(
    		    "-fx-background-color: #339eff; -fx-text-fill: white; -fx-font-size: 13;" + // lighter blue on hover
    		    "-fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;" +
    		    "-fx-padding: 8 18; -fx-cursor: hand;"
    		));
    		btnSave.setOnMouseExited(e -> btnSave.setStyle(
    		    "-fx-background-color: #008cff; -fx-text-fill: white; -fx-font-size: 13;" +
    		    "-fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;" +
    		    "-fx-padding: 8 18; -fx-cursor: hand;"
    		));
     PasswordField pfPassword = (PasswordField) ((VBox) passwordGroup).getChildren().get(1);

     // Success banner (hidden initially)
     HBox successBanner = new HBox(8);
     successBanner.setPadding(new Insets(10, 14, 10, 14));
     successBanner.setStyle("-fx-background-color: rgba(35,134,54,0.15); -fx-background-radius: 8; -fx-border-color: #238636; -fx-border-width: 1; -fx-border-radius: 8;");
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
             userService.updateUserPassword(Session.getUserId(), SecurityUtils.hashPassword(newPassword));
             pfPassword.clear();
             saved = true;
         }

         if (saved) {
             successBanner.setVisible(true); successBanner.setManaged(true);
             // Auto-hide after 2s
             new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
                 successBanner.setVisible(false); successBanner.setManaged(false);
             })).play();
         }
     });

     actions.getChildren().addAll(btnCancel, btnSave);
     body.getChildren().addAll(sectionLabel, usernameGroup, usernameErr, passwordGroup, successBanner, div, actions);

     card.getChildren().addAll(header, body);
     overlay.getChildren().add(card);

     // Close when clicking outside the card
     overlay.setOnMouseClicked(e -> {
         if (e.getTarget() == overlay) settingsStage.close();
     });

     Scene scene = new Scene(overlay);
     scene.setFill(Color.TRANSPARENT);
     settingsStage.setScene(scene);
     settingsStage.showAndWait();
 }

 // Helper: labeled text field for settings
 private VBox makeSettingsField(String labelText, String initialValue) {
     Label lbl = new Label(labelText);
     lbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12;");

     TextField tf = new TextField(initialValue);
     tf.setStyle(
         "-fx-background-color: #010409;" +
         "-fx-text-fill: #c9d1d9;" +
         "-fx-border-color: #30363d;" +
         "-fx-border-width: 1;" +
         "-fx-border-radius: 8;" +
         "-fx-background-radius: 8;" +
         "-fx-font-size: 13;" +
         "-fx-padding: 10 12;"
     );
     tf.setMaxWidth(Double.MAX_VALUE);
     tf.setOnMouseEntered(e -> tf.setStyle(
         "-fx-background-color: #010409; -fx-text-fill: #c9d1d9; -fx-border-color: #8b949e;" +
         "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;" +
         "-fx-font-size: 13; -fx-padding: 10 12;"
     ));
     tf.setOnMouseExited(e -> tf.setStyle(
         "-fx-background-color: #010409; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d;" +
         "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;" +
         "-fx-font-size: 13; -fx-padding: 10 12;"
     ));

     VBox group = new VBox(6, lbl, tf);
     return group;
 }

 // Helper: labeled password field for settings
 private VBox makeSettingsFieldPassword(String labelText, String promptText) {
     Label lbl = new Label(labelText);
     lbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12;");

     PasswordField pf = new PasswordField();
     pf.setPromptText(promptText);
     pf.setStyle(
         "-fx-background-color: #010409;" +
         "-fx-text-fill: #c9d1d9;" +
         "-fx-prompt-text-fill: #484f58;" +
         "-fx-border-color: #30363d;" +
         "-fx-border-width: 1;" +
         "-fx-border-radius: 8;" +
         "-fx-background-radius: 8;" +
         "-fx-font-size: 13;" +
         "-fx-padding: 10 12;"
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

                // Watch status
                int userId = Session.getUserId();
                int filmId = film.getFilm_id();
                int dur    = (int) film.getDuration();
                WatchStatus status;
                if (!filmProgressService.exists(userId, filmId))                           status = WatchStatus.NOT_STARTED;
                else if (filmProgressService.getLastPosition(userId, filmId) >= dur - 2)   status = WatchStatus.COMPLETED;
                else                                                                        status = WatchStatus.IN_PROGRESS;

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
            fadeButton(left,  e.getX() < 150                    ? 1 : 0, 200);
            fadeButton(right, e.getX() > root.getWidth() - 150  ? 1 : 0, 200);
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

        // ✅ Wrap to control alignment + clipping
        StackPane posterWrapper = new StackPane(poster);
        posterWrapper.setPrefSize(300, 420);
        posterWrapper.setMaxSize(300, 420);
        posterWrapper.setAlignment(Pos.CENTER);

        // ✅ Clip to force clean edges (fixes overflow / bad fit)
        Rectangle posterClip = new Rectangle(300, 420);
        posterClip.setArcWidth(20);
        posterClip.setArcHeight(20);
        posterWrapper.setClip(posterClip);

        // OPTIONAL: slight zoom to avoid empty gaps
        poster.setScaleX(1.05);
        poster.setScaleY(1.05);

        // Fade overlay (your code is good 👍)
        Region posterFade = new Region();
        posterFade.setPrefSize(300, 420);
        posterFade.setStyle(
            "-fx-background-color: linear-gradient(to right," +
            "  transparent 0%, rgba(7,9,15,0.55) 70%, rgba(7,9,15,1.0) 100%);"
        );

        // Final container
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

        Button trailerBtn = new Button("▶  Trailer");
        Button episodesBtn = new Button("≡  Episodes");
        styleCardButton(trailerBtn, false);
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
        epHeaderTitle.setStyle(
            "-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;"
        );

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

        // Episode rows
        VBox episodesList = new VBox(6);
        episodesList.setPadding(new Insets(10, 16, 16, 16));

        ScrollPane episodesScroll = new ScrollPane(episodesList);
        episodesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        episodesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        episodesScroll.setFitToWidth(true);
        episodesScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");
        VBox.setVgrow(episodesScroll, Priority.ALWAYS);
        episodesScroll.setOnScroll(e ->
            episodesScroll.setVvalue(episodesScroll.getVvalue() - e.getDeltaY() * 0.003));

        ScrollBar customScrollBar = new ScrollBar();
        customScrollBar.setOrientation(Orientation.VERTICAL);
        customScrollBar.setMin(0); customScrollBar.setMax(1);
        customScrollBar.setPrefWidth(5);
        customScrollBar.setStyle("-fx-background-color: transparent;");
        customScrollBar.valueProperty().bindBidirectional(episodesScroll.vvalueProperty());
        Platform.runLater(() -> {
            Node thumb = customScrollBar.lookup(".thumb");
            if (thumb != null) thumb.setStyle(
                "-fx-background-color: rgba(56,189,248,0.55); -fx-background-radius: 10;");
        });
        episodesScroll.viewportBoundsProperty().addListener((o, ov, nv) ->
            updateThumbSize(episodesScroll, customScrollBar));

        int userId = Session.getUserId();
        Map<Integer, WatchStatus> progressMap = episodeProgressService.loadUserProgress(userId);

        // Build the shared episode detail pane
        StackPane episodeDetailPane = new StackPane();
        episodeDetailPane.setPrefSize(740, 420);
        episodeDetailPane.setStyle("-fx-background-color: #07090f;");

        // Build episode rows
        for (Episode ep : s.getEpisodes()) {
            HBox row = buildEpisodeRow(ep, s, progressMap, contentWrapper, episodeDetailPane, episodesView, popup, userId);
            episodesList.getChildren().add(row);
        }

        HBox scrollRow = new HBox(4, episodesScroll, customScrollBar);
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
    // Single definition — 8 params including episodesView
    private HBox buildEpisodeRow(
            Episode ep, Season s,
            Map<Integer, WatchStatus> progressMap,
            StackPane contentWrapper,
            StackPane detailPane,
            VBox episodesView,
            Stage popup, int userId) {

        WatchStatus status = progressMap.getOrDefault(ep.getEpId(), WatchStatus.NOT_STARTED);

        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));

        String baseStyle =
            "-fx-background-color: rgba(15,20,32,0.6);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: rgba(56,189,248,0.07);" +
            "-fx-border-width: 1; -fx-border-radius: 10; -fx-cursor: hand;";
        String hoverStyle =
            "-fx-background-color: rgba(56,189,248,0.08);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: rgba(56,189,248,0.25);" +
            "-fx-border-width: 1; -fx-border-radius: 10; -fx-cursor: hand;";
        row.setStyle(baseStyle);
        row.setOnMouseEntered(e -> row.setStyle(hoverStyle));
        row.setOnMouseExited(e  -> row.setStyle(baseStyle));

        // Number circle
        Label numBadge = new Label(String.format("%02d", ep.getNumEpisode()));
        numBadge.setPrefSize(34, 34); numBadge.setMinSize(34, 34);
        numBadge.setAlignment(Pos.CENTER);
        numBadge.setStyle(
            "-fx-background-color: rgba(56,189,248,0.12); -fx-background-radius: 17;" +
            "-fx-border-color: rgba(56,189,248,0.3); -fx-border-radius: 17; -fx-border-width: 1;" +
            "-fx-text-fill: #38bdf8; -fx-font-size: 12px; -fx-font-weight: bold;"
        );

        // Title + stars
        VBox textCol = new VBox(4);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        Label epTitleLbl = new Label(ep.getTitle());
        epTitleLbl.setStyle(
            "-fx-text-fill: rgba(226,232,240,0.95); -fx-font-size: 14px; -fx-font-weight: bold;"
        );

        HBox miniStars = new HBox(2);
        int rating = (int) Math.round(ep.getRating());
        for (int i = 0; i < 5; i++) {
            Label st = new Label("★");
            st.setStyle("-fx-font-size: 10px; -fx-text-fill: " +
                (i < rating ? "#38bdf8" : "rgba(255,255,255,0.12)") + ";");
            miniStars.getChildren().add(st);
        }
        textCol.getChildren().addAll(epTitleLbl, miniStars);

        // Duration
        int h = ep.getDuration() / 60, m = ep.getDuration() % 60;
        Label duration = new Label((h > 0 ? h + "h " : "") + m + "m");
        duration.setStyle("-fx-text-fill: rgba(148,163,184,0.6); -fx-font-size: 12px;");

        // Status pill
        String pillText   = switch (status) { case COMPLETED -> "✓ Watched"; case IN_PROGRESS -> "▶ In Progress"; default -> "Not Started"; };
        String pillBg     = switch (status) { case COMPLETED -> "rgba(34,197,94,0.15)";  case IN_PROGRESS -> "rgba(56,189,248,0.15)";  default -> "rgba(100,116,139,0.12)"; };
        String pillBorder = switch (status) { case COMPLETED -> "rgba(34,197,94,0.5)";   case IN_PROGRESS -> "rgba(56,189,248,0.45)";  default -> "rgba(100,116,139,0.25)"; };
        String pillFg     = switch (status) { case COMPLETED -> "#4ade80";                case IN_PROGRESS -> "#38bdf8";                default -> "#64748b"; };

        Label statusPill = new Label(pillText);
        statusPill.setStyle(
            "-fx-background-color: " + pillBg + "; -fx-border-color: " + pillBorder + ";" +
            "-fx-border-width: 1; -fx-border-radius: 20; -fx-background-radius: 20;" +
            "-fx-text-fill: " + pillFg + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 9;"
        );

        Label arrow = new Label("›");
        arrow.setStyle("-fx-text-fill: rgba(56,189,248,0.4); -fx-font-size: 20px;");

        row.getChildren().addAll(numBadge, textCol, duration, statusPill, arrow);

        // Click → populate detail + switch view
        row.setOnMouseClicked(ev -> {
            populateEpisodeDetail(detailPane, ep, s, contentWrapper, episodesView, popup, userId);
            switchView(contentWrapper, detailPane);
        });

        return row;
    }

    // ═══════════════════════ EPISODE DETAIL ═══════════════════════
    // Single definition — 7 params including contentWrapper + episodesView
    private void populateEpisodeDetail(
            StackPane detailPane, Episode ep, Season s,
            StackPane contentWrapper, VBox episodesView,
            Stage popup, int userId) {

        detailPane.getChildren().clear();

        // ── COVER ─────────────────────────────────────────────
        ImageView cover = new ImageView();
        cover.setFitWidth(740);
        cover.setFitHeight(280);
        cover.setPreserveRatio(false);

        try {
            cover.setImage(new Image(ep.getCovertUrl(), true));
        } catch (Exception ignored) {}

        Region coverGradient = new Region();
        coverGradient.setPrefSize(740, 280);
        coverGradient.setStyle(
            "-fx-background-color: linear-gradient(to bottom," +
            "rgba(7,9,15,0) 0%, rgba(7,9,15,0.6) 55%, rgba(7,9,15,1.0) 100%);"
        );

        StackPane coverPane = new StackPane(cover, coverGradient);
        coverPane.setMaxSize(740, 280);
        StackPane.setAlignment(coverPane, Pos.TOP_CENTER);

        // IMPORTANT: don't block clicks
        coverPane.setMouseTransparent(true);

        // ── PLAY BUTTON ───────────────────────────────────────
        Button play = new Button("▶");
        play.setPrefSize(64, 64);
        play.setStyle(
            "-fx-background-color: rgba(56,189,248,0.18); -fx-background-radius: 32;" +
            "-fx-border-color: #38bdf8; -fx-border-radius: 32; -fx-border-width: 2;" +
            "-fx-text-fill: white; -fx-font-size: 22px; -fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(56,189,248,0.55), 22, 0.4, 0, 0);"
        );

        // Pulse animation
        ScaleTransition pulse = new ScaleTransition(Duration.millis(900), play);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.12);
        pulse.setToY(1.12);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        // Hover
        play.setOnMouseEntered(e -> {
            pulse.stop();
            play.setScaleX(1.18);
            play.setScaleY(1.18);
        });

        play.setOnMouseExited(e -> {
            play.setScaleX(1.0);
            play.setScaleY(1.0);
            pulse.play();
        });

        StackPane.setAlignment(play, Pos.CENTER);
        StackPane.setMargin(play, new Insets(0, 0, 160, 0));

        // ── INFO PANEL ────────────────────────────────────────
        VBox infoPanel = new VBox(8);
        infoPanel.setPadding(new Insets(0, 28, 20, 28));
        infoPanel.setAlignment(Pos.TOP_LEFT);
        StackPane.setAlignment(infoPanel, Pos.BOTTOM_CENTER);

        Button backBtn = new Button("← Episodes");
        backBtn.setStyle(
            "-fx-background-color: rgba(56,189,248,0.1);" +
            "-fx-border-color: rgba(56,189,248,0.35);" +
            "-fx-text-fill: #38bdf8;"
        );
        addHoverAnimation(backBtn);

        Label epNum = new Label("EPISODE " + ep.getNumEpisode());
        Label epTitle = new Label(ep.getTitle());

        int h = ep.getDuration() / 60;
        int m = ep.getDuration() % 60;

        Label metaLabel = new Label(h + "h " + m + "m");

        Label synopsis = new Label(
            ep.getResume() != null ? ep.getResume() : "No synopsis available."
        );
        synopsis.setWrapText(true);

        infoPanel.getChildren().addAll(backBtn, epNum, epTitle, metaLabel, synopsis);

        // ── ACTIONS ───────────────────────────────────────────
        play.setOnAction(e -> {
            System.out.println("PLAY CLICKED ✅");

            try {
                if (popup != null) popup.close();

                goToLecturePageEpisode(
                    s.getSerieId(),
                    s.getSeasonNum(),
                    ep.getEpId()
                );

                

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        backBtn.setOnAction(e -> switchView(contentWrapper, episodesView));

        // ── FINAL LAYOUT (FIXED ORDER) ─────────────────────────
        detailPane.getChildren().addAll(coverPane, infoPanel, play);

        // FORCE play button on top
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

    // ═══════════════════════ BUTTON STYLES ═══════════════════════
    private void styleCardButton(Button btn, boolean primary) {
        if (primary) {
            btn.setStyle(
                "-fx-background-color: #38bdf8; -fx-text-fill: #07090f;" +
                "-fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8;" +
                "-fx-padding: 8 20; -fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(56,189,248,0.45), 16, 0.3, 0, 0);"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: rgba(56,189,248,0.08); -fx-text-fill: #38bdf8;" +
                "-fx-font-size: 13px; -fx-background-radius: 8;" +
                "-fx-border-color: rgba(56,189,248,0.35); -fx-border-width: 1;" +
                "-fx-border-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;"
            );
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TRAILER POPUP
    // ─────────────────────────────────────────────────────────────────────────
    private void showTrailerPopup(Object item, int seasonIndex) {
        String trailerUrl = null;
        if (item instanceof Film f)       trailerUrl = f.getVideo_url();
        else if (item instanceof Serie s) {
            if (s.getSeasons() != null && !s.getSeasons().isEmpty()) {
                int idx = (seasonIndex < 0 || seasonIndex >= s.getSeasons().size()) ? 0 : seasonIndex;
                trailerUrl = s.getSeasons().get(idx).getTrailerUrl();
            }
        }
        if (trailerUrl == null) return;

        URL videoUrl  = getClass().getResource(trailerUrl);
        if (videoUrl == null) { System.err.println("Video not found: " + trailerUrl); return; }
        String videoPath = trailerUrl.startsWith("http") ? trailerUrl : videoUrl.toExternalForm();

        javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
        webView.setPrefSize(1500, 700);
        webView.getEngine().loadContent(
            "<html><body style='margin:0;background:black;'>" +
            "<video width='100%' height='100%' controls>" +
            "<source src='" + videoPath + "' type='video/mp4'>" +
            "</video></body></html>"
        );

        Rectangle2D screen   = Screen.getPrimary().getBounds();
        double fullW = screen.getWidth(), fullH = screen.getHeight();
        double smallW = 1200, smallH = 600;

        Stage popup = new Stage();
        popup.initOwner(rootPane.getScene().getWindow());
        popup.initModality(Modality.WINDOW_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.setWidth(fullW); popup.setHeight(fullH);
        popup.setX(0); popup.setY(0);

        Button toggleSize = new Button("🗗");
        toggleSize.setStyle("-fx-background-color:#008cff; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:50%; -fx-padding:5 8;");
        addHoverAnimation(toggleSize);

        Button exitButton = new Button("✕");
        exitButton.setStyle("-fx-background-color:#008cff; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:50%; -fx-padding:5 8;");
        exitButton.setOnAction(e -> popup.close());
        addHoverAnimation(exitButton);

        VBox layout = new VBox(15);
        layout.setStyle("-fx-background-color: rgba(0,0,0,0.2); -fx-background-radius:15; -fx-padding:15; -fx-alignment:center;");
        layout.setPrefSize(fullW, fullH);

        final boolean[] isFullScreen = {true};
        toggleSize.setOnAction(e -> {
            if (isFullScreen[0]) {
                popup.setWidth(smallW); popup.setHeight(smallH);
                popup.setX((fullW - smallW) / 2); popup.setY((fullH - smallH) / 2);
                layout.setPrefSize(smallW, smallH);
            } else {
                popup.setWidth(fullW); popup.setHeight(fullH);
                popup.setX(0); popup.setY(0);
                layout.setPrefSize(fullW, fullH);
            }
            isFullScreen[0] = !isFullScreen[0];
        });

        HBox topBar = new HBox(10, toggleSize, exitButton);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(10));
        topBar.setPickOnBounds(false);

        layout.getChildren().addAll(topBar, webView);

        StackPane root = new StackPane(layout);
        root.setStyle("-fx-background-color: rgba(0,0,0,0.85);");

        popup.setOnHidden(e -> {
            webView.getEngine().load(null);
            if (autoSlide != null) autoSlide.play();
        });

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
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
                new KeyValue(card.scaleXProperty(),   scale,   Interpolator.EASE_BOTH),
                new KeyValue(card.scaleYProperty(),   scale,   Interpolator.EASE_BOTH),
                new KeyValue(card.opacityProperty(),  opacity, Interpolator.EASE_BOTH)
            )).play();
        }
    }

    private void centerSlide(ScrollPane scrollPane, StackPane card, HBox slider) {
        double scrollWidth     = slider.getWidth();
        double scrollPaneWidth = scrollPane.getViewportBounds().getWidth();
        double cardCenter      = card.getBoundsInParent().getMinX() + card.getBoundsInParent().getWidth() / 2.0;
        double hValue = Math.min(Math.max((cardCenter - scrollPaneWidth / 2) / (scrollWidth - scrollPaneWidth), 0), 1);
        new Timeline(new KeyFrame(Duration.millis(400), new KeyValue(scrollPane.hvalueProperty(), hValue, Interpolator.EASE_BOTH))).play();
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
    private void autoHideScrollbar(ScrollPane scrollPane, ScrollBar scrollBar) {
        Node content = scrollPane.getContent();
        if (content == null) return;
        Runnable check = () -> {
            boolean need = content.getLayoutBounds().getHeight() > scrollPane.getViewportBounds().getHeight() + 1;
            scrollBar.setVisible(need);
            scrollBar.setManaged(need);
        };
        content.layoutBoundsProperty().addListener((obs, o, n) -> check.run());
        scrollPane.viewportBoundsProperty().addListener((obs, o, n) -> check.run());
        Platform.runLater(check);
    }

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
    private void styleModernButton(Button btn) {
        btn.setStyle(
            "-fx-background-color: #0f172a; -fx-text-fill: #38bdf8; -fx-font-weight: bold;" +
            "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #38bdf8;" +
            "-fx-border-width: 1.5; -fx-padding: 6 14; -fx-cursor: hand;"
        );
    }

    private void addHoverAnimation(Button btn) {
        ScaleTransition up   = new ScaleTransition(Duration.millis(120), btn); up.setToX(1.15);   up.setToY(1.15);
        ScaleTransition down = new ScaleTransition(Duration.millis(120), btn); down.setToX(1.0); down.setToY(1.0);
        btn.setOnMouseEntered(e -> up.playFromStart());
        btn.setOnMouseExited (e -> down.playFromStart());
    }

    public void showPopup(FeaturedItem item) {
        if (item.getType().equalsIgnoreCase("film"))       showFilmPopup(item);
        else if (item.getType().equalsIgnoreCase("serie")) showSeriePopup(item);
    }
}