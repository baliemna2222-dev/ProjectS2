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
    @FXML private Region    seekFill, volumeFill;
    @FXML private StackPane seekBarWrapper, volumeSliderWrapper;
    private MediaPlayer mediaPlayer;
    private Stage       stage;
    private boolean     isPlaying           = true;
    private boolean     isMuted             = false;
    private boolean     wasPausedBeforeSeek = false;
    private boolean     videoReady          = false;

    private String      pendingUrl;
    private String      pendingTitle;
    private Integer filmId            = null;
    private Integer episodeId         = null;
    private Integer currentSeasonId   = null;
    private Integer currentNumEpisode = null;
    private final FeaturedService        featuredService        = new FeaturedService();
    private       FilmProgressService    filmProgressService;
    private       EpisodeProgressService episodeProgressService;
    private final EpisodeService         episodeService         = new EpisodeService();
    private final SerieService           serieService           = new SerieService();
    private LecturePageController parentController;
    private Timeline idleTimer, progressTimer, saveTimer;
    private Timeline  bingeTimer;
    private StackPane bingeOverlay;
    private Label     bingeCountdownLabel;
    private Arc       bingeArc;
    private StackPane endOverlay;

    private static final int    BINGE_COUNTDOWN = 10;
    private static final double MED_W = 960, MED_H = 540;
    private static final double THUMB_RADIUS = 8.0;

    //  INIT
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

        mediaView.fitWidthProperty().bind(rootPane.widthProperty());
        mediaView.fitHeightProperty().bind(rootPane.heightProperty());
        mediaView.setPreserveRatio(true);
        Platform.runLater(() -> {
            paintFill(false, volumeSlider.getValue() / 100.0);

            seekBarWrapper.widthProperty().addListener((obs, o, n) -> {
                if (n.doubleValue() > 0) {
                    double max = seekBar.getMax();
                    paintFill(true, max > 0 ? seekBar.getValue() / max : 0);
                }
            });
            volumeSliderWrapper.widthProperty().addListener((obs, o, n) -> {
                if (n.doubleValue() > 0) paintFill(false, volumeSlider.getValue() / 100.0);
            });
        });
    }

    private void paintFill(boolean isSeek, double pct) {
        pct = Math.min(1.0, Math.max(0.0, pct));
        Region    fill = isSeek ? seekFill       : volumeFill;
        StackPane wrap = isSeek ? seekBarWrapper : volumeSliderWrapper;
        if (fill == null || wrap == null) return;

        double totalW = wrap.getWidth();
        if (totalW <= 0) return;  // layout not ready — listener will repaint

        double usable = Math.max(0, totalW - THUMB_RADIUS * 2);
        double fillW  = THUMB_RADIUS + usable * pct;
        fill.setPrefWidth(fillW);
    }

    public void setParentController(LecturePageController c) { parentController = c; }

    public void loadVideo(String url, String title) {
        pendingUrl   = url;
        pendingTitle = title != null ? title : "";
    }

    public void setStage(Stage s) {
        stage = s;
        btnClose.setOnAction(e -> closePlayer());
        if (btnMiniPlayer != null) btnMiniPlayer.setOnAction(e -> toggleMiniPlayer());
        s.fullScreenProperty().addListener((obs, was, is) ->
            btnMiniPlayer.setText(is ? "⛶" : "⛶"));
    }

    public void setContext(Integer filmId, Integer episodeId) {
        this.filmId    = filmId;
        this.episodeId = episodeId;
    }

    public void setEpisodeContext(int seasonId, int numEpisode) {
        currentSeasonId   = seasonId;
        currentNumEpisode = numEpisode;
    }

    public void startPlayback() {
        if (pendingUrl == null || pendingUrl.isEmpty()) return;

        hideEndOverlay();
        btnNextEpisode.setVisible(false);
        btnNextEpisode.setManaged(false);

        titleLabel.setText(pendingTitle);
        loadingSpinner.setVisible(true);

        int startPos = 0, userId = Session.getUserId();
        if      (filmId    != null) startPos = filmProgressService.getLastPosition(userId, filmId);
        else if (episodeId != null) startPos = episodeProgressService.getEpisodeLastPosition(userId, episodeId);
        final int finalStart = startPos;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
            videoReady  = false;
        }

        String finalUrl = resolveUrl(pendingUrl);

        if (finalUrl == null || !finalUrl.contains(":")) {
            loadingSpinner.setVisible(false);
            return;
        }

        Media media;
        try {
            media = new Media(finalUrl);
        } catch (Exception e) {
            loadingSpinner.setVisible(false);
            e.printStackTrace();
            return;
        }

        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaView.setPreserveRatio(true);

        mediaPlayer.setOnReady(() -> {
            loadingSpinner.setVisible(false);
            videoReady = true;
            if (finalStart > 0) mediaPlayer.seek(Duration.seconds(finalStart));
            mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
            mediaPlayer.play();
            isPlaying = true;
            Platform.runLater(() -> {
                btnPlay.setText("⏸");
                progressTimer.play();
                saveTimer.play();
                paintFill(false, volumeSlider.getValue() / 100.0);
            });
        });

        mediaPlayer.setOnError(() -> {
            System.err.println("Media error: " + mediaPlayer.getError().getMessage());
            loadingSpinner.setVisible(false);
        });

        mediaPlayer.setOnEndOfMedia(this::handleEndOfMedia);
    }

    private void handleEndOfMedia() {
        if (progressTimer != null) progressTimer.stop();
        if (saveTimer     != null) saveTimer.stop();

        saveProgressToDB(true);

        Platform.runLater(() -> {
            isPlaying = false;
            btnPlay.setText("▶");

            if (episodeId == null || currentSeasonId == null || currentNumEpisode == null) {
                showEndOverlay(null);
                return;
            }
            Episode next = episodeService.getNextEpisode(currentSeasonId, currentNumEpisode);
            if (next == null) { tryNextSeason(); return; }
            showNextEpisodeButton(next);
            showBingeOverlay(next);
        });
    }

    private void showEndOverlay(Episode nextEpisode) {
        hideEndOverlay();
        endOverlay = new StackPane();
        endOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.80);");
        endOverlay.setPickOnBounds(true);

        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: rgba(6,12,22,0.97);" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: rgba(0,200,240,0.18);" +
            "-fx-border-radius: 18;" +
            "-fx-padding: 40 52 36 52;");
        card.setMaxWidth(420);

        Label check = new Label("✓");
        check.setStyle(
            "-fx-text-fill: #00c8f0; -fx-font-size: 38px; -fx-font-weight: bold;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,200,240,0.7),22,0,0,0);");

        Label sub = new Label(nextEpisode != null ? "Episode complete" : "You've finished watching");
        sub.setStyle("-fx-text-fill: rgba(255,255,255,0.4); -fx-font-size: 13px;");

        Label ttl = new Label(pendingTitle);
        ttl.setWrapText(true);
        ttl.setMaxWidth(340);
        ttl.setAlignment(Pos.CENTER);
        ttl.setStyle(
            "-fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;" +
            "-fx-text-alignment: center;");

        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setMaxWidth(180);
        divider.setStyle("-fx-background-color: rgba(0,200,240,0.12);");

        Button again = makeBtn("↺  Watch Again", true);
        again.setOnAction(e -> { hideEndOverlay(); replayCurrentVideo(); });

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.getChildren().add(again);

        if (nextEpisode != null) {
            Button goNext = makeBtn("Next Episode  ▶", false);
            final Episode fn = nextEpisode;
            goNext.setOnAction(e -> { hideEndOverlay(); launchNextEpisode(fn); });
            row.getChildren().add(goNext);
        }

        Button back = new Button("← Back to details");
        back.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: rgba(255,255,255,0.28);" +
            "-fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 4 0 0 0;");
        back.setOnMouseEntered(e -> back.setStyle(back.getStyle().replace("0.28", "0.55")));
        back.setOnMouseExited (e -> back.setStyle(back.getStyle().replace("0.55", "0.28")));
        back.setOnAction(e -> closePlayer());

        card.getChildren().addAll(check, sub, ttl, divider, row, back);
        endOverlay.getChildren().add(card);
        endOverlay.setOpacity(0);
        rootPane.getChildren().add(endOverlay);

        FadeTransition ft = new FadeTransition(Duration.millis(350), endOverlay);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    private Button makeBtn(String text, boolean primary) {
        String base = primary
            ? "-fx-background-color: #00c8f0; -fx-text-fill: #02060c;" +
              "-fx-font-size: 14px; -fx-font-weight: bold;" +
              "-fx-background-radius: 24; -fx-cursor: hand; -fx-padding: 11 28;"
            : "-fx-background-color: rgba(0,200,240,0.10); -fx-text-fill: #00c8f0;" +
              "-fx-font-size: 13px; -fx-font-weight: bold;" +
              "-fx-border-color: rgba(0,200,240,0.38); -fx-border-radius: 24;" +
              "-fx-background-radius: 24; -fx-cursor: hand; -fx-padding: 11 24;";
        String hover = primary
            ? base.replace("#00c8f0;", "#33d6f5;")
            : base.replace("0.10)", "0.22)").replace("0.38)", "0.65)").replace("#00c8f0;", "white;");

        Button btn = new Button(text);
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited (e -> btn.setStyle(base));
        return btn;
    }

    private void hideEndOverlay() {
        if (endOverlay != null) {
            rootPane.getChildren().remove(endOverlay);
            endOverlay = null;
        }
    }

    private void replayCurrentVideo() {
        if (mediaPlayer == null) return;
        mediaPlayer.seek(Duration.ZERO);
        mediaPlayer.play();
        isPlaying  = true;
        videoReady = true;
        btnPlay.setText("⏸");
        if (progressTimer != null) progressTimer.play();
        if (saveTimer     != null) saveTimer.play();
    }
