package JStream.controller;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import JStream.dao.FilmProgressDAO;

import java.io.File;

import JStream.dao.EpisodeProgressDAO;
import JStream.entity.Session;
import JStream.utils.Database;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class VideoPlayerController {

    // ── FXML nodes ───────────────────────────────────────────────────────────
	@FXML private MediaView mediaView; // Baddelna el WebView
    private MediaPlayer mediaPlayer;   // Hédha houa el "Moteur" el jdid
    @FXML private StackPane rootPane;
    @FXML private HBox topBar;
    @FXML private VBox bottomControls;
    @FXML private Slider seekBar, volumeSlider;
    @FXML private Button btnClose, btnPlay, btnMute,
                         btnForward, btnRewind, btnMiniPlayer;
    @FXML private Label titleLabel, timeLabel;
    @FXML private ProgressIndicator loadingSpinner;

    // ── State ─────────────────────────────────────────────────────────────────
    private Timeline idleTimer, progressTimer, saveTimer;
    private Stage    stage;
    private boolean  isPlaying           = true;
    private boolean  isMuted             = false;
    private boolean  wasPausedBeforeSeek = false;
    private boolean  videoReady          = false;
    // Stored by loadVideo(), consumed by startPlayback()
    private String  pendingUrl;
    private String  pendingTitle;

    // ── DB progress context (set via setContext before startPlayback) ─────────
    private Integer filmId    = null;   // non-null → film mode
    private Integer episodeId = null;   // non-null → episode mode

    private FilmProgressDAO    filmProgressDAO;
    private EpisodeProgressDAO episodeProgressDAO;

    // Mini-player dimensions
 // Dimensions mta3 el "Medium" mode (mouch sghir barcha)
    private static final double MED_W = 960; 
    private static final double MED_H = 540;
    @FXML
    public void initialize() {

        // Init DAOs
        try {
            java.sql.Connection conn = Database.getConnection();
            filmProgressDAO    = new FilmProgressDAO(conn);
            episodeProgressDAO = new EpisodeProgressDAO(conn);
        } catch (Exception e) {
            System.err.println("⚠️ DB connection failed in VideoPlayer: " + e.getMessage());
        }

        // ── Timers ───────────────────────────────────────────────────────────
        idleTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> fadeUI(0)));
        idleTimer.setCycleCount(1);

        progressTimer = new Timeline(new KeyFrame(Duration.millis(500), e -> updateProgress()));
        progressTimer.setCycleCount(Timeline.INDEFINITE);

        // Save progress to DB every 10 s while playing
        saveTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> saveProgressToDB()));
        saveTimer.setCycleCount(Timeline.INDEFINITE);

        // ── Auto-hide ────────────────────────────────────────────────────────
        rootPane.setOnMouseMoved(e -> { fadeUI(1); idleTimer.playFromStart(); });
        bottomControls.setOnMouseEntered(e -> { fadeUI(1); idleTimer.stop(); });
        bottomControls.setOnMouseExited(e  -> { if (isPlaying) idleTimer.playFromStart(); });

        // ── Transparent click overlay (WebView swallows clicks otherwise) ───
        Platform.runLater(() -> {
            Region overlay = new Region();
            overlay.setStyle("-fx-background-color: transparent;");
            overlay.setPickOnBounds(true);
            rootPane.getChildren().add(1, overlay); // above WebView, below gradient

            overlay.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (event.getClickCount() == 1)       togglePlay();
                    else if (event.getClickCount() == 2 && stage != null)
                        stage.setFullScreen(!stage.isFullScreen());
                }
                rootPane.requestFocus();
            });
            overlay.setOnMouseMoved(e -> { fadeUI(1); idleTimer.playFromStart(); });
        });

        // ── Keyboard shortcuts ───────────────────────────────────────────────
        rootPane.setFocusTraversable(true);
        rootPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case SPACE  -> { togglePlay();  event.consume(); }
                case F      -> { if (stage != null) stage.setFullScreen(!stage.isFullScreen()); event.consume(); }
                case M      -> { toggleMute();  event.consume(); }
                case RIGHT  -> { seek(10);      event.consume(); }
                case LEFT   -> { seek(-10);     event.consume(); }
                case ESCAPE -> { closePlayer(); event.consume(); }
			default -> throw new IllegalArgumentException("Unexpected value: " + event.getCode());
            }
        });

     // ── Volume slider (Native) ───
        volumeSlider.valueProperty().addListener((obs, o, n) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(n.doubleValue() / 100.0);
            }
            updateVolumeIcon(n.doubleValue() / 100.0);
        });

        // ── Seek bar (Native) ───
        seekBar.setOnMousePressed(e -> {
            if (mediaPlayer == null) return;
            wasPausedBeforeSeek = !isPlaying;
            mediaPlayer.pause();
        });
        seekBar.setOnMouseReleased(e -> {
            if (mediaPlayer == null) return;
            mediaPlayer.seek(Duration.seconds(seekBar.getValue()));
            if (!wasPausedBeforeSeek) mediaPlayer.play();
        });

        // ── Buttons ──────────────────────────────────────────────────────────
        btnPlay.setOnAction(e       -> togglePlay());
        btnMute.setOnAction(e       -> toggleMute());
        btnForward.setOnAction(e    -> seek(10));
        btnRewind.setOnAction(e     -> seek(-10));

        if (btnMiniPlayer != null) {
            btnMiniPlayer.setOnAction(e -> toggleToggleView());
        }

        // Zidha f-el initialize be-sh dima el fullscreen icon tetbaddel s7i7a
        if (btnMiniPlayer != null) {
        	btnMiniPlayer.setOnAction(e -> {
                stage.setFullScreen(!stage.isFullScreen());
            });
        }

        setupButtonHover(btnPlay);
        setupButtonHover(btnClose);
        setupButtonHover(btnMiniPlayer);
        setupButtonHover(btnMute);
        setupButtonHover(btnForward);
        setupButtonHover(btnRewind);
        if (btnMiniPlayer != null) setupButtonHover(btnMiniPlayer);
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /** Store video URL + title. Call before stage.show(). */
    public void loadVideo(String url, String title) {
        this.pendingUrl   = url;
        this.pendingTitle = (title != null) ? title : "";
    }

    /** Wire stage. Call before stage.show(). */
    public void setStage(Stage s) {
        this.stage = s;
        
        btnClose.setOnAction(e -> closePlayer());

        // El button i-3ayet lel logic mta3 el toggle elli fih el dimension el metwassta
        btnMiniPlayer.setOnAction(e -> toggleToggleView());

        // Listener dima 3assas: ken el stage khraj mel Fullscreen (b-ESC masalann)
        // i-da7mish el icon mta3 el button dima s7i7a
        s.fullScreenProperty().addListener((obs, wasFs, isFs) -> {
            if (isFs) {
                btnMiniPlayer.setText("🗗"); // Icon mta3 mini/center
            } else {
                btnMiniPlayer.setText("🗖"); // Icon mta3 Fullscreen
                // Hna dima i-ji f-el Center ki n-khalliouh mouch fullscreen
            }
        });
    }
    public void setContext(Integer filmId, Integer episodeId) {
        this.filmId    = filmId;
        this.episodeId = episodeId;
    }

    public void startPlayback() {
        if (pendingUrl == null || pendingUrl.isEmpty()) return;

        titleLabel.setText(pendingTitle);
        loadingSpinner.setVisible(true);

        // --- 1. Jib el last position mel base ---
        int startPos = 0;
        int userId = Session.getUserId();
        try {
            if (filmId != null) {
                startPos = filmProgressDAO.getLastPosition(userId, filmId);
            } else if (episodeId != null) {
                startPos = episodeProgressDAO.getLastPosition(userId, episodeId);
            }
        } catch (Exception e) {
            System.err.println("⚠️ DB Error fetching last position: " + e.getMessage());
        }

        final int finalStartPos = startPos;

        // --- 2. Fix Path & Init Media ---
        String finalUrl = resolveUrl(pendingUrl);
        Media media = new Media(finalUrl);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaView.setPreserveRatio(true);

        mediaPlayer.setOnReady(() -> {
            loadingSpinner.setVisible(false);
            videoReady = true;

            // --- 3. SEEK l-el blasa el 9dima ---
            if (finalStartPos > 0) {
                mediaPlayer.seek(Duration.seconds(finalStartPos));
            }

            mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
            mediaPlayer.play();
            
            progressTimer.play();
            saveTimer.play();
        });

        mediaPlayer.setOnError(() -> {
            System.err.println("❌ Media Error: " + mediaPlayer.getError().getMessage());
            loadingSpinner.setVisible(false);
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            System.out.println("🎬 Video Finished! Saving as COMPLETED..."); // Zid hedhi be-sh tchoufha f-el Console
            saveProgressToDB(false); // 👈 HEDHI EL LEZMA: true be-sh t-mchi lel setCompleted
            
            Platform.runLater(() -> {
                isPlaying = false;
                btnPlay.setText("▶");
            });
        });
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void updateProgress() {
        if (mediaPlayer == null || !videoReady) return;

        Duration current = mediaPlayer.getCurrentTime();
        Duration total = mediaPlayer.getTotalDuration();

        if (total != null && total.toSeconds() > 0) {
            Platform.runLater(() -> {
                seekBar.setMax(total.toSeconds());
                seekBar.setValue(current.toSeconds());
                timeLabel.setText(formatTime(current.toSeconds()) + " / " + formatTime(total.toSeconds()));
            });
        }
    }
    /** Called by saveTimer every 10 s — saves IN_PROGRESS. */
    private void saveProgressToDB() {
        saveProgressToDB(false);
    }

   
    private void saveProgressToDB(boolean completed) {
        if (mediaPlayer == null) return;
        int userId = Session.getUserId();

        // ─── LOGIQUE FILM ───
        if (filmId != null) {
            if (completed) {
                int totalDur = (int) mediaPlayer.getTotalDuration().toSeconds();
                filmProgressDAO.setCompleted(userId, filmId, totalDur);
                System.out.println("🎬 Film: COMPLETED saved.");
            } else {
                int currentPos = (int) mediaPlayer.getCurrentTime().toSeconds();
                filmProgressDAO.setInProgress(userId, filmId, currentPos);
                System.out.println("⏳ Film Progress: " + currentPos + "s");
            }
        } 
        // ─── LOGIQUE EPISODE (SERIE) ───
        else if (episodeId != null) {
            if (completed) {
                int totalDur = (int) mediaPlayer.getTotalDuration().toSeconds();
                episodeProgressDAO.setCompleted(userId, episodeId, totalDur);
                System.out.println("📺 Episode: COMPLETED saved.");
            } else {
                int currentPos = (int) mediaPlayer.getCurrentTime().toSeconds();
                episodeProgressDAO.setInProgress(userId, episodeId, currentPos);
                System.out.println("⏳ Episode Progress: " + currentPos + "s");
            }
        }
    }

    private void togglePlay() {
        if (mediaPlayer == null) return;
        if (isPlaying) {
            mediaPlayer.pause();
            btnPlay.setText("▶");
            saveProgressToDB();
        } else {
            mediaPlayer.play();
            btnPlay.setText("⏸");
        }
        isPlaying = !isPlaying;
    }

    private void seek(int seconds) {
        if (mediaPlayer == null) return;
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(seconds)));
    }

    private void toggleMute() {
        if (mediaPlayer == null) return;
        isMuted = !isMuted;
        mediaPlayer.setMute(isMuted);
        updateVolumeIcon(isMuted ? 0 : volumeSlider.getValue() / 100.0);
    }

  
    private void toggleToggleView() {
        if (stage == null) return;

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        if (stage.isFullScreen()) {
            // I-khroj mel Fullscreen w i-ji f-el Center (Medium Size)
            stage.setFullScreen(false);
            stage.setAlwaysOnTop(true);
            
            stage.setWidth(MED_W);
            stage.setHeight(MED_H);
            stage.setX((screen.getWidth() - MED_W) / 2);
            stage.setY((screen.getHeight() - MED_H) / 2);
        } else {
            // Yarja3 Fullscreen
            stage.setAlwaysOnTop(false);
            stage.setFullScreen(true);
        }
    }

  
    private void fadeUI(double opacity) {
        if (!isPlaying && opacity == 0) return;
        FadeTransition ft1 = new FadeTransition(Duration.millis(300), topBar);
        FadeTransition ft2 = new FadeTransition(Duration.millis(300), bottomControls);
        ft1.setToValue(opacity); ft2.setToValue(opacity);
        ft1.play(); ft2.play();
    }

    private void updateVolumeIcon(double vol) {
        if (isMuted || vol == 0) btnMute.setText("🔇");
        else if (vol < 0.4)      btnMute.setText("🔉");
        else                     btnMute.setText("🔊");
    }

    private void closePlayer() {
        saveProgressToDB();
        videoReady = false;
        progressTimer.stop();
        idleTimer.stop();
        saveTimer.stop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // Free memory
        }
        if (stage != null) stage.close();
    }

    private String resolveUrl(String url) {
        try {
            if (url.startsWith("http")) return url;
            
            // Jarreb lawwej f-el resources (src/main/resources)
            java.net.URL res = getClass().getResource(url);
            if (res != null) return res.toExternalForm();
            
            // Ken ma l9ahsh, i-3tabrou Path absolute f-el disk (C:/...)
            File file = new File(url);
            if (file.exists()) {
                return file.toURI().toString();
            }
            return url;
        } catch (Exception e) { 
            return url; 
        }
    }

    private String formatTime(double totalSeconds) {
        int s = (int) totalSeconds;
        int h = s / 3600, m = (s % 3600) / 60, sec = s % 60;
        return h > 0
            ? String.format("%d:%02d:%02d", h, m, sec)
            : String.format("%d:%02d", m, sec);
    }

    /** Zoom-in on hover instead of shrinking — smooth ScaleTransition. */
    private void setupButtonHover(Button b) {
        // Reset inline scale styles that were set before
        b.setStyle(b.getStyle()
            .replace("-fx-scale-x:1.0;", "")
            .replace("-fx-scale-y:1.0;", "")
            .replace("-fx-scale-x:1.15;", "")
            .replace("-fx-scale-y:1.15;", ""));

        ScaleTransition zoomIn  = new ScaleTransition(Duration.millis(120), b);
        zoomIn.setToX(1.18); zoomIn.setToY(1.18);

        ScaleTransition zoomOut = new ScaleTransition(Duration.millis(120), b);
        zoomOut.setToX(1.0); zoomOut.setToY(1.0);

        b.setOnMouseEntered(e -> {
            zoomIn.playFromStart();
            b.setStyle(b.getStyle().replace("-fx-text-fill:white;", "-fx-text-fill:#00d4ff;")
                + "-fx-text-fill:#00d4ff;");
        });
        b.setOnMouseExited(e -> {
            zoomOut.playFromStart();
            b.setStyle(b.getStyle().replace("-fx-text-fill:#00d4ff;", "-fx-text-fill:white;"));
        });
    }
}