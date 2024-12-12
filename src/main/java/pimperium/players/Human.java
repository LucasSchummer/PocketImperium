package pimperium.players;

import java.util.*;

import javafx.util.Pair;

import pimperium.elements.Hexagon;
import pimperium.elements.Sector;
import pimperium.elements.Ship;
import pimperium.models.Game;


public class Human extends Player {


	public Human(Game game) {
		super(game);
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

		List<Hexagon> expandHexs = new ArrayList<>();

		boolean validMove = false;

		while (!validMove) {

			expandHexs.clear();

			for (int i = 0; i < efficiency; i++) {

				System.out.println(this.getPseudo() + ", cliquez sur l'hexagone où vous souhaitez placer votre vaisseau.");

				try {
					// Wait for the player to select the hexagon
					Hexagon hex = game.getController().waitForHexagonSelection();
					expandHexs.add(hex);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			validMove = this.game.checkExpandValidity(expandHexs, this);

			if (!validMove) {
				System.out.println("Le coup que vous avez essayé de jouer n'est pas valide. Veuillez réessayer");
			}
		}

		// Set the ships and execute the command
		List<Ship> ships = new ArrayList<>();
		for (Hexagon hex : new HashSet<>(expandHexs)) {
			int hexOccurrences = Collections.frequency(expandHexs, hex);
			for (int i = 0; i < hexOccurrences; i++) {
				ships.add(hex.getShips().get(i));
			}
		}
		this.expand.setShips(ships);
		this.expand.execute();
	}

/*	
	// Without the clickable hexagons
	public void doExplore(int efficiency) {


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
*/
	
	// With the clickable hexagons
	public void doExplore(int efficiency) {

		System.out.println(this.getPseudo() + " explore le plateau avec une efficacité de " + efficiency);

		List<Pair<List<Ship>, List<Hexagon>>> moves = new ArrayList<>();
		boolean validMoves = false;
		while (!validMoves) {
			boolean newMove = true;
			moves.clear();
			while (moves.size() < efficiency && newMove) {

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

						System.out.println("Combien de vaisseaux voulez-vous déplacer depuis cet hexagone ?");

						int numShips = this.game.scanner.nextInt();

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

						moves.add(new Pair<>(fleet, new ArrayList<>(Collections.nCopies(fleet.size(), target))));

						validInput = true; // Exit the loop
					} catch (Exception e) {
						System.out.println("Entrée invalide. Veuillez réessayer.");
						this.game.scanner.nextLine(); // Clear the invalid input
					}
				}

				if (moves.size() < efficiency) {
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
			}

			validMoves = game.checkExploreValidity(moves);

			if (!validMoves) {
				System.out.println("Les coups proposés ne sont pas valides. Veuillez réessayer.");
			}
		}

		// Execute each move
		for (Pair<List<Ship>, List<Hexagon>> move : moves) {
			this.explore.setShips(move.getKey());
			this.explore.setTargets(move.getValue());
			this.explore.execute();
		}
	}

/*
	// Without the clickable hexagons
	public void doExterminate(int efficiency) {

		System.out.println(this.getPseudo() + " is exterminating");

		List<Pair<List<Ship>, Hexagon>> moves = new ArrayList<>();


		boolean validMoves = false;

		while (!validMoves) {

			boolean newMove = true;
			moves.clear();
			while (moves.size() < efficiency && newMove) {

				boolean validInput = false;
				int i, j;

				// Loop until valid input is received
				while (!validInput) {
					try {
						System.out.println(this.getPseudo() + ", entrez la position du système que vous voulez attaquer (i j) : ");
						i = this.game.scanner.nextInt();
						j = this.game.scanner.nextInt();

						Hexagon target = this.game.getMap()[i][j];

						if (target == null) {
							throw(new Exception("Système invalide."));
						}


						System.out.println(this.getPseudo() + ", combien de flottes voulez-vous utiliser ? : ");
						int numFlottes = this.game.scanner.nextInt();

						List<Ship> fleet = new ArrayList<Ship>();

						for (int k = 0; k < numFlottes; k++) {
							System.out.println(this.getPseudo() + ", entrez la position de la flotte que vous voulez utiliser (i j) : ");
							int fleetI = this.game.scanner.nextInt();
							int fleetJ = this.game.scanner.nextInt();

							Hexagon fleetHex = this.game.getMap()[fleetI][fleetJ];
							if (fleetHex == null || fleetHex.getShips().isEmpty() || fleetHex.getOccupant() != this) {
								throw new Exception("Flotte invalide.");
							}
	
							fleet.addAll(fleetHex.getShips());
						}

						// Add the move to the moves list
						moves.add(new Pair<>(fleet, target));

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
						try {
							System.out.print("Voulez-vous attaquer un autre système ? (0/1) : ");
							int input = this.game.scanner.nextInt();
							if (input != 0 && input != 1) {
								throw(new Exception());
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

			validMoves = game.checkExterminateValidity(moves);

			if (!validMoves) {
				System.out.println("Le coup que vous avez essayé de jouer n'est pas valide. Veuillez réessayer");
			}
		}


		// Execute each move
		for (Pair<List<Ship>, Hexagon> move : moves) {
			//Set the ships and execute the command
			this.exterminate.setShips(move.getKey());
			this.exterminate.setTarget(move.getValue());
			this.exterminate.execute();
		}

	}
*/



// With the clickable hexagons
public void doExterminate(int efficiency) {

    System.out.println(this.getPseudo() + " extermine des sysytèmes avec une efficacité de " + efficiency);

    List<Pair<Set<Ship>, Hexagon>> moves = new ArrayList<>();

    boolean validMoves = false;

    while (!validMoves) {

        boolean newMove = true;
        moves.clear();
        while (moves.size() < efficiency && newMove) {

            boolean validInput = false;
            int numFlottes = 0;
            Hexagon target = null;

            // Loop until valid input is received
            while (!validInput) {
                try {
                    System.out.println(this.getPseudo() + ", cliquez sur le système que vous voulez attaquer.");

					target = game.getController().waitForHexagonSelection();

                    if (target == null || target.getSystem() == null || target.getOccupant() == this) {
                        System.out.println("Système invalide.");
						continue;
                    }

                    System.out.println(this.getPseudo() + ", combien de flottes voulez-vous utiliser ? : ");
                    numFlottes = this.game.scanner.nextInt();

                    Set<Ship> fleet = new HashSet<Ship>();

                    for (int k = 0; k < numFlottes; k++) {
                        System.out.println(this.getPseudo() + ", cliquez sur la flotte que vous voulez utiliser (hexagone).");

						Hexagon fleetHex = game.getController().waitForHexagonSelection();

						if (fleetHex == null || fleetHex.getShips().isEmpty() || fleetHex.getOccupant() != this) {
							System.out.println("Flotte invalide.");
							continue;
						}

						fleet.addAll(fleetHex.getShips());

                    }

                    // Add the move to the moves list
                    moves.add(new Pair<>(fleet, target));

                    validInput = true; // Exit the loop
                } catch (Exception e) {
                    System.out.println("Entrée invalide");
                    this.game.scanner.nextLine(); // Clear the invalid input
                }
            }

            if (moves.size() < efficiency) {
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

        validMoves = game.checkExterminateValidity(moves, this);

        if (!validMoves) {
            System.out.println("Le coup que vous avez essayé de jouer n'est pas valide. Veuillez réessayer");
        }
    }


    // Execute each move
    for (Pair<Set<Ship>, Hexagon> move : moves) {
        // Set the ships and execute the command
        this.exterminate.setShips(move.getKey());
        this.exterminate.setTarget(move.getValue());
        this.exterminate.execute();
    }
}


	public static void main(String[] args) {
		System.out.println("Lancez le jeu depuis GameController.js");
	}

}