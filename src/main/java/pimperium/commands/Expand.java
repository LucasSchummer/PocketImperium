package pimperium.commands;

import java.io.Serializable;

import pimperium.elements.Ship;
import pimperium.players.Player;

/**
 * Command card belonging to a player that enables him to perform Expand
 */
public class Expand implements Command, Serializable {

    private static final long serialVersionUID = 1L;
    private Player player;
    private Ship shipInvolved;

    public Expand(Player player) {
        this.player = player;      
    }

    /**
     * Set the ship to expand on
     * @param ship The ship concerned by the Expand move
     */
    public void setShip(Ship ship) {
        this.shipInvolved = ship;
    }


    /**
     * Execute the expand move (Create the new ship)
     */
    public void execute() {

        System.out.println(this.shipInvolved + " expanded");
        this.player.createShip(this.shipInvolved.getPosition());
        shipInvolved.setHasExpanded(true);

        this.player.deleteExtraShips();

    }
}
