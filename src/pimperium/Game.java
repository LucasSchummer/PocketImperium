package pimperium;
//import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
	
	private static final int MAP_ROWS = 9;
	private static final int MAP_COLS = 6;

	private int round;
	private Hexagon[][] hexs;
	private Sector[] sectors;
	
	public Game() {
		//Initialization in the constructor
		this.round = 0; 
		this.hexs = new Hexagon[MAP_ROWS][MAP_COLS];
		this.sectors = new Sector[MAP_ROWS]; //9 sectors
	}
	
	public void generateMap() {
		
		//Generate hexagons
		for (int i=0; i<9; i++) {
			int lineWidth = 5 + (i%2==0? 1:0);
			for (int j=0; j<lineWidth; j++) {
				hexs[i][j] = new Hexagon(i, j);
			}
		}
		
		
		//Generate Sectors
		final Sector[] temp_NormalSectors = {
		    new NormalSector(0, 0, 1, 0, 2, 1),
		    new NormalSector(2, 0, 0, 0, 1, 0),
		    new NormalSector(2, 1, 0, 0, 0, 1),
		    new NormalSector(1, 0, 2, 0, 2, 1),
		    new NormalSector(1, 0, 0, 0, 2, 0),
		    new NormalSector(1, 0, 0, 0, 0, 1)
		}; // (plus simple)


		List<Integer> indexes = new ArrayList<>();
		for (int i = 0; i <= 5; i++) {
		    indexes.add(i);
		}
		Collections.shuffle(indexes);
		
		//Add the 3 top sectors
		for (int i = 0; i < 3; i++) {
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
		for (int i = 3; i < 6; i++) {
			this.sectors[i+3] = temp_NormalSectors[indexes.get(i)];
		}
		
		
		
		//Assign systems to the right hexs
		for (int i=0; i < MAP_ROWS; i++) {
			int i_i = i / 3;
			int i_j = i % 3;
			ArrayList<HSystem> systems = this.sectors[i].getSystems();
			ArrayList<Integer> systemsCoords = this.sectors[i].getSystemsCoordinates();
			for (int j=0; j<systems.size(); j++) {
				Hexagon hex;
				if (i != 4) {
					//On the last line, the sectors are upside down, so the indexes of hexes won't be the same
					if (i_i<2) {
						hex = this.hexs[3*i_i + systemsCoords.get(2*j)][2*i_j + systemsCoords.get(2*j+1)];
					} else {
						//The row index is simple, it is 2-i, however it is more complicated for the column index
						//On the first and third row, we have to switch 0 and 1, while on the second line, 0 stays 0
						int sysLine = systemsCoords.get(2*j);
						int sectorColumn = 0;
						if (sysLine != 1) {
							sectorColumn = 1 - systemsCoords.get(2*j+1);
						}
						hex = this.hexs[3*i_i + (2 - sysLine)][2*i_j + sectorColumn];
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
		
		for (int i=0; i<MAP_ROWS; i++) {
			int lineWidth = 5 + (i%2==0? 1:0);
			for (int j=0; j<lineWidth; j++) {
				Hexagon hex = hexs[i][j];
				//Left neighbour
				if (j>0) {
					hex.addNeighbour(hexs[i][j-1]);
				}
				//Right neighbour
				if (j<lineWidth-1) {
					hex.addNeighbour(hexs[i][j+1]);
				}
				
				//Top neighbours
				if (i>0) {
					//Even lines
					if (i%2==0) {
						if (j>0) {
							hex.addNeighbour(hexs[i-1][j-1]);
						}
						if (j<lineWidth-1) {
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
				if (i<MAP_ROWS - 1) {
					//Even lines
					if (i%2==0) {
						if (j>0) {
							hex.addNeighbour(hexs[i+1][j-1]);
						}
						if (j<lineWidth-1) {
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

	
	public Hexagon[][] getMap() {
		return this.hexs;
	}
	
	//Test Methods
	public String displayMap() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAP_ROWS; i++) {
            int lineWidth = 5 + (i % 2 == 0 ? 1 : 0);
            for (int j = 0; j < lineWidth; j++) {
                sb.append(hexs[i][j]).append("\n");
            }
        }
        return sb.toString();
    }
    
    public String displayMap(int line) {
        StringBuilder sb = new StringBuilder();
        int lineWidth = 5 + (line % 2 == 0 ? 1 : 0);
        for (int j = 0; j < lineWidth; j++) {
            sb.append(hexs[line][j]).append("\n");
        }
        return sb.toString();
    }
	
	
	//Main method
	public static void main(String[] args) {
		
		System.out.println("Hello World !");
		Game game = new Game();
		game.generateMap();
		game.createHexNeighbours();
		System.out.println(game.displayMap());


	}

}
