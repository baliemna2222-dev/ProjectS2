package JStream.controller;

import java.awt.Scrollbar;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.FeaturedItemProgress;
import JStream.entity.Film;
import JStream.entity.MyListManager;
import JStream.entity.Season;
import JStream.entity.Serie;
import JStream.entity.Session;
import JStream.entity.WatchStatus;
import JStream.service.EpisodeProgressService;
import JStream.service.FeaturedService;
import JStream.service.FilmProgressService;
import JStream.service.MylistService;
import JStream.utils.ImageUtil;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class CardController {
	
    @FXML private ImageView poster;
    @FXML private Label typeBadge;
    @FXML private Button playBtn;
    @FXML private Button addBtn;
    @FXML private StackPane rootPane;
    @FXML private Label starsLabel;
    @FXML private Rectangle progressBg;
    private Timeline autoSlide;
    private FeaturedItem currentItem;
    private FeaturedController featuredController;
    private FeaturedService featuredService = new FeaturedService();
    private EpisodeProgressService episodeProgressService = new EpisodeProgressService();

    FilmProgressService filmProgressService = new FilmProgressService(featuredService);
    public void setFeaturedController(FeaturedController controller) {
        this.featuredController = controller;
    }
    @FXML
    public void initialize() {
       setupButtonHover(addBtn);
       setupButtonHover(playBtn);
       
       MyListManager.getInstance().addListener((filmId, serieId) -> {
    	    Platform.runLater(() -> {
    	        if (currentItem != null) {
    	            int currentFilmId = "film".equalsIgnoreCase(currentItem.getType()) ? currentItem.getId() : 0;
    	            int currentSerieId = "serie".equalsIgnoreCase(currentItem.getType()) ? currentItem.getSerieId() : 0;

    	            // If the updated item matches this controller's current item, refresh the button
    	            if (currentFilmId == filmId && currentSerieId == serieId) {
    	                updateAddButton(addBtn, currentItem);
    	            }
    	        }
    	    });
    	});
       
       
          }

    /** Fade buttons in or out */
   
    
    public void setItem(FeaturedItem item) {
        this.currentItem = item;
     // Make the whole card clickable like the play button
        poster.getParent().setOnMouseClicked(e -> {
            if (autoSlide != null) autoSlide.pause(); // pause carousel

            // Same logic as play button
            String type = currentItem.getType().toLowerCase();
            if (type.equals("film")) {
                showFilmPopup(currentItem);
            } else if (type.equals("serie")) {
                showSeriePopup(currentItem);
            }
        });
        // ------------------ Poster ------------------
        poster.setImage(ImageUtil.load(item.getPosterUrl()));
        // ------------------ Type Badge ------------------
        if (item.getSerieId() != 0) {
            typeBadge.setText("SERIE");
        } else {
            typeBadge.setText("FILM");
        }

        // ------------------ Button Actions ------------------
        playBtn.setOnAction(e -> {
            if (autoSlide != null) autoSlide.pause(); // pause carousel

            // Open the correct popup based on type
            String type = currentItem.getType().toLowerCase();
            if (type.equals("film")) {
                showFilmPopup(currentItem);    // open film popup
            } else if (type.equals("serie")) {
                showSeriePopup(currentItem);   // open series popup
            }
        });
        addBtn.setOnAction(e -> handleAddToList());
        updateAddButton(addBtn, item);
        starsLabel.setText(getStars(item.getRating()));
        show(playBtn);
        show(addBtn);
        show(starsLabel);
        show(typeBadge);
        hide(progressFill);
    }
    private MylistService mylistService = new MylistService();
    

    // --- Toggle Add/Remove ---
    @FXML
    private void handleAddToList() {
        if (currentItem == null) return;

        int userId = Session.getUserId();
        int filmId = "film".equalsIgnoreCase(currentItem.getType()) ? currentItem.getId() : 0;
        int serieId = "serie".equalsIgnoreCase(currentItem.getType()) ? currentItem.getSerieId() : 0;

        boolean alreadyAdded = mylistService.isInList(userId, filmId, serieId);

        if (alreadyAdded) {
            mylistService.removeItem(userId, filmId, serieId);
        } else {
            mylistService.addItem(userId, filmId, serieId);
        }

        // Update this controller's button immediately
        updateAddButton(addBtn, currentItem);

        // Notify all other controllers to update
        MyListManager.getInstance().notifyItemUpdated(filmId, serieId);
    }

    /** Updates the + button to show + or ✔ depending on whether the item is in the list */
    private void updateAddButton(Button button, FeaturedItem item) {
        int userId = Session.getUserId();
        int filmId = 0;
        int serieId = 0;

        if ("film".equalsIgnoreCase(item.getType())) {
            filmId = item.getId();
        } else if ("serie".equalsIgnoreCase(item.getType())) {
            serieId = item.getSerieId();
        }

        if (mylistService.isInList(userId, filmId, serieId)) {
            button.setText("✔");
            pumpButton(button); // optional animation
        } else {
            button.setText("+");
            pumpButton(button);
        }
    }

    /** Simple pump animation for the button */
    private void pumpButton(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }
    private void setupButtonHover(Button button) {
        // original style
        String style = """
            -fx-background-color: rgba(0,0,0,0.0);
            -fx-text-fill: #1E90FF;
            -fx-font-size: 10px;
            -fx-font-weight: bold;
            -fx-background-radius: 25;
            -fx-border-color: #1E90FF;
            -fx-border-width: 2;
            -fx-border-radius: 25;
            -fx-cursor: hand;
            """;
        button.setStyle(style);

        // Hover animation: scale up
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), button);
        scaleUp.setToX(1.1); // 10% bigger
        scaleUp.setToY(1.1);

        // Hover animation: scale back
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), button);
        scaleDown.setToX(1.0); // back to original size
        scaleDown.setToY(1.0);

        button.setOnMouseEntered(e -> {
            scaleDown.stop(); // stop any running shrink animation
            scaleUp.playFromStart(); // play grow animation
        });

        button.setOnMouseExited(e -> {
            scaleUp.stop(); // stop any running grow animation
            scaleDown.playFromStart(); // play shrink animation
        });
    }
    private String getStars(int rating) {
        rating = Math.max(0, Math.min(5, rating)); // ensure rating is 0-5
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rating; i++) sb.append("★");  // filled star
        for (int i = rating; i < 5; i++) sb.append("☆");  // empty star
        return sb.toString();
    }
    public void showFilmPopup(FeaturedItem item) {
        Stage popup = new Stage();
        popup.initOwner(rootPane.getScene().getWindow());
        popup.initModality(Modality.WINDOW_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0); -fx-background-radius: 4;");
        root.setPadding(new Insets(20));
        root.setPrefSize(1000, 600);

        VBox content = new VBox(20);
        content.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 4; -fx-padding: 20;");
        content.setMaxWidth(Double.MAX_VALUE);

        // Close button
        Button close = new Button("✕");
        close.setStyle("-fx-background-color:#008cff; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:50%; -fx-padding:5 10;");
        close.setOnAction(e -> popup.close());

        HBox topBar = new HBox(close);
        topBar.setAlignment(Pos.TOP_RIGHT);
        content.getChildren().add(topBar);

        try {
            if ("film".equalsIgnoreCase(item.getType())) {
                Film film = featuredService.getFilmDetails(item.getId());

                HBox filmBox = new HBox(30);
                filmBox.setAlignment(Pos.TOP_LEFT);
                filmBox.setMaxWidth(Double.MAX_VALUE);

                ImageView poster = new ImageView();
                poster.setFitWidth(300);
                poster.setFitHeight(450);

                poster.setImage(ImageUtil.load(film.getPoster_url()));
                // Right info column
                VBox right = new VBox(15);
                right.setAlignment(Pos.TOP_LEFT);
                right.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(right, Priority.ALWAYS);

                ImageView titleImage = new ImageView();
                titleImage.setFitHeight(150);
                titleImage.setPreserveRatio(true);

                titleImage.setImage(ImageUtil.load(film.getTitle_image_url()));
                // Stars
                HBox starsBox = new HBox(3);
                int fullStars = film.getRating();
                for (int i = 0; i < 5; i++) {
                    Label star = new Label("★");
                    star.setStyle("-fx-font-size:24; -fx-font-weight:bold;");
                    star.setTextFill(i < fullStars ? Color.DEEPSKYBLUE : Color.LIGHTGRAY);
                    starsBox.getChildren().add(star);
                }

                // Duration
                int totalMinutes = (int) film.getDuration();
                int hours = totalMinutes / 60;
                int minutes = totalMinutes % 60;
                Label duration = new Label("⏱ " + hours + "h " + minutes + "min");
                duration.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:16;");

                // Casting
                Label casting = new Label("Casting: " + (film.getCasting() != null ? film.getCasting() : ""));
                casting.setWrapText(true);
                casting.setMaxWidth(600);
                casting.setStyle("-fx-text-fill:#cccccc; -fx-font-size:16;");

                // Categories
                String cats = film.getCategories() != null ?
                        film.getCategories().stream().map(c -> c.getName()).reduce((a, b) -> a + " • " + b).orElse("")
                        : "";
                Label categories = new Label("Categories: " + cats);
                categories.setWrapText(true);
                categories.setMaxWidth(600);
                categories.setStyle("-fx-text-fill:#00aaff; -fx-font-size:16;");

                // Synopsis
                Label synopsis = new Label(film.getSynopsis() != null ? film.getSynopsis() : "");
                synopsis.setWrapText(true);
                synopsis.setMaxWidth(600);
                synopsis.setStyle("-fx-text-fill:#cccccc;-fx-font-size:16;");

                right.getChildren().addAll(titleImage, starsBox, duration, casting, categories, synopsis);
                filmBox.getChildren().addAll(poster, right);
                content.getChildren().add(filmBox);

                // Buttons
                Button trailer = new Button("Trailer");
                trailer.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-color: #00aaff;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-text-fill: #00aaff;" +
                        "-fx-font-size: 20;" +
                        "-fx-padding: 6 20;" +
                        "-fx-effect: dropshadow(gaussian,#00aaff,10,0,0,0);"
                );
                trailer.setOnAction(e -> showTrailerPopup(film, 0));

                Button play = new Button("▶");
                play.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-color: #00aaff;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-text-fill: #00aaff;" +
                        "-fx-font-size: 20;" +
                        "-fx-padding: 6 20;" +
                        "-fx-effect: dropshadow(gaussian,#00aaff,10,0,0,0);"
                );
                play.setOnAction(e -> goToLecturePageFilm(film.getFilm_id()));
                addHoverAnimation(close);
                addHoverAnimation(trailer);
                ScaleTransition pulse = new ScaleTransition(Duration.millis(800), play);
                pulse.setFromX(1); pulse.setFromY(1);
                pulse.setToX(1.10); pulse.setToY(1.10);
                pulse.setCycleCount(Animation.INDEFINITE);
                pulse.setAutoReverse(true);
                pulse.play();

                // Wrap buttons in HBox
                HBox buttonBox = new HBox(20, trailer, play);
                buttonBox.setAlignment(Pos.CENTER_RIGHT);
                buttonBox.setMaxWidth(Double.MAX_VALUE);

                content.getChildren().add(buttonBox);

                // Marquee effect for long labels
                for (Label lbl : new Label[]{casting, categories, synopsis}) {
                    lbl.widthProperty().addListener((obs, oldVal, newVal) -> {
                        if (lbl.getWidth() > 600) {
                            TranslateTransition marquee = new TranslateTransition(Duration.seconds(15), lbl);
                            marquee.setFromX(0);
                            marquee.setToX(-lbl.getWidth());
                            marquee.setCycleCount(Animation.INDEFINITE);
                            marquee.setInterpolator(Interpolator.LINEAR);
                            marquee.play();
                        }
                    });
                }
             // Status label
             // 🎯 Status label
                Label statusLabel = new Label();
                statusLabel.setStyle(
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 4 10;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-radius: 20;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4,0,0,2);"
                );

                int userId = Session.getUserId();
                int filmId = film.getFilm_id();
                int dur = (int) film.getDuration();

                WatchStatus status;

                // 🔥 Determine status
                if (!filmProgressService.exists(userId, filmId)) {
                    status = WatchStatus.NOT_STARTED;
                } else {
                    int lastPosition = filmProgressService.getLastPosition(userId, filmId);

                    if (lastPosition >= dur - 2) {
                        status = WatchStatus.COMPLETED;
                    } else {
                        status = WatchStatus.IN_PROGRESS;
                    }
                }

                // 🏷 Apply text
                statusLabel.setText(status.toString());

                // 🎨 Apply color
                switch (status) {
                    case NOT_STARTED -> statusLabel.setStyle(
                        statusLabel.getStyle() + "-fx-background-color: #777777;"
                    );
                    case IN_PROGRESS -> statusLabel.setStyle(
                        statusLabel.getStyle() + "-fx-background-color: #008cff;"
                    );
                    case COMPLETED -> statusLabel.setStyle(
                        statusLabel.getStyle() + "-fx-background-color: #00c853;"
                    );
                }

                // ✅ Add to UI
                right.getChildren().add(statusLabel);
                play.setOnAction(e -> {
                	 popup.close(); 
                    goToLecturePageFilm(film.getFilm_id()); // start playback
                });
                root.getChildren().add(content);
                FadeTransition fade = new FadeTransition(Duration.millis(400), root);
                fade.setFromValue(0); fade.setToValue(1); fade.play();

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
    }
   	private void addHoverAnimation(Button btn) {
	    ScaleTransition up = new ScaleTransition(Duration.millis(120), btn);
	    up.setToX(1.15); up.setToY(1.15);

	    ScaleTransition down = new ScaleTransition(Duration.millis(120), btn);
	    down.setToX(1.0); down.setToY(1.0);

	    btn.setOnMouseEntered(e -> up.playFromStart());
	    btn.setOnMouseExited(e -> down.playFromStart());
	}

   	private void showTrailerPopup(Object item, int seasonIndex) {
        String trailerUrl = null;

        if (item instanceof Film) {
            trailerUrl = ((Film) item).getTrailer_url();
        } else if (item instanceof Serie) {
            Serie serie = (Serie) item;
            if (serie.getSeasons() != null && !serie.getSeasons().isEmpty()) {
                if (seasonIndex < 0 || seasonIndex >= serie.getSeasons().size()) seasonIndex = 0;
                trailerUrl = serie.getSeasons().get(seasonIndex).getTrailerUrl();
            }
        }

        String videoPath;

        if (trailerUrl.startsWith("http")) {
            // Online video
            videoPath = trailerUrl;

        } else {
            java.io.File file = new java.io.File(trailerUrl);

            if (file.exists()) {
                // LOCAL FILE (Windows path)
                videoPath = file.toURI().toString();  // ✅ VERY IMPORTANT
            } else {
                // Try classpath
                URL resource = getClass().getResource(
                    trailerUrl.startsWith("/") ? trailerUrl : "/" + trailerUrl
                );

                if (resource != null) {
                    videoPath = resource.toExternalForm();
                } else {
                    System.out.println("Video NOT FOUND: " + trailerUrl);
                    return;
                }
            }
        }

        System.out.println("FINAL PATH = " + videoPath);
        // ── Modern HTML5 player with custom controls ──────────────────
        String html =
        		"<!DOCTYPE html><html><head><style>" +

        		"  * { margin:0; padding:0; box-sizing:border-box; }" +

        		"  body { background:#000; overflow:hidden; display:flex; flex-direction:column;" +
        		"         width:100vw; height:100vh; font-family:'Segoe UI',sans-serif; }" +

        		"  video { flex:1; width:100%; min-height:0; object-fit:contain; display:block; cursor:pointer; }" +

        		/* ================= CONTROLS BAR ================= */
        		"  #bar {" +
        		"    background: linear-gradient(to top, rgba(0,0,0,0.98), rgba(0,20,40,0.85));" +
        		"    padding: 6px 18px 10px;" +
        		"    display:flex; flex-direction:column; gap:7px;" +
        		"  }" +

        		/* ================= PROGRESS BAR ================= */
        		"  #prog-wrap {" +
        		"    position:relative; height:4px; background:rgba(255,255,255,0.08);" +
        		"    border-radius:4px; cursor:pointer; transition:height 0.15s;" +
        		"  }" +

        		"  #prog-wrap:hover { height:6px; }" +

        		"  #prog-buf { position:absolute; height:100%; border-radius:4px;" +
        		"    background:rgba(0,140,255,0.2); width:0%; pointer-events:none; }" +

        		"  #prog-fill { position:absolute; height:100%; border-radius:4px;" +
        		"    background:linear-gradient(to right,#002b55,#00aaff); width:0%; pointer-events:none; }" +

        		"  #prog-thumb { position:absolute; top:50%; width:13px; height:13px;" +
        		"    background:#00aaff; border-radius:50%; transform:translate(-50%,-50%);" +
        		"    box-shadow:0 0 10px rgba(0,170,255,0.9); left:0%;" +
        		"    opacity:0; transition:opacity 0.15s; pointer-events:none; }" +

        		"  #prog-wrap:hover #prog-thumb { opacity:1; }" +

        		/* ================= ROW ================= */
        		"  #row { display:flex; align-items:center; gap:10px; }" +

        		"  .btn {" +
        		"    background:rgba(0,0,0,0.6);" +
        		"    border:none;" +
        		"    cursor:pointer;" +
        		"    color:#00aaff;" +
        		"    font-size:13px;" +
        		"    border-radius:7px;" +
        		"    padding:4px 9px;" +
        		"    transition:0.2s;" +
        		"    display:flex; align-items:center; justify-content:center;" +
        		"    min-width:32px; height:30px;" +
        		"  }" +

        		"  .btn:hover { background:rgba(0,140,255,0.25); color:#ffffff; }" +
        		"  .btn:active { background:rgba(0,100,180,0.4); }" +

        		/* ================= TIME ================= */
        		"  #time { font-size:11px; color:rgba(180,200,255,0.75); min-width:105px; letter-spacing:0.3px; }" +

        		/* ================= VOLUME ================= */
        		"  #vol-wrap { display:flex; align-items:center; gap:6px; }" +

        		"  #vol { -webkit-appearance:none; width:72px; height:3px;" +
        		"    background:rgba(255,255,255,0.15); border-radius:3px; outline:none; cursor:pointer; }" +

        		"  #vol::-webkit-slider-thumb { -webkit-appearance:none; width:12px; height:12px;" +
        		"    background:#00aaff; border-radius:50%; box-shadow:0 0 6px rgba(0,170,255,0.7); }" +

        		"  #spacer { flex:1; }" +

        		/* ================= SPEED ================= */
        		"  #speed { background:rgba(0,0,0,0.7); border:none;" +
        		"    color:#00aaff; font-size:11px; padding:4px 7px; border-radius:6px;" +
        		"    cursor:pointer; outline:none; }" +

        		"  #speed:hover { background:rgba(0,140,255,0.2); }" +

        		/* ================= TOOLTIP ================= */
        		"  #tip { position:fixed; bottom:72px; left:50%; transform:translateX(-50%);" +
        		"    background:rgba(0,0,0,0.85); border:none;" +
        		"    color:#00aaff; font-size:11px; padding:4px 14px; border-radius:20px;" +
        		"    opacity:0; transition:opacity 0.25s; pointer-events:none; }" +

        		"  ::-webkit-scrollbar { display:none; }" +

        		"</style></head><body>" +
        		"<video id='v'>" +
        		"  <source src='" + videoPath + "' type='video/mp4'>" +
        		"  <source src='" + videoPath + "' type='video/webm'>" +
        		"  <source src='" + videoPath + "' type='video/ogg'>" +
        		"  <source src='" + videoPath + "' type='video/avi'>" +
        		"  <source src='" + videoPath + "' type='video/mov'>" +
        		"  <source src='" + videoPath + "' type='video/mkv'>" +
        		"</video>" +
        		"<div id='bar'>" +
        		"  <div id='prog-wrap' onmousedown='seekStart(event)' onmousemove='seekHover(event)'>" +
        		"    <div id='prog-buf'></div>" +
        		"    <div id='prog-fill'></div>" +
        		"    <div id='prog-thumb'></div>" +
        		"  </div>" +

        		"  <div id='row'>" +
        		"    <button class='btn' onclick='togglePlay()' id='playBtn'>&#9654;</button>" +
        		"    <button class='btn' onclick='skip(-10)'>&#9198; 10</button>" +
        		"    <button class='btn' onclick='skip(10)'>10 &#9197;</button>" +

        		"    <div id='vol-wrap'>" +
        		"      <button class='btn' onclick='toggleMute()' id='muteBtn'>&#128266;</button>" +
        		"      <input id='vol' type='range' min='0' max='1' step='0.02' value='1' oninput='setVol(this.value)'>" +
        		"    </div>" +

        		"    <span id='time'>0:00 / 0:00</span>" +

        		"    <div id='spacer'></div>" +

        		"    <select id='speed' onchange='setSpeed(this.value)'>" +
        		"      <option value='0.25'>0.25×</option>" +
        		"      <option value='0.5'>0.5×</option>" +
        		"      <option value='0.75'>0.75×</option>" +
        		"      <option value='1' selected>1×</option>" +
        		"      <option value='1.25'>1.25×</option>" +
        		"      <option value='1.5'>1.5×</option>" +
        		"      <option value='2'>2×</option>" +
        		"    </select>" +
        		"  </div>" +
        		"</div>" +

        		"<div id='tip'></div>" +

        		"<script>" +
        		"var v=document.getElementById('v')," +
        		"pFill=document.getElementById('prog-fill')," +
        		"pBuf=document.getElementById('prog-buf')," +
        		"pThumb=document.getElementById('prog-thumb')," +
        		"playBtn=document.getElementById('playBtn')," +
        		"muteBtn=document.getElementById('muteBtn')," +
        		"timeEl=document.getElementById('time')," +
        		"tip=document.getElementById('tip')," +
        		"seeking=false, tipTimer;" +

        		"v.addEventListener('timeupdate',function(){" +
        		"  if(!v.duration||seeking)return;" +
        		"  var p=(v.currentTime/v.duration*100).toFixed(2)+'%';" +
        		"  pFill.style.width=p; pThumb.style.left=p;" +
        		"  timeEl.textContent=fmt(v.currentTime)+' / '+fmt(v.duration);" +
        		"});" +

        		"v.addEventListener('progress',function(){" +
        		"  if(!v.duration||!v.buffered.length)return;" +
        		"  pBuf.style.width=(v.buffered.end(v.buffered.length-1)/v.duration*100)+'%';" +
        		"});" +

        		"v.addEventListener('play',function(){playBtn.textContent='⏸';});" +
        		"v.addEventListener('pause',function(){playBtn.textContent='▶';});" +
        		"v.addEventListener('ended',function(){playBtn.textContent='↺';});" +
        		"v.addEventListener('click',togglePlay);" +

        		"function togglePlay(){if(v.ended){v.currentTime=0;v.play();}else if(v.paused)v.play();else v.pause();}" +
        		"function skip(s){v.currentTime=Math.max(0,Math.min(v.duration||0,v.currentTime+s));toast((s>0?'+':'')+s+'s');}" +

        		"function seekAt(e){" +
        		"var r=document.getElementById('prog-wrap').getBoundingClientRect();" +
        		"var ratio=Math.max(0,Math.min(1,(e.clientX-r.left)/r.width));" +
        		"v.currentTime=ratio*(v.duration||0);" +
        		"}" +

        		"function seekStart(e){" +
        		"seeking=true;seekAt(e);" +
        		"document.addEventListener('mousemove',seekAt);" +
        		"document.addEventListener('mouseup',function up(){" +
        		"seeking=false;" +
        		"document.removeEventListener('mousemove',seekAt);" +
        		"document.removeEventListener('mouseup',up);" +
        		"});" +
        		"}" +

        		"function seekHover(e){" +
        		"var r=document.getElementById('prog-wrap').getBoundingClientRect();" +
        		"var t=Math.max(0,(e.clientX-r.left)/r.width)*v.duration;" +
        		"tip.textContent=fmt(t);" +
        		"tip.style.opacity='1';tip.style.left=e.clientX+'px';" +
        		"}" +

        		"document.getElementById('prog-wrap').addEventListener('mouseleave',function(){tip.style.opacity='0';});" +

        		"function setVol(val){v.volume=parseFloat(val);v.muted=val==0;}" +
        		"function toggleMute(){v.muted=!v.muted;document.getElementById('vol').value=v.muted?0:v.volume;}" +
        		"function setSpeed(val){v.playbackRate=parseFloat(val);}" +

        		"function fmt(s){var h=Math.floor(s/3600),m=Math.floor((s%3600)/60),ss=Math.floor(s%60);" +
        		"return (h>0?h+':':'')+(h>0&&m<10?'0':'')+m+':'+(ss<10?'0':'')+ss;}" +

        		"document.addEventListener('keydown',function(e){" +
        		"if(e.code==='Space'){e.preventDefault();togglePlay();}" +
        		"if(e.code==='ArrowRight')skip(5);" +
        		"if(e.code==='ArrowLeft')skip(-5);" +
        		"});" +

        		"v.play().catch(function(){});" +
        		"</script></body></html>";

        WebView webView = new WebView();
        webView.setPrefSize(1500, 700);
        webView.getEngine().loadContent(html);

        // ── Stage ────────────────────────────────────────────────────
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double fullWidth  = screenBounds.getWidth();
        double fullHeight = screenBounds.getHeight();
        double smallWidth = 1200, smallHeight = 660;

        Stage popup = new Stage();
        popup.initOwner(rootPane.getScene().getWindow());
        popup.initModality(Modality.WINDOW_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.setWidth(fullWidth); popup.setHeight(fullHeight);
        popup.setX(0); popup.setY(0);

        // ── Backdrop ─────────────────────────────────────────────────
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.88);");

        // ── Card ─────────────────────────────────────────────────────
        VBox card = new VBox(0);
        card.setMaxSize(fullWidth - 40, fullHeight - 40);
        card.setPrefSize(fullWidth - 40, fullHeight - 40);
        card.setStyle(
            "-fx-background-color: #07090f;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: rgba(56,189,248,0.18);" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 14;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.95),60,0.7,0,10);"
        );

        Rectangle cardClip = new Rectangle();
        cardClip.setArcWidth(28); cardClip.setArcHeight(28);
        card.layoutBoundsProperty().addListener((o, ov, nv) -> {
            cardClip.setWidth(nv.getWidth());
            cardClip.setHeight(nv.getHeight());
        });
        card.setClip(cardClip);

        // ── Card top bar ─────────────────────────────────────────────
        HBox cardBar = new HBox(8);
        cardBar.setAlignment(Pos.CENTER_RIGHT);
        cardBar.setPadding(new Insets(9, 12, 9, 16));
        cardBar.setMinHeight(42); cardBar.setMaxHeight(42);
        cardBar.setStyle(
            "-fx-background-color: #0a0e1a;" +
            "-fx-border-color: transparent transparent rgba(56,189,248,0.1) transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Region d1 = minidot("#1e3a5f"), d2 = minidot("#2c5282"), d3 = minidot("#4a90d9");
        Region barSpacer = new Region(); HBox.setHgrow(barSpacer, Priority.ALWAYS);

        Button btnResize = popupCtrlBtn("⊡", false);
        Button btnClose  = popupCtrlBtn("✕", true);
        btnClose.setOnAction(e -> popup.close());

        final boolean[] isSmall = {false};
        btnResize.setOnAction(e -> {
            if (!isSmall[0]) {
                card.setMaxSize(smallWidth, smallHeight);
                card.setPrefSize(smallWidth, smallHeight);
                isSmall[0] = true; btnResize.setText("⊞");
            } else {
                card.setMaxSize(fullWidth - 40, fullHeight - 40);
                card.setPrefSize(fullWidth - 40, fullHeight - 40);
                isSmall[0] = false; btnResize.setText("⊡");
            }
        });

        cardBar.getChildren().addAll(d1, d2, d3, barSpacer, btnResize, btnClose);
        VBox.setVgrow(webView, Priority.ALWAYS);
        card.getChildren().addAll(cardBar, webView);

        root.setOnMouseClicked(e -> { if (e.getTarget() == root) popup.close(); });
        root.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        java.net.URL cssUrl = getClass().getResource("/style/scrollbar.css");
        if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

        popup.setScene(scene);
        popup.setAlwaysOnTop(true);
        popup.toFront();
        popup.requestFocus();
        popup.setOnHidden(e -> {
            webView.getEngine().load(null);
            if (autoSlide != null) autoSlide.play();
        });
        popup.showAndWait();
}

    private Button popupCtrlBtn(String symbol, boolean isClose) {
        Button btn = new Button(symbol);
        btn.setPrefSize(30, 30); btn.setMinSize(30, 30); btn.setMaxSize(30, 30);
        btn.setCursor(Cursor.HAND);
        String base = "-fx-background-radius:7;-fx-border-radius:7;-fx-border-width:1;-fx-font-size:12;-fx-padding:0;";
        String normal  = base + "-fx-background-color:#0d1829;-fx-border-color:#1e3a5f;-fx-text-fill:#4a90d9;";
        String hover   = base + (isClose
            ? "-fx-background-color:#c0392b;-fx-border-color:#e74c3c;-fx-text-fill:white;"
            : "-fx-background-color:#162238;-fx-border-color:#2c5282;-fx-text-fill:#7eb8f7;");
        String pressed = base + (isClose
            ? "-fx-background-color:#922b21;-fx-border-color:#c0392b;-fx-text-fill:white;"
            : "-fx-background-color:#0a1520;-fx-border-color:#1e3a5f;-fx-text-fill:#4a90d9;");
        btn.setStyle(normal);
        btn.setOnMouseEntered(e  -> btn.setStyle(hover));
        btn.setOnMouseExited(e   -> btn.setStyle(normal));
        btn.setOnMousePressed(e  -> btn.setStyle(pressed));
        btn.setOnMouseReleased(e -> btn.setStyle(hover));
        return btn;
    }

    private Region minidot(String color) {
        Region d = new Region();
        d.setPrefSize(9, 9); d.setMinSize(9, 9); d.setMaxSize(9, 9);
        d.setStyle("-fx-background-color:" + color + ";-fx-background-radius:5;");
        HBox.setMargin(d, new Insets(0, 1, 0, 0));
        return d;
    }
	     public void showSeriePopup(FeaturedItem item) {
	            if (item == null || !"serie".equalsIgnoreCase(item.getType())) return;

	            Serie serie;
	            try {
	                serie = featuredService.getFullSerie(item.getSerieId());
	                if (serie == null) return;
	            } catch (SQLException e) {
	                e.printStackTrace();
	                return;
	            }

	            final Stage popup = new Stage();
	            popup.initOwner(rootPane.getScene().getWindow());
	            popup.initModality(Modality.WINDOW_MODAL);
	            popup.initStyle(StageStyle.TRANSPARENT);
	            
	            // ROOT STACKPANE
	            StackPane root = new StackPane();
	            root.setStyle("-fx-background-color: rgba(0,0,0,0.0);"); 
	            root.setTranslateY(50); 
	            root.setMaxWidth(1200); 
	            root.setMaxHeight(600);
	            root.setPadding(new Insets(30));
	            
	            // SCROLL PANE
	            ScrollPane scrollPane = new ScrollPane();
	            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	            scrollPane.setStyle("-fx-background: rgba(0,0,0,0.0); -fx-background-color:rgba(0,0,0,0.0);");
	            scrollPane.setFitToHeight(true);
	            scrollPane.setPrefHeight(500);
	            
	            HBox slider = new HBox(40); 
	            slider.setAlignment(Pos.CENTER_LEFT);
	            slider.setPadding(new Insets(50, 50, 50, 50));

	            List<Season> seasons = serie.getSeasons();
	            List<StackPane> cards = new ArrayList<>();
	            final int[] currentIndex = {0};

	            for (int i = 0; i < seasons.size(); i++) {
	                StackPane card = createSeasonCard(serie, i,popup); // <-- pass index now
	                int index = i;

	                card.setOnMouseClicked(e -> {
	                    currentIndex[0] = index;
	                    updateSeasonSlider(cards, currentIndex[0]);
	                    centerSlide(scrollPane, card, slider);
	                });

	                cards.add(card);
	                slider.getChildren().add(card);
	            }

	            scrollPane.setContent(slider);
	            root.getChildren().add(scrollPane);

	            // --- MODERN BUTTONS ---
	            Button left = new Button("<");
	            styleSlideButton(left);
	            left.setOnAction(e -> {
	                if (currentIndex[0] > 0) {
	                    currentIndex[0]--;
	                    updateSeasonSlider(cards, currentIndex[0]);
	                    centerSlide(scrollPane, cards.get(currentIndex[0]), slider);
	                }
	            });

	            Button right = new Button(">");
	            styleSlideButton(right);
	            right.setOnAction(e -> {
	                if (currentIndex[0] < cards.size() - 1) {
	                    currentIndex[0]++;
	                    updateSeasonSlider(cards, currentIndex[0]);
	                    centerSlide(scrollPane, cards.get(currentIndex[0]), slider);
	                }
	            });

	            StackPane.setAlignment(left, Pos.CENTER_LEFT);
	            StackPane.setMargin(left, new Insets(0, 0, 0, 10));
	            StackPane.setAlignment(right, Pos.CENTER_RIGHT);
	            StackPane.setMargin(right, new Insets(0, 10, 0, 0));

	            root.getChildren().addAll(left, right);

	            // Hover effect for buttons
	            root.setOnMouseMoved(e -> {
	                double fadeDuration = 200;
	                if (e.getX() < 150) fadeButton(left, 1, fadeDuration);
	                else fadeButton(left, 0, fadeDuration);

	                if (e.getX() > root.getWidth() - 150) fadeButton(right, 1, fadeDuration);
	                else fadeButton(right, 0, fadeDuration);
	            });

	            // CLOSE BUTTON
	            Button close = new Button("✕");
	            addHoverAnimation(close);
	            close.setStyle("-fx-background-color:#008cff; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:50%; -fx-padding:5 10;");
	            close.setOnAction(e -> popup.close());
	            StackPane.setAlignment(close, Pos.TOP_RIGHT);
	            StackPane.setMargin(close, new Insets(20));
	            scrollPane.setPrefHeight(600); // taller
	            scrollPane.setPrefWidth(1400);  // slightly narrower than full width
	            scrollPane.setMaxWidth(1400);
	            scrollPane.setMaxHeight(500);
	            root.getChildren().add(close);

	            // Center first slide
	            currentIndex[0] = 0;
	            updateSeasonSlider(cards, currentIndex[0]);
	            Platform.runLater(() -> centerSlide(scrollPane, cards.get(0), slider));

	            // Fade in popup
	            root.setOpacity(0);
	            FadeTransition fade = new FadeTransition(Duration.millis(400), root);
	            fade.setToValue(1);
	            fade.play();

	            
	            Scene scene = new Scene(root); // no fixed size
	            scene.setFill(Color.TRANSPARENT);

	            popup.setScene(scene);
	            popup.initOwner(rootPane.getScene().getWindow());
	            popup.initModality(Modality.WINDOW_MODAL);
	            popup.initStyle(StageStyle.TRANSPARENT);

	            // make it full screen relative to owner
	            popup.setWidth(rootPane.getScene().getWidth());
	            popup.setHeight(rootPane.getScene().getHeight());

	            root.setMaxWidth(Double.MAX_VALUE);
	            root.setMaxHeight(Double.MAX_VALUE);
	            root.setPrefWidth(Double.MAX_VALUE);
	            root.setPrefHeight(Double.MAX_VALUE);
	            scene.setFill(Color.TRANSPARENT);
	            popup.setScene(scene);
	            popup.showAndWait();
	            ImageView seriesTitle = new ImageView();
	            seriesTitle.setFitHeight(80);
	            seriesTitle.setPreserveRatio(true);
	            String titleUrl = seasons.size() > 3 
	            	    ? seasons.get(3).getTitleUrl() 
	            	    : seasons.get(0).getTitleUrl();

	            	seriesTitle.setImage(ImageUtil.load(titleUrl));
	            // --- BOTTOM SPACE / Series description ---
	            Label seriesDescription = new Label(serie.getSynopsis() != null ? serie.getSynopsis() : "No description available");
	            seriesDescription.setWrapText(true);
	            seriesDescription.setTextFill(Color.LIGHTGRAY);
	            seriesDescription.setStyle("-fx-font-size:16;");
	            seriesDescription.setMaxWidth(1000);

	            // Wrap the scrollPane with VBox
	            VBox container = new VBox(20); // 20px spacing
	            container.setAlignment(Pos.TOP_CENTER);
	            container.getChildren().addAll(seriesTitle, scrollPane, seriesDescription);

	            // Replace root.getChildren().add(scrollPane) with:
	            root.getChildren().add(container);
	            
	        }

	        // Style buttons for modern look
	        private void styleSlideButton(Button btn) {
	            btn.setStyle("-fx-background-color: transparent;" +
	                         "-fx-text-fill: #00aaff;" +
	                         "-fx-font-size: 36;" +
	                         "-fx-font-weight: bold;" +
	                         "-fx-effect: dropshadow(gaussian, rgba(0,255,255,0.7), 10,0,0,0);");
	            btn.setOpacity(0); // hidden initially
	        }

	        // Fade helper
	      
	    // Helper: fade button to target opacity
	    private void fadeButton(Button button, double targetOpacity, double durationMs) {
	        Timeline timeline = new Timeline(
	                new KeyFrame(Duration.millis(durationMs),
	                        new KeyValue(button.opacityProperty(), targetOpacity, Interpolator.EASE_BOTH))
	        );
	        timeline.play();
	    }
	    // --- Update card scale & opacity ---
	    private void updateSeasonSlider(List<StackPane> cards, int currentIndex) {
	        for (int i = 0; i < cards.size(); i++) {
	            StackPane card = cards.get(i);
	            int offset = i - currentIndex;

	            double scale = 1.0;
	            double opacity = 1.0;

	            if (offset == 0) {
	                scale = 1.1;
	                opacity = 1.0;
	            } else if (Math.abs(offset) == 1) {
	                scale = 0.95;
	                opacity = 0.85;
	            } else if (Math.abs(offset) == 2) {
	                scale = 0.9;
	                opacity = 0.6;
	            } else {
	                scale = 0.85;
	                opacity = 0.4;
	            }

	            Timeline anim = new Timeline(
	                    new KeyFrame(Duration.millis(400),
	                            new KeyValue(card.scaleXProperty(), scale, Interpolator.EASE_BOTH),
	                            new KeyValue(card.scaleYProperty(), scale, Interpolator.EASE_BOTH),
	                            new KeyValue(card.opacityProperty(), opacity, Interpolator.EASE_BOTH)
	                    )
	            );
	            anim.play();
	        }
	    }

	    // --- Center clicked card ---
	    private void centerSlide(ScrollPane scrollPane, StackPane card, HBox slider) {
	        double scrollWidth = slider.getWidth();
	        double scrollPaneWidth = scrollPane.getViewportBounds().getWidth();
	        double cardCenter = card.getBoundsInParent().getMinX() + card.getBoundsInParent().getWidth() / 2.0;
	        double hValue = (cardCenter - scrollPaneWidth / 2) / (scrollWidth - scrollPaneWidth);
	        hValue = Math.min(Math.max(hValue, 0), 1);

	        Timeline anim = new Timeline(
	                new KeyFrame(Duration.millis(400),
	                        new KeyValue(scrollPane.hvalueProperty(), hValue, Interpolator.EASE_BOTH))
	        );
	        anim.play();
	    }
	    
	    private StackPane createSeasonCard(Serie serie, int seasonIndex, Stage popup) {
	        Season s = serie.getSeasons().get(seasonIndex);

	        StackPane card = new StackPane();
	        card.setPrefSize(740, 420);
	        card.setStyle(
	            "-fx-background-color: #07090f;" +
	            "-fx-background-radius: 18;" +
	            "-fx-border-color: rgba(56,189,248,0.18);" +
	            "-fx-border-width: 1.5;" +
	            "-fx-border-radius: 18;" +
	            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.85), 40, 0.6, 0, 8);"
	        );

	        Rectangle clip = new Rectangle(740, 420);
	        clip.setArcWidth(36); clip.setArcHeight(36);
	        card.setClip(clip);

	        StackPane contentWrapper = new StackPane();
	        contentWrapper.setPrefSize(740, 420);
	        card.getChildren().add(contentWrapper);

	        // ── POSTER ───────────────────────────────────────────────────
	        ImageView poster = new ImageView();
	        poster.setFitWidth(300);
	        poster.setFitHeight(420);
	        poster.setPreserveRatio(true);
	        poster.setSmooth(true);
	        poster.setCache(true);

	        poster.setImage(ImageUtil.load(s.getPosterUrl()));
	        StackPane posterWrapper = new StackPane(poster);
	        posterWrapper.setPrefSize(300, 420);
	        posterWrapper.setMaxSize(300, 420);
	        posterWrapper.setAlignment(Pos.CENTER);

	        Rectangle posterClip = new Rectangle(300, 420);
	        posterClip.setArcWidth(20);
	        posterClip.setArcHeight(20);
	        posterWrapper.setClip(posterClip);

	        poster.setScaleX(1.05);
	        poster.setScaleY(1.05);

	        Region posterFade = new Region();
	        posterFade.setPrefSize(300, 420);
	        posterFade.setStyle(
	            "-fx-background-color: linear-gradient(to right," +
	            "  transparent 0%, rgba(7,9,15,0.55) 70%, rgba(7,9,15,1.0) 100%);"
	        );

	        StackPane posterPane = new StackPane(posterWrapper, posterFade);
	        posterPane.setPrefSize(300, 420);
	        posterPane.setMaxSize(300, 420);
	        StackPane.setAlignment(posterPane, Pos.CENTER_LEFT);

	        // ── INFO PANEL ───────────────────────────────────────────────
	        VBox infoBox = new VBox(12);
	        infoBox.setPadding(new Insets(28, 24, 24, 18));
	        infoBox.setAlignment(Pos.TOP_LEFT);
	        infoBox.setMaxWidth(420);
	        infoBox.setPrefWidth(420);
	        StackPane.setAlignment(infoBox, Pos.CENTER_RIGHT);

	        Label badge = new Label("SEASON " + s.getSeasonNum());
	        badge.setStyle(
	            "-fx-background-color: rgba(56,189,248,0.12);" +
	            "-fx-border-color: rgba(56,189,248,0.45);" +
	            "-fx-border-width: 1; -fx-border-radius: 4; -fx-background-radius: 4;" +
	            "-fx-text-fill: #38bdf8; -fx-font-size: 10px; -fx-font-weight: bold;" +
	            "-fx-letter-spacing: 3; -fx-padding: 4 10;"
	        );

	        Label title = new Label(s.getTitle() != null ? s.getTitle() : "Untitled Season");
	        title.setStyle(
	            "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;" +
	            "-fx-wrap-text: true;"
	        );
	        title.setMaxWidth(380); title.setWrapText(true);

	        HBox starsBox = new HBox(4);
	        starsBox.setAlignment(Pos.CENTER_LEFT);
	        for (int i = 0; i < 5; i++) {
	            Label star = new Label("★");
	            star.setStyle("-fx-font-size: 15px; -fx-text-fill: " +
	                (i < s.getRating() ? "#38bdf8;" : "rgba(255,255,255,0.15);"));
	            starsBox.getChildren().add(star);
	        }

	        String statusText = s.getStatus() != null ? s.getStatus() : "Unknown";
	        boolean isOngoing = statusText.equalsIgnoreCase("Ongoing");
	        int epCount = s.getEpisodes() != null ? s.getEpisodes().size() : 0;

	        Label statusPill = new Label(statusText.toUpperCase());
	        statusPill.setStyle(
	            "-fx-background-color: " + (isOngoing ? "rgba(34,197,94,0.15)" : "rgba(148,163,184,0.12)") + ";" +
	            "-fx-border-color: "     + (isOngoing ? "rgba(34,197,94,0.5)"  : "rgba(148,163,184,0.3)")  + ";" +
	            "-fx-border-width: 1; -fx-border-radius: 20; -fx-background-radius: 20;" +
	            "-fx-text-fill: "        + (isOngoing ? "#4ade80" : "#94a3b8") + ";" +
	            "-fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 1.5; -fx-padding: 3 10;"
	        );

	        Label episodeCount = new Label(epCount + " Episodes");
	        episodeCount.setStyle("-fx-text-fill: rgba(148,163,184,0.75); -fx-font-size: 13px;");

	        Label synopsis = new Label(s.getSynopsis() != null ? s.getSynopsis() : "No synopsis available.");
	        synopsis.setWrapText(true); synopsis.setMaxWidth(370);
	        synopsis.setStyle(
	            "-fx-text-fill: rgba(203,213,225,0.7); -fx-font-size: 12px; -fx-line-spacing: 3;"
	        );

	        Region divider = new Region();
	        divider.setPrefHeight(1); divider.setMaxHeight(1);
	        divider.setStyle(
	            "-fx-background-color: linear-gradient(to right, transparent, rgba(56,189,248,0.4), transparent);"
	        );

	        Button trailerBtn  = new Button("▶  Trailer");
	        Button episodesBtn = new Button("≡  Episodes");
	        styleCardButton(trailerBtn,  false);
	        styleCardButton(episodesBtn, true);
	        addHoverAnimation(trailerBtn);
	        addHoverAnimation(episodesBtn);

	        ScaleTransition epPulse = new ScaleTransition(Duration.millis(900), episodesBtn);
	        epPulse.setFromX(1.0); epPulse.setFromY(1.0);
	        epPulse.setToX(1.05);  epPulse.setToY(1.05);
	        epPulse.setAutoReverse(true);
	        epPulse.setCycleCount(Animation.INDEFINITE);
	        epPulse.play();

	        infoBox.getChildren().addAll(
	            badge, title, starsBox,
	            new HBox(10, statusPill, episodeCount) {{ setAlignment(Pos.CENTER_LEFT); }},
	            synopsis, divider,
	            new HBox(12, trailerBtn, episodesBtn) {{ setAlignment(Pos.CENTER_LEFT); }}
	        );

	        // ── MAIN VIEW ────────────────────────────────────────────────
	        HBox mainView = new HBox(posterPane, infoBox);
	        mainView.setAlignment(Pos.CENTER_LEFT);
	        mainView.setPrefSize(740, 420);

	        // ── EPISODES LIST VIEW ───────────────────────────────────────
	        VBox episodesView = new VBox(0);
	        episodesView.setPrefSize(740, 420);
	        episodesView.setStyle("-fx-background-color: #07090f;");

	        // Header
	        Button backToMain = new Button("←");
	        backToMain.setStyle(
	            "-fx-background-color: rgba(56,189,248,0.1); -fx-background-radius: 50%;" +
	            "-fx-border-color: rgba(56,189,248,0.35); -fx-border-radius: 50%; -fx-border-width: 1;" +
	            "-fx-text-fill: #38bdf8; -fx-font-size: 18px; -fx-font-weight: bold;" +
	            "-fx-padding: 4 10; -fx-cursor: hand;"
	        );
	        addHoverAnimation(backToMain);

	        Label epHeaderTitle = new Label("Season " + s.getSeasonNum() + " — Episodes");
	        epHeaderTitle.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");

	        Label epHeaderCount = new Label(epCount + " episodes");
	        epHeaderCount.setStyle("-fx-text-fill: #38bdf8; -fx-font-size: 12px;");

	        Region epHeaderSpacer = new Region();
	        HBox.setHgrow(epHeaderSpacer, Priority.ALWAYS);

	        HBox epHeader = new HBox(12, backToMain, epHeaderTitle, epHeaderSpacer, epHeaderCount);
	        epHeader.setAlignment(Pos.CENTER_LEFT);
	        epHeader.setPadding(new Insets(16, 24, 12, 24));
	        epHeader.setStyle(
	            "-fx-background-color: linear-gradient(to bottom, #0d1117, transparent);" +
	            "-fx-border-color: transparent transparent rgba(56,189,248,0.18) transparent;" +
	            "-fx-border-width: 0 0 1 0;"
	        );

	        // Episode rows container
	        VBox episodesList = new VBox(6);
	        episodesList.setPadding(new Insets(10, 8, 16, 16));

	        // ── ScrollPane — built-in bars hidden, wheel handled manually ──
	        ScrollPane episodesScroll = new ScrollPane(episodesList);
	        episodesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	        episodesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	        episodesScroll.setFitToWidth(true);
	        episodesScroll.setStyle(
	            "-fx-background: transparent;" +
	            "-fx-background-color: transparent;" +
	            "-fx-padding: 0;"
	        );
	        VBox.setVgrow(episodesScroll, Priority.ALWAYS);
	        episodesScroll.setOnScroll(e ->
	            episodesScroll.setVvalue(
	                Math.max(0, Math.min(1,
	                    episodesScroll.getVvalue() - e.getDeltaY() * 0.003))
	            )
	        );

	        // ── Custom slim scrollbar ──────────────────────────────────────
	        ScrollBar customScrollBar = new ScrollBar();
	        customScrollBar.setOrientation(Orientation.VERTICAL);
	        customScrollBar.setMin(0);
	        customScrollBar.setMax(1);
	        customScrollBar.setValue(0);
	        customScrollBar.setUnitIncrement(0.05);
	        customScrollBar.setBlockIncrement(0.2);

	        // Lock width to exactly 6px
	        customScrollBar.setPrefWidth(6);
	        customScrollBar.setMinWidth(6);
	        customScrollBar.setMaxWidth(6);

	        

	        // Load scrollbar.css so .thumb gets the blue-black pill look
	        customScrollBar.getStylesheets().add(
	            getClass().getResource("/view/css/scrollbar.css").toExternalForm()
	        );

	        // Two-way bind: dragging thumb ↔ scrolling pane
	        customScrollBar.valueProperty().bindBidirectional(episodesScroll.vvalueProperty());

	        // Adjust thumb size to reflect visible portion of content
	        episodesScroll.viewportBoundsProperty().addListener((o, ov, nv) ->
	            updateThumbSize(episodesScroll, customScrollBar));

	        // Style the thumb directly after CSS is applied
	        Platform.runLater(() -> {
	            Node thumb = customScrollBar.lookup(".thumb");
	            if (thumb != null) thumb.setStyle(
	                "-fx-background-color: #1e3a5f;" +
	                "-fx-background-radius: 10;" +
	                "-fx-border-color: transparent;"
	            );
	            Node track = customScrollBar.lookup(".track");
	            if (track != null) track.setStyle(
	                "-fx-background-color: transparent;" +
	                "-fx-border-color: transparent;"
	            );
	            // Hide arrow buttons
	            for (String sel : new String[]{".increment-button", ".decrement-button"}) {
	                Node btn = customScrollBar.lookup(sel);
	                if (btn != null) btn.setStyle(
	                    "-fx-pref-width: 0; -fx-pref-height: 0;" +
	                    "-fx-min-width: 0; -fx-min-height: 0;" +
	                    "-fx-max-width: 0; -fx-max-height: 0;" +
	                    "-fx-background-color: transparent;"
	                );
	            }
	            // Hover: brighten thumb to accent blue
	            if (thumb != null) {
	                thumb.setOnMouseEntered(ev -> thumb.setStyle(
	                    "-fx-background-color: #4a90d9;" +
	                    "-fx-background-radius: 10;" +
	                    "-fx-border-color: transparent;"
	                ));
	                thumb.setOnMouseExited(ev -> thumb.setStyle(
	                    "-fx-background-color: #1e3a5f;" +
	                    "-fx-background-radius: 10;" +
	                    "-fx-border-color: transparent;"
	                ));
	            }
	        });

	        // ── Episode detail pane ───────────────────────────────────────
	        int userId = Session.getUserId();
	        Map<Integer, WatchStatus> progressMap = episodeProgressService.loadUserProgress(userId);

	        StackPane episodeDetailPane = new StackPane();
	        episodeDetailPane.setPrefSize(740, 420);
	        episodeDetailPane.setStyle("-fx-background-color: #07090f;");

	        for (Episode ep : s.getEpisodes()) {
	            HBox row = buildEpisodeRow(
	                ep, s, progressMap, contentWrapper,
	                episodeDetailPane, episodesView, popup, userId
	            );
	            episodesList.getChildren().add(row);
	        }

	        // scrollRow: ScrollPane fills width, slim bar on the right edge
	        HBox scrollRow = new HBox(0, episodesScroll, customScrollBar);
	        HBox.setHgrow(episodesScroll, Priority.ALWAYS);
	        VBox.setVgrow(scrollRow, Priority.ALWAYS);

	        episodesView.getChildren().addAll(epHeader, scrollRow);

	        // ── WIRE ACTIONS ─────────────────────────────────────────────
	        trailerBtn.setOnAction(e  -> showTrailerPopup(serie, seasonIndex));
	        episodesBtn.setOnAction(e -> switchView(contentWrapper, episodesView));
	        backToMain.setOnAction(e  -> switchView(contentWrapper, mainView));

	        contentWrapper.getChildren().setAll(mainView);
	        return card;
	    }
	    // Single definition — 8 params including episodesView
	    private HBox buildEpisodeRow(
	            Episode ep, Season s,
	            Map<Integer, WatchStatus> progressMap,
	            StackPane contentWrapper,
	            StackPane detailPane,
	            VBox episodesView,
	            Stage popup, int userId) {

	        WatchStatus status = progressMap.getOrDefault(ep.getEpId(), WatchStatus.NOT_STARTED);

	        HBox row = new HBox(14);
	        row.setAlignment(Pos.CENTER_LEFT);
	        row.setPadding(new Insets(12, 16, 12, 16));

	        String baseStyle =
	            "-fx-background-color: rgba(15,20,32,0.6);" +
	            "-fx-background-radius: 10;" +
	            "-fx-border-color: rgba(56,189,248,0.07);" +
	            "-fx-border-width: 1; -fx-border-radius: 10; -fx-cursor: hand;";
	        String hoverStyle =
	            "-fx-background-color: rgba(56,189,248,0.08);" +
	            "-fx-background-radius: 10;" +
	            "-fx-border-color: rgba(56,189,248,0.25);" +
	            "-fx-border-width: 1; -fx-border-radius: 10; -fx-cursor: hand;";
	        row.setStyle(baseStyle);
	        row.setOnMouseEntered(e -> row.setStyle(hoverStyle));
	        row.setOnMouseExited(e  -> row.setStyle(baseStyle));

	        // Number circle
	        Label numBadge = new Label(String.format("%02d", ep.getNumEpisode()));
	        numBadge.setPrefSize(34, 34); numBadge.setMinSize(34, 34);
	        numBadge.setAlignment(Pos.CENTER);
	        numBadge.setStyle(
	            "-fx-background-color: rgba(56,189,248,0.12); -fx-background-radius: 17;" +
	            "-fx-border-color: rgba(56,189,248,0.3); -fx-border-radius: 17; -fx-border-width: 1;" +
	            "-fx-text-fill: #38bdf8; -fx-font-size: 12px; -fx-font-weight: bold;"
	        );

	        // Title + stars
	        VBox textCol = new VBox(4);
	        HBox.setHgrow(textCol, Priority.ALWAYS);

	        Label epTitleLbl = new Label(ep.getTitle());
	        epTitleLbl.setStyle(
	            "-fx-text-fill: rgba(226,232,240,0.95); -fx-font-size: 14px; -fx-font-weight: bold;"
	        );

	        HBox miniStars = new HBox(2);
	        int rating = (int) Math.round(ep.getRating());
	        for (int i = 0; i < 5; i++) {
	            Label st = new Label("★");
	            st.setStyle("-fx-font-size: 10px; -fx-text-fill: " +
	                (i < rating ? "#38bdf8" : "rgba(255,255,255,0.12)") + ";");
	            miniStars.getChildren().add(st);
	        }
	        textCol.getChildren().addAll(epTitleLbl, miniStars);

	        // Duration
	        int h = ep.getDuration() / 60, m = ep.getDuration() % 60;
	        Label duration = new Label((h > 0 ? h + "h " : "") + m + "m");
	        duration.setStyle("-fx-text-fill: rgba(148,163,184,0.6); -fx-font-size: 12px;");

	        // Status pill
	        String pillText   = switch (status) { case COMPLETED -> "✓ Watched"; case IN_PROGRESS -> "▶ In Progress"; default -> "Not Started"; };
	        String pillBg     = switch (status) { case COMPLETED -> "rgba(34,197,94,0.15)";  case IN_PROGRESS -> "rgba(56,189,248,0.15)";  default -> "rgba(100,116,139,0.12)"; };
	        String pillBorder = switch (status) { case COMPLETED -> "rgba(34,197,94,0.5)";   case IN_PROGRESS -> "rgba(56,189,248,0.45)";  default -> "rgba(100,116,139,0.25)"; };
	        String pillFg     = switch (status) { case COMPLETED -> "#4ade80";                case IN_PROGRESS -> "#38bdf8";                default -> "#64748b"; };

	        Label statusPill = new Label(pillText);
	        statusPill.setStyle(
	            "-fx-background-color: " + pillBg + "; -fx-border-color: " + pillBorder + ";" +
	            "-fx-border-width: 1; -fx-border-radius: 20; -fx-background-radius: 20;" +
	            "-fx-text-fill: " + pillFg + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 9;"
	        );

	        Label arrow = new Label("›");
	        arrow.setStyle("-fx-text-fill: rgba(56,189,248,0.4); -fx-font-size: 20px;");

	        row.getChildren().addAll(numBadge, textCol, duration, statusPill, arrow);

	        // Click → populate detail + switch view
	        row.setOnMouseClicked(ev -> {
	            populateEpisodeDetail(detailPane, ep, s, contentWrapper, episodesView, popup, userId);
	            switchView(contentWrapper, detailPane);
	        });

	        return row;
	    }

	    // ═══════════════════════ EPISODE DETAIL ═══════════════════════
	    // Single definition — 7 params including contentWrapper + episodesView
	    private void populateEpisodeDetail(
	            StackPane detailPane, Episode ep, Season s,
	            StackPane contentWrapper, VBox episodesView,
	            Stage popup, int userId) {

	        detailPane.getChildren().clear();

	        // ── COVER ─────────────────────────────────────────────
	        ImageView cover = new ImageView();
	        cover.setFitWidth(740);
	        cover.setFitHeight(280);
	        cover.setPreserveRatio(false);

	        cover.setImage(ImageUtil.load(ep.getCovertUrl()));

	        Region coverGradient = new Region();
	        coverGradient.setPrefSize(740, 280);
	        coverGradient.setStyle(
	            "-fx-background-color: linear-gradient(to bottom," +
	            "rgba(7,9,15,0) 0%, rgba(7,9,15,0.6) 55%, rgba(7,9,15,1.0) 100%);"
	        );

	        StackPane coverPane = new StackPane(cover, coverGradient);
	        coverPane.setMaxSize(740, 280);
	        StackPane.setAlignment(coverPane, Pos.TOP_CENTER);

	        // IMPORTANT: don't block clicks
	        coverPane.setMouseTransparent(true);

	        // ── PLAY BUTTON ───────────────────────────────────────
	        Button play = new Button("▶");
	        play.setPrefSize(64, 64);
	        play.setStyle(
	            "-fx-background-color: rgba(56,189,248,0.18); -fx-background-radius: 32;" +
	            "-fx-border-color: #38bdf8; -fx-border-radius: 32; -fx-border-width: 2;" +
	            "-fx-text-fill: white; -fx-font-size: 22px; -fx-cursor: hand;" +
	            "-fx-effect: dropshadow(gaussian, rgba(56,189,248,0.55), 22, 0.4, 0, 0);"
	        );

	        // Pulse animation
	        ScaleTransition pulse = new ScaleTransition(Duration.millis(900), play);
	        pulse.setFromX(1.0);
	        pulse.setFromY(1.0);
	        pulse.setToX(1.12);
	        pulse.setToY(1.12);
	        pulse.setAutoReverse(true);
	        pulse.setCycleCount(Animation.INDEFINITE);
	        pulse.play();

	        // Hover
	        play.setOnMouseEntered(e -> {
	            pulse.stop();
	            play.setScaleX(1.18);
	            play.setScaleY(1.18);
	        });

	        play.setOnMouseExited(e -> {
	            play.setScaleX(1.0);
	            play.setScaleY(1.0);
	            pulse.play();
	        });

	        StackPane.setAlignment(play, Pos.CENTER);
	        StackPane.setMargin(play, new Insets(0, 0, 160, 0));

	        // ── INFO PANEL ────────────────────────────────────────
	        VBox infoPanel = new VBox(8);
	        infoPanel.setPadding(new Insets(0, 28, 20, 28));
	        infoPanel.setAlignment(Pos.TOP_LEFT);
	        StackPane.setAlignment(infoPanel, Pos.BOTTOM_CENTER);

	        Button backBtn = new Button("← Episodes");
	        backBtn.setStyle(
	            "-fx-background-color: rgba(56,189,248,0.1);" +
	            "-fx-border-color: rgba(56,189,248,0.35);" +
	            "-fx-text-fill: #38bdf8;"
	        );
	        addHoverAnimation(backBtn);

	        Label epNum = new Label("EPISODE " + ep.getNumEpisode());
	        Label epTitle = new Label(ep.getTitle());

	        int h = ep.getDuration() / 60;
	        int m = ep.getDuration() % 60;

	        Label metaLabel = new Label(h + "h " + m + "m");

	        Label synopsis = new Label(
	            ep.getResume() != null ? ep.getResume() : "No synopsis available."
	        );
	        synopsis.setWrapText(true);

	        infoPanel.getChildren().addAll(backBtn, epNum, epTitle, metaLabel, synopsis);

	        // ── ACTIONS ───────────────────────────────────────────
	        play.setOnAction(e -> {
	            System.out.println("PLAY CLICKED ✅");

	            try {
	                if (popup != null) popup.close();

	                goToLecturePageEpisode(
	                    s.getSerieId(),
	                    s.getSeasonNum(),
	                    ep.getEpId()
	                );

	                

	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	        });

	        backBtn.setOnAction(e -> switchView(contentWrapper, episodesView));

	        // ── FINAL LAYOUT (FIXED ORDER) ─────────────────────────
	        detailPane.getChildren().addAll(coverPane, infoPanel, play);

	        // FORCE play button on top
	        play.toFront();
	    }

	    // ═══════════════════════ VIEW SWITCHER ═══════════════════════
	    private void switchView(StackPane wrapper, Node target) {
	        if (!wrapper.getChildren().isEmpty()) {
	            Node current = wrapper.getChildren().get(0);
	            if (current == target) return;
	            FadeTransition out = new FadeTransition(Duration.millis(160), current);
	            out.setToValue(0);
	            out.setOnFinished(e -> {
	                wrapper.getChildren().setAll(target);
	                target.setOpacity(0);
	                FadeTransition in = new FadeTransition(Duration.millis(200), target);
	                in.setToValue(1);
	                in.play();
	            });
	            out.play();
	        } else {
	            wrapper.getChildren().setAll(target);
	        }
	    }

	   
	    private void updateThumbSize(ScrollPane scrollPane, ScrollBar scrollBar) {
	    	Node content = scrollPane.getContent();
	        if (content == null) return;

	        content.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
	            double contentHeight = newBounds.getHeight();
	            double viewportHeight = scrollPane.getViewportBounds().getHeight();

	            if (contentHeight <= 0) return;

	            double ratio = viewportHeight / contentHeight;

	            // clamp
	            ratio = Math.max(0.05, Math.min(ratio, 1.0));

	            scrollBar.setVisibleAmount(ratio);
	        });

	        scrollPane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
	            double contentHeight = content.getLayoutBounds().getHeight();
	            double viewportHeight = newVal.getHeight();

	            if (contentHeight <= 0) return;

	            double ratio = viewportHeight / contentHeight;
	            ratio = Math.max(0.05, Math.min(ratio, 1.0));

	            scrollBar.setVisibleAmount(ratio);
	        });
	    }
	    private void styleCardButton(Button btn, boolean primary) {
	        if (primary) {
	            btn.setStyle(
	                "-fx-background-color: #38bdf8; -fx-text-fill: #07090f;" +
	                "-fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8;" +
	                "-fx-padding: 8 20; -fx-cursor: hand;" +
	                "-fx-effect: dropshadow(gaussian, rgba(56,189,248,0.45), 16, 0.3, 0, 0);"
	            );
	        } else {
	            btn.setStyle(
	                "-fx-background-color: rgba(56,189,248,0.08); -fx-text-fill: #38bdf8;" +
	                "-fx-font-size: 13px; -fx-background-radius: 8;" +
	                "-fx-border-color: rgba(56,189,248,0.35); -fx-border-width: 1;" +
	                "-fx-border-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;"
	            );
	        }
	    }

	 	private void goToLecturePageFilm(int filmId) {
    	    try {
    	        FXMLLoader loader = new FXMLLoader(
    	            getClass().getClassLoader().getResource("view/fxml/LecturePage.fxml")
    	        );
    	        Parent root = loader.load();

    	        LecturePageController controller = loader.getController();
    	        controller.initFilm(filmId);

    	        // 🔥 Get current stage
    	        Stage stage = (Stage) rootPane.getScene().getWindow();

    	        // 🔥 Replace scene
    	        stage.getScene().setRoot(root);

    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	}
    	private void goToLecturePageEpisode(int serieId, int seasonNum, int episodeNum) {
    	    try {
    	        FXMLLoader loader = new FXMLLoader(
    	            getClass().getClassLoader().getResource("view/fxml/LecturePage.fxml")
    	        );
    	        Parent root = loader.load();

    	        LecturePageController controller = loader.getController();
    	        controller.initEpisode(serieId, seasonNum, episodeNum);
 
    	        // 🔥 Get current stage
    	        Stage stage = (Stage) rootPane.getScene().getWindow();

    	        // 🔥 Replace scene
    	        stage.getScene().setRoot(root);

    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	}
	    	 public void showPopup(FeaturedItem item) {
	    	        CardController controller = new CardController();
	    	        if (item.getType().equalsIgnoreCase("film")) {
	    	            controller.showFilmPopup(item);
	    	        } else if (item.getType().equalsIgnoreCase("serie")) {
	    	            controller.showSeriePopup(item);
	    	        }
	    	    }
	    	 @FXML private Rectangle progressFill;

	    	 public void setData(FeaturedItemProgress data) {
	    		 show(playBtn);
	    	     show(addBtn);
	    	     show(starsLabel);
	    	     show(progressFill);
	    	     show(typeBadge);
	    	     FeaturedItem item = data.getItem();
	    	     this.currentItem = item;

	    	     // ------------------ Make the card clickable ------------------
	    	     poster.getParent().setOnMouseClicked(e -> {
	    	         if (autoSlide != null) autoSlide.pause(); // pause carousel

	    	         String type = currentItem.getType().toLowerCase();
	    	         if (type.equals("film")) {
	    	             showFilmPopup(currentItem);
	    	         } else if (type.equals("serie")) {
	    	             showSeriePopup(currentItem);
	    	         }
	    	     });

	    	     // ------------------ Poster ------------------
	    	     poster.setImage(ImageUtil.load(item.getPosterUrl()));

	    	     // ------------------ Type Badge ------------------
	    	     if (item.getSerieId() != 0) {
	    	         typeBadge.setText("SERIE");
	    	     } else {
	    	         typeBadge.setText("FILM");
	    	     }

	    	     // ------------------ Button Actions ------------------
	    	     playBtn.setOnAction(e -> {
	    	         if (autoSlide != null) autoSlide.pause(); // pause carousel

	    	         String type = currentItem.getType().toLowerCase();
	    	         if (type.equals("film")) {
	    	             showFilmPopup(currentItem);
	    	         } else if (type.equals("serie")) {
	    	             showSeriePopup(currentItem);
	    	         }
	    	     });

	    	     addBtn.setOnAction(e -> handleAddToList());
	    	     updateAddButton(addBtn, item);

	    	     // ------------------ Rating Stars ------------------
	    	     starsLabel.setText(getStars(item.getRating()));

	    	     // ------------------ Progress Line ------------------
	    	     int lastPos = data.getLastPosition();
	    	     int totalDuration = 1; // default to avoid division by zero

	    	     try {
	    	         if (item.getType().equalsIgnoreCase("film")) {
	    	             // Get film duration
	    	             Film film = featuredService.getFilmDetails(item.getId());
	    	             if (film != null) totalDuration = (int) film.getDuration(); // duration in seconds
	    	         } else if (item.getType().equalsIgnoreCase("serie")) {
	    	             // Get all episodes for the series
	    	             Serie serie = featuredService.getFullSerie(item.getSerieId());
	    	             if (serie != null) {
	    	                 List<Episode> episodes = featuredService.getEpisodesBySerie(serie.getSerieId());
	    	                 totalDuration = episodes.stream().mapToInt(Episode::getDuration).sum();

	    	                 // Compute last position across episodes
	    	                 lastPos = 0;
	    	                 for (Episode ep : episodes) {
	    	                     WatchStatus epStatus = episodeProgressService.getEpisodeStatus(Session.getUserId(), ep.getEpId());
	    	                     int epLastPos = episodeProgressService.getEpisodeLastPosition(Session.getUserId(), ep.getEpId());

	    	                     if (epStatus == WatchStatus.COMPLETED) {
	    	                         lastPos += ep.getDuration();
	    	                     } else if (epStatus == WatchStatus.IN_PROGRESS) {
	    	                         lastPos += epLastPos;
	    	                     }
	    	                 }
	    	             }
	    	         }
	    	     } catch (SQLException e) {
	    	         e.printStackTrace();
	    	         // fallback in case of DB failure
	    	         lastPos = 0;
	     	         totalDuration = 1;
	    	     }

	    	     // Compute progress safely
	    	     double progress = (double) lastPos / totalDuration;
	    	     progress = Math.max(0, Math.min(progress, 1));

	    	     // Set progress rectangle
	    	     progressFill.setWidth(180 * progress);
	    	     switch (data.getStatus()) {
	    	         case COMPLETED -> progressFill.setStyle("-fx-fill: #00FFAA;");
	    	         case IN_PROGRESS -> progressFill.setStyle("-fx-fill: #1E90FF;");
	    	         case NOT_STARTED -> progressFill.setWidth(0);
	    	     }
	    	    
	    	 }
	    	 public void setItem2(FeaturedItem item) {
	    	        this.currentItem = item;
	    	        
	    	     // Make the whole card clickable like the play button
	    	        poster.getParent().setOnMouseClicked(e -> {
	    	            if (autoSlide != null) autoSlide.pause(); // pause carousel

	    	            // Same logic as play button
	    	            String type = currentItem.getType().toLowerCase();
	    	            if (type.equals("film")) {
	    	                showFilmPopup(currentItem);
	    	            } else if (type.equals("serie")) {
	    	                showSeriePopup(currentItem);
	    	            }
	    	        });
	    	        // ------------------ Poster ------------------
	    	        poster.setImage(ImageUtil.load(item.getPosterUrl()));
	    	        // ------------------ Type Badge ------------------
	    	       
	    	        addBtn.setOnAction(e -> handleAddToList());
	    	        updateAddButton(addBtn, item);
	    	        starsLabel.setText(getStars(item.getRating()));
	    	        hide(playBtn);
	    	        hide(starsLabel);
	    	        hide(progressFill);
	    	        hide(progressBg);
	    	        
	    	        
	    	    }
	    	 private void show(Node node) {
	    		    node.setVisible(true);
	    		    node.setManaged(true);
	    		}

	    		private void hide(Node node) {
	    		    node.setVisible(false);
	    		    node.setManaged(false);
	    		}
	    		public void setItem_mylist(FeaturedItem item) {
	    		    this.currentItem = item;

	    		    // ------------------ CLICK ON POSTER ------------------
	    		    poster.getParent().setOnMouseClicked(e -> {
	    		        if (autoSlide != null) autoSlide.pause();

	    		        String type = currentItem.getType().toLowerCase();

	    		        if (type.equals("film")) {
	    		            showFilmPopup(currentItem);

	    		        } else if (type.equals("serie")) {
	    		            // 🎯 Direct smart resume instead of popup
	    		            playBtn.fire();
	    		        }
	    		    });

	    		    // ------------------ POSTER ------------------
	    		    poster.setImage(ImageUtil.load(item.getPosterUrl()));

	    		    // ------------------ TYPE BADGE ------------------
	    		    if (item.getSerieId() != 0) {
	    		        typeBadge.setText("SERIE");
	    		    } else {
	    		        typeBadge.setText("FILM");
	    		    }

	    		    // ------------------ PLAY BUTTON ------------------
	    		    playBtn.setOnAction(e -> {
	    		        try {
	    		            String type = currentItem.getType().toLowerCase();

	    		            if (type.equals("film")) {
	    		                showFilmPopup(currentItem);

	    		            } else if (type.equals("serie")) {

	    		                Serie serie = featuredService.getFullSerie(currentItem.getSerieId());
	    		                int userId = Session.getUserId();
	    		                Map<Integer, WatchStatus> progressMap =
	    		                        episodeProgressService.loadUserProgress(userId);

	    		                // 🔥 Ensure correct order (important!)
	    		                serie.getSeasons().sort(Comparator.comparingInt(Season::getSeasonNum));

	    		                Episode inProgressEpisode = null;
	    		                Episode nextEpisode = null;
	    		                int targetSeasonNum = 0;

	    		                // 🔍 Find best episode (priority logic)
	    		                outer:
	    		                for (Season s : serie.getSeasons()) {

	    		                    s.getEpisodes().sort(Comparator.comparingInt(Episode::getNumEpisode));

	    		                    for (Episode ep : s.getEpisodes()) {

	    		                        WatchStatus status = progressMap.getOrDefault(
	    		                                ep.getEpId(),
	    		                                WatchStatus.NOT_STARTED
	    		                        );

	    		                        // 🎯 1. Resume where user stopped
	    		                        if (status == WatchStatus.IN_PROGRESS) {
	    		                            inProgressEpisode = ep;
	    		                            targetSeasonNum = s.getSeasonNum();
	    		                            break outer;
	    		                        }

	    		                        // ▶️ 2. First not started episode
	    		                        if (status == WatchStatus.NOT_STARTED && nextEpisode == null) {
	    		                            nextEpisode = ep;
	    		                            targetSeasonNum = s.getSeasonNum();
	    		                        }
	    		                    }
	    		                }

	    		                // 🎬 Final decision
	    		                Episode targetEpisode;

	    		                if (inProgressEpisode != null) {
	    		                    targetEpisode = inProgressEpisode;

	    		                } else if (nextEpisode != null) {
	    		                    targetEpisode = nextEpisode;

	    		                } else {
	    		                    // 🔁 All watched → restart
	    		                    Season firstSeason = serie.getSeasons().get(0);
	    		                    targetEpisode = firstSeason.getEpisodes().get(0);
	    		                    targetSeasonNum = firstSeason.getSeasonNum();
	    		                }

	    		                // 🚀 Navigate (LecturePageController handles progress saving)
	    		                goToLecturePageEpisode(
	    		                        serie.getSerieId(),
	    		                        targetSeasonNum,
	    		                        targetEpisode.getEpId()
	    		                );
	    		            }

	    		        } catch (Exception ex) {
	    		            ex.printStackTrace();
	    		        }
	    		    });

	    		    // ------------------ OTHER BUTTONS ------------------
	    		    addBtn.setOnAction(e -> handleAddToList());
	    		    updateAddButton(addBtn, item);

	    		    // ------------------ UI ------------------
	    		    starsLabel.setText(getStars(item.getRating()));

	    		    show(playBtn);
	    		    show(addBtn);
	    		    show(starsLabel);
	    		    show(typeBadge);
	    		    hide(progressFill);
	    		}

	    		    
	    		        } 
	    		        
	    		  