//NEXT SEANSON
    private void tryNextSeason() {
        try {
            int serieId = episodeService.getSerieIdBySeasonId(currentSeasonId);
            Serie serie = serieService.getSerieById(serieId);
            if (serie == null || serie.getSeasons() == null) {
                Platform.runLater(() -> showEndOverlay(null));
                return;
            }

            int curNum = -1;
            for (Season s : serie.getSeasons())
                if (s.getSeasonId() == currentSeasonId) { curNum = s.getSeasonNum(); break; }
            if (curNum == -1) { Platform.runLater(() -> showEndOverlay(null)); return; }

            Season next = null;
            for (Season s : serie.getSeasons())
                if (s.getSeasonNum() == curNum + 1) { next = s; break; }

            if (next == null || next.getEpisodes() == null || next.getEpisodes().isEmpty()) {
                Platform.runLater(() -> showEndOverlay(null));
                return;
            }

            Episode first = next.getEpisodes().get(0);
            currentSeasonId   = next.getSeasonId();
            currentNumEpisode = 0;
            final Episode fe  = first;
            Platform.runLater(() -> { showNextEpisodeButton(fe); showBingeOverlay(fe); });

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> showEndOverlay(null));
        }
    }
//Next EPISODE
    private void showNextEpisodeButton(Episode next) {
        btnNextEpisode.setVisible(true);
        btnNextEpisode.setManaged(true);
        btnNextEpisode.setText("▶  Next: E" + next.getNumEpisode());
        btnNextEpisode.setOnAction(e -> {
            stopBingeTimer();
            hideBingeOverlay();
            hideNextEpisodeButton();
            launchNextEpisode(next);
        });
    }

    private void showBingeOverlay(Episode nextEpisode) {
        bingeOverlay = new StackPane();
        bingeOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.72);");
        bingeOverlay.setPickOnBounds(true);

        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: rgba(8,16,26,0.96);" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(0,200,240,0.22);" +
            "-fx-border-radius: 16;" +
            "-fx-padding: 34 46;");
        card.setMaxWidth(400);

        Label up = new Label("Up next");
        up.setStyle("-fx-text-fill: rgba(255,255,255,0.4); -fx-font-size: 12px;");

        Label ep = new Label("E" + nextEpisode.getNumEpisode() + "  —  " + nextEpisode.getTitle());
        ep.setStyle("-fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
        ep.setWrapText(true);
        ep.setMaxWidth(320);

        StackPane arcContainer = new StackPane();
        arcContainer.setPrefSize(80, 80);
        Circle arcBg = new Circle(0, 0, 36);
        arcBg.setFill(Color.TRANSPARENT);
        arcBg.setStroke(Color.web("#1e2a38"));
        arcBg.setStrokeWidth(4);
        bingeArc = new Arc(0, 0, 32, 32, 90, 360);
        bingeArc.setType(ArcType.OPEN);
        bingeArc.setFill(Color.TRANSPARENT);
        bingeArc.setStroke(Color.web("#00c8f0"));
        bingeArc.setStrokeWidth(4);
        bingeArc.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        bingeArc.setLength(360); 

        bingeCountdownLabel = new Label(String.valueOf(BINGE_COUNTDOWN));
        bingeCountdownLabel.setStyle(
            "-fx-text-fill: #00c8f0; -fx-font-size: 20px; -fx-font-weight: bold;");

        arcContainer.getChildren().addAll(arcBg, bingeArc, bingeCountdownLabel);

        Button watch  = makeBtn("▶  Watch Now", true);
        Button cancel = new Button("Cancel");
        cancel.setStyle(
            "-fx-background-color: rgba(255,255,255,0.06);" +
            "-fx-text-fill: rgba(255,255,255,0.45); -fx-font-size: 13px;" +
            "-fx-background-radius: 18; -fx-cursor: hand; -fx-padding: 8 18;");

        HBox row = new HBox(12, watch, cancel);
        row.setAlignment(Pos.CENTER);

        card.getChildren().addAll(up, ep, arcContainer, row);
        bingeOverlay.getChildren().add(card);
        rootPane.getChildren().add(bingeOverlay);

        watch.setOnAction(e  -> {
            stopBingeTimer(); hideBingeOverlay(); hideNextEpisodeButton();
            launchNextEpisode(nextEpisode);
        });
        cancel.setOnAction(e -> {
            stopBingeTimer(); hideBingeOverlay();
            showEndOverlay(nextEpisode);
        });

        startBingeTimer(nextEpisode);
    }

    private void startBingeTimer(Episode nextEpisode) {
        final int[] rem = {BINGE_COUNTDOWN};
        bingeTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            Platform.runLater(() -> {
                final int snap = --rem[0];

                if (bingeCountdownLabel != null)
                    bingeCountdownLabel.setText(String.valueOf(snap));
                if (bingeArc != null)
                    bingeArc.setLength(360.0 * snap / BINGE_COUNTDOWN);
                if (snap <= 0) {
                    stopBingeTimer();
                    hideBingeOverlay();
                    hideNextEpisodeButton();
                    launchNextEpisode(nextEpisode);
                }
            });
        }));
        bingeTimer.setCycleCount(Timeline.INDEFINITE); 
        bingeTimer.play();
    }
    private void stopBingeTimer() {
        if (bingeTimer != null) { bingeTimer.stop(); bingeTimer = null; }
    }

    private void hideBingeOverlay() {
        if (bingeOverlay != null) {
            rootPane.getChildren().remove(bingeOverlay);
            bingeOverlay        = null;
            bingeCountdownLabel = null;
            bingeArc            = null;
        }
    }
    private void hideNextEpisodeButton() {
        if (btnNextEpisode != null) {
            btnNextEpisode.setVisible(false);
            btnNextEpisode.setManaged(false);
        }
    }

    private void launchNextEpisode(Episode next) {
        stopBingeTimer();
        hideBingeOverlay();
        hideEndOverlay();
        hideNextEpisodeButton();
        if (progressTimer != null) progressTimer.stop();
        if (saveTimer     != null) saveTimer.stop();
        videoReady = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        filmId            = null;
        episodeId         = next.getEpId();
        currentSeasonId   = next.getSeasonId();
        currentNumEpisode = next.getNumEpisode();
        pendingUrl        = next.getVideoUrl();
        pendingTitle      = next.getTitle();

        Platform.runLater(() -> {
            if (parentController != null) {
                try {
                    int serieId = episodeService.getSerieIdBySeasonId(currentSeasonId);
                    Serie serie = serieService.getSerieById(serieId);

                    int sn = 1;
                    if (serie != null && serie.getSeasons() != null)
                        for (Season s : serie.getSeasons())
                            if (s.getSeasonId() == next.getSeasonId()) { sn = s.getSeasonNum(); break; }

                    parentController.updateUI(
                        next.getTitle(),
                        next.getResume() != null ? next.getResume()
                            : (serie != null ? serie.getSynopsis() : ""),
                        next.getDuration() + " min",
                        serie != null ? serie.getRating()  : 0,
                        serie != null ? serie.getCasting() : "",
                        "S" + sn + " - E" + next.getNumEpisode(),
                        next.getVideoUrl(),
                        next.getEpId());
                } catch (Exception e) { e.printStackTrace(); }
            }
            startPlayback();
        });
    }

    private void setupTimers() {
        idleTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> fadeUI(0)));
        idleTimer.setCycleCount(1);

        progressTimer = new Timeline(new KeyFrame(Duration.millis(500), e -> updateProgress()));
        progressTimer.setCycleCount(Timeline.INDEFINITE);

        saveTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> saveProgressToDB()));
        saveTimer.setCycleCount(Timeline.INDEFINITE);
    }

