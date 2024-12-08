package pimperium.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import pimperium.controllers.GameController;

public class PlayerSetupView {
    private GameController controller;
    private VBox root;
    private Spinner<Integer> humanPlayersSpinner;

    // Constructor to initialize the view with the given GameController
    public PlayerSetupView(GameController controller) {
        this.controller = controller;
        createView();
    }

    // Method to create the view layout and components
    private void createView() {
        // Set background image
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("file:assets/background.png", 600, 400, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        // Initialize root VBox with spacing and alignment
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(backgroundImage));

        // Create and style the title text
        Text title = new Text("Pocket Imperium");
        title.setFont(Font.font("Verdana", 40));
        title.setStyle("-fx-fill: white; -fx-effect: dropshadow(one-pass-box, black, 5, 0, 2, 2);");

        // Create and style the label for human players
        Label humanPlayersLabel = new Label("Nombre de joueurs humains :");
        humanPlayersLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        // Create and style the spinner for selecting the number of human players
        humanPlayersSpinner = new Spinner<>(1, 3, 1);
        humanPlayersSpinner.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3); -fx-text-fill: white;");

        // Create and style the next button (just a try for now)
        Button nextButton = new Button("Suivant");
        nextButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3);"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 10;");
        // Set action for the next button to call the controller's setupPlayerNames method
        nextButton.setOnAction(event -> controller.setupPlayerNames(humanPlayersSpinner.getValue()));

        // Add all components to the root VBox
        root.getChildren().addAll(title, humanPlayersLabel, humanPlayersSpinner, nextButton);
        root.setPadding(new Insets(50));
        root.setSpacing(30);
    }

    // Method to get the root VBox
    public VBox getRoot() {
        return root;
    }
}