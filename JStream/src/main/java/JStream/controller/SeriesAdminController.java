package JStream.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import JStream.entity.Serie;
import JStream.entity.Category;
import JStream.service.SerieService;
import JStream.service.FeaturedService;

public class SeriesAdminController {

    // ── FXML Fields ───────────────────────────────────────────────────────────
    @FXML private TextField   titleField;
    @FXML private TextField   directorField;
    @FXML private TextField   ageRatingField;
    @FXML private TextArea    synopsisField;
    @FXML private TextField   castingField;
    @FXML private TextField   coverField;
    @FXML private TextField   titleUrlField;
    @FXML private TextField   searchField;
    @FXML private ListView<Category> categoryListView;
    @FXML private TilePane    seriesContainer;
    @FXML private ScrollPane  scrollPane;
    @FXML private Label       formTitle;
    @FXML private Button      submitBtn;
    @FXML private Button      cancelEditBtn;
    @FXML private Label       serieCountLabel;
    @FXML private VBox        formPanel;
    @FXML private Label       validationLabel;

    // ── State ─────────────────────────────────────────────────────────────────
    private Serie           editingSerie = null;
    private SerieService    serieService;
    private FeaturedService featuredService;

    // ── Constructor ───────────────────────────────────────────────────────────
    public SeriesAdminController() {
        this.serieService = new SerieService();
        try {
            this.featuredService = new FeaturedService();
        } catch (Exception e) {
            System.err.println("FeaturedService init error: " + e.getMessage());
        }
    }

    // ── Init ──────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Allow multi-selection in category list
        categoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        categoryListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        // Bind visibility to managed so hidden items don't take space
        cancelEditBtn.managedProperty().bind(cancelEditBtn.visibleProperty());
        validationLabel.managedProperty().bind(validationLabel.visibleProperty());
        validationLabel.setVisible(false);

        // Clear validation on typing
        titleField.textProperty().addListener((obs, o, n) -> clearValidation());

        // Live search
        searchField.textProperty().addListener((obs, o, n) -> filterSeries(n));

