package JStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class application extends Application {
	@Override
    public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getClassLoader().getResource("view/fxml/admin_home.fxml"));
			Parent root = loader.load();   
	        Scene scene = new Scene(root);

	        stage.setTitle("Login");
	        stage.setScene(scene);
	        stage.setMaximized(true);
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