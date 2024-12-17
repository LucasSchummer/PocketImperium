package pimperium.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
//import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Arrays;
import java.util.Random;
import java.util.HashSet;
import java.util.stream.Collectors;

import javafx.util.Pair;

import pimperium.controllers.GameController;
import pimperium.elements.CentralSector;
import pimperium.elements.HSystem;
import pimperium.elements.Hexagon;
import pimperium.elements.NormalSector;
import pimperium.elements.Sector;
import pimperium.elements.Ship;
import pimperium.elements.SideSector;
import pimperium.players.Bot;
import pimperium.players.Human;
import pimperium.players.Player;
import pimperium.players.RandomBot;
import pimperium.utils.Colors;
import pimperium.utils.Debugger;
import pimperium.utils.Possibilities;


public class Game implements Runnable, Serializable {

	private static final long serialVersionUID = 1L;
	public static final int MAP_ROWS = 9;
	public static final int MAP_COLS = 6;
	public static final int NB_PLAYERS = 3;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private int round;
	private int round_step;
	private Hexagon[][] hexs;
	private Sector[] sectors;
	private Player[] players;
	//The players ordered for the round (line i is the order for the ith action)
	private Player[][] orderPlayers;
	private Integer[][] efficiencies;
	private  Possibilities possibilities;
	private boolean gameEnded;

	// Non-serializable variables
	public transient Scanner scanner = new Scanner(System.in);
	private transient Thread t;
	private transient GameController controller;

	public Game() {
		//Initialization in the constructor
		this.round = 0; 
		this.hexs = new Hexagon[MAP_ROWS][MAP_COLS];
		this.sectors = new Sector[9];
		this.possibilities = Possibilities.getInstance(this);
		this.gameEnded = false;
	}

	public void setController(GameController controller) {
        this.controller = controller;
    }

    public GameController getController() {
        return this.controller;
    }

	public Player[] getPlayers() {
		return this.players;
	}

	public void setPlayers(Player[] players) {
		this.players = players;
	}

	public void setup() {
		this.generateMap();
		this.createHexNeighbours();
		this.createTriPrime();
		//this.createPlayers();
		System.out.println("Plateau de jeu :");
		System.out.println(this.displayMap());
		this.setupFleets();
	}

	public void startGame() {
		this.t = new Thread(this, "Game");
		this.t.start();
	}

	public void stopGame() {
		this.gameEnded = true;
		if (t != null) {
			t.interrupt();
		}
		if (scanner != null) {
			scanner.close();
		}
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
		    new NormalSector(0, 0, 1, 0, 2, 1, "normal1.png"),
		    new NormalSector(2, 0, 0, 0, 1, 0, "normal2.png"),
		    new NormalSector(2, 1, 0, 0, 0, 1, "normal3.png"),
		    new NormalSector(1, 0, 2, 0, 2, 1, "normal4.png"),
		    new NormalSector(1, 0, 0, 0, 2, 0, "normal5.png"),
		    new NormalSector(1, 0, 0, 0, 0, 1, "normal6.png")
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
			this.sectors[3] = new SideSector(0,0,1,0,2,0,"side1.png");
			this.sectors[4] = new CentralSector();
			this.sectors[5] = new SideSector(1,1,0,0,2,0,"side2.png");
		} else {
			this.sectors[3] = new SideSector(1,1,0,0,2,0,"side2.png");
			this.sectors[4] = new CentralSector();
			this.sectors[5] = new SideSector(0,0,1,0,2,0,"side1.png");
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

		Set<Hexagon> formerTriPrimes = new HashSet<>();
		Set<Hexagon> triPrimeNeighbors = new HashSet<>();

		// List of coordinates of the central hexes
		int[][] centralHexCoords = {{3,2}, {4,2}, {4,3}, {5,2}};
		for (int[] coord : centralHexCoords) {
			int i = coord[0];
			int j = coord[1];
			formerTriPrimes.add(hexs[i][j]);
		}

		for (Hexagon[] row : hexs) {
			for (Hexagon hex : row) {
				if (hex != null && !formerTriPrimes.contains(hex)) {
					// Check if any of the formerTriPrime hexes are neighbors
					if (!Collections.disjoint(hex.getNeighbours(), formerTriPrimes)) {
						// Add Tri-Prime as a neighbor
						triPrimeNeighbors.add(hex);
					}
				}
			}
		}

		for (Hexagon neighbor : triPrimeNeighbors) {
			neighbor.printNeighbours();
			neighbor.getNeighbours().removeAll(formerTriPrimes); // Remove old neighbors
			neighbor.getNeighbours().add(triPrime); // Add Tri-Prime as a neighbor
			neighbor.printNeighbours();
		}

		for (int[] coord : centralHexCoords) {
			int i = coord[0];
			int j = coord[1];
			hexs[i][j] = triPrime;
		}

		triPrime.setNeighbours(triPrimeNeighbors);
		triPrimeNeighbors.removeAll(formerTriPrimes);

	}

