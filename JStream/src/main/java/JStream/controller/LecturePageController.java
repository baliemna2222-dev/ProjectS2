package JStream.controller;

import JStream.entity.Episode;
import JStream.entity.Film;
import JStream.entity.Serie;
import JStream.entity.Season;
import JStream.service.FeaturedService;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.stage.Popup;
import javafx.util.Duration;

import java.sql.SQLException;

public class LecturePageController {

    // --- NAVBAR ---
    @FXML private ImageView logoNav, bellIcon;
    @FXML private StackPane bellContainer;
    @FXML private Circle notificationCircle;
    @FXML private Button btnBack, playButton, btnNotification, profileBtn;

    // --- HERO & CONTAINERS ---
    @FXML private VBox mainContainer;
    @FXML private ImageView backgroundImage, posterImage;
    @FXML private Label titleLabel, scoreLabel, yearLabel, durationLabel, ageRatingLabel, descriptionLabel;
    @FXML private Label starringLabel, directorLabel, categoriesLabel, episodeInfoLabel;
    @FXML private HBox starsBox, castBox;

    // --- TABS ---
    @FXML private Rectangle lineOverview, lineTrailers;
    @FXML private Button tabOverview, tabTrailers;

    private AudioClip bellSound;
    private Popup notificationPopup = new Popup();
    private VBox notificationContent = new VBox();
    private boolean isNotificationVisible = false;

    // ============== INITIALIZE ==============
    @FXML
    public void initialize() {
        setupNavbar();
        setupNotificationSystem();
        setupTabLogic(); // Logic mta3 el tabs Overview/Trailers
        
        if (btnBack != null) {
            btnBack.setOnAction(e -> System.out.println("Retour..."));
        }

        // Entrance Animation
        if (mainContainer != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), mainContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
        populateStars(9.0);
        if (posterImage != null) addHoverEffect(posterImage);
        if (playButton != null) addHoverEffect(playButton);
        
        loadCast();
    }

    private void setupTabLogic() {
        if (tabOverview != null && tabTrailers != null) {
            tabOverview.setOnAction(e -> {
                lineOverview.setVisible(true);
                lineTrailers.setVisible(false);
                tabOverview.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
                tabTrailers.setStyle("-fx-background-color: transparent; -fx-text-fill: #7a80a0;");
            });

            tabTrailers.setOnAction(e -> {
                lineOverview.setVisible(false);
                lineTrailers.setVisible(true);
                tabTrailers.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
                tabOverview.setStyle("-fx-background-color: transparent; -fx-text-fill: #7a80a0;");
            });
        }
    }

    private void addHoverEffect(Node node) {
        ScaleTransition stIn = new ScaleTransition(Duration.millis(200), node);
        stIn.setToX(1.05); stIn.setToY(1.05);

        ScaleTransition stOut = new ScaleTransition(Duration.millis(200), node);
        stOut.setToX(1.0); stOut.setToY(1.0);

        node.setOnMouseEntered(e -> {
            stIn.play();
            node.setEffect(new javafx.scene.effect.DropShadow(25, Color.web("#2d54ff", 0.6)));
        });
        node.setOnMouseExited(e -> {
            stOut.play();
            node.setEffect(null); 
        });
    }

