package pimperium.elements;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import pimperium.players.Player;

import java.util.HashSet;

public class Hexagon implements Serializable {

	private static final long serialVersionUID = 1L;

	private int pos_x;
	private int pos_y;
	private boolean isTriPrime = false;
	private Set<Hexagon> neighbors;
	private HSystem system;
	private List<Ship> ships;
	
	public Hexagon(int x, int y) {
		this.pos_x = x;
		this.pos_y = y;
		this.neighbors = new HashSet<>();
		this.ships = new ArrayList<Ship>();
	}
	
	public void addSystem(HSystem system) {
		this.system = system;
		system.setHex(this);
	}

	public void setTriPrime() {
		this.isTriPrime = true;
		HSystem system = new HSystem(3);
		this.addSystem(system);
	}

	public boolean isTriPrime() {
		return this.isTriPrime;
	}
	
	public void addNeighbor(Hexagon hex) {
		this.neighbors.add(hex);
	}

	public void addNeighbor(Set<Hexagon> hexs) {
		this.neighbors.addAll(hexs);
	}
	
	public void setNeighbours(Set<Hexagon> neighbours) {
		this.neighbors = neighbours;
	}

	public void removeNeighbor(Set<Hexagon> hexs) {
		this.neighbors.removeAll(hexs);
	}

	public List<Hexagon> getOriginsExterminate(Player player) {

		List<Hexagon> origins = new ArrayList<Hexagon>();
		// Find all the hexs the player can attack from
		for (Hexagon hex : this.getNeighbours()) {
			boolean isOccupant = hex.getOccupant() == player;
			List<Ship> usableShips = hex.getShips().stream()
					.filter(ship -> !ship.hasExterminated())
					.toList();
			boolean hasUsableShips = !usableShips.isEmpty();
			if (isOccupant && hasUsableShips) {
				origins.add(hex);
			}
		}
		return origins;
	}
	
	public Set<Hexagon> getNeighbours() {
		return this.neighbors;
	}
	
	public void printNeighbours() {
		System.out.print("List of neighbours for ");
		System.out.println(this);
		for (Hexagon hex : this.neighbors) {
			System.out.println(hex);
		}
	}
	
	public int getx() {
		return this.pos_x;
	}
	
	public int gety() {
		return this.pos_y;
	}
	
	@Override
	public String toString() {
		if (this.isTriPrime) {
			return "TriPrime";
		} else {
			return "[" + this.pos_x + "," + this.pos_y + "]";
		}
	}

	// Remove all ships from the hexagon
	public void removeShips() {
		this.ships.clear(); 
	}

	public HSystem getSystem() {
		return this.system;
	}

	public int getSystemLevel() {
		if (this.system == null) {
			return 0;
		} else {
			return this.system.getLevel();
		}
	}

	public Player getOccupant() {
		if (this.ships.isEmpty()) {
			return null;
		} else {
			return this.ships.getFirst().getOwner();
		}
	}

	public void setSystem(HSystem system) {
		this.system = system;
	}

	public List<Ship> getShips() {
		return this.ships;
	}

	public void addShip(Ship ship) {
		this.ships.add(ship);
	}

	public void removeShip(Ship ship) {
		this.ships.remove(ship);
	}

	// Remove specific ships from the hexagon
	public void removeShips(List<Ship> shipsToRemove) {
		this.ships.removeAll(shipsToRemove);
	}

	public void setShips(List<Ship> ships) {
		this.ships = ships;
	}

	public static void main(String[] args) {
		System.out.println("Hello World !");
	}
}
