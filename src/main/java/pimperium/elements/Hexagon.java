package pimperium.elements;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import pimperium.players.Player;


/**
 * A hexagon constituting the map
 */
public class Hexagon implements Serializable {

	private static final long serialVersionUID = 1L;

	private int pos_x;
	private int pos_y;
	private boolean isTriPrime = false;
	/**
	 * The set of direct neighbors
	 */
	private Set<Hexagon> neighbors;
	/**
	 * The system situated on the hexagon
	 */
	private HSystem system;
	/**
	 * The fleet currently on the hexagon
	 */
	private List<Ship> ships;

	/**
	 * Create the hexagon and initialize attributes
	 * @param x The x position of the hexagon on the map
	 * @param y The y position of the hexagon on the map
	 */
	public Hexagon(int x, int y) {
		this.pos_x = x;
		this.pos_y = y;
		this.neighbors = new HashSet<>();
		this.ships = new ArrayList<Ship>();
	}

	/**
	 * Place a system on the hexagon
	 * @param system The system previously created
	 */
	public void addSystem(HSystem system) {
		this.system = system;
		system.setHex(this);
	}

	/**
	 * Make the hexagon TriPrime by adding it a lvl-3 system
	 */
	public void setTriPrime() {
		this.isTriPrime = true;
		HSystem system = new HSystem(3);
		this.addSystem(system);
	}

	public boolean isTriPrime() {
		return this.isTriPrime;
	}

	/**
	 * Add a neighbor to the hexagon neighbors set
	 * @param hex The hexagon to add to the neighbors set
	 */
	public void addNeighbor(Hexagon hex) {
		this.neighbors.add(hex);
	}

	/**
	 * Set the neighbors set
	 * @param neighbours The set of neighbors to set as the neighbors of the hexagon
	 */
	public void setNeighbours(Set<Hexagon> neighbours) {
		this.neighbors = neighbours;
	}

	/**
	 * Retrieve all the hexagon neighbors that the designated player can attack from
	 * @param player The player performing the Exterminate move
	 * @return The list of hexagons the player can attack this hexagon from
	 */
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

	public String toString() {
		if (this.isTriPrime) {
			return "TriPrime";
		} else {
			return "[" + this.pos_x + "," + this.pos_y + "]";
		}
	}

	public HSystem getSystem() {
		return this.system;
	}

	/**
	 * Get the level of the system on this hexagon (0 if there is no system)
	 * @return The level of the system (0-1-2-3)
	 */
	public int getSystemLevel() {
		if (this.system == null) {
			return 0;
		} else {
			return this.system.getLevel();
		}
	}

	/**
	 * Get the player currently occupying the hexagon
	 * @return The occupant player (null if the hexagon is unoccupied)
	 */
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

}
