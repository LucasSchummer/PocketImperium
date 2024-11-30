package pimperium;

import java.util.ArrayList;

public class SideSector extends Sector{
	
	private int sys_2_x;
	private int sys_2_y;
	private int sys_1_x1;
	private int sys_1_y1;
	private int sys_1_x2;
	private int sys_1_y2;
	private ArrayList<Integer> systems_coordinates;

	private int id;

	
	public SideSector(int sys_2_x, int sys_2_y, int sys_1_x1, int sys_1_y1, int sys_1_x2, int  sys_1_y2, int id) {
		this.systems_coordinates = new ArrayList<Integer>();
		this.systems_coordinates.add(sys_2_x);
		this.systems_coordinates.add(sys_2_y);
		this.systems_coordinates.add(sys_1_x1);
		this.systems_coordinates.add(sys_1_y1);
		this.systems_coordinates.add(sys_1_x2);
		this.systems_coordinates.add(sys_1_y2);
//		this.sys_2_x = sys_2_x;
//		this.sys_2_y = sys_2_y;
//		this.sys_1_x1 = sys_1_x1;
//		this.sys_1_y1 = sys_1_y1;
//		this.sys_1_x2 = sys_1_x2;
//		this.sys_1_y2 = sys_1_y2;
		this.systems.add(new HSystem(2));
		this.systems.add(new HSystem(1));
		this.systems.add(new HSystem(1));

		this.id = id;
	}
	
	public ArrayList<Integer> getSystemsCoordinates() {
		return this.systems_coordinates;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
