package pimperium;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;


public class Human extends Player{
	
	public Human(Game game) {
		super(game);
	}
	
	//Choose a lvl-1 system to place 2 ships
	public void setupInitialFleet() {
		System.out.println("Warning! Not implemented");
		//Should assert that the hex chosen indeed has a lvl-1 system and that it is in an unoccupied sector
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the positon of the hex where you want to place your fleet : " );
		int i = scanner.nextInt();
		int j = scanner.nextInt();
		scanner.close();
		
		//Find the target hex and create 2 ships on it
		Hexagon hex = this.game.getMap()[i][j];
		this.addShip(hex);
		this.addShip(hex);
	}
	
	public int[] chooseOrderCommands() {
		System.out.println("Warning! Not implemented");
		this.orderCommands = new int[] {0,2,1};
		return orderCommands;
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
