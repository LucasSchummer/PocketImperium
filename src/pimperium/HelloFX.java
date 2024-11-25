package pimperium;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HelloFX extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("Hello, JavaFX!");
        Scene scene = new Scene(label, 320, 240);
        stage.setScene(scene);
        stage.setTitle("JavaFX Hello World");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}