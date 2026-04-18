package JStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

public class application extends Application {

    private long lastEscPressTime = 0;

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("view/fxml/Raksha.fxml"));
            Parent root = loader.load();
            javafx.geometry.Rectangle2D screenBounds =
                javafx.stage.Screen.getPrimary().getVisualBounds();

            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

            // ---------------- FULLSCREEN ----------------
            stage.setTitle("Raksha");
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(null);
            stage.setMinWidth(1000);
            stage.setMinHeight(600);
            stage.getIcons().add(
                new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/assets/images/logo/R.png")
                )
            );

            // Bind root size to scene so layout always fills the window
            if (root instanceof javafx.scene.layout.Region region) {
                region.prefWidthProperty().bind(scene.widthProperty());
                region.prefHeightProperty().bind(scene.heightProperty());
            }
            // ---------------- DOUBLE ESC EXIT ----------------
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {

                    long currentTime = System.currentTimeMillis();

                    if (currentTime - lastEscPressTime < 500) {
                        // ✅ pressed twice → exit
                        stage.close();
                    } else {
                        // ⏱ first press → store time
                        lastEscPressTime = currentTime;

                        System.out.println("Press ESC again to exit...");
                    }
                }
            });

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.setProperty("javafx.fxml.ignoreVersionMismatch", "true");
        launch(args);
    }
}