//short cuts
    private void setupMouseAndKeyboardEvents() {
        rootPane.setOnMouseMoved(e -> { fadeUI(1); idleTimer.playFromStart(); });
        bottomControls.setOnMouseEntered(e -> { fadeUI(1); idleTimer.stop(); });
        bottomControls.setOnMouseExited(e  -> { if (isPlaying) idleTimer.playFromStart(); });

        Platform.runLater(() -> {
            Region overlay = new Region();
            overlay.setStyle("-fx-background-color: transparent;");
            overlay.setPickOnBounds(true);
            rootPane.getChildren().add(1, overlay);
            overlay.setOnMouseClicked(ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    if (ev.getClickCount() == 1) togglePlay();
                    else if (ev.getClickCount() == 2 && stage != null)
                        stage.setFullScreen(!stage.isFullScreen());
                }
                rootPane.requestFocus();
            });
            overlay.setOnMouseMoved(e -> { fadeUI(1); idleTimer.playFromStart(); });
        });

        rootPane.setFocusTraversable(true);
        rootPane.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            switch (ev.getCode()) {
                case SPACE  -> { togglePlay();  ev.consume(); }
                case F      -> { if (stage != null) stage.setFullScreen(!stage.isFullScreen()); ev.consume(); }
                case M      -> { toggleMute();  ev.consume(); }
                case RIGHT  -> { seek(10);      ev.consume(); }
                case LEFT   -> { seek(-10);     ev.consume(); }
                case R      -> { hideEndOverlay(); replayCurrentVideo(); ev.consume(); }
                case ESCAPE -> { closePlayer(); ev.consume(); }
                default     -> {}
            }
        });
    }
    private void setupSliders() {
        volumeSlider.valueProperty().addListener((obs, o, n) -> {
            double vol = n.doubleValue() / 100.0;
            if (mediaPlayer != null) mediaPlayer.setVolume(vol);
            updateVolumeIcon(vol);
            paintFill(false, vol);
        });

        seekBar.valueProperty().addListener((obs, o, n) -> {
            double max = seekBar.getMax();
            if (max > 0) paintFill(true, n.doubleValue() / max);
        });

        seekBar.setOnMousePressed(e -> {
            if (mediaPlayer == null) return;
            wasPausedBeforeSeek = !isPlaying;
            mediaPlayer.pause();
        });

        seekBar.setOnMouseReleased(e -> {
            if (mediaPlayer == null) return;
            mediaPlayer.seek(Duration.seconds(seekBar.getValue()));
            if (!wasPausedBeforeSeek) {
                mediaPlayer.play();
                isPlaying = true;
                btnPlay.setText("⏸");
            }
        });
    }

    private void setupControlButtons() {
        btnPlay.setOnAction(e    -> togglePlay());
        btnMute.setOnAction(e    -> toggleMute());
        btnForward.setOnAction(e -> seek(10));
        btnRewind.setOnAction(e  -> seek(-10));

        for (Button b : new Button[]{btnPlay, btnClose, btnMute, btnForward, btnRewind})
            setupButtonHover(b);
        if (btnMiniPlayer != null) setupButtonHover(btnMiniPlayer);
    }

    private void updateProgress() {
        if (mediaPlayer == null || !videoReady) return;
        Duration cur = mediaPlayer.getCurrentTime();
        Duration tot = mediaPlayer.getTotalDuration();
        if (tot != null && tot.toSeconds() > 0) {
            double pct = cur.toSeconds() / tot.toSeconds();
            Platform.runLater(() -> {
                seekBar.setMax(tot.toSeconds());
                seekBar.setValue(cur.toSeconds());
                timeLabel.setText(formatTime(cur.toSeconds()) + " / " + formatTime(tot.toSeconds()));
                paintFill(true, pct);
            });
        }
    }

    private void saveProgressToDB() { saveProgressToDB(false); }
    private void saveProgressToDB(boolean completed) {
        if (mediaPlayer == null) return;
        int uid = Session.getUserId();
        int cur = (int) mediaPlayer.getCurrentTime().toSeconds();
        int tot = (int) mediaPlayer.getTotalDuration().toSeconds();
        if (filmId != null) {
            if (completed || cur >= tot - 2) filmProgressService.setCompleted(uid, filmId, tot);
            else                             filmProgressService.setInProgress(uid, filmId, cur);
        } else if (episodeId != null) {
            if (completed || cur >= tot - 2) episodeProgressService.markCompleted(uid, episodeId, tot);
            else                             episodeProgressService.markInProgress(uid, episodeId, cur);
        }
    }
