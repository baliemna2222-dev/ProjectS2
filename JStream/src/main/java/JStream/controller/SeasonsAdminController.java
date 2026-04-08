package JStream.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import JStream.entity.Serie;
import JStream.entity.Season;
import JStream.service.SeasonService;

import java.util.List;

public class SeasonsAdminController {

    @FXML private Label titreSerieLabel;
    @FXML private VBox seasonsListContainer;

    @FXML private TextField seasonNumField;
    @FXML private TextField plannedEpisodesField;
    @FXML private TextField ratingField;
    @FXML private TextField titleField;
    @FXML private TextArea synopsisField;
    @FXML private TextField trailerUrlField;
    @FXML private TextField posterUrlField;
    @FXML private TextField titleUrlField;
    @FXML private TextField imageUrlField;
    @FXML private ComboBox<String> statusComboBox;

    private final SeasonService seasonService = new SeasonService();
    private Serie serieActuelle;

    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll("Ongoing", "Completed", "Cancelled", "Upcoming");
    }

    // Méthode appelée depuis SeriesAdminController pour passer l'objet Serie
    public void initData(Serie serie) {
        this.serieActuelle = serie;
        
        if (titreSerieLabel != null) {
            titreSerieLabel.setText("Saisons de : " + serie.getTitle());
        }
        
        chargerSaisons();
    }

    private void chargerSaisons() {
        if (seasonsListContainer != null) {
            seasonsListContainer.getChildren().clear();
        }
        
        List<Season> seasons = seasonService.getSeasonsBySerie(serieActuelle.getSerieId());
        
        for (Season season : seasons) {
            String titreAffiche = "Saison " + season.getSeasonNum();
            if (season.getTitle() != null && !season.getTitle().isEmpty()) {
                titreAffiche += " - " + season.getTitle();
            }

            Label lblSeason = new Label(titreAffiche);
            lblSeason.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            
            Label lblDetails = new Label(season.getPlannedEpisodes() + " épisodes prévus | Statut : " + season.getStatus());
            lblDetails.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
            
            Button episodesButton = new Button("Voir les épisodes");
            episodesButton.setStyle("-fx-background-color: #0ef; -fx-text-fill: black; -fx-background-radius: 10; -fx-cursor: hand;");
            episodesButton.setOnAction(e -> ouvrirDetailsSaison(season));

            HBox actions = new HBox(8, episodesButton);
            actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

            VBox card = new VBox(10, lblSeason, lblDetails, actions);
            card.setStyle("-fx-background-color: #2c3e50; -fx-padding: 18; -fx-background-radius: 10;");

            seasonsListContainer.getChildren().add(card);
        }
    }

    @FXML
    private void handleAddSeason() {
        if (serieActuelle == null || seasonNumField.getText().isEmpty()) {
            System.out.println("Veuillez remplir le numéro de saison.");
            return;
        }

        Season season = new Season();
        season.setSerieId(serieActuelle.getSerieId());
        season.setTitle(titleField.getText());
        season.setSynopsis(synopsisField.getText());
        season.setTrailerUrl(trailerUrlField.getText());
        season.setPosterUrl(posterUrlField.getText());
        season.setTitleUrl(titleUrlField.getText());
        season.setImageUrl(imageUrlField.getText());
        season.setStatus(statusComboBox.getValue());

        try {
            season.setSeasonNum(Integer.parseInt(seasonNumField.getText()));
            if (!plannedEpisodesField.getText().isEmpty()) {
                season.setPlannedEpisodes(Integer.parseInt(plannedEpisodesField.getText()));
            }
            if (!ratingField.getText().isEmpty()) {
                season.setRating(Integer.parseInt(ratingField.getText()));
            }
        } catch (NumberFormatException e) {
            System.out.println("Erreur de format numérique.");
            return;
        }

        seasonService.addSeason(season);
        clearFields();
        chargerSaisons(); // On met à jour la vue de droite immédiatement
    }

    @FXML
    private void retourSeries() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/fxml/admin_series.fxml"));
            javafx.scene.Parent view = loader.load();
            replaceContentArea(view);
        } catch (Exception e) {
            System.out.println("Erreur lors du retour aux séries.");
            e.printStackTrace();
        }
    }

    private void ouvrirDetailsSaison(Season season) {
        try {
            java.net.URL resourceUrl = getClass().getResource("/view/fxml/admin_episodes.fxml");
            if (resourceUrl == null) {
                System.err.println("ERREUR CRITIQUE : Fichier admin_episodes.fxml introuvable.");
                return;
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(resourceUrl);
            javafx.scene.Parent view = loader.load();
            EpisodesAdminController controller = loader.getController();
            controller.initData(season, serieActuelle);

            replaceContentArea(view);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de la page des épisodes : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void replaceContentArea(javafx.scene.Parent view) {
        javafx.scene.Scene scene = titreSerieLabel.getScene();
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

    private void clearFields() {
        seasonNumField.clear(); 
        plannedEpisodesField.clear(); 
        ratingField.clear();
        titleField.clear(); 
        synopsisField.clear(); 
        trailerUrlField.clear();
        posterUrlField.clear(); 
        titleUrlField.clear(); 
        imageUrlField.clear();
        statusComboBox.getSelectionModel().clearSelection();
    }
}