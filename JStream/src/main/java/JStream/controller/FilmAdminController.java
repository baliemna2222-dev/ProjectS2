package JStream.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import JStream.entity.Film;
import JStream.entity.Category;
import JStream.service.FilmService;
import JStream.service.FeaturedService;

public class FilmAdminController {

    // --- Champs de formulaire FXML ---
    @FXML private TextField titleField;
    @FXML private TextField durationField;
    @FXML private TextField ageRatingField;
    @FXML private DatePicker releaseDatePicker;
    
    @FXML private TextField synopsisField;
    @FXML private TextField castingField;
    
    @FXML private TextField videoField;
    @FXML private TextField coverField;
    @FXML private TextField titleImageField;
    @FXML private TextField posterField;
    
    @FXML private ListView<Category> categoryListView; 
    @FXML private TilePane filmsContainer;

    // --- Services ---
    private FilmService filmService;
    private FeaturedService featuredService;

    // 🔥 NEW: Explicit constructor to handle exception properly
    public FilmAdminController() {
        this.filmService = new FilmService();
        try {
            // Attempting to create the FeaturedService
            this.featuredService = new FeaturedService();
        } catch (Exception e) {
            System.out.println("Erreur critique lors de l'initialisation du FeaturedService : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Permet la sélection multiple dans la liste avec Ctrl/Cmd !
        categoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        categoryListView.setCellFactory(list -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        
        loadCategories();
        loadFilms();
    }

    private void loadCategories() {
        // On s'assure que le service n'est pas null avant de l'appeler
        if (featuredService != null) {
            categoryListView.getItems().setAll(featuredService.getAllCategories()); 
        }
    }

    private void loadFilms() {
        filmsContainer.getChildren().clear();
        List<Film> films = filmService.getAllFilms();

        for (Film film : films) {
            VBox card = createFilmCard(film);
            filmsContainer.getChildren().add(card);
        }
    }

    private VBox createFilmCard(Film film) {

        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(250);

        // IMAGE
        String posterPath = film.getPoster_url();
        if (posterPath != null && !posterPath.isEmpty()) {
            try {
                Image img;
                File posterFile = new File(posterPath);
                if (posterPath.startsWith("http://") || posterPath.startsWith("https://")) {
                    img = new Image(posterPath, true);
                } else if (posterFile.exists()) {
                    img = new Image(posterFile.toURI().toString(), true);
                } else {
                    img = new Image(getClass().getResource(posterPath).toExternalForm(), true);
                }
                imageView.setImage(img);
            } catch (Exception e) {
                System.out.println("Image introuvable pour le film : " + film.getTitle());
            }
        }

        // TITLE
        Label title = new Label(film.getTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        // CATEGORIES AS CHIPS
        FlowPane categoryChips = new FlowPane();
        categoryChips.setHgap(6);
        categoryChips.setVgap(6);
        categoryChips.setPrefWrapLength(180);
        if (film.getCategories() != null) {
            for (Category category : film.getCategories()) {
                Label chip = new Label(category.getName());
                chip.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px;");
                categoryChips.getChildren().add(chip);
            }
        }

        // BUTTON PLAY
        Button playBtn = new Button("▶ Play");
        playBtn.setOnAction(e -> playVideo(film.getVideo_url()));

        VBox card = new VBox(8, imageView, title, categoryChips, playBtn);
        card.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 14; -fx-background-radius: 14;");

        return card;
    }

    private void playVideo(String path) {
        if (path == null || path.isEmpty()) {
            System.out.println("Aucun chemin vidéo spécifié.");
            return;
        }

        try {
            Media media;
            File videoFile = new File(path);
            if (path.startsWith("http://") || path.startsWith("https://")) {
                media = new Media(path);
            } else if (videoFile.exists()) {
                media = new Media(videoFile.toURI().toString());
            } else {
                media = new Media(getClass().getResource(path).toExternalForm());
            }

            MediaPlayer player = new MediaPlayer(media);
            MediaView mediaView = new MediaView(player);

            mediaView.setFitWidth(800);
            mediaView.setFitHeight(600);

            Stage stage = new Stage();
            StackPane root = new StackPane(mediaView);

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Lecture vidéo");
            stage.show();

            player.play();

        } catch (Exception e) {
            System.out.println("Erreur de lecture vidéo : " + e.getMessage());
        }
    }

    @FXML
    private void chooseVideoFile() {
        chooseFile(videoField, "Choisir une vidéo", new FileChooser.ExtensionFilter("Fichiers vidéo", "*.mp4", "*.mov", "*.avi", "*.mkv"));
    }

    @FXML
    private void chooseCoverFile() {
        chooseFile(coverField, "Choisir une image de couverture", new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
    }

    @FXML
    private void chooseTitleImageFile() {
        chooseFile(titleImageField, "Choisir une image de titre", new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
    }

    @FXML
    private void choosePosterFile() {
        chooseFile(posterField, "Choisir une affiche", new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
    }

    private void chooseFile(TextField targetField, String title, FileChooser.ExtensionFilter... filters) {
        if (targetField == null || targetField.getScene() == null) {
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().setAll(filters);

        File selectedFile = chooser.showOpenDialog(targetField.getScene().getWindow());
        if (selectedFile != null) {
            targetField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void addFilm() {
        Film film = new Film();
        
        // 1. Textes simples
        film.setTitle(titleField.getText());
        film.setSynopsis(synopsisField.getText());
        film.setCasting(castingField.getText());
        film.setAge_rating(ageRatingField.getText());
        
        // 2. URLs (Médias)
        film.setVideo_url(videoField.getText());
        film.setImage_url(coverField.getText());
        film.setTitle_image_url(titleImageField.getText());
        film.setPoster_url(posterField.getText());

        // 3. Durée
        try {
            film.setDuration(Double.parseDouble(durationField.getText()));
        } catch (NumberFormatException e) {
            film.setDuration(0.0);
        }

        // 4. Date de sortie
        if (releaseDatePicker.getValue() != null) {
            film.setRelease_date(releaseDatePicker.getValue().atStartOfDay());
        }

        // 5. Catégories sélectionnées
        ObservableList<Category> selectedCategories = categoryListView.getSelectionModel().getSelectedItems();
        film.setCategories(new ArrayList<>(selectedCategories));

        // 6. Sauvegarde
        filmService.addFilm(film);
        
        // 7. Rafraîchissement
        loadFilms();
        clearFields();
    }

    private void clearFields() {
        titleField.clear();
        durationField.clear();
        ageRatingField.clear();
        synopsisField.clear();
        castingField.clear();
        videoField.clear();
        coverField.clear();
        titleImageField.clear();
        posterField.clear();
        releaseDatePicker.setValue(null);
        categoryListView.getSelectionModel().clearSelection();
    }
}