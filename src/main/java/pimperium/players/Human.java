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

	// Chooses a lvl-1 system to place 2 ships
	public void setupInitialFleet() {
		
		// Wait for the view to be initialized
		while (!game.viewInitialized) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println(this.getPseudo() + ", cliquez sur l'hexagone où vous souhaitez placer votre flotte.");
		game.getController().getView().addLogMessage("Cliquez sur l'hexagone où vous souhaitez placer votre flotte.", this, "normal");

		synchronized (game.getController()) {
			try {
				Hexagon hex = this.game.getController().waitForHexagonSelection();

				// Vérifier que l'hexagone est valide pour le placement initial
				if (hex == null || hex.getSystem() == null || hex.getSystem().getLevel() != 1) {
					System.out.println("Hexagone invalide pour placer la flotte. Veuillez choisir un autre hexagone.");
					game.getController().getView().addLogMessage("Hexagone invalide pour placer la flotte. Veuillez choisir un autre hexagone.", this, "error");
					setupInitialFleet(); // Ask for input again
					return;
				}

				// Check that the hex is not already occupied by anyone and not empty
				if (hex.getOccupant() != null && hex.getOccupant() != this) {
					System.out.println("Ce système est déjà contrôlé par un autre joueur. Veuillez choisir un autre hexagone.");
					game.getController().getView().addLogMessage("Ce système est déjà contrôlé par un autre joueur. Veuillez choisir un autre hexagone.", this, "error");
					setupInitialFleet(); // Ask for input again
					return;
				}

				if (!hex.getShips().isEmpty()) {
					System.out.println("Cet hexagone contient déjà des vaisseaux. Veuillez choisir un autre hexagone.");
					game.getController().getView().addLogMessage("Cet hexagone contient déjà des vaisseaux. Veuillez choisir un autre hexagone.", this, "error");
					setupInitialFleet(); // Ask for input again
					return;
				}

				// Check that the hex is in a unoccupied sector
				if (game.findSector(hex).isOccupied()) {
					System.out.println("Cet hexagone se situe dans un secteur déjà controlé par un joueur. Veuillez choisir un autre hexagone.");
					game.getController().getView().addLogMessage("Cet hexagone se situe dans un secteur déjà controlé par un joueur. Veuillez choisir un autre hexagone.", this, "error");
					setupInitialFleet(); // Ask for input again
					return;
				}

				// Placer les navires sur l'hexagone sélectionné
				this.createShip(hex);
				this.createShip(hex);

				System.out.println("Deux navires de " + this.getPseudo() + " ont été placés sur l'hexagone " + hex);
				game.getController().getView().addLogMessage("Deux de vos navires ont été placés sur l'hexagone " + hex, this, "normal");

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void chooseOrderCommands() {
		System.out.println(this.getPseudo() + ", veuillez choisir l'ordre des commandes.");
		game.getController().getView().addLogMessage("Veuillez choisir l'ordre des commandes.", this, "normal");

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
		game.getController().getView().addLogMessage("Choisissez un secteur à scorer", this, "normal");

		while (!validChoice) {
			try {
				// Wait for the player to select the hexagon
				sector = game.getController().waitForSectorSelection();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (sector == null) {
				System.out.println("Secteur invalide. Veuillez réessayer.");
				game.getController().getView().addLogMessage("Secteur invalide. Veuillez réessayer.", this, "error");
			} else if (sector.isTriPrime()) {
				System.out.println("Vous ne pouvez pas choisir le secteur Tri-Prime. Veuillez choisir un autre secteur.");
				game.getController().getView().addLogMessage("Vous ne pouvez pas choisir le secteur Tri-Prime. Veuillez choisir un autre secteur.", this, "error");
			} else if (scoredSectors.contains(sector)) {
				System.out.println("Ce secteur a déjà été choisi. Veuillez choisir un autre secteur.");
				game.getController().getView().addLogMessage("Ce secteur a déjà été choisi. Veuillez choisir un autre secteur.", this, "error");
			} else {
				validChoice = true;
			}
		}
		int sectorId = this.game.findSectorId(sector);
		System.out.println(this.getPseudo() + " a choisi le secteur " + sectorId + " à scorer.");
		game.getController().getView().addLogMessage("Vous avez choisi le secteur " + sectorId + ".", this, "normal");
		return sector;
	}
	// With the clickable hexagons
	public void doExpand(int efficiency) {

		System.out.println(this.getPseudo() + " rajoute un vaisseau avec une efficacité de " + efficiency);
		game.getController().getView().addLogMessage("Expand (efficacité : " + efficiency + ")", this, "normal");

		boolean validMove = false;
		Hexagon hex = new Hexagon(0,0);

		List<Ship> possShips = Possibilities.getInstance(game).expand(this);

		for (int i = 0; i < efficiency; i++) {

			if (possShips.isEmpty()) {
				game.getController().getView().addLogMessage("Aucune expansion possible.", this, "normal");
				break;
			}

			validMove = false;

			while (!validMove) {

				System.out.println(this.getPseudo() + ", cliquez sur l'hexagone où vous souhaitez placer votre vaisseau.");
				game.getController().getView().addLogMessage("Cliquez sur l'hexagone où vous souhaitez placer votre vaisseau.", this, "normal");

				try {
					// Wait for the player to select the hexagon
					hex = game.getController().waitForHexagonSelection();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				validMove = this.game.checkExpandValidity(hex, this);

				if (!validMove) {
					System.out.println("L'hexagone que vous avez choisi n'est pas valide. Veuillez réessayer");
					game.getController().getView().addLogMessage("L'hexagone que vous avez choisi n'est pas valide. Veuillez réessayer", this, "error");
				}
			}

			// Set the ships and execute the command
			List<Ship> ships = new ArrayList<>();
			ships.add(hex.getShips().stream()
					.filter(ship -> !ship.hasExpanded())
					.toList().get(0));
			this.expand.setShips(ships);
			this.expand.execute();

			possShips = Possibilities.getInstance(game).expand(this);

			this.game.triggerInterfaceUpdate();
		}
	}

	// With the clickable hexagons
	public void doExplore(int efficiency) {

		System.out.println(this.getPseudo() + " explore le plateau avec une efficacité de " + efficiency);
		game.getController().getView().addLogMessage("Explore (efficacité : " + efficiency + ")", this, "normal");

		Pair<List<Ship>, List<Hexagon>> move = new Pair<>(new ArrayList<>(), new ArrayList<>());

		int movesDone = 0;
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
						game.getController().getView().addLogMessage("Cliquez sur la flotte que vous souhaitez déplacer.", this, "normal");

						// Wait for the player to select the origin hexagon
						Hexagon originHex = game.getController().waitForHexagonSelection();

						// Verify that the hex contains the player's ships
						if (originHex == null || originHex.getShips().isEmpty() || originHex.getOccupant() != this) {
							System.out.println("Cet hexagone ne contient pas vos vaisseaux. Veuillez réessayer.");
							game.getController().getView().addLogMessage("Cet hexagone ne contient pas vos vaisseaux. Veuillez réessayer.", this, "error");
							continue;
						}
						int numShips = 0;

						if (originHex.getShips().size() > 1) {
							System.out.println("Combien de vaisseaux voulez-vous déplacer depuis cet hexagone ?");
							game.getController().getView().addLogMessage("Combien de vaisseaux voulez-vous déplacer depuis cet hexagone ?", this, "normal");
							numShips = this.game.getController().waitForUserInput();
						} else {
							numShips = 1;
						}

						if (numShips <= 0 || numShips > originHex.getShips().size()) {
							System.out.println("Nombre de vaisseaux invalide. Veuillez réessayer.");
							game.getController().getView().addLogMessage("Nombre de vaisseaux invalide. Veuillez réessayer.", this, "error");
							continue;
						}

						List<Ship> fleet = new ArrayList<>(originHex.getShips().subList(0, numShips));

						System.out.println(this.getPseudo() + ", cliquez sur l'hexagone de destination.");
						game.getController().getView().addLogMessage("Cliquez sur l'hexagone de destination.", this, "normal");

						Hexagon target = game.getController().waitForHexagonSelection();

						if (target == null) {
							System.out.println("Destination invalide. Veuillez réessayer.");
							game.getController().getView().addLogMessage("Destination invalide. Veuillez réessayer.", this, "error");
							continue;
						}

						move = new Pair<>(fleet, new ArrayList<>(Collections.nCopies(fleet.size(), target)));

						validInput = true;
					} catch (Exception e) {
						System.out.println("Entrée invalide. Veuillez réessayer.");
						game.getController().getView().addLogMessage("Entrée invalide. Veuillez réessayer.", this, "error");
						this.game.scanner.nextLine();
					}
				}

				validMove = this.game.checkExploreValidity(move);

				if (!validMove) {
					System.out.println("Le coup proposé n'est pas valide. Veuillez réessayer.");
					game.getController().getView().addLogMessage("Le coup proposé n'est pas valide. Veuillez réessayer.", this, "error");
				}
			}

			this.explore.setShips(move.getKey());
			this.explore.setTargets(move.getValue());
			this.explore.execute();

			movesDone++;

			this.game.triggerInterfaceUpdate();

			possibleMoves = Possibilities.getInstance(game).explore(this);

			if (movesDone < efficiency && !possibleMoves.isEmpty()) {
				boolean validResponse = false;
				while (!validResponse) {
					System.out.print("Voulez-vous déplacer une autre flotte ? (0/1) : ");
					game.getController().getView().addLogMessage("Voulez-vous déplacer une autre flotte ? (0/1) : ", this, "normal");
					try {
						int input = this.game.getController().waitForUserInput();
						if (input != 0 && input != 1) {
							System.out.println("Entrée invalide. Veuillez entrer 0 ou 1");
							game.getController().getView().addLogMessage("Entrée invalide. Veuillez entrer 0 ou 1", this, "error");
							continue;
						}

						newMove = input == 1;
						validResponse = true;

					} catch (Exception e) {
						System.out.println("Entrée invalide. Veuillez entrer 0 ou 1");
						game.getController().getView().addLogMessage("Entrée invalide. Veuillez entrer 0 ou 1", this, "error");
						this.game.scanner.nextLine();
					}
				}
			} else {
				newMove = false;
			}
		}
	}

	// With the clickable hexagons
	public void doExterminate(int efficiency) {

		System.out.println(this.getPseudo() + " extermine des systèmes avec une efficacité de " + efficiency);
		game.getController().getView().addLogMessage("Exterminate (efficacité : " + efficiency + ")", this, "normal");

		Pair<Set<Ship>, Hexagon> move = new Pair<>(new HashSet<>(), new Hexagon(0,0));
		Set<Hexagon> targets = new HashSet<>();
		Hexagon target = null;

		boolean newMove = true;
		boolean validMove = false;
		boolean validInput = false;
		int movesDone = 0;

		List<Pair<Set<Ship>, Hexagon>> possibleMoves = Possibilities.getInstance(game).exterminate(this);

		while (movesDone < efficiency && newMove && !possibleMoves.isEmpty()) {

			validMove = false;

			while (!validMove) {

				validInput = false;
				int numFlottes = 0;

				while (!validInput) {
					try {
						System.out.println(this.getPseudo() + ", cliquez sur le système que vous voulez attaquer.");
						game.getController().getView().addLogMessage("Cliquez sur le système que vous voulez attaquer.", this, "normal");

						target = game.getController().waitForHexagonSelection();

						if (target == null || target.getSystem() == null || target.getOccupant() == this) {
							System.out.println("Système invalide.");
							game.getController().getView().addLogMessage("Système invalide.", this, "error");
							continue;
						}

						if (targets.contains(target)) {
							System.out.println("Vous avez déjà attaqué ce système.");
							game.getController().getView().addLogMessage("Vous avez déjà attaqué ce système.", this, "error");
							continue;
						}

						List<Hexagon> possibleOrigins = target.getOriginsExterminate(this);

						Set<Ship> fleet = new HashSet<Ship>();

						if (possibleOrigins.size() == 0) {
							System.out.println("Aucune flotte disponible pour attaquer ce système.");
							game.getController().getView().addLogMessage("Aucune flotte disponible pour attaquer ce système.", this, "error");
							continue;

						} else if (possibleOrigins.size() > 1) {

							System.out.println(this.getPseudo() + ", combien de flottes voulez-vous utiliser ? : ");
							game.getController().getView().addLogMessage("Combien de flottes voulez-vous utiliser ?", this, "normal");
							numFlottes = this.game.getController().waitForUserInput();

							for (int k = 0; k < numFlottes; k++) {
								System.out.println(this.getPseudo() + ", cliquez sur la flotte que vous voulez utiliser.");
								game.getController().getView().addLogMessage("Cliquez sur la flotte que vous voulez utiliser.", this, "normal");

								Hexagon fleetHex = game.getController().waitForHexagonSelection();

								if (fleetHex == null || fleetHex.getShips().isEmpty() || fleetHex.getOccupant() != this) {
									System.out.println("Flotte invalide.");
									game.getController().getView().addLogMessage("Flotte invalide.", this, "error");
									continue;
								}

								List<Ship> usableShips = fleetHex.getShips().stream()
												.filter(ship -> !ship.hasExterminated())
												.toList();

								System.out.println("Combien de vaisseaux situés sur " + fleetHex + " voulez-vous utiliser ?");
								game.getController().getView().addLogMessage("Combien de vaisseaux situés sur " + fleetHex + " voulez-vous utiliser ?", this, "normal");
								int numShips = 1;
								if (usableShips.size() > 1) {
									numShips = this.game.getController().waitForUserInput();
								}

								if (numShips > usableShips.size()) {
									System.out.println("Vous ne disposez pas d'autant de vaisseaux capables d'exterminer.");
									game.getController().getView().addLogMessage("Vous ne disposez pas d'autant de vaisseaux capables d'exterminer.", this, "error");
									continue;
								}

								for (int i = 0; i < numShips; i++) {
									fleet.add(usableShips.get(i));
								}
							}

						} else {

							List<Ship> usableShips = possibleOrigins.get(0).getShips().stream()
									.filter(ship -> !ship.hasExterminated())
									.toList();

							System.out.println("Combien de vaisseaux situés sur " + possibleOrigins.get(0) + " voulez-vous utiliser ?");
							game.getController().getView().addLogMessage("Combien de vaisseaux situés sur " + possibleOrigins.get(0) + " voulez-vous utiliser ?", this, "normal");
							int numShips = 1;
							if (usableShips.size() > 1) {
								numShips = this.game.getController().waitForUserInput();
							}

							if (numShips > usableShips.size()) {
								System.out.println("Vous ne disposez pas d'autant de vaisseaux capables d'exterminer.");
								game.getController().getView().addLogMessage("Vous ne disposez pas d'autant de vaisseaux capables d'exterminer.", this, "error");
								continue;
							}

							for (int i = 0; i < numShips; i++) {
								fleet.add(usableShips.get(i));
							}
						}

						move = new Pair<>(fleet, target);
						validMove = game.checkExterminateValidity(move, this);

						if (!validMove) {
							System.out.println("Le coup que vous avez essayé de jouer n'est pas valide. Veuillez réessayer");
							game.getController().getView().addLogMessage("Le coup que vous avez essayé de jouer n'est pas valide. Veuillez réessayer", this, "error");

							//Debugger.displayAllExterminateMoves(this, this.game);
						}

						validInput = true;
					} catch (Exception e) {
						System.out.println("Entrée invalide");
						game.getController().getView().addLogMessage("Entrée invalide", this, "error");
						this.game.scanner.nextLine();
					}
				}
			}

			this.exterminate.setShips(move.getKey());
			this.exterminate.setTarget(move.getValue());
			this.exterminate.execute();

			targets.add(target);

			this.game.triggerInterfaceUpdate();

			movesDone++;

			possibleMoves = Possibilities.getInstance(game).exterminate(this);

			if (movesDone < efficiency && !possibleMoves.isEmpty()) {
				validInput = false;
				while (!validInput) {
					try {
						System.out.print("Voulez-vous attaquer un autre système ? (0/1) : ");
						game.getController().getView().addLogMessage("Voulez-vous attaquer un autre système ? (0/1) : ", this, "normal");
						int input = this.game.getController().waitForUserInput();
						if (input != 0 && input != 1) {
							System.out.println("Entrée invalide. Veuillez entrer 0 ou 1");
							game.getController().getView().addLogMessage("Entrée invalide. Veuillez entrer 0 ou 1", this, "error");
							continue;
						}

						newMove = input == 1;
						validInput = true;

					} catch (Exception e) {
						System.out.println("Entrée invalide. Veuillez entrer 0 ou 1");
						game.getController().getView().addLogMessage("Entrée invalide. Veuillez entrer 0 ou 1", this, "error");
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