package JStream.controller;

import JStream.entity.Episode;
import JStream.entity.Season;
import JStream.entity.Serie;
import JStream.entity.Session;
import JStream.service.EpisodeProgressService;
import JStream.service.EpisodeService;
import JStream.service.FeaturedService;
import JStream.service.FilmProgressService;
import JStream.service.SerieService;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.sql.SQLException;

public class VideoPlayerController {

    // ── FXML Nodes ──────────────────────────────────────────────
    @FXML private MediaView         mediaView;
    @FXML private StackPane         rootPane;
    @FXML private HBox              topBar;
    @FXML private VBox              bottomControls;
    @FXML private Slider            seekBar, volumeSlider;
    @FXML private Button            btnClose, btnPlay, btnMute,
                                    btnForward, btnRewind, btnMiniPlayer,
                                    btnNextEpisode;
    @FXML private Label             titleLabel, timeLabel;
    @FXML private ProgressIndicator loadingSpinner;

    // ── Player & State ──────────────────────────────────────────
    private MediaPlayer mediaPlayer;
    private Stage       stage;
    private boolean     isPlaying           = true;
    private boolean     isMuted             = false;
    private boolean     wasPausedBeforeSeek = false;
    private boolean     videoReady          = false;
    private String      pendingUrl;
    private String      pendingTitle;

    // ── Context ────────────────────────────────────────────────
    private Integer filmId            = null;
    private Integer episodeId         = null;
    private Integer currentSeasonId   = null;
    private Integer currentNumEpisode = null;

    // ── Services ───────────────────────────────────────────────
    private FilmProgressService    filmProgressService;
    private EpisodeProgressService episodeProgressService;
    private EpisodeService         episodeService = new EpisodeService();

    // ── Timers ─────────────────────────────────────────────────
    private Timeline idleTimer, progressTimer, saveTimer;

    // ── Binge-watching ─────────────────────────────────────────
    private Timeline  bingeTimer;
    private StackPane bingeOverlay;
    private Label     bingeCountdownLabel;
    private Arc       bingeArc;
    private static final int    BINGE_COUNTDOWN = 10;
    private static final double MED_W           = 960;
    private static final double MED_H           = 540;
    
    // ───────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        filmProgressService    = new FilmProgressService();
        episodeProgressService = new EpisodeProgressService();
       
        setupTimers();
        setupMouseAndKeyboardEvents();
        setupSliders();
        setupControlButtons();

        // Bouton next episode : caché par défaut
        btnNextEpisode.setVisible(false);
        btnNextEpisode.setManaged(false);
        setupButtonHover(btnNextEpisode);
        // 🔥 Make video fill the whole container
        mediaView.fitWidthProperty().bind(rootPane.widthProperty());
        mediaView.fitHeightProperty().bind(rootPane.heightProperty());

