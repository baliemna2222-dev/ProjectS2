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
import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import JStream.entity.Film;
import JStream.entity.Category;
import JStream.service.FilmService;
import JStream.service.FeaturedService;

public class FilmAdminController {

    // ── FXML Fields ──────────────────────────────────────────────────────────
    @FXML private TextField      titleField;
    @FXML private TextField      durationField;
    @FXML private TextField      ageRatingField;
    @FXML private DatePicker     releaseDatePicker;
    @FXML private TextArea       synopsisArea;
    @FXML private TextField      castingField;
    @FXML private TextField      videoField;
    @FXML private TextField      coverField;
    @FXML private TextField      titleImageField;
    @FXML private TextField      posterField;
    @FXML private TextField      trailerField;
    @FXML private TextField      searchField;
    @FXML private ListView<Category> categoryListView;
    @FXML private TilePane       filmsContainer;
    @FXML private ScrollPane     scrollPane;
    @FXML private Label          formTitle;
    @FXML private Button         submitBtn;
    @FXML private Button         cancelEditBtn;
    @FXML private Label          filmCountLabel;
    @FXML private VBox           formPanel;

    // ── State ─────────────────────────────────────────────────────────────────
    private Film         editingFilm = null;   // null = "add" mode
    private FilmService  filmService;
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
            @Override protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        // Live search
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterFilms(newVal));

        loadCategories();
        loadFilms(filmService.getAllFilms());
        setAddMode();
    }

    // ── Mode switching ────────────────────────────────────────────────────────
    private void setAddMode() {
        editingFilm = null;
        formTitle.setText("✦ Add New Film");
        submitBtn.setText("Add Film");
        submitBtn.setStyle(submitBtn.getStyle().replace("#3b82f6", "#E50914") + "; -fx-background-color: #E50914;");
        cancelEditBtn.setVisible(false);
        clearFields();
    }

    private void setEditMode(Film film) {
        editingFilm = film;
        formTitle.setText("✎ Editing: " + film.getTitle());
        submitBtn.setText("Save Changes");
        cancelEditBtn.setVisible(true);

        // Populate fields
        titleField.setText(nvl(film.getTitle()));
        durationField.setText(film.getDuration() > 0 ? String.valueOf((int) film.getDuration()) : "");
        ageRatingField.setText(nvl(film.getAge_rating()));
        synopsisArea.setText(nvl(film.getSynopsis()));
        castingField.setText(nvl(film.getCasting()));
        videoField.setText(nvl(film.getVideo_url()));
        coverField.setText(nvl(film.getImage_url()));
        titleImageField.setText(nvl(film.getTitle_image_url()));
        posterField.setText(nvl(film.getPoster_url()));
        trailerField.setText(nvl(film.getTrailer_url()));

        if (film.getRelease_date() != null)
            releaseDatePicker.setValue(film.getRelease_date().toLocalDate());
        else
            releaseDatePicker.setValue(null);

        // Re-select categories
        categoryListView.getSelectionModel().clearSelection();
        if (film.getCategories() != null) {
            ObservableList<Category> items = categoryListView.getItems();
            for (int i = 0; i < items.size(); i++) {
                for (Category sel : film.getCategories()) {
                    if (items.get(i).getCategory_id() == sel.getCategory_id())
                        categoryListView.getSelectionModel().select(i);
                }
            }
        }

        // Scroll to form
        scrollPane.setVvalue(0);
        animateFormHighlight();
    }

    // ── FXML Actions ──────────────────────────────────────────────────────────
    @FXML
    private void handleSubmit() {
        if (editingFilm == null) addFilm();
        else                     updateFilm();
    }

    @FXML
    private void cancelEdit() {
        setAddMode();
    }

    @FXML
    private void addFilm() {
        Film film = buildFilmFromForm();
        filmService.addFilm(film);
        showToast("Film added successfully ✓");
        loadFilms(filmService.getAllFilms());
        clearFields();
        setAddMode();
    }

    private void updateFilm() {
        populateFilmFromForm(editingFilm);
        filmService.updateFilm(editingFilm);
        showToast("Film updated successfully ✓");
        loadFilms(filmService.getAllFilms());
        setAddMode();
    }

    // ── File choosers ─────────────────────────────────────────────────────────
    @FXML private void chooseVideoFile()      { chooseFile(videoField,      "Choose Video",        "Video Files", "*.mp4","*.mov","*.avi","*.mkv"); }
    @FXML private void chooseCoverFile()      { chooseFile(coverField,      "Choose Cover Image",  "Images",      "*.png","*.jpg","*.jpeg","*.gif","*.webp"); }
    @FXML private void chooseTitleImageFile() { chooseFile(titleImageField, "Choose Title Image",  "Images",      "*.png","*.jpg","*.jpeg","*.gif","*.webp"); }
    @FXML private void choosePosterFile()     { chooseFile(posterField,     "Choose Poster",       "Images",      "*.png","*.jpg","*.jpeg","*.gif","*.webp"); }
    @FXML private void chooseTrailerFile()    { chooseFile(trailerField,    "Choose Trailer",      "Video Files", "*.mp4","*.mov","*.avi","*.mkv"); }

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
            // Staggered fade-in
            int delay = i * 40;
            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            FadeTransition fade  = new FadeTransition(Duration.millis(300), card);
            fade.setFromValue(0); fade.setToValue(1);
            TranslateTransition slide = new TranslateTransition(Duration.millis(300), card);
            slide.setFromY(20); slide.setToY(0);
            pause.setOnFinished(e -> new ParallelTransition(fade, slide).play());
            pause.play();
        }
    }

    private void filterFilms(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            loadFilms(filmService.getAllFilms());
        } else {
            loadFilms(filmService.searchFilms(keyword.trim()));
        }
    }

    // ── Card builder ──────────────────────────────────────────────────────────
    private VBox createFilmCard(Film film) {
        // Poster
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(false);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 4);");
        loadImage(imageView, film.getPoster_url());

        // Hover overlay
        StackPane posterPane = new StackPane(imageView);
        posterPane.setPrefSize(180, 250);

        VBox overlay = new VBox(6);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.75); -fx-background-radius: 10;");
        overlay.setOpacity(0);

        Button playBtn = iconButton("▶  Play",     "#E50914");
        Button editBtn = iconButton("✎  Edit",     "#3b82f6");
        Button delBtn  = iconButton("✕  Delete",   "#6b7280");

        playBtn.setOnAction(e -> playVideo(film.getVideo_url()));
        editBtn.setOnAction(e -> setEditMode(film));
        delBtn.setOnAction(e  -> confirmDelete(film));

        overlay.getChildren().addAll(playBtn, editBtn, delBtn);
        posterPane.getChildren().add(overlay);

        // Hover animation
        FadeTransition fadeIn  = new FadeTransition(Duration.millis(180), overlay);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(180), overlay);
        posterPane.setOnMouseEntered(e -> { fadeIn.setFromValue(overlay.getOpacity()); fadeIn.setToValue(1); fadeIn.play(); });
        posterPane.setOnMouseExited(e  -> { fadeOut.setFromValue(overlay.getOpacity()); fadeOut.setToValue(0); fadeOut.play(); });

        // Title
        Label title = new Label(film.getTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-wrap-text: true; -fx-max-width: 180;");

        // Category chips
        FlowPane chips = new FlowPane();
        chips.setHgap(5); chips.setVgap(5);
        chips.setPrefWrapLength(180);
        if (film.getCategories() != null) {
            for (Category c : film.getCategories()) {
                Label chip = new Label(c.getName());
                chip.setStyle("-fx-background-color: rgba(229,9,20,0.20); -fx-text-fill: #ff6b6b; -fx-padding: 2 8; -fx-background-radius: 20; -fx-font-size: 10px;");
                chips.getChildren().add(chip);
            }
        }

        // Meta row
        Label meta = new Label(
            (film.getDuration() > 0 ? (int) film.getDuration() + "min" : "") +
            (film.getAge_rating() != null ? "  ·  " + film.getAge_rating() : "")
        );
        meta.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11px;");

        VBox card = new VBox(8, posterPane, title, chips, meta);
        card.setStyle("""
            -fx-background-color: #141414;
            -fx-padding: 12;
            -fx-background-radius: 12;
            -fx-border-color: rgba(255,255,255,0.06);
            -fx-border-radius: 12;
            -fx-cursor: hand;
            """);

        // Card hover lift
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(229,9,20,0.25), 20, 0, 0, 6);"));
        card.setOnMouseExited(e  -> card.setStyle(card.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(229,9,20,0.25), 20, 0, 0, 6);", "")));

        return card;
    }

    // ── Delete confirmation ────────────────────────────────────────────────────
    private void confirmDelete(Film film) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Film");
        alert.setHeaderText("Delete \"" + film.getTitle() + "\"?");
        alert.setContentText("This action cannot be undone.");
        alert.getDialogPane().setStyle("-fx-background-color: #1a1a2e; -fx-font-family: 'Segoe UI';");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            filmService.deleteFilm(film.getFilm_id());
            showToast("Film deleted ✓");
            loadFilms(filmService.getAllFilms());
            if (editingFilm != null && editingFilm.getFilm_id() == film.getFilm_id())
                setAddMode();
        }
    }

    // ── Video player ──────────────────────────────────────────────────────────
    private void playVideo(String path) {
        if (path == null || path.isBlank()) { showToast("No video path set."); return; }
        try {
            Media media;
            File f = new File(path);
            if (path.startsWith("http://") || path.startsWith("https://")) media = new Media(path);
            else if (f.exists())                                             media = new Media(f.toURI().toString());
            else                                                             media = new Media(getClass().getResource(path).toExternalForm());

            MediaPlayer player = new MediaPlayer(media);
            MediaView    view  = new MediaView(player);
            view.setFitWidth(900); view.setFitHeight(506);

            // Controls bar
            Button playPause = new Button("⏸");
            Slider  progress = new Slider();
            progress.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(progress, Priority.ALWAYS);
            Label timeLabel = new Label("0:00");
            timeLabel.setStyle("-fx-text-fill: white;");

            player.currentTimeProperty().addListener((obs, o, n) -> {
                if (!progress.isValueChanging())
                    progress.setValue(n.toSeconds() / player.getTotalDuration().toSeconds() * 100);
                int sec = (int) n.toSeconds();
                timeLabel.setText(sec / 60 + ":" + String.format("%02d", sec % 60));
            });
            progress.valueProperty().addListener((obs, o, n) -> {
                if (progress.isValueChanging())
                    player.seek(player.getTotalDuration().multiply(n.doubleValue() / 100));
            });
            playPause.setOnAction(e -> {
                if (player.getStatus() == MediaPlayer.Status.PLAYING) { player.pause(); playPause.setText("▶"); }
                else { player.play(); playPause.setText("⏸"); }
            });

            HBox controls = new HBox(10, playPause, progress, timeLabel);
            controls.setAlignment(Pos.CENTER_LEFT);
            controls.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-padding: 10 16;");

            VBox root = new VBox(view, controls);
            root.setStyle("-fx-background-color: black;");

            Scene scene = new Scene(root, 900, 540);
            Stage stage = new Stage();
            stage.setTitle("▶  " + "Now Playing");
            stage.setScene(scene);
            stage.setOnHidden(e -> player.stop());
            stage.show();
            player.play();

        } catch (Exception e) {
            showToast("Video error: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Film buildFilmFromForm() {
        Film film = new Film();
        populateFilmFromForm(film);
        return film;
    }

    private void populateFilmFromForm(Film film) {
        film.setTitle(titleField.getText());
        film.setSynopsis(synopsisArea.getText());
        film.setCasting(castingField.getText());
        film.setAge_rating(ageRatingField.getText());
        film.setVideo_url(videoField.getText());
        film.setImage_url(coverField.getText());
        film.setTitle_image_url(titleImageField.getText());
        film.setPoster_url(posterField.getText());
        film.setTrailer_url(trailerField.getText());
        try { film.setDuration(Double.parseDouble(durationField.getText())); }
        catch (NumberFormatException ignored) { film.setDuration(0); }
        if (releaseDatePicker.getValue() != null)
            film.setRelease_date(releaseDatePicker.getValue().atStartOfDay());
        ObservableList<Category> sel = categoryListView.getSelectionModel().getSelectedItems();
        film.setCategories(new ArrayList<>(sel));
    }

    private void clearFields() {
        titleField.clear(); durationField.clear(); ageRatingField.clear();
        synopsisArea.clear(); castingField.clear();
        videoField.clear(); coverField.clear(); titleImageField.clear();
        posterField.clear(); trailerField.clear();
        releaseDatePicker.setValue(null);
        categoryListView.getSelectionModel().clearSelection();
    }

    private void loadImage(ImageView iv, String path) {
        if (path == null || path.isEmpty()) return;
        try {
            Image img;
            File f = new File(path);
            if (path.startsWith("http"))       img = new Image(path, true);
            else if (f.exists())               img = new Image(f.toURI().toString(), true);
            else                               img = new Image(getClass().getResource(path).toExternalForm(), true);
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
        ft.setFromValue(0.5); ft.setToValue(1.0); ft.play();
    }

    private void showToast(String msg) {
        // Simple console toast — replace with a real Notification if desired
        System.out.println("[JStream] " + msg);
        // You can upgrade this to a JavaFX Popup/SnackBar component
    }

    private String nvl(String s) { return s == null ? "" : s; }
}