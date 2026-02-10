package JStream.controller;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import JStream.entity.User;
import JStream.service.UserService;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
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

    // Hyperlinks
    @FXML private Hyperlink goToSignUp, goToLogin, forgotPassword;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Floating rectangles animation
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

    // ========== LOGIN ==========
    @FXML private void handleLogin() {
        clearLoginMessages();
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();

        if(username.isEmpty() || password.isEmpty()) {
            loginError.setText("Please fill all fields"); 
            loginError.setVisible(true);
            return;
        }

        User user = userService.login(username, password);
        if(user != null) {
            goToHomepage();
        } else {
            loginError.setText("Invalid username or password");
            loginError.setVisible(true);
        }
    }

    // ========== SIGNUP ==========
    @FXML private void handleSignup() {
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
            goToHomepage();
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
        if(email.isEmpty()) { showError("Enter your email"); return; }

        boolean sent = userService.sendVerificationCode(email);
        if(!sent) { showError("Email not found"); return; }

        showSuccess("Verification code sent to your email");

        verifywithEmail.setVisible(false);
        sendCodeBtn.setVisible(false);
        verificationCode.setVisible(true);
        verifyBtn.setVisible(true);
    }

    @FXML private void handleVerifyCode() {
        clearForgotMessages();
        String code = verificationCode.getText().trim();
        if(code.isEmpty()) { showError("Enter verification code"); return; }

        if(userService.verifyCode(code)) goToHomepage();
        else showError("Invalid or expired code");
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
    @FXML private void goToHomepage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/homepage.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) loginForm.getScene().getWindow();
            stage.setScene(scene); 
            stage.centerOnScreen();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
