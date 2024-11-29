package pimperium;
import javafx.util.Pair;

import java.util.*;


public class Human extends Player {


	public Human(Game game) {
		super(game);
	}

	//Choose a lvl-1 system to place 2 ships
	public void setupInitialFleet() {

		int i = -1, j = -1;

		boolean validInput = false;

		// Loop until valid input is received
		while (!validInput) {
			try {
				System.out.println(this.getPseudo() + ", entrez la position de l'hexagone où vous souhaitez placer votre flotte (i j) : ");
				i = this.game.scanner.nextInt();
				j = this.game.scanner.nextInt();

				// Validate the coordinates
				if (i < 0 || i >= Game.MAP_ROWS || j < 0 || j >= Game.MAP_COLS) {
					System.out.println("Coordonnées invalides. Veuillez réessayer.");
					continue; // Ask for input again
				}

				Hexagon hex = this.game.getMap()[i][j];
				if (hex == null || hex.getSystem() == null || hex.getSystem().getLevel() != 1) {
					System.out.println("Hexagone invalide pour placer la flotte. Veuillez choisir un autre hexagone.");
					continue; // Ask for input again
				}

				// Check that the hex is not already occupied by anyone and not empty
				if (hex.getSystem().getController() != null && hex.getSystem().getController() != this) {
					System.out.println("Ce système est déjà contrôlé par un autre joueur. Veuillez choisir un autre hexagone.");
					continue; // Ask for input again
				}

				if (!hex.getShips().isEmpty()) {
					System.out.println("Cet hexagone contient déjà des vaisseaux. Veuillez choisir un autre hexagone.");
					continue; // Ask for input again
				}


				// Check that the hex is in a unoccupied sector
				if (game.findSector(hex).isOccupied()) {
					System.out.println("Cet hexagone se situe dans un secteur déjà controlé par un joueur. Veuillez choisir un autre hexagone.");
					continue; // Ask for input again
				}

				// Add ships to the selected hexagon
				this.createShip(hex);
				this.createShip(hex);

				System.out.println("Deux navires de " + this.getPseudo() + " ont été placés sur l'hexagone " + hex);

				validInput = true; // Exit the loop
			} catch (Exception e) {
				System.out.println("Entrée invalide. Veuillez entrer deux entiers séparés par un espace.");
				this.game.scanner.nextLine(); // Clear the invalid input
			}
		}

	}

	public void chooseOrderCommands() {

		int[] order = new int[3];
		int input;
		Set<Integer> alreadyChosen = new HashSet<Integer>();
		System.out.println(this.getPseudo() +" Choisissez l'ordre des commandes (0: Expand, 1: Explore, 2: Exterminate) pour le tour : ");
		for (int i = 0; i < 3; i++) {
			boolean validInput = false;
			while (!validInput) {
				System.out.print("Commande " + (i + 1) + " : ");
				input = this.game.scanner.nextInt();
				if (input < 0 || input > 2 || alreadyChosen.contains(input)) {
					System.out.println("Commande invalide. Veuillez réessayer.");
				} else {
					validInput = true;
					alreadyChosen.add(input);
					order[i] = input;
				}
			}

		}

		this.orderCommands = order;

	}

	public void doExpand(int efficiency) {

		System.out.println(this.getPseudo() + " is expanding");

		boolean validity = false;

		List<Ship> expandShips = new ArrayList<Ship>();
		while (!validity) {
			expandShips = new ArrayList<Ship>();
			for (int i=0; i<efficiency; i++) {

				System.out.println("Enter the index of the ship you want to expand : " );
				int index = this.game.scanner.nextInt();
				expandShips.add(this.ships.get(index));

			}

			validity = game.checkExpandValidity(expandShips);
		}

		//Set the ships and execute the command
		this.expand.setShips(expandShips);
		this.expand.execute();

	}

