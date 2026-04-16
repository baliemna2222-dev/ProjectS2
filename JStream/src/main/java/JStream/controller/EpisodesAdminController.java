package JStream.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import JStream.entity.Episode;
import JStream.entity.Serie;
import JStream.entity.Season;
import JStream.service.EpisodeService;

import java.io.File;
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
        titleField.textProperty().addListener((obs, o, n)      -> clearValidation());
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
        releasedAtPicker.setValue(
            ep.getReleasedAt() != null ? ep.getReleasedAt().toLocalDateTime().toLocalDate() : null
        );

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
        Episode ep = buildEpisodeFromForm();
        episodeService.addEpisode(ep);
        clearFields();
        loadEpisodes();
        setAddMode();
        showToast("Episode added successfully ✓");
    }

    private void doUpdateEpisode() {
        populateEpisodeFromForm(editingEpisode);
        episodeService.updateEpisode(editingEpisode);
        loadEpisodes();
        setAddMode();
        showToast("Episode updated successfully ✓");
    }

    // ── File choosers ─────────────────────────────────────────────────────────
    @FXML private void chooseVideoFile()  { chooseFile(videoUrlField,  "Choose Video File",
            new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mkv", "*.avi", "*.mov", "*.webm", "*.flv")); }
    @FXML private void chooseCoverFile()  { chooseFile(covertUrlField, "Choose Cover Image",
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp", "*.bmp")); }

    private void chooseFile(TextField target, String title, FileChooser.ExtensionFilter filter) {
        if (target == null || target.getScene() == null) return;
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().add(filter);
        File f = fc.showOpenDialog(target.getScene().getWindow());
        if (f != null) target.setText(f.getAbsolutePath());
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
        Label badge = new Label("E" + ep.getNumEpisode());
        badge.setStyle("""
            -fx-background-color: linear-gradient(to bottom right, #0ea5e9, #1d4ed8);
            -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;
            -fx-padding: 8 12; -fx-background-radius: 8; -fx-min-width: 42;
            """);

        Label lblTitle = new Label(nvl(ep.getTitle()).isEmpty() ? "Untitled Episode" : ep.getTitle());
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        // Build meta line
        StringBuilder meta = new StringBuilder();
        if (ep.getDuration() > 0)      meta.append(ep.getDuration()).append(" min");
        if (ep.getRating() > 0)        meta.append(meta.isEmpty() ? "" : "  ·  ").append("★ ").append(ep.getRating()).append("/5");
        if (ep.getReleasedAt() != null) meta.append(meta.isEmpty() ? "" : "  ·  ").append(ep.getReleasedAt().toLocalDateTime().toLocalDate());

        Label lblMeta = new Label(meta.toString());
        lblMeta.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");

        Label lblResume = new Label(nvl(ep.getResume()));
        lblResume.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11px; -fx-wrap-text: true;");
        lblResume.setMaxWidth(500);
        lblResume.setVisible(!nvl(ep.getResume()).isEmpty());
        lblResume.setManaged(!nvl(ep.getResume()).isEmpty());

        VBox info = new VBox(3, lblTitle, lblMeta, lblResume);
        info.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button editBtn = smallBtn("✎ Edit", "#0ea5e9");
        Button delBtn  = smallBtn("✕",      "#374151");

        editBtn.setOnAction(e -> setEditMode(ep));
        delBtn.setOnAction(e  -> confirmDelete(ep));

        HBox actions = new HBox(8, editBtn, delBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(14, badge, info, actions);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(row);
        final String baseStyle = """
            -fx-background-color: #080d1a;
            -fx-padding: 16 18;
            -fx-background-radius: 12;
            -fx-border-color: rgba(14,165,233,0.14);
            -fx-border-radius: 12;
            -fx-cursor: hand;
            """;
        card.setStyle(baseStyle);
        card.setOnMouseEntered(e -> card.setStyle(baseStyle + "-fx-effect: dropshadow(gaussian, rgba(14,165,233,0.28), 16, 0, 0, 3);"));
        card.setOnMouseExited(e  -> card.setStyle(baseStyle));

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
        Stage popup = new Stage();
        popup.initOwner(episodeListContainer.getScene().getWindow());
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);

        VBox backdrop = new VBox();
        backdrop.setAlignment(Pos.CENTER);
        backdrop.setStyle("-fx-background-color: rgba(0,0,0,0.60);");

        VBox card = new VBox(22);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(400);
        card.setPadding(new Insets(34, 38, 34, 38));
        card.setStyle("""
            -fx-background-color: #0f172a;
            -fx-background-radius: 20;
            -fx-border-color: rgba(239,68,68,0.25);
            -fx-border-radius: 20; -fx-border-width: 1;
            """);

        Label icon = new Label("✕");
        icon.setStyle("""
            -fx-background-color: rgba(239,68,68,0.12);
            -fx-text-fill: #f87171; -fx-font-size: 20px; -fx-font-weight: bold;
            -fx-min-width: 58; -fx-min-height: 58; -fx-background-radius: 29;
            """);
        icon.setAlignment(Pos.CENTER);

        String epLabel = "Episode " + ep.getNumEpisode() +
                         (nvl(ep.getTitle()).isEmpty() ? "" : " — " + ep.getTitle());
        Label title = new Label("Delete " + epLabel + "?");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");
        title.setWrapText(true);
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label msg = new Label("This episode will be permanently removed.");
        msg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px; -fx-text-alignment: center;");
        msg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button cancelBtn = styledBtn("Cancel", "rgba(255,255,255,0.07)", "#94a3b8");
        Button deleteBtn = styledBtn("Delete", "#dc2626",                "white");

        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(deleteBtn.getStyle().replace("#dc2626","#b91c1c")));
        deleteBtn.setOnMouseExited(e  -> deleteBtn.setStyle(deleteBtn.getStyle().replace("#b91c1c","#dc2626")));

        HBox btns = new HBox(12, cancelBtn, deleteBtn);
        btns.setAlignment(Pos.CENTER);
        card.getChildren().addAll(icon, title, msg, btns);

        card.setScaleX(0.88); card.setScaleY(0.88); card.setOpacity(0);
        backdrop.getChildren().add(card);

        javafx.stage.Window owner = episodeListContainer.getScene().getWindow();
        backdrop.setPrefWidth(owner.getWidth());
        backdrop.setPrefHeight(owner.getHeight());

        Scene scene = new Scene(backdrop);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        popup.setScene(scene);
        popup.setX(owner.getX()); popup.setY(owner.getY());

        FadeTransition fi = new FadeTransition(Duration.millis(180), card);
        ScaleTransition si = new ScaleTransition(Duration.millis(180), card);
        fi.setToValue(1); si.setToX(1); si.setToY(1);
        new ParallelTransition(fi, si).play();

        cancelBtn.setOnAction(e -> popup.close());
        deleteBtn.setOnAction(e -> {
            episodeService.deleteEpisode(ep.getEpId());
            loadEpisodes();
            if (editingEpisode != null && editingEpisode.getEpId() == ep.getEpId()) setAddMode();
            popup.close();
        });
        popup.showAndWait();
    }

    private Button styledBtn(String text, String bg, String fg) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; " +
                   "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 11 28; " +
                   "-fx-background-radius: 12; -fx-cursor: hand; -fx-border-width: 0;");
        return b;
    }

    // ── Validation ────────────────────────────────────────────────────────────
    private boolean validateForm() {
        List<String> errors = new ArrayList<>();

        if (numEpisodeField.getText().isBlank()) {
            errors.add("Episode number required");
        } else {
            try { Integer.parseInt(numEpisodeField.getText().trim()); }
            catch (NumberFormatException e) { errors.add("Episode number must be an integer"); }
        }
        if (titleField.getText().isBlank()) errors.add("Title required");

        if (!durationField.getText().isBlank()) {
            try { Integer.parseInt(durationField.getText().trim()); }
            catch (NumberFormatException e) { errors.add("Duration must be an integer (minutes)"); }
        }
        if (!ratingField.getText().isBlank()) {
            try {
                float r = Float.parseFloat(ratingField.getText().trim());
                if (r < 1f || r > 5f) errors.add("Rating must be 1–5");
            } catch (NumberFormatException e) { errors.add("Rating must be a number (1–5)"); }
        }
        if (videoUrlField.getText().isBlank()) errors.add("Video file is required");

        if (!errors.isEmpty()) {
            showValidation("⚠  " + String.join("  ·  ", errors));
            shakeForm();
            return false;
        }
        return true;
    }

    private void shakeForm() {
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), formPanel);
        shake.setFromX(-6); shake.setToX(6); shake.setCycleCount(4); shake.setAutoReverse(true);
        shake.play();
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
        try { ep.setNumEpisode(Integer.parseInt(numEpisodeField.getText().trim())); } catch (Exception ignored) {}
        try { ep.setDuration(Integer.parseInt(durationField.getText().trim())); }     catch (Exception ignored) {}
        try { ep.setRating(Float.parseFloat(ratingField.getText().trim())); }          catch (Exception ignored) {}
        LocalDate d = releasedAtPicker.getValue();
        ep.setReleasedAt(d != null ? Timestamp.valueOf(d.atStartOfDay()) : null);
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