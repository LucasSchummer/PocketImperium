package pimperium.elements;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Sector card constituting the game map
 */
public abstract class Sector implements Serializable {

	protected static final long serialVersionUID = 1L;

	/**
	 * List of systems belonging to the sector
	 */
	protected ArrayList<HSystem> systems;
	/**
	 * Path of the image corresponding to the sector
	 */
	protected String path;
	
	public Sector() {
		systems = new ArrayList<HSystem>();
	}

	public ArrayList<HSystem> getSystems() {
		return this.systems;
	}

	/**
	 * Check if any of the sector system in controlled by a player
	 * @return Whether the sector is occupied or not, as a boolean
	 */
	public boolean isOccupied() {
		boolean occupied = false;
		for (HSystem system : this.systems) {
			if (system.getHex().getOccupant() != null) {
				occupied = true;
				break;
			}
		}
		return occupied;
	}

	/**
	 * Check if the sector is TriPrime
	 * @return Whether the sector is TriPrime or not, as a boolean
	 */
	public boolean isTriPrime() {
		return false;
	}

	public String getPath() {
		return this.path;
	}

	/**
	 * Get the coordinates of the systems among the sector
	 * @return The list of coordinates as (x1,y1,x2,y2,...)
	 */
	public abstract ArrayList<Integer> getSystemsCoordinates();

}
