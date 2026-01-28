package JStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class application extends Application {
	@Override
    public void start(Stage stage) throws Exception {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getClassLoader().getResource("view/fxml/e.fxml"));
		Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setMaximized(true); // full laptop screen
        stage.show();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}