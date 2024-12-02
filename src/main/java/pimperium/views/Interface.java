package pimperium.views;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import pimperium.models.Game;
import javafx.scene.control.Button;


public class Interface {

    private Game game;
    private GridPane gridPane;
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
        
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public ImageView[][] getImageViews() {
        return imageViews;
    }

}