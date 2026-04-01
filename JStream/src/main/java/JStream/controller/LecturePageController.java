package JStream.controller;

import java.net.URL;
import java.sql.SQLException;

import JStream.entity.Episode;
import JStream.entity.FeaturedItem;
import JStream.entity.Film;
import JStream.entity.MyListManager;
import JStream.entity.Season;
import JStream.entity.Serie;
import JStream.entity.Session;
import JStream.service.FeaturedService;
import JStream.service.MylistService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;



public class LecturePageController {
	@FXML private String currentTrailerUrl;
	@FXML private Button addToListButton;
	private MylistService mylistService = new MylistService();
	private FeaturedItem currentItem;
    // --- NAVBAR ---
    @FXML private ImageView logoNav, bellIcon;
    @FXML private Button btnMostWatched, btnMyList;
    @FXML private StackPane bellContainer;
    @FXML private Circle notificationCircle;
    @FXML private Button btnBack, playButton, btnNotification, profileBtn;

    // --- HERO & CONTAINERS ---
    @FXML private VBox mainContainer;
    @FXML private ImageView backgroundImage, posterImage;
    @FXML private Label titleLabel, scoreLabel, yearLabel, durationLabel, ageRatingLabel, descriptionLabel;
    @FXML private Label starringLabel, directorLabel, categoriesLabel, episodeInfoLabel;
    @FXML private HBox starsBox, castBox;

    // --- TABS ---
    @FXML private Rectangle lineOverview, lineTrailers;
    @FXML private Button tabOverview, tabTrailers;

    private AudioClip bellSound;
    private Popup notificationPopup = new Popup();
    private VBox notificationContent = new VBox();
    private boolean isNotificationVisible = false;

    // ============== INITIALIZE ==============
    @FXML
    public void initialize() {
    	setupNavbar();
        setupNotificationSystem();
        setupTabLogic(); // Logic mta3 el tabs Overview/Trailers
        
       
        if (btnBack != null) {
            btnBack.setOnAction(e -> handleBackAction());
        }
        
        btnNotification.setOnAction(e -> {
            if (isNotificationVisible) {
                hideNotification(); // El dot tetna7a b-animation mezyena
            }
            showPopup();});
        // Entrance Animation
        if (mainContainer != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), mainContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
        populateStars(9.0);
  if (addToListButton != null) {
            
            addToListButton.setOnAction(e -> handleAddToList());
          
        }        if (posterImage != null) addHoverEffect(posterImage);
        if (playButton != null) addHoverEffect(playButton);
       
        if (btnBack != null) {
            addButtonInteractions(btnBack);
            
        }
        
        if (btnMostWatched != null) {
            addButtonInteractions(btnMostWatched); 
            btnMostWatched.setOnAction(e -> navigateTo("/view/fxml/MyHistory.fxml"));
        }
 
        if (btnMyList != null) {
            addButtonInteractions(btnMyList);
            btnMyList.setOnAction(e -> navigateTo("/view/fxml/MyList.fxml"));
        }
        loadCast();
    }
  
   
    private void navigateTo(String fxmlPath) {
        try {
            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) {
                System.err.println("FXML mal9itchou: " + fxmlPath);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.getScene().setRoot(root);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTabLogic() {
        if (tabOverview != null && tabTrailers != null) {
            tabOverview.setOnAction(e -> {
                lineOverview.setVisible(true);
                lineTrailers.setVisible(false);
                tabOverview.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
                tabTrailers.setStyle("-fx-background-color: transparent; -fx-text-fill: #7a80a0;");
            });

            tabTrailers.setOnAction(e -> {
                lineOverview.setVisible(false);
                lineTrailers.setVisible(true);
                tabTrailers.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
                tabOverview.setStyle("-fx-background-color: transparent; -fx-text-fill: #7a80a0;");

                // 🔥 Hna el "Trigger" mta3 el Video
                if (this.currentTrailerUrl != null && !this.currentTrailerUrl.isEmpty()) {
                    showTrailerPopup(this.currentTrailerUrl);
                } else {
                    System.out.println("⚠️ Trailer URL mal9inehch l-hal content!");
                }
            });
        }
    }
    private void addHoverEffect(Node node) {
        ScaleTransition stIn = new ScaleTransition(Duration.millis(200), node);
        stIn.setToX(1.05); stIn.setToY(1.05);

        ScaleTransition stOut = new ScaleTransition(Duration.millis(200), node);
        stOut.setToX(1.0); stOut.setToY(1.0);

        node.setOnMouseEntered(e -> {
            stIn.play();
            node.setEffect(new javafx.scene.effect.DropShadow(25, Color.web("#2d54ff", 0.6)));
        });
        node.setOnMouseExited(e -> {
            stOut.play();
            node.setEffect(null); 
        });
    }

    private void loadCast() {
        if (castBox == null) return;
        castBox.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            VBox actorCard = createActorCard("Actor Name", "/assets/images/profile.png");
            castBox.getChildren().add(actorCard);
        }
    }

    private VBox createActorCard(String name, String imgPath) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        try {
            ImageView img = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
            img.setFitWidth(80); img.setFitHeight(80);
            Circle clip = new Circle(40, 40, 40);
            img.setClip(clip);
            Label n = new Label(name); n.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            card.getChildren().addAll(img, n);
            addHoverEffect(card);
        } catch (Exception e) {}
        return card;
    }

