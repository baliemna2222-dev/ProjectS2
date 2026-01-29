package JStream.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class Logincontroller implements Initializable {

    private static final int WIDTH = 1640;
    private static final int HEIGHT = 800;
    private static final int ELEMENTS = 30;

    @FXML
    private Pane dotsPane;

    @FXML
    private VBox loginForm;

    @FXML
    private VBox signupForm;

    @FXML
    private Hyperlink goToSignUp;

    @FXML
    private Hyperlink goToLogin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Random random = new Random();

        // --- Animated background dots ---
        for (int i = 0; i < ELEMENTS; i++) {
            Rectangle rect = new Rectangle(10, 10);
            rect.setArcWidth(10);
            rect.setArcHeight(10);

            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#0159e4"));
            glow.setRadius(30);
            glow.setSpread(0.6);
            rect.setEffect(glow);
            rect.setFill(Color.web("#01133A"));

            rect.setX(random.nextInt(WIDTH));
            rect.setY(HEIGHT + random.nextInt(200));

            TranslateTransition transition = new TranslateTransition();
            transition.setNode(rect);
            transition.setFromY(0);
            transition.setToY(-HEIGHT - 300);
            transition.setDuration(Duration.seconds(6 + random.nextInt(15)));
            transition.setDelay(Duration.seconds(random.nextInt(8)));
            transition.setCycleCount(TranslateTransition.INDEFINITE);
            transition.play();

            dotsPane.getChildren().add(rect);
        }

        // --- Switch between login/signup ---
        goToSignUp.setOnAction(e -> {
            loginForm.setVisible(false);
            signupForm.setVisible(true);
        });

        goToLogin.setOnAction(e -> {
            signupForm.setVisible(false);
            loginForm.setVisible(true);
        });
    }
}
