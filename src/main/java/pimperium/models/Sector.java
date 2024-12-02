package pimperium.models;

import java.util.ArrayList;

public class Sector {
	
	protected ArrayList<HSystem> systems;
	protected String path;
	
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

	public boolean isOccupied() {
		boolean occupied = false;
		for (HSystem system : this.systems) {
			if (system.getController() != null) {
				occupied = true;
				break;
			}
		}
		return occupied;
	}
	
	public ArrayList<Integer> getSystemsCoordinates() {
		return new ArrayList<Integer>();
	}
	
	public boolean isTriPrime() {
		return false;
	}

	public String getPath() {
		return this.path;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
