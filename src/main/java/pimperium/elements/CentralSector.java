package pimperium.elements;

import java.util.ArrayList;

/**
 * The central sector of the map containing TriPrime
 */
public class CentralSector extends Sector{
	
	//Testing commit
	public CentralSector() {
		this.systems.add(new HSystem(3));
		this.path = "center.png";
	}

	@Override
	public boolean isTriPrime() {
		return true;
	}

	/**
	 *
	 * @return an empty list
	 */
	public ArrayList<Integer> getSystemsCoordinates() {
		return new ArrayList<>();
	}

}
