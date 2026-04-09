package JStream.controller;

import JStream.service.CommentService;
import JStream.entity.Comment;
 
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.util.List;

public class AdminCommentsController {

    @FXML
    private VBox commentsContainer;

    @FXML
    private Label countLabel;

    private final CommentService commentService = new CommentService();

    @FXML
    private void initialize() {
        loadComments();
    }

    private void loadComments() {
        List<Comment> comments = commentService.getSignaledComments();
        commentsContainer.getChildren().clear();

        if (countLabel != null) {
            countLabel.setText(comments.size() + " reported comment" + (comments.size() != 1 ? "s" : ""));
        }

        if (comments.isEmpty()) {
            showEmptyState();
        } else {
            int[] delay = {0};
            for (Comment c : comments) {
                VBox card = buildCommentCard(c, delay[0]++ * 60);
                commentsContainer.getChildren().add(card);
            }
        }
    }

    private void showEmptyState() {
        VBox emptyBox = new VBox(16);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPadding(new Insets(60));

        Label icon = new Label("✅");
        icon.setStyle("-fx-font-size: 48px;");

        Label title = new Label("All clear!");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label sub = new Label("No reported comments to review.");
        sub.setStyle("-fx-text-fill: #8899aa; -fx-font-size: 14px;");

        emptyBox.getChildren().addAll(icon, title, sub);
        commentsContainer.getChildren().add(emptyBox);
    }

    private VBox buildCommentCard(Comment c, int animDelayMs) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #1c2333, #161d2b);" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #ff416c44;" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setRadius(20);
        shadow.setOffsetY(8);
        shadow.setColor(Color.color(0, 0, 0, 0.55));
        card.setEffect(shadow);

        // ── Header row: avatar + meta ──────────────────────────────
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);

        // Avatar circle with initials
        StackPane avatarPane = new StackPane();
        Circle avatarBg = new Circle(24);
        avatarBg.setFill(Color.web("#ff416c"));
        Label initials = new Label(getInitials(c.getUserID()));
        initials.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        avatarPane.getChildren().addAll(avatarBg, initials);

        // Meta column
        VBox metaCol = new VBox(3);
        Label userLabel = new Label("User #" + c.getUserID());
        userLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        String targetText = c.isForFilm()
            ? "🎬 Film ID: " + c.getFilmID()
            : "📺 Episode ID: " + c.getEpID();
        Label targetLabel = new Label(targetText);
        targetLabel.setStyle("-fx-text-fill: #8899aa; -fx-font-size: 12px;");

        metaCol.getChildren().addAll(userLabel, targetLabel);

        // Flag badge
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label flagBadge = new Label("🚨 Flagged");
        flagBadge.setStyle(
            "-fx-background-color: #ff416c22;" +
            "-fx-text-fill: #ff416c;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 4 10 4 10;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #ff416c55;" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1;"
        );

        header.getChildren().addAll(avatarPane, metaCol, spacer, flagBadge);

        // ── Divider ────────────────────────────────────────────────
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #ffffff11;");

        // ── Comment content ────────────────────────────────────────
        Label contentLabel = new Label("\" " + c.getContent() + " \"");
        contentLabel.setStyle(
            "-fx-text-fill: #ccd6e0;" +
            "-fx-font-size: 15px;" +
            "-fx-font-style: italic;" +
            "-fx-line-spacing: 4;"
        );
        contentLabel.setWrapText(true);

        // ── Action buttons ─────────────────────────────────────────
        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button deleteBtn = new Button("🗑  Delete Comment");
        deleteBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #ff416c, #ff4b2b);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 9 20 9 20;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #ff2244, #ff3311);" +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;" +
            "-fx-padding: 9 20 9 20; -fx-background-radius: 10; -fx-cursor: hand;"
        ));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #ff416c, #ff4b2b);" +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;" +
            "-fx-padding: 9 20 9 20; -fx-background-radius: 10; -fx-cursor: hand;"
        ));
        deleteBtn.setOnAction(e -> {
            commentService.deleteComment(c.getComment_id());
            removeCardWithAnimation(card);
        });

        Button ignoreBtn = new Button("✓  Ignore Report");
        ignoreBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #00d2ff;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 8 20 8 20;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #00d2ff66;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.5;" +
            "-fx-cursor: hand;"
        );
        ignoreBtn.setOnMouseEntered(e -> ignoreBtn.setStyle(
            "-fx-background-color: #00d2ff18;" +
            "-fx-text-fill: #00d2ff; -fx-font-weight: bold; -fx-font-size: 13px;" +
            "-fx-padding: 8 20 8 20; -fx-background-radius: 10;" +
            "-fx-border-color: #00d2ff; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-cursor: hand;"
        ));
        ignoreBtn.setOnMouseExited(e -> ignoreBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #00d2ff; -fx-font-weight: bold; -fx-font-size: 13px;" +
            "-fx-padding: 8 20 8 20; -fx-background-radius: 10;" +
            "-fx-border-color: #00d2ff66; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-cursor: hand;"
        ));
        ignoreBtn.setOnAction(e -> {
            commentService.pardonComment(c.getComment_id());
            removeCardWithAnimation(card);
        });

        actions.getChildren().addAll(ignoreBtn, deleteBtn);
        card.getChildren().addAll(header, sep, contentLabel, actions);

        // Entrance animation
        card.setOpacity(0);
        card.setTranslateY(20);
        FadeTransition ft = new FadeTransition(Duration.millis(350), card);
        ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(350), card);
        tt.setToY(0);
        ft.setDelay(Duration.millis(animDelayMs));
        tt.setDelay(Duration.millis(animDelayMs));
        ft.play();
        tt.play();

        return card;
    }

    private void removeCardWithAnimation(VBox card) {
        FadeTransition ft = new FadeTransition(Duration.millis(280), card);
        ft.setToValue(0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(280), card);
        tt.setToY(-15);
        ft.setOnFinished(e -> {
            commentsContainer.getChildren().remove(card);
            updateCountLabel();
            if (commentsContainer.getChildren().isEmpty()) showEmptyState();
        });
        ft.play();
        tt.play();
    }

    private void updateCountLabel() {
        if (countLabel != null) {
            int remaining = commentsContainer.getChildren().size();
            countLabel.setText(remaining + " reported comment" + (remaining != 1 ? "s" : ""));
        }
    }

    private String getInitials(int userId) {
        // Placeholder — replace with real username lookup
        return "U" + (userId % 100);
    }
}