package pimperium;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;


public class Interface {

    private GridPane gridPane;
    private Button[][] buttons;
    private ImageView[][] imageViews;
    private Image[][] images;

    public Interface() {
        gridPane = new GridPane();
        buttons = new Button[3][3];
        images = new Image[3][3];
        imageViews = new ImageView[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
/*                Button button = new Button();
                button.setPrefSize(100, 100); // Size of each cell
                buttons[i][j] = button;
                gridPane.add(button, j, i); // Add button to gridPane*/
                Image image = new Image("file:assets/NormalSector1.png");
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100); // Set size of each cell
                imageView.setFitHeight(100);
                imageViews[i][j] = imageView;
                gridPane.add(imageView, i, j); // Add imageView to gridPane
            }
        }
    }

    public void updateView(String[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText(board[i][j]); // Update button text
            }
        }
    }


    public GridPane getGridPane() {
        return gridPane;
    }

    public Button[][] getButtons() {
        return buttons;
    }

    public ImageView[][] getImageViews() {
        return imageViews;
    }

}