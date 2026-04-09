package JStream.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import JStream.entity.Episode;
import JStream.entity.Serie;
import JStream.entity.Season;
import JStream.service.EpisodeService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EpisodesAdminController {

    // ── FXML Fields ───────────────────────────────────────────────────────────
    @FXML private Label      seasonInfoLabel;
    @FXML private TextField  seasonIdField;
    @FXML private TextField  numEpisodeField;
    @FXML private TextField  titleField;
    @FXML private TextField  durationField;
    @FXML private TextField  ratingField;
    @FXML private TextArea   resumeField;
    @FXML private TextField  videoUrlField;
    @FXML private TextField  covertUrlField;
    @FXML private DatePicker releasedAtPicker;
    @FXML private VBox       episodeListContainer;
    @FXML private Label      formTitleLabel;
    @FXML private Button     submitBtn;
    @FXML private Button     cancelEditBtn;
    @FXML private Label      validationLabel;
    @FXML private VBox       formPanel;
    @FXML private Label      episodeCountLabel;

    // ── State ─────────────────────────────────────────────────────────────────
    private final EpisodeService episodeService = new EpisodeService();
    private Season  currentSeason;
    private Serie   currentSerie;
    private Episode editingEpisode = null;

    // ── Init ──────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        cancelEditBtn.managedProperty().bind(cancelEditBtn.visibleProperty());
        validationLabel.managedProperty().bind(validationLabel.visibleProperty());
        validationLabel.setVisible(false);
        cancelEditBtn.setVisible(false);

        numEpisodeField.textProperty().addListener((obs, o, n) -> clearValidation());
        titleField.textProperty().addListener((obs, o, n) -> clearValidation());
    }

    public void initData(Season season, Serie serie) {
        this.currentSeason = season;
        this.currentSerie  = serie;

        if (seasonInfoLabel != null)
            seasonInfoLabel.setText(serie.getTitle() + "  ›  Season " + season.getSeasonNum());
        if (seasonIdField != null) {
            seasonIdField.setText(String.valueOf(season.getSeasonId()));
            seasonIdField.setEditable(false);
        }

        loadEpisodes();
    }

    // ── Mode switching ────────────────────────────────────────────────────────
    private void setAddMode() {
        editingEpisode = null;
        formTitleLabel.setText("✦ Add Episode");
        submitBtn.setText("Add Episode");
        cancelEditBtn.setVisible(false);
        clearFields();
        clearValidation();
    }

    private void setEditMode(Episode ep) {
        editingEpisode = ep;
        formTitleLabel.setText("✎ Editing: Episode " + ep.getNumEpisode());
        submitBtn.setText("Save Changes");
        cancelEditBtn.setVisible(true);

        numEpisodeField.setText(String.valueOf(ep.getNumEpisode()));
        titleField.setText(nvl(ep.getTitle()));
        resumeField.setText(nvl(ep.getResume()));
        videoUrlField.setText(nvl(ep.getVideoUrl()));
        covertUrlField.setText(nvl(ep.getCovertUrl()));
        durationField.setText(ep.getDuration() > 0 ? String.valueOf(ep.getDuration()) : "");
        ratingField.setText(ep.getRating() > 0 ? String.valueOf(ep.getRating()) : "");
        if (ep.getReleasedAt() != null)
            releasedAtPicker.setValue(ep.getReleasedAt().toLocalDateTime().toLocalDate());
        else
            releasedAtPicker.setValue(null);
        clearValidation();
        animateFormHighlight();
    }

    // ── Submit ────────────────────────────────────────────────────────────────
    @FXML
    private void handleAddEpisode() {
        if (!validateForm()) return;
        if (editingEpisode == null) doAddEpisode();
        else                        doUpdateEpisode();
    }

    @FXML
    private void cancelEdit() { setAddMode(); }

    private void doAddEpisode() {
        clearValidation();
        Episode ep = buildEpisodeFromForm();
        episodeService.addEpisode(ep);
        clearFields();
        loadEpisodes();
        setAddMode();
        showToast("Episode added successfully ✓");
    }

    private void doUpdateEpisode() {
        clearValidation();
        populateEpisodeFromForm(editingEpisode);
        episodeService.updateEpisode(editingEpisode);
        loadEpisodes();
        setAddMode();
        showToast("Episode updated successfully ✓");
    }

    // ── Load episodes ─────────────────────────────────────────────────────────
    private void loadEpisodes() {
        if (episodeListContainer == null) return;
        episodeListContainer.getChildren().clear();

        List<Episode> episodes = episodeService.getEpisodesBySeason(currentSeason.getSeasonId());
        episodeCountLabel.setText(episodes.size() + " episode" + (episodes.size() != 1 ? "s" : ""));

        if (episodes.isEmpty()) {
            Label empty = new Label("No episodes yet. Add the first one →");
            empty.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 14px; -fx-padding: 40 0;");
            episodeListContainer.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < episodes.size(); i++) {
            VBox card = createEpisodeCard(episodes.get(i));
            card.setOpacity(0);
            episodeListContainer.getChildren().add(card);

            int delay = i * 50;
            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            FadeTransition fade   = new FadeTransition(Duration.millis(280), card);
            fade.setFromValue(0); fade.setToValue(1);
            TranslateTransition slide = new TranslateTransition(Duration.millis(280), card);
            slide.setFromX(-16); slide.setToX(0);
            pause.setOnFinished(e -> new ParallelTransition(fade, slide).play());
            pause.play();
        }
    }

    // ── Episode card ──────────────────────────────────────────────────────────
    private VBox createEpisodeCard(Episode ep) {
        // Number badge
        Label badge = new Label("E" + ep.getNumEpisode());
        badge.setStyle("""
            -fx-background-color: linear-gradient(to bottom right, #0ea5e9, #1d4ed8);
            -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;
            -fx-padding: 8 12; -fx-background-radius: 8; -fx-min-width: 42;
            """);

        Label lblTitle = new Label(nvl(ep.getTitle()).isEmpty() ? "Untitled Episode" : ep.getTitle());
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblMeta = new Label(
            (ep.getDuration() > 0 ? ep.getDuration() + " min" : "") +
            (ep.getRating() > 0 ? "  ·  ★ " + ep.getRating() + "/5" : "") +
            (ep.getReleasedAt() != null ? "  ·  " + ep.getReleasedAt().toLocalDateTime().toLocalDate() : "")
        );
        lblMeta.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");

        Label lblResume = new Label(nvl(ep.getResume()));
        lblResume.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11px; -fx-wrap-text: true;");
        lblResume.setMaxWidth(500);

        VBox info = new VBox(3, lblTitle, lblMeta, lblResume);
        info.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Actions
        Button editBtn = smallBtn("✎ Edit",  "#0ea5e9");
        Button delBtn  = smallBtn("✕",       "#374151");

        editBtn.setOnAction(e -> setEditMode(ep));
        delBtn.setOnAction(e  -> confirmDelete(ep));

        HBox actions = new HBox(8, editBtn, delBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(14, badge, info, actions);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(row);
        card.setStyle("""
            -fx-background-color: #080d1a;
            -fx-padding: 16 18;
            -fx-background-radius: 12;
            -fx-border-color: rgba(14,165,233,0.14);
            -fx-border-radius: 12;
            -fx-cursor: hand;
            """);

        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(14,165,233,0.28), 16, 0, 0, 3);"));
        card.setOnMouseExited(e  -> card.setStyle(card.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(14,165,233,0.28), 16, 0, 0, 3);", "")));

        return card;
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    @FXML
    private void retourSaisons() {
        try {
            java.net.URL resourceUrl = getClass().getResource("/view/fxml/admin_seasons.fxml");
            if (resourceUrl == null) { System.err.println("admin_seasons.fxml not found"); return; }
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(resourceUrl);
            javafx.scene.Parent view = loader.load();
            SeasonsAdminController ctrl = loader.getController();
            ctrl.initData(currentSerie);
            replaceContentArea(view);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void replaceContentArea(javafx.scene.Parent view) {
        javafx.scene.Scene scene = seasonInfoLabel.getScene();
        if (scene == null) return;
        javafx.scene.Parent root = scene.getRoot();
        javafx.scene.Node area = root.lookup("#contentArea");
        if (area instanceof Pane pane) pane.getChildren().setAll(view);
        else scene.setRoot(view);
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    private void confirmDelete(Episode ep) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Episode");
        alert.setHeaderText("Delete Episode " + ep.getNumEpisode() + "?");
        alert.setContentText("This cannot be undone.");
        alert.getDialogPane().setStyle("-fx-background-color: #080d1a;");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            episodeService.deleteEpisode(ep.getEpId());
            loadEpisodes();
            if (editingEpisode != null && editingEpisode.getEpId() == ep.getEpId())
                setAddMode();
        }
    }

    // ── Validation ────────────────────────────────────────────────────────────
    private boolean validateForm() {
        List<String> errors = new ArrayList<>();

        if (numEpisodeField.getText().isBlank()) errors.add("Episode number required");
        else {
            try { Integer.parseInt(numEpisodeField.getText()); }
            catch (NumberFormatException e) { errors.add("Episode number must be an integer"); }
        }
        if (titleField.getText().isBlank()) errors.add("Title required");
        if (!durationField.getText().isBlank()) {
            try { Integer.parseInt(durationField.getText()); }
            catch (NumberFormatException e) { errors.add("Duration must be an integer (minutes)"); }
        }
        if (!ratingField.getText().isBlank()) {
            try {
                int r = Integer.parseInt(ratingField.getText());
                if (r < 1 || r > 5) errors.add("Rating must be 1–5");
            } catch (NumberFormatException e) { errors.add("Rating must be a number"); }
        }
        if (videoUrlField.getText().isBlank()) errors.add("Video URL required");

        if (!errors.isEmpty()) {
            showValidation("⚠  " + String.join("  ·  ", errors));
            TranslateTransition shake = new TranslateTransition(Duration.millis(60), formPanel);
            shake.setFromX(-6); shake.setToX(6); shake.setCycleCount(4); shake.setAutoReverse(true);
            shake.play();
            return false;
        }
        return true;
    }

    private void showValidation(String msg) { validationLabel.setText(msg); validationLabel.setVisible(true); }
    private void clearValidation()          { validationLabel.setVisible(false); }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Episode buildEpisodeFromForm() {
        Episode ep = new Episode();
        ep.setSeasonId(currentSeason.getSeasonId());
        populateEpisodeFromForm(ep);
        return ep;
    }

    private void populateEpisodeFromForm(Episode ep) {
        ep.setTitle(titleField.getText().trim());
        ep.setResume(resumeField.getText().trim());
        ep.setVideoUrl(videoUrlField.getText().trim());
        ep.setCovertUrl(covertUrlField.getText().trim());
        try { ep.setNumEpisode(Integer.parseInt(numEpisodeField.getText())); }  catch (Exception ignored) {}
        try { ep.setDuration(Integer.parseInt(durationField.getText())); }       catch (Exception ignored) {}
        try { ep.setRating(Integer.parseInt(ratingField.getText())); }           catch (Exception ignored) {}
        LocalDate d = releasedAtPicker.getValue();
        if (d != null) ep.setReleasedAt(Timestamp.valueOf(d.atStartOfDay()));
    }

    private void clearFields() {
        if (currentSeason != null && seasonIdField != null)
            seasonIdField.setText(String.valueOf(currentSeason.getSeasonId()));
        numEpisodeField.clear(); titleField.clear(); resumeField.clear();
        durationField.clear(); ratingField.clear();
        videoUrlField.clear(); covertUrlField.clear();
        releasedAtPicker.setValue(null);
    }

    private Button smallBtn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5 14; -fx-background-radius: 16; -fx-cursor: hand;");
        return b;
    }

    private void animateFormHighlight() {
        FadeTransition ft = new FadeTransition(Duration.millis(200), formPanel);
        ft.setFromValue(0.4); ft.setToValue(1.0); ft.play();
    }

    private String nvl(String s) { return s == null ? "" : s; }

    private void showToast(String msg) {
        System.out.println("[JStream] " + msg);
    }
}