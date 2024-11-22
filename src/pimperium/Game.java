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
		this.players = new Player[NB_PLAYERS];
		Scanner scanner = new Scanner(System.in);
		Set<String> usedPseudos = new HashSet<>();
	
		for (int i = 0; i < NB_PLAYERS; i++) {
			String pseudo;
			while (true) {
				System.out.print("Le joueur " + (i + 1) + " est-il un humain ? (oui/non) : ");
				String type = scanner.nextLine().trim().toLowerCase();
	
				if (type.equals("oui") || type.equals("o")) {
					System.out.print("Entrez le pseudo pour le joueur " + (i + 1) + " : ");
					pseudo = scanner.nextLine().trim();
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
					pseudo = botNames.get(random.nextInt(botNames.size()));
				}
	
				if (!usedPseudos.contains(pseudo)) {
					usedPseudos.add(pseudo);
					break;
				} else {
					System.out.println("Le pseudo " + pseudo + " est déjà pris. Veuillez en choisir un autre.");
				}
			}
	
			Human player = new Human(this);
			player.setPseudo(pseudo);
			this.players[i] = player;
		}
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
		// Verify that each ship moves to a different position
		Set<Ship> uniqueShips = new HashSet<>(ships);
		Set<Hexagon> uniqueTargets = new HashSet<>(targets);
		return uniqueShips.size() == ships.size() && uniqueTargets.size() == targets.size();
	}


	//Assert that the exterminate move from the player is valid
	public boolean checkExterminateValidity(List<Ship> ships, List<Hexagon> targets) {
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

		game.generateMap();
    	game.displayBoard();

		//Should put all of this is a setup() method
		game.createHexNeighbours();
		game.createPlayers();
		game.setupFleets();
		game.playRound();
		
		//System.out.println(game.displayMap());


	}

}
