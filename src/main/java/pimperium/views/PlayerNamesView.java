package pimperium.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import pimperium.controllers.GameController;
import pimperium.models.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * View showing the Player name/strategy selection menu
 */
public class PlayerNamesView {
    /**
     * The controller in the VCM design pattern
     */
    private GameController controller;
    /**
     * Base layer of the view
     */
    private VBox root;
    /**
     * Input fields to enter pseudos
     */
    private List<TextField> nameFields;
    /**
     * Labels of the text-fields
     */
    private List<Label> nameLabels;
    /**
     * Number of humans among players (from 0 to 3)
     */
    private int humanPlayerCount;
    /**
     * ComboBoxes for bot strategy selection
     */
    private List<ComboBox<String>> botStrategyCombos;

    /**
     *
     * @param controller The controller int VCM
     * @param humanPlayerCount The number of human players among 3 players
     */
    public PlayerNamesView(GameController controller, int humanPlayerCount) {
        this.controller = controller;
        this.humanPlayerCount = humanPlayerCount;
        createView();
    }

    /**
     * Inizialize the view
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
        titleImage.setFitWidth(300); // Set the desired width
        titleImage.setPreserveRatio(true); // Preserve the aspect ratio

        nameFields = new ArrayList<>();
        nameLabels = new ArrayList<>();
        botStrategyCombos = new ArrayList<>();

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
            nameField.setStyle("-fx-background-color: rgba(255, 255, 255); -fx-text-fill: black;");
            nameFields.add(nameField);
            fieldsBox.getChildren().addAll(nameLabel, nameField);
        }

        // Calculate the number of bots
        int botCount = Game.NB_PLAYERS - humanPlayerCount;

        // Create selectors for bot strategies (Random by default)
        for (int i = 1; i <= botCount; i++) {
            Label botLabel = new Label("Stratégie du bot " + i + " :");
            botLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            ComboBox<String> strategyCombo = new ComboBox<>();
            strategyCombo.getItems().addAll("Offensif", "Défensif", "Aléatoire");
            strategyCombo.getSelectionModel().select("Aléatoire"); // Set "Aléatoire" as the default selection
            botStrategyCombos.add(strategyCombo);
            fieldsBox.getChildren().addAll(botLabel, strategyCombo);
        }

        // Create and style the start button
        Button startButton = new Button("Lancer la Partie");
        startButton.setOnAction(event -> controller.startGameWithPlayers(getPlayerNames(), getBotStrategies()));


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

    /**
     * Retrieve the player names from the text boxes
     * @return The player names as a list of strings
     */
    private List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        for (TextField field : nameFields) {
            names.add(field.getText());
        }
        return names;
    }

    /**
     * Retrieve the bot strategies from the combo boxes
     * @return the bot strategies as a list of strings
     */
    public List<String> getBotStrategies() {
        List<String> strategies = new ArrayList<>();
        for (ComboBox<String> combo : botStrategyCombos) {
            strategies.add(combo.getValue());
        }
        return strategies;
    }

    public VBox getRoot() {
        return root;
    }

}