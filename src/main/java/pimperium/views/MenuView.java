package pimperium.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import pimperium.controllers.GameController;

/**
 * View showing the game main menu
 */
public class MenuView {
    /**
     * The Controller in the VCM design pattern
     */
    private GameController controller;
    /**
     * Base layer of the view
     */
    private VBox root;

    /**
     *
     * @param controller The controller in VCM
     */
    public MenuView(GameController controller) {
        this.controller = controller;
        createView();
    }

    /**
     * Initialize the view with buttons to start game
     */
    private void createView() {
        // Load the background image from the assets folder
        Image backgroundImage = new Image(getClass().getResource("/assets/background.jpg").toExternalForm());

        // Create a BackgroundImage with properties to fit the screen
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, backgroundSize);

        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(background));

        // Load the title image from the assets folder
        ImageView titleImage = new ImageView(new Image(getClass().getResource("/assets/title.png").toExternalForm()));
        titleImage.fitWidthProperty().bind(root.widthProperty().multiply(0.5)); 
        titleImage.setPreserveRatio(true); // Preserve the aspect ratio

        // Create buttons for new game and load game
        Button newGameButton = new Button("Nouvelle Partie");
        Button loadGameButton = new Button("Charger une Partie");

        // Style the buttons
        newGameButton.getStyleClass().add("button");
        loadGameButton.getStyleClass().add("button");

        newGameButton.prefWidthProperty().bind(root.widthProperty().multiply(0.3));
        loadGameButton.prefWidthProperty().bind(root.widthProperty().multiply(0.3));

        // Set actions for the buttons
        newGameButton.setOnAction(event -> controller.startNewGame());
        loadGameButton.setOnAction(event -> controller.loadGame());

        // Add all elements to the root layout
        root.getChildren().addAll(titleImage, newGameButton, loadGameButton);
        root.setPadding(new Insets(50));
        root.setSpacing(30);
    }

    public VBox getRoot() {
        return root;
    }
}