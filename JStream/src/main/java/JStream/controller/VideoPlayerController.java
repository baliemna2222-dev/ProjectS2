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
    private final FeaturedService        featuredService        = new FeaturedService();
    private       FilmProgressService    filmProgressService;
    private       EpisodeProgressService episodeProgressService;
    private final EpisodeService         episodeService         = new EpisodeService();
    private final SerieService           serieService           = new SerieService();

    // ── Parent controller (for UI sync) ────────────────────────
    private LecturePageController parentController;

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
        filmProgressService    = new FilmProgressService(featuredService);
        episodeProgressService = new EpisodeProgressService();

        setupTimers();
        setupMouseAndKeyboardEvents();
        setupSliders();
        setupControlButtons();

        btnNextEpisode.setVisible(false);
        btnNextEpisode.setManaged(false);
        setupButtonHover(btnNextEpisode);

        mediaView.fitWidthProperty().bind(rootPane.widthProperty());
        mediaView.fitHeightProperty().bind(rootPane.heightProperty());
        mediaView.setPreserveRatio(true);
    }

    // ── Public API ─────────────────────────────────────────────

    public void setParentController(LecturePageController controller) {
        this.parentController = controller;
    }

    public void loadVideo(String url, String title) {
        pendingUrl   = url;
        pendingTitle = (title != null) ? title : "";
    }

    public void setStage(Stage s) {
        this.stage = s;
        btnClose.setOnAction(e -> closePlayer());
        if (btnMiniPlayer != null)
            btnMiniPlayer.setOnAction(e -> toggleMiniPlayer());
        s.fullScreenProperty().addListener((obs, wasFs, isFs) ->
            btnMiniPlayer.setText(isFs ? "🗗" : "🗖"));
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

        // Reset next-episode button
        btnNextEpisode.setVisible(false);
        btnNextEpisode.setManaged(false);

        titleLabel.setText(pendingTitle);
        loadingSpinner.setVisible(true);

        // ── Resume position ──
        int startPos = 0;
        int userId   = Session.getUserId();
        if      (filmId    != null) startPos = filmProgressService.getLastPosition(userId, filmId);
        else if (episodeId != null) startPos = episodeProgressService.getEpisodeLastPosition(userId, episodeId);

        final int finalStartPos = startPos;

        // ── Dispose old player if re-used ──
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
            videoReady  = false;
        }

        Media       media = new Media(resolveUrl(pendingUrl));
        mediaPlayer       = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaView.setPreserveRatio(true);

        mediaPlayer.setOnReady(() -> {
            loadingSpinner.setVisible(false);
            videoReady = true;
            if (finalStartPos > 0)
                mediaPlayer.seek(Duration.seconds(finalStartPos));
            mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
            mediaPlayer.play();
            isPlaying = true;
            btnPlay.setText("⏸");
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

            // Film or incomplete context → nothing to do
            if (episodeId == null || currentSeasonId == null || currentNumEpisode == null)
                return;

            Episode next = episodeService.getNextEpisode(currentSeasonId, currentNumEpisode);
            System.out.println("next = " + (next != null ? next.getTitle() : "NULL"));

            if (next == null) {
                // ── End of season — check next season ──
                tryNextSeason();
                return;
            }

            showNextEpisodeButton(next);
            showBingeOverlay(next);
        });
    }

    // ── Try next season when current season is finished ────────
    private void tryNextSeason() {
        try {
            int serieId = episodeService.getSerieIdBySeasonId(currentSeasonId);
            Serie serie = serieService.getSerieById(serieId);
            if (serie == null || serie.getSeasons() == null) return;

            // Find current season index
            int currentSeasonNum = -1;
            for (Season s : serie.getSeasons()) {
                if (s.getSeasonId() == currentSeasonId) {
                    currentSeasonNum = s.getSeasonNum();
                    break;
                }
            }
            if (currentSeasonNum == -1) return;

            // Find next season
            Season nextSeason = null;
            for (Season s : serie.getSeasons()) {
                if (s.getSeasonNum() == currentSeasonNum + 1) {
                    nextSeason = s;
                    break;
                }
            }
            if (nextSeason == null || nextSeason.getEpisodes() == null
                    || nextSeason.getEpisodes().isEmpty()) return;

            Episode firstEp = nextSeason.getEpisodes().get(0);

            // Update season context
            this.currentSeasonId   = nextSeason.getSeasonId();
            this.currentNumEpisode = 0; // will be set properly in launchNextEpisode

            Platform.runLater(() -> {
                showNextEpisodeButton(firstEp);
                showBingeOverlay(firstEp);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Persistent "Next Episode" button ───────────────────────
    private void showNextEpisodeButton(Episode next) {
        btnNextEpisode.setVisible(true);
        btnNextEpisode.setManaged(true);
        btnNextEpisode.setText("▶ Next: E" + next.getNumEpisode());
        btnNextEpisode.setOnAction(e -> {
            stopBingeTimer();
            hideBingeOverlay();
            hideNextEpisodeButton();
            launchNextEpisode(next);
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

        Label upNextLabel = new Label("Next Episode");
        upNextLabel.setStyle("-fx-text-fill: #7a80a0; -fx-font-size: 13px;");

        Label epTitle = new Label(
            "E" + nextEpisode.getNumEpisode() + " — " + nextEpisode.getTitle());
        epTitle.setStyle(
            "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        epTitle.setWrapText(true);
        epTitle.setMaxWidth(340);

        // Arc countdown
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
            "-fx-text-fill: #00d4ff; -fx-font-size: 22px; -fx-font-weight: bold;");

        arcContainer.getChildren().addAll(arcBg, bingeArc, bingeCountdownLabel);

        Button btnWatch = new Button("▶  Watch Now");
        btnWatch.setStyle(
            "-fx-background-color: linear-gradient(to right, #00d4ff, #005fb8);" +
            "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;" +
            "-fx-background-radius: 25; -fx-cursor: hand; -fx-padding: 10 28;"
        );

        Button btnCancel = new Button("Cancel");
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
            // btnNextEpisode stays visible so user can still navigate
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
            bingeCountdownLabel = null;
            bingeArc = null;
        }
    }

    private void hideNextEpisodeButton() {
        if (btnNextEpisode != null) {
            btnNextEpisode.setVisible(false);
            btnNextEpisode.setManaged(false);
        }
    }

    // ── Launch Next Episode ────────────────────────────────────

    private void launchNextEpisode(Episode next) {
        stopBingeTimer();
        hideBingeOverlay();
        hideNextEpisodeButton();

        if (progressTimer != null) progressTimer.stop();
        if (saveTimer     != null) saveTimer.stop();

        videoReady = false;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        try {
            int serieId = episodeService.getSerieIdBySeasonId(currentSeasonId);
            Serie serie = serieService.getSerieById(serieId);

            // ── Update internal context ──
            this.filmId            = null;
            this.episodeId         = next.getEpId();
            this.currentSeasonId   = next.getSeasonId();
            this.currentNumEpisode = next.getNumEpisode();
            this.pendingUrl        = next.getVideoUrl();
            this.pendingTitle      = next.getTitle();

            // ── Update LecturePage UI then start playback ──
            Platform.runLater(() -> {
                if (parentController != null) {
                    try {
                        // Find season num for this episode's season
                        int seasonNum = 1;
                        if (serie != null && serie.getSeasons() != null) {
                            for (Season s : serie.getSeasons()) {
                                if (s.getSeasonId() == next.getSeasonId()) {
                                    seasonNum = s.getSeasonNum();
                                    break;
                                }
                            }
                        }

                        parentController.updateUI(
                            next.getTitle(),
                            next.getResume() != null
                                ? next.getResume()
                                : (serie != null ? serie.getSynopsis() : ""),
                            next.getDuration() + " min",
                            serie != null ? serie.getRating() : 0,
                            serie != null ? serie.getCasting() : "",
                            "S" + seasonNum + " - E" + next.getNumEpisode(),
                            next.getVideoUrl(),
                            next.getEpId()
                        );

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // ✅ Start playback AFTER UI update
                startPlayback();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Timers ─────────────────────────────────────────────────

    private void setupTimers() {
        idleTimer = new Timeline(
            new KeyFrame(Duration.seconds(3), e -> fadeUI(0)));
        idleTimer.setCycleCount(1);

        progressTimer = new Timeline(
            new KeyFrame(Duration.millis(500), e -> updateProgress()));
        progressTimer.setCycleCount(Timeline.INDEFINITE);

        saveTimer = new Timeline(
            new KeyFrame(Duration.seconds(10), e -> saveProgressToDB()));
        saveTimer.setCycleCount(Timeline.INDEFINITE);
    }

    // ── Mouse & Keyboard ───────────────────────────────────────

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
                    if (event.getClickCount() == 1)
                        togglePlay();
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

    // ── Sliders ────────────────────────────────────────────────

    private void setupSliders() {
        volumeSlider.valueProperty().addListener((obs, o, n) -> {
            if (mediaPlayer != null)
                mediaPlayer.setVolume(n.doubleValue() / 100.0);
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

    // ── Control Buttons ────────────────────────────────────────

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

    // ── Progress ───────────────────────────────────────────────

    private void updateProgress() {
        if (mediaPlayer == null || !videoReady) return;
        Duration current = mediaPlayer.getCurrentTime();
        Duration total   = mediaPlayer.getTotalDuration();
        if (total != null && total.toSeconds() > 0) {
            Platform.runLater(() -> {
                seekBar.setMax(total.toSeconds());
                seekBar.setValue(current.toSeconds());
                timeLabel.setText(
                    formatTime(current.toSeconds()) + " / " + formatTime(total.toSeconds()));
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
                filmProgressService.setCompleted(userId, filmId, totalTime);
            else
                filmProgressService.setInProgress(userId, filmId, currentTime);

        } else if (episodeId != null) {
            if (completed || currentTime >= totalTime - 2)
                episodeProgressService.markCompleted(userId, episodeId, totalTime);
            else
                episodeProgressService.markInProgress(userId, episodeId, currentTime);
        }
    }

    // ── Playback Controls ──────────────────────────────────────

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
        if (mediaPlayer != null)
            mediaPlayer.seek(
                mediaPlayer.getCurrentTime().add(Duration.seconds(seconds)));
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

    private void closePlayer() {
        stopBingeTimer();
        hideBingeOverlay();
        hideNextEpisodeButton();
        saveProgressToDB();
        videoReady = false;
        if (progressTimer != null) progressTimer.stop();
        if (idleTimer     != null) idleTimer.stop();
        if (saveTimer     != null) saveTimer.stop();
        if (mediaPlayer   != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        if (stage != null) stage.close();
    }

    // ── UI Helpers ─────────────────────────────────────────────

    private void fadeUI(double opacity) {
        if (!isPlaying && opacity == 0) return;
        FadeTransition ft1 = new FadeTransition(Duration.millis(300), topBar);
        FadeTransition ft2 = new FadeTransition(Duration.millis(300), bottomControls);
        ft1.setToValue(opacity); ft2.setToValue(opacity);
        ft1.play(); ft2.play();
    }

    private void updateVolumeIcon(double vol) {
        if      (isMuted || vol == 0) btnMute.setText("🔇");
        else if (vol < 0.4)           btnMute.setText("🔉");
        else                          btnMute.setText("🔊");
    }

    private void setupButtonHover(Button b) {
        ScaleTransition zoomIn  = new ScaleTransition(Duration.millis(120), b);
        ScaleTransition zoomOut = new ScaleTransition(Duration.millis(120), b);
        zoomIn.setToX(1.18);  zoomIn.setToY(1.18);
        zoomOut.setToX(1.0);  zoomOut.setToY(1.0);

        b.setOnMouseEntered(e -> {
            zoomIn.playFromStart();
            b.setStyle(b.getStyle().replace("-fx-text-fill:white;", "")
                + "-fx-text-fill:#00d4ff;");
        });
        b.setOnMouseExited(e -> {
            zoomOut.playFromStart();
            b.setStyle(b.getStyle().replace("-fx-text-fill:#00d4ff;", "")
                + "-fx-text-fill:white;");
        });
    }

    // ── Utilities ──────────────────────────────────────────────

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
        int s   = (int) totalSeconds;
        int h   = s / 3600;
        int m   = (s % 3600) / 60;
        int sec = s % 60;
        return h > 0
            ? String.format("%d:%02d:%02d", h, m, sec)
            : String.format("%d:%02d", m, sec);
    }
}