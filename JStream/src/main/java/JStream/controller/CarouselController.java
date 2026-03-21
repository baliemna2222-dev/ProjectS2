package JStream.controller;

import JStream.entity.Category;
import JStream.entity.FeaturedItem;
import JStream.service.FeaturedService;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class CarouselController {

    @FXML private Label categoryTitle;
    @FXML private HBox contentBox;
    @FXML private Pane viewport;
    @FXML private Button leftBtn, rightBtn;
    @FXML private HBox paginationBox;
    @FXML private StackPane carouselPane;

    private final int CARDS_PER_SLIDE = 7;
    private double currentTranslateX = 0;
    private int totalSlides = 0;
    private int currentSlideIndex = 0;

    private final FeaturedService featuredService = new FeaturedService();

    @FXML
    public void initialize() {
        // Clip viewport so content doesn’t overflow
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);

        // Bind arrows to height
        leftBtn.prefHeightProperty().bind(viewport.heightProperty());
        rightBtn.prefHeightProperty().bind(viewport.heightProperty());

        // Initially hide arrows
        leftBtn.setOpacity(0);
        rightBtn.setOpacity(0);

        // Show/hide arrows on hover
        carouselPane.setOnMouseEntered(e -> {
            leftBtn.setOpacity(1);
            rightBtn.setOpacity(1);
        });
        carouselPane.setOnMouseExited(e -> {
            leftBtn.setOpacity(0);
            rightBtn.setOpacity(0);
        });
    }

    /** Load carousel items for a given category */
    public void setCategory(Category category) {
        categoryTitle.setText(category.getName());
        contentBox.getChildren().clear();

        List<FeaturedItem> items = featuredService.getItemsByCategory(category.getName());
        loadItems(items);

        // Reset carousel
        currentSlideIndex = 0;
        moveToSlide(0);
        setupPagination();
        updateArrowState();
    }
    public void setCategoryTitle(String title) {
        categoryTitle.setText(title);
    }
    public void loadItems(List<FeaturedItem> items) {
        contentBox.getChildren().clear();

        for (FeaturedItem item : items) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Card.fxml"));
                Node card = loader.load();
                CardController controller = loader.getController();
                controller.setItem(item);

                card.setOnMouseEntered(e -> {
                    card.setScaleX(1.2);
                    card.setScaleY(1.2);
                });
                card.setOnMouseExited(e -> {
                    card.setScaleX(1);
                    card.setScaleY(1);
                });

                contentBox.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int totalCards = contentBox.getChildren().size();
        totalSlides = (int) Math.ceil((double) totalCards / CARDS_PER_SLIDE);

        // Reset carousel
        currentSlideIndex = 0;
        moveToSlide(0);
        setupPagination();
        updateArrowState();
    }
    @FXML
    private void scrollRight() {
        if (currentSlideIndex < totalSlides - 1) {
            currentSlideIndex++;
            moveToSlide(currentSlideIndex);
        }
    }

    @FXML
    private void scrollLeft() {
        if (currentSlideIndex > 0) {
            currentSlideIndex--;
            moveToSlide(currentSlideIndex);
        }
    }

    /** Move the carousel to a specific slide */
    private void moveToSlide(int slideIndex) {
        if (contentBox.getChildren().isEmpty()) return;

        Node firstCard = contentBox.getChildren().get(0);
        double cardWidth = firstCard.prefWidth(-1);
        double spacing = contentBox.getSpacing();
        double slideWidth = CARDS_PER_SLIDE * (cardWidth + spacing);

        double maxScroll = Math.max(0, contentBox.getWidth() - viewport.getWidth());

        currentTranslateX = -slideIndex * slideWidth;
        if (-currentTranslateX > maxScroll) currentTranslateX = -maxScroll;
        if (currentTranslateX > 0) currentTranslateX = 0;

        animateScroll();
        updatePagination();
        updateArrowState();
    }

    private void animateScroll() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), contentBox);
        tt.setToX(currentTranslateX);
        tt.play();
    }

    /** Update arrow colors based on position */
    private void updateArrowState() {
        leftBtn.setTextFill(currentSlideIndex == 0 ? Color.GRAY : Color.WHITE);
        rightBtn.setTextFill(currentSlideIndex >= totalSlides - 1 ? Color.GRAY : Color.WHITE);
    }

    /** Create pagination dots */
    private void setupPagination() {
        if (paginationBox == null) return;
        paginationBox.getChildren().clear();

        for (int i = 0; i < totalSlides; i++) {
            Rectangle rect = new Rectangle(15, 5);
            rect.setArcWidth(2);
            rect.setArcHeight(2);
            rect.setFill(i == currentSlideIndex ? Color.WHITE : Color.GRAY);
            paginationBox.getChildren().add(rect);
        }
    }

    /** Update pagination dots on slide change */
    private void updatePagination() {
        if (paginationBox == null) return;
        for (int i = 0; i < paginationBox.getChildren().size(); i++) {
            Rectangle rect = (Rectangle) paginationBox.getChildren().get(i);
            rect.setFill(i == currentSlideIndex ? Color.WHITE : Color.GRAY);
        }
    }
}