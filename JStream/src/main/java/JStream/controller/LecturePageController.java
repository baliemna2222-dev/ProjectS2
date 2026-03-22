package JStream.controller;

import JStream.entity.Episode;
import JStream.entity.Film;
import JStream.entity.Serie;
import JStream.entity.Season;
import JStream.service.FeaturedService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.sql.SQLException;

public class LecturePageController {

    @FXML private Label titleLabel;
    @FXML private ImageView posterImage;
    @FXML private Label descriptionLabel;
    @FXML private Label durationLabel;
    @FXML private HBox starsBox;
    @FXML private Label categoriesLabel;
    @FXML private Label episodeInfoLabel; // Only for episodes
    @FXML private Button playTrailerButton;

    // ---------------- Init Data for Film ----------------
    public void initFilm(int filmId) {
        episodeInfoLabel.setVisible(false); // hide for films
        try {
            Film film = new FeaturedService().getFilmDetails(filmId);
            titleLabel.setText(film.getTitle());
           
            descriptionLabel.setText(film.getSynopsis());
            durationLabel.setText(film.getDuration() + " min");
            categoriesLabel.setText(film.getCasting());
            populateStars(film.getRating());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------- Init Data for Episode ----------------
    public void initEpisode(int serieId, int seasonNum, int episodeNum) {
        try {
            Serie serie = new FeaturedService().getFullSerie(serieId);
            Episode ep = findEpisodeInSerie(serie, seasonNum, episodeNum);

            titleLabel.setText(ep.getTitle() + " (" + serie.getTitle() + ")");
            
            descriptionLabel.setText(ep.getResume());
            durationLabel.setText(ep.getDuration() + " min");
            episodeInfoLabel.setText("Season " + ep.getSeasonId() + " - Episode " + ep.getNumEpisode());
            episodeInfoLabel.setVisible(true);
            categoriesLabel.setText(serie.getCategoriesAsString());
            populateStars(serie.getRating());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateStars(int rating) {
        starsBox.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            Label star = new Label("★");
            star.setTextFill(i < rating ? Color.DEEPSKYBLUE : Color.GRAY);
            starsBox.getChildren().add(star);
        }
    }

    private Episode findEpisodeInSerie(Serie serie, int seasonNum, int episodeNum) {
        for (Season s : serie.getSeasons()) {
            if (s.getSeasonNum() == seasonNum) {
                for (Episode ep : s.getEpisodes()) {
                    if (ep.getNumEpisode() == episodeNum) return ep;
                }
            }
        }
        return null;
    }
}