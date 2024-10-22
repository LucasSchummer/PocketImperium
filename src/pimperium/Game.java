package pimperium;
//import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Game {
	
	private static final int MAP_ROWS = 9;
	private static final int MAP_COLS = 6;
	private static final int NB_PLAYERS = 3;

	private int round;
	private int round_step;
	private Hexagon[][] hexs;
	private Sector[] sectors;
	private Player[] players;
	//The players ordered for the round (line i is the order for the ith action)
	private Player[][] orderPlayers;
	private Integer[][] efficiencies;
	
	
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
		
		
		//Generate the 6 Normal Sectors
		final Sector[] temp_NormalSectors = {
		    new NormalSector(0, 0, 1, 0, 2, 1),
		    new NormalSector(2, 0, 0, 0, 1, 0),
		    new NormalSector(2, 1, 0, 0, 0, 1),
		    new NormalSector(1, 0, 2, 0, 2, 1),
		    new NormalSector(1, 0, 0, 0, 2, 0),
		    new NormalSector(1, 0, 0, 0, 0, 1)
		}; 

		
		//Create a random list of indexes to shuffle the normal sectors
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
				
				//Link the system to the corresponding hex
				//System.out.println(hex);
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
	
	//Create the players according to their type
	public void createPlayers() {

		System.out.println("Warning! Not implemented");
		this.players = new Player[] {
				new Human(this),
				new Human(this),
				new Human(this),
		};
		this.players[0].setPseudo("Luke Skywalker");
		this.players[1].setPseudo("Obiwan Kenobi");
		this.players[2].setPseudo("Han Solo");
	}
	
	//Ask all the players to place their initial fleet
	public void setupFleets() {
		//Should call the method twice on each player in a specific order
		System.out.println("Warning! Not implemented");
		for (Player player: this.players) {
			player.setupInitialFleet();
		}
	}

	public void getPlayOrder(int[] order1, int[] order2, int[] order3) {
		System.out.println("Warning! Not implemented");
		this.orderPlayers = new Player[][] {
			{this.players[0], this.players[2], this.players[1]},
			{this.players[1], this.players[0], this.players[2]},
			{this.players[2], this.players[1], this.players[0]}
		};
		this.efficiencies = new Integer[][] {
			{2,1,1},
			{1,2,2},
			{2,2,2}
		};
	}
	
	public void playRound() {
		
		System.out.println("Warning! Changing start player not implemented");
		int[] order1 = this.players[0].chooseOrderCommands();
		int[] order2 = this.players[1].chooseOrderCommands();
		int[] order3 = this.players[2].chooseOrderCommands();
		
		//Set play order and efficiencies for the round
		this.getPlayOrder(order1, order2, order3);
		
		this.round_step = 0;
		
		for (int i=0; i<3; i++) {
			this.playRoundStep();
			this.round_step++;
		}
		
	}
	
	public void playRoundStep() {
		
		for (int j=0; j<NB_PLAYERS; j++) {
			this.orderPlayers[this.round_step][j].doAction(j, this.efficiencies[this.round_step][j]);
		}
		
	}
	
	//Assert that the expand move from the player is valid
	public boolean checkExpandValidity(List<Ship> ships) {
		//The only way that the expand could be invalid is if the player tries to expand twice on the same ship
		//We create a set to remove the duplicates and compare the sizes
		
		Set<Ship> uniqueShips = new HashSet<>(ships);
		return uniqueShips.size() == ships.size();
		
	}
	
	//Assert that the explore move from the player is valid
	public boolean checkExploreValidity(List<Ship> ships, List<Hexagon> targets) {
		System.out.println("Warning! Not implemented");
		boolean validity = true;
		return validity;
	}
	
	//Assert that the exterminate move from the player is valid
	public boolean checkExterminateValidity(List<Ship> ships, List<Hexagon> targets) {
		System.out.println("Warning! Not implemented");
		boolean validity = true;
		return validity;
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
		
		Game game = new Game();
		//Should put all of this is a setup() method
		game.generateMap();
		game.createHexNeighbours();
		game.createPlayers();
		game.setupFleets();
		game.playRound();
		
		//System.out.println(game.displayMap());


	}

}