    private void setupNavbar() {
        try {
            if (logoNav != null) logoNav.setImage(new Image(getClass().getResourceAsStream("/assets/images/logo/Raksha.png")));
            if (bellIcon != null) bellIcon.setImage(new Image(getClass().getResourceAsStream("/assets/images/bellwhiter.png")));
        } catch (Exception e) {}
    }

    private void setupNotificationSystem() {
        try {
            bellSound = new AudioClip(getClass().getResource("/assets/sounds/notification.mp3").toString());
        } catch (Exception e) {}

        notificationContent.setStyle("-fx-background-color: #111111; -fx-background-radius: 8; -fx-padding: 15; -fx-spacing: 10;");
        notificationContent.setPrefWidth(250);
        Label popTitle = new Label("Notifications");
        popTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        notificationContent.getChildren().add(popTitle);
        notificationPopup.getContent().add(notificationContent);
        notificationPopup.setAutoHide(true);

        bellContainer.setOnMouseEntered(e -> {
            shakeBell();
            if (bellSound != null) bellSound.play();
            showNotificationDot();
            showPopup();
        });
    }
    

    public void initFilm(int filmId) {
        if (episodeInfoLabel != null) episodeInfoLabel.setVisible(false);
        try {
            Film film = new FeaturedService().getFilmDetails(filmId);
            this.currentTrailerUrl = film.getVideo_url();
            this.currentItem = new FeaturedItem(
            	    film.getFilm_id(),
            	    film.getTitle(),
            	    film.getSynopsis(),
            	    film.getVideo_url(),
            	    film.getImage_url(),
            	    film.getTitle_image_url(),
            	    film.getPoster_url(),
            	    film.getCategories() != null ?
            	        film.getCategories().stream()
            	            .map(c -> c.getName())
            	            .collect(java.util.stream.Collectors.toList())
            	        : new java.util.ArrayList<>(),
            	    film.getAge_rating(),
            	    film.getRating()
            	);
            updateUI(film.getTitle(), film.getSynopsis(), film.getDuration() + " min", 
                     film.getRating(), film.getCasting(), film.getPoster_url(), null,null,0);
            if (scoreLabel != null) scoreLabel.setText(String.valueOf(film.getRating()));
            if (addToListButton != null) updateAddButton(addToListButton, currentItem);
            
        } catch (SQLException e) { e.printStackTrace(); }
    }
    private Serie serie ;
    private Episode ep ;
    public void initEpisode(int serieId, int seasonNum, int episodeNum) {
        try {
             serie = new FeaturedService().getFullSerie(serieId);
            ep = findEpisodeInSerie(serie, seasonNum, episodeNum);
            
            if (ep != null) {
                // Nlawjou 3al Saison el s7i7a besh njibdou el Trailer mte3ha
                if (serie.getSeasons() != null) {
                    for (Season s : serie.getSeasons()) {
                        if (s.getSeasonNum() == seasonNum) {
                            this.currentTrailerUrl = s.getTrailerUrl(); // Sajjel el URL
                            break; 
                        }
                    }
                    this.currentItem = new FeaturedItem(
                    		ep.getSeasonId(),
                    	    serie.getSerieId(),
                    	    serie.getTitle(),
                    	    serie.getSynopsis(),
                    	    this.currentTrailerUrl,
                    	    serie.getCovertUrl(),
                    	    serie.getTitleUrl(),
                    	    serie.getCovertUrl(),
                    	    serie.getCategories() != null ?
                    	        serie.getCategories().stream()
                    	            .map(c -> c.getName())
                    	            .collect(java.util.stream.Collectors.toList())
                    	        : new java.util.ArrayList<>(),
                    	    serie.getAge_rating(),
                    	    serie.getRating(),
                    	    null, seasonNum, episodeNum
                    	);
                }

                updateUI(ep.getTitle(), ep.getResume() != null ? ep.getResume() : serie.getSynopsis(), 
                         ep.getDuration() + " min", serie.getRating(), serie.getCasting(), 
                         serie.getCovertUrl(), "S" + seasonNum + " - E" + episodeNum,ep.getVideoUrl(),ep.getEpId());
                if (addToListButton != null) updateAddButton(addToListButton, currentItem);
                
                if (scoreLabel != null) scoreLabel.setText(String.valueOf(serie.getRating()));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }

    public void updateUI(String title, String desc, String duration, int rating, String cast, String imgPath, String epInfo,String video,int id) {
        titleLabel.setText(title);
        descriptionLabel.setText(desc);
        durationLabel.setText(duration);
        starringLabel.setText(cast);
        
        if (epInfo != null && episodeInfoLabel != null) {
            episodeInfoLabel.setText(epInfo);
            episodeInfoLabel.setVisible(true);
        }

        if (imgPath != null) {
            try {
                Image img = new Image(getClass().getResourceAsStream(imgPath));
                posterImage.setImage(img);
                backgroundImage.setImage(img);
            } catch (Exception e) {}
        }
        populateStars(rating);
        if (playButton != null) {
        	if (playButton != null) {
        	    playButton.setOnAction(e -> {

        	        if (currentItem == null) {
        	            System.out.println("⚠️ No current item!");
        	            return;
        	        }

        	        // 🎬 FILM
        	        if ("film".equalsIgnoreCase(currentItem.getType())) {

        	            if (currentTrailerUrl != null) {
        	                openVideoPlayer(
        	                    currentTrailerUrl,
        	                    currentItem.getTitle(),
        	                    null // 👈 important (film = no episodeId)
        	                );
        	            } else {
        	                System.out.println("⚠️ Film video URL missing!");
        	            }

        	        // 📺 EPISODE
        	        } else if ("serie".equalsIgnoreCase(currentItem.getType())) {

        	            if (video != null) {
        	                openVideoPlayer(
        	                    video,
        	                    title,
        	                    id
        	                );
        	            } else {
        	                System.out.println("⚠️ Episode not loaded!");
        	            }
        	        }
        	    });
        	}}
    }

    private void populateStars(double rating) {
        if (starsBox == null) return;
        
        starsBox.getChildren().clear();
        
        // Na7i el "/ 2.0" ken el rating dima 3la 5
        double starsToHighlight = rating; 

        for (int i = 1; i <= 5; i++) {
            Label star = new Label("★");
            
            // Ken el i asgher mel rating (mathalan 3), i-highlighty el star
            if (i <= starsToHighlight) {
                star.setStyle("-fx-text-fill: #00d4ff; " + 
                              "-fx-font-size: 22px; " + 
                              "-fx-effect: dropshadow(three-pass-box, rgba(0, 212, 255, 0.8), 15, 0, 0, 0);");
            } else {
                star.setStyle("-fx-text-fill: #2a3140; " + 
                              "-fx-font-size: 22px;");
            }
            
            starsBox.getChildren().add(star);
        }
    }

    private void shakeBell() {
        RotateTransition rt = new RotateTransition(Duration.millis(100), bellIcon);
        rt.setFromAngle(-10); rt.setToAngle(10);
        rt.setCycleCount(4); rt.setAutoReverse(true);
        rt.play();
    }

    private void showNotificationDot() {
        if (!isNotificationVisible) {
            notificationCircle.setVisible(true);
            isNotificationVisible = true;
        }
    }
    private void hideNotification() {
        if (notificationCircle == null || !isNotificationVisible) return;
        
        isNotificationVisible = false;

        // Animation Parallel: Fade + Scale
        ParallelTransition hide = new ParallelTransition();

        FadeTransition fade = new FadeTransition(Duration.millis(300), notificationCircle);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), notificationCircle);
        scale.setToX(0);
        scale.setToY(0);

        hide.getChildren().addAll(fade, scale);
        
        // Ki toufa el animation, n-raj3ou el scale 1 (bech el marra el jaya tban s7i7a) w n-sakrou el visibility
        hide.setOnFinished(e -> {
            notificationCircle.setVisible(false);
            notificationCircle.setScaleX(1);
            notificationCircle.setScaleY(1);
            notificationCircle.setOpacity(1);
        });
        
        hide.play();
    }

