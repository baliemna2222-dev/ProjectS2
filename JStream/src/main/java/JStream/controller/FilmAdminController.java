package JStream.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import JStream.entity.Film;
import JStream.entity.Category;
import JStream.service.FilmService;
import JStream.service.FeaturedService;

public class FilmAdminController {

    // ── FXML Fields ───────────────────────────────────────────────────────────
    @FXML private TextField          titleField;
    @FXML private TextField          directorField;
    @FXML private TextField          durationField;
    @FXML private TextField          ageRatingField;
    @FXML private TextField          ratingField;
    @FXML private DatePicker         releaseDatePicker;
    @FXML private TextArea           synopsisArea;
    @FXML private TextField          castingField;
    @FXML private TextField          videoField;
    @FXML private TextField          trailerField;
    @FXML private TextField          coverField;
    @FXML private TextField          titleImageField;
    @FXML private TextField          posterField;
    @FXML private TextField          posterVField;
    @FXML private TextField          searchField;
    @FXML private ListView<Category> categoryListView;
    @FXML private TilePane           filmsContainer;
    @FXML private ScrollPane         scrollPane;
    @FXML private Label              formTitle;
    @FXML private Button             submitBtn;
    @FXML private Button             cancelEditBtn;
    @FXML private Label              filmCountLabel;
    @FXML private Label              validationLabel;
    @FXML private VBox               formPanel;
    @FXML private VBox               rootContainer;

    // ── State ─────────────────────────────────────────────────────────────────
    private Film            editingFilm = null;
    private FilmService     filmService;
    private FeaturedService featuredService;

    // ── Constructor ───────────────────────────────────────────────────────────
    public FilmAdminController() {
        this.filmService = new FilmService();
        try {
            this.featuredService = new FeaturedService();
        } catch (Exception e) {
            System.err.println("FeaturedService init error: " + e.getMessage());
        }
    }

