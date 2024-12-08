package pimperium.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import pimperium.controllers.GameController;

public class MenuView {
    private GameController controller;
    private VBox root;

    public MenuView(GameController controller) {
        this.controller = controller;
        createView();
    }

    // Method to create the view for the menu
    private void createView() {
        // Load the background image from the assets folder
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("file:assets/background.png", 600, 400, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(backgroundImage));

        // Add the title
        Text title = new Text("Pocket Imperium");
        title.setFont(Font.font("Verdana", 40));
        title.setStyle("-fx-fill: white; -fx-effect: dropshadow(one-pass-box, black, 5, 0, 2, 2);");

        // Create buttons for new game and load game
        Button newGameButton = new Button("Nouvelle Partie");
        Button loadGameButton = new Button("Charger une Partie");

        // Style the buttons
        String buttonStyle = "-fx-background-color: rgba(255, 255, 255, 0.3);"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 10;";
        newGameButton.setStyle(buttonStyle);
        loadGameButton.setStyle(buttonStyle);

        // Set actions for the buttons
        newGameButton.setOnAction(event -> controller.startNewGame());
        loadGameButton.setOnAction(event -> controller.loadGame());

        // Add all elements to the root layout
        root.getChildren().addAll(title, newGameButton, loadGameButton);
        root.setPadding(new Insets(50));
        root.setSpacing(30);
    }

    // Method to get the root layout
    public VBox getRoot() {
        return root;
    }
}