    private void loadCast() {
        if (castBox == null) return;
        castBox.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            VBox actorCard = createActorCard("Actor Name", "/assets/images/profile.png");
            castBox.getChildren().add(actorCard);
        }
    }

    private VBox createActorCard(String name, String imgPath) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        try {
            ImageView img = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
            img.setFitWidth(80); img.setFitHeight(80);
            Circle clip = new Circle(40, 40, 40);
            img.setClip(clip);
            Label n = new Label(name); n.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            card.getChildren().addAll(img, n);
            addHoverEffect(card);
        } catch (Exception e) {}
        return card;
    }

    private void setupNavbar() {
        try {
            if (logoNav != null) logoNav.setImage(new Image(getClass().getResourceAsStream("/assets/images/logo/Raksha.png")));
            if (bellIcon != null) bellIcon.setImage(new Image(getClass().getResourceAsStream("/assets/images/bellwhiter.png")));
        } catch (Exception e) {}
    }

    private void setupNotificationSystem() {
        try {
            bellSound = new AudioClip(getClass().getResource("/assets/sounds/notification.mp3").toString());
        } catch (Exception e) {}

        notificationContent.setStyle("-fx-background-color: #111111; -fx-background-radius: 8; -fx-padding: 15; -fx-spacing: 10;");
        notificationContent.setPrefWidth(250);
        Label popTitle = new Label("Notifications");
        popTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        notificationContent.getChildren().add(popTitle);
        notificationPopup.getContent().add(notificationContent);
        notificationPopup.setAutoHide(true);

        bellContainer.setOnMouseEntered(e -> {
            shakeBell();
            if (bellSound != null) bellSound.play();
            showNotificationDot();
            showPopup();
        });
    }
    

    public void initFilm(int filmId) {
        if (episodeInfoLabel != null) episodeInfoLabel.setVisible(false);
        try {
            Film film = new FeaturedService().getFilmDetails(filmId);
            updateUI(film.getTitle(), film.getSynopsis(), film.getDuration() + " min", 
                     film.getRating(), film.getCasting(), film.getPoster_url(), null);
            if (scoreLabel != null) scoreLabel.setText(String.valueOf(film.getRating()));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void initEpisode(int serieId, int seasonNum, int episodeNum) {
        try {
            Serie serie = new FeaturedService().getFullSerie(serieId);
            Episode ep = findEpisodeInSerie(serie, seasonNum, episodeNum);
            if (ep != null) {
                updateUI(ep.getTitle(), ep.getResume() != null ? ep.getResume() : serie.getSynopsis(), 
                         ep.getDuration() + " min", serie.getRating(), serie.getCasting(), 
                         serie.getCovertUrl(), "S" + seasonNum + " - E" + episodeNum);
                if (scoreLabel != null) scoreLabel.setText(String.valueOf(serie.getRating()));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void updateUI(String title, String desc, String duration, int rating, String cast, String imgPath, String epInfo) {
        titleLabel.setText(title);
        descriptionLabel.setText(desc);
        durationLabel.setText(duration);
        starringLabel.setText(cast);
        
        if (epInfo != null && episodeInfoLabel != null) {
            episodeInfoLabel.setText(epInfo);
            episodeInfoLabel.setVisible(true);
        }

        if (imgPath != null) {
            try {
                Image img = new Image(getClass().getResourceAsStream(imgPath));
                posterImage.setImage(img);
                backgroundImage.setImage(img);
            } catch (Exception e) {}
        }
        populateStars(rating);
    }

    private void populateStars(double rating) {
        if (starsBox == null) return;
        
        starsBox.getChildren().clear();
        // Rating 3la 10, na9smouh 3la 2 be-ch iwalli 3la 5 stars
        double starsToHighlight = rating / 2.0; 

        for (int i = 1; i <= 5; i++) {
            Label star = new Label("★");
            
            if (i <= starsToHighlight) {
                // Stars elli yech3lou (Blue Glow)
                star.setStyle("-fx-text-fill: #00d4ff; " + 
                              "-fx-font-size: 22px; " + 
                              "-fx-effect: dropshadow(three-pass-box, rgba(0, 212, 255, 0.8), 15, 0, 0, 0);");
            } else {
                // Stars el matfya (Dark Blue/Gray)
                star.setStyle("-fx-text-fill: #2a3140; " + 
                              "-fx-font-size: 22px;");
            }
            
            starsBox.getChildren().add(star);
        }
    }

    private void shakeBell() {
        RotateTransition rt = new RotateTransition(Duration.millis(100), bellIcon);
        rt.setFromAngle(-10); rt.setToAngle(10);
        rt.setCycleCount(4); rt.setAutoReverse(true);
        rt.play();
    }

    private void showNotificationDot() {
        if (!isNotificationVisible) {
            notificationCircle.setVisible(true);
            isNotificationVisible = true;
        }
    }

    private void showPopup() {
        Bounds bounds = bellContainer.localToScreen(bellContainer.getBoundsInLocal());
        notificationPopup.show(bellContainer, bounds.getMinX() - 200, bounds.getMaxY() + 10);
    }

    private Episode findEpisodeInSerie(Serie serie, int seasonNum, int episodeNum) {
        for (Season s : serie.getSeasons()) {
            if (s.getSeasonNum() == seasonNum) {
                for (Episode ep : s.getEpisodes()) {
                    if (ep.getNumEpisode() == episodeNum) return ep;
                }
            }
        }
        return null;
    }
}