package JStream.controller;

import java.io.File;
import java.util.*;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import JStream.entity.Film;
import JStream.entity.Category;
import JStream.service.ActorService;
import JStream.service.FilmService;
import JStream.service.FeaturedService;

public class FilmAdminController {

    // ── FXML Fields ───────────────────────────────────────────────────────────
    @FXML private TextField   titleField;
    @FXML private TextField   directorField;
    @FXML private TextField   durationField;
    @FXML private TextField   ageRatingField;
    @FXML private TextField   ratingField;
    @FXML private DatePicker  releaseDatePicker;
    @FXML private TextArea    synopsisArea;
    @FXML private TextField   castingField;
    @FXML private TextField   videoField;
    @FXML private TextField   trailerField;
    @FXML private TextField   coverField;
    @FXML private TextField   titleImageField;
    @FXML private TextField   posterField;
    @FXML private TextField   posterVField;
    @FXML private TextField   searchField;

    @FXML private FlowPane    categoryChipPane;

    // ── The form VBox where we inject the actor panel at the bottom ───────────
    @FXML private VBox        formPanel;

    @FXML private TilePane    filmsContainer;
    @FXML private ScrollPane  scrollPane;
    @FXML private Label       formTitle;
    @FXML private Button      submitBtn;
    @FXML private Button      cancelEditBtn;
    @FXML private Label       filmCountLabel;
    @FXML private Label       validationLabel;

    // ── State ─────────────────────────────────────────────────────────────────
    private Film              editingFilm   = null;
    private FilmService       filmService;
    private FeaturedService   featuredService;
    private ActorService      actorService  = new ActorService();

    private List<Category>    allCategories = new ArrayList<>();
    private final Set<Integer> selectedCatIds = new LinkedHashSet<>();

    // ── Actor panel (reusable helper) ─────────────────────────────────────────
    private ActorPanelBuilder actorPanel;

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final String C_BG_DEEP = "#07091a";
    private static final String C_BG_CARD = "#0b1026";
    private static final String C_ACCENT  = "#2563eb";
    private static final String C_ACCENT2 = "#38bdf8";
    private static final String C_TEXT    = "#e2e8f0";
    private static final String C_MUTED   = "#64748b";
    private static final String C_DANGER  = "#ef4444";

    // ── Constructor ───────────────────────────────────────────────────────────
    public FilmAdminController() {
        this.filmService = new FilmService();
        try {
            this.featuredService = new FeaturedService();
        } catch (Exception e) {
            System.err.println("[JStream] FeaturedService init error: " + e.getMessage());
        }
    }

    // ── Init ──────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        loadCategoriesOnce();
        loadFilms(filmService.getAllFilms());

        searchField.textProperty().addListener((obs, o, n) -> filterFilms(n));

        validationLabel.setVisible(false);
        validationLabel.setManaged(false);
        cancelEditBtn.setVisible(false);
        cancelEditBtn.setManaged(false);

        // ── Build and inject the actor panel into the form ────────────────────
        actorPanel = new ActorPanelBuilder(actorService, () ->
            editingFilm != null ? editingFilm.getFilm_id() : -1
        );
        actorPanel.setMode(ActorPanelBuilder.Mode.FILM);
        VBox actorSection = actorPanel.buildPanel();

