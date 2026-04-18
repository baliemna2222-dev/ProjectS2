package JStream.controller;

import JStream.entity.Category;
import JStream.entity.FeaturedItem;
import JStream.service.FeaturedService;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.IOException;
import java.util.ArrayList;
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
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);
        leftBtn.prefHeightProperty().bind(viewport.heightProperty());
        rightBtn.prefHeightProperty().bind(viewport.heightProperty());
        leftBtn.setOpacity(0);
        rightBtn.setOpacity(0);
        carouselPane.setOnMouseEntered(e -> {leftBtn.setOpacity(1);    // Show or hide arrows on hover
                                             rightBtn.setOpacity(1);
        });
        carouselPane.setOnMouseExited(e -> { leftBtn.setOpacity(0);
                                             rightBtn.setOpacity(0);
        });
           
    }
//category carousel
    public void setCategory(Category category) {
        categoryTitle.setText(category.getName());
        List<FeaturedItem> items = featuredService.getItemsByCategory(category.getName());
        loadItems(items);
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
                JStream.controller.CardController controller = loader.getController();
                controller.setItem(item);

                card.setOnMouseEntered(e -> {card.setScaleX(1.2);
                                             card.setScaleY(1.2);
                });
                    
                card.setOnMouseExited(e -> { card.setScaleX(1);
                                               card.setScaleY(1);
                });
                   
                contentBox.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int totalCards = contentBox.getChildren().size();
        totalSlides = (int) Math.ceil((double) totalCards / CARDS_PER_SLIDE);
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

    private void updateArrowState() {
        leftBtn.setTextFill(currentSlideIndex == 0 ? Color.GRAY : Color.WHITE);
        rightBtn.setTextFill(currentSlideIndex >= totalSlides - 1 ? Color.GRAY : Color.WHITE);
    }
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
    private void updatePagination() {
        if (paginationBox == null) return;
        for (int i = 0; i < paginationBox.getChildren().size(); i++) {
            Rectangle rect = (Rectangle) paginationBox.getChildren().get(i);
            rect.setFill(i == currentSlideIndex ? Color.WHITE : Color.GRAY);
        }
    }
//top rated carousel
    private TranslateTransition loopTransition;
    private static final double TOP_CARD_WIDTH = 250;   
    private static final double TOP_CARD_HEIGHT = 150;  
    private static final double TOP_CARD_SPACING = 60;
    private static final double LOOP_DURATION = 40;

    void loadTopRatedInfinite(List<FeaturedItem> topItems) {
        categoryTitle.setText("🔥 Top Rated");
        contentBox.getChildren().clear();
        contentBox.setSpacing(TOP_CARD_SPACING);
        contentBox.setTranslateY(-10);  // Move carousel more to the top
        contentBox.setTranslateX(0);

        int rank = 1;
        for (FeaturedItem item : topItems) {
            contentBox.getChildren().add(createTopCard(item, rank++));
        }
        List<Node> original = new ArrayList<>(contentBox.getChildren());
        for (Node node : original) {  contentBox.getChildren().add(cloneCard((StackPane) node));}
        startInfiniteLoop();
    }

    private StackPane createTopCard(FeaturedItem item, int rank) {
        StackPane root = new StackPane();
        root.setPrefSize(TOP_CARD_WIDTH, TOP_CARD_HEIGHT-100);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        Text number = new Text(String.valueOf(rank)); 
        number.setFont(Font.font("Arial Black", 200)); // bold font
        number.setFill(Color.TRANSPARENT);             // transparent fill
        number.setStroke(Color.GRAY);                // white border
        number.setStrokeWidth(6);                     // thickness of border
        number.setTranslateX(-TOP_CARD_WIDTH / 3.5);
        
        number.setMouseTransparent(true);
        Node cardNode;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Card.fxml"));
            cardNode = loader.load();
            JStream.controller.CardController controller = loader.getController();
            controller.setItem2(item);
            cardNode.setUserData(item);
            if (cardNode instanceof Region region) {
                region.setPrefSize(TOP_CARD_WIDTH-80, TOP_CARD_HEIGHT+130);
                region.setMinSize(Region.USE_PREF_SIZE-80, Region.USE_PREF_SIZE+130);
                region.setMaxSize(Region.USE_PREF_SIZE-80, Region.USE_PREF_SIZE+130);
            }

            StackPane.setAlignment(cardNode, javafx.geometry.Pos.BOTTOM_RIGHT);
            StackPane.setMargin(cardNode, new javafx.geometry.Insets(0, 0, 0, 0));

        } catch (IOException e) {
            e.printStackTrace();
            return root;
        }
            cardNode.setOnMouseEntered(e -> { cardNode.setScaleX(1.05);
                                               cardNode.setScaleY(1.05);
                                              stopLoop();
        });
           
        cardNode.setOnMouseExited(e -> {
            cardNode.setScaleX(1.0);
            cardNode.setScaleY(1.0);
            resumeLoop();
        });

        root.getChildren().addAll(number, cardNode);
        number.toBack();

        return root;
    }
    private StackPane cloneCard(StackPane original) {
        StackPane clone = new StackPane();
        clone.setPrefSize(TOP_CARD_WIDTH, TOP_CARD_HEIGHT-100);
        clone.setStyle("-fx-background-color: transparent;");

        for (Node child : original.getChildren()) {
            if (child instanceof Text) {
                Text txt = (Text) child;
                Text copy = new Text(txt.getText());
                copy.setFont(txt.getFont());
                copy.setFill(txt.getFill());
                copy.setStroke(txt.getStroke());
                copy.setStrokeWidth(txt.getStrokeWidth());
                copy.setTranslateX(txt.getTranslateX());
                copy.setTranslateY(txt.getTranslateY());
                copy.setMouseTransparent(true);
                copy.toBack(); // ensure number stays behind card
                clone.getChildren().add(copy);
            } else {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Card.fxml"));
                    Node cardNode = loader.load();
                    JStream.controller.CardController controller = loader.getController();

                    if (child.getUserData() instanceof FeaturedItem) {
                        controller.setItem2((FeaturedItem) child.getUserData());
                        cardNode.setUserData(child.getUserData());
                    }
                    if (cardNode instanceof Region region) {
                    	  region.setPrefSize(TOP_CARD_WIDTH-80, TOP_CARD_HEIGHT+130);
                          region.setMinSize(Region.USE_PREF_SIZE-80, Region.USE_PREF_SIZE+130);
                          region.setMaxSize(Region.USE_PREF_SIZE-80, Region.USE_PREF_SIZE+130);    }
                    double scaleWidth = 1;
                    double scaleHeight = 1;
                    StackPane.setAlignment(cardNode, javafx.geometry.Pos.BOTTOM_RIGHT);
                    StackPane.setMargin(cardNode, new javafx.geometry.Insets(0, 0, 0, 0));
                    cardNode.setOnMouseEntered(e -> { cardNode.setScaleX(scaleWidth + 0.1);
                        cardNode.setScaleY(scaleHeight + 0.1);
                        stopLoop();
                    });
                       
                    cardNode.setOnMouseExited(e -> {  cardNode.setScaleX(scaleWidth);
                        cardNode.setScaleY(scaleHeight);
                        resumeLoop();
                    });
                      

                    clone.getChildren().add(cardNode);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return clone;
    }
    private void startInfiniteLoop() {
        double totalWidth = (contentBox.getChildren().size() / 2.0) * (TOP_CARD_WIDTH + TOP_CARD_SPACING);

        loopTransition = new TranslateTransition(Duration.seconds(LOOP_DURATION), contentBox);
        loopTransition.setFromX(0);
        loopTransition.setToX(-totalWidth);
        loopTransition.setInterpolator(javafx.animation.Interpolator.LINEAR);
        loopTransition.setCycleCount(Animation.INDEFINITE);
        loopTransition.play();
    }

    private void stopLoop() {
        if (loopTransition != null) loopTransition.pause();
    }

    private void resumeLoop() {
        if (loopTransition != null) loopTransition.play();
    }
}