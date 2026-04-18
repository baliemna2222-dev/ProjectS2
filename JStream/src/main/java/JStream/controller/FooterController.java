package JStream.controller;
 
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
 
import java.awt.Desktop;
import java.net.URI;
 
public class FooterController {
      @FXML private Label brandName;
 
    // Link labels
    @FXML private Label aboutUs, careers, press;
    @FXML private Label helpCenter, terms, privacy;
 
    @FXML private Button q1, q2, q3;
    @FXML private Label  a1, a2, a3;
    @FXML private Label  arrow1, arrow2, arrow3;
 
    @FXML private VBox faqCard1, faqCard2, faqCard3;
 
    // Social icons
    @FXML private ImageView fbIcon, twIcon, igIcon;
  
    @FXML
    public void initialize() {
        setupBrandPulse();
        setupLinkHovers();
        setupFAQ(q1, a1, a2, a3, arrow1, faqCard1);
        setupFAQ(q2, a2, a1, a3, arrow2, faqCard2);
        setupFAQ(q3, a3, a1, a2, arrow3, faqCard3);
        setupFaqCardHover(faqCard1);
        setupFaqCardHover(faqCard2);
        setupFaqCardHover(faqCard3);
        loadIcons();
        setupSocialClicks();
    }
 

    private void setupBrandPulse() {
        if (brandName == null) return;
        FadeTransition fade = new FadeTransition(Duration.seconds(3), brandName);
        fade.setFromValue(1.0);
        fade.setToValue(0.75);
        fade.setAutoReverse(true);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setInterpolator(Interpolator.EASE_BOTH);
        fade.play();
    }
  
    private void setupLinkHovers() {
        Label[] hoverLabels = {aboutUs, careers, press, helpCenter, terms, privacy};
        for (Label lbl : hoverLabels) {
            if (lbl == null) continue;
            lbl.setOnMouseEntered(e -> animateLabelHover(lbl, true));
            lbl.setOnMouseExited(e  -> animateLabelHover(lbl, false));
        }
    }
 
    private void animateLabelHover(Label lbl, boolean entering) {
        String colorOn  = "-fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-underline: true;";
        String colorOff = "-fx-text-fill: #8899bb;  -fx-font-size: 14px; -fx-cursor: hand;";
 
        ScaleTransition st = new ScaleTransition(Duration.millis(140), lbl);
        if (entering) {
            lbl.setStyle(colorOn);
            st.setToX(1.04);
            st.setToY(1.04);
        } else {
            lbl.setStyle(colorOff);
            st.setToX(1.0);
            st.setToY(1.0);
        }
        st.setInterpolator(Interpolator.EASE_OUT);
        st.play();
    }
 
 
    private void setupFAQ(Button question, Label ownAnswer,
                           Label otherAnswer1, Label otherAnswer2,
                           Label ownArrow, VBox ownCard) {
        if (question == null || ownAnswer == null) return;
 
        question.setOnAction(e -> {
            boolean isOpen = ownAnswer.isVisible();
 
            collapseAnswer(a1, arrow1, faqCard1);
            collapseAnswer(a2, arrow2, faqCard2);
            collapseAnswer(a3, arrow3, faqCard3);
 
            if (!isOpen) {
                expandAnswer(ownAnswer, ownArrow, ownCard);
            }
        });
         ownCard.setOnMouseClicked(e -> question.fire());
    }
 
    private void expandAnswer(Label answer, Label arrow, VBox card) {
        answer.setVisible(true);
        answer.setManaged(true);
        answer.setOpacity(0);
         FadeTransition ft = new FadeTransition(Duration.millis(260), answer);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setInterpolator(Interpolator.EASE_OUT);
        ft.play();
         if (arrow != null) {
            RotateTransition rt = new RotateTransition(Duration.millis(200), arrow);
            rt.setFromAngle(0);
            rt.setToAngle(90);
            rt.setInterpolator(Interpolator.EASE_OUT);
            rt.play();
        }
         if (card != null) {
            card.setStyle(card.getStyle()
                .replace("-fx-border-color: #1e3a8a;", "-fx-border-color: #2563eb;")
                + "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.35), 20, 0.3, 0, 0);");
        }
    }
 
    private void collapseAnswer(Label answer, Label arrow, VBox card) {
        if (answer == null) return;
        answer.setVisible(false);
        answer.setManaged(false);
 
        if (arrow != null) {
            RotateTransition rt = new RotateTransition(Duration.millis(180), arrow);
            rt.setToAngle(0);
            rt.setInterpolator(Interpolator.EASE_OUT);
            rt.play();
        }
 
        if (card != null) {
            card.setStyle(
                "-fx-background-color: #0c1428;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #1e3a8a;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;" +
                "-fx-cursor: hand;"
            );
        }
    }

    private void setupFaqCardHover(VBox card) {
        if (card == null) return;
 
        String base = "-fx-background-color: #0c1428;" +
                      "-fx-background-radius: 14;" +
                      "-fx-border-color: #1e3a8a;" +
                      "-fx-border-width: 1;" +
                      "-fx-border-radius: 14;" +
                      "-fx-cursor: hand;";
 
        String hovered = "-fx-background-color: #0e1830;" +
                         "-fx-background-radius: 14;" +
                         "-fx-border-color: #3b82f6;" +
                         "-fx-border-width: 1;" +
                         "-fx-border-radius: 14;" +
                         "-fx-cursor: hand;" +
                         "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.22), 18, 0.25, 0, 0);";
 
        card.setOnMouseEntered(e -> card.setStyle(hovered));
        card.setOnMouseExited(e  -> {
            Label answer = card == faqCard1 ? a1 : card == faqCard2 ? a2 : a3;
            if (answer == null || !answer.isVisible()) {
                card.setStyle(base);
            }
        });
    }
   private void loadIcons() {
        try {
            fbIcon.setImage(loadImage("/assets/images/facebook.png"));
            twIcon.setImage(loadImage("/assets/images/x.png"));
            igIcon.setImage(loadImage("/assets/images/instagram.png"));
        } catch (Exception e) {
            System.out.println("Error loading social icons: " + e.getMessage());
        }
    }
 
    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResource(path).toExternalForm());
        } catch (Exception e) {
            System.out.println("Missing image: " + path);
            return null;
        }
    }
 
    private void setupSocialClicks() {
        setupIconClick(fbIcon, "https://facebook.com");
        setupIconClick(twIcon, "https://twitter.com");
        setupIconClick(igIcon, "https://instagram.com");
    }
 
    private void setupIconClick(ImageView icon, String url) {
        if (icon == null) return;
 
        icon.setOnMouseClicked(e -> openLink(url));
         icon.setOnMouseEntered(e -> {
            icon.setOpacity(0.85);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), icon);
            st.setToX(1.15);
            st.setToY(1.15);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
 
        icon.setOnMouseExited(e -> {
            icon.setOpacity(1.0);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), icon);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
         icon.setOnMousePressed(e  -> icon.setOpacity(0.55));
        icon.setOnMouseReleased(e -> icon.setOpacity(0.85));
    }
 
    private void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}