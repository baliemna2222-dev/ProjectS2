package JStream.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import JStream.entity.Session;
import JStream.entity.User;
import JStream.service.UserService;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Logincontroller implements Initializable {

    private static final int WIDTH = 1640;
    private static final int HEIGHT = 800;
    private static final int ELEMENTS = 30;

    @FXML private Pane dotsPane;
    @FXML private VBox loginForm, signupForm, forgotForm;

    // Login
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private Button loginBtn;
    @FXML private Label loginError;

    // Signup
    @FXML private TextField signupUsername;
    @FXML private TextField signupEmail;
    @FXML private PasswordField signupPassword;
    @FXML private CheckBox signupAgreeTerms;
    @FXML private Button signupBtn;
    @FXML private Label signupError;

    // Forgot Password
    @FXML private TextField verifywithEmail;
    @FXML private TextField verificationCode;
    @FXML private Button sendCodeBtn;
    @FXML private Button verifyBtn;
    @FXML private Label messageLabel;
    @FXML private ImageView logoImage;
    // Hyperlinks
    @FXML private Hyperlink goToSignUp, goToLogin, forgotPassword;
 // Add this field at the top of LoginController
    private String pendingEmail = null;
    private final UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Floating rectangles animation
    	  logoImage.setImage(new Image(getClass().getResource("/assets/images/logo/Raksha.png").toExternalForm()));
    
        Random random = new Random();
        for (int i = 0; i < ELEMENTS; i++) {
            Rectangle rect = new Rectangle(10,10);
            rect.setArcWidth(10); rect.setArcHeight(10);
            DropShadow glow = new DropShadow(30, Color.web("#0159e4"));
            glow.setSpread(0.6);
            rect.setEffect(glow);
            rect.setFill(Color.web("#01133A"));
            rect.setX(random.nextInt(WIDTH)); rect.setY(HEIGHT + random.nextInt(200));
            TranslateTransition t = new TranslateTransition(Duration.seconds(6 + random.nextInt(15)), rect);
            t.setFromY(0); t.setToY(-HEIGHT-300); t.setCycleCount(TranslateTransition.INDEFINITE);
            t.setDelay(Duration.seconds(random.nextInt(8))); t.play();
            dotsPane.getChildren().add(rect);
        }

        // Form switches
        goToSignUp.setOnAction(e -> showSignup());
        goToLogin.setOnAction(e -> showLogin());
        forgotPassword.setOnAction(e -> showForgot());

        // Disable signup button until checkbox checked
        signupBtn.setDisable(true);
        signupAgreeTerms.selectedProperty().addListener((obs, oldV, newV) -> signupBtn.setDisable(!newV));

        // Initially hide forgot password fields
        verificationCode.setVisible(false);
        verifyBtn.setVisible(false);
    }
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Raksha.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            RakshaController controller = loader.getController();

            stage.getScene().setRoot(root);
            stage.setMaximized(true);

            // ✅ Force layout pass THEN bind — without this Raksha doesn't know the real size
            Platform.runLater(() -> {
                root.applyCss();
                root.layout();
                controller.initLayoutBindings(stage);

                // ✅ Second pass to catch any remaining layout issues
                Platform.runLater(() -> {
                    root.applyCss();
                    root.layout();
                    controller.initLayoutBindings(stage);
                });
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========== LOGIN ==========
    @FXML private void handleLogin(ActionEvent event) {
        clearLoginMessages();
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();

        System.out.println("🔐 Trying login: username=" + username + " password=" + password);

        if (username.isEmpty() || password.isEmpty()) {
            loginError.setText("Please fill all fields");
            loginError.setVisible(true);
            return;
        }

        User user = userService.login(username, password);
        System.out.println("👤 User returned: " + (user != null ? user.getId() + " / " + user.getUsername() : "NULL"));

        if (user != null) {
            Session.login(user.getId(), user.getUsername());
            goToHomepage(event);
        } else {
            loginError.setText("Invalid username or password");
            loginError.setVisible(true);
        }
    }

    // ========== SIGNUP ==========
    @FXML private void handleSignup(ActionEvent event) {
        clearSignupMessages();

        String username = signupUsername.getText().trim();
        String email = signupEmail.getText().trim();
        String password = signupPassword.getText();

        if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            signupError.setText("Please fill all fields"); 
            signupError.setVisible(true);
            return;
        }

        if(!isValidEmail(email)) {
            signupError.setText("Enter a valid email address"); 
            signupError.setVisible(true);
            return;
        }

        boolean success = userService.register(username, email, password);
        
        if(success) {
        	User user = userService.login(username, password);
        	 Session.login(user.getId(), user.getUsername());
        	 goToHomepage(event);
            
        } else {
            // Provide clear feedback on what failed
            if(userService.usernameExists(username)) {
                signupError.setText("Username already exists");
            } else if(userService.emailExists(email)) {
                signupError.setText("Email already exists");
            } else {
                signupError.setText("Registration failed");
            }
            signupError.setVisible(true);
        }
    }

    private boolean isValidEmail(String email) {
        // Basic email regex
        String regex = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$";
        return Pattern.matches(regex, email);
    }

    // ========== FORGOT PASSWORD ==========
    @FXML private void handleForgotPassword() { showForgot(); }

    @FXML private void handleSendCode() {
        clearForgotMessages();
        String email = verifywithEmail.getText().trim();
        if (email.isEmpty()) { showError("Enter your email"); return; }

        sendCodeBtn.setDisable(true);
        sendCodeBtn.setText("Sending...");

        Task<Boolean> sendTask = new Task<>() {
            @Override
            protected Boolean call() {
                return userService.sendVerificationCode(email);
            }
        };

        sendTask.setOnSucceeded(e -> {
            boolean sent = sendTask.getValue();
            sendCodeBtn.setDisable(false);
            sendCodeBtn.setText("Send Code");

            if (!sent) {
                showError("Email not found or failed to send");
                return;
            }

            // ✅ Save email before hiding the field
            pendingEmail = email;

            showSuccess("✅ Code sent to " + email);
            verifywithEmail.setVisible(false);
            sendCodeBtn.setVisible(false);
            verificationCode.setVisible(true);
            verifyBtn.setVisible(true);
        });

        sendTask.setOnFailed(e -> {
            sendCodeBtn.setDisable(false);
            sendCodeBtn.setText("Send Code");
            showError("Failed to send email. Check your connection.");
        });

        new Thread(sendTask).start();
    }
    @FXML private void handleVerifyCode(ActionEvent event) {
        clearForgotMessages();
        String code = verificationCode.getText().trim();
        if (code.isEmpty()) { showError("Enter verification code"); return; }

        if (userService.verifyCode(code)) {

            if (pendingEmail == null) {
                showError("Session expired, please try again");
                showForgot();
                return;
            }

            // ✅ Fetch user by email and log them in
            User user = userService.getUserByEmail(pendingEmail);

            if (user != null) {
                Session.login(user.getId(), user.getUsername());
                goToHomepage(event);
            } else {
                showError("User not found");
            }

        } else {
            showError("Invalid or expired code");
        }
    }

    @FXML private void backToLogin() { showLogin(); }

    // ========== MESSAGES ==========
    private void clearLoginMessages() { loginError.setVisible(false); messageLabel.setVisible(false);}
    private void clearSignupMessages() { signupError.setVisible(false); messageLabel.setVisible(false);}
    private void clearForgotMessages() { messageLabel.setVisible(false); }

    private void showError(String msg) { messageLabel.setText(msg); messageLabel.setStyle("-fx-text-fill: #ff4d4d;"); messageLabel.setVisible(true);}
    private void showSuccess(String msg) { messageLabel.setText(msg); messageLabel.setStyle("-fx-text-fill: #4dff4d;"); messageLabel.setVisible(true);}

    // ========== CLEAR FIELDS ==========
    private void clearLoginFields() { 
        loginUsername.clear(); 
        loginPassword.clear(); 
        loginError.setVisible(false);
        loginBtn.setDisable(false);
    }

    private void clearSignupFields() { 
        signupUsername.clear(); 
        signupEmail.clear(); 
        signupPassword.clear(); 
        signupAgreeTerms.setSelected(false); 
        signupBtn.setDisable(true);
        signupError.setVisible(false);
    }

    private void clearForgotFields() { 
        verifywithEmail.clear(); 
        verificationCode.clear(); 
        verificationCode.setVisible(false); 
        verifyBtn.setVisible(false); 
        sendCodeBtn.setVisible(true); 
        verifywithEmail.setVisible(true); 
        messageLabel.setVisible(false);
    }

    // ========== FORM SWITCH ==========
    private void showLogin() {
        loginForm.setVisible(true); 
        signupForm.setVisible(false); 
        forgotForm.setVisible(false);
        clearLoginFields(); clearSignupFields(); clearForgotFields();
    }

    private void showSignup() {
        loginForm.setVisible(false); 
        signupForm.setVisible(true); 
        forgotForm.setVisible(false);
        clearLoginFields(); clearSignupFields(); clearForgotFields();
    }

    private void showForgot() {
        loginForm.setVisible(false); 
        signupForm.setVisible(false); 
        forgotForm.setVisible(true);
        clearLoginFields(); clearSignupFields(); clearForgotFields();
    }

    // ========== HOME PAGE ==========
    @FXML
    private void goToHomepage(ActionEvent event) {
        // Get the stage and current scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        Parent currentRoot = scene.getRoot();

        // Show a spinner overlay while loading
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(100, 100);

        if (currentRoot instanceof Pane pane) {
            pane.getChildren().add(spinner);
            StackPane.setAlignment(spinner, Pos.CENTER);
        }

        // Load FXML in background thread
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                return FXMLLoader.load(getClass().getResource("/view/fxml/HomePage.fxml"));
            }
        };

        loadTask.setOnSucceeded(e -> {
            Parent root = loadTask.getValue();

            // Swap root
            stage.getScene().setRoot(root);

            // Fade in
            FadeTransition ft = new FadeTransition(Duration.millis(500), root);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        });

        new Thread(loadTask).start();
    }
}
