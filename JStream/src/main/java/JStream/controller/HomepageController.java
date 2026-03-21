package JStream.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import JStream.entity.Category;
import JStream.entity.FeaturedItem;
import JStream.service.FeaturedService;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.util.Duration;
import javafx.scene.effect.InnerShadow;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;
public class HomepageController {

	@FXML
	private Pane carouselBackground;
	@FXML
	private Label carouselTitle;
	@FXML
	private Label carouselDescription;
	@FXML
	private ImageView logoImage;

	private int currentImageIndex = -1;

	private final String[] carouselImages = {
	        "/assets/images/t1.jpeg",
	        "/assets/images/t2.jpeg",
	        "/assets/images/t3.jpeg"
	};
	@FXML
	private HBox carouselIndicators;
	private double posterWidth = 160; 
	private Rectangle[] indicators;

	private final String[] titles = {"Movie 1", "Movie 2", "Movie 3"};
	private final String[] descriptions = {"Description 1", "Description 2", "Description 3"};
	
	    @FXML
	    private ImageView bellIcon;

	   
	    private Circle notificationDot;
	    private AudioClip bellSound;
	    private boolean isNotificationVisible = false; 
	    
	    @FXML
	    private Pane featuredBackground;

	    @FXML
	    private Label featuredTitle;

	    @FXML
	    private Label featuredDescription;

	    @FXML
	    private HBox featuredIndicators;
	    @FXML
	    private Timeline autoScrollTimeline;
	  
	 // Current mouse X inside scrollPane
	    private double mouseX = -1;

	    // Timeline for continuous scrolling
	    private Timeline scrollTimeline;
	    @FXML private Button btnHome, btnMovies, btnSeries, btnTvShows, btnMyList;
	    @FXML private Rectangle lineHome, lineMovies, lineSeries, lineTvShows, lineMyList;

	    private Button activeButton;
	    private Rectangle activeLine;
	    
	    @FXML
	    private TextField searchInput;

	    private boolean opened = false;
	    
	    private Popup suggestionsPopup = new Popup();
	    private VBox suggestionsContent = new VBox();
	    
	    @FXML
	    private StackPane bellContainer;

	    private Popup notificationPopup = new Popup();
	    private VBox notificationContent = new VBox();
	    private boolean popupVisible = false;
	    
	    @FXML
	    private void initialize() {
	    	/*Header section*/
	        activeButton = btnHome; 
	        activeLine = lineHome;
	        
	        setupButton(btnHome, lineHome);
	        setupButton(btnMovies, lineMovies);
	        setupButton(btnSeries, lineSeries);
	        setupButton(btnTvShows, lineTvShows);
	        setupButton(btnMyList, lineMyList);

	        animateLine(activeLine, activeButton.getWidth());
	        setButtonWhite(activeButton);
	        
	        
		logoImage.setImage(new Image(getClass().getResourceAsStream("/assets/images/logo/Raksha.png")));
	    
		/*Bell notification animation!*/
		 bellIcon.setImage(new Image(getClass().getResourceAsStream("/assets/images/bellwhiter.png")));

	        // Load sound
	        bellSound = new AudioClip(getClass().getResource("/assets/sounds/notification.mp3").toString());

	        // Create red notification dot
	        notificationDot = new Circle(4, Color.BLUE);
	        notificationDot.setTranslateX(10); // position top-right of bell
	        notificationDot.setTranslateY(-12);
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
	        
	       //search button
	        suggestionsContent.setStyle(
	                "-fx-background-color: #111;" +
	                "-fx-padding: 5;" +
	                "-fx-spacing: 2;" +
	                "-fx-background-radius: 6;"
	            );

	            suggestionsPopup.getContent().add(suggestionsContent);

	            // show suggestions on typing
	            searchInput.textProperty().addListener((obs, oldText, newText) -> {
	                if(newText.isEmpty()){
	                    suggestionsPopup.hide();
	                } else {
	                    showSuggestions(newText);
	                }
	            });

	            // hide popup when focus lost
	            searchInput.focusedProperty().addListener((obs, oldVal, newVal) -> {
	                if(!newVal){
	                    suggestionsPopup.hide();
	                }
	            });
		/*CarouselSection*/
	            loadCarouselsByCategory();
	    
         
	}
	    @FXML private VBox categoryContainer;
	    private final FeaturedService featuredService = new FeaturedService();

