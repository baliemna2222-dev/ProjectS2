package JStream.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.Desktop;
import java.net.URI;

public class FooterController {

    // ================= LINKS =================
    @FXML private Label aboutUs, careers, press;
    @FXML private Label helpCenter, terms, privacy;

    // ================= FAQ =================
    @FXML private Button q1, q2, q3;
    @FXML private Label a1, a2, a3;

    // ================= SOCIAL ICONS =================
    @FXML private ImageView fbIcon, twIcon, igIcon;

    // ================= INIT =================
    @FXML
    public void initialize() {

        // ===== Hover Effects for Links =====
        

        // ===== FAQ Toggle Logic =====
        setupFAQ(q1, a1);
        setupFAQ(q2, a2);
        setupFAQ(q3, a3);

        // ===== Load Social Icons =====
        loadIcons();

        // ===== Social Icon Clicks =====
        setupSocialClicks();
    }

    // ================= FAQ LOGIC =================
    private void setupFAQ(Button question, Label answer) {
        if (question == null || answer == null) return;

        // Initially hidden
        answer.setVisible(false);
        answer.setManaged(false);

        question.setOnAction(e -> {
            boolean visible = answer.isVisible();
            answer.setVisible(!visible);
            answer.setManaged(!visible);
        });
    }

    // ================= HOVER EFFECT =================
  
    // ================= LOAD IMAGES =================
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

    // ================= SOCIAL ICON CLICKS =================
    private void setupSocialClicks() {
        setupIconClick(fbIcon, "https://facebook.com");
        setupIconClick(twIcon, "https://twitter.com");
        setupIconClick(igIcon, "https://instagram.com");
    }

    private void setupIconClick(ImageView icon, String url) {
        if (icon == null) return;

        icon.setOnMouseClicked(e -> openLink(url));
        icon.setOnMouseEntered(e -> icon.setOpacity(0.7));
        icon.setOnMouseExited(e -> icon.setOpacity(1.0));
    }

    // ================= OPEN LINK =================
    private void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}