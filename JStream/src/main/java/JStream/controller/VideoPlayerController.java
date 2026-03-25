package JStream.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;

public class VideoPlayerController {

    @FXML private WebView webView;
    @FXML private StackPane rootPane;
    @FXML private HBox topBar;
    @FXML private VBox bottomControls; 
    @FXML private Slider seekBar;
    @FXML private Button btnClose, btnPlay, btnFullscreen, btnMiniPlayer;
    @FXML private Label titleLabel;

    private Timeline idleTimer;
    private Stage stage;
    private boolean isFullScreen = true;
    private Rectangle2D screenBounds;

    // Configuration mta3 el mode sghir (MiniPlayer)
    private double smallWidth = 1100;
    private double smallHeight = 600;

    @FXML
    public void initialize() {
        screenBounds = Screen.getPrimary().getBounds();

        // --- 1. Logic Auto-Hide (Netflix Style) ---
        // El controls tetkhabba ba3d 3 seconds ken l-mouse ma t-t7arrakch
        idleTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> hideControls()));
        idleTimer.setCycleCount(1);

        rootPane.setOnMouseMoved(e -> showControls());
        rootPane.setOnMouseClicked(e -> showControls());

        // Initial state
        showControls();
        
        // --- 2. Hover Effects lel Buttons ---
        setupButtonHover(btnPlay);
        setupButtonHover(btnClose);
        setupButtonHover(btnFullscreen);
    }

    private void showControls() {
        topBar.setOpacity(1);
        bottomControls.setOpacity(1);
        idleTimer.playFromStart(); // Reset el counter
    }

    private void hideControls() {
        // Animation sghira lel opacity bech i-ji pro
        topBar.setOpacity(0);
        bottomControls.setOpacity(0);
    }

    private void setupButtonHover(Button btn) {
        if (btn == null) return;
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-text-fill: #00d4ff; -fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-text-fill: white; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));
    }

    public void loadVideo(String url, String title) {
        if (titleLabel != null) titleLabel.setText(title);

        try {
            URL videoUrl = getClass().getResource(url);
            String videoPath = (videoUrl != null) ? videoUrl.toExternalForm() : url;

            // HTML CSS integrated bech el video t-3abbi el ecran kemla (Netflix-like)
            String html = "<html><body style='margin:0; background:black; overflow:hidden;'>" +
                    "<video id='vid' width='100%' height='100%' autoplay " +
                    "style='display:block; width:100vw; height:100vh; object-fit:contain;'>" +
                    "<source src='" + videoPath + "' type='video/mp4'>" +
                    "</video></body></html>";

            webView.getEngine().loadContent(html);

        } catch (Exception e) {
            System.err.println("❌ Erreur fel loading mta3 el video: " + e.getMessage());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        
        // Bouton Close (X)
        btnClose.setOnAction(e -> {
            webView.getEngine().load(null); // Stop el video stream
            stage.close();
        });

        // Toggle Fullscreen / MiniPlayer
        if (btnFullscreen != null) {
            btnFullscreen.setOnAction(e -> toggleScreenMode());
        }
        
        // Ken 3andek bouton miniPlayer ekhir
        if (btnMiniPlayer != null) {
            btnMiniPlayer.setOnAction(e -> toggleScreenMode());
        }

        // Sigalet stop ki stage yet8la9 mel "Alt+F4" walla taskira okhra
        stage.setOnHidden(e -> webEngineStop());
    }

    private void toggleScreenMode() {
        if (isFullScreen) {
            // Mode Sghir (Centered)
            stage.setWidth(smallWidth);
            stage.setHeight(smallHeight);
            stage.setX((screenBounds.getWidth() - smallWidth) / 2);
            stage.setY((screenBounds.getHeight() - smallHeight) / 2);
            isFullScreen = false;
        } else {
            // Full Screen
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setX(0);
            stage.setY(0);
            isFullScreen = true;
        }
    }

    private void webEngineStop() {
        if (webView != null) {
            webView.getEngine().load(null);
        }
    }
}