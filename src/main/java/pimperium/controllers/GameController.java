package pimperium.controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import pimperium.elements.Hexagon;
import pimperium.models.Game;
import pimperium.views.Interface;

public class GameController extends Application {
    private Game game;
    private Interface view;
    Map<Hexagon, Polygon> hexPolygonMap;
    Map<Polygon, Hexagon> polygonHexMap;

    private Hexagon selectedHexagon;

    public void start(Stage primaryStage) {

        game = new Game();
        game.setController(this);
        game.addPropertyChangeListener(this::onGameChange);
        game.startGame();

        hexPolygonMap = new HashMap<>();
        polygonHexMap = new HashMap<>();

        view = new Interface(this);

        // Set up the stage
        Scene scene = new Scene(view.getRoot(), 529, 754);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pocket Imperium");
        primaryStage.show();
        }

    public synchronized void handleHexagonClick(Polygon polygon) {
        selectedHexagon = polygonHexMap.get(polygon);
        System.out.println(selectedHexagon + " clicked");
        notify(); // Notify waiting threads
    }

    public synchronized Hexagon getSelectedHexagon() {
        return selectedHexagon;
    }

    public synchronized void resetSelectedHexagon() {
        selectedHexagon = null;
    }

    public synchronized Hexagon waitForHexagonSelection() throws InterruptedException {
        while (selectedHexagon == null) {
            wait();
        }
        Hexagon hex = selectedHexagon;
        selectedHexagon = null; // Reset for next selection
        return hex;
    }

    public void onGameChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case "hexUpdated":
                for (Hexagon hex : this.hexPolygonMap.keySet()) {
                    Platform.runLater(() -> this.view.updateHexagon(hex));
                }
                break;
            default:
        }
    }

    public Game getGame() {
        return this.game;
    }

    public Map<Hexagon, Polygon> getHexPolygonMap() {
        return this.hexPolygonMap;
    }

    public Map<Polygon, Hexagon> getPolygonHexMap() {
        return this.polygonHexMap;
    }

    // Tried with saving (working) but not with loading, so can't say if it's done correctly
    // Saves the game in the root of the project
    public void saveGame(String filename) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(game); 
        out.close();
        fileOut.close();
    }

    // Load the game from the root of the project
    public void loadGame(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        game = (Game) in.readObject();
        in.close();
        fileIn.close();
    }

    public static void main(String[] args) {
        launch();
    }
}
