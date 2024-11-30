package pimperium;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class GameController extends Application {
    private Game game;
    private Interface view;

    public void start(Stage primaryStage) {
        game = new Game();
        view = new Interface();

        // Add event handlers for imageViews
        ImageView[][] imageViews = view.getImageViews();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int row = i;
                int col = j;
                imageViews[i][j].setOnMouseClicked(event -> handleMove(row, col));
            }
        }

        // Set up the stage
        Scene scene = new Scene(view.getGridPane(), 300, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simple Game");
        primaryStage.show();
    }


    private void handleMove(int row, int col) {
        view.updateView(new String[][]{}); // Update view after a valid move
    }

    // Add event handlers to handle user interactions
    // e.g., responding to button clicks or key presses

    public static void main(String[] args) {
        launch();
    }
}
