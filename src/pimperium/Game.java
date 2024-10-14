package pimperium;
import java.util.ArrayList;

public class Game {
	
	private int round;
	private Hexagon[][] hexs;
	
	public Game() {
		
	}
	
	public void generateMap() {
		
		this.hexs = new Hexagon[9][6];
		for (int i=0; i<9; i++) {
			int line_width = 5 + (i%2==0? 1:0);
			for (int j=0; j<line_width; j++) {
				hexs[i][j] = new Hexagon(i, j);
			}
		}
		
	}
	
	public void createHexNeighbours() {
		for (int i=0; i<9; i++) {
			int line_width = 5 + (i%2==0? 1:0);
			for (int j=0; j<line_width; j++) {
				Hexagon hex = hexs[i][j];
				//Left neighbour
				if (j>0) {
					hex.addNeighbour(hexs[i][j-1]);
				}
				//Right neighbour
				if (j<line_width-1) {
					hex.addNeighbour(hexs[i][j+1]);
				}
				
				//Top neighbours
				if (i>0) {
					//Even lines
					if (i%2==0) {
						if (j>0) {
							hex.addNeighbour(hexs[i-1][j-1]);
						}
						if (j<line_width-1) {
							hex.addNeighbour(hexs[i-1][j]);
						}
					}
					//Odd lines
					else {
						hex.addNeighbour(hexs[i-1][j]);
						hex.addNeighbour(hexs[i-1][j+1]);
					}
				}
				
				//Bottom neighbours
				if (i<8) {
					//Even lines
					if (i%2==0) {
						if (j>0) {
							hex.addNeighbour(hexs[i+1][j-1]);
						}
						if (j<line_width-1) {
							hex.addNeighbour(hexs[i+1][j]);
						}
					}
					//Odd lines
					else {
						hex.addNeighbour(hexs[i+1][j]);
						hex.addNeighbour(hexs[i+1][j+1]);
					}
				}

			}
		}
	}

	
	public Hexagon[][] get_map() {
		return this.hexs;
	}
	
	//Test Methods
	public void displayMap() {
		for (int i=0; i<9; i++) {
			int line_width = 5 + (i%2==0? 1:0);
			for (int j=0; j<line_width; j++) {
				System.out.println(hexs[i][j]);
			}
		}
	}
	
	public void displayMap(int line) {
		int line_width = 5 + (line%2==0? 1:0);
		for (int j=0; j<line_width; j++) {
			System.out.println(hexs[line][j]);
		}
	}
	
	
	//Main method
	public static void main(String[] args) {
		
		System.out.println("Hello World !");
		Game game = new Game();
		game.generateMap();
		game.createHexNeighbours();
		//game.displayMap();
		game.hexs[8][5].printNeighbours();

	}

}
