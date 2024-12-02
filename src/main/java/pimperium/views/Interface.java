package pimperium.views;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import pimperium.models.Game;
import javafx.scene.control.Button;


public class Interface {

    private Game game;
    private GridPane gridPane;
    private Pane hexPane;
    private ImageView[][] imageViews;

    public Interface(Game game) {
        this.game = game;

        gridPane = new GridPane();
        imageViews = new ImageView[3][3];

        gridPane.setPrefWidth(525);
        gridPane.setPrefHeight(750);
        // Remove spacing and padding
        gridPane.setHgap(2); // Horizontal gap between columns
        gridPane.setVgap(2); // Vertical gap between rows
        gridPane.setPadding(new javafx.geometry.Insets(0));// Padding around the grid

        for (int i = 0; i < this.game.getSectors().length; i++) {
            Image image = new Image("file:assets/" + this.game.getSectors()[i].getPath());
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
            gridPane.add(imageView, i % 3, (int) i / 3); // Add imageView to gridPane
        }

        hexPane = new Pane();
        hexPane.setPickOnBounds(false);

        Polygon hex = createHexagon(44, 75, 48);
        hex.setOnMouseClicked(event -> handleHexagonClick());

        hexPane.getChildren().add(hex);
        
    }

    private Polygon createHexagon(double centerX, double centerY, double radius) {
        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            hexagon.getPoints().addAll(x, y);
        }
        hexagon.setFill(Color.RED); // Transparent fill
        hexagon.setStroke(Color.TRANSPARENT); // No visible border
        return hexagon;
    }

    private void handleHexagonClick() {
        System.out.println("Hexagon clicked");
    }

    public Pane getRoot() {
        Pane root = new Pane();
        root.getChildren().addAll(gridPane, hexPane); // Add GridPane and overlay
        return root;
    }

    public ImageView[][] getImageViews() {
        return imageViews;
    }

}