package pimperium;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;


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
				if (hex.getSystem() == null || hex.getSystem().getLevel() != 1) {
					System.out.println("Hexagone invalide pour placer la flotte. Veuillez choisir un autre hexagone.");
					continue; // Ask for input again
				}

				// TODO: Check that the hex is not already occupied by anyone

				// Add ships to the selected hexagon
				this.addShip(hex);
				this.addShip(hex);
				System.out.println("Deux navires à " + this.getPseudo() + " ont été placés sur l'hexagone " + hex);

				validInput = true; // Exit the loop
			} catch (Exception e) {
				System.out.println("Entrée invalide. Veuillez entrer deux entiers séparés par un espace.");
				this.game.scanner.nextLine(); // Clear the invalid input
			}
		}

	}

	public int[] chooseOrderCommands() {

		int[] order = new int[3];
		System.out.println("Choisissez l'ordre des commandes (0: Expand, 1: Explore, 2: Exterminate) pour le tour : ");
		for (int i = 0; i < 3; i++) {
			System.out.print("Commande " + (i + 1) + " : ");
			order[i] = this.game.scanner.nextInt();
			if (order[i] < 0 || order[i] > 2) {
				System.out.println("Commande invalide. Veuillez réessayer.");
				i--;
			}
		}

		this.orderCommands = order;
		return order;
	}

	public void doAction(int index, int efficiency) {
		switch (index) {
		case 0: {
			this.doExpand(efficiency);
			break;
		}
		case 1: {
			this.doExplore(efficiency);
			break;
		}
		case 2: {
			this.doExterminate(efficiency);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + index);
		}
	}

	public void doExpand(int efficiency) {

		System.out.println(this.getPseudo() + " is expanding");

		boolean validity = false;

		List<Ship> expandShips = new ArrayList<Ship>();
		while (!validity) {
			expandShips = new ArrayList<Ship>();
			for (int i=0; i<efficiency; i++) {
				Scanner scanner = new Scanner(System.in);
				System.out.println("Enter the index of the ship you want to expand : " );
				int index = scanner.nextInt();
				expandShips.add(this.ships.get(index));
				scanner.close();
			}

			validity = game.checkExpandValidity(expandShips);
		}

		//Set the ships and execute the command
		this.expand.setShips(expandShips);
		this.expand.execute();


	}

	public void doExplore(int efficiency) {

		System.out.println(this.getPseudo() + " is exploring");

		boolean validity = false;

		List<Ship> exploreShips = new ArrayList<Ship>();
		List<Hexagon> targetHexagons = new ArrayList<Hexagon>();

		while (!validity) {
			exploreShips = new ArrayList<Ship>();
			targetHexagons = new ArrayList<Hexagon>();
			for (int i=0; i<efficiency; i++) {
				Scanner scanner = new Scanner(System.in);
				System.out.println("Enter the index of the ship you want to move : " );
				int indexShip = scanner.nextInt();
				exploreShips.add(this.ships.get(indexShip));
				System.out.println("Enter the position of the hex you want to move this ship into : " );
				int i_hex = scanner.nextInt();
				int j_hex = scanner.nextInt();
				scanner.close();

				Hexagon hex = this.game.getMap()[i_hex][j_hex];
				targetHexagons.add(hex);

			}

			validity = game.checkExploreValidity(exploreShips, targetHexagons);
		}

		//Set the ships and execute the command
		this.explore.setShips(exploreShips);
		this.explore.setTargets(targetHexagons);
		this.explore.execute();

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
				Scanner scanner = new Scanner(System.in);
				System.out.println("Enter the index of the ship you want to send to fight : " );
				int indexShip = scanner.nextInt();
				exterminateShips.add(this.ships.get(indexShip));
				System.out.println("Enter the position of the hex you want to attack : " );
				int i_hex = scanner.nextInt();
				int j_hex = scanner.nextInt();
				scanner.close();

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
