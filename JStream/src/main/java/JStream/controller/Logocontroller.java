package JStream.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class Logocontroller implements Initializable {

    @FXML
    private MediaView mediaView;

    private void switchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mediaView.getScene().getWindow();

            Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());

            stage.setScene(newScene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        URL videoUrl = getClass().getResource("/assets/videos/videoplayback.mp4");
        if (videoUrl == null) {
            System.out.println("❌ Video not found!");
            return;
        }

        Media media = new Media(videoUrl.toExternalForm());
        MediaPlayer player = new MediaPlayer(media);

        mediaView.setPreserveRatio(false); 
        mediaView.setSmooth(true);
        mediaView.setCache(true);
        mediaView.setMediaPlayer(player);

        player.play();

        player.setOnEndOfMedia(() -> {
            player.stop();
            player.dispose(); 
            switchToLogin();
        });

        // Resize video to full screen
        mediaView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                mediaView.fitWidthProperty().bind(newScene.widthProperty());
                mediaView.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
    }
}
