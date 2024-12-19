package pimperium.elements;

import pimperium.players.Player;

import java.io.Serializable;

public class Ship implements Serializable {

    private static final long serialVersionUID = 1L;

    private Hexagon position;
    private Player player;

    private boolean hasExpanded = false;
    private boolean hasExplored = false;
    private boolean hasExterminated = false;

    // Constructor
    public Ship(Hexagon position, Player player) {
        this.position = position;
        this.player = player;
        this.position.addShip(this);
    }

    // Getter and Setter for position
    public Hexagon getPosition() {
        return this.position;
    }

    public Player getOwner() {
        return this.player;
    }

    // Move method to change the ship's position
    public void move(Hexagon newPosition) {
        //System.out.println(this + " moved to new position: " + newPosition);
        this.position.removeShip(this);
        this.position = newPosition;
        this.position.addShip(this);
    }

    // Destroy method to remove the ship from the player's fleet
    public void destroy() {
        player.removeShip(this);
        position.removeShip(this);
        //System.out.println(this + " destroyed");
    }

    public boolean hasExpanded() {
        return this.hasExpanded;
    }

    public boolean hasExplored() {
        return this.hasExplored;
    }

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