package JStream.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;

public class BellController {

    @FXML
    private ImageView bellIcon;

    @FXML
    private StackPane bellContainer;

    private Circle notificationDot;
    private AudioClip bellSound;
    private boolean isNotificationVisible = false; 

    @FXML
    public void initialize() {
        // Load bell image
        bellIcon.setImage(new Image(getClass().getResourceAsStream("/assets/images/bellwhiter.png")));

        // Load sound
        bellSound = new AudioClip(getClass().getResource("/assets/sounds/notification.mp3").toString());

        // Create red notification dot
        notificationDot = new Circle(7, Color.RED);
        notificationDot.setTranslateX(13); // position top-right of bell
        notificationDot.setTranslateY(-13);
        notificationDot.setScaleX(0); // start hidden with scale 0
        notificationDot.setScaleY(0);
        bellContainer.getChildren().add(notificationDot);

        bellContainer.setOnMouseEntered(e -> {
            shakeBell();
            bellSound.play();
            if (!isNotificationVisible) {
                showNotification();
            }
        });
    }

    private void shakeBell() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(bellIcon.rotateProperty(), 0)),
                new KeyFrame(Duration.millis(100), new KeyValue(bellIcon.rotateProperty(), -10)),
                new KeyFrame(Duration.millis(200), new KeyValue(bellIcon.rotateProperty(), 10)),
                new KeyFrame(Duration.millis(300), new KeyValue(bellIcon.rotateProperty(), -15)),
                new KeyFrame(Duration.millis(400), new KeyValue(bellIcon.rotateProperty(), 15)),
                new KeyFrame(Duration.millis(500), new KeyValue(bellIcon.rotateProperty(), -20)),
                new KeyFrame(Duration.millis(600), new KeyValue(bellIcon.rotateProperty(), 20)),
                new KeyFrame(Duration.millis(700), new KeyValue(bellIcon.rotateProperty(), -15)),
                new KeyFrame(Duration.millis(800), new KeyValue(bellIcon.rotateProperty(), 15)),
                new KeyFrame(Duration.millis(900), new KeyValue(bellIcon.rotateProperty(), -10)),
                new KeyFrame(Duration.millis(1000), new KeyValue(bellIcon.rotateProperty(), 0))
        );
        timeline.play();
    }

    private void showNotification() {
        isNotificationVisible = true;
        notificationDot.setVisible(true);
        ScaleTransition bounce = new ScaleTransition(Duration.millis(500), notificationDot);
        bounce.setFromX(0);
        bounce.setFromY(0);
        bounce.setToX(1);
        bounce.setToY(1);
        bounce.setInterpolator(Interpolator.EASE_OUT);
        bounce.play();
    }

    private void hideNotification() {
        isNotificationVisible = false;

        // Smooth fade and shrink
        ParallelTransition hide = new ParallelTransition();

        FadeTransition fade = new FadeTransition(Duration.millis(300), notificationDot);
        fade.setFromValue(1);
        fade.setToValue(0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), notificationDot);
        scale.setToX(0);
        scale.setToY(0);

        hide.getChildren().addAll(fade, scale);
        hide.setOnFinished(e -> notificationDot.setVisible(false));
        hide.play();
    }
}
