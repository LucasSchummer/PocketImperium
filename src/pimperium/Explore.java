package pimperium;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Explore implements Command {
    private Player player;
    private List<Hexagon> targetHexagons;
    private List<Ship> shipsInvolved;

    public Explore(Player player) {
        this.player = player;
    }
    
    public void setShips(List<Ship> shipsInvolved) {
    	this.shipsInvolved = shipsInvolved;
    }
    
    public void setTargets(List<Hexagon> targetHexagons) {
    	this.targetHexagons = targetHexagons;
    }

    @Override
    public void execute() {
        // Logic for exploring (scouting or revealing information in the hex)
        //System.out.println(player.getPseudo() + " is exploring hex " + targetHexagons);

        ArrayList<Ship> shipList = new ArrayList<Ship>(this.shipsInvolved);
         // Move each ship to its destination
         for (int i=0; i<this.shipsInvolved.size(); i++) {
        	 shipList.get(i).move(this.targetHexagons.get(i));
         }
         
    }
}
