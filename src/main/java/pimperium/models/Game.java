package pimperium.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

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
import pimperium.players.Player;
import pimperium.utils.Possibilities;


/**
 * The model in the VCM design pattern, performing all the game logic
 */
public class Game implements Runnable, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Number of rows of the map
	 */
	public static final int MAP_ROWS = 9;
	/**
	 * Number of columns of the map
	 */
	public static final int MAP_COLS = 6;
	/**
	 * Number of players
	 */
	public static final int NB_PLAYERS = 3;
	/**
	 * Max amount of ships a player can control
	 */
	public static final int MAX_SHIPS = 15;
	/**
	 * Delay between bot actions to make the game comprehensible for the user
	 */
	public static final int DELAY = 2000;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private int round;
	private int round_step;
	private boolean gameEnded;
	public boolean viewInitialized = false;
	/**
	 * The map stored as a matrix of hexagons. The last hexagon of odd lines will be null (because we deal with hexagons and not squares)
	 */
	private Hexagon[][] hexs;
	/**
	 * The list of sectors ordered from left to right, top to bottom
	 */
	private Sector[] sectors;
	/**
	 * The list of players, with order switched each round
	 */
	private Player[] players;
	/**
	 * Matrix of players storing the play order for the round. Line i stores the order for the ith action of the round
	 */
	private Player[][] orderPlayers;
	/**
	 * Matrix of integers storing the action efficiencies for the round. Line i stores the efficiencies for the ith step of the round
	 */
	private Integer[][] efficiencies;
	/**
	 * Instance of Possibilities used to generate all possible moves for each action
	 */
	private  Possibilities possibilities;

	// Non-serializable variables
	public transient Scanner scanner = new Scanner(System.in);
	private transient Thread t;
	private transient GameController controller;

	/**
	 * Constructor of Game initializing attributes
	 */
	public Game() {
		this.round = 0;
		this.hexs = new Hexagon[MAP_ROWS][MAP_COLS];
		this.sectors = new Sector[9];
		this.possibilities = Possibilities.getInstance(this);
		this.gameEnded = false;
	}

	/**
	 * Set the instance of game controller
	 *
	 * @param controller Instance of controller that links the game with the view
	 */
	public void setController(GameController controller) {
        this.controller = controller;
    }

	/**
	 * Get the instance of game controller
	 *
	 * @return the instance of game controller
	 */
    public GameController getController() {
        return this.controller;
    }

	/**
	 * Set the attribute storing the state of the view
	 */
	public void setViewInitialized() {
		viewInitialized = true;
	}

	/**
	 * Get the list of players
	 *
	 * @return the list of players as a length 3 array
	 */
	public Player[] getPlayers() {
		return this.players;
	}

	/**
	 * Set the list of players
	 *
	 * @param players The array of players (length 3)
	 */
	public void setPlayers(Player[] players) {
		this.players = players;
	}

	/**
	 * Get the round number
	 *
	 * @return round number
	 */
	public int getRound() {
		return this.round;
	}

	/**
	 * Initial setup of the map
	 */
	public void setup() {
		this.generateMap();
		this.createHexNeighbours();
		this.createTriPrime();
		System.out.println("Plateau de jeu :");
		System.out.println(this.displayMap());
	}

	/**
	 * Start the game thread and launch the game
	 */
	public void startGame() {
		this.t = new Thread(this, "Game");
		this.t.start();
	}

	/**
	 * Stop the game thread
	 */
	public void stopGame() {
		this.gameEnded = true;
		if (t != null) {
			t.interrupt();
		}
		if (scanner != null) {
			scanner.close();
		}
	}

	/**
	 * Random generation of the map by shuffling sector cards
	 */
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
				hex.addSystem(systems.get(j));

			}
		}
				
	}

	/**
	 * Research of the set of neighbors for each hex of the map
	 */
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

	/**
	 * Gather the 4 middle hexagons to form TriPrime
	 */
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
			neighbor.getNeighbours().removeAll(formerTriPrimes); // Remove old neighbors
			neighbor.getNeighbours().add(triPrime); // Add Tri-Prime as a neighbor
		}

		for (int[] coord : centralHexCoords) {
			int i = coord[0];
			int j = coord[1];
			hexs[i][j] = triPrime;
		}

		triPrime.setNeighbours(triPrimeNeighbors);
		triPrimeNeighbors.removeAll(formerTriPrimes);

	}

	/**
	 * Retrieve the sector that one hexagon belongs to
	 *
	 * @param hex A hexagon containing a system
	 * @return the sector containing the hexagon
	 */
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

	/**
	 * Find the id of a sector
	 *
	 * @param sector One of the 9 sectors
	 * @return the id of the sector
	 */
	public int findSectorId(Sector sector) {
		int id = 0;
		for (int i = 0; i < this.sectors.length; i++) {
			if (this.sectors[i] == sector) id = i;
		}
		return id;
	}

	/**
	 * Ask all the players to place their 2 initial fleets
	 */
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

	/**
	 * Calculate the play order and the efficiency of each action for the 3 steps of the round to go
	 */
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
						this.efficiencies[i][j] = 1;
						break;
					case 2:
						this.efficiencies[i][j] = 2;
						break;
					default:
						this.efficiencies[i][j] = 3;
						break;
				}
			}
		}

	}

	/**
	 * Change the default play order between players
	 */
	public void switchStartPlayer() {
		Player temp = this.players[0];
		for (int i = 0; i < this.players.length-1; i++) {
			this.players[i] = this.players[i+1];
		}
		this.players[this.players.length-1] = temp;
	}

	/**
	 * Play a full round
	 */
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

	}

	/**
	 * Play a round step (one action from each player)
	 */
	public void playRoundStep() {
		for (int j=0; j<NB_PLAYERS; j++) {
			this.orderPlayers[this.round_step][j].doAction(this.round_step, this.efficiencies[this.round_step][j]);
			triggerInterfaceUpdate();
        }
	}

	/**
	 * Notify the controller that the map has changed
	 */
	public void triggerInterfaceUpdate() {
		this.pcs.firePropertyChange("hexUpdated", null, null);
	}

	/**
	 * Perform the sustaining of ships at the end of the round (destroy excess ships)
	 */
	public void sustainShips() {
		System.out.println("Suppression des vaisseaux en trop...");
		this.getController().getView().addLogMessage("Suppression des vaisseaux en trop...", null, "normal");
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

	/**
	 * Retrieve the player currently controlling TriPrime
	 *
	 * @return the player controlling TriPrime if it exists
	 */
	public Player getTriPrimeController() {
		// Get the TriPrime hexagon
		Hexagon triPrimeHex = this.hexs[4][2]; 
		if (triPrimeHex != null && triPrimeHex.isTriPrime()) {
			// Return the occupant of TriPrime
			return triPrimeHex.getOccupant();
		}
		return null; // If no occupant
	}

	/**
	 * Perform the scoring of the round by asking each player to choose a sector to score
	 */
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
			this.getController().getView().addLogMessage("Contrôle le Tri-Prime, il peut choisir un secteur supplémentaire.", triPrimeController, "normal");
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

	/**
	 * Perform the final scoring step at the end of the game where points are doubled
	 */
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
		this.getController().getView().addLogMessage("Le gagnant est " + winner.getPseudo() + " avec " + winner.getScore() + " points!", null, "bold");

		this.pcs.firePropertyChange("scoreUpdated", null, null);

		this.stopGame();
	}

	/**
	 * Find the winner by comparing scores
	 * @return the winner of the game
	 */
	public Player getWinner() {
		Player winner = this.players[0];
		for (Player player : this.players) {
			if (player.getScore() > winner.getScore()) {
				winner = player;
			}
		}

		return winner;
	}

	/**
	 * Check the validity of an Expand move tried by a player
	 *
	 * @param hex The hexagon where the Expand would be performed
	 * @param player The player trying the move
	 * @return The validity of the move as a boolean
	 */
	public boolean checkExpandValidity(Hexagon hex, Player player) {

		boolean controlsHex = hex.getOccupant() == player;

		// Get all the ships expandable on the desired location
		List<Ship> possibleShips = possibilities.expand(player).stream()
				.filter(ship -> ship.getPosition() == hex) // Condition: keep ships that have not expanded yet
				.toList();

		boolean enoughShips = !possibleShips.isEmpty();

		return controlsHex && enoughShips;

	}

	/**
	 * Check the validity of an Explore move tried by a player
	 *
	 * @param move The move tried by the player, stored as a list of ships assigned to a list of destinations
	 * @return The validity of the move as a boolean
	 */
	public boolean checkExploreValidity(Pair<List<Ship>, List<Hexagon>> move) {

		List<Pair<List<Ship>, List<Hexagon>>> possibleMoves = possibilities.explore(move.getKey().getFirst().getOwner());

		return possibleMoves.contains(move);
	}

	/**
	 * Check the validity of an Exterminate move tried by a player
	 *
	 * @param move The move tried by the player, stored as a list of ships assigned to a hexagon
	 * @param player The player performing the move
	 * @return The validity of the move as a boolean
	 */
	public boolean checkExterminateValidity(Pair<Set<Ship>, Hexagon> move, Player player) {


		// Check that all the moves are valid
		List<Pair<Set<Ship>, Hexagon>> possibleMoves = possibilities.exterminate(player);

		return possibleMoves.contains(move);

	}

	/**
	 * Get the map
	 *
	 * @return the map as a 9*6 array of hexagons
	 */
	public Hexagon[][] getMap() {
		return this.hexs;
	}

	/**
	 * Get the sectors
	 *
	 * @return the sectors as a length 9 array
	 */
	public Sector[] getSectors() {
		return this.sectors;
	}

	/**
	 * Builds a printable String that represents the map
	 *
	 * @return the map in the form of a String
	 */
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

	/**
	 * Start the game and loop until it's over
	 */
	public void run() {

		// Setup the game if it has just been created
		if (this.round == 0) {
			this.setupFleets();
		}
		// In case we are loading an existing game, instantiate the scanner and possibilities (which is transient)
		else {
			this.scanner = new Scanner(System.in);
			for (Player p : this.players) {
				if (p instanceof Bot) ((Bot) p).setPossibilities();
			}
		}

		while (this.round < 9 && !gameEnded) {
			this.playRound();
	
			// Verifies if a player lost all his ships
			for (Player player : this.players) {
				if (player.countShips() == 0) {
					System.out.println(player.getPseudo() + " lost all his ships.");
					this.getController().getView().addLogMessage(player.getPseudo() + " a perdu tous ses vaisseaux.", null, "normal");
					player.setScore(0);
					gameEnded = true;
				}
			}
			this.round++;
			this.pcs.firePropertyChange("roundOver", null, null);
		}
	
		// Final scoring
		this.doFinalScore();
	}

	/**
	 * Add the Property Change Listener to the game Property Change Support
	 *
	 * @param pcl The Property Change Listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}

}
