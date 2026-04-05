package JStream.controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RakshaController {

    @FXML private MediaView heroBackground;
    @FXML private StackPane rootPane;
    @FXML private StackPane heroPane;
    @FXML private Pane videoContainer;
    @FXML private Pane overlayBottom;
    @FXML private Pane overlayLeft;
    @FXML private ScrollPane scrollPane;
    @FXML private ImageView logo;

    private MediaPlayer mediaPlayer;
    private final List<File> videoFiles = new ArrayList<>();
    private int currentVideoIndex = 0;

    private static final Color BG = Color.web("#0a0a0f");

    @FXML
    public void initialize() {
        logo.setImage(new Image(getClass().getResourceAsStream("/assets/images/logo/Raksha.png")));
        setupGradientOverlays();
        bindVideoToContainer();
        styleScrollPane();
        setupVideos();

        // ✅ Wait until scene + stage are fully ready
        rootPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                    if (newWin instanceof Stage stage) {
                        stage.showingProperty().addListener((obs, wasShowing, isShowing) -> {
                            if (isShowing) initLayoutBindings(stage);
                        });
                        // In case already showing
                        if (stage.isShowing()) initLayoutBindings(stage);
                    }
                });
                // In case window already attached
                if (newScene.getWindow() instanceof Stage stage) {
                    if (stage.isShowing()) initLayoutBindings(stage);
                    else stage.showingProperty().addListener((obs, wasShowing, isShowing) -> {
                        if (isShowing) initLayoutBindings(stage);
                    });
                }
            }
        });
    }

    /**
     * Set dynamic bindings for responsive layout. Call this after loading FXML.
     */
    public void initLayoutBindings(Stage stage) {
        if (rootPane instanceof Region r) {
            r.prefWidthProperty().bind(stage.widthProperty());
            r.prefHeightProperty().bind(stage.heightProperty());
        }

        // ✅ ScrollPane fills rootPane exactly
        scrollPane.prefWidthProperty().bind(rootPane.widthProperty());
        scrollPane.prefHeightProperty().bind(rootPane.heightProperty());
        scrollPane.maxWidthProperty().bind(rootPane.widthProperty());
        scrollPane.maxHeightProperty().bind(rootPane.heightProperty());

        videoContainer.prefWidthProperty().bind(heroPane.widthProperty());
        videoContainer.prefHeightProperty().bind(heroPane.heightProperty());

        heroBackground.fitWidthProperty().bind(videoContainer.widthProperty());
        heroBackground.fitHeightProperty().bind(videoContainer.heightProperty());

        // ✅ Width only — never bind height on scroll content
        if (scrollPane.getContent() instanceof Region content) {
            content.prefWidthProperty().bind(scrollPane.widthProperty());
        }

        overlayBottom.prefWidthProperty().bind(heroPane.widthProperty());
        overlayBottom.prefHeightProperty().bind(heroPane.heightProperty());
        overlayLeft.prefWidthProperty().bind(heroPane.widthProperty());
        overlayLeft.prefHeightProperty().bind(heroPane.heightProperty());

        Platform.runLater(() ->
            scrollPane.lookupAll(".viewport").forEach(n ->
                n.setStyle("-fx-background-color: transparent;")
            )
        );
    }
    // ─────────────────────────────────────────────
    // GRADIENT OVERLAYS
    // ─────────────────────────────────────────────
    private void setupGradientOverlays() {
        LinearGradient bottomGrad = new LinearGradient(
            0, 1, 0, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0.00, Color.color(BG.getRed(), BG.getGreen(), BG.getBlue(), 1.0)),
            new Stop(0.40, Color.color(BG.getRed(), BG.getGreen(), BG.getBlue(), 0.6)),
            new Stop(1.00, Color.color(BG.getRed(), BG.getGreen(), BG.getBlue(), 0.1))
        );
        overlayBottom.setBackground(new Background(
            new BackgroundFill(bottomGrad, CornerRadii.EMPTY, Insets.EMPTY)
        ));

        LinearGradient leftGrad = new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0.00, Color.color(BG.getRed(), BG.getGreen(), BG.getBlue(), 0.95)),
            new Stop(0.38, Color.color(BG.getRed(), BG.getGreen(), BG.getBlue(), 0.70)),
            new Stop(0.65, Color.color(BG.getRed(), BG.getGreen(), BG.getBlue(), 0.0))
        );
        overlayLeft.setBackground(new Background(
            new BackgroundFill(leftGrad, CornerRadii.EMPTY, Insets.EMPTY)
        ));

        // Make overlays always fill heroPane
        Platform.runLater(() -> {
            overlayBottom.prefWidthProperty().bind(heroPane.widthProperty());
            overlayBottom.prefHeightProperty().bind(heroPane.heightProperty());
            overlayLeft.prefWidthProperty().bind(heroPane.widthProperty());
            overlayLeft.prefHeightProperty().bind(heroPane.heightProperty());
        });
    }

    // ─────────────────────────────────────────────
    // VIDEO PLAYBACK
    // ─────────────────────────────────────────────
    private void setupVideos() {
        try {
            URL resource = getClass().getResource("/assets/videos/shorts");
            if (resource == null) return;

            File folder = new File(resource.toURI());
            File[] files = folder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".mp4") ||
                name.toLowerCase().endsWith(".m4v") ||
                name.toLowerCase().endsWith(".mov") ||
                name.toLowerCase().endsWith(".avi")
            );
            if (files != null && files.length > 0) {
                videoFiles.addAll(Arrays.asList(files));
                playVideo(0);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void bindVideoToContainer() {
        heroBackground.setPreserveRatio(false);

        Platform.runLater(() -> {
            videoContainer.prefWidthProperty().bind(heroPane.widthProperty());
            videoContainer.prefHeightProperty().bind(heroPane.heightProperty());

            heroBackground.fitWidthProperty().bind(videoContainer.widthProperty());
            heroBackground.fitHeightProperty().bind(videoContainer.heightProperty());

            Rectangle clip = new Rectangle();
            clip.widthProperty().bind(videoContainer.widthProperty());
            clip.heightProperty().bind(videoContainer.heightProperty());
            videoContainer.setClip(clip);
        });
    }

    private void styleScrollPane() {
        Platform.runLater(() ->
            scrollPane.lookupAll(".viewport").forEach(n ->
                n.setStyle("-fx-background-color: transparent;")
            )
        );
    }

    private void playVideo(int index) {
        if (videoFiles.isEmpty()) return;

        currentVideoIndex = index % videoFiles.size();
        File videoFile = videoFiles.get(currentVideoIndex);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        Media media = new Media(videoFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.0);
        mediaPlayer.setAutoPlay(true);

        heroBackground.setPreserveRatio(false);
        heroBackground.setRotate(0);
        heroBackground.setMediaPlayer(mediaPlayer);

        mediaPlayer.setOnEndOfMedia(() -> Platform.runLater(() -> playVideo(currentVideoIndex + 1)));
        mediaPlayer.setOnError(() -> System.err.println("MediaPlayer error: " + mediaPlayer.getError()));
    }

    // ─────────────────────────────────────────────
    // BUTTON ACTION
    // ─────────────────────────────────────────────
    @FXML
    private void handleStartWatching() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) heroBackground.getScene().getWindow();

            // ✅ Reuse existing scene instead of creating a new one
            stage.getScene().setRoot(root);
            stage.setMaximized(true);

            // ✅ Bind login root to stage size
            ((Region) root).prefWidthProperty().bind(stage.widthProperty());
            ((Region) root).prefHeightProperty().bind(stage.heightProperty());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Release media resources. Call on stage close.
     */
    public void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
}