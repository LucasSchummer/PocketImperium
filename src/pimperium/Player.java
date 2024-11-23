package pimperium;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

public abstract class Player {
    protected List<Ship> ships;
    protected int shipCount;
    protected String pseudo;
    protected Game game;
    protected int[] orderCommands;
    //protected int score;
    protected Expand expand;
    protected Explore explore;
    protected Exterminate exterminate;

    public Player(Game game) {
        this.ships = new ArrayList<Ship>();
        this.shipCount = 0;
        this.pseudo = ""; 
        this.game = game;
        this.expand = new Expand(this);
        this.explore = new Explore(this);
        this.exterminate = new Exterminate(this);
    }
    
    //Return and set the order of commands as a list of int
    //Ex: {1,0,2} : Explore/Expand/Exterminate
    public abstract int[] chooseOrderCommands();

    public int[] getOrderCommands() {
        return this.orderCommands;
    }

    public void addShip(Hexagon target) {
    	Ship ship = new Ship(target, this);
        this.ships.add(ship);
        this.shipCount++;
    }

    public void removeShip(Ship ship) {
        this.ships.remove(ship);
        this.shipCount--;
    }

    public List<Ship> getShips() {
        return this.ships;
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
    
    public abstract void setupInitialFleet();

    public void doAction(int index, int efficiency) {
        int action = this.orderCommands[index];
        switch (action) {
            case 0: {
                this.doExpand(efficiency);
                break;
            }
            case 1: {
                this.doExplore(efficiency);
                break;
            }
            case 2: {
                this.doExterminate(efficiency);
                break;
            }
            default:
                throw new IllegalArgumentException("Unexpected value: " + index);
        }
    }


    public abstract void doExpand(int efficiency);
    public abstract void doExplore(int efficiency);
    public abstract void doExterminate(int efficiency);

    
}