	//Create the players according to their type
	public void createPlayers() {
		this.players = new Player[NB_PLAYERS];
		List<String> chosenPseudos = new ArrayList<>();
		List<Colors> availableColors = new ArrayList<>(Arrays.asList(Colors.values()));
		Collections.shuffle(availableColors);

		for (int i = 0; i < NB_PLAYERS; i++) {
			boolean validInput = false;
			while (!validInput) {
				System.out.print("Le joueur " + (i + 1) + " est-il un humain ? (oui/non) : ");
				String type = scanner.nextLine().trim().toLowerCase();

				if (type.equals("oui") || type.equals("o")) {
					System.out.print("Entrez le pseudo pour le joueur " + (i + 1) + " : ");
					String pseudo = scanner.nextLine().trim();
					chosenPseudos.add(pseudo);
					Colors playerColor = availableColors.remove(0);
					Human human = new Human(this, playerColor);
					human.setPseudo(pseudo);
					this.players[i] = human;
					validInput = true;
				} else if (type.equals("non") || type.equals("n")) {
					List<String> botNames = Arrays.asList(
						"Luke Skywalker", "Obiwan Kenobi", "Han Solo", 
						"Darth Vader", "Leia Organa", "Yoda", 
						"Anakin Skywalker", "Padmé Amidala", "Mace Windu", 
						"Qui-Gon Jinn", "Ahsoka Tano", "Rey", 
						"Kylo Ren", "Finn", "Poe Dameron"
					);
					Random random = new Random();
					boolean validName = false;
					String botPseudo = botNames.get(random.nextInt(botNames.size()));
					// Make sure that 2 bots don't have the same pseudo
					while (!validName) {
						botPseudo = botNames.get(random.nextInt(botNames.size()));
						validName = !chosenPseudos.contains(botPseudo);
					}
					Colors botColor = availableColors.remove(0);
					Bot bot = new RandomBot(this, botColor);
					bot.setPseudo(botPseudo);
					this.players[i] = bot;
					validInput = true;
				} else {
					System.out.println("Entrée invalide. Veuillez répondre par 'oui' ou 'non'.");
				}
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

	public int findSectorId(Sector sector) {
		int id = 0;
		for (int i = 0; i < this.sectors.length; i++) {
			if (this.sectors[i] == sector) id = i;
		}
		return id;
	}

	//Ask all the players to place their initial fleet
	public void setupFleets() {
		for (Player player: this.players) {
			player.setupInitialFleet();
			this.pcs.firePropertyChange("hexUpdated", null, null);
		}
		for (int i = 0; i < this.players.length; i++) {
			this.players[this.players.length-1-i].setupInitialFleet();
			this.pcs.firePropertyChange("hexUpdated", null, null);
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

/*		this.efficiencies = new Integer[][] {
				{1,1,1},
				{1,1,1},
				{1,1,1}
		};*/

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

		for (Player player : players) {
			player.resetOrderCommands();
			player.resetShips();
		}

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
		this.pcs.firePropertyChange("hexUpdated", null, null);
		this.doScore();
		this.round++;
		this.pcs.firePropertyChange("roundOver", null, null);

	}
	
	public void playRoundStep() {
		for (int j=0; j<NB_PLAYERS; j++) {
			this.orderPlayers[this.round_step][j].doAction(this.round_step, this.efficiencies[this.round_step][j]);
			triggerInterfaceUpdate();
		}
	}

	public void triggerInterfaceUpdate() {
		this.pcs.firePropertyChange("hexUpdated", null, null);
	}

	// Remove excess ships on every hexagon
	public void sustainShips() {
		System.out.println("Suppression des vaisseaux en trop...");
		for (int i = 0; i < MAP_ROWS; i++) {
			for (int j = 0; j < MAP_COLS; j++) {
				Hexagon hex = hexs[i][j];
				if (hex != null) {
					int systemLevel = hex.getSystemLevel();
					int maxShips = 1 + systemLevel;
					List<Ship> ships = hex.getShips();
					if (ships.size() > maxShips) {
						int shipsToRemove = ships.size() - maxShips;
						// Create a copy of the list of ships to destroy, then delete them by iterating over this copy
						List<Ship> shipsToDestroy = new ArrayList<>(ships.subList(0, shipsToRemove));
						for (Ship ship : shipsToDestroy) {
							ship.destroy(); // Return the ship to the reserve
						}
					}
				}
			}
		}
	}

	// Return the controller of the TriPrime (if existing)
	public Player getTriPrimeController() {
		// Get the TriPrime hexagon
		Hexagon triPrimeHex = this.hexs[4][2]; 
		if (triPrimeHex != null && triPrimeHex.isTriPrime()) {
			// Return the occupant of TriPrime
			return triPrimeHex.getOccupant();
		}
		return null; // If no occupant
	}


	// Calculate the score of each player
	public void doScore() {
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
			System.out.println(triPrimeController.getPseudo() + " controle le Tri-Prime");
			System.out.println("Il peut choisir un secteur supplémentaire");
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
					if (system.getHex().getOccupant() == player) {
						score += system.getLevel();
					}
				}
			}
			player.addScore(score);
			System.out.println("Le score de " + player.getPseudo() + " est " + score);
		}

		this.pcs.firePropertyChange("scoreUpdated", null, null);
	}

	// Calculate the final score of each player
	public void doFinalScore() {
		System.out.println("Calcul du score final...");
		
		// All sectors are scored again with doubled values
		for (Player player : this.players) {
			int finalScore = player.getScore(); // Add the points already scored during the game
			for (Sector sector : this.sectors) {
				for (HSystem system : sector.getSystems()) {
					if (system.getHex().getOccupant() == player) {
						int systemValue = system.getLevel() * 2; // Double the value of the system
						finalScore += systemValue;
					}
					}
			}
			System.out.println("Score final de " + player.getPseudo() + ": " + finalScore);
			player.setScore(finalScore);
		}
		
		// Determine the winner
		Player winner = this.getWinner();

		System.out.println("Le gagnant est " + winner.getPseudo() + " avec " + winner.getScore() + " points!");

		this.stopGame();
	}

	// Get the winner of the game
	public Player getWinner() {
		Player winner = this.players[0];
		for (Player player : this.players) {
			if (player.getScore() > winner.getScore()) {
				winner = player;
			}
		}

		return winner;
	}

/*	// Assert that the expand move from the player is valid
	public boolean checkExpandValidity(List<Ship> ships) {
		// Check that no ship is expanded twice
		Set<Ship> uniqueShips = new HashSet<>(ships);
		boolean notTwice = uniqueShips.size() == ships.size();

		// Get all the possible ships to expand on
		List<Ship> possShips = possibilities.expand(ships.getFirst().getOwner());
		boolean allPossible = possShips.containsAll(ships);

		return notTwice && allPossible;
	}*/

	public boolean checkExpandValidity(Hexagon hex, Player player) {

		// Checks that the user controls all the selected hexagons
/*		boolean controlsHexs = true;
		for (Hexagon hex : hexs) {
			if (hex.getOccupant() != player) controlsHexs = false;
		}*/

		boolean controlsHex = hex.getOccupant() == player;

		// Get all the ships expandable on the desired location
		List<Ship> possibleShips = possibilities.expand(player).stream()
				.filter(ship -> ship.getPosition() == hex) // Condition: keep ships that have not expanded yet
				.toList();

		boolean enoughShips = !possibleShips.isEmpty();

/*		boolean enoughShips = true;
		List<Ship> ships = new ArrayList<>();
		try {
			for (Hexagon hex : new HashSet<Hexagon>(hexs)) {
				int hexOccurences = Collections.frequency(hexs, hex);
				for (int i = 0; i < hexOccurences; i++) {
					ships.add(hex.getShips().get(i));
				}
			}
		} catch (Exception e) {
			enoughShips = false;
		}*/

/*		System.out.println("controls hexs : " + controlsHexs);
		System.out.println("enough ships : " + enoughShips);*/


/*		// Get all the possible ships to expand on
		List<Ship> possShips = possibilities.expand(ships.getFirst().getOwner());
		boolean allPossible = possShips.containsAll(ships);*/

		return controlsHex && enoughShips;

	}
	
	// Assert that the explore move from the player is valid
	public boolean checkExploreValidity(Pair<List<Ship>, List<Hexagon>> move) {

		// Check that no ship is moved twice
/*		Set<Hexagon> origins = new HashSet<Hexagon>();
		for (Pair<List<Ship>, List<Hexagon>> move : moves) {
			origins.add(move.getKey().getFirst().getPosition());
		}*/

/*		List<Ship> goodShips = move.getKey().stream()
				.filter(ship -> !ship.hasExplored()) // Condition: keep ships that have not explored yet
				.toList();

		boolean notTwice = move.getKey().size() == goodShips.size();*/

		//boolean notTwice = origins.size() == moves.size();

		// Check that all the moves are possible
		List<Pair<List<Ship>, List<Hexagon>>> possibleMoves = possibilities.explore(move.getKey().getFirst().getOwner());
		boolean possible = possibleMoves.contains(move);

		return possible;
	}

	// Assert that the exterminate move from the player is valid
	public boolean checkExterminateValidity(Pair<Set<Ship>, Hexagon> move, Player player) {

/*		// Check that no system is attacked twice
		Set<Hexagon> targets = new HashSet<Hexagon>();
		for (Pair<Set<Ship>, Hexagon> move : moves) {
			targets.add(move.getValue());
		}
		boolean notTwice = targets.size() == moves.size();*/

		// Check that all the moves are valid
		List<Pair<Set<Ship>, Hexagon>> possibleMoves = possibilities.exterminate(player);
		boolean Possible = possibleMoves.contains(move);

		//Debugger.displayAllExterminateMoves(possibleMoves, player);

/*		for (Pair<Set<Ship>, Hexagon> move : moves) {
			System.out.println("Moved checked :");
			Debugger.displayExterminateMove(move);
		}*/


		return Possible;

	}

	public Hexagon[][] getMap() {
		return this.hexs;
	}

	public Sector[] getSectors() {
		return this.sectors;
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

	public void run() {

		// Setup the game if it has just been created
		if (this.round == 0) {
			this.setup();
		}
		// In case we are loading an existing game, instantiate the scanner and possibilities (which is transient)
		else {
			this.scanner = new Scanner(System.in);
			for (Player p : this.players) {
				if (p instanceof Bot) ((Bot) p).setPossibilities();
			}
		}

		while (this.round < 9) {
			this.playRound();
	
			// Verifies if a player lost all his ships
			for (Player player : this.players) {
				if (player.countShips() == 0) {
					System.out.println(player.getPseudo() + " lost all his ships.");
					break;
				}
			}
		}
	
		// Final scoring
		this.doFinalScore();
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}

	//Main method
	public static void main(String[] args) {
		System.out.println("Lancez le jeu depuis GameController.js");
	}

}
