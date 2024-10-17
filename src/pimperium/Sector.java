package pimperium;

import java.util.ArrayList;

public class Sector {
	
	protected ArrayList<HSystem> systems;
	
	public Sector() {
		systems = new ArrayList<HSystem>();
	}
	
	public int scoreSector() {
		var score = 0;
	    for (int i = 0; i < this.systems.size(); i++) {
	        score += this.systems.get(i).getLevel();
	      }
	    return score;
	}
	
	public ArrayList<HSystem> getSystems() {
		return this.systems;
	}
	
	public ArrayList<Integer> getSystemsCoordinates() {
		return new ArrayList<Integer>();
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
