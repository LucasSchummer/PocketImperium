package pimperium.elements;

import java.util.ArrayList;

/**
 * The sector on the side of the middle row, next to TriPrime
 */
public class SideSector extends Sector{

	/**
	 * List of coordinates of the systems among the sector as (x1,y1,x2,y2,x3,y3). 0<=x<=2 and 0<=y<=1
	 */
	private ArrayList<Integer> systems_coordinates;

	/**
	 * Create the systems of the sector and save their coordinates
	 * @param sys_2_x x coord of the lvl-2 system
	 * @param sys_2_y y coord of the lvl-2 system
	 * @param sys_1_x1 x coord of the first lvl-1 system
	 * @param sys_1_y1 y coord of the first lvl-1 system
	 * @param sys_1_x2 x coord of the second lvl-1 system
	 * @param sys_1_y2 y coord of the second lvl-1 system
	 * @param path The path of the image corresponding to the sector
	 */
	public SideSector(int sys_2_x, int sys_2_y, int sys_1_x1, int sys_1_y1, int sys_1_x2, int  sys_1_y2, String path) {
		this.systems_coordinates = new ArrayList<Integer>();
		this.systems_coordinates.add(sys_2_x);
		this.systems_coordinates.add(sys_2_y);
		this.systems_coordinates.add(sys_1_x1);
		this.systems_coordinates.add(sys_1_y1);
		this.systems_coordinates.add(sys_1_x2);
		this.systems_coordinates.add(sys_1_y2);

		this.systems.add(new HSystem(2));
		this.systems.add(new HSystem(1));
		this.systems.add(new HSystem(1));

		this.path = path;

	}
	
	public ArrayList<Integer> getSystemsCoordinates() {
		return this.systems_coordinates;
	}

}
