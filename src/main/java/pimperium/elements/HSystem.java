package pimperium.elements;

import java.io.Serializable;

/**
 * A system positioned on a hexagon
 */
public class HSystem implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The level of the system (between 1 and 3)
	 */
	private int level;
	/**
	 * The hexagon where the system is positioned
	 */
	private Hexagon hex;

	public HSystem(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public void setHex(Hexagon hex) {
		this.hex = hex;
	}

	public Hexagon getHex() {
		return this.hex;
	}

}
