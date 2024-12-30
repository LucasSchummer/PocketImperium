package pimperium.controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


import java.beans.PropertyChangeEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import pimperium.elements.Hexagon;
import pimperium.elements.Sector;
import pimperium.models.Game;
import pimperium.players.*;
import pimperium.utils.Colors;
import pimperium.views.Interface;
import pimperium.views.MenuView;
import pimperium.views.PlayerNamesView;
import pimperium.views.PlayerSetupView;

public class GameController extends Application {
    private transient Stage primaryStage;
    private Game game;
    private transient Interface view;
    private transient Map<Hexagon, Polygon> hexPolygonMap;
    private transient Map<Polygon, Hexagon> polygonHexMap;
    private transient Map<ImageView, Sector> imageViewSectorMap;

    private Hexagon selectedHexagon;
    private Sector selectedSector;

    private String userInput;

    private MediaPlayer menuPlayer;
    private MediaPlayer gamePlayer;

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
    
        // Display the main menu
        showMainMenu();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void startNewGame() {
        showPlayerSetup();
    }

    public void showPlayerSetup() {
        PlayerSetupView setupView = new PlayerSetupView(this);
        Scene scene = new Scene(setupView.getRoot(), 750, 500);
        scene.getStylesheets().add("file:assets/style.css");
        primaryStage.setScene(scene);
    }
    
    public void setupPlayerNames(int humanPlayerCount) {
        PlayerNamesView namesView = new PlayerNamesView(this, humanPlayerCount);
        Scene scene = new Scene(namesView.getRoot(), 750, 500);
        scene.getStylesheets().add("file:assets/style.css");
        primaryStage.setScene(scene);
    }