    private void showPopup() {
        Bounds bounds = bellContainer.localToScreen(bellContainer.getBoundsInLocal());
        notificationPopup.show(bellContainer, bounds.getMinX() - 200, bounds.getMaxY() + 10);
    }

    private Episode findEpisodeInSerie(Serie serie, int seasonNum, int episodeNum) {
        for (Season s : serie.getSeasons()) {
            if (s.getSeasonNum() == seasonNum) {
                for (Episode ep : s.getEpisodes()) {
                    if (ep.getNumEpisode() == episodeNum) return ep;
                }
            }
        }
        return null;
    }
    @FXML
    private void handleBackAction() {
        try {
            // 1. Chargi el Home Page FXML
           
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/fxml/HomePage.fxml"));
            javafx.scene.Parent root = loader.load();

            // 2. jib el Stage el 7aliya mel button
            javafx.stage.Stage stage = (javafx.stage.Stage) btnBack.getScene().getWindow();

            // 3. Beddel el Scene
            stage.getScene().setRoot(root);
            
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    private void addButtonInteractions(Button btn) {
        // 1. Animation mta3 el kbor (Scale)
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), btn);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), btn);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        // 2. Glow Effect (DropShadow)
        javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
        glow.setColor(Color.web("#00d4ff", 0.8)); // Nafs el bleu mta3 el stars
        glow.setRadius(20);
        glow.setSpread(0.12);

        // 3. Event Handlers
        btn.setOnMouseEntered(e -> {
            scaleUp.play();
            btn.setEffect(glow);
        });

        btn.setOnMouseExited(e -> {
            scaleDown.play();
            btn.setEffect(null);
        });

        // Ken t7ebha "t-vibri" chwaya ki t-cliqui (optional)
        btn.setOnMousePressed(e -> {
            btn.setScaleX(0.95);
            btn.setScaleY(0.95);
        });
        
        btn.setOnMouseReleased(e -> {
            btn.setScaleX(1.1);
            btn.setScaleY(1.1);
        });
    }
   
    private void showTrailerPopup(String url) {
        try {
            URL videoUrl = getClass().getResource(url);
            if (videoUrl == null) {
                System.out.println("Video file not found: " + url);
                return;
            }

            String videoPath = url.startsWith("http") ? url : videoUrl.toExternalForm();

            javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
            webView.setPrefSize(1500, 700);

            String html =
                "<html><body style='margin:0; background:black;'>" +
                "<video width='100%' height='100%' controls autoplay>" +
                "<source src='" + videoPath + "' type='video/mp4'>" +
                "Your browser does not support the video tag." +
                "</video></body></html>";

            webView.getEngine().loadContent(html);

            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getBounds();
            double fullWidth = screenBounds.getWidth();
            double fullHeight = screenBounds.getHeight();
            double smallWidth = 1200;
            double smallHeight = 600;

            Stage popup = new Stage();
            popup.initOwner(btnBack.getScene().getWindow()); // 👈 hna el far9
            popup.initModality(Modality.WINDOW_MODAL);
            popup.setTitle("Trailer");
            popup.initStyle(StageStyle.TRANSPARENT);
            popup.setWidth(fullWidth);
            popup.setHeight(fullHeight);
            popup.setX(0);
            popup.setY(0);

            StackPane root = new StackPane();
            root.setStyle("-fx-background-color: rgba(0,0,0,0.85);");

            VBox layout = new VBox(15);
            layout.setStyle("-fx-background-color: rgba(0,0,0,0.2); -fx-background-radius:15; -fx-padding:15; -fx-alignment:center;");
            layout.setPrefSize(fullWidth, fullHeight);

            Button toggleSize = new Button("🗗");
            toggleSize.setStyle("-fx-background-color:#008cff;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:50%;-fx-padding:5 8;");

            Button exitButton = new Button("✕");
            exitButton.setStyle("-fx-background-color:#008cff;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:50%;-fx-padding:5 8;");
            exitButton.setOnAction(ev -> {
                webView.getEngine().load(null);
                popup.close();
                // Reset tab → Overview
                lineOverview.setVisible(true);
                lineTrailers.setVisible(false);
                tabOverview.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
                tabTrailers.setStyle("-fx-background-color: transparent; -fx-text-fill: #7a80a0;");
            });

            final boolean[] isFullScreen = {true};
            toggleSize.setOnAction(ev -> {
                if (isFullScreen[0]) {
                    popup.setWidth(smallWidth);
                    popup.setHeight(smallHeight);
                    popup.setX((screenBounds.getWidth() - smallWidth) / 2);
                    popup.setY((screenBounds.getHeight() - smallHeight) / 2);
                    layout.setPrefSize(smallWidth, smallHeight);
                    isFullScreen[0] = false;
                } else {
                    popup.setWidth(fullWidth);
                    popup.setHeight(fullHeight);
                    popup.setX(0); popup.setY(0);
                    layout.setPrefSize(fullWidth, fullHeight);
                    isFullScreen[0] = true;
                }
            });

            HBox topBar = new HBox(10, toggleSize, exitButton);
            topBar.setAlignment(Pos.TOP_RIGHT);
            topBar.setPadding(new javafx.geometry.Insets(10));
            topBar.setPickOnBounds(false);

            layout.getChildren().addAll(topBar, webView);
            root.getChildren().add(layout);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            popup.setScene(scene);

            popup.setOnHidden(ev -> {
                webView.getEngine().load(null);
                // Reset tab → Overview
                lineOverview.setVisible(true);
                lineTrailers.setVisible(false);
                tabOverview.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
                tabTrailers.setStyle("-fx-background-color: transparent; -fx-text-fill: #7a80a0;");
            });

            popup.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
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
        updateAddButton(addToListButton, currentItem);

        // Notify all other controllers to update
        MyListManager.getInstance().notifyItemUpdated(filmId, serieId);
    } private void updateAddButton(Button button, FeaturedItem item) {
        int userId = Session.getUserId();
        int filmId = 0;
        int serieId = 0;

        if ("film".equalsIgnoreCase(item.getType())) {
            filmId = item.getId();
        } else if ("serie".equalsIgnoreCase(item.getType())) {
            serieId = item.getSerieId();
        }

        if (mylistService.isInList(userId, filmId, serieId)) {
            button.setText("✔ added");
            button.setStyle("-fx-background-color:#00aaff;-fx-text-fill:white; -fx-background-radius: 25; -fx-cursor: hand; -fx-border-color: rgba(255,255,255,0);");
            
            pumpButton(button); // optional animation
        } else {
            button.setText("+ My List");
            button.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-background-radius: 25; -fx-cursor: hand; -fx-border-color: rgba(255,255,255,0);");
            pumpButton(button);
        }
    }
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
  
    private void openVideoPlayer(String videoUrl, String title, Integer episodeId) {

        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            System.err.println("❌ Video URL is NULL or empty!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/VideoPlayer.fxml"));
            Parent root = loader.load();
            VideoPlayerController controller = loader.getController();

            Stage videoStage = new Stage();
            videoStage.initOwner(mainContainer.getScene().getWindow());
            videoStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            videoStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            controller.setStage(videoStage);       
            controller.loadVideo(videoUrl, title); 
            controller.setParentController(this);
            if (episodeId == null) {
                // 🎬 FILM
                controller.setContext(currentItem.getId(), null);

                System.out.println("🎬 Mode: Film (ID: " + currentItem.getId() + ")");

            } else {
                // 📺 EPISODE
                controller.setContext(null, episodeId);

                // ✅ VERY IMPORTANT (for next episode)fui
                controller.setEpisodeContext(
                    ep.getSeasonId(),
                    ep.getNumEpisode()
                );

                System.out.println("📺 Mode: Episode (ID: " + episodeId + ")");
            }
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.BLACK);

            videoStage.setScene(scene);

            javafx.geometry.Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
            videoStage.setX(screen.getMinX());
            videoStage.setY(screen.getMinY());
            videoStage.setWidth(screen.getWidth());
            videoStage.setHeight(screen.getHeight());

            videoStage.show(); 
            controller.startPlayback();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur f-el VideoPlayer: " + e.getMessage());
        }
    }
}