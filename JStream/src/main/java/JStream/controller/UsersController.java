package JStream.controller;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import JStream.entity.User;
import JStream.entity.UserRole;
import JStream.service.UserService;

public class UsersController {

    @FXML
    private VBox userListContainer;

    @FXML
    private TextField newUsernameField;

    @FXML
    private TextField newEmailField;

    @FXML
    private TextField newPasswordField;

    @FXML
    private ComboBox<String> newRoleComboBox;

    @FXML
    private Label addUserMessage;

    private UserService userService = new UserService();

    @FXML
    public void initialize() {
        setupForm();
        loadUsers();
    }

    private void loadUsers() {
        List<User> users = userService.getAllUsers();
        userListContainer.getChildren().clear();

        for (User user : users) {
            userListContainer.getChildren().add(createUserCard(user));
        }
    }

    private void setupForm() {
        if (newRoleComboBox != null) {
            newRoleComboBox.getItems().setAll("USER", "ADMIN");
            newRoleComboBox.getSelectionModel().select("USER");
        }
    }

    @FXML
    private void handleAddUser() {
        String username = newUsernameField.getText().trim();
        String email = newEmailField.getText().trim();
        String password = newPasswordField.getText();
        String roleValue = newRoleComboBox.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAddUserMessage("Remplissez tous les champs.", true);
            return;
        }

        UserRole role = "ADMIN".equalsIgnoreCase(roleValue) ? UserRole.ADMIN : UserRole.USER;
        boolean created = userService.createUser(username, email, password, role);

        if (created) {
            showAddUserMessage("Utilisateur ajouté avec succès.", false);
            clearAddUserForm();
            loadUsers();
        } else {
            showAddUserMessage("Erreur : username ou email déjà utilisé.", true);
        }
    }

    private void clearAddUserForm() {
        if (newUsernameField != null) {
            newUsernameField.clear();
        }
        if (newEmailField != null) {
            newEmailField.clear();
        }
        if (newPasswordField != null) {
            newPasswordField.clear();
        }
        if (newRoleComboBox != null) {
            newRoleComboBox.getSelectionModel().select("USER");
        }
    }

    private void showAddUserMessage(String message, boolean error) {
        if (addUserMessage != null) {
            addUserMessage.setText(message);
            addUserMessage.setStyle(error ? "-fx-text-fill: #f87171;" : "-fx-text-fill: #7dd3fc;");
        }
    }

    private HBox createUserCard(User user) {
        Label username = new Label(user.getUsername());
        username.getStyleClass().add("user-card-title");

        Label email = new Label(user.getEmail());
        email.getStyleClass().add("user-card-description");

        Label role = new Label("Rôle : " + (user.getRole() != null ? user.getRole().name() : "USER"));
        role.getStyleClass().add("user-card-meta");

        VBox userInfo = new VBox(username, email, role);
        userInfo.setSpacing(6);

        Button toggleRoleButton = new Button(user.getRole() == null || user.getRole().name().equalsIgnoreCase("USER") ? "Make Admin" : "Make User");
        toggleRoleButton.getStyleClass().addAll("user-card-button", "edit");
        toggleRoleButton.setOnAction(e -> toggleUserRole(user));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("user-card-button", "delete");
        deleteButton.setOnAction(e -> deleteUser(user));

        HBox actionBox = new HBox(toggleRoleButton, deleteButton);
        actionBox.setSpacing(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        HBox card = new HBox(userInfo, actionBox);
        card.getStyleClass().add("user-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(20);
        card.setPadding(new Insets(18));
        HBox.setHgrow(userInfo, Priority.ALWAYS);

        return card;
    }

    private void toggleUserRole(User user) {
        if (user.getRole() == null || user.getRole().name().equalsIgnoreCase("USER")) {
            user.setRole(JStream.entity.UserRole.ADMIN);
        } else {
            user.setRole(JStream.entity.UserRole.USER);
        }
        userService.updateUserRole(user);
        loadUsers();
    }

    private void deleteUser(User user) {
        userService.deleteUser(user.getId());
        loadUsers();
    }
}