        VBox actorCard = new VBox(0, actorSection);
        actorCard.setStyle(
            "-fx-background-color: rgba(37,99,235,0.05);" +
            "-fx-border-color: rgba(37,99,235,0.18);" +
            "-fx-border-radius: 12; -fx-background-radius: 12;" +
            "-fx-padding: 16;"
        );

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.07);");
        formPanel.getChildren().addAll(sep, actorCard);

        setAddMode();
    }

    // ── Category chips ────────────────────────────────────────────────────────
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
                -fx-background-color: linear-gradient(to right, #2563eb, #38bdf8);
                -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;
                -fx-padding: 5 14; -fx-background-radius: 30; -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(37,99,235,0.55), 8, 0, 0, 2);
                """);
        } else {
            chip.setStyle("""
                -fx-background-color: rgba(37,99,235,0.12); -fx-text-fill: #94a3b8;
                -fx-font-size: 11px; -fx-padding: 5 14; -fx-background-radius: 30;
                -fx-cursor: hand; -fx-border-color: rgba(37,99,235,0.28); -fx-border-radius: 30;
                """);
        }
    }

    // ── Mode switching ────────────────────────────────────────────────────────
    private void setAddMode() {
        editingFilm = null;
        formTitle.setText("✦  Add New Film");
        submitBtn.setText("Add Film");
        cancelEditBtn.setVisible(false);
        cancelEditBtn.setManaged(false);
        clearFields();
        if (actorPanel != null) {
            actorPanel.clearPending();
            actorPanel.refreshActors();
        }
    }

    private void setEditMode(Film film) {
        editingFilm = film;

        formTitle.setText("✎  Editing: " + film.getTitle());
        submitBtn.setText("Save Changes");
        cancelEditBtn.setVisible(true);
        cancelEditBtn.setManaged(true);

        titleField.setText(nvl(film.getTitle()));
        directorField.setText(nvl(film.getDirector()));
        durationField.setText(film.getDuration() > 0 ? String.valueOf((int) film.getDuration()) : "");
        ageRatingField.setText(nvl(film.getAge_rating()));
        // ── FIX: rating is float, format without trailing .0 when it's a whole number ──
        ratingField.setText(film.getRating() > 0
            ? (film.getRating() == Math.floor(film.getRating())
                ? String.valueOf((int) film.getRating())
                : String.valueOf(film.getRating()))
            : "");
        synopsisArea.setText(nvl(film.getSynopsis()));
        castingField.setText(nvl(film.getCasting()));
        videoField.setText(nvl(film.getVideo_url()));
        trailerField.setText(nvl(film.getTrailer_url()));
        coverField.setText(nvl(film.getImage_url()));
        titleImageField.setText(nvl(film.getTitle_image_url()));
        posterField.setText(nvl(film.getPoster_url()));
        posterVField.setText(nvl(film.getPosterV_url()));

        if (film.getRelease_date() != null)
            releaseDatePicker.setValue(film.getRelease_date().toLocalDate());
        else
            releaseDatePicker.setValue(null);

        // ── FIX: fetch fresh categories from DB (IDs are correct), rebuild chips ONCE ──
        selectedCatIds.clear();
        List<Category> freshCats = featuredService != null
            ? featuredService.getCategoriesByFilm(film.getFilm_id())
            : film.getCategories();
        if (freshCats != null) {
            for (Category c : freshCats) {
                System.out.println("[DEBUG] cat: " + c.getName() + " | id: " + c.getCategory_id());
                selectedCatIds.add(c.getCategory_id());
            }
        }
        rebuildCategoryChips(); // called exactly ONCE with correct IDs

        if (actorPanel != null) actorPanel.refreshActors();

        scrollPane.setVvalue(0);
        animateFormHighlight();
    }

    // ── Submit ────────────────────────────────────────────────────────────────
    @FXML
    private void handleSubmit() {
        if (!validateForm()) return;
        if (editingFilm == null) doAddFilm();
        else                     doUpdateFilm();
    }

    @FXML
    private void cancelEdit() { setAddMode(); }

    private void doAddFilm() {
        Film film = buildFilmFromForm();
        filmService.addFilm(film);
        editingFilm = film;
        if (actorPanel != null) actorPanel.flushPendingActors();
        showToast("Film added successfully ✓", true);
        loadFilms(filmService.getAllFilms());
        setAddMode();
    }

    private void doUpdateFilm() {
        System.out.println("[DEBUG] Selected cat IDs at save: " + selectedCatIds);
        populateFilmFromForm(editingFilm);
        filmService.updateFilm(editingFilm);
        showToast("Film updated successfully ✓", true);
        loadFilms(filmService.getAllFilms());
        setAddMode();
    }

    // ── File choosers ─────────────────────────────────────────────────────────
    @FXML private void chooseVideoFile()      { chooseFile(videoField,      "Choose Video",       "Video Files",  "*.mp4","*.mov","*.avi","*.mkv"); }
    @FXML private void chooseTrailerFile()    { chooseFile(trailerField,    "Choose Trailer",     "Video Files",  "*.mp4","*.mov","*.avi","*.mkv"); }
    @FXML private void chooseCoverFile()      { chooseFile(coverField,      "Choose Cover Image", "Images",       "*.png","*.jpg","*.jpeg","*.gif","*.webp"); }
    @FXML private void chooseTitleImageFile() { chooseFile(titleImageField, "Choose Title Image", "Images",       "*.png","*.jpg","*.jpeg","*.gif","*.webp"); }
    @FXML private void choosePosterFile()     { chooseFile(posterField,     "Choose Poster (H)",  "Images",       "*.png","*.jpg","*.jpeg","*.gif","*.webp"); }
    @FXML private void choosePosterVFile()    { chooseFile(posterVField,    "Choose Poster (V)",  "Images",       "*.png","*.jpg","*.jpeg","*.gif","*.webp"); }

    private void chooseFile(TextField target, String title, String desc, String... exts) {
        if (target == null || target.getScene() == null) return;
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(desc, exts));
        File f = fc.showOpenDialog(target.getScene().getWindow());
        if (f != null) target.setText(f.getAbsolutePath());
    }

    // ── Load / filter ─────────────────────────────────────────────────────────
    private void loadFilms(List<Film> films) {
        filmsContainer.getChildren().clear();
        filmCountLabel.setText(films.size() + " film" + (films.size() != 1 ? "s" : ""));
        for (int i = 0; i < films.size(); i++) {
            VBox card = createFilmCard(films.get(i));
            card.setOpacity(0);
            filmsContainer.getChildren().add(card);
            int delay = i * 35;
            PauseTransition     p = new PauseTransition(Duration.millis(delay));
            FadeTransition      f = new FadeTransition(Duration.millis(280), card);
            TranslateTransition s = new TranslateTransition(Duration.millis(280), card);
            f.setFromValue(0); f.setToValue(1);
            s.setFromY(16);    s.setToY(0);
            p.setOnFinished(e -> new ParallelTransition(f, s).play());
            p.play();
        }
    }

    private void filterFilms(String keyword) {
        if (keyword == null || keyword.isBlank()) loadFilms(filmService.getAllFilms());
        else                                      loadFilms(filmService.searchFilms(keyword.trim()));
    }

    // ── Film card ─────────────────────────────────────────────────────────────
    private VBox createFilmCard(Film film) {
        ImageView iv = new ImageView();
        iv.setFitWidth(180); iv.setFitHeight(260);
        iv.setPreserveRatio(false);
        loadImage(iv, film.getPoster_url());

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(180, 260);
        clip.setArcWidth(14); clip.setArcHeight(14);
        iv.setClip(clip);

        VBox overlay = new VBox(10);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(7,9,26,0.82); -fx-background-radius: 12;");
        overlay.setOpacity(0);

        Button editBtn = overlayBtn("✎  Edit",   C_ACCENT);
        Button delBtn  = overlayBtn("✕  Delete", "#374151");
        editBtn.setOnAction(e -> setEditMode(film));
        delBtn.setOnAction(e  -> showDeleteDialog(film));
        overlay.getChildren().addAll(editBtn, delBtn);

        StackPane poster = new StackPane(iv, overlay);
        poster.setPrefSize(180, 260);

        FadeTransition fi = new FadeTransition(Duration.millis(160), overlay);
        FadeTransition fo = new FadeTransition(Duration.millis(160), overlay);
        poster.setOnMouseEntered(e -> { fi.stop(); fo.stop(); fi.setFromValue(overlay.getOpacity()); fi.setToValue(1); fi.play(); });
        poster.setOnMouseExited(e  -> { fi.stop(); fo.stop(); fo.setFromValue(overlay.getOpacity()); fo.setToValue(0); fo.play(); });

        Label lblTitle = new Label(film.getTitle());
        lblTitle.setStyle("-fx-text-fill: " + C_TEXT + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-wrap-text: true; -fx-max-width: 180;");

        Label lblDir = new Label(film.getDirector() != null ? "Dir. " + film.getDirector() : "");
        lblDir.setStyle("-fx-text-fill: " + C_MUTED + "; -fx-font-size: 11px;");

        HBox actorBubbles = buildActorBubbles(film.getFilm_id());

        FlowPane chips = new FlowPane();
        chips.setHgap(4); chips.setVgap(4);
        chips.setPrefWrapLength(180);
        if (film.getCategories() != null) {
            for (Category c : film.getCategories()) {
                Label chip = new Label(c.getName());
                chip.setStyle("""
                    -fx-background-color: rgba(37,99,235,0.22);
                    -fx-text-fill: #60a5fa; -fx-padding: 2 8;
                    -fx-background-radius: 20; -fx-font-size: 10px;
                    """);
                chips.getChildren().add(chip);
            }
        }

        // ── FIX: rating displayed as float ──
        String ratingStr = film.getRating() > 0
            ? "  ·  ★ " + (film.getRating() == Math.floor(film.getRating())
                ? String.valueOf((int) film.getRating())
                : String.valueOf(film.getRating()))
            : "";
        String meta =
            (film.getDuration() > 0 ? (int) film.getDuration() + " min" : "") +
            (notBlank(film.getAge_rating()) ? "  ·  " + film.getAge_rating() : "") +
            ratingStr;
        Label lblMeta = new Label(meta);
        lblMeta.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11px;");

        VBox card = new VBox(8, poster, lblTitle, lblDir, actorBubbles, chips, lblMeta);
        card.setPadding(new Insets(12));
        String baseStyle = """
            -fx-background-color: #0b1026; -fx-background-radius: 14;
            -fx-border-color: rgba(37,99,235,0.18); -fx-border-radius: 14; -fx-cursor: hand;
            """;
        card.setStyle(baseStyle);
        card.setOnMouseEntered(e -> card.setStyle(baseStyle + "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.45), 24, 0, 0, 6);"));
        card.setOnMouseExited(e  -> card.setStyle(baseStyle));
        return card;
    }

    private HBox buildActorBubbles(int filmId) {
        HBox row = new HBox(-8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(2, 0, 0, 0));

        if (filmId <= 0) return row;

        List<JStream.entity.Actor> actors = actorService.getActorsByFilm(filmId);
        int shown = Math.min(actors.size(), 3);
        for (int i = 0; i < shown; i++) {
            JStream.entity.Actor a = actors.get(i);
            ImageView iv = new ImageView();
            iv.setFitWidth(24); iv.setFitHeight(24);
            iv.setPreserveRatio(false);
            loadImage(iv, a.getPhotoUrl());

            javafx.scene.shape.Circle clipC = new javafx.scene.shape.Circle(12, 12, 12);
            iv.setClip(clipC);

            StackPane bubble = new StackPane(iv);
            bubble.setPrefSize(24, 24);
            bubble.setMinSize(24, 24);
            bubble.setMaxSize(24, 24);
            bubble.setStyle(
                "-fx-background-color: rgba(37,99,235,0.25);" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #0b1026; -fx-border-width: 1.5; -fx-border-radius: 12;"
            );

            Tooltip.install(bubble, styledTooltip(a.getName() + (notBlank(a.getRoleName()) ? " — " + a.getRoleName() : "")));
            row.getChildren().add(bubble);
        }

        if (actors.size() > 3) {
            Label more = new Label("+" + (actors.size() - 3));
            more.setStyle(
                "-fx-text-fill: #60a5fa; -fx-font-size: 9px; -fx-font-weight: bold;" +
                "-fx-padding: 0 0 0 12;"
            );
            row.getChildren().add(more);
        }
        return row;
    }

    private Tooltip styledTooltip(String text) {
        Tooltip tt = new Tooltip(text);
        tt.setStyle(
            "-fx-background-color: #111827; -fx-text-fill: #e2e8f0;" +
            "-fx-font-size: 11px; -fx-background-radius: 8; -fx-padding: 6 10;"
        );
        return tt;
    }

    // ── Delete dialog ─────────────────────────────────────────────────────────
    private void showDeleteDialog(Film film) {
        Stage popup = new Stage();
        popup.initOwner(scrollPane.getScene().getWindow());
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);

        VBox backdrop = new VBox();
        backdrop.setAlignment(Pos.CENTER);
        backdrop.setStyle("-fx-background-color: rgba(0,0,0,0.60);");

        VBox card = new VBox(24);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(400);
        card.setPadding(new Insets(36, 40, 36, 40));
        card.setStyle("""
            -fx-background-color: #0f172a; -fx-background-radius: 20;
            -fx-border-color: rgba(239,68,68,0.25); -fx-border-radius: 20; -fx-border-width: 1;
            """);

        Label iconCircle = new Label("✕");
        iconCircle.setStyle("""
            -fx-background-color: rgba(239,68,68,0.12); -fx-text-fill: #f87171;
            -fx-font-size: 20px; -fx-font-weight: bold;
            -fx-min-width: 60; -fx-min-height: 60; -fx-background-radius: 30;
            """);
        iconCircle.setAlignment(Pos.CENTER);

        Label title    = new Label("Delete Film");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
        Label filmName = new Label("\"" + film.getTitle() + "\"");
        filmName.setStyle("-fx-text-fill: #f87171; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label msg = new Label("This film will be permanently removed.\nThis action cannot be undone.");
        msg.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px; -fx-text-alignment: center;");
        msg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        msg.setWrapText(true);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.07);");

        Button cancelBtn = dialogBtn("Cancel",      "rgba(255,255,255,0.07)", "#94a3b8");
        Button deleteBtn = dialogBtn("Delete Film",  C_DANGER,                "white");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(deleteBtn.getStyle().replace(C_DANGER, "#b91c1c")));
        deleteBtn.setOnMouseExited(e  -> deleteBtn.setStyle(deleteBtn.getStyle().replace("#b91c1c", C_DANGER)));

        HBox btns = new HBox(12, cancelBtn, deleteBtn);
        btns.setAlignment(Pos.CENTER);

        card.getChildren().addAll(iconCircle, title, filmName, msg, sep, btns);
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
            filmService.deleteFilm(film.getFilm_id());
            loadFilms(filmService.getAllFilms());
            if (editingFilm != null && editingFilm.getFilm_id() == film.getFilm_id()) setAddMode();
            showToast("Film deleted ✓", false);
            popup.close();
        });

        popup.showAndWait();
    }

    // ── Validation ────────────────────────────────────────────────────────────
    private boolean validateForm() {
        clearValidation();
        List<String> errors = new ArrayList<>();
        if (isBlank(titleField.getText()))   errors.add("Title required");
        if (isBlank(synopsisArea.getText())) errors.add("Synopsis required");
        if (isBlank(coverField.getText()))   errors.add("Cover image required");
        if (selectedCatIds.isEmpty())        errors.add("Select at least one category");
        if (!isBlank(durationField.getText())) {
            try { Double.parseDouble(durationField.getText().trim()); }
            catch (NumberFormatException e) { errors.add("Duration must be a number"); }
        }
        // ── FIX: validate rating as float 0.0–5.0 ──
        if (!isBlank(ratingField.getText())) {
            try {
                float r = Float.parseFloat(ratingField.getText().trim());
                if (r < 0f || r > 5f) errors.add("Rating must be between 0 and 5");
            } catch (NumberFormatException e) { errors.add("Rating must be a number (e.g. 4.5)"); }
        }
        if (!errors.isEmpty()) {
            showValidationMsg("⚠  " + String.join("  ·  ", errors));
            shakeForm();
            return false;
        }
        return true;
    }

    private void shakeForm() {
        TranslateTransition shake = new TranslateTransition(Duration.millis(55), formPanel);
        shake.setFromX(-7); shake.setToX(7); shake.setCycleCount(5); shake.setAutoReverse(true);
        shake.play();
    }
    private void showValidationMsg(String msg) { validationLabel.setText(msg); validationLabel.setVisible(true);  validationLabel.setManaged(true);  }
    private void clearValidation()              { validationLabel.setText(""); validationLabel.setVisible(false); validationLabel.setManaged(false); }

    // ── Form helpers ──────────────────────────────────────────────────────────
    private Film buildFilmFromForm() {
        Film film = new Film();
        populateFilmFromForm(film);
        return film;
    }

    private void populateFilmFromForm(Film film) {
        film.setTitle(titleField.getText().trim());
        film.setDirector(directorField.getText().trim());
        film.setSynopsis(synopsisArea.getText().trim());
        film.setCasting(castingField.getText().trim());
        film.setAge_rating(ageRatingField.getText().trim());
        film.setVideo_url(videoField.getText().trim());
        film.setTrailer_url(trailerField.getText().trim());
        film.setImage_url(coverField.getText().trim());
        film.setTitle_image_url(titleImageField.getText().trim());
        film.setPoster_url(posterField.getText().trim());
        film.setPosterV_url(posterVField.getText().trim());
        try { film.setDuration(Double.parseDouble(durationField.getText().trim())); }
        catch (Exception ignored) { film.setDuration(0); }
        // ── FIX: parse rating as float ──
        try { film.setRating(Float.parseFloat(ratingField.getText().trim())); }
        catch (Exception ignored) { film.setRating(0f); }
        film.setRelease_date(releaseDatePicker.getValue() != null
            ? releaseDatePicker.getValue().atStartOfDay() : null);
        List<Category> selected = new ArrayList<>();
        for (Category c : allCategories)
            if (selectedCatIds.contains(c.getCategory_id())) selected.add(c);
        film.setCategories(selected);
    }

    private void clearFields() {
        titleField.clear(); directorField.clear(); durationField.clear();
        ageRatingField.clear(); ratingField.clear(); synopsisArea.clear();
        castingField.clear(); videoField.clear(); trailerField.clear();
        coverField.clear(); titleImageField.clear(); posterField.clear();
        posterVField.clear(); releaseDatePicker.setValue(null);
        selectedCatIds.clear();
        rebuildCategoryChips();
        clearValidation();
    }

    // ── Image loading ─────────────────────────────────────────────────────────
    private void loadImage(ImageView iv, String path) {
        if (path == null || path.isBlank()) return;
        try {
            Image img;
            if (path.startsWith("http") || path.startsWith("file:")) {
                img = new Image(path, true);
            } else {
                File f = new File(path);
                if (f.exists()) {
                    img = new Image(f.toURI().toString(), true);
                } else {
                    var res = getClass().getResource(path);
                    if (res != null) img = new Image(res.toExternalForm(), true);
                    else return;
                }
            }
            iv.setImage(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── UI helpers ────────────────────────────────────────────────────────────
    private Button overlayBtn(String text, String bg) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: white; -fx-font-size: 12px;" +
                   "-fx-padding: 7 22; -fx-background-radius: 22; -fx-cursor: hand; -fx-min-width: 130;");
        return b;
    }

    private Button dialogBtn(String text, String bg, String fg) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + ";" +
                   "-fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 11 28;" +
                   "-fx-background-radius: 12; -fx-cursor: hand; -fx-border-width: 0;");
        return b;
    }

    private void animateFormHighlight() {
        FadeTransition ft = new FadeTransition(Duration.millis(220), formPanel);
        ft.setFromValue(0.45); ft.setToValue(1.0); ft.play();
    }

    private void showToast(String msg, boolean success) {
        System.out.println("[JStream] " + msg);
        if (scrollPane == null || scrollPane.getScene() == null) return;
        javafx.scene.Parent root = scrollPane.getScene().getRoot();
        if (!(root instanceof StackPane sp)) return;

        Label toast = new Label("  " + msg + "  ");
        toast.setStyle("""
            -fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 13px;
            -fx-font-weight: bold; -fx-padding: 12 22; -fx-background-radius: 30;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 14, 0, 0, 4);
            """.formatted(success
                ? "linear-gradient(to right,#059669,#10b981)"
                : "linear-gradient(to right,#dc2626,#ef4444)"));

        StackPane.setAlignment(toast, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(toast, new Insets(0, 24, 24, 0));
        toast.setOpacity(0);
        sp.getChildren().add(toast);

        FadeTransition      fadeIn  = new FadeTransition(Duration.millis(250), toast);
        TranslateTransition up      = new TranslateTransition(Duration.millis(250), toast);
        PauseTransition     hold    = new PauseTransition(Duration.millis(2200));
        FadeTransition      fadeOut = new FadeTransition(Duration.millis(300), toast);
        fadeIn.setToValue(1); up.setFromY(12); up.setToY(0);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> sp.getChildren().remove(toast));
        new SequentialTransition(new ParallelTransition(fadeIn, up), hold, fadeOut).play();
    }

    private String nvl(String s)       { return s == null ? "" : s; }
    private boolean isBlank(String s)  { return s == null || s.isBlank(); }
    private boolean notBlank(String s) { return s != null && !s.isBlank(); }
}