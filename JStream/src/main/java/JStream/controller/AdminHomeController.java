package JStream.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class AdminHomeController {

    @FXML private StackPane contentArea;

    @FXML private Button dashboardBtn;
    @FXML private Button filmsBtn;
    @FXML private Button seriesBtn;
    @FXML private Button categoriesBtn;
    @FXML private Button usersBtn;
    @FXML private Button commentsBtn;
    @FXML private Button logoutBtn;

    @FXML
    public void initialize() {
        openDashboard();
    }

    @FXML
    private void openDashboard() {
        loadView("/view/fxml/admin_dashboard.fxml");
        activateButton(dashboardBtn);
    }

    @FXML
    private void openFilmsView() {
        loadView("/view/fxml/admin_films.fxml");
        activateButton(filmsBtn);
    }

    @FXML
    private void openSeriesView() {
        loadView("/view/fxml/admin_series.fxml");
        activateButton(seriesBtn);
    }

    @FXML
    private void openCategoriesView() {
        loadView("/view/fxml/admin_categories.fxml");
        activateButton(categoriesBtn);
    }

    @FXML
    private void openUsersView() {
        loadView("/view/fxml/admin_users.fxml");
        activateButton(usersBtn);
    }

    @FXML
    private void openCommentsView() {
        loadView("/view/fxml/admin_comments.fxml");
        activateButton(commentsBtn);
    }

    @FXML 
    private void logout() {
    	  try {
              FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/Login.fxml"));
              Parent root = loader.load();

              javafx.stage.Stage stage = (javafx.stage.Stage) logoutBtn.getScene().getWindow();
              
              /**Reuse existing scene instead of creating a new one*/
              stage.getScene().setRoot(root);
              // Ne pas forcer le mode maximisé - maintenir l'état du stage

              // ✅ Bind login root to stage size
              ((Region) root).prefWidthProperty().bind(stage.widthProperty());
              ((Region) root).prefHeightProperty().bind(stage.heightProperty());

          } catch (IOException e) {
              e.printStackTrace();
          }
    }

    private void activateButton(Button target) {
        Button[] navButtons = {dashboardBtn, filmsBtn, seriesBtn, categoriesBtn, usersBtn, commentsBtn};
        for (Button btn : navButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("nav-button-active");
            }
        }
        if (target != null && !target.getStyleClass().contains("nav-button-active")) {
            target.getStyleClass().add("nav-button-active");
        }
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("Error: Unable to load file " + fxmlPath);
            e.printStackTrace();
        }
    }
}