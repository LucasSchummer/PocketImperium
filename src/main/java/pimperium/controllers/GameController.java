package pimperium.controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
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

/**
 * Controller in the VCM design pattern
 */
public class GameController extends Application {
    private transient Stage primaryStage;
    private Game game;
    private transient Interface view;
    /**
     * Map linking game hexagons to view graphical hexagons (polygons)
     */
    private transient Map<Hexagon, Polygon> hexPolygonMap;
    /**
     * Map linking view graphical hexagons to game hexagons
     */
    private transient Map<Polygon, Hexagon> polygonHexMap;
    /**
     * Map linking view image sectors with game sectors
     */
    private transient Map<ImageView, Sector> imageViewSectorMap;

    /**
     * Stores the hexagon that has been selected with the interface
     */
    private Hexagon selectedHexagon;
    /**
     * Stores the sector that has been selected with the interface
     */
    private Sector selectedSector;

    private String userInput;

    /**
     * Music player for the menu
     */
    private MediaPlayer menuPlayer;
    /**
     * Music player in game
     */
    private MediaPlayer gamePlayer;

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
    
        // Display the main menu
        showMainMenu();
    }

    /**
     * Create a new game, as opposed to load an existing save
     */
    public void startNewGame() {
        showPlayerSetup();
    }

    /**
     * Show the player setup interface to choose the number of human players
     */
    public void showPlayerSetup() {
        PlayerSetupView setupView = new PlayerSetupView(this);
        Scene scene = new Scene(setupView.getRoot(), 750, 500);
        scene.getStylesheets().add("file:assets/style.css");
        primaryStage.setScene(scene);
    }

    /**
     * Show the second player setup interface to choose pseudos and bot strategies
     *
     * @param humanPlayerCount The number of humans among players
     */
    public void setupPlayerNames(int humanPlayerCount) {
        PlayerNamesView namesView = new PlayerNamesView(this, humanPlayerCount);
        Scene scene = new Scene(namesView.getRoot(), 750, 500);
        scene.getStylesheets().add("file:assets/style.css");
        primaryStage.setScene(scene);
    }

    /**
     * Create the players and set up the game to start the game
     *
     * @param playerNames The pseudos of the human players
     * @param botStrategies The strategy for each bot
     */
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
                primaryStage.setFullScreen(true);
                primaryStage.show();
            });
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Display the main menu, where the user can start a game
     */
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

    /**
     * Save the selected hexagon when the user clicks on the corresponding polygon
     * @param polygon The interface polygon clicked by the user
     */
    public synchronized void handleHexagonClick(Polygon polygon) {
        selectedHexagon = polygonHexMap.get(polygon);
        //System.out.println(selectedHexagon + " clicked");
        notify(); // Notify waiting threads
    }

    /**
     * Save the selected sector when the user clicks on the corresponding image view
     * @param imageView The sector image clicked by the user
     */
    public synchronized void handleSectorClick(ImageView imageView) {
        selectedSector = imageViewSectorMap.get(imageView);
        notify(); // Notify waiting threads
    }

    /**
     * Wait for the user to select a hexagon through the interface
     * @return The hexagon chosen by the user
     * @throws InterruptedException
     */
    public synchronized Hexagon waitForHexagonSelection() throws InterruptedException {
        selectedHexagon = null;
        while (selectedHexagon == null) {
            wait();
        }
        Hexagon hex = selectedHexagon;
        selectedHexagon = null; // Reset for next selection
        return hex;
    }

    /**
     * Wait for the user to select a sector through the interface
     * @return The sector chosen by the user
     * @throws InterruptedException
     */
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

    /**
     * React to game events and update the view accordingly
     * @param event The game event triggered by the PCS/PCL
     */
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

    /**
     * Check the validity of the order of commands chosen by the user and set it in the game if it is correct
     * @param player
     * @param selectedCommands List of commands chosen by the user, in the desired order
     */
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

    /**
     * Save the input entered by the user in the input text box
     * @param input The user input
     */
    public void handleUserInput(String input) {
        synchronized (this) {
            this.userInput = input;
            //System.out.println(userInput);
            notify(); 
        }
    }

    /**
     * Wait for the user to enter a number in the input box
     * @return The integer entered by the user
     * @throws InterruptedException
     */
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

    /**
     * Create a backup of the current game
     * @param filename The name of the backup file to be created
     * @throws IOException
     */
    public void saveGame(String filename) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(game); 
        out.close();
        fileOut.close();
        System.out.println("Jeu sauvegardé");
    }

    /**
     * Load the last game backup and start the game from this point
     */
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

        }  catch (ClassNotFoundException e) {
            System.out.println("Fichier de jeu sauvegardé non trouvé.");
            System.out.println(e.getMessage());
        }
        catch (IOException e) {
            System.out.println("Le jeu sauvegardé n'a pas pu être chargé.");
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
