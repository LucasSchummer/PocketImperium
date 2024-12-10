package pimperium.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import pimperium.controllers.GameController;
import java.util.ArrayList;
import java.util.List;

public class PlayerNamesView {
    private GameController controller;
    private VBox root;
    private List<TextField> nameFields;
    private List<Label> nameLabels;
    private int humanPlayerCount;

    // Constructor to initialize the view with the game controller and number of human players
    public PlayerNamesView(GameController controller, int humanPlayerCount) {
        this.controller = controller;
        this.humanPlayerCount = humanPlayerCount;
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

        nameFields = new ArrayList<>();
        nameLabels = new ArrayList<>();

        // VBox to hold the player name fields
        VBox fieldsBox = new VBox(15);
        fieldsBox.setAlignment(Pos.CENTER);

        // Create labels and text fields for each player
        for (int i = 1; i <= humanPlayerCount; i++) {
            Label nameLabel = new Label("Pseudo du joueur " + i + " :");
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            nameLabels.add(nameLabel);
            TextField nameField = new TextField();
            nameField.setMaxWidth(200);
            nameField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-text-fill: black;");
            nameFields.add(nameField);
            fieldsBox.getChildren().addAll(nameLabel, nameField);
        }

        // Create and style the start button
        Button startButton = new Button("Lancer la Partie");
        startButton.setOnAction(event -> controller.startGameWithPlayers(getPlayerNames()));


        for (TextField nameField : nameFields) {
            nameField.prefWidthProperty().bind(root.widthProperty().multiply(0.4));
        }
        startButton.prefWidthProperty().bind(root.widthProperty().multiply(0.3));

        for (Label nameLabel : nameLabels) {
            nameLabel.getStyleClass().add("label");
        }
        startButton.getStyleClass().add("button");

        // Add all components to the root VBox
        root.getChildren().addAll(titleImage, fieldsBox, startButton);
        root.setPadding(new Insets(50));
        root.setSpacing(30);
    }

    // Method to get the player names from the text fields
    private List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        for (TextField field : nameFields) {
            names.add(field.getText());
        }
        return names;
    }

    // Method to get the root VBox
    public VBox getRoot() {
        return root;
    }
}