        loadCategories();
        loadSeries(serieService.getAllSeries());
        setAddMode();
    }

    // ── Mode switching ────────────────────────────────────────────────────────
    private void setAddMode() {
        editingSerie = null;
        formTitle.setText("✦ Add New Series");
        submitBtn.setText("Add Series");
        cancelEditBtn.setVisible(false);
        clearFields();
        clearValidation();
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

        // ── FIX: re-select categories by matching category_id ─────────────
        // Must run after the ListView is rendered, so use Platform.runLater
        javafx.application.Platform.runLater(() -> {
            categoryListView.getSelectionModel().clearSelection();
            if (serie.getCategories() == null) return;

            ObservableList<Category> items = categoryListView.getItems();
            for (int i = 0; i < items.size(); i++) {
                final int idx = i;
                boolean shouldSelect = serie.getCategories().stream()
                        .anyMatch(sel -> sel.getCategory_id() == items.get(idx).getCategory_id());
                if (shouldSelect) {
                    categoryListView.getSelectionModel().select(idx);
                }
            }
        });

        clearValidation();
        scrollPane.setVvalue(0);
        animateFormHighlight();
    }

    // ── FXML Actions ──────────────────────────────────────────────────────────
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

    // ── File choosers ─────────────────────────────────────────────────────────
    @FXML private void chooseCoverFile()    { chooseImageFile(coverField,    "Choose Cover Image"); }
    @FXML private void chooseTitleUrlFile() { chooseImageFile(titleUrlField, "Choose Title Logo"); }

    private void chooseImageFile(TextField target, String dialogTitle) {
        if (target == null || target.getScene() == null) return;
        FileChooser fc = new FileChooser();
        fc.setTitle(dialogTitle);
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp", "*.bmp")
        );
        File f = fc.showOpenDialog(target.getScene().getWindow());
        if (f != null) target.setText(f.getAbsolutePath());
    }

    // ── Navigation to seasons ─────────────────────────────────────────────────
    private void ouvrirDetailsSerie(Serie serie) {
        try {
            String fxmlPath = "/view/fxml/admin_seasons.fxml";
            java.net.URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                System.err.println("FXML not found: " + fxmlPath);
                return;
            }
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(resourceUrl);
            javafx.scene.Parent view = loader.load();
            SeasonsAdminController ctrl = loader.getController();
            ctrl.initData(serie);
            replaceContentArea(view);
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void replaceContentArea(javafx.scene.Parent view) {
        javafx.scene.Scene scene = titleField.getScene();
        if (scene == null) return;
        javafx.scene.Parent root = scene.getRoot();
        javafx.scene.Node contentArea = root.lookup("#contentArea");
        if (contentArea instanceof Pane pane) {
            pane.getChildren().setAll(view);
        } else {
            scene.setRoot(view);
        }
    }

    // ── Validation ────────────────────────────────────────────────────────────
    private boolean validateForm() {
        List<String> errors = new ArrayList<>();

        if (titleField.getText().isBlank())   errors.add("Title is required");
        if (synopsisField.getText().isBlank()) errors.add("Synopsis is required");
        if (coverField.getText().isBlank())    errors.add("Cover image is required");
        if (categoryListView.getSelectionModel().getSelectedItems().isEmpty())
            errors.add("At least one category must be selected");

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

    private void showValidation(String msg) {
        validationLabel.setText(msg);
        validationLabel.setVisible(true);
    }

    private void clearValidation() {
        validationLabel.setVisible(false);
    }

    // ── Load / filter ─────────────────────────────────────────────────────────
    private void loadCategories() {
        if (featuredService != null)
            categoryListView.getItems().setAll(featuredService.getAllCategories());
    }

    private void loadSeries(List<Serie> series) {
        seriesContainer.getChildren().clear();

        String text = series.size() + " series";
        serieCountLabel.setText(text);

        if (serieCountLabel != null) {
            serieCountLabel.setText(text);
        }

        for (int i = 0; i < series.size(); i++) {
            VBox card = createSerieCard(series.get(i));
            card.setOpacity(0);
            seriesContainer.getChildren().add(card);

            int delay = i * 45;
            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            FadeTransition fade = new FadeTransition(Duration.millis(320), card);
            fade.setFromValue(0); fade.setToValue(1);

            TranslateTransition slide = new TranslateTransition(Duration.millis(320), card);
            slide.setFromY(18); slide.setToY(0);

            pause.setOnFinished(e -> new ParallelTransition(fade, slide).play());
            pause.play();
        }
    }
    private void filterSeries(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            loadSeries(serieService.getAllSeries());
        } else {
            String kw = keyword.toLowerCase().trim();
            List<Serie> filtered = serieService.getAllSeries().stream()
                .filter(s -> s.getTitle().toLowerCase().contains(kw)
                          || s.getCategoriesAsString().toLowerCase().contains(kw))
                .toList();
            loadSeries(filtered);
        }
    }

    // ── Card builder ──────────────────────────────────────────────────────────
    private VBox createSerieCard(Serie serie) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(false);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 14, 0, 0, 5);");
        loadImage(imageView, serie.getCovertUrl());

        StackPane posterPane = new StackPane(imageView);
        posterPane.setPrefSize(180, 250);

        // Hover overlay with buttons
        VBox overlay = new VBox(6);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0,5,30,0.82); -fx-background-radius: 10;");
        overlay.setOpacity(0);

        Button seasonsBtn = iconButton("▶  Seasons", "#1d4ed8");
        Button editBtn    = iconButton("✎  Edit",    "#0ea5e9");
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

        // Director label
        Label dirLabel = new Label(nvl(serie.getDirector()).isEmpty() ? "" : "Dir. " + serie.getDirector());
        dirLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 10px; -fx-font-style: italic;");
        dirLabel.setVisible(!nvl(serie.getDirector()).isEmpty());
        dirLabel.setManaged(!nvl(serie.getDirector()).isEmpty());

        // Category chips
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

        VBox card = new VBox(8, posterPane, title, dirLabel, chips, meta);
        card.setStyle("""
            -fx-background-color: #080d1a;
            -fx-padding: 12;
            -fx-background-radius: 12;
            -fx-border-color: rgba(29,78,216,0.15);
            -fx-border-radius: 12;
            -fx-cursor: hand;
            """);

        final String baseStyle = card.getStyle();
        card.setOnMouseEntered(e -> card.setStyle(baseStyle + "-fx-effect: dropshadow(gaussian, rgba(29,78,216,0.40), 22, 0, 0, 6);"));
        card.setOnMouseExited(e  -> card.setStyle(baseStyle));

        return card;
    }

    // ── Delete confirmation ────────────────────────────────────────────────────
    private void confirmDelete(Serie serie) {

        Stage popup = new Stage();
        popup.initOwner(scrollPane.getScene().getWindow());
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        popup.initStyle(javafx.stage.StageStyle.UNDECORATED);

        // ── ROOT ──
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.65);");
        root.setPrefSize(400, 220);

        // ── CARD ──
        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setStyle("""
            -fx-background-color: linear-gradient(to bottom right, #0f172a, #111827);
            -fx-background-radius: 18;
            -fx-padding: 26;
            -fx-border-color: rgba(255,255,255,0.08);
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0, 0, 10);
        """);

        // Title
        Label title = new Label("Delete Series?");
        title.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 20px;
            -fx-font-weight: bold;
        """);

        // Message
        Label msg = new Label("Are you sure you want to delete:\n" + serie.getTitle() +
                "\n\nAll seasons and episodes will also be removed.");
        msg.setStyle("""
            -fx-text-fill: #cbd5e1;
            -fx-font-size: 13px;
            -fx-text-alignment: center;
        """);

        // Buttons
        Button deleteBtn = new Button("Delete");
        Button cancelBtn = new Button("Cancel");

        deleteBtn.setStyle("""
            -fx-background-color: #ef4444;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-padding: 8 20;
            -fx-cursor: hand;
        """);

        cancelBtn.setStyle("""
            -fx-background-color: #1f2937;
            -fx-text-fill: #e5e7eb;
            -fx-background-radius: 10;
            -fx-padding: 8 20;
            -fx-cursor: hand;
        """);

        // Hover effect
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("""
            -fx-background-color: #dc2626;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-padding: 8 20;
        """));

        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("""
            -fx-background-color: #ef4444;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-padding: 8 20;
        """));

        HBox buttons = new HBox(12, cancelBtn, deleteBtn);
        buttons.setAlignment(Pos.CENTER);

        card.getChildren().addAll(title, msg, buttons);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 420, 240);
        popup.setScene(scene);
        popup.centerOnScreen();

        // ── ANIMATION ──
        card.setScaleX(0.8);
        card.setScaleY(0.8);
        card.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.millis(180), card);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(180), card);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1);
        scale.setToY(1);

        new ParallelTransition(fade, scale).play();

        // ── ACTIONS ──
        cancelBtn.setOnAction(e -> popup.close());

        deleteBtn.setOnAction(e -> {
            serieService.deleteSerie(serie.getSerieId());
            loadSeries(serieService.getAllSeries());
            showToast("Series deleted ✓");

            if (editingSerie != null &&
                editingSerie.getSerieId() == serie.getSerieId()) {
                setAddMode();
            }

            popup.close();
        });

        popup.showAndWait();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
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
        ObservableList<Category> sel = categoryListView.getSelectionModel().getSelectedItems();
        s.setCategories(new ArrayList<>(sel));
    }

    private void clearFields() {
        titleField.clear();
        directorField.clear();
        ageRatingField.clear();
        synopsisField.clear();
        castingField.clear();
        coverField.clear();
        titleUrlField.clear();
        categoryListView.getSelectionModel().clearSelection();
    }

    private void loadImage(ImageView iv, String path) {
        if (path == null || path.isEmpty()) return;
        try {
            Image img;
            File f = new File(path);
            if (path.startsWith("http"))   img = new Image(path, true);
            else if (f.exists())            img = new Image(f.toURI().toString(), true);
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

    private void animateFormHighlight() {
        FadeTransition ft = new FadeTransition(Duration.millis(200), formPanel);
        ft.setFromValue(0.4); ft.setToValue(1.0); ft.play();
    }

    private String nvl(String s) { return s == null ? "" : s; }

    private void showToast(String msg) {
        System.out.println("[JStream] " + msg);
    }
}