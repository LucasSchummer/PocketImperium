package pimperium.views;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import pimperium.controllers.GameController;

import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

import javafx.util.Pair;

import pimperium.elements.Hexagon;
import pimperium.players.Player;
import pimperium.utils.Colors;
import pimperium.players.Bot;
import pimperium.players.Human;

public class Interface {

    private GameController controller;
    private GridPane gridPane;
    private Pane hexPane;
    private ImageView[][] imageViews;
    private VBox sidePanel;
    private VBox topSection;
    private VBox toptopSection;
    private VBox middleSection;
    private VBox bottomSection;
    private VBox gameLogPanel;
    private Image speakerOnImage = new Image("file:assets/speaker_high_volume.png");
    private Image speakerOffImage = new Image("file:assets/speaker_muted.png");
    private ImageView speakerImageOnView = new ImageView(speakerOnImage);
    private ImageView speakerImageOffView = new ImageView(speakerOffImage);

    private Button musicControlButton;
    private boolean isMusicPlaying = true;

    public Interface(GameController controller) {
        this.controller = controller;

        gridPane = new GridPane();
        imageViews = new ImageView[3][3];

        gridPane.setPrefWidth(825);
        gridPane.setPrefHeight(750);
        // Remove spacing and padding
        gridPane.setHgap(2); // Horizontal gap between columns
        gridPane.setVgap(2); // Vertical gap between rows
        gridPane.setPadding(new javafx.geometry.Insets(0));// Padding around the grid

        // Load the background image from the assets folder
        Image backgroundImage = new Image("file:assets/background.jpg");

        // Create a BackgroundImage with properties to fit the screen
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, backgroundSize);

        gameLogPanel = new VBox();
        gameLogPanel.setPadding(new Insets(10));
        gameLogPanel.setAlignment(Pos.TOP_LEFT);
        gameLogPanel.setPrefWidth(400);
        gameLogPanel.setSpacing(10);

        Text logTitle = new Text("Déroulement de la partie");
        logTitle.setFill(Color.WHITE);
        logTitle.setFont(Font.font("Orbitron", FontWeight.BOLD, 18));
        VBox.setMargin(logTitle, new Insets(0, 0, 15, 0)); 
        gameLogPanel.getChildren().add(logTitle);

        ScrollPane gameLogScrollPane = new ScrollPane(gameLogPanel);
        gameLogScrollPane.setPrefWidth(400);
        gameLogScrollPane.setFitToWidth(true); // Adjust the content width to the ScrollPane width
        gameLogScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Always show the vertical scrollbar
        gameLogScrollPane.setBackground(new Background(background));
        gameLogScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;"); // Set background to transparent
            
        // Add empty space at the bottom
        Text emptySpace = new Text("\n");
        emptySpace.setFont(Font.font("Orbitron", 14));
        gameLogPanel.getChildren().add(emptySpace);

        sidePanel = new VBox(20);
        sidePanel.setPadding(new Insets(10));
        sidePanel.setAlignment(Pos.TOP_CENTER);
        sidePanel.setPrefWidth(275);
        sidePanel.setMaxWidth(275);

        topSection = new VBox();
        topSection.setSpacing(10);
        //topSection.setPrefHeight(174);

        middleSection = new VBox();
        middleSection.setSpacing(10);
        //middleSection.setPrefHeight(358);

        bottomSection = new VBox();
        bottomSection.setSpacing(20);
        //bottomSection.setPrefHeight(228);
        Text inputTitle = new Text("Entrées");
        inputTitle.setFill(Color.WHITE);
        inputTitle.setFont(Font.font("Orbitron", FontWeight.BOLD, 18));
        bottomSection.getChildren().add(inputTitle);

