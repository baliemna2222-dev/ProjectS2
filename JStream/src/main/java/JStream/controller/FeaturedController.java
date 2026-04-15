package JStream.controller;

import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
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
import javafx.animation.*;
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
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeaturedController {
 
    // ═══════════════════════ FXML FIELDS ═══════════════════════
    @FXML private StackPane rootPane;
    @FXML private ImageView heroBackground;
    @FXML private ImageView heroTitleImage;
    @FXML private Label heroDescription;
    @FXML private HBox heroStars;
    @FXML private HBox typeTag;
    @FXML private Label heroCategories;
    @FXML private Label heroAge;
    @FXML private Label lastEpisodeLabel;
    @FXML private Label seriesStatusLabel;
    @FXML private Button playButton;
    @FXML private Button watchTrailerButton;
    @FXML private Button addToListButton;
    @FXML public Rectangle rect1, rect2, rect3, rect4, rect5;
    @FXML public Label typeLabel;
    @FXML private Rectangle typeLine;

    // ═══════════════════════ FIELDS ═══════════════════════
    private FeaturedService featuredService;
    private List<FeaturedItem> latestItems;
    private int currentIndex = 0;
    private Timeline autoSlide;
    private MylistService myListService;
    private FeaturedItem currentItem;
    private EpisodeProgressService episodeProgressService = new EpisodeProgressService();
    private FilmProgressService filmProgressService = new FilmProgressService(featuredService);

    // ═══════════════════════ INIT ═══════════════════════
    @FXML
    public void initialize() {
        featuredService = new FeaturedService();
        myListService = new MylistService();

        MyListManager.getInstance().addListener((filmId, serieId) -> {
            Platform.runLater(() -> {
                if (currentItem != null) {
                    int currentFilmId = "film".equalsIgnoreCase(currentItem.getType()) ? currentItem.getId() : 0;
                    int currentSerieId = "serie".equalsIgnoreCase(currentItem.getType()) ? currentItem.getSerieId() : 0;
                    if (currentFilmId == filmId && currentSerieId == serieId) {
                        updateAddButton(addToListButton, currentItem);
                    }
                }
            });
        });

        setupCarousel();

        DropShadow soft = new DropShadow();
        soft.setColor(Color.BLACK);
        soft.setRadius(5);
        soft.setOffsetX(1);
        soft.setOffsetY(1);
        typeLabel.setEffect(soft);
    }

    // ═══════════════════════ MY LIST ═══════════════════════
    private void pumpButton(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.2);  st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void handleAddToList() {
        if (currentItem == null) return;
        int userId = Session.getUserId();
        int filmId  = "film".equalsIgnoreCase(currentItem.getType())  ? currentItem.getId()      : 0;
        int serieId = "serie".equalsIgnoreCase(currentItem.getType()) ? currentItem.getSerieId() : 0;
        boolean alreadyAdded = myListService.isInList(userId, filmId, serieId);
        if (alreadyAdded) myListService.removeItem(userId, filmId, serieId);
        else              myListService.addItem(userId, filmId, serieId);
        updateAddButton(addToListButton, currentItem);
        MyListManager.getInstance().notifyItemUpdated(filmId, serieId);
    }

    private void updateAddButton(Button button, FeaturedItem item) {
        if (item == null) return;
        int userId  = Session.getUserId();
        int filmId  = item.getType().equalsIgnoreCase("film")  ? item.getId()      : 0;
        int serieId = item.getType().equalsIgnoreCase("serie") ? item.getSerieId() : 0;
        boolean alreadyAdded = myListService.isInList(userId, filmId, serieId);
        if (alreadyAdded) {
            button.setText("✔ Added");
            button.setStyle("-fx-background-color:#00aaff;-fx-font-weight:bold;-fx-text-fill:white;-fx-font-size:16;-fx-padding:6 25;-fx-background-radius:2;");
        } else {
            button.setText("➕ My List");
            button.setStyle("-fx-background-color:transparent;-fx-border-color:#00aaff;-fx-border-width:2;-fx-text-fill:#00aaff;-fx-font-size:16;-fx-padding:6 20;-fx-background-radius:2;");
        }
        pumpButton(button);
    }

    // ═══════════════════════ CAROUSEL ═══════════════════════
    private void setupCarousel() {
        try {
            latestItems = featuredService.getLatestFeatured(4);
            if (latestItems == null || latestItems.isEmpty()) return;

            Rectangle[] rects = {rect1, rect2, rect3, rect4, rect5};
            highlightAndShow(rects, 0);

            for (int i = 0; i < rects.length; i++) {
                final int index = i;
                rects[i].setOnMouseClicked(e -> {
                    highlightAndShow(rects, index);
                    if (autoSlide != null) autoSlide.playFromStart();
                });
            }

            autoSlide = new Timeline(new KeyFrame(Duration.seconds(7), e -> {
                currentIndex = (currentIndex + 1) % latestItems.size();
                highlightAndShow(rects, currentIndex);
            }));
            autoSlide.setCycleCount(Timeline.INDEFINITE);
            autoSlide.play();

            Button[] buttons = {playButton, watchTrailerButton, addToListButton};
            for (Button btn : buttons) {
                btn.setOnMouseEntered(e -> {
                    autoSlide.pause();
                    ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
                    st.setToX(1.08); st.setToY(1.08); st.play();
                });
                btn.setOnMouseExited(e -> {
                    autoSlide.play();
                    ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
                    st.setToX(1.0); st.setToY(1.0); st.play();
                });
            }

            watchTrailerButton.setOnAction(e -> { if (autoSlide != null) autoSlide.pause(); showTrailerPopup(); });
            playButton.setOnAction(e -> {
                if (autoSlide != null) autoSlide.pause();
                String type = currentItem.getType().toLowerCase();
                if (type.equals("film"))  showFilmPopup(currentItem);
                else if (type.equals("serie")) showSeriePopup(currentItem);
            });
            addToListButton.setOnAction(e -> handleAddToList());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayFeatured(FeaturedItem item) {
        heroBackground.setImage(ImageUtil.load(item.getMainImageUrl()));

        if (item.getTitleImageUrl() != null && !item.getTitleImageUrl().isEmpty()) {
            heroTitleImage.setImage(ImageUtil.load(item.getTitleImageUrl()));
            heroTitleImage.setVisible(true);
        } else {
            heroTitleImage.setImage(ImageUtil.load(null));
            heroTitleImage.setVisible(false);
        }

        heroDescription.setText(item.getSynopsis() != null ? item.getSynopsis() : "");
        heroCategories.setText(item.getCategoriesAsString() != null ? item.getCategoriesAsString() : "");

        String age = item.getAgeRating();
        boolean hasAge = age != null && !age.isBlank();
        heroAge.setText(hasAge ? age : "");
        heroAge.setVisible(hasAge);
        heroAge.setManaged(hasAge);

        heroStars.getChildren().clear();
        heroStars.getChildren().clear(); 
        heroStars.setSpacing(4);         
        heroStars.setAlignment(Pos.CENTER_LEFT);

     // 1. Prepare the container
        heroStars.getChildren().clear();
        heroStars.setSpacing(4);
        heroStars.setAlignment(Pos.CENTER_LEFT);

        double rating = item.getRating(); 
        double size = 20.0;

        for (int i = 1; i <= 5; i++) {
            StackPane starPane = new StackPane();
            starPane.setAlignment(Pos.CENTER_LEFT);

            // Background (Gray)
            Text starEmpty = new Text("★");
            starEmpty.setFill(Color.LIGHTGRAY);
            starEmpty.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            starEmpty.setBoundsType(TextBoundsType.VISUAL); 

            // Foreground (Blue)
            Text starFilled = new Text("★");
            starFilled.setFill(Color.DEEPSKYBLUE);
            starFilled.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            starFilled.setBoundsType(TextBoundsType.VISUAL); 

            double fill = Math.min(1.0, Math.max(0.0, rating - (i - 1)));

            if (fill >= 1.0) {
                starPane.getChildren().add(starFilled);
            } else if (fill <= 0.0) {
                starPane.getChildren().add(starEmpty);
            } else {
                Rectangle clip = new Rectangle(0, 0, fill * size, 40); 
                clip.setY(-10); 
                
                starFilled.setClip(clip);
                starPane.getChildren().addAll(starEmpty, starFilled);
            }

            heroStars.getChildren().add(starPane);
        }

        typeLabel.setText("     " + item.getType().toUpperCase());

        boolean isSerie = "serie".equalsIgnoreCase(item.getType());
        String lastEp = isSerie ? "S" + item.getSeasonNumber() + ":E" + item.getLastEpisodeNumber() : "";
        boolean hasLastEp = isSerie && item.getSeasonNumber() != 0 && item.getLastEpisodeNumber() != 0;
        lastEpisodeLabel.setText(hasLastEp ? lastEp : "");
        lastEpisodeLabel.setVisible(hasLastEp);
        lastEpisodeLabel.setManaged(hasLastEp);

        String status = item.getSeasonStatus();
        boolean hasStatus = isSerie && status != null && !status.isBlank();
        seriesStatusLabel.setText(hasStatus ? status : "");
        seriesStatusLabel.setVisible(hasStatus);
        seriesStatusLabel.setManaged(hasStatus);

        boolean hasTrailer = item.getTrailerUrl() != null && !item.getTrailerUrl().isBlank();
        watchTrailerButton.setVisible(true);
        watchTrailerButton.setManaged(true);
        watchTrailerButton.setDisable(!hasTrailer);

        currentItem = item;
        updateAddButton(addToListButton, currentItem);
    }

    private void highlightAndShow(Rectangle[] rects, int index) {
        currentIndex = index;
        ParallelTransition indicatorAnim = new ParallelTransition();
        for (int i = 0; i < rects.length; i++) {
            FillTransition fill = new FillTransition(Duration.millis(250), rects[i]);
            fill.setToValue(i == index ? Color.WHITE : Color.web("#888888"));
            ScaleTransition scale = new ScaleTransition(Duration.millis(250), rects[i]);
            scale.setToX(i == index ? 1.5 : 1.0);
            scale.setToY(i == index ? 1.5 : 1.0);
            indicatorAnim.getChildren().addAll(fill, scale);
        }
        indicatorAnim.play();

        if (latestItems == null || index >= latestItems.size()) return;
        FeaturedItem nextItem = latestItems.get(index);

        Node[] contentNodes = {heroTitleImage, heroDescription, heroStars, heroCategories,
                heroAge, lastEpisodeLabel, seriesStatusLabel, playButton, watchTrailerButton, addToListButton};

        ParallelTransition fadeOut = new ParallelTransition();
        FadeTransition bgFadeOut = new FadeTransition(Duration.millis(400), heroBackground);
        bgFadeOut.setToValue(0);
        fadeOut.getChildren().add(bgFadeOut);

        for (Node node : contentNodes) {
            TranslateTransition slideLeft = new TranslateTransition(Duration.millis(700), node);
            slideLeft.setByX(-200);
            FadeTransition fade = new FadeTransition(Duration.millis(700), node);
            fade.setToValue(0);
            fadeOut.getChildren().addAll(slideLeft, fade);
        }

        TranslateTransition typeSlideOut = new TranslateTransition(Duration.millis(700), typeTag);
        typeSlideOut.setByX(200);
        typeSlideOut.play();
        fadeOut.play();

        fadeOut.setOnFinished(e -> {
            displayFeatured(nextItem);
            heroBackground.setOpacity(0);
            for (Node node : contentNodes) {
                node.setOpacity(0);
                node.setTranslateX(-700);
            }
            typeTag.setTranslateX(200);

            FadeTransition bgFadeIn = new FadeTransition(Duration.millis(600), heroBackground);
            bgFadeIn.setToValue(1);

            ParallelTransition slideIn = new ParallelTransition();
            for (Node node : contentNodes) {
                TranslateTransition slide = new TranslateTransition(Duration.millis(600), node);
                slide.setToX(0);
                FadeTransition fade = new FadeTransition(Duration.millis(600), node);
                fade.setToValue(1);
                slideIn.getChildren().addAll(slide, fade);
            }

            TranslateTransition typeSlideIn = new TranslateTransition(Duration.millis(600), typeTag);
            typeSlideIn.setToX(0);
            new ParallelTransition(bgFadeIn, slideIn, typeSlideIn).play();
        });
    }

    
    // ═══════════════════════ TRAILER POPUPS ═══════════════════════
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
    private void showTrailerPopup() {
        if (latestItems == null || latestItems.isEmpty()) return;

        FeaturedItem item = latestItems.get(currentIndex);
        String trailerUrl = item.getTrailerUrl();

        if (trailerUrl == null || trailerUrl.isBlank()) return;

        String videoPath;

        if (trailerUrl.startsWith("http")) {
            // 🌐 Online video
            videoPath = trailerUrl;

        } else {
            java.io.File file = new java.io.File(trailerUrl);

            if (file.exists()) {
                // 💻 Local file (Windows path)
                videoPath = file.toURI().toString();  // ✅ FIX
            } else {
                // 📦 Try resources folder
                URL resource = getClass().getResource(
                    trailerUrl.startsWith("/") ? trailerUrl : "/" + trailerUrl
                );

                if (resource != null) {
                    videoPath = resource.toExternalForm();
                } else {
                    System.out.println("Trailer NOT FOUND: " + trailerUrl);
                    return;
                }
            }
        }

        System.out.println("FINAL VIDEO PATH = " + videoPath);

        buildVideoPopup(
            videoPath,
            "Trailer - " + item.getTitle(),
            item,
            this.currentItem
        );
    }

    private void buildVideoPopup(String videoPath, String title, FeaturedItem listItem, FeaturedItem heroItem) {

        WebView webView = new WebView();
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
        popup.setOnHidden(e -> {
            webView.getEngine().load(null);
            if (autoSlide != null) autoSlide.play();
        });
        popup.showAndWait();
        
       
    }

    // ═══════════════════════ FILM POPUP ═══════════════════════
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

        Button close = new Button("✕");
        close.setStyle("-fx-background-color:#008cff; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:50%; -fx-padding:5 10;");
        close.setOnAction(e -> popup.close());
        addHoverAnimation(close);

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
                VBox right = new VBox(15);
                right.setAlignment(Pos.TOP_LEFT);
                right.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(right, Priority.ALWAYS);
                ImageView titleImage = new ImageView();
                titleImage.setFitHeight(150);
                titleImage.setPreserveRatio(true);

                titleImage.setImage(ImageUtil.load(film.getTitle_image_url()));
                HBox starsBox = new HBox(3);
                starsBox.getChildren().clear(); 
                starsBox.setSpacing(3); // Consistent with your HBox(3)
                starsBox.setAlignment(Pos.CENTER_LEFT);

                double rating = film.getRating(); 
                double size = 24.0;

                for (int i = 1; i <= 5; i++) {
                    StackPane starPane = new StackPane();
                    starPane.setAlignment(Pos.CENTER_LEFT);

                    // 1. Create the Background (Empty) Star
                    Label bgStar = new Label("★");
                    bgStar.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
                    bgStar.setTextFill(Color.LIGHTGRAY);

                    // 2. Create the Foreground (Filled) Star
                    Label fgStar = new Label("★");
                    fgStar.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
                    fgStar.setTextFill(Color.DEEPSKYBLUE);

                    // 3. Calculate how much of this specific star is filled
                    double fill = Math.min(1.0, Math.max(0.0, rating - (i - 1)));

                    if (fill >= 1.0) {
                        // Full Star
                        starPane.getChildren().add(fgStar);
                    } else if (fill <= 0) {
                        // Empty Star
                        starPane.getChildren().add(bgStar);
                    } else {
                        // Partial Star Clipping Logic
                        // We use the 'size' variable (24) to calculate the clip width
                        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(fill * size, size * 1.5);
                        fgStar.setClip(clip);
                        
                        // Add both: Background is visible through the parts not covered by the clipped Foreground
                        starPane.getChildren().addAll(bgStar, fgStar);
                    }

                    starsBox.getChildren().add(starPane);
                }

                int totalMinutes = (int) film.getDuration();
                Label duration = new Label("⏱ " + totalMinutes/60 + "h " + totalMinutes%60 + "min");
                duration.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:16;");

                Label casting = new Label("Casting: " + (film.getCasting() != null ? film.getCasting() : ""));
                casting.setWrapText(true); casting.setMaxWidth(600);
                casting.setStyle("-fx-text-fill:#cccccc; -fx-font-size:16;");

                String cats = film.getCategories() != null ?
                    film.getCategories().stream().map(c -> c.getName()).reduce((a, b) -> a + " • " + b).orElse("") : "";
                Label categories = new Label("Categories: " + cats);
                categories.setWrapText(true); categories.setMaxWidth(600);
                categories.setStyle("-fx-text-fill:#00aaff; -fx-font-size:16;");

                Label synopsis = new Label(film.getSynopsis() != null ? film.getSynopsis() : "");
                synopsis.setWrapText(true); synopsis.setMaxWidth(600);
                synopsis.setStyle("-fx-text-fill:#cccccc;-fx-font-size:16;");

                // Watch status
                int userId = Session.getUserId();
                int filmId = film.getFilm_id();
                int dur    = (int) film.getDuration();
                WatchStatus watchStatus;
                if (!filmProgressService.exists(userId, filmId)) watchStatus = WatchStatus.NOT_STARTED;
                else {
                    int last = filmProgressService.getLastPosition(userId, filmId);
                    watchStatus = (last >= dur - 2) ? WatchStatus.COMPLETED : WatchStatus.IN_PROGRESS;
                }
                Label statusLabel = new Label(watchStatus.toString());
                statusLabel.setStyle(
                    "-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;" +
                    "-fx-padding: 4 10; -fx-background-radius: 20; -fx-border-radius: 20;" +
                    "-fx-background-color: " + switch (watchStatus) {
                        case NOT_STARTED -> "#777777";
                        case IN_PROGRESS -> "#008cff";
                        case COMPLETED   -> "#00c853";
                    } + ";"
                );

                right.getChildren().addAll(titleImage, starsBox, duration, casting, categories, synopsis, statusLabel);
                filmBox.getChildren().addAll(poster, right);
                content.getChildren().add(filmBox);

                Button trailer = new Button("Trailer");
                trailer.setStyle(
                    "-fx-background-color:transparent;-fx-background-radius:30;-fx-border-color:#00aaff;" +
                    "-fx-border-width:2;-fx-border-radius:50%;-fx-text-fill:#00aaff;-fx-font-size:20;" +
                    "-fx-padding:6 20;-fx-effect:dropshadow(gaussian,#00aaff,10,0,0,0);"
                );
                trailer.setOnAction(e -> showTrailerPopup(film, 0));
                addHoverAnimation(trailer);

                Button play = new Button("▶");
                play.setStyle(
                    "-fx-background-color:transparent;-fx-background-radius:30;-fx-border-color:#00aaff;" +
                    "-fx-border-width:2;-fx-border-radius:50%;-fx-text-fill:#00aaff;-fx-font-size:20;" +
                    "-fx-padding:6 20;-fx-effect:dropshadow(gaussian,#00aaff,10,0,0,0);"
                );
                play.setOnAction(e -> { popup.close(); goToLecturePageFilm(film.getFilm_id()); });

                ScaleTransition pulse = new ScaleTransition(Duration.millis(800), play);
                pulse.setFromX(1); pulse.setFromY(1);
                pulse.setToX(1.10); pulse.setToY(1.10);
                pulse.setCycleCount(Animation.INDEFINITE);
                pulse.setAutoReverse(true);
                pulse.play();

                content.getChildren().add(new HBox(20, trailer, play) {{ setAlignment(Pos.CENTER_RIGHT); setMaxWidth(Double.MAX_VALUE); }});

                root.getChildren().add(content);
                FadeTransition fade = new FadeTransition(Duration.millis(400), root);
                fade.setFromValue(0); fade.setToValue(1); fade.play();
            }
        } catch (SQLException ex) { ex.printStackTrace(); }

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
    }

    // ═══════════════════════ SERIE POPUP ═══════════════════════
    public void showSeriePopup(FeaturedItem item) {
        if (item == null || !"serie".equalsIgnoreCase(item.getType())) return;

        Serie serie;
        try {
            serie = featuredService.getFullSerie(item.getSerieId());
            if (serie == null) return;
        } catch (SQLException e) { e.printStackTrace(); return; }

        final Stage popup = new Stage();
        popup.initOwner(rootPane.getScene().getWindow());
        popup.initModality(Modality.WINDOW_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.0);");
        root.setPadding(new Insets(30));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: rgba(0,0,0,0.0); -fx-background-color: rgba(0,0,0,0.0);");
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(600);
        scrollPane.setPrefWidth(1400);
        scrollPane.setMaxWidth(1400);
        scrollPane.setMaxHeight(500);

        HBox slider = new HBox(40);
        slider.setAlignment(Pos.CENTER_LEFT);
        slider.setPadding(new Insets(50, 50, 50, 50));

        List<Season> seasons = serie.getSeasons();
        List<StackPane> cards = new ArrayList<>();
        final int[] currentIdx = {0};

        for (int i = 0; i < seasons.size(); i++) {
            StackPane card = createSeasonCard(serie, i, popup);
            int index = i;
            card.setOnMouseClicked(e -> {
                currentIdx[0] = index;
                updateSeasonSlider(cards, currentIdx[0]);
                centerSlide(scrollPane, card, slider);
            });
            cards.add(card);
            slider.getChildren().add(card);
        }

        scrollPane.setContent(slider);
        root.getChildren().add(scrollPane);

        Button left = new Button("<");
        styleSlideButton(left);
        left.setOnAction(e -> {
            if (currentIdx[0] > 0) {
                currentIdx[0]--;
                updateSeasonSlider(cards, currentIdx[0]);
                centerSlide(scrollPane, cards.get(currentIdx[0]), slider);
            }
        });

        Button right = new Button(">");
        styleSlideButton(right);
        right.setOnAction(e -> {
            if (currentIdx[0] < cards.size() - 1) {
                currentIdx[0]++;
                updateSeasonSlider(cards, currentIdx[0]);
                centerSlide(scrollPane, cards.get(currentIdx[0]), slider);
            }
        });

        StackPane.setAlignment(left, Pos.CENTER_LEFT);
        StackPane.setMargin(left, new Insets(0, 0, 0, 10));
        StackPane.setAlignment(right, Pos.CENTER_RIGHT);
        StackPane.setMargin(right, new Insets(0, 10, 0, 0));
        root.getChildren().addAll(left, right);

        root.setOnMouseMoved(e -> {
            fadeButton(left,  e.getX() < 150 ? 1 : 0, 200);
            fadeButton(right, e.getX() > root.getWidth() - 150 ? 1 : 0, 200);
        });

        Button close = new Button("✕");
        addHoverAnimation(close);
        close.setStyle("-fx-background-color:#008cff;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:50%;-fx-padding:5 10;");
        close.setOnAction(e -> popup.close());
        StackPane.setAlignment(close, Pos.TOP_RIGHT);
        StackPane.setMargin(close, new Insets(20));
        root.getChildren().add(close);

        currentIdx[0] = 0;
        updateSeasonSlider(cards, currentIdx[0]);
        Platform.runLater(() -> centerSlide(scrollPane, cards.get(0), slider));

        root.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(400), root);
        fade.setToValue(1);
        fade.play();

        root.setMaxWidth(Double.MAX_VALUE);
        root.setMaxHeight(Double.MAX_VALUE);
        root.setPrefWidth(Double.MAX_VALUE);
        root.setPrefHeight(Double.MAX_VALUE);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.setWidth(rootPane.getScene().getWidth());
        popup.setHeight(rootPane.getScene().getHeight());
        popup.showAndWait();
    }

    // ═══════════════════════ SLIDE HELPERS ═══════════════════════
    private void styleSlideButton(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent;-fx-text-fill: #00aaff;" +
            "-fx-font-size: 36;-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,255,255,0.7), 10,0,0,0);"
        );
        btn.setOpacity(0);
    }

    private void fadeButton(Button button, double targetOpacity, double durationMs) {
        new Timeline(new KeyFrame(Duration.millis(durationMs),
            new KeyValue(button.opacityProperty(), targetOpacity, Interpolator.EASE_BOTH))).play();
    }

    private void updateSeasonSlider(List<StackPane> cards, int currentIndex) {
        for (int i = 0; i < cards.size(); i++) {
            int offset = i - currentIndex;
            double scale   = offset == 0 ? 1.1 : Math.abs(offset) == 1 ? 0.95 : Math.abs(offset) == 2 ? 0.9 : 0.85;
            double opacity = offset == 0 ? 1.0 : Math.abs(offset) == 1 ? 0.85 : Math.abs(offset) == 2 ? 0.6 : 0.4;
            StackPane card = cards.get(i);
            new Timeline(new KeyFrame(Duration.millis(400),
                new KeyValue(card.scaleXProperty(),   scale,   Interpolator.EASE_BOTH),
                new KeyValue(card.scaleYProperty(),   scale,   Interpolator.EASE_BOTH),
                new KeyValue(card.opacityProperty(),  opacity, Interpolator.EASE_BOTH)
            )).play();
        }
    }

    private void centerSlide(ScrollPane scrollPane, StackPane card, HBox slider) {
        double scrollWidth     = slider.getWidth();
        double scrollPaneWidth = scrollPane.getViewportBounds().getWidth();
        double cardCenter      = card.getBoundsInParent().getMinX() + card.getBoundsInParent().getWidth() / 2.0;
        double hValue          = Math.min(Math.max((cardCenter - scrollPaneWidth / 2) / (scrollWidth - scrollPaneWidth), 0), 1);
        new Timeline(new KeyFrame(Duration.millis(400),
            new KeyValue(scrollPane.hvalueProperty(), hValue, Interpolator.EASE_BOTH))).play();
    }

    // ═══════════════════════ SEASON CARD ═══════════════════════
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
        starsBox.getChildren().clear(); 
        starsBox.setAlignment(Pos.CENTER_LEFT);
        starsBox.setSpacing(2); 

        double rating = s.getRating(); 
        double fontSize = 15.0;

        for (int i = 1; i <= 5; i++) {
            StackPane starPane = new StackPane();
            starPane.setAlignment(Pos.CENTER_LEFT);
            Label bgStar = new Label("★");
            bgStar.setStyle("-fx-font-size: 15px; -fx-text-fill: rgba(255,255,255,0.15);");

            Label fgStar = new Label("★");
            fgStar.setStyle("-fx-font-size: 15px; -fx-text-fill: #38bdf8; -fx-font-weight: bold;");

            // 3. Calculate Fill (0.0 to 1.0)
            double fill = Math.min(1.0, Math.max(0.0, rating - (i - 1)));

            if (fill >= 1.0) {
                // Star is fully blue
                starPane.getChildren().add(fgStar);
            } else if (fill <= 0) {
                // Star is fully dim
                starPane.getChildren().add(bgStar);
            } else {
               
                javafx.scene.shape.Rectangle clip1 = new javafx.scene.shape.Rectangle(fill * fontSize, 25);
                fgStar.setClip(clip1);
                
                starPane.getChildren().addAll(bgStar, fgStar);
            }

            starsBox.getChildren().add(starPane);
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

    // ═══════════════════════ EPISODE ROW ═══════════════════════
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
        miniStars.getChildren().clear(); 
        miniStars.setSpacing(1);
        miniStars.setAlignment(Pos.CENTER_LEFT);

        double rawRating = ep.getRating(); 
        double size = 10.0; 

        for (int i = 1; i <= 5; i++) {
            StackPane starPane = new StackPane();
            starPane.setAlignment(Pos.CENTER_LEFT);

            Label bgStar = new Label("★");
            bgStar.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.12); -fx-padding: 0;");

            Label fgStar = new Label("★");
            fgStar.setStyle("-fx-font-size: 10px; -fx-text-fill: #38bdf8; -fx-font-weight: bold; -fx-padding: 0;");

            double fill = Math.min(1.0, Math.max(0.0, rawRating - (i - 1)));

            if (fill >= 1.0) {
                starPane.getChildren().add(fgStar);
            } else if (fill <= 0) {
                starPane.getChildren().add(bgStar);
            } else {
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(fill * size, 15);
                fgStar.setClip(clip);
                
                starPane.getChildren().addAll(bgStar, fgStar);
            }

            miniStars.getChildren().add(starPane);
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

    // ═══════════════════════ BUTTON STYLES ═══════════════════════
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

    private void addHoverAnimation(Button btn) {
        ScaleTransition up   = new ScaleTransition(Duration.millis(120), btn);
        up.setToX(1.15); up.setToY(1.15);
        ScaleTransition down = new ScaleTransition(Duration.millis(120), btn);
        down.setToX(1.0); down.setToY(1.0);
        btn.setOnMouseEntered(e -> up.playFromStart());
        btn.setOnMouseExited(e  -> down.playFromStart());
    }

    // ═══════════════════════ SCROLLBAR HELPERS ═══════════════════════
    private void updateThumbSize(ScrollPane scrollPane, ScrollBar scrollBar) {
        Node content = scrollPane.getContent();
        if (content == null) return;
        content.layoutBoundsProperty().addListener((obs, ov, nv) -> {
            double ratio = Math.max(0.05, Math.min(nv.getHeight() > 0
                ? scrollPane.getViewportBounds().getHeight() / nv.getHeight() : 1.0, 1.0));
            scrollBar.setVisibleAmount(ratio);
        });
        scrollPane.viewportBoundsProperty().addListener((obs, ov, nv) -> {
            double ch = content.getLayoutBounds().getHeight();
            if (ch <= 0) return;
            scrollBar.setVisibleAmount(Math.max(0.05, Math.min(nv.getHeight() / ch, 1.0)));
        });
    }

    // ═══════════════════════ NAVIGATION ═══════════════════════
    private void goToLecturePageFilm(int filmId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("view/fxml/LecturePage.fxml"));
            Parent root = loader.load();
            LecturePageController controller = loader.getController();
            controller.initFilm(filmId);
            ((Stage) rootPane.getScene().getWindow()).getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void goToLecturePageEpisode(int serieId, int seasonNum, int episodeId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("view/fxml/LecturePage.fxml"));
            Parent root = loader.load();
            LecturePageController controller = loader.getController();
            controller.initEpisode(serieId, seasonNum, episodeId);
            ((Stage) rootPane.getScene().getWindow()).getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }
}