package pimperium.elements;

import java.util.ArrayList;

public class SideSector extends Sector{
	
	private ArrayList<Integer> systems_coordinates;

	
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
