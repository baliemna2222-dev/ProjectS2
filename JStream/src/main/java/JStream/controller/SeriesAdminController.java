package JStream.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import JStream.entity.Serie;
import JStream.entity.Category;
import JStream.service.ActorService;
import JStream.service.SerieService;
import JStream.service.FeaturedService;

public class SeriesAdminController {

    @FXML private TextField   titleField;
    @FXML private TextField   directorField;
    @FXML private TextField   ageRatingField;
    @FXML private TextArea    synopsisField;
    @FXML private TextField   castingField;
    @FXML private TextField   coverField;
    @FXML private TextField   titleUrlField;
    @FXML private TextField   searchField;
    @FXML private FlowPane    categoryChipPane;

    @FXML private TilePane    seriesContainer;
    @FXML private ScrollPane  scrollPane;
    @FXML private Label       formTitle;
    @FXML private Button      submitBtn;
    @FXML private Button      cancelEditBtn;
    @FXML private Label       serieCountLabel;
    @FXML private VBox        formPanel;
    @FXML private Label       validationLabel;
    private Serie           editingSerie  = null;
    private SerieService    serieService;
    private FeaturedService featuredService;
    private ActorService    actorService  = new ActorService();

    private List<Category>         allCategories  = new ArrayList<>();
    private final Set<Integer>     selectedCatIds = new LinkedHashSet<>();
    private ActorPanelBuilder actorPanel;
    private static final String C_ACCENT = "#1d4ed8";
    private static final String C_ACCENT2 = "#0ea5e9";
    private static final String C_TEXT    = "#e2e8f0";
    private static final String C_MUTED   = "#64748b";
    private static final String C_DANGER  = "#ef4444";

    //  Constructor
    public SeriesAdminController() {
        this.serieService = new SerieService();
        try {
            this.featuredService = new FeaturedService();
        } catch (Exception e) {
            System.err.println("FeaturedService init error: " + e.getMessage());
        }
    }
    @FXML
    public void initialize() {
        cancelEditBtn.managedProperty().bind(cancelEditBtn.visibleProperty());
        validationLabel.managedProperty().bind(validationLabel.visibleProperty());
        validationLabel.setVisible(false);
        cancelEditBtn.setVisible(false);

        titleField.textProperty().addListener((obs, o, n) -> clearValidation());
        searchField.textProperty().addListener((obs, o, n) -> filterSeries(n));

        loadCategoriesOnce();
        loadSeries(serieService.getAllSeries());

        actorPanel = new ActorPanelBuilder(actorService, () ->
            editingSerie != null ? editingSerie.getSerieId() : -1
        );
        actorPanel.setMode(ActorPanelBuilder.Mode.SERIE);
        VBox actorSection = actorPanel.buildPanel();

        VBox actorCard = new VBox(0, actorSection);
        actorCard.setStyle(
            "-fx-background-color: rgba(29,78,216,0.05);" +
            "-fx-border-color: rgba(29,78,216,0.18);" +
            "-fx-border-radius: 12; -fx-background-radius: 12;" +
            "-fx-padding: 16;"
        );

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.07);");
        formPanel.getChildren().addAll(sep, actorCard);

