package JStream.controller;

import java.io.File;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.effect.InnerShadow;

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

	private Rectangle[] indicators;

	private final String[] titles = {"Movie 1", "Movie 2", "Movie 3"};
	private final String[] descriptions = {"Description 1", "Description 2", "Description 3"};
	 @FXML
	    private Button btnMovies, btnSeries, btnMyList , btnHome;

	    @FXML
	    private Circle dotMovies, dotSeries, dotMyList ,dotHome;
	    @FXML
	    private ImageView bellIcon;

	    @FXML
	    private StackPane bellContainer;

	    private Circle notificationDot;
	    private AudioClip bellSound;
	    private boolean isNotificationVisible = false; 
	    
	    @FXML
	    private HBox actionCarousel;
	    @FXML
	    private ScrollPane scrollAction;
	    @FXML
	    private Button btnLeftAction, btnRightAction;

	@FXML
	public void initialize() {
	    logoImage.setImage(new Image(getClass().getResourceAsStream("/assets/images/logo.png")));
	    createIndicators();
	    updateCarouselBackground();
	    startCarousel();
	    btnHome.setOnAction(e -> selectButton(btnHome, dotHome));
	    btnMovies.setOnAction(e -> selectButton(btnMovies, dotMovies));
        btnSeries.setOnAction(e -> selectButton(btnSeries, dotSeries));
        btnMyList.setOnAction(e -> selectButton(btnMyList, dotMyList));
        selectButton(btnHome, dotHome);
        bellIcon.setImage(new Image(getClass().getResourceAsStream("/assets/images/bellwhiter.png")));

        // Load sound
        bellSound = new AudioClip(getClass().getResource("/assets/sounds/notification.mp3").toString());

        // Create red notification dot
        notificationDot = new Circle(5, Color.BLUE);
        notificationDot.setTranslateX(13); // position top-right of bell
        notificationDot.setTranslateY(-13);
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
        
        loadCategory(actionCarousel, "/assets/images/films");

	}
	public void loadCategory(HBox carousel, String folderPath) {
	  
	    try {
	        File folder = new File(getClass().getResource(folderPath).toURI());
	        File[] files = folder.listFiles((dir, name) -> 
	            name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
	        );

	        if (files == null) return;

	        for (File f : files) {
	            Image img = new Image(f.toURI().toString());
	            ImageView movie = new ImageView(img);

	            movie.setFitWidth(150);
	            movie.setFitHeight(220);
	            movie.setPreserveRatio(true);
	            movie.setSmooth(true);
	            movie.setCache(true);

	            // Hover effect
	            movie.setOnMouseEntered(e -> {
	                movie.toFront();
	                ScaleTransition st = new ScaleTransition(Duration.millis(200), movie);
	                st.setToX(1.2);
	                st.setToY(1.2);
	                st.play();
	                movie.setTranslateY(-20);
	                movie.setEffect(new DropShadow(20, Color.BLACK));
	            });

	            movie.setOnMouseExited(e -> {
	                ScaleTransition st = new ScaleTransition(Duration.millis(200), movie);
	                st.setToX(1);
	                st.setToY(1);
	                st.play();
	                movie.setTranslateY(0);
	                movie.setEffect(null);
	            });

	            carousel.getChildren().add(movie);
	        }

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
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

	private void selectButton(Button selectedButton, Circle selectedDot) {
	    // Reset all dots
		dotHome.setFill(javafx.scene.paint.Color.TRANSPARENT);
	    dotMovies.setFill(javafx.scene.paint.Color.TRANSPARENT);
	    dotSeries.setFill(javafx.scene.paint.Color.TRANSPARENT);
	    dotMyList.setFill(javafx.scene.paint.Color.TRANSPARENT);

	    // Reset button colors
	    btnHome.setStyle("-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-size: 16;");
	    btnMovies.setStyle("-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-size: 16;");
	    btnSeries.setStyle("-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-size: 16;");
	    btnMyList.setStyle("-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-size: 16;");

	    // Highlight selected
	    selectedButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
	    selectedDot.setFill(javafx.scene.paint.Color.DODGERBLUE); // small circle dot
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
}
