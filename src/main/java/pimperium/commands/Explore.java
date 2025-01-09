package pimperium.commands;

import java.io.Serializable;
import java.util.List;

import pimperium.elements.Hexagon;
import pimperium.elements.Ship;
import pimperium.players.Player;

/**
 * Command card belonging to a player that enables him to perform Explore
 */
public class Explore implements Command, Serializable {
    private static final long serialVersionUID = 1L;
    private Player player;
    private List<Hexagon> targetHexagons;
    private List<Ship> shipsInvolved;

    public Explore(Player player) {
        this.player = player;
    }

    /**
     * Set the fleet concerned by the Explore move
     * @param shipsInvolved List of ships to be moved
     */
    public void setShips(List<Ship> shipsInvolved) {
    	this.shipsInvolved = shipsInvolved;
    }

    /**
     * Set the list of destinations corresponding to the list of ships
     * @param targetHexagons List of hexagons, with potentially the same hexagon multiple times in case multiple share a common destination
     */
    public void setTargets(List<Hexagon> targetHexagons) {
    	this.targetHexagons = targetHexagons;
    }

    /**
     * Execute the Explore move (Move each ship to the corresponding hexagon)
     */
    public void execute() {

        // Check that the lists have the same size
        if (shipsInvolved.size() != targetHexagons.size()) {
            throw new IllegalArgumentException("The number of ships and targets must be identical.");
        }

         // Move each ship to its destination
         for (int i=0; i<this.shipsInvolved.size(); i++) {
        	 this.shipsInvolved.get(i).move(this.targetHexagons.get(i));
             this.shipsInvolved.get(i).setHasExplored(true);
         }
         
    }
}
