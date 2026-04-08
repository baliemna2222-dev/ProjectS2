package JStream.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import JStream.entity.Episode;
import JStream.entity.Serie;
import JStream.entity.Season;
import JStream.service.EpisodeService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class EpisodesAdminController {

    @FXML private Label seasonInfoLabel;
    @FXML private TextField seasonIdField;
    @FXML private TextField numEpisodeField;
    @FXML private TextField titleField;
    @FXML private TextField durationField;
    @FXML private TextField ratingField;
    @FXML private TextArea resumeField;
    @FXML private TextField videoUrlField;
    @FXML private TextField covertUrlField;
    @FXML private DatePicker releasedAtPicker;
    @FXML private VBox episodeListContainer;

    private final EpisodeService episodeService = new EpisodeService();
    private Season currentSeason;
    private Serie currentSerie;

    @FXML
    private void handleAddEpisode() {
        if (seasonIdField.getText().isEmpty() || numEpisodeField.getText().isEmpty()) return;

        Episode episode = new Episode();
        episode.setTitle(titleField.getText());
        episode.setResume(resumeField.getText());
        episode.setVideoUrl(videoUrlField.getText());
        episode.setCovertUrl(covertUrlField.getText());

        try {
            episode.setSeasonId(Integer.parseInt(seasonIdField.getText()));
            episode.setNumEpisode(Integer.parseInt(numEpisodeField.getText()));
            if (!durationField.getText().isEmpty()) episode.setDuration(Integer.parseInt(durationField.getText()));
            if (!ratingField.getText().isEmpty()) episode.setRating(Integer.parseInt(ratingField.getText()));
        } catch (NumberFormatException e) {
            System.out.println("Erreur de format numérique.");
            return;
        }

        LocalDate localDate = releasedAtPicker.getValue();
        if (localDate != null) {
            episode.setReleasedAt(Timestamp.valueOf(localDate.atStartOfDay()));
        }

        episodeService.addEpisode(episode);
        clearFields();
        loadEpisodes();
    }

    public void initData(Season season, Serie serie) {
        this.currentSeason = season;
        this.currentSerie = serie;

        if (seasonInfoLabel != null) {
            seasonInfoLabel.setText("Saison " + season.getSeasonNum() + " de " + serie.getTitle());
        }

        if (seasonIdField != null) {
            seasonIdField.setText(String.valueOf(season.getSeasonId()));
            seasonIdField.setEditable(false);
        }

        loadEpisodes();
    }

    private void loadEpisodes() {
        if (episodeListContainer == null) return;
        episodeListContainer.getChildren().clear();

        List<Episode> episodes = episodeService.getEpisodesBySeason(currentSeason.getSeasonId());
        if (episodes == null || episodes.isEmpty()) {
            Label emptyLabel = new Label("Aucun épisode pour cette saison.");
            emptyLabel.setStyle("-fx-text-fill: gray;");
            episodeListContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Episode episode : episodes) {
            Label epLabel = new Label("Épisode " + episode.getNumEpisode() + " - " + episode.getTitle());
            epLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            Label epDetails = new Label(episode.getDuration() + " min • Note: " + episode.getRating());
            epDetails.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

            VBox epCard = new VBox(4, epLabel, epDetails);
            epCard.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 12; -fx-background-radius: 10;");
            episodeListContainer.getChildren().add(epCard);
        }
    }

    @FXML
    private void retourSaisons() {
        try {
            java.net.URL resourceUrl = getClass().getResource("/view/fxml/admin_seasons.fxml");
            if (resourceUrl == null) {
                System.err.println("ERREUR : admin_seasons.fxml introuvable.");
                return;
            }
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(resourceUrl);
            javafx.scene.Parent view = loader.load();
            SeasonsAdminController controller = loader.getController();
            controller.initData(currentSerie);
            replaceContentArea(view);
        } catch (Exception e) {
            System.out.println("Erreur lors du retour aux saisons : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void replaceContentArea(javafx.scene.Parent view) {
        javafx.scene.Scene scene = seasonInfoLabel.getScene();
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
        if (currentSeason == null) seasonIdField.clear();
        numEpisodeField.clear(); titleField.clear();
        durationField.clear(); ratingField.clear(); resumeField.clear();
        videoUrlField.clear(); covertUrlField.clear(); releasedAtPicker.setValue(null);
    }
}