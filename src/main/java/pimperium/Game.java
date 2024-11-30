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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;


public class Game{
	
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
	private Possibilities possibilities;

	public Scanner scanner = new Scanner(System.in);


	public Game() {
		//Initialization in the constructor
		this.round = 0; 
		this.hexs = new Hexagon[MAP_ROWS][MAP_COLS];
		this.sectors = new Sector[9];
		this.possibilities = Possibilities.getInstance(this);
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
		    new NormalSector(0, 0, 1, 0, 2, 1, 4),
		    new NormalSector(2, 0, 0, 0, 1, 0, 5),
		    new NormalSector(2, 1, 0, 0, 0, 1, 6),
		    new NormalSector(1, 0, 2, 0, 2, 1, 1),
		    new NormalSector(1, 0, 0, 0, 2, 0, 2),
		    new NormalSector(1, 0, 0, 0, 0, 1, 3)
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
			this.sectors[3] = new SideSector(0,0,1,0,2,0,1);
			this.sectors[4] = new CentralSector();
			this.sectors[5] = new SideSector(1,1,0,0,2,0,2);
		} else {
			this.sectors[3] = new SideSector(1,1,0,0,2,0,2);
			this.sectors[4] = new CentralSector();
			this.sectors[5] = new SideSector(0,0,1,0,2,0,1);
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

	// Gather the 4 middle hexes into one hex to create Tri-Prime
	public void createTriPrime() {
		// Create Tri-Prime
		Hexagon triPrime = new Hexagon(4, 2);
		triPrime.setTriPrime();
		triPrime.addSystem(new HSystem(3)); // Level 3 system
	
		// List of coordinates of the central hexes
		int[][] centralHexCoords = {{3,2}, {4,2}, {4,3}, {5,2}};
	
		// Set to store the neighbors of Tri-Prime
		Set<Hexagon> triPrimeNeighbors = new HashSet<>();
	
		for (int[] coord : centralHexCoords) {
			int i = coord[0];
			int j = coord[1];
			Hexagon hex = hexs[i][j];
	
			// Add the neighbors of the central hex to Tri-Prime
			for (Hexagon neighbor : hex.getNeighbours()) {
				if (!neighbor.isTriPrime()) {
					triPrimeNeighbors.add(neighbor);
					// Update the neighbors of each adjacent hex
					neighbor.getNeighbours().remove(hex);
					neighbor.addNeighbor(triPrime);
				}
			}
	
			// Replace the central hex with Tri-Prime in the grid
			hexs[i][j] = triPrime;
		}
	
		// Update the neighbors of Tri-Prime
		triPrime.setNeighbours(triPrimeNeighbors);
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
				// TODO : Change bot type
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

	public Sector findSector(Hexagon hex) {
		//Initialize the sector as the first one
		Sector sector = this.sectors[0];
		//Find the sector that contains the hex
		for (Sector s : this.sectors) {
			if (s.getSystems().contains(hex.getSystem())) {
				sector = s;
			}
		}
		return sector;
	}

	//Ask all the players to place their initial fleet
	public void setupFleets() {
		for (Player player: this.players) {
			player.setupInitialFleet();
		}
		for (int i = 0; i < this.players.length; i++) {
			this.players[this.players.length-1-i].setupInitialFleet();
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
						this.efficiencies[i][j] = Math.min(2, this.orderPlayers[i][j].countShips());
						break;
					default:
						this.efficiencies[i][j] = Math.min(3, this.orderPlayers[i][j].countShips());
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

	public void startGame() {
		// TODO Count the score of the players and check victory/defeat on each round_step
		//(in case a player lost all of his ships)
		this.setup();
		this.playRound();
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

		this.sustainShips();
		this.doFinalScore();
		
		this.round++;

	}
	
	public void playRoundStep() {
		for (int j=0; j<NB_PLAYERS; j++) {
			this.orderPlayers[this.round_step][j].doAction(this.round_step, this.efficiencies[this.round_step][j]);
		}
		
	}

	// Remove excess ships on every hexagon
	public void sustainShips() {
		for (int i = 0; i < MAP_ROWS; i++) {
			for (int j = 0; j < MAP_COLS; j++) {
				Hexagon hex = hexs[i][j];
				if (hex != null) {
					int systemLevel = hex.getSystemLevel();
					int maxShips = 1 + systemLevel;
					List<Ship> ships = hex.getShips();
					if (ships.size() > maxShips) {
						int shipsToRemove = ships.size() - maxShips;
						for (int k = 0; k < shipsToRemove; k++) {
							Ship ship = ships.get(k);
							ship.destroy(); // Return the ship to the reserve
						}
					}
				}
			}
		}
	}

	// Return the controller of the TriPrime (if existing)
	public Player getTriPrimeController() {
		// Retrieve the Tri-Prime sector
		Sector triPrimeSector = this.sectors[4]; // TriPrime Sector
		if (triPrimeSector != null) {
			// First and uniq system of the Sector is the TriPrime
			HSystem triPrimeSystem = triPrimeSector.getSystems().get(0); 
			if (triPrimeSystem.getController() != null) {
				return triPrimeSystem.getController();
			}
		}
		return null;
	}
		
	
	public void doFinalScore() {
		Set<Sector> scoredSectors = new HashSet<>();
	
		// Each player chooses a sector to score
		for (Player player : this.players) {
			Sector chosenSector = player.chooseSectorToScore(scoredSectors, this.sectors);
			if (chosenSector != null) {
				scoredSectors.add(chosenSector);
			}
		}
	
		// Check if a player controls Tri-Prime
		Player triPrimeController = getTriPrimeController();
		if (triPrimeController != null) {
			// The player controlling Tri-Prime chooses an additional sector
			Sector additionalSector = triPrimeController.chooseSectorToScore(scoredSectors, this.sectors);
			if (additionalSector != null) {
				scoredSectors.add(additionalSector);
			}
		}
	
		// Calculate points for each player
		for (Player player : this.players) {
			int score = 0;
			for (Sector sector : scoredSectors) {
				for (HSystem system : sector.getSystems()) {
					if (system.getController() == player) {
						score += system.getLevel();
					}
				}
			}
			player.addScore(score);
			System.out.println("The score of " + player.getPseudo() + " is " + score);
		}
	}
	
	//Assert that the expand move from the player is valid
	public boolean checkExpandValidity(List<Ship> ships) {
		//Check that no ship is expanded twice
		Set<Ship> uniqueShips = new HashSet<>(ships);
		boolean notTwice = uniqueShips.size() == ships.size();

		//Get all the possible ships to expand on
		List<Ship> possShips = possibilities.expand(ships.getFirst().getOwner());
		boolean allPossible = possShips.containsAll(ships);

		return notTwice && allPossible;
	}
	
	//Assert that the explore move from the player is valid
	public boolean checkExploreValidity(List<Pair<List<Ship>, List<Hexagon>>> moves) {

		// Check that no ship is moved twice
		Set<Hexagon> origins = new HashSet<Hexagon>();
		for (Pair<List<Ship>, List<Hexagon>> move : moves) {
			origins.add(move.getKey().getFirst().getPosition());
		}

		boolean notTwice = origins.size() == moves.size();

		// Check that all the moves are possible
		List<Pair<List<Ship>, List<Hexagon>>> possibleMoves = possibilities.explore(moves.getFirst().getKey().getFirst().getOwner());
		boolean allPossible = possibleMoves.containsAll(moves);

		return notTwice && allPossible;
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


	// Test Methods
	public String displayMap() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < MAP_ROWS; i++) {
			int lineWidth = 5 + (i % 2 == 0 ? 1 : 0);
			// Add spaces to align hexagons
			if (i % 2 == 1) {
				sb.append("  ");
			}
			for (int j = 0; j < lineWidth; j++) {
				Hexagon hex = hexs[i][j];
				// Avoid displaying Tri-Prime multiple times
				if (hex != null) {
					if (hex.isTriPrime()) {
						if (i == 4 && j == 2) {
							sb.append("  [3]");
						} else if (i == 4) {
							sb.append("   ");
						} else {
							sb.append("    ");
						}
					} else if (hex.getSystem() != null) {
						sb.append("[").append(hex.getSystem().getLevel()).append("] ");
					} else {
						sb.append("[0] ");
					}
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

/*	// Display the board (graphic interface)
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
	} */

	//Main method
	public static void main(String[] args) {

		Game game = new Game();

		game.setup();
    	//game.displayBoard();
		game.playRound();

	}

}
