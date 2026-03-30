package JStream.controller;

import JStream.entity.FeaturedItem;
import JStream.entity.FeaturedItemProgress;
import JStream.entity.Session;
import JStream.entity.WatchStatus;
import JStream.service.FeaturedService;
import JStream.service.HistoryService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.List;

public class HistoryController {

    @FXML
    private HBox historyContainer;
    public void initialize() {
        loadHistory();
    }

    private void loadHistory() {
        try {
            HistoryService h = new HistoryService();
            int userId = Session.getUserId();

            List<FeaturedItemProgress> list = h.getItemsWithProgress(userId);

            historyContainer.getChildren().clear();

            for (FeaturedItemProgress progress : list) {

                // ❗ Only show watched items
                if (progress.getStatus() == WatchStatus.NOT_STARTED)
                    continue;

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/view/fxml/Card.fxml")
                );

                StackPane card = loader.load();

                // Set data to controller
                CardController controller = loader.getController();
                controller.setData(progress);

                // 〽️ Add hover effect
                card.setOnMouseEntered(event -> {
                    card.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5);"
                            + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
                });

                card.setOnMouseExited(event -> {
                    card.setStyle(""); // reset style
                });

                historyContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}