	public void doExplore(int efficiency) {

		//TODO Complexify user-input (Move only a part of the ships of the fleet/drop ships on the way/
		// add ships to the fleet on the way)

		System.out.println(this.getPseudo() + " is exploring");

		List<Pair<List<Ship>, List<Hexagon>>> moves = new ArrayList<>();

		boolean validMoves = false;

		while (!validMoves) {

			boolean newMove = true;
			moves = new ArrayList<>();
			while (moves.size() < efficiency && newMove) {

				boolean validInput = false;
				int i, j;

				// Loop until valid input is received
				while (!validInput) {
					try {
						System.out.println(this.getPseudo() + ", entrez la position de la flotte que vous voulez déplacer (i j) : ");
						i = this.game.scanner.nextInt();
						j = this.game.scanner.nextInt();

						List<Ship> fleet = this.game.getMap()[i][j].getShips();

						// Check that the user chose a hex he has a fleet on
						if (fleet.isEmpty() || fleet.getFirst().getOwner() != this) {
							throw(new Exception());
						}

						System.out.println(this.getPseudo() + ", entrez la position où vous voulez déplacer la flotte (i j) : ");
						i = this.game.scanner.nextInt();
						j = this.game.scanner.nextInt();

						Hexagon target = this.game.getMap()[i][j];
						if (target == null) {
							throw(new Exception());
						}

						// Add the move to the moves list
						moves.add(new Pair<>(fleet, new ArrayList<>(Collections.nCopies(fleet.size(), target))));

						validInput = true; // Exit the loop
					} catch (Exception e) {
						System.out.println("Entrée invalide. Veuillez entrer deux entiers séparés par un espace.");
						this.game.scanner.nextLine(); // Clear the invalid input
					}

				}

				if (moves.size() < efficiency) {
					// Ask the user if he wants to move another fleet
					validInput = false;
					while (!validInput) {
						System.out.print("Voulez-vous déplacer une autre flotte ? (0/1) : ");
						try {
							int input = this.game.scanner.nextInt();
							if (input != 0 && input != 1) {
								throw(new Exception());
							}
							newMove = input == 1;
							validInput = true;
						} catch (Exception e) {
							System.out.println("Entrée invalide. Veuillez entrer 0 ou 1");
						}

					}
				}

			}

			validMoves = game.checkExploreValidity(moves);

			if (!validMoves) {
				System.out.println("Le coup que vous avez essayé de jouer n'est pas valide. Veuillez réessayer");
			}
		}


		// Execute each move
		for (Pair<List<Ship>, List<Hexagon>> move : moves) {
			//Set the ships and execute the command
			this.explore.setShips(move.getKey());
			this.explore.setTargets(move.getValue());
			this.explore.execute();
		}

	}

	public void doExterminate(int efficiency) {

		System.out.println(this.getPseudo() + " is exterminating");

		boolean validity = false;

		List<Ship> exterminateShips = new ArrayList<Ship>();
		List<Hexagon> targetHexagons = new ArrayList<Hexagon>();

		while (!validity) {
			exterminateShips = new ArrayList<Ship>();
			targetHexagons = new ArrayList<Hexagon>();
			for (int i=0; i<efficiency; i++) {

				System.out.println("Enter the index of the ship you want to send to fight : " );
				int indexShip = this.game.scanner.nextInt();
				exterminateShips.add(this.ships.get(indexShip));
				System.out.println("Enter the position of the hex you want to attack : " );
				int i_hex = this.game.scanner.nextInt();
				int j_hex = this.game.scanner.nextInt();

				Hexagon hex = this.game.getMap()[i_hex][j_hex];
				targetHexagons.add(hex);

			}

			validity = game.checkExterminateValidity(exterminateShips, targetHexagons);
		}

		//Set the ships and execute the command
		this.exterminate.setShips(exterminateShips);
		this.exterminate.setTargets(targetHexagons);
		this.exterminate.execute();

	}

	public static void main(String[] args) {

	}

}