	    private void loadCarouselsByCategory() {
	        try {
	            List<Category> categories = featuredService.getAllCategories();

	            for (Category category : categories) {
	                // 1️⃣ Get items for the category first
	                List<FeaturedItem> items = featuredService.getItemsByCategory(category.getName());

	                // 2️⃣ Skip empty categories
	                if (items.isEmpty()) continue;

	                // 3️⃣ Load FXML only if items exist
	                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Carousel.fxml"));
	                Node carouselNode = loader.load();

	                // 4️⃣ Pass the items to the CarouselController
	                CarouselController carouselController = loader.getController();
	                carouselController.loadItems(items);      // set the items
	                carouselController.setCategoryTitle(category.getName()); // set the title

	                // 5️⃣ Add to VBox
	                categoryContainer.getChildren().add(carouselNode);
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	    /* buttons animation */
	    private void setupButton(Button btn, Rectangle line) {
	        // Hover effect
	        btn.setOnMouseEntered(e -> {
	            animateLine(line, btn.getWidth());
	            setButtonWhite(btn);
	        });

	        btn.setOnMouseExited(e -> {
	            if (btn != activeButton) {
	                animateLine(line, 0);
	                setButtonGray(btn);
	            }
	        });

	        // Click effect
	        btn.setOnAction(e -> setActive(btn, line));
	    }

	    private void setActive(Button btn, Rectangle line) {
	        // Reset old active
	        if (activeButton != null && activeLine != null) {
	            setButtonGray(activeButton);
	            animateLine(activeLine, 0);
	        }

	        // Set new active
	        activeButton = btn;
	        activeLine = line;
	        setButtonWhite(activeButton);
	        animateLine(activeLine, activeButton.getWidth());
	    }

	    private void animateLine(Rectangle line, double targetWidth) {
	        Timeline timeline = new Timeline();
	        KeyValue kv = new KeyValue(line.widthProperty(), targetWidth);
	        KeyFrame kf = new KeyFrame(Duration.millis(200), kv); // smooth 200ms
	        timeline.getKeyFrames().add(kf);
	        timeline.play();
	    }

	    private void setButtonWhite(Button btn) {
	        btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 16;");
	    }

	    private void setButtonGray(Button btn) {
	        btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #cccccc; -fx-font-size: 16;");
	    }
	
	    
	    @FXML
	    private void toggleSearch() {

	        Timeline animation;

	        if(!opened){

	            animation = new Timeline(
	                    new KeyFrame(Duration.millis(250),
	                            new KeyValue(searchInput.prefWidthProperty(), 220)
	                    )
	            );

	            searchInput.requestFocus();
	            opened = true;

	        }else{

	            animation = new Timeline(
	                    new KeyFrame(Duration.millis(250),
	                            new KeyValue(searchInput.prefWidthProperty(), 0)
	                    )
	            );

	            opened = false;
	        }

	        animation.play();
	    }
	    private void showSuggestions(String text){

	        suggestionsContent.getChildren().clear();

	        List<String> movies = List.of(
	            "Interstellar",
	            "Inception",
	            "Inside Out",
	            "Iron Man",
	            "Indiana Jones"
	        );

	        for(String movie : movies){

	            if(movie.toLowerCase().contains(text.toLowerCase())){

	                Label item = new Label(movie);
	                item.setStyle(
	                    "-fx-text-fill: white;" +
	                    "-fx-padding: 8 10;" +
	                    "-fx-background-radius: 4;"
	                );

	                item.setOnMouseEntered(e -> item.setStyle(
	                    "-fx-background-color: #222;" +
	                    "-fx-text-fill: white;" +
	                    "-fx-padding: 8 10;" +
	                    "-fx-background-radius: 4;"
	                ));

	                item.setOnMouseExited(e -> item.setStyle(
	                    "-fx-text-fill: white;" +
	                    "-fx-padding: 8 10;" +
	                    "-fx-background-radius: 4;"
	                ));

	                item.setOnMouseClicked(e -> {
	                    searchInput.setText(movie);
	                    suggestionsPopup.hide();
	                });

	                suggestionsContent.getChildren().add(item);
	            }
	        }

	        if(!suggestionsPopup.isShowing()){
	            // position popup under the search field
	            Bounds bounds = searchInput.localToScreen(searchInput.getBoundsInLocal());
	            suggestionsPopup.show(searchInput, bounds.getMinX(), bounds.getMaxY() + 2);
	        }
	    }
	    
	    private void setupNotificationPopup() {
	        // Style the VBox
	        notificationContent.setStyle(
	            "-fx-background-color: #1e1e1e;" + // dark modern background
	            "-fx-background-radius: 10;" +
	            "-fx-padding: 10;" +
	            "-fx-spacing: 8;" +
	            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 10,0,0,2);"
	        );
	        notificationContent.setPrefWidth(250); // width of popup
	        notificationContent.setPrefHeight(150); // height, adjust as needed

	        // Empty for now
	        Label emptyLabel = new Label("No notifications");
	        emptyLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 14;");
	        notificationContent.getChildren().add(emptyLabel);

	        // Add content to popup
	        notificationPopup.getContent().add(notificationContent);
	        notificationPopup.setAutoHide(true); // hides automatically when clicking outside

	        // Show popup on hover
	        bellContainer.setOnMouseEntered(e -> showNotificationPopup());
	        bellContainer.setOnMouseExited(e -> hideNotificationPopup());

	        // Optional: toggle on click
	        bellContainer.setOnMouseClicked(e -> {
	            if (popupVisible) {
	                hideNotificationPopup();
	            } else {
	                showNotificationPopup();
	            }
	        });
	    }
	    private void showNotificationPopup() {
	        if (popupVisible) return;

	        Bounds bounds = bellContainer.localToScreen(bellContainer.getBoundsInLocal());
	        notificationPopup.show(bellContainer, bounds.getMinX(), bounds.getMaxY() + 5);

	        // Fade in
	        FadeTransition fade = new FadeTransition(Duration.millis(250), notificationContent);
	        notificationContent.setOpacity(0);
	        fade.setToValue(1);
	        fade.play();

	        popupVisible = true;
	    }

	    private void hideNotificationPopup() {
	        if (!popupVisible) return;

	        FadeTransition fade = new FadeTransition(Duration.millis(200), notificationContent);
	        fade.setToValue(0);
	        fade.setOnFinished(e -> notificationPopup.hide());
	        fade.play();

	        popupVisible = false;
	    }
	
	private void updateIndicators() {

	    for (int i = 0; i < indicators.length; i++) {

	        if (i == currentImageIndex) {
	            indicators[i].setFill(Color.WHITE);  // active
	            indicators[i].setWidth(60);          // active longer line
	        } else {
	            indicators[i].setFill(Color.GRAY);   // inactive
	            indicators[i].setWidth(40);
	        }
	    }
	}

	private void createIndicators() {

	    indicators = new Rectangle[carouselImages.length];

	    for (int i = 0; i < carouselImages.length; i++) {

	        Rectangle line = new Rectangle(40, 4); // width, height
	        line.setArcWidth(4);
	        line.setArcHeight(4);
	        line.setFill(Color.GRAY); // inactive color

	        indicators[i] = line;
	        carouselIndicators.getChildren().add(line);
	    }
	}

	

	private void startCarousel() {
	    Timeline timeline = new Timeline(
	            new KeyFrame(Duration.seconds(5), e -> updateCarouselBackground())
	    );
	    timeline.setCycleCount(Timeline.INDEFINITE);
	    timeline.play();
	}
	
	private void updateCarouselBackground() {
	    currentImageIndex = (currentImageIndex + 1) % carouselImages.length;
	    
	    // Background image
	    carouselBackground.setMaxHeight(700);
	    carouselBackground.setStyle(
	        "-fx-background-image: url('" + carouselImages[currentImageIndex] + "');" +
	        "-fx-background-size: cover;" +
	        "-fx-background-position: center center;" +
	        "-fx-background-radius: 20;"
	    );

	    // Create inner shadow (inside border effect)
	    InnerShadow innerShadow = new InnerShadow();
	    innerShadow.setRadius(60);        // how strong the fade is
	    innerShadow.setChoke(0.4);        // how thick the dark edge is
	    innerShadow.setColor(Color.BLACK);

	    carouselBackground.setEffect(innerShadow);

	    // Update texts
	    carouselTitle.setText(titles[currentImageIndex]);
	    carouselDescription.setText(descriptions[currentImageIndex]);
	    updateIndicators();
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
    public void animateTopCarousel(StackPane carouselContainer) {
        // Fade in
        FadeTransition fade = new FadeTransition(Duration.seconds(1.2), carouselContainer);
        fade.setFromValue(0);
        fade.setToValue(1);

        // Slide from top
        TranslateTransition slide = new TranslateTransition(Duration.seconds(1.2), carouselContainer);
        slide.setFromY(-50);  // start slightly above
        slide.setToY(0);

        // Play both together
        fade.play();
        slide.play();
    }
   

    private VBox createPosterBox(Image img) {
        ImageView poster = new ImageView(img);
        poster.setFitWidth(150);
        poster.setFitHeight(220);
        poster.setPreserveRatio(true);
        poster.setSmooth(true);
        poster.setCache(true);

        // Hover effect
        poster.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), poster);
            st.setToX(1.15);
            st.setToY(1.15);
            st.play();
            poster.setEffect(new DropShadow(20, Color.BLACK));
        });
        poster.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), poster);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
            poster.setEffect(null);
        });

        VBox box = new VBox(poster);
        box.setStyle("-fx-padding: 5; -fx-alignment: center;");
        return box;
    }

   
    public void setFeaturedMovie(String image, String title, String description) {

        featuredBackground.setStyle(
            "-fx-background-image: url('" + image + "');" +
            "-fx-background-size: cover;" +
            "-fx-background-position: center;"
        );

        featuredTitle.setText(title);
        featuredDescription.setText(description);
    }
    private void setRating(int stars){

        heroStars.getChildren().clear();

        for(int i=0;i<5;i++){

            Label star = new Label("★");

            if(i < stars)
                star.setStyle("-fx-text-fill:#00aaff; -fx-font-size:18;");
            else
                star.setStyle("-fx-text-fill:#555; -fx-font-size:18;");

            heroStars.getChildren().add(star);
        }
    }
    @FXML
    private MediaView heroTrailer;

    private MediaPlayer mediaPlayer;
    @FXML
    private ImageView heroBackground;

  

    @FXML
    private ImageView heroTitleImage;

    @FXML
    private HBox heroStars;

    @FXML
    private Label heroCategories;

    @FXML
    private Label heroDescription;

    @FXML
    private Button playButton;

    @FXML
    private Button infoButton;

    @FXML
    private Button trailerButton;

    @FXML
    private HBox heroIndicators;
    @FXML
    private void playTrailer(){

        Media media = new Media(getClass()
            .getResource("/assets/trailers/trailer.mp4")
            .toExternalForm());

        mediaPlayer = new MediaPlayer(media);

        heroTrailer.setMediaPlayer(mediaPlayer);

        heroBackground.setVisible(false);
        heroTrailer.setVisible(true);

        mediaPlayer.play();
    }
    private void changeSlide(Image image){

        FadeTransition fadeOut =
                new FadeTransition(Duration.millis(400), heroBackground);

        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {

            heroBackground.setImage(image);

            FadeTransition fadeIn =
                    new FadeTransition(Duration.millis(400), heroBackground);

            fadeIn.setToValue(1);
            fadeIn.play();
        });

        fadeOut.play();
    }
    
}
