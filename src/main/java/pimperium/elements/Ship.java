package pimperium.elements;

import pimperium.players.Player;

import java.io.Serializable;

/**
 * A spatial ship belonging to a player and navigating on the map
 */
public class Ship implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The hexagon where the ship is currently situated
     */
    private Hexagon position;
    /**
     * The owner of the ship
     */
    private Player player;

    /**
     * Whether the ship has already been concerned by an Expand move this round
     */
    private boolean hasExpanded = false;
    /**
     * Whether the ship has already been concerned by an Explore move this round
     */
    private boolean hasExplored = false;
    /**
     * Whether the ship has already been concerned by an Exterminate move this round
     */
    private boolean hasExterminated = false;

    /**
     * Create a new ship for a given player at a given position
     * @param position
     * @param player
     */
    public Ship(Hexagon position, Player player) {
        this.position = position;
        this.player = player;
        this.position.addShip(this);
    }

    public Hexagon getPosition() {
        return this.position;
    }

    public Player getOwner() {
        return this.player;
    }

    /**
     * Move the ship to another hexagon and update both hexagons accordingly
     * @param newPosition The destination of the ship
     */
    public void move(Hexagon newPosition) {
        //System.out.println(this + " moved to new position: " + newPosition);
        this.position.removeShip(this);
        this.position = newPosition;
        this.position.addShip(this);
    }

    /**
     * Destroy the ship by removing it from the player's fleet and from the hexagon
     */
    public void destroy() {
        player.removeShip(this);
        position.removeShip(this);
        //System.out.println(this + " destroyed");
    }

    /**
     * Whether the ship has already been expanded on this round
     * @return
     */
    public boolean hasExpanded() {
        return this.hasExpanded;
    }

    /**
     * Whether the ship has already explored this round
     * @return
     */
    public boolean hasExplored() {
        return this.hasExplored;
    }

    /**
     * Whether the ship has already exterminated this round
     * @return
     */
    public boolean hasExterminated() {
        return this.hasExterminated;
    }

    public void setHasExpanded(boolean hasExpanded) {
        this.hasExpanded = hasExpanded;
    }

    public void setHasExplored(boolean hasExplored) {
        this.hasExplored = hasExplored;
    }

    public void setHasExterminated(boolean hasExterminated) {
        this.hasExterminated = hasExterminated;
    }
    
    public String toString() {
    	return "Ship on " + position + " belonging to " + player.getPseudo();
    }
}