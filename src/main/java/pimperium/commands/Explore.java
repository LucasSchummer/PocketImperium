package pimperium.commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pimperium.elements.Hexagon;
import pimperium.elements.Ship;
import pimperium.players.Player;

public class Explore implements Command, Serializable {
    private static final long serialVersionUID = 1L;
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

        // Check that the lists have the same size
        if (shipsInvolved.size() != targetHexagons.size()) {
            throw new IllegalArgumentException("The number of ships and targets must be identical.");
        }

         // Move each ship to its destination
         for (int i=0; i<this.shipsInvolved.size(); i++) {
        	 this.shipsInvolved.get(i).move(this.targetHexagons.get(i));
         }
         
    }
}
