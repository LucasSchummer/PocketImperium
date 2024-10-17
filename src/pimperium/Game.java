package pimperium;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
	
	private int round;
	private Hexagon[][] hexs;
	private Sector[] sectors;
	
	public Game() {
		
	}
	
	public void generateMap() {
		
		//Generate hexagons
		this.hexs = new Hexagon[9][6];
		for (int i=0; i<9; i++) {
			int line_width = 5 + (i%2==0? 1:0);
			for (int j=0; j<line_width; j++) {
				hexs[i][j] = new Hexagon(i, j);
			}
		}
		
		
		//Generate Sectors
		this.sectors = new Sector[9];
		final Sector[] temp_NormalSectors = new Sector[6];
		temp_NormalSectors[0] = new NormalSector(0,0,1,0,2,1);
		temp_NormalSectors[1] = new NormalSector(2,0,0,0,1,0);
		temp_NormalSectors[2] = new NormalSector(2,1,0,0,0,1);
		temp_NormalSectors[3] = new NormalSector(1,0,2,0,2,1);
		temp_NormalSectors[4] = new NormalSector(1,0,0,0,2,0);
		temp_NormalSectors[5] = new NormalSector(1,0,0,0,0,1);
		
		List<Integer> indexes = new ArrayList<>();
		for (int i = 0; i <= 5; i++) {
		    indexes.add(i);
		}
		Collections.shuffle(indexes);
		
		//Add the 3 top sectors
		for (int i = 0; i <= 2; i++) {
			this.sectors[i] = temp_NormalSectors[indexes.get(i)];
		}
		//Add the central row
		if (Math.random() > 0.5) {
			this.sectors[3] = new SideSector(0,0,1,0,2,0);
			this.sectors[4] = new CentralSector();
			this.sectors[5] = new SideSector(1,1,0,0,2,0);
		} else {
			this.sectors[3] = new SideSector(1,1,0,0,2,0);
			this.sectors[4] = new CentralSector();
			this.sectors[5] = new SideSector(0,0,1,0,2,0);
		}
		//Add the 3 bottom sectors
		for (int i = 3; i <= 5; i++) {
			this.sectors[i+3] = temp_NormalSectors[indexes.get(i)];
		}
		
		
		//Assign systems to the right hexs
		for (int i=0; i<9; i++) {
			int i_i = i / 3;
			int i_j = i % 3;
			ArrayList<HSystem> systems = this.sectors[i].getSystems();
			ArrayList<Integer> systems_coords = this.sectors[i].getSystemsCoordinates();
			for (int j=0; j<systems.size(); j++) {
				Hexagon hex;
				if (i != 4) {
					//On the last line, the sectors are upside down, so the indexes of hexes won't be the same
					if (i_i<2) {
						hex = this.hexs[3*i_i + systems_coords.get(2*j)][2*i_j + systems_coords.get(2*j+1)];
					} else {
						//The row index is simple, it is 2-i, however it is more complicated for the column index
						//On  the first and third row, we have to switch 0 and 1, while on the second line, 0 stays 0
						int sys_line = systems_coords.get(2*j);
						int sector_column = 0;
						if (sys_line != 1) {
							sector_column = 1 - systems_coords.get(2*j+1);
						}
						hex = this.hexs[3*i_i + (2 - sys_line)][2*i_j + sector_column];
					}
				} else {
					hex = this.hexs[3][2];
				}

				System.out.println(hex);
				hex.addSystem(systems.get(j));
				systems.get(j).setHex(hex);

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


	}

}
