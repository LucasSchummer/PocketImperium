package pimperium;

public class Ship {

    private Hexagon position;
    private Player player;

    // Constructor
    public Ship(Hexagon position, Player player) {
        this.position = position;
        this.player = player;
    }

    // Getter and Setter for position
    public Hexagon getPosition() {
        return this.position;
    }

    public void setPosition(Hexagon position) {
        this.position = position;
    }


    public Player getOwner() {
        return this.player;
    }


    // Move method to change the ship's position
    public void move(Hexagon newPosition) {
        this.position = newPosition;
        System.out.println(this + "moved to new position: " + newPosition);
    }

    // Destroy method to remove the ship from the player's fleet
    public void destroy() {
        player.removeShip(this);
        System.out.println("Ship destroyed");
    }
    
    public String toString() {
    	return "Ship on " + position + " belonging to " + player.getPseudo();
    }
}