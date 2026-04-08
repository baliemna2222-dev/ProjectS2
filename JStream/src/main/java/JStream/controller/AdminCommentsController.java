package JStream.controller;

import JStream.service.CommentService;
import JStream.entity.Comment;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.List;

public class AdminCommentsController {

    @FXML
    private VBox commentsContainer;

    private final CommentService commentService = new CommentService();

    @FXML
    private void initialize() {
        //loadComments();
    }

  /*  private void loadComments() {
        List<Comment> comments = commentService.getSignaledComments();
        commentsContainer.getChildren().clear();

        if (comments.isEmpty()) {
            Label noComments = new Label("🎉 No reported comments!");
            noComments.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 30;");
            
            VBox emptyContainer = new VBox(noComments);
            emptyContainer.setAlignment(Pos.CENTER);
            commentsContainer.getChildren().add(emptyContainer);
            
        } else {
            for (Comment c : comments) {
                VBox commentBox = new VBox();
                
                // Application du style "Carte" du Dashboard
                String cardStyle = "-fx-background-color: linear-gradient(to bottom right, #2b323c, #1e2329); " +
                                   "-fx-padding: 20; " +
                                   "-fx-spacing: 12; " +
                                   "-fx-background-radius: 15; " +
                                   "-fx-border-color: #ffffff1a; " +
                                   "-fx-border-radius: 15; " +
                                   "-fx-border-width: 1;";
                commentBox.setStyle(cardStyle);

                // Adding drop shadow effect in Java
                DropShadow dropShadow = new DropShadow();
                dropShadow.setRadius(10);
                dropShadow.setOffsetY(6);
                dropShadow.setOffsetX(0);
                dropShadow.setColor(Color.color(0, 0, 0, 0.5)); // #00000080
                commentBox.setEffect(dropShadow);
                
                // Info: User and Target (Film or Series)
                String target = c.getFilmID() > 0 ? "Film ID: " + c.getFilmID() : "Series ID: " + c.getSerieID();
                Label infoLabel = new Label("Author ID: " + c.getUserID() + "  •  About: " + target);
                // Using pink/red from dashboard to remind this is a report
                infoLabel.setStyle("-fx-text-fill: #ff416c; -fx-font-weight: bold; -fx-font-size: 14px;");

                // Comment content
                Label contentLabel = new Label("« " + c.getContent() + " »");
                contentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-style: italic; -fx-padding: 5 0 10 0;");
                contentLabel.setWrapText(true);

                // Action buttons
                HBox actionBox = new HBox(15);
                
                Button deleteBtn = new Button("🗑️ Delete");
                // Style amélioré pour les boutons
                deleteBtn.setStyle("-fx-background-color: #ff416c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15 8 15; -fx-background-radius: 8; -fx-cursor: hand;");
                deleteBtn.setOnAction(e -> {
                    commentService.deleteComment(c.getComment_id());
                    loadComments(); 
                });

                Button ignoreBtn = new Button("✅ Ignore");
                ignoreBtn.setStyle("-fx-background-color: #2b323c; -fx-text-fill: #00d2ff; -fx-font-weight: bold; -fx-border-color: #00d2ff; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-width: 1.5; -fx-padding: 7 15 7 15; -fx-cursor: hand;");
                ignoreBtn.setOnAction(e -> {
                    commentService.pardonComment(c.getComment_id());
                    loadComments(); 
                });

                actionBox.getChildren().addAll(deleteBtn, ignoreBtn);
                commentBox.getChildren().addAll(infoLabel, contentLabel, actionBox);
                commentsContainer.getChildren().add(commentBox);
            }
        }
    }*/
}