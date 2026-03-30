package JStream.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class LoginSignupController {

    @FXML private AnchorPane slidingOverlay;
    @FXML private Text overlayTitle;
    
    private boolean isLoginView = true;

    @FXML
    private void toggleForm() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.7), slidingOverlay);
        
        if (isLoginView) {
            // Slide to the Left (Showing Register)
            transition.setToX(-400); 
            overlayTitle.setText("WELCOME BACK!");
        } else {
            // Slide to the Right (Showing Login)
            transition.setToX(0);
            overlayTitle.setText("WELCOME!");
        }
        
        transition.play();
        isLoginView = !isLoginView;
    }

}