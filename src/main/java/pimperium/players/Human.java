package pimperium.players;

import java.util.*;

import javafx.util.Pair;

import pimperium.elements.Hexagon;
import pimperium.elements.Sector;
import pimperium.elements.Ship;
import pimperium.models.Game;
import pimperium.utils.Colors;
import pimperium.utils.Debugger;
import pimperium.utils.Possibilities;


public class Human extends Player {


	public Human(Game game, Colors color) {
		super(game, color);
	}

	/*
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
				if (hex.getOccupant() != null && hex.getOccupant() != this) {
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

	} */

	// Chooses a lvl-1 system to place 2 ships
	public void setupInitialFleet() {
        System.out.println(this.getPseudo() + ", cliquez sur l'hexagone où vous souhaitez placer votre flotte.");

        synchronized (game.getController()) {
            try {

				Hexagon hex = this.game.getController().waitForHexagonSelection();

                // Vérifier que l'hexagone est valide pour le placement initial
                if (hex == null || hex.getSystem() == null || hex.getSystem().getLevel() != 1) {
					System.out.println("Hexagone invalide pour placer la flotte. Veuillez choisir un autre hexagone.");
					setupInitialFleet(); // Ask for input again
					return;
				}

				// Check that the hex is not already occupied by anyone and not empty
				if (hex.getOccupant() != null && hex.getOccupant() != this) {
					System.out.println("Ce système est déjà contrôlé par un autre joueur. Veuillez choisir un autre hexagone.");
					setupInitialFleet(); // Ask for input again
					return;
				}

				if (!hex.getShips().isEmpty()) {
					System.out.println("Cet hexagone contient déjà des vaisseaux. Veuillez choisir un autre hexagone.");
					setupInitialFleet(); // Ask for input again
					return;
				}

				// Check that the hex is in a unoccupied sector
				if (game.findSector(hex).isOccupied()) {
					System.out.println("Cet hexagone se situe dans un secteur déjà controlé par un joueur. Veuillez choisir un autre hexagone.");
					setupInitialFleet(); // Ask for input again
					return;
				}

                // Placer les navires sur l'hexagone sélectionné
                this.createShip(hex);
                this.createShip(hex);

                System.out.println("Deux navires de " + this.getPseudo() + " ont été placés sur l'hexagone " + hex);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

	/*
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

	} */

	public void chooseOrderCommands() {
        System.out.println(this.getPseudo() + ", veuillez choisir l'ordre des commandes.");

		// Display the command selection interface
		game.getController().getView().showCommandSelection(this);

		// Wait for the player to select the commands
		synchronized (game.getController()) {
			while (this.orderCommands == null) {
				try {
					game.getController().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

		@Override
		public Sector chooseSectorToScore(Set<Sector> scoredSectors, Sector[] sectors) {

			boolean validChoice = false;
			Sector sector = null;

			System.out.println(this.getPseudo() + " choisit un secteur à scorer");

			while (!validChoice) {
				try {
					// Wait for the player to select the hexagon
					sector = game.getController().waitForSectorSelection();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (sector == null) {
					System.out.println("Secteur invalide. Veuillez réessayer.");
				} else if (sector.isTriPrime()) {
					System.out.println("Vous ne pouvez pas choisir le secteur Tri-Prime. Veuillez choisir un autre secteur.");
				} else if (scoredSectors.contains(sector)) {
					System.out.println("Ce secteur a déjà été choisi. Veuillez choisir un autre secteur.");
				} else {
					validChoice = true;
				}

			}

			int sectorId = this.game.findSectorId(sector);
			System.out.println(this.getPseudo() + " a choisi le secteur " + sectorId + " à scorer.");
			return sector;

		}

	/*
	@Override
	public Sector chooseSectorToScore(Set<Sector> scoredSectors, Sector[] sectors) {
		Scanner scanner = this.game.scanner;
		Sector chosenSector = null;
		boolean validChoice = false;
		int sectorId = -1;
		System.out.println(this.getPseudo() + " is choosing a sector to score");
		
		while (!validChoice) {
			try {
				System.out.println(this.getPseudo() + ", choose a sector to score [1-4 | 6-9]:");
				sectorId = scanner.nextInt();
				
				if (sectorId < 1 || sectorId > sectors.length || sectorId == 5) {
					System.out.println("Invalid sector ID. Please try again.");
					continue;
				}
				
				chosenSector = sectors[sectorId-1];

				if (chosenSector == null) {
					System.out.println("Invalid sector. Please try again.");
				} else if (chosenSector.isTriPrime()) {
					System.out.println("You cannot choose the Tri-Prime sector. Please choose another one.");
				} else if (!chosenSector.isOccupied()) {
					System.out.println("You cannot choose an unoccupied sector. Please choose another one.");
				} else if (scoredSectors.contains(chosenSector)) {
					System.out.println("This sector has already been chosen. Please choose another one.");
				} else {
					validChoice = true;
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Please enter a number.");
				scanner.nextLine(); // Clear the invalid input
			}
		}
		System.out.println(this.getPseudo() + " has chosen sector " + sectorId + " to score.");
		return chosenSector;
	}*/

/*	
	// Without the clickable hexagons
	public void doExpand(int efficiency) {

		System.out.println(this.getPseudo() + " is expanding");

		boolean validity = false;

		List<Ship> expandShips = new ArrayList<Ship>();
		while (!validity) {
			expandShips.clear();
			for (int i=0; i<efficiency; i++) {

				System.out.println("Enter the index of the ship you want to expand : " );
				int index = this.game.scanner.nextInt();
				if (index < 0 || index >= this.ships.size()) {
					System.out.println("Index out of range. Please enter a valid index.");
					i--; // Decrement i to retry this iteration
					continue;
				}
				expandShips.add(this.ships.get(index));

			}

			validity = game.checkExpandValidity(expandShips);
		}

		//Set the ships and execute the command
		this.expand.setShips(expandShips);
		this.expand.execute();

	}*/

	// With the clickable hexagons
	public void doExpand(int efficiency) {

		System.out.println(this.getPseudo() + " rajoute un vaisseau avec une efficacité de " + efficiency);

		//List<Hexagon> expandHexs = new ArrayList<>();

		//boolean validMove = false;
		boolean validMove = false;
		Hexagon hex = new Hexagon(0,0);

		//expandHexs.clear();

		for (int i = 0; i < efficiency; i++) {

			validMove = false;

			while (!validMove) {

				System.out.println(this.getPseudo() + ", cliquez sur l'hexagone où vous souhaitez placer votre vaisseau.");

				try {
					// Wait for the player to select the hexagon
					hex = game.getController().waitForHexagonSelection();
					//expandHexs.add(hex);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				validMove = this.game.checkExpandValidity(hex, this);

				if (!validMove) {
					System.out.println("L'hexagone que vous avez choisi n'est pas valide. Veuillez réessayer");
				}


			}

			// Set the ships and execute the command
			List<Ship> ships = new ArrayList<>();
			ships.add(hex.getShips().stream()
					.filter(ship -> !ship.hasExpanded())
					.toList().getFirst());
			this.expand.setShips(ships);
			this.expand.execute();

			this.game.triggerInterfaceUpdate();

		}

/*		// Set the ships and execute the command
		List<Ship> ships = new ArrayList<>();
		for (Hexagon hex : new HashSet<>(expandHexs)) {
			int hexOccurrences = Collections.frequency(expandHexs, hex);
			for (int i = 0; i < hexOccurrences; i++) {
				ships.add(hex.getShips().get(i));
			}
		}
		this.expand.setShips(ships);
		this.expand.execute();*/
	}


	// With the clickable hexagons
	public void doExplore(int efficiency) {

		System.out.println(this.getPseudo() + " explore le plateau avec une efficacité de " + efficiency);

		//List<Pair<List<Ship>, List<Hexagon>>> moves = new ArrayList<>();
		Pair<List<Ship>, List<Hexagon>> move = new Pair<>(new ArrayList<>(),new ArrayList<>());

		int movesDone = 0;

		boolean validMoves = false;
		boolean validMove = false;

		boolean newMove = true;

		List<Pair<List<Ship>, List<Hexagon>>> possibleMoves = Possibilities.getInstance(game).explore(this);

		while (movesDone < efficiency && newMove && !possibleMoves.isEmpty()) {

			validMove = false;

			while (!validMove) {

				boolean validInput = false;

				while (!validInput) {
					try {
						System.out.println(this.getPseudo() + ", cliquez sur la flotte que vous souhaitez déplacer.");

						// Wait for the player to select the origin hexagon
						Hexagon originHex = game.getController().waitForHexagonSelection();

						// Verify that the hex contains the player's ships
						if (originHex == null || originHex.getShips().isEmpty() || originHex.getOccupant() != this) {
							System.out.println("Cet hexagone ne contient pas vos vaisseaux. Veuillez réessayer.");
							continue;
						}

						int numShips = 0;

						if (originHex.getShips().size() > 1) {
							System.out.println("Combien de vaisseaux voulez-vous déplacer depuis cet hexagone ?");
							numShips = this.game.scanner.nextInt();
						} else {
							numShips = 1;
						}

						if (numShips <= 0 || numShips > originHex.getShips().size()) {
							System.out.println("Nombre de vaisseaux invalide. Veuillez réessayer.");
							continue;
						}

						List<Ship> fleet = new ArrayList<>(originHex.getShips().subList(0, numShips));

						System.out.println(this.getPseudo() + ", cliquez sur l'hexagone de destination.");

						// Wait for the player to select the destination hexagon
						Hexagon target = game.getController().waitForHexagonSelection();

						if (target == null) {
							System.out.println("Destination invalide. Veuillez réessayer.");
							continue;
						}

						//moves.add(new Pair<>(fleet, new ArrayList<>(Collections.nCopies(fleet.size(), target))));
						move = new Pair<>(fleet, new ArrayList<>(Collections.nCopies(fleet.size(), target)));

						validInput = true; // Exit the loop
					} catch (Exception e) {
						System.out.println("Entrée invalide. Veuillez réessayer.");
						this.game.scanner.nextLine(); // Clear the invalid input
					}
				}

				validMove = this.game.checkExploreValidity(move);

				if (!validMove) {
					System.out.println("Le coup proposé n'est pas valide. Veuillez réessayer.");

					//Debugger.displayAllExploreMoves(this, this.game);
				}

			}

			this.explore.setShips(move.getKey());
			this.explore.setTargets(move.getValue());
			this.explore.execute();

			movesDone++;

			this.game.triggerInterfaceUpdate();

			possibleMoves = Possibilities.getInstance(game).explore(this);

			if (movesDone < efficiency && !possibleMoves.isEmpty()) {
				// Ask the user if he wants to move another fleet
				boolean validResponse = false;
				while (!validResponse) {
					System.out.print("Voulez-vous déplacer une autre flotte ? (0/1) : ");
					try {
						int input = this.game.scanner.nextInt();
						if (input != 0 && input != 1) {
							throw new Exception();
						}
						newMove = input == 1;
						validResponse = true;
					} catch (Exception e) {
						System.out.println("Entrée invalide. Veuillez entrer 0 ou 1");
						this.game.scanner.nextLine();
					}
				}
			} else {
				newMove = false;
			}

			//validMoves = game.checkExploreValidity(moves);

/*			if (!validMoves) {
				System.out.println("Les coups proposés ne sont pas valides. Veuillez réessayer.");
			}*/
		}

/*		// Execute each move
		for (Pair<List<Ship>, List<Hexagon>> move : moves) {
			this.explore.setShips(move.getKey());
			this.explore.setTargets(move.getValue());
			this.explore.execute();
		}*/
	}

	// With the clickable hexagons
	public void doExterminate(int efficiency) {

		System.out.println(this.getPseudo() + " extermine des sysytèmes avec une efficacité de " + efficiency);

		//List<Pair<Set<Ship>, Hexagon>> moves = new ArrayList<>();
		Pair<Set<Ship>, Hexagon> move = new Pair<>(new HashSet<>(), new Hexagon(0,0));
		Set<Hexagon> targets = new HashSet<>();
		Hexagon target = null;

		//boolean validMoves = false;

		boolean newMove = true;
		boolean validMove = false;
		boolean validInput = false;
		int movesDone = 0;

		List<Pair<Set<Ship>, Hexagon>> possibleMoves = Possibilities.getInstance(game).exterminate(this);

		//moves.clear();
		while (movesDone < efficiency && newMove && !possibleMoves.isEmpty()) {

			validMove = false;

			while (!validMove) {

				validInput = false;
				int numFlottes = 0;

				// Loop until valid input is received
				while (!validInput) {
					try {
						System.out.println(this.getPseudo() + ", cliquez sur le système que vous voulez attaquer.");

						target = game.getController().waitForHexagonSelection();

						if (target == null || target.getSystem() == null || target.getOccupant() == this) {
							System.out.println("Système invalide.");
							continue;
						}

						if (targets.contains(target)) {
							System.out.println("Vous avez déjà attaqué ce système.");
							continue;
						}

						List<Hexagon> possibleOrigins = target.getOriginsExterminate(this);

						Set<Ship> fleet = new HashSet<Ship>();

						if (possibleOrigins.size() == 0) {
							System.out.println("Aucune flotte disponible pour attaquer ce système.");
							continue;

						} else if (possibleOrigins.size() > 1) {

							System.out.println(this.getPseudo() + ", combien de flottes voulez-vous utiliser ? : ");
							numFlottes = this.game.scanner.nextInt();

							for (int k = 0; k < numFlottes; k++) {
								System.out.println(this.getPseudo() + ", cliquez sur la flotte que vous voulez utiliser (hexagone).");

								Hexagon fleetHex = game.getController().waitForHexagonSelection();

								if (fleetHex == null || fleetHex.getShips().isEmpty() || fleetHex.getOccupant() != this) {
									System.out.println("Flotte invalide.");
									continue;
								}

								fleet.addAll(fleetHex.getShips());

							}

						} else {

							fleet.addAll(possibleOrigins.getFirst().getShips());

						}

						move = new Pair<>(fleet, target);
						validMove = game.checkExterminateValidity(move, this);

						if (!validMove) {
							System.out.println("Le coup que vous avez essayé de jouer n'est pas valide. Veuillez réessayer");

							Debugger.displayAllExterminateMoves(this, this.game);
						}

						validInput = true; // Exit the loop
					} catch (Exception e) {
						System.out.println("Entrée invalide");
						this.game.scanner.nextLine(); // Clear the invalid input
					}
				}

			}

			//Execute the move
			this.exterminate.setShips(move.getKey());
			this.exterminate.setTarget(move.getValue());
			this.exterminate.execute();

			targets.add(target);

			this.game.triggerInterfaceUpdate();

			movesDone++;

			// Regenerate the possible moves to make sure the player doesn't try to play again if it's impossible
			possibleMoves = Possibilities.getInstance(game).exterminate(this);

			if (movesDone < efficiency && !possibleMoves.isEmpty()) {
				// Ask the user if he wants to attack another system
				validInput = false;
				while (!validInput) {
					try {
						System.out.print("Voulez-vous attaquer un autre système ? (0/1) : ");
						int input = this.game.scanner.nextInt();
						if (input != 0 && input != 1) {
							throw new Exception();
						}
						newMove = (input == 1);
						validInput = true;
					} catch (Exception e) {
						System.out.println("Entrée invalide. Veuillez entrer 0 ou 1");
						this.game.scanner.nextLine();
					}
				}
			} else {
				newMove = false;
			}

		}

	}


	public static void main(String[] args) {
		System.out.println("Lancez le jeu depuis GameController.js");
	}

}