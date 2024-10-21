package pimperium;

public class Ship {

    private Hexagon position;
    private Player player;

    // Constructor
    public Ship(Hexagon position, Player player) {
        this.position = position;
        this.player = player;
        player.addShip(this, position);
    }

    // Getter and Setter for position
    public Hexagon getPosition() {
        return this.position;
    }

    public void setPosition(Hexagon position) {
        this.position = position;
    }

    // Move method to change the ship's position
    public void move(Hexagon newPosition) {
        this.position = newPosition;
        System.out.println("Ship moved to new position: " + newPosition);
    }

    // Destroy method to remove the ship from the player's fleet
    public void destroy() {
        player.removeShip(this);
        System.out.println("Ship destroyed");
    }
}