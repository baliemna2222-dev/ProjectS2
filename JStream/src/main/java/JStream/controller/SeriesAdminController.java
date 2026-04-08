package JStream.controller;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import JStream.entity.Serie;
import JStream.entity.Category;
import JStream.service.SerieService;
import JStream.service.FeaturedService;

public class SeriesAdminController {

    @FXML private TextField titleField;
    @FXML private TextField ageRatingField;
    @FXML private TextArea synopsisField;
    @FXML private TextField castingField;
    @FXML private TextField coverField;
    @FXML private TextField titleUrlField;
    
    @FXML private ListView<Category> categoryListView; 
    @FXML private TilePane seriesContainer;

    private SerieService serieService;
    private FeaturedService featuredService;

    public SeriesAdminController() {
        this.serieService = new SerieService();
        try {
            this.featuredService = new FeaturedService();
        } catch (Exception e) {
            System.out.println("Erreur init FeaturedService: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        categoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        categoryListView.setCellFactory(list -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        loadCategories();
        loadSeries();
    }

    private void loadCategories() {
        if (featuredService != null) {
            categoryListView.getItems().setAll(featuredService.getAllCategories()); 
        }
    }

    private void loadSeries() {
        seriesContainer.getChildren().clear();
        List<Serie> series = serieService.getAllSeries();

        for (Serie serie : series) {
            VBox card = createSerieCard(serie);
            seriesContainer.getChildren().add(card);
        }
    }

    private VBox createSerieCard(Serie serie) {
        ImageView imageView = new ImageView();
        try {
            String imageUrl = serie.getCovertUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Image img = new Image(getClass().getResource(imageUrl).toExternalForm());
                imageView.setImage(img);
            }
        } catch (Exception e) {
            System.out.println("Image introuvable pour : " + serie.getTitle());
        }

        imageView.setFitWidth(150);
        imageView.setFitHeight(200);

        Label title = new Label(serie.getTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label categoriesLabel = new Label(serie.getCategoriesAsString());
        categoriesLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 10px;");

        VBox card = new VBox(5, imageView, title, categoriesLabel);
        // Ajout du curseur pour indiquer que c'est cliquable
        card.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 10; -fx-background-radius: 10; -fx-cursor: hand;");

        // Événement au clic
        card.setOnMouseClicked(event -> {
            System.out.println("Série sélectionnée : " + serie.getTitle());
            ouvrirDetailsSerie(serie);
        });

        return card;
    }

    private void ouvrirDetailsSerie(Serie serie) {
        try {
            String fxmlPath = "/view/fxml/admin_seasons.fxml";
            java.net.URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                System.err.println("ERREUR CRITIQUE : Le fichier FXML est introuvable au chemin : " + fxmlPath);
                return;
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(resourceUrl);
            javafx.scene.Parent view = loader.load();

            SeasonsAdminController seasonsController = loader.getController();
            seasonsController.initData(serie);

            replaceContentArea(view);

        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de la page des saisons : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void replaceContentArea(javafx.scene.Parent view) {
        javafx.scene.Scene scene = titleField.getScene();
        if (scene == null) {
            return;
        }
        javafx.scene.Parent root = scene.getRoot();
        javafx.scene.Node contentArea = root.lookup("#contentArea");
        if (contentArea instanceof javafx.scene.layout.Pane) {
            javafx.scene.layout.Pane pane = (javafx.scene.layout.Pane) contentArea;
            pane.getChildren().setAll(view);
        } else {
            scene.setRoot(view);
        }
    }
    @FXML
    private void addSerie() {
        Serie serie = new Serie();
        
        serie.setTitle(titleField.getText());
        serie.setSynopsis(synopsisField.getText());
        serie.setCasting(castingField.getText());
        serie.setAge_rating(ageRatingField.getText());
        serie.setCovertUrl(coverField.getText());
        serie.setTitleUrl(titleUrlField.getText());

        ObservableList<Category> selectedCategories = categoryListView.getSelectionModel().getSelectedItems();
        serie.setCategories(new ArrayList<>(selectedCategories));

        serieService.addSerie(serie);
        
        loadSeries();
        clearFields();
    }

    private void clearFields() {
        titleField.clear();
        ageRatingField.clear();
        synopsisField.clear();
        castingField.clear();
        coverField.clear();
        titleUrlField.clear();
        categoryListView.getSelectionModel().clearSelection();
    }
}