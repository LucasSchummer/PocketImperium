package pimperium;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;


public class Human extends Player {
	private Scanner scanner;
	
	public Human(Game game) {
		super(game);
		this.scanner = new Scanner(System.in);
	}
	
	//Choose a lvl-1 system to place 2 ships
	public void setupInitialFleet() {
		System.out.println(this.getPseudo()+", entrez la position de l'hexagone où vous souhaitez placer votre flotte (i j) : ");
		int i = scanner.nextInt();
		int j = scanner.nextInt();

		// Valider les coordonnées
		if (i < 0 || i >= Game.MAP_ROWS || j < 0 || j >= Game.MAP_COLS) {
			System.out.println("Coordonnées invalides. Veuillez réessayer.");
			setupInitialFleet();
			return;
		}
		
		Hexagon hex = this.game.getMap()[i][j];
		if (hex.getSystem() == null || hex.getSystem().getLevel() != 1) {
			System.out.println("Hexagone invalide pour placer la flotte. Veuillez choisir un autre hexagone.");
			setupInitialFleet();
			return;
		}
		
		// Ajouter 2 navires sur l'hexagone sélectionné
		this.addShip(hex);
		this.addShip(hex);
		System.out.println("Deux navires à "+this.getPseudo()+" ont été placés sur l'hexagone " + hex);
		
	}

	public int[] chooseOrderCommands() {
		int[] order = new int[3];
		System.out.println(this.getPseudo()+", choisissez l'ordre des commandes (0: Expand, 1: Explore, 2: Exterminate) pour le tour : ");
		for (int i = 0; i < 3; i++) {
			System.out.print("Commande " + (i + 1) + " : ");
			order[i] = scanner.nextInt();
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
			throw new IllegalArgumentException("Valeur innatendue: " + index);
		}
	}
	
	public void doExpand(int efficiency) {
		
		System.out.println(this.getPseudo() + " is expanding");
		
		boolean validity = false;
		
		List<Ship> expandShips = new ArrayList<Ship>();
		while (!validity) {
			expandShips.clear();
			for (int i = 0; i < efficiency; i++) {
				System.out.print("Entrez l'indice du vaisseau que vous souhaitez étendre : ");
				int index = scanner.nextInt();
				expandShips.add(this.ships.get(index));
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