        setAddMode();
    }
    private void loadCategoriesOnce() {
        if (featuredService == null) return;
        allCategories = featuredService.getAllCategories();
        rebuildCategoryChips();
    }

    private void rebuildCategoryChips() {
        if (categoryChipPane == null) return;
        categoryChipPane.getChildren().clear();
        categoryChipPane.setHgap(8);
        categoryChipPane.setVgap(8);
        categoryChipPane.setPadding(new Insets(6, 0, 6, 0));
        for (Category cat : allCategories) {
            boolean selected = selectedCatIds.contains(cat.getCategory_id());
            categoryChipPane.getChildren().add(createCategoryChip(cat, selected));
        }
    }

    private ToggleButton createCategoryChip(Category cat, boolean initiallySelected) {
        ToggleButton chip = new ToggleButton(cat.getName());
        chip.setSelected(initiallySelected);
        applyChipStyle(chip, initiallySelected);
        chip.selectedProperty().addListener((obs, wasOn, isOn) -> {
            applyChipStyle(chip, isOn);
            if (isOn) selectedCatIds.add(cat.getCategory_id());
            else      selectedCatIds.remove(cat.getCategory_id());
        });
        return chip;
    }

    private void applyChipStyle(ToggleButton chip, boolean selected) {
        if (selected) {
            chip.setStyle("""
                -fx-background-color: linear-gradient(to right, #1d4ed8, #0ea5e9);
                -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;
                -fx-padding: 5 14; -fx-background-radius: 30; -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(29,78,216,0.55), 8, 0, 0, 2);
                """);
        } else {
            chip.setStyle("""
                -fx-background-color: rgba(29,78,216,0.12); -fx-text-fill: #94a3b8;
                -fx-font-size: 11px; -fx-padding: 5 14; -fx-background-radius: 30;
                -fx-cursor: hand; -fx-border-color: rgba(29,78,216,0.28); -fx-border-radius: 30;
                """);
        }
    }
    private void setAddMode() {
        editingSerie = null;
        formTitle.setText("✦ Add New Series");
        submitBtn.setText("Add Series");
        cancelEditBtn.setVisible(false);
        clearFields();
        clearValidation();
        if (actorPanel != null) {
            actorPanel.clearPending();
            actorPanel.refreshActors();
        }
    }

    private void setEditMode(Serie serie) {
        editingSerie = serie;
        formTitle.setText("✎ Editing: " + serie.getTitle());
        submitBtn.setText("Save Changes");
        cancelEditBtn.setVisible(true);

        titleField.setText(nvl(serie.getTitle()));
        directorField.setText(nvl(serie.getDirector()));
        ageRatingField.setText(nvl(serie.getAge_rating()));
        synopsisField.setText(nvl(serie.getSynopsis()));
        castingField.setText(nvl(serie.getCasting()));
        coverField.setText(nvl(serie.getCovertUrl()));
        titleUrlField.setText(nvl(serie.getTitleUrl()));

        selectedCatIds.clear();
        if (serie.getCategories() != null)
            serie.getCategories().forEach(c -> selectedCatIds.add(c.getCategory_id()));
        rebuildCategoryChips();

        if (actorPanel != null) actorPanel.refreshActors();

        clearValidation();
        scrollPane.setVvalue(0);
        animateFormHighlight();
    }
    @FXML
    private void handleSubmit() {
        if (!validateForm()) return;
        if (editingSerie == null) doAddSerie();
        else                      doUpdateSerie();
    }

    @FXML
    private void cancelEdit() { setAddMode(); }

    private void doAddSerie() {
        Serie serie = buildSerieFromForm();
        serieService.addSerie(serie);
        editingSerie = serie;  // give panel a real ID
        if (actorPanel != null) actorPanel.flushPendingActors();
        loadSeries(serieService.getAllSeries());
        clearFields();
        setAddMode();
        showToast("Series added successfully ✓");
    }

    private void doUpdateSerie() {
        populateSerieFromForm(editingSerie);
        serieService.updateSerie(editingSerie);
        loadSeries(serieService.getAllSeries());
        setAddMode();
        showToast("Series updated successfully ✓");
    }

    @FXML private void chooseCoverFile()    { chooseImageFile(coverField,    "Choose Cover Image"); }
    @FXML private void chooseTitleUrlFile() { chooseImageFile(titleUrlField, "Choose Title Logo"); }

    private void chooseImageFile(TextField target, String dialogTitle) {
        if (target == null || target.getScene() == null) return;
        FileChooser fc = new FileChooser();
        fc.setTitle(dialogTitle);
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files",
            "*.png","*.jpg","*.jpeg","*.gif","*.webp","*.bmp"));
        File f = fc.showOpenDialog(target.getScene().getWindow());
        if (f != null) target.setText(f.getAbsolutePath());
    }
    private void ouvrirDetailsSerie(Serie serie) {
        try {
            String fxmlPath = "/view/fxml/admin_seasons.fxml";
            java.net.URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) { System.err.println("FXML not found: " + fxmlPath); return; }
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(resourceUrl);
            javafx.scene.Parent view = loader.load();
            SeasonsAdminController ctrl = loader.getController();
            ctrl.initData(serie);
            replaceContentArea(view);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void replaceContentArea(javafx.scene.Parent view) {
        javafx.scene.Scene scene = titleField.getScene();
        if (scene == null) return;
        javafx.scene.Parent root = scene.getRoot();
        javafx.scene.Node contentArea = root.lookup("#contentArea");
        if (contentArea instanceof Pane pane) pane.getChildren().setAll(view);
        else                                  scene.setRoot(view);
    }
    private boolean validateForm() {
        List<String> errors = new ArrayList<>();
        if (titleField.getText().isBlank())    errors.add("Title is required");
        if (synopsisField.getText().isBlank()) errors.add("Synopsis is required");
        if (coverField.getText().isBlank())    errors.add("Cover image is required");
        if (selectedCatIds.isEmpty())          errors.add("At least one category must be selected");
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
    private void showValidation(String msg)  { validationLabel.setText(msg); validationLabel.setVisible(true); }
    private void clearValidation()           { validationLabel.setVisible(false); }
    private void loadSeries(List<Serie> series) {
        seriesContainer.getChildren().clear();
        serieCountLabel.setText(series.size() + " series");
        for (int i = 0; i < series.size(); i++) {
            VBox card = createSerieCard(series.get(i));
            card.setOpacity(0);
            seriesContainer.getChildren().add(card);
            int delay = i * 45;
            PauseTransition p = new PauseTransition(Duration.millis(delay));
            FadeTransition  f = new FadeTransition(Duration.millis(320), card);
            TranslateTransition s = new TranslateTransition(Duration.millis(320), card);
            f.setFromValue(0); f.setToValue(1);
            s.setFromY(18);    s.setToY(0);
            p.setOnFinished(e -> new ParallelTransition(f, s).play());
            p.play();
        }
    }

    private void filterSeries(String keyword) {
        if (keyword == null || keyword.isBlank()) { loadSeries(serieService.getAllSeries()); return; }
        String kw = keyword.toLowerCase().trim();
        List<Serie> filtered = serieService.getAllSeries().stream()
            .filter(s -> s.getTitle().toLowerCase().contains(kw)
                      || s.getCategoriesAsString().toLowerCase().contains(kw))
            .toList();
        loadSeries(filtered);
    }
    private VBox createSerieCard(Serie serie) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180); imageView.setFitHeight(250);
        imageView.setPreserveRatio(false);
        loadImage(imageView, serie.getCovertUrl());

        StackPane posterPane = new StackPane(imageView);
        posterPane.setPrefSize(180, 250);

        VBox overlay = new VBox(6);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0,5,30,0.82); -fx-background-radius: 10;");
        overlay.setOpacity(0);

        Button seasonsBtn = iconButton("▶  Seasons", C_ACCENT);
        Button editBtn    = iconButton("✎  Edit",    C_ACCENT2);
        Button delBtn     = iconButton("✕  Delete",  "#374151");

        seasonsBtn.setOnAction(e -> ouvrirDetailsSerie(serie));
        editBtn.setOnAction(e    -> setEditMode(serie));
        delBtn.setOnAction(e     -> confirmDelete(serie));
        overlay.getChildren().addAll(seasonsBtn, editBtn, delBtn);
        posterPane.getChildren().add(overlay);

        FadeTransition fadeIn  = new FadeTransition(Duration.millis(180), overlay);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(180), overlay);
        posterPane.setOnMouseEntered(e -> { fadeIn.setFromValue(overlay.getOpacity()); fadeIn.setToValue(1); fadeIn.play(); });
        posterPane.setOnMouseExited(e  -> { fadeOut.setFromValue(overlay.getOpacity()); fadeOut.setToValue(0); fadeOut.play(); });

        Label title = new Label(serie.getTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-wrap-text: true; -fx-max-width: 180;");

        Label dirLabel = new Label(nvl(serie.getDirector()).isEmpty() ? "" : "Dir. " + serie.getDirector());
        dirLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 10px; -fx-font-style: italic;");
        dirLabel.setVisible(!nvl(serie.getDirector()).isEmpty());
        dirLabel.setManaged(!nvl(serie.getDirector()).isEmpty());
        HBox actorBubbles = buildActorBubbles(serie.getSerieId());

        FlowPane chips = new FlowPane();
        chips.setHgap(5); chips.setVgap(5);
        chips.setPrefWrapLength(180);
        if (serie.getCategories() != null) {
            for (Category c : serie.getCategories()) {
                Label chip = new Label(c.getName());
                chip.setStyle("-fx-background-color: rgba(29,78,216,0.25); -fx-text-fill: #60a5fa; -fx-padding: 2 8; -fx-background-radius: 20; -fx-font-size: 10px;");
                chips.getChildren().add(chip);
            }
        }

        Label meta = new Label(nvl(serie.getAge_rating()));
        meta.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");

        VBox card = new VBox(8, posterPane, title, dirLabel, actorBubbles, chips, meta);
        card.setStyle("""
            -fx-background-color: #080d1a; -fx-padding: 12;
            -fx-background-radius: 12; -fx-border-color: rgba(29,78,216,0.15);
            -fx-border-radius: 12; -fx-cursor: hand;
            """);
        final String baseStyle = card.getStyle();
        card.setOnMouseEntered(e -> card.setStyle(baseStyle + "-fx-effect: dropshadow(gaussian, rgba(29,78,216,0.40), 22, 0, 0, 6);"));
        card.setOnMouseExited(e  -> card.setStyle(baseStyle));
        return card;
    }

    private HBox buildActorBubbles(int serieId) {
        HBox row = new HBox(-8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(2, 0, 0, 0));
        if (serieId <= 0) return row;

        List<JStream.entity.Actor> actors = actorService.getActorsBySerie(serieId);
        int shown = Math.min(actors.size(), 3);
        for (int i = 0; i < shown; i++) {
            JStream.entity.Actor a = actors.get(i);
            ImageView iv = new ImageView();
            iv.setFitWidth(24); iv.setFitHeight(24);
            iv.setPreserveRatio(false);
            loadImage(iv, a.getPhotoUrl());

            javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(12, 12, 12);
            iv.setClip(clip);

            StackPane bubble = new StackPane(iv);
            bubble.setPrefSize(24, 24); bubble.setMinSize(24, 24); bubble.setMaxSize(24, 24);
            bubble.setStyle(
                "-fx-background-color: rgba(29,78,216,0.25); -fx-background-radius: 12;" +
                "-fx-border-color: #080d1a; -fx-border-width: 1.5; -fx-border-radius: 12;"
            );
            Tooltip.install(bubble, styledTooltip(a.getName() + (notBlank(a.getRoleName()) ? " — " + a.getRoleName() : "")));
            row.getChildren().add(bubble);
        }
        if (actors.size() > 3) {
            Label more = new Label("+" + (actors.size() - 3));
            more.setStyle("-fx-text-fill: #60a5fa; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 0 0 0 12;");
            row.getChildren().add(more);
        }
        return row;
    }
    private void confirmDelete(Serie serie) {
        Stage popup = new Stage();
        popup.initOwner(scrollPane.getScene().getWindow());
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);

        VBox backdrop = new VBox();
        backdrop.setAlignment(Pos.CENTER);
        backdrop.setStyle("-fx-background-color: rgba(0,0,0,0.65);");

        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(400);
        card.setPadding(new Insets(32, 36, 32, 36));
        card.setStyle("""
            -fx-background-color: #0f172a; -fx-background-radius: 20;
            -fx-border-color: rgba(239,68,68,0.25); -fx-border-radius: 20; -fx-border-width: 1;
            """);

        Label icon = new Label("✕");
        icon.setAlignment(Pos.CENTER);
        icon.setStyle("""
            -fx-text-fill: #f87171; -fx-font-size: 20px; -fx-font-weight: bold;
            -fx-background-color: rgba(239,68,68,0.12); -fx-background-radius: 28;
            -fx-min-width: 56; -fx-min-height: 56;
            """);

        Label title = new Label("Delete Series?");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label msg = new Label("\"" + serie.getTitle() + "\"\nAll seasons and episodes will be permanently removed.");
        msg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px; -fx-text-alignment: center;");
        msg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        msg.setWrapText(true);

        Button cancelBtn = styledBtn("Cancel",        "rgba(255,255,255,0.07)", "#94a3b8");
        Button deleteBtn = styledBtn("Delete Series",  C_DANGER,                 "white");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(deleteBtn.getStyle().replace(C_DANGER, "#b91c1c")));
        deleteBtn.setOnMouseExited(e  -> deleteBtn.setStyle(deleteBtn.getStyle().replace("#b91c1c", C_DANGER)));

        HBox btns = new HBox(12, cancelBtn, deleteBtn);
        btns.setAlignment(Pos.CENTER);
        card.getChildren().addAll(icon, title, msg, btns);

        card.setScaleX(0.88); card.setScaleY(0.88); card.setOpacity(0);
        backdrop.getChildren().add(card);

        javafx.stage.Window owner = scrollPane.getScene().getWindow();
        backdrop.setPrefWidth(owner.getWidth());
        backdrop.setPrefHeight(owner.getHeight());

        Scene scene = new Scene(backdrop);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        popup.setScene(scene);
        popup.setX(owner.getX()); popup.setY(owner.getY());

        FadeTransition  fi = new FadeTransition(Duration.millis(180), card);
        ScaleTransition si = new ScaleTransition(Duration.millis(180), card);
        fi.setToValue(1); si.setToX(1); si.setToY(1);
        new ParallelTransition(fi, si).play();

        cancelBtn.setOnAction(e -> popup.close());
        deleteBtn.setOnAction(e -> {
            serieService.deleteSerie(serie.getSerieId());
            loadSeries(serieService.getAllSeries());
            showToast("Series deleted ✓");
            if (editingSerie != null && editingSerie.getSerieId() == serie.getSerieId()) setAddMode();
            popup.close();
        });
        popup.showAndWait();
    }
    private Serie buildSerieFromForm() {
        Serie s = new Serie();
        populateSerieFromForm(s);
        return s;
    }

    private void populateSerieFromForm(Serie s) {
        s.setTitle(titleField.getText().trim());
        s.setDirector(directorField.getText().trim());
        s.setSynopsis(synopsisField.getText().trim());
        s.setCasting(castingField.getText().trim());
        s.setAge_rating(ageRatingField.getText().trim());
        s.setCovertUrl(coverField.getText().trim());
        s.setTitleUrl(titleUrlField.getText().trim());
        List<Category> selected = new ArrayList<>();
        for (Category c : allCategories)
            if (selectedCatIds.contains(c.getCategory_id())) selected.add(c);
        s.setCategories(selected);
    }

    private void clearFields() {
        titleField.clear(); directorField.clear(); ageRatingField.clear();
        synopsisField.clear(); castingField.clear(); coverField.clear(); titleUrlField.clear();
        selectedCatIds.clear();
        rebuildCategoryChips();
    }

    private void loadImage(ImageView iv, String path) {
        if (path == null || path.isEmpty()) return;
        try {
            Image img;
            File f = new File(path);
            if      (path.startsWith("http")) img = new Image(path, true);
            else if (f.exists())              img = new Image(f.toURI().toString(), true);
            else {
                java.net.URL res = getClass().getResource(path);
                if (res == null) return;
                img = new Image(res.toExternalForm(), true);
            }
            iv.setImage(img);
        } catch (Exception ignored) {}
    }

    private Button iconButton(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 20; -fx-background-radius: 20; -fx-cursor: hand; -fx-min-width: 120;");
        return b;
    }

    private Button styledBtn(String text, String bg, String fg) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 11 28; -fx-background-radius: 12; -fx-cursor: hand; -fx-border-width: 0;");
        return b;
    }

    private Tooltip styledTooltip(String text) {
        Tooltip tt = new Tooltip(text);
        tt.setStyle("-fx-background-color: #111827; -fx-text-fill: #e2e8f0; -fx-font-size: 11px; -fx-background-radius: 8; -fx-padding: 6 10;");
        return tt;
    }

    private void animateFormHighlight() {
        FadeTransition ft = new FadeTransition(Duration.millis(200), formPanel);
        ft.setFromValue(0.4); ft.setToValue(1.0); ft.play();
    }

    private String nvl(String s)       { return s == null ? "" : s; }
    private boolean notBlank(String s) { return s != null && !s.isBlank(); }

    private void showToast(String msg) {
        System.out.println("[JStream] " + msg);
    }
}