    // ── Init ──────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        categoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        categoryListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        
        loadCategories();
        loadFilms(filmService.getAllFilms());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterFilms(newVal));

        setAddMode();
    }

    // ── Mode switching ────────────────────────────────────────────────────────
    private void setAddMode() {
        editingFilm = null;
        formTitle.setText("✦ Add New Film");
        submitBtn.setText("Add Film");
        submitBtn.setStyle("");
        cancelEditBtn.setVisible(false);
        cancelEditBtn.setManaged(false);
        clearFields();
    }

    private void setEditMode(Film film) {
        editingFilm = film;
        formTitle.setText("✎ Editing: " + film.getTitle());
        submitBtn.setText("Save Changes");
        cancelEditBtn.setVisible(true);
        cancelEditBtn.setManaged(true);

        // Populate text fields
        titleField.setText(nvl(film.getTitle()));
        directorField.setText(nvl(film.getDirector()));
        durationField.setText(film.getDuration() > 0 ? String.valueOf((int) film.getDuration()) : "");
        ageRatingField.setText(nvl(film.getAge_rating()));
        ratingField.setText(film.getRating() > 0 ? String.valueOf(film.getRating()) : "");
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

        
        selectFilmCategories(film);
        

        scrollPane.setVvalue(0);
        animateFormHighlight();
    }
    private void selectFilmCategories(Film film) {
        if (film.getCategories() == null || film.getCategories().isEmpty()) return;

        // Create ID set
        final java.util.Set<Integer> ids = new java.util.HashSet<>();
        for (Category c : film.getCategories()) {
            ids.add(c.getCategory_id());
        }

        // Retry mechanism (VERY important)
        Platform.runLater(() -> {
            ObservableList<Category> items = categoryListView.getItems();

            // If items not loaded yet → retry again
            if (items == null || items.isEmpty()) {
                Platform.runLater(() -> selectFilmCategories(film));
                return;
            }

            MultipleSelectionModel<Category> sm = categoryListView.getSelectionModel();
            sm.clearSelection();

            for (int i = 0; i < items.size(); i++) {
                if (ids.contains(items.get(i).getCategory_id())) {
                    sm.select(i);
                }
            }
        });
    }

    // ── FXML Actions ──────────────────────────────────────────────────────────
    @FXML
    private void handleSubmit() {
        if (!validateForm()) return;
        if (editingFilm == null) doAddFilm();
        else                     doUpdateFilm();
    }

    @FXML
    private void cancelEdit() {
        setAddMode();
    }

    private void doAddFilm() {
        clearValidation();
        Film film = buildFilmFromForm();
        filmService.addFilm(film);
        showToast("Film added successfully ✓");
        loadFilms(filmService.getAllFilms());
        setAddMode();
    }

    private void doUpdateFilm() {
        clearValidation();
        populateFilmFromForm(editingFilm);
        filmService.updateFilm(editingFilm);
        showToast("Film updated successfully ✓");
        loadFilms(filmService.getAllFilms());
        setAddMode();
    }

    // ── File choosers ─────────────────────────────────────────────────────────
    @FXML private void chooseVideoFile()      { chooseFile(videoField,      "Choose Video",       "Video Files", "*.mp4","*.mov","*.avi","*.mkv"); }
    @FXML private void chooseTrailerFile()    { chooseFile(trailerField,    "Choose Trailer",     "Video Files", "*.mp4","*.mov","*.avi","*.mkv"); }
    @FXML private void chooseCoverFile()      { chooseFile(coverField,      "Choose Cover Image", "Images",      "*.png","*.jpg","*.jpeg","*.gif","*.webp"); }
    @FXML private void chooseTitleImageFile() { chooseFile(titleImageField, "Choose Title Image", "Images",      "*.png","*.jpg","*.jpeg","*.gif","*.webp"); }
    @FXML private void choosePosterFile()     { chooseFile(posterField,     "Choose Poster (H)",  "Images",      "*.png","*.jpg","*.jpeg","*.gif","*.webp"); }
    @FXML private void choosePosterVFile()    { chooseFile(posterVField,    "Choose Poster (V)",  "Images",      "*.png","*.jpg","*.jpeg","*.gif","*.webp"); }

    private void chooseFile(TextField target, String dialogTitle, String desc, String... exts) {
        if (target == null || target.getScene() == null) return;
        FileChooser fc = new FileChooser();
        fc.setTitle(dialogTitle);
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(desc, exts));
        File f = fc.showOpenDialog(target.getScene().getWindow());
        if (f != null) target.setText(f.getAbsolutePath());
    }

    // ── Load / filter ─────────────────────────────────────────────────────────
    private void loadCategories() {
        if (featuredService != null)
            categoryListView.getItems().setAll(featuredService.getAllCategories());
    }

    private void loadFilms(List<Film> films) {
        filmsContainer.getChildren().clear();
        filmCountLabel.setText(films.size() + " film" + (films.size() != 1 ? "s" : ""));
        for (int i = 0; i < films.size(); i++) {
            VBox card = createFilmCard(films.get(i));
            card.setOpacity(0);
            filmsContainer.getChildren().add(card);
            int delay = i * 40;
            PauseTransition pause   = new PauseTransition(Duration.millis(delay));
            FadeTransition  fade    = new FadeTransition(Duration.millis(300), card);
            fade.setFromValue(0); fade.setToValue(1);
            TranslateTransition slide = new TranslateTransition(Duration.millis(300), card);
            slide.setFromY(20); slide.setToY(0);
            pause.setOnFinished(e -> new ParallelTransition(fade, slide).play());
            pause.play();
        }
    }

    private void filterFilms(String keyword) {
        if (keyword == null || keyword.isBlank()) loadFilms(filmService.getAllFilms());
        else                                      loadFilms(filmService.searchFilms(keyword.trim()));
    }

    // ── Card builder ──────────────────────────────────────────────────────────
    private VBox createFilmCard(Film film) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(false);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 4);");
        loadImage(imageView, film.getPoster_url());

        StackPane posterPane = new StackPane(imageView);
        posterPane.setPrefSize(180, 250);

        VBox overlay = new VBox(6);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.75); -fx-background-radius: 10;");
        overlay.setOpacity(0);

        Button editBtn = iconButton("✎  Edit",   "#0ea5e9");
        Button delBtn  = iconButton("✕  Delete", "#374151");
        editBtn.setOnAction(e -> setEditMode(film));
        delBtn.setOnAction(e  -> confirmDelete(film));

        overlay.getChildren().addAll(editBtn, delBtn);
        posterPane.getChildren().add(overlay);

        FadeTransition fadeIn  = new FadeTransition(Duration.millis(180), overlay);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(180), overlay);
        posterPane.setOnMouseEntered(e -> { fadeIn.setFromValue(overlay.getOpacity());  fadeIn.setToValue(1);  fadeIn.play(); });
        posterPane.setOnMouseExited(e  -> { fadeOut.setFromValue(overlay.getOpacity()); fadeOut.setToValue(0); fadeOut.play(); });

        Label title = new Label(film.getTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-wrap-text: true; -fx-max-width: 180;");

        Label director = new Label(film.getDirector() != null ? "Dir. " + film.getDirector() : "");
        director.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 11px;");

        FlowPane chips = new FlowPane();
        chips.setHgap(5); chips.setVgap(5);
        chips.setPrefWrapLength(180);
        if (film.getCategories() != null) {
            for (Category c : film.getCategories()) {
                Label chip = new Label(c.getName());
                chip.setStyle("-fx-background-color: rgba(29,78,216,0.25); -fx-text-fill: #60a5fa; " +
                              "-fx-padding: 2 8; -fx-background-radius: 20; -fx-font-size: 10px;");
                chips.getChildren().add(chip);
            }
        }

        Label meta = new Label(
            (film.getDuration() > 0 ? (int) film.getDuration() + " min" : "") +
            (film.getAge_rating() != null && !film.getAge_rating().isBlank() ? "  ·  " + film.getAge_rating() : "") +
            (film.getRating() > 0 ? "  ·  ★ " + film.getRating() : "")
        );
        meta.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11px;");

        VBox card = new VBox(8, posterPane, title, director, chips, meta);
        card.setStyle("""
            -fx-background-color: #080d1a;
            -fx-padding: 12;
            -fx-background-radius: 12;
            -fx-border-color: rgba(29,78,216,0.15);
            -fx-border-radius: 12;
            -fx-cursor: hand;
            """);

        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() +
            "-fx-effect: dropshadow(gaussian, rgba(29,78,216,0.40), 22, 0, 0, 6);"));
        card.setOnMouseExited(e  -> card.setStyle(card.getStyle()
            .replace("-fx-effect: dropshadow(gaussian, rgba(29,78,216,0.40), 22, 0, 0, 6);", "")));

        return card;
    }

    private void confirmDelete(Film film) {
        Stage popup = new Stage();
        popup.initOwner(scrollPane.getScene().getWindow());
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        popup.initStyle(javafx.stage.StageStyle.UNDECORATED);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.65);");
        root.setPrefSize(400, 220);

        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setStyle("""
            -fx-background-color: linear-gradient(to bottom right, #0f172a, #111827);
            
            -fx-padding: 26;
            -fx-border-color: rgba(255,255,255,0.08);
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0, 0, 10);
        """);

        Label title = new Label("Delete Film?");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label msg = new Label("Are you sure you want to delete:\n" + film.getTitle());
        msg.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px; -fx-text-alignment: center;");

        Button deleteBtn = new Button("Delete");
        Button cancelBtn = new Button("Cancel");

        deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 20; -fx-cursor: hand;");
        cancelBtn.setStyle("-fx-background-color: #1f2937; -fx-text-fill: #e5e7eb; -fx-background-radius: 10; -fx-padding: 8 20; -fx-cursor: hand;");

        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 20;"));
        deleteBtn.setOnMouseExited(e  -> deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 20;"));

        HBox buttons = new HBox(12, cancelBtn, deleteBtn);
        buttons.setAlignment(Pos.CENTER);

        card.getChildren().addAll(title, msg, buttons);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 420, 240);
        popup.setScene(scene);
        popup.centerOnScreen();

        card.setScaleX(0.8); card.setScaleY(0.8); card.setOpacity(0);
        FadeTransition  fade  = new FadeTransition(Duration.millis(180), card);
        ScaleTransition scale = new ScaleTransition(Duration.millis(180), card);
        fade.setFromValue(0);  fade.setToValue(1);
        scale.setFromX(0.8); scale.setFromY(0.8); scale.setToX(1); scale.setToY(1);
        new ParallelTransition(fade, scale).play();

        cancelBtn.setOnAction(e -> popup.close());
        deleteBtn.setOnAction(e -> {
            filmService.deleteFilm(film.getFilm_id());
            loadFilms(filmService.getAllFilms());
            showToast("Film deleted ✓");
            if (editingFilm != null && editingFilm.getFilm_id() == film.getFilm_id())
                setAddMode();
            popup.close();
        });

        popup.showAndWait();
    }

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
        catch (NumberFormatException ignored) { film.setDuration(0); }

        try { film.setRating(Integer.parseInt(ratingField.getText().trim())); }
        catch (NumberFormatException ignored) { film.setRating(0); }

        if (releaseDatePicker.getValue() != null)
            film.setRelease_date(releaseDatePicker.getValue().atStartOfDay());
        else
            film.setRelease_date(null);

        ObservableList<Category> sel = categoryListView.getSelectionModel().getSelectedItems();
        film.setCategories(new ArrayList<>(sel));
    }

    private boolean validateForm() {
        clearValidation();
        List<String> errors = new ArrayList<>();

        if (titleField.getText() == null || titleField.getText().isBlank())
            errors.add("Title is required");
        if (synopsisArea.getText() == null || synopsisArea.getText().isBlank())
            errors.add("Synopsis is required");
        if (coverField.getText() == null || coverField.getText().isBlank())
            errors.add("Cover image is required");
        if (categoryListView.getSelectionModel().getSelectedItems() == null ||
            categoryListView.getSelectionModel().getSelectedItems().isEmpty())
            errors.add("At least one category must be selected");

        if (!errors.isEmpty()) {
            showValidation("⚠  " + String.join("  ·  ", errors));
            TranslateTransition shake = new TranslateTransition(Duration.millis(60), formPanel);
            shake.setFromX(-6); shake.setToX(6);
            shake.setCycleCount(4); shake.setAutoReverse(true);
            shake.play();
            return false;
        }
        return true;
    }

    private void showValidation(String msg) {
        validationLabel.setText(msg);
        validationLabel.setVisible(true);
        validationLabel.setManaged(true);
    }

    private void clearValidation() {
        validationLabel.setText("");
        validationLabel.setVisible(false);
        validationLabel.setManaged(false);
    }

    private void clearFields() {
        titleField.clear();
        directorField.clear();
        durationField.clear();
        ageRatingField.clear();
        ratingField.clear();
        synopsisArea.clear();
        castingField.clear();
        videoField.clear();
        trailerField.clear();
        coverField.clear();
        titleImageField.clear();
        posterField.clear();
        posterVField.clear();
        releaseDatePicker.setValue(null);
        categoryListView.getSelectionModel().clearSelection();
        clearValidation();
    }

    private void loadImage(ImageView iv, String path) {
        if (path == null || path.isEmpty()) return;
        try {
            Image img;
            File f = new File(path);
            if (path.startsWith("http"))  img = new Image(path, true);
            else if (f.exists())          img = new Image(f.toURI().toString(), true);
            else                          img = new Image(getClass().getResource(path).toExternalForm(), true);
            iv.setImage(img);
        } catch (Exception ignored) {}
    }

    private Button iconButton(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 12px; " +
                   "-fx-padding: 6 20; -fx-background-radius: 20; -fx-cursor: hand; -fx-min-width: 120;");
        return b;
    }

    private void animateFormHighlight() {
        FadeTransition ft = new FadeTransition(Duration.millis(200), formPanel);
        ft.setFromValue(0.5); ft.setToValue(1.0); ft.play();
    }

    private void showToast(String msg) {
        System.out.println("[JStream] " + msg);
    }

    private String nvl(String s) { return s == null ? "" : s; }
}