        TextField userInputField = new TextField();
        //userInputField.setPromptText("Entrez votre commande ici");
        userInputField.setMaxWidth(150); // Set the maximum width
        // Set TextFormatter to allow only integers
        userInputField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) { // Allow optional '-' for negative integers
                return change; // Accept change
            }
            return null; // Reject change
        }));

        // Trigger when Enter is pressed in the TextField
        userInputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String input = userInputField.getText();
                controller.handleUserInput(input);
                userInputField.clear();
            }
        });

        bottomSection.getChildren().add(userInputField);


        Button validateButton = new Button("Valider");
        validateButton.getStyleClass().add("button");
        validateButton.setOnAction(event -> {
            String input = userInputField.getText();
            controller.handleUserInput(input);
            userInputField.clear();
        });
        validateButton.setPrefWidth(150);
        VBox.setMargin(validateButton, new Insets(10, 0, 15, 0)); 
        bottomSection.getChildren().add(validateButton);


        Rectangle separator2 = new Rectangle();
        separator2.setFill(Color.WHITE);
        separator2.widthProperty().bind(sidePanel.widthProperty());
        separator2.setHeight(2);

        Rectangle separator3 = new Rectangle();
        separator3.setFill(Color.WHITE);
        separator3.widthProperty().bind(sidePanel.widthProperty());
        separator3.setHeight(2);

        Rectangle separator4 = new Rectangle();
        separator4.setFill(Color.WHITE);
        separator4.heightProperty().bind(sidePanel.heightProperty());
        separator4.setWidth(2);

        
        // Adds the Controle music Button
        ImageView speakerImageView = new ImageView(speakerOnImage);
        speakerImageView.setFitWidth(20);
        speakerImageView.setFitHeight(20);

        speakerImageOnView.setFitWidth(20);
        speakerImageOnView.setFitHeight(20);
        speakerImageOffView.setFitWidth(20);
        speakerImageOffView.setFitHeight(20);
        
        musicControlButton = new Button();
        musicControlButton.setGraphic(speakerImageView);
        musicControlButton.setPrefSize(60, 60);
        musicControlButton.setStyle(
            "-fx-background-radius: 30;"
        + "-fx-background-color: white;"
        + "-fx-padding: 0;"
        );
        musicControlButton.setOnAction(event -> toggleMusic());

        musicControlButton.setPrefSize(60, 60);
        musicControlButton.setMaxSize(60, 60);

        // Add the music control button to the bottom section
        bottomSection.getChildren().add(musicControlButton);
        VBox.setMargin(musicControlButton, new Insets(10, 0, 20, 0)); // Add margin to the button     
        
        sidePanel.getChildren().addAll(topSection, separator2, middleSection, separator3, bottomSection);

        Text title3 = new Text("Commandes");
        title3.setFill(Color.WHITE);
        title3.setFont(Font.font("Orbitron", FontWeight.BOLD, 18));
        middleSection.getChildren().add(title3);



        for (int i = 0; i < this.controller.getGame().getSectors().length; i++) {
            Image image = new Image("file:assets/" + this.controller.getGame().getSectors()[i].getPath());
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(false);
            imageView.setFitHeight(250);
            imageView.setFitWidth(175);

            // Flip vertically the last 3 sectors
            if (i > 5) {
                Rotate rotate = new Rotate(180, imageView.getFitWidth() / 2, imageView.getFitHeight() / 2); // Rotate around the center
                imageView.getTransforms().add(rotate);
            }

            imageViews[(int) i / 3][i % 3] = imageView;

            // Create a white rectangle overlay
            Rectangle overlay = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
            overlay.setFill(Color.WHITE);
            overlay.setOpacity(0); // Initially transparent

            StackPane stackPane = new StackPane(imageView, overlay);

            // Add hover effect for brightness adjustment
            stackPane.setOnMouseEntered(event -> overlay.setOpacity(0.1)); // Brighten on hover
            stackPane.setOnMouseExited(event -> overlay.setOpacity(0));    // Reset brightness

            stackPane.setOnMouseClicked(event -> this.controller.handleSectorClick(imageView));
            this.controller.getImageViewSectorMap().put(imageView, this.controller.getGame().getSectors()[i]);

            gridPane.add(stackPane, i % 3, (int) i / 3); // Add imageView to gridPane
        }

        this.changeSectorsTransparency(true);

        this.addHexagons();
        
        StackPane gamePane = new StackPane();
        gamePane.getChildren().addAll(gridPane, hexPane);

        Rectangle separator = new Rectangle();
        separator.setFill(Color.WHITE);
        separator.heightProperty().bind(sidePanel.heightProperty());
        separator.setWidth(2);
        

        
        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(gamePane);
        mainLayout.setRight(sidePanel);
        mainLayout.setLeft(gameLogScrollPane);
        mainLayout.setBackground(new Background(background));

        
        this.root = mainLayout;

    }

    public void addHexagons() {

        this.hexPane = new Pane();
        this.hexPane.setPickOnBounds(false);

        // Create triPrime's StackPane and add it to the hexagons pane
        Polygon triPrime = createTriPrime();
        StackPane triPrimeStack = new StackPane(triPrime);
        triPrimeStack.setLayoutX(264.5 - triPrime.getLayoutBounds().getWidth() / 2); // Adjust position
        triPrimeStack.setLayoutY(377.0 - triPrime.getLayoutBounds().getHeight() / 2);

        this.hexPane.getChildren().add(triPrimeStack);
        this.controller.getPolygonHexMap().put(triPrime, this.controller.getGame().getMap()[3][2]);
        this.controller.getHexPolygonMap().put(this.controller.getGame().getMap()[3][2], triPrime);


        // Set the coordinates where we don't want to draw a hex, as TriPime is there
        List<Pair<Integer, Integer>> coordsNoHex = new ArrayList<>();
        coordsNoHex.add(new Pair<>(3,2));
        coordsNoHex.add(new Pair<>(4,2));
        coordsNoHex.add(new Pair<>(4,3));
        coordsNoHex.add(new Pair<>(5,2));

        // Create each hexagon with its own pane and add it to the hexs pane
        for (int i = 0; i < 9; i++) {
            int lineWidth = 5 + (i%2==0? 1:0);
            int basePosX = 44 + (lineWidth==6? 0:44);
            for (int j = 0; j < lineWidth; j++) {
                if (!coordsNoHex.contains(new Pair<>(i,j))) {
                    int centerX = basePosX + 88 * j +  (int)(j/2);
                    int centerY = 75 + 75 * i + (int)(i/2);
                    int radius = 49;

                    // Create hexagon shape
                    Polygon polygon = createHexagon(0, 0, radius); // Centered within StackPane

                    // Create StackPane to wrap the hexagon
                    StackPane hexStack = new StackPane();
                    hexStack.setPrefSize(2 * radius, 2 * radius); // Size to fit the hexagon
                    hexStack.setLayoutX(centerX - radius); // Position StackPane's top-left corner
                    hexStack.setLayoutY(centerY - radius);
                    hexStack.getChildren().add(polygon);

                    this.hexPane.getChildren().add(hexStack);

                    // Link the polygon to the Game Hex in two maps
                    this.controller.getPolygonHexMap().put(polygon, this.controller.getGame().getMap()[i][j]);
                    this.controller.getHexPolygonMap().put(this.controller.getGame().getMap()[i][j], polygon);
                }
            }
        }

    }

    public Polygon createHexagon(double centerX, double centerY, double radius) {
        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            hexagon.getPoints().addAll(x, y);
        }
        hexagon.setFill(Color.WHITE);
        hexagon.setStroke(Color.TRANSPARENT); // No visible border
        hexagon.setOpacity(0); // Transparent fill

        // Add opacity events
        hexagon.setOnMouseEntered(event -> hexagon.setOpacity(0.1));
        hexagon.setOnMouseExited(event -> hexagon.setOpacity(0));
        hexagon.setOnMouseClicked(event -> this.controller.handleHexagonClick(hexagon));
        return hexagon;
    }

    public Polygon createTriPrime() {
        Polygon triPrime = new Polygon();

        triPrime.getPoints().addAll(264.5, 254.0);
        triPrime.getPoints().addAll(306.5, 279.0);
        triPrime.getPoints().addAll(306.5, 329.0);
        triPrime.getPoints().addAll(349.5, 354.0);
        triPrime.getPoints().addAll(349.5, 403.0);
        triPrime.getPoints().addAll(306.5, 429.0);
        triPrime.getPoints().addAll(306.5, 478.0);
        triPrime.getPoints().addAll(264.5, 503.0);
        triPrime.getPoints().addAll(222.5, 478.0);
        triPrime.getPoints().addAll(222.5, 429.0);
        triPrime.getPoints().addAll(179.5, 403.0);
        triPrime.getPoints().addAll(179.5, 354.0);
        triPrime.getPoints().addAll(222.5, 329.0);
        triPrime.getPoints().addAll(222.5, 279.0);

        triPrime.setFill(Color.WHITE); 
        triPrime.setStroke(Color.TRANSPARENT); // No visible border
        triPrime.setOpacity(0); // Transparent fill

        // Add oppacity events
        triPrime.setOnMouseEntered(event -> triPrime.setOpacity(0.1));
        triPrime.setOnMouseExited(event -> triPrime.setOpacity(0));
        triPrime.setOnMouseClicked(event -> this.controller.handleHexagonClick(triPrime));

        return triPrime;
    }

    public void updateHexagon(Hexagon hex) {

        // Get the pane that the polygon belongs to
        Pane pane = (Pane) this.controller.getHexPolygonMap().get(hex).getParent();

        // Clear any existing display on the hex
        pane.getChildren().removeIf(node ->  !(node instanceof Polygon));

        // Draw the ships on the hex
        if (!hex.getShips().isEmpty()) {
/*            Text shipCount = new Text(hex.getOccupant().getPseudo() + " : " + String.valueOf(hex.getShips().size()));
            shipCount.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 13));
            shipCount.setFill(Color.WHITE);
            shipCount.setWrappingWidth(80);
            shipCount.setMouseTransparent(true);*/
            if (hex.isTriPrime()) {
                pane.getChildren().add(drawShipsTriPrime(hex.getShips().size(), hex.getOccupant().getColor()));
            } else {
                pane.getChildren().add(drawShips(hex.getShips().size(), hex.getOccupant().getColor()));
            }

        }

    }

    // Method to display command selection
    public void showCommandSelection(Human player) {
        Platform.runLater(() -> {
            // Clear the side panel
            middleSection.getChildren().clear();

            // Title
            Text commandsTitle = new Text("Choisissez vos commandes");
            commandsTitle.setFill(Color.WHITE);
            commandsTitle.setFont(Font.font("Orbitron", FontWeight.BOLD, 18));
            middleSection.getChildren().add(commandsTitle);

            // List of available commands
            List<String> availableCommands = new ArrayList<>(Arrays.asList("Expand", "Explore", "Exterminate"));
            List<String> selectedCommands = new ArrayList<>();

            // Create a list to store ComboBoxes
            List<ComboBox<String>> comboBoxes = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                Text instruction = new Text("Commande " + i + " :");
                instruction.setFill(Color.WHITE);
                instruction.setFont(Font.font("Orbitron", 14));
                middleSection.getChildren().add(instruction);

                ComboBox<String> comboBox = new ComboBox<>();
                comboBox.getItems().addAll(availableCommands);
                comboBox.setPrefWidth(150);
                comboBoxes.add(comboBox);
                middleSection.getChildren().add(comboBox);

                // Add some space between each command
                middleSection.setSpacing(10);
            }
            
            Button validateButton = new Button("Valider");
            validateButton.getStyleClass().add("button");
            validateButton.setPrefWidth(150);
            VBox.setMargin(validateButton, new Insets(15, 0, 20, 0));  
            middleSection.getChildren().add(validateButton);

            validateButton.setOnAction(event -> {
                selectedCommands.clear();
                Set<String> selectedSet = new HashSet<>();
                for (ComboBox<String> comboBox : comboBoxes) {
                    String command = comboBox.getValue();
                    if (command == null || selectedSet.contains(command)) {
                        // Display an error message if a command is not selected or duplicated
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur");
                        alert.setHeaderText(null);
                        alert.setContentText("Veuillez sélectionner des commandes distinctes pour chaque emplacement.");
                        alert.showAndWait();
                        return;
                    }
                    selectedCommands.add(command);
                    selectedSet.add(command);
                }
                // Notify the controller that the commands are chosen
                controller.handleCommandSelection(player, selectedCommands);
            });
        });
    }

    private Color getPlayerColor(Colors colorEnum) {
        switch (colorEnum) {
            case RED:
                return Color.rgb(255, 102, 102);
            case GREEN:
                return Color.rgb(102, 255, 102);
            case BLUE:
                return Color.rgb(102, 102, 255);
            case YELLOW:
                return Color.rgb(255, 255, 102);
            case PURPLE:
                return Color.rgb(204, 102, 255);
            case ORANGE:
                return Color.rgb(255, 178, 102);
            default:
                return Color.WHITE;
        }
    }

    public void addLogMessage(String message, Player player, String fontWeight) {
        Platform.runLater(() -> {
            // Reduce the opacity of existing messages in groups of 3
            for (int i = 1; i < gameLogPanel.getChildren().size(); i++) { // Ignore the title at index 0
                javafx.scene.Node node = gameLogPanel.getChildren().get(i);
                if (node instanceof TextFlow) {
                    double newOpacity = Math.max(0.4, 0.9 - i * 0.03); // Decrease opacity in steps of 0.1, with a minimum of 0.5
                    node.setOpacity(newOpacity);
                    Font font = Font.font("Orbitron", FontWeight.NORMAL, 14);
                    //Text text = (Text) ((TextFlow) node).getChildren().getFirst();
                    for (Node text : ((TextFlow) node).getChildren()) {
                        if (text instanceof Text) {
                            ((Text) text).setFont(font);
                        }
                    }
                }
            }

            TextFlow logEntry;
            if (player != null && fontWeight != "error") {
                // Create text nodes with different colors for player messages
                Text playerText = new Text(player.getPseudo() + " : ");
                playerText.setFill(getPlayerColor(player.getColor())); 
                
                Text messageText = new Text(message);
                messageText.setFill(Color.WHITE);

                // Set font for both texts
                Font font = Font.font("Orbitron", FontWeight.findByName(fontWeight.toUpperCase()), 16);
                playerText.setFont(font);
                messageText.setFont(font);

                logEntry = new TextFlow(playerText, messageText);
            } else if (fontWeight == "error") {
                // Create single white text for game messages
                Text gameText = new Text(message);
                gameText.setFill(Color.RED);
                Font font = Font.font("Orbitron", FontWeight.findByName(fontWeight.toUpperCase()), 14);
                gameText.setFont(font);
                
                logEntry = new TextFlow(gameText);
            } else {
                // Create single white text for game messages
                Text gameText = new Text(message);
                gameText.setFill(Color.WHITE);
                Font font = Font.font("Orbitron", FontWeight.findByName(fontWeight.toUpperCase()), 14);
                gameText.setFont(font);
                
                logEntry = new TextFlow(gameText);
            }

            logEntry.setOpacity(1.0);
            logEntry.setPrefWidth(375);
            logEntry.setLineSpacing(2);

            // Add the message just after the title (index 1)
            gameLogPanel.getChildren().add(1, logEntry);
        });
    }   

    public void updateScores(Player[] players) {
        // Clear the current content of the score section
        topSection.getChildren().clear();
    
        // Round section
        Text roundTitle = new Text("Round " + Math.min((controller.getGame().getRound() + 1), 9) + "/9");
        roundTitle.setFill(Color.WHITE); 
        roundTitle.setFont(Font.font("Orbitron", FontWeight.BOLD, 18));
        topSection.getChildren().add(roundTitle);

        // Add separator with HBox to remove padding

        HBox separatorBox = new HBox();
        separatorBox.setPadding(new Insets(0));
        separatorBox.setAlignment(Pos.CENTER);
        
        Rectangle roundSeparator = new Rectangle();
        roundSeparator.setFill(Color.WHITE);
        roundSeparator.widthProperty().bind(sidePanel.widthProperty());
        roundSeparator.setHeight(2);
        
        separatorBox.getChildren().add(roundSeparator);
        VBox.setMargin(separatorBox, new Insets(5, -10, 5, -10)); // Negative margin to compensate for parent padding
        topSection.getChildren().add(separatorBox);

        // Section title for scores
        Text title2 = new Text("Score");
        title2.setFill(Color.WHITE);
        title2.setFont(Font.font("Orbitron", FontWeight.BOLD, 18));
        topSection.getChildren().add(title2);

    
        // Display the score for each player
        for (Player player : players) {
            String playerType = (player instanceof Bot) ? "(Bot) " : "";
            String playerScore = playerType + player.getPseudo() + " : " + player.getScore();
            Text scoreText = new Text(playerScore);
            
            // Set the color of the pseudo to match the player's ship color
            scoreText.setFill(getPlayerColor(player.getColor()));
            scoreText.setFont(Font.font("Orbitron", 14));
            topSection.getChildren().add(scoreText);
        }
    
        // Add spacing between each score
        topSection.setSpacing(10);
    }

    public Pane drawShips(int numShips, Colors colorEnum) {

        Image shipImage = new Image("file:assets/spaceship3.png");
        Pane shipPane = new Pane();

        
        float hue = colorEnum.getHue();

        if (numShips > 1) {
            int radius = 20;
            // Add ships in a circular layout
            for (int i = 0; i < numShips; i++) {
                double angle = 2 * Math.PI / numShips * i; // Angle for each ship
                double x = 50 + radius * Math.cos(angle); // X-coordinate
                double y = 50 + radius * Math.sin(angle); // Y-coordinate

                // Create an ImageView for the ship
                ImageView ship = new ImageView(shipImage);

                // Apply color filter for Player
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setHue(hue);
                ship.setEffect(colorAdjust);

                ship.setRotate(90 + 360*angle/2/Math.PI);

                int width = 20;
                ship.setFitWidth(width); // Set ship width
                ship.setFitHeight(width); // Set ship height
                ship.setX(x - width/2); // Center the image at the calculated position
                ship.setY(y - width/2);

                // Add the ship to the pane
                shipPane.getChildren().add(ship);
            }
        } else {

            // Create an ImageView for the ship
            ImageView ship = new ImageView(shipImage);

            // Apply color filter for Player
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(hue);
            ship.setEffect(colorAdjust);

            int width = 20;
            ship.setFitWidth(width); // Set ship width
            ship.setFitHeight(width); // Set ship height
            ship.setX(50 - width/2); // Center the image at the calculated position
            ship.setY(50 - width/2);

            // Add the ship to the pane
            shipPane.getChildren().add(ship);
        }

        shipPane.setMouseTransparent(true);
        return shipPane;
    }

    public Pane drawShipsTriPrime(int numShips, Colors colorEnum) {

        Image shipImage = new Image("file:assets/spaceship3.png");
        Pane shipPane = new Pane();

        float hue = colorEnum.getHue();

        if (numShips > 1) {
            int radius = 20;
            // Add ships in a circular layout
            for (int i = 0; i < numShips; i++) {
                double angle = 2 * Math.PI / numShips * i; // Angle for each ship
                double x = 87 + radius * Math.cos(angle); // X-coordinate
                double y = 125 + radius * Math.sin(angle); // Y-coordinate

                // Create an ImageView for the ship
                ImageView ship = new ImageView(shipImage);

                // Apply color filter for Player
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setHue(hue);
                ship.setEffect(colorAdjust);

                ship.setRotate(90 + 360*angle/2/Math.PI);

                int width = 20;
                ship.setFitWidth(width); // Set ship width
                ship.setFitHeight(width); // Set ship height
                ship.setX(x - width/2); // Center the image at the calculated position
                ship.setY(y - width/2);

                // Add the ship to the pane
                shipPane.getChildren().add(ship);
            }
        } else {

            // Create an ImageView for the ship
            ImageView ship = new ImageView(shipImage);

            // Apply color filter for Player
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(hue);
            ship.setEffect(colorAdjust);

            int width = 20;
            ship.setFitWidth(width); // Set ship width
            ship.setFitHeight(width); // Set ship height
            ship.setX(87 - width/2); // Center the image at the calculated position
            ship.setY(125 - width/2);

            // Add the ship to the pane
            shipPane.getChildren().add(ship);
        }

        shipPane.setMouseTransparent(true);
        return shipPane;

    }

    private void toggleMusic() {
        if (isMusicPlaying) {
            controller.getGamePlayer().pause();
            musicControlButton.setGraphic(speakerImageOffView);
            musicControlButton.setPrefSize(60, 60);
            musicControlButton.setStyle(
                "-fx-background-radius: 30;"
            + "-fx-background-color: white;"
            + "-fx-padding: 0;"
            );  
        } else {
            controller.getGamePlayer().play();
            musicControlButton.setGraphic(speakerImageOnView);
            musicControlButton.setStyle(
                "-fx-background-radius: 30;"
            + "-fx-background-color: white;"
            + "-fx-padding: 0;"
            );
        }
        isMusicPlaying = !isMusicPlaying;
    }

    public void changeSectorsTransparency(boolean transparent) {
        this.gridPane.setMouseTransparent(transparent);
    }

    public void changeHexsTransparency(boolean transparent) {
        this.hexPane.setMouseTransparent(transparent);
    }

    private Pane root;

    public Pane getRoot() {
        return root;
    }

    public ImageView[][] getImageViews() {
        return imageViews;
    }

}