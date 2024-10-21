package pimperium;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Ship> ships;
    private int shipCount;
    private String pseudo;
    //private int score;

    public Player() {
        this.ships = new ArrayList<>();
        this.shipCount = 0;
        this.pseudo = ""; 
    }

    // On doit les rajouter dans le diagramme de Classes ?
    public void addShip(Ship ship, Hexagon target) {
        ship.setPosition(target);
        this.ships.add(ship);
        this.shipCount++;
    }

    public void removeShip(Ship ship) {
        this.ships.remove(ship);
        this.shipCount--;
    }

    public int getShipCount() {
        return this.shipCount;
    }

    // Getter and setter for the pseudo
    public String getPseudo() {
        return this.pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
}