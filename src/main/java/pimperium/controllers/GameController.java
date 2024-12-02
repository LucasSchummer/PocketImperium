package pimperium.controllers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import pimperium.models.Game;
import pimperium.views.Interface;

public class GameController extends Application {
    private Game game;
    private Interface view;

    public void start(Stage primaryStage) {

        game = new Game();
        game.startGame();

        view = new Interface(game);

        // Set up the stage
        Scene scene = new Scene(view.getRoot(), 529, 754);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pocket Imperium");
        primaryStage.show();

    }


    // Add event handlers to handle user interactions
    // e.g., responding to button clicks or key presses

    public static void main(String[] args) {
        launch();
    }
}