    public void startGameWithPlayers(List<String> playerNames, List<String> botStrategies) {
        try {
            System.out.println("Starting the game with players: " + playerNames);
    
            // Create a new instance of Game
            game = new Game();
            game.setController(this);
            game.addPropertyChangeListener(this::onGameChange);
    
            // Create human players
            List<Player> players = new ArrayList<>();
            List<String> chosenPseudos = new ArrayList<>(playerNames);
            List<Colors> availableColors = new ArrayList<>(Arrays.asList(Colors.values()));
		    Collections.shuffle(availableColors);

            for (String name : playerNames) {
                Colors playerColor = availableColors.remove(0);
                Human human = new Human(game, playerColor);
                human.setPseudo(name);
                players.add(human);
            }

            List<String> botNames = Arrays.asList(
                "Luke Skywalker", "Obiwan Kenobi", "Han Solo", 
                "Darth Vader", "Leia Organa", "Yoda", 
                "Anakin Skywalker", "Padmé Amidala", "Mace Windu", 
                "Qui-Gon Jinn", "Ahsoka Tano", "Rey", 
                "Kylo Ren", "Finn", "Poe Dameron"
            );
    
            // Add bots to reach the total number of players
            for (int i = 0; i < botStrategies.size(); i++) {
                String strategy = botStrategies.get(i);
                Colors botColor = availableColors.remove(0);
                Bot bot;
                switch (strategy) {
                    case "Offensif":
                        bot = new OffensiveBot(game, botColor);
                        break;
                    case "Défensif":
                        // bot = new DefensiveBot(game, botColor);
                        bot = new DefensiveBot(game, botColor);
                        break;
                    case "Aléatoire":
                    default:
                        bot = new RandomBot(game, botColor);
                        break;
                }

                Random random = new Random();

                boolean validName = false;
                String botPseudo = botNames.get(random.nextInt(botNames.size()));
                // Make sure that 2 bots don't have the same pseudo
                while (!validName) {
                    botPseudo = botNames.get(random.nextInt(botNames.size()));
                    validName = !chosenPseudos.contains(botPseudo);
                }
                chosenPseudos.add(botPseudo);


                bot.setPseudo(botPseudo);
                players.add(bot);
            }

            game.setPlayers(players.toArray(new Player[0]));
            game.setup();

            // Initialize the game view
            hexPolygonMap = new HashMap<>();
            polygonHexMap = new HashMap<>();
            imageViewSectorMap = new HashMap<>();
            view = new Interface(this);

            game.setViewInitialized();

            Platform.runLater(() -> this.view.updateScores(game.getPlayers()));


            // Start the game thread
            game.startGame();

            menuPlayer.stop();
            menuPlayer.dispose();
            
            if (gamePlayer == null) {
                Media gameMedia = new Media(Paths.get("assets/music/space-ranger.mp3").toUri().toString());
                gamePlayer = new MediaPlayer(gameMedia);
                gamePlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
            gamePlayer.play();


            // Display the game interface
            Platform.runLater(() -> {
                Scene scene = new Scene(view.getRoot(), 1200, 750);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Pocket Imperium");
                primaryStage.show();
            });
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMainMenu() {
        MenuView menuView = new MenuView(this);
        Scene scene = new Scene(menuView.getRoot(), 750, 500);
        scene.getStylesheets().add("file:assets/style.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pocket Imperium - Main Menu");
        primaryStage.show();
        if (menuPlayer == null) {
            Media menuMedia = new Media(Paths.get("assets/music/milky-way.mp3").toUri().toString());
            menuPlayer = new MediaPlayer(menuMedia);
            menuPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        }
        menuPlayer.play();
    }

    public synchronized void handleHexagonClick(Polygon polygon) {
        selectedHexagon = polygonHexMap.get(polygon);
        //System.out.println(selectedHexagon + " clicked");
        notify(); // Notify waiting threads
    }

    public synchronized void handleSectorClick(ImageView imageView) {
        selectedSector = imageViewSectorMap.get(imageView);
        notify(); // Notify waiting threads
    }

    public synchronized Hexagon getSelectedHexagon() {
        return selectedHexagon;
    }

    public synchronized void resetSelectedHexagon() {
        selectedHexagon = null;
    }

    public synchronized Hexagon waitForHexagonSelection() throws InterruptedException {
        selectedHexagon = null;
        while (selectedHexagon == null) {
            wait();
        }
        Hexagon hex = selectedHexagon;
        selectedHexagon = null; // Reset for next selection
        return hex;
    }

    public synchronized Sector waitForSectorSelection() throws InterruptedException {
        selectedSector = null;
        Platform.runLater(() -> this.view.changeSectorsTransparency(false));
        Platform.runLater(() -> this.view.changeHexsTransparency(true));
        while (selectedSector == null) {
            wait();
        }
        Sector sector = selectedSector;
        selectedSector = null;
        Platform.runLater(() -> this.view.changeHexsTransparency(false));
        Platform.runLater(() -> this.view.changeSectorsTransparency(true));
        return sector;
    }

    public void onGameChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case "hexUpdated":
                for (Hexagon hex : this.hexPolygonMap.keySet()) {
                    Platform.runLater(() -> this.view.updateHexagon(hex));
                }
                break;
            case "scoreUpdated":
                Platform.runLater(() -> this.view.updateScores(game.getPlayers()));
                break;
            case "roundOver":
                try {
                    this.saveGame("SavedGames/SavedGame.dat");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
        }
    }

    public synchronized void handleCommandSelection(Human player, List<String> selectedCommands) {
        // Commands to indexes
        Map<String, Integer> commandMap = new HashMap<>();
        commandMap.put("Expand", 0);
        commandMap.put("Explore", 1);
        commandMap.put("Exterminate", 2);

        int[] order = new int[3];
        Set<Integer> alreadyChosen = new HashSet<>();

        for (int i = 0; i < selectedCommands.size(); i++) {
            int cmdIndex = commandMap.get(selectedCommands.get(i));
            if (alreadyChosen.contains(cmdIndex)) {
                return;
            }
            order[i] = cmdIndex;
            alreadyChosen.add(cmdIndex);
        }

        player.setOrderCommands(order);
        notify();
    }

    public void handleUserInput(String input) {
        synchronized (this) {
            this.userInput = input;
            //System.out.println(userInput);
            notify(); 
        }
    }

    public synchronized int waitForUserInput() throws InterruptedException {
        userInput = null;
        while (userInput == null) {
            wait();
        }
        int input = Integer.parseInt(userInput);
        userInput = null;
        return input;
    }

    public Game getGame() {
        return this.game;
    }

    public Interface getView() {
        return this.view;
    }

    public MediaPlayer getMenuPlayer() {
        return menuPlayer;
    }
    
    public MediaPlayer getGamePlayer() {
        return gamePlayer;
    }

    public Map<Hexagon, Polygon> getHexPolygonMap() {
        return this.hexPolygonMap;
    }

    public Map<Polygon, Hexagon> getPolygonHexMap() {
        return this.polygonHexMap;
    }

    public Map<ImageView, Sector> getImageViewSectorMap() {
        return this.imageViewSectorMap;
    }

    // Saves the game in the root of the project
    public void saveGame(String filename) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(game); 
        out.close();
        fileOut.close();
        System.out.println("Jeu sauvgardé");
    }

    // Load the game from the SavedGames directory
    public void loadGame() {
        String filename = "SavedGames/savedGame.dat";
        try {
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            game = (Game) in.readObject();
            in.close();
            fileIn.close();

            // Set the controller and listeners
            game.setController(this);
            game.addPropertyChangeListener(this::onGameChange);

            // Initialize the view and maps
            hexPolygonMap = new HashMap<>();
            polygonHexMap = new HashMap<>();
            imageViewSectorMap = new HashMap<>();
            view = new Interface(this);

            // Display the game interface
            Platform.runLater(() -> {
                Scene scene = new Scene(view.getRoot(), 1200, 750);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Pocket Imperium");
                primaryStage.show();
            });

            for (Hexagon hex : this.hexPolygonMap.keySet()) {
                Platform.runLater(() -> this.view.updateHexagon(hex));
            }

            Platform.runLater(() -> this.view.updateScores(game.getPlayers()));

            // Start the game thread
            game.startGame();

            menuPlayer.stop();
            menuPlayer.dispose();
            
            if (gamePlayer == null) {
                Media gameMedia = new Media(Paths.get("assets/music/space-ranger.mp3").toUri().toString());
                gamePlayer = new MediaPlayer(gameMedia);
                gamePlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
            gamePlayer.play();

        } catch (IOException e) {
            System.out.println("Le jeu sauvegardé n'a pas pu être chargé.");
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Fichier de jeu sauvegardé non trouvé.");
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