//PLAYBACK BUTTON
    private void togglePlay() {
        if (mediaPlayer == null) return;
        if (isPlaying) {
            mediaPlayer.pause();
            btnPlay.setText("▶");
            saveProgressToDB();
            idleTimer.stop();
            fadeUI(1);
        } else {
            mediaPlayer.play();
            btnPlay.setText("⏸");
            idleTimer.playFromStart();
        }
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
            stage.setWidth(MED_W);
            stage.setHeight(MED_H);
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
        hideEndOverlay();
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

    private void fadeUI(double opacity) {
        FadeTransition ft1 = new FadeTransition(Duration.millis(280), topBar);
        FadeTransition ft2 = new FadeTransition(Duration.millis(280), bottomControls);
        ft1.setToValue(opacity);
        ft2.setToValue(opacity);
        ft1.play();
        ft2.play();
    }

    private void updateVolumeIcon(double vol) {
        if      (isMuted || vol == 0) btnMute.setText("🔇");
        else if (vol < 0.4)           btnMute.setText("🔉");
        else                          btnMute.setText("🔊");
    }

    private void setupButtonHover(Button b) {
        ScaleTransition in  = new ScaleTransition(Duration.millis(110), b);
        ScaleTransition out = new ScaleTransition(Duration.millis(110), b);
        in.setToX(1.15); in.setToY(1.15);
        out.setToX(1.0); out.setToY(1.0);
        b.setOnMouseEntered(e -> { in.playFromStart(); b.setOpacity(1.0); });
        b.setOnMouseExited (e -> { out.playFromStart(); b.setOpacity(0.75); });
        b.setOpacity(0.75);
    }

    private String resolveUrl(String url) {
        if (url == null || url.isBlank()) return null;
        try {
            if (url.startsWith("http://") || url.startsWith("https://")
                    || url.startsWith("file:///")) return url;
            java.net.URL res = getClass().getResource(url);
            if (res != null) return res.toExternalForm();
            File f = new File(url);
            if (f.exists()) return f.toURI().toString();
            return url;
        } catch (Exception e) { return url; }
    }

    private String formatTime(double totalSecs) {
        int s = (int) totalSecs, h = s / 3600, m = (s % 3600) / 60, sec = s % 60;
        return h > 0
            ? String.format("%d:%02d:%02d", h, m, sec)
            : String.format("%d:%02d", m, sec);
    }
}