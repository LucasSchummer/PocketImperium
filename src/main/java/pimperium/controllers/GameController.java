package pimperium.controllers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

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

    public void start(Stage primaryStage) {

        game = new Game();
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

    public void handleHexagonClick(Polygon polygon) {
        System.out.println(this.polygonHexMap.get(polygon) + " clicked");
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

    public static void main(String[] args) {
        launch();
    }
}
