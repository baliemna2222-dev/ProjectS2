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
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import JStream.dao.FilmProgressDAO;
import JStream.dao.EpisodeProgressDAO;
import JStream.entity.Session;
import JStream.utils.Database;

public class VideoPlayerController {

    // ── FXML nodes ───────────────────────────────────────────────────────────
    @FXML private WebView webView;
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
        webView.setContextMenuEnabled(false);

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

        // ── Volume slider ────────────────────────────────────────────────────
        volumeSlider.valueProperty().addListener((obs, o, n) -> {
            double vol = n.doubleValue() / 100.0;
            if (videoReady) js("document.getElementById('vid').volume = " + vol);
            updateVolumeIcon(vol);
        });

        // ── Seek bar ─────────────────────────────────────────────────────────
        seekBar.setOnMousePressed(e -> {
            if (!videoReady) return;
            wasPausedBeforeSeek = !isPlaying;
            progressTimer.pause();
            if (isPlaying) js("document.getElementById('vid').pause()");
        });
        seekBar.setOnMouseReleased(e -> {
            if (!videoReady) return;
            js("document.getElementById('vid').currentTime = " + seekBar.getValue());
            if (!wasPausedBeforeSeek) js("document.getElementById('vid').play()");
            progressTimer.play();
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

    /**
     * Set DB context so watch progress is saved.
     * Call BEFORE startPlayback().
     *   - For a film:   setContext(filmId, null)
     *   - For episode:  setContext(null, episodeId)
     */
    public void setContext(Integer filmId, Integer episodeId) {
        this.filmId    = filmId;
        this.episodeId = episodeId;
    }

    /**
     * Load HTML into WebView. Call AFTER stage.show().
     *
     * Correct order in LecturePageController:
     *   controller.setStage(videoStage);
     *   controller.loadVideo(url, title);
     *   controller.setContext(filmId, null);   // or (null, epId) for episode
     *   videoStage.show();
     *   controller.startPlayback();            ← always last
     */
    public void startPlayback() {
        if (pendingUrl == null) {
            System.err.println("⚠️ startPlayback() called with no pending URL");
            return;
        }

        titleLabel.setText(pendingTitle);
        loadingSpinner.setVisible(true);
        videoReady = false;
        isPlaying  = true;
        btnPlay.setText("⏸");
        progressTimer.stop();
        saveTimer.stop();

        webView.getEngine().getLoadWorker().stateProperty().addListener(
            new javafx.beans.value.ChangeListener<javafx.concurrent.Worker.State>() {
                @Override
                public void changed(
                        javafx.beans.value.ObservableValue<
                            ? extends javafx.concurrent.Worker.State> obs,
                        javafx.concurrent.Worker.State old,
                        javafx.concurrent.Worker.State newState) {

                    if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                        obs.removeListener(this);
                        videoReady = true;
                        Platform.runLater(() -> {
                            loadingSpinner.setVisible(false);
                            progressTimer.play();
                            saveTimer.play();

                            double vol = volumeSlider.getValue() / 100.0;
                            js("document.getElementById('vid').volume = " + vol);
                            if (isMuted) js("document.getElementById('vid').muted = true");

                            js("document.getElementById('vid')" +
                               ".addEventListener('ended', function() {" +
                               "  window._jstreamEnded = true;" +
                               "});");

                            rootPane.requestFocus();
                        });

                    } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                        obs.removeListener(this);
                        Platform.runLater(() -> loadingSpinner.setVisible(false));
                        System.err.println("❌ WebView FAILED: " + pendingUrl);
                    }
                }
            });

        String path = resolveUrl(pendingUrl);
        String html = """
                <html>
                <body style="margin:0;padding:0;background:#000;overflow:hidden;
                             width:100vw;height:100vh;display:flex;
                             justify-content:center;align-items:center;">
                  <video id="vid" autoplay playsinline
                         style="width:100%%;height:100%%;object-fit:contain;display:block;">
                    <source src="%s" type="video/mp4">
                  </video>
                </body>
                </html>
                """.formatted(path);

        webView.getEngine().loadContent(html);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void updateProgress() {
        if (!videoReady) return;
        try {
            Object ended = js("window._jstreamEnded || false");
            if (Boolean.TRUE.equals(ended)) {
                js("window._jstreamEnded = false;");
                // Mark completed in DB
                saveProgressToDB(true);
                Platform.runLater(() -> {
                    isPlaying = false;
                    btnPlay.setText("▶");
                    fadeUI(1);
                    idleTimer.stop();
                    saveTimer.stop();
                });
            }

            Object curr = js("document.getElementById('vid').currentTime");
            Object dur  = js("document.getElementById('vid').duration");
            if (curr instanceof Number c && dur instanceof Number d) {
                double total   = d.doubleValue();
                double current = c.doubleValue();
                if (total > 0 && !Double.isNaN(total)) {
                    Platform.runLater(() -> {
                        seekBar.setMax(total);
                        seekBar.setValue(current);
                        timeLabel.setText(formatTime(current) + " / " + formatTime(total));
                    });
                }
            }
        } catch (Exception ignored) {}
    }

    /** Called by saveTimer every 10 s — saves IN_PROGRESS. */
    private void saveProgressToDB() {
        saveProgressToDB(false);
    }

    /**
     * Persists watch position.
     * @param completed true → writes COMPLETED, false → IN_PROGRESS
     */
    private void saveProgressToDB(boolean completed) {
        if (filmProgressDAO == null && episodeProgressDAO == null) return;

        Object curr = js("document.getElementById('vid').currentTime");
        if (!(curr instanceof Number)) return;
        int positionSec = ((Number) curr).intValue();

        int userId = Session.getUserId();

        try {
            if (filmId != null && filmProgressDAO != null) {
                if (completed) filmProgressDAO.setCompleted(userId, filmId, positionSec);
                else           filmProgressDAO.setInProgress(userId, filmId, positionSec);
            } else if (episodeId != null && episodeProgressDAO != null) {
                if (completed) episodeProgressDAO.setCompleted(userId, episodeId, positionSec);
                else           episodeProgressDAO.setInProgress(userId, episodeId, positionSec);
            }
        } catch (Exception e) {
            System.err.println("⚠️ saveProgressToDB failed: " + e.getMessage());
        }
    }

    private void togglePlay() {
        if (!videoReady) return;
        if (isPlaying) {
            js("document.getElementById('vid').pause()");
            btnPlay.setText("▶");
            fadeUI(1);
            idleTimer.stop();
            saveProgressToDB(); // Save on pause
        } else {
            js("document.getElementById('vid').play()");
            btnPlay.setText("⏸");
            idleTimer.playFromStart();
        }
        isPlaying = !isPlaying;
    }

    private void toggleMute() {
        if (!videoReady) return;
        isMuted = !isMuted;
        js("document.getElementById('vid').muted = " + isMuted);
        updateVolumeIcon(isMuted ? 0 : volumeSlider.getValue() / 100.0);
    }

    private void seek(int seconds) {
        if (!videoReady) return;
        js("document.getElementById('vid').currentTime += " + seconds);
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

    private Object js(String script) {
        try { return webView.getEngine().executeScript(script); }
        catch (Exception e) { return null; }
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
        saveProgressToDB(); // Save on close
        videoReady = false;
        progressTimer.stop();
        idleTimer.stop();
        saveTimer.stop();
        webView.getEngine().load(null);
        if (stage != null) stage.close();
    }

    private String resolveUrl(String url) {
        try {
            java.net.URL res = getClass().getResource(url);
            return (res != null) ? res.toExternalForm() : url;
        } catch (Exception e) { return url; }
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