        // 🔥 Keep aspect ratio (important)
        mediaView.setPreserveRatio(true);
    }

    // ── Public API ─────────────────────────────────────────────

    public void loadVideo(String url, String title) {
        pendingUrl   = url;
        pendingTitle = (title != null) ? title : "";
    }

    public void setStage(Stage s) {
        this.stage = s;
        btnClose.setOnAction(e -> closePlayer());
        if (btnMiniPlayer != null) btnMiniPlayer.setOnAction(e -> toggleMiniPlayer());
        s.fullScreenProperty().addListener((obs, wasFs, isFs) ->
            btnMiniPlayer.setText(isFs ? "🗗" : "🗖")
        );
    }

    public void setContext(Integer filmId, Integer episodeId) {
        this.filmId    = filmId;
        this.episodeId = episodeId;
    }

    public void setEpisodeContext(int seasonId, int numEpisode) {
        this.currentSeasonId   = seasonId;
        this.currentNumEpisode = numEpisode;
    }

    public void startPlayback() {
        if (pendingUrl == null || pendingUrl.isEmpty()) return;

        // Réinitialiser le bouton à chaque nouvelle lecture
        btnNextEpisode.setVisible(false);
        btnNextEpisode.setManaged(false);

        titleLabel.setText(pendingTitle);
        loadingSpinner.setVisible(true);

        int startPos = 0;
        int userId   = Session.getUserId();
        if      (filmId    != null) startPos = filmProgressService.getLastPosition(userId, filmId);
        else if (episodeId != null) startPos = episodeProgressService.getEpisodeLastPosition(userId, episodeId);

        final int finalStartPos = startPos;

        Media media = new Media(resolveUrl(pendingUrl));
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaView.setPreserveRatio(true);

        mediaPlayer.setOnReady(() -> {
            loadingSpinner.setVisible(false);
            videoReady = true;
            if (finalStartPos > 0) mediaPlayer.seek(Duration.seconds(finalStartPos));
            mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
            mediaPlayer.play();
            progressTimer.play();
            saveTimer.play();
        });

        mediaPlayer.setOnError(() -> {
            System.err.println("❌ Media Error: " + mediaPlayer.getError().getMessage());
            loadingSpinner.setVisible(false);
        });

        mediaPlayer.setOnEndOfMedia(this::handleEndOfMedia);
    }

    // ── End of Media ───────────────────────────────────────────

    private void handleEndOfMedia() {
        saveProgressToDB(true);
        Platform.runLater(() -> {
            isPlaying = false;
            btnPlay.setText("▶");

            System.out.println("=== END OF MEDIA ===");
            System.out.println("episodeId       = " + episodeId);
            System.out.println("currentSeasonId = " + currentSeasonId);
            System.out.println("currentNumEp    = " + currentNumEpisode);

            if (episodeId == null || currentSeasonId == null || currentNumEpisode == null) return;

            Episode next = episodeService.getNextEpisode(currentSeasonId, currentNumEpisode);
            System.out.println("next = " + (next != null ? next.getTitle() : "NULL"));

            if (next == null) return; // fin de saison

            // ✅ Bouton persistant
            btnNextEpisode.setVisible(true);
            btnNextEpisode.setManaged(true);
            btnNextEpisode.setOnAction(e -> {
                stopBingeTimer();
                hideBingeOverlay();
                hideNextEpisodeButton();
                launchNextEpisode(next);
            });

            // ✅ Binge overlay avec countdown
            showBingeOverlay(next);
        });
    }
 
    // ── Binge-watching Overlay ─────────────────────────────────

    private void showBingeOverlay(Episode nextEpisode) {
        bingeOverlay = new StackPane();
        bingeOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.75);");
        bingeOverlay.setPickOnBounds(true);

        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: rgba(8,16,26,0.95);" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(0,212,255,0.25);" +
            "-fx-border-radius: 16;" +
            "-fx-padding: 36 48;"
        );
        card.setMaxWidth(420);

        Label upNextLabel = new Label("Épisode suivant");
        upNextLabel.setStyle("-fx-text-fill: #7a80a0; -fx-font-size: 13px;");

        Label epTitle = new Label("E" + nextEpisode.getNumEpisode() + " — " + nextEpisode.getTitle());
        epTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        epTitle.setWrapText(true);
        epTitle.setMaxWidth(340);

        StackPane arcContainer = new StackPane();
        arcContainer.setPrefSize(80, 80);

        Circle arcBg = new Circle(38, Color.TRANSPARENT);
        arcBg.setStroke(Color.web("#2a3140"));
        arcBg.setStrokeWidth(4);

        bingeArc = new Arc(40, 40, 34, 34, 90, 360);
        bingeArc.setType(ArcType.OPEN);
        bingeArc.setFill(Color.TRANSPARENT);
        bingeArc.setStroke(Color.web("#00d4ff"));
        bingeArc.setStrokeWidth(4);
        bingeArc.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

        bingeCountdownLabel = new Label(String.valueOf(BINGE_COUNTDOWN));
        bingeCountdownLabel.setStyle(
            "-fx-text-fill: #00d4ff; -fx-font-size: 22px; -fx-font-weight: bold;"
        );

        arcContainer.getChildren().addAll(arcBg, bingeArc, bingeCountdownLabel);

        Button btnWatch = new Button("▶  Regarder maintenant");
        btnWatch.setStyle(
            "-fx-background-color: linear-gradient(to right, #00d4ff, #005fb8);" +
            "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;" +
            "-fx-background-radius: 25; -fx-cursor: hand; -fx-padding: 10 28;"
        );

        Button btnCancel = new Button("Annuler");
        btnCancel.setStyle(
            "-fx-background-color: rgba(255,255,255,0.07);" +
            "-fx-text-fill: #7a80a0; -fx-font-size: 13px;" +
            "-fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 8 20;"
        );

        HBox btnRow = new HBox(14, btnWatch, btnCancel);
        btnRow.setAlignment(Pos.CENTER);

        card.getChildren().addAll(upNextLabel, epTitle, arcContainer, btnRow);
        bingeOverlay.getChildren().add(card);
        rootPane.getChildren().add(bingeOverlay);

        btnWatch.setOnAction(e -> {
            stopBingeTimer();
            hideBingeOverlay();
            hideNextEpisodeButton();
            launchNextEpisode(nextEpisode);
        });

        btnCancel.setOnAction(e -> {
            stopBingeTimer();
            hideBingeOverlay();
            // btnNextEpisode reste visible → l'user peut encore naviguer
        });

        startBingeTimer(nextEpisode);
    }

    private void startBingeTimer(Episode nextEpisode) {
        final int[] remaining = {BINGE_COUNTDOWN};

        bingeTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            remaining[0]--;

            if (bingeCountdownLabel != null)
                bingeCountdownLabel.setText(String.valueOf(remaining[0]));

            if (bingeArc != null)
                bingeArc.setLength(360.0 * remaining[0] / BINGE_COUNTDOWN);

            if (remaining[0] <= 0) {
                stopBingeTimer();
                Platform.runLater(() -> {
                    hideBingeOverlay();
                    hideNextEpisodeButton();
                    launchNextEpisode(nextEpisode);
                });
            }
        }));
        bingeTimer.setCycleCount(BINGE_COUNTDOWN);
        bingeTimer.play();
    }

    private void stopBingeTimer() {
        if (bingeTimer != null) { bingeTimer.stop(); bingeTimer = null; }
    }

    private void hideBingeOverlay() {
        if (bingeOverlay != null) {
            rootPane.getChildren().remove(bingeOverlay);
            bingeOverlay = null;
        }
    }

    private void hideNextEpisodeButton() {
        if (btnNextEpisode != null) {
            btnNextEpisode.setVisible(false);
            btnNextEpisode.setManaged(false);
        }
    }

    // ── Launch Next Episode (même fenêtre, même controller) ───
 // Top of VideoPlayerController
    private LecturePageController parentController;

    public void setParentController(LecturePageController controller) {
        this.parentController = controller;
    }
