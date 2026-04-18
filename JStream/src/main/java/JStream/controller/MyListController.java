package JStream.controller;

import JStream.entity.FeaturedItem;
import JStream.entity.MyListManager;
import JStream.entity.Session;
import JStream.entity.UsernameChangeNotifier;
import JStream.service.MylistService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MyListController implements Initializable {

    @FXML private TilePane filmContainer;     
    @FXML private TilePane serieContainer;    
    @FXML private Label usernameLabel;        
    @FXML private ScrollPane mainScroll;
    private MylistService mylistService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	 mylistService = new MylistService();
    	  UsernameChangeNotifier.addListener(newName -> usernameLabel.setText(newName));
    	    usernameLabel.setText("Welcome , " + Session.getUsername() + " to your WatchList !");
    	    
    	    loadMyList();

    	    MyListManager.getInstance().addListener((filmId, serieId) -> {
    	        Platform.runLater(() -> removeCardFromUI(filmId, serieId));
    	    });
    }

    private void loadMyList() {
        int userId = Session.getUserId();
        List<FeaturedItem> items = mylistService.getUserList(userId);

        filmContainer.getChildren().clear();
        serieContainer.getChildren().clear();

        for (FeaturedItem item : items) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Card.fxml"));
                Node cardNode = loader.load();

                CardController controller = loader.getController();
                controller.setItem_mylist(item);

                cardNode.setUserData(item);

                cardNode.setOnMouseEntered(this::handleCardHoverEnter);
                cardNode.setOnMouseExited(this::handleCardHoverExit);

                if ("film".equalsIgnoreCase(item.getType())) {
                    filmContainer.getChildren().add(cardNode);
                } else {
                    serieContainer.getChildren().add(cardNode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (filmContainer.getChildren().isEmpty()) {
            Label empty = new Label("You haven’t added any films yet. Start exploring and add your favorites to watch later!");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            StackPane wrapper = new StackPane(empty);
            wrapper.setPrefHeight(150);
            wrapper.setAlignment(Pos.CENTER);
            filmContainer.getChildren().add(wrapper);
        }
        if (serieContainer.getChildren().isEmpty()) {
            Label empty = new Label("You haven’t added any series yet. Start exploring and add your favorites to watch later!");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            StackPane wrapper = new StackPane(empty);
            wrapper.setPrefHeight(150);
            wrapper.setAlignment(Pos.CENTER);
            serieContainer.getChildren().add(wrapper);
        }
        Platform.runLater(() -> mainScroll.setVvalue(0));
  
        // Show message if no items
        if (filmContainer.getChildren().isEmpty()) {
        	 Label empty = new Label("You haven’t added any films yet. Start exploring and add your favorites to watch later!");
             empty.setStyle("-fx-text-fill: white; -fx-font-size: 18px; ");
             StackPane wrapper = new StackPane(empty);
             wrapper.setPrefHeight(150);
             wrapper.setAlignment(Pos.CENTER);
            filmContainer.getChildren().add(wrapper);
        }
        if (serieContainer.getChildren().isEmpty()) {
        	 Label empty = new Label("You haven’t added any Series yet. Start exploring and add your favorites to watch later!");
             empty.setStyle("-fx-text-fill: white; -fx-font-size: 18px; ");
             StackPane wrapper = new StackPane(empty);
             wrapper.setPrefHeight(150);
             wrapper.setAlignment(Pos.CENTER);
            serieContainer.getChildren().add(wrapper);
        }
    }
    
    private void removeCardFromUI(int filmId, int serieId) {
        filmContainer.getChildren().removeIf(node -> {
            FeaturedItem item = (FeaturedItem) node.getUserData();
            return item != null && item.getId() == filmId && "film".equalsIgnoreCase(item.getType());
        });
        serieContainer.getChildren().removeIf(node -> {
            FeaturedItem item = (FeaturedItem) node.getUserData();
            return item != null && item.getSerieId() == serieId && "serie".equalsIgnoreCase(item.getType());
        });
        if (filmContainer.getChildren().isEmpty()) {
            Label empty = new Label("You haven’t added any films yet. Start exploring and add your favorites to watch later!");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            StackPane wrapper = new StackPane(empty);
            wrapper.setPrefHeight(150);
            wrapper.setAlignment(Pos.CENTER);
            filmContainer.getChildren().add(wrapper);
        }

        if (serieContainer.getChildren().isEmpty()) {
            Label empty = new Label("You haven’t added any Series yet. Start exploring and add your favorites to watch later!");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            StackPane wrapper = new StackPane(empty);
            wrapper.setPrefHeight(150);
            wrapper.setAlignment(Pos.CENTER);
            serieContainer.getChildren().add(wrapper);
        }
        Platform.runLater(() -> mainScroll.setVvalue(0));
    }
    @FXML
    private void handleCardHoverEnter(MouseEvent e) {
        Node card = (Node) e.getSource();
        card.setScaleX(1.1);
        card.setScaleY(1.1);
        card.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0);");
    }

    @FXML
    private void handleCardHoverExit(MouseEvent e) {
        Node card = (Node) e.getSource();
        card.setScaleX(1);
        card.setScaleY(1);
        card.setStyle("-fx-effect: none;");
    }
}