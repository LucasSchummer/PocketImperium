package pimperium;
import java.util.ArrayList;

public class Hexagon {
	
	private int pos_x;
	private int pos_y;
	private ArrayList<Hexagon> neighbours;
	private HSystem system;
	
	
	public Hexagon(int x, int y) {
		
		this.pos_x = x;
		this.pos_y = y;
		this.neighbours = new ArrayList<Hexagon>();
		
	}
	
	public void addSystem(HSystem system) {
		this.system = system;
	}
	
	public void addNeighbour(Hexagon hex) {
		this.neighbours.add(hex);
	}
	
	public ArrayList<Hexagon> getNeighbours() {
		return this.neighbours;
	}
	
	public void printNeighbours() {
		System.out.print("List of neighbours for ");
		System.out.println(this);
	    for (int i = 0; i < this.neighbours.size(); i++) {
	        System.out.println(this.neighbours.get(i));
	      }
	}
	
	public int getx() {
		return this.pos_x;
	}
	
	public int gety() {
		return this.pos_y;
	}
	
	public String toString() {
		return "hex"+pos_x+"_"+pos_y;
	}

	public static void main(String[] args) {
		System.out.println("Hello World !");
	}

}