private int serieid;
private Serie serie;

private void launchNextEpisode(Episode next) {
    // 🔹 Stop UI / timers
    stopBingeTimer();
    hideBingeOverlay();
    hideNextEpisodeButton();

    if (progressTimer != null) progressTimer.stop();
    if (saveTimer != null) saveTimer.stop();

    videoReady = false;

    // 🔹 Stop current media safely
    if (mediaPlayer != null) {
        mediaPlayer.stop();
        mediaPlayer.dispose();
        mediaPlayer = null;
    }

    // 🔹 Services
    
    EpisodeService episodeService = new EpisodeService();
    SerieService serieService = new SerieService();

    try {
        // 🔹 Load serie & season
        this.serieid = episodeService.getSerieIdBySeasonId(currentSeasonId);
        this.serie   = serieService.getSerieById(serieid);
       

        // 🔹 Update internal state
        this.filmId            = null;
        this.episodeId         = next.getEpId();
        this.currentNumEpisode = next.getNumEpisode();
        this.pendingUrl        = next.getVideoUrl();
        this.pendingTitle      = next.getTitle();

       
        // 🔹 Update UI on JavaFX thread
        if (parentController != null) {

            Platform.runLater(() -> {
                try {
                    parentController.updateUI(
                    	
                        next.getTitle(),
                        next.getResume() != null ? next.getResume() : serie.getSynopsis(),
                        next.getDuration() + " min",
                        serie.getRating(),
                        serie.getCasting(),
                         
                         " - E" + next.getNumEpisode(), // ✅ dynamic
                        next.getVideoUrl(),
                        next.getEpId()
                    );

                    // ▶ Start playback AFTER UI update
                    startPlayback();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } 

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    // ── Private Helpers ────────────────────────────────────────

    private void setupTimers() {
        idleTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> fadeUI(0)));
        idleTimer.setCycleCount(1);

        progressTimer = new Timeline(new KeyFrame(Duration.millis(500), e -> updateProgress()));
        progressTimer.setCycleCount(Timeline.INDEFINITE);

        saveTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> saveProgressToDB()));
        saveTimer.setCycleCount(Timeline.INDEFINITE);
    }

    private void setupMouseAndKeyboardEvents() {
        rootPane.setOnMouseMoved(e -> { fadeUI(1); idleTimer.playFromStart(); });
        bottomControls.setOnMouseEntered(e -> { fadeUI(1); idleTimer.stop(); });
        bottomControls.setOnMouseExited(e  -> { if (isPlaying) idleTimer.playFromStart(); });

        Platform.runLater(() -> {
            Region overlay = new Region();
            overlay.setStyle("-fx-background-color: transparent;");
            overlay.setPickOnBounds(true);
            rootPane.getChildren().add(1, overlay);

            overlay.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (event.getClickCount() == 1)      togglePlay();
                    else if (event.getClickCount() == 2 && stage != null)
                        stage.setFullScreen(!stage.isFullScreen());
                }
                rootPane.requestFocus();
            });
            overlay.setOnMouseMoved(e -> { fadeUI(1); idleTimer.playFromStart(); });
        });

        rootPane.setFocusTraversable(true);
        rootPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case SPACE  -> { togglePlay();  event.consume(); }
                case F      -> { if (stage != null) stage.setFullScreen(!stage.isFullScreen()); event.consume(); }
                case M      -> { toggleMute();  event.consume(); }
                case RIGHT  -> { seek(10);      event.consume(); }
                case LEFT   -> { seek(-10);     event.consume(); }
                case ESCAPE -> { closePlayer(); event.consume(); }
                default     -> {}
            }
        });
    }

    private void setupSliders() {
        volumeSlider.valueProperty().addListener((obs, o, n) -> {
            if (mediaPlayer != null) mediaPlayer.setVolume(n.doubleValue() / 100.0);
            updateVolumeIcon(n.doubleValue() / 100.0);
        });

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
    }

    private void setupControlButtons() {
        btnPlay.setOnAction(e    -> togglePlay());
        btnMute.setOnAction(e    -> toggleMute());
        btnForward.setOnAction(e -> seek(10));
        btnRewind.setOnAction(e  -> seek(-10));

        setupButtonHover(btnPlay);
        setupButtonHover(btnClose);
        setupButtonHover(btnMute);
        setupButtonHover(btnForward);
        setupButtonHover(btnRewind);
        if (btnMiniPlayer != null) setupButtonHover(btnMiniPlayer);
    }

    private void updateProgress() {
        if (mediaPlayer == null || !videoReady) return;
        Duration current = mediaPlayer.getCurrentTime();
        Duration total   = mediaPlayer.getTotalDuration();
        if (total != null && total.toSeconds() > 0) {
            Platform.runLater(() -> {
                seekBar.setMax(total.toSeconds());
                seekBar.setValue(current.toSeconds());
                timeLabel.setText(formatTime(current.toSeconds())
                        + " / " + formatTime(total.toSeconds()));
            });
        }
    }

    private void saveProgressToDB()                  { saveProgressToDB(false); }
    private void saveProgressToDB(boolean completed) {
        if (mediaPlayer == null) return;
        int userId      = Session.getUserId();
        int currentTime = (int) mediaPlayer.getCurrentTime().toSeconds();
        int totalTime   = (int) mediaPlayer.getTotalDuration().toSeconds();

        if (filmId != null) {
            if (completed || currentTime >= totalTime - 2)
                filmProgressService.markCompleted(userId, filmId, totalTime);
            else
                filmProgressService.markInProgress(userId, filmId, currentTime);
        } else if (episodeId != null) {
            if (completed || currentTime >= totalTime - 2)
                episodeProgressService.markCompleted(userId, episodeId, totalTime);
            else
                episodeProgressService.markInProgress(userId, episodeId, currentTime);
        }
    }

    private void togglePlay() {
        if (mediaPlayer == null) return;
        if (isPlaying) { mediaPlayer.pause(); btnPlay.setText("▶"); saveProgressToDB(); }
        else           { mediaPlayer.play();  btnPlay.setText("⏸"); }
        isPlaying = !isPlaying;
    }

    private void seek(int seconds) {
        if (mediaPlayer != null)
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(seconds)));
    }

    private void toggleMute() {
        if (mediaPlayer == null) return;
        isMuted = !isMuted;
        mediaPlayer.setMute(isMuted);
        updateVolumeIcon(isMuted ? 0 : volumeSlider.getValue() / 100.0);
    }

    private void toggleMiniPlayer() {
        if (stage == null) return;
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
            stage.setAlwaysOnTop(true);
            stage.setWidth(MED_W);  stage.setHeight(MED_H);
            stage.setX((screen.getWidth()  - MED_W) / 2);
            stage.setY((screen.getHeight() - MED_H) / 2);
        } else {
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
        if (isMuted || vol == 0)  btnMute.setText("🔇");
        else if (vol < 0.4)       btnMute.setText("🔉");
        else                      btnMute.setText("🔊");
    }

    private void closePlayer() {
        stopBingeTimer();
        hideBingeOverlay();
        hideNextEpisodeButton();
        saveProgressToDB();
        videoReady = false;
        progressTimer.stop();
        idleTimer.stop();
        saveTimer.stop();
        if (mediaPlayer != null) { mediaPlayer.stop(); mediaPlayer.dispose(); }
        if (stage != null) stage.close();
    }

    private String resolveUrl(String url) {
        try {
            if (url.startsWith("http")) return url;
            java.net.URL res = getClass().getResource(url);
            if (res != null) return res.toExternalForm();
            File file = new File(url);
            if (file.exists()) return file.toURI().toString();
            return url;
        } catch (Exception e) { return url; }
    }

    private String formatTime(double totalSeconds) {
        int s = (int) totalSeconds;
        int h = s / 3600, m = (s % 3600) / 60, sec = s % 60;
        return h > 0
            ? String.format("%d:%02d:%02d", h, m, sec)
            : String.format("%d:%02d", m, sec);
    }

    private void setupButtonHover(Button b) {
        ScaleTransition zoomIn  = new ScaleTransition(Duration.millis(120), b);
        zoomIn.setToX(1.18); zoomIn.setToY(1.18);
        ScaleTransition zoomOut = new ScaleTransition(Duration.millis(120), b);
        zoomOut.setToX(1.0);  zoomOut.setToY(1.0);

        b.setOnMouseEntered(e -> {
            zoomIn.playFromStart();
            b.setStyle(b.getStyle().replace("-fx-text-fill:white;", "") + "-fx-text-fill:#00d4ff;");
        });
        b.setOnMouseExited(e -> {
            zoomOut.playFromStart();
            b.setStyle(b.getStyle().replace("-fx-text-fill:#00d4ff;", "") + "-fx-text-fill:white;");
        });
    }
}