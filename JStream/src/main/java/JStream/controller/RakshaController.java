package JStream.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RakshaController {

    @FXML private MediaView heroBackground;
    @FXML private Rectangle overlay;
    @FXML private StackPane rootPane;
    @FXML private Group videoGroup;
    @FXML private ScrollPane scrollPane;

    private MediaPlayer mediaPlayer;
    private List<File> videoFiles = new ArrayList<>();
    private int currentVideoIndex = 0;

    @FXML
    public void initialize() {
        setupVideos();
        bindUIElements();
    }

    /**
     * Load video files from resources and start playback.
     */
    private void setupVideos() {
        try {
            URL resource = getClass().getResource("/assets/videos/shorts");
            if (resource == null) return;

            File folder = new File(resource.toURI());
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4"));
            if (files != null && files.length > 0) {
                videoFiles = Arrays.asList(files);
                playVideo(currentVideoIndex);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bind MediaView and overlay size to the rootPane and apply rotation.
     */
    private void bindUIElements() {
        // Rotate the video
        heroBackground.setRotate(270);

        // Bind MediaView size to rootPane size
        heroBackground.fitWidthProperty().bind(rootPane.heightProperty());
        heroBackground.fitHeightProperty().bind(rootPane.widthProperty());

        // Bind overlay size to rootPane size
        overlay.widthProperty().bind(rootPane.widthProperty());
        overlay.heightProperty().bind(rootPane.heightProperty());

        // Optional: enforce max size
        rootPane.setMaxHeight(700);
        rootPane.setMaxWidth(900);

        // Apply CSS after UI is rendered
        Platform.runLater(() -> {
            // scrollPane or StackPane CSS if needed
        });
    }

    /**
     * Handle "Start Watching" button click.
     */
    @FXML
    private void handleStartWatching() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Play a video from the videoFiles list by index.
     */
    private void playVideo(int index) {
        if (videoFiles.isEmpty()) return;

        // Loop back if index exceeds list size
        if (index >= videoFiles.size()) {
            index = 0;
        }

        File videoFile = videoFiles.get(index);
        Media media = new Media(videoFile.toURI().toString());

        // Stop previous MediaPlayer
        if (mediaPlayer != null) mediaPlayer.stop();

        mediaPlayer = new MediaPlayer(media);
        heroBackground.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(true);

        // Play next video on end
        mediaPlayer.setOnEndOfMedia(() -> {
            currentVideoIndex = (currentVideoIndex + 1) % videoFiles.size();
            playVideo(currentVideoIndex);
        });
    }

    /**
     * Stop video playback. Call this on scene close.
     */
    public void stopVideo() {
        if (mediaPlayer != null) mediaPlayer.stop();
    }
}