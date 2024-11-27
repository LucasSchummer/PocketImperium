package pimperium;
//import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Arrays;
import java.util.Random;
import java.util.HashSet;
import javax.swing.*;
import java.awt.*;


public class Game {
	
	public static final int MAP_ROWS = 9;
	public static final int MAP_COLS = 6;
	public static final int NB_PLAYERS = 3;

	private int round;
	private int round_step;
	private Hexagon[][] hexs;
	private Sector[] sectors;
	private Player[] players;
	//The players ordered for the round (line i is the order for the ith action)
	private Player[][] orderPlayers;
	private Integer[][] efficiencies;

	public Scanner scanner = new Scanner(System.in);
	
	
	public Game() {
		//Initialization in the constructor
		this.round = 0; 
		this.hexs = new Hexagon[MAP_ROWS][MAP_COLS];
		this.sectors = new Sector[MAP_ROWS]; //9 sectors
	}

	public void setup() {
		this.generateMap();
		this.createHexNeighbours();
		this.createTriPrime();
		this.createPlayers();
		System.out.println("Plateau de jeu :");
		System.out.println(this.displayMap());
		this.setupFleets();

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
		for (int i=0; i < 9; i++) {
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
					hex.addNeighbor(hexs[i][j-1]);
				}
				//Right neighbour
				if (j<lineWidth-1) {
					hex.addNeighbor(hexs[i][j+1]);
				}
				
				//Top neighbours
				if (i>0) {
					//Even lines
					if (i%2==0) {
						if (j>0) {
							hex.addNeighbor(hexs[i-1][j-1]);
						}
						if (j<lineWidth-1) {
							hex.addNeighbor(hexs[i-1][j]);
						}
					}
					//Odd lines
					else {
						hex.addNeighbor(hexs[i-1][j]);
						hex.addNeighbor(hexs[i-1][j+1]);
					}
				}
				
				//Bottom neighbours
				if (i<MAP_ROWS - 1) {
					//Even lines
					if (i%2==0) {
						if (j>0) {
							hex.addNeighbor(hexs[i+1][j-1]);
						}
						if (j<lineWidth-1) {
							hex.addNeighbor(hexs[i+1][j]);
						}
					}
					//Odd lines
					else {
						hex.addNeighbor(hexs[i+1][j]);
						hex.addNeighbor(hexs[i+1][j+1]);
					}
				}

			}
		}
	}

	//Gather the 4 middle hexs into one hex to create Tri-Prime
	public void createTriPrime() {

		Set<Hexagon> centralHexs = new HashSet<Hexagon>();
		centralHexs.add(this.hexs[3][2]);
		centralHexs.add(this.hexs[4][2]);
		centralHexs.add(this.hexs[4][3]);
		centralHexs.add(this.hexs[5][2]);

		//Create triPrime and set its neighbors as the neighbors of the 4 central hexs
		Hexagon triPrime = new Hexagon(3, 2);
		triPrime.setTriPrime();
		for (Hexagon hex : centralHexs) {
			triPrime.addNeighbor(hex.getNeighbours());
		}

		//For each hex, if it has one of the central hexes as a neighbor, we assign triPrime instead
		for (int i=0; i<9; i++) {
			int lineWidth = 5 + (i%2==0? 1:0);
			for (int j=0; j<lineWidth; j++) {
				Set<Hexagon> intersection = new HashSet<Hexagon>(centralHexs);
				intersection.retainAll(this.hexs[i][j].getNeighbours());
				if (!intersection.isEmpty()) {
					this.hexs[i][j].addNeighbor(triPrime);
					this.hexs[i][j].removeNeighbor(centralHexs);
				}
			}
		}

		//Delete the central hexs from triPrime's neighbors
		triPrime.removeNeighbor(centralHexs);

		this.hexs[3][2] = triPrime;

		this.hexs[4][2] = triPrime;
		this.hexs[4][3] = triPrime;
		this.hexs[5][2] = triPrime;

	}
	
	//Create the players according to their type
	public void createPlayers() {
		this.players = new Player[NB_PLAYERS];

		for (int i = 0; i < NB_PLAYERS; i++) {
			System.out.print("Le joueur " + (i + 1) + " est-il un humain ? (oui/non) : ");
			String type = scanner.nextLine().trim().toLowerCase();

			if (type.equals("oui") || type.equals("o")) {
				System.out.print("Entrez le pseudo pour le joueur " + (i + 1) + " : ");
				String pseudo = scanner.nextLine().trim();
				Human human = new Human(this);
				human.setPseudo(pseudo);
				this.players[i] = human;
			} else {
				// TODO : Changer par la classe Bot
				List<String> botNames = Arrays.asList(
					"Luke Skywalker", "Obiwan Kenobi", "Han Solo", 
					"Darth Vader", "Leia Organa", "Yoda", 
					"Anakin Skywalker", "Padmé Amidala", "Mace Windu", 
					"Qui-Gon Jinn", "Ahsoka Tano", "Rey", 
					"Kylo Ren", "Finn", "Poe Dameron"
				);
				Random random = new Random();
				String botPseudo = botNames.get(random.nextInt(botNames.size()));
				Bot bot = new RandomBot(this);
				bot.setPseudo(botPseudo);
				this.players[i] = bot;
			}
		}

	}
	
	//Ask all the players to place their initial fleet
	public void setupFleets() {
		//TODO Ask each player twice in a specific order
		for (Player player: this.players) {
			player.setupInitialFleet();
		}
	}

	public void getPlayOrder() {

		this.orderPlayers = new Player[3][NB_PLAYERS];
		for (int i = 0; i < 3; i++) {

			//Set the default order for the ith action (the ith line of orderPlayers)
			this.orderPlayers[i] = new Player[]{this.players[0], this.players[1], this.players[2]};

			//Bubble sort to order the players for the ith action
			for (int j = 0; j < 3 - 1; j++) {
				// Traverse the array up to the unsorted portion
				for (int k = 0; k < 3 - j - 1; k++) {
					// Compare ith action of adjacent players
					if (orderPlayers[i][k].getOrderCommands()[i] > orderPlayers[i][k + 1].getOrderCommands()[i]) {
						//Switch players
						Player temp = this.orderPlayers[i][k];
						this.orderPlayers[i][k] = this.orderPlayers[i][k + 1];
						this.orderPlayers[i][k+1] = temp;
					}
				}
			}

		}

		// Calculate efficiency of each action
		this.efficiencies = new Integer[3][NB_PLAYERS];
		for (int i = 0; i < 3; i++) {
			int[] commandCount = new int[3]; // To count occurrences of each command (0: Expand, 1: Explore, 2: Exterminate)
			for (int j = 0; j < NB_PLAYERS; j++) {
				int command = this.orderPlayers[i][j].getOrderCommands()[i];
				commandCount[command]++;
			}
	
			for (int j = 0; j < NB_PLAYERS; j++) {
				int command = this.orderPlayers[i][j].getOrderCommands()[i];
				switch (commandCount[command]) {
					case 3:
						this.efficiencies[i][j] = 1; // To work in the for loops in the doActions
						break;
					case 2:
						this.efficiencies[i][j] = Math.max(2, this.orderPlayers[i][j].countShips());
						break;
					default:
						this.efficiencies[i][j] = Math.max(3, this.orderPlayers[i][j].countShips());
						break;
				}
			}
		}

		this.efficiencies = new Integer[][] {
				{1,1,1},
				{1,1,1},
				{1,1,1}
		};

	}

	//Switch the start player
	public void switchStartPlayer() {
		Player temp = this.players[0];
		for (int i = 0; i < this.players.length-1; i++) {
			this.players[i] = this.players[i+1];
		}
		this.players[this.players.length-1] = temp;
	}

	public void playRound() {

		if (this.round > 0) this.switchStartPlayer();

		for (Player player : this.players) {
			player.chooseOrderCommands();
		}
		
		//Set play order and efficiencies for the round
		this.getPlayOrder();
		
		this.round_step = 0;
		
		for (int i = 0; i < 3; i++) {
			this.playRoundStep();
			this.round_step++;
		}

		this.round++;
		
	}
	
	public void playRoundStep() {
		
		for (int j=0; j<NB_PLAYERS; j++) {
			this.orderPlayers[this.round_step][j].doAction(this.round_step, this.efficiencies[this.round_step][j]);
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
		// For each move to be valid, there are a few conditions :
		// Target hexagon is 1 or 2 hexs away from origin
		// Ship can not go through Tri-Prime
		// Ship can not go through or into a hex controlled by another player
		// A ship can only be moved once

		Set<Ship> uniqueShips = new HashSet<>(ships);
		boolean notTwice = uniqueShips.size() == ships.size();

		boolean distanceGood = true;
		for (int i = 0; i < ships.size(); i++) {
			Set<Hexagon> distance1Targets = ships.get(i).getPosition().getNeighbours();

			// Remove from the list of possible targets the hexs controlled by another player
			distance1Targets.removeIf(hex -> hex.getOccupant() != null & hex.getOccupant() != ships.getFirst().getOwner());

			Set<Hexagon> distance2Targets = new HashSet<Hexagon>();

			//We add the 2nd degree neighbors for each hex except triPrime
			for (Hexagon hex1 : distance1Targets) {
				if (!hex1.isTriPrime()) {
					distance2Targets.addAll(hex1.getNeighbours());
				}
			}

			//Get all the possible targets for the ship (and remove the hexs controlled by another player)
			distance1Targets.addAll(distance2Targets);
			distance1Targets.removeIf(hex -> hex.getOccupant() != null & hex.getOccupant() != ships.getFirst().getOwner());

			if (!distance1Targets.contains(targets.get(i))) distanceGood = false;

		}

		return notTwice & distanceGood;
	}

	//Assert that the exterminate move from the player is valid
	public boolean checkExterminateValidity(List<Ship> ships, List<Hexagon> targets) {
		// Verify that the ships target different hexagons and that the targets are adjacent
		if (ships.size() != targets.size()) {
			return false;
		}
		for (Hexagon hex : targets) {
			if (hex.getShips().isEmpty()) {
				return false; // No enemy to exterminate in this hexagon
			}
		}
		Set<Hexagon> uniqueTargets = new HashSet<>(targets);
		return uniqueTargets.size() == targets.size();
	}

	public Hexagon[][] getMap() {
		return this.hexs;
	}
	
	//Test Methods
	public String displayMap() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < MAP_ROWS; i++) {
			int lineWidth = 5 + (i % 2 == 0 ? 1 : 0);
			if (i % 2 == 1) {
				sb.append("  "); 
			}
			for (int j = 0; j < lineWidth; j++) {
				Hexagon hex = hexs[i][j];
				if (hex != null && hex.getSystem() != null) {
					// TODO : Inititalize Hexs of TripPrime to Level 3 (0 currently)
					sb.append("[" + hex.getSystem().getLevel() + "] ");
				} else {
					sb.append("[0] ");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}
    
    public String displayMap(int line) {
		StringBuilder sb = new StringBuilder();
		int lineWidth = 5 + (line % 2 == 0 ? 1 : 0);
		for (int j = 0; j < lineWidth; j++) {
			Hexagon hex = hexs[line][j];
			if (hex != null && hex.getSystem() != null) {
				sb.append(hex.getSystem().getLevel()).append(" ");
			} else {
				sb.append("0 ").append(" ");
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	// Display the board (graphic interface)
	public void displayBoard() {
		JFrame frame = new JFrame("Plateau de jeu");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(650, 800);
	
		JPanel panel = new JPanel() {
			Image backgroundImage = new ImageIcon(getClass().getResource("/assets/background2.png")).getImage();
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
	
				// Dessiner l'image de fond
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
	
				// Dessiner les hexagones
				g.setColor(Color.BLUE);
				drawHexagons(g);
			}
		};

		frame.add(panel);
		frame.setVisible(true);
	}

	// Draws all the hexagons on the board
	private void drawHexagons(Graphics g) {
		int panelWidth = 650;
		int panelHeight = 800;
		int hexWidth = 90; 
		int hexHeight = 75;
		int totalHexWidth = 5 * hexWidth + hexWidth / 2;
		int totalHexHeight = MAP_ROWS * hexHeight;
		int offsetX = (panelWidth - totalHexWidth) / 2;
		int offsetY = (panelHeight - totalHexHeight) / 2;

		for (int i = 0; i < MAP_ROWS; i++) {
			int lineWidth = 5 + (i % 2 == 0 ? 1 : 0);
			for (int j = 0; j < lineWidth; j++) {
				Hexagon hex = hexs[i][j];
				int x = offsetX + j * hexWidth + (i % 2) * (hexWidth / 2);
				int y = offsetY + i * hexHeight;
				drawHexagon(g, x, y);
			}
		}
	}

	// Detail to draw one hexagon
	private void drawHexagon(Graphics g, int x, int y) {
		Polygon hex = new Polygon();
		int radius = 45; 
		for (int i = 0; i < 6; i++) {
			int angleDeg = 60 * i + 30;
			double angleRad = Math.toRadians(angleDeg);
			int xPoint = x + (int)(radius * Math.cos(angleRad));
			int yPoint = y + (int)(radius * Math.sin(angleRad));
			hex.addPoint(xPoint, yPoint);
		}
		g.drawPolygon(hex);
	}

	//Main method
	public static void main(String[] args) {
		
		Game game = new Game();

		game.setup();
    	game.displayBoard();
		game.playRound();

		Possibilities poss = Possibilities.getInstance(game);
		List<Hexagon> hexs = poss.setupFleet();

		for (Hexagon hex : hexs) {
			System.out.println(hex);
		}



	}

}
