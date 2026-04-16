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

import JStream.entity.Serie;
import JStream.entity.Season;
import JStream.service.SeasonService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeasonsAdminController {

    // ── FXML Fields ───────────────────────────────────────────────────────────
    @FXML private Label       titreSerieLabel;
    @FXML private VBox        seasonsListContainer;
    @FXML private TextField   seasonNumField;
    @FXML private TextField   plannedEpisodesField;
    @FXML private TextField   ratingField;
    @FXML private TextField   titleField;
    @FXML private TextArea    synopsisField;
    @FXML private TextField   trailerUrlField;
    @FXML private TextField   posterUrlField;
    @FXML private TextField   titleUrlField;
    @FXML private TextField   imageUrlField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label       formTitleLabel;
    @FXML private Button      submitBtn;
    @FXML private Button      cancelEditBtn;
    @FXML private Label       validationLabel;
    @FXML private VBox        formPanel;
    @FXML private Label       seasonCountLabel;

    // ── State ─────────────────────────────────────────────────────────────────
    private final SeasonService seasonService = new SeasonService();
    private Serie  serieActuelle;
    private Season editingSeason = null;

    // ── Init ──────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll("Ongoing", "Completed", "Cancelled", "Upcoming");
        cancelEditBtn.managedProperty().bind(cancelEditBtn.visibleProperty());
        validationLabel.managedProperty().bind(validationLabel.visibleProperty());
        validationLabel.setVisible(false);
        cancelEditBtn.setVisible(false);

        seasonNumField.textProperty().addListener((obs, o, n) -> clearValidation());
        titleField.textProperty().addListener((obs, o, n) -> clearValidation());
    }

    public void initData(Serie serie) {
        this.serieActuelle = serie;
        if (titreSerieLabel != null)
            titreSerieLabel.setText(serie.getTitle());
        chargerSaisons();
    }

    // ── Mode switching ────────────────────────────────────────────────────────
    private void setAddMode() {
        editingSeason = null;
        formTitleLabel.setText("✦ Add Season");
        submitBtn.setText("Add Season");
        cancelEditBtn.setVisible(false);
        clearFields();
        clearValidation();
    }

    private void setEditMode(Season season) {
        editingSeason = season;
        formTitleLabel.setText("✎ Editing: Season " + season.getSeasonNum());
        submitBtn.setText("Save Changes");
        cancelEditBtn.setVisible(true);

        seasonNumField.setText(String.valueOf(season.getSeasonNum()));
        titleField.setText(nvl(season.getTitle()));
        synopsisField.setText(nvl(season.getSynopsis()));
        plannedEpisodesField.setText(season.getPlannedEpisodes() > 0 ? String.valueOf(season.getPlannedEpisodes()) : "");
        ratingField.setText(season.getRating() > 0 ? String.valueOf(season.getRating()) : "");
        trailerUrlField.setText(nvl(season.getTrailerUrl()));
        posterUrlField.setText(nvl(season.getPosterUrl()));
        titleUrlField.setText(nvl(season.getTitleUrl()));
        imageUrlField.setText(nvl(season.getImageUrl()));
        statusComboBox.setValue(season.getStatus());

        clearValidation();
        animateFormHighlight();
    }

    // ── Submit ────────────────────────────────────────────────────────────────
    @FXML
    private void handleAddSeason() {
        if (!validateForm()) return;
        if (editingSeason == null) doAddSeason();
        else                       doUpdateSeason();
    }

    @FXML
    private void cancelEdit() { setAddMode(); }

    private void doAddSeason() {
        Season s = buildSeasonFromForm();
        seasonService.addSeason(s);
        clearFields();
        chargerSaisons();
        setAddMode();
        showToast("Season added successfully ✓");
    }

    private void doUpdateSeason() {
        populateSeasonFromForm(editingSeason);
        seasonService.updateSeason(editingSeason);
        chargerSaisons();
        setAddMode();
        showToast("Season updated successfully ✓");
    }

    // ── File choosers ─────────────────────────────────────────────────────────
    @FXML private void choosePosterFile()   { chooseImageFile(posterUrlField,  "Choose Poster Image"); }
    @FXML private void chooseTitleUrlFile() { chooseImageFile(titleUrlField,   "Choose Title Logo"); }
    @FXML private void chooseImageFile()    { chooseImageFile(imageUrlField,   "Choose General Image"); }
    @FXML private void chooseTrailerFile()  { chooseVideoFile(trailerUrlField, "Choose Trailer Video"); }

    private void chooseImageFile(TextField target, String dialogTitle) {
        chooseFile(target, dialogTitle, new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp", "*.bmp"));
    }

    private void chooseVideoFile(TextField target, String dialogTitle) {
        chooseFile(target, dialogTitle, new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mkv", "*.avi", "*.mov", "*.webm"));
    }

    private void chooseFile(TextField target, String dialogTitle, FileChooser.ExtensionFilter filter) {
        if (target == null || target.getScene() == null) return;
        FileChooser fc = new FileChooser();
        fc.setTitle(dialogTitle);
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().add(filter);
        File f = fc.showOpenDialog(target.getScene().getWindow());
        if (f != null) target.setText(f.getAbsolutePath());
    }

    // ── Load seasons ──────────────────────────────────────────────────────────
    private void chargerSaisons() {
        if (seasonsListContainer == null) return;
        seasonsListContainer.getChildren().clear();

        List<Season> seasons = seasonService.getSeasonsBySerie(serieActuelle.getSerieId());
        seasonCountLabel.setText(seasons.size() + " season" + (seasons.size() != 1 ? "s" : ""));

        if (seasons.isEmpty()) {
            Label empty = new Label("No seasons yet. Add the first one →");
            empty.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 14px; -fx-padding: 40 0;");
            seasonsListContainer.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < seasons.size(); i++) {
            Season season = seasons.get(i);
            VBox card = createSeasonCard(season);
            card.setOpacity(0);
            seasonsListContainer.getChildren().add(card);

            int delay = i * 60;
            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            FadeTransition fade   = new FadeTransition(Duration.millis(300), card);
            fade.setFromValue(0); fade.setToValue(1);
            TranslateTransition slide = new TranslateTransition(Duration.millis(300), card);
            slide.setFromX(-20); slide.setToX(0);
            pause.setOnFinished(e -> new ParallelTransition(fade, slide).play());
            pause.play();
        }
    }

    // ── Season card ───────────────────────────────────────────────────────────
    private VBox createSeasonCard(Season season) {
        Label badge = new Label("S" + season.getSeasonNum());
        badge.setStyle("""
            -fx-background-color: linear-gradient(to bottom right, #1d4ed8, #0ea5e9);
            -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px;
            -fx-padding: 10 16; -fx-background-radius: 10; -fx-min-width: 52;
            """);

        String displayTitle = "Season " + season.getSeasonNum();
        if (season.getTitle() != null && !season.getTitle().isEmpty())
            displayTitle += " — " + season.getTitle();

        Label lblTitle = new Label(displayTitle);
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px;");

        // Status chip colours
        String statusBg = switch (nvl(season.getStatus())) {
            case "Completed" -> "rgba(16,185,129,0.20)";
            case "Ongoing"   -> "rgba(59,130,246,0.20)";
            case "Cancelled" -> "rgba(239,68,68,0.20)";
            default          -> "rgba(107,114,128,0.20)";
        };
        String statusFg = switch (nvl(season.getStatus())) {
            case "Completed" -> "#34d399";
            case "Ongoing"   -> "#60a5fa";
            case "Cancelled" -> "#f87171";
            default          -> "#9ca3af";
        };
        String statusLabel = nvl(season.getStatus()).isEmpty() ? "Unknown" : season.getStatus();
        Label statusChip = new Label(statusLabel);
        statusChip.setStyle("-fx-background-color: " + statusBg + "; -fx-text-fill: " + statusFg
                + "; -fx-padding: 3 10; -fx-background-radius: 20; -fx-font-size: 11px;");

        String metaText = season.getPlannedEpisodes() > 0 ? season.getPlannedEpisodes() + " episodes planned" : "";
        if (season.getRating() > 0) metaText += (metaText.isEmpty() ? "" : "  ·  ") + "★ " + season.getRating() + "/5";
        Label lblMeta = new Label(metaText);
        lblMeta.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        VBox info = new VBox(4, lblTitle, new HBox(8, statusChip, lblMeta));
        info.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button episodesBtn = smallBtn("▶ Episodes", "#1d4ed8");
        Button editBtn     = smallBtn("✎ Edit",     "#0ea5e9");
        Button delBtn      = smallBtn("✕",           "#374151");

        episodesBtn.setOnAction(e -> ouvrirDetailsSaison(season));
        editBtn.setOnAction(e     -> setEditMode(season));
        delBtn.setOnAction(e      -> confirmDelete(season));

        HBox actions = new HBox(8, episodesBtn, editBtn, delBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(14, badge, info, actions);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(row);
        final String baseStyle = """
            -fx-background-color: #080d1a;
            -fx-padding: 18 20;
            -fx-background-radius: 14;
            -fx-border-color: rgba(29,78,216,0.18);
            -fx-border-radius: 14;
            -fx-cursor: hand;
            """;
        card.setStyle(baseStyle);
        card.setOnMouseEntered(e -> card.setStyle(baseStyle + "-fx-effect: dropshadow(gaussian, rgba(29,78,216,0.35), 18, 0, 0, 4);"));
        card.setOnMouseExited(e  -> card.setStyle(baseStyle));

        return card;
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    private void ouvrirDetailsSaison(Season season) {
        try {
            java.net.URL resourceUrl = getClass().getResource("/view/fxml/admin_episodes.fxml");
            if (resourceUrl == null) { System.err.println("admin_episodes.fxml not found"); return; }
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(resourceUrl);
            javafx.scene.Parent view = loader.load();
            EpisodesAdminController ctrl = loader.getController();
            ctrl.initData(season, serieActuelle);
            replaceContentArea(view);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void retourSeries() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/view/fxml/admin_series.fxml"));
            javafx.scene.Parent view = loader.load();
            replaceContentArea(view);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void replaceContentArea(javafx.scene.Parent view) {
        javafx.scene.Scene scene = titreSerieLabel.getScene();
        if (scene == null) return;
        javafx.scene.Parent root = scene.getRoot();
        javafx.scene.Node area = root.lookup("#contentArea");
        if (area instanceof Pane pane) pane.getChildren().setAll(view);
        else scene.setRoot(view);
    }

    // ── Delete confirmation ────────────────────────────────────────────────────
    private void confirmDelete(Season season) {
        Stage popup = new Stage();
        popup.initOwner(seasonsListContainer.getScene().getWindow());
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

        Label title = new Label("Delete Season " + season.getSeasonNum() + "?");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label msg = new Label("All episodes in this season will be\npermanently removed.");
        msg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px; -fx-text-alignment: center;");
        msg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        msg.setWrapText(true);

        Button cancelBtn = styledBtn("Cancel",  "rgba(255,255,255,0.07)", "#94a3b8");
        Button deleteBtn = styledBtn("Delete",  "#dc2626",                "white");

        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(deleteBtn.getStyle().replace("#dc2626","#b91c1c")));
        deleteBtn.setOnMouseExited(e  -> deleteBtn.setStyle(deleteBtn.getStyle().replace("#b91c1c","#dc2626")));

        HBox btns = new HBox(12, cancelBtn, deleteBtn);
        btns.setAlignment(Pos.CENTER);
        card.getChildren().addAll(icon, title, msg, btns);

        card.setScaleX(0.88); card.setScaleY(0.88); card.setOpacity(0);
        backdrop.getChildren().add(card);

        javafx.stage.Window owner = seasonsListContainer.getScene().getWindow();
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
            seasonService.deleteSeason(season.getSeasonId());
            chargerSaisons();
            if (editingSeason != null && editingSeason.getSeasonId() == season.getSeasonId()) setAddMode();
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

        if (seasonNumField.getText().isBlank()) {
            errors.add("Season number required");
        } else {
            try { Integer.parseInt(seasonNumField.getText().trim()); }
            catch (NumberFormatException e) { errors.add("Season number must be an integer"); }
        }
        if (!plannedEpisodesField.getText().isBlank()) {
            try { Integer.parseInt(plannedEpisodesField.getText().trim()); }
            catch (NumberFormatException e) { errors.add("Planned episodes must be an integer"); }
        }
        if (!ratingField.getText().isBlank()) {
            try {
                float r = Float.parseFloat(ratingField.getText().trim());
                if (r < 1f || r > 5f) errors.add("Rating must be between 1 and 5");
            } catch (NumberFormatException e) { errors.add("Rating must be a number (1–5)"); }
        }

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
    private Season buildSeasonFromForm() {
        Season s = new Season();
        s.setSerieId(serieActuelle.getSerieId());
        populateSeasonFromForm(s);
        return s;
    }

    private void populateSeasonFromForm(Season s) {
        s.setTitle(titleField.getText().trim());
        s.setSynopsis(synopsisField.getText().trim());
        s.setTrailerUrl(trailerUrlField.getText().trim());
        s.setPosterUrl(posterUrlField.getText().trim());
        s.setTitleUrl(titleUrlField.getText().trim());
        s.setImageUrl(imageUrlField.getText().trim());
        s.setStatus(statusComboBox.getValue());
        try { s.setSeasonNum(Integer.parseInt(seasonNumField.getText().trim())); }           catch (Exception ignored) {}
        try { s.setPlannedEpisodes(Integer.parseInt(plannedEpisodesField.getText().trim())); } catch (Exception ignored) {}
        try { s.setRating(Float.parseFloat(ratingField.getText().trim())); }                 catch (Exception ignored) {}
    }

    private void clearFields() {
        seasonNumField.clear(); titleField.clear(); synopsisField.clear();
        plannedEpisodesField.clear(); ratingField.clear();
        trailerUrlField.clear(); posterUrlField.clear(); titleUrlField.clear(); imageUrlField.clear();
        statusComboBox.getSelectionModel().clearSelection();
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