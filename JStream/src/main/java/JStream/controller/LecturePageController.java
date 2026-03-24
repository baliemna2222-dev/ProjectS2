package JStream.controller;

import JStream.entity.Episode;
import JStream.entity.Film;
import JStream.entity.Serie;
import JStream.entity.Season;
import JStream.service.FeaturedService;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
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
import javafx.stage.Popup;
import javafx.util.Duration;

import java.sql.SQLException;

public class LecturePageController {

    // --- NAVBAR (Identique à Homepage) ---
    @FXML private ImageView logoNav;
    @FXML private StackPane bellContainer;
    @FXML private ImageView bellIcon;
    @FXML private Circle notificationCircle;
    @FXML private Button btnBack;

    // --- HERO SECTION ---
    @FXML private ImageView backgroundImage;
    @FXML private ImageView posterImage;
    @FXML private Label titleLabel;
    @FXML private Label scoreLabel;
    @FXML private Label yearLabel;
    @FXML private Label durationLabel;
    @FXML private Label ageRatingLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label starringLabel;
    @FXML private Label directorLabel;
    @FXML private Label categoriesLabel;
    @FXML private Label episodeInfoLabel;

    // --- CONTENEURS DYNAMIQUES ---
    @FXML private HBox starsBox;
    @FXML private HBox castBox;
    @FXML private HBox reviewsBox;
    @FXML private HBox relatedBox;

    // --- LOGIQUE NOTIFICATION & POPUP ---
    private Popup notificationPopup = new Popup();
    private VBox notificationContent = new VBox();
    private AudioClip bellSound;
    private boolean isNotificationVisible = false;

    @FXML
    public void initialize() {
        setupNavbar();
        setupNotificationSystem();
        
        // Action bouton retour
        if (btnBack != null) {
            btnBack.setOnAction(e -> System.out.println("Retour à la page précédente..."));
        }
    }

    private void setupNavbar() {
        if (logoNav != null) {
            logoNav.setImage(new Image(getClass().getResourceAsStream("/assets/images/logo/Raksha.png")));
        }
        if (bellIcon != null) {
            bellIcon.setImage(new Image(getClass().getResourceAsStream("/assets/images/bellwhiter.png")));
        }
    }

    private void setupNotificationSystem() {
        // Chargement du son
        try {
            bellSound = new AudioClip(getClass().getResource("/assets/sounds/notification.mp3").toString());
        } catch (Exception e) {
            System.err.println("Son non trouvé");
        }

        // Init point rouge (caché au départ)
        if (notificationCircle != null) {
            notificationCircle.setVisible(false);
            notificationCircle.setScaleX(0);
            notificationCircle.setScaleY(0);
        }

        // Style du Popup (Noir cinématique)
        notificationContent.setStyle("-fx-background-color: #111111; -fx-background-radius: 8; -fx-padding: 15; -fx-spacing: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 15, 0, 0, 5);");
        notificationContent.setPrefWidth(250);
        Label popTitle = new Label("Notifications");
        popTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Label popMsg = new Label("No new notifications.");
        popMsg.setStyle("-fx-text-fill: #888;");
        notificationContent.getChildren().addAll(popTitle, popMsg);
        notificationPopup.getContent().add(notificationContent);
        notificationPopup.setAutoHide(true);

        // Events sur la cloche
        bellContainer.setOnMouseEntered(e -> {
            shakeBell();
            if (bellSound != null) bellSound.play();
            showNotificationDot();
            showPopup();
        });
    }

    // ---------------- LOGIQUE D'AFFICHAGE DES DONNÉES ----------------

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
        categoriesLabel.setText("Action, Adventure"); // Exemple statique ou à lier
        
        if (epInfo != null && episodeInfoLabel != null) {
            episodeInfoLabel.setText(epInfo);
            episodeInfoLabel.setVisible(true);
        }

        if (imgPath != null) {
            Image img = new Image(getClass().getResourceAsStream(imgPath));
            posterImage.setImage(img);
            backgroundImage.setImage(img);
        }
        populateStars(rating);
    }

    // ---------------- ANIMATIONS ET UI HELPERS ----------------

    private void populateStars(int rating) {
        starsBox.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-font-size: 18px;");
            star.setTextFill(i < rating ? Color.web("#0000CD") : Color.GRAY);
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
            ScaleTransition st = new ScaleTransition(Duration.millis(300), notificationCircle);
            st.setToX(1); st.setToY(1);
            st.play();
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