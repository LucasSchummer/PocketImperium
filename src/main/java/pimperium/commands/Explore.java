package pimperium.commands;

import java.util.ArrayList;
import java.util.List;

import pimperium.models.Hexagon;
import pimperium.models.Player;
import pimperium.models.Ship;

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

         // Move each ship to its destination
         for (int i=0; i<this.shipsInvolved.size(); i++) {
        	 this.shipsInvolved.get(i).move(this.targetHexagons.get(i));
         }
         
    }
}
