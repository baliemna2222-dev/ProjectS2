package JStream.controller;

import JStream.service.CommentService;
import JStream.entity.Comment;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.util.List;

public class AdminCommentsController {

    @FXML private VBox commentsContainer;
    @FXML private Label countLabel;

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
    //carte principal
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("admin-comment-card");

        DropShadow shadow = new DropShadow();
        shadow.setRadius(20);
        shadow.setOffsetY(8);
        shadow.setColor(Color.color(0, 0, 0, 0.55));
        card.setEffect(shadow);
//header
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane avatarPane = new StackPane();
        Circle avatarBg = new Circle(24);
        avatarBg.setFill(Color.web("#1e88e5"));
      
        VBox metaCol = new VBox(3);
    
     Label userLabel = new Label(
         c.getUsername() != null && !c.getUsername().isBlank()
             ? c.getUsername()
             : "User #" + c.getUserID() 
     );

     String uname = c.getUsername();
     String avatarText = (uname != null && uname.length() >= 2)
         ? uname.substring(0, 2).toUpperCase()
         : "U" + (c.getUserID() % 100);
     new Label(avatarText);

        String targetText = c.isForFilm()
            ? "🎬  Film ID: " + c.getFilmID()
            : "📺  Episode ID: " + c.getEpID();
        Label targetLabel = new Label(targetText);
        targetLabel.setStyle(
            "-fx-text-fill: rgba(100,181,246,0.75);" +
            "-fx-font-size: 12px;"
        );
        metaCol.getChildren().addAll(userLabel, targetLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label flagBadge = new Label("🚨  Flagged");
        flagBadge.setStyle(
            "-fx-background-color: rgba(66,165,245,0.16);" +
            "-fx-text-fill: #cfe8ff;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 5 12 5 12;" +
            "-fx-background-radius: 999;"
        );

        header.getChildren().addAll(avatarPane, metaCol, spacer, flagBadge);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(66,165,245,0.15); -fx-border-color: transparent;");
        VBox.setMargin(sep, new Insets(0, 0, 2, 0));

        Label contentLabel = new Label("\" " + c.getContent() + " \"");
        contentLabel.getStyleClass().add("admin-comment-content");
        contentLabel.setWrapText(true);

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button ignoreBtn = new Button("✓  Ignore Report");
        String ignoreBase =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #42a5f5;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 9 20 9 20;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: rgba(66,165,245,0.5);" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.5;" +
            "-fx-cursor: hand;";
        String ignoreHover =
            "-fx-background-color: rgba(66,165,245,0.12);" +
            "-fx-text-fill: #64b5f6;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 9 20 9 20;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: rgba(66,165,245,0.85);" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.5;" +
            "-fx-cursor: hand;";
        ignoreBtn.setStyle(ignoreBase);
        ignoreBtn.setOnMouseEntered(e -> ignoreBtn.setStyle(ignoreHover));
        ignoreBtn.setOnMouseExited(e  -> ignoreBtn.setStyle(ignoreBase));
        ignoreBtn.setOnAction(e -> {
            commentService.pardonComment(c.getComment_id());
            removeCardWithAnimation(card);
        });

        Button deleteBtn = new Button("🗑  Delete Comment");
        String deleteBase =
            "-fx-background-color: linear-gradient(to right, #e53935, #c62828);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 9 20 9 20;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;";
        String deleteHover =
            "-fx-background-color: linear-gradient(to right, #ef5350, #d32f2f);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 9 20 9 20;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;";
        deleteBtn.setStyle(deleteBase);
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(deleteHover));
        deleteBtn.setOnMouseExited(e  -> deleteBtn.setStyle(deleteBase));
        deleteBtn.setOnAction(e -> {
            commentService.deleteComment(c.getComment_id());
            removeCardWithAnimation(card);
        });

        actions.getChildren().addAll(ignoreBtn, deleteBtn);
        card.getChildren().addAll(header, sep, contentLabel, actions);
//animation 
        card.setOpacity(0);
        card.setTranslateY(20);
        FadeTransition ft = new FadeTransition(Duration.millis(350), card);
        ft.setToValue(1);
        ft.setDelay(Duration.millis(animDelayMs));
        TranslateTransition tt = new TranslateTransition(Duration.millis(350), card);
        tt.setToY(0);
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
}