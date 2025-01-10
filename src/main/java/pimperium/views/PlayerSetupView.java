package pimperium.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
        // Load the background image from the assets folder
        Image backgroundImage = new Image("file:assets/background.jpg");

        // Create a BackgroundImage with properties to fit the screen
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, backgroundSize);
 
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(background));

        // Load the title image from the assets folder
        ImageView titleImage = new ImageView(new Image("file:assets/title.png"));
        titleImage.setFitWidth(300); // Set the desired width
        titleImage.setPreserveRatio(true); // Preserve the aspect ratio

        // Create and style the label for human players
        Label humanPlayersLabel = new Label("Nombre de joueurs humains :");
        humanPlayersLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        // Create and style the spinner for selecting the number of human players
        humanPlayersSpinner = new Spinner<>(0, 3, 1);
        humanPlayersSpinner.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3); -fx-text-fill: white;");

        
        // Create and style the next button (just a try for now)
        Button nextButton = new Button("Suivant");

        // Set action for the next button to call the controller's setupPlayerNames method
        nextButton.setOnAction(event -> controller.setupPlayerNames(humanPlayersSpinner.getValue()));
 
        humanPlayersLabel.getStyleClass().add("label");
        nextButton.getStyleClass().add("button");

        humanPlayersLabel.prefWidthProperty().bind(root.widthProperty().multiply(0.6));
        humanPlayersSpinner.prefWidthProperty().bind(root.widthProperty().multiply(0.2));
        nextButton.prefWidthProperty().bind(root.widthProperty().multiply(0.3));

        // Add all components to the root VBox
        root.getChildren().addAll(titleImage, humanPlayersLabel, humanPlayersSpinner, nextButton);
        root.setPadding(new Insets(50));
        root.setSpacing(30);
    }

    // Method to get the root VBox
    public VBox getRoot() {